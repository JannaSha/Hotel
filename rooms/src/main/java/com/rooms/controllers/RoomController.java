package com.rooms.controllers;


import com.fasterxml.jackson.databind.util.JSONPObject;
import com.rooms.models.Room;
import com.rooms.repo.RoomsRepository;
import com.rooms.repo.RoomsTypesRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.data.domain.Pageable;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/room")
@Validated
public class RoomController {
    @Autowired
    RoomsRepository repository;
    @Autowired
    RoomsTypesRepository repositoryType;

    private static final Logger log = Logger.getLogger(RoomTypeController.class);

    private JSONPObject getJSONObject(String message) {
        return new JSONPObject("message", message);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/create",
            consumes = "application/json", produces="application/json")
    public ResponseEntity<Room> createRoom(@RequestBody @Valid Room room) {
        ResponseEntity<Room> response;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=UTF-8");

        if (!repository.exists(room.getId()) && repository.save(room) != null &&
                repositoryType.exists(room.getRoomType())) {
            headers.setLocation(ServletUriComponentsBuilder
                    .fromCurrentServletMapping().path("/room/{id}").build()
                    .expand(room.getId()).toUri());
            response = new ResponseEntity<>(room, headers, HttpStatus.CREATED);
            log.info("Create room id = " + room.getId());
        }
        else {
//            response = new ResponseEntity<>(getJSONObject("Error creating of room id = " + room.getId()),
//                    headers, HttpStatus.CONFLICT);
            response = new ResponseEntity<>(HttpStatus.CONFLICT);
            log.error("Error creating of room id = " + room.getId());
        }
        return response;
    }


    @RequestMapping(method = RequestMethod.PUT, value = "/modify/{id}", consumes = "application/json",
            produces="application/json")
    public ResponseEntity<Room> modifyRoom(@PathVariable("id") long id, @RequestBody @Valid Room room) {
        ResponseEntity<Room> response;
        HttpHeaders headers = new HttpHeaders();

        if (!repository.exists(id)) {
//            response = new ResponseEntity<>(getJSONObject("Error modify room, room is not found id = " + id),
//                    headers, HttpStatus.NOT_FOUND);
            response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
            log.error("Error modify room, room is not found id = " + id);
        }
        else if (room.getId() == id && repository.save(room) != null) {
            response = new ResponseEntity<>(room, headers, HttpStatus.OK);
            log.error("Successfully modify room id = " + id);
        }
        else {
//            response = new ResponseEntity<>(getJSONObject("Error modify room, database error id = " + id),
//                    headers, HttpStatus.CONFLICT);
            response = new ResponseEntity<>(HttpStatus.CONFLICT);
            log.error("Error modify room, database error id = " + id);
        }
        return response;
    }


    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    public ResponseEntity<Room> findById(@PathVariable("id") long id){
        ResponseEntity<Room> response;
        Room room;

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=UTF-8");
        if (!repository.exists(id)) {
//            response = new ResponseEntity<>(getJSONObject("Error get room, room is not found id = "),
//                    headers, HttpStatus.NOT_FOUND);
            response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
            log.error("Error get room, room is not found id = " + id);
        }
        else {
            room = repository.findOne(id);
            response = new ResponseEntity<>(room, headers, HttpStatus.OK);
            log.info("Successfully get room id = " + id);
        }
        return response;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/rooms", params = {"page", "size"})
    public ResponseEntity<List<Room>> findAll(@RequestParam("page") Integer page, @RequestParam("size") Integer size) {
        ResponseEntity<List<Room>> response;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=UTF-8");

        if (page < 0 || size < 0) {
//            response = new ResponseEntity<>(getJSONObject("Error get rooms: page < 0 or size < 0"),
//                    headers, HttpStatus.BAD_REQUEST);
            response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            log.error("Error get rooms: page < 0 or size < 0");
        }
        else {
            Pageable pageable = new PageRequest(page, size);
            List<Room> rooms = repository.findAll(pageable);
            response = new ResponseEntity<>(rooms, headers, HttpStatus.OK);
            log.info("Successfully get rooms");
        }
        return response;
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/delete/{id}")
    public ResponseEntity<Room> deleteRoom (@PathVariable("id") long id) {
        ResponseEntity<Room> response;

        if (!repository.exists(id)) {
//            response = new ResponseEntity<>(getJSONObject("Error delete room, room is not found id = " + id),
//                    new HttpHeaders(), HttpStatus.NOT_FOUND);
            response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
            log.error("Error delete room, room is not found id = " + id);
        }
        else {
            repository.delete(id);
            response = new ResponseEntity<>(new HttpHeaders(), HttpStatus.OK);
            log.info("Deleted room id = " + id);
        }
        return response;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/roomtype/{id}", params = {"vacant"})
    public ResponseEntity<List<Room>> findByType(@PathVariable("id") long id, @RequestParam("vacant") boolean vacant) {
        ResponseEntity<List<Room>> response;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=UTF-8");

        if (repositoryType.exists(id)) {
            List<Room> rooms = repository.findByRoomTypeAndVacant(id, vacant);
            response = new ResponseEntity<>(rooms, headers, HttpStatus.OK);
            log.info("Successfully get room id = " + id);
        }
        else {
//            response = new ResponseEntity<>(getJSONObject("Error get room id = " + id + HttpStatus.NOT_FOUND.toString()),
//                    headers, HttpStatus.NOT_FOUND);
            response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
            log.info("Error get room id = " + id + HttpStatus.NOT_FOUND.toString());
        }
        return response;
    }

}
