# Documento de Requisitos do Sistema

## Sistema de Gestão de Reservas de Ambientes Acadêmicos

**Versão:** 1.0  
**Data:** 27 de novembro de 2025  
**Instituição:** Instituto Federal  

---

## 1. Introdução

### 1.1 Objetivo do Documento

Este documento tem como objetivo descrever de forma detalhada os requisitos funcionais e não funcionais do Sistema de Gestão de Reservas de Ambientes Acadêmicos. O sistema foi desenvolvido para otimizar a alocação e o uso de espaços físicos (salas de aula, laboratórios, auditórios, etc.) em instituições de ensino, proporcionando um ambiente digital centralizado para solicitação, aprovação e gerenciamento de reservas.

### 1.2 Escopo do Sistema

O sistema abrange funcionalidades relacionadas à gestão de usuários, ambientes, catálogos de horários, reservas, turmas e notificações. O público-alvo inclui administradores, coordenadores, professores e estudantes da instituição.

### 1.3 Definições e Terminologia

- **Ambiente:** Espaço físico disponível para reserva (sala, laboratório, auditório).
- **Catálogo:** Janela de tempo pré-definida que indica quando um ambiente está disponível para reserva.
- **Reserva:** Solicitação de uso de um ambiente em data e horário específicos.
- **Host:** Usuário responsável pela criação de uma reserva.
- **Membro:** Usuário participante de uma reserva, adicionado pelo host.
- **Turma:** Grupo de estudantes associados a um professor e um curso específico.

---

## 2. Requisitos Funcionais

Os requisitos funcionais descrevem as funcionalidades e comportamentos que o sistema deve prover aos usuários.

### RF01 - Gestão de Usuários

#### RF01.1 - Cadastro de Usuários
O sistema deve permitir o cadastro de novos usuários contendo as seguintes informações obrigatórias:
- Nome completo (até 100 caracteres)
- E-mail institucional único (até 100 caracteres)
- Senha (armazenada de forma criptografada)
- Status do usuário (ATIVO, INATIVO, PENDENTE)

#### RF01.2 - Pré-Cadastro
O sistema deve oferecer um fluxo de pré-cadastro onde usuários interessados podem solicitar acesso ao sistema, informando nome e e-mail. O pré-cadastro fica pendente até aprovação por um administrador.

#### RF01.3 - Autenticação de Usuários
O sistema deve permitir que usuários façam login utilizando e-mail e senha. Apenas usuários com status ATIVO podem autenticar-se no sistema.

#### RF01.4 - Perfis de Acesso (Roles)
O sistema deve implementar um sistema de perfis de acesso com os seguintes papéis:
- **ADMIN:** Acesso total ao sistema, incluindo gestão de usuários, ambientes, catálogos, reservas e turmas.
- **COORDENADOR:** Gestão de ambientes, aprovação de reservas, gerenciamento de catálogos e visualização de relatórios.
- **PROFESSOR:** Criação e gestão de turmas, criação de reservas, visualização de seus próprios dados.
- **ESTUDANTE:** Solicitação de reservas, matrícula em turmas, visualização de suas próprias reservas.

#### RF01.5 - Gerenciamento de Perfis
O sistema deve permitir que apenas usuários com perfil ADMIN atribuam ou modifiquem os perfis (roles) de outros usuários.

#### RF01.6 - Atualização de Dados Cadastrais
O sistema deve permitir que usuários atualizem seus dados cadastrais (nome, e-mail) e alterem sua senha mediante autenticação da senha atual.

#### RF01.7 - Redefinição de Senha
O sistema deve oferecer um mecanismo de redefinição de senha por meio de código PIN enviado ao e-mail do usuário.

#### RF01.8 - Listagem de Usuários
O sistema deve permitir que administradores visualizem a lista completa de usuários cadastrados, com possibilidade de filtro por status e perfil.

#### RF01.9 - Desativação de Usuários
O sistema deve permitir que administradores desativem usuários, alterando seu status para INATIVO, impedindo login sem excluir os dados históricos.

---

### RF02 - Gestão de Ambientes

