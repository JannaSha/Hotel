package com.gateway.clients;


import com.gateway.models.RoomType;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;


public class RoomsTypeClient {
    private String serviceUrl = "http://localhost:2222/roomtype";
    private RestTemplate restTemplate;

    public RoomsTypeClient() {
        restTemplate = new RestTemplate();
    }

    public ResponseEntity<RoomType> findByIdRoomType(long id) {
        return restTemplate.getForEntity(serviceUrl + String.format("/%d", id), RoomType.class);
    }

    public ResponseEntity<RoomType> modifyRoomType(long id, RoomType roomType) {
        return restTemplate.exchange(serviceUrl + String.format("/modify/%d", id), HttpMethod.PUT,
                new HttpEntity<>(roomType), RoomType.class);
    }

    public Void deleteRoomType(long id) {
        restTemplate.delete(serviceUrl + String.format("/delete/%d", id));
        return null;
    }

    public ResponseEntity<Object> findAllRoomsType(Integer page, Integer size) {
        return restTemplate.getForEntity(serviceUrl + String.format("/roomtypes?page=%d&size=%d", page, size),
                Object.class);
    }
}