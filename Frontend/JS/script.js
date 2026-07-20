/// URL base del backend
const API_URL = 'http://localhost:7000';

document.addEventListener('DOMContentLoaded', () => {
    const btnDirector = document.getElementById('btn-director');
    const btnDocente = document.getElementById('btn-docente');
    const formulario = document.getElementById('formulario-login');
    const inputUsuario = document.getElementById('usuario');
    const inputPassword = document.getElementById('password');

    let rolSeleccionado = 'Director'; // por defecto, coincide con el botón activo inicial

    // Función para cambiar la clase activa
    function cambiarRolActivo(botonSeleccionado, botonInactivo, rol) {
        botonSeleccionado.classList.add('activo');
        botonInactivo.classList.remove('activo');
        rolSeleccionado = rol;
    }

    btnDirector.addEventListener('click', () => {
        cambiarRolActivo(btnDirector, btnDocente, 'Director');
    });

    btnDocente.addEventListener('click', () => {
        cambiarRolActivo(btnDocente, btnDirector, 'Docente');
    });

    // Muestra un mensaje de error debajo del formulario
    function mostrarError(mensaje) {
        let errorDiv = document.getElementById('mensaje-error');
        if (!errorDiv) {
            errorDiv = document.createElement('p');
            errorDiv.id = 'mensaje-error';
            errorDiv.style.color = '#e63946';
            errorDiv.style.marginTop = '10px';
            errorDiv.style.textAlign = 'center';
            formulario.appendChild(errorDiv);
        }
        errorDiv.textContent = mensaje;
    }

    function limpiarError() {
        const errorDiv = document.getElementById('mensaje-error');
        if (errorDiv) errorDiv.remove();
    }

    formulario.addEventListener('submit', async (e) => {
        e.preventDefault();
        limpiarError();

        const nombreUsuario = inputUsuario.value.trim();
        const contrasena = inputPassword.value;

        if (!nombreUsuario || !contrasena) {
            mostrarError('Debes ingresar usuario y contraseña.');
            return;
        }

        const botonSubmit = formulario.querySelector('.boton-ingresar');
        botonSubmit.disabled = true;
        botonSubmit.textContent = 'Ingresando...';

        try {
            const response = await fetch(`${API_URL}/login`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                credentials: 'include', // IMPORTANTE: para que el navegador guarde la cookie de sesión
                body: JSON.stringify({
                    nombreUsuario: nombreUsuario,
                    contrasena: contrasena
                })
            });

            if (response.status === 401) {
                mostrarError('Usuario o contraseña incorrectos.');
                return;
            }

            if (!response.ok) {
                const texto = await response.text();
                mostrarError('Error del servidor: ' + texto);
                return;
            }

            const usuario = await response.json();

            // Validación opcional: si el rol seleccionado en el botón no coincide con el rol real
            if (usuario.rol !== rolSeleccionado) {
                mostrarError(`Este usuario corresponde al rol "${usuario.rol}", no a "${rolSeleccionado}".`);
                // Cerramos la sesión que se acaba de abrir, ya que el rol elegido no coincide
                await fetch(`${API_URL}/logout`, { method: 'POST', credentials: 'include' });
                return;
            }

            // Guardamos algunos datos básicos para mostrarlos en bienvenida.html
            sessionStorage.setItem('idUsuario', usuario.idUsuario);
            sessionStorage.setItem('rol', usuario.rol);
            sessionStorage.setItem('nombreUsuario', usuario.nombreUsuario);
            sessionStorage.setItem('nombreCompleto', `${usuario.nombre} ${usuario.apellidoPaterno} ${usuario.apellidoMaterno}`);

            // Redirigimos siempre a Bienvenida.html (director y docente) - está dentro de Paginas/
            window.location.href = 'Paginas/Bienvenida.html';

        } catch (error) {
            console.error('Error de conexión:', error);
            mostrarError('No se pudo conectar con el servidor. Verifica que el backend esté corriendo.');
        } finally {
            botonSubmit.disabled = false;
            botonSubmit.textContent = 'Iniciar Sesión';
        }
    });
});

// MOSTRAR / OCULTAR CONTRASEÑA
document.addEventListener('DOMContentLoaded', () => {
    const inputPassword = document.getElementById('password');
    const iconoOjo = document.getElementById('icono-ojo-password');

    if (inputPassword && iconoOjo) {
        iconoOjo.addEventListener('click', () => {
            const esPassword = inputPassword.type === 'password';
            inputPassword.type = esPassword ? 'text' : 'password';

            // Cambia el ícono según el estado (ajusta las rutas a como tú las acomodes)
            iconoOjo.src = esPassword ? 'Img/IconosAcciones/ojoAbierto.svg' : 'Img/IconosAcciones/ojoCerrado.svg';
            iconoOjo.alt = esPassword ? 'Ocultar contraseña' : 'Mostrar contraseña';
        });
    }
});