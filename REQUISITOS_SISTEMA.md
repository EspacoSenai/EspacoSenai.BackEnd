# Documento de Requisitos de Software
## Sistema de Gerenciamento de Reservas - Espaço SENAI

---

## 1. VISÃO GERAL DO SISTEMA

### 1.1 Descrição do Projeto
O **Sistema Espaço SENAI** é uma aplicação web backend desenvolvida em Spring Boot para gerenciar reservas de ambientes educacionais (salas, laboratórios, equipamentos) em instituições de ensino do SENAI. O sistema permite que estudantes, professores, coordenadores e administradores realizem, aprovem e gerenciem reservas de forma organizada e eficiente.

### 1.2 Objetivos Principais
- Centralizar o gerenciamento de ambientes e recursos educacionais
- Automatizar o processo de aprovação de reservas
- Evitar conflitos de horários e duplas reservas
- Notificar usuários sobre eventos relacionados às reservas
- Controlar acesso baseado em perfis de usuário (RBAC)
- Gerenciar turmas e seus participantes

### 1.3 Stakeholders
- **Estudantes**: Solicitam reservas de ambientes/recursos
- **Professores**: Criam turmas, gerenciam reservas de suas turmas
- **Coordenadores**: Aprovam/rejeitam reservas, supervisionam o sistema
- **Administradores**: Gerenciam usuários, ambientes, configurações gerais
- **Sistema**: Executa tarefas automáticas (scheduler, notificações)

### 1.4 Tecnologias Utilizadas
- **Backend**: Spring Boot 3.4.1, Java 21
- **Banco de Dados**: MySQL 8
- **Segurança**: Spring Security + OAuth2 Resource Server, JWT (RSA)
- **ORM**: Spring Data JPA, Hibernate
- **Cache**: Caffeine
- **Email**: Spring Mail (SMTP Gmail)
- **Documentação**: SpringDoc OpenAPI (Swagger)
- **Build**: Maven

---

## 2. REQUISITOS FUNCIONAIS (RF)

### 2.1 Gerenciamento de Usuários

#### RF01 - Cadastro de Usuário (Sign Up)
**Descrição**: O sistema deve permitir que novos usuários se cadastrem fornecendo nome, email, senha e roles desejadas.
**Prioridade**: Alta
**Atores**: Usuário não autenticado
**Arquivo**: `AuthController.java`, `UsuarioService.java`
**Fluxo**:
1. Usuário envia dados de cadastro (POST /auth/signup)
2. Sistema valida email único
3. Sistema verifica elegibilidade no pré-cadastro
4. Sistema gera código de confirmação e envia por email
5. Sistema retorna token temporário para confirmação
**Validações**:
- Email deve ser único
- Email deve estar na lista de pré-cadastros (se aplicável)
- Senha deve atender critérios de segurança

#### RF02 - Confirmação de Conta por Email
**Descrição**: O sistema deve enviar um código de verificação por email e validar antes de ativar a conta.
**Prioridade**: Alta
**Atores**: Usuário não autenticado
**Arquivo**: `AuthController.java`, `CodigoService.java`, `EmailService.java`
**Fluxo**:
1. Usuário recebe código de 6 dígitos por email
2. Usuário envia token + código (GET /auth/confirmar-conta/{token}/{codigo})
3. Sistema valida código e prazo de validade
4. Sistema ativa conta e cria usuário definitivo
5. Sistema limpa código do cache

#### RF03 - Autenticação (Login)
**Descrição**: O sistema deve autenticar usuários através de email/tag e senha, retornando token JWT.
**Prioridade**: Alta
**Atores**: Usuário cadastrado
**Arquivo**: `AuthController.java`, `TokenService.java`
**Fluxo**:
1. Usuário envia identificador (email ou tag) e senha (POST /auth/signin)
2. Sistema valida credenciais
3. Sistema verifica status do usuário (não suspenso)
4. Sistema gera token JWT com roles
5. Sistema retorna token + tempo de expiração
**Regras**:
- Token válido por tempo configurável (default: 1 hora)
- Usuários suspensos não podem fazer login

#### RF04 - Recuperação de Senha
**Descrição**: O sistema deve permitir redefinir senha através de código enviado por email.
**Prioridade**: Alta
**Atores**: Usuário cadastrado
**Arquivo**: `AuthController.java`, `CodigoService.java`, `UsuarioService.java`
**Fluxo**:
1. Usuário solicita redefinição (POST /auth/redefinir-senha)
2. Sistema envia código por email
3. Usuário valida código (GET /auth/redefinir-senha/validar-codigo/{token}/{codigo})
4. Usuário define nova senha (POST /auth/redefinir-senha/nova-senha/{token})
5. Sistema atualiza senha criptografada

#### RF05 - Gerenciamento de Perfis de Acesso (Roles)
**Descrição**: O sistema deve suportar 4 perfis: ADMIN, COORDENADOR, PROFESSOR, ESTUDANTE.
**Prioridade**: Alta
**Atores**: Administrador
**Arquivo**: `Role.java`, `SecurityConfig.java`
**Roles**:
- **ADMIN**: Acesso total ao sistema
- **COORDENADOR**: Aprovar/rejeitar reservas, gerenciar usuários
- **PROFESSOR**: Criar turmas, gerenciar reservas de turmas
- **ESTUDANTE**: Criar reservas, participar de turmas

#### RF06 - Sistema de Tags Únicas
**Descrição**: O sistema deve gerar tags únicas de 5 caracteres alfanuméricos para cada usuário.
**Prioridade**: Média
**Atores**: Sistema
**Arquivo**: `Usuario.java`, `CodigoUtil.java`
**Regras**:
- Tag deve ser única no sistema
- Pode ser usada para login (alternativa ao email)
- Gerada automaticamente ou definida pelo usuário

#### RF07 - Atualização de Dados do Usuário
**Descrição**: Usuários devem poder atualizar seus dados pessoais.
**Prioridade**: Média
**Atores**: Usuário autenticado
**Arquivo**: `UsuarioController.java`, `UsuarioService.java`
**Dados atualizáveis**: nome, email, senha
**Restrições**: Email deve permanecer único

#### RF08 - Listagem e Busca de Usuários
**Descrição**: Administradores e coordenadores podem listar e buscar usuários.
**Prioridade**: Média
**Atores**: ADMIN, COORDENADOR
**Arquivo**: `UsuarioController.java`
**Endpoints**:
- GET /usuario/buscar (todos)
- GET /usuario/buscar/{id} (por ID)
- GET /usuario/buscar/tag/{tag} (por tag)

