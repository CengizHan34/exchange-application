package com.example.exchangeapplication.controller.base;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

/**
 * @author created by cengizhan on 30.04.2021
 */
abstract public class BaseController {
    public ResponseEntity responseEntity(Object t) {
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(t);
    }
}
