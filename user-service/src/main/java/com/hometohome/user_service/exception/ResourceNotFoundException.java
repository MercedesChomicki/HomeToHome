package com.hometohome.user_service.exception;

import java.io.Serializable;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resourceName, Serializable id) {
        super(resourceName + " con ID " + id + " no existe.");
    }
}

