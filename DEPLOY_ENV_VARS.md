# Guia de Configuração de Variáveis de Ambiente para Produção

Este arquivo fornece um guia passo-a-passo para configurar a aplicação em ambiente de produção (nuvem).

## 1. Variáveis de Ambiente Necessárias

### 1.1 Spring Profile
```bash
SPRING_PROFILES_ACTIVE=prod
```

### 1.2 Chave RSA Privada (CRÍTICO)

A chave privada RSA deve ser injetada como uma **string PEM completa**:

```bash
JWT_PRIVATE_KEY=-----BEGIN PRIVATE KEY-----\nMIIEvQIBADANBgkqhkiG9w0BAQE...\n-----END PRIVATE KEY-----
```

**Como preparar a chave:**
1. Acesse seu arquivo `app.key` localmente
2. Converta para string com quebras de linha como `\n`:
   ```powershell
   # Windows PowerShell
   $key = Get-Content app.key -Raw
   $key = $key -replace "`r`n", "\n"
   Write-Host $key
   ```
   
   ```bash
   # Linux/Mac
   cat app.key | tr '\n' '\\n'
   ```
3. Copie a saída e configure como variável de ambiente

### 1.3 Banco de Dados

```bash
DB_URL=jdbc:mysql://seu-host:3306/reserva
DB_USERNAME=seu-usuario
DB_PASSWORD=sua-senha
```

### 1.4 Configurações de Email

```bash
EMAILSENDER_HOST=smtp.gmail.com
EMAILSENDER_PORT=587
EMAILSENDER_USERNAME=seu-email@gmail.com
EMAILSENDER_PASSWORD=sua-senha-de-app
```

