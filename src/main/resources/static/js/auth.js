// API Base URL
const API_BASE_URL = '/auth';

// State management
let signupToken = null;
let resetToken = null;

// Toggle between login and signup forms
function toggleForms(event) {
    event.preventDefault();
    const loginForm = document.getElementById('loginForm');
    const signupForm = document.getElementById('signupForm');
    
    loginForm.classList.toggle('active');
    signupForm.classList.toggle('active');
    
    // Clear error messages
    clearMessages();
}

// Show forgot password form
function showForgotPassword(event) {
    event.preventDefault();
    hideAllForms();
    document.getElementById('forgotPasswordForm').classList.add('active');
    clearMessages();
}

// Back to login
function backToLogin(event) {
    event.preventDefault();
    hideAllForms();
    document.getElementById('loginForm').classList.add('active');
    clearMessages();
}

// Back to signup
function backToSignup(event) {
    event.preventDefault();
    hideAllForms();
    document.getElementById('signupForm').classList.add('active');
    clearMessages();
}

// Hide all forms
function hideAllForms() {
    const forms = document.querySelectorAll('.form-wrapper');
    forms.forEach(form => form.classList.remove('active'));
}

// Clear all messages
function clearMessages() {
    const messages = document.querySelectorAll('.error-message, .success-message');
    messages.forEach(msg => {
        msg.classList.remove('show');
        msg.textContent = '';
    });
}

// Show error message
function showError(elementId, message) {
    const element = document.getElementById(elementId);
    element.textContent = message;
    element.classList.add('show');
}

// Show success message
function showSuccess(elementId, message) {
    const element = document.getElementById(elementId);
    element.textContent = message;
    element.classList.add('show');
}

// Handle Login Form
document.getElementById('login-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    clearMessages();
    
    const formData = {
        identificador: document.getElementById('login-identificador').value,
        senha: document.getElementById('login-senha').value
    };
    
    const button = e.target.querySelector('button[type="submit"]');
    button.disabled = true;
    button.classList.add('loading');
    
    try {
        const response = await fetch(`${API_BASE_URL}/signin`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(formData)
        });
        
        if (response.ok) {
            const data = await response.json();
            // Store user info (token is in cookie managed by backend)
            localStorage.setItem('user', JSON.stringify(data));
            // Small delay to ensure cookie is set
            setTimeout(() => {
                window.location.href = '/dashboard.html';
            }, 100);
        } else {
            const error = await response.json();
            showError('login-error', error.message || 'Erro ao fazer login. Verifique suas credenciais.');
        }
    } catch (error) {
        showError('login-error', 'Erro de conexão. Tente novamente mais tarde.');
        console.error('Login error:', error);
    } finally {
        button.disabled = false;
        button.classList.remove('loading');
    }
});

// Handle Signup Form
document.getElementById('signup-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    clearMessages();
    
    const senha = document.getElementById('signup-senha').value;
    const confirmarSenha = document.getElementById('signup-confirmar-senha').value;
    
    // Validate password match
    if (senha !== confirmarSenha) {
        showError('signup-error', 'As senhas não coincidem.');
        return;
    }
    
    const formData = {
        nome: document.getElementById('signup-nome').value,
        email: document.getElementById('signup-email').value,
        senha: senha,
        status: 'ATIVO',
        rolesIds: [2] // Default role ID for new users
    };
    
    const button = e.target.querySelector('button[type="submit"]');
    button.disabled = true;
    button.classList.add('loading');
    
    try {
        const response = await fetch(`${API_BASE_URL}/signup`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(formData)
        });
        
        if (response.ok) {
            const data = await response.json();
            signupToken = data.token;
            
            // Show verification form
            hideAllForms();
            document.getElementById('verificationForm').classList.add('active');
            showSuccess('signup-success', data.message || 'Código de verificação enviado!');
        } else {
            const error = await response.json();
            showError('signup-error', error.message || 'Erro ao criar conta. Verifique os dados.');
        }
    } catch (error) {
        showError('signup-error', 'Erro de conexão. Tente novamente mais tarde.');
        console.error('Signup error:', error);
    } finally {
        button.disabled = false;
        button.classList.remove('loading');
    }
});

// Handle Verification Form
document.getElementById('verification-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    clearMessages();
    
    if (!signupToken) {
        showError('verification-error', 'Token de verificação não encontrado. Tente se cadastrar novamente.');
        return;
    }
    
    const codigo = document.getElementById('verification-code').value;
    
    const button = e.target.querySelector('button[type="submit"]');
    button.disabled = true;
    button.classList.add('loading');
    
    try {
        const response = await fetch(`${API_BASE_URL}/confirmar-conta/${signupToken}/${codigo}`, {
            method: 'GET'
        });
        
        if (response.ok) {
            const data = await response.json();
            alert(data.message || 'Conta confirmada com sucesso! Faça login para continuar.');
            
            // Reset to login form
            hideAllForms();
            document.getElementById('loginForm').classList.add('active');
            signupToken = null;
            
            // Clear signup form
            document.getElementById('signup-form').reset();
        } else {
            const error = await response.json();
            showError('verification-error', error.message || 'Código inválido. Tente novamente.');
        }
    } catch (error) {
        showError('verification-error', 'Erro de conexão. Tente novamente mais tarde.');
        console.error('Verification error:', error);
    } finally {
        button.disabled = false;
        button.classList.remove('loading');
    }
});

