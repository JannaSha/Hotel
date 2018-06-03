package com.rooms.controllers;


import com.fasterxml.jackson.databind.util.JSONPObject;
import com.rooms.TokenManager;
import com.rooms.models.Room;
import com.rooms.models.Token;
import com.rooms.repo.RoomsRepository;
import com.rooms.repo.RoomsTypesRepository;
import com.rooms.repo.TokenRepository;
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
import java.util.Optional;

@RestController
@RequestMapping(value = "/room")
@Validated
public class RoomController {
    @Autowired
    RoomsRepository repository;
    @Autowired
    RoomsTypesRepository repositoryType;

    public static Token currentToken;

    private static final Logger log = Logger.getLogger(RoomTypeController.class);

    private JSONPObject getJSONObject(String message) {
        return new JSONPObject("message", message);
    }

    @Autowired
    private TokenRepository tokenRepository;

    @RequestMapping(method = RequestMethod.GET, value = "/token")
    public ResponseEntity<String> getToken(@RequestHeader HttpHeaders header) {
        ResponseEntity<String> response;
        HttpHeaders headers = new HttpHeaders();

        if (TokenManager.getAppKey() != null && TokenManager.getAppKey().equals(header.get("Authorization").get(0))) {
            tokenRepository.deleteAll();
            Token temp = TokenManager.generateToken();
//            currentToken = temp;
            tokenRepository.save(temp);
            headers.add("token", temp.getValue());
            response = new ResponseEntity<>(headers, HttpStatus.OK);
        } else {
            response = new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return response;
    }


    ResponseEntity<String> checkToken(HttpHeaders headers) {
        List<Token> tokens = tokenRepository.findAll();
        if (tokens != null && tokens.size() == 1 && headers.containsKey("token") &&
                headers.get("token").get(0).equals(tokens.get(0).getValue())) {
            return null;
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/create",
            consumes = "application/json", produces="application/json")
    public ResponseEntity<Room> createRoom(@RequestBody @Valid Room room, @RequestHeader HttpHeaders header) {
        if (checkToken(header) != null)
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        ResponseEntity<Room> response;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=UTF-8");

        if (!repository.existsById(room.getId()) && repository.save(room) != null &&
                repositoryType.existsById(room.getRoomType())) {
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
    public ResponseEntity<Room> modifyRoom(@PathVariable("id") long id,
                                           @RequestBody @Valid Room room,
                                           @RequestHeader HttpHeaders header) {
        if (checkToken(header) != null)
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        ResponseEntity<Room> response;
        HttpHeaders headers = new HttpHeaders();

        if (!repository.existsById(id)) {
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
    public ResponseEntity<Room> findById(@PathVariable("id") long id, @RequestHeader HttpHeaders header){
        if (checkToken(header) != null)
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        ResponseEntity<Room> response;
        Optional<Room> room;

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=UTF-8");
        if (!repository.existsById(id)) {
//            response = new ResponseEntity<>(getJSONObject("Error get room, room is not found id = "),
//                    headers, HttpStatus.NOT_FOUND);
            response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
            log.error("Error get room, room is not found id = " + id);
        }
        else {
            room = repository.findById(id);
            response = new ResponseEntity<>(room.get(), headers, HttpStatus.OK);
            log.info("Successfully get room id = " + id);
        }
        return response;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/rooms", params = {"page", "size"})
    public ResponseEntity<List<Room>> findAll(@RequestParam("page") Integer page,
                                              @RequestParam("size") Integer size,
                                              @RequestHeader HttpHeaders header) {
        if (checkToken(header) != null)
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

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
    public ResponseEntity<Room> deleteRoom (@PathVariable("id") long id, @RequestHeader HttpHeaders header) {
        if (checkToken(header) != null)
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        ResponseEntity<Room> response;
        if (!repository.existsById(id)) {
//            response = new ResponseEntity<>(getJSONObject("Error delete room, room is not found id = " + id),
//                    new HttpHeaders(), HttpStatus.NOT_FOUND);
            response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
            log.error("Error delete room, room is not found id = " + id);
        }
        else {
            repository.deleteById(id);
            response = new ResponseEntity<>(new HttpHeaders(), HttpStatus.OK);
            log.info("Deleted room id = " + id);
        }
        return response;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/roomtype/{id}", params = {"vacant"})
    public ResponseEntity<List<Room>> findByType(@PathVariable("id") long id,
                                                 @RequestParam("vacant") boolean vacant,
                                                 @RequestHeader HttpHeaders header) {
        if (checkToken(header) != null)
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        ResponseEntity<List<Room>> response;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=UTF-8");

        if (repositoryType.existsById(id)) {
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
