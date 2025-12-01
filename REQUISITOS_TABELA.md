# Documento de Requisitos do Sistema - Formato Tabela

## Sistema de Gestão de Reservas de Ambientes Acadêmicos

**Versão:** 1.0  
**Data:** 27 de novembro de 2025  
**Instituição:** Instituto Federal  

---

## 1. Requisitos Funcionais

### RF01 - Gestão de Usuários

| ID | Requisito | Descrição | Prioridade |
|---|---|---|---|
| RF01.1 | Cadastro de Usuários | O sistema deve permitir o cadastro de novos usuários com nome completo (até 100 caracteres), e-mail institucional único (até 100 caracteres), senha criptografada e status (ATIVO, INATIVO, PENDENTE). | Alta |
| RF01.2 | Pré-Cadastro | O sistema deve oferecer fluxo de pré-cadastro onde usuários interessados podem solicitar acesso informando nome e e-mail, ficando pendente até aprovação por administrador. | Média |
| RF01.3 | Autenticação de Usuários | O sistema deve permitir login via e-mail e senha. Apenas usuários com status ATIVO podem autenticar-se. | Alta |
| RF01.4 | Perfis de Acesso (Roles) | O sistema deve implementar perfis: ADMIN (acesso total), COORDENADOR (gestão de ambientes e reservas), PROFESSOR (criação de turmas e reservas), ESTUDANTE (solicitação de reservas e matrícula em turmas). | Alta |
| RF01.5 | Gerenciamento de Perfis | O sistema deve permitir que apenas usuários ADMIN atribuam ou modifiquem perfis de outros usuários. | Alta |
| RF01.6 | Atualização de Dados Cadastrais | O sistema deve permitir que usuários atualizem nome, e-mail e senha mediante autenticação da senha atual. | Média |
| RF01.7 | Redefinição de Senha | O sistema deve oferecer redefinição de senha via código PIN enviado ao e-mail do usuário. | Média |
| RF01.8 | Listagem de Usuários | O sistema deve permitir que administradores visualizem lista completa de usuários com filtro por status e perfil. | Média |
| RF01.9 | Desativação de Usuários | O sistema deve permitir que administradores desativem usuários alterando status para INATIVO, sem excluir dados históricos. | Média |

---

### RF02 - Gestão de Ambientes

| ID | Requisito | Descrição | Prioridade |
|---|---|---|---|
| RF02.1 | Cadastro de Ambientes | O sistema deve permitir cadastro com nome único (até 100 caracteres), descrição (até 500 caracteres), disponibilidade (DISPONÍVEL/INDISPONÍVEL), tipo de aprovação (AUTOMÁTICA/MANUAL), indicador emUso e responsável (COORDENADOR). | Alta |
| RF02.2 | Atribuição de Responsável | O sistema deve permitir atribuir um COORDENADOR como responsável por um ambiente com permissões especiais. | Alta |
| RF02.3 | Controle de Disponibilidade | Ambientes DISPONÍVEIS aparecem para novas reservas. INDISPONÍVEIS não aparecem mas mantêm reservas aprovadas. | Alta |
| RF02.4 | Tipo de Aprovação | AUTOMÁTICA: reservas aprovadas automaticamente sem conflitos. MANUAL: reservas ficam PENDENTES até aprovação. | Alta |
| RF02.5 | Atualização de Ambientes | Administradores ou responsáveis podem atualizar nome, descrição, disponibilidade e tipo de aprovação. | Média |
| RF02.6 | Exclusão de Ambientes | Ao excluir: cancela todas reservas ativas (PENDENTE, APROVADA, CONFIRMADA, ACONTECENDO), envia notificações e remove catálogos associados. | Alta |
| RF02.7 | Indisponibilização Temporária | Marca ambiente e catálogos como INDISPONÍVEIS, cancela reservas PENDENTES, não afeta reservas aprovadas. | Alta |
| RF02.8 | Disponibilização de Ambientes | Torna ambiente e catálogos disponíveis novamente para novas reservas. | Média |
| RF02.9 | Consulta de Ambientes | Permite consulta com filtros por disponibilidade e nome, retornando informações detalhadas incluindo catálogos. | Alta |
| RF02.10 | Indicador de Uso em Tempo Real | Mantém campo "emUso" atualizado indicando se há reserva em andamento no momento. | Média |

