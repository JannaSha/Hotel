package com.food.models;


import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Time;

@Entity
@Table(name = "food")
public class Food implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(name = "name")
    private String name;
    @JsonFormat(pattern = "HH:mm:ss")
    @Column(name = "food_time")
    private Time foodTime;
    @Column(name = "price")
    private BigDecimal price;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Time getFoodTime() {
        return foodTime;
    }

    public Food() {};

    public Food(String name, Time mealTime, BigDecimal price) {
        this.name = name;
        this.price = price;
        this.foodTime = mealTime;
    }

    @Override
    public String toString() {
        return String.format("Food: name = %s, foodTime =  %s, price = %s",
                this.name, this.foodTime.toString(), this.price.toString());
    }
}
