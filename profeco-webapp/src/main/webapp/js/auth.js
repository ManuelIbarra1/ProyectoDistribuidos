// auth.js - Versión corregida y simplificada
console.log('=== auth.js CARGADO ===');

if (typeof window.authService === 'undefined') {
    
    window.authService = {
        baseUrl: 'http://localhost:8085',
        
        async login(usuario, contrasena) {
            console.log('🔐 Login para:', usuario);
            
            const url = `${this.baseUrl}/api/auth/login`;
            console.log('🌐 URL de login:', url);
            
            try {
                // PRIMERO: Probar conexión básica
                console.log('🧪 Probando conexión...');
                const testResponse = await fetch(`${this.baseUrl}/api/auth/health`);
                console.log('✅ Conexión OK, status:', testResponse.status);
                
                // SEGUNDO: Hacer login
                console.log('📤 Enviando credenciales...');
                const response = await fetch(url, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Accept': 'application/json'
                    },
                    body: JSON.stringify({ usuario, contrasena })
                });
                
                console.log('📥 Response status:', response.status, response.statusText);
                
                // Verificar headers CORS
                console.log('🛡️ Headers CORS:');
                const corsHeader = response.headers.get('Access-Control-Allow-Origin');
                console.log('Access-Control-Allow-Origin:', corsHeader);
                console.log('Access-Control-Allow-Methods:', response.headers.get('Access-Control-Allow-Methods'));
                console.log('Access-Control-Allow-Headers:', response.headers.get('Access-Control-Allow-Headers'));
                
                if (!response.ok) {
                    const errorText = await response.text();
                    console.error('❌ Error del servidor:', errorText);
                    throw new Error(`Error ${response.status}: ${errorText}`);
                }
                
                const data = await response.json();
                console.log('✅ Login exitoso!', data);
                
                if (data.token) {
                    // Guardar en localStorage
                    localStorage.setItem('token', data.token);
                    localStorage.setItem('user', JSON.stringify({
                        usuario: data.usuario,
                        rol: data.rol
                    }));
                    
                    // Mostrar mensaje y redirigir
                    const alertDiv = document.getElementById('login-alert');
                    if (alertDiv) {
                        alertDiv.innerHTML = `
                            <div class="alert alert-success alert-dismissible fade show" role="alert">
                                <strong>¡Login exitoso!</strong> Redirigiendo al dashboard...
                                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                            </div>
                        `;
                        alertDiv.classList.remove('d-none');
                    }
                    
                    // Redirigir después de 1.5 segundos según el rol
                    setTimeout(() => {
                        if (data.rol === 'profeco') {
                            window.location.href = 'dashboard-profeco.html';
                        } else {
                            window.location.href = 'dashboard.html';
                        }
                    }, 1500);
                    
                    return data;
                } else {
                    throw new Error('No se recibió token del servidor');
                }
                
            } catch (error) {
                console.error('💥 Error completo:', error);
                
                // Mostrar error en pantalla
                const alertDiv = document.getElementById('login-alert');
                if (alertDiv) {
                    let errorMessage = error.message;
                    
                    // Mensajes amigables
                    if (error.message.includes('Failed to fetch')) {
                        errorMessage = 'Error de conexión. Verifica que:<br>' +
                                      '1. API Gateway (puerto 8085) esté corriendo<br>' +
                                      '2. Auth Service (puerto 8081) esté corriendo<br>' +
                                      '3. No haya problemas de CORS';
                    } else if (error.message.includes('CORS')) {
                        errorMessage = 'Error de CORS. Soluciones:<br>' +
                                      '1. Usa Chrome con: <code>chrome.exe --disable-web-security --user-data-dir="C:/Temp"</code><br>' +
                                      '2. O instala extensión "Allow CORS"<br>' +
                                      '3. O usa Firefox para desarrollo';
                    }
                    
                    alertDiv.innerHTML = `
                        <div class="alert alert-danger alert-dismissible fade show" role="alert">
                            <strong>Error:</strong> ${errorMessage}
                            <hr>
                            <small class="text-muted">Consulta la consola (F12) para más detalles</small>
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                    `;
                    alertDiv.classList.remove('d-none');
                }
                
                throw error;
            }
        },
        
        logout() {
            console.log('🚪 Cerrando sesión...');
            localStorage.removeItem('token');
            localStorage.removeItem('user');
            window.location.href = 'login.html';
        },
        
        isAuthenticated() {
            const token = localStorage.getItem('token');
            console.log('🔍 Usuario autenticado?:', !!token);
            return !!token;
        },
        
        getCurrentUser() {
            const userStr = localStorage.getItem('user');
            const user = userStr ? JSON.parse(userStr) : null;
            console.log('👤 Usuario actual:', user);
            return user;
        }
    };
    
    console.log('✅ authService creado exitosamente');
} else {
    console.log('ℹ️ authService ya estaba definido');
}