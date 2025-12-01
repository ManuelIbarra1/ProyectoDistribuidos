// dashboard.js - Funcionalidades del dashboard
class Dashboard {
    constructor() {
        this.user = authService.getCurrentUser();
        this.init();
    }
    
    async init() {
        await this.loadUserInfo();
        await this.loadStats();
        await this.loadRecentQuejas();
        this.setupEventListeners();
    }
    
    async loadUserInfo() {
        document.getElementById('user-name').textContent = this.user.usuario;
        document.getElementById('user-role').textContent = this.user.rol;
        document.getElementById('user-role').className = 
            `badge bg-${this.user.rol === 'profeco' ? 'danger' : 'success'}`;
    }
    
    async loadStats() {
        try {
            if (this.user.rol === 'profeco') {
                // Cargar estadísticas globales
                const response = await fetch('http://localhost:8085/api/quejas/estadisticas/total', {
                    headers: { 'Authorization': `Bearer ${localStorage.getItem('token')}` }
                });
                const data = await response.json();
                document.getElementById('total-quejas').textContent = data.totalQuejas || 0;
            } else {
                // Cargar estadísticas del usuario
                const response = await fetch(`http://localhost:8085/api/quejas/usuario/${this.user.usuario}`, {
                    headers: { 'Authorization': `Bearer ${localStorage.getItem('token')}` }
                });
                const data = await response.json();
                document.getElementById('mis-quejas').textContent = data.total || 0;
            }
        } catch (error) {
            console.error('Error cargando stats:', error);
        }
    }
    
    async loadRecentQuejas() {
        // Implementar carga de quejas recientes
    }
}

