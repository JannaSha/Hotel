package com.statistic.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.object.OrderKafka;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "order_statistic")
@JsonFormat
public class OrderStatistic implements Serializable {
    @Id
    @Min(0)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Min(0)
    @Column(name = "room_type_id")
    private long roomTypeId;
    @Column(name = "cost")
    private BigDecimal cost;
    @NotNull
    @Min(0)
    @Column(name = "user_id")
    private long userId;
    @NotNull
    @Min(0)
    @Column(name = "night_amount")
    private Integer nightAmount;
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd/HH:mm:ss")
    @Column(name = "arrival_date")
    private Timestamp arrivalDate;

    public long getUserId() {
        return userId;
    }

    public Integer getNightAmount() {
        return nightAmount;
    }

    public Timestamp getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(Timestamp arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public void setNightAmount(Integer nightAmount) {
        this.nightAmount = nightAmount;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public OrderStatistic() {};

    public OrderStatistic(long userId, Integer nightAmount, long roomTypeId,
                      Timestamp arrivalDate, BigDecimal cost) {
        this.arrivalDate = arrivalDate;
        this.nightAmount = nightAmount;
        this.userId = userId;
        this.cost = cost;
        this.roomTypeId = roomTypeId;
    }

    public OrderStatistic(OrderStatistic obj) {
        this.arrivalDate = obj.getArrivalDate();
        this.nightAmount = obj.getNightAmount();
        this.userId = obj.getUserId();
        this.cost = obj.getCost();
        this.roomTypeId = obj.getRoomTypeId();
    }

    public OrderStatistic(OrderKafka orderKafka) {
        this.roomTypeId = orderKafka.getRoomTypeId();
        this.arrivalDate = orderKafka.getArrivalDate();
        this.cost = orderKafka.getCost();
        this.nightAmount = orderKafka.getNightAmount();
        this.userId = orderKafka.getUserId();
    }

    @Override
    public String toString() {
        return String.format(
                "Order: room type id = %d, user id = %d, arrival date = %s, " +
                        "night amount = %d, cost = %s",
                this.roomTypeId, this.userId, this.arrivalDate.toString(),
                this.nightAmount, this.cost.toString());
    }

    public long getRoomTypeId() {
        return roomTypeId;
    }

    public void setRoomTypeId(long roomTypeId) {
        this.roomTypeId = roomTypeId;
    }
}
