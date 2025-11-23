package com.api.reserva.util;

import java.security.SecureRandom;

public class CodigoUtil {

    public static String gerarCodigo(int lenght) {
        SecureRandom random = new SecureRandom();
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder codigo = new StringBuilder(lenght);

        for (int i = 0; i < lenght; i++) {
            int index = random.nextInt(caracteres.length());
            codigo.append(caracteres.charAt(index));
        }
        return codigo.toString();
    }

    public static Integer gerarPin(Integer lenght) {
        SecureRandom random = new SecureRandom();
        String caracteres = "0123456789";
        StringBuilder sb = new StringBuilder(lenght);

        for (int i = 0; i < lenght; i++) {
            int index = random.nextInt(caracteres.length());
            sb.append(caracteres.charAt(index));
        }
        return Integer.parseInt(String.valueOf(sb));
    }

}
