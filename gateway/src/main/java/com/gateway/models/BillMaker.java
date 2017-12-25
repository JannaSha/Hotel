package com.gateway.models;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public class BillMaker {

    @NotNull
    @Min(0)
    private long cartNumber;

    public void setCartNumber(long cartNumber) {
        this.cartNumber = cartNumber;
    }

    public long getCartNumber() {
        return cartNumber;
    }

    @Override
    public String toString() {
        return String.format("Bill Maker cart number = %d", cartNumber);
    }
}
