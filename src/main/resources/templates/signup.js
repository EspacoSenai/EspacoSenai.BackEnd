(() => {
  const form = document.getElementById('signupForm');
  const btn = document.getElementById('signupBtn');
  const feedback = document.getElementById('feedback');
  const verifySection = document.getElementById('verifySection');
  const emailShow = document.getElementById('emailShow');
  const tokenEl = document.getElementById('token');
  const codigoEl = document.getElementById('codigo');
  const confirmBtn = document.getElementById('confirmarBtn');
  const verifyFeedback = document.getElementById('verifyFeedback');
  const reenviarBtn = document.getElementById('reenviarBtn');

  function setFeedback(el, message, type = 'error') {
    el.textContent = message || '';
    el.className = 'feedback show ' + (type === 'success' ? 'success' : 'error');
  }

  async function postJson(url, body) {
    const res = await fetch(url, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(body)
    });
    const isJson = (res.headers.get('content-type') || '').includes('application/json');
    const data = isJson ? await res.json() : await res.text();
    if (!res.ok) {
      const message = (data && data.message) || (typeof data === 'string' ? data : 'Erro na requisição');
      throw { status: res.status, message, data };
    }
    return data;
  }

  form.addEventListener('submit', async (ev) => {
    ev.preventDefault();
    setFeedback(feedback, '', 'error');

    const nome = document.getElementById('nome').value.trim();
    const email = document.getElementById('email').value.trim();
    const senha = document.getElementById('senha').value;
    const senha2 = document.getElementById('senha2').value;

    if (!nome || !email || !senha || !senha2) {
      setFeedback(feedback, 'Preencha todos os campos.');
      return;
    }
    if (senha !== senha2) {
      setFeedback(feedback, 'As senhas não coincidem.');
      return;
    }

    btn.disabled = true;
    btn.textContent = 'Verificando...';

    try {
      const data = await postJson('/auth/signup', { nome, email, senha });
      // Espera: { status, message, token }
      emailShow.textContent = email;
      tokenEl.value = data.token || '';
      verifySection.classList.add('show');
      setFeedback(feedback, (data && data.message) || 'Se elegível, enviamos um código para seu email.', 'success');
    } catch (err) {
      setFeedback(feedback, err.message || 'Erro no cadastro.');
    } finally {
      btn.disabled = false;
      btn.textContent = 'Criar conta';
    }
  });

  confirmBtn.addEventListener('click', async () => {
    setFeedback(verifyFeedback, '');
    const token = tokenEl.value.trim();
    const codigo = codigoEl.value.trim();
    if (!token || !codigo) {
      setFeedback(verifyFeedback, 'Informe o código e aguarde o token.');
      return;
    }
    try {
      const res = await fetch(`/auth/confirmar-conta/${encodeURIComponent(token)}/${encodeURIComponent(codigo)}`);
      if (!res.ok) {
        const data = await res.json().catch(() => ({}));
        throw new Error(data.message || 'Falha ao confirmar.');
      }
      setFeedback(verifyFeedback, 'Conta confirmada! Redirecionando para login...', 'success');
      setTimeout(() => (window.location.href = '/login'), 900);
    } catch (e) {
      setFeedback(verifyFeedback, e.message || 'Erro ao confirmar.');
    }
  });

  reenviarBtn.addEventListener('click', async (ev) => {
    ev.preventDefault();
    setFeedback(verifyFeedback, '');
    // Reaproveita os dados do formulário para reenviar
    const nome = document.getElementById('nome').value.trim();
    const email = document.getElementById('email').value.trim();
    const senha = document.getElementById('senha').value;
    if (!email || !senha || !nome) {
      setFeedback(verifyFeedback, 'Preencha nome, email e senha para reenviar.');
      return;
    }
    try {
      const data = await postJson('/auth/signup', { nome, email, senha });
      tokenEl.value = data.token || tokenEl.value;
      setFeedback(verifyFeedback, 'Código reenviado. Verifique seu email.', 'success');
    } catch (err) {
      setFeedback(verifyFeedback, err.message || 'Erro ao reenviar.');
    }
  });
})();