---

### RF03 - Gestão de Catálogos de Horários

| ID | Requisito | Descrição | Prioridade |
|---|---|---|---|
| RF03.1 | Criação de Catálogos | Permite criar catálogos com ambiente vinculado, dia da semana (SEGUNDA a DOMINGO), hora início/fim e disponibilidade. | Alta |
| RF03.2 | Validação de Horários | Valida que hora fim seja posterior à hora início e não haja sobreposição no mesmo ambiente e dia da semana. | Alta |
| RF03.3 | Atualização de Catálogos | Permite atualização. Ao marcar como INDISPONÍVEL, cancela reservas PENDENTES automaticamente. | Média |
| RF03.4 | Exclusão de Catálogos | Ao excluir, cancela reservas ativas (PENDENTE, APROVADA, CONFIRMADA, ACONTECENDO) e envia notificações. | Alta |
| RF03.5 | Consulta de Catálogos | Permite consultar catálogos por ambiente, retornando horários disponíveis para reserva. | Alta |
| RF03.6 | Listagem por Dia da Semana | Permite filtrar catálogos por dia da semana para visualização da disponibilidade semanal. | Média |

---

### RF04 - Gestão de Reservas

| ID | Requisito | Descrição | Prioridade |
|---|---|---|---|
| RF04.1 | Criação de Reservas | Usuários autenticados criam reservas informando catálogo, data, horários e finalidade (até 500 caracteres). Sistema atribui criador como host, gera código único de 5 caracteres, define status inicial (PENDENTE ou APROVADA) e registra timestamp. | Alta |
| RF04.2 | Validação de Reservas | Valida data futura ou atual, horário dentro do catálogo e ausência de conflitos com reservas aprovadas. | Alta |
| RF04.3 | Ciclo de Vida das Reservas | Status: PENDENTE (aguardando aprovação), APROVADA (confirmada), CONFIRMADA (confirmada pelo host), ACONTECENDO (em andamento), CONCLUIDA (finalizada), CANCELADA (cancelada), NEGADA (rejeitada). | Alta |
| RF04.4 | Aprovação de Reservas | Administradores e coordenadores aprovam reservas PENDENTES alterando status para APROVADA. | Alta |
| RF04.5 | Rejeição de Reservas | Administradores e coordenadores rejeitam reservas PENDENTES informando motivo, alterando status para NEGADA. | Alta |
| RF04.6 | Cancelamento de Reservas | Host cancela próprias reservas. Administradores/coordenadores cancelam qualquer reserva. Exige motivo. Status muda para CANCELADA e envia notificações. | Alta |
| RF04.7 | Atualização de Reservas | Host ou administradores atualizam data, horários e finalidade de reservas PENDENTES ou APROVADAS respeitando validações. | Média |
| RF04.8 | Adição de Membros | Host adiciona outros usuários como membros (participantes) da reserva. | Média |
| RF04.9 | Remoção de Membros | Host ou administradores removem membros de uma reserva. | Média |
| RF04.10 | Saída Voluntária de Reserva | Membros saem voluntariamente de reservas das quais participam. | Baixa |
| RF04.11 | Ingresso via Código | Usuários ingressam em reserva informando código único de 5 caracteres, tornando-se membros. | Média |
| RF04.12 | Regeneração de Código | Host gera novo código para reserva, invalidando código anterior para novos ingressos. | Baixa |
| RF04.13 | Consulta de Reservas | Administradores/coordenadores consultam todas reservas. Usuários consultam apenas suas reservas (como host ou membro). Filtros por data, status, ambiente e host. | Alta |
| RF04.14 | Atualização Automática de Status | Scheduler atualiza APROVADAS para ACONTECENDO no horário início, ACONTECENDO para CONCLUIDA no horário fim, cancela PENDENTES expiradas e atualiza emUso dos ambientes. | Alta |
| RF04.15 | Histórico de Reservas | Mantém registro de todas reservas incluindo canceladas e concluídas para auditoria e relatórios. | Média |

---

### RF05 - Gestão de Turmas

