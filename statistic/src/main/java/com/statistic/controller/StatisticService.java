package com.statistic.controller;

import com.object.OrderKafka;
import com.statistic.model.AuthStatistic;
import com.statistic.model.OrderStatistic;
import com.statistic.model.RoomStatisticReport;
import com.statistic.model.UserStatisticReport;
import com.statistic.repos.AuthStatisticRepository;
import com.statistic.repos.OrderStatisticRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

@Component
public class StatisticService {
    @Autowired
    AuthStatisticRepository authStatisticRepository;
    @Autowired
    OrderStatisticRepository orderStatisticRepository;

    void saveOrderKafka(OrderKafka orderKafka) {
        if (orderKafka.getLogin() == null)
        {
            orderStatisticRepository.save(new OrderStatistic(orderKafka));
        } else {
            authStatisticRepository.save(new AuthStatistic(orderKafka));
        }
    }

    List<AuthStatistic> getAllAuthStatistic() {
        return authStatisticRepository.findAll();
    }

    List<RoomStatisticReport> getRoomStatistic() {
        List<RoomStatisticReport> result  = new LinkedList<>();
        for (Object[] item: orderStatisticRepository.statisticRoomTypes()) {
            RoomStatisticReport roomTypeStatisticReport = new RoomStatisticReport();
            roomTypeStatisticReport.setAmount(((BigInteger)item[0]).longValue());
            roomTypeStatisticReport.setCost((BigDecimal)item[1]);
            roomTypeStatisticReport.setNightAmount(((BigInteger)item[2]).longValue());
            roomTypeStatisticReport.setRoomTypeId((Integer)item[3]);
            result.add(roomTypeStatisticReport);
        }
        return result;
    }

    List<UserStatisticReport> getUserStatistic(){
        List<UserStatisticReport> result  = new LinkedList<>();
        for (Object[] item: orderStatisticRepository.statisticRoomTypes()) {
            UserStatisticReport roomTypeStatisticReport = new UserStatisticReport();
            roomTypeStatisticReport.setAmount(((BigInteger)item[0]).longValue());
            roomTypeStatisticReport.setCost((BigDecimal)item[1]);
            roomTypeStatisticReport.setNightAmount(((BigInteger)item[2]).longValue());
            roomTypeStatisticReport.setUserId((Integer)item[3]);
            result.add(roomTypeStatisticReport);
        }
        return result;
    }
}
