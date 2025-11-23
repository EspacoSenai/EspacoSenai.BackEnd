// Ambientes e Reservas Management

// API endpoints
const AMBIENTE_API = '/ambiente';
const RESERVA_API = '/reserva';

// Hide all content sections
function hideAllSections() {
    document.querySelectorAll('.content-section').forEach(section => {
        section.style.display = 'none';
    });
    document.querySelector('.dashboard-cards').style.display = 'grid';
}

// Show Minhas Reservas
async function showMinhasReservas() {
    hideAllSections();
    const section = document.getElementById('reservas-section');
    const content = document.getElementById('reservas-content');
    
    section.style.display = 'block';
    document.querySelector('.dashboard-cards').style.display = 'none';
    content.innerHTML = '<p>Carregando reservas...</p>';
    
    try {
        const response = await apiCall(`${RESERVA_API}/minhas-reservas`, {
            method: 'GET'
        });
        
        if (response && response.ok) {
            const reservas = await response.json();
            
            if (reservas.length === 0) {
                content.innerHTML = '<p>Você ainda não tem reservas.</p>';
            } else {
                content.innerHTML = reservas.map(reserva => `
                    <div class="reserva-card">
                        <h4>Reserva #${reserva.id}</h4>
                        <p><strong>Data:</strong> ${formatDate(reserva.data)}</p>
                        <p><strong>Horário:</strong> ${reserva.horaInicio} - ${reserva.horaFim}</p>
                        <p><strong>Status:</strong> <span class="status-badge status-${reserva.statusReserva.toLowerCase()}">${formatStatus(reserva.statusReserva)}</span></p>
                        ${reserva.host ? `<p><strong>Criada por:</strong> ${reserva.host.nome}</p>` : ''}
                        ${reserva.msgUsuario ? `<p><strong>Mensagem:</strong> ${reserva.msgUsuario}</p>` : ''}
                        ${reserva.membros && reserva.membros.length > 0 ? `<p><strong>Participantes:</strong> ${reserva.membros.length}</p>` : ''}
                    </div>
                `).join('');
            }
        } else {
            content.innerHTML = '<p>Erro ao carregar reservas.</p>';
        }
    } catch (error) {
        console.error('Error loading reservas:', error);
        content.innerHTML = '<p>Erro ao carregar reservas.</p>';
    }
}

// Show Ambientes
async function showAmbientes() {
    hideAllSections();
    const section = document.getElementById('ambientes-section');
    const content = document.getElementById('ambientes-content');
    
    section.style.display = 'block';
    document.querySelector('.dashboard-cards').style.display = 'none';
    content.innerHTML = '<p>Carregando ambientes...</p>';
    
    try {
        const response = await apiCall(`${AMBIENTE_API}/buscar`, {
            method: 'GET'
        });
        
        if (response && response.ok) {
            const ambientes = await response.json();
            
            if (ambientes.length === 0) {
                content.innerHTML = '<p>Nenhum ambiente disponível no momento.</p>';
            } else {
                content.innerHTML = ambientes.map(ambiente => {
                    const catalogosCount = ambiente.catalogos ? ambiente.catalogos.length : 0;
                    const catalogosDisponiveis = ambiente.catalogos ? 
                        ambiente.catalogos.filter(c => c.disponibilidade === 'DISPONIVEL').length : 0;
                    
                    return `
                        <div class="ambiente-card">
                            <h4>${ambiente.nome}</h4>
                            <p><strong>Disponibilidade:</strong> 
                                <span class="status-badge status-${ambiente.disponibilidade.toLowerCase()}">
                                    ${formatDisponibilidade(ambiente.disponibilidade)}
                                </span>
                            </p>
                            <p><strong>Aprovação:</strong> ${formatAprovacao(ambiente.aprovacao)}</p>
                            <p><strong>Horários:</strong> ${catalogosDisponiveis} disponível(is) de ${catalogosCount} total</p>
                            ${ambiente.responsavel ? `<p><strong>Responsável:</strong> ${ambiente.responsavel.nome}</p>` : ''}
                        </div>
                    `;
                }).join('');
            }
        } else {
            content.innerHTML = '<p>Erro ao carregar ambientes.</p>';
        }
    } catch (error) {
        console.error('Error loading ambientes:', error);
        content.innerHTML = '<p>Erro ao carregar ambientes.</p>';
    }
}

