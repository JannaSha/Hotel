package com.statistic.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.object.OrderKafka;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "auth_statistic")
@JsonFormat
public class AuthStatistic implements Serializable {
    @Id
    @Min(0)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @NotNull
    @Column(name = "login")
    private String login;
    @NotNull
    @Column(name = "password")
    private String password;
    @NotNull
    @Min(0)
    @Max(1)
    @Column(name = "is_success")
    private Integer isSuccess;
    @NotNull
    @Column(name = "date")
    private Timestamp date;

    public AuthStatistic() { }

    public long getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public Integer getIsSuccess() {
        return isSuccess;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setIsSuccess(Integer isSuccess) {
        this.isSuccess = isSuccess;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }


    public AuthStatistic(OrderKafka orderKafka) {
        this.login = orderKafka.getLogin();
        this.date = orderKafka.getDate();
        this.isSuccess = orderKafka.getIsSuccess();
        this.password = orderKafka.getPassword();
    }

    @Override
    public String toString() {
        return "AuthStatistic{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", isSuccess=" + isSuccess +
                ", date=" + date +
                '}';
    }
}

