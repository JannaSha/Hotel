package com.statistic.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="/jsa/kafka")
public class StatisticController {
    @Autowired
    MessageStorage storage;

    @RequestMapping(method = RequestMethod.GET, value="/consumer")
    public String getAllRecievedMessage(){
        String messages = storage.toString();
        storage.clear();
        return messages;
    }

}
