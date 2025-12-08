package com.api.reserva.service;

import com.api.reserva.dto.PreCadastroDTO;
import com.api.reserva.entity.PreCadastro;
import com.api.reserva.entity.Role;
import com.api.reserva.entity.Usuario;
import com.api.reserva.exception.SemResultadosException;
import com.api.reserva.exception.UsuarioDuplicadoException;
import com.api.reserva.repository.PreCadastroRepository;
import com.api.reserva.repository.RoleRepository;
import com.api.reserva.repository.UsuarioRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PreCadastroService {

    private final PreCadastroRepository preCadastroRepository;
    private final RoleRepository roleRepository;
    private final UsuarioRepository usuarioRepository;    private final EmailService emailService;

    public PreCadastroService(PreCadastroRepository preCadastroRepository, RoleRepository roleRepository, UsuarioRepository usuarioRepository, EmailService emailService) {
        this.preCadastroRepository = preCadastroRepository;
        this.roleRepository = roleRepository;
        this.usuarioRepository = usuarioRepository;
        this.emailService = emailService;
    }

    public List<PreCadastroDTO> buscar() {
        List<PreCadastroDTO> preCadastros = preCadastroRepository.findAll()
                .stream()
                .map(PreCadastroDTO::new)
                .toList();
        if (preCadastros.isEmpty()) {
            throw new SemResultadosException();
        }

        return preCadastros;
    }


    public boolean verificarElegibilidade(String email) {
        PreCadastro preCadastro = preCadastroRepository.findByEmail(email);

        if (preCadastro != null) {
            return true;
        }

        return false;
    }

//    public boolean verificarElegibilidade(String identificador, String identificador) {
//        PreCadastro preCadastro = preCadastroRepository.findByIdentificador(identificador)
//
//        if(preCadastro != null) {
//            return true;
//        }
//
//        return false;
//    }

    public void salvar(PreCadastroDTO preCadastroDTO) {
        PreCadastro preCadastrado = preCadastroRepository.findByEmail(preCadastroDTO.getEmail());
        Usuario usuario = usuarioRepository.findByEmail(preCadastroDTO.getEmail());


        if (preCadastrado != null || usuario != null) {
            throw new UsuarioDuplicadoException("Estudante já cadastrado ou pré-cadastrado com este email.");
        }

        PreCadastro preCadastro = new PreCadastro();
        preCadastro.setNome(preCadastroDTO.getNome());
        preCadastro.setEmail(preCadastroDTO.getEmail());
        preCadastro.setSeCadastrou(false);

        emailService.enviarEmail(preCadastroDTO.getEmail(),
                "Espaço Senai Você foi pré-cadastrado",
                "Você está apto para criar uma conta. Acesse a página principal de cadastro.");

        preCadastroRepository.save(preCadastro);
    }

    @Transactional
    public List<PreCadastro> salvarEstudantesPlanilha(MultipartFile planilha) {
        List<PreCadastro> preCadastros = new ArrayList<>();
        Set<String> emailsPreCadastrados = new HashSet<>();
        try (InputStream is = planilha.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Role roleEstudante = roleRepository.findByRoleNome(Role.Values.ESTUDANTE).orElseThrow(() ->
                    new SemResultadosException(String.format("Role %s", Role.Values.ESTUDANTE.name())));
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                // Pula linhas 0 (título) e 1 (cabeçalho)
                if (row.getRowNum() < 2) continue;

                // Verifica se as células não são nulas
                if (row.getCell(0) == null || row.getCell(1) == null) continue;

                String nome = row.getCell(0).getStringCellValue().trim();
                String email = row.getCell(1).getStringCellValue().trim().toLowerCase();

                // Ignora linhas com nome ou email vazios
                if (nome.isBlank() || email.isBlank()) continue;

                PreCadastro preCadastro = new PreCadastro();
                preCadastro.setNome(nome);
                preCadastro.setEmail(email);
                preCadastros.add(preCadastro);
                emailsPreCadastrados.add(email);
            }
            preCadastroRepository.saveAll(preCadastros);
//            emailService.enviarMultiplosEmail(
//                    emailsPreCadastrados,
//                    "Espaço Senai Você foi pré-cadastrado",
//                    "Você está apto para criar uma conta. Acesse a página principal de cadastro."
//            );
        } catch (IOException e) {
            throw new RuntimeException("Erro ao processar a planilha: " + e.getMessage());
        }
        return preCadastros;
    }

//    public boolean estudantePreCadastrado(String email, String telefone) {
//        boolean preCadastrado = preCadastroRepository.existsByEmailOrTelefone(email, telefone);
//        Usuario usuario = usuarioRepository.findByEmailOrTelefone(email, telefone);
//
//        if (preCadastrado && usuario != null && usuario.getStatus() == UsuarioStatus.INATIVO) {
//            return true;
//        }
//
//        return false;
//    }

    @Transactional
    public void atualizar(Long id, PreCadastroDTO preCadastroDTO) {
        PreCadastro preCadastro = preCadastroRepository.findById(id).orElseThrow(() ->
                new SemResultadosException("Pré-cadastro", "atualização"));
        preCadastro.setNome(preCadastroDTO.getNome());
        preCadastro.setEmail(preCadastroDTO.getEmail());
        preCadastroRepository.save(preCadastro);

    }

    public void deletar(Long id) {
        PreCadastro preCadastro = preCadastroRepository.findById(id).orElseThrow(() ->
                new SemResultadosException("Pré-cadastro", "exclusão"));
        preCadastroRepository.delete(preCadastro);
    }

    public boolean isElegivel(String email) {
        if (preCadastroRepository.existsByEmail(email)) {
            return true;
        } else {
            return false;
        }
    }
}
