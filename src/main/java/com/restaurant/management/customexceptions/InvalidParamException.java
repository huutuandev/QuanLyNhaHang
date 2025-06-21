package com.restaurant.management.customexceptions;

public class InvalidParamException extends Exception{
    public InvalidParamException(String message) {
        super(message);
    }
}

