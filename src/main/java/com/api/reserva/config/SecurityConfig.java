package com.api.reserva.config;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${jwt.private.key}")
    private RSAPrivateKey privateKey;

    @Value("${jwt.public.key}")
    private RSAPublicKey publicKey;

    /**
     * Configura a cadeia de filtros de segurança para a aplicação.
     *
     * Este método define as configurações de segurança para as requisições HTTP, incluindo:
     * - Permissão para endpoints públicos específicos.
     * - Exigência de autenticação para todas as outras requisições.
     * - Desabilitação de proteção CSRF (Cross-Site Request Forgery).
     * - Configuração do servidor de recursos OAuth2 para usar JWT (JSON Web Token).
     * - Política de criação de sessão como STATELESS (sem estado).
     *
     * @param httpSecurity Objeto HttpSecurity usado para configurar a segurança HTTP.
     * @return Um objeto SecurityFilterChain configurado.
     * @throws Exception Caso ocorra algum erro durante a configuração.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                // Configura as autorizações para as requisições HTTP
                .authorizeHttpRequests(authorization -> authorization
                        // Permite acesso público ao endpoint de login (POST e GET)
                        .requestMatchers(HttpMethod.GET,"/auth/**").permitAll()
                        .requestMatchers(HttpMethod.POST,"/auth/**").permitAll()
//                        .requestMatchers(HttpMethod.GET, "/usuario/confirmar-conta/{token}/{codigo}").permitAll()
                        // Exige autenticação para qualquer outra requisição
                        .anyRequest().authenticated())
                // Desabilita a proteção contra CSRF
                .csrf(AbstractHttpConfigurer::disable)
                // Configura o servidor de recursos OAuth2 para usar JWT
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(Customizer.withDefaults()))
                // Define a política de criação de sessão como sem estado
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }

    /**
     * Configura o decodificador de JWT (JSON Web Token) para a aplicação.
     *
     * Este método utiliza a classe NimbusJwtDecoder para criar um decodificador de JWT
     * baseado em uma chave pública RSA. O decodificador é usado para validar e interpretar
     * os tokens JWT recebidos pela aplicação.
     *
     * @return Um objeto JwtDecoder configurado com a chave pública RSA.
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(publicKey).build();
    }

    /**
     * Configura o codificador de JWT (JSON Web Token) para a aplicação.
     *
     * Este método utiliza a classe NimbusJwtEncoder para criar um codificador de JWT
     * baseado em uma chave RSA. Ele constrói um objeto JWK (JSON Web Key) que contém
     * a chave pública e privada RSA, e o encapsula em um JWKSet imutável. O codificador
     * é usado para assinar tokens JWT com a chave privada.
     *
     * @return Um objeto JwtEncoder configurado com a chave RSA.
     */
    @Bean
    public JwtEncoder jwtEncoder() {
        // Cria um objeto JWK (JSON Web Key) com a chave pública e privada RSA
        JWK jwk = new RSAKey.Builder((this.publicKey)) // Configura a chave pública RSA
                .privateKey(privateKey) // Configura a chave privada RSA
                .build(); // Constrói o objeto JWK

        // Encapsula o JWK em um JWKSet imutável
        ImmutableJWKSet jwks = new ImmutableJWKSet<>(new JWKSet(jwk));

        // Retorna um codificador de JWT configurado com o JWKSet
        return new NimbusJwtEncoder(jwks);
    }

    /**
     * Configura o codificador de senhas para a aplicação.
     *
     * Este método cria e retorna uma instância de BCryptPasswordEncoder, que é usada para
     * codificar senhas de forma segura. O BCrypt é um algoritmo de hash que incorpora um
     * fator de custo, tornando-o mais resistente a ataques de força bruta.
     *
     * @return Um objeto PasswordEncoder configurado com o algoritmo BCrypt.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        // Retorna uma instância de BCryptPasswordEncoder para codificação de senhas
        return new BCryptPasswordEncoder();
    }
}