// Show Nova Reserva
async function showNovaReserva() {
    hideAllSections();
    const section = document.getElementById('nova-reserva-section');
    
    section.style.display = 'block';
    document.querySelector('.dashboard-cards').style.display = 'none';
    
    // Set min date to today
    const today = new Date().toISOString().split('T')[0];
    document.getElementById('reserva-data').min = today;
    
    // Load ambientes
    await loadAmbientesSelect();
    
    // Clear form
    document.getElementById('nova-reserva-form').reset();
    document.getElementById('reserva-catalogo').disabled = true;
    document.getElementById('reserva-catalogo').innerHTML = '<option value="">Selecione um ambiente primeiro</option>';
}

// Load ambientes for select
async function loadAmbientesSelect() {
    const select = document.getElementById('reserva-ambiente');
    
    try {
        const response = await apiCall(`${AMBIENTE_API}/buscar`, {
            method: 'GET'
        });
        
        if (response && response.ok) {
            const ambientes = await response.json();
            
            select.innerHTML = '<option value="">Selecione um ambiente</option>' +
                ambientes
                    .filter(amb => amb.disponibilidade === 'DISPONIVEL')
                    .map(amb => `<option value="${amb.id}">${amb.nome}</option>`)
                    .join('');
        }
    } catch (error) {
        console.error('Error loading ambientes:', error);
        select.innerHTML = '<option value="">Erro ao carregar ambientes</option>';
    }
}

// Load catalogos when ambiente is selected
async function loadCatalogos() {
    const ambienteId = document.getElementById('reserva-ambiente').value;
    const catalogoSelect = document.getElementById('reserva-catalogo');
    
    if (!ambienteId) {
        catalogoSelect.disabled = true;
        catalogoSelect.innerHTML = '<option value="">Selecione um ambiente primeiro</option>';
        return;
    }
    
    catalogoSelect.disabled = true;
    catalogoSelect.innerHTML = '<option value="">Carregando horários...</option>';
    
    try {
        const response = await apiCall(`${AMBIENTE_API}/buscar/${ambienteId}`, {
            method: 'GET'
        });
        
        if (response && response.ok) {
            const ambiente = await response.json();
            
            if (ambiente.catalogos && ambiente.catalogos.length > 0) {
                const catalogosDisponiveis = ambiente.catalogos.filter(cat => 
                    cat.disponibilidade === 'DISPONIVEL'
                );
                
                if (catalogosDisponiveis.length > 0) {
                    catalogoSelect.innerHTML = '<option value="">Selecione um horário</option>' +
                        catalogosDisponiveis.map(cat => 
                            `<option value="${cat.id}">
                                ${formatDiaSemana(cat.diaSemana)} - ${cat.horaInicio} às ${cat.horaFim}
                            </option>`
                        ).join('');
                    catalogoSelect.disabled = false;
                } else {
                    catalogoSelect.innerHTML = '<option value="">Nenhum horário disponível</option>';
                }
            } else {
                catalogoSelect.innerHTML = '<option value="">Nenhum horário cadastrado</option>';
            }
        }
    } catch (error) {
        console.error('Error loading catalogos:', error);
        catalogoSelect.innerHTML = '<option value="">Erro ao carregar horários</option>';
    }
}

