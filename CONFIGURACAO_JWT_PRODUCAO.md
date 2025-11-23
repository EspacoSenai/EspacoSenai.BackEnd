# Configuração JWT - Dev Local vs Produção

## 📋 Visão Geral

O sistema agora suporta carregar a chave privada RSA de duas formas diferentes:
- **Dev Local**: De arquivo (`app.key`)
- **Produção (Nuvem)**: De variável de ambiente (string PEM)

Não é necessário modificar o código entre ambientes!

## 🔧 Dev Local (Como está)

No `application-dev.properties`:
```properties
jwt.private.key=classpath:app.key
jwt.public.key=classpath:app.pub
```

Os arquivos `app.key` e `app.pub` ficam em `src/main/resources/`.

## ☁️ Produção (Nuvem)

### 1. Preparar a Chave Privada

Pegue o conteúdo do arquivo `app.key` (que está em formato PEM):

```
-----BEGIN PRIVATE KEY-----
MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQC...
(várias linhas)
...XYZ==
-----END PRIVATE KEY-----
```

### 2. Configurar Variável de Ambiente

Na sua plataforma de nuvem (Heroku, AWS, Azure, etc.), configure a variável de ambiente:

**Nome**: `JWT_PRIVATE_KEY`

**Valor**: Cole todo o conteúdo da chave privada (incluindo os cabeçalhos `-----BEGIN PRIVATE KEY-----` e `-----END PRIVATE KEY-----`)

#### Exemplos por plataforma:

**Heroku:**
```bash
heroku config:set JWT_PRIVATE_KEY="-----BEGIN PRIVATE KEY-----
MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQC...
...
-----END PRIVATE KEY-----"
```

**AWS Elastic Beanstalk:**
- Console AWS → Elastic Beanstalk → Environment → Configuration → Software
- Adicionar variável de ambiente `JWT_PRIVATE_KEY` com o valor da chave

**Azure App Service:**
- Portal Azure → App Service → Configuration → Application Settings
- New application setting: `JWT_PRIVATE_KEY` = (colar chave)

**Docker / Docker Compose:**
```yaml
environment:
  - JWT_PRIVATE_KEY=-----BEGIN PRIVATE KEY-----\nMIIEvgIBADANBgkqhkiG9w0...\n-----END PRIVATE KEY-----
```

### 3. Remover Configuração de Arquivo (Opcional)

No `application.properties` de produção, você pode:
- Remover a linha `jwt.private.key=classpath:app.key`
- Ou deixá-la com valor vazio: `jwt.private.key=`

O sistema tentará carregar de arquivo primeiro; se não encontrar, usará a variável de ambiente automaticamente.

### 4. Chave Pública

A chave pública (`jwt.public.key`) pode continuar como arquivo, pois ela pode ser exposta sem riscos de segurança. Se preferir, também pode ser movida para variável de ambiente seguindo o mesmo padrão.

## 🔐 Segurança

### ✅ Boas Práticas

- **Nunca** comite arquivos `.key` no Git (já deve estar no `.gitignore`)
- Use variáveis de ambiente para chaves privadas em produção
- Mantenha backups seguros das chaves
- Use HTTPS em produção para proteger os tokens em trânsito

### ⚠️ Importante

- A chave privada (`app.key`) é **sensível** → nunca exponha
- A chave pública (`app.pub`) pode ser compartilhada sem problemas
- Se a chave privada vazar, gere um novo par de chaves imediatamente

## 🧪 Como Testar

### Local
```bash
mvn spring-boot:run
```
Deve carregar de `classpath:app.key` automaticamente.

### Produção (Simulação Local)
```bash
export JWT_PRIVATE_KEY="-----BEGIN PRIVATE KEY-----
MIIEvgIBADA...
-----END PRIVATE KEY-----"

mvn spring-boot:run -Dspring.profiles.active=prod
```

## 🛠️ Troubleshooting

### Erro: "Chave privada não configurada"
- Verifique se `jwt.private.key` está configurado OU
- Verifique se a variável de ambiente `JWT_PRIVATE_KEY` está definida

### Erro: "Erro ao carregar chave privada RSA"
- Verifique o formato da chave (deve ser PKCS#8 PEM)
- Certifique-se de incluir `-----BEGIN PRIVATE KEY-----` e `-----END PRIVATE KEY-----`
- Verifique se não há caracteres extras ou quebras de linha incorretas

### Como gerar novas chaves (se necessário)
```bash
# Gerar chave privada
openssl genrsa -out app.key 2048

# Converter para PKCS#8 (formato esperado)
openssl pkcs8 -topk8 -inform PEM -outform PEM -in app.key -out app.key.pkcs8 -nocrypt

# Extrair chave pública
openssl rsa -in app.key -pubout -out app.pub

# Renomear
mv app.key.pkcs8 app.key
```

## 📚 Como Funciona Internamente

O `SecurityConfig` implementa a seguinte lógica:

1. Verifica se `jwt.private.key` está definido e começa com `classpath:`
   - Se sim → carrega de arquivo
   - Se não → vai para passo 2

2. Verifica se a variável de ambiente `JWT_PRIVATE_KEY` está definida
   - Se sim → converte string PEM para RSAPrivateKey
   - Se não → lança exceção

3. A conversão PEM para RSAPrivateKey:
   - Remove cabeçalhos (`-----BEGIN...`)
   - Remove quebras de linha
   - Decodifica Base64
   - Cria RSAPrivateKey usando KeyFactory

Isso permite deploy sem modificação de código!

