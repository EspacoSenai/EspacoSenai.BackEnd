package com.api.reserva.config.websocket;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.lang.NonNull;
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
        // Tentar extrair do header Authorization (padrão HTTP)
        List<String> headerAuth = request.getHeaders().get("Authorization");
        if (headerAuth != null && !headerAuth.isEmpty()) {
            for (String candidate : headerAuth) {
                if (StringUtils.hasText(candidate)) {
                    String cleaned = cleanToken(candidate);
                    if (cleaned != null) {
                        System.out.println("✅ Token extraído do header Authorization");
                        return cleaned;
                    }
                }
            }
        }

        // Fallback: tentar extrair da query string ou parâmetros
        if (request instanceof ServletServerHttpRequest servletRequest) {
            HttpServletRequest httpServletRequest = servletRequest.getServletRequest();
            String uri = request.getURI().toString();

            // Extrair token da query string
            if (uri.contains("token=")) {
                String[] parts = uri.split("token=");
                if (parts.length > 1) {
                    String token = parts[1].split("[&\\?]")[0];
                    if (StringUtils.hasText(token)) {
                        System.out.println("✅ Token extraído da query string 'token'");
                        return token;
                    }
                }
            }

            // Tentar access_token
            String tokenParam = httpServletRequest.getParameter("access_token");
            if (StringUtils.hasText(tokenParam)) {
                System.out.println("✅ Token extraído do parâmetro 'access_token'");
                return cleanToken(tokenParam);
            }

            // Tentar token
            tokenParam = httpServletRequest.getParameter("token");
            if (StringUtils.hasText(tokenParam)) {
                System.out.println("✅ Token extraído do parâmetro 'token'");
                return cleanToken(tokenParam);
            }

            // Tentar extrair de access_token na query string
            if (uri.contains("access_token=")) {
                String[] parts = uri.split("access_token=");
                if (parts.length > 1) {
                    String token = parts[1].split("[&\\?]")[0];
                    if (StringUtils.hasText(token)) {
                        System.out.println("✅ Token extraído da query string 'access_token'");
                        return token;
                    }
                }
            }
        }

        System.out.println("⚠️ Token NÃO encontrado em Authorization header ou parâmetros");
        System.out.println("📍 URI: " + request.getURI());
        System.out.println("📍 Headers: " + request.getHeaders());
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

