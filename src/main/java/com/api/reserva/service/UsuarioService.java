package com.api.reserva.service;

import com.api.reserva.dto.UsuarioDTO;
import com.api.reserva.entity.Usuario;
import com.api.reserva.enums.UsuarioRole;
import com.api.reserva.enums.UsuarioStatus;
import com.api.reserva.exception.SemResultadosException;
import com.api.reserva.exception.UsuarioDuplicadoException;
import com.api.reserva.repository.UsuarioRepository;
import com.api.reserva.util.MetodosUsuario;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    UsuarioRepository usuarioRepository;

    /**
     * Cria um novo usuário no sistema.
     */

    public UsuarioDTO listar(Long id) {
        return new UsuarioDTO(usuarioRepository.findById(id)
                .orElseThrow(SemResultadosException::new));
    }

    /**
     * Lista todos os usuários do sistema.
     */
    public List<UsuarioDTO> listar() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        return usuarios.stream().map(UsuarioDTO::new).toList();
    }

    /**
     * Salva um novo usuário no sistema.
     */
    public void salvarEstudante(UsuarioDTO usuarioDTO) {

        if (usuarioRepository.existsByEmailOrTelefone(usuarioDTO.getEmail(), usuarioDTO.getTelefone())) {
            throw new UsuarioDuplicadoException();
        }


        Usuario usuario = new Usuario(usuarioDTO);
        usuario.setStatus(UsuarioStatus.INATIVO);
        usuario.setRole(UsuarioRole.ESTUDANTE);
        usuario.setTag(MetodosUsuario.gerarTag(usuario));

        usuarioRepository.save(usuario);
    }

    @Transactional
    public List<Usuario> salvarEstudantesPlanilha(MultipartFile planilha) {
        List<Usuario> estudantes = new ArrayList<>();
        try (InputStream is = planilha.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
                for (Row row : sheet) {
                    if(row.getRowNum() == 0) continue;
                    String nome = row.getCell(0).getStringCellValue();
                    String email = row.getCell(1).getStringCellValue();
                    String telefone = String.format("%.0f", row.getCell(4).getNumericCellValue());
                    Usuario estudante = new Usuario();
                    estudante.setNome(nome);
                    estudante.setEmail(email);
                    estudante.setSenha(null);
                    estudante.setTelefone(telefone);
                    estudante.setRole(UsuarioRole.ESTUDANTE);
                    estudante.setStatus(UsuarioStatus.INATIVO);
                    estudante.setTag(MetodosUsuario.gerarTag(estudante));
                    while (estudantes.stream().anyMatch(e -> e.getTag().equals(estudante.getTag()))) {
                        estudante.setTag(MetodosUsuario.gerarTag(estudante));

                    }

                    estudantes.add(estudante);
                    System.out.print(estudante.getTag());
                    System.out.print(estudante.getNome());


                }
                usuarioRepository.saveAll(estudantes);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao processar a planilha: " + e.getMessage());
        }
        return estudantes;
    }

    public void salvarInterno(UsuarioDTO usuarioDTO) {

        if (usuarioRepository.existsByEmailOrTelefone(usuarioDTO.getEmail(), usuarioDTO.getTelefone())) {
            throw new UsuarioDuplicadoException();
        }
        Usuario usuario = new Usuario(usuarioDTO);
        usuario.setStatus(UsuarioStatus.INATIVO);

        usuarioRepository.save(usuario);
    }


    /**
     * Atualiza os dados de um usuário existente.
     */
    public void atualizar(Long id, UsuarioDTO usuarioDTO) {

        if (usuarioRepository.existsByEmailOrTelefoneAndIdNot(usuarioDTO.getEmail(), usuarioDTO.getTelefone(), id)) {
            throw new UsuarioDuplicadoException();
        }

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(SemResultadosException::new);

        usuario.setNome(usuarioDTO.getNome());
        usuario.setEmail(usuarioDTO.getEmail());
        usuario.setSenha(usuarioDTO.getSenha());
        usuario.setTelefone(usuarioDTO.getTelefone());

        usuarioRepository.save(usuario);
    }

    /**
     * Exclui um usuário do sistema.
     */

    public void excluir(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(SemResultadosException::new);
        usuarioRepository.delete(usuario);
    }

}