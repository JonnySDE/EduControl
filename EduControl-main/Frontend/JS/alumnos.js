const API_URL = 'http://localhost:7000';

// Variables globales
let listaAlumnosOriginal = [];
let matriculaAEliminar = null;
let rolUsuarioActual = null;

let gruposCache = []; 
let gruposCargados = false;

async function cargarGruposDesdeAPI() {
    if (gruposCargados) return gruposCache;
    try {
        const response = await fetch(`${API_URL}/grupos`, { credentials: 'include' });
        if (response.ok) {
            gruposCache = await response.json();
            gruposCargados = true;
        }
    } catch (error) {
        console.error('Error cargando grupos:', error);
    }
    return gruposCache;
}

function obtenerNombreGrupo(idGrupo) {
    const grupo = gruposCache.find(g => g.idGrupo === idGrupo);
    return grupo ? `${grupo.grado}° ${grupo.grupo}` : "Sin asignar";
}

function llenarSelectGrupos(selectElement, idGrupoActual) {
    if (!selectElement) return;
    selectElement.innerHTML = '';
    gruposCache.forEach(g => {
        const option = document.createElement('option');
        option.value = g.idGrupo;
        option.textContent = `${g.grado}° ${g.grupo}`;
        if (idGrupoActual !== undefined && g.idGrupo === idGrupoActual) {
            option.selected = true;
        }
        selectElement.appendChild(option);
    });
}

async function obtenerSesion() {
    try {
        const response = await fetch(`${API_URL}/session`, { credentials: 'include' });
        if (!response.ok) {
            window.location.href = '../../index.html';
            return null;
        }
        const sesion = await response.json();
        rolUsuarioActual = sesion.rol;
        return sesion;
    } catch (error) {
        console.error('Error obteniendo sesión:', error);
        return null;
    }
}

document.addEventListener('DOMContentLoaded', async () => {
    const items = document.querySelectorAll('.item-menu');
    items.forEach(item => {
        if (item.innerText.trim() === "Alumno") item.classList.add('active');
    });

    
    await cargarGruposDesdeAPI();

    if (document.getElementById('tabla-cuerpo')) {
        cargarAlumnos();
    }
    if (document.getElementById('info-nombre')) {
        inicializarVerAlumno();
    }
    if (document.getElementById('form-editar')) {
        inicializarEditarAlumno();
    }
    if (document.getElementById('formRegistrarAlumno')) {
        inicializarRegistrarAlumno();
    }
});

function mostrarMensaje(texto, tipo) {
    let container = document.getElementById('toast-container');
    if (!container) {
        container = document.createElement('div');
        container.id = 'toast-container';
        document.body.appendChild(container);
    }

    const toast = document.createElement('div');
    toast.className = `mensaje-toast ${tipo === 'error' ? 'toast-advertencia' : 'toast-exito'}`;

    const icono = tipo === 'error' ? '../../Img/IconosAlumnos/advertencia.svg' : '../../Img/IconosAlumnos/correcto.svg';
    toast.innerHTML = `<img src="${icono}"> <span>${texto}</span>`;

    container.appendChild(toast);

    setTimeout(() => {
        toast.remove();
    }, 3000);
}

// ==========================================================
// LISTA DE ALUMNOS (Alumnos.html)
// ==========================================================

async function cargarAlumnos() {
    try {
        const response = await fetch(`${API_URL}/alumnos`, { credentials: 'include' });

        if (response.status === 401) {
            window.location.href = '../../index.html';
            return;
        }

        if (!response.ok) throw new Error('Error');

        let datos = await response.json();
        listaAlumnosOriginal = datos.sort((a, b) => (a.numeroLista || 0) - (b.numeroLista || 0));

        renderizarTabla(listaAlumnosOriginal);
    } catch (error) {
        console.error("Error al cargar:", error);
        mostrarMensaje("No se pudo conectar con el servidor", "error");
    }
}

function renderizarTabla(alumnos) {
    const tablaCuerpo = document.getElementById('tabla-cuerpo');
    if (!tablaCuerpo) return;

    tablaCuerpo.innerHTML = '';
    alumnos.forEach(alumno => {
        const fila = document.createElement('tr');
        fila.innerHTML = `
            <td>${alumno.nombre} ${alumno.apellidoPaterno} ${alumno.apellidoMaterno}</td>
            <td>${obtenerNombreGrupo(alumno.idGrupo)}</td>
            <td class="acciones-celda">
                <img src="../../Img/IconosAlumnos/ver.svg" onclick="verAlumno(${alumno.matricula})">
                <img src="../../Img/IconosAlumnos/editar.svg" onclick="editarAlumno(${alumno.matricula})">
                <img src="../../Img/IconosAlumnos/eliminar.svg" onclick="eliminarAlumno(${alumno.matricula})">
            </td>
        `;
        tablaCuerpo.appendChild(fila);
    });
}

