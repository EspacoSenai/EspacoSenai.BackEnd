# DOCUMENTAÇÃO INTEGRA DE REQUISITOS
## Sistema de Gestão de Reservas de Ambientes Acadêmicos

**Versão:** 1.0  
**Data:** Dezembro 2024  
**Instituição:** Espaço SENAI  
**Projeto:** EspacoSenai.BackEnd  
**Framework:** Spring Boot 3.4.1  
**Java:** OpenJDK 21  
**Banco de Dados:** MySQL 8.0+

---

## ÍNDICE

1. [Visão Geral do Projeto](#visão-geral)
2. [Requisitos Funcionais](#requisitos-funcionais)
3. [Requisitos Não Funcionais](#requisitos-não-funcionais)
4. [Tecnologias e Dependências](#tecnologias)
5. [Modelo de Dados](#modelo-de-dados)

---

# VISÃO GERAL DO PROJETO {#visão-geral}

## Objetivo

O **Sistema de Gestão de Reservas de Ambientes Acadêmicos** é uma solução web desenvolvida para otimizar a alocação e o uso de espaços físicos (salas de aula, laboratórios, auditórios, etc.) em instituições de ensino. O sistema proporciona um ambiente digital centralizado para solicitação, aprovação e gerenciamento de reservas de ambientes.

## Escopo

O sistema abrange funcionalidades relacionadas à:
- Gestão de usuários com diferentes perfis
- Gestão de ambientes acadêmicos
- Gestão de catálogos de horários
- Sistema completo de reservas com ciclo de vida
- Gestão de turmas e estudantes
- Sistema de notificações
- Autenticação e autorização baseada em roles
- Funcionalidades administrativas

## Público-Alvo

- **Administradores:** Gestão total do sistema
- **Coordenadores:** Gestão de ambientes e aprovação de reservas
- **Professores:** Criação de turmas e reservas
- **Estudantes:** Solicitação de reservas e matrícula em turmas

---

# REQUISITOS FUNCIONAIS {#requisitos-funcionais}

## RF01 - GESTÃO DE USUÁRIOS

| ID | Requisito | Descrição | Prioridade |
|---|---|---|---|
| RF01.1 | Cadastro de Usuários | O sistema deve permitir o cadastro de novos usuários com nome (até 100 caracteres), e-mail único (até 100 caracteres), senha criptografada e status (ATIVO, INATIVO, PENDENTE). | **ALTA** |
| RF01.2 | Pré-Cadastro | O sistema deve oferecer fluxo de pré-cadastro para usuários solicitarem acesso, informando nome e e-mail. Fica pendente até aprovação por administrador. | **MÉDIA** |
| RF01.3 | Autenticação de Usuários | O sistema deve permitir login via e-mail e senha. Apenas usuários com status ATIVO podem autenticar-se. | **ALTA** |
| RF01.4 | Perfis de Acesso (Roles) | Implementar 4 perfis: ADMIN (acesso total), COORDENADOR (gestão de ambientes), PROFESSOR (criação de turmas), ESTUDANTE (solicitação de reservas). | **ALTA** |
| RF01.5 | Gerenciamento de Perfis | Apenas usuários ADMIN podem atribuir ou modificar perfis (roles) de outros usuários. | **ALTA** |
| RF01.6 | Atualização de Dados Cadastrais | Usuários podem atualizar nome, e-mail e alterar senha mediante autenticação da senha atual. | **MÉDIA** |
| RF01.7 | Redefinição de Senha | O sistema deve oferecer redefinição de senha via código PIN enviado ao e-mail do usuário. | **MÉDIA** |
| RF01.8 | Listagem de Usuários | Administradores visualizam lista completa de usuários com filtro por status e perfil. | **MÉDIA** |
| RF01.9 | Desativação de Usuários | Administradores podem desativar usuários alterando status para INATIVO, sem excluir dados históricos. | **MÉDIA** |

---

## RF02 - GESTÃO DE AMBIENTES

| ID | Requisito | Descrição | Prioridade |
|---|---|---|---|
| RF02.1 | Cadastro de Ambientes | Permitir cadastro com nome único (até 100 caracteres), descrição (até 500 caracteres), disponibilidade (DISPONÍVEL/INDISPONÍVEL), tipo de aprovação (AUTOMÁTICA/MANUAL), indicador emUso e responsável (COORDENADOR). | **ALTA** |
| RF02.2 | Atribuição de Responsável | Atribuir um COORDENADOR como responsável por um ambiente com permissões especiais sobre o mesmo. | **ALTA** |
| RF02.3 | Controle de Disponibilidade | Ambientes DISPONÍVEIS aparecem para novas reservas. INDISPONÍVEIS não aparecem, mas mantêm reservas aprovadas ativas. | **ALTA** |
| RF02.4 | Tipo de Aprovação | AUTOMÁTICA: reservas aprovadas automaticamente sem conflitos. MANUAL: reservas ficam PENDENTES até aprovação. | **ALTA** |
| RF02.5 | Atualização de Ambientes | Administradores ou responsáveis atualizam nome, descrição, disponibilidade e tipo de aprovação. | **MÉDIA** |
| RF02.6 | Exclusão de Ambientes | Ao excluir: cancela todas reservas ativas (PENDENTE, APROVADA, CONFIRMADA, ACONTECENDO), envia notificações e remove catálogos. | **ALTA** |
| RF02.7 | Indisponibilização Temporária | Marca ambiente e seus catálogos como INDISPONÍVEIS, cancela reservas PENDENTES, não afeta reservas aprovadas. | **ALTA** |
| RF02.8 | Disponibilização de Ambientes | Torna ambiente e seus catálogos disponíveis novamente para novas reservas. | **MÉDIA** |
| RF02.9 | Consulta de Ambientes | Permite consulta com filtros por disponibilidade e nome, retornando informações detalhadas incluindo catálogos. | **ALTA** |
| RF02.10 | Indicador de Uso em Tempo Real | Mantém campo "emUso" atualizado indicando se há reserva em andamento no momento. | **MÉDIA** |

---

## RF03 - GESTÃO DE CATÁLOGOS DE HORÁRIOS

| ID | Requisito | Descrição | Prioridade |
|---|---|---|---|
| RF03.1 | Criação de Catálogos | Criar catálogos com ambiente vinculado, dia da semana (SEG-DOM), hora início/fim e disponibilidade. | **ALTA** |
| RF03.2 | Validação de Horários | Validar que hora fim seja posterior à hora início e não haja sobreposição no mesmo ambiente/dia. | **ALTA** |
| RF03.3 | Atualização de Catálogos | Permitir atualização. Ao marcar como INDISPONÍVEL, cancela reservas PENDENTES automaticamente. | **MÉDIA** |
| RF03.4 | Exclusão de Catálogos | Ao excluir, cancela reservas ativas (PENDENTE, APROVADA, CONFIRMADA, ACONTECENDO) e envia notificações. | **ALTA** |
| RF03.5 | Consulta de Catálogos | Permitir consultar catálogos por ambiente, retornando horários disponíveis para reserva. | **ALTA** |
| RF03.6 | Listagem por Dia da Semana | Permitir filtrar catálogos por dia da semana para visualização da disponibilidade semanal. | **MÉDIA** |

---

## RF04 - GESTÃO DE RESERVAS

| ID | Requisito | Descrição | Prioridade |
|---|---|---|---|
| RF04.1 | Criação de Reservas | Usuários autenticados criam reservas com catálogo, data, horários e finalidade (até 500 caracteres). Sistema atribui criador como host, gera código único de 5 caracteres, define status inicial (PENDENTE/APROVADA) e registra timestamp. | **ALTA** |
| RF04.2 | Validação de Reservas | Validar data futura ou atual, horário dentro do catálogo e ausência de conflitos com reservas aprovadas. Validar 15 minutos de antecedência. | **ALTA** |
| RF04.3 | Ciclo de Vida das Reservas | Implementar status: PENDENTE (aguardando aprovação), APROVADA (confirmada), CONFIRMADA (pelo host), ACONTECENDO (em andamento), CONCLUIDA (finalizada), CANCELADA, NEGADA (rejeitada). | **ALTA** |
| RF04.4 | Aprovação de Reservas | Administradores e coordenadores aprovam reservas PENDENTES alterando status para APROVADA. | **ALTA** |
| RF04.5 | Rejeição de Reservas | Administradores e coordenadores rejeitam reservas PENDENTES informando motivo, alterando status para NEGADA. | **ALTA** |
| RF04.6 | Cancelamento de Reservas | Host cancela próprias reservas. Administradores/coordenadores cancelam qualquer reserva. Exige motivo. Status muda para CANCELADA e envia notificações. | **ALTA** |
| RF04.7 | Atualização de Reservas | Host ou administradores atualizam data, horários e finalidade de reservas PENDENTES/APROVADAS respeitando validações. | **MÉDIA** |
| RF04.8 | Adição de Membros | Host adiciona outros usuários como membros (participantes) da reserva. | **MÉDIA** |
| RF04.9 | Remoção de Membros | Host ou administradores removem membros de uma reserva. | **MÉDIA** |
| RF04.10 | Saída Voluntária de Reserva | Membros saem voluntariamente de reservas das quais participam. | **BAIXA** |
| RF04.11 | Ingresso via Código | Usuários ingressam em reserva informando código único de 5 caracteres, tornando-se membros. | **MÉDIA** |
| RF04.12 | Regeneração de Código | Host gera novo código para reserva, invalidando código anterior para novos ingressos. | **BAIXA** |
| RF04.13 | Consulta de Reservas | Administradores/coordenadores consultam todas reservas. Usuários consultam apenas suas (como host ou membro). Filtros por data, status, ambiente, host. | **ALTA** |
| RF04.14 | Atualização Automática de Status | Scheduler atualiza APROVADAS→ACONTECENDO no horário início, ACONTECENDO→CONCLUIDA no horário fim, cancela PENDENTES expiradas, atualiza emUso dos ambientes. | **ALTA** |
| RF04.15 | Histórico de Reservas | Manter registro de todas reservas (canceladas, concluídas) para auditoria e relatórios. | **MÉDIA** |
| RF04.16 | Conflitos e Validações | Validar: usuário não em 2 ambientes simultaneamente, não em 2 reservas com horários sobrepostos no mesmo dia, respeitar capacidade da turma. | **ALTA** |

---

## RF05 - GESTÃO DE TURMAS

| ID | Requisito | Descrição | Prioridade |
|---|---|---|---|
| RF05.1 | Criação de Turmas | Professores e administradores criam turmas com nome único (até 100 caracteres), modalidade (FIC/TÉCNICO/FACULDADE), curso (até 100 caracteres), datas início/término, capacidade máxima e professor responsável. Sistema gera código de acesso único de 5 caracteres. | **ALTA** |
| RF05.2 | Matrícula em Turmas | Estudantes matriculam-se via código de acesso se capacidade disponível e data de término não expirada. | **ALTA** |
| RF05.3 | Atualização de Turmas | Professores responsáveis ou administradores atualizam datas, capacidade e modalidade. Ao antecipar data término, cancela reservas futuras associadas. | **MÉDIA** |
| RF05.4 | Exclusão de Turmas | Ao excluir, cancela reservas ativas associadas, envia notificações e preserva histórico de estudantes. | **MÉDIA** |
| RF05.5 | Consulta de Turmas | Professores visualizam suas turmas com estudantes. Administradores visualizam todas turmas. Estudantes visualizam turmas matriculadas. | **ALTA** |
| RF05.6 | Remoção de Estudantes | Professores ou administradores removem estudantes de turma. | **MÉDIA** |
| RF05.7 | Saída Voluntária de Turma | Estudantes saem voluntariamente de turmas matriculadas. | **BAIXA** |
| RF05.8 | Associação de Reservas a Turmas | Permitir associar reservas a turmas específicas facilitando agendamento de aulas e atividades. | **MÉDIA** |

---

## RF06 - SISTEMA DE NOTIFICAÇÕES

| ID | Requisito | Descrição | Prioridade |
|---|---|---|---|
| RF06.1 | Geração de Notificações | Gerar notificações internas para: criação, aprovação, rejeição e cancelamento de reserva, adição/remoção de membros, indisponibilização de ambiente, exclusão de catálogo, alterações em turmas. | **ALTA** |
| RF06.2 | Visualização de Notificações | Usuários visualizam notificações ordenadas por data de criação (mais recentes primeiro). | **ALTA** |
| RF06.3 | Marcação como Lidas | Usuários marcam notificações como lidas. | **MÉDIA** |
| RF06.4 | Notificações por E-mail | Enviar e-mails para aprovação/cancelamento de reservas e código PIN para redefinição de senha. | **MÉDIA** |
| RF06.5 | Histórico de Notificações | Manter histórico de todas notificações enviadas para cada usuário. | **BAIXA** |

---

## RF07 - AUTENTICAÇÃO E AUTORIZAÇÃO

| ID | Requisito | Descrição | Prioridade |
|---|---|---|---|
| RF07.1 | Autenticação JWT | Autenticar usuários com tokens JWT (JSON Web Tokens), validando e-mail e senha. | **ALTA** |
| RF07.2 | Armazenamento de Tokens | Suportar tokens em Header Authorization (Bearer Token) e Cookies HTTP seguros. | **ALTA** |
| RF07.3 | Expiração de Tokens | Tokens JWT com tempo de expiração configurável, exigindo nova autenticação após período definido. | **ALTA** |
| RF07.4 | Controle de Acesso por Roles | Restringir acesso por perfis: ADMIN (administrativos), ADMIN/COORDENADOR (gestão de ambientes), ADMIN/COORDENADOR/PROFESSOR (criação de turmas), todos autenticados (reservas). | **ALTA** |
| RF07.5 | Logout | Permitir logout invalidando token atual. | **MÉDIA** |

---

## RF08 - FUNCIONALIDADES ADMINISTRATIVAS

| ID | Requisito | Descrição | Prioridade |
|---|---|---|---|
| RF08.1 | Dashboard Administrativo | Fornecer painel com visão geral: total de usuários por perfil, ambientes por status, reservas por status, turmas ativas. | **MÉDIA** |
| RF08.2 | Aprovação de Pré-Cadastros | Administradores aprovam ou rejeitam solicitações de pré-cadastro. | **MÉDIA** |
| RF08.3 | Gestão de Cache | Implementar cache para consultas frequentes e fornecer endpoints administrativos para limpeza de cache. | **BAIXA** |
| RF08.4 | Logs e Auditoria | Registrar tentativas de login (sucesso/falha), criação/atualização/exclusão de entidades, aprovações/rejeições de reservas, alterações administrativas. | **MÉDIA** |

---

# REQUISITOS NÃO FUNCIONAIS {#requisitos-não-funcionais}

## RNF01 - SEGURANÇA

| ID | Requisito | Descrição | Prioridade |
|---|---|---|---|
| RNF01.1 | Criptografia de Senhas | Senhas armazenadas com algoritmo BCrypt com salt único por usuário. | **ALTA** |
| RNF01.2 | Comunicação Segura | Comunicação via HTTPS (TLS/SSL) em produção. | **ALTA** |
| RNF01.3 | Proteção contra Ataques | Proteção contra SQL Injection (PreparedStatements/ORM), XSS (sanitização), CSRF (tokens) e Brute Force (limitação de tentativas). | **ALTA** |
| RNF01.4 | Validação de Entradas | Todas entradas validadas no backend (tipo, formato, comprimento). | **ALTA** |
| RNF01.5 | Controle de Acesso | Controle rigoroso com verificação de autorização em operações sensíveis. | **ALTA** |
| RNF01.6 | Proteção de Dados Sensíveis | Senhas e tokens não expostos em logs, erros ou respostas de API. | **ALTA** |
| RNF01.7 | Assinatura de Tokens JWT | Tokens assinados com chaves RSA (pública/privada) para autenticidade e integridade. | **ALTA** |

---

## RNF02 - DESEMPENHO

| ID | Requisito | Descrição | Prioridade |
|---|---|---|---|
| RNF02.1 | Tempo de Resposta | Consultas simples: até 1s. Operações de escrita: até 2s. Consultas complexas/relatórios: até 5s. | **ALTA** |
| RNF02.2 | Capacidade de Usuários Simultâneos | Suporte mínimo de 100 usuários simultâneos sem degradação perceptível. | **ALTA** |
| RNF02.3 | Otimização de Consultas | Índices apropriados no BD e prevenção de consultas N+1. | **ALTA** |
| RNF02.4 | Cache | Cache em memória (Caffeine) para ambientes, catálogos e configurações. | **MÉDIA** |
| RNF02.5 | Paginação | Listas com grande volume retornadas com paginação. | **MÉDIA** |

---

## RNF03 - DISPONIBILIDADE E CONFIABILIDADE

| ID | Requisito | Descrição | Prioridade |
|---|---|---|---|
| RNF03.1 | Disponibilidade | Disponibilidade mínima de 99% durante horário de funcionamento (8h-22h dias úteis). | **ALTA** |
| RNF03.2 | Recuperação de Falhas | Tratamento robusto: falhas de BD com mensagens amigáveis, falhas de e-mail não interrompem operações, logs detalhados. | **ALTA** |
| RNF03.3 | Backup de Dados | Backup periódico do BD, recomendação de backup diário automatizado. | **ALTA** |
| RNF03.4 | Integridade Referencial | Integridade entre todas entidades com constraints e relacionamentos apropriados. | **ALTA** |
| RNF03.5 | Transações | Operações críticas (reservas, aprovações, cancelamentos) em transações atômicas. | **ALTA** |

---

## RNF04 - USABILIDADE

| ID | Requisito | Descrição | Prioridade |
|---|---|---|---|
| RNF04.1 | Interface Responsiva | Interface adaptável a desktop, tablet e smartphone. | **ALTA** |
| RNF04.2 | Feedback ao Usuário | Feedback claro sobre sucesso, erros, validações e processamento em andamento. | **ALTA** |
| RNF04.3 | Mensagens de Erro Amigáveis | Mensagens claras em português, explicando problema e sugerindo solução. | **ALTA** |
| RNF04.4 | Navegação Intuitiva | Navegação com menus organizados e nomenclatura consistente. | **MÉDIA** |
| RNF04.5 | Acessibilidade | Boas práticas: contraste adequado, textos alternativos, navegação por teclado. | **MÉDIA** |
| RNF04.6 | Documentação do Usuário | Guia rápido e documentação de implementação incluídos. | **MÉDIA** |

---

## RNF05 - MANUTENIBILIDADE

| ID | Requisito | Descrição | Prioridade |
|---|---|---|---|
| RNF05.1 | Arquitetura em Camadas | Camadas bem definidas: Controller (requisições), Service (lógica), Repository (dados), Entity (modelo), DTO (transferência). | **ALTA** |
| RNF05.2 | Padrões de Código | Convenções Java/Spring Boot: Classes PascalCase, métodos camelCase, constantes UPPER_SNAKE_CASE, pacotes lowercase. | **ALTA** |
| RNF05.3 | Separação de Responsabilidades | Classes com responsabilidade única e bem definida. | **ALTA** |
| RNF05.4 | Injeção de Dependências | Uso de injeção de dependências Spring, evitando acoplamento forte. | **ALTA** |
| RNF05.5 | Tratamento de Exceções | Tratamento centralizado com @ControllerAdvice. | **ALTA** |
| RNF05.6 | Versionamento de API | Padrões de versionamento para evolução sem quebrar integrações. | **MÉDIA** |

---

## RNF06 - PORTABILIDADE

| ID | Requisito | Descrição | Prioridade |
|---|---|---|---|
| RNF06.1 | Configuração Externa | Arquivo application.properties para configurações sem recompilação (BD, e-mail, porta, chaves). | **ALTA** |
| RNF06.2 | Suporte a Múltiplos Ambientes | Suportar perfis Spring (dev, prod) para diferentes configurações por ambiente. | **ALTA** |
| RNF06.3 | Banco de Dados | Utilizar MySQL 8.0 ou superior como banco de dados relacional. | **ALTA** |
| RNF06.4 | Empacotamento | Empacotável como arquivo JAR executável, facilitando implantação. | **ALTA** |
| RNF06.5 | Scripts de Inicialização | Incluir scripts para inicialização em diferentes plataformas (Windows, Linux). | **MÉDIA** |

---

## RNF07 - COMPATIBILIDADE

| ID | Requisito | Descrição | Prioridade |
|---|---|---|---|
| RNF07.1 | Navegadores | Compatível com versões recentes: Chrome, Firefox, Edge, Safari. | **ALTA** |
| RNF07.2 | Java Runtime | Executável em Java 17 ou superior (LTS). | **ALTA** |
| RNF07.3 | Framework | Utilizar Spring Boot 3.x com dependências: Spring Web, Security, Data JPA, Mail. | **ALTA** |

---

## RNF08 - ESCALABILIDADE

| ID | Requisito | Descrição | Prioridade |
|---|---|---|---|
| RNF08.1 | Crescimento de Dados | Manter desempenho com até 10.000 usuários, 100 ambientes, 50.000 reservas/ano. | **ALTA** |
| RNF08.2 | Carga de Trabalho | Suportar picos de acesso em períodos de alta demanda (início de semestre, matrículas). | **ALTA** |

---

## RNF09 - CONFORMIDADE E REGULAMENTAÇÃO

| ID | Requisito | Descrição | Prioridade |
|---|---|---|---|
| RNF09.1 | LGPD | Conformidade com Lei Geral de Proteção de Dados: coletar dados necessários, obter consentimento, permitir exclusão, implementar segurança. | **ALTA** |
| RNF09.2 | Privacidade | Respeitar privacidade: dados não compartilhados, acesso restrito por perfil, logs protegidos. | **ALTA** |

---

# TECNOLOGIAS E DEPENDÊNCIAS {#tecnologias}

## Stack Tecnológico

| Componente | Tecnologia | Versão | Descrição |
|---|---|---|---|
| **Backend** | Spring Boot | 3.4.1 | Framework Java para desenvolvimento web |
| **Linguagem** | Java | 21 (OpenJDK) | Linguagem de programação principal |
| **Banco de Dados** | MySQL | 8.0+ | Banco de dados relacional |
| **ORM** | Spring Data JPA / Hibernate | Spring Boot 3.x | Mapeamento objeto-relacional |
| **Segurança** | Spring Security | Spring Boot 3.x | Autenticação e autorização |
| **Tokens** | JWT + OAuth2 | Spring Security 6.x | Autenticação com JSON Web Tokens |
| **E-mail** | Spring Mail | Spring Boot 3.x | Envio de e-mails |
| **Cache** | Caffeine | Latest | Cache em memória |
| **API Documentation** | SpringDoc OpenAPI | 2.5.0 | Documentação automática com Swagger/OpenAPI |
| **Validação** | Spring Validation | Spring Boot 3.x | Validação de dados |
| **Testing** | JUnit 5 + Mockito | Spring Boot 3.x | Testes unitários e integração |
| **Build Tool** | Maven | 4.0+ | Gerenciador de dependências e build |
| **Frontend** | HTML5/CSS3/JavaScript | Vanilla JS | Interface web responsiva |
| **Template Engine** | Thymeleaf | Spring Boot 3.x | Renderização de templates HTML |
| **Relatórios** | JasperReports | 6.20.0 | Geração de relatórios em PDF |
| **Excel** | Apache POI | 5.2.5 | Manipulação de arquivos Excel |
| **PDF** | iText | 2.1.7 | Geração e manipulação de PDFs |

---

## Dependências Maven Principais

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>

<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>

<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.5.0</version>
</dependency>

<dependency>
    <groupId>com.github.ben-manes.caffeine</groupId>
    <artifactId>caffeine</artifactId>
</dependency>

<dependency>
    <groupId>net.sf.jasperreports</groupId>
    <artifactId>jasperreports</artifactId>
    <version>6.20.0</version>
</dependency>

<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml</artifactId>
    <version>5.2.5</version>
</dependency>

<dependency>
    <groupId>com.lowagie</groupId>
    <artifactId>itext</artifactId>
    <version>2.1.7</version>
</dependency>
```

---

# MODELO DE DADOS {#modelo-de-dados}

## Entidades Principais

### 1. Usuario

| Campo | Tipo | Constraints | Descrição |
|---|---|---|---|
| id | BIGINT | PK, AUTO_INCREMENT | Identificador único do usuário |
| nome | VARCHAR(100) | NOT NULL | Nome completo do usuário |
| email | VARCHAR(100) | NOT NULL, UNIQUE | E-mail institucional único |
| senha | VARCHAR(255) | NOT NULL | Senha criptografada (BCrypt) |
| status | ENUM | NOT NULL | ATIVO, INATIVO, PENDENTE |
| notificacoes | OneToMany | | Notificações do usuário |
| roles | ManyToMany | | Papéis/permissões do usuário |
| hostReservas | OneToMany | | Reservas como host |
| membroReservas | ManyToMany | | Reservas como membro |

### 2. Role

| Campo | Tipo | Constraints | Descrição |
|---|---|---|---|
| id | BIGINT | PK, AUTO_INCREMENT | Identificador único da role |
| authority | VARCHAR(100) | NOT NULL, UNIQUE | Nome da role (ADMIN, COORDENADOR, PROFESSOR, ESTUDANTE) |

### 3. Ambiente

| Campo | Tipo | Constraints | Descrição |
|---|---|---|---|
| id | BIGINT | PK, AUTO_INCREMENT | Identificador único |
| nome | VARCHAR(100) | NOT NULL, UNIQUE | Nome do ambiente |
| descricao | VARCHAR(500) | | Descrição do ambiente |
| disponibilidade | ENUM | NOT NULL | DISPONÍVEL, INDISPONÍVEL |
| aprovacao | ENUM | NOT NULL | AUTOMÁTICA, MANUAL |
| emUso | BOOLEAN | DEFAULT FALSE | Indica se está em uso |
| responsavel_id | BIGINT | FK | Coordenador responsável |
| catalogos | OneToMany | | Catálogos de horários |

### 4. Catalogo

| Campo | Tipo | Constraints | Descrição |
|---|---|---|---|
| id | BIGINT | PK, AUTO_INCREMENT | Identificador único |
| ambiente_id | BIGINT | FK, NOT NULL | Referência ao ambiente |
| diaSemana | ENUM | NOT NULL | SEGUNDA, TERCA, QUARTA, QUINTA, SEXTA, SABADO, DOMINGO |
| horaInicio | TIME | NOT NULL | Hora de início do catálogo |
| horaFim | TIME | NOT NULL | Hora de fim do catálogo |
| disponibilidade | ENUM | NOT NULL | DISPONÍVEL, INDISPONÍVEL |
| reservas | OneToMany | | Reservas neste catálogo |

### 5. Reserva

| Campo | Tipo | Constraints | Descrição |
|---|---|---|---|
| id | BIGINT | PK, AUTO_INCREMENT | Identificador único |
| host_id | BIGINT | FK, NOT NULL | Usuário criador da reserva |
| catalogo_id | BIGINT | FK | Referência ao catálogo |
| data | DATE | NOT NULL | Data da reserva |
| horaInicio | TIME | NOT NULL | Hora de início |
| horaFim | TIME | NOT NULL | Hora de fim |
| statusReserva | ENUM | NOT NULL | PENDENTE, APROVADA, CONFIRMADA, ACONTECENDO, CONCLUIDA, CANCELADA, NEGADA |
| finalidade | VARCHAR(500) | | Descrição do motivo da reserva |
| codigo | VARCHAR(5) | NOT NULL, UNIQUE | Código único para ingresso |
| criadoEm | DATETIME | NOT NULL | Data/hora de criação |
| membros | ManyToMany | | Usuários participantes |

### 6. Turma

| Campo | Tipo | Constraints | Descrição |
|---|---|---|---|
| id | BIGINT | PK, AUTO_INCREMENT | Identificador único |
| nome | VARCHAR(100) | NOT NULL, UNIQUE | Nome da turma |
| modalidade | ENUM | NOT NULL | FIC, TÉCNICO, FACULDADE |
| curso | VARCHAR(100) | NOT NULL | Nome do curso |
| dataInicio | DATE | NOT NULL | Data de início da turma |
| dataTermino | DATE | NOT NULL | Data de término da turma |
| capacidadeMaxima | INT | NOT NULL | Máximo de estudantes |
| professor_id | BIGINT | FK, NOT NULL | Professor responsável |
| codigo | VARCHAR(5) | NOT NULL, UNIQUE | Código de acesso para matrícula |
| estudantes | ManyToMany | | Estudantes matriculados |

### 7. Notificacao

| Campo | Tipo | Constraints | Descrição |
|---|---|---|---|
| id | BIGINT | PK, AUTO_INCREMENT | Identificador único |
| usuario_id | BIGINT | FK, NOT NULL | Usuário destinatário |
| tipo | ENUM | NOT NULL | Tipo de notificação |
| mensagem | VARCHAR(500) | NOT NULL | Conteúdo da notificação |
| lida | BOOLEAN | DEFAULT FALSE | Status de leitura |
| criadoEm | DATETIME | NOT NULL | Data/hora de criação |

### 8. PreCadastro

| Campo | Tipo | Constraints | Descrição |
|---|---|---|---|
| id | BIGINT | PK, AUTO_INCREMENT | Identificador único |
| nome | VARCHAR(100) | NOT NULL | Nome do solicitante |
| email | VARCHAR(100) | NOT NULL, UNIQUE | E-mail para contato |
| status | ENUM | NOT NULL | PENDENTE, APROVADO, REJEITADO |
| criadoEm | DATETIME | NOT NULL | Data/hora da solicitação |

---

## Relacionamentos

| Entidades | Tipo | Descrição |
|---|---|---|
| Usuario ↔ Role | ManyToMany | Um usuário pode ter múltiplos papéis |
| Usuario ↔ Notificacao | OneToMany | Um usuário pode receber múltiplas notificações |
| Usuario ↔ Reserva (host) | OneToMany | Um usuário cria múltiplas reservas |
| Usuario ↔ Reserva (membros) | ManyToMany | Um usuário participa de múltiplas reservas |
| Usuario ↔ Ambiente | OneToMany | Um coordenador é responsável por um ou mais ambientes |
| Usuario ↔ Turma | OneToMany | Um professor tem múltiplas turmas |
| Usuario ↔ Turma (estudantes) | ManyToMany | Um estudante está em múltiplas turmas |
| Ambiente ↔ Catalogo | OneToMany | Um ambiente tem múltiplos catálogos |
| Catalogo ↔ Reserva | ManyToOne | Uma reserva referencia um catálogo |
| Turma ↔ Reserva | ManyToOne (implicit) | Uma reserva pode estar associada a uma turma |

---

## Índices e Constraints Recomendados

| Tabela | Índice/Constraint | Campos | Descrição |
|---|---|---|---|
| tb_usuarios | UNIQUE | email | Garante unicidade de e-mail |
| tb_usuarios | INDEX | status | Facilita filtros por status |
| tb_usuarios | INDEX | roles | Melhora busca por perfis |
| tb_ambientes | UNIQUE | nome | Garante nome único |
| tb_ambientes | INDEX | disponibilidade | Facilita filtros |
| tb_ambientes | INDEX | responsavel_id | Melhora busca de ambientes por responsável |
| tb_catalogo | UNIQUE | (ambiente_id, diaSemana) | Evita sobreposição no mesmo dia |
| tb_catalogo | INDEX | ambiente_id | Facilita busca por ambiente |
| tb_reserva | UNIQUE | codigo | Garante código único |
| tb_reserva | INDEX | host_id | Facilita busca de reservas por host |
| tb_reserva | INDEX | catalogo_id | Melhora busca de reservas por catálogo |
| tb_reserva | INDEX | statusReserva | Facilita filtros por status |
| tb_reserva | INDEX | (data, statusReserva) | Otimiza busca por data e status |
| tb_turma | UNIQUE | nome | Garante nome único de turma |
| tb_turma | UNIQUE | codigo | Garante código único |
| tb_turma | INDEX | professor_id | Facilita busca de turmas por professor |
| tb_notificacao | INDEX | usuario_id | Facilita busca de notificações |
| tb_notificacao | INDEX | lida | Facilita filtros de lidas/não lidas |

---

## Sumário de Requisitos

### Requisitos Funcionais por Módulo

| Módulo | Quantidade de Requisitos |
|---|---|
| Gestão de Usuários (RF01) | 9 |
| Gestão de Ambientes (RF02) | 10 |
| Gestão de Catálogos (RF03) | 6 |
| Gestão de Reservas (RF04) | 16 |
| Gestão de Turmas (RF05) | 8 |
| Sistema de Notificações (RF06) | 5 |
| Autenticação e Autorização (RF07) | 5 |
| Funcionalidades Administrativas (RF08) | 4 |
| **TOTAL RF** | **63** |

### Requisitos Não Funcionais por Categoria

| Categoria | Quantidade de Requisitos |
|---|---|
| Segurança (RNF01) | 7 |
| Desempenho (RNF02) | 5 |
| Disponibilidade e Confiabilidade (RNF03) | 5 |
| Usabilidade (RNF04) | 6 |
| Manutenibilidade (RNF05) | 6 |
| Portabilidade (RNF06) | 5 |
| Compatibilidade (RNF07) | 3 |
| Escalabilidade (RNF08) | 2 |
| Conformidade e Regulamentação (RNF09) | 2 |
| **TOTAL RNF** | **41** |

---

## Resumo Total

| Tipo | Quantidade |
|---|---|
| Requisitos Funcionais | 63 |
| Requisitos Não Funcionais | 41 |
| **TOTAL DE REQUISITOS** | **104** |

---

## Observações Importantes

1. **Prioridade Alta (ALTA):** Requisitos essenciais para a funcionalidade básica do sistema
2. **Prioridade Média (MÉDIA):** Requisitos importantes que melhoram a experiência do usuário
3. **Prioridade Baixa (BAIXA):** Requisitos de conveniência ou melhorias futuras

4. **Validações Críticas em Reservas:**
   - Não permitir reserva em data anterior a hoje
   - Validar 15 minutos de antecedência para reservas de hoje
   - Não permitir sobreposição de horários no mesmo catálogo
   - Validar que hora fim > hora início
   - Usuário não pode estar em 2 ambientes simultaneamente
   - Usuário não pode ter 2 reservas com horários sobrepostos no mesmo dia
   - Uma reserva por dia por ambiente com status PENDENTE/APROVADA/CONFIRMADA/ACONTECENDO

5. **Transações Críticas:**
   - Criação de reserva (validações + geração de código)
   - Aprovação/Rejeição de reserva
   - Cancelamento de reserva (notificações)
   - Exclusão de ambiente (cancelamento de reservas)
   - Atualização automática de status (scheduler)

6. **Segurança:**
   - Senhas armazenadas com BCrypt
   - Tokens JWT com chaves RSA
   - Validação de entrada em backend
   - Proteção contra SQL Injection, XSS, CSRF e Brute Force

7. **Performance:**
   - Índices em campos frequentemente consultados
   - Cache Caffeine para dados imutáveis
   - Paginação de listas grandes
   - Evitar N+1 queries

---

**Documento Gerado:** Dezembro 2024  
**Versão:** 1.0  
**Status:** Completo

