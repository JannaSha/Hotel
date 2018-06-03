package com.gateway.clients;


import com.gateway.models.RoomType;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;


public class RoomsTypeClient extends TokenClient {
    private String serviceUrl = "http://localhost:2222/roomtype";
    private RestTemplate restTemplate;

    public RoomsTypeClient() {
        restTemplate = new RestTemplate();
    }

    public ResponseEntity<RoomType> findByIdRoomType(long id) {
        if (setToken(serviceUrl + "/token", restTemplate)) {
            HttpHeaders appHashHeaders = new HttpHeaders();
            appHashHeaders.add("token", token);
            HttpEntity<String> entity = new HttpEntity<>("parameters", appHashHeaders);
            return restTemplate.exchange(serviceUrl + String.format("/%d", id), HttpMethod.GET, entity, RoomType.class);
        } else {
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    public ResponseEntity<RoomType> modifyRoomType(long id, RoomType roomType) {
        if (setToken(serviceUrl + "/token", restTemplate)) {
            HttpHeaders appHashHeaders = new HttpHeaders();
            appHashHeaders.add("token", token);
            HttpEntity<RoomType> entity = new HttpEntity<>(roomType, appHashHeaders);
            return restTemplate.exchange(serviceUrl + String.format("/modify/%d", id), HttpMethod.PUT,
                entity, RoomType.class);
        } else {
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

//    public Void deleteRoomType(long id) {
//        restTemplate.delete(serviceUrl + String.format("/delete/%d", id));
//        return null;
//    }

    public ResponseEntity<Object> findAllRoomsType(Integer page, Integer size) {
        if (setToken(serviceUrl + "/token", restTemplate)) {
            HttpHeaders appHashHeaders = new HttpHeaders();
            appHashHeaders.add("token", token);
            HttpEntity<String> entity = new HttpEntity<>("parameters", appHashHeaders);
            return restTemplate.exchange(serviceUrl + String.format("/roomtypes?page=%d&size=%d", page, size),
                HttpMethod.GET, entity, Object.class);
        } else {
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }
}