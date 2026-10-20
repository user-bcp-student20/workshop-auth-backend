package com.bcp.workshop.auth;

import io.smallrye.jwt.build.Jwt;

public final class LegacyTokenUtil {
    private static final long TOKEN_LIFETIME_SECONDS = 3600;

    private LegacyTokenUtil() {}

    public static String create(String username, String role) {
        return Jwt.upn(username)
                .subject(username)
                .groups(role)
                .expiresIn(TOKEN_LIFETIME_SECONDS)
                .sign();
    }
}
