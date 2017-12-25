package com.orders.controllers;


import com.fasterxml.jackson.databind.util.JSONPObject;
import com.orders.models.Order;
import com.orders.repos.OrderRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import sun.net.www.http.HttpClient;

import javax.validation.Valid;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@RestController
@RequestMapping(value = "/order" )
public class OrderController {

    @Autowired
    private OrderRepository repository;

    private static final Logger log = Logger.getLogger(OrderController.class);

    private JSONPObject getJSONObject(String message) {
        return new JSONPObject("message", message);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/create", consumes = "application/json",
            produces="application/json")
    public ResponseEntity<Order> createOrder(@RequestBody @Valid Order order) {
        ResponseEntity<Order> response;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=UTF-8");
        if (order.getOrderDate() == null) {
            order.setOrderDate(Timestamp.from(Instant.now()));
        }
        if (!repository.exists(order.getId())) {
            if (repository.save(order) != null) {
                headers.setLocation(ServletUriComponentsBuilder
                        .fromCurrentServletMapping().path("/order/{id}").build()
                        .expand(order.getId()).toUri());
                response = new ResponseEntity<>(order, headers, HttpStatus.CREATED);
                log.info("Create order id = " + order.getId());
            } else {
//                response = new ResponseEntity<>(getJSONObject("Error saving order in db id = " + order.getId()),
//                        headers, HttpStatus.INTERNAL_SERVER_ERROR);
                response = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                log.error("Error saving order in db id = " + order.getId());
            }
        }
        else {
//            response = new ResponseEntity<>(getJSONObject("Error creating of order id = " + order.getId()),
//                    headers, HttpStatus.CONFLICT);
            response = new ResponseEntity<>(HttpStatus.CONFLICT);
            log.error("Error creating of order id = " + order.getId());
        }
        return response;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/orders", params = {"page", "size"})
    public ResponseEntity<List<Order>> findAllOrders(@RequestParam("page") Integer page,
                                                @RequestParam("size") Integer size) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=UTF-8");
        ResponseEntity<List<Order>> response;
        if (page < 0 || size < 0) {
//            response = new ResponseEntity<>(getJSONObject("Error: page < 0 or size < 0"), headers, HttpStatus.BAD_REQUEST);
            response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            log.error("Error: page < 0 or size < 0");
        }
        else {
            Pageable pageable = new PageRequest(page, size);
            List<Order> roomTypes = repository.findAll(pageable);
            response = new ResponseEntity<>(roomTypes, headers, HttpStatus.OK);
            log.info("Successfully get orders");
        }
        return response;
    }


    @RequestMapping(method =  RequestMethod.GET, value = "/{id}")
    public ResponseEntity<Order> findById(@PathVariable("id") long id) {
        ResponseEntity<Order> response;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=UTF-8");

        Order order = repository.findById(id);
        if (order != null)  {
            response = new ResponseEntity<>(order, headers, HttpStatus.OK);
            log.info("Get order successfully id = " + id);
        }
        else {
            response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
//            response = new ResponseEntity<>(getJSONObject("Error: order is not found id = " + id),
//                    headers, HttpStatus.NOT_FOUND);
//            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "\"Error: order is not found id = \" + id");
            log.error("Error: order is not found id = " + id);
        }
        return response;
    }

    @RequestMapping(method =  RequestMethod.GET, value = "/user/{id}",  produces="application/json;charset=UTF-8")
    public ResponseEntity<List<Order>> findByUserId(@PathVariable("id") long id) {
        ResponseEntity<List<Order>> response;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=UTF-8");

        List<Order> order = repository.findByUserId(id);
        if (order != null && !order.isEmpty())  {
            response = new ResponseEntity<>(order, headers, HttpStatus.OK);
            log.info(String.format("Get order for user id = %d successfully ", id));
        }
        else {
//            response = new ResponseEntity<Object>(getJSONObject(
//                    String.format("Error: orders sre not found for user id = %d successfully", id)),
//                    headers, HttpStatus.NOT_FOUND);
            response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
            log.error(String.format("Error: orders sre not found for user id = %d successfully", id));
        }
        return response;
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/modify/{id}", consumes = "application/json",
            produces="application/json")
    public ResponseEntity<Order> modifyOrder(@PathVariable("id") long id, @RequestBody @Valid Order order) {
        ResponseEntity<Order> response;
        HttpHeaders headers = new HttpHeaders();

        if (!repository.exists(id)) {
//            response = new ResponseEntity<>(getJSONObject("Error modify order, room is not found id = " + id),
//                    headers, HttpStatus.NOT_FOUND);
            response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
            log.error("Error modify order, room is not found id = " + id);
        }
        else if (order.getId() == id && repository.save(order) != null) {
            response = new ResponseEntity<>(order, headers, HttpStatus.OK);
            log.error("Successfully modify order id = " + id);
        }
        else {
//            response = new ResponseEntity<>(getJSONObject("Error modify order, database error id = " + id),
//                    headers, HttpStatus.CONFLICT);
            response = new ResponseEntity<>(HttpStatus.CONFLICT);
            log.error("Error modify order, database error id = " + id);
        }
        return response;
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/delete/{id}")
    public ResponseEntity<Order> delete(@PathVariable("id") long id) {
        ResponseEntity<Order> response;

        if (!repository.exists(id)) {
//            response = new ResponseEntity<>(getJSONObject("Error trying deleting order id = " + id),
//                    new HttpHeaders(), HttpStatus.NOT_FOUND);
            response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
            log.error("Error trying deleting order id = " + id);
        }
        else {
            repository.delete(id);
            response = new ResponseEntity<>(new HttpHeaders(), HttpStatus.OK);
            log.info("Successfully delete order id = " + id);
        }
        return response;
    }


}