| ID | Requisito | Descrição | Prioridade |
|---|---|---|---|
| RF05.1 | Criação de Turmas | Professores e administradores criam turmas com nome único (até 100 caracteres), modalidade (FIC/TÉCNICO/FACULDADE), curso (até 100 caracteres), datas início/término, capacidade máxima e professor responsável. Sistema gera código de acesso único de 5 caracteres. | Alta |
| RF05.2 | Matrícula em Turmas | Estudantes matriculam-se via código de acesso se capacidade disponível e data de término não expirada. | Alta |
| RF05.3 | Atualização de Turmas | Professores responsáveis ou administradores atualizam datas, capacidade e modalidade. Ao antecipar data término, cancela reservas futuras associadas automaticamente. | Média |
| RF05.4 | Exclusão de Turmas | Ao excluir, cancela reservas ativas associadas, envia notificações e preserva histórico de estudantes. | Média |
| RF05.5 | Consulta de Turmas | Professores visualizam suas turmas com estudantes. Administradores visualizam todas turmas. Estudantes visualizam turmas matriculadas. | Alta |
| RF05.6 | Remoção de Estudantes | Professores ou administradores removem estudantes de turma. | Média |
| RF05.7 | Saída Voluntária de Turma | Estudantes saem voluntariamente de turmas matriculadas. | Baixa |
| RF05.8 | Associação de Reservas a Turmas | Permite associar reservas a turmas específicas facilitando agendamento de aulas e atividades. | Média |

---

### RF06 - Sistema de Notificações

| ID | Requisito | Descrição | Prioridade |
|---|---|---|---|
| RF06.1 | Geração de Notificações | Gera notificações internas para: criação, aprovação, rejeição e cancelamento de reserva, adição/remoção de membros, indisponibilização de ambiente, exclusão de catálogo e alterações em turmas. | Alta |
| RF06.2 | Visualização de Notificações | Usuários visualizam notificações ordenadas por data de criação (mais recentes primeiro). | Alta |
| RF06.3 | Marcação como Lidas | Usuários marcam notificações como lidas. | Média |
| RF06.4 | Notificações por E-mail | Envia e-mails para aprovação/cancelamento de reservas e código PIN para redefinição de senha. | Média |
| RF06.5 | Histórico de Notificações | Mantém histórico de todas notificações enviadas para cada usuário. | Baixa |

---

### RF07 - Autenticação e Autorização

| ID | Requisito | Descrição | Prioridade |
|---|---|---|---|
| RF07.1 | Autenticação JWT | Autentica usuários com tokens JWT (JSON Web Tokens), validando e-mail e senha. | Alta |
| RF07.2 | Armazenamento de Tokens | Suporta tokens em Header Authorization (Bearer Token) e Cookies HTTP seguros. | Alta |
| RF07.3 | Expiração de Tokens | Tokens JWT têm tempo de expiração configurável, exigindo nova autenticação após período definido. | Alta |
| RF07.4 | Controle de Acesso Baseado em Roles | Restringe acesso por perfis: endpoints administrativos (ADMIN), gestão de ambientes/reservas (ADMIN/COORDENADOR), criação de turmas (ADMIN/COORDENADOR/PROFESSOR), solicitação de reservas (todos autenticados). | Alta |
| RF07.5 | Logout | Permite logout invalidando token atual. | Média |

---

### RF08 - Funcionalidades Administrativas

| ID | Requisito | Descrição | Prioridade |
|---|---|---|---|
| RF08.1 | Dashboard Administrativo | Fornece painel com visão geral: total de usuários por perfil, ambientes por status, reservas por status e turmas ativas. | Média |
| RF08.2 | Aprovação de Pré-Cadastros | Administradores aprovam ou rejeitam solicitações de pré-cadastro. | Média |
| RF08.3 | Gestão de Cache | Implementa cache para consultas frequentes e fornece endpoints administrativos para limpeza de cache. | Baixa |
| RF08.4 | Logs e Auditoria | Registra tentativas de login (sucesso/falha), criação/atualização/exclusão de entidades, aprovações/rejeições de reservas e alterações administrativas. | Média |

---

## 2. Requisitos Não Funcionais

### RNF01 - Segurança

