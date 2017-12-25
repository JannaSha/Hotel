package com.gateway.clients;

import com.gateway.models.User;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;


public class UsersClient {
    final private String serviceUrl = "http://localhost:1111/user";

    private RestTemplate restTemplate;

    public UsersClient() {
        restTemplate = new RestTemplate();
    }

    public ResponseEntity<User> findByPassportNumber(long number) {
        return restTemplate.getForEntity(serviceUrl + String.format("/passport/%d", number), User.class);
    }

    public ResponseEntity<User> modifyUser(long id, User user) {
        return restTemplate.exchange(serviceUrl + String.format("/modify/%d", id), HttpMethod.PUT,
                new HttpEntity<>(user), User.class);
    }

    public ResponseEntity<User> createUser(User user) {
        return restTemplate.exchange(serviceUrl + "/create", HttpMethod.POST, new HttpEntity<>(user), User.class);
    }

    public ResponseEntity<User> findOne(long id) {
        return restTemplate.getForEntity(serviceUrl + String.format("/%d", id), User.class);
    }

}

