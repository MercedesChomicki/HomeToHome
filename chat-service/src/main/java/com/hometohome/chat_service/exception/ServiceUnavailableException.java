package com.hometohome.chat_service.exception;

public class ServiceUnavailableException extends RuntimeException {
    public ServiceUnavailableException(String msg) {
        super(msg);
    }
}