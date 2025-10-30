package com.api.reserva.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class MetodosAuth {

    public static Set<String> extrairRole(Authentication authentication) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Set<String> userRoles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
        return userRoles;
    }

    public static Long extrairId(Authentication authentication) {
        Long id = Long.parseLong(authentication.getName());
        return id;
    }
}
