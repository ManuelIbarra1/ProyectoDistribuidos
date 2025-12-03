// quejas.js - Servicio para manejar quejas
console.log('=== quejas.js CARGADO ===');

if (typeof window.quejasService === 'undefined') {
    
    window.quejasService = {
        baseUrl: 'http://localhost:8085',
        
        async crearQueja(titulo, descripcion, comercio) {
            console.log(' Creando nueva queja...');
            
            const url = `${this.baseUrl}/api/quejas`;
            
            try {
                const token = localStorage.getItem('token');
                if (!token) {
                    throw new Error('No autenticado');
                }
                
                const response = await fetch(url, {
                    method: 'POST',
                    headers: {
                        'Authorization': `Bearer ${token}`,
                        'Content-Type': 'application/json',
                        'Accept': 'application/json'
                    },
                    body: JSON.stringify({
                        titulo: titulo,
                        descripcion: descripcion,
                        comercio: comercio
                    })
                });
                
                console.log(' Response status:', response.status);
                
                if (!response.ok) {
                    const errorText = await response.text();
                    console.error(' Error creando queja:', errorText);
                    
                    try {
                        const errorJson = JSON.parse(errorText);
                        throw new Error(errorJson.error || `Error ${response.status}`);
                    } catch {
                        throw new Error(`Error ${response.status}: ${errorText}`);
                    }
                }
                
                const data = await response.json();
                console.log(' Queja creada exitosamente:', data);
                return data;
                
            } catch (error) {
                console.error(' Error completo:', error);
                throw error;
            }
        },
        
        async obtenerMisQuejas() {
            console.log(' Obteniendo mis quejas...');
            
            const url = `${this.baseUrl}/api/quejas/mis-quejas`;
            
            try {
                const token = localStorage.getItem('token');
                if (!token) {
                    throw new Error('No autenticado');
                }
                
                const response = await fetch(url, {
                    method: 'GET',
                    headers: {
                        'Authorization': `Bearer ${token}`,
                        'Accept': 'application/json'
                    }
                });
                
                console.log(' Response status:', response.status);
                
                if (!response.ok) {
                    const errorText = await response.text();
                    console.error(' Error obteniendo quejas:', errorText);
                    throw new Error(`Error ${response.status}: ${errorText}`);
                }
                
                const data = await response.json();
                console.log(` ${data.total} quejas obtenidas`);
                return data.quejas || [];
                
            } catch (error) {
                console.error(' Error obteniendo quejas:', error);
                throw error;
            }
        },
        
        async obtenerTodasLasQuejas() {
            console.log('️ Obteniendo todas las quejas...');
            
            const url = `${this.baseUrl}/api/quejas`;
            
            try {
                const token = localStorage.getItem('token');
                if (!token) {
                    throw new Error('No autenticado');
                }
                
                const response = await fetch(url, {
                    method: 'GET',
                    headers: {
                        'Authorization': `Bearer ${token}`,
                        'Accept': 'application/json'
                    }
                });
                
                console.log(' Response status:', response.status);
                
                if (!response.ok) {
                    const errorText = await response.text();
                    console.error(' Error obteniendo todas las quejas:', errorText);
                    throw new Error(`Error ${response.status}: ${errorText}`);
                }
                
                const data = await response.json();
                console.log(` ${data.total} quejas obtenidas (todas)`);
                return data.quejas || [];
                
            } catch (error) {
                console.error(' Error obteniendo todas las quejas:', error);
                throw error;
            }
        },
        
        async obtenerQuejaPorId(quejaId) {
            console.log(` Obteniendo queja ID: ${quejaId}`);
            
            const url = `${this.baseUrl}/api/quejas/${quejaId}`;
            
            try {
                const token = localStorage.getItem('token');
                if (!token) {
                    throw new Error('No autenticado');
                }
                
                const response = await fetch(url, {
                    method: 'GET',
                    headers: {
                        'Authorization': `Bearer ${token}`,
                        'Accept': 'application/json'
                    }
                });
                
                console.log(' Response status:', response.status);
                
                if (!response.ok) {
                    if (response.status === 404) {
                        throw new Error('Queja no encontrada');
                    }
                    const errorText = await response.text();
                    throw new Error(`Error ${response.status}: ${errorText}`);
                }
                
                const queja = await response.json();
                console.log(' Queja obtenida:', queja.quejaId);
                return queja;
                
            } catch (error) {
                console.error(' Error obteniendo queja:', error);
                throw error;
            }
        },
        
        async obtenerEstadisticasResumen() {
            console.log(' Obteniendo estadísticas...');
            
            const url = `${this.baseUrl}/api/quejas/estadisticas/resumen`;
            
            try {
                const token = localStorage.getItem('token');
                if (!token) {
                    throw new Error('No autenticado');
                }
                
                const response = await fetch(url, {
                    method: 'GET',
                    headers: {
                        'Authorization': `Bearer ${token}`,
                        'Accept': 'application/json'
                    }
                });
                
                console.log(' Response status:', response.status);
                
                if (!response.ok) {
                    const errorText = await response.text();
                    console.error(' Error obteniendo estadísticas:', errorText);
                    throw new Error(`Error ${response.status}: ${errorText}`);
                }
                
                const data = await response.json();
                console.log(' Estadísticas obtenidas:', data);
                return data;
                
            } catch (error) {
                console.error(' Error obteniendo estadísticas:', error);
                throw error;
            }
        },
        
        async obtenerQuejasPorUsuario(usuario) {
            console.log(` Obteniendo quejas de usuario: ${usuario}`);
            
            const url = `${this.baseUrl}/api/quejas/usuario/${encodeURIComponent(usuario)}`;
            
            try {
                const token = localStorage.getItem('token');
                if (!token) {
                    throw new Error('No autenticado');
                }
                
                const response = await fetch(url, {
                    method: 'GET',
                    headers: {
                        'Authorization': `Bearer ${token}`,
                        'Accept': 'application/json'
                    }
                });
                
                console.log(' Response status:', response.status);
                
                if (!response.ok) {
                    const errorText = await response.text();
                    console.error(' Error obteniendo quejas de usuario:', errorText);
                    throw new Error(`Error ${response.status}: ${errorText}`);
                }
                
                const data = await response.json();
                console.log(` ${data.total} quejas obtenidas para ${usuario}`);
                return data.quejas || [];
                
            } catch (error) {
                console.error(' Error obteniendo quejas de usuario:', error);
                throw error;
            }
        }
    };
    
    console.log(' quejasService creado exitosamente');
} else {
    console.log('ℹ️ quejasService ya estaba definido');
}