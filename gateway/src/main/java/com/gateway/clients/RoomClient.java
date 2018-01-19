package com.gateway.clients;


import com.gateway.models.Room;
import com.gateway.models.RoomType;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;



public class RoomClient extends TokenClient {

    private String serviceUrl = "http://localhost:2222/room";
    private String serviceTypeUrl = "http://localhost:2222/roomtype";

    private RestTemplate restTemplate;

    public RoomClient() {
        restTemplate = new RestTemplate();
    }


    public ResponseEntity<Room> findById(long id) {
        if (setToken(serviceUrl + "/token", restTemplate)) {
            HttpHeaders appHashHeaders = new HttpHeaders();
            appHashHeaders.add("token", token);
            HttpEntity<String> entity = new HttpEntity<>("parameters", appHashHeaders);
            return restTemplate.exchange(serviceUrl + String.format("/%d", id), HttpMethod.GET, entity, Room.class);
        } else {
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    public ResponseEntity<Room[]> findByType(long id, boolean vacant) {
        if (setToken(serviceUrl + "/token", restTemplate)) {
            HttpHeaders appHashHeaders = new HttpHeaders();
            appHashHeaders.add("token", token);
            HttpEntity<String> entity = new HttpEntity<>("parameters", appHashHeaders);
            return restTemplate.exchange(serviceUrl + String.format("/roomtype/%d?vacant=%b", id, vacant),
                    HttpMethod.GET, entity, Room[].class);
        } else {
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    public ResponseEntity<Room> modifyRoom(long id, Room room) {
        if (setToken(serviceUrl + "/token", restTemplate)) {
            HttpHeaders appHashHeaders = new HttpHeaders();
            appHashHeaders.add("token", token);
            HttpEntity<Room> entity = new HttpEntity<>(room, appHashHeaders);
            return restTemplate.exchange(serviceUrl + String.format("/modify/%d", id), HttpMethod.PUT,
                entity, Room.class);
        } else {
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    public ResponseEntity<Object> findAll(Integer page, Integer size) {
        if (setToken(serviceUrl + "/token", restTemplate)) {
            HttpHeaders appHashHeaders = new HttpHeaders();
            appHashHeaders.add("token", token);
            HttpEntity<String> entity = new HttpEntity<>("parameters", appHashHeaders);
            return restTemplate.exchange(serviceUrl + String.format("/rooms?page=%d&size=%d", page, size),
                    HttpMethod.GET, entity, Object.class);
        } else {
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    public ResponseEntity<RoomType> findByIdRoomType(long id) {
        if (setToken(serviceTypeUrl + "/token", restTemplate)) {
            HttpHeaders appHashHeaders = new HttpHeaders();
            appHashHeaders.add("token", token);
            HttpEntity<String> entity = new HttpEntity<>("parameters", appHashHeaders);
            return restTemplate.exchange(serviceTypeUrl + String.format("/%d", id), HttpMethod.GET, entity, RoomType.class);
        } else {
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    public ResponseEntity<RoomType> modifyRoomType(long id, RoomType roomType) {
        if (setToken(serviceTypeUrl + "/token", restTemplate)) {
            HttpHeaders appHashHeaders = new HttpHeaders();
            appHashHeaders.add("token", token);
            HttpEntity<RoomType> entity = new HttpEntity<>(roomType, appHashHeaders);
            return restTemplate.exchange(serviceTypeUrl + String.format("/modify/%d", id), HttpMethod.PUT,
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
        if (setToken(serviceTypeUrl + "/token", restTemplate)) {
            HttpHeaders appHashHeaders = new HttpHeaders();
            appHashHeaders.add("token", token);
            HttpEntity<String> entity = new HttpEntity<>("parameters", appHashHeaders);
            return restTemplate.exchange(serviceTypeUrl + String.format("/roomtypes?page=%d&size=%d", page, size),
                    HttpMethod.GET, entity, Object.class);
        } else {
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }
}