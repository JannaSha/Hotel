package com.gateway.controllers;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gateway.models.*;
import com.gateway.clients.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ArrayBlockingQueue;

import java.util.function.Supplier;

@RestController
@RequestMapping(value = "/hotel" )
@EnableAutoConfiguration
@Validated
public class Controller {

    private BillClient billClient = new BillClient();
//    private RoomsTypeClient roomsTypeClient = new RoomsTypeClient();
    private UsersClient usersClient = new UsersClient();
    private RoomClient roomClient = new RoomClient();
    private OrdersClient ordersClient = new OrdersClient();
    private AuthClient authClient = new AuthClient();

    private static final Logger log = Logger.getLogger(Controller.class);

    private final Integer MAX_QUEUE_SIZE = 10;

    @Autowired
    KafkaProducer producer;

    @RequestMapping(method=RequestMethod.GET, value="/producer")
    public String producer(@RequestParam("data")String data){
        producer.send(data);
        System.out.println("Done");
        return "Done";
    }

    private BigDecimal getPrice(Integer nightAmount, BigDecimal nightPrice, Integer userOrderAmount) {
        BigDecimal cost = nightPrice.multiply(new BigDecimal(nightAmount));
        if (userOrderAmount > 10) {
             cost = cost.multiply(new BigDecimal(0.97));
        }
        return cost;
    }

    private ResponseEntity<Object> saveAll(Room room, Room oldRoom, RoomType oldRoomType,
                          RoomType roomType, User user) {
        ResponseEntity<RoomType> roomTypeResponseEntity =
                handle(() -> roomClient.modifyRoomType(roomType.getId(), roomType), "rooms");
        if (roomTypeResponseEntity.getStatusCode() == HttpStatus.NOT_FOUND) {
            log.error(String.format("POST/user/%d/order: No room type = %d. %s",
                    user.getId(), roomType.getId(), roomTypeResponseEntity.getStatusCode().toString()));
            return new ResponseEntity<>(new ErrorEntity(HttpStatus.NOT_FOUND.value(),
                            "No room type = " + roomType.getId()), HttpStatus.NOT_FOUND);
        } else if (roomTypeResponseEntity.getStatusCode() == HttpStatus.CONFLICT) {
            log.error(String.format("POST/user/%d/order: Error modify room type = %d. %s",
                    user.getId(), roomType.getId(), roomTypeResponseEntity.getStatusCode().toString()));
            return new ResponseEntity<>(new ErrorEntity(HttpStatus.CONFLICT.value(),
                            "Error modify room type = " + roomType.getId()), HttpStatus.CONFLICT);
        } else if (roomTypeResponseEntity.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE) {
            log.error(String.format("POST/user/%d/order: Service room is unavailable. %s",
                    user.getId(), roomTypeResponseEntity.getStatusCode().toString()));
            return new ResponseEntity<>(new ErrorEntity(HttpStatus.SERVICE_UNAVAILABLE.value(),
                            "Service room is unavailable"), HttpStatus.SERVICE_UNAVAILABLE);
        } else if (roomTypeResponseEntity.getStatusCode() != HttpStatus.OK) {
            log.error(String.format("POST/user/%d/order: Other error (room service). %s",
                    user.getId(), roomTypeResponseEntity.getStatusCode().toString()));
            return new ResponseEntity<>(new ErrorEntity(roomTypeResponseEntity.getStatusCode().value(),
                            "Other error in room service"), roomTypeResponseEntity.getStatusCode());
        }

            ResponseEntity<Room> roomResponseEntity =  handle(() -> roomClient.modifyRoom(room.getId(), room),
                    "rooms");
        if (roomResponseEntity.getStatusCode() == HttpStatus.NOT_FOUND) {
            log.error(String.format("POST/user/%d/order: No room  = %d. %s",
                    user.getId(), room.getId(), roomTypeResponseEntity.getStatusCode().toString()));
            handle(() -> roomClient.modifyRoomType(oldRoomType.getId(), oldRoomType), "room types");
            return new ResponseEntity<>(new ErrorEntity(HttpStatus.NOT_FOUND.value(),
                    "No room  = " + room.getId()), HttpStatus.NOT_FOUND);
        } else if (roomResponseEntity.getStatusCode() == HttpStatus.CONFLICT) {
            log.error(String.format("POST/user/%d/order: Error modify room = %d. %s",
                    user.getId(), room.getId(), roomTypeResponseEntity.getStatusCode().toString()));
            handle(() -> roomClient.modifyRoomType(oldRoomType.getId(), oldRoomType), "room types");
            return new ResponseEntity<>(new ErrorEntity(HttpStatus.CONFLICT.value(),
                    "Error modify room = " + room.getId()), HttpStatus.CONFLICT);
        } else if (roomTypeResponseEntity.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE) {
            log.error(String.format("POST/user/%d/order: Service room is unavailable. %s",
                    user.getId(), roomTypeResponseEntity.getStatusCode().toString()));
            return new ResponseEntity<>(new ErrorEntity(HttpStatus.SERVICE_UNAVAILABLE.value(),
                    "Service room is unavailable"), HttpStatus.SERVICE_UNAVAILABLE);
        } else if (roomResponseEntity.getStatusCode() != HttpStatus.OK) {
            log.error(String.format("POST/user/%d/order: Other error (room service). %s",
                    user.getId(), roomTypeResponseEntity.getStatusCode().toString()));
            handle(() -> roomClient.modifyRoomType(oldRoomType.getId(), oldRoomType), "room types");
            return new ResponseEntity<>(new ErrorEntity(roomTypeResponseEntity.getStatusCode().value(),
                    "Other error in room service"), roomTypeResponseEntity.getStatusCode());
        }

        ResponseEntity<User> responseEntityUser = handle( ()-> usersClient.modifyUser(user.getId(), user),
                "users");
        if (responseEntityUser.getStatusCode() != HttpStatus.OK &&
                    responseEntityUser.getStatusCode() != HttpStatus.CREATED) {
            log.error(String.format("POST/user/%d/order: Error modify user. %s",
                    user.getId(), responseEntityUser.getStatusCode().toString()));
            handle(() -> roomClient.modifyRoomType(oldRoomType.getId(), oldRoomType), "room types");
            handle(() -> roomClient.modifyRoom(oldRoom.getId(), oldRoom), "rooms");
            return new ResponseEntity<>(new ErrorEntity(responseEntityUser.getStatusCode().value(),
                    responseEntityUser.getStatusCode().toString()), responseEntityUser.getStatusCode());
        }
        return new ResponseEntity<>(new Order(roomResponseEntity.getBody().getId(), responseEntityUser.getBody().getId(),
                new Timestamp(Instant.now().getEpochSecond()), -1, null, null,
                new BigDecimal(0)), HttpStatus.OK);
    }
    private BlockingQueue<TaskRecord> taskQueue = null;

