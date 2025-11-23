// API Base Configuration
const API_CONFIG = {
    baseUrl: '',
    headers: {
        'Content-Type': 'application/json'
    }
};

// Check authentication (não redireciona automaticamente)
function checkAuth() {
    // A autenticação real é feita via cookie JWT pelo backend
    // Esta função apenas verifica se há dados do usuário no localStorage
    const user = localStorage.getItem('user');
    return !!user;
}

// Logout function
async function logout() {
    try {
        // Call logout endpoint to clear cookie
        await fetch('/auth/logout', {
            method: 'POST',
            credentials: 'include'
        });
    } catch (error) {
        console.error('Logout error:', error);
    } finally {
        // Clear localStorage and redirect
        localStorage.removeItem('user');
        window.location.href = '/index.html';
    }
}

// Generic API call function
async function apiCall(endpoint, options = {}) {
    const defaultOptions = {
        headers: API_CONFIG.headers,
        credentials: 'include' // Include cookies
    };
    
    const finalOptions = {
        ...defaultOptions,
        ...options,
        headers: {
            ...defaultOptions.headers,
            ...options.headers
        }
    };
    
    try {
        const response = await fetch(`${API_CONFIG.baseUrl}${endpoint}`, finalOptions);
        
        // Handle unauthorized
        if (response.status === 401 || response.status === 403) {
            logout();
            return null;
        }
        
        return response;
    } catch (error) {
        console.error('API call error:', error);
        throw error;
    }
}

// Get user profile
async function getUserProfile() {
    try {
        const response = await apiCall('/usuario/meu-perfil', {
            method: 'GET'
        });
        
        if (response && response.ok) {
            return await response.json();
        }
        return null;
    } catch (error) {
        console.error('Error fetching user profile:', error);
        return null;
    }
}

// Export functions for use in other files
window.apiCall = apiCall;
window.checkAuth = checkAuth;
window.logout = logout;
window.getUserProfile = getUserProfile;
