/* ============================================
   SIGA Seguros - Core JavaScript
   ============================================ */

// Auth guard: redirect to login if no token (skip on login page)
(function() {
    const isLoginPage = window.location.pathname === '/login' || window.location.pathname === '/';
    if (!isLoginPage && !localStorage.getItem('token')) {
        window.location.href = '/login';
    }
})();

/* ========== PERMISSION MATRIX ========== */
const PERMS = {
    dashboard:   { view: ['ADMIN','GESTOR','COMERCIAL','OPERADOR','FINANCEIRO'], create: [], edit: [], delete: [] },
    clientes:    { view: ['ADMIN','GESTOR','COMERCIAL','OPERADOR'], create: ['ADMIN','GESTOR','COMERCIAL'], edit: ['ADMIN','GESTOR','COMERCIAL'], delete: ['ADMIN','GESTOR'] },
    propostas:   { view: ['ADMIN','GESTOR','COMERCIAL','OPERADOR'], create: ['ADMIN','GESTOR','COMERCIAL'], edit: ['ADMIN','GESTOR','COMERCIAL'], delete: ['ADMIN','GESTOR'] },
    apolices:    { view: ['ADMIN','GESTOR','COMERCIAL','OPERADOR'], create: ['ADMIN','GESTOR','OPERADOR'],  edit: ['ADMIN','GESTOR','OPERADOR'],  delete: ['ADMIN','GESTOR'] },
    renovacoes:  { view: ['ADMIN','GESTOR','COMERCIAL','OPERADOR'], create: ['ADMIN','GESTOR','COMERCIAL','OPERADOR'], edit: ['ADMIN','GESTOR','COMERCIAL','OPERADOR'], delete: ['ADMIN','GESTOR'] },
    sinistros:   { view: ['ADMIN','GESTOR','COMERCIAL','OPERADOR'], create: ['ADMIN','GESTOR','OPERADOR'],  edit: ['ADMIN','GESTOR','OPERADOR'],  delete: [] },
    financeiro:  { view: ['ADMIN','GESTOR','FINANCEIRO'], create: ['ADMIN','GESTOR','FINANCEIRO'], edit: ['ADMIN','GESTOR','FINANCEIRO'], delete: [] },
    comissoes:   { view: ['ADMIN','GESTOR','FINANCEIRO'], create: ['ADMIN','GESTOR','FINANCEIRO'], edit: ['ADMIN','GESTOR','FINANCEIRO'], delete: [] },
    seguradoras: { view: ['ADMIN','GESTOR','COMERCIAL','OPERADOR'], create: ['ADMIN','GESTOR'], edit: ['ADMIN','GESTOR'], delete: ['ADMIN'] },
    corretoras:  { view: ['ADMIN','GESTOR','COMERCIAL','OPERADOR'], create: ['ADMIN','GESTOR'], edit: ['ADMIN','GESTOR'], delete: ['ADMIN'] },
    usuarios:    { view: ['ADMIN'], create: ['ADMIN'], edit: ['ADMIN'], delete: ['ADMIN'] },
    auditoria:   { view: ['ADMIN','AUDITOR'], create: [], edit: [], delete: [] }
};

function getUserPerfil() {
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    return user.perfil || '';
}

function hasPermission(module, action) {
    const perfil = getUserPerfil();
    const mod = PERMS[module];
    if (!mod) return false;
    const allowed = mod[action || 'view'];
    return allowed && allowed.includes(perfil);
}

function canView(module) { return hasPermission(module, 'view'); }
function canCreate(module) { return hasPermission(module, 'create'); }
function canEdit(module) { return hasPermission(module, 'edit'); }
function canDelete(module) { return hasPermission(module, 'delete'); }

/** Retorna o módulo da página atual baseado no path */
function currentModule() {
    const path = window.location.pathname;
    const match = path.match(/\/app\/(\w+)/);
    return match ? match[1] : 'dashboard';
}

const API = {
    baseUrl: '/api',
    token: localStorage.getItem('token'),

    headers() {
        const h = { 'Content-Type': 'application/json' };
        if (this.token) h['Authorization'] = 'Bearer ' + this.token;
        return h;
    },

    async request(method, path, body) {
        const opts = { method, headers: this.headers() };
        if (body) opts.body = JSON.stringify(body);
        const res = await fetch(this.baseUrl + path, opts);
        if (res.status === 401) {
            localStorage.removeItem('token');
            localStorage.removeItem('user');
            window.location.href = '/login';
            return;
        }
        if (!res.ok) {
            const err = await res.json().catch(() => ({ message: 'Erro na requisição' }));
            throw new Error(err.message || 'Erro ' + res.status);
        }
        return res.json();
    },

    get(path) { return this.request('GET', path); },
    post(path, body) { return this.request('POST', path, body); },
    put(path, body) { return this.request('PUT', path, body); },
    patch(path, body) { return this.request('PATCH', path, body); },
    del(path) { return this.request('DELETE', path); }
};

