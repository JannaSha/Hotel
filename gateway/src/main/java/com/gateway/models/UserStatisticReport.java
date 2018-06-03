package com.gateway.models;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

@JsonFormat
public class UserStatisticReport implements Serializable {
    @NotNull
    @Min(0)
    private long amount;
    @NotNull
    @Min(0)
    private BigDecimal cost;
    @NotNull
    @Min(0)
    private long nightAmount;
    @NotNull
    @Min(0)
    private long userId;


    public long getAmount() {
        return amount;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public long getNightAmount() {
        return nightAmount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public void setNightAmount(long nightAmount) {
        this.nightAmount = nightAmount;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "UserStatisticReport{" +
                "amount=" + amount +
                ", cost=" + cost +
                ", nightAmount=" + nightAmount +
                ", userId=" + userId +
                '}';
    }
}
