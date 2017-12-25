package com.users.models;

import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;


@Entity
@Table(name = "users")
public class User implements Serializable{

    @Id
    @Min(0)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @NotNull
    @Min(0)
    @Column(name = "passport_number")
    private long passportNumber;
    @NotNull
    @Length(max = 256)
    @Column(name = "first_name")
    private String firstName;
    @NotNull
    @Length(max = 256)
    @Column(name = "last_name")
    private String  lastName;
    @Min(0)
    @NotNull
    @Column(name = "orders_amount")
    private Integer ordersAmount;

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


    public User() {};

    public User(long passportNumber, String firstName, String lastName, Integer ordersAmount) {
       this.firstName = firstName;
       this.lastName = lastName;
       this.passportNumber = passportNumber;
       this.ordersAmount = ordersAmount;
    }

    public User(long id, long passportNumber, String firstName, String lastName, Integer ordersAmount) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.passportNumber = passportNumber;
        this.ordersAmount = ordersAmount;
    }

    public User(User obj){
        this.id = obj.getId();
        this.firstName = obj.getFirstName();
        this.lastName = obj.getLastName();
        this.passportNumber = obj.getPassportNumber();
        this.ordersAmount = obj.getOrdersAmount();
    }


    @Override
    public String toString() {
        return String.format("User: id = %d First name = %s, last name =  %s, passport = %d, ordersAmount = %d",
                this.id, this.firstName, this.lastName, this.passportNumber, this.ordersAmount);
    }
}