// Handle Forgot Password Form
document.getElementById('forgot-password-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    clearMessages();
    
    const formData = {
        identificador: document.getElementById('forgot-email').value
    };
    
    const button = e.target.querySelector('button[type="submit"]');
    button.disabled = true;
    button.classList.add('loading');
    
    try {
        const response = await fetch(`${API_BASE_URL}/redefinir-senha`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(formData)
        });
        
        if (response.ok) {
            const data = await response.json();
            resetToken = data.token;
            
            // Show reset verification form
            hideAllForms();
            document.getElementById('resetVerificationForm').classList.add('active');
            alert(data.message || 'Código enviado para seu email!');
        } else {
            const error = await response.json();
            showError('forgot-error', error.message || 'Erro ao enviar código. Verifique o email.');
        }
    } catch (error) {
        showError('forgot-error', 'Erro de conexão. Tente novamente mais tarde.');
        console.error('Forgot password error:', error);
    } finally {
        button.disabled = false;
        button.classList.remove('loading');
    }
});

// Handle Reset Verification Form
document.getElementById('reset-verification-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    clearMessages();
    
    if (!resetToken) {
        showError('reset-verification-error', 'Token não encontrado. Tente novamente.');
        return;
    }
    
    const codigo = document.getElementById('reset-code').value;
    
    const button = e.target.querySelector('button[type="submit"]');
    button.disabled = true;
    button.classList.add('loading');
    
    try {
        const response = await fetch(`${API_BASE_URL}/redefinir-senha/validar-codigo/${resetToken}/${codigo}`, {
            method: 'GET'
        });
        
        if (response.ok) {
            // Show new password form
            hideAllForms();
            document.getElementById('newPasswordForm').classList.add('active');
        } else {
            const error = await response.json();
            showError('reset-verification-error', error.message || 'Código inválido. Tente novamente.');
        }
    } catch (error) {
        showError('reset-verification-error', 'Erro de conexão. Tente novamente mais tarde.');
        console.error('Reset verification error:', error);
    } finally {
        button.disabled = false;
        button.classList.remove('loading');
    }
});

// Handle New Password Form
document.getElementById('new-password-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    clearMessages();
    
    const novaSenha = document.getElementById('new-password').value;
    const confirmarNovaSenha = document.getElementById('new-password-confirm').value;
    
    // Validate password match
    if (novaSenha !== confirmarNovaSenha) {
        showError('new-password-error', 'As senhas não coincidem.');
        return;
    }
    
    if (!resetToken) {
        showError('new-password-error', 'Token não encontrado. Tente novamente.');
        return;
    }
    
    const formData = {
        novaSenha: novaSenha
    };
    
    const button = e.target.querySelector('button[type="submit"]');
    button.disabled = true;
    button.classList.add('loading');
    
    try {
        const response = await fetch(`${API_BASE_URL}/redefinir-senha/nova-senha/${resetToken}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(formData)
        });
        
        if (response.ok) {
            const data = await response.json();
            alert(data.message || 'Senha alterada com sucesso! Faça login com sua nova senha.');
            
            // Reset to login form
            hideAllForms();
            document.getElementById('loginForm').classList.add('active');
            resetToken = null;
            
            // Clear forms
            document.getElementById('forgot-password-form').reset();
            document.getElementById('new-password-form').reset();
        } else {
            const error = await response.json();
            showError('new-password-error', error.message || 'Erro ao alterar senha. Tente novamente.');
        }
    } catch (error) {
        showError('new-password-error', 'Erro de conexão. Tente novamente mais tarde.');
        console.error('New password error:', error);
    } finally {
        button.disabled = false;
        button.classList.remove('loading');
    }
});

// Check if user is already logged in
window.addEventListener('DOMContentLoaded', () => {
    const currentPath = window.location.pathname;
    const isLoginPage = currentPath === '/index.html' || currentPath === '/' || currentPath === '';
    
    if (isLoginPage) {
        const user = localStorage.getItem('user');
        if (user) {
            // User has data, try to verify if still authenticated
            fetch('/usuario/meu-perfil', {
                method: 'GET',
                credentials: 'include'
            })
            .then(response => {
                if (response.ok) {
                    // Still authenticated, redirect to dashboard
                    window.location.href = '/dashboard.html';
                }
                // If not authenticated, stay on login page
            })
            .catch(() => {
                // Error or not authenticated, stay on login page
            });
        }
    }
});
