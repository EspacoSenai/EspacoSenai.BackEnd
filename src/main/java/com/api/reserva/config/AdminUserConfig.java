package com.api.reserva.config;

import com.api.reserva.entity.Role;
import com.api.reserva.entity.Usuario;
import com.api.reserva.enums.UsuarioStatus;
import com.api.reserva.repository.RoleRepository;
import com.api.reserva.repository.UsuarioRepository;
import com.api.reserva.util.CodigoUtil;
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
        Optional<Role> roleAdmin = roleRepository.findByRoleNome(Role.Values.ADMIN);

        roleAdmin.ifPresentOrElse( role -> {
            if (usuarioRepository.findByEmail("admin") == null) {
                Usuario admin = new Usuario();
                admin.setNome("admin");
                admin.setEmail("admin");
                admin.setSenha(passwordEncoder.encode("1234"));
                admin.getRoles().add(roleAdmin.get());
                admin.setTag(CodigoUtil.gerarCodigo(5));
                admin.setStatus(UsuarioStatus.ATIVO);
                usuarioRepository.save(admin);
                System.out.println("admin criado");
            } else {
                System.out.println("admin já existe");
            }
        }, () -> {
            System.out.println("role não existe, admin não criado. verificar db");
        });
    }
}
