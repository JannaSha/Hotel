package com.gateway.models;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.net.URI;
import java.sql.Timestamp;


@JsonFormat
public class OrderGetter implements Serializable{

    @NotNull
    @Min(0)
    private long id;
    @NotNull
    @Min(0)
    private long roomId;
    @Null
    @Min(0)
    private RoomType roomType;
//    private URI roomTypeUrl;
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd/HH:mm:ss")
    private Timestamp orderDate;
    @Null
    private Billing bill;
//    private URI billUrl;
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

    public Billing getBillId() {
        return bill;
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

    public void setBillId(Billing billId) {
        this.bill = billId;
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

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public Billing getBill() {
        return bill;
    }

    public RoomType getRoomType() {
        return roomType;
    }

//    public URI getBillUrl() {
//        return billUrl;
//    }
//
//    public URI getRoomTypeUrl() {
//        return roomTypeUrl;
//    }

    public void setBill(Billing bill) {
        this.bill = bill;
    }

//    public void setBillUrl(URI billUrl) {
//        this.billUrl = billUrl;
//    }
//
//    public void setRoomTypeUrl(URI roomTypeUrl) {
//        this.roomTypeUrl = roomTypeUrl;
//    }

    public OrderGetter() {};

    public OrderGetter(long roomId, long userId, Timestamp orderDate, Billing billId,  Integer nightAmount,
                 Timestamp arrivalDate, BigDecimal cost, RoomType roomType, URI roomTypeUrl, URI billUrl) {
        this.orderDate = orderDate;
        this.bill = billId;
        this.arrivalDate = arrivalDate;
        this.nightAmount = nightAmount;
        this.roomId = roomId;
        this.userId = userId;
        this.cost = cost;
        this.roomType = roomType;
//        this.roomTypeUrl = roomTypeUrl;
//        this.billUrl = billUrl;
    }

    public OrderGetter(Order obj) {
        this.orderDate = obj.getOrderDate();
        this.arrivalDate = obj.getArrivalDate();
        this.nightAmount = obj.getNightAmount();
        this.roomId = obj.getRoomId();
        this.userId = obj.getUserId();
        this.cost = obj.getCost();
        this.id = obj.getId();
    }

    @Override
    public String toString() {
        return String.format("Order: room id = %d, order date =  %s, bill id = %s, user id = %d, arrival date = %s, " +
                        "night amount = %d, cost = %s",
                this.roomId, this.orderDate.toString(), this.bill.toString(), this.userId, this.arrivalDate.toString(),
                this.nightAmount, this.cost.toString());
    }
}
