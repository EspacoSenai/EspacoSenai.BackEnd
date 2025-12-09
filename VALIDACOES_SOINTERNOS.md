# 📋 Documentação de Validações - Atributo `soInternos`

## 🎯 Objetivo
Controlar o acesso a ambientes restritos apenas para uso interno, impedindo que estudantes puros (sem outras funções) façam reservas nestes ambientes.

---

## 🔐 Definições

### **Estudante Puro**
Um usuário que possui APENAS a role `SCOPE_ESTUDANTE`. Não pode acessar ambientes com `soInternos = true`.

### **Usuário Autorizado**
Um usuário que possui uma ou mais das seguintes roles:
- `SCOPE_ADMIN`
- `SCOPE_COORDENADOR`
- `SCOPE_PROFESSOR`
- Estudante com múltiplas roles (ex: ESTUDANTE + PROFESSOR)

---

## ✅ Validações Implementadas

### 1️⃣ **Na Criação de Reserva (`salvar`)**

**Cenário:** Estudante puro tenta criar uma reserva em ambiente `soInternos = true`

```java
validarUsuarioPodeEstarNoAmbiente(host, ambiente);
```

**Resultado:** 
- ✗ Lança `SemPermissaoException`
- Mensagem: "O ambiente '[nome]' é restrito apenas para uso interno. Estudantes puros não podem fazer reservas neste ambiente."

**Status da Reserva:** Não é criada

---

### 2️⃣ **Na Adição de Membros à Reserva**

**Cenário:** Ao criar uma reserva, tentar adicionar estudante puro como membro em ambiente `soInternos = true`

```java
validarUsuarioPodeEstarNoAmbiente(membro, ambiente);
```

**Resultado:**
- ✗ Lanza `SemPermissaoException`
- Mensagem: "O ambiente '[nome]' é restrito apenas para uso interno. Estudantes puros não podem fazer reservas neste ambiente."

**Status da Reserva:** Criada sem os membros não autorizados

---

### 3️⃣ **No Ingresso via Código (`ingressarViacodigo`)**

**Cenário:** Estudante puro tenta ingressar em uma reserva de ambiente `soInternos = true` usando código

```java
Ambiente ambiente = reserva.getCatalogo().getAmbiente();
validarUsuarioPodeEstarNoAmbiente(usuario, ambiente);
```

**Resultado:**
- ✗ Lança `SemPermissaoException`
- Mensagem: "O ambiente '[nome]' é restrito apenas para uso interno. Estudantes puros não podem fazer reservas neste ambiente."

**Status do Ingresso:** Negado

---

### 4️⃣ **Na Atualização de Ambiente - Mudança para `soInternos = true`**

**Cenário:** Admin/Coordenador altera um ambiente para `soInternos = true`

**Ações Automáticas:**
1. Busca todas as reservas futuras/ativas do ambiente
2. Identifica estudantes puros que possuem reservas
3. Cancela automaticamente as reservas desses estudantes
4. Notifica:
   - ✉️ Host da reserva cancelada
   - ✉️ Todos os membros participantes
   - ✉️ Admins e coordenador do ambiente

**Mensagem de Notificação:**
```
Reserva Cancelada Automaticamente ❌

Sua reserva no ambiente '[nome]' para [data] foi CANCELADA AUTOMATICAMENTE.
Este ambiente agora é restrito apenas para uso interno.
```

**Status das Reservas:** Alterado para `CANCELADA`

---

### 5️⃣ **Na Atualização de Ambiente - Mudança para `soInternos = false`**

**Cenário:** Admin/Coordenador altera um ambiente de `soInternos = true` para `false`

**Ações:**
- Notifica admins e coordenador que o ambiente agora está aberto para todos

**Mensagem:**
```
Ambiente Aberto para Todos 🔓

O ambiente '[nome]' agora está disponível para reservas de todos os usuários, 
incluindo estudantes.
```

---

### 6️⃣ **Na Atualização de Reserva - Mudança de Ambiente**

**Cenário:** Host tenta mover reserva para outro ambiente que é `soInternos = true`

**Validação:**
```java
if (ambienteNovo.isSoInternos()) {
    validarUsuarioPodeEstarNoAmbiente(reserva.getHost(), ambienteNovo);
}
```

**Resultado:**
- Se host é estudante puro: ✗ Lança `SemPermissaoException`
- Se host é autorizado: ✓ Atualização permitida

---

## 🔄 Fluxo de Transição de Status (Validações Conflituosas)

### **Estados Válidos da Reserva**
```
PENDENTE → APROVADA → CONFIRMADA → ACONTECENDO → FINALIZADA
          ↓           ↓              ↓
        NEGADA    CANCELADA      CANCELADA
```

### **Transições Inválidas**

| Status Atual | Nova Transição | Permitida? | Razão |
|---|---|---|---|
| CANCELADA | Qualquer | ❌ | Reserva já finalizada |
| NEGADA | Qualquer | ❌ | Reserva já finalizada |
| CONFIRMADA | PENDENTE | ❌ | Não pode desconfirmar |
| ACONTECENDO | PENDENTE | ❌ | Não pode reverter |
| ACONTECENDO | APROVADA | ❌ | Não pode reverter |
| ACONTECENDO | CANCELADA | ✅ | Cancelamento emergencial |

### **Método de Validação**
```java
private void validarTransicaoDeStatus(Reserva reserva, StatusReserva novoStatus)
```

---

## 📊 Casos de Uso - Matriz de Permissões

### **Criar Reserva**

