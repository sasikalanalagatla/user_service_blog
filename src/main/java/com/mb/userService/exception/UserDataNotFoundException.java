package com.mb.userService.exception;

public class UserDataNotFoundException extends RuntimeException{
    public UserDataNotFoundException(String message) {
        super(message);
    }

}
