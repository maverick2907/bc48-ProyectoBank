package com.bootcamp48.java.card.exception;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ErrorResponse {
    private Integer status;
    private String message;
    private Date timestamp;
    List<String> errors;
}