#### RF02.1 - Cadastro de Ambientes
O sistema deve permitir o cadastro de ambientes com as seguintes informações:
- Nome único (até 100 caracteres)
- Descrição (até 500 caracteres)
- Disponibilidade (DISPONÍVEL, INDISPONÍVEL)
- Tipo de aprovação (AUTOMÁTICA, MANUAL)
- Indicador de uso atual (emUso: true/false)
- Responsável (usuário com perfil COORDENADOR)

#### RF02.2 - Atribuição de Responsável
O sistema deve permitir atribuir um usuário com perfil COORDENADOR como responsável por um ambiente. O responsável tem permissões especiais sobre o ambiente.

#### RF02.3 - Controle de Disponibilidade
O sistema deve controlar a disponibilidade dos ambientes:
- Ambientes DISPONÍVEIS aparecem como opções para novas reservas.
- Ambientes INDISPONÍVEIS não aparecem como opções, mas mantêm reservas já aprovadas.

#### RF02.4 - Tipo de Aprovação
O sistema deve implementar dois tipos de aprovação para reservas:
- **AUTOMÁTICA:** Reservas são aprovadas automaticamente se não houver conflitos de horário.
- **MANUAL:** Reservas ficam com status PENDENTE até aprovação do responsável ou administrador.

#### RF02.5 - Atualização de Ambientes
O sistema deve permitir que administradores ou responsáveis atualizem as informações de um ambiente, incluindo nome, descrição, disponibilidade e tipo de aprovação.

#### RF02.6 - Exclusão de Ambientes
O sistema deve permitir a exclusão de ambientes. Ao excluir um ambiente:
- Todas as reservas ativas (PENDENTE, APROVADA, CONFIRMADA, ACONTECENDO) devem ser canceladas automaticamente.
- Notificações devem ser enviadas aos hosts e membros das reservas canceladas.
- Todos os catálogos associados devem ser removidos.

#### RF02.7 - Indisponibilização de Ambientes
O sistema deve permitir indisponibilizar um ambiente temporariamente:
- O ambiente e seus catálogos são marcados como INDISPONÍVEIS.
- Reservas PENDENTES são canceladas automaticamente.
- Reservas aprovadas não são afetadas.

#### RF02.8 - Disponibilização de Ambientes
O sistema deve permitir disponibilizar novamente um ambiente indisponível, tornando-o e seus catálogos disponíveis para novas reservas.

#### RF02.9 - Consulta de Ambientes
O sistema deve permitir a consulta de ambientes com filtros por disponibilidade e nome, retornando informações detalhadas incluindo catálogos associados.

#### RF02.10 - Indicador de Uso em Tempo Real
O sistema deve manter atualizado o campo "emUso" do ambiente, indicando se há uma reserva em andamento no momento atual.

---

### RF03 - Gestão de Catálogos de Horários

#### RF03.1 - Criação de Catálogos
O sistema deve permitir a criação de catálogos de horários associados a ambientes, contendo:
- Ambiente vinculado
- Dia da semana (SEGUNDA, TERCA, QUARTA, QUINTA, SEXTA, SABADO, DOMINGO)
- Hora de início
- Hora de fim
- Disponibilidade (DISPONÍVEL, INDISPONÍVEL)

#### RF03.2 - Validação de Horários
O sistema deve validar que:
- A hora de fim seja posterior à hora de início.
- Não haja sobreposição de catálogos para o mesmo ambiente no mesmo dia da semana.

#### RF03.3 - Atualização de Catálogos
O sistema deve permitir a atualização de catálogos existentes. Ao alterar a disponibilidade para INDISPONÍVEL:
- Reservas PENDENTES neste catálogo devem ser canceladas automaticamente.

#### RF03.4 - Exclusão de Catálogos
O sistema deve permitir a exclusão de catálogos. Ao excluir:
- Reservas ativas (PENDENTE, APROVADA, CONFIRMADA, ACONTECENDO) devem ser canceladas.
- Notificações devem ser enviadas aos usuários afetados.

