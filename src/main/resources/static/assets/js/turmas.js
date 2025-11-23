// Variável global para armazenar o perfil do usuário
let currentUserProfile = null;

// Função para verificar permissões
function hasPermission(...roles) {
    if (!currentUserProfile || !currentUserProfile.role) return false;
    return roles.includes(currentUserProfile.role);
}

// Função para mostrar a seção de turmas
window.showTurmas = async function() {
    try {
        // Obter perfil do usuário se ainda não tiver
        if (!currentUserProfile) {
            currentUserProfile = await getUserProfile();
        }

        hideAllSections();
        document.getElementById('turmas-section').style.display = 'block';

        // Mostrar botão de nova turma apenas para ADMIN e PROFESSOR
        const btnNovaTurma = document.getElementById('btn-nova-turma');
        if (hasPermission('ADMIN', 'PROFESSOR')) {
            btnNovaTurma.style.display = 'inline-block';
        } else {
            btnNovaTurma.style.display = 'none';
        }

        // Carregar turmas
        await loadTurmas();
    } catch (error) {
        console.error('Erro ao mostrar turmas:', error);
        alert('Erro ao carregar turmas');
    }
};

// Função para carregar lista de turmas
async function loadTurmas() {
    try {
        const turmas = await apiCall('/turma/buscar', 'GET');
        const content = document.getElementById('turmas-content');

        if (!turmas || turmas.length === 0) {
            content.innerHTML = '<p style="grid-column: 1 / -1; text-align: center;">Nenhuma turma encontrada.</p>';
            return;
        }

        content.innerHTML = turmas.map(turma => `
            <div class="turma-card">
                <h3>${turma.nome}</h3>
                <p><strong>Curso:</strong> ${turma.curso}</p>
                <p><strong>Modalidade:</strong> ${formatModalidade(turma.modalidade)}</p>
                <p><strong>Professor:</strong> ${turma.professor.nome}</p>
                <p><strong>Estudantes:</strong> ${turma.estudantesIds ? turma.estudantesIds.length : 0} / ${turma.capacidade}</p>
                <p><strong>Período:</strong> ${formatDate(turma.dataInicio)} a ${formatDate(turma.dataTermino)}</p>
                <p><strong>Código:</strong> <code>${turma.codigoAcesso}</code></p>
                <button class="btn-primary" onclick="showDetalhesTurma(${turma.id})">Ver Detalhes</button>
            </div>
        `).join('');
    } catch (error) {
        console.error('Erro ao carregar turmas:', error);
        document.getElementById('turmas-content').innerHTML = 
            '<p style="grid-column: 1 / -1; text-align: center; color: red;">Erro ao carregar turmas.</p>';
    }
}

// Função para mostrar formulário de nova turma
window.showNovaTurma = function() {
    hideAllSections();
    document.getElementById('form-turma-section').style.display = 'block';
    document.getElementById('form-turma-title').textContent = 'Nova Turma';
    document.getElementById('form-turma').reset();
    document.getElementById('turma-id').value = '';
};

// Função para mostrar formulário de editar turma
window.showEditarTurma = async function(turmaId) {
    try {
        const turma = await apiCall(`/turma/buscar/${turmaId}`, 'GET');
        
        hideAllSections();
        document.getElementById('form-turma-section').style.display = 'block';
        document.getElementById('form-turma-title').textContent = 'Editar Turma';
        
        document.getElementById('turma-id').value = turma.id;
        document.getElementById('turma-nome').value = turma.nome;
        document.getElementById('turma-curso').value = turma.curso;
        document.getElementById('turma-modalidade').value = turma.modalidade;
        document.getElementById('turma-capacidade').value = turma.capacidade;
        document.getElementById('turma-data-inicio').value = turma.dataInicio;
        document.getElementById('turma-data-termino').value = turma.dataTermino;
    } catch (error) {
        console.error('Erro ao carregar turma:', error);
        alert('Erro ao carregar dados da turma');
    }
};

