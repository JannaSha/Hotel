package com.gateway.models;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.sql.Timestamp;

@JsonFormat
public class OrderCreator {
    @NotNull
    @Min(0)
    private long roomTypeId;
    @NotNull
    @Min(0)
    private Integer nightAmount;
    @NotNull
    @JsonFormat(pattern = "yyyy:MM:dd/HH:mm:ss")
    private Timestamp arrivalDate;

    public Timestamp getArrivalDate() {
        return arrivalDate;
    }

    public Integer getNightAmount() {
        return nightAmount;
    }

    public long getRoomTypeId() {
        return roomTypeId;
    }


    @Override
    public String toString() {
        return String.format("roomtype id = %d, night amount = %d, arrival date = %s",
                this.roomTypeId, this.nightAmount, this.arrivalDate.toString());
    }
}
