package com.api.reserva.enums;

public enum UsuarioStatus {
    // Usuario está ativo e pode acessar normalmente o sistema.
    ATIVO,
    // Usuario foi pré-cadastrado (planilha ou externo) e precisa confirmar o código enviado por e-mail.
    INATIVO,
    // Cadastro externo: usuário confirmou o e-mail, mas aguarda aprovação manual do administrador/coordenador.
    PENDENTE,
    // Usuario teve o acesso bloqueado, geralmente por violação de regras.
    BLOQUEADO,
    // Usuario foi removido do sistema e não tem mais acesso.
    EXCLUIDO,
    // Usuario teve a matrícula expirada e não pode acessar o sistema.
    MATRICULA_EXPIRADA
    /*
     * Fluxo de status:
     * - Pré-cadastro (planilha ou externo): INATIVO → (confirma e-mail)
     *   - Se for da planilha: INATIVO → ATIVO
     *   - Se for externo: INATIVO → PENDENTE → (aprovado) → ATIVO
     * - ATIVO pode virar BLOQUEADO, EXCLUIDO ou MATRICULA_EXPIRADA conforme regras.
     */
}