package com.gateway.clients;


import com.gateway.models.Room;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedList;
import java.util.List;


public class RoomClient {

    private String serviceUrl = "http://localhost:2222/room";
    private RestTemplate restTemplate;

    public RoomClient() {
        restTemplate = new RestTemplate();
    }

    public ResponseEntity<Room> findById(long id) {
        return restTemplate.getForEntity(serviceUrl + String.format("/%d", id), Room.class);
    }

    public ResponseEntity<Room[]> findByType(long id, boolean vacant) {
        return restTemplate.getForEntity(serviceUrl + String.format("/roomtype/%d?vacant=%b", id, vacant),
                Room[].class);
    }

    public ResponseEntity<Room> modifyRoom(long id, Room room) {
        return restTemplate.exchange(serviceUrl + String.format("/modify/%d", id), HttpMethod.PUT,
                new HttpEntity<>(room),Room.class);
    }

    public ResponseEntity<Object> findAll(Integer page, Integer size) {
         return restTemplate.getForEntity(serviceUrl + String.format("/rooms?page=%d&size=%d", page, size),
                 Object.class);
    }
}