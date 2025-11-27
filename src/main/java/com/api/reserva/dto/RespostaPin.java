package com.api.reserva.dto;

import com.api.reserva.entity.ReservaImpressora;

public class RespostaPin {
    private Long id;
    private boolean confirmacao;

    public RespostaPin(){}

    public RespostaPin(ReservaImpressora reserva) {
        this.id = reserva.getId();
        this.confirmacao = true;
    }

    public Long getId() {
        return id;
    }

    public boolean isConfirmacao() {
        return confirmacao;
    }
}
