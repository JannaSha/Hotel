package com.statistic.controller;

import com.statistic.model.AuthStatistic;
import com.statistic.model.RoomStatisticReport;
import com.statistic.model.UserStatisticReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping(value="/statistic")
public class StatisticController {
    @Autowired
    MessageStorage storage;

    @Autowired
    StatisticService service;

    @GetMapping(value = "/userrepost")
    public ResponseEntity<List<UserStatisticReport>> getUserStatisticReport(){
        List<UserStatisticReport> result;
        try {
            result = service.getUserStatistic();
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    @GetMapping(value = "/roomreport")
    public ResponseEntity<List<RoomStatisticReport>> getRoomStatisticReport(){
        List<RoomStatisticReport> result;
        try {
            result = service.getRoomStatistic();
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    @GetMapping(value="/auth")
    public ResponseEntity<List<AuthStatistic>> getAuthStatisticReport() {
        List<AuthStatistic> result;
        try {
            result = service.getAllAuthStatistic();
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

}
