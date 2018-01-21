package com.gateway.clients;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gateway.TokenManage;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.util.function.Supplier;

public abstract class TokenClient {
    Timestamp lastTokenTime;
    String token;
    TokenManage tokenManage = new TokenManage();

    boolean setToken(String path, RestTemplate restTemplate) {
        if (token == null || lastTokenTime == null || !tokenManage.checkToken(lastTokenTime)) {
            HttpHeaders appHashHeaders = new HttpHeaders();
            ResponseEntity<String> tokenResponse;
            appHashHeaders.add("Authorization", tokenManage.getHashAppParams());
            HttpEntity<String> entity = new HttpEntity<>("parameters", appHashHeaders);
            try {
                tokenResponse = restTemplate.exchange(path, HttpMethod.GET, entity, String.class);
            } catch (HttpClientErrorException | HttpServerErrorException | ResourceAccessException exc) {
                return false;
            }
            if (!tokenResponse.getHeaders().containsKey("token")) {
                return false;
            }
            token = tokenResponse.getHeaders().get("token").get(0);
            lastTokenTime = new Timestamp(System.currentTimeMillis());
        }
        return true;
    }
}
