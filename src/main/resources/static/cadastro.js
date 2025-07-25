document.getElementById('cadastroForm').addEventListener('submit', async function(e) {
    e.preventDefault();
    const form = e.target;
    const dados = {
        nome: form.nome.value,
        email: form.email.value,
        telefone: form.telefone.value,
        senha: form.senha.value
    };
    const resposta = await fetch('/api/usuarios/estudante', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(dados)
    });
    const mensagem = document.getElementById('mensagem');
    if (resposta.ok) {
        mensagem.innerText = 'Cadastro realizado! Verifique seu e-mail para confirmação.';
    } else {
        let erro = 'Não foi possível cadastrar.';
        try {
            const json = await resposta.json();
            erro = json.message || erro;
        } catch {}
        mensagem.innerText = 'Erro: ' + erro;
    }
});

