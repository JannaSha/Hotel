package com.gateway.models;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.math.BigDecimal;
import javax.validation.constraints.Positive;

public class RoomType {

    @Min(0)
    private long id;
    @NotNull
    @Min(0)
    private Integer amountVacantRooms;
    @NotNull
    @Min(0)
    private Integer amountRooms;
    @NotNull
    @NotEmpty
    @Length(max = 256)
    private String typeName;
    @NotNull
    @NotEmpty
    @Length(max = 256)
    private String description;
    @NotNull
    private BigDecimal price;
    @NotNull
    @Min(0)
    private Integer amountRoomines;

    public long getId() {
        return this.id;
    }

    public Integer getAmountRooms() {
        return amountRooms;
    }

    public Integer getAmountVacantRooms() {
        return this.amountVacantRooms;
    }

    public String getTypeName() {
        return this.typeName;
    }

    public String getDescription() {
        return this.description;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public Integer getAmountRoomines() {
        return amountRoomines;
    }

    public void setAmountVacantRooms(Integer amountVacantRooms) {
        this.amountVacantRooms = amountVacantRooms;
    }

    public RoomType() {}

    public RoomType(RoomType obj) {
        this.amountRoomines = obj.getAmountRoomines();
        this.amountRooms = obj.getAmountRooms();
        this.description = obj.getDescription();
        this.id = obj.getId();
        this.price = obj.getPrice();
        this.typeName = obj.getTypeName();
        this.amountVacantRooms = obj.getAmountVacantRooms();
    }

    public RoomType(Integer amountVacantRooms,
                    Integer amountRooms, String typeName,
                    String description, BigDecimal price, Integer amountRoomines) {
        this.amountVacantRooms = amountVacantRooms;
        this.amountRooms = amountRooms;
        this.typeName = typeName;
        this.price = price;
        this.description = description;
        this.amountRoomines = amountRoomines;
    }
    @Override
    public String toString() {
        return String.format("id = %d, amount_vacant_rooms = %d,  amount_rooms = %d, " +
                "typeName = %s, description = %s, price = %f, amount_roomines = %d",
                this.id, this.amountVacantRooms, this.amountRooms, this.typeName, this.description,
                this.price, this.amountRoomines);
    }
}