#### RF03.5 - Consulta de Catálogos
O sistema deve permitir consultar catálogos por ambiente, retornando informações sobre os horários disponíveis para reserva.

#### RF03.6 - Listagem de Catálogos por Dia
O sistema deve permitir filtrar catálogos por dia da semana, facilitando a visualização da disponibilidade semanal de um ambiente.

---

### RF04 - Gestão de Reservas

#### RF04.1 - Criação de Reservas
O sistema deve permitir que usuários autenticados criem reservas informando:
- Catálogo (que define o ambiente)
- Data da reserva
- Hora de início
- Hora de fim
- Finalidade (até 500 caracteres)

O sistema deve:
- Atribuir automaticamente o usuário criador como host da reserva.
- Gerar um código único de 5 caracteres alfanuméricos.
- Definir o status inicial como PENDENTE ou APROVADA (dependendo do tipo de aprovação do ambiente).
- Registrar data e hora de criação (criadoEm).

#### RF04.2 - Validação de Reservas
O sistema deve validar que:
- A data da reserva seja futura ou no dia atual.
- O horário solicitado esteja dentro do intervalo do catálogo escolhido.
- Não haja conflito com outras reservas aprovadas no mesmo catálogo, data e horário.

#### RF04.3 - Ciclo de Vida das Reservas
O sistema deve implementar os seguintes status para reservas:
- **PENDENTE:** Aguardando aprovação manual.
- **APROVADA:** Reserva confirmada e aguardando data/hora de início.
- **CONFIRMADA:** Reserva confirmada pelo host (status intermediário opcional).
- **ACONTECENDO:** Reserva em andamento no momento atual.
- **CONCLUIDA:** Reserva finalizada após o término do horário.
- **CANCELADA:** Reserva cancelada pelo host, administrador ou sistema.
- **NEGADA:** Reserva rejeitada por administrador ou coordenador.

#### RF04.4 - Aprovação de Reservas
O sistema deve permitir que administradores e coordenadores aprovem reservas PENDENTES, alterando seu status para APROVADA.

#### RF04.5 - Rejeição de Reservas
O sistema deve permitir que administradores e coordenadores rejeitem reservas PENDENTES, informando o motivo da rejeição. O status deve ser alterado para NEGADA.

#### RF04.6 - Cancelamento de Reservas
O sistema deve permitir que:
- O host cancele suas próprias reservas.
- Administradores e coordenadores cancelem qualquer reserva.
- O motivo do cancelamento seja informado.

Ao cancelar:
- O status deve ser alterado para CANCELADA.
- Notificações devem ser enviadas ao host e membros.

#### RF04.7 - Atualização de Reservas
O sistema deve permitir que o host ou administradores atualizem data, horários e finalidade de reservas PENDENTES ou APROVADAS, respeitando as regras de validação.

#### RF04.8 - Adição de Membros
O sistema deve permitir que o host adicione outros usuários como membros (participantes) da reserva.

#### RF04.9 - Remoção de Membros
O sistema deve permitir que o host ou administradores removam membros de uma reserva.

#### RF04.10 - Saída Voluntária de Reserva
O sistema deve permitir que membros saiam voluntariamente de reservas das quais participam.

#### RF04.11 - Ingresso via Código
O sistema deve permitir que usuários ingressem em uma reserva informando o código único da reserva, tornando-se membros.

#### RF04.12 - Regeneração de Código
O sistema deve permitir que o host gere um novo código para a reserva, invalidando o código anterior para novos ingressos.

#### RF04.13 - Consulta de Reservas
O sistema deve permitir:
- Administradores e coordenadores consultem todas as reservas.
- Usuários consultem apenas suas reservas (como host ou membro).
- Filtros por data, status, ambiente e host.

#### RF04.14 - Atualização Automática de Status
O sistema deve possuir um serviço agendado (scheduler) que:
- Atualiza reservas APROVADAS para ACONTECENDO quando o horário de início é atingido.
- Atualiza reservas ACONTECENDO para CONCLUIDA quando o horário de fim é atingido.
- Cancela automaticamente reservas PENDENTES expiradas.
- Atualiza o campo emUso dos ambientes conforme o status das reservas.