const inputBusqueda = document.querySelector('.buscar-grupo input');
if (inputBusqueda) {
    inputBusqueda.addEventListener('input', (e) => {
        const texto = e.target.value.toLowerCase();
        const filtrados = listaAlumnosOriginal.filter(a =>
            `${a.nombre} ${a.apellidoPaterno} ${a.apellidoMaterno}`.toLowerCase().includes(texto)
        );
        renderizarTabla(filtrados);
    });
}

function eliminarAlumno(matricula) {
    matriculaAEliminar = matricula;
    document.getElementById('modal-eliminar').style.display = 'flex';
}

document.getElementById('btn-cancelar')?.addEventListener('click', () => {
    document.getElementById('modal-eliminar').style.display = 'none';
});

document.getElementById('btn-confirmar')?.addEventListener('click', async () => {
    if (!matriculaAEliminar) return;

    try {
        const response = await fetch(`${API_URL}/alumnos/${matriculaAEliminar}`, {
            method: 'DELETE',
            credentials: 'include'
        });

        if (response.ok) {
            mostrarMensaje("Alumno eliminado correctamente", "exito");
            document.getElementById('modal-eliminar').style.display = 'none';
            cargarAlumnos();
        } else {
            const texto = await response.text();
            mostrarMensaje("Error al eliminar: " + texto, "error");
            document.getElementById('modal-eliminar').style.display = 'none';
        }
    } catch (error) {
        mostrarMensaje("Error de conexión con el servidor", "error");
        document.getElementById('modal-eliminar').style.display = 'none';
    }
});

function verAlumno(matricula) {
    window.location.href = `VerAlumno.html?matricula=${matricula}`;
}
function editarAlumno(matricula) {
    window.location.href = `EditarAlumno.html?matricula=${matricula}`;
}

// ==========================================================
// VER DETALLE DE ALUMNO (VerAlumno.html)
// ==========================================================

async function inicializarVerAlumno() {
    const params = new URLSearchParams(window.location.search);
    const matricula = params.get('matricula');

    if (!matricula) return;

    try {
        const response = await fetch(`${API_URL}/alumnos/${matricula}`, { credentials: 'include' });

        if (response.status === 401) {
            window.location.href = '../../index.html';
            return;
        }

        if (response.status === 403) {
            document.getElementById('info-nombre').innerText = "No tienes permiso para ver este alumno";
            return;
        }

        if (!response.ok) {
            throw new Error("No se pudo obtener la información del alumno.");
        }

        const alumno = await response.json();

        document.getElementById('info-nombre').innerText =
            `${alumno.nombre || ''} ${alumno.apellidoPaterno || ''} ${alumno.apellidoMaterno || ''}`.trim();

        document.getElementById('info-grupo').innerText = obtenerNombreGrupo(alumno.idGrupo);
        document.getElementById('info-lista').innerText = alumno.numeroLista ?? "---";

        document.getElementById('info-promedio').innerText = alumno.promedioFinal || "---";

    } catch (error) {
        console.error("Error al cargar datos:", error);
        document.getElementById('info-nombre').innerText = "Error al cargar la información";
    }
}

// ==========================================================
// EDITAR ALUMNO (EditarAlumno.html)
// ==========================================================

