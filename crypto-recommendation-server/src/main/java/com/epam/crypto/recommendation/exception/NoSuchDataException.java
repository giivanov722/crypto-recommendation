package com.epam.crypto.recommendation.exception;

public class NoSuchDataException extends RuntimeException{
    public NoSuchDataException(String message){
        super(message);
    }
}