#### RF04.15 - Histórico de Reservas
O sistema deve manter registro histórico de todas as reservas, incluindo canceladas e concluídas, para fins de auditoria e relatórios.

---

### RF05 - Gestão de Turmas

#### RF05.1 - Criação de Turmas
O sistema deve permitir que professores e administradores criem turmas informando:
- Nome único da turma (até 100 caracteres)
- Modalidade (FIC, TÉCNICO, FACULDADE)
- Curso (até 100 caracteres)
- Data de início
- Data de término
- Capacidade máxima de estudantes
- Professor responsável

O sistema deve gerar automaticamente um código de acesso único de 5 caracteres.

#### RF05.2 - Matrícula em Turmas
O sistema deve permitir que estudantes se matriculem em turmas utilizando o código de acesso, desde que:
- A capacidade máxima não tenha sido atingida.
- A data de término da turma não tenha expirado.

#### RF05.3 - Atualização de Turmas
O sistema deve permitir que professores responsáveis ou administradores atualizem informações da turma, incluindo datas, capacidade e modalidade.

Ao alterar a data de término para uma data anterior:
- Reservas futuras associadas à turma devem ser canceladas automaticamente.

#### RF05.4 - Exclusão de Turmas
O sistema deve permitir a exclusão de turmas. Ao excluir:
- Reservas ativas associadas à turma devem ser canceladas.
- Notificações devem ser enviadas aos membros afetados.
- O histórico de estudantes matriculados deve ser preservado.

#### RF05.5 - Consulta de Turmas
O sistema deve permitir:
- Professores visualizem suas turmas com lista de estudantes matriculados.
- Administradores visualizem todas as turmas.
- Estudantes visualizem turmas nas quais estão matriculados.

#### RF05.6 - Remoção de Estudantes
O sistema deve permitir que professores ou administradores removam estudantes de uma turma.

#### RF05.7 - Saída Voluntária de Turma
O sistema deve permitir que estudantes saiam voluntariamente de turmas nas quais estão matriculados.

#### RF05.8 - Associação de Reservas a Turmas
O sistema deve permitir associar reservas a turmas específicas, facilitando o agendamento de aulas e atividades acadêmicas.

---

### RF06 - Sistema de Notificações

#### RF06.1 - Geração de Notificações
O sistema deve gerar notificações internas para os seguintes eventos:
- Criação de reserva
- Aprovação de reserva
- Rejeição de reserva (com motivo)
- Cancelamento de reserva (com motivo)
- Adição como membro de reserva
- Remoção de reserva
- Indisponibilização de ambiente com reservas ativas
- Exclusão de catálogo com reservas ativas
- Alterações em turmas que afetem reservas

#### RF06.2 - Visualização de Notificações
O sistema deve permitir que usuários visualizem suas notificações, ordenadas por data de criação (mais recentes primeiro).

#### RF06.3 - Marcação de Notificações como Lidas
O sistema deve permitir que usuários marquem notificações como lidas.

#### RF06.4 - Notificações por E-mail
O sistema deve enviar notificações por e-mail para eventos importantes, como:
- Aprovação de reservas
- Cancelamento de reservas
- Código PIN para redefinição de senha

#### RF06.5 - Histórico de Notificações
O sistema deve manter histórico de todas as notificações enviadas para cada usuário.

---

### RF07 - Autenticação e Autorização

#### RF07.1 - Autenticação JWT
O sistema deve autenticar usuários utilizando tokens JWT (JSON Web Tokens), validando e-mail e senha.

#### RF07.2 - Armazenamento de Tokens
O sistema deve suportar o armazenamento de tokens em:
- Header Authorization (Bearer Token)
- Cookies HTTP seguros

#### RF07.3 - Expiração de Tokens
Os tokens JWT devem ter tempo de expiração configurável, exigindo nova autenticação após o período definido.

