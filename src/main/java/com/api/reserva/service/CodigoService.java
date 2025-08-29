package com.api.reserva.service;

import com.api.reserva.dto.DadosCodigoDTO;
import com.api.reserva.dto.UsuarioDTO;
import com.api.reserva.exception.UsuarioDuplicadoException;
import com.api.reserva.repository.UsuarioRepository;
import com.api.reserva.util.CodigoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CodigoService {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private PreCadastroService preCadastroService;

    @Cacheable(value = "codigos", key = "#token")
    public DadosCodigoDTO buscarCodigo(String token) {
        return null;
    }

    @CachePut(value = "codigos", key = "#token")
    public DadosCodigoDTO criarContaEmCache(String token, UsuarioDTO usuarioDTO) {
        if(usuarioService.usuarioExistePorEmail(usuarioDTO.getEmail())) {
            throw new UsuarioDuplicadoException();
        }

        if(preCadastroService.verificarElegibilidade(usuarioDTO.getEmail())){
            DadosCodigoDTO dadosCodigoDTO = new DadosCodigoDTO(
                    CodigoUtil.gerarCodigo(6),
                    usuarioDTO.getEmail(),
                    DadosCodigoDTO.Finalidade.EMAIL_VERIFICACAO
            );

            Map<String, Object> dadosUsuario = new HashMap<>();
            dadosUsuario.put("nome", usuarioDTO.getNome());
            dadosUsuario.put("email", usuarioDTO.getEmail());
            dadosUsuario.put("senhaCriptografada", passwordEncoder.encode(usuarioDTO.getSenha()));
            dadosCodigoDTO.setDadosAdicionais(dadosUsuario);

            emailService.enviarEmail(
                    usuarioDTO.getEmail(),
                    "Espaco Senai. Confirme sua conta.",
                    "Código: " + dadosCodigoDTO.getCodigo() + ". Expira em 15 minutos."
            );
            return dadosCodigoDTO;
        } 
        return null;
    }

    @CachePut(value = "codigos", key = "#token")
    public DadosCodigoDTO criarCodigoRedefinirSenha(String token, String email) {
        DadosCodigoDTO dadosCodigoDTO = new DadosCodigoDTO(
                CodigoUtil.gerarCodigo(6),
                email,
                DadosCodigoDTO.Finalidade.REDEFINICAO_SENHA
        );
        return dadosCodigoDTO;
    }

    @CachePut(value = "codigos", key = "#token")
    public DadosCodigoDTO criarCodigoConfirmarReserva(String token, String email, Long reservaId) {
        DadosCodigoDTO dadosCodigoDTO = new DadosCodigoDTO(
                CodigoUtil.gerarCodigo(6),
                email,
                DadosCodigoDTO.Finalidade.CONFIRMACAO_RESERVA
        );

        Map<String, Object> dadosReserva = new HashMap<>();
        dadosReserva.put("reservaId", reservaId);

        dadosCodigoDTO.setDadosAdicionais(dadosReserva);
        return dadosCodigoDTO;
    }

    @CacheEvict(value = "codigos", key = "#token")
    public void removerCodigo(String token) {
    }

}
