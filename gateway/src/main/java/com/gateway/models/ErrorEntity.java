package com.gateway.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@JsonFormat
public class ErrorEntity {
    @NotNull
    @Min(0)
    private Integer errorCode;
    @NotNull
    @Length(max = 512)
    private String message;

    public ErrorEntity(Integer code, String message) {
        this.errorCode = code;
        this.message = message;
    }
}
