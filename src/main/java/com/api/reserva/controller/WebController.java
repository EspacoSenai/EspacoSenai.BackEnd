//package com.api.reserva.controller;
//
//import com.api.reserva.dto.AmbienteReferenciaDTO;
//import com.api.reserva.dto.TurmaReferenciaDTO;
//import com.api.reserva.dto.UsuarioReferenciaDTO;
//import com.api.reserva.entity.Role;
//import com.api.reserva.service.AmbienteService;
//import com.api.reserva.service.ReservaService;
//import com.api.reserva.service.TurmaService;
//import com.api.reserva.service.UsuarioService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.Authentication;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//
//import java.util.List;
//import java.util.Set;
//
//@Controller
//public class WebController {
//
//    @Autowired
//    private AmbienteService ambienteService;
//
//    @Autowired
//    private TurmaService turmaService;
//
//    @Autowired
//    private UsuarioService usuarioService;
//
//    @Autowired
//    private ReservaService reservaService;
//
//    @Autowired
//    private com.api.reserva.repository.RoleRepository roleRepository;
//
//    // ===== HOME =====
//    @GetMapping("/")
//    public String home(Authentication authentication, Model model) {
//        if (authentication != null && authentication.isAuthenticated()) {
//            try {
//                    UsuarioReferenciaDTO usuario = usuarioService.buscarMeuPerfil(authentication);
//                    addRoleFlags(authentication, model);
//                model.addAttribute("usuario", usuario);
//                    // role flags are set by addRoleFlags
//                return "redirect:/dashboard";
//            } catch (Exception e) {
//                return "redirect:/login";
//            }
//        }
//        return "index";
//    }
//
//    private void addRoleFlags(Authentication authentication, Model model) {
//        if (authentication == null || !authentication.isAuthenticated()) {
//            model.addAttribute("isAdmin", false);
//            model.addAttribute("isCoordenador", false);
//            model.addAttribute("isProfessor", false);
//            model.addAttribute("isEstudante", false);
//            return;
//        }
//        UsuarioReferenciaDTO usuario = usuarioService.buscarMeuPerfil(authentication);
//        boolean isAdmin = usuario.getRoles().stream().anyMatch(r -> r.getRoleNome() == Role.Values.ADMIN);
//        boolean isCoordenador = usuario.getRoles().stream().anyMatch(r -> r.getRoleNome() == Role.Values.COORDENADOR);
//        boolean isProfessor = usuario.getRoles().stream().anyMatch(r -> r.getRoleNome() == Role.Values.PROFESSOR);
//        boolean isEstudante = usuario.getRoles().stream().anyMatch(r -> r.getRoleNome() == Role.Values.ESTUDANTE);
//        model.addAttribute("isAdmin", isAdmin);
//        model.addAttribute("isCoordenador", isCoordenador);
//        model.addAttribute("isProfessor", isProfessor);
//        model.addAttribute("isEstudante", isEstudante);
//    }
//
//    // ===== DASHBOARD =====
//    @GetMapping("/dashboard")
//    public String dashboard(Authentication authentication, Model model) {
//        if (authentication == null || !authentication.isAuthenticated()) {
//            return "redirect:/login";
//        }
//
//        try {
//            UsuarioReferenciaDTO usuario = usuarioService.buscarMeuPerfil(authentication);
//            addRoleFlags(authentication, model);
//            Set<TurmaReferenciaDTO> minhasTurmas = usuarioService.minhasTurmas(authentication);
//            List<AmbienteReferenciaDTO> ambientes = ambienteService.buscar();
//
//            model.addAttribute("usuario", usuario);
//                model.addAttribute("isAdmin", isAdmin);
//            model.addAttribute("minhasTurmas", minhasTurmas);
//            model.addAttribute("ambientes", ambientes);
//
//            return "dashboard";
//        } catch (Exception e) {
//            model.addAttribute("erro", "Erro ao carregar dashboard: " + e.getMessage());
//            return "erro";
//        }
//    }
//
//    // ===== AMBIENTES =====
//    @GetMapping("/ambientes")
//    public String listarAmbientes(Authentication authentication, Model model) {
//        if (authentication == null || !authentication.isAuthenticated()) {
//            return "redirect:/login";
//        }
//
//        try {
//            List<AmbienteReferenciaDTO> ambientes = ambienteService.buscar();
//            UsuarioReferenciaDTO usuario = usuarioService.buscarMeuPerfil(authentication);
//            addRoleFlags(authentication, model);
//
//            model.addAttribute("ambientes", ambientes);
//            model.addAttribute("usuario", usuario);
//                model.addAttribute("isAdmin", isAdmin);
//
//            return "ambientes/lista";
//        } catch (Exception e) {
//            model.addAttribute("erro", "Erro ao listar ambientes: " + e.getMessage());
//            return "erro";
//        }
//    }
//
//        @GetMapping("/ambientes/novo")
//        public String novoAmbiente(Authentication authentication, Model model) {
//            if (authentication == null || !authentication.isAuthenticated()) return "redirect:/login";
//            try {
//                UsuarioReferenciaDTO usuario = usuarioService.buscarMeuPerfil(authentication);
//                boolean isAdmin = usuario.getRoles().stream().anyMatch(r -> r.getRoleNome() == Role.Values.ADMIN);
//                boolean isCoordenador = usuario.getRoles().stream().anyMatch(r -> r.getRoleNome() == Role.Values.COORDENADOR);
//                if (!(isAdmin || isCoordenador)) return "redirect:/ambientes";
//                model.addAttribute("usuario", usuario);
//                model.addAttribute("isAdmin", isAdmin);
//                model.addAttribute("isCoordenador", isCoordenador);
//                return "ambientes/create";
//            } catch (Exception e) {
//                model.addAttribute("erro", "Erro ao carregar página: " + e.getMessage());
//                return "erro";
//            }
//        }
//
//        @GetMapping("/ambientes/{id}/editar")
//        public String editarAmbiente(@PathVariable Long id, Authentication authentication, Model model) {
//            if (authentication == null || !authentication.isAuthenticated()) return "redirect:/login";
//            try {
//                UsuarioReferenciaDTO usuario = usuarioService.buscarMeuPerfil(authentication);
//                boolean isAdmin = usuario.getRoles().stream().anyMatch(r -> r.getRoleNome() == Role.Values.ADMIN);
//                boolean isCoordenador = usuario.getRoles().stream().anyMatch(r -> r.getRoleNome() == Role.Values.COORDENADOR);
//                if (!(isAdmin || isCoordenador)) return "redirect:/ambientes/" + id;
//                AmbienteReferenciaDTO ambiente = ambienteService.buscar(id);
//                model.addAttribute("ambiente", ambiente);
//                model.addAttribute("usuario", usuario);
//                model.addAttribute("isAdmin", isAdmin);
//                model.addAttribute("isCoordenador", isCoordenador);
//                return "ambientes/edit";
//            } catch (Exception e) {
//                model.addAttribute("erro", "Erro: " + e.getMessage());
//                return "erro";
//            }
//        }
//
//    @GetMapping("/ambientes/{id}")
//    public String verAmbiente(@PathVariable Long id, Authentication authentication, Model model) {
//        if (authentication == null || !authentication.isAuthenticated()) {
//            return "redirect:/login";
//        }
//
//        try {
//            AmbienteReferenciaDTO ambiente = ambienteService.buscar(id);
//            UsuarioReferenciaDTO usuario = usuarioService.buscarMeuPerfil(authentication);
//            addRoleFlags(authentication, model);
//
//            model.addAttribute("ambiente", ambiente);
//            model.addAttribute("usuario", usuario);
//                model.addAttribute("isAdmin", isAdmin);
//
//            return "ambientes/detalhe";
//        } catch (Exception e) {
//            model.addAttribute("erro", "Ambiente não encontrado");
//            return "erro";
//        }
//    }
//
//    // ===== TURMAS =====
//    @GetMapping("/turmas")
//    public String listarTurmas(Authentication authentication, Model model) {
//        if (authentication == null || !authentication.isAuthenticated()) {
//            return "redirect:/login";
//        }
//
//        try {
//            List<TurmaReferenciaDTO> turmas = turmaService.buscar();
//            UsuarioReferenciaDTO usuario = usuarioService.buscarMeuPerfil(authentication);
//            addRoleFlags(authentication, model);
//            model.addAttribute("turmas", turmas);
//            model.addAttribute("usuario", usuario);
//            return "turmas/lista";
//        } catch (Exception e) {
//            model.addAttribute("erro", "Erro ao listar turmas: " + e.getMessage());
//            return "erro";
//        }
//    }
//
//    @GetMapping("/turmas/novo")
//    public String novoTurma(Authentication authentication, Model model) {
//        if (authentication == null || !authentication.isAuthenticated()) return "redirect:/login";
//        try {
//            addRoleFlags(authentication, model);
//            // only admin or professor can create
//            if (!((Boolean) model.getAttribute("isAdmin") || (Boolean) model.getAttribute("isProfessor"))) return "redirect:/turmas";
//            UsuarioReferenciaDTO usuario = usuarioService.buscarMeuPerfil(authentication);
//            model.addAttribute("usuario", usuario);
//            return "turmas/create";
//        } catch (Exception e) {
//            model.addAttribute("erro", "Erro ao carregar página: " + e.getMessage());
//            return "erro";
//        }
//    }
//
//    @GetMapping("/turmas/{id}/editar")
//    public String editarTurma(@PathVariable Long id, Authentication authentication, Model model) {
//        if (authentication == null || !authentication.isAuthenticated()) return "redirect:/login";
//        try {
//            addRoleFlags(authentication, model);
//            if (!((Boolean) model.getAttribute("isAdmin") || (Boolean) model.getAttribute("isProfessor"))) return "redirect:/turmas/" + id;
//            UsuarioReferenciaDTO usuario = usuarioService.buscarMeuPerfil(authentication);
//            TurmaReferenciaDTO turma = turmaService.buscar(id);
//            model.addAttribute("usuario", usuario);
//            model.addAttribute("turma", turma);
//            return "turmas/edit";
//        } catch (Exception e) {
//            model.addAttribute("erro", e.getMessage());
//            return "erro";
//        }
//    }
//
//    @GetMapping("/turmas/{id}")
//    public String verTurmaDetalhe(@PathVariable Long id, Authentication authentication, Model model) {
//        if (authentication == null || !authentication.isAuthenticated()) return "redirect:/login";
//        try {
//            addRoleFlags(authentication, model);
//            TurmaReferenciaDTO turma = turmaService.buscar(id);
//            UsuarioReferenciaDTO usuario = usuarioService.buscarMeuPerfil(authentication);
//            model.addAttribute("turma", turma);
//            model.addAttribute("usuario", usuario);
//            return "turmas/detalhe";
//        } catch (Exception e) {
//            model.addAttribute("erro", e.getMessage());
//            return "erro";
//        }
//    }
//
//    @GetMapping("/usuarios")
//    public String listarUsuarios(Authentication authentication, Model model) {
//        if (authentication == null || !authentication.isAuthenticated()) return "redirect:/login";
//        try {
//            UsuarioReferenciaDTO usuario = usuarioService.buscarMeuPerfil(authentication);
//            addRoleFlags(authentication, model);
//            model.addAttribute("usuario", usuario);
//            model.addAttribute("usuarios", usuarioService.buscarTodos(authentication));
//            return "usuarios/lista";
//        } catch (Exception e) {
//            model.addAttribute("erro", "Erro ao listar usuários: " + e.getMessage());
//            return "erro";
//        }
//    }
//
//    @GetMapping("/usuarios/novo")
//    public String novoUsuario(Authentication authentication, Model model) {
//        if (authentication == null || !authentication.isAuthenticated()) return "redirect:/login";
//        try {
//            UsuarioReferenciaDTO usuario = usuarioService.buscarMeuPerfil(authentication);
//            addRoleFlags(authentication, model);
//            if (!((Boolean) model.getAttribute("isAdmin"))) return "redirect:/usuarios";
//            model.addAttribute("roles", roleRepository.findAll());
//            model.addAttribute("usuario", usuario);
//            return "usuarios/create";
//        } catch (Exception e) {
//            model.addAttribute("erro", e.getMessage());
//            return "erro";
//        }
//    }
//
//    @GetMapping("/usuarios/{id}")
//    public String verUsuario(@PathVariable Long id, Authentication authentication, Model model) {
//        if (authentication == null || !authentication.isAuthenticated()) return "redirect:/login";
//        try {
//            UsuarioReferenciaDTO usuario = usuarioService.buscarMeuPerfil(authentication);
//            model.addAttribute("usuario", usuario);
//            model.addAttribute("isAdmin", usuario.getRoles().stream().anyMatch(r -> r.getRoleNome() == Role.Values.ADMIN));
//            model.addAttribute("usuarioItem", usuarioService.buscarPorId(id));
//            return "usuarios/detalhe";
//        } catch (Exception e) {
//            model.addAttribute("erro", e.getMessage());
//            return "erro";
//        }
//    }
//
//    @GetMapping("/turmas/{id}")
//    public String verTurma(@PathVariable Long id, Authentication authentication, Model model) {
//        if (authentication == null || !authentication.isAuthenticated()) {
//            return "redirect:/login";
//        }
//
//        try {
//            TurmaReferenciaDTO turma = turmaService.buscar(id);
//            UsuarioReferenciaDTO usuario = usuarioService.buscarMeuPerfil(authentication);
//
//            model.addAttribute("turma", turma);
//            model.addAttribute("usuario", usuario);
//
//            return "turmas/detalhe";
//        } catch (Exception e) {
//            model.addAttribute("erro", "Turma não encontrada");
//            return "erro";
//        }
//    }
//
//    // ===== MINHAS TURMAS =====
//    @GetMapping("/minhas-turmas")
//    public String minhasTurmas(Authentication authentication, Model model) {
//        if (authentication == null || !authentication.isAuthenticated()) {
//            return "redirect:/login";
//        }
//
//        try {
//            Set<TurmaReferenciaDTO> turmas = usuarioService.minhasTurmas(authentication);
//            UsuarioReferenciaDTO usuario = usuarioService.buscarMeuPerfil(authentication);
//
//            model.addAttribute("turmas", turmas);
//            model.addAttribute("usuario", usuario);
//
//            return "turmas/minhas-turmas";
//        } catch (Exception e) {
//            model.addAttribute("erro", "Erro ao listar suas turmas: " + e.getMessage());
//            return "erro";
//        }
//    }
//
//    // ===== PERFIL =====
//    @GetMapping("/perfil")
//    public String meuPerfil(Authentication authentication, Model model) {
//        if (authentication == null || !authentication.isAuthenticated()) {
//            return "redirect:/login";
//        }
//
//        try {
//            UsuarioReferenciaDTO usuario = usuarioService.buscarMeuPerfil(authentication);
//            model.addAttribute("usuario", usuario);
//            return "usuario/perfil";
//        } catch (Exception e) {
//            model.addAttribute("erro", "Erro ao carregar perfil: " + e.getMessage());
//            return "erro";
//        }
//    }
//
//    // ===== NOTIFICACOES =====
//    @GetMapping("/notificacoes")
//    public String notificacoes(Authentication authentication, Model model) {
//        if (authentication == null || !authentication.isAuthenticated()) {
//            return "redirect:/login";
//        }
//
//        try {
//            UsuarioReferenciaDTO usuario = usuarioService.buscarMeuPerfil(authentication);
//            model.addAttribute("usuario", usuario);
//            model.addAttribute("notificacoes", usuario.getNotificacoes());
//            return "notificacoes";
//        } catch (Exception e) {
//            model.addAttribute("erro", "Erro ao carregar notificações: " + e.getMessage());
//            return "erro";
//        }
//    }
//
//    // ===== ERRO =====
//    @GetMapping("/erro")
//    public String erro() {
//        return "erro";
//    }
//}
//
