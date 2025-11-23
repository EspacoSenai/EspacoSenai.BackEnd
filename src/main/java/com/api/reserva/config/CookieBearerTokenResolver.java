package com.api.reserva.config;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;

/**
 * BearerTokenResolver implementation that resolves a JWT from a cookie.
 *
 * The default cookie name is `access_token` (the same name used in the login controller).
 */
public class CookieBearerTokenResolver implements BearerTokenResolver {

    private final String cookieName;

    public CookieBearerTokenResolver() {
        this("access_token");
    }

    public CookieBearerTokenResolver(String cookieName) {
        this.cookieName = cookieName;
    }

    @Override
    @Nullable
    public String resolve(HttpServletRequest request) {
        if (request == null) {
            return null;
        }

        // First look for Authorization header (normal behavior)
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }

        // If not present, try to find the token in the configured cookie
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (cookie != null && cookieName.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}