#### RF07.4 - Controle de Acesso Baseado em Roles
O sistema deve restringir o acesso a funcionalidades com base nos perfis (roles) dos usuários:
- Endpoints administrativos acessíveis apenas por ADMIN
- Gestão de ambientes e aprovação de reservas por ADMIN e COORDENADOR
- Criação de turmas por ADMIN, COORDENADOR e PROFESSOR
- Solicitação de reservas por todos os usuários autenticados

#### RF07.5 - Logout
O sistema deve permitir que usuários façam logout, invalidando o token atual.

---

### RF08 - Funcionalidades Administrativas

#### RF08.1 - Dashboard Administrativo
O sistema deve fornecer um painel administrativo com visão geral de:
- Total de usuários por perfil
- Total de ambientes por status
- Reservas por status
- Turmas ativas

#### RF08.2 - Aprovação de Pré-Cadastros
O sistema deve permitir que administradores aprovem ou rejeitem solicitações de pré-cadastro.

#### RF08.3 - Gestão de Cache
O sistema deve implementar cache para otimizar consultas frequentes e fornecer endpoints administrativos para limpeza de cache.

#### RF08.4 - Logs e Auditoria
O sistema deve registrar em logs as seguintes operações:
- Tentativas de login (sucesso e falha)
- Criação, atualização e exclusão de entidades
- Aprovações e rejeições de reservas
- Alterações administrativas

---

## 3. Requisitos Não Funcionais

Os requisitos não funcionais especificam atributos de qualidade, restrições e características técnicas do sistema.

### RNF01 - Segurança

#### RNF01.1 - Criptografia de Senhas
As senhas dos usuários devem ser armazenadas no banco de dados utilizando algoritmos de hash seguros (BCrypt) com salt único para cada usuário.

#### RNF01.2 - Comunicação Segura
A comunicação entre cliente e servidor deve ser protegida com protocolo HTTPS (TLS/SSL) em ambiente de produção.

#### RNF01.3 - Proteção contra Ataques Comuns
O sistema deve implementar proteções contra:
- SQL Injection (utilizando PreparedStatements e ORM)
- Cross-Site Scripting (XSS) (sanitização de entradas)
- Cross-Site Request Forgery (CSRF) (tokens CSRF quando aplicável)
- Brute Force (limitação de tentativas de login)

#### RNF01.4 - Validação de Entradas
Todas as entradas de usuário devem ser validadas no backend, verificando tipo, formato e comprimento dos dados.

#### RNF01.5 - Controle de Acesso
O sistema deve implementar controle de acesso rigoroso, verificando autorização em todas as operações sensíveis.

#### RNF01.6 - Proteção de Dados Sensíveis
Informações sensíveis (senhas, tokens) não devem ser expostas em logs, mensagens de erro ou respostas de API.

#### RNF01.7 - Assinatura de Tokens JWT
Os tokens JWT devem ser assinados utilizando chaves RSA (par de chaves pública/privada) para garantir autenticidade e integridade.

---

### RNF02 - Desempenho

#### RNF02.1 - Tempo de Resposta
O sistema deve responder às requisições em tempo aceitável:
- Consultas simples: até 1 segundo
- Operações de escrita: até 2 segundos
- Consultas complexas (relatórios): até 5 segundos

#### RNF02.2 - Capacidade de Usuários Simultâneos
O sistema deve suportar no mínimo 100 usuários simultâneos sem degradação perceptível de desempenho.

#### RNF02.3 - Otimização de Consultas
O sistema deve utilizar índices apropriados no banco de dados e evitar consultas N+1 em relacionamentos de entidades.

#### RNF02.4 - Cache
O sistema deve implementar cache em memória (Caffeine) para dados acessados frequentemente:
- Lista de ambientes disponíveis
- Catálogos de horários
- Configurações do sistema

#### RNF02.5 - Paginação
Listas com grande volume de dados devem ser retornadas com paginação para otimizar o uso de recursos.

---

### RNF03 - Disponibilidade e Confiabilidade

#### RNF03.1 - Disponibilidade
O sistema deve ter disponibilidade mínima de 99% durante o horário de funcionamento da instituição (considerando 8h-22h em dias úteis).

