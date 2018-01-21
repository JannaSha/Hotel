package com.gateway.clients;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import java.security.Principal;
import java.sql.Timestamp;

public class AuthClient {

    private String serviceUrl = "http://localhost:8081/auth";
    private RestTemplate restTemplate;

    public AuthClient() {
        restTemplate = new RestTemplate();
    }

    public ResponseEntity<String> getCurrent (HttpHeaders headers) {
        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
        System.out.println(entity.getHeaders());
        return restTemplate.exchange(serviceUrl + "/current", HttpMethod.GET, entity, String.class);
    }
    public ResponseEntity<String> makeAuth (String password, String username, String secret) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + secret);
        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
//        System.out.println(entity.getHeaders());
        String tempUrl = "http://localhost:8081/oauth/token?grant_type=password&redirect_uri=https://www.yandex.ru&username="
                + username + "&password=" + password;
        return restTemplate.exchange(tempUrl, HttpMethod.POST, entity, String.class);
    }
}

