package com.hometohome.chat_service.config;

import java.security.Principal;

public class StompPrincipal implements Principal {
    private final String name;
    private final String token;

    public StompPrincipal(String name, String token) {
        this.name = name;
        this.token = token;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getToken() {
        return token;
    }
}