// Handle Nova Reserva Form
document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('nova-reserva-form');
    if (form) {
        form.addEventListener('submit', async (e) => {
            e.preventDefault();
            clearReservaMessages();
            
            const formData = {
                catalogoId: parseInt(document.getElementById('reserva-catalogo').value),
                data: document.getElementById('reserva-data').value,
                horaInicio: document.getElementById('reserva-hora-inicio').value,
                horaFim: document.getElementById('reserva-hora-fim').value,
                msgUsuario: document.getElementById('reserva-mensagem').value || null
            };
            
            // Validate
            if (!formData.catalogoId) {
                showReservaError('Selecione um horário disponível.');
                return;
            }
            
            const button = e.target.querySelector('button[type="submit"]');
            button.disabled = true;
            button.classList.add('loading');
            
            try {
                const response = await apiCall(`${RESERVA_API}/salvar`, {
                    method: 'POST',
                    body: JSON.stringify(formData)
                });
                
                if (response && response.ok) {
                    const data = await response.json();
                    showReservaSuccess(data.message || 'Reserva criada com sucesso! Aguarde aprovação.');
                    
                    // Clear form
                    setTimeout(() => {
                        form.reset();
                        document.getElementById('reserva-catalogo').disabled = true;
                        showMinhasReservas();
                    }, 2000);
                } else {
                    const error = await response.json();
                    showReservaError(error.message || 'Erro ao criar reserva. Tente novamente.');
                }
            } catch (error) {
                console.error('Error creating reserva:', error);
                showReservaError('Erro de conexão. Tente novamente mais tarde.');
            } finally {
                button.disabled = false;
                button.classList.remove('loading');
            }
        });
    }
});

// Clear reserva messages
function clearReservaMessages() {
    const errorMsg = document.getElementById('reserva-error');
    const successMsg = document.getElementById('reserva-success');
    
    if (errorMsg) {
        errorMsg.classList.remove('show');
        errorMsg.textContent = '';
    }
    if (successMsg) {
        successMsg.classList.remove('show');
        successMsg.textContent = '';
    }
}

// Show reserva error
function showReservaError(message) {
    const element = document.getElementById('reserva-error');
    if (element) {
        element.textContent = message;
        element.classList.add('show');
    }
}

// Show reserva success
function showReservaSuccess(message) {
    const element = document.getElementById('reserva-success');
    if (element) {
        element.textContent = message;
        element.classList.add('show');
    }
}

// Format helpers
function formatDate(dateString) {
    if (!dateString) return 'N/A';
    const date = new Date(dateString);
    return date.toLocaleDateString('pt-BR');
}

function formatStatus(status) {
    const statusMap = {
        'PENDENTE': 'Pendente',
        'APROVADA': 'Aprovada',
        'REJEITADA': 'Rejeitada',
        'CANCELADA': 'Cancelada',
        'CONCLUIDA': 'Concluída'
    };
    return statusMap[status] || status;
}

function formatDisponibilidade(disponibilidade) {
    const map = {
        'DISPONIVEL': 'Disponível',
        'INDISPONIVEL': 'Indisponível'
    };
    return map[disponibilidade] || disponibilidade;
}

function formatAprovacao(aprovacao) {
    const map = {
        'AUTOMATICA': 'Automática',
        'MANUAL': 'Manual'
    };
    return map[aprovacao] || aprovacao;
}

function formatDiaSemana(dia) {
    const map = {
        'SEGUNDA': 'Segunda-feira',
        'TERCA': 'Terça-feira',
        'QUARTA': 'Quarta-feira',
        'QUINTA': 'Quinta-feira',
        'SEXTA': 'Sexta-feira',
        'SABADO': 'Sábado',
        'DOMINGO': 'Domingo'
    };
    return map[dia] || dia;
}

// Export functions
window.showMinhasReservas = showMinhasReservas;
window.showAmbientes = showAmbientes;
window.showNovaReserva = showNovaReserva;
window.loadCatalogos = loadCatalogos;
window.hideAllSections = hideAllSections;
