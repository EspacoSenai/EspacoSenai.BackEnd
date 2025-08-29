package com.api.reserva.util;

import java.security.SecureRandom;

public class CodigoUtil {

    public static String gerarCodigo(int lenght) {
        SecureRandom random = new SecureRandom();
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder(lenght);

        for (int i = 0; i < lenght; i++) {
            int index = random.nextInt(caracteres.length());
            sb.append(caracteres.charAt(index));
        }
        return sb.toString();
    }

}