// Função para mostrar detalhes da turma
window.showDetalhesTurma = async function(turmaId) {
    try {
        const turma = await apiCall(`/turma/buscar/${turmaId}`, 'GET');
        
        hideAllSections();
        document.getElementById('detalhes-turma-section').style.display = 'block';
        document.getElementById('detalhes-turma-title').textContent = turma.nome;

        const canEdit = hasPermission('ADMIN', 'PROFESSOR');
        const isProfessor = currentUserProfile && turma.professor.id === currentUserProfile.id;
        const canManage = hasPermission('ADMIN') || isProfessor;

        let html = `
            <div class="turma-detalhes">
                <div class="detalhes-info">
                    <h3>Informações da Turma</h3>
                    <p><strong>Nome:</strong> ${turma.nome}</p>
                    <p><strong>Curso:</strong> ${turma.curso}</p>
                    <p><strong>Modalidade:</strong> ${formatModalidade(turma.modalidade)}</p>
                    <p><strong>Professor:</strong> ${turma.professor.nome} (${turma.professor.email})</p>
                    <p><strong>Capacidade:</strong> ${turma.estudantesIds ? turma.estudantesIds.length : 0} / ${turma.capacidade}</p>
                    <p><strong>Período:</strong> ${formatDate(turma.dataInicio)} a ${formatDate(turma.dataTermino)}</p>
                    <p><strong>Código de Acesso:</strong> <code>${turma.codigoAcesso}</code></p>
                </div>
        `;

        if (canManage) {
            html += `
                <div class="detalhes-actions" style="margin: 20px 0; display: flex; gap: 10px; flex-wrap: wrap;">
                    <button class="btn-primary" onclick="showEditarTurma(${turma.id})">Editar Turma</button>
                    <button class="btn-primary" onclick="gerarNovoCodigo(${turma.id})">Gerar Novo Código</button>
                    <button class="btn-primary" onclick="showGerenciarEstudantes(${turma.id})">Gerenciar Estudantes</button>
                    <button class="btn-danger" onclick="deletarTurma(${turma.id})">Deletar Turma</button>
                </div>
            `;
        }

        // Lista de estudantes
        html += `
                <div class="detalhes-estudantes">
                    <h3>Estudantes (${turma.estudantesIds ? turma.estudantesIds.length : 0})</h3>
        `;

        if (turma.estudantesIds && turma.estudantesIds.length > 0) {
            html += '<ul class="estudantes-list">';
            turma.estudantesIds.forEach(estudante => {
                html += `
                    <li>
                        <span>${estudante.nome} - ${estudante.email}</span>
                        ${canManage ? `<button class="btn-danger btn-sm" onclick="removerEstudante(${turma.id}, ${estudante.id})">Remover</button>` : ''}
                    </li>
                `;
            });
            html += '</ul>';
        } else {
            html += '<p>Nenhum estudante inscrito ainda.</p>';
        }

        html += `
                </div>
            </div>
        `;

        document.getElementById('detalhes-turma-content').innerHTML = html;
    } catch (error) {
        console.error('Erro ao carregar detalhes da turma:', error);
        alert('Erro ao carregar detalhes da turma');
    }
};

// Função para mostrar formulário de ingressar em turma
window.showIngressarTurma = function() {
    hideAllSections();
    document.getElementById('ingressar-turma-section').style.display = 'block';
    document.getElementById('form-ingressar-turma').reset();
};

// Função para gerar novo código
window.gerarNovoCodigo = async function(turmaId) {
    if (!confirm('Deseja gerar um novo código de acesso? O código anterior será invalidado.')) {
        return;
    }

    try {
        await apiCall(`/turma/gerar-novo-codigo/${turmaId}`, 'GET');
        alert('Novo código gerado com sucesso!');
        showDetalhesTurma(turmaId);
    } catch (error) {
        console.error('Erro ao gerar novo código:', error);
        alert('Erro ao gerar novo código');
    }
};

// Função para deletar turma
window.deletarTurma = async function(turmaId) {
    if (!confirm('Tem certeza que deseja deletar esta turma? Esta ação não pode ser desfeita.')) {
        return;
    }

    try {
        await apiCall(`/turma/deletar/${turmaId}`, 'DELETE');
        alert('Turma deletada com sucesso!');
        showTurmas();
    } catch (error) {
        console.error('Erro ao deletar turma:', error);
        alert('Erro ao deletar turma');
    }
};

// Função para remover estudante
window.removerEstudante = async function(turmaId, estudanteId) {
    if (!confirm('Deseja remover este estudante da turma?')) {
        return;
    }

    try {
        await apiCall(`/turma/remover-estudantes/${turmaId}`, 'PATCH', [estudanteId]);
        alert('Estudante removido com sucesso!');
        showDetalhesTurma(turmaId);
    } catch (error) {
        console.error('Erro ao remover estudante:', error);
        alert('Erro ao remover estudante');
    }
};