#### RF09 - Suspensão de Usuários
**Descrição**: O sistema deve permitir suspender estudantes temporariamente com motivo e período.
**Prioridade**: Alta
**Atores**: ADMIN, COORDENADOR
**Arquivo**: `EstudanteSuspensao.java`, `UsuarioService.java`
**Dados**: usuário, dataInicio, dataFim, motivo, status
**Regras**:
- Usuários suspensos não podem criar novas reservas
- Reservas existentes são canceladas ou mantidas (configurável)

#### RF10 - Cadastro Privilegiado
**Descrição**: Administradores podem criar usuários diretamente sem processo de confirmação.
**Prioridade**: Baixa
**Atores**: ADMIN
**Arquivo**: `UsuarioController.java`
**Endpoint**: POST /usuario/salvar-privilegiado

---

### 2.2 Gerenciamento de Ambientes

#### RF11 - Cadastro de Ambientes
**Descrição**: O sistema deve permitir cadastrar ambientes (salas, laboratórios) com nome, descrição e configurações.
**Prioridade**: Alta
**Atores**: ADMIN, COORDENADOR
**Arquivo**: `AmbienteController.java`, `AmbienteService.java`, `Ambiente.java`
**Campos**:
- nome (único)
- descrição
- disponibilidade (DISPONIVEL, INDISPONIVEL)
- aprovacao (MANUAL, AUTOMATICA)
- emUso (boolean)
- responsáveis (lista de usuários)

#### RF12 - Disponibilidade de Ambientes
**Descrição**: Ambientes podem ser marcados como DISPONIVEL ou INDISPONIVEL.
**Prioridade**: Alta
**Atores**: ADMIN, COORDENADOR
**Arquivo**: `Disponibilidade.java`
**Regras**:
- Ambientes INDISPONIVEL não aparecem para reserva
- Mudança de status notifica usuários com reservas ativas

#### RF13 - Modo de Aprovação de Ambientes
**Descrição**: Ambientes podem ter aprovação MANUAL ou AUTOMATICA.
**Prioridade**: Alta
**Atores**: ADMIN, COORDENADOR
**Arquivo**: `Aprovacao.java`
**Regras**:
- AUTOMATICA: Reservas são aprovadas imediatamente se não houver conflitos
- MANUAL: Reservas ficam PENDENTE até aprovação de coordenador

#### RF14 - Recursos Associados a Ambientes
**Descrição**: Ambientes podem ter recursos/equipamentos associados (impressoras 3D, computadores, etc).
**Prioridade**: Média
**Atores**: ADMIN, COORDENADOR
**Arquivo**: `Recurso.java`, `AmbienteRecursoController.java`
**Campos**: nome, descrição, disponibilidade, emUso

#### RF15 - Responsáveis por Ambientes
**Descrição**: Ambientes podem ter múltiplos usuários responsáveis.
**Prioridade**: Média
**Atores**: ADMIN, COORDENADOR
**Arquivo**: `Ambiente.java` (relacionamento ManyToMany)
**Regras**: Responsáveis recebem notificações sobre reservas do ambiente

#### RF16 - Listagem e Busca de Ambientes
**Descrição**: Todos os usuários autenticados podem listar e buscar ambientes.
**Prioridade**: Alta
**Atores**: Todos os usuários autenticados
**Arquivo**: `AmbienteController.java`
**Filtros possíveis**: disponibilidade, tipo de aprovação

#### RF17 - Atualização de Ambientes
**Descrição**: Administradores podem atualizar dados de ambientes.
**Prioridade**: Média
**Atores**: ADMIN, COORDENADOR
**Arquivo**: `AmbienteController.java`

#### RF18 - Exclusão de Ambientes
**Descrição**: Administradores podem excluir ambientes sem reservas ativas.
**Prioridade**: Baixa
**Atores**: ADMIN
**Regras**: Não permitir exclusão se houver reservas futuras

---

### 2.3 Gerenciamento de Catálogos e Horários

#### RF19 - Criação de Catálogos
**Descrição**: O sistema deve permitir criar grades de horários disponíveis por ambiente e dia da semana.
**Prioridade**: Alta
**Atores**: ADMIN, COORDENADOR
**Arquivo**: `Catalogo.java`, `CatalogoController.java`
**Campos**:
- ambiente
- diaSemana (SEGUNDA, TERCA, QUARTA, QUINTA, SEXTA, SABADO, DOMINGO)
- horaInicio
- horaFim
- disponibilidade

#### RF20 - Validação de Horários
**Descrição**: O sistema deve validar horários de início e fim dos catálogos.
**Prioridade**: Alta
**Atores**: Sistema
**Arquivo**: `ValidacaoDatasEHorarios.java`
**Regras**:
- horaInicio deve ser antes de horaFim
- Não permitir sobreposição de horários no mesmo dia/ambiente

#### RF21 - Horários Pré-definidos
**Descrição**: O sistema deve suportar cadastro de horários padronizados reutilizáveis.
**Prioridade**: Média
**Atores**: ADMIN
**Arquivo**: `Horario.java`, `HorarioController.java`
**Exemplo**: 08:00-10:00, 10:00-12:00, 14:00-16:00

---

### 2.4 Gerenciamento de Reservas

#### RF22 - Criação de Reserva
**Descrição**: Usuários autenticados podem criar reservas de ambientes.
**Prioridade**: Alta
**Atores**: ADMIN, COORDENADOR, PROFESSOR, ESTUDANTE
**Arquivo**: `ReservaController.java`, `ReservaService.java`, `Reserva.java`
**Campos obrigatórios**:
- host (usuário solicitante)
- catalogo (grade de horário)
- data
- horaInicio
- horaFim
- msgUsuario (opcional)
**Fluxo**:
1. Usuário seleciona ambiente, data e horário
2. Sistema valida disponibilidade
3. Sistema verifica conflitos
4. Sistema cria reserva com status PENDENTE ou APROVADA (conforme tipo de aprovação)
5. Sistema gera código único de 5 caracteres para a reserva

#### RF23 - Validação de Conflitos de Reservas
**Descrição**: O sistema deve impedir reservas conflitantes no mesmo ambiente/horário.
**Prioridade**: Alta
**Atores**: Sistema
**Arquivo**: `ReservaService.java`, `ValidacaoDatasEHorarios.java`
**Regras**:
- Mesmo ambiente não pode ter 2 reservas APROVADAS ou ACONTECENDO no mesmo horário
- Verificar sobreposição de horários
- Permitir múltiplas reservas PENDENTE (fila de espera)

#### RF24 - Código Único de Reserva
**Descrição**: Cada reserva deve ter um código alfanumérico único de 5 caracteres.
**Prioridade**: Alta
**Atores**: Sistema
**Arquivo**: `Reserva.java`, `CodigoUtil.java`
**Uso**: Permitir que outros usuários ingressem na reserva via código

