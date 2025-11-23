# 🚀 Guia Rápido - Frontend Espaço SENAI

## Como Testar o Sistema

### 1️⃣ Iniciar o Servidor

```bash
# No diretório do projeto
mvnw spring-boot:run
```

### 2️⃣ Acessar o Sistema

Abra o navegador e acesse:
```
http://localhost:8080
```

## 📝 Fluxos de Teste

### Criar Nova Conta

1. Na página inicial, clique em **"Cadastre-se"**
2. Preencha o formulário:
   - **Nome**: Seu nome completo
   - **Email**: email@exemplo.com
   - **Senha**: mínimo 8 caracteres
   - **Confirmar Senha**: mesma senha
3. Clique em **"Cadastrar"**
4. Um código será enviado para o email cadastrado
5. Insira o código recebido
6. Conta confirmada! Faça login

### Fazer Login

1. Na página inicial, preencha:
   - **Email**: seu email cadastrado
   - **Senha**: sua senha
2. Clique em **"Entrar"**
3. Você será redirecionado ao Dashboard

### Recuperar Senha

1. Na página de login, clique em **"Esqueci minha senha"**
2. Digite seu email
3. Clique em **"Enviar Código"**
4. Insira o código recebido por email
5. Digite sua nova senha
6. Confirme a nova senha
7. Pronto! Faça login com a nova senha

## 🎨 Características do Frontend

### ✅ Páginas Implementadas

- **index.html** - Login, Cadastro e Recuperação de Senha
- **dashboard.html** - Painel do usuário autenticado

### ✅ Funcionalidades

- ✔️ Login com validação
- ✔️ Cadastro com verificação por email
- ✔️ Recuperação de senha
- ✔️ Logout
- ✔️ Visualização de perfil
- ✔️ Design responsivo
- ✔️ Mensagens de erro/sucesso
- ✔️ Validação de formulários

### 🎨 Design Responsivo

O sistema se adapta automaticamente a:
- 💻 Desktop (1200px+)
- 📱 Tablet (768px-1199px)  
- 📱 Mobile (<768px)

## 🔐 Validações Implementadas

### Senha
- Mínimo: 8 caracteres
- Máximo: 15 caracteres
- Deve coincidir na confirmação

### Email
- Formato válido obrigatório
- Máximo: 100 caracteres

### Nome
- Máximo: 100 caracteres
- Campo obrigatório

## 🐛 Solução de Problemas

### "Erro de conexão"
- Verifique se o servidor está rodando
- Confirme a URL: http://localhost:8080

### "Token não encontrado"
- Limpe o cache do navegador
- Tente fazer logout e login novamente

### "Código inválido"
- Verifique se digitou corretamente
- Código pode ter expirado (solicite novo)

### Não recebe email
- Verifique spam/lixo eletrônico
- Confirme configuração SMTP no application.properties

## 📊 Status dos Endpoints

### Autenticação (/auth)
- ✅ POST /signin - Login
- ✅ POST /signup - Cadastro
- ✅ GET /confirmar-conta/{token}/{codigo}
- ✅ POST /redefinir-senha
- ✅ GET /redefinir-senha/validar-codigo/{token}/{codigo}
- ✅ POST /redefinir-senha/nova-senha/{token}

### Usuário (/usuario)
- ✅ GET /meu-perfil

## 💡 Dicas de Uso

1. **Primeira vez?** Crie uma conta nova
2. **Esqueceu a senha?** Use o recurso de recuperação
3. **Múltiplas tentativas de login** podem bloquear temporariamente
4. **Código expira** após alguns minutos - solicite novo se necessário

## 🎯 Próximos Passos

Para expandir o frontend, você pode adicionar:
- [ ] Gestão completa de reservas
- [ ] Listagem de ambientes
- [ ] Calendário de reservas
- [ ] Upload de avatar
- [ ] Edição de perfil
- [ ] Notificações em tempo real
- [ ] Histórico de reservas

## 📞 Suporte

Em caso de problemas, verifique:
1. Console do navegador (F12)
2. Logs do servidor
3. Configurações de email (SMTP)
4. Variáveis de ambiente

---

**Bom uso do sistema! 🎉**
