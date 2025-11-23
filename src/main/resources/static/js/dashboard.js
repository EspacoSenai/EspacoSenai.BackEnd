// Load user info on dashboard
async function loadUserInfo() {
    try {
        const profile = await getUserProfile();
        
        if (profile) {
            const userName = document.getElementById('user-name');
            if (userName) {
                userName.textContent = profile.nome || 'Usuário';
            }
            
            // Controlar visibilidade do card de Turmas
            // Turmas devem ser visíveis para todos, mas com funcionalidades diferentes
            // Estudantes só podem ver turmas e ingressar por código
            // O controle de permissões específicas é feito dentro de turmas.js
            
        } else {
            // Se não conseguir carregar o perfil, redireciona para login
            window.location.href = '/index.html';
        }
    } catch (error) {
        console.error('Error loading user info:', error);
        // Em caso de erro (não autenticado), redireciona para login
        window.location.href = '/index.html';
    }
}

// Load and display user profile
async function loadProfile() {
    // Hide other sections
    document.querySelectorAll('.content-section').forEach(section => {
        section.style.display = 'none';
    });
    document.querySelector('.dashboard-cards').style.display = 'none';
    
    const profileSection = document.getElementById('profile-section');
    const profileContent = document.getElementById('profile-content');
    
    profileSection.style.display = 'block';
    profileContent.innerHTML = '<p>Carregando informações...</p>';
    
    try {
        const profile = await getUserProfile();
        
        if (profile) {
            profileContent.innerHTML = `
                <p><strong>ID:</strong> ${profile.id}</p>
                <p><strong>Nome:</strong> ${profile.nome}</p>
                <p><strong>Email:</strong> ${profile.email}</p>
                <p><strong>Status:</strong> ${profile.status}</p>
                <p><strong>Roles:</strong> ${profile.rolesIds ? profile.rolesIds.join(', ') : 'N/A'}</p>
                <button class="btn btn-secondary" onclick="hideAllSections()" style="margin-top: 20px;">Voltar</button>
            `;
        } else {
            profileContent.innerHTML = '<p>Erro ao carregar perfil.</p>';
        }
    } catch (error) {
        console.error('Error loading profile:', error);
        profileContent.innerHTML = '<p>Erro ao carregar perfil.</p>';
    }
}

// Initialize dashboard
window.addEventListener('DOMContentLoaded', () => {
    loadUserInfo();
});

// Make loadProfile available globally
window.loadProfile = loadProfile;
