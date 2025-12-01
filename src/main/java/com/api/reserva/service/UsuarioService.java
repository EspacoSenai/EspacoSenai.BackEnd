package com.api.reserva.service;

import com.api.reserva.dto.DadosCodigoDTO;
import com.api.reserva.dto.TurmaReferenciaDTO;
import com.api.reserva.dto.UsuarioDTO;
import com.api.reserva.dto.UsuarioReferenciaDTO;
import com.api.reserva.entity.PreCadastro;
import com.api.reserva.entity.Reserva;
import com.api.reserva.entity.Role;
import com.api.reserva.entity.Usuario;
import com.api.reserva.enums.StatusReserva;
import com.api.reserva.enums.UsuarioStatus;
import com.api.reserva.exception.CodigoInvalidoException;
import com.api.reserva.exception.SemPermissaoException;
import com.api.reserva.exception.SemResultadosException;
import com.api.reserva.exception.UsuarioDuplicadoException;
import com.api.reserva.repository.*;
import com.api.reserva.util.MetodosAuth;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    private ReservaRepository reservaRepository;
    @Autowired
    @Lazy
    private CodigoService codigoService;
    @Autowired
    private TurmaRepository turmaRepository;

    /**
     * Lista usuários filtrados por role do usuário autenticado:
     * - ADMIN: retorna todos
     * - COORDENADOR: retorna PROFESSOR e ESTUDANTE
     * - PROFESSOR: retorna apenas ESTUDANTE
     */
    public List<UsuarioReferenciaDTO> buscarTodos(Authentication authentication) {
        Set<String> roles = MetodosAuth.extrairRole(authentication);
        List<Usuario> usuarios = usuarioRepository.findAll();

        List<Usuario> usuariosFiltrados;

        if (roles.contains("SCOPE_ADMIN")) {
            // ADMIN vê todos
            usuariosFiltrados = usuarios;
        } else if (roles.contains("SCOPE_COORDENADOR")) {
            // COORDENADOR vê PROFESSOR e ESTUDANTE
            usuariosFiltrados = usuarios.stream()
                    .filter(u -> u.getRoles().stream()
                            .anyMatch(role -> role.getRoleNome() == Role.Values.PROFESSOR ||
                                    role.getRoleNome() == Role.Values.ESTUDANTE))
                    .collect(Collectors.toList());
        } else if (roles.contains("SCOPE_PROFESSOR")) {
            // PROFESSOR vê apenas ESTUDANTE
            usuariosFiltrados = usuarios.stream()
                    .filter(u -> u.getRoles().stream()
                            .anyMatch(role -> role.getRoleNome() == Role.Values.ESTUDANTE))
                    .collect(Collectors.toList());
        } else {
            // Outras roles não veem nada
            usuariosFiltrados = new java.util.ArrayList<>();
        }

        if (usuariosFiltrados.isEmpty()) {
            throw new SemResultadosException("Nenhum usuário encontrado.");
        }

        return usuariosFiltrados.stream().map(UsuarioReferenciaDTO::new).toList();
    }

    public UsuarioReferenciaDTO buscarPorId(Long id) {
        return new UsuarioReferenciaDTO(usuarioRepository.findById(id)
                .orElseThrow(SemResultadosException::new));
    }

    public UsuarioReferenciaDTO buscarMeuPerfil(Authentication authentication) {
        Long usuarioId = MetodosAuth.extrairId(authentication);
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new SemResultadosException("Usuário"));
        return new UsuarioReferenciaDTO(usuario);
    }

    @Transactional
    public void confirmarConta(String token, String codigo) {

//        Set<String> rolesToken;

        DadosCodigoDTO dadosCodigoDTO = codigoService.buscarCodigo(token);

        if (dadosCodigoDTO == null || !dadosCodigoDTO.getCodigo().equals(codigo)) {
            throw new CodigoInvalidoException();
        }

        String nome = dadosCodigoDTO.getDado("nome").toString();
        String email = dadosCodigoDTO.getDado("email").toString();
        String senhaCriptografada = dadosCodigoDTO.getDado("senhaCriptografada").toString();

        Usuario usuario = new Usuario(
                nome,
                email,
                senhaCriptografada,
                UsuarioStatus.ATIVO
        );

        Role roleEstudante = roleRepository.findByRoleNome(Role.Values.ESTUDANTE)
                .orElseThrow(() -> new SemResultadosException("Role ESTUDANTE"));
        usuario.getRoles().add(roleEstudante);
        usuarioRepository.save(usuario);

        PreCadastro preCadastro = preCadastroRepository.findByEmail(email);
        preCadastro.setSeCadastrou(true);
        preCadastroRepository.save(preCadastro);

        codigoService.deletarCodigo(token);

        emailService.enviarEmail(
                email,
                "EspacoSenai. Conta confirmada.",
                "Sua conta está ativa e pronta para uso. Agradecemos pela confirmação."
        );
    }

    public boolean redefinirSenhaValidarCodigo(String token, String codigo) {
        DadosCodigoDTO dadosCodigoDTO = codigoService.buscarCodigo(token);
        if (dadosCodigoDTO == null || !dadosCodigoDTO.getCodigo().equals(codigo)) {
            throw new CodigoInvalidoException();
        }

        dadosCodigoDTO.setValidado(true);
        return true;
    }

    @Transactional
    public void redefinirSenhaNovaSenha(String token, String novaSenha) {
        DadosCodigoDTO dadosCodigoDTO = codigoService.buscarCodigo(token);

        if (!dadosCodigoDTO.isValidado()) {
            throw new CodigoInvalidoException();
        }

        Usuario usuario = usuarioRepository.findByIdentificador(dadosCodigoDTO.getIdentificador());

        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(usuario);

        codigoService.deletarCodigo(token);

        emailService.enviarEmail(
                usuario.getEmail(),
                "Espaco Senai. Senha alterada.",
                "Sua senha foi alterada com sucesso."
        );
    }


    /*
     * Acesso de admins controlado por controller
     * Validação:
     * - ADMIN: pode criar qualquer role (menos ADMIN)
     * - COORDENADOR: pode criar apenas ESTUDANTE
     * - PROFESSOR: pode criar apenas ESTUDANTE
     */
    @Transactional
    public void salvarPrivilegiado(@Valid @RequestBody UsuarioDTO internoDTO, Authentication authentication) {

        if (usuarioRepository.existsByEmail(internoDTO.getEmail())) {
            throw new UsuarioDuplicadoException();
        }

        Set<String> rolesToken = MetodosAuth.extrairRole(authentication);
        Set<Role> roles = new HashSet<>(roleRepository.findAllById(internoDTO.getRolesIds()));
        roles.removeIf(userRole -> Role.Values.ADMIN.equals(userRole.getRoleNome()));

        // Validação de permissão por role
        if (rolesToken.contains("SCOPE_COORDENADOR") || rolesToken.contains("SCOPE_PROFESSOR")) {
            // COORDENADOR e PROFESSOR só podem criar ESTUDANTE
            if (!roles.stream().allMatch(role -> role.getRoleNome() == Role.Values.ESTUDANTE)) {
                throw new SemPermissaoException("Você só pode criar usuários com role ESTUDANTE.");
            }
        } else if (!rolesToken.contains("SCOPE_ADMIN")) {
            // Apenas ADMIN, COORDENADOR e PROFESSOR podem criar usuários
            throw new SemPermissaoException("Permissão negada para criar usuários.");
        }

        if (roles.isEmpty()) {
            throw new SemResultadosException("Role(s)");
        }

        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome(internoDTO.getNome());
        novoUsuario.setEmail(internoDTO.getEmail());
        novoUsuario.setSenha(passwordEncoder.encode(internoDTO.getSenha()));
        novoUsuario.setStatus(internoDTO.getStatus());


        novoUsuario.setRoles(roles);

        List<String> rolesString = roles.stream()
                .map(r -> r.getRoleNome().toString())
                .collect(Collectors.toList());

        usuarioRepository.save(novoUsuario);
        emailService.enviarEmail(
                internoDTO.getEmail(),
                "EspacoSenai. Um administrador te cadastrou.",
                "Olá " + novoUsuario.getNome() + ", você foi cadastrado por um administrador." + ". Seu privilégio é: " + rolesString);
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
//        } else if (preCadastroService.verificarElegibilidade(usuarioDTO.getEmail())) {
//            usuario.getRoles().add(roleRepository.findByRoleNome(Role.Values.ESTUDANTE)
//                    .orElseThrow(() -> new SemResultadosException("Role ESTUDANTE")));
//            usuarioRepository.save(usuario);
//        }
//    }

    /**
     * Atualiza os dados de um usuário existente.
     */
    @Transactional
    public void atualizar(UsuarioDTO usuarioDTO, Authentication authentication) {
        if (usuarioRepository.existsByEmail(usuarioDTO.getEmail())) {
            throw new UsuarioDuplicadoException();
        }

        Long usuarioId = MetodosAuth.extrairId(authentication);

        if (usuarioRepository.existsByEmailAndIdNot(usuarioDTO.getEmail(), usuarioId)) {
            throw new UsuarioDuplicadoException();
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(SemResultadosException::new);

        usuario.setEmail(usuarioDTO.getEmail());
        usuario.setSenha(passwordEncoder.encode(usuarioDTO.getSenha()));

        usuarioRepository.save(usuario);
    }

    @Transactional
    public void alterarStatus(Long id, Long idStatus) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(SemResultadosException::new);
        try {
            UsuarioStatus novoStatus = UsuarioStatus.fromId(idStatus);
            usuario.setStatus(novoStatus);
            if (equals(UsuarioStatus.BLOQUEADO)) {
                Set<Reserva> reservas = reservaRepository.findAllByHost_Id(id)
                        .stream()
                        .filter(r -> !r.getStatusReserva().equals(StatusReserva.CANCELADA) &&
                                !r.getStatusReserva().equals(StatusReserva.NEGADA) &&
                                !r.getStatusReserva().equals(StatusReserva.CONCLUIDA))
                        .collect(Collectors.toSet());
            }
        } catch (SemResultadosException e) {
            throw new SemResultadosException("Status de usuário");
        }
        usuarioRepository.save(usuario);
    }

    public Set<TurmaReferenciaDTO> minhasTurmas(Authentication authentication) {
        Long usuarioId = MetodosAuth.extrairId(authentication);
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(SemResultadosException::new);

        Set<TurmaReferenciaDTO> turmas = new HashSet<>();

        // Se o usuário tem role PROFESSOR, adiciona turmas onde é professor
        boolean ehProfessor = usuario.getRoles().stream()
                .anyMatch(role -> role.getRoleNome() == Role.Values.PROFESSOR);

        if (ehProfessor) {
            // Adiciona turmas onde é professor
            turmaRepository.findAllByProfessor_Id(usuarioId)
                    .stream()
                    .map(TurmaReferenciaDTO::new)
                    .forEach(turmas::add);
        }

        // Adiciona turmas onde é estudante (tanto para professor quanto para estudante)
        turmaRepository.findAllByEstudantes_Id(usuarioId)
                .stream()
                .map(TurmaReferenciaDTO::new)
                .forEach(turmas::add);

        return turmas;
    }

    public List<UsuarioReferenciaDTO> buscarEstudantes() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        List<Usuario> estudantes = usuarios.stream()
                .filter(u -> u.getRoles().stream()
                        .anyMatch(role -> role.getRoleNome() == Role.Values.ESTUDANTE))
                .collect(Collectors.toList());

        if (estudantes.isEmpty()) {
            throw new SemResultadosException("Nenhum estudante encontrado.");
        }

        return estudantes.stream().map(UsuarioReferenciaDTO::new).toList();
    }

    public Set<UsuarioReferenciaDTO> buscarPorRole(Role.Values roleNome) {
        List<Usuario> usuarios = usuarioRepository.findAll();
        Set<Usuario> usuariosFiltrados = usuarios.stream()
                .filter(u -> u.getRoles().stream()
                        .anyMatch(role -> role.getRoleNome() == roleNome))
                .collect(Collectors.toSet());

        if (usuariosFiltrados.isEmpty()) {
            throw new SemResultadosException("Nenhum usuário encontrado com a role: " + roleNome);
        }

        return usuariosFiltrados.stream()
                .map(UsuarioReferenciaDTO::new)
                .collect(Collectors.toSet());
    }
}
