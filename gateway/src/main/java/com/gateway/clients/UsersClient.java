package com.gateway.clients;

import com.gateway.models.User;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;


public class UsersClient extends TokenClient {
    final private String serviceUrl = "http://localhost:1111/user";
    private RestTemplate restTemplate;


    public UsersClient() {
        restTemplate = new RestTemplate();
    }

//    public ResponseEntity<User> findByPassportNumber(long number) {
//        return restTemplate.getForEntity(serviceUrl + String.format("/passport/%d", number), User.class);
//    }

    public ResponseEntity<User> findByUserName(String username) {
        if (setToken(serviceUrl + "/token", restTemplate)) {
            HttpHeaders appHashHeaders = new HttpHeaders();
            appHashHeaders.add("token", token);
            HttpEntity<String> entity = new HttpEntity<>("parameters", appHashHeaders);
            return restTemplate.exchange(serviceUrl + String.format("/username/%s", username), HttpMethod.GET,
                entity, User.class);
        } else {
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    public ResponseEntity<User> modifyUser(long id, User user) {
        if (setToken(serviceUrl + "/token", restTemplate)) {
            HttpHeaders appHashHeaders = new HttpHeaders();
            appHashHeaders.add("token", token);
            HttpEntity<User> entity = new HttpEntity<>(user, appHashHeaders);
            return restTemplate.exchange(serviceUrl + String.format("/modify/%d", id), HttpMethod.PUT,
                entity, User.class);
        } else {
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

//    public ResponseEntity<User> createUser(User user) {
//        return restTemplate.exchange(serviceUrl + "/create", HttpMethod.POST, new HttpEntity<>(user), User.class);
//    }

    public ResponseEntity<User> findOne(long id) {
        if (setToken(serviceUrl + "/token", restTemplate)) {
            HttpHeaders appHashHeaders = new HttpHeaders();
            appHashHeaders.add("token", token);
            HttpEntity<String> entity = new HttpEntity<>("parameters", appHashHeaders);
            return restTemplate.exchange(serviceUrl + String.format("/%d", id), HttpMethod.GET, entity, User.class);
        } else {
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

}