/* ========== AUTH ========== */
async function doLogin(e) {
    e.preventDefault();
    const email = document.getElementById('email').value;
    const senha = document.getElementById('senha').value;
    const btn = e.target.querySelector('button[type=submit]');
    const errDiv = document.getElementById('loginError');

    btn.disabled = true;
    btn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Entrando...';
    errDiv.classList.add('d-none');

    try {
        const res = await API.post('/auth/login', { email, senha });
        localStorage.setItem('token', res.data.token);
        localStorage.setItem('refreshToken', res.data.refreshToken);
        localStorage.setItem('user', JSON.stringify({ nome: res.data.nome, email: res.data.email, perfil: res.data.perfil }));
        API.token = res.data.token;
        window.location.href = '/app/dashboard';
    } catch (err) {
        errDiv.textContent = err.message;
        errDiv.classList.remove('d-none');
    } finally {
        btn.disabled = false;
        btn.innerHTML = '<i class="bi bi-box-arrow-in-right me-2"></i>Entrar';
    }
}

function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('user');
    window.location.href = '/login';
}

function alterarSenha() {
    const html = `
        <form id="formAlterarSenha">
            <div class="mb-3">
                <label class="form-label">Senha Atual</label>
                <input type="password" class="form-control" id="senhaAtual" required>
            </div>
            <div class="mb-3">
                <label class="form-label">Nova Senha</label>
                <input type="password" class="form-control" id="novaSenha" required minlength="6">
            </div>
            <div class="mb-3">
                <label class="form-label">Confirmar Nova Senha</label>
                <input type="password" class="form-control" id="confirmaSenha" required>
            </div>
        </form>`;
    showModal('Alterar Senha', html, async () => {
        const senhaAtual = document.getElementById('senhaAtual').value;
        const novaSenha = document.getElementById('novaSenha').value;
        const confirmaSenha = document.getElementById('confirmaSenha').value;
        if (novaSenha !== confirmaSenha) { showToast('Senhas não conferem', 'danger'); return; }
        const user = JSON.parse(localStorage.getItem('user'));
        await API.post('/auth/alterar-senha', { email: user.email, senhaAtual, novaSenha });
        showToast('Senha alterada com sucesso', 'success');
        bootstrap.Modal.getInstance(document.getElementById('dynamicModal')).hide();
    });
}

/* ========== UI UTILS ========== */
function showToast(message, type = 'success') {
    const container = document.getElementById('toastContainer');
    if (!container) return;
    const icons = { success: 'check-circle-fill', danger: 'exclamation-triangle-fill', warning: 'exclamation-circle-fill', info: 'info-circle-fill' };
    const toast = document.createElement('div');
    toast.className = `toast show align-items-center text-bg-${type} border-0`;
    toast.setAttribute('role', 'alert');
    toast.innerHTML = `
        <div class="d-flex">
            <div class="toast-body"><i class="bi bi-${icons[type] || 'info-circle-fill'} me-2"></i>${message}</div>
            <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
        </div>`;
    container.appendChild(toast);
    setTimeout(() => toast.remove(), 4000);
}

function showModal(title, bodyHtml, onSave, size = '') {
    let modal = document.getElementById('dynamicModal');
    if (modal) modal.remove();
    modal = document.createElement('div');
    modal.id = 'dynamicModal';
    modal.className = 'modal fade';
    modal.tabIndex = -1;
    modal.innerHTML = `
        <div class="modal-dialog ${size ? 'modal-' + size : ''}">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">${title}</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">${bodyHtml}</div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Cancelar</button>
                    ${onSave ? '<button type="button" class="btn btn-primary" id="modalSaveBtn">Salvar</button>' : ''}
                </div>
            </div>
        </div>`;
    document.body.appendChild(modal);
    const bsModal = new bootstrap.Modal(modal);
    if (onSave) {
        document.getElementById('modalSaveBtn').addEventListener('click', async () => {
            try { await onSave(); } catch (err) { showToast(err.message, 'danger'); }
        });
    }
    bsModal.show();
    modal.addEventListener('hidden.bs.modal', () => modal.remove());
}

function toggleSidebar() {
    document.getElementById('sidebar').classList.toggle('show');
}