| Tipo de Usuário | Ambiente Normal | Ambiente soInternos |
|---|---|---|
| Estudante Puro | ✅ Permitido | ❌ Bloqueado |
| Estudante + Professor | ✅ Permitido | ✅ Permitido |
| Professor | ✅ Permitido | ✅ Permitido |
| Coordenador | ✅ Permitido | ✅ Permitido |
| Admin | ✅ Permitido | ✅ Permitido |

### **Adicionar como Membro**

| Tipo de Usuário | Ambiente Normal | Ambiente soInternos |
|---|---|---|
| Estudante Puro | ✅ Permitido | ❌ Bloqueado |
| Estudante + Professor | ✅ Permitido | ✅ Permitido |
| Professor | ✅ Permitido | ✅ Permitido |
| Coordenador | ✅ Permitido | ✅ Permitido |
| Admin | ✅ Permitido | ✅ Permitido |

### **Ingressar via Código**

| Tipo de Usuário | Ambiente Normal | Ambiente soInternos |
|---|---|---|
| Estudante Puro | ✅ Permitido | ❌ Bloqueado |
| Estudante + Professor | ✅ Permitido | ✅ Permitido |
| Professor | ✅ Permitido | ✅ Permitido |
| Coordenador | ✅ Permitido | ✅ Permitido |
| Admin | ✅ Permitido | ✅ Permitido |

---

## 🛠️ Métodos Utilitários

### **validarUsuarioPodeEstarNoAmbiente**
```java
private void validarUsuarioPodeEstarNoAmbiente(Usuario usuario, Ambiente ambiente)
```
Centraliza a validação de permissão de um usuário estar em um ambiente.

### **validarTransicaoDeStatus**
```java
private void validarTransicaoDeStatus(Reserva reserva, StatusReserva novoStatus)
```
Valida se a transição de status é permitida.

---

## 📝 Resumo de Mudanças no Código

### **ReservaService.java**
- ✅ Validação em `salvar()`
- ✅ Validação em `atualizar()`
- ✅ Validação em `ingressarViacodigo()`
- ✅ Novo método `validarUsuarioPodeEstarNoAmbiente()`
- ✅ Novo método `validarTransicaoDeStatus()`

### **AmbienteService.java**
- ✅ Tratamento de mudança `soInternos` false → true
- ✅ Tratamento de mudança `soInternos` true → false
- ✅ Cancelamento automático de reservas de estudantes puros
- ✅ Notificações apropriadas em cada caso

---

## 🔔 Notificações Geradas

### **Quando `soInternos` muda de false para true**

#### Notificação ao Host (se estudante puro)
```
Título: Reserva Cancelada Automaticamente ❌
Corpo: Sua reserva no ambiente '[nome]' para [data] foi CANCELADA AUTOMATICAMENTE.
       Este ambiente agora é restrito apenas para uso interno.
```

#### Notificação ao Membro (se estudante puro)
```
Título: Reserva Cancelada Automaticamente ❌
Corpo: A reserva no ambiente '[nome]' para [data] foi CANCELADA AUTOMATICAMENTE.
       Este ambiente agora é restrito apenas para uso interno.
```

#### Notificação aos Admins/Coordenador
```
Título: Ambiente Restrito para Uso Interno 🔒
Corpo: O ambiente '[nome]' agora é restrito apenas para uso interno.
       [N] reserva(s) de estudantes foram canceladas automaticamente.
```

### **Quando `soInternos` muda de true para false**

#### Notificação aos Admins/Coordenador
```
Título: Ambiente Aberto para Todos 🔓
Corpo: O ambiente '[nome]' agora está disponível para reservas de todos os usuários,
       incluindo estudantes.
```

---

## ⚠️ Exceções Lançadas

1. **SemPermissaoException** - Quando estudante puro tenta:
   - Criar reserva em ambiente `soInternos = true`
   - Adicionar como membro em ambiente `soInternos = true`
   - Ingressar via código em ambiente `soInternos = true`
   - Mover reserva para ambiente `soInternos = true`

2. **HorarioInvalidoException** - Quando:
   - Transição de status é inválida
   - Reserva já foi finalizada (CANCELADA, NEGADA)

---

## 🧪 Testes Recomendados

### Teste 1: Criação de Reserva - Estudante Puro
```
1. Login como Estudante Puro
2. Tentar criar reserva em ambiente com soInternos = true
3. Esperado: SemPermissaoException com mensagem apropriada
```

### Teste 2: Mudança de soInternos
```
1. Login como Admin
2. Criar ambiente com soInternos = false
3. Login como Estudante Puro
4. Criar reserva nesse ambiente (deve funcionar)
5. Login como Admin
6. Atualizar ambiente para soInternos = true
7. Esperado: Reserva cancelada automaticamente
8. Estudante recebe notificação de cancelamento
```

### Teste 3: Adição de Membro
```
1. Login como Professor
2. Criar reserva em ambiente com soInternos = true
3. Tentar adicionar Estudante Puro como membro
4. Esperado: SemPermissaoException
```

### Teste 4: Ingresso via Código
```
1. Login como Professor
2. Criar reserva em ambiente com soInternos = true
3. Gerar código da reserva
4. Logout
5. Login como Estudante Puro
6. Tentar ingressar usando código
7. Esperado: SemPermissaoException
```

---

## 📌 Notas Importantes

- A validação é **centralizada** no método `validarUsuarioPodeEstarNoAmbiente()`
- Estudantes com múltiplas roles (ex: ESTUDANTE + PROFESSOR) **podem acessar** ambientes `soInternos`
- Cancelamentos automáticos são **transacionais** e notificam todos os envolvidos
- Transições de status são **validadas** para evitar estados inconsistentes
- A propagação do atributo `soInternos` está **completa** em todos os DTOs e entidades


