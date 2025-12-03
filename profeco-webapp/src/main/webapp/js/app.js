// app.js - Single Page Application
class ProfecoApp {
    constructor() {
        this.currentUser = null;
        this.init();
    }

    async init() {
        // Verificar autenticación al cargar
        await this.checkAuth();
        this.setupEventListeners();

        // Ocultar loading
        setTimeout(() => {
            document.getElementById('loading').classList.add('hidden');
        }, 500);
    }

    async checkAuth() {
        try {
            const response = await fetch('/auth/login', {
                method: 'POST',
                headers: {'Content-Type': 'application/json'}
            });

            if (response.ok) {
                const data = await response.json();
                if (data.status === 'authenticated') {
                    // Cargar usuario desde localStorage
                    this.currentUser = JSON.parse(localStorage.getItem('user') || '{}');
                    this.showAuthenticatedView();
                } else {
                    this.showLoginView();
                }
            } else {
                this.showLoginView();
            }
        } catch (error) {
            console.error('Error checking auth:', error);
            this.showLoginView();
        }
    }

    showAuthenticatedView() {
        // Mostrar navbar con usuario
        this.renderNavbar();

        // Mostrar template de dashboard
        const template = document.getElementById('template-dashboard');
        const content = template.content.cloneNode(true);
        document.getElementById('content').innerHTML = '';
        document.getElementById('content').appendChild(content);

        // Mostrar/ocultar menú admin según rol
        if (this.currentUser.rol === 'profeco') {
            document.getElementById('admin-menu').classList.remove('d-none');
        }

        // Cargar dashboard por defecto
        this.loadDashboard();
    }

    showLoginView() {
        // Ocultar navbar
        document.getElementById('navbar').classList.add('hidden');

        // Mostrar template de login
        const template = document.getElementById('template-login');
        const content = template.content.cloneNode(true);
        document.getElementById('content').innerHTML = '';
        document.getElementById('content').appendChild(content);

        // Configurar formulario de login
        const form = document.getElementById('login-form');
        if (form) {
            form.addEventListener('submit', (e) => this.handleLogin(e));
        }
    }

    renderNavbar() {
        const navbar = document.getElementById('navbar');
        navbar.classList.remove('hidden');

        const userInfo = document.getElementById('user-info');
        if (this.currentUser) {
            userInfo.innerHTML = `
                <span class="me-3">
                    <i class="bi bi-person-circle me-1"></i> ${this.currentUser.usuario}
                    <span class="badge bg-${this.currentUser.rol === 'profeco' ? 'danger' : 'success'}">
                        ${this.currentUser.rol}
                    </span>
                </span>
                <button class="btn btn-outline-danger btn-sm" onclick="app.logout()">
                    <i class="bi bi-box-arrow-right"></i> Salir
                </button>
            `;
        }
    }

    async handleLogin(event) {
        event.preventDefault();

        const usuario = document.getElementById('usuario').value;
        const contrasena = document.getElementById('contrasena').value;

        try {
            const result = await authService.login(usuario, contrasena);

            if (result.token) {
                this.currentUser = {
                    usuario: result.usuario,
                    rol: result.rol
                };
                localStorage.setItem('user', JSON.stringify(this.currentUser));
                this.showAuthenticatedView();
            } else {
                this.showError('Credenciales incorrectas');
            }
        } catch (error) {
            this.showError(error.message || 'Error al iniciar sesión');
        }
    }

    async logout() {
        await authService.logout();
        this.currentUser = null;
        localStorage.removeItem('user');
        this.showLoginView();
    }

    async loadDashboard() {
        const content = document.getElementById('dashboard-content');
        content.innerHTML = `
            <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
                <h1 class="h2"><i class="bi bi-house-door me-2"></i>Dashboard</h1>
                <div class="btn-toolbar mb-2 mb-md-0">
                    <button class="btn btn-primary" onclick="app.loadCrearQueja()">
                        <i class="bi bi-plus-circle me-1"></i> Nueva Queja
                    </button>
                </div>
            </div>
            <div id="dashboard-stats"></div>
            <div id="recent-quejas" class="mt-4"></div>
        `;

        // Cargar estadísticas
        await this.loadStats();
        // Cargar quejas recientes
        await this.loadRecentQuejas();
    }

    async loadStats() {
        try {
            const stats = await quejasService.getStats();
            const container = document.getElementById('dashboard-stats');
            container.innerHTML = `
                <div class="row">
                    <div class="col-md-4">
                        <div class="card bg-primary text-white">
                            <div class="card-body">
                                <div class="d-flex justify-content-between">
                                    <div>
                                        <h6 class="card-title">Total Quejas</h6>
                                        <h2 class="mb-0">${stats.total || 0}</h2>
                                    </div>
                                    <div class="align-self-center">
                                        <i class="bi bi-file-text display-6"></i>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            `;
        } catch (error) {
            console.error('Error loading stats:', error);
        }
    }

