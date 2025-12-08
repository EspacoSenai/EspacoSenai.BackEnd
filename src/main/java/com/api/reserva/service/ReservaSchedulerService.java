package com.api.reserva.service;

import com.api.reserva.entity.Ambiente;
import com.api.reserva.entity.Reserva;
import com.api.reserva.enums.StatusReserva;
import com.api.reserva.event.ReservaStatusEvent;
import com.api.reserva.repository.AmbienteRepository;
import com.api.reserva.repository.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

@Service
public class ReservaSchedulerService {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private AmbienteRepository ambienteRepository;

    @Scheduled(fixedRate = 60000) // Executa a cada 1 minuto
    public void verificarReservasAtivas() {
        LocalDate hoje = LocalDate.now();
        LocalTime agora = LocalTime.now();

        Set<Reserva> reservas = reservaRepository.findAllByData(hoje);

        for (Reserva reserva : reservas) {
            LocalTime inicio = reserva.getHoraInicio();
            LocalTime fim = reserva.getHoraFim();

            // Iniciar: se aprovada e hora atual >= inicio e < fim
            if (reserva.getStatusReserva() == StatusReserva.APROVADA
                    && !agora.isBefore(inicio)
                    && agora.isBefore(fim)) {

                reserva.setStatusReserva(StatusReserva.ACONTECENDO);
                Ambiente ambiente = reserva.getCatalogo().getAmbiente();
                ambiente.setEmUso(true);
                reservaRepository.save(reserva);
                ambienteRepository.save(ambiente);

                // Publica evento para notificar listeners
                eventPublisher.publishEvent(new ReservaStatusEvent(this, reserva, true));
            }
            // Finalizar: se acontecendo e hora atual >= fim
            else if (reserva.getStatusReserva() == StatusReserva.ACONTECENDO
                    && !agora.isBefore(fim)) {

                reserva.setStatusReserva(StatusReserva.CONCLUIDA);
                Ambiente ambiente = reserva.getCatalogo().getAmbiente();
                ambiente.setEmUso(false);
                reservaRepository.save(reserva);
                ambienteRepository.save(ambiente);

                // Publica evento para notificar listeners
                eventPublisher.publishEvent(new ReservaStatusEvent(this, reserva, false));
            }
        }
    }

    @Scheduled(fixedRate = 60000) // Executa a cada 1 minuto
    public void cancelarReservasExpiradas() {
        LocalDate hoje = LocalDate.now();
        LocalTime agora = LocalTime.now();

        Set<Reserva> pendentes = reservaRepository.findAllByStatusReserva(StatusReserva.PENDENTE);

        for (Reserva reserva : pendentes) {
            boolean dataAnterior = reserva.getData().isBefore(hoje);
            boolean hojeEPassouInicio = reserva.getData().isEqual(hoje) && reserva.getHoraInicio().isBefore(agora);

            if (dataAnterior || hojeEPassouInicio) {
                reserva.setStatusReserva(StatusReserva.CANCELADA);
                reservaRepository.save(reserva);
            }
        }
    }
}