package com.rooms.models;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity()
@Table(name = "rooms")
public class Room implements Serializable{

    private static final long serialVersionUID = -3009157732242241605L;

    @Id
    @Min(0)
    private long id;
    @NotNull
    @Min(0)
    @Column(name = "room_type")
    private long roomType;
    @NotNull
    @Column(name = "vacant")
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

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public void setVacant(boolean vacant) {
        this.vacant = vacant;
    }

    public Room() {};

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
