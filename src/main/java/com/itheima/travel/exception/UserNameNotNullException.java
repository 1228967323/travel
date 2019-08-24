package com.itheima.travel.exception;

public class UserNameNotNullException extends Exception {
    public UserNameNotNullException() {
    }
    public UserNameNotNullException(String message) {
        super(message);
    }
}
