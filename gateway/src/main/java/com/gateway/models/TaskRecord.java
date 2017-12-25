package com.gateway.models;

import rx.functions.Function;

public class TaskRecord {
    private String url;
    private Runnable function;
    private String message;


    public Runnable getFunction() {
        return function;
    }

    public String getUrl() {
        return url;
    }

    public String getMessage() {
        return message;
    }

    public void setFunction(Runnable function) {
        this.function = function;
    }

    public TaskRecord(String url, Runnable function, String message) {
        this.function = function;
        this.message = message;
        this.url = url;
    }
}
