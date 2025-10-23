(function(){
  const tbody = document.getElementById('tbodyCursos');
  const feedback = document.getElementById('feedback');
  const btnNovo = document.getElementById('btnNovo');
  const btnRecarregar = document.getElementById('btnRecarregar');
  const secForm = document.getElementById('secForm');
  const form = document.getElementById('formCurso');
  const formTitulo = document.getElementById('formTitulo');
  const campoId = document.getElementById('cursoId');
  const campoNome = document.getElementById('cursoNome');
  const campoDesc = document.getElementById('cursoDesc');
  const btnCancelar = document.getElementById('btnCancelar');

  function setFb(msg, type='error'){
    feedback.textContent = msg || '';
    feedback.className = 'feedback show ' + (type==='success' ? 'success' : 'error');
  }

  function getToken(){ return localStorage.getItem('accessToken'); }
  function getAuth(){ const t=getToken(); return t? { 'Authorization': 'Bearer ' + t } : {}; }

  function parseScopes(){
    // Decodifica payload do JWT para extrair scope
    const t = getToken();
    if(!t) return [];
    try {
      const payload = JSON.parse(atob(t.split('.')[1]));
      const scope = payload.scope || '';
      const raw = String(scope).split(/\s+/).filter(Boolean);
      // Normaliza para sempre haver ambas as formas: cru (ADMIN) e com prefixo (SCOPE_ADMIN)
      const expanded = new Set();
      raw.forEach(s => { expanded.add(s); if(!s.startsWith('SCOPE_')) expanded.add('SCOPE_'+s); });
      return Array.from(expanded);
    } catch(e) { return []; }
  }

  const scopes = parseScopes();
  const podeLer = scopes.some(s => ['SCOPE_ADMIN','SCOPE_COORDENADOR','SCOPE_PROFESSOR','SCOPE_ESTUDANTE','ADMIN','COORDENADOR','PROFESSOR','ESTUDANTE'].includes(s));
  const podeEscrever = scopes.some(s => ['SCOPE_ADMIN','SCOPE_COORDENADOR','SCOPE_PROFESSOR','ADMIN','COORDENADOR','PROFESSOR'].includes(s));

  if(podeEscrever){ btnNovo.classList.remove('hidden'); secForm.classList.add('hidden'); } else { btnNovo.classList.add('hidden'); secForm.classList.add('hidden'); }

  function row(c){
    const tr = document.createElement('tr');
    tr.innerHTML = `
      <td>${c.id}</td>
      <td>${c.nome ?? ''}</td>
      <td>${c.descricao ?? ''}</td>
      <td>
        <div class="actions">
          ${podeEscrever ? `<button data-act="edit">Editar</button><button data-act="del">Excluir</button>` : `<span style="color:var(--muted)">—</span>`}
        </div>
      </td>`;
    if(podeEscrever){
      tr.querySelector('[data-act="edit"]').addEventListener('click', () => editar(c));
      tr.querySelector('[data-act="del"]').addEventListener('click', () => excluir(c));
    }
    return tr;
  }

  async function carregar(){
    if(!podeLer){ setFb('Você não tem permissão para visualizar cursos.'); return; }
    setFb('');
    try{
      const res = await fetch('/curso/buscar', { headers: { 'Accept':'application/json', ...getAuth() }});
      const data = await res.json();
      if(!res.ok) throw new Error((data && data.message) || 'Erro ao buscar cursos');
      tbody.innerHTML = '';
      (data && data.length ? data : []).forEach(c => tbody.appendChild(row(c)));
      if(!data || !data.length){
        const tr = document.createElement('tr'); tr.innerHTML = '<td colspan="4">Nenhum curso cadastrado.</td>'; tbody.appendChild(tr);
      }
    }catch(e){ setFb(e.message || 'Falha ao carregar'); }
  }

  function editar(c){
    secForm.classList.remove('hidden');
    formTitulo.textContent = 'Editar curso';
    campoId.value = c.id;
    campoNome.value = c.nome || '';
    campoDesc.value = c.descricao || '';
  }

  function novo(){
    secForm.classList.remove('hidden');
    formTitulo.textContent = 'Novo curso';
    campoId.value = '';
    campoNome.value = '';
    campoDesc.value = '';
  }

  async function salvar(ev){
    ev.preventDefault();
    if(!podeEscrever){ setFb('Sem permissão para salvar.'); return; }
    const id = campoId.value.trim();
    const body = { nome: campoNome.value.trim(), descricao: campoDesc.value.trim() };
    try{
      const url = id ? `/curso/atualizar/${id}` : '/curso/salvar';
      const method = id ? 'PUT' : 'POST';
      const res = await fetch(url, { method, headers: { 'Content-Type': 'application/json', ...getAuth() }, body: JSON.stringify(body) });
      let data = null; try{ data = await res.json(); }catch{}
      if(!res.ok){ throw new Error((data && data.message) || 'Erro ao salvar'); }
      setFb((data && data.message) || 'Salvo com sucesso', 'success');
      secForm.classList.add('hidden');
      await carregar();
    }catch(e){ setFb(e.message || 'Falha ao salvar'); }
  }

  async function excluir(c){
    if(!podeEscrever){ setFb('Sem permissão para excluir.'); return; }
    if(!confirm(`Excluir o curso "${c.nome}"?`)) return;
    try{
      const res = await fetch(`/curso/deletar/${c.id}`, { method:'DELETE', headers: getAuth() });
      if(res.status !== 204 && !res.ok){
        let data=null; try{ data = await res.json(); }catch{}
        throw new Error((data && data.message) || 'Erro ao excluir');
      }
      setFb('Excluído com sucesso', 'success');
      await carregar();
    }catch(e){ setFb(e.message || 'Falha ao excluir'); }
  }

  btnNovo && btnNovo.addEventListener('click', novo);
  btnRecarregar && btnRecarregar.addEventListener('click', carregar);
  btnCancelar && btnCancelar.addEventListener('click', () => secForm.classList.add('hidden'));
  form && form.addEventListener('submit', salvar);

  carregar();
})();