    async loadRecentQuejas() {
        try {
            const quejas = await quejasService.getMyQuejas();
            const container = document.getElementById('recent-quejas');

            if (quejas && quejas.length > 0) {
                let html = `
                    <div class="card">
                        <div class="card-header">
                            <h5 class="mb-0"><i class="bi bi-list-check me-2"></i> Mis Quejas Recientes</h5>
                        </div>
                        <div class="card-body">
                            <div class="list-group">
                `;

                quejas.slice(0, 5).forEach(queja => {
                    html += `
                        <div class="list-group-item list-group-item-action queja-card mb-2">
                            <div class="d-flex w-100 justify-content-between">
                                <h6 class="mb-1">${queja.titulo}</h6>
                                <small class="text-muted">
                                    <span class="badge bg-info">${queja.estado || 'RECIBIDA'}</span>
                                </small>
                            </div>
                            <p class="mb-1">${queja.descripcion}</p>
                            <small class="text-muted">
                                <i class="bi bi-shop me-1"></i> ${queja.comercio} 
                                | <i class="bi bi-calendar me-1"></i> ${queja.fecha}
                                | <i class="bi bi-hash me-1"></i> ${queja.quejaId}
                            </small>
                        </div>
                    `;
                });

                html += `
                            </div>
                        </div>
                    </div>
                `;
                container.innerHTML = html;
            } else {
                container.innerHTML = `
                    <div class="card">
                        <div class="card-body text-center py-5">
                            <i class="bi bi-file-text display-1 text-muted"></i>
                            <h5 class="mt-3">No tienes quejas registradas</h5>
                            <p class="text-muted">Comienza registrando tu primera queja</p>
                            <button class="btn btn-primary" onclick="app.loadCrearQueja()">
                                <i class="bi bi-plus-circle me-1"></i> Crear Primera Queja
                            </button>
                        </div>
                    </div>
                `;
            }
        } catch (error) {
            console.error('Error loading quejas:', error);
        }
    }

    async loadCrearQueja() {
        const content = document.getElementById('dashboard-content');
        content.innerHTML = `
            <div class="row justify-content-center">
                <div class="col-md-8">
                    <div class="card">
                        <div class="card-header bg-primary text-white">
                            <h5 class="mb-0"><i class="bi bi-plus-circle me-2"></i> Registrar Nueva Queja</h5>
                        </div>
                        <div class="card-body">
                            <div id="queja-alert" class="alert d-none"></div>
                            <form id="queja-form">
                                <div class="mb-3">
                                    <label for="queja-titulo" class="form-label">Título *</label>
                                    <input type="text" class="form-control" id="queja-titulo" 
                                           placeholder="Ej: Producto en mal estado" required>
                                </div>
                                <div class="mb-3">
                                    <label for="queja-descripcion" class="form-label">Descripción *</label>
                                    <textarea class="form-control" id="queja-descripcion" 
                                              rows="4" placeholder="Describa el problema..." required></textarea>
                                </div>
                                <div class="mb-3">
                                    <label for="queja-comercio" class="form-label">Comercio *</label>
                                    <input type="text" class="form-control" id="queja-comercio" 
                                           placeholder="Ej: Supermercado XYZ" required>
                                </div>
                                <div class="d-grid gap-2 d-md-flex justify-content-md-end">
                                    <button type="button" class="btn btn-secondary me-md-2" onclick="app.loadDashboard()">
                                        <i class="bi bi-arrow-left"></i> Cancelar
                                    </button>
                                    <button type="submit" class="btn btn-primary">
                                        <i class="bi bi-check-circle"></i> Registrar Queja
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        `;

        // Configurar formulario
        const form = document.getElementById('queja-form');
        if (form) {
            form.addEventListener('submit', (e) => this.handleCrearQueja(e));
        }
    }

    async handleCrearQueja(event) {
        event.preventDefault();

        const quejaData = {
            titulo: document.getElementById('queja-titulo').value,
            descripcion: document.getElementById('queja-descripcion').value,
            comercio: document.getElementById('queja-comercio').value,
            usuario: this.currentUser.usuario
        };

        try {
            const result = await quejasService.crearQueja(quejaData);
            this.showSuccess('Queja registrada exitosamente');
            setTimeout(() => this.loadDashboard(), 1500);
        } catch (error) {
            this.showError(error.message || 'Error al crear queja');
        }
    }

    async loadMisQuejas() {
        // Implementar similar a loadDashboard pero con todas las quejas
    }

    async loadAdmin() {
        if (this.currentUser.rol !== 'profeco')
            return;

        // Implementar vista admin
    }

    showError(message) {
        const alert = document.getElementById('login-alert') ||
                document.getElementById('queja-alert');
        if (alert) {
            alert.textContent = message;
            alert.className = 'alert alert-danger';
            alert.classList.remove('d-none');
            setTimeout(() => alert.classList.add('d-none'), 5000);
        }
    }

    showSuccess(message) {
        const alert = document.getElementById('queja-alert');
        if (alert) {
            alert.textContent = message;
            alert.className = 'alert alert-success';
            alert.classList.remove('d-none');
        }
    }

    setupEventListeners() {
        // Event listeners globales
    }
}

// Inicializar aplicación
const app = new ProfecoApp();
window.app = app;

