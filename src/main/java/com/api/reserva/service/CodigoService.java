package com.api.reserva.service;

import com.api.reserva.dto.DadosCodigoDTO;
import com.api.reserva.dto.UsuarioDTO;
import com.api.reserva.entity.PreCadastro;
import com.api.reserva.entity.Usuario;
import com.api.reserva.exception.CodigoInvalidoException;
import com.api.reserva.exception.SemResultadosException;
import com.api.reserva.exception.UsuarioDuplicadoException;
import com.api.reserva.repository.PreCadastroRepository;
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
    @Autowired
    private PreCadastroRepository preCadastroRepository;

    @Cacheable(value = "codigos", key = "#token")
    public DadosCodigoDTO buscarCodigo(String token) {
        throw new CodigoInvalidoException();
    }

    @CachePut(value = "codigos", key = "#token")
    public DadosCodigoDTO criarContaEmCache(String token, UsuarioDTO usuarioDTO) {
        if (usuarioRepository.existsByEmail(usuarioDTO.getEmail())) {
            throw new UsuarioDuplicadoException();
        }

        PreCadastro preCadastro = preCadastroRepository.findByEmail(usuarioDTO.getEmail());

        if (preCadastro != null && !preCadastro.isSeCadastrou()) {
            String codigo = CodigoUtil.gerarCodigo(6);

            DadosCodigoDTO dadosCodigoDTO = new DadosCodigoDTO(
                    codigo,
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
                    "EspacoSenai. Confirme sua conta.",
                    String.format("Seu código é: %s. Expira em 15 minutos.", codigo)
            );

            return dadosCodigoDTO;
        }
        return null;
    }

//    @CachePut(value = "codigos", key = "#token")
//    public DadosCodigoDTO gerarCodigoParaEmail(String token, String identificador, String){
//        String token = CodigoUtil.gerarCodigo(6);
//    }

    @CachePut(value = "codigos", key = "#token")
    public DadosCodigoDTO redefinirSenhaGerarCodigo(String token, String identificador) {

        Usuario usuario = usuarioRepository.findByIdentificador(identificador);

        if (usuario == null) {
            throw new SemResultadosException();
        }

        DadosCodigoDTO dadosCodigoDTO = new DadosCodigoDTO(
                CodigoUtil.gerarCodigo(6),
                identificador,
                DadosCodigoDTO.Finalidade.REDEFINICAO_SENHA
        );

        emailService.enviarEmail(
                usuario.getEmail() ,
                "EspacoSenai. Redefinição de senha.",
                String.format("Seu código para redefinir a senha é: %s. Expira em 15 minutos.", dadosCodigoDTO.getCodigo()));
        return dadosCodigoDTO;
    }

//    @CachePut(value = "codigos", key = "#token")
//    public DadosCodigoDTO criarCodigoRedefinirSenha(String token, String identificador, String senha) {
//
//        Usuario usuario = usuarioRepository.findByIdentificador(identificador);
//
//        if (usuario == null) {
//            throw new SemResultadosException();
//        }
//
//        DadosCodigoDTO dadosCodigoDTO = new DadosCodigoDTO(
//                CodigoUtil.gerarCodigo(6),
//                identificador,
//                DadosCodigoDTO.Finalidade.REDEFINICAO_SENHA
//        );
//
//        Map<String, Object> dadosAdicionais = new HashMap<>();
////        dadosAdicionais.put("senhaCriptografada", passwordEncoder.encode(senha));
//        dadosCodigoDTO.setDadosAdicionais(dadosAdicionais);
//
//        emailService.enviarEmail(
//                usuario.getEmail() ,
//                "EspacoSenai. Redefinição de senha.",
//                String.format("Seu código para redefinir a senha é: %s. Expira em 15 minutos.", dadosCodigoDTO.getCodigo()));
//        return dadosCodigoDTO;
//    }

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
    public void deletarCodigo(String token) {
    }
}
