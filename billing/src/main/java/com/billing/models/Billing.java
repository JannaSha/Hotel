package com.billing.models;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;


@Entity
@Table(name = "billings")
@JsonFormat
public class Billing implements Serializable{

    @Id
    @Min(0)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(name = "cost")
    @NotNull
    private BigDecimal cost;
    @Min(0)
    @NotNull
    @Column(name = "cart_number")
    private Long cartNumber;

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