#### RF25 - Aprovação de Reserva
**Descrição**: Coordenadores podem aprovar reservas pendentes.
**Prioridade**: Alta
**Atores**: ADMIN, COORDENADOR
**Arquivo**: `ReservaController.java`
**Endpoint**: PATCH /reserva/aprovar/{id}
**Efeitos**:
- Status muda para APROVADA
- Notifica host e membros
- Bloqueia horário no catálogo

#### RF26 - Rejeição de Reserva
**Descrição**: Coordenadores podem rejeitar reservas com motivo.
**Prioridade**: Alta
**Atores**: ADMIN, COORDENADOR
**Arquivo**: `ReservaController.java`
**Endpoint**: PATCH /reserva/rejeitar/{id}
**Campos**: motivo (obrigatório)
**Efeitos**:
- Status muda para NEGADA
- Notifica host com motivo
- Libera horário

#### RF27 - Cancelamento de Reserva
**Descrição**: Host pode cancelar suas próprias reservas.
**Prioridade**: Alta
**Atores**: Host da reserva
**Arquivo**: `ReservaController.java`
**Endpoint**: DELETE /reserva/deletar/{id}
**Regras**:
- Apenas host ou admin pode cancelar
- Notifica todos os membros
- Libera horário

#### RF28 - Atualização de Reserva
**Descrição**: Host pode atualizar dados da reserva antes de ser aprovada.
**Prioridade**: Média
**Atores**: Host da reserva
**Arquivo**: `ReservaController.java`
**Endpoint**: PATCH /reserva/atualizar/{id}
**Campos atualizáveis**: data, horaInicio, horaFim, msgUsuario
**Restrições**: Apenas reservas PENDENTE podem ser editadas

#### RF29 - Ingresso em Reserva via Código
**Descrição**: Usuários podem ingressar em reservas existentes usando o código de 5 caracteres.
**Prioridade**: Alta
**Atores**: Usuários autenticados
**Arquivo**: `ReservaController.java`
**Endpoint**: POST /reserva/ingressar/{codigo}
**Regras**:
- Reserva deve estar APROVADA ou PENDENTE
- Usuário é adicionado à lista de membros
- Host recebe notificação

#### RF30 - Saída de Reserva
**Descrição**: Membros podem sair de reservas que participam.
**Prioridade**: Média
**Atores**: Membros da reserva
**Arquivo**: `ReservaController.java`
**Endpoint**: DELETE /reserva/sair/{id}
**Regras**: Host não pode sair (deve cancelar a reserva)

#### RF31 - Remoção de Participante
**Descrição**: Host pode remover participantes da reserva.
**Prioridade**: Média
**Atores**: Host da reserva
**Arquivo**: `ReservaController.java`
**Endpoint**: DELETE /reserva/{reservaId}/remover-participante/{participanteId}

#### RF32 - Geração de Novo Código
**Descrição**: Host pode gerar novo código de acesso para a reserva.
**Prioridade**: Baixa
**Atores**: Host da reserva
**Arquivo**: `ReservaController.java`
**Endpoint**: PATCH /reserva/gerar-codigo/{id}
**Uso**: Útil para revogar acesso com código antigo

#### RF33 - Listagem de Reservas
**Descrição**: Sistema deve permitir listar reservas com diferentes filtros.
**Prioridade**: Alta
**Atores**: Depende do endpoint
**Arquivo**: `ReservaController.java`
**Endpoints**:
- GET /reserva/buscar (todas - ADMIN/COORDENADOR)
- GET /reserva/buscar/usuario (minhas reservas - todos)
- GET /reserva/buscar/{id} (detalhes - todos)

#### RF34 - Estados de Reserva
**Descrição**: Reservas devem ter estados bem definidos no ciclo de vida.
**Prioridade**: Alta
**Atores**: Sistema
**Arquivo**: `StatusReserva.java`
**Estados**:
- **PENDENTE**: Aguardando aprovação
- **APROVADA**: Aprovada, aguardando horário
- **ACONTECENDO**: Em andamento no momento
- **CONCLUIDA**: Finalizada com sucesso
- **CANCELADA**: Cancelada pelo host
- **NEGADA**: Rejeitada por coordenador
- **CONFIRMADA**: (adicional, se necessário)

#### RF35 - Validação de Turma para Estudantes
**Descrição**: Estudantes devem estar em uma turma válida para criar reservas.
**Prioridade**: Alta
**Atores**: Sistema
**Arquivo**: `ReservaService.java`
**Regras**:
- Estudante deve pertencer a pelo menos uma turma ativa
- Turma deve estar dentro do período (dataInicio <= hoje <= dataTermino)
- Professores, coordenadores e admins não precisam de turma

---

### 2.5 Gerenciamento de Turmas

#### RF36 - Criação de Turma
**Descrição**: Professores e admins podem criar turmas.
**Prioridade**: Alta
**Atores**: ADMIN, PROFESSOR
**Arquivo**: `TurmaController.java`, `TurmaService.java`, `Turma.java`
**Campos**:
- nome (único)
- modalidade (enum: PRESENCIAL, EAD, HIBRIDO, etc)
- curso
- dataInicio
- dataTermino
- capacidadeMaxima
- professor (responsável)
- codigoAcesso (gerado automaticamente, 5 caracteres)

#### RF37 - Código de Acesso da Turma
**Descrição**: Cada turma tem código único para ingresso de estudantes.
**Prioridade**: Alta
**Atores**: Sistema
**Arquivo**: `Turma.java`, `CodigoUtil.java`
**Regras**: Código de 5 caracteres alfanuméricos único

#### RF38 - Ingresso em Turma via Código
**Descrição**: Estudantes podem ingressar em turmas usando código de acesso.
**Prioridade**: Alta
**Atores**: ESTUDANTE
**Arquivo**: `TurmaController.java`
**Endpoint**: PATCH /turma/ingressar-por-codigo/{codigoAcesso}
**Validações**:
- Código deve existir
- Turma não pode estar cheia (capacidade)
- Turma deve estar ativa (dentro do período)

#### RF39 - Adição Manual de Estudantes
**Descrição**: Professores podem adicionar estudantes manualmente à turma.
**Prioridade**: Média
**Atores**: ADMIN, PROFESSOR
**Arquivo**: `TurmaController.java`
**Endpoint**: POST /turma/adicionarestudantes/{turmaId}
**Body**: Lista de IDs de estudantes

#### RF40 - Remoção de Estudantes
**Descrição**: Professores podem remover estudantes da turma.
**Prioridade**: Média
**Atores**: ADMIN, PROFESSOR
**Arquivo**: `TurmaController.java`
**Endpoint**: DELETE /turma/removerestudantes/{turmaId}

