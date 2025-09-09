package com.hometohome.chat_service.config;

public class PrincipalContextHolder {
    private static final ThreadLocal<StompPrincipal> context = new ThreadLocal<>();

    public static void setPrincipal(StompPrincipal principal) {
        context.set(principal);
    }

    public static StompPrincipal getPrincipal() {
        return context.get();
    }

    public static String getToken() {
        return context.get() != null ? context.get().getToken() : null;
    }

    public static void clear() {
        context.remove();
    }
}