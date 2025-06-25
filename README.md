# Espaço Senai Backend

## API de Grade de Aulas

### Criar uma nova aula

**POST** `/grade-aula/salvar`

**Body da requisição:**
```json
{
    "dia": "QUARTA",
    "idDisciplina": 1,
    "idHorario": 1,
    "idPeriodo": 1,
    "idProfessor": 1,
    "sala": "Sala 201"
}
```

**Campos obrigatórios:**
- `dia`: Dia da semana (SEGUNDA, TERCA, QUARTA, QUINTA, SEXTA, SABADO, DOMINGO)
- `idDisciplina`: ID da disciplina
- `idHorario`: ID do horário
- `idPeriodo`: ID do período
- `idProfessor`: ID do professor (deve ter role PROFESSOR)
- `sala`: Nome da sala

**Resposta de sucesso (201):**
```json
"Aula salva com sucesso."
```

### Atualizar uma aula

**PUT** `/grade-aula/atualizar/{id}`

**Body da requisição:** (mesmo formato do criar)

### Excluir uma aula

**DELETE** `/grade-aula/excluir/{id}?idProfessor={idProfessor}`

**Parâmetros:**
- `id`: ID da aula a ser excluída
- `idProfessor`: ID do professor que está excluindo (deve ter role PROFESSOR)

### Listar todas as aulas

**GET** `/grade-aula/listar`

### Buscar aula por ID

**GET** `/grade-aula/listar/{id}`

**Resposta:**
```json
{
    "id": 1,
    "sala": "Sala 201",
    "professor": {
        "id": 1,
        "nome": "João Silva",
        "email": "joao@email.com"
    },
    "disciplina": {
        "id": 1,
        "disciplina": "Matemática"
    },
    "horario": {
        "id": 1,
        "inicio": "08:00",
        "fim": "09:00"
    },
    "periodo": {
        "id": 1,
        "periodo": "2024.1"
    },
    "dia": "QUARTA"
}
```