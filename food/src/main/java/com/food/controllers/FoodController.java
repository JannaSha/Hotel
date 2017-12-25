package com.food.controllers;

import com.food.models.Food;
import com.food.repo.FoodRepository;
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

@RestController
@RequestMapping(value = "/food" )
public class FoodController {

    @Autowired
    FoodRepository repository;

    private static final Logger log = Logger.getLogger(FoodController.class);

    @RequestMapping(method = RequestMethod.POST, value = "/create",
            consumes = "application/json", produces="application/json")
    public ResponseEntity<Food> createRoom(@RequestBody @Valid Food food) {
        ResponseEntity<Food> response;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        if (repository.findByName(food.getName()) == null && repository.save(food) != null) {
            response = new ResponseEntity<Food>(food, headers, HttpStatus.CREATED);
            headers.setLocation(ServletUriComponentsBuilder
                    .fromCurrentServletMapping().path("/food/{id}").build()
                    .expand(food.getId()).toUri());
            log.info("Create food id = " + food.getId());
        }
        else {
            response = new ResponseEntity<Food>(headers, HttpStatus.CONFLICT);
            log.error("Error creating food id = " + food.getId());
        }
        return response;
    }


    @RequestMapping(method = RequestMethod.PUT, value = "/modify/{id}", consumes = "application/json", produces="application/json")
    public ResponseEntity<Food> modifyRoom(@PathVariable long id, @RequestBody @Valid Food food) {
        ResponseEntity<Food> response;
        HttpHeaders headers = new HttpHeaders();

        if (!repository.exists(id)) {
            response = new ResponseEntity<Food>(headers, HttpStatus.NOT_FOUND);
            log.error("Error modify food, food is not found id = " + id);
        }
        else if (food.getId() == id && repository.save(food) != null) {
            response = new ResponseEntity<Food>(food, headers, HttpStatus.OK);
            log.error("Successfully modify food id = " + id);
        }
        else {
            response = new ResponseEntity<Food>(headers, HttpStatus.CONFLICT);
            log.error("Error modify food, database error id = " + id);
        }
        return response;
    }


    @RequestMapping(method = RequestMethod.GET, value = "/foodlist", consumes = "application/json", produces="application/json",
            params = {"page", "size"})
    public ResponseEntity<List<Food>> findAll(@RequestParam("page") Integer page, @RequestParam("size") Integer size) {
        ResponseEntity<List<Food>> response;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        if (page < 0 || size < 0) {
            response = new ResponseEntity<List<Food>>(headers, HttpStatus.BAD_REQUEST);
            log.error("Error get food: page < 0 or size < 0");
        } else {
            Pageable pageable = new PageRequest(page, size);
            List<Food> food = repository.findAll(pageable);
            response = new ResponseEntity<List<Food>>(food, headers, HttpStatus.OK);
            log.info("Successfully get food");
        }
        return response;

    }

    @RequestMapping(method =  RequestMethod.GET, value = "/{id}")
    public ResponseEntity<Food> findOne(@PathVariable("id") long id) {
        ResponseEntity<Food> response;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        if (repository.exists(id)) {
            Food food = repository.findOne(id);
            response = new ResponseEntity<Food>(food, headers, HttpStatus.OK);
            log.info("Get food successfully id = " + id);
        }
        else {
            response = new ResponseEntity<Food>(headers, HttpStatus.NOT_FOUND);
            log.error("Error: food is not found id = " + id);
        }
        return response;
    }

    @RequestMapping(method =  RequestMethod.GET, value = "/foodname/{name}")
    public ResponseEntity<Food> findOneByName(@PathVariable String name) {
        ResponseEntity<Food> response;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        if (repository.findByName(name) != null) {
            Food food = repository.findByName(name);
            response = new ResponseEntity<Food>(food, headers, HttpStatus.OK);
            log.info("Get food successfully name = " + name);
        }
        else {
            response = new ResponseEntity<Food>(headers, HttpStatus.NOT_FOUND);
            log.error("Error: food is not found name = " + name);
        }
        return response;
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/delete/{id}")
    public ResponseEntity<Food> delete(@PathVariable long id) {
        ResponseEntity<Food> response;

        if (!repository.exists(id)) {
            response = new ResponseEntity<Food>(new HttpHeaders(), HttpStatus.NOT_FOUND);
            log.error("Error trying deleting food id = " + id);
        }
        else {
            repository.delete(id);
            response = new ResponseEntity<Food>(new HttpHeaders(), HttpStatus.OK);
            log.info("Successfully delete food id = " + id);
        }
        return response;
    }

}
