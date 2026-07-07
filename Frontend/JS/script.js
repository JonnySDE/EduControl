// Esperamos a que todo el HTML cargue
document.addEventListener('DOMContentLoaded', () => {
    const btnDirector = document.getElementById('btn-director');
    const btnDocente = document.getElementById('btn-docente');

    // Función para cambiar la clase activa
    function cambiarRolActivo(botonSeleccionado, botonInactivo) {
        botonSeleccionado.classList.add('activo');
        botonInactivo.classList.remove('activo');
    }

    // Eventos de clic
    btnDirector.addEventListener('click', () => {
        cambiarRolActivo(btnDirector, btnDocente);
    });

    btnDocente.addEventListener('click', () => {
        cambiarRolActivo(btnDocente, btnDirector);
    });

    // Opcional: Evitar que el formulario recargue la página al hacer pruebas
    const formulario = document.getElementById('formulario-login');
    formulario.addEventListener('submit', (e) => {
        e.preventDefault();
        console.log("Intentando iniciar sesión...");
        // Aquí iría tu lógica de conexión con la base de datos
    });
});

