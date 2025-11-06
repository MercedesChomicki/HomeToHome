package com.hometohome.chat_service.config;

public class TokenContext {
    private static final ThreadLocal<String> HOLDER = new ThreadLocal<>();

    public static void setToken(String token) {
        HOLDER.set(token);
    }

    public static String getToken() {
        return HOLDER.get();
    }

    public static void clear() {
        HOLDER.remove();
    }
}
