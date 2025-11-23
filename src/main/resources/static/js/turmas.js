// Turmas Management

// Variável global para armazenar o perfil do usuário
let userProfile = null;

// Função para verificar permissões
function hasPermission(...requiredRoles) {
    if (!userProfile || !userProfile.roles || userProfile.roles.length === 0) return false;
    
    // userProfile.roles é um Set/Array de objetos Role
    // Cada Role tem um campo roleNome (ex: "ADMIN", "PROFESSOR", "ESTUDANTE")
    const userRoles = userProfile.roles.map(r => r.roleNome);
    
    return requiredRoles.some(role => userRoles.includes(role));
}

// Função para mostrar a seção de turmas
window.showTurmas = async function() {
    try {
        console.log('showTurmas chamado');
        
        // Obter perfil do usuário se ainda não tiver
        if (!userProfile) {
            console.log('Carregando perfil do usuário...');
            userProfile = await getUserProfile();
            console.log('Perfil carregado:', userProfile);
        }

        hideAllSections();
        const section = document.getElementById('turmas-section');
        section.style.display = 'block';
        document.querySelector('.dashboard-cards').style.display = 'none';

        // Mostrar botão de nova turma apenas para ADMIN e PROFESSOR
        const btnNovaTurma = document.getElementById('btn-nova-turma');
        const canCreate = hasPermission('ADMIN', 'PROFESSOR');
        console.log('Pode criar turma?', canCreate, 'Roles:', userProfile?.roles);
        
        if (canCreate) {
            btnNovaTurma.style.display = 'inline-block';
        } else {
            btnNovaTurma.style.display = 'none';
        }

        // Carregar turmas
        await loadTurmas();
    } catch (error) {
        console.error('Erro ao mostrar turmas:', error);
        alert('Erro ao carregar turmas: ' + error.message);
    }
};

// Função para carregar lista de turmas
async function loadTurmas() {
    const content = document.getElementById('turmas-content');
    content.innerHTML = '<p>Carregando turmas...</p>';
    
    try {
        console.log('Buscando turmas...');
        const response = await apiCall('/usuario/minhas-turmas', {
            method: 'GET'
        });

        console.log('Response:', response);

        if (!response) {
            throw new Error('Sem resposta do servidor');
        }

        if (!response.ok) {
            let errorMessage = 'Erro desconhecido';
            try {
                const errorData = await response.json();
                errorMessage = errorData.message || errorData.error || errorMessage;
            } catch (e) {
                errorMessage = await response.text() || `Status ${response.status}`;
            }
            console.error('Erro na resposta:', errorMessage);
            throw new Error(errorMessage);
        }

        let turmas = await response.json();
        console.log('Turmas recebidas:', turmas);

        // Converter Set para Array se necessário
        if (!Array.isArray(turmas)) {
            turmas = Array.from(turmas || []);
        }

        if (!turmas || turmas.length === 0) {
            content.innerHTML = `
                <div style="grid-column: 1 / -1; text-align: center; padding: 40px;">
                    <p style="font-size: 1.2em; margin-bottom: 20px;">📚 Nenhuma turma encontrada</p>
                    <p style="color: #666;">
                        ${hasPermission('ADMIN', 'PROFESSOR') ? 
                            'Clique em "Nova Turma" para criar a primeira turma.' : 
                            'Aguarde até que turmas sejam criadas ou use um código para ingressar.'}
                    </p>
                </div>
            `;
            return;
        }

        content.innerHTML = turmas.map(turma => `
            <div class="turma-card">
                <h3>${turma.nome}</h3>
                <p><strong>Curso:</strong> ${turma.curso}</p>
                <p><strong>Modalidade:</strong> ${formatModalidade(turma.modalidade)}</p>
                <p><strong>Professor:</strong> ${turma.professor ? turma.professor.nome : 'N/A'}</p>
                <p><strong>Estudantes:</strong> ${turma.estudantesIds ? turma.estudantesIds.length : 0} / ${turma.capacidade}</p>
                <p><strong>Período:</strong> ${formatDate(turma.dataInicio)} a ${formatDate(turma.dataTermino)}</p>
                <p><strong>Código:</strong> <code>${turma.codigoAcesso}</code></p>
                <button class="btn btn-primary" onclick="showDetalhesTurma(${turma.id})">Ver Detalhes</button>
            </div>
        `).join('');
    } catch (error) {
        console.error('Erro ao carregar turmas:', error);
        content.innerHTML = 
            `<div style="grid-column: 1 / -1; text-align: center; padding: 40px;">
                <p style="color: red; font-size: 1.1em; margin-bottom: 10px;">⚠️ Erro ao carregar turmas</p>
                <p style="color: #666;">${error.message}</p>
                <button class="btn btn-secondary" onclick="loadTurmas()" style="margin-top: 20px; max-width: 200px; margin-left: auto; margin-right: auto;">Tentar Novamente</button>
            </div>`;
    }
}

// Função para mostrar formulário de nova turma
window.showNovaTurma = function() {
    hideAllSections();
    const section = document.getElementById('form-turma-section');
    section.style.display = 'block';
    document.querySelector('.dashboard-cards').style.display = 'none';
    document.getElementById('form-turma-title').textContent = 'Nova Turma';
    document.getElementById('form-turma').reset();
    document.getElementById('turma-id').value = '';
};