async function inicializarEditarAlumno() {
    const sesion = await obtenerSesion();
    if (!sesion) return;

    const params = new URLSearchParams(window.location.search);
    const matricula = params.get('matricula');
    if (!matricula) return;

    // Si es Docente, no puede cambiar el grupo del alumno: se deshabilita el select
    const selectGrupo = document.getElementById('edit-grupo');
    if (sesion.rol === 'Docente' && selectGrupo) {
        selectGrupo.disabled = true;
    }

    try {
        const response = await fetch(`${API_URL}/alumnos/${matricula}`, { credentials: 'include' });

        if (response.status === 401) {
            window.location.href = '../../index.html';
            return;
        }

        if (response.status === 403) {
            mostrarMensaje("No tienes permiso para editar este alumno", "error");
            setTimeout(() => window.location.href = 'Alumnos.html', 1500);
            return;
        }

        const alumno = await response.json();

        document.getElementById('edit-nombre').value = alumno.nombre || '';
        document.getElementById('edit-apellido-paterno').value = alumno.apellidoPaterno || '';
        document.getElementById('edit-apellido-materno').value = alumno.apellidoMaterno || '';
        if (selectGrupo) llenarSelectGrupos(selectGrupo, alumno.idGrupo);
        document.getElementById('edit-lista').value = alumno.numeroLista || '';

        document.getElementById('edit-promedio').value = alumno.promedioFinal || '';

    } catch (error) {
        console.error("Error al cargar los datos:", error);
        mostrarMensaje("No se pudo conectar con el servidor", "error");
    }

    document.getElementById('form-editar').addEventListener('submit', async (e) => {
        e.preventDefault();

        const matricula = new URLSearchParams(window.location.search).get('matricula');

        const datosActualizados = {
            nombre: document.getElementById('edit-nombre').value,
            apellidoPaterno: document.getElementById('edit-apellido-paterno').value,
            apellidoMaterno: document.getElementById('edit-apellido-materno').value,
            numeroLista: parseInt(document.getElementById('edit-lista').value),
            promedioFinal: parseFloat(document.getElementById('edit-promedio').value)
        };

        if (sesion.rol !== 'Docente' && selectGrupo) {
            datosActualizados.idGrupo = parseInt(selectGrupo.value);
        }

        try {
            const response = await fetch(`${API_URL}/alumnos/${matricula}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                credentials: 'include',
                body: JSON.stringify(datosActualizados)
            });

            if (response.ok) {
                mostrarMensaje("Alumno actualizado con éxito", "exito");
                setTimeout(() => window.location.href = 'Alumnos.html', 1200);
            } else {
                const errorText = await response.text();
                mostrarMensaje("Error al actualizar: " + errorText, "error");
            }
        } catch (error) {
            console.error("Error al enviar la actualización:", error);
            mostrarMensaje("No se pudo conectar con el servidor", "error");
        }
    });
}

// ==========================================================
// REGISTRAR ALUMNO (RegistrarAlumno.html)
// ==========================================================

async function inicializarRegistrarAlumno() {
    const sesion = await obtenerSesion();
    if (!sesion) return;

    const inputGrupo = document.getElementById('idGrupo');
    let selectGrupoRegistro = null;

    if (inputGrupo) {
        if (sesion.rol === 'Docente') {
            const labelGrupo = inputGrupo.previousElementSibling && inputGrupo.previousElementSibling.tagName === 'LABEL'
                ? inputGrupo.previousElementSibling
                : null;
            if (labelGrupo) labelGrupo.style.display = 'none';
            inputGrupo.style.display = 'none';
        } else {
            selectGrupoRegistro = document.createElement('select');
            selectGrupoRegistro.id = 'idGrupo';
            selectGrupoRegistro.className = inputGrupo.className;
            llenarSelectGrupos(selectGrupoRegistro);
            inputGrupo.parentNode.replaceChild(selectGrupoRegistro, inputGrupo);
        }
    }

    const formRegistrar = document.getElementById('formRegistrarAlumno');
    formRegistrar.addEventListener('submit', async (e) => {
        e.preventDefault();

        const camposObligatorios = ['matricula', 'nombre', 'apellidoPaterno', 'apellidoMaterno', 'numeroLista'];
        let camposVacios = false;
        camposObligatorios.forEach(id => {
            const el = document.getElementById(id);
            if (el && el.value.trim() === "") camposVacios = true;
        });

        if (camposVacios) {
            mostrarMensaje("Campos vacíos, asegúrese de llenar todos los campos", "error");
            return;
        }

        const nuevoAlumno = {
            matricula: parseInt(document.getElementById('matricula').value),
            nombre: document.getElementById('nombre').value,
            apellidoPaterno: document.getElementById('apellidoPaterno').value,
            apellidoMaterno: document.getElementById('apellidoMaterno').value,
            numeroLista: parseInt(document.getElementById('numeroLista').value)
        };

        if (sesion.rol !== 'Docente' && selectGrupoRegistro) {
            nuevoAlumno.idGrupo = parseInt(selectGrupoRegistro.value);
        }

        try {
            const response = await fetch(`${API_URL}/alumnos`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                credentials: 'include',
                body: JSON.stringify(nuevoAlumno)
            });

            if (response.ok) {
                mostrarMensaje("Alumno guardado correctamente", "exito");
                setTimeout(() => window.location.href = "Alumnos.html", 1500);
            } else {
                const texto = await response.text();
                mostrarMensaje("Error al guardar: " + texto, "error");
            }
        } catch (error) {
            mostrarMensaje("No se pudo conectar con el servidor", "error");
        }
    });
}