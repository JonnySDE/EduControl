const API_URL = 'http://localhost:7000';

let idUsuarioActual = null;

function seleccionarRol(elementoSeleccionado) {
    const botones = document.querySelectorAll('.boton-rol');
    botones.forEach(btn => btn.classList.remove('active'));
    elementoSeleccionado.classList.add('active');
}

// Marca visualmente el botón de rol correcto (no editable, solo informativo)
function mostrarRol(rol) {
    const botones = document.querySelectorAll('.boton-rol');
    botones.forEach(btn => {
        btn.classList.remove('active');
        if (btn.textContent.trim() === rol) {
            btn.classList.add('active');
        }
    });
}

// --- Carga los datos reales del usuario logueado ---
async function cargarDatosUsuario() {
    try {
        // 1. Obtenemos quién está logueado
        const resSesion = await fetch(`${API_URL}/session`, {
            method: 'GET',
            credentials: 'include'
        });

        if (!resSesion.ok) {
            window.location.href = '../../index.html'; // ajusta la ruta si es necesario
            return;
        }

        const sesion = await resSesion.json();
        idUsuarioActual = sesion.idUsuario;

        // 2. Traemos los datos completos del usuario
        const resUsuario = await fetch(`${API_URL}/usuarios/${idUsuarioActual}`, {
            method: 'GET',
            credentials: 'include'
        });

        if (!resUsuario.ok) {
            mostrarToast('advertencia', 'No se pudieron cargar los datos del usuario');
            return;
        }

        const usuario = await resUsuario.json();

        // 3. Llenamos la vista con los datos reales
        const inputNombre = document.querySelector('.columna-datos input[type="text"]:not(#input-username)');
        if (inputNombre) {
            inputNombre.value = `${usuario.nombre} ${usuario.apellidoPaterno} ${usuario.apellidoMaterno}`;
        }

        const inputUsername = document.getElementById('input-username');
        if (inputUsername) {
            inputUsername.value = usuario.nombreUsuario;
        }

        mostrarRol(usuario.rol);

        const previewFoto = document.getElementById('preview-foto');
        const fotoGuardada = localStorage.getItem('fotoPerfil');
        if (previewFoto && fotoGuardada) {
            previewFoto.src = fotoGuardada;
        }

        // El campo contraseña nunca se llena con el valor real (el backend no lo manda)
        const inputPassword = document.getElementById('input-password');
        if (inputPassword) {
            inputPassword.value = '••••••••';
        }

    } catch (error) {
        console.error('Error cargando datos del usuario:', error);
        mostrarToast('advertencia', 'No se pudo conectar con el servidor');
    }
}

document.addEventListener('DOMContentLoaded', () => {

    if (window.location.pathname.includes('Cuenta.html')) {
        cargarDatosUsuario();
    }

    // --- LÓGICA PARA EDITARFOTO.HTML ---
    if (window.location.pathname.includes('EditarFoto.html')) {
        const previewFoto = document.getElementById('preview-foto');
        const inputArchivo = document.getElementById('input-archivo');
        const btnGuardar = document.getElementById('btn-guardar');
        const fotoGuardada = localStorage.getItem('fotoPerfil');

        if (fotoGuardada) {
            previewFoto.src = fotoGuardada;
        }

        inputArchivo.addEventListener('change', function () {
            const file = this.files[0];
            if (file) {
                const reader = new FileReader();
                reader.onload = (e) => {
                    previewFoto.src = e.target.result;
                };
                reader.readAsDataURL(file);
            }
        });

        btnGuardar.addEventListener('click', () => {
            localStorage.setItem('fotoPerfil', previewFoto.src);
            window.location.href = 'Cuenta.html';
        });
    }
});

// PARA SABER DONDE ESTA EL USUARIO
document.addEventListener("DOMContentLoaded", () => {
    const partes = window.location.pathname.split("/").filter(Boolean);
    const carpetaActual = partes[partes.length - 2];

    document.querySelectorAll(".menu-lateral .item-menu").forEach(enlace => {
        const hrefPartes = enlace.getAttribute("href").split("/").filter(Boolean);
        const carpetaEnlace = hrefPartes[hrefPartes.length - 2];

        if (carpetaEnlace === carpetaActual) {
            enlace.classList.add("active");
        } else {
            enlace.classList.remove("active");
        }
    });
});

/* CAMBIAR USUARIO */
function abrirModal() {
    const modal = document.getElementById("modal-editar-usuario");
    const inputUsername = document.getElementById("input-username");
    const usuarioActual = document.getElementById("usuario-actual");
    const usuarioNuevo = document.getElementById("usuario-nuevo");

    usuarioActual.value = inputUsername.value;
    usuarioNuevo.value = "";

    modal.classList.add("activo");
}

function cerrarModal() {
    const modal = document.getElementById("modal-editar-usuario");
    modal.classList.remove("activo");
}