// Função para mostrar formulário de editar turma
window.showEditarTurma = async function(turmaId) {
    try {
        const response = await apiCall(`/turma/buscar/${turmaId}`, {
            method: 'GET'
        });

        if (!response || !response.ok) {
            throw new Error('Erro ao buscar turma');
        }

        const turma = await response.json();
        
        hideAllSections();
        const section = document.getElementById('form-turma-section');
        section.style.display = 'block';
        document.querySelector('.dashboard-cards').style.display = 'none';
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
        const response = await apiCall(`/turma/buscar/${turmaId}`, {
            method: 'GET'
        });

        if (!response || !response.ok) {
            throw new Error('Erro ao buscar turma');
        }

        const turma = await response.json();
        
        hideAllSections();
        const section = document.getElementById('detalhes-turma-section');
        section.style.display = 'block';
        document.querySelector('.dashboard-cards').style.display = 'none';
        document.getElementById('detalhes-turma-title').textContent = turma.nome;

        const canEdit = hasPermission('ADMIN', 'PROFESSOR');
        const isProfessor = userProfile && turma.professor && turma.professor.id === userProfile.id;
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
                    <button class="btn btn-primary" onclick="showEditarTurma(${turma.id})">Editar Turma</button>
                    <button class="btn btn-primary" onclick="gerarNovoCodigo(${turma.id})">Gerar Novo Código</button>
                    <button class="btn btn-primary" onclick="showGerenciarEstudantes(${turma.id})">Gerenciar Estudantes</button>
                    <button class="btn btn-danger" onclick="deletarTurma(${turma.id})">Deletar Turma</button>
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
                        ${canManage ? `<button class="btn btn-danger btn-sm" onclick="removerEstudante(${turma.id}, ${estudante.id})">Remover</button>` : ''}
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
    const section = document.getElementById('ingressar-turma-section');
    section.style.display = 'block';
    document.querySelector('.dashboard-cards').style.display = 'none';
    document.getElementById('form-ingressar-turma').reset();
};

// Função para gerar novo código
window.gerarNovoCodigo = async function(turmaId) {
    if (!confirm('Deseja gerar um novo código de acesso? O código anterior será invalidado.')) {
        return;
    }

    try {
        const response = await apiCall(`/turma/gerar-novo-codigo/${turmaId}`, {
            method: 'GET'
        });

        if (!response || !response.ok) {
            throw new Error('Erro ao gerar novo código');
        }

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
        const response = await apiCall(`/turma/deletar/${turmaId}`, {
            method: 'DELETE'
        });

        if (!response || !response.ok) {
            throw new Error('Erro ao deletar turma');
        }

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
        const response = await apiCall(`/turma/remover-estudantes/${turmaId}`, {
            method: 'PATCH',
            body: JSON.stringify([estudanteId])
        });

        if (!response || !response.ok) {
            throw new Error('Erro ao remover estudante');
        }

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
        const turmaResponse = await apiCall(`/turma/buscar/${turmaId}`, {
            method: 'GET'
        });

        if (!turmaResponse || !turmaResponse.ok) {
            throw new Error('Erro ao buscar turma');
        }

        const turma = await turmaResponse.json();
        
        // Buscar todos os estudantes disponíveis
        const estudantesResponse = await apiCall('/usuario/buscar-estudantes', {
            method: 'GET'
        });

        if (!estudantesResponse || !estudantesResponse.ok) {
            throw new Error('Erro ao buscar estudantes');
        }

        const estudantes = await estudantesResponse.json();
        
        // Filtrar estudantes que não estão na turma
        const estudantesNaTurma = turma.estudantesIds ? turma.estudantesIds.map(e => e.id) : [];
        const estudantesDisponiveis = estudantes.filter(e => !estudantesNaTurma.includes(e.id));

        let html = `
            <div style="margin: 20px 0;">
                <h3>Adicionar Estudantes</h3>
                <form id="form-adicionar-estudantes">
                    <div class="form-group">
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
                    ${estudantesDisponiveis.length > 0 ? '<button type="submit" class="btn btn-primary">Adicionar Selecionados</button>' : ''}
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
                    const response = await apiCall(`/turma/adicionar-estudantes/${turmaId}`, {
                        method: 'PATCH',
                        body: JSON.stringify(estudantesIds)
                    });

                    if (!response || !response.ok) {
                        throw new Error('Erro ao adicionar estudantes');
                    }

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
                let response;
                if (turmaId) {
                    // Atualizar
                    response = await apiCall(`/turma/atualizar/${turmaId}`, {
                        method: 'PUT',
                        body: JSON.stringify(data)
                    });
                } else {
                    // Criar
                    response = await apiCall('/turma/salvar', {
                        method: 'POST',
                        body: JSON.stringify(data)
                    });
                }

                if (!response || !response.ok) {
                    throw new Error('Erro ao salvar turma');
                }

                alert(turmaId ? 'Turma atualizada com sucesso!' : 'Turma criada com sucesso!');
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
                const response = await apiCall(`/turma/ingressar-por-codigo/${codigo}`, {
                    method: 'GET'
                });

                if (!response || !response.ok) {
                    throw new Error('Código inválido');
                }

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
