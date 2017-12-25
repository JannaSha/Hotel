package com.gateway.models;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.io.Serializable;

public class RoomGetter implements Serializable{

    @Min(0)
    private long id;
    private RoomType roomType;

    private boolean vacant;

    public void setId(long roomNumber) {
        this.id = roomNumber;
    }

    public long getId() {
        return this.id;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public boolean isVacant() {
        return vacant;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public void setVacant(boolean vacant) {
        this.vacant = vacant;
    }

    public RoomGetter() {};

    public RoomGetter(RoomGetter obj) {
        this.id = obj.getId();
        this.roomType = obj.getRoomType();
        this.vacant = obj.isVacant();
    }

    public RoomGetter(long roomNumber, RoomType roomType, boolean vacant) {
        this.id = roomNumber;
        this.roomType = roomType;
        this.vacant = vacant;
    }

    @Override
    public String toString() {
        return String.format("Room: number = %d, type = %s, vacant = %b",
                this.id, this.roomType.toString(), this.vacant);
    }
}