// Função para mostrar gerenciamento de estudantes
window.showGerenciarEstudantes = async function(turmaId) {
    try {
        const turma = await apiCall(`/turma/buscar/${turmaId}`, 'GET');
        
        // Buscar todos os estudantes disponíveis
        const usuarios = await apiCall('/usuario/buscar', 'GET');
        const estudantes = usuarios.filter(u => u.role === 'ESTUDANTE');
        
        // Filtrar estudantes que não estão na turma
        const estudantesNaTurma = turma.estudantesIds ? turma.estudantesIds.map(e => e.id) : [];
        const estudantesDisponiveis = estudantes.filter(e => !estudantesNaTurma.includes(e.id));

        let html = `
            <div style="margin: 20px 0;">
                <h3>Adicionar Estudantes</h3>
                <form id="form-adicionar-estudantes">
                    <div class="form-row">
                        <label>Selecione os estudantes:</label>
                        <div style="max-height: 300px; overflow-y: auto; border: 1px solid #ddd; padding: 10px; border-radius: 4px;">
        `;

        if (estudantesDisponiveis.length > 0) {
            estudantesDisponiveis.forEach(estudante => {
                html += `
                    <div style="margin: 5px 0;">
                        <input type="checkbox" id="est-${estudante.id}" value="${estudante.id}">
                        <label for="est-${estudante.id}">${estudante.nome} - ${estudante.email}</label>
                    </div>
                `;
            });
        } else {
            html += '<p>Todos os estudantes já estão na turma ou não há estudantes disponíveis.</p>';
        }

        html += `
                        </div>
                    </div>
                    ${estudantesDisponiveis.length > 0 ? '<button type="submit" class="btn-primary">Adicionar Selecionados</button>' : ''}
                </form>
            </div>
        `;

        document.getElementById('detalhes-turma-content').innerHTML = html;

        if (estudantesDisponiveis.length > 0) {
            document.getElementById('form-adicionar-estudantes').addEventListener('submit', async (e) => {
                e.preventDefault();
                const checkboxes = document.querySelectorAll('input[type="checkbox"]:checked');
                const estudantesIds = Array.from(checkboxes).map(cb => parseInt(cb.value));

                if (estudantesIds.length === 0) {
                    alert('Selecione pelo menos um estudante');
                    return;
                }

                try {
                    await apiCall(`/turma/adicionar-estudantes/${turmaId}`, 'PATCH', estudantesIds);
                    alert('Estudantes adicionados com sucesso!');
                    showDetalhesTurma(turmaId);
                } catch (error) {
                    console.error('Erro ao adicionar estudantes:', error);
                    alert('Erro ao adicionar estudantes');
                }
            });
        }
    } catch (error) {
        console.error('Erro ao carregar estudantes:', error);
        alert('Erro ao carregar estudantes');
    }
};

// Handlers de formulários
document.addEventListener('DOMContentLoaded', () => {
    // Handler para form de turma
    const formTurma = document.getElementById('form-turma');
    if (formTurma) {
        formTurma.addEventListener('submit', async (e) => {
            e.preventDefault();

            const turmaId = document.getElementById('turma-id').value;
            const data = {
                nome: document.getElementById('turma-nome').value,
                curso: document.getElementById('turma-curso').value,
                modalidade: document.getElementById('turma-modalidade').value,
                capacidade: parseInt(document.getElementById('turma-capacidade').value),
                dataInicio: document.getElementById('turma-data-inicio').value,
                dataTermino: document.getElementById('turma-data-termino').value
            };

            // Validar datas
            if (new Date(data.dataInicio) > new Date(data.dataTermino)) {
                alert('A data de início não pode ser maior que a data de término');
                return;
            }

            try {
                if (turmaId) {
                    // Atualizar
                    await apiCall(`/turma/atualizar/${turmaId}`, 'PUT', data);
                    alert('Turma atualizada com sucesso!');
                } else {
                    // Criar
                    await apiCall('/turma/salvar', 'POST', data);
                    alert('Turma criada com sucesso!');
                }
                showTurmas();
            } catch (error) {
                console.error('Erro ao salvar turma:', error);
                alert('Erro ao salvar turma: ' + (error.message || 'Erro desconhecido'));
            }
        });
    }

    // Handler para form de ingressar em turma
    const formIngressar = document.getElementById('form-ingressar-turma');
    if (formIngressar) {
        formIngressar.addEventListener('submit', async (e) => {
            e.preventDefault();

            const codigo = document.getElementById('codigo-acesso').value.trim();

            if (!codigo) {
                alert('Digite o código de acesso');
                return;
            }

            try {
                await apiCall(`/turma/ingressar-por-codigo/${codigo}`, 'GET');
                alert('Você ingressou na turma com sucesso!');
                showTurmas();
            } catch (error) {
                console.error('Erro ao ingressar na turma:', error);
                alert('Erro ao ingressar na turma: Código inválido ou turma cheia');
            }
        });
    }
});

// Funções auxiliares de formatação
function formatDate(dateString) {
    if (!dateString) return '';
    const date = new Date(dateString + 'T00:00:00');
    return date.toLocaleDateString('pt-BR');
}

function formatModalidade(modalidade) {
    const modalidades = {
        'FIC': 'FIC',
        'TECNICO': 'Técnico',
        'FACULDADE': 'Faculdade'
    };
    return modalidades[modalidade] || modalidade;
}
