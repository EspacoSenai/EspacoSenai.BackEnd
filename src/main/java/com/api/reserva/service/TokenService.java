package com.api.reserva.service;

import com.api.reserva.dto.LoginRequest;
import com.api.reserva.dto.LoginResponse;
import com.api.reserva.entity.Usuario;
import com.api.reserva.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.stream.Collectors;

@Service
public class TokenService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtEncoder jwtEncoder;

    // Constantes de expiração
    private static final Long EXPIRACAO_USUARIO_SEGUNDOS = 6000L; // ~1.6 horas
    private static final Long EXPIRACAO_IMPRESSORA_SEGUNDOS = 604800L; // 1 semana (7 dias)

    /**
     * Login unificado para todos os usuários
     * - Usuários normais: Token de ~1.6 horas
     * - Impressoras: Token de 1 semana
     * 
     * A diferenciação acontece automática baseado na role do usuário
     */
    public LoginResponse signIn(LoginRequest loginRequest) {
        Usuario usuario = usuarioRepository.findByIdentificador(loginRequest.identificador());

        if(usuario != null && usuario.isLoginValid(loginRequest.senha(), passwordEncoder)) {

            String scopes = usuario.getRoles().stream()
                    .map(role -> role.getRoleNome().name())
                    .collect(Collectors.joining(" "));

            // ✅ Verificar se é impressora para usar token de 1 semana
            boolean ehImpressora = usuario.getRoles().stream()
                    .anyMatch(role -> role.getRoleNome() == com.api.reserva.entity.Role.Values.IMPRESSORA);
            
            Long expiresIn = ehImpressora ? EXPIRACAO_IMPRESSORA_SEGUNDOS : EXPIRACAO_USUARIO_SEGUNDOS;

            JwtClaimsSet claims = JwtClaimsSet.builder()
                    .issuer("myapp")
                    .subject(usuario.getId().toString())
                    .claim("scope", scopes)
                    .issuedAt(Instant.now())
                    .expiresAt(Instant.now().plusSeconds(expiresIn))
                    .build();

            String jwtValue = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
            return new LoginResponse(jwtValue, expiresIn);
        } else {
            throw new BadCredentialsException("Identificador ou senha inválidos.");
        }
    }
}
