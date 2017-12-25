package com.billing.controllers;

import com.billing.models.Billing;
import com.billing.repos.BillingRepository;
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import javax.xml.ws.http.HTTPException;
import java.util.List;

@RestController
@RequestMapping(value = "/billing" )
@Validated
public class BillingControllers {

    @Autowired
    BillingRepository repository;

    private static final Logger log = Logger.getLogger(BillingRepository.class);


    private JSONPObject getJSONObject(String message) {
        return new JSONPObject("message", message);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/create", consumes = "application/json",
            produces="application/json")
    public ResponseEntity<Billing> createBill(@RequestBody @Valid Billing bill) {
        ResponseEntity<Billing> response;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=UTF-8");
        if (repository.save(bill) != null) {
            headers.setLocation(ServletUriComponentsBuilder
                    .fromCurrentServletMapping().path("/billing/{id}").build()
                    .expand(bill.getId()).toUri());
            response = new ResponseEntity<>(bill, headers, HttpStatus.CREATED);
            log.info("Create bill id = " + bill.getId());
        }
        else {
//            response = new ResponseEntity<>(getJSONObject("Error creating bill id = " + bill.getId()), headers,
//                    HttpStatus.CONFLICT);
            response = new ResponseEntity<>(HttpStatus.CONFLICT);
            log.error("Error creating bill id = " + bill.getId());
        }
        return response;
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/modify/{id}",
            consumes = "application/json", produces="application/json")
    public ResponseEntity<Billing> modifyBill(@PathVariable("id") long id, @RequestBody @Valid Billing bill) {
        ResponseEntity<Billing> response;
        HttpHeaders headers = new HttpHeaders();

        if (!repository.exists(id)) {
//            response = new ResponseEntity<>(getJSONObject("Error modify bill, bill is not found id = " + id),
//                    HttpStatus.NOT_FOUND);
            response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
            log.error("Error modify bill, bill is not found id = " + id);
        }
        else if (bill.getId() == id && repository.save(bill) != null) {
            response = new ResponseEntity<>(bill, headers, HttpStatus.OK);
            log.error("Successfully modify bill id = " + id);
        }
        else {
//            response = new ResponseEntity<>(getJSONObject("Error modify bill, database error id = " + id),
//                    HttpStatus.CONFLICT);
            response = new ResponseEntity<>(HttpStatus.CONFLICT);
            log.error("Error modify bill, database error id = " + id);
        }
        return response;
    }


    @RequestMapping(method = RequestMethod.GET, value = "/billings", produces="application/json;charset=UTF-8",
            params = {"page", "size"})
    public ResponseEntity<List<Billing>> findAll(@RequestParam("page") Integer page,
                                                 @RequestParam("size") Integer size) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=UTF-8");
        if (page < 0 || size < 0) {
            log.error("Error: page < 0 or size < 0");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//            return new ResponseEntity<>(getJSONObject("Error: page < 0 or size < 0"), HttpStatus.BAD_REQUEST);
        }
        Pageable pageable = new PageRequest(page, size);
        List<Billing> billings = repository.findAll(pageable);
        log.info("Successfully get billings");
        return new ResponseEntity<>(billings, headers, HttpStatus.OK);
    }

    @RequestMapping(method =  RequestMethod.GET, value = "/{id}")
    public ResponseEntity<Billing> findOne(@PathVariable long id) {
        ResponseEntity<Billing> response;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=UTF-8");

        if (repository.exists(id)) {
            Billing bill = repository.findOne(id);
            if (bill != null) {
                response = new ResponseEntity<>(bill, headers, HttpStatus.OK);
                log.info("Get bill successfully id = " + id);
            }
            else {
//                response = new ResponseEntity<>(getJSONObject("Error: bill is not found id = " + id),
//                        headers, HttpStatus.INTERNAL_SERVER_ERROR);
                response = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                log.error("Error: bill is not found id = " + id);
            }
        }
        else {
//            response = new ResponseEntity<>(getJSONObject("Error: bill is not found id = " + id),
//                    headers, HttpStatus.NOT_FOUND);
            response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
            log.error("Error: bill is not found id = " + id);
        }
        return response;
    }


    @RequestMapping(method = RequestMethod.DELETE, value = "/delete/{id}")
    public ResponseEntity<Billing> delete(@PathVariable long id) {
        ResponseEntity<Billing> response;

        if (!repository.exists(id)) {
//            response = new ResponseEntity<>(getJSONObject("Error trying deleting bill id = " + id),
//                    HttpStatus.NOT_FOUND);
            response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
            log.error("Error trying deleting bill id = " + id);
//            throw new HttpClientErrorException(HttpSt)
        }
        else {
            repository.delete(id);
            response = new ResponseEntity<>(new HttpHeaders(), HttpStatus.OK);
            log.info("Successfully delete bill id = " + id);
        }
        return response;
    }
}
