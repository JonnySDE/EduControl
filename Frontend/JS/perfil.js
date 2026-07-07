function seleccionarRol(elementoSeleccionado) {
    // 1. Quitamos la clase 'active' a todos los botones del grupo
    const botones = document.querySelectorAll('.boton-rol');
    botones.forEach(btn => btn.classList.remove('active'));

    // 2. Agregamos la clase 'active' solo al botón que se hizo clic
    elementoSeleccionado.classList.add('active');
}

// --- Lógica compartida para Cuenta y EditarFoto ---

document.addEventListener('DOMContentLoaded', () => {
    
    // --- LÓGICA PARA CUENTA.HTML ---
    if (window.location.pathname.includes('Cuenta.html')) {
        const imgPerfil = document.getElementById('preview-foto');
        const fotoGuardada = localStorage.getItem('fotoPerfil');
        
        // Si hay una foto guardada y el elemento existe en el HTML, la ponemos
        if (fotoGuardada && imgPerfil) {
            imgPerfil.src = fotoGuardada;
        }
    }

    // --- LÓGICA PARA EDITARFOTO.HTML ---
    if (window.location.pathname.includes('EditarFoto.html')) {
        const previewFoto = document.getElementById('preview-foto');
        const inputArchivo = document.getElementById('input-archivo');
        const btnGuardar = document.getElementById('btn-guardar');
        const fotoGuardada = localStorage.getItem('fotoPerfil');

        // Cargar foto actual si existe
        if (fotoGuardada) {
            previewFoto.src = fotoGuardada;
        }

        // Previsualizar al cambiar el archivo
        inputArchivo.addEventListener('change', function() {
            const file = this.files[0];
            if (file) {
                const reader = new FileReader();
                reader.onload = (e) => {
                    previewFoto.src = e.target.result;
                };
                reader.readAsDataURL(file);
            }
        });

        // Guardar y redirigir
        btnGuardar.addEventListener('click', () => {
            localStorage.setItem('fotoPerfil', previewFoto.src);
            window.location.href = 'Cuenta.html';
        });
    }
});

// PARA SABER DONDE ESTA EL USUARIO
document.addEventListener("DOMContentLoaded", () => {
    const partes = window.location.pathname.split("/").filter(Boolean);
    const carpetaActual = partes[partes.length - 2]; // ej: "Cuenta"

    document.querySelectorAll(".menu-lateral .item-menu").forEach(enlace => {
        const hrefPartes = enlace.getAttribute("href").split("/").filter(Boolean);
        const carpetaEnlace = hrefPartes[hrefPartes.length - 2]; // carpeta del href

        // Solo agrega "active" si coincide EXACTAMENTE la carpeta
        if (carpetaEnlace === carpetaActual) {
            enlace.classList.add("active");
        } else {
            enlace.classList.remove("active");
        }
    });
});

/* CAMBIAR USUARIO */
// Al cargar la página, revisa si ya hay un usuario guardado (simulando "base de datos" temporalmente)
document.addEventListener("DOMContentLoaded", () => {
    const inputUsername = document.getElementById("input-username");
    const usuarioGuardado = localStorage.getItem("nombreUsuario");
    if (usuarioGuardado && inputUsername) {
        inputUsername.value = usuarioGuardado;
    }

    const inputPassword = document.getElementById("input-password");
    const passwordGuardada = localStorage.getItem("contrasenaUsuario");
    if (passwordGuardada && inputPassword) {
        inputPassword.value = passwordGuardada;
    }
});

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

function guardarUsuario() {
    const nuevoValor = document.getElementById("usuario-nuevo").value.trim();

    if (nuevoValor === "") {
        mostrarToast("advertencia", "Campos vacíos, asegúrese de llenar todos los campos");
        return;
    }

    localStorage.setItem("nombreUsuario", nuevoValor);
    document.getElementById("input-username").value = nuevoValor;

    cerrarModal();
    mostrarToast("exito", "Datos Guardados correctamente");
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

function guardarContrasena() {
    const actual = document.getElementById("contrasena-actual").value;
    const nueva = document.getElementById("contrasena-nueva").value;
    const confirmar = document.getElementById("contrasena-confirmar").value;

    const contrasenaGuardada = localStorage.getItem("contrasenaUsuario") || "";

    if (actual === "" || nueva === "" || confirmar === "") {
        mostrarToast("advertencia", "Campos vacíos, asegúrese de llenar todos los campos");
        return;
    }

    if (contrasenaGuardada && actual !== contrasenaGuardada) {
        mostrarToast("advertencia", "La contraseña actual no es correcta");
        return;
    }

    if (nueva !== confirmar) {
        mostrarToast("advertencia", "La nueva contraseña y su confirmación no coinciden");
        return;
    }

    localStorage.setItem("contrasenaUsuario", nueva);
    document.getElementById("input-password").value = nueva;

    cerrarModalContrasena();
    mostrarToast("exito", "Datos Guardados correctamente");
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

    // Se quita solo después de 4 segundos
    setTimeout(() => {
        toast.classList.add("saliendo");
        setTimeout(() => toast.remove(), 300); // espera que termine la animación de salida
    }, 4000);
}