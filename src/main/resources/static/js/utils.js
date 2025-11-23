// Utility Functions

// Format date
function formatDate(dateString) {
    if (!dateString) return 'N/A';
    const date = new Date(dateString);
    return date.toLocaleDateString('pt-BR', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
    });
}

// Format status
function formatStatus(status) {
    const statusMap = {
        'ATIVO': 'Ativo',
        'INATIVO': 'Inativo',
        'PENDENTE': 'Pendente',
        'BLOQUEADO': 'Bloqueado'
    };
    return statusMap[status] || status;
}

// Validate email
function validateEmail(email) {
    const regex = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/;
    return regex.test(email);
}

// Validate password
function validatePassword(password) {
    return password.length >= 8 && password.length <= 15;
}

// Show loading spinner
function showLoading(elementId) {
    const element = document.getElementById(elementId);
    if (element) {
        element.innerHTML = '<div class="loading-spinner"></div>';
    }
}

// Hide loading spinner
function hideLoading(elementId) {
    const element = document.getElementById(elementId);
    if (element) {
        element.innerHTML = '';
    }
}

// Debounce function
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

// Copy to clipboard
async function copyToClipboard(text) {
    try {
        await navigator.clipboard.writeText(text);
        return true;
    } catch (err) {
        console.error('Failed to copy:', err);
        return false;
    }
}

// Generate random ID
function generateId() {
    return Math.random().toString(36).substr(2, 9);
}

// Format currency (if needed in future)
function formatCurrency(value) {
    return new Intl.NumberFormat('pt-BR', {
        style: 'currency',
        currency: 'BRL'
    }).format(value);
}

// Export functions
window.utils = {
    formatDate,
    formatStatus,
    validateEmail,
    validatePassword,
    showLoading,
    hideLoading,
    debounce,
    copyToClipboard,
    generateId,
    formatCurrency
};
