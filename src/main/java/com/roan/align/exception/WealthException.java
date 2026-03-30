package com.roan.align.exception;

import org.springframework.http.HttpStatus;

/**
 * Custom exception for Wealth application errors
 * 
 * @author Roan
 * @date 2026/3/18
 */
public class WealthException extends RuntimeException {

    private final int code;
    private final HttpStatus status;
    private final String description;

    public WealthException(int code, HttpStatus status, String description, String message) {
        super(message);
        this.code = code;
        this.status = status;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }
}