#### RF41 - Atualização de Professor
**Descrição**: Admins e professores podem alterar o professor responsável pela turma.
**Prioridade**: Média
**Atores**: ADMIN, PROFESSOR
**Arquivo**: `TurmaController.java`
**Endpoint**: PATCH /turma/atualizarprofessor/{turmaId}/{professorId}

#### RF42 - Geração de Novo Código de Turma
**Descrição**: Professores podem gerar novo código de acesso para a turma.
**Prioridade**: Baixa
**Atores**: ADMIN, PROFESSOR
**Arquivo**: `TurmaController.java`
**Endpoint**: PATCH /turma/gerar-novo-codigo/{turmaId}

#### RF43 - Listagem de Turmas
**Descrição**: Listar turmas conforme permissões.
**Prioridade**: Alta
**Atores**: ADMIN, COORDENADOR, PROFESSOR
**Arquivo**: `TurmaController.java`
**Endpoints**:
- GET /turma/buscar (todas)
- GET /turma/buscar/{id} (detalhes)

#### RF44 - Validação de Período de Turma
**Descrição**: Sistema deve validar se turma está ativa com base em datas.
**Prioridade**: Alta
**Atores**: Sistema
**Arquivo**: `TurmaService.java`
**Regras**: dataInicio <= dataAtual <= dataTermino

#### RF45 - Capacidade Máxima de Turma
**Descrição**: Turmas devem respeitar capacidade máxima de estudantes.
**Prioridade**: Média
**Atores**: Sistema
**Regras**: Bloquear ingresso se capacidade atingida

---

### 2.6 Sistema de Notificações

#### RF46 - Notificação de Nova Reserva
**Descrição**: Sistema notifica responsáveis quando nova reserva é criada.
**Prioridade**: Alta
**Atores**: Sistema
**Arquivo**: `NotificacaoService.java`, `ReservaService.java`
**Tipo**: NOVA_RESERVA

#### RF47 - Notificação de Aprovação
**Descrição**: Host e membros recebem notificação quando reserva é aprovada.
**Prioridade**: Alta
**Atores**: Sistema
**Tipo**: RESERVA_APROVADA

#### RF48 - Notificação de Rejeição
**Descrição**: Host recebe notificação com motivo quando reserva é rejeitada.
**Prioridade**: Alta
**Atores**: Sistema
**Tipo**: RESERVA_REJEITADA

#### RF49 - Notificação de Cancelamento
**Descrição**: Membros recebem notificação quando reserva é cancelada.
**Prioridade**: Alta
**Atores**: Sistema
**Tipo**: RESERVA_CANCELADA

#### RF50 - Notificação de Início de Reserva
**Descrição**: Sistema notifica host quando reserva começa.
**Prioridade**: Média
**Atores**: Sistema
**Arquivo**: `ReservaEventListener.java`, `ReservaSchedulerService.java`
**Tipo**: RESERVA_COMECOU
**Trigger**: Scheduler detecta horaInicio

#### RF51 - Notificação de Término de Reserva
**Descrição**: Sistema notifica host quando reserva termina.
**Prioridade**: Média
**Atores**: Sistema
**Tipo**: RESERVA_TERMINOU
**Trigger**: Scheduler detecta horaFim

#### RF52 - Lembrete de Reserva
**Descrição**: Sistema pode enviar lembretes antes do horário da reserva.
**Prioridade**: Baixa
**Atores**: Sistema
**Tipo**: LEMBRETE_RESERVA

#### RF53 - Listagem de Notificações
**Descrição**: Usuários podem listar suas notificações.
**Prioridade**: Média
**Atores**: Usuários autenticados
**Arquivo**: `NotificacaoService.java`

#### RF54 - Marcar Notificação como Lida
**Descrição**: Usuários podem marcar notificações como lidas.
**Prioridade**: Média
**Atores**: Usuários autenticados
**Arquivo**: `NotificacaoService.java`

#### RF55 - Exclusão de Notificação
**Descrição**: Usuários podem excluir suas notificações.
**Prioridade**: Baixa
**Atores**: Usuários autenticados

---

### 2.7 Agendamento Automático (Scheduler)

#### RF56 - Verificação Periódica de Reservas
**Descrição**: Sistema verifica reservas a cada 1 minuto para atualizar status.
**Prioridade**: Alta
**Atores**: Sistema
**Arquivo**: `ReservaSchedulerService.java`
**Anotação**: @Scheduled(fixedRate = 60000)

#### RF57 - Início Automático de Reserva
**Descrição**: Sistema muda status para ACONTECENDO quando horaInicio é atingida.
**Prioridade**: Alta
**Atores**: Sistema
**Fluxo**:
1. Scheduler detecta reserva APROVADA com horaInicio <= agora < horaFim
2. Muda status para ACONTECENDO
3. Marca ambiente como emUso = true
4. Publica evento ReservaStatusEvent
5. Listener envia notificação ao host

#### RF58 - Término Automático de Reserva
**Descrição**: Sistema muda status para CONCLUIDA quando horaFim é atingida.
**Prioridade**: Alta
**Atores**: Sistema
**Fluxo**:
1. Scheduler detecta reserva ACONTECENDO com horaFim <= agora
2. Muda status para CONCLUIDA
3. Marca ambiente como emUso = false
4. Publica evento ReservaStatusEvent
5. Listener envia notificação ao host

#### RF59 - Event Listener para Eventos de Reserva
**Descrição**: Sistema escuta eventos de mudança de status e reage apropriadamente.
**Prioridade**: Alta
**Atores**: Sistema
**Arquivo**: `ReservaEventListener.java`, `ReservaStatusEvent.java`
**Eventos**:
- Início de reserva (isInicio = true)
- Término de reserva (isInicio = false)

#### RF60 - Limpeza de Códigos Expirados
**Descrição**: Sistema deve limpar códigos de confirmação expirados do cache.
**Prioridade**: Média
**Atores**: Sistema
**Arquivo**: `CodigoService.java`, `CaffeineConfig.java`
**Configuração**: Caffeine cache com TTL

---

### 2.8 Pré-cadastro

#### RF61 - Registro de Pré-cadastro
**Descrição**: Administradores podem pré-cadastrar emails autorizados.
**Prioridade**: Média
**Atores**: ADMIN
**Arquivo**: `PreCadastro.java`, `PreCadastroController.java`
**Campos**: nome, email, seCadastrou (boolean)

#### RF62 - Validação de Elegibilidade
**Descrição**: Sistema valida se email está na lista de pré-cadastros ao criar conta.
**Prioridade**: Média
**Atores**: Sistema
**Arquivo**: `PreCadastroService.java`

