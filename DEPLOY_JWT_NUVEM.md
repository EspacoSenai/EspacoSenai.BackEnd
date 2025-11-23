# Como Configurar JWT_PRIVATE_KEY na Nuvem

## 🎯 Resumo Rápido

Para produção, basta configurar a variável de ambiente `JWT_PRIVATE_KEY` com o conteúdo do arquivo `app.key`.

## 📝 Passo a Passo

### 1. Obter o Conteúdo da Chave

Abra o arquivo `src/main/resources/app.key` e copie TODO o conteúdo, incluindo:
- `-----BEGIN PRIVATE KEY-----`
- Todo o conteúdo Base64 (várias linhas)
- `-----END PRIVATE KEY-----`

### 2. Configurar na Plataforma de Nuvem

#### Heroku
```bash
heroku config:set JWT_PRIVATE_KEY="$(cat src/main/resources/app.key)"
```

Ou pelo dashboard:
1. Settings → Config Vars
2. KEY: `JWT_PRIVATE_KEY`
3. VALUE: Cole o conteúdo completo da chave

#### AWS Elastic Beanstalk
```bash
# Via CLI
aws elasticbeanstalk update-environment \
  --environment-name seu-ambiente \
  --option-settings Namespace=aws:elasticbeanstalk:application:environment,OptionName=JWT_PRIVATE_KEY,Value="$(cat src/main/resources/app.key)"
```

Ou pelo console:
1. Configuration → Software → Environment properties
2. Name: `JWT_PRIVATE_KEY`
3. Value: Cole o conteúdo da chave

#### Azure App Service
Via Azure CLI:
```bash
az webapp config appsettings set \
  --name seu-app \
  --resource-group seu-grupo \
  --settings JWT_PRIVATE_KEY="$(cat src/main/resources/app.key)"
```

Ou pelo portal:
1. Configuration → Application settings → New application setting
2. Name: `JWT_PRIVATE_KEY`
3. Value: Cole o conteúdo da chave

#### Google Cloud Run
```bash
gcloud run services update seu-servico \
  --set-env-vars "JWT_PRIVATE_KEY=$(cat src/main/resources/app.key | base64)"
```

#### Docker
No `docker-compose.yml`:
```yaml
services:
  api:
    environment:
      JWT_PRIVATE_KEY: |
        -----BEGIN PRIVATE KEY-----
        MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQC...
        (copie todo o conteúdo aqui)
        -----END PRIVATE KEY-----
```

Ou via `.env` file:
```bash
JWT_PRIVATE_KEY="-----BEGIN PRIVATE KEY-----
MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQC...
-----END PRIVATE KEY-----"
```

### 3. Não Precisa Mudar o application.properties

O código já detecta automaticamente:
- **Local**: Usa `jwt.private.key=classpath:app.key`
- **Produção**: Usa variável de ambiente `JWT_PRIVATE_KEY`

## ✅ Como Testar Localmente (Simular Produção)

### Windows (PowerShell)
```powershell
$env:JWT_PRIVATE_KEY = Get-Content "src\main\resources\app.key" -Raw
.\mvnw.cmd spring-boot:run
```

### Windows (CMD)
```cmd
set JWT_PRIVATE_KEY=<cole aqui o conteúdo da chave>
mvnw.cmd spring-boot:run
```

### Linux/Mac
```bash
export JWT_PRIVATE_KEY="$(cat src/main/resources/app.key)"
./mvnw spring-boot:run
```

## 🔍 Verificar se Funcionou

Ao iniciar a aplicação, você deve ver no log:
- ✅ Sem erros relacionados a "Chave privada não configurada"
- ✅ Application started successfully

Se der erro:
- Verifique se a variável está definida: `echo $JWT_PRIVATE_KEY` (Linux/Mac) ou `echo %JWT_PRIVATE_KEY%` (Windows)
- Verifique se o conteúdo está completo (incluindo BEGIN e END)
- Verifique se não há caracteres extras

## 🔐 Segurança

- ✅ A chave privada está protegida em variável de ambiente (não no código)
- ✅ A chave privada NÃO vai para o Git (está no .gitignore)
- ✅ Cada ambiente (dev, staging, prod) pode ter sua própria chave
- ⚠️ NUNCA exponha a chave privada em logs ou respostas HTTP
- ⚠️ Use HTTPS em produção

## 📦 O Que Fazer no Deploy

1. Configurar `JWT_PRIVATE_KEY` na plataforma
2. Fazer deploy normalmente
3. Pronto! O app detecta automaticamente

**Não precisa**:
- ❌ Modificar código
- ❌ Criar profiles diferentes
- ❌ Incluir arquivo .key no JAR de produção
- ❌ Fazer deploy do arquivo app.key

## 🆘 Troubleshooting

### Erro: "Chave privada não configurada"
→ A variável de ambiente não foi definida corretamente

### Erro: "InvalidKeySpecException"
→ O formato da chave está incorreto. Verifique se é PKCS#8 (deve começar com `-----BEGIN PRIVATE KEY-----`)

### Como converter chave se necessário:
```bash
openssl pkcs8 -topk8 -inform PEM -outform PEM -in app.key -out app.key.new -nocrypt
mv app.key.new app.key
```

## 💡 Dica Extra

Para maior segurança em produção, considere usar serviços de gerenciamento de segredos:
- AWS Secrets Manager
- Azure Key Vault
- Google Secret Manager
- HashiCorp Vault

Mas a solução com variável de ambiente já é segura para a maioria dos casos!

