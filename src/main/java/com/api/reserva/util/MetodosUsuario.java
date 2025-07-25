package com.api.reserva.util;

import com.api.reserva.entity.Usuario;
import com.api.reserva.enums.UsuarioRole;

import java.util.Random;

public class MetodosUsuario {

    public static String gerarTag(Usuario usuario) {
        Random random = new Random();
        Integer nmrTag = random.nextInt(99999) + 1;

        if (usuario.getRole() == UsuarioRole.ESTUDANTE) {
            return String.format("ESE%05d", nmrTag);
        } else if (usuario.getRole() == UsuarioRole.COORDENADOR) {
            return String.format("ESC%05d", nmrTag);
        } else if (usuario.getRole() == UsuarioRole.ADMIN) {
            return String.format("ESA%05d", nmrTag);
        } else {
            throw new RuntimeException("Erro ao gerar TAG.");
        }
    }
}
