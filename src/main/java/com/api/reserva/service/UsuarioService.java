package com.api.reserva.service;

import com.api.reserva.dto.DadosCodigoDTO;
import com.api.reserva.dto.UsuarioDTO;
import com.api.reserva.dto.UsuarioReferenciaDTO;
import com.api.reserva.entity.Role;
import com.api.reserva.entity.Usuario;
import com.api.reserva.enums.UsuarioStatus;
import com.api.reserva.exception.CodigoInvalidoException;
import com.api.reserva.exception.SemPermissaoException;
import com.api.reserva.exception.SemResultadosException;
import com.api.reserva.exception.UsuarioDuplicadoException;
import com.api.reserva.repository.PreCadastroRepository;
import com.api.reserva.repository.RoleRepository;
import com.api.reserva.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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
        codigoService.removerCodigo(token);
    }

//    @Transactional
//    public void salvar(UsuarioDTO usuarioDTO, Authentication authentication) {
//        if (usuarioRepository.existsByEmailOrTelefone(usuarioDTO.getEmail(), usuarioDTO.getTelefone())) {
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
//        usuario.setTelefone(usuarioDTO.getTelefone());
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

    @Transactional
    public void criarInterno(@Valid UsuarioDTO internoDTO, Usuario autenticacao) {

        // ✅ Validações iniciais
        if (internoDTO == null || autenticacao == null) {
            throw new IllegalArgumentException("Dados inválidos para criação de usuário");
        }

        if (usuarioExistePorEmail(internoDTO.getEmail())) {
            throw new UsuarioDuplicadoException("Email já cadastrado no sistema");
        }

        // ✅ Roles do autenticado
        Set<Role> rolesAutenticacao = autenticacao.getRoles();
        boolean isAdmin = rolesAutenticacao.stream()
                .anyMatch(r -> r.getRoleNome().equals(Role.Values.ADMIN));


        // ✅ Roles solicitadas
        List<Role> rolesSolicitadas = roleRepository.findAllById(internoDTO.getRolesIds());
        if (rolesSolicitadas.isEmpty()) {
            throw new SemResultadosException("Nenhuma role válida encontrada");
        }

        // ✅ Regras de negócio
        Set<Role> rolesParaAdicionar = new HashSet<>();
        for (Role role : rolesSolicitadas) {
            if (role.getRoleNome() == Role.Values.ADMIN) {
                throw new SemPermissaoException("Não é permitido criar novos administradores");
            }
            if (role.getRoleNome().equals(Role.Values.ADMIN) && !isAdmin) {
                String teste = String.valueOf(role.getRoleNome());
                throw new SemPermissaoException("Apenas ADMINS podem criar outros coordenadores: " + teste);
            }
            rolesParaAdicionar.add(role);
        }

        if (rolesParaAdicionar.isEmpty()) {
            throw new IllegalArgumentException("Nenhuma role válida foi selecionada para o novo usuário");
        }

        // ✅ Criação do usuário
        Usuario usuario = new Usuario();
        usuario.setNome(internoDTO.getNome().trim());
        usuario.setEmail(internoDTO.getEmail().trim().toLowerCase());
        usuario.setSenha(passwordEncoder.encode(internoDTO.getSenha()));
        usuario.setStatus(UsuarioStatus.ATIVO);
        usuario.setRoles(rolesParaAdicionar);
        usuario.gerarTag();

        usuarioRepository.save(usuario);
    }


}