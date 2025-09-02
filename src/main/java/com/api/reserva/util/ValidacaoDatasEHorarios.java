package com.api.reserva.util;

import com.api.reserva.exception.DataInvalidaException;
import com.api.reserva.exception.HorarioInvalidoException;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class ValidacaoDatasEHorarios {

    // Converte um objeto LocalDate para uma String no formato ISO (yyyy-MM-dd), padrão do Java
    public static String converterDataParaString(LocalDate data) {
        // Usa o formatador ISO_LOCAL_DATE, padrão do Java (yyyy-MM-dd)
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        // Formata a data recebida para String usando o formatador padrão
        return formatter.format(data);
    }

    // Converte uma String no formato ISO (yyyy-MM-dd) para um objeto LocalDate
    public static LocalDate converterStringParaData(String data) {
        // Usa o formatador ISO_LOCAL_DATE, padrão do Java (yyyy-MM-dd)
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        // Faz o parse da String para LocalDate usando o formatador padrão
        return LocalDate.parse(data, formatter);
    }

    public static boolean validarDatas(LocalDate dataInicio, LocalDate dataTermino) {
        if (dataInicio == null || dataTermino == null) {
            throw new DataInvalidaException("As datas não podem ser nulas.");
        } else if (Objects.equals(dataInicio, dataTermino)) {
            throw new DataInvalidaException("As datas não podem ser idênticas.");
        } else if (dataTermino.isBefore(dataInicio)) {
            throw new DataInvalidaException("A data de término não pode ser inferior a data de início.");
        }
//        else if (dataTermino.isBefore(LocalDate.now())) {
//            throw new DataInvalidaException("A data de término precisa ser futura");
//        }

        return true;
    }

    public static boolean validarHorarios(LocalTime horaInicio, LocalTime horaFim) {
        if (horaInicio == null || horaFim == null) {
            throw new HorarioInvalidoException("Os horários não podem ser nulos.");
        } else if (Objects.equals(horaInicio, horaFim)) {
            throw new HorarioInvalidoException("Os horários não podem ser idênticos.");
        } else if (horaFim.isBefore(horaInicio)) {
            throw new HorarioInvalidoException();
        }

        Duration duracao = Duration.between(horaInicio, horaFim);

        if(duracao.toMinutes() < 15) {
            throw new HorarioInvalidoException("Os catálogos devem durar no mínimo 15 minutos.");
        }

        return true;
    }
}
