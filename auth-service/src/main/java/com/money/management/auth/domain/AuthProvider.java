package com.money.management.auth.domain;

public enum AuthProvider {
    LOCAL,
    FACEBOOK,
    GOOGLE,
    TWITTER;

    public static AuthProvider getProvider(String value) {
        for (AuthProvider v : values())
            if (v.name().equalsIgnoreCase(value)) return v;
        throw new IllegalArgumentException();
    }

}
