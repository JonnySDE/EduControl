const API_URL = 'http://localhost:7000';

document.addEventListener('DOMContentLoaded', async () => {
    try {
        const response = await fetch(`${API_URL}/session`, {
            method: 'GET',
            credentials: 'include' // manda la cookie de sesión
        });

        if (!response.ok) {
            // No hay sesión activa: regresamos al login (un nivel arriba de Paginas/)
            window.location.href = '../index.html';
            return;
        }

        const sesion = await response.json();
        // sesion = { idUsuario, rol, nombreUsuario }

        personalizarVista(sesion);

    } catch (error) {
        console.error('Error verificando sesión:', error);
        window.location.href = '../index.html';
    }
});

function personalizarVista(sesion) {
    const nombreCompleto = sessionStorage.getItem('nombreCompleto') || sesion.nombreUsuario;

    // Cambiamos el título de la tarjeta y el rol mostrado según quién inició sesión
    const tituloRolElemento = document.querySelector('.columna-info:nth-child(5) h3');
    const nombreRolElemento = document.querySelector('.columna-info:nth-child(5) .dato-info p');

    if (tituloRolElemento && nombreRolElemento) {
        if (sesion.rol === 'Docente') {
            tituloRolElemento.textContent = 'Docente';
            nombreRolElemento.textContent = nombreCompleto;
        } else {
            tituloRolElemento.textContent = 'Director';
            nombreRolElemento.textContent = nombreCompleto;
        }
    }
}