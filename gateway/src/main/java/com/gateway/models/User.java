package com.gateway.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;


@JsonFormat
public class User implements Serializable{
    @Min(0)
    @Null
    private long id;
    @Min(0)
    @NotNull
    private long passportNumber;
    @NotNull
    @NotEmpty
    @Length(max = 256)
    private String firstName;
    @NotEmpty
    @NotNull
    @Length(max = 256)
    private String  lastName;
    @NotNull
    @Min(0)
    private Integer ordersAmount;

    @NotNull
    @Column(name = "username")
    private String username;


    public long getId() {
        return id;
    }

    public String getLastName() {
        return lastName;
    }

    public Integer getOrdersAmount() {
        return ordersAmount;
    }

    public long getPassportNumber() {
        return passportNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setOrdersAmount(Integer ordersAmount) {
        this.ordersAmount = ordersAmount;
    }

    public void setPassportNumber(long passportNumber) {
        this.passportNumber = passportNumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public User() {};

    public User(long passportNumber, String firstName, String lastName, Integer ordersAmount, String username) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.passportNumber = passportNumber;
        this.ordersAmount = ordersAmount;
        this.username = username;
    }

    public User(long id, long passportNumber, String firstName,
                String lastName, Integer ordersAmount, String username) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.passportNumber = passportNumber;
        this.ordersAmount = ordersAmount;
        this.username = username;
    }

    public User(User obj){
        this.id = obj.getId();
        this.firstName = obj.getFirstName();
        this.lastName = obj.getLastName();
        this.passportNumber = obj.getPassportNumber();
        this.ordersAmount = obj.getOrdersAmount();
        this.username = obj.getUsername();
    }


    @Override
    public String toString() {
        return String.format("User: id = %d First name = %s, last name =  %s, " +
                        "passport = %d, ordersAmount = %d, username = %s",
                this.id, this.firstName, this.lastName, this.passportNumber, this.ordersAmount, this.username);
    }
}
