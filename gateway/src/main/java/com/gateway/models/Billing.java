package com.gateway.models;


import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Positive;
import java.io.Serializable;
import java.math.BigDecimal;


@JsonFormat
public class Billing implements Serializable{

    @Null
    @Min(0)
    private long id;

    @NotNull
    private BigDecimal cost;

    @NotNull
    @Min(0)
    private long cartNumber;

    public long getId() {
        return id;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public long getCartNumber() {
        return cartNumber;
    }

    public void setCartNumber(long cartNumber) {
        this.cartNumber = cartNumber;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public Billing() {};

    public Billing(long cartNumber, BigDecimal cost) {
        this.cartNumber = cartNumber;
        this.cost = cost;
    }

    public Billing(long id, long cartNumber, BigDecimal cost) {
        this.id = id;
        this.cartNumber = cartNumber;
        this.cost = cost;
    }

    @Override
    public String toString() {
        return String.format("Billing: id = %d, cost =  %s, cart number = %d",
                this.id, this.cost.toString(), this.cartNumber);
    }
}
