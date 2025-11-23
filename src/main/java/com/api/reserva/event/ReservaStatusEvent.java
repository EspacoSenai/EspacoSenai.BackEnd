package com.api.reserva.event;

import com.api.reserva.entity.Reserva;
import org.springframework.context.ApplicationEvent;

public class ReservaStatusEvent extends ApplicationEvent {
    private final Reserva reserva;
    private final boolean isInicio; // true para início, false para fim

    public ReservaStatusEvent(Object source, Reserva reserva, boolean isInicio) {
        super(source);
        this.reserva = reserva;
        this.isInicio = isInicio;
    }

    public Reserva getReserva() {
        return reserva;
    }

    public boolean isInicio() {
        return isInicio;
    }
}
