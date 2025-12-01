// js/dashboard.js

// ===== INICIALIZACIÓN DEL DASHBOARD =====
document.addEventListener('DOMContentLoaded', function() {
    console.log('Dashboard cargado');
    
    // Verificar autenticación
    if (!window.authService || !window.authService.isAuthenticated()) {
        console.warn('Usuario no autenticado, redirigiendo...');
        window.location.href = 'login.html';
        return;
    }
    
    // Mostrar información del usuario
    const user = window.authService.getCurrentUser();
    if (user) {
        // Navbar
        document.getElementById('user-info-nav').innerHTML = `
            <i class="bi bi-person-circle me-1"></i> ${user.usuario}
            <span class="badge bg-${user.rol === 'profeco' ? 'danger' : 'success'}">
                ${user.rol}
            </span>
        `;
        
        // Mensaje de bienvenida
        document.getElementById('welcome-title').textContent = `Bienvenido, ${user.usuario}`;
        document.getElementById('welcome-text').textContent = 
            `Rol: ${user.rol} - ${user.rol === 'profeco' ? 'Panel de administración' : 'Panel de consumidor'}`;
        
        // Cargar contenido del dashboard
        loadDashboardContent(user);
    } else {
        window.location.href = 'login.html';
    }
});

function loadDashboardContent(user) {
    if (user.rol === 'consumidor') {
        loadMisQuejas();
    } else if (user.rol === 'profeco') {
        // Para PROFECO, podríamos cargar todas las quejas
        loadAllQuejas();
    }
}

async function loadMisQuejas() {
    const user = window.authService.getCurrentUser();
    if (!user) return;

    const quejasContainer = document.getElementById('quejas-list-container');
    quejasContainer.innerHTML = '<h2>Cargando mis quejas...</h2>';

    try {
        const data = await apiClient.get(`/profeco-webapp/api/quejas/usuario/${user.usuario}`);
        
        document.getElementById('quejas-count').textContent = data.total || 0;

        let quejasHtml = `
            <div class="card">
                <div class="card-header d-flex justify-content-between align-items-center">
                    <h5 class="mb-0"><i class="bi bi-list-check me-2"></i>Mis Quejas</h5>
                    <span>Total: ${data.total}</span>
                </div>
                <div class="card-body">
        `;

        if (data.quejas && data.quejas.length > 0) {
            quejasHtml += '<ul class="list-group">';
            data.quejas.forEach(queja => {
                const estado = queja.estado || 'Enviada';
                quejasHtml += `
                    <li class="list-group-item">
                        <div class="d-flex justify-content-between align-items-start">
                            <div>
                                <h6 class="mb-1">${queja.titulo}</h6>
                                <p class="mb-1"><strong>Comercio:</strong> ${queja.comercio}</p>
                                <p class="mb-1"><strong>Descripción:</strong> ${queja.descripcion}</p>
                            </div>
                            <span class="badge bg-info">${estado}</span>
                        </div>
                        <small class="text-muted">ID: ${queja.quejaId} - Fecha: ${new Date(queja.fecha).toLocaleString()}</small>
                        <div class="mt-2">
                            ${estado === 'Enviada' ? `<button class="btn btn-sm btn-outline-primary me-2" onclick="editQueja('${queja.quejaId}', '${queja.titulo}', '${queja.descripcion}')">Editar</button>` : ''}
                            <button class="btn btn-sm btn-outline-danger" onclick="deleteMisQueja('${queja.quejaId}')">Eliminar</button>
                        </div>
                    </li>
                `;
            });
            quejasHtml += '</ul>';
        } else {
            quejasHtml += '<p class="text-center">No tienes ninguna queja registrada.</p>';
        }

        quejasHtml += `
                </div>
            </div>
        `;
        quejasContainer.innerHTML = quejasHtml;

    } catch (error) {
        console.error('Error cargando mis quejas:', error);
        quejasContainer.innerHTML = `<div class="alert alert-danger">Error al cargar las quejas: ${error.message}</div>`;
    }
}

