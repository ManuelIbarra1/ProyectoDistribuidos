// js/dashboard-profeco.js

// === VARIABLES GLOBALES ===
let statusModalInstance = null;
let currentQuejaId = null;

// ===== INICIALIZACIÓN DEL DASHBOARD DE PROFECO =====
document.addEventListener('DOMContentLoaded', function() {
    console.log('Dashboard PROFECO cargado');

    // Inicializar el modal de Bootstrap
    const statusModalEl = document.getElementById('statusModal');
    if (statusModalEl) {
        statusModalInstance = new bootstrap.Modal(statusModalEl);
    }
    
    // Verificar autenticación y rol
    if (!window.authService || !window.authService.isAuthenticated()) {
        console.warn('Usuario no autenticado, redirigiendo a login...');
        window.location.href = 'login.html';
        return;
    }
    
    const user = window.authService.getCurrentUser();
    if (!user || user.rol !== 'profeco') {
        console.error('Acceso denegado. Se requiere rol de PROFECO.');
        authService.logout(); // Cerrar sesión si el rol es incorrecto
        return;
    }
    
    // Mostrar información del usuario
    document.getElementById('user-info-nav').innerHTML = `
        <i class="bi bi-person-circle me-1"></i> ${user.usuario}
        <span class="badge bg-danger">${user.rol}</span>
    `;
    
    // Mensaje de bienvenida
    document.getElementById('welcome-title').textContent = `Bienvenido, ${user.usuario}`;
    document.getElementById('welcome-text').textContent = `Rol: ${user.rol} - Desde aquí puedes gestionar todas las quejas de los consumidores.`;
    
    // Cargar contenido inicial del dashboard
    loadBuzonDeQuejas();
});

async function loadBuzonDeQuejas() {
    const quejasContainer = document.getElementById('quejas-list-container');
    quejasContainer.innerHTML = '<h2>Cargando quejas del sistema...</h2>';

    try {
        const data = await apiClient.get(`/profeco-webapp/api/quejas`);
        
        const quejasCountEl = document.getElementById('quejas-count');
        if (quejasCountEl) {
            quejasCountEl.textContent = data.total || 0;
        }

        let quejasHtml = `
            <div class="card">
                <div class="card-header d-flex justify-content-between align-items-center bg-light">
                    <h5 class="mb-0"><i class="bi bi-inbox-fill me-2"></i>Buzón de Quejas</h5>
                    <span class="badge bg-primary rounded-pill">${data.total || 0} quejas totales</span>
                </div>
                <div class="card-body">
        `;

        if (data.quejas && data.quejas.length > 0) {
            quejasHtml += '<ul class="list-group">';
            data.quejas.forEach(queja => {
                const estado = queja.estado || 'recibida';
                let estadoClass = 'secondary'; // Color por defecto
                switch (estado.toLowerCase()) {
                    case 'recibida':
                        estadoClass = 'info';
                        break;
                    case 'aceptada':
                        estadoClass = 'success';
                        break;
                    case 'en espera':
                        estadoClass = 'warning';
                        break;
                    case 'devuelta':
                        estadoClass = 'dark';
                        break;
                    case 'rechazado':
                        estadoClass = 'danger';
                        break;
                }

                quejasHtml += `
                    <li class="list-group-item queja-card mb-3 shadow-sm">
                        <div class="d-flex w-100 justify-content-between">
                            <h6 class="mb-1 fw-bold">${queja.titulo}</h6>
                            <span class="badge bg-${estadoClass}">${estado}</span>
                        </div>
                        <p class="mb-1">${queja.descripcion}</p>
                        <hr>
                        <div class="row">
                            <div class="col-md-6">
                                <small class="text-muted"><strong>Consumidor:</strong> ${queja.usuario}</small><br>
                                <small class="text-muted"><strong>Comercio:</strong> ${queja.comercio}</small>
                            </div>
                            <div class="col-md-6 text-md-end">
                                <small class="text-muted"><strong>ID:</strong> ${queja.quejaId}</small><br>
                                <small class="text-muted"><strong>Fecha:</strong> ${new Date(queja.fecha).toLocaleString()}</small>
                            </div>
                        </div>
                        <div class="mt-3 text-end">
                            <button class="btn btn-sm btn-outline-primary" onclick="changeQuejaStatus('${queja.quejaId}', '${queja.estado}')">
                                <i class="bi bi-pencil-square me-1"></i>Cambiar Estado
                            </button>
                            <button class="btn btn-sm btn-outline-danger" onclick="deleteQueja('${queja.quejaId}')">
                                <i class="bi bi-trash-fill me-1"></i>Eliminar
                            </button>
                        </div>
                    </li>
                `;
            });
            quejasHtml += '</ul>';
        } else {
            quejasHtml += '<p class="text-center p-4">No hay quejas registradas en el sistema.</p>';
        }

        quejasHtml += `
                </div>
            </div>
        `;
        quejasContainer.innerHTML = quejasHtml;

    } catch (error) {
        console.error('Error cargando todas las quejas:', error);
        quejasContainer.innerHTML = `<div class="alert alert-danger">Error al cargar las quejas: ${error.message}</div>`;
    }
}

function changeQuejaStatus(quejaId, currentStatus) {
    currentQuejaId = quejaId; // Guardar el ID de la queja actual
    
    // Actualizar el contenido del modal
    document.getElementById('modal-queja-id').textContent = quejaId;
    document.getElementById('new-status-select').value = currentStatus;

    // Mostrar el modal
    if (statusModalInstance) {
        statusModalInstance.show();
    }
}

async function saveNewStatus() {
    const nuevoEstado = document.getElementById('new-status-select').value;
    
    if (currentQuejaId && nuevoEstado) {
        try {
            await apiClient.post(`/profeco-webapp/api/quejas/${currentQuejaId}?_method=PUT`, { estado: nuevoEstado });
            alert('Estado de la queja actualizado con éxito.');
            
            if (statusModalInstance) {
                statusModalInstance.hide();
            }
            
            loadBuzonDeQuejas(); // Recargar la lista
            
        } catch (error) {
            alert(`Error al actualizar el estado: ${error.message}`);
        }
    }
}

async function deleteQueja(quejaId) {
    if (confirm('¿Está seguro de que desea eliminar esta queja? Esta acción no se puede deshacer.')) {
        try {
            await apiClient.post(`/profeco-webapp/api/quejas/${quejaId}?_method=DELETE`, {});
            alert('Queja eliminada con éxito.');
            loadBuzonDeQuejas(); // Recargar la lista
        } catch (error) {
            alert(`Error al eliminar la queja: ${error.message}`);
        }
    }
}

function logout() {
    if (window.authService) {
        if (confirm('¿Estás seguro de cerrar sesión?')) {
            window.authService.logout();
        }
    } else {
        // Si authService no está, igual redirigir
        localStorage.clear();
        window.location.href = 'login.html';
    }
}

// Hacer las funciones globales para que el onclick del HTML las encuentre
window.loadBuzonDeQuejas = loadBuzonDeQuejas;
window.saveNewStatus = saveNewStatus;
window.changeQuejaStatus = changeQuejaStatus;

