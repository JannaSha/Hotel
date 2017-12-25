package com.gateway.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Positive;
import java.io.Serializable;

public class Room implements Serializable{

    @Null
    @Min(0)
    private long id;

    @NotNull
    @Min(0)
    private long roomType;

    @NotNull
    private boolean vacant;

    public void setId(long roomNumber) {
        this.id = roomNumber;
    }

    public long getId() {
        return this.id;
    }

    public long getRoomType() {
        return roomType;
    }

    public boolean isVacant() {
        return vacant;
    }

    public void setRoomType(long roomType) {
        this.roomType = roomType;
    }

    public void setVacant(boolean vacant) {
        this.vacant = vacant;
    }

    public Room() {};

    public Room(Room obj) {
        this.id = obj.getId();
        this.roomType = obj.getRoomType();
        this.vacant = obj.isVacant();
    }

    public Room(long roomNumber, Integer roomType, boolean vacant) {
        this.id = roomNumber;
        this.roomType = roomType;
        this.vacant = vacant;
    }

    @Override
    public String toString() {
        return String.format("Room: number = %d, type = %d, vacant = %b", this.id, this.roomType, this.vacant);
    }
}
