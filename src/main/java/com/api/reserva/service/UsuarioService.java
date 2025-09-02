package com.api.reserva.service;

import com.api.reserva.dto.DadosCodigoDTO;
import com.api.reserva.dto.UsuarioDTO;
import com.api.reserva.dto.UsuarioReferenciaDTO;
import com.api.reserva.entity.Role;
import com.api.reserva.entity.Usuario;
import com.api.reserva.enums.UsuarioStatus;
import com.api.reserva.exception.CodigoInvalidoException;
import com.api.reserva.exception.SemResultadosException;
import com.api.reserva.exception.UsuarioDuplicadoException;
import com.api.reserva.repository.PreCadastroRepository;
import com.api.reserva.repository.RoleRepository;
import com.api.reserva.repository.UsuarioRepository;
import com.api.reserva.util.MetodosAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class UsuarioService {

    @Autowired
    UsuarioRepository usuarioRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private PreCadastroRepository preCadastroRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private PreCadastroService preCadastroService;
    @Autowired
    @Lazy
    private CodigoService codigoService;

    /**
     * Lista todos os usuários do sistema.
     */
    public List<UsuarioReferenciaDTO> buscar() {
        List<Usuario> usuarios = usuarioRepository.findAll();

        if (usuarios.isEmpty()) {
            throw new SemResultadosException("Nenhum usuário encontrado.");
        }

        return usuarios.stream().map(UsuarioReferenciaDTO::new).toList();
    }

    public UsuarioReferenciaDTO buscar(Long id) {
        return new UsuarioReferenciaDTO(usuarioRepository.findById(id)
                .orElseThrow(SemResultadosException::new));
    }

    public UsuarioReferenciaDTO buscarPorTag(String tag) {
        return new UsuarioReferenciaDTO(usuarioRepository
                .findByTag(tag)
                .orElseThrow(SemResultadosException::new));
    }

    @Transactional
    public void confirmarConta(String token, String codigo) {

//        Set<String> rolesToken;

        DadosCodigoDTO dadosCodigoDTO = codigoService.buscarCodigo(token);

        if(dadosCodigoDTO == null || !dadosCodigoDTO.getCodigo().equals(codigo)) {
            throw new CodigoInvalidoException();
        }

        String nome = dadosCodigoDTO.getDado("nome").toString();
        String email = dadosCodigoDTO.getDado("email").toString();
        String senhaCriptografada = dadosCodigoDTO.getDado("senhaCriptografada").toString();

        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setEmail(email);
//        usuario.setTelefone(usuarioDTO.getTelefone());
        usuario.setSenha(senhaCriptografada);
        usuario.gerarTag();
        usuario.setStatus(UsuarioStatus.ATIVO);

        Role roleEstudante = roleRepository.findByRoleNome(Role.Values.ESTUDANTE)
                .orElseThrow(() -> new SemResultadosException("Role ESTUDANTE"));
        usuario.getRoles().add(roleEstudante);
        usuarioRepository.save(usuario);
        preCadastroService.bu(usuario.getEmail());
        codigoService.deletarCodigo(token);
    }

//    @Transactional
//    public void salvar(UsuarioDTO usuarioDTO, Authentication authentication) {
//        if (usuarioRepository.existsByEmail(usuarioDTO.getEmail())) {
//            throw new UsuarioDuplicadoException();
//        }
//
//        Set<String> rolesToken;
//
//        if (authentication != null) {
//            rolesToken = MetodosAuth.extrairRole(authentication);
//        } else {
//            rolesToken = Collections.emptySet();
//        }
//
//        Usuario usuario = new Usuario();
//        usuario.setNome(usuarioDTO.getNome());
//        usuario.setEmail(usuarioDTO.getEmail());
//        usuario.setSenha(passwordEncoder.encode(usuarioDTO.getSenha()));
//        usuario.gerarTag();
//        usuario.setStatus(UsuarioStatus.INATIVO);
//
//        List<Role> roles = roleRepository.findAllById(usuarioDTO.getRolesIds());
//        roles.removeIf(role -> Role.Values.ADMIN.equals(role.getRoleNome()));
//
//        if (rolesToken.contains("SCOPE_ADMIN")) {
//            usuario.setStatus(usuarioDTO.getStatus());
//            usuario.setRoles(new HashSet<>(roles));
//            usuarioRepository.save(usuario);
//        } else if (preCadastroService.verificarElegibilidade(usuarioDTO.getEmail(), usuarioDTO.getTelefone())) {
//            usuario.getRoles().add(roleRepository.findByRoleNome(Role.Values.ESTUDANTE)
//                    .orElseThrow(() -> new SemResultadosException("Role ESTUDANTE")));
//            usuarioRepository.save(usuario);
//        }
//    }


    /**
     * Atualiza os dados de um usuário existente.
     */
    public void atualizar(Long id, UsuarioDTO usuarioDTO) {
        if (usuarioRepository.existsByEmailAndIdNot(usuarioDTO.getEmail(), id)) {
            throw new UsuarioDuplicadoException();
        }

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(SemResultadosException::new);

        usuario.setNome(usuarioDTO.getNome());
        usuario.setEmail(usuarioDTO.getEmail());
        usuario.setSenha(usuarioDTO.getSenha());

        usuarioRepository.save(usuario);
    }

    /**
     * Exclui um usuário do sistema.
     */
    public void deletar(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(SemResultadosException::new);
        usuarioRepository.delete(usuario);
    }

    public boolean usuarioExistePorEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

}