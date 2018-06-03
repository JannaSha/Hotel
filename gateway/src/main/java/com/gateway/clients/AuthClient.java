package com.gateway.clients;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

public class AuthClient {

    private String serviceUrl = "http://localhost:8081/";
    private RestTemplate restTemplate;

    public AuthClient() {
        restTemplate = new RestTemplate();
    }

    public ResponseEntity<String> getCurrent (HttpHeaders headers) {
        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
        return restTemplate.exchange(serviceUrl + "/current", HttpMethod.GET, entity, String.class);
    }
    public ResponseEntity<String> makeAuth (String password, String username, String authorization) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization",  authorization);
        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
        String tempUrl = String.format("http://localhost:8081/oauth/token?redirect_uri=https:/" +
                "/www.yandex.ru&grant_type=password&username=%s&password=%s", username, password);
        return restTemplate.exchange(tempUrl, HttpMethod.POST, entity, String.class);
    }
}