| ID | Requisito | Descrição | Prioridade |
|---|---|---|---|
| RNF01.1 | Criptografia de Senhas | Senhas armazenadas com algoritmo BCrypt com salt único por usuário. | Alta |
| RNF01.2 | Comunicação Segura | Comunicação via HTTPS (TLS/SSL) em produção. | Alta |
| RNF01.3 | Proteção contra Ataques | Proteção contra SQL Injection (PreparedStatements/ORM), XSS (sanitização), CSRF (tokens) e Brute Force (limitação de tentativas). | Alta |
| RNF01.4 | Validação de Entradas | Todas entradas validadas no backend (tipo, formato, comprimento). | Alta |
| RNF01.5 | Controle de Acesso | Controle rigoroso com verificação de autorização em operações sensíveis. | Alta |
| RNF01.6 | Proteção de Dados Sensíveis | Senhas e tokens não expostos em logs, erros ou respostas de API. | Alta |
| RNF01.7 | Assinatura de Tokens JWT | Tokens assinados com chaves RSA (pública/privada) para autenticidade e integridade. | Alta |

---

### RNF02 - Desempenho

| ID | Requisito | Descrição | Prioridade |
|---|---|---|---|
| RNF02.1 | Tempo de Resposta | Consultas simples: até 1s. Operações de escrita: até 2s. Consultas complexas/relatórios: até 5s. | Alta |
| RNF02.2 | Capacidade de Usuários Simultâneos | Suporte mínimo de 100 usuários simultâneos sem degradação perceptível. | Alta |
| RNF02.3 | Otimização de Consultas | Índices apropriados no BD e prevenção de consultas N+1. | Alta |
| RNF02.4 | Cache | Cache em memória (Caffeine) para ambientes, catálogos e configurações. | Média |
| RNF02.5 | Paginação | Listas com grande volume retornadas com paginação. | Média |

---

### RNF03 - Disponibilidade e Confiabilidade

| ID | Requisito | Descrição | Prioridade |
|---|---|---|---|
| RNF03.1 | Disponibilidade | Disponibilidade mínima de 99% durante horário de funcionamento (8h-22h dias úteis). | Alta |
| RNF03.2 | Recuperação de Falhas | Tratamento robusto: falhas de BD com mensagens amigáveis, falhas de e-mail não interrompem operações, logs detalhados para diagnóstico. | Alta |
| RNF03.3 | Backup de Dados | Backup periódico do BD, recomendação de backup diário automatizado. | Alta |
| RNF03.4 | Integridade Referencial | Integridade entre todas entidades com constraints e relacionamentos apropriados. | Alta |
| RNF03.5 | Transações | Operações críticas (reservas, aprovações, cancelamentos) em transações atômicas. | Alta |

---

### RNF04 - Usabilidade

| ID | Requisito | Descrição | Prioridade |
|---|---|---|---|
| RNF04.1 | Interface Responsiva | Interface adaptável a desktop, tablet e smartphone. | Alta |
| RNF04.2 | Feedback ao Usuário | Feedback claro sobre sucesso, erros, validações e processamento em andamento. | Alta |
| RNF04.3 | Mensagens de Erro Amigáveis | Mensagens claras em português, explicando problema e sugerindo solução. | Alta |
| RNF04.4 | Navegação Intuitiva | Navegação com menus organizados e nomenclatura consistente. | Média |
| RNF04.5 | Acessibilidade | Boas práticas: contraste adequado, textos alternativos, navegação por teclado. | Média |
| RNF04.6 | Documentação do Usuário | Guia rápido e documentação de implementação incluídos. | Média |

---

### RNF05 - Manutenibilidade

| ID | Requisito | Descrição | Prioridade |
|---|---|---|---|
| RNF05.1 | Arquitetura em Camadas | Camadas bem definidas: Controller (requisições HTTP), Service (lógica negócio), Repository (dados), Entity (modelo), DTO (transferência). | Alta |
| RNF05.2 | Padrões de Código | Convenções Java/Spring Boot: Classes PascalCase, métodos camelCase, constantes UPPER_SNAKE_CASE, pacotes lowercase. | Alta |
| RNF05.3 | Separação de Responsabilidades | Classes com responsabilidade única e bem definida. | Alta |
| RNF05.4 | Injeção de Dependências | Uso de injeção de dependências Spring, evitando acoplamento forte. | Alta |
| RNF05.5 | Tratamento de Exceções | Tratamento centralizado com @ControllerAdvice. | Alta |
| RNF05.6 | Versionamento de API | Padrões de versionamento para evolução sem quebrar integrações. | Média |

