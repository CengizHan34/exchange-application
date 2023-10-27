package com.example.exchangeapplication.feignclient.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.util.Objects;

@ToString
@Data
@AllArgsConstructor
public class ApiError {
    private int code;
    private String type;
    private String info;

    public String getInfo() {
        if (Objects.nonNull(info)) return info;
        return type;
    }
}
