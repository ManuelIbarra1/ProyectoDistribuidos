// quejas.js - Servicio de quejas
class QuejasService {
    constructor() {
        this.api = apiClient;
    }

    async crearQueja(quejaData) {
        try {
            const response = await this.api.post('/api/quejas', quejaData);
            
            if (response.mensaje) {
                return response;
            } else {
                throw new Error(response.error || 'Error al crear queja');
            }
        } catch (error) {
            console.error('Error creating queja:', error);
            throw error;
        }
    }

    async getMyQuejas() {
        try {
            const user = authService.getCurrentUser();
            const response = await this.api.get(`/api/quejas/usuario/${user.usuario}`);
            
            if (response.quejas) {
                return response.quejas;
            } else {
                throw new Error(response.error || 'Error al obtener quejas');
            }
        } catch (error) {
            console.error('Error getting quejas:', error);
            throw error;
        }
    }

    async getAllQuejas() {
        if (!authService.isProfeco()) {
            throw new Error('No autorizado');
        }
        
        try {
            const response = await this.api.get('/api/quejas');
            return response.quejas || [];
        } catch (error) {
            console.error('Error getting all quejas:', error);
            throw error;
        }
    }

    async getStats() {
        if (!authService.isProfeco()) {
            // Para consumidores, solo contar sus quejas
            const quejas = await this.getMyQuejas();
            return { total: quejas.length };
        }
        
        try {
            const response = await this.api.get('/api/quejas/estadisticas/total');
            return response;
        } catch (error) {
            console.error('Error getting stats:', error);
            throw error;
        }
    }
}

const quejasService = new QuejasService();

