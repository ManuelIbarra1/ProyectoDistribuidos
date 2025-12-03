// auth.js - VERSIÓN COMPLETA CON REDIRECCIÓN
console.log('=== auth.js CARGADO para API Gateway ===');

if (typeof window.authService === 'undefined') {
    
    window.authService = {
        // ✅ URL CORRECTA: API Gateway en 8085
        baseUrl: 'http://localhost:8085',
        
        // ========== MÉTODO REGISTRAR ==========
        async registrar(usuario, contrasena, rol = 'consumidor') {
            console.log('📝 Registrando usuario...');
            
            const url = `${this.baseUrl}/api/auth/registro`;
            
            const registroData = {
                usuario: usuario.trim(),
                contrasena: contrasena.trim(),
                rol: rol.trim()
            };
            
            console.log('📤 Enviando:', registroData);
            
            try {
                const response = await fetch(url, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Accept': 'application/json'
                    },
                    mode: 'cors',
                    body: JSON.stringify(registroData)
                });
                
                console.log('📥 Response status:', response.status);
                
                const responseText = await response.text();
                console.log('📥 Response body:', responseText);
                
                if (!response.ok) {
                    let errorMessage = `Error ${response.status}`;
                    try {
                        const errorJson = JSON.parse(responseText);
                        errorMessage = errorJson.error || errorJson.message || responseText;
                    } catch {
                        errorMessage = responseText || `Error ${response.status}`;
                    }
                    throw new Error(errorMessage);
                }
                
                let data;
                try {
                    data = JSON.parse(responseText);
                } catch {
                    data = { mensaje: 'Registro exitoso' };
                }
                
                console.log('✅ Registro exitoso:', data);
                return data;
                
            } catch (error) {
                console.error('💥 Error en registro:', error);
                throw error;
            }
        },
        
        // ========== MÉTODO LOGIN ==========
        async login(usuario, contrasena) {
            console.log('🔐 Login para:', usuario);
            
            const loginData = {
                usuario: usuario.trim(),
                contrasena: contrasena.trim()
            };
            
            console.log('📤 Datos de login:', loginData);
            
            const url = `${this.baseUrl}/api/auth/login`;
            
            try {
                const response = await fetch(url, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Accept': 'application/json'
                    },
                    mode: 'cors',
                    body: JSON.stringify(loginData)
                });
                
                console.log('📥 Response status:', response.status);
                
                if (!response.ok) {
                    const errorText = await response.text();
                    console.error('❌ Error login:', errorText);
                    
                    let errorMessage;
                    try {
                        const errorJson = JSON.parse(errorText);
                        errorMessage = errorJson.error || errorJson.message || `Error ${response.status}`;
                    } catch {
                        errorMessage = errorText || `Error ${response.status}`;
                    }
                    throw new Error(errorMessage);
                }
                
                const data = await response.json();
                console.log('✅ Login exitoso!', data);
                
                // ⭐⭐⭐ ESTA ES LA LÍNEA CRÍTICA QUE TE FALTA ⭐⭐⭐
                return this.handleSuccessfulLogin(data, usuario);
                
            } catch (error) {
                console.error('💥 Error en login:', error);
                throw error;
            }
        },
        
        // ========== MANEJO DE LOGIN EXITOSO ==========
        handleSuccessfulLogin(data, usuarioOriginal) {
            console.log('🎯 Procesando login exitoso...');
            
            if (data.token) {
                // Extraer información del usuario
                let userRol = data.rol || 'consumidor';
                let userNombre = data.usuario || usuarioOriginal;
                
                console.log('👤 Información del usuario:');
                console.log('  - Rol:', userRol);
                console.log('  - Nombre:', userNombre);
                console.log('  - Token recibido:', data.token ? 'Sí (' + data.token.length + ' chars)' : 'No');
                
                // Guardar en localStorage
                const userData = {
                    id: data.id || Date.now(),
                    usuario: userNombre,
                    correo: usuarioOriginal.includes('@') ? usuarioOriginal : '',
                    rol: userRol,
                    nombre: userNombre,
                    fechaLogin: new Date().toISOString(),
                    token: data.token
                };
                
                localStorage.setItem('token', data.token);
                localStorage.setItem('user', JSON.stringify(userData));
                
                console.log('💾 Datos guardados en localStorage');
                
                // ⭐⭐⭐ REDIRIGIR SEGÚN EL ROL ⭐⭐⭐
                this.redirectByRole(userRol);
                
                return data;
            } else {
                console.error('❌ No se recibió token en la respuesta');
                throw new Error('No se recibió token de autenticación');
            }
        },
        
        // ========== REDIRECCIÓN POR ROL ==========
        redirectByRole(role) {
            console.log('🔄 Redirigiendo por rol:', role);
            
            // Normalizar el rol (minúsculas, sin espacios)
            const roleNormalized = (role || '').toLowerCase().trim();
            
            let redirectUrl = 'dashboard.html'; // Por defecto
            
            // Mapeo de roles a dashboards
            if (roleNormalized === 'profeco' || 
                roleNormalized === 'admin' || 
                roleNormalized === 'administrador' || 
                roleNormalized === 'supervisor') {
                redirectUrl = 'dashboard-profeco.html';
            } else if (roleNormalized === 'consumidor' || 
                       roleNormalized === 'usuario' || 
                       roleNormalized === 'cliente') {
                redirectUrl = 'dashboard-consumidor.html';
            } else {
                console.warn(`⚠️ Rol no reconocido: "${role}", usando dashboard por defecto`);
            }
            
            console.log(`🎯 Redirigiendo a: ${redirectUrl}`);
            console.log('⏱️ Esperando 1 segundo antes de redirigir...');
            
            // Redirigir después de 1 segundo (para ver mensajes)
            setTimeout(() => {
                console.log('🚀 Redirigiendo ahora...');
                window.location.href = redirectUrl;
            }, 1000);
        },
        
        // ========== UTILIDADES ==========
        logout() {
            console.log('🚪 Cerrando sesión...');
            localStorage.clear();
            window.location.href = 'login.html';
        },
        
        isAuthenticated() {
            const token = localStorage.getItem('token');
            if (!token) {
                console.log('❌ No hay token en localStorage');
                return false;
            }
            
            try {
                // Verificar formato JWT básico
                const parts = token.split('.');
                if (parts.length !== 3) return false;
                
                // Verificar expiración (opcional)
                const payload = JSON.parse(atob(parts[1]));
                const exp = payload.exp * 1000; // JWT exp está en segundos
                return Date.now() < exp;
                
            } catch (error) {
                console.error('Error verificando token:', error);
                return false;
            }
        },
        
        getCurrentUser() {
            const userStr = localStorage.getItem('user');
            if (!userStr) return null;
            
            try {
                return JSON.parse(userStr);
            } catch (error) {
                console.error('Error parseando usuario:', error);
                return null;
            }
        },
        
        getUserRole() {
            const user = this.getCurrentUser();
            return user ? user.rol : null;
        },
        
        isConsumer() {
            const role = this.getUserRole();
            const roleLower = (role || '').toLowerCase();
            return roleLower === 'consumidor' || 
                   roleLower === 'usuario' || 
                   roleLower === 'cliente';
        },
        
        isAdmin() {
            const role = this.getUserRole();
            const roleLower = (role || '').toLowerCase();
            return roleLower === 'profeco' || 
                   roleLower === 'admin' || 
                   roleLower === 'administrador' || 
                   roleLower === 'supervisor';
        },
        
        async validateToken() {
            const token = localStorage.getItem('token');
            if (!token) return false;
            
            try {
                const response = await fetch(`${this.baseUrl}/api/auth/validar`, {
                    method: 'POST',
                    headers: {
                        'Authorization': `Bearer ${token}`,
                        'Content-Type': 'application/json'
                    }
                });
                
                if (!response.ok) return false;
                const data = await response.json();
                return data.valido === true;
            } catch (error) {
                console.error('Error validando token:', error);
                return false;
            }
        },
        
        // Método para hacer requests autenticadas
        async authenticatedFetch(endpoint, options = {}) {
            const token = localStorage.getItem('token');
            if (!token) throw new Error('No autenticado');
            
            const defaultOptions = {
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                }
            };
            
            const finalOptions = { ...defaultOptions, ...options };
            const url = `${this.baseUrl}${endpoint}`;
            
            const response = await fetch(url, finalOptions);
            
            if (response.status === 401) {
                this.logout();
                throw new Error('Sesión expirada');
            }
            
            if (!response.ok) {
                throw new Error(`HTTP ${response.status}: ${await response.text()}`);
            }
            
            return response.json();
        }
    };
    
    console.log('✅ authService creado con redirección por rol');
}

console.log('authService disponible:', typeof window.authService !== 'undefined');