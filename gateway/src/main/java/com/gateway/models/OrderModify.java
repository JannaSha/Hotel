package com.gateway.models;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.Future;
import javax.validation.constraints.Min;
import javax.validation.constraints.Null;
import javax.validation.constraints.Positive;
import java.sql.Timestamp;

@JsonFormat
public class OrderModify {
    @Min(0)
    private Integer nightAmount;
    @Future
    @JsonFormat(pattern = "yyyy:MM:dd/HH:mm:ss")
    private Timestamp arrivalDate;

    public Timestamp getArrivalDate() {
        return arrivalDate;
    }

    public Integer getNightAmount() {
        return nightAmount;
    }


    @Override
    public String toString() {
        return String.format("night amount = %d, arrival date = %s", this.nightAmount, this.arrivalDate.toString());
    }
}