---

### RNF06 - Portabilidade

| ID | Requisito | Descrição | Prioridade |
|---|---|---|---|
| RNF06.1 | Configuração Externa | Arquivo application.properties para configurações sem recompilação (BD, e-mail, porta, chaves). | Alta |
| RNF06.2 | Suporte a Múltiplos Ambientes | Perfis Spring (dev, prod) para diferentes configurações. | Alta |
| RNF06.3 | Banco de Dados | MySQL 8.0 ou superior. | Alta |
| RNF06.4 | Empacotamento | Empacotável como JAR executável. | Alta |
| RNF06.5 | Scripts de Inicialização | Scripts para Windows (run-local.bat) e Linux/Unix (run-local.sh, run-prod.sh). | Média |

---

### RNF07 - Compatibilidade

| ID | Requisito | Descrição | Prioridade |
|---|---|---|---|
| RNF07.1 | Navegadores | Compatível com versões recentes de Chrome, Firefox, Edge e Safari. | Alta |
| RNF07.2 | Java Runtime | Executável em Java 17 ou superior (LTS). | Alta |
| RNF07.3 | Framework | Spring Boot 3.x com Spring Web, Security, Data JPA e Mail. | Alta |

---

### RNF08 - Escalabilidade

| ID | Requisito | Descrição | Prioridade |
|---|---|---|---|
| RNF08.1 | Crescimento de Dados | Desempenho adequado até 10.000 usuários, 100 ambientes e 50.000 reservas/ano. | Média |
| RNF08.2 | Carga de Trabalho | Suporte a picos de acesso em períodos de alta demanda (início de semestre, matrículas). | Média |

---

### RNF09 - Conformidade e Regulamentação

| ID | Requisito | Descrição | Prioridade |
|---|---|---|---|
| RNF09.1 | LGPD | Conformidade com LGPD: coletar apenas dados necessários, obter consentimento, permitir exclusão e implementar medidas de segurança. | Alta |
| RNF09.2 | Privacidade | Dados pessoais não compartilhados com terceiros, acesso restrito por perfil, logs protegidos. | Alta |

---

## 3. Matriz de Rastreabilidade

### Entidades do Sistema x Requisitos Funcionais

| Entidade | Requisitos Relacionados | Descrição |
|---|---|---|
| Usuario | RF01 (completo), RF07.1, RF07.2, RF07.4, RF08.1 | Gestão completa de usuários, autenticação e autorização |
| Ambiente | RF02 (completo), RF04.13, RF08.1 | Gestão de espaços físicos disponíveis para reserva |
| Catalogo | RF03 (completo), RF04.1, RF04.2 | Definição de janelas de tempo para reservas |
| Reserva | RF04 (completo), RF05.8, RF06.1, RF08.1 | Gestão completa do ciclo de vida das reservas |
| Turma | RF05 (completo), RF06.1, RF08.1 | Gestão de turmas e matrículas de estudantes |
| Notificacao | RF06 (completo) | Sistema de notificações internas e por e-mail |
| PreCadastro | RF01.2, RF08.2 | Fluxo de solicitação de acesso ao sistema |
| Role | RF01.4, RF01.5, RF07.4 | Controle de perfis de acesso e permissões |

---

## 4. Resumo Quantitativo

### Requisitos Funcionais por Módulo

| Módulo | Quantidade | Prioridade Alta | Prioridade Média | Prioridade Baixa |
|---|---|---|---|---|
| RF01 - Gestão de Usuários | 9 | 4 | 5 | 0 |
| RF02 - Gestão de Ambientes | 10 | 7 | 3 | 0 |
| RF03 - Gestão de Catálogos | 6 | 4 | 2 | 0 |
| RF04 - Gestão de Reservas | 15 | 9 | 4 | 2 |
| RF05 - Gestão de Turmas | 8 | 3 | 4 | 1 |
| RF06 - Sistema de Notificações | 5 | 2 | 2 | 1 |
| RF07 - Autenticação e Autorização | 5 | 4 | 1 | 0 |
| RF08 - Funcionalidades Administrativas | 4 | 0 | 3 | 1 |
| **TOTAL** | **62** | **33** | **24** | **5** |

