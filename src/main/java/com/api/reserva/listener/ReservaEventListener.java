package com.api.reserva.listener;

import com.api.reserva.entity.Usuario;
import com.api.reserva.enums.NotificacaoTipo;
import com.api.reserva.event.ReservaStatusEvent;
import com.api.reserva.service.NotificacaoService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ReservaEventListener {

    private final NotificacaoService notificacaoService;

    public ReservaEventListener(NotificacaoService notificacaoService) {
        this.notificacaoService = notificacaoService;
    }

    @EventListener
    public void handleReservaStatus(ReservaStatusEvent event) {
        Usuario host = event.getReserva().getHost();

        if (event.isInicio()) {
            // Notificar que a reserva começou
            notificacaoService.novaNotificacao(
                    host,
                    NotificacaoTipo.RESERVA_COMECOU,
                    "Sua reserva começou",
                    "A sua reserva com ID " + event.getReserva().getId() +
                    " começou agora e terminará às " + event.getReserva().getHoraFim() + "."
            );
            System.out.println("Notificação enviada: Reserva " + event.getReserva().getId() + " iniciou.");
        } else {
            // Notificar que a reserva terminou
            notificacaoService.novaNotificacao(
                    host,
                    NotificacaoTipo.RESERVA_TERMINOU,
                    "Sua reserva terminou",
                    "A sua reserva com ID " + event.getReserva().getId() + " foi concluída."
            );
            System.out.println("Notificação enviada: Reserva " + event.getReserva().getId() + " terminou.");
        }
    }
}
