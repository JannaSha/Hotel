package com.gateway.clients;


import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

public class StatisticClient {
    private String serviceUrl = "http://localhost:7777/statistic";
    private RestTemplate restTemplate;

    public StatisticClient() {
        restTemplate = new RestTemplate();
    }

    public ResponseEntity<Object> getUserReport() {
        return restTemplate.getForEntity(serviceUrl + "/userrepost", Object.class);
    }

    public ResponseEntity<Object> getRoomReport() {
        return restTemplate.getForEntity(serviceUrl + "/roomreport", Object.class);
    }

    public ResponseEntity<Object> getAuthReport() {
        return restTemplate.getForEntity(serviceUrl + "/auth", Object.class);
    }

}
