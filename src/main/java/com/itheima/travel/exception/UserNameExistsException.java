package com.itheima.travel.exception;

public class UserNameExistsException extends Exception {
    public UserNameExistsException() {
    }
    public UserNameExistsException(String message) {
        super(message);
    }
}
