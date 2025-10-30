(function () {
  const form = document.getElementById('loginForm');
  // Se já estiver logado, pula para home
  try {
    const existing = localStorage.getItem('accessToken');
    if (existing) {
      window.location.href = '/home';
      return;
    }
  } catch {}

  const feedbackEl = document.getElementById('feedback');
  const submitBtn = document.getElementById('submitBtn');
  const signupLink = document.getElementById('signupLink');
  const esqueciLink = document.getElementById('esqueciLink');

  function showFeedback(message, type = 'error') {
    feedbackEl.textContent = message;
    feedbackEl.className = 'feedback show ' + (type === 'success' ? 'success' : 'error');
  }

  function clearFeedback() {
    feedbackEl.textContent = '';
    feedbackEl.className = 'feedback';
  }

  async function postJson(url, body) {
    const res = await fetch(url, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(body)
    });

    const contentType = res.headers.get('content-type') || '';
    let data = null;
    if (contentType.includes('application/json')) {
      data = await res.json();
    } else {
      data = await res.text();
    }

    if (!res.ok) {
      // try to extract message from JSON body
      const message = (data && data.message) || (typeof data === 'string' ? data : 'Erro na requisição');
      const status = res.status;
      throw { message, status, data };
    }

    return data;
  }

  form.addEventListener('submit', async (ev) => {
    ev.preventDefault();
    clearFeedback();

    const identificador = document.getElementById('identificador').value.trim();
    const senha = document.getElementById('senha').value;

    if (!identificador || !senha) {
      showFeedback('Preencha identificador e senha.', 'error');
      return;
    }

    submitBtn.disabled = true;
    submitBtn.textContent = 'Entrando...';

    try {
      const body = { identificador, senha };
      const data = await postJson('/auth/signin', body);

      // sucesso — guarda token e redireciona
      if (data && data.accessToken) {
        localStorage.setItem('accessToken', data.accessToken);
        showFeedback('Login realizado com sucesso. Redirecionando...', 'success');
        setTimeout(() => {
          window.location.href = '/home';
        }, 800);
      } else {
        showFeedback('Resposta do servidor inválida.', 'error');
      }
    } catch (err) {
      const msg = err && err.message ? err.message : 'Erro ao autenticar';
      showFeedback(msg, 'error');
    } finally {
      submitBtn.disabled = false;
      submitBtn.textContent = 'Entrar';
    }
  });

  // Esqueci a senha: pergunta o identificador e chama o endpoint
  esqueciLink.addEventListener('click', async (ev) => {
    ev.preventDefault();
    clearFeedback();

    const identificador = prompt('Digite seu email ou identificador para receber o código por email:');
    if (!identificador) return;

    try {
      const data = await postJson('/auth/redefinir-senha', { identificador });
      const message = (data && data.message) ? data.message : 'Se o usuário existir, um código foi enviado por e-mail.';
      showFeedback(message, 'success');
    } catch (err) {
      showFeedback(err.message || 'Erro ao solicitar redefinição.', 'error');
    }
  });

  // Link de cadastro: redireciona para uma rota de signup caso exista
  signupLink.addEventListener('click', (ev) => {
    ev.preventDefault();
    // Se houver um fluxo de signup na aplicação, definir a rota corretamente
  window.location.href = '/signup';
  });
})();

