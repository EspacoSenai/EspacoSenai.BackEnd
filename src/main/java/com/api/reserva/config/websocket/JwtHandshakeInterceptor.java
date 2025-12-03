package com.api.reserva.config.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import jakarta.servlet.http.HttpServletRequest;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Validates the JWT presented during the WebSocket handshake and attaches the authenticated user
 * to the session attributes so that STOMP frames carry a proper Principal.
 */
@Component
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(JwtHandshakeInterceptor.class);

    private final JwtDecoder jwtDecoder;

    public JwtHandshakeInterceptor(JwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }

    @Override
    public boolean beforeHandshake(@NonNull ServerHttpRequest request,
                                   @NonNull ServerHttpResponse response,
                                   @NonNull WebSocketHandler wsHandler,
                                   @NonNull Map<String, Object> attributes) {
        String token = resolveToken(request);
        System.out.println("🔍 WebSocket Handshake - Token: " + (token != null ? "ENCONTRADO" : "NÃO ENCONTRADO"));

        if (!StringUtils.hasText(token)) {
            logger.warn("⚠️ WebSocket handshake: token ausente - verificar se está no header Authorization");
            System.out.println("⚠️ Headers recebidos: " + request.getHeaders());
            // Não rejeitar ainda para debugar
            return true; // Permitir temporariamente para debugar
        }

        try {
            Jwt jwt = jwtDecoder.decode(token);
            Long usuarioId = Long.valueOf(jwt.getSubject());
            Collection<GrantedAuthority> authorities = extractAuthorities(jwt);
            Principal principal = new UsernamePasswordAuthenticationToken(usuarioId.toString(), null, authorities);

            attributes.put("principal", principal);
            attributes.put("usuarioId", usuarioId);
            System.out.println("✅ WebSocket autenticado para usuário: " + usuarioId);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            logger.warn("❌ WebSocket handshake rejeitado: token inválido", ex);
            System.out.println("❌ Erro ao decodificar token: " + ex.getMessage());
            return false;
        }
    }

    @Override
    public void afterHandshake(@NonNull ServerHttpRequest request,
                               @NonNull ServerHttpResponse response,
                               @NonNull WebSocketHandler wsHandler,
                               Exception exception) {
        // noop
    }

    private String resolveToken(ServerHttpRequest request) {
        List<String> headerAuth = request.getHeaders().get("Authorization");
        if (headerAuth != null) {
            for (String candidate : headerAuth) {
                if (StringUtils.hasText(candidate)) {
                    return cleanToken(candidate);
                }
            }
        }

        if (request instanceof ServletServerHttpRequest servletRequest) {
            HttpServletRequest httpServletRequest = servletRequest.getServletRequest();
            String tokenParam = httpServletRequest.getParameter("access_token");
            if (!StringUtils.hasText(tokenParam)) {
                tokenParam = httpServletRequest.getParameter("token");
            }
            if (!StringUtils.hasText(tokenParam)) {
                tokenParam = httpServletRequest.getParameter(StompCommand.CONNECT.name().toLowerCase());
            }
            return cleanToken(tokenParam);
        }

        return null;
    }

    private String cleanToken(String rawToken) {
        if (!StringUtils.hasText(rawToken)) {
            return null;
        }
        return rawToken.startsWith("Bearer ") ? rawToken.substring(7) : rawToken;
    }

    private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        Object scopeClaim = jwt.getClaims().get("scope");
        if (scopeClaim == null) {
            return List.of();
        }

        List<String> scopes = new ArrayList<>();
        if (scopeClaim instanceof String scopeString) {
            scopes = List.of(scopeString.split(" "));
        } else if (scopeClaim instanceof Collection<?> scopeCollection) {
            scopes = scopeCollection.stream()
                    .map(Object::toString)
                    .collect(Collectors.toList());
        }

        return scopes.stream()
                .filter(StringUtils::hasText)
                .map(scope -> scope.startsWith("SCOPE_") ? scope : "SCOPE_" + scope)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}

