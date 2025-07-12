package com.api.taskmanager.exception;

public class InvalidTaskStateException extends RuntimeException{
    public InvalidTaskStateException(String msg){
        super(msg);
    }
}