function formatCurrency(val) {
    if (val == null) return '-';
    return Number(val).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
}

function formatDate(str) {
    return str || '-';
}

function statusBadge(status) {
    const map = {
        'ATIVO': 'success', 'ATIVA': 'success', 'APROVADA': 'success', 'PAGO': 'success', 'RECEBIDA': 'success', 'RENOVADA': 'success',
        'INATIVO': 'secondary', 'ENVIADA': 'info', 'NAO_RENOVADA': 'danger', 'ATRASADA': 'danger',
        'EM_COTACAO': 'info', 'EM_ANALISE': 'info', 'ABERTO': 'info', 'PENDENTE': 'warning',
        'CANCELADA': 'danger', 'RECUSADA': 'danger', 'NEGADO': 'danger', 'VENCIDA': 'danger', 'ATRASADO': 'danger',
        'SUSPENSA': 'warning', 'DOCUMENTACAO_PENDENTE': 'warning', 'CONTATO_REALIZADO': 'info',
        'INDENIZADO': 'success', 'PARCIALMENTE_INDENIZADO': 'info'
    };
    const cls = map[status] || 'secondary';
    const label = (status || '').replace(/_/g, ' ');
    return `<span class="badge-status badge-${cls}">${label}</span>`;
}

/* ========== PAGINATION ========== */
function renderPagination(page, loadFn) {
    const total = page.totalPages || 1;
    const current = page.number || 0;
    const totalElements = page.totalElements || 0;
    const from = current * page.size + 1;
    const to = Math.min((current + 1) * page.size, totalElements);

    let html = `<span>${from}-${to} de ${totalElements}</span><nav><ul class="pagination pagination-sm mb-0">`;
    html += `<li class="page-item ${current === 0 ? 'disabled' : ''}"><a class="page-link" href="#" onclick="${loadFn}(${current - 1}); return false;">&laquo;</a></li>`;
    for (let i = Math.max(0, current - 2); i <= Math.min(total - 1, current + 2); i++) {
        html += `<li class="page-item ${i === current ? 'active' : ''}"><a class="page-link" href="#" onclick="${loadFn}(${i}); return false;">${i + 1}</a></li>`;
    }
    html += `<li class="page-item ${current >= total - 1 ? 'disabled' : ''}"><a class="page-link" href="#" onclick="${loadFn}(${current + 1}); return false;">&raquo;</a></li>`;
    html += `</ul></nav>`;
    return html;
}

/* ========== INIT ========== */
document.addEventListener('DOMContentLoaded', () => {
    const path = window.location.pathname;
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    const perfil = user.perfil || '';

    // Set active sidebar link
    document.querySelectorAll('.sidebar-link').forEach(link => {
        if (link.getAttribute('href') === path || (path === '/' && link.dataset.page === 'dashboard')) {
            link.classList.add('active');
        }
    });

    // Set user name and role
    const el = document.getElementById('userName');
    if (el && user.nome) el.textContent = user.nome;
    const roleEl = document.getElementById('userRole');
    if (roleEl && perfil) roleEl.textContent = perfil;

    // Apply sidebar permissions: hide items user cannot view
    document.querySelectorAll('.sidebar-item').forEach(item => {
        const link = item.querySelector('.sidebar-link');
        if (!link) return;
        const page = link.dataset.page;
        if (page && PERMS[page]) {
            const viewers = PERMS[page].view;
            if (viewers.length > 0 && !viewers.includes(perfil)) {
                item.style.display = 'none';
            }
        }
    });

    // Hide empty divider sections
    document.querySelectorAll('.sidebar-divider').forEach(div => {
        let next = div.nextElementSibling;
        let hasVisible = false;
        while (next && !next.classList.contains('sidebar-divider')) {
            if (next.classList.contains('sidebar-item') && next.style.display !== 'none') {
                hasVisible = true;
                break;
            }
            next = next.nextElementSibling;
        }
        if (!hasVisible) div.style.display = 'none';
    });

    // Hide "New" buttons if user cannot create on current module
    const mod = currentModule();
    if (mod && !canCreate(mod)) {
        document.querySelectorAll('.page-header .btn-primary').forEach(btn => {
            if (btn.textContent.match(/Nov[oa]/)) btn.style.display = 'none';
        });
    }

    // Block access to restricted page (redirect to dashboard)
    if (path.startsWith('/app') && mod && PERMS[mod] && !canView(mod)) {
        window.location.href = '/app/dashboard';
        return;
    }

    // Verify auth for app pages
    if (path.startsWith('/app') && !API.token) {
        window.location.href = '/login';
    }
});
