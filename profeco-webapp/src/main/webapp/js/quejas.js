document.addEventListener('DOMContentLoaded', () => {
    const quejaForm = document.getElementById('queja-form');
    const quejaAlert = document.getElementById('queja-alert');

    // Verificar si el formulario existe en la página actual
    if (quejaForm) {
        quejaForm.addEventListener('submit', async (e) => {
            e.preventDefault();

            // Mostrar un indicador de carga
            showAlert('Enviando queja...', 'info');

            const titulo = document.getElementById('titulo').value;
            const comercio = document.getElementById('comercio').value;
            const descripcion = document.getElementById('descripcion').value;
            
            const user = authService.getCurrentUser();

            if (!user || !user.usuario) {
                showAlert('Error: No se pudo obtener la información del usuario. Por favor, inicie sesión de nuevo.', 'danger');
                setTimeout(() => authService.logout(), 3000);
                return;
            }

            const quejaData = {
                usuario: user.usuario,
                titulo: titulo,
                descripcion: descripcion,
                comercio: comercio
            };

            try {
                // Usar el ApiServlet como proxy
                const response = await apiClient.post('/profeco-webapp/api/quejas', quejaData);

                showAlert('¡Queja registrada con éxito! Redirigiendo al dashboard...', 'success');
                console.log('Queja creada:', response);

                // Limpiar formulario y redirigir
                quejaForm.reset();
                setTimeout(() => {
                    window.location.href = 'dashboard.html';
                }, 2000);

            } catch (error) {
                console.error('Error al registrar la queja:', error);
                const errorMessage = error.message || 'Ocurrió un error desconocido. Revisa la consola para más detalles.';
                showAlert(`Error al registrar la queja: ${errorMessage}`, 'danger');
            }
        });
    }

    /**
     * Muestra una alerta en el div 'queja-alert'.
     * @param {string} message - El mensaje a mostrar.
     * @param {string} type - El tipo de alerta (e.g., 'success', 'danger', 'info').
     */
    function showAlert(message, type) {
        if (quejaAlert) {
            quejaAlert.innerHTML = `
                <div class="alert alert-${type} alert-dismissible fade show" role="alert">
                    ${message}
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
            `;
            quejaAlert.classList.remove('d-none');
        }
    }
    
    // Si estamos en el dashboard, cargar las quejas
    if (window.location.pathname.endsWith('dashboard.html')) {
        // La lógica para cargar quejas en el dashboard iría aquí
        console.log('En el dashboard. La carga de quejas se manejará en dashboard.js');
    }
});