    private ExecutorService taskExecutorService = null;

    @PostConstruct
    private void initExecutors() {
        taskQueue = new ArrayBlockingQueue(MAX_QUEUE_SIZE);
        taskExecutorService = Executors.newSingleThreadExecutor();
        taskExecutorService.submit (() -> {
            try {
                while (true) {
                    TaskRecord task = taskQueue.take();
                    log.info(String.format("Task is taken url = %s", task.getUrl()));
                    task.getFunction().run();
                    try {
                        taskQueue.put(task);
                        Thread.sleep(10000);
                    } catch (java.lang.InterruptedException ecx) {
                        log.info("Error: " + ecx.toString());
                    } catch (Exception exc) {
                        log.info(String.format("Task error = %s url = %s", task.getUrl(), exc.toString()));
                    }
                }
            } catch (Exception exc) {
                log.info(String.format("Task error = %s", exc.toString()));
            }
        });
    }

    /* Получение всех комнат в отеле */
    @RequestMapping(method = RequestMethod.GET, value = "/rooms",
            params = {"page", "size"}, produces="application/json")
    public Object getAllRooms(@RequestParam("page") Integer page,
                              @RequestParam("size") Integer size,
                              @RequestHeader HttpHeaders head) {
        if (!head.containsKey("scope")) {
            log.error("/roomtype: No scope in header");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (!head.get("scope").get(0).equals("ui") && !head.get("scope").get(0).equals("api")) {
            log.error("/roomtype: Scope does not equal ui or api");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        ResponseEntity<Object> responseEntity = handle(() -> roomClient.findAll(page, size), "room");
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            log.info("GET/rooms: Get rooms successfully. " + responseEntity.getStatusCode().toString());
        }
        return responseEntity;
    }

    /* Получение всех типов комнат в отеле */
    @RequestMapping(method = RequestMethod.GET, value = "/roomtypes",
            params = {"page", "size"}, produces="application/json")
    public Object getAllRoomTypes(@RequestParam("page") Integer page,
                                  @RequestParam("size") Integer size,
                                  @RequestHeader HttpHeaders header){
        if (!header.containsKey("scope")) {
            log.error("/roomtype: No scope in header");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (!header.get("scope").get(0).equals("ui") && !header.get("scope").get(0).equals("api")) {
            log.error("/roomtype: Scope does not equal ui or api");
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        if (header.get("scope").get(0).equals("api")) {
            ResponseEntity<String> authResponse = handle(() -> authClient.getCurrent(header), "auth");
            if (authResponse.getStatusCode() == HttpStatus.UNAUTHORIZED ||
                    authResponse.getStatusCode() == HttpStatus.FORBIDDEN) {
                log.error(String.format("GET/roomtypes: Token does not exist. %s",
                         authResponse.getStatusCode().toString()));
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            if (authResponse.getStatusCode() != HttpStatus.OK) {
                log.error(String.format("GET/roomtypes: Can not get token. %s",
                        authResponse.getStatusCode().toString()));
                return new ResponseEntity<>(authResponse.getStatusCode());
            }

//            ObjectMapper mapper = new ObjectMapper();
//            JsonNode actualObj;
//            try {
//                actualObj = mapper.readTree(authResponse.getBody());
//            } catch (IOException ex) {
//                log.error(String.format("GET/roomtypes: Error parse json auth file. %s",
//                        HttpStatus.INTERNAL_SERVER_ERROR.toString()));
//                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//            }
        }

        ResponseEntity<Object> responseEntity = handle(() -> roomClient.findAllRoomsType(page, size),
                "room types");
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            log.info("GET/roomtypes: Get room types types successfully. " + responseEntity.getStatusCode().toString());
        }
        return responseEntity;

    }

    /* Получение описания заказа авторизованным пользователем */
    /* Права для клиента scope = ui */
    @RequestMapping(method = RequestMethod.GET, value = "/user/{username}/order/{orderId}", produces="application/json")
    public ResponseEntity<Order> getUserOrder(@PathVariable("username") String username,
                                              @PathVariable("orderId") long orderId,
                                              @RequestHeader HttpHeaders header) {
        ResponseEntity<String> authResponse = handle(() -> authClient.getCurrent(header), "auth");
        if (authResponse.getStatusCode() == HttpStatus.UNAUTHORIZED ||
                authResponse.getStatusCode() == HttpStatus.FORBIDDEN) {
            log.error(String.format("GET/user/%s/orders: Token does not exist. %s",
                    username, authResponse.getStatusCode().toString()));
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if (authResponse.getStatusCode() != HttpStatus.OK) {
            log.error(String.format("GET/user/%s/orders: Can not get token. %s",
                    username, authResponse.getStatusCode().toString()));
            return new ResponseEntity<>(authResponse.getStatusCode());
        }
        ObjectMapper mapper = new ObjectMapper();
        JsonNode actualObj;
        try {
            actualObj = mapper.readTree(authResponse.getBody());
        } catch (IOException ex) {
            log.error(String.format("GET/user/%s/order/%d: Error parse json auth file. %s",
                    username, orderId, HttpStatus.INTERNAL_SERVER_ERROR.toString()));
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        ResponseEntity<User> responseEntityUser = handle(() -> usersClient.findByUserName(username), "users");
        if (responseEntityUser.getStatusCode() == HttpStatus.NOT_FOUND) {
            log.error(String.format("GET/user/%s/order/%d: User does not exist. %s",
                    username, orderId, HttpStatus.UNAUTHORIZED.toString()));
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        else if (responseEntityUser.getStatusCode() != HttpStatus.OK) {
            log.error(String.format("GET/user/%s/order/%d: Other error. %s",
                    username, orderId, responseEntityUser.getStatusCode().toString()));
            return new ResponseEntity<>(responseEntityUser.getStatusCode());
        }
        if (!actualObj.get("userAuthentication").get("details").get("username").toString().
                equals("\"" + responseEntityUser.getBody().getUsername() + "\"") ||
                header.get("scope").toString().equals("\"ui\"")) {
            log.error(String.format("GET/user/%s/order/%d: UNAUTHORIZED error . %s",
                    username, orderId, HttpStatus.UNAUTHORIZED.toString()));
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        ResponseEntity<Order> orderResponseEntity = handle(() -> ordersClient.findById(orderId), "orders");
        if (orderResponseEntity.getStatusCode() == HttpStatus.OK) {
            log.info(String.format("GET/user/%s/order/%d: Get order's description successfully. %s",
                    username, orderId, orderResponseEntity.getStatusCode().toString()));
        }
        return orderResponseEntity;
    }

    /* Получение всех заказов авторизованного пользователя */
    @RequestMapping(method = RequestMethod.GET, value = "/user/{username}/orders", produces="application/json")
    public ResponseEntity<List<OrderGetter>> getAllUserOrders(@PathVariable("username") String username,
                                                              @RequestHeader HttpHeaders header) {

        ResponseEntity<String> authResponse = handle(() -> authClient.getCurrent(header), "auth");
        if (authResponse.getStatusCode() == HttpStatus.UNAUTHORIZED ||
                authResponse.getStatusCode() == HttpStatus.FORBIDDEN) {
            log.error(String.format("GET/user/%s/orders: Token does not exist. %s",
                    username, authResponse.getStatusCode().toString()));
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if (authResponse.getStatusCode() != HttpStatus.OK) {
            log.error(String.format("GET/user/%s/orders: Can not get token. %s",
                    username, authResponse.getStatusCode().toString()));
            return new ResponseEntity<>(authResponse.getStatusCode());
        }
        ObjectMapper mapper = new ObjectMapper();
        JsonNode actualObj;
        try {
            actualObj = mapper.readTree(authResponse.getBody());
        } catch (Exception ex) {
            log.error(String.format("GET/user/%s/orders: Error parse json auth file. %s",
                    username, HttpStatus.INTERNAL_SERVER_ERROR.toString()));
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        ResponseEntity<User> responseEntityUser = handle(() -> usersClient.findByUserName(username), "users");
        if (responseEntityUser.getStatusCode() == HttpStatus.NOT_FOUND) {
            log.error(String.format("GET/user/%s/orders: User does not exist. %s",
                    username, responseEntityUser.getStatusCode().toString()));
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        else if (responseEntityUser.getStatusCode() != HttpStatus.OK) {
            log.error(String.format("GET/user/%s/orders: Other error. %s",
                    username, responseEntityUser.getStatusCode().toString()));
            return new ResponseEntity<>(responseEntityUser.getStatusCode());
        }
        if (!actualObj.get("userAuthentication").get("details").get("username").toString().
                equals("\"" + responseEntityUser.getBody().getUsername() + "\"") ||
                header.get("scope").toString().equals("ui")) {
            log.error(String.format("GET/user/%s/orders: UNAUTHORIZED error . %s",
                    username, HttpStatus.UNAUTHORIZED.toString()));
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        long userId = responseEntityUser.getBody().getId();
        ResponseEntity<Order[]> ordersResponseEntity =
                handle(() -> ordersClient.findByUserId(userId), "orders");
        if (ordersResponseEntity.getStatusCode() == HttpStatus.NOT_FOUND) {
            log.error(String.format("GET/user/%d/orders: User does not have orders. %s",
                    userId, ordersResponseEntity.getStatusCode().toString()));
            return new ResponseEntity<>(ordersResponseEntity.getStatusCode());
        } else if (ordersResponseEntity.getStatusCode() != HttpStatus.OK) {
            log.error(String.format("GET/user/%d/orders: Other error. (order service) %s",
                    userId, ordersResponseEntity.getStatusCode().toString()));
            return new ResponseEntity<>(ordersResponseEntity.getStatusCode());
        }
        List<OrderGetter> orderGetters = new LinkedList<>();
        for (Order item:  ordersResponseEntity.getBody()) {
            OrderGetter orderGetterTemp = new OrderGetter(item);
            if (item.getBillId() != -1) {
                ResponseEntity<Billing> tempBill =
                        handle( () -> billClient.findOne(item.getBillId()), "billings");
                if (tempBill.getStatusCode() == HttpStatus.OK) {
                    orderGetterTemp.setBillId(tempBill.getBody());
                }
                else {
                    orderGetterTemp.setBillId(null);
                }
            }
            ResponseEntity<Room> tempRoom = handle( () -> roomClient.findById(item.getRoomId()), "rooms");
            if ( tempRoom.getStatusCode() == HttpStatus.OK) {
                ResponseEntity<RoomType> tempRoomType =
                        handle(() -> roomClient.findByIdRoomType(tempRoom.getBody().getRoomType()),
                                "room types");
                if (tempRoomType.getStatusCode() == HttpStatus.OK) {
                    orderGetterTemp.setRoomType(tempRoomType.getBody());
                }
                else {
                    orderGetterTemp.setRoomType(null);
                }
            } else  {
                orderGetterTemp.setRoomType(null);
            }
            orderGetters.add(orderGetterTemp);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=UTF-8");

        log.info(String.format("GET/user/%d/orders: Get user's orders successfully. %s",
                userId, responseEntityUser.getStatusCode().toString()));
        return new ResponseEntity<>(orderGetters, headers, HttpStatus.OK);
    }


    /* Создание заказа для авторизованного пользователя */
    @RequestMapping(method = RequestMethod.POST, value = "/user/{username}/order",
            consumes = "application/json", produces="application/json")
    public ResponseEntity<Order> createOrder(@PathVariable("username") String username,
                                             @RequestBody @Valid OrderCreator orderCreator,
                                             @RequestHeader HttpHeaders header) {

        ResponseEntity<String> authResponse = handle(() -> authClient.getCurrent(header), "auth");
        if (authResponse.getStatusCode() == HttpStatus.UNAUTHORIZED ||
                authResponse.getStatusCode() == HttpStatus.FORBIDDEN) {
            log.error(String.format("GET/user/%s/orders: Token does not exist. %s",
                    username, authResponse.getStatusCode().toString()));
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if (authResponse.getStatusCode() != HttpStatus.OK) {
            log.error(String.format("GET/user/%s/orders: Can not get token. %s",
                    username, authResponse.getStatusCode().toString()));
            return new ResponseEntity<>(authResponse.getStatusCode());
        }
        ObjectMapper mapper = new ObjectMapper();
        JsonNode actualObj;
        try {
            actualObj = mapper.readTree(authResponse.getBody());
        } catch (IOException ex) {
            log.error(String.format("GET/user/%s/order: Error parse json auth file. %s",
                    username, HttpStatus.INTERNAL_SERVER_ERROR.toString()));
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        ResponseEntity<User> responseUser = handle(() -> usersClient.findByUserName(username), "users");
        if (responseUser.getStatusCode() == HttpStatus.NOT_FOUND) {
            log.error(String.format("POST/user/%s/order: User does not exist. %s",
                    username, HttpStatus.UNAUTHORIZED.toString()));
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if (responseUser.getStatusCode() != HttpStatus.OK) {
            log.error(String.format("POST/user/%s/order: Other error (user service). %s",
                    username, responseUser.getStatusCode().toString()));
            return new ResponseEntity<>(responseUser.getStatusCode());
        }
        if (!actualObj.get("userAuthentication").get("details").get("username").toString().
                equals("\"" + responseUser.getBody().getUsername() + "\"") ||
                header.get("scope").toString().equals("\"ui\"")) {
            log.error(String.format("GET/user/%s/order: UNAUTHORIZED error . %s",
                    username, HttpStatus.UNAUTHORIZED.toString()));
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        long userId = responseUser.getBody().getId();
        ResponseEntity<RoomType> roomTypeResponse =
                handle(() -> roomClient.findByIdRoomType(orderCreator.getRoomTypeId()), "rooms");
        if (roomTypeResponse.getStatusCode() == HttpStatus.NOT_FOUND) {
            log.error(String.format("POST/user/%d/order: Room type = %d does not exist. %s",
                    userId, orderCreator.getRoomTypeId(), responseUser.getStatusCode().toString()));
            return new ResponseEntity<>(roomTypeResponse.getStatusCode());
        }
        if (roomTypeResponse.getStatusCode() != HttpStatus.OK) {
            log.error(String.format("POST/user/%d/order: Other error (roomType service). %s",
                    userId, responseUser.getStatusCode().toString()));
            return new ResponseEntity<>(roomTypeResponse.getStatusCode());
        }
        if (roomTypeResponse.getBody().getAmountVacantRooms() == 0) {
            log.error(String.format("POST/user/%d/order: No vacant room in room type = %d. %s",
                    userId, orderCreator.getRoomTypeId(), HttpStatus.BAD_REQUEST));
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        ResponseEntity<Room[]> roomResponse =
                handle(() -> roomClient.findByType(roomTypeResponse.getBody().getId(), true), "room types");
        if (roomResponse.getStatusCode() == HttpStatus.NOT_FOUND) {
            log.error(String.format("POST/user/%d/order: No vacant room for room type = %d. %s",
                    userId, orderCreator.getRoomTypeId(), responseUser.getStatusCode().toString()));
            return new ResponseEntity<>(roomResponse.getStatusCode());
        }
        if (roomResponse.getStatusCode() != HttpStatus.OK) {
            log.error(String.format("POST/user/%d/order: Other error (room service). %s",
                    userId, responseUser.getStatusCode().toString()));
            return new ResponseEntity<>(roomResponse.getStatusCode());
        }

        RoomType newRoomType = new RoomType(roomTypeResponse.getBody());
        newRoomType.setAmountVacantRooms((roomTypeResponse.getBody().getAmountVacantRooms() - 1));
        Room newRoom = new Room(roomResponse.getBody()[0]);
        newRoom.setVacant(false);
        User newUser = new User(responseUser.getBody());
        newUser.setOrdersAmount(newUser.getOrdersAmount() + 1);

        ResponseEntity<Object> orderResponseEntityTemp = saveAll(newRoom, roomResponse.getBody()[0],
                roomTypeResponse.getBody(), newRoomType, newUser);

        if (orderResponseEntityTemp.getStatusCode() != HttpStatus.OK) {
            return new ResponseEntity<>(orderResponseEntityTemp.getStatusCode());
        }
        Order order = (Order) orderResponseEntityTemp.getBody();
        order.setArrivalDate(orderCreator.getArrivalDate());
        order.setNightAmount(orderCreator.getNightAmount());
        order.setCost(getPrice(orderCreator.getNightAmount(),
                roomTypeResponse.getBody().getPrice(), responseUser.getBody().getOrdersAmount()));
        Order tempOrder = order;
        ResponseEntity<Order> orderResponseEntity = handle(() -> ordersClient.createOrder(tempOrder), "order");
        if (orderResponseEntity.getStatusCode() != HttpStatus.CREATED) {
            log.error(String.format("POST/user/%d/order: Error create order. %s",
                    userId, orderResponseEntity.getStatusCode().toString()));
            handle(() -> roomClient.modifyRoomType(
                    roomTypeResponse.getBody().getId(), roomTypeResponse.getBody()), "room types");
            handle(() -> roomClient.modifyRoom(roomResponse.getBody()[0].getId(), roomResponse.getBody()[0]),
                    "rooms");
            handle(() -> usersClient.modifyUser(responseUser.getBody().getId(), responseUser.getBody()), "users");
            return orderResponseEntity;
        }
        order = (Order) orderResponseEntityTemp.getBody();
        log.info(String.format("POST/user/%d/order: Order = %d create successfully. %s",
                userId, order.getId(), orderResponseEntityTemp.getStatusCode().toString()));
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ServletUriComponentsBuilder
                .fromCurrentServletMapping().path("hotel/user/{userId}/order/{orderId}").build()
                .expand(userId, order.getId())
                .expand(order.getId()).toUri());
        return new ResponseEntity<>(order, headers, HttpStatus.CREATED);
    }

    /* Оплата существующего заказа авторизованным пользователем */
    @RequestMapping(method = RequestMethod.POST, value = "/user/{username}/order/{orderId}/billing",
            consumes = "application/json", produces="application/json")
    public ResponseEntity<Order> billOrder(@PathVariable("username") String username, @PathVariable("orderId") long orderId,
                                           @RequestBody @Valid BillMaker bill, @RequestHeader HttpHeaders header)
    {
        ResponseEntity<String> authResponse = handle(() -> authClient.getCurrent(header), "auth");
        if (authResponse.getStatusCode() == HttpStatus.UNAUTHORIZED ||
                authResponse.getStatusCode() == HttpStatus.FORBIDDEN) {
            log.error(String.format("GET/user/%s/orders: Token does not exist. %s",
                    username, authResponse.getStatusCode().toString()));
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if (authResponse.getStatusCode() != HttpStatus.OK) {
            log.error(String.format("GET/user/%s/orders: Can not get token. %s",
                    username, authResponse.getStatusCode().toString()));
            return new ResponseEntity<>(authResponse.getStatusCode());
        }
        ObjectMapper mapper = new ObjectMapper();
        JsonNode actualObj;
        try {
            actualObj = mapper.readTree(authResponse.getBody());
        } catch (IOException ex) {
            log.error(String.format("GET/user/%s/order/%d: Error parse json auth file. %s",
                    username, orderId, HttpStatus.INTERNAL_SERVER_ERROR.toString()));
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        ResponseEntity<User> responseEntityUser = handle(() -> usersClient.findByUserName(username), "users");
        if (responseEntityUser.getStatusCode() == HttpStatus.NOT_FOUND) {
            log.error(String.format("POST/user/%s/order/%d/billing: User are't found. %s",
                    username, orderId, responseEntityUser.getStatusCode().toString()));
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        else if (responseEntityUser.getStatusCode() != HttpStatus.OK) {
            log.error(String.format("POST/user/%s/order/%d/billing: Other error (user service). %s",
                    username, orderId, responseEntityUser.getStatusCode().toString()));
            return new ResponseEntity<>(responseEntityUser.getStatusCode());
        }
        if (!actualObj.get("userAuthentication").get("details").get("username").toString().
                equals("\"" + responseEntityUser.getBody().getUsername() + "\"") ||
                header.get("scope").toString().equals("\"ui\"")) {
            log.error(String.format("GET/user/%s/order/%d/billing: UNAUTHORIZED error . %s",
                    username, orderId, HttpStatus.UNAUTHORIZED.toString()));
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        long userId = responseEntityUser.getBody().getId();
        ResponseEntity<Order> orderResponseEntity = handle(() -> ordersClient.findById(orderId), "orders");
        if (orderResponseEntity.getStatusCode() == HttpStatus.NOT_FOUND) {
            log.error(String.format("POST/user/%d/order/%d/billing: Order are't found. %s",
                    userId, orderId, orderResponseEntity.getStatusCode().toString()));
            return new ResponseEntity<>(orderResponseEntity.getStatusCode());
        }
        if (orderResponseEntity.getStatusCode() != HttpStatus.OK) {
            log.error(String.format("POST/user/%d/order/%d/billing: Other error (user service). %s",
                    userId, orderId, responseEntityUser.getStatusCode().toString()));
            return new ResponseEntity<>(orderResponseEntity.getStatusCode());
        }
        if (orderResponseEntity.getBody().getUserId() != userId) {
            log.error(String.format("POST/user/%d/order/%d/billing: User can not pay by order, must be owner. %s",
                    userId, orderId, HttpStatus.BAD_REQUEST));
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (orderResponseEntity.getBody().getBillId() != -1) {
            log.error(String.format("POST/user/%d/order/%d/billing: Order has already paid. %s",
                    userId, orderId, HttpStatus.NOT_ACCEPTABLE));
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
        Order tempOrder = orderResponseEntity.getBody();
        ResponseEntity<Billing> responseEntityBilling =
                handle(() -> billClient.createBill(new Billing(bill.getCartNumber(), tempOrder.getCost())), "billings");
        if (responseEntityBilling.getStatusCode() != HttpStatus.CREATED) {
            log.error(String.format("POST/user/%d/order/%d/billing: Error create bill (billing service). %s",
                    userId, orderId, responseEntityUser.getStatusCode().toString()));
            return new ResponseEntity<>(responseEntityBilling.getStatusCode());
        }
        tempOrder.setBillId(responseEntityBilling.getBody().getId());
        orderResponseEntity = handle(() -> ordersClient.modifyOrder(orderId, tempOrder), "orders");
        if (orderResponseEntity.getStatusCode() != HttpStatus.OK) {
            log.error(String.format("POST/user/%d/order/%d/billing: Error modify order (order service). %s",
                    userId, orderId, orderResponseEntity.getStatusCode().toString()));
            handle(() -> billClient.delete(responseEntityBilling.getBody().getId()), "billing");
            return new ResponseEntity<>(orderResponseEntity.getStatusCode());
        }
        log.info(String.format("POST/user/%d/order/%d/billing: Order = %d has paid successfully. %s",
                userId, orderId, orderId, orderResponseEntity.getStatusCode().toString()));
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ServletUriComponentsBuilder
                .fromCurrentServletMapping().path("hotel/user/{userId}/order/{orderId}").build()
                .expand(userId, orderId).toUri());
        return new ResponseEntity<>(orderResponseEntity.getBody(), headers, HttpStatus.CREATED);
    }

    /* Изменение сущесвующего заказа авторизованным пользователем */
    @RequestMapping(method = RequestMethod.PUT, value = "/user/{username}/order/{orderId}",
            produces="application/json", consumes = "application/json")
    public ResponseEntity<Order> modifyUserOrder(@PathVariable("username") String username,
                                                 @PathVariable("orderId") long orderId,
                                                 @RequestBody @Valid OrderModify newOrder,
                                                 @RequestHeader HttpHeaders header) {
        ResponseEntity<String> authResponse = handle(() -> authClient.getCurrent(header), "auth");
        if (authResponse.getStatusCode() == HttpStatus.UNAUTHORIZED ||
                authResponse.getStatusCode() == HttpStatus.FORBIDDEN) {
            log.error(String.format("GET/user/%s/orders: Token does not exist. %s",
                    username, authResponse.getStatusCode().toString()));
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if (authResponse.getStatusCode() != HttpStatus.OK) {
            log.error(String.format("GET/user/%s/orders: Can not get token. %s",
                    username, authResponse.getStatusCode().toString()));
            return new ResponseEntity<>(authResponse.getStatusCode());
        }
        ObjectMapper mapper = new ObjectMapper();
        JsonNode actualObj;
        try {
            actualObj = mapper.readTree(authResponse.getBody());
        } catch (IOException ex) {
            log.error(String.format("GET/user/%s/order/%d: Error parse json auth file. %s",
                    username, orderId, HttpStatus.INTERNAL_SERVER_ERROR.toString()));
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        boolean is_changed = false;
        ResponseEntity<User> responseEntityUser = handle(() -> usersClient.findByUserName(username), "users");
        if (responseEntityUser.getStatusCode() == HttpStatus.NOT_FOUND) {
            log.error(String.format("PUT/user/%s/order/%d: User is't found. %s",
                    username, orderId, responseEntityUser.getStatusCode().toString()));
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        else if (responseEntityUser.getStatusCode() != HttpStatus.OK) {
            log.error(String.format("PUT/user/%s/order/%d: Error modify order (user service). %s",
                    username, orderId, responseEntityUser.getStatusCode().toString()));
            return new ResponseEntity<>(responseEntityUser.getStatusCode());
        }
        if (!actualObj.get("userAuthentication").get("details").get("username").toString().
                equals("\"" + responseEntityUser.getBody().getUsername() + "\"") ||
                header.get("scope").toString().equals("\"ui\"")) {
            log.error(String.format("GET/user/%s/order/%d: UNAUTHORIZED error . %s",
                    username, orderId, HttpStatus.UNAUTHORIZED.toString()));
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        long userId = responseEntityUser.getBody().getId();
        ResponseEntity<Order> orderResponseEntity = handle(() -> ordersClient.findById(orderId),"orders");
        if (orderResponseEntity.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE){
            Boolean result = taskQueue.offer(new TaskRecord(
                    String.format("/user/%d/order/%d", userId, orderId),
                    () -> modifyUserOrder(username, orderId, newOrder, header), "message"));
            if (!result) {
                log.info("[updatePost($postId)] => couldn't schedule task because queue is full");
                return null;
            }
            log.info("Put update test in queue");
            return new ResponseEntity<>(HttpStatus.OK);
        }
        if (orderResponseEntity.getStatusCode() == HttpStatus.NOT_FOUND) {
            log.error(String.format("PUT/user/%d/order/%d: Order is not found (order service). %s",
                    userId, orderId, responseEntityUser.getStatusCode().toString()));
            return new ResponseEntity<>(responseEntityUser.getStatusCode());
        } else if (orderResponseEntity.getStatusCode() != HttpStatus.OK) {
            log.error(String.format("PUT/user/%d/order/%d: Other error (order service). %s",
                    userId, orderId, responseEntityUser.getStatusCode().toString()));
            return new ResponseEntity<>(responseEntityUser.getStatusCode());
        }
        Order oldOrder = orderResponseEntity.getBody();

        if (oldOrder.getUserId() != userId) {
            log.error(String.format("PUT/user/%d/order/%d: Only order owner can modify order. %s",
                    userId, orderId, HttpStatus.BAD_REQUEST));
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (oldOrder.getBillId() != -1 ) {
            log.error(String.format("PUT/user/%d/order/%d: Can not modify paid order . %s",
                    userId, orderId, HttpStatus.BAD_REQUEST));
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Room room = handle(() -> roomClient.findById(oldOrder.getRoomId()),"rooms").getBody();
        RoomType roomType = handle(() -> roomClient.findByIdRoomType(
                room.getRoomType()), "room types").getBody();
        if (newOrder.getArrivalDate() != null && !newOrder.getArrivalDate().equals(oldOrder.getArrivalDate()) &&
                newOrder.getArrivalDate().toLocalDateTime().isAfter(LocalDateTime.now())) {
                oldOrder.setArrivalDate(newOrder.getArrivalDate());
                is_changed = true;
            }
        if (newOrder.getNightAmount() != null && !newOrder.getNightAmount().equals(oldOrder.getNightAmount())) {
            oldOrder.setNightAmount(newOrder.getNightAmount());
            oldOrder.setCost(getPrice(oldOrder.getNightAmount(), roomType.getPrice(),
                    responseEntityUser.getBody().getOrdersAmount()));
            is_changed = true;
        }
        if (is_changed) {
             orderResponseEntity = handle(() -> ordersClient.modifyOrder(orderId, oldOrder), "orders");
             if (orderResponseEntity.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE){
                Boolean result = taskQueue.offer(new TaskRecord(
                        String.format("/user/%d/order/%d", userId, orderId),
                        () -> modifyUserOrder(username, orderId, newOrder, header), "message"));
                 if (!result) {
                     log.info("[updatePost($postId)] => couldn't schedule task because queue is full");
                     return null;
                 }
                 log.info("Put update test in queue");
                 return new ResponseEntity<>(HttpStatus.OK);
             }
             if (orderResponseEntity.getStatusCode() != HttpStatus.OK) {
                 log.error(String.format("PUT/user/%d/order/%d: Error modify order (order service). %s",
                         userId, orderId, responseEntityUser.getStatusCode().toString()));
                 return new ResponseEntity<>(responseEntityUser.getStatusCode());
             }
        }
        log.info(String.format("PUT/user/%d/order/%d/: Order = %d has modified successfully. %s",
                userId, orderId, orderId, orderResponseEntity.getStatusCode().toString()));
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ServletUriComponentsBuilder
                .fromCurrentServletMapping().path("hotel/user/{userId}/order/{oderId}").build()
                .expand(userId, orderId).toUri());

        return new ResponseEntity<>(orderResponseEntity.getBody(), headers, HttpStatus.OK);
    }

    /* Закрытие заказа администратором */
    @RequestMapping(method = RequestMethod.PUT, value = "user/{userId}/order/{orderId}/close",
            consumes = "application/json", produces="application/json")
    public ResponseEntity<Order> closeOrder(@PathVariable("userId") long userId, @PathVariable("orderId") long orderId)
    {
        ResponseEntity<User> responseEntityUser = handle(() -> usersClient.findOne(userId), "users");
        if (responseEntityUser.getStatusCode() == HttpStatus.NOT_FOUND) {
            log.error(String.format("PUT/user/%d/order/%d/close: User is't found. %s",
                    userId, orderId, HttpStatus.UNAUTHORIZED.toString()));
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
            ResponseEntity<Order> orderResponseEntity = handle(() -> ordersClient.findById(orderId), "orders");
        if (orderResponseEntity.getStatusCode() != HttpStatus.OK) {
            log.error(String.format("PUT/user/%d/order/%d/close: Error get order (order service). %s",
                    userId, orderId, orderResponseEntity.getStatusCode().toString()));
            return orderResponseEntity;
        }
        ResponseEntity<Room> roomResponseEntity = handle(
                () -> roomClient.findById(orderResponseEntity.getBody().getRoomId()), "rooms");
        if (roomResponseEntity.getStatusCode() != HttpStatus.OK) {
            log.error(String.format("PUT/user/%d/order/%d/close: Error get room = %d (room service). %s",
                    userId, orderId,  orderResponseEntity.getBody().getRoomId(),
                    orderResponseEntity.getStatusCode().toString()));
            return new ResponseEntity<>(roomResponseEntity.getStatusCode());
        }

        Long tempRoomTypeId = roomResponseEntity.getBody().getRoomType();
        ResponseEntity<RoomType> roomTypeResponseEntity = handle(() -> roomClient.findByIdRoomType(tempRoomTypeId),
                "room types");
        if (roomTypeResponseEntity.getStatusCode() != HttpStatus.OK) {
            log.error(String.format("PUT/user/%d/order/%d/close: Error get room type = %d (room type service). %s",
                    userId, orderId, roomResponseEntity.getBody().getRoomType(),
                    orderResponseEntity.getStatusCode().toString()));
            return new ResponseEntity<>(roomTypeResponseEntity.getStatusCode());
        }

        roomResponseEntity.getBody().setVacant(true);
        roomTypeResponseEntity.getBody().setAmountVacantRooms(
                roomTypeResponseEntity.getBody().getAmountVacantRooms() + 1);
        Room tempRoom = roomResponseEntity.getBody();
        roomResponseEntity = handle(() -> roomClient.modifyRoom(tempRoom.getId(), tempRoom), "rooms");
        if (roomResponseEntity.getStatusCode() != HttpStatus.OK) {
            log.error(String.format("PUT/user/%d/order/%d/close: Error modify room = %d (room service). %s",
                    userId, orderId, roomResponseEntity.getBody().getRoomType(),
                    orderResponseEntity.getStatusCode().toString()));
            return new ResponseEntity<>(roomResponseEntity.getStatusCode());
        }
        RoomType tempRoomType = roomTypeResponseEntity.getBody();
        Long roomTypeId = tempRoomType.getId();
        roomTypeResponseEntity = handle( () -> roomClient.modifyRoomType(roomTypeId, tempRoomType), "room types");
        if (roomTypeResponseEntity.getStatusCode() != HttpStatus.OK) {
            tempRoom.setVacant(false);
            handle(() -> roomClient.modifyRoom(tempRoom.getId(), tempRoom), "rooms");
            log.error(String.format("PUT/user/%d/order/%d/close: Error modify room type = %d (room type service). %s",
                    userId, orderId, roomTypeResponseEntity.getBody().getId(),
                    orderResponseEntity.getStatusCode().toString()));
            return new ResponseEntity<>(roomResponseEntity.getStatusCode());
        }
        log.info(String.format("PUT/user/%d/order/%d/close: Order = %d has closed successfully. %s",
                userId, orderId, orderId, orderResponseEntity.getStatusCode().toString()));
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ServletUriComponentsBuilder
                .fromCurrentServletMapping().path("hotel/user/{userId}/order/{orderId}").build()
                .expand(userId, orderId).toUri());
        return new ResponseEntity<>(orderResponseEntity.getBody(), headers, HttpStatus.OK);
    }

    /* Удаление заказа авторизованным пользователем */
    @RequestMapping(method = RequestMethod.DELETE, value = "/user/{username}/order/{orderId}")
    public ResponseEntity<Order> deleteOrder(@PathVariable("username") String username,
                                             @PathVariable("orderId") long orderId,
                                             @RequestHeader HttpHeaders header) {

        ResponseEntity<String> authResponse = handle(() -> authClient.getCurrent(header), "auth");
        if (authResponse.getStatusCode() == HttpStatus.UNAUTHORIZED ||
                authResponse.getStatusCode() == HttpStatus.FORBIDDEN) {
            log.error(String.format("GET/user/%s/orders: Token does not exist. %s",
                    username, authResponse.getStatusCode().toString()));
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if (authResponse.getStatusCode() != HttpStatus.OK) {
            log.error(String.format("GET/user/%s/orders: Can not get token. %s",
                    username, authResponse.getStatusCode().toString()));
            return new ResponseEntity<>(authResponse.getStatusCode());
        }
        ObjectMapper mapper = new ObjectMapper();
        JsonNode actualObj;
        try {
            actualObj = mapper.readTree(authResponse.getBody());
        } catch (IOException ex) {
            log.error(String.format("GET/user/%s/order/%d: Error parse json auth file. %s",
                    username, orderId, HttpStatus.INTERNAL_SERVER_ERROR.toString()));
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        ResponseEntity<User> responseEntityUser = handle(() -> usersClient.findByUserName(username), "users");
        if (responseEntityUser.getStatusCode() == HttpStatus.NOT_FOUND) {
            log.error(String.format("DELETE/user/%s/order/%d: User is't found. %s",
                    username, orderId, HttpStatus.UNAUTHORIZED.toString()));
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        else if (responseEntityUser.getStatusCode() != HttpStatus.OK) {
            log.error(String.format("DELETE/user/%s/order/%d: Other error (user service). %s",
                    username, orderId, responseEntityUser.getStatusCode().toString()));
            return new ResponseEntity<>(responseEntityUser.getStatusCode());
        }
        if (!actualObj.get("userAuthentication").get("details").get("username").toString().
                equals("\"" + responseEntityUser.getBody().getUsername() + "\"") ||
                header.get("scope").toString().equals("\"ui\"")) {
            log.error(String.format("GET/user/%s/order/%d: UNAUTHORIZED error . %s",
                    username, orderId, HttpStatus.UNAUTHORIZED.toString()));
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        long userId = responseEntityUser.getBody().getId();
        ResponseEntity<Order> orderResponseEntity = handle(() -> ordersClient.findById(orderId), "orders");
        if (orderResponseEntity.getStatusCode() != HttpStatus.OK) {
            log.error(String.format("DELETE/user/%d/order/%d: Order is't found. %s",
                    userId, orderId, orderResponseEntity.getStatusCode().toString()));
            return new ResponseEntity<>(orderResponseEntity.getStatusCode());
        }
        if (orderResponseEntity.getBody().getArrivalDate().toLocalDateTime().isBefore(LocalDateTime.now())) {
            log.error(String.format("DELETE/user/%d/order/%d: Can not close order if arrival date is before now. %s",
                    userId, orderId, HttpStatus.BAD_REQUEST.toString()));
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        orderResponseEntity = handle(() -> ordersClient.delete(orderId), "orders");
        if (orderResponseEntity.getStatusCode() != HttpStatus.OK){
            log.error(String.format("DELETE/user/%d/order/%d: Error delete order. %s",
                    userId, orderId, orderResponseEntity.getStatusCode()));
        }
        log.info(String.format("PUT/user/%d/order/%d/close: Order = %d has closed successfully. %s",
                userId, orderId, orderId, orderResponseEntity.getStatusCode().toString()));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/token", produces="application/json")
    public ResponseEntity<Object> make_auth(@RequestHeader HttpHeaders header) {
        if (!header.containsKey("password") || !header.containsKey("username") || !header.containsKey("authorization")) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String password = header.get("password").get(0);
        String username = header.get("username").get(0);
        String authorization = header.get("authorization").get(0);

        ResponseEntity<String> authResponse = handle(() -> authClient.makeAuth(password, username, authorization), "auth");
        if (authResponse.getStatusCode() == HttpStatus.OK) {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/json;charset=UTF-8");
            log.info("/token: token get successfully " + HttpStatus.OK.toString());
            return new ResponseEntity<>(authResponse.getBody(), headers, HttpStatus.OK);
        }
        log.error("/token error getting token " + authResponse.getStatusCode().toString());
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    private <T> ResponseEntity<T> handle(Supplier<ResponseEntity<T>> supplier, String serviceName) {
        try {
            ResponseEntity<T> temp = supplier.get();
            return new ResponseEntity<>(temp.getBody(), temp.getHeaders(), temp.getStatusCode());
        } catch (HttpClientErrorException | HttpServerErrorException exc) {
            return new ResponseEntity<T>(HttpStatus.valueOf(exc.getStatusCode().value()));
        } catch (ResourceAccessException exc) {
            return new ResponseEntity<T>(HttpStatus.SERVICE_UNAVAILABLE);

        }
    }

    private <T> ResponseEntity<T> handleDelete(Supplier<Void> supplier) {
        try {
            supplier.get();
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (HttpClientErrorException | HttpServerErrorException exc) {
            return new ResponseEntity<>(HttpStatus.valueOf(exc.getStatusCode().value()));
        }
    }
}