async function guardarUsuario() {
    const nuevoValor = document.getElementById("usuario-nuevo").value.trim();

    if (nuevoValor === "") {
        mostrarToast("advertencia", "Campos vacíos, asegúrese de llenar todos los campos");
        return;
    }

    if (!idUsuarioActual) {
        mostrarToast("advertencia", "No se pudo identificar tu sesión, recarga la página");
        return;
    }

    try {
        const response = await fetch(`${API_URL}/usuarios/${idUsuarioActual}/nombre-usuario`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            credentials: 'include',
            body: JSON.stringify({ nombreUsuario: nuevoValor })
        });

        if (response.status === 409) {
            mostrarToast("advertencia", "Ese nombre de usuario ya está en uso");
            return;
        }

        if (response.status === 403) {
            mostrarToast("advertencia", "No tienes permiso para hacer este cambio");
            return;
        }

        if (!response.ok) {
            const texto = await response.text();
            mostrarToast("advertencia", "Error: " + texto);
            return;
        }

        document.getElementById("input-username").value = nuevoValor;
        cerrarModal();
        mostrarToast("exito", "Datos Guardados correctamente");

    } catch (error) {
        console.error('Error guardando usuario:', error);
        mostrarToast("advertencia", "No se pudo conectar con el servidor");
    }
}

/* MODAL PARA LA CONTRASEÑA */
function abrirModalContrasena() {
    const modal = document.getElementById("modal-editar-contrasena");
    document.getElementById("contrasena-actual").value = "";
    document.getElementById("contrasena-nueva").value = "";
    document.getElementById("contrasena-confirmar").value = "";
    modal.classList.add("activo");
}

function cerrarModalContrasena() {
    const modal = document.getElementById("modal-editar-contrasena");
    modal.classList.remove("activo");
}

async function guardarContrasena() {
    const actual = document.getElementById("contrasena-actual").value;
    const nueva = document.getElementById("contrasena-nueva").value;
    const confirmar = document.getElementById("contrasena-confirmar").value;

    if (actual === "" || nueva === "" || confirmar === "") {
        mostrarToast("advertencia", "Campos vacíos, asegúrese de llenar todos los campos");
        return;
    }

    if (nueva !== confirmar) {
        mostrarToast("advertencia", "La nueva contraseña y su confirmación no coinciden");
        return;
    }

    if (!idUsuarioActual) {
        mostrarToast("advertencia", "No se pudo identificar tu sesión, recarga la página");
        return;
    }

    try {
        const response = await fetch(`${API_URL}/usuarios/${idUsuarioActual}/contrasena`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            credentials: 'include',
            body: JSON.stringify({
                contrasenaActual: actual,
                contrasenaNueva: nueva
            })
        });

        if (response.status === 401) {
            mostrarToast("advertencia", "La contraseña actual no es correcta");
            return;
        }

        if (response.status === 403) {
            mostrarToast("advertencia", "No tienes permiso para hacer este cambio");
            return;
        }

        if (!response.ok) {
            const texto = await response.text();
            mostrarToast("advertencia", "Error: " + texto);
            return;
        }

        cerrarModalContrasena();
        mostrarToast("exito", "Datos Guardados correctamente");

    } catch (error) {
        console.error('Error guardando contraseña:', error);
        mostrarToast("advertencia", "No se pudo conectar con el servidor");
    }
}

/* CERRAR SESIÓN */
function abrirModalCerrarSesion() {
    const modal = document.getElementById("modal-cerrar-sesion");
    modal.classList.add("activo");
}

function cerrarModalCerrarSesion() {
    const modal = document.getElementById("modal-cerrar-sesion");
    modal.classList.remove("activo");
}

async function confirmarCerrarSesion() {
    try {
        await fetch(`${API_URL}/logout`, {
            method: 'POST',
            credentials: 'include'
        });
    } catch (error) {
        console.error('Error al cerrar sesión:', error);
    } finally {
        sessionStorage.clear();
        window.location.href = '../../index.html';
    }
}

/* AVISOS ADVERTENCIA Y CORRECTO */
function mostrarToast(tipo, mensaje) {
    const contenedor = document.getElementById("toast-container");

    const icono = tipo === "exito"
        ? "../../Img/IconosCuenta/correcto.svg"
        : "../../Img/IconosCuenta/advertencia.svg";

    const claseTipo = tipo === "exito" ? "toast-exito" : "toast-advertencia";

    const toast = document.createElement("div");
    toast.className = `toast ${claseTipo}`;
    toast.innerHTML = `
        <img src="${icono}" alt="${tipo}">
        <span>${mensaje}</span>
    `;

    contenedor.appendChild(toast);

    setTimeout(() => {
        toast.classList.add("saliendo");
        setTimeout(() => toast.remove(), 300);
    }, 4000);
}