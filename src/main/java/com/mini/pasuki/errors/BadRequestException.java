package com.mini.pasuki.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends Exception {

    @Override
    public String toString() {
        return "bad request";
    }
}
