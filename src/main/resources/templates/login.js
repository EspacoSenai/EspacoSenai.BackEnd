document.getElementById('loginForm').addEventListener('submit', async function(e) {
    e.preventDefault();
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;
    try {
        // Autenticação (ajuste o endpoint conforme necessário)
        const res = await fetch('/api/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });
        if (!res.ok) throw new Error('Usuário ou senha inválidos');
        const data = await res.json();
        if (data.role === 'ADMIN') {
            document.querySelector('.login-container').style.display = 'none';
            document.getElementById('adminPanel').style.display = 'block';
            loadAmbientes(data.token);
        } else {
            document.getElementById('loginError').innerText = 'Acesso restrito a administradores.';
            document.getElementById('loginError').style.display = 'block';
        }
    } catch (err) {
        document.getElementById('loginError').innerText = err.message;
        document.getElementById('loginError').style.display = 'block';
    }
});

async function loadAmbientes(token) {
    const ambientesRes = await fetch('/api/ambientes', {
        headers: { 'Authorization': 'Bearer ' + token }
    });
    const ambientes = await ambientesRes.json();
    const container = document.getElementById('ambientesContainer');
    container.innerHTML = '';
    for (const ambiente of ambientes) {
        const div = document.createElement('div');
        div.className = 'ambiente';
        div.innerHTML = `<h3>${ambiente.nome}</h3><div id="catalogos-${ambiente.id}"></div>`;
        container.appendChild(div);
        loadCatalogos(ambiente.id, token);
        // CRUD botões para ambiente
        div.innerHTML += `<button onclick="editAmbiente(${ambiente.id}, '${token}')">Editar</button> <button onclick="deleteAmbiente(${ambiente.id}, '${token}')">Excluir</button>`;
    }
    // Botão para criar novo ambiente
    container.innerHTML += `<button onclick="createAmbiente('${token}')">Novo Ambiente</button>`;
}

async function loadCatalogos(ambienteId, token) {
    const res = await fetch(`/api/catalogos/ambiente/${ambienteId}`, {
        headers: { 'Authorization': 'Bearer ' + token }
    });
    const catalogos = await res.json();
    const div = document.getElementById(`catalogos-${ambienteId}`);
    div.innerHTML = '';
    for (const catalogo of catalogos) {
        div.innerHTML += `<div>${catalogo.nome} <button onclick="editCatalogo(${catalogo.id}, '${token}')">Editar</button> <button onclick="deleteCatalogo(${catalogo.id}, '${token}')">Excluir</button></div>`;
    }
    div.innerHTML += `<button onclick="createCatalogo(${ambienteId}, '${token}')">Novo Catálogo</button>`;
}

// Funções CRUD para ambientes
async function editAmbiente(id, token) {
    const nome = prompt('Novo nome do ambiente:');
    if (!nome) return;
    await fetch(`/api/ambientes/${id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json', 'Authorization': 'Bearer ' + token },
        body: JSON.stringify({ nome })
    });
    loadAmbientes(token);
}
async function deleteAmbiente(id, token) {
    if (!confirm('Excluir ambiente?')) return;
    await fetch(`/api/ambientes/${id}`, {
        method: 'DELETE',
        headers: { 'Authorization': 'Bearer ' + token }
    });
    loadAmbientes(token);
}
async function createAmbiente(token) {
    const nome = prompt('Nome do novo ambiente:');
    if (!nome) return;
    await fetch('/api/ambientes', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json', 'Authorization': 'Bearer ' + token },
        body: JSON.stringify({ nome })
    });
    loadAmbientes(token);
}
// Funções CRUD para catálogos
async function editCatalogo(id, token) {
    const nome = prompt('Novo nome do catálogo:');
    if (!nome) return;
    await fetch(`/api/catalogos/${id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json', 'Authorization': 'Bearer ' + token },
        body: JSON.stringify({ nome })
    });
    // Recarregar ambientes para atualizar catálogos
    loadAmbientes(token);
}
async function deleteCatalogo(id, token) {
    if (!confirm('Excluir catálogo?')) return;
    await fetch(`/api/catalogos/${id}`, {
        method: 'DELETE',
        headers: { 'Authorization': 'Bearer ' + token }
    });
    loadAmbientes(token);
}
async function createCatalogo(ambienteId, token) {
    const nome = prompt('Nome do novo catálogo:');
    if (!nome) return;
    await fetch('/api/catalogos', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json', 'Authorization': 'Bearer ' + token },
        body: JSON.stringify({ nome, ambienteId })
    });
    loadAmbientes(token);
}
