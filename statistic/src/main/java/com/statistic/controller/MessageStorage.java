package com.statistic.controller;

import java.util.ArrayList;
import java.util.List;

import com.object.OrderKafka;
import org.springframework.stereotype.Component;

@Component
public class MessageStorage {

    private List<OrderKafka> storage = new ArrayList<>();

    public void put(OrderKafka message){
        storage.add(message);
    }

    public String toString(){
        StringBuffer info = new StringBuffer();
        storage.forEach(msg->info.append(msg).append("<br/>"));
        return info.toString();
    }

    public void clear(){
        storage.clear();
    }
}