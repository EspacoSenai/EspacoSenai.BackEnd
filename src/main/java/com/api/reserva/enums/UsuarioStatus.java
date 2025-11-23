package com.api.reserva.enums;

public enum UsuarioStatus {
    // Usuario está ativo e pode acessar normalmente o sistema.
    ATIVO(1L),
    // Usuario teve o acesso bloqueado indefinidamente.
    BLOQUEADO(3L),
    // Usuario teve a matrícula expirada e não pode acessar o sistema.
    MATRICULA_EXPIRADA(5L);
    /*
     * Fluxo de status:
     * - Pré-cadastro (planilha ou externo): INATIVO → (confirma e-mail)
     *   - Se for da planilha: INATIVO → ATIVO
     *   - Se for externo: INATIVO → PENDENTE → (aprovado) → ATIVO
     * - ATIVO pode virar BLOQUEADO, EXCLUIDO ou MATRICULA_EXPIRADA conforme regras.
     */

    private final Long id;

    UsuarioStatus(Long codigo) {
        this.id = codigo;
    }

    public Long getId() {
        return id;
    }

    // Retorna o UsuarioStatus correspondente ao id fornecido.
    // Lança IllegalArgumentException se nenhum status corresponder ao id.
    public static UsuarioStatus fromId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("id não pode ser nulo");
        }
        for (UsuarioStatus s : values()) {
            if (s.getId().equals(id)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Nenhum UsuarioStatus para id: " + id);
    }

  }