#### RF63 - Marcação de Cadastro Concluído
**Descrição**: Sistema marca pré-cadastro como concluído após confirmação.
**Prioridade**: Baixa
**Atores**: Sistema

---

### 2.9 Cache e Performance

#### RF64 - Cache de Códigos de Confirmação
**Descrição**: Sistema usa cache para armazenar códigos temporários.
**Prioridade**: Alta
**Atores**: Sistema
**Arquivo**: `CodigoService.java`, `CaffeineConfig.java`
**Tecnologia**: Caffeine
**TTL**: Configurável (ex: 10 minutos)

#### RF65 - Cache de Dados Frequentes
**Descrição**: Sistema pode cachear dados frequentemente acessados.
**Prioridade**: Baixa
**Atores**: Sistema
**Candidatos**: Catálogos, Ambientes, Horários

---

### 2.10 Envio de Emails

#### RF66 - Envio de Email de Confirmação
**Descrição**: Sistema envia email com código de confirmação ao cadastrar.
**Prioridade**: Alta
**Atores**: Sistema
**Arquivo**: `EmailService.java`
**Template**: Código de 6 dígitos

#### RF67 - Envio de Email de Recuperação de Senha
**Descrição**: Sistema envia email com código para redefinir senha.
**Prioridade**: Alta
**Atores**: Sistema

#### RF68 - Envio de Múltiplos Emails
**Descrição**: Sistema pode enviar emails para múltiplos destinatários.
**Prioridade**: Média
**Atores**: Sistema
**Uso**: Notificar todos os membros de uma reserva

#### RF69 - Configuração SMTP
**Descrição**: Sistema usa SMTP Gmail configurado via variáveis de ambiente.
**Prioridade**: Alta
**Atores**: Sistema
**Arquivo**: `application.properties`
**Variáveis**: EMAILSENDER_USERNAME, EMAILSENDER_PASSWORD, EMAILSENDER_PORT

---

## 3. REQUISITOS NÃO FUNCIONAIS (RNF)

### 3.1 Segurança

#### RNF01 - Autenticação JWT com RSA
**Descrição**: Sistema deve usar tokens JWT assinados com chaves RSA.
**Prioridade**: Alta
**Arquivo**: `SecurityConfig.java`, `TokenService.java`
**Detalhes**:
- Chave privada (app.key) para assinar tokens
- Chave pública (app.pub) para validar tokens
- Algoritmo: RS256

#### RNF02 - Criptografia de Senhas
**Descrição**: Senhas devem ser criptografadas com BCrypt antes de armazenar.
**Prioridade**: Alta
**Arquivo**: `SecurityConfig.java`
**Tecnologia**: BCryptPasswordEncoder
**Custo**: Default (10 rounds)

#### RNF03 - Autorização Baseada em Roles (RBAC)
**Descrição**: Endpoints devem validar permissões via @PreAuthorize.
**Prioridade**: Alta
**Arquivo**: Controllers (todos)
**Exemplo**: @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_COORDENADOR')")

#### RNF04 - Proteção contra CSRF
**Descrição**: API REST deve ser stateless e não usar cookies/sessões.
**Prioridade**: Alta
**Arquivo**: `SecurityConfig.java`
**Configuração**: CSRF desabilitado (stateless API)

#### RNF05 - CORS Configurável
**Descrição**: Sistema deve permitir configurar origens permitidas.
**Prioridade**: Média
**Arquivo**: `WebConfig.java`
**Padrão**: Permitir todas as origens (desenvolvimento)
**Produção**: Restringir a domínios específicos

#### RNF06 - Variáveis de Ambiente Sensíveis
**Descrição**: Credenciais não devem estar hardcoded, usar variáveis de ambiente.
**Prioridade**: Alta
**Arquivo**: `application.properties`
**Variáveis**: DB_HOST, DB_PORT, DB_USERNAME, DB_PASSWORD, JWT_PRIVATE_KEY

#### RNF07 - Validação de Entrada
**Descrição**: Sistema deve validar todos os dados de entrada com Bean Validation.
**Prioridade**: Alta
**Tecnologia**: Jakarta Validation (@Valid, @NotNull, @NotBlank, etc)

---

### 3.2 Performance

#### RNF08 - Cache de Dados
**Descrição**: Sistema deve cachear dados frequentes para melhorar performance.
**Prioridade**: Média
**Tecnologia**: Caffeine
**Arquivo**: `CaffeineConfig.java`

#### RNF09 - Otimização de Queries
**Descrição**: Usar fetch strategies apropriadas para evitar N+1 queries.
**Prioridade**: Média
**Técnicas**: LAZY loading, JOIN FETCH quando necessário

#### RNF10 - Pool de Conexões
**Descrição**: Sistema deve usar pool de conexões com banco de dados.
**Prioridade**: Alta
**Tecnologia**: HikariCP (padrão Spring Boot)

#### RNF11 - Agendamento Eficiente
**Descrição**: Scheduler não deve sobrecarregar o sistema.
**Prioridade**: Média
**Arquivo**: `ReservaSchedulerService.java`
**Configuração**: fixedRate adequado (60000ms = 1 minuto)

---

### 3.3 Escalabilidade

#### RNF12 - Arquitetura em Camadas
**Descrição**: Sistema deve separar responsabilidades (Controller, Service, Repository).
**Prioridade**: Alta
**Padrão**: MVC + Service Layer

#### RNF13 - API RESTful
**Descrição**: Endpoints devem seguir convenções REST.
**Prioridade**: Alta
**Padrões**:
- GET (buscar), POST (criar), PUT/PATCH (atualizar), DELETE (excluir)
- Códigos HTTP semânticos (200, 201, 204, 400, 401, 403, 404)

#### RNF14 - Stateless
**Descrição**: API não deve manter estado de sessão no servidor.
**Prioridade**: Alta
**Benefício**: Facilita escalonamento horizontal

#### RNF15 - DTOs para Transferência
**Descrição**: Usar DTOs para separar modelo de domínio da API.
**Prioridade**: Média
**Arquivo**: Package `dto`

---

### 3.4 Confiabilidade

#### RNF16 - Transações Atômicas
**Descrição**: Operações críticas devem ser transacionais.
**Prioridade**: Alta
**Anotação**: @Transactional
**Exemplos**: Criação de reserva, aprovação, cancelamento

#### RNF17 - Tratamento Global de Exceções
**Descrição**: Sistema deve ter handler global para exceções.
**Prioridade**: Alta
**Arquivo**: `ExceptionGlobal.java`
**Anotação**: @RestControllerAdvice

