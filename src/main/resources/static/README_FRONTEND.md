# Frontend - Sistema de Reservas Espaço SENAI

## 📋 Descrição

Frontend completo e funcional para o sistema de gestão de reservas do Espaço SENAI, com interface moderna e responsiva.

## 🚀 Funcionalidades Implementadas

### ✅ Autenticação
- **Login** - Autenticação de usuários com email e senha
- **Cadastro** - Registro de novos usuários com validação
- **Verificação de Conta** - Confirmação via código enviado por email
- **Recuperação de Senha** - Reset de senha com código de verificação
- **Logout** - Encerramento seguro da sessão

### 🎨 Interface
- Design moderno e responsivo
- Gradiente roxo/azul no fundo
- Animações suaves nas transições
- Mensagens de erro e sucesso contextualizadas
- Validação em tempo real dos formulários

### 🔒 Segurança
- Tokens JWT armazenados em cookies (gerenciados pela API)
- Validação de senha (8-15 caracteres)
- Proteção de rotas autenticadas
- Redirect automático se não autenticado

## 📁 Estrutura de Arquivos

```
src/main/resources/static/
├── index.html          # Página de login/cadastro
├── dashboard.html      # Dashboard do usuário autenticado
├── css/
│   └── styles.css      # Estilos globais
└── js/
    ├── auth.js         # Lógica de autenticação
    ├── api.js          # Configuração e chamadas à API
    └── dashboard.js    # Lógica do dashboard
```

## 🔌 Integração com API

### Endpoints Utilizados

#### Autenticação (`/auth`)
- `POST /auth/signin` - Login
- `POST /auth/signup` - Cadastro
- `GET /auth/confirmar-conta/{token}/{codigo}` - Verificação de conta
- `POST /auth/redefinir-senha` - Solicitar reset de senha
- `GET /auth/redefinir-senha/validar-codigo/{token}/{codigo}` - Validar código
- `POST /auth/redefinir-senha/nova-senha/{token}` - Definir nova senha

#### Usuário (`/usuario`)
- `GET /usuario/meu-perfil` - Obter dados do usuário logado

## 🎯 Fluxo de Uso

### 1. Cadastro
1. Usuário preenche formulário de cadastro
2. Sistema envia código de verificação por email
3. Usuário insere código recebido
4. Conta é ativada

### 2. Login
1. Usuário fornece email e senha
2. Sistema valida credenciais
3. Token JWT é armazenado em cookie
4. Usuário é redirecionado ao dashboard

### 3. Recuperação de Senha
1. Usuário clica em "Esqueci minha senha"
2. Insere email cadastrado
3. Recebe código por email
4. Valida código
5. Define nova senha

## 💻 Como Executar

### Pré-requisitos
- Java 17+
- Maven
- MySQL configurado
- Variáveis de ambiente configuradas

### Executar o Projeto

```bash
# No diretório raiz do projeto
mvnw spring-boot:run
```

### Acessar o Frontend

```
http://localhost:8080/index.html
```

ou simplesmente:

```
http://localhost:8080/
```

## 📱 Responsividade

O frontend é totalmente responsivo e se adapta a:
- Desktop (1200px+)
- Tablet (768px - 1199px)
- Mobile (< 768px)

## 🎨 Paleta de Cores

```css
--primary-color: #0066cc      /* Azul principal */
--primary-hover: #0052a3      /* Azul hover */
--secondary-color: #6c757d    /* Cinza */
--success-color: #28a745      /* Verde sucesso */
--danger-color: #dc3545       /* Vermelho erro */
--warning-color: #ffc107      /* Amarelo aviso */
```

## 🔧 Validações Implementadas

### Cadastro
- Nome: máximo 100 caracteres
- Email: formato válido, máximo 100 caracteres
- Senha: entre 8 e 15 caracteres
- Confirmação de senha: deve coincidir

### Login
- Email/identificador: obrigatório
- Senha: obrigatória

## 📝 Observações Técnicas

### Armazenamento
- Token JWT: Cookie HTTP-only (gerenciado pela API)
- Dados do usuário: LocalStorage (apenas dados não sensíveis)

### Segurança
- CSRF desabilitado (API Stateless com JWT)
- Sessões: Stateless
- Autenticação: Bearer Token via Cookie

### Funcionalidades Futuras
- Página de perfil completa
- Gestão de reservas
- Visualização de ambientes
- Notificações em tempo real
- Upload de avatar

## 🐛 Troubleshooting

### Erro de CORS
- Verificar configuração CORS no backend
- Certificar-se que credenciais estão habilitadas

### Token não persiste
- Verificar configuração de cookies no SecurityConfig
- Verificar domain e path dos cookies

### Redirecionamento não funciona
- Limpar cache e cookies do navegador
- Verificar console do navegador para erros JavaScript

## 📄 Licença

Projeto desenvolvido para o Sistema de Reservas Espaço SENAI.

---

**Desenvolvido com ❤️ para Espaço SENAI**