### Requisitos Não Funcionais por Categoria

| Categoria | Quantidade | Prioridade Alta | Prioridade Média | Prioridade Baixa |
|---|---|---|---|---|
| RNF01 - Segurança | 7 | 7 | 0 | 0 |
| RNF02 - Desempenho | 5 | 3 | 2 | 0 |
| RNF03 - Disponibilidade e Confiabilidade | 5 | 5 | 0 | 0 |
| RNF04 - Usabilidade | 6 | 3 | 3 | 0 |
| RNF05 - Manutenibilidade | 6 | 5 | 1 | 0 |
| RNF06 - Portabilidade | 5 | 4 | 1 | 0 |
| RNF07 - Compatibilidade | 3 | 3 | 0 | 0 |
| RNF08 - Escalabilidade | 2 | 0 | 2 | 0 |
| RNF09 - Conformidade e Regulamentação | 2 | 2 | 0 | 0 |
| **TOTAL** | **41** | **32** | **9** | **0** |

---

## 5. Modelo de Dados Resumido

| Entidade | Campos Principais | Relacionamentos |
|---|---|---|
| Usuario | id, nome, email (UNIQUE), senha, status | N:N com Role, 1:N com Reserva (host), N:N com Reserva (membros), 1:N com Notificacao, 1:N com Turma (professor), N:N com Turma (estudantes) |
| Ambiente | id, nome (UNIQUE), descricao, disponibilidade, aprovacao, emUso | N:1 com Usuario (responsavel), 1:N com Catalogo |
| Catalogo | id, diaSemana, horaInicio, horaFim, disponibilidade | N:1 com Ambiente, 1:N com Reserva |
| Reserva | id, data, horaInicio, horaFim, statusReserva, finalidade, codigo (UNIQUE), criadoEm | N:1 com Usuario (host), N:N com Usuario (membros), N:1 com Catalogo |
| Turma | id, nome (UNIQUE), modalidade, curso, dataInicio, dataTermino, capacidade, codigoAcesso (UNIQUE) | N:1 com Usuario (professor), N:N com Usuario (estudantes) |
| Notificacao | id, titulo, mensagem, criadoEm, lida | N:1 com Usuario |
| PreCadastro | id, nome, email (UNIQUE), seCadastrou | - |
| Role | id, roleNome | N:N com Usuario |

---

## 6. Glossário de Status e Enumerações

| Enumeração | Valores Possíveis | Contexto de Uso |
|---|---|---|
| UsuarioStatus | ATIVO, INATIVO, PENDENTE | Status do usuário no sistema |
| Disponibilidade | DISPONÍVEL, INDISPONÍVEL | Status de ambientes e catálogos |
| Aprovacao | AUTOMÁTICA, MANUAL | Tipo de aprovação de reservas em ambientes |
| StatusReserva | PENDENTE, APROVADA, CONFIRMADA, ACONTECENDO, CONCLUIDA, CANCELADA, NEGADA | Ciclo de vida das reservas |
| DiaSemana | SEGUNDA, TERCA, QUARTA, QUINTA, SEXTA, SABADO, DOMINGO | Dia da semana dos catálogos |
| Modalidade | FIC, TÉCNICO, FACULDADE | Modalidade das turmas |

---

## 7. Considerações Finais

Este documento apresenta de forma tabular e estruturada todos os requisitos funcionais e não funcionais do Sistema de Gestão de Reservas de Ambientes Acadêmicos. A organização em tabelas facilita:

- **Rastreabilidade:** Identificação clara de cada requisito por ID único
- **Priorização:** Classificação por prioridade (Alta, Média, Baixa) para planejamento de desenvolvimento
- **Compreensão:** Visualização rápida de todos os requisitos e suas características
- **Gestão:** Acompanhamento do progresso de implementação