#### RNF03.2 - Recuperação de Falhas
O sistema deve tratar exceções de forma robusta:
- Falhas de banco de dados devem ser tratadas com mensagens amigáveis
- Falhas no serviço de e-mail não devem interromper outras operações
- Logs detalhados devem ser mantidos para diagnóstico

#### RNF03.3 - Backup de Dados
O sistema deve permitir backup periódico do banco de dados, com recomendação de backup diário automatizado.

#### RNF03.4 - Integridade Referencial
O sistema deve manter a integridade referencial entre todas as entidades, utilizando constraints e relacionamentos apropriados no banco de dados.

#### RNF03.5 - Transações
Operações críticas (criação de reservas, aprovações, cancelamentos) devem ser executadas em transações atômicas, garantindo consistência dos dados.

---

### RNF04 - Usabilidade

#### RNF04.1 - Interface Responsiva
A interface web deve ser responsiva, adaptando-se a diferentes resoluções e dispositivos (desktop, tablet, smartphone).

#### RNF04.2 - Feedback ao Usuário
O sistema deve fornecer feedback claro e imediato sobre:
- Sucesso de operações
- Erros e validações
- Processamento em andamento (loading indicators)

#### RNF04.3 - Mensagens de Erro Amigáveis
Mensagens de erro devem ser claras, em português, explicando o problema e sugerindo solução quando possível.

#### RNF04.4 - Navegação Intuitiva
A interface deve ter navegação intuitiva com menus organizados e nomenclatura consistente.

#### RNF04.5 - Acessibilidade
A interface deve seguir boas práticas de acessibilidade web (contraste adequado, textos alternativos, navegação por teclado).

#### RNF04.6 - Documentação do Usuário
O sistema deve incluir guia rápido e documentação de implementação para orientar os usuários.

---

### RNF05 - Manutenibilidade

#### RNF05.1 - Arquitetura em Camadas
O sistema deve seguir arquitetura em camadas bem definidas:
- **Controller:** Recebimento de requisições HTTP
- **Service:** Lógica de negócio
- **Repository:** Acesso a dados
- **Entity:** Modelo de domínio
- **DTO:** Objetos de transferência de dados

#### RNF05.2 - Padrões de Código
O código deve seguir convenções de nomenclatura e estilo do Java e Spring Boot:
- Classes em PascalCase
- Métodos em camelCase
- Constantes em UPPER_SNAKE_CASE
- Pacotes em lowercase

#### RNF05.3 - Separação de Responsabilidades
Cada classe deve ter responsabilidade única e bem definida, facilitando manutenção e testes.

#### RNF05.4 - Injeção de Dependências
O sistema deve utilizar injeção de dependências do Spring Framework, evitando acoplamento forte entre componentes.

#### RNF05.5 - Tratamento de Exceções
O sistema deve implementar tratamento centralizado de exceções utilizando `@ControllerAdvice`.

#### RNF05.6 - Versionamento de API
A API REST deve seguir padrões de versionamento para facilitar evolução sem quebrar integrações existentes.

---

### RNF06 - Portabilidade

#### RNF06.1 - Configuração Externa
O sistema deve utilizar arquivo `application.properties` para configurações, permitindo alteração sem recompilação:
- Credenciais de banco de dados
- Configurações de e-mail
- Porta do servidor
- Chaves de criptografia

#### RNF06.2 - Suporte a Múltiplos Ambientes
O sistema deve suportar perfis Spring (dev, prod) para diferentes configurações por ambiente.

#### RNF06.3 - Banco de Dados
O sistema deve utilizar MySQL 8.0 ou superior como banco de dados relacional.

#### RNF06.4 - Empacotamento
O sistema deve ser empacotável como arquivo JAR executável, facilitando implantação.

#### RNF06.5 - Scripts de Inicialização
O sistema deve incluir scripts para inicialização em diferentes plataformas:
- Windows (run-local.bat)
- Linux/Unix (run-local.sh, run-prod.sh)

---

### RNF07 - Compatibilidade

#### RNF07.1 - Navegadores
A interface web deve ser compatível com as versões recentes dos principais navegadores:
- Google Chrome
- Mozilla Firefox
- Microsoft Edge
- Safari

#### RNF07.2 - Java Runtime
O sistema deve ser executável em Java 17 ou superior (LTS).

#### RNF07.3 - Framework
O sistema utiliza Spring Boot 3.x com as seguintes dependências principais:
- Spring Web
- Spring Security
- Spring Data JPA
- Spring Mail

---

### RNF08 - Escalabilidade

#### RNF08.1 - Crescimento de Dados
O sistema deve manter desempenho adequado com crescimento do volume de dados:
- Até 10.000 usuários
- Até 100 ambientes
- Até 50.000 reservas por ano

#### RNF08.2 - Carga de Trabalho
O sistema deve suportar picos de acesso em períodos de alta demanda (início de semestre, períodos de matrícula).

---

### RNF09 - Conformidade e Regulamentação

#### RNF09.1 - LGPD
O sistema deve estar em conformidade com a Lei Geral de Proteção de Dados (LGPD):
- Coletar apenas dados necessários
- Obter consentimento para uso de dados
- Permitir exclusão de dados pessoais mediante solicitação
- Implementar medidas de segurança adequadas

#### RNF09.2 - Privacidade
O sistema deve respeitar a privacidade dos usuários:
- Dados pessoais não devem ser compartilhados com terceiros
- Acesso a dados deve ser restrito conforme perfil
- Logs devem ser protegidos e acessíveis apenas por administradores

---

## 4. Modelo de Dados

### 4.1 Entidades Principais

#### Usuario
- id (PK)
- nome
- email (UNIQUE)
- senha (criptografada)
- status (ATIVO, INATIVO, PENDENTE)
- roles (relacionamento N:N com Role)

#### Ambiente
- id (PK)
- nome (UNIQUE)
- descricao
- disponibilidade (DISPONÍVEL, INDISPONÍVEL)
- aprovacao (AUTOMÁTICA, MANUAL)
- emUso (boolean)
- responsavel_id (FK para Usuario)

#### Catalogo
- id (PK)
- ambiente_id (FK para Ambiente)
- diaSemana (SEGUNDA a DOMINGO)
- horaInicio
- horaFim
- disponibilidade (DISPONÍVEL, INDISPONÍVEL)

#### Reserva
- id (PK)
- host_id (FK para Usuario)
- catalogo_id (FK para Catalogo)
- data
- horaInicio
- horaFim
- statusReserva (PENDENTE, APROVADA, NEGADA, etc.)
- finalidade
- codigo (UNIQUE, 5 caracteres)
- criadoEm
- membros (relacionamento N:N com Usuario)

#### Turma
- id (PK)
- nome (UNIQUE)
- modalidade (FIC, TÉCNICO, FACULDADE)
- curso
- dataInicio
- dataTermino
- capacidade
- codigoAcesso (UNIQUE, 5 caracteres)
- professor_id (FK para Usuario)
- estudantes (relacionamento N:N com Usuario)

#### Notificacao
- id (PK)
- usuario_id (FK para Usuario)
- titulo
- mensagem
- criadoEm
- lida (boolean)

#### PreCadastro
- id (PK)
- nome
- email (UNIQUE)
- seCadastrou (boolean)

---

## 5. Considerações Finais

### 5.1 Priorização

Os requisitos funcionais RF01 a RF07 são considerados essenciais para o funcionamento básico do sistema. Requisitos adicionais podem ser implementados em fases posteriores.

### 5.2 Evolução do Sistema

O sistema deve ser projetado com flexibilidade para incorporar futuras melhorias, tais como:
- Relatórios analíticos avançados
- Integração com sistemas acadêmicos externos
- Aplicativo mobile nativo
- Sistema de avaliação de ambientes
- Gestão de recursos materiais (projetores, computadores, etc.)

### 5.3 Manutenção e Suporte

É recomendado estabelecer processo de manutenção contínua com:
- Monitoramento de performance
- Análise de logs
- Atualização de dependências
- Correção de bugs reportados
- Implementação de melhorias sugeridas pelos usuários