async function loadAllQuejas() {
    const quejasContainer = document.getElementById('quejas-list-container');
    quejasContainer.innerHTML = '<h2>Cargando todas las quejas...</h2>';

    try {
        const data = await apiClient.get(`/profeco-webapp/api/quejas`);
        
        document.getElementById('quejas-count').textContent = data.total || 0;

        let quejasHtml = `
            <div class="card">
                <div class="card-header d-flex justify-content-between align-items-center">
                    <h5 class="mb-0"><i class="bi bi-journal-text me-2"></i>Todas las Quejas</h5>
                    <span>Total: ${data.total}</span>
                </div>
                <div class="card-body">
        `;

        if (data.quejas && data.quejas.length > 0) {
            quejasHtml += '<div class="table-responsive"><table class="table table-striped">';
            quejasHtml += `
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Usuario</th>
                        <th>Título</th>
                        <th>Comercio</th>
                        <th>Estado</th>
                        <th>Acciones</th>
                    </tr>
                </thead>
                <tbody>
            `;
            data.quejas.forEach(queja => {
                quejasHtml += `
                    <tr>
                        <td>${queja.quejaId}</td>
                        <td>${queja.usuario}</td>
                        <td>${queja.titulo}</td>
                        <td>${queja.comercio}</td>
                        <td><span class="badge bg-info">${queja.estado || 'Enviada'}</span></td>
                        <td>
                            <button class="btn btn-sm btn-primary" onclick="changeQuejaStatus('${queja.quejaId}')">Cambiar Estado</button>
                            <button class="btn btn-sm btn-danger" onclick="deleteQueja('${queja.quejaId}')">Eliminar</button>
                        </td>
                    </tr>
                `;
            });
            quejasHtml += '</tbody></table></div>';
        } else {
            quejasHtml += '<p class="text-center">No hay quejas registradas en el sistema.</p>';
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


async function changeQuejaStatus(quejaId) {
    const nuevoEstado = prompt("Ingrese el nuevo estado para la queja (ej: 'En progreso', 'Resuelta'):");
    if (nuevoEstado && nuevoEstado.trim() !== '') {
        try {
            // TUNNELING PUT OVER POST
            await apiClient.post(`/profeco-webapp/api/quejas/${quejaId}?_method=PUT`, { estado: nuevoEstado });
            alert('Estado de la queja actualizado con éxito.');
            loadAllQuejas(); // Recargar la lista
        } catch (error) {
            alert(`Error al actualizar el estado: ${error.message}`);
        }
    }
}

async function deleteQueja(quejaId) {
    if (confirm('¿Está seguro de que desea eliminar esta queja? Esta acción no se puede deshacer.')) {
        try {
            // TUNNELING DELETE OVER POST
            await apiClient.post(`/profeco-webapp/api/quejas/${quejaId}?_method=DELETE`, {});
            alert('Queja eliminada con éxito.');
            loadAllQuejas(); // Recargar la lista
        } catch (error) {
            alert(`Error al eliminar la queja: ${error.message}`);
        }
    }
}

async function editQueja(quejaId, currentTitle, currentDescription) {
    const newTitle = prompt("Edite el título de la queja:", currentTitle);
    const newDescription = prompt("Edite la descripción de la queja:", currentDescription);

    if (newTitle !== null && newDescription !== null) {
        try {
            const updates = { titulo: newTitle, descripcion: newDescription };
            // TUNNELING PUT OVER POST
            await apiClient.post(`/profeco-webapp/api/quejas/${quejaId}?_method=PUT`, updates);
            alert('Queja actualizada con éxito.');
            loadMisQuejas(); // Recargar la lista
        } catch (error) {
            alert(`Error al actualizar la queja: ${error.message}`);
        }
    }
}

async function deleteMisQueja(quejaId) {
    if (confirm('¿Está seguro de que desea eliminar esta queja? Esta acción no se puede deshacer.')) {
        try {
            // TUNNELING DELETE OVER POST
            await apiClient.post(`/profeco-webapp/api/quejas/${quejaId}?_method=DELETE`, {});
            alert('Queja eliminada con éxito.');
            loadMisQuejas(); // Recargar la lista
        } catch (error) {
            alert(`Error al eliminar la queja: ${error.message}`);
        }
    }
}

// ===== FUNCIONES GLOBALES =====
function loadCrearQueja() {
    window.location.href = 'crear-queja.html';
}

function logout() {
    if (window.authService) {
        if (confirm('¿Estás seguro de cerrar sesión?')) {
            window.authService.logout();
        }
    } else {
        window.location.href = 'login.html';
    }
}

window.loadCrearQueja = loadCrearQueja;
window.loadMisQuejas = loadMisQuejas;
window.logout = logout;
window.changeQuejaStatus = changeQuejaStatus;
window.deleteQueja = deleteQueja;
window.editQueja = editQueja;
window.deleteMisQueja = deleteMisQueja;