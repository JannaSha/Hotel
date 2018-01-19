package com.rooms.models;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "tokens")
@JsonFormat
public class Token {
    @Id
    @Min(0)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotNull
    @Column(name="value")
    private String value;

    public Token() {

    }

    public Token(String token) {
        this.value = token;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }


    public String getValue() {
        return value;
    }


    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("Token: value = %s", value);
    }
}

