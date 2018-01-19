package com.gateway;

public enum ServiceNames {
    ORDERS(0),
    BILLING(1),
    AUTH(2),
    USERS(3),
    ROOMS(4);

    private final int value;
    private ServiceNames(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
