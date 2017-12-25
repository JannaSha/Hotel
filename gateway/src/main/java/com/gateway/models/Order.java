package com.gateway.models;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

@JsonFormat
public class Order implements Serializable{

    @Min(0)
    private long id;
    @NotNull
    @Min(0)
    private long roomId;
    @JsonFormat(pattern = "yyyy-MM-dd/HH:mm:ss")
    private Timestamp orderDate;
    @NotNull
    @Min(-1)
    private long billId;
    @NotNull
    @Min(0)
    private long userId;
    @NotNull
    @Min(0)
    private Integer nightAmount;
    @NotNull
    @Future
    @JsonFormat(pattern = "yyyy-MM-dd/HH:mm:ss")
    private Timestamp arrivalDate;
    @NotNull
    private BigDecimal cost;

    public long getId() {
        return id;
    }

    public Timestamp getOrderDate() {
        return orderDate;
    }

    public long getBillId() {
        return billId;
    }

    public long getRoomId() {
        return roomId;
    }

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

    public void setBillId(long billId) {
        this.billId = billId;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setNightAmount(Integer nightAmount) {
        this.nightAmount = nightAmount;
    }

    public void setOrderDate(Timestamp orderDate) {
        this.orderDate = orderDate;
    }

    public void setRoomId(long roomId) {
        this.roomId = roomId;
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

    public Order() {};

    public Order(long roomId, long userId, Timestamp orderDate, long billId,  Integer nightAmount,
                 Timestamp arrivalDate, BigDecimal cost) {
        this.orderDate = orderDate;
        this.billId = billId;
        this.arrivalDate = arrivalDate;
        this.nightAmount = nightAmount;
        this.roomId = roomId;
        this.userId = userId;
        this.cost = cost;
    }

    public Order(long id, long roomId, long userId, Timestamp orderDate, long billId,  Integer nightAmount,
                 Timestamp arrivalDate, BigDecimal cost) {
        this.id = id;
        this.orderDate = orderDate;
        this.billId = billId;
        this.arrivalDate = arrivalDate;
        this.nightAmount = nightAmount;
        this.roomId = roomId;
        this.userId = userId;
        this.cost = cost;
    }

    @Override
    public String toString() {
        return String.format("Order: room id = %d, order date =  %s, bill id = %d, user id = %d, arrival date = %s, " +
                        "night amount = %d, cost = %s",
                this.roomId, this.orderDate.toString(), this.billId, this.userId, this.arrivalDate.toString(),
                this.nightAmount, this.cost.toString());
    }
}