#### RNF18 - Exceções Customizadas
**Descrição**: Sistema deve ter exceções específicas para cada erro de negócio.
**Prioridade**: Média
**Arquivo**: Package `exception`
**Exemplos**: SemResultadosException, DadoDuplicadoException, HorarioInvalidoException

#### RNF19 - Validação de Regras de Negócio
**Descrição**: Sistema deve validar todas as regras antes de persistir.
**Prioridade**: Alta
**Arquivo**: Services (classes de serviço)
**Exemplos**: Conflitos de horário, capacidade de turma, turma válida

#### RNF20 - Logs Estruturados
**Descrição**: Sistema deve logar operações importantes.
**Prioridade**: Média
**Exemplos**: Início/término de reserva, aprovações, erros

---

### 3.5 Usabilidade

#### RNF21 - Mensagens de Erro Claras
**Descrição**: Respostas de erro devem ser descritivas e úteis.
**Prioridade**: Média
**Formato**: JSON com status, message, timestamp

#### RNF22 - Códigos Fáceis de Digitar
**Descrição**: Códigos de acesso devem ser curtos (5 caracteres) e alfanuméricos.
**Prioridade**: Média
**Arquivo**: `CodigoUtil.java`

#### RNF23 - Documentação de API
**Descrição**: Sistema deve ter documentação interativa da API.
**Prioridade**: Alta
**Tecnologia**: SpringDoc OpenAPI (Swagger UI)
**URL**: /swagger-ui.html

#### RNF24 - Respostas Padronizadas
**Descrição**: Respostas devem seguir formato consistente.
**Prioridade**: Média
**Arquivo**: `ResponseBuilder.java`

---

### 3.6 Manutenibilidade

#### RNF25 - Código Limpo
**Descrição**: Código deve seguir convenções Java e Spring Boot.
**Prioridade**: Média
**Padrões**: Nomes descritivos, métodos pequenos, comentários quando necessário

#### RNF26 - Injeção de Dependências
**Descrição**: Usar injeção de dependências do Spring.
**Prioridade**: Alta
**Anotações**: @Autowired, @Service, @Repository, @Controller

#### RNF27 - Separação de Configurações
**Descrição**: Configurações devem estar em arquivos properties separados.
**Prioridade**: Alta
**Arquivo**: application.properties, application-dev.properties

#### RNF28 - Versionamento
**Descrição**: Código deve estar em sistema de controle de versão.
**Prioridade**: Alta
**Recomendação**: Git

---

### 3.7 Portabilidade

#### RNF29 - Banco de Dados Configurável
**Descrição**: Sistema deve funcionar com diferentes instâncias MySQL via variáveis.
**Prioridade**: Alta
**Variáveis**: DB_HOST, DB_PORT, DB_USERNAME, DB_PASSWORD

#### RNF30 - Compatibilidade Docker
**Descrição**: Sistema deve ser containerizável.
**Prioridade**: Média
**Tecnologia**: Docker, docker-compose

#### RNF31 - Build Automatizado
**Descrição**: Sistema deve ter build reproduzível.
**Prioridade**: Alta
**Tecnologia**: Maven
**Comando**: mvn clean install

#### RNF32 - Profiles Spring
**Descrição**: Sistema deve suportar diferentes perfis (dev, prod).
**Prioridade**: Média
**Arquivo**: application.properties
**Propriedade**: spring.profiles.active

---

### 3.8 Observabilidade

#### RNF33 - Health Check
**Descrição**: Sistema deve expor endpoint de health.
**Prioridade**: Alta
**Tecnologia**: Spring Boot Actuator
**Endpoint**: /actuator/health

#### RNF34 - Métricas
**Descrição**: Sistema pode expor métricas de performance.
**Prioridade**: Baixa
**Tecnologia**: Spring Boot Actuator
**Endpoint**: /actuator/metrics

#### RNF35 - SQL Logging
**Descrição**: Sistema pode logar queries SQL em desenvolvimento.
**Prioridade**: Baixa
**Configuração**: spring.jpa.show-sql=true

---

## 4. REGRAS DE NEGÓCIO

### RN01 - Conflito de Reservas
Não é permitido ter duas reservas APROVADAS ou ACONTECENDO no mesmo ambiente com horários sobrepostos.

### RN02 - Aprovação Automática
Se o ambiente tiver aprovação = AUTOMATICA, reservas são aprovadas automaticamente se não houver conflitos.

### RN03 - Aprovação Manual
Se o ambiente tiver aprovacao = MANUAL, reservas ficam PENDENTE até aprovação de coordenador.

### RN04 - Estudante Requer Turma Válida
Estudantes só podem criar reservas se estiverem em uma turma ativa (dentro do período dataInicio-dataTermino).

### RN05 - Código Único
Códigos de reserva e turma devem ser únicos no sistema.

### RN06 - Tag Única
Tags de usuário devem ser únicas no sistema.

### RN07 - Email Único
Emails de usuário devem ser únicos no sistema.

### RN08 - Validação de Horários
horaInicio deve ser sempre anterior a horaFim.

### RN09 - Validação de Datas
data de reserva deve ser futura ou presente.

### RN10 - Período de Turma
Turma só aceita novos membros se dataInicio <= hoje <= dataTermino.

### RN11 - Capacidade de Turma
Turma não pode ter mais estudantes que capacidadeMaxima.

### RN12 - Suspensão Bloqueia Reservas
Usuários com EstudanteSuspensao ativa não podem criar novas reservas.

### RN13 - Ambiente Indisponível
Ambientes com disponibilidade = INDISPONIVEL não aparecem para reserva.

### RN14 - Recurso Indisponível
Recursos com disponibilidade = INDISPONIVEL não podem ser reservados.

### RN15 - Mudança de Status Automática
Status de reserva muda automaticamente por scheduler:
- APROVADA → ACONTECENDO (quando horaInicio chega)
- ACONTECENDO → CONCLUIDA (quando horaFim chega)

### RN16 - Notificação de Mudanças
Qualquer mudança de status de reserva gera notificação aos envolvidos.

### RN17 - Reserva Futura
Não é possível criar reserva para data/hora já passada.

### RN18 - Catálogo Ativo
Só é possível criar reserva em catálogo com disponibilidade = DISPONIVEL.

### RN19 - Dia da Semana
Reserva deve respeitar o dia da semana do catálogo selecionado.

### RN20 - Ambiente em Uso
Quando reserva está ACONTECENDO, ambiente.emUso = true.

---

## 5. MODELO DE DADOS PRINCIPAL

### 5.1 Entidades Principais

#### Usuario
- **id**: Long (PK)
- **nome**: String(100)
- **email**: String(100, unique)
- **senha**: String (criptografado)
- **tag**: String(5, unique)
- **status**: UsuarioStatus (enum)
- **roles**: Set<Role> (ManyToMany)
- **notificacoes**: Set<Notificacao> (OneToMany)
- **membroReservas**: Set<Reserva> (ManyToMany)

