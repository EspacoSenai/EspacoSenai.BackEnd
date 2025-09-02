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

    public LoginResponse signIn(LoginRequest loginRequest) {
        Usuario usuario = usuarioRepository.findByIdentificador(loginRequest.identificador());

        if(usuario != null && usuario.isLoginValid(loginRequest.senha(), passwordEncoder)) {

            String scopes = usuario.getRoles().stream()
                    .map(role -> role.getRoleNome().name())
                    .collect(Collectors.joining(" "));

            Long expiresIn = 6000L;

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
