package com.gateway.clients;


import com.gateway.models.Order;
import com.netflix.ribbon.proxy.annotation.Http;
import org.aspectj.weaver.ast.Or;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.List;


public class OrdersClient {

    private String serviceUrl = "http://localhost:5555/order";
    private RestTemplate restTemplate;

    public OrdersClient() {
        restTemplate = new RestTemplate();
    }

    public ResponseEntity<Order> findById(long id) {
        return restTemplate.getForEntity(serviceUrl + String.format("/%d", id), Order.class);
    }

    public ResponseEntity<Order> createOrder(Order order) {
        return restTemplate.exchange(serviceUrl + "/create", HttpMethod.POST, new HttpEntity<>(order), Order.class);
    }

    public ResponseEntity<Order[]> findByUserId(long id) {
        return restTemplate.getForEntity(serviceUrl + String.format("/user/%d", id), Order[].class);
    }

    public ResponseEntity<Order> modifyOrder(long id, Order order) {
        return restTemplate.exchange(serviceUrl + String.format("/modify/%d", id),
                HttpMethod.PUT, new HttpEntity<>(order), Order.class);
    }

    public Void delete(long id) {
        restTemplate.delete(serviceUrl + String.format("/delete/%d", id));
        return null;
    }

}