#### Role
- **id**: Long (PK)
- **roleNome**: Values (enum: ADMIN, COORDENADOR, PROFESSOR, ESTUDANTE)

#### Ambiente
- **id**: Long (PK)
- **nome**: String(100, unique)
- **descricao**: String(500)
- **disponibilidade**: Disponibilidade (enum)
- **aprovacao**: Aprovacao (enum)
- **emUso**: boolean
- **recursos**: Set<Recurso> (OneToMany)
- **catalogos**: Set<Catalogo> (OneToMany)
- **responsaveis**: Set<Usuario> (ManyToMany)

#### Catalogo
- **id**: Long (PK)
- **ambiente**: Ambiente (ManyToOne)
- **horaInicio**: LocalTime
- **horaFim**: LocalTime
- **diaSemana**: DiaSemana (enum)
- **disponibilidade**: Disponibilidade (enum)
- **reservas**: Set<Reserva> (OneToMany)

#### Reserva
- **id**: Long (PK)
- **host**: Usuario (ManyToOne)
- **membros**: Set<Usuario> (ManyToMany)
- **catalogo**: Catalogo (ManyToOne)
- **data**: LocalDate
- **horaInicio**: LocalTime
- **horaFim**: LocalTime
- **statusReserva**: StatusReserva (enum)
- **msgUsuario**: String(500)
- **msgInterna**: String(500)
- **codigo**: String(5, unique)
- **dataHoraSolicitacao**: LocalDateTime

#### Turma
- **id**: Long (PK)
- **nome**: String(100, unique)
- **modalidade**: Modalidade (enum)
- **curso**: String
- **dataInicio**: LocalDate
- **dataTermino**: LocalDate
- **codigoAcesso**: String(5, unique)
- **estudantes**: Set<Usuario> (ManyToMany)
- **capacidadeMaxima**: Integer
- **professor**: Usuario (ManyToOne)

#### Notificacao
- **id**: Long (PK)
- **usuario**: Usuario (ManyToOne)
- **notificacaoTipo**: NotificacaoTipo (enum)
- **titulo**: String(100)
- **mensagem**: String(500)
- **dataHoraCriacao**: LocalDateTime
- **lida**: boolean

#### EstudanteSuspensao
- **id**: Long (PK)
- **usuario**: Usuario (ManyToOne)
- **dataInicio**: Date
- **dataFim**: Date
- **motivo**: String(500)
- **statusSuspensao**: StatusSuspensao (enum)

#### PreCadastro
- **id**: Long (PK)
- **nome**: String(100)
- **email**: String(100, unique)
- **seCadastrou**: boolean

#### Recurso
- **id**: Long (PK)
- **ambiente**: Ambiente (ManyToOne)
- **nome**: String(100, unique)
- **descricao**: String
- **disponibilidade**: Disponibilidade (enum)
- **emUso**: boolean

#### Horario
- **id**: Long (PK)
- **horaInicio**: LocalTime
- **horaFim**: LocalTime

---

### 5.2 Relacionamentos

1. **Usuario ↔ Role**: ManyToMany (tb_usuarios_roles)
2. **Usuario ↔ Notificacao**: OneToMany
3. **Usuario ↔ Reserva (host)**: OneToMany
4. **Usuario ↔ Reserva (membros)**: ManyToMany (tb_reserva_participantes)
5. **Usuario ↔ Turma (estudantes)**: ManyToMany (tb_turmas_estudantes)
6. **Usuario ↔ Turma (professor)**: ManyToOne
7. **Usuario ↔ Ambiente (responsaveis)**: ManyToMany (tb_ambiente_responsavel)
8. **Usuario ↔ EstudanteSuspensao**: OneToMany
9. **Ambiente ↔ Recurso**: OneToMany
10. **Ambiente ↔ Catalogo**: OneToMany
11. **Catalogo ↔ Reserva**: OneToMany

---

## 6. CASOS DE USO PRINCIPAIS

### CU01 - Criar e Aprovar Reserva

**Ator Principal**: Estudante, Professor
**Atores Secundários**: Coordenador, Sistema

**Fluxo Principal**:
1. Usuário faz login
2. Usuário lista ambientes disponíveis
3. Usuário seleciona ambiente e visualiza catálogos (horários disponíveis)
4. Usuário escolhe data e horário
5. Usuário preenche mensagem (opcional)
6. Sistema valida:
   - Usuário está em turma válida (se estudante)
   - Não há conflito de horários
   - Catálogo está disponível
7. Sistema cria reserva:
   - Status = PENDENTE (se aprovação manual) ou APROVADA (se automática)
   - Gera código único
8. Sistema notifica responsáveis (se manual)
9. Coordenador aprova/rejeita reserva (se manual)
10. Sistema notifica host
11. No horário, scheduler muda status para ACONTECENDO
12. Sistema notifica host (início)
13. Ao fim, scheduler muda status para CONCLUIDA
14. Sistema notifica host (término)

**Fluxos Alternativos**:
- 6a. Conflito de horário → Erro, sugerir outros horários
- 6b. Estudante sem turma → Erro, solicitar ingresso em turma
- 9a. Reserva rejeitada → Notifica host com motivo, libera horário

---

### CU02 - Ingressar em Reserva via Código

**Ator Principal**: Estudante, Professor

**Fluxo Principal**:
1. Host cria reserva e recebe código único (ex: "A1B2C")
2. Host compartilha código com colegas
3. Colega faz login
4. Colega acessa endpoint /reserva/ingressar/{codigo}
5. Sistema valida:
   - Código existe
   - Reserva está APROVADA ou PENDENTE
6. Sistema adiciona colega aos membros
7. Sistema notifica host sobre novo membro

---

### CU03 - Criar e Gerenciar Turma

**Ator Principal**: Professor

**Fluxo Principal**:
1. Professor faz login
2. Professor cria turma informando:
   - Nome, modalidade, curso
   - dataInicio, dataTermino
   - capacidadeMaxima
3. Sistema gera código de acesso único
4. Professor compartilha código com estudantes
5. Estudantes ingressam via código
6. Sistema valida capacidade
7. Estudantes agora podem criar reservas

**Fluxos Alternativos**:
- 6a. Capacidade cheia → Erro, estudante não pode ingressar
- Professor pode adicionar/remover estudantes manualmente

---

### CU04 - Sistema de Notificações em Tempo Real

**Ator Principal**: Sistema

