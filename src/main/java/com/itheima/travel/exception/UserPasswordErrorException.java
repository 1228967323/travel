package com.itheima.travel.exception;

public class UserPasswordErrorException extends Exception {
    public UserPasswordErrorException() {
    }

    public UserPasswordErrorException(String message) {
        super(message);
    }
}
