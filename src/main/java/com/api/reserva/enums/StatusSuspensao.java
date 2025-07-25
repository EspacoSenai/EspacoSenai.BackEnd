package com.api.reserva.enums;

public enum StatusSuspensao {
    /**
     * Suspensão vigente, Usuario está impedido de acessar o sistema.
     */
    ATIVA,
    /**
     * Suspensão já terminou, mantida para histórico.
     */
    EXPIRADA,
    /**
     * Suspensão foi revogada antes do término.
     */
    CANCELADA,
    /**
     * Suspensão cadastrada, mas ainda não entrou em vigor (ex: data futura).
     */
    AGUARDANDO
}