**Fluxo Principal**:
1. Evento ocorre (reserva aprovada, início, término, etc)
2. Sistema cria notificação na base de dados
3. Sistema envia email (se configurado)
4. Usuário acessa sistema e vê notificação
5. Usuário marca como lida
6. Usuário pode excluir notificação

---

## 7. ENDPOINTS DA API (Resumo)

### Autenticação
- POST /auth/signin
- POST /auth/signup
- GET /auth/confirmar-conta/{token}/{codigo}
- POST /auth/redefinir-senha
- GET /auth/redefinir-senha/validar-codigo/{token}/{codigo}
- POST /auth/redefinir-senha/nova-senha/{token}

### Usuários
- GET /usuario/buscar
- GET /usuario/buscar/{id}
- GET /usuario/buscar/tag/{tag}
- PUT /usuario/atualizar
- POST /usuario/salvar-privilegiado
- DELETE /usuario/deletar/{id}

### Ambientes
- GET /ambiente/buscar
- GET /ambiente/buscar/{id}
- POST /ambiente/salvar
- PUT /ambiente/atualizar/{id}
- DELETE /ambiente/deletar/{id}

### Catálogos
- GET /catalogo/buscar
- GET /catalogo/buscar/{id}
- POST /catalogo/salvar
- PUT /catalogo/atualizar/{id}
- DELETE /catalogo/deletar/{id}

### Reservas
- GET /reserva/buscar
- GET /reserva/buscar/usuario
- GET /reserva/buscar/{id}
- POST /reserva/salvar
- PATCH /reserva/atualizar/{id}
- DELETE /reserva/deletar/{id}
- PATCH /reserva/aprovar/{id}
- PATCH /reserva/rejeitar/{id}
- POST /reserva/ingressar/{codigo}
- DELETE /reserva/sair/{id}
- PATCH /reserva/gerar-codigo/{id}
- DELETE /reserva/{reservaId}/remover-participante/{participanteId}

### Turmas
- GET /turma/buscar
- GET /turma/buscar/{id}
- POST /turma/salvar
- POST /turma/adicionarestudantes/{turmaId}
- DELETE /turma/removerestudantes/{turmaId}
- PATCH /turma/atualizarprofessor/{turmaId}/{professorId}
- PATCH /turma/ingressar-por-codigo/{codigoAcesso}
- PATCH /turma/gerar-novo-codigo/{turmaId}

### Horários
- GET /horario/buscar
- POST /horario/salvar
- DELETE /horario/deletar/{id}

### Pré-cadastro
- GET /precadastro/buscar
- POST /precadastro/salvar
- DELETE /precadastro/deletar/{id}

---

## 8. ARQUIVOS PRINCIPAIS DO PROJETO

### Configuração
- `application.properties` - Configurações gerais
- `SecurityConfig.java` - Configuração de segurança
- `WebConfig.java` - Configuração CORS
- `CaffeineConfig.java` - Configuração de cache
- `AdminUserConfig.java` - Criação de usuário admin inicial

### Entidades
- `Usuario.java`, `Role.java`
- `Ambiente.java`, `Recurso.java`
- `Catalogo.java`, `Horario.java`
- `Reserva.java`
- `Turma.java`
- `Notificacao.java`
- `EstudanteSuspensao.java`
- `PreCadastro.java`
- `CodigoConfirmacao.java`

### Controllers
- `AuthController.java`
- `UsuarioController.java`
- `AmbienteController.java`
- `CatalogoController.java`
- `ReservaController.java`
- `TurmaController.java`
- `HorarioController.java`
- `PreCadastroController.java`

### Services
- `TokenService.java`
- `UsuarioService.java`
- `AmbienteService.java`
- `CatalogoService.java`
- `ReservaService.java`
- `TurmaService.java`
- `NotificacaoService.java`
- `EmailService.java`
- `CodigoService.java`
- `ReservaSchedulerService.java`

### Utilities
- `CodigoUtil.java` - Geração de códigos únicos
- `ValidacaoDatasEHorarios.java` - Validações de datas
- `ResponseBuilder.java` - Padronização de respostas
- `MetodosAuth.java` - Métodos auxiliares de autenticação

### Exceções
- `ExceptionGlobal.java` - Handler global
- `SemResultadosException.java`
- `DadoDuplicadoException.java`
- `HorarioInvalidoException.java`
- `DataInvalidaException.java`
- `SemPermissaoException.java`
- `TurmaInvalidaException.java`

### Events & Listeners
- `ReservaStatusEvent.java` - Evento de mudança de status
- `ReservaEventListener.java` - Listener para notificações

---

## 9. MELHORIAS FUTURAS SUGERIDAS

### Alta Prioridade
1. **RF Faltantes**: Gestão de reservas com overrun (quando passa do tempo)
2. **Webhooks**: Notificações via webhook para frontend
3. **WebSocket**: Notificações em tempo real
4. **Relatórios**: Exportação de relatórios (PDF, Excel)
5. **Auditoria**: Log de todas as ações de usuários

### Média Prioridade
6. **Dashboard**: Estatísticas de uso de ambientes
7. **Fila de Espera**: Sistema de fila quando ambiente está ocupado
8. **QR Code**: Geração de QR code para códigos de acesso
9. **Recorrência**: Reservas recorrentes (semanais, mensais)
10. **Imagens**: Upload de imagens de ambientes

### Baixa Prioridade
11. **Avaliações**: Usuários avaliam ambientes após uso
12. **Favoritos**: Usuários marcam ambientes favoritos
13. **Integração Calendar**: Integração com Google Calendar
14. **Chat**: Sistema de chat entre membros de reserva
15. **Mobile App**: Aplicativo móvel

---

## 10. CONCLUSÃO

Este documento apresenta os requisitos funcionais e não funcionais do **Sistema de Gerenciamento de Reservas - Espaço SENAI**, uma aplicação robusta desenvolvida em Spring Boot para gerenciar reservas de ambientes educacionais.

O sistema implementa:
- ✅ Autenticação e autorização seguras (JWT + OAuth2)
- ✅ Gerenciamento completo de usuários, ambientes e reservas
- ✅ Sistema de turmas com códigos de acesso
- ✅ Notificações automáticas por eventos
- ✅ Agendamento automático (scheduler) para controle de reservas
- ✅ Cache para performance
- ✅ API REST bem estruturada
- ✅ Validações robustas de regras de negócio
- ✅ Tratamento de exceções
- ✅ Documentação OpenAPI (Swagger)

O sistema está pronto para uso em ambientes educacionais do SENAI, facilitando o agendamento e controle de recursos compartilhados.

---

**Versão**: 1.0  
**Data**: 2025-01-17  
**Autores**: Equipe de Desenvolvimento Espaço SENAI  
**Contato**: [Informações de contato]

