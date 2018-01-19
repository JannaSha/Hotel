package com.orders.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;


import com.orders.controllers.OrderController;
import com.orders.models.Order;
import com.orders.repos.OrderRepository;
import org.junit.Before;
import org.junit.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;


import static org.mockito.MockitoAnnotations.initMocks;

@SpringBootTest
@WebAppConfiguration
@ContextConfiguration(classes = {TestContext.class})
public class OrdersControllerTest {

    private ObjectMapper mapper = new ObjectMapper();

    @InjectMocks
    private OrderController orderController;

    @Mock
    private OrderRepository orderRepository;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
    }

    @Test
    public void shouldFindOrderById() {
        Order order = new Order(1, 100, 2, Timestamp.valueOf("2017-01-01 14:14:14"), 1, 3,
                Timestamp.valueOf("2017-01-01 14:14:14"), new BigDecimal(123));
        Mockito.when(orderRepository.findById(order.getId())).thenReturn(order);
        try {
            mockMvc.perform(get("/order/1"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.roomId", is(100)))
                    .andExpect(jsonPath("$.userId", is(2)))
                    .andExpect(jsonPath("$.orderDate", is("2017-01-01/11:14:14")))
                    .andExpect(jsonPath("$.billId", is(1)))
                    .andExpect(jsonPath("$.nightAmount", is(3)))
                    .andExpect(jsonPath("$.arrivalDate", is("2017-01-01/11:14:14")))
                    .andExpect(jsonPath("$.cost", is(123)));

        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    @Test
    public void shouldNotFindOrderById() {
        Mockito.when(orderRepository.findById(1L)).thenReturn(null);
        try {
            mockMvc.perform(get("/order/1"))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    @Test
    public void shouldFindAllOrders() {
        List<Order> orders = new LinkedList<>();
        orders.add(new Order(1L, 100, 2, Timestamp.valueOf("2017-01-01 14:14:14"), 1, 3,
                Timestamp.valueOf("2017-01-01 14:14:14"), new BigDecimal(123)));
        orders.add(new Order(2L, 100, 2, Timestamp.valueOf("2017-01-02 14:14:14"), 1, 3,
                Timestamp.valueOf("2017-02-01 14:14:14"), new BigDecimal(123)));
        Integer page = 0;
        Integer size = 2;
        Mockito.when(orderRepository.findAll(new PageRequest(page, size))).thenReturn(orders);
        try {
            mockMvc.perform(get(String.format("/order/orders?page=%d&size=%d", page, size)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
        } catch (Exception exc) {
            exc.printStackTrace();
        }
        assertEquals("bad", orders, orderController.findAllOrders(page, size, null).getBody());
    }

    @Test
    public void shouldNotFindAllOrdersBadPage() {
        Integer page = -1;
        Integer size = 2;
        try {
            mockMvc.perform(get(String.format("/order/orders?page=%d&size=%d", page, size)))
                    .andExpect(status().isBadRequest());
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    @Test
    public void shouldNotFindAllOrdersBadSize() {
        Integer page = 1;
        Integer size = -2;
        try {
            mockMvc.perform(get(String.format("/order/orders?page=%d&size=%d", page, size)))
                    .andExpect(status().isBadRequest());
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    @Test
    public void shouldFindOrdersByUserId() {
        List<Order> orders = new LinkedList<>();
        orders.add(new Order(1, 100, 1, Timestamp.valueOf("2017-01-01 14:14:14"), 1, 3,
                Timestamp.valueOf("2017-01-01 14:14:14"), new BigDecimal(123)));
        orders.add(new Order(2, 100, 1, Timestamp.valueOf("2017-01-02 14:14:14"), 1, 3,
                Timestamp.valueOf("2017-02-01 14:14:14"), new BigDecimal(123)));
        Mockito.when(orderRepository.findByUserId(1L)).thenReturn(orders);
        try {
            mockMvc.perform(get("/order/user/1"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
        } catch (Exception exc) {
            exc.printStackTrace();
        }
        assertEquals("bad", orders, orderController.findByUserId(1, null).getBody());

    }

    @Test
    public void shouldNotFindOrdersByUserId() {
        Mockito.when(orderRepository.findByUserId(1L)).thenReturn(null);
        try {
            mockMvc.perform(get("/order/user/1"))
                    .andExpect(status().isNotFound());
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    @Test
    public void shouldDeleteUserById() {
        Order order = new Order(1, 100, 2, Timestamp.valueOf("2017-01-01 14:14:14"), 1, 3,
                Timestamp.valueOf("2017-01-01 14:14:14"), new BigDecimal(123));
        Mockito.when(orderRepository.exists(order.getId())).thenReturn(true);
        Mockito.doNothing().when(orderRepository).delete(order.getId());
        try {
            mockMvc.perform(delete(String.format("/order/delete/%d", order.getId())))
                    .andExpect(status().isOk());
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    @Test
    public void shouldNotDeleteUserById() {
        long roomId = 1L;
        Mockito.when(orderRepository.exists(roomId)).thenReturn(false);
        try {
            mockMvc.perform(delete(String.format("/order/delete/%d", roomId)))
                    .andExpect(status().isNotFound());
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    @Test
    public void shouldCreateOrder() {
        Order order = new Order(1, 100, 2, Timestamp.valueOf("2018-01-01 14:14:14"), 1, 3,
                Timestamp.valueOf("2018-01-01 14:14:14"), new BigDecimal(123));
        String json = "";
        try {
            json = mapper.writeValueAsString(order);
        } catch (Exception exc) {
            exc.printStackTrace();
        }
        Mockito.when(orderRepository.exists(1L)).thenReturn(false);
        Mockito.when(orderRepository.save(any(Order.class))).thenReturn(order);
        try {
            mockMvc.perform(post("/order/create")
                    .content(json)
                    .header("Content-Type",  "application/json"))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.roomId", is(100)))
                    .andExpect(jsonPath("$.userId", is(2)))
                    .andExpect(jsonPath("$.orderDate", is("2018-01-01/11:14:14")))
                    .andExpect(jsonPath("$.billId", is(1)))
                    .andExpect(jsonPath("$.nightAmount", is(3)))
                    .andExpect(jsonPath("$.arrivalDate", is("2018-01-01/11:14:14")))
                    .andExpect(jsonPath("$.cost", is(123)))
                    .andExpect(header().string("location", "http://localhost/order/1"));

        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    @Test
    public void shouldNotCreateOrderServerError() {
        Order order = new Order(1, 100, 2, Timestamp.valueOf("2018-01-01 14:14:14"), 1, 3,
                Timestamp.valueOf("2018-01-01 14:14:14"), new BigDecimal(123));
        String json = "";
        try {
            json = mapper.writeValueAsString(order);
        } catch (Exception exc) {
            exc.printStackTrace();
        }
        Mockito.when(orderRepository.exists(1L)).thenReturn(false);
        Mockito.when(orderRepository.save(any(Order.class))).thenReturn(null);
        try {
            mockMvc.perform(post("/order/create")
                    .content(json)
                    .header("Content-Type",  "application/json"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));

        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    @Test
    public void shouldNotCreateOrderConflict() {
        Order order = new Order(1, 100, 2, Timestamp.valueOf("2018-01-01 14:14:14"), 1, 3,
                Timestamp.valueOf("2018-01-01 14:14:14"), new BigDecimal(123));
        String json = "";
        try {
            json = mapper.writeValueAsString(order);
        } catch (Exception exc) {
            exc.printStackTrace();
        }
        Mockito.when(orderRepository.exists(1L)).thenReturn(true);
        try {
            mockMvc.perform(post("/order/create")
                    .content(json)
                    .header("Content-Type",  "application/json"))
                    .andExpect(status().isConflict())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));

        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

}