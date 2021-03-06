package com.users.controllers;


import com.fasterxml.jackson.databind.util.JSONPObject;
import com.users.TokenManager;
import com.users.models.Token;
import com.users.models.User;
import com.users.repos.TokenRepository;
import com.users.repos.UserRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/user" )
public class UserController {

    @Autowired
    UserRepository repository;
    @Autowired
    private TokenRepository tokenRepository;
    private static final Logger log = Logger.getLogger(UserController.class);


    @RequestMapping(method = RequestMethod.GET, value = "/token")
    public ResponseEntity<String> getToken(@RequestHeader HttpHeaders header) {
        ResponseEntity<String> response;
        HttpHeaders headers = new HttpHeaders();

        if (TokenManager.getAppKey() != null && TokenManager.getAppKey().equals(header.get("Authorization").get(0))) {
            tokenRepository.deleteAll();
            Token temp = TokenManager.generateToken();
            tokenRepository.save(temp);
            headers.add("token", temp.getValue());
            response = new ResponseEntity<>(headers, HttpStatus.OK);
        } else {
            response = new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return response;
    }


    private ResponseEntity<User> checkToken(HttpHeaders headers) {
        List<Token> tokens = tokenRepository.findAll();
        if (tokens != null && tokens.size() == 1 && headers.containsKey("token") &&
                headers.get("token").get(0).equals(tokens.get(0).getValue())) {
            return null;
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    private JSONPObject getJSONObject(String message) {
        return new JSONPObject("message", message);
    }


    @RequestMapping(method = RequestMethod.POST, value = "/create", consumes = "application/json",
            produces="application/json")
    public ResponseEntity<User> createUser(@RequestBody @Valid User user, @RequestHeader HttpHeaders header) {
        if (checkToken(header) != null)
            return checkToken(header);

        ResponseEntity<User> response;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=UTF-8");

        if (repository.findByPassportNumber(user.getPassportNumber()) == null) {
            if (repository.save(user) != null) {
                headers.setLocation(ServletUriComponentsBuilder
                        .fromCurrentServletMapping().path("/user/{id}").build()
                        .expand(user.getId()).toUri());
                response = new ResponseEntity<>(user, headers, HttpStatus.CREATED);
                log.info("Create user: passport number = " + user.getPassportNumber());
            }
            else {
//                response = new ResponseEntity<>(
//                        getJSONObject("Error creating user: passport number, db error = " + user.getPassportNumber()),
//                        headers, HttpStatus.INTERNAL_SERVER_ERROR);
                response = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                log.error("Error creating user: passport number, db error = " + user.getPassportNumber());
            }
        }
        else {
//            response = new ResponseEntity<>(
//                    getJSONObject("Error creating user: passport number, this passport already exists = " +
//                            user.getPassportNumber()),headers, HttpStatus.CONFLICT);
            response = new ResponseEntity<>(HttpStatus.CONFLICT);
            log.error("Error creating user: passport number, this passport already exists = " + user.getPassportNumber());
        }
        return response;
    }


    @RequestMapping(method = RequestMethod.PUT, value = "/modify/{id}",
            consumes = "application/json", produces="application/json")
    public ResponseEntity<User> modifyUser(@PathVariable("id") long id,
                                           @RequestBody @Valid User user,
                                           @RequestHeader HttpHeaders header) {
        if (checkToken(header) != null)
            return checkToken(header);

        ResponseEntity<User> response;
        HttpHeaders headers = new HttpHeaders();

        if (!repository.existsById(id)) {
//            response = new ResponseEntity<>(
//                    getJSONObject("Error modify user, user is not found passport number = " + id),
//                    headers, HttpStatus.NOT_FOUND);
            response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
            log.error("Error modify user, user is not found passport number = " + id);
        }
        else if (user.getId() == id && repository.save(user) != null) {
            response = new ResponseEntity<>(user, headers, HttpStatus.OK);
            log.error("Successfully modify user passport number = " + id);
        }
        else {
//            response = new ResponseEntity<>(getJSONObject("Error modify user, database error passport number = " + id),
//                    headers, HttpStatus.CONFLICT);
            response = new ResponseEntity<>(HttpStatus.CONFLICT);
            log.error("Error modify user, database error passport number = " + id);
        }
        return response;
    }


    @RequestMapping(method = RequestMethod.GET, value = "/users", params = {"page", "size"})
    public ResponseEntity<List<User>> findAll(@RequestParam("page") Integer page,
                                              @RequestParam("size") Integer size,
                                              @RequestHeader HttpHeaders header) {
        if (checkToken(header) != null)
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        ResponseEntity<List<User>> response;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=UTF-8");

        if (page < 0 || size < 0) {
//            response = new ResponseEntity<>(getJSONObject("Error get users: page < 0 or size < 0"),
//                    headers, HttpStatus.BAD_REQUEST);
            response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            log.error("Error get users: page < 0 or size < 0");
        } else {
            Pageable pageable = new PageRequest(page, size);
            List<User> users = repository.findAll(pageable);
            response = new ResponseEntity<>(users, headers, HttpStatus.OK);
            log.info("Successfully get users");
        }
        return response;

    }

    @RequestMapping(method =  RequestMethod.GET, value = "/{id}")
    public ResponseEntity<User> findOne(@PathVariable("id") long id, @RequestHeader HttpHeaders header) {
        if (checkToken(header) != null)
            return checkToken(header);

        ResponseEntity<User> response;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=UTF-8");

        if (repository.existsById(id)) {
            Optional<User> user = repository.findById(id);
            response = new ResponseEntity<>(user.get(), headers, HttpStatus.OK);
            log.info("Get user successfully id = " + id);
        }
        else {
//            response = new ResponseEntity<>(getJSONObject("Error: user is not found id = " + id),
//                    headers, HttpStatus.NOT_FOUND);
            response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
            log.error("Error: user is not found id = " + id);
        }
        return response;
    }

    @RequestMapping(method =  RequestMethod.GET, value = "/username/{username}")
    public ResponseEntity<User> findOne(@PathVariable("username") String username, @RequestHeader HttpHeaders header) {
        if (checkToken(header) != null)
            return checkToken(header);

        ResponseEntity<User> response;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=UTF-8");

        User user = repository.findByUsername(username);
        if (user != null) {
            response = new ResponseEntity<>(user, headers, HttpStatus.OK);
            log.info("Get user successfully id = " + username);
        }
        else {
//            response = new ResponseEntity<>(getJSONObject("Error: user is not found id = " + id),
//                    headers, HttpStatus.NOT_FOUND);
            response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
            log.error("Error: user is not found id = " + username);
        }
        return response;
    }

    @RequestMapping(method =  RequestMethod.GET, value = "/passport/{number}")
    public ResponseEntity<User> findByPassportNumber(@PathVariable("number") long number,
                                                     @RequestHeader HttpHeaders header) {
        if (checkToken(header) != null)
            return checkToken(header);

        ResponseEntity<User> response;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=UTF-8");

        User user = repository.findByPassportNumber(number);
        if (user != null) {
            response = new ResponseEntity<>(user, headers, HttpStatus.OK);
            log.info("Get user successfully passport number = " + number);
        }
        else {
//            response = new ResponseEntity<>(getJSONObject("Error: user is not found passport number = " + number),
//                    headers, HttpStatus.NOT_FOUND);
            response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
            log.error("Error: user is not found passport number = " + number);
        }
        return response;
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/delete/{id}")
    public ResponseEntity<User> delete(@PathVariable("id") long id, @RequestHeader HttpHeaders header) {
        if (checkToken(header) != null)
            return checkToken(header);

        ResponseEntity<User> response;

        if (!repository.existsById(id)) {
//            response = new ResponseEntity<>(getJSONObject("Error trying deleting user passport number = " + id),
//                    HttpStatus.NOT_FOUND);
            response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
            log.error("Error trying deleting user passport number = " + id);
        }
        else {
            repository.deleteById(id);
            response = new ResponseEntity<>(HttpStatus.OK);
            log.info("Successfully delete user passport number = " + id);
        }
        return response;
    }



}