**Nota:** Se usar Gmail, gere uma [senha de app](https://myaccount.google.com/apppasswords)

### 1.5 Servidor (Opcional)

```bash
SERVER_PORT=8080
```

---

## 2. Configuração por Plataforma

### 2.1 Azure App Service

1. Acesse **Configuração** > **Configurações da aplicação**
2. Adicione cada variável como **Nova configuração de aplicação**
3. Clique em **Salvar**

```
SPRING_PROFILES_ACTIVE = prod
JWT_PRIVATE_KEY = -----BEGIN PRIVATE KEY-----\n...
DB_URL = jdbc:mysql://...
DB_USERNAME = ...
DB_PASSWORD = ...
EMAILSENDER_HOST = smtp.gmail.com
EMAILSENDER_PORT = 587
EMAILSENDER_USERNAME = ...
EMAILSENDER_PASSWORD = ...
```

### 2.2 AWS Elastic Beanstalk

Crie um arquivo `.ebextensions/env.config`:

```yaml
option_settings:
  aws:elasticbeanstalk:application:environment:
    SPRING_PROFILES_ACTIVE: prod
    JWT_PRIVATE_KEY: "-----BEGIN PRIVATE KEY-----\n...\n-----END PRIVATE KEY-----"
    DB_URL: jdbc:mysql://...
    DB_USERNAME: seu-usuario
    DB_PASSWORD: sua-senha
    EMAILSENDER_HOST: smtp.gmail.com
    EMAILSENDER_PORT: 587
    EMAILSENDER_USERNAME: seu-email@gmail.com
    EMAILSENDER_PASSWORD: sua-senha-app
```

### 2.3 Heroku

```bash
heroku config:set SPRING_PROFILES_ACTIVE=prod
heroku config:set JWT_PRIVATE_KEY="-----BEGIN PRIVATE KEY-----\n...\n-----END PRIVATE KEY-----"
heroku config:set DB_URL=jdbc:mysql://...
heroku config:set DB_USERNAME=seu-usuario
heroku config:set DB_PASSWORD=sua-senha
heroku config:set EMAILSENDER_HOST=smtp.gmail.com
heroku config:set EMAILSENDER_PORT=587
heroku config:set EMAILSENDER_USERNAME=seu-email@gmail.com
heroku config:set EMAILSENDER_PASSWORD=sua-senha-app
```

### 2.4 Docker Compose

Crie um arquivo `docker-compose.yml`:

```yaml
version: '3.8'
services:
  app:
    image: seu-registro/espaco-senai-backend:latest
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - JWT_PRIVATE_KEY=-----BEGIN PRIVATE KEY-----\n...\n-----END PRIVATE KEY-----
      - DB_URL=jdbc:mysql://db:3306/reserva
      - DB_USERNAME=usuario
      - DB_PASSWORD=senha
      - EMAILSENDER_HOST=smtp.gmail.com
      - EMAILSENDER_PORT=587
      - EMAILSENDER_USERNAME=seu-email@gmail.com
      - EMAILSENDER_PASSWORD=sua-senha-app
    depends_on:
      - db
  
  db:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: senha
      MYSQL_DATABASE: reserva
    volumes:
      - db_data:/var/lib/mysql

volumes:
  db_data:
```

---

## 3. Build e Deploy

### 3.1 Build da Aplicação

```bash
# Limpa compilações anteriores
mvn clean

# Compila e empacota a aplicação
mvn package -DskipTests

# O JAR será gerado em target/reserva-0.0.1-SNAPSHOT.jar
```

### 3.2 Teste Local com Variáveis de Produção

Para testar a configuração de produção localmente:

```powershell
# Windows PowerShell
$env:SPRING_PROFILES_ACTIVE = "prod"
$env:JWT_PRIVATE_KEY = "-----BEGIN PRIVATE KEY-----\n..."
$env:DB_URL = "jdbc:mysql://seu-host:3306/reserva"
$env:DB_USERNAME = "usuario"
$env:DB_PASSWORD = "senha"
$env:EMAILSENDER_HOST = "smtp.gmail.com"
$env:EMAILSENDER_PORT = "587"
$env:EMAILSENDER_USERNAME = "seu-email@gmail.com"
$env:EMAILSENDER_PASSWORD = "sua-senha-app"

mvn spring-boot:run
```

```bash
# Linux/Mac
export SPRING_PROFILES_ACTIVE=prod
export JWT_PRIVATE_KEY="-----BEGIN PRIVATE KEY-----\n..."
export DB_URL="jdbc:mysql://seu-host:3306/reserva"
export DB_USERNAME="usuario"
export DB_PASSWORD="senha"
export EMAILSENDER_HOST="smtp.gmail.com"
export EMAILSENDER_PORT="587"
export EMAILSENDER_USERNAME="seu-email@gmail.com"
export EMAILSENDER_PASSWORD="sua-senha-app"

mvn spring-boot:run
```

### 3.3 Executar via JAR

```bash
java -jar reserva-0.0.1-SNAPSHOT.jar
```

---

## 4. Validação de Deploy

Após fazer o deploy, verifique se está funcionando:

```bash
# Teste de health check
curl https://seu-api.com/actuator/health

# Verifique os logs
# Azure: Monitorar > Registros ao vivo
# AWS: CloudWatch > Logs
# Heroku: heroku logs --tail
```

---

## 5. Troubleshooting

### Erro: "Chave privada não configurada"
- Verifique se `JWT_PRIVATE_KEY` está definida como variável de ambiente
- Verifique se contém os headers `-----BEGIN PRIVATE KEY-----` e `-----END PRIVATE KEY-----`
- Verifique se as quebras de linha estão como `\n`

### Erro: "Unable to open JDBC Connection"
- Verifique se `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` estão corretos
- Verifique se a aplicação consegue conectar ao banco de dados (firewall, VPN, etc.)

### Erro: "No such file or directory: app.pub"
- Copie o arquivo `app.pub` para o classpath ou volume da aplicação
- Em Docker, adicione ao Dockerfile:
  ```dockerfile
  COPY src/main/resources/app.pub /app/
  ```

### Erro: "Invalid credentials"
- Verifique as credenciais de email
- Se usar Gmail, use [senha de app](https://myaccount.google.com/apppasswords), não a senha da conta

---

## 6. Checklist de Deploy

- [ ] JWT_PRIVATE_KEY está definida com a chave PEM completa
- [ ] SPRING_PROFILES_ACTIVE está definida como `prod`
- [ ] DB_URL, DB_USERNAME, DB_PASSWORD estão corretos
- [ ] EMAILSENDER_* está configurado corretamente
- [ ] app.pub está no classpath da aplicação
- [ ] Teste de health check retorna status `UP`
- [ ] Logs não mostram erros relacionados a chaves RSA ou banco de dados


