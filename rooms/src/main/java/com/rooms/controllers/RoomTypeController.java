package com.rooms.controllers;


import com.fasterxml.jackson.databind.util.JSONPObject;
import com.rooms.models.RoomType;
import com.rooms.repo.RoomsTypesRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "roomtype")
@Validated
public class RoomTypeController {

    @Autowired
    RoomsTypesRepository repository;

    private static final Logger log = Logger.getLogger(RoomTypeController.class);

    private JSONPObject getJSONObject(String message) {
        return new JSONPObject("message", message);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/create")
    public ResponseEntity<RoomType> createRoomType(@RequestBody @Valid RoomType roomType) {
        ResponseEntity<RoomType> response;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=UTF-8");

        if (repository.save(roomType) != null) {
            headers.setLocation(ServletUriComponentsBuilder
                    .fromCurrentServletMapping().path("/roomtype/{id}").build()
                    .expand(roomType.getId()).toUri());
            response = new ResponseEntity<>(roomType, headers, HttpStatus.CREATED);
            log.info("Create room successfully id = " + roomType.getId());
        }
        else {
//            response = new ResponseEntity<>(getJSONObject("Conflict during creation room type  = " + roomType.toString()),
//                    headers, HttpStatus.CONFLICT);
            response = new ResponseEntity<>(HttpStatus.CONFLICT);
            log.error("Conflict during creation room type  = " + roomType.toString());
        }
        return response;
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/modify/{id}",
            consumes = "application/json", produces="application/json")
    public ResponseEntity<RoomType> modifyRoomType(@PathVariable("id") long id, @RequestBody @Valid RoomType room) {
        ResponseEntity<RoomType> response;
        HttpHeaders headers = new HttpHeaders();

        if (!repository.exists(id)) {
//            response = new ResponseEntity<>(getJSONObject("Error trying modify room type id = " + id),
//                    headers, HttpStatus.NOT_FOUND);
            response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
            log.error("Error trying modify room type id = " + id);
        }
        else if (room.getId() == id && repository.save(room) != null) {
            response = new ResponseEntity<>(room, headers, HttpStatus.OK);
            log.info("Modify room type id = " + id);
        }
        else {
//            response = new ResponseEntity<>(getJSONObject("Error trying modify room type id = " + id),
//                    headers, HttpStatus.CONFLICT);
            response = new ResponseEntity<>(HttpStatus.CONFLICT);
            log.info("Error trying modify room type id = " + id);
        }
        return response;
    }

    @RequestMapping(method =  RequestMethod.GET, value = "/roomtypes", params = {"page", "size"})
    public ResponseEntity<List<RoomType>> findAllRoomsType(@RequestParam("page") Integer page,
                                                           @RequestParam("size") Integer size) {
        ResponseEntity<List<RoomType>> response;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=UTF-8");

        if (page < 0 || size < 0) {
//            response = new ResponseEntity<>(getJSONObject("Error: page < 0 or size < 0"),
//                    headers, HttpStatus.BAD_REQUEST);
            response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            log.error("Error: page < 0 or size < 0");
        }
        else {
            Pageable pageable = new PageRequest(page, size);
            List<RoomType> roomTypes = repository.findAll(pageable);
            response = new ResponseEntity<>(roomTypes, headers, HttpStatus.OK);
            log.info("Show list of room types successfully");
        }
        return response;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    public ResponseEntity<RoomType> findByIdRoomType(@PathVariable("id") long id) {
        ResponseEntity<RoomType> response;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=UTF-8");

        if (repository.exists(id)) {
            RoomType roomType = repository.findOne(id);
            response = new ResponseEntity<>(roomType, headers, HttpStatus.OK);
            log.info("Show room type successfully id = " + id);
        }
        else {
//            response = new ResponseEntity<>(getJSONObject("Room type is not found id = " + id),
//                    headers, HttpStatus.NOT_FOUND);
            response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
            log.error("Room type is not found id = " + id);
        }
        return response;
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/delete/{id}")
    public ResponseEntity<RoomType> deleteRoomType(@PathVariable("id") long id) {
        ResponseEntity<RoomType> response;

        if (!repository.exists(id)) {
//            response = new ResponseEntity<>(getJSONObject("Error trying deleting room type id = " + id),
//                    new HttpHeaders(), HttpStatus.NOT_FOUND);
            response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
            log.error("Error trying deleting room type id = " + id);
        }
        else {
            repository.delete(id);
            response = new ResponseEntity<>(new HttpHeaders(), HttpStatus.OK);
            log.info("Successfully delete room type id = " + id);
        }
        return response;
    }
}
