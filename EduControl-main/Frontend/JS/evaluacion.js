const API_URL = 'http://localhost:7000';

let rolUsuarioActual = null;
let idUsuarioActual = null;

const inputs = {
    tarea: () => document.getElementById('input-tarea'),
    asistencia: () => document.getElementById('input-asistencia'),
    participacion: () => document.getElementById('input-participacion'),
    disciplina: () => document.getElementById('input-disciplina'),
    examen: () => document.getElementById('input-examen'),
};

document.addEventListener('DOMContentLoaded', async () => {
    const sesion = await obtenerSesion();
    if (!sesion) return;

    rolUsuarioActual = sesion.rol;
    idUsuarioActual = sesion.idUsuario;

    if (rolUsuarioActual === 'Director') {
        await inicializarVistaDirector();
    } else {
        await cargarCriteriosPropios();
        activarEdicion();
    }

    Object.values(inputs).forEach(getInput => {
        getInput().addEventListener('input', actualizarTotalVisual);
    });
});

async function obtenerSesion() {
    try {
        const response = await fetch(`${API_URL}/session`, { credentials: 'include' });
        if (!response.ok) {
            window.location.href = '../../index.html';
            return null;
        }
        return await response.json();
    } catch (error) {
        console.error('Error obteniendo sesión:', error);
        mostrarToast('advertencia', 'No se pudo conectar con el servidor');
        return null;
    }
}

function actualizarTotalVisual() {
    const suma = Object.values(inputs).reduce((acc, getInput) => acc + (parseInt(getInput().value) || 0), 0);
    const totalCelda = document.getElementById('total-porcentaje');
    totalCelda.textContent = suma + '%';
    totalCelda.style.color = suma === 100 ? '#1a7d3a' : '#c0392b';
}

function llenarInputs(datos) {
    inputs.tarea().value = datos.porcentajeTarea ?? 0;
    inputs.asistencia().value = datos.porcentajeAsistencia ?? 0;
    inputs.participacion().value = datos.porcentajeParticipacion ?? 0;
    inputs.disciplina().value = datos.porcentajeDisciplina ?? 0;
    inputs.examen().value = datos.porcentajeExamen ?? 0;
    actualizarTotalVisual();
}

// ==========================================================
// FLUJO DOCENTE: cargar y editar sus propios criterios
// ==========================================================

async function cargarCriteriosPropios() {
    try {
        const response = await fetch(`${API_URL}/config-criterios`, { credentials: 'include' });
        if (!response.ok) {
            mostrarToast('advertencia', 'No se pudieron cargar tus criterios');
            return;
        }
        const datos = await response.json();
        llenarInputs(datos);
    } catch (error) {
        console.error('Error cargando criterios:', error);
        mostrarToast('advertencia', 'No se pudo conectar con el servidor');
    }
}

function activarEdicion() {
    Object.values(inputs).forEach(getInput => getInput().disabled = false);

    document.getElementById('btn-guardar').addEventListener('click', guardarCriterios);
    document.getElementById('btn-limpiar').addEventListener('click', () => {
        Object.values(inputs).forEach(getInput => getInput().value = '');
        actualizarTotalVisual();
    });
}

async function guardarCriterios() {
    const body = {
        porcentajeTarea: parseInt(inputs.tarea().value) || 0,
        porcentajeAsistencia: parseInt(inputs.asistencia().value) || 0,
        porcentajeParticipacion: parseInt(inputs.participacion().value) || 0,
        porcentajeDisciplina: parseInt(inputs.disciplina().value) || 0,
        porcentajeExamen: parseInt(inputs.examen().value) || 0,
    };

    try {
        const response = await fetch(`${API_URL}/config-criterios`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            credentials: 'include',
            body: JSON.stringify(body)
        });

        if (!response.ok) {
            const texto = await response.text();
            mostrarToast('advertencia', texto);
            return;
        }

        mostrarToast('exito', 'Criterios guardados correctamente');
    } catch (error) {
        console.error('Error guardando criterios:', error);
        mostrarToast('advertencia', 'No se pudo conectar con el servidor');
    }
}

// ==========================================================
// FLUJO DIRECTOR: seleccionar docente y ver en solo lectura
// ==========================================================

async function inicializarVistaDirector() {
    // Ocultamos los botones de edición: el Director nunca guarda desde aquí
    document.getElementById('acciones-evaluacion').style.display = 'none';
    Object.values(inputs).forEach(getInput => getInput().disabled = true);

    document.getElementById('selector-docente-contenedor').style.display = 'block';

    const select = document.getElementById('selector-docente');

    try {
        const response = await fetch(`${API_URL}/usuarios`, { credentials: 'include' });
        if (!response.ok) {
            mostrarToast('advertencia', 'No se pudo cargar la lista de docentes');
            return;
        }
        const usuarios = await response.json();
        const docentes = usuarios.filter(u => u.rol === 'Docente');

        docentes.forEach(doc => {
            const option = document.createElement('option');
            option.value = doc.idUsuario;
            option.textContent = `${doc.nombre} ${doc.apellidoPaterno} ${doc.apellidoMaterno}`;
            select.appendChild(option);
        });
    } catch (error) {
        console.error('Error cargando docentes:', error);
        mostrarToast('advertencia', 'No se pudo conectar con el servidor');
    }

    select.addEventListener('change', async () => {
        const idDocente = select.value;
        const aviso = document.getElementById('aviso-solo-lectura');

        if (!idDocente) {
            llenarInputs({});
            aviso.style.display = 'none';
            return;
        }

        try {
            const response = await fetch(`${API_URL}/config-criterios/${idDocente}`, { credentials: 'include' });
            if (!response.ok) {
                mostrarToast('advertencia', 'No se pudieron cargar los criterios de ese docente');
                return;
            }
            const datos = await response.json();
            llenarInputs(datos);
            aviso.style.display = 'block';
        } catch (error) {
            console.error('Error cargando criterios del docente:', error);
            mostrarToast('advertencia', 'No se pudo conectar con el servidor');
        }
    });
}

// ==========================================================
// TOASTS
// ==========================================================

function mostrarToast(tipo, mensaje) {
    let contenedor = document.getElementById('toast-container');
    if (!contenedor) {
        contenedor = document.createElement('div');
        contenedor.id = 'toast-container';
        document.body.appendChild(contenedor);
    }

    const esExito = tipo === 'exito';

    const toast = document.createElement('div');
    toast.className = `alerta-mensaje ${esExito ? 'alerta-exito' : 'alerta-advertencia'}`;

    const icono = document.createElement('div');
    icono.className = 'alerta-icono';
    icono.innerHTML = esExito
        ? `<svg width="20" height="20" viewBox="0 0 24 24" fill="none"><path d="M20 6L9 17l-5-5" stroke="white" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"/></svg>`
        : `<svg width="18" height="18" viewBox="0 0 24 24" fill="none"><path d="M12 2L1 21h22L12 2z" fill="white"/><rect x="11" y="9" width="2" height="6" fill="#c9860a"/><rect x="11" y="16" width="2" height="2" fill="#c9860a"/></svg>`;

    const texto = document.createElement('span');
    texto.className = 'alerta-texto';
    texto.textContent = mensaje;

    toast.appendChild(icono);
    toast.appendChild(texto);
    contenedor.appendChild(toast);

    setTimeout(() => {
        toast.classList.add('alerta-salida');
        setTimeout(() => toast.remove(), 300);
    }, 3500);
}