package com.gateway.models;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

@JsonFormat
public class RoomStatisticReport implements Serializable {
    @NotNull
    @Min(0)
    private long amount;
    @NotNull
    @Min(0)
    private BigDecimal cost;
    @NotNull
    @Min(0)
    private Integer roomTypeId;
    @NotNull
    @Min(0)
    private long nightAmount;


    @Override
    public String toString() {
        return "RoomTypeStatisticReport{" +
                "amount = " + amount +
                ", cost = " + cost +
                ", roomTypeId = " + roomTypeId +
                ", nightAmount = " + nightAmount +
                '}';
    }

    public long getAmount() {
        return amount;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public Integer getRoomTypeId() {
        return roomTypeId;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public void setRoomTypeId(Integer roomTypeId) {
        this.roomTypeId = roomTypeId;
    }

    public long getNightAmount() {
        return nightAmount;
    }

    public void setNightAmount(long nightAmount) {
        this.nightAmount = nightAmount;
    }
}
