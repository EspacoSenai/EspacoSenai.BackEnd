package com.api.reserva.config;

import com.api.reserva.entity.Role;
import com.api.reserva.entity.Usuario;
import com.api.reserva.enums.UsuarioStatus;
import com.api.reserva.repository.RoleRepository;
import com.api.reserva.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Configuration
public class AdminUserConfig implements CommandLineRunner {
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        criarUsuarioAdmin();
        criarUsuarioImpressora();
    }

    /**
     * Cria usuário ADMIN na inicialização se não existir
     */
    private void criarUsuarioAdmin() {
        Optional<Role> roleAdmin = roleRepository.findByRoleNome(Role.Values.ADMIN);

        roleAdmin.ifPresentOrElse(role -> {
            if (usuarioRepository.findByEmail("admin") == null) {
                Usuario admin = new Usuario();
                admin.setNome("admin");
                admin.setEmail("admin");
                admin.setSenha(passwordEncoder.encode("1234"));
                admin.getRoles().add(roleAdmin.get());
                admin.setStatus(UsuarioStatus.ATIVO);
                usuarioRepository.save(admin);
                System.out.println("✓ Usuário ADMIN criado com sucesso");
            } else {
                System.out.println("ℹ Usuário ADMIN já existe");
            }
        }, () -> {
            System.out.println("✗ Role ADMIN não existe, admin não criado. Verificar banco de dados");
        });
    }

    /**
     * Cria usuário IMPRESSORA na inicialização se não existir
     * Um único usuário para todas as impressoras
     * Token JWT com duração de 1 semana (604800 segundos)
     */
    private void criarUsuarioImpressora() {
        Optional<Role> roleImpressora = roleRepository.findByRoleNome(Role.Values.IMPRESSORA);

        roleImpressora.ifPresentOrElse(role -> {
            if (usuarioRepository.findByEmail("impressora") == null) {
                Usuario impressora = new Usuario();
                impressora.setNome("Usuario Impressora");
                impressora.setEmail("impressora@sistema.com");
                // Senha: "impressora123" - codificada com BCrypt
                impressora.setSenha(passwordEncoder.encode("impressora123"));
                impressora.getRoles().add(roleImpressora.get());
                impressora.setStatus(UsuarioStatus.ATIVO);
                usuarioRepository.save(impressora);
                System.out.println("✓ Usuário IMPRESSORA criado com sucesso");
                System.out.println("  - Identificador: impressora");
                System.out.println("  - Senha: impressora123");
                System.out.println("  - Token JWT: 1 semana (604800 segundos)");
                System.out.println("  - Login: POST /auth/signin");
            } else {
                System.out.println("ℹ Usuário IMPRESSORA já existe");
            }
        }, () -> {
            System.out.println("✗ Role IMPRESSORA não existe, impressora não criada. Verificar banco de dados");
        });
    }
}
