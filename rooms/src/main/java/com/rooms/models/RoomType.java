package com.rooms.models;

import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
@Table(name = "rooms_types")
public class RoomType {
    @Id
    @Min(0)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Min(0)
    @NotNull
    @Column(name = "amount_vacant_rooms")
    private Integer amountVacantRooms;

    @Min(0)
    @NotNull
    @Column(name = "amount_rooms")
    private Integer amountRooms;

    @NotNull
    @Length(max = 256)
    @Column(name = "type_name")
    private String typeName;

    @NotNull
    @Length(max = 256)
    @Column(name = "description")
    private String description;

    @NotNull
    @Column(name = "price")
    private BigDecimal price;

    @NotNull
    @Min(0)
    @Column(name = "amount_roomines")
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

    public RoomType(long id, Integer amountVacantRooms,
                    Integer amountRooms, String typeName,
                    String description, BigDecimal price, Integer amountRoomines) {
        this.amountVacantRooms = amountVacantRooms;
        this.amountRooms = amountRooms;
        this.typeName = typeName;
        this.price = price;
        this.description = description;
        this.amountRoomines = amountRoomines;
        this.id = id;
    }

    @Override
    public String toString() {
        return String.format("id = %d, amount_vacant_rooms = %d,  amount_rooms = %d, " +
                "typeName = %s, description = %s, price = %f, amount_roomines = %d",
                this.id, this.amountVacantRooms, this.amountRooms, this.typeName, this.description,
                this.price, this.amountRoomines);
    }
}
