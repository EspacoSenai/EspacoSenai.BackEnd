package com.api.reserva.service;

import com.api.reserva.dto.UsuarioDTO;
import com.api.reserva.entity.Usuario;
import com.api.reserva.enums.UsuarioRole;
import com.api.reserva.enums.UsuarioStatus;
import com.api.reserva.exception.SemResultadosException;
import com.api.reserva.exception.UsuarioDuplicadoException;
import com.api.reserva.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    UsuarioRepository repository;

    //Listar uma pessoa
    public UsuarioDTO listar(Long id) {
        return new UsuarioDTO(repository.findById(id)
                .orElseThrow(SemResultadosException::new));
    }

    //Listar todas as pessoas
    public List<UsuarioDTO> listar() {
        List<Usuario> usuarios = repository.findAll();
        return usuarios.stream().map(UsuarioDTO::new).toList();
    }

    public void salvar(UsuarioDTO usuarioDTO) {
        //validação de duplicidade
        if (repository.existsByEmailOrTelefone(usuarioDTO.getEmail(), usuarioDTO.getTelefone())) {
            throw new UsuarioDuplicadoException();
        }
        Usuario usuario = new Usuario(usuarioDTO);
        usuario.setStatus(UsuarioStatus.ATIVO);
        usuario.setRole(UsuarioRole.ESTUDANTE);

        repository.save(usuario);
    }

    public void atualizar(UsuarioDTO usuarioDTO, Long id) {
        Usuario usuario = repository.findById(id).orElseThrow(() -> new SemResultadosException("atualização."));

        if (repository.existsByEmailOrTelefone(usuarioDTO.getEmail(), usuarioDTO.getTelefone())) {
            throw new UsuarioDuplicadoException();
        }

        if (!usuarioDTO.getNome().equals(usuario.getNome())) {
            usuario.setNome(usuarioDTO.getNome());
        }

        if (!usuarioDTO.getEmail().equals(usuario.getEmail())) {
            usuario.setEmail(usuarioDTO.getEmail());
        }

        if (!usuarioDTO.getSenha().equals(usuario.getSenha())) {
            usuario.setSenha(usuarioDTO.getSenha());
        }

       String telefoneLimpo = usuarioDTO.getTelefone().replace(" ", "").replace("-", "");
       if (telefoneLimpo.length() == 11 && !telefoneLimpo.equals(usuario.getTelefone())) {
           usuario.setTelefone(telefoneLimpo);
       }

        if (!usuarioDTO.getStatus().equals(usuario.getStatus())) {
            usuario.setStatus(usuarioDTO.getStatus());
        }

        if (!usuarioDTO.getRole().equals(usuario.getRole())) {
            usuario.setRole(usuarioDTO.getRole());
        }
        repository.save(usuario);
    }

    //Deletar pessoa
    public void excluir(Long id) {
        if (!repository.existsById(id)) {
            throw new SemResultadosException();
        }
        repository.deleteById(id);
    }
}