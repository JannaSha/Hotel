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


public class OrdersClient extends TokenClient {

    private String serviceUrl = "http://localhost:5555/order";
    private RestTemplate restTemplate;

    public OrdersClient() {
        restTemplate = new RestTemplate();
    }

    public ResponseEntity<Order> findById(long id) {
        if (setToken(serviceUrl + "/token", restTemplate)) {
            HttpHeaders appHashHeaders = new HttpHeaders();
            appHashHeaders.add("token", token);
            HttpEntity<String> entity = new HttpEntity<>("parameters", appHashHeaders);
            return restTemplate.exchange(serviceUrl + String.format("/%d", id), HttpMethod.GET, entity, Order.class);
        } else {
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    public ResponseEntity<Order> createOrder(Order order) {
        if (setToken(serviceUrl + "/token", restTemplate)) {
            HttpHeaders appHashHeaders = new HttpHeaders();
            appHashHeaders.add("token", token);
            HttpEntity<Order> entity = new HttpEntity<>(order, appHashHeaders);
            return restTemplate.exchange(serviceUrl + "/create", HttpMethod.POST, entity, Order.class);
        } else {
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    public ResponseEntity<Order[]> findByUserId(long id) {
        if (setToken(serviceUrl + "/token", restTemplate)) {
            HttpHeaders appHashHeaders = new HttpHeaders();
            appHashHeaders.add("token", token);
            HttpEntity<String> entity = new HttpEntity<>("parameters", appHashHeaders);
            return restTemplate.exchange(serviceUrl + String.format("/user/%d", id), HttpMethod.GET, entity, Order[].class);
        } else {
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    public ResponseEntity<Order> modifyOrder(long id, Order order) {
        if (setToken(serviceUrl + "/token", restTemplate)) {
            HttpHeaders appHashHeaders = new HttpHeaders();
            appHashHeaders.add("token", token);
            HttpEntity<Order> entity = new HttpEntity<>(order, appHashHeaders);
            return restTemplate.exchange(serviceUrl + String.format("/modify/%d", id),
                HttpMethod.PUT, entity, Order.class);
        } else {
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    public ResponseEntity<Order> delete(long id) {
        if (setToken(serviceUrl + "/token", restTemplate)) {
            HttpHeaders appHashHeaders = new HttpHeaders();
            appHashHeaders.add("token", token);
            HttpEntity<String> entity = new HttpEntity<>("parameters", appHashHeaders);
            return restTemplate.exchange(serviceUrl + String.format("/delete/%d", id), HttpMethod.DELETE, entity, Order.class);
        } else {
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

}
