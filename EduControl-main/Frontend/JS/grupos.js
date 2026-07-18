const API_URL = 'http://localhost:7000';

let rolUsuarioActual = null;
let listaDocentes = [];
let mapaAsignaciones = {}; 
let grupoSeleccionadoId = null;

document.addEventListener('DOMContentLoaded', async () => {
    const sesion = await obtenerSesion();
    if (!sesion) return;

    rolUsuarioActual = sesion.rol;

    ajustarUIPorRol();
    await cargarDocentes();
    await cargarAsignaciones();
    await cargarGrupos();

    inicializarEventosGlobales();
    inicializarModales();
});

// ==========================================================
// SESIÓN Y PERMISOS
// ==========================================================

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

function ajustarUIPorRol() {
    if (rolUsuarioActual !== 'Director') {
        const btnAbrirModal = document.getElementById('btnAbrirModal');
        if (btnAbrirModal) btnAbrirModal.style.display = 'none';
    }
}

// ==========================================================
// CARGA DE DATOS
// ==========================================================

async function cargarDocentes() {
    try {
        const response = await fetch(`${API_URL}/usuarios`, { credentials: 'include' });
        if (!response.ok) return;
        const usuarios = await response.json();
        listaDocentes = usuarios.filter(u => u.rol === 'Docente');
        llenarSelectsDocente();
    } catch (error) {
        console.error('Error cargando docentes:', error);
    }
}

function llenarSelectsDocente() {
    const selects = [document.getElementById('docente'), document.getElementById('editDocente')];
    selects.forEach(select => {
        if (!select) return;
        select.innerHTML = '<option value="" disabled selected>Seleccione un docente...</option>';
        listaDocentes.forEach(doc => {
            const option = document.createElement('option');
            option.value = doc.idUsuario;
            option.textContent = `${doc.nombre} ${doc.apellidoPaterno} ${doc.apellidoMaterno}`;
            select.appendChild(option);
        });
    });
}

async function cargarAsignaciones() {
    try {
        const response = await fetch(`${API_URL}/asignar-grupo`, { credentials: 'include' });
        if (!response.ok) return;
        const asignaciones = await response.json();
        mapaAsignaciones = {};
        asignaciones.forEach(a => {
            mapaAsignaciones[a.idGrupo] = a.idUsuario;
        });
    } catch (error) {
        console.error('Error cargando asignaciones:', error);
    }
}

async function cargarGrupos() {
    try {
        const response = await fetch(`${API_URL}/grupos`, { credentials: 'include' });
        if (!response.ok) {
            mostrarToast('advertencia', 'No se pudieron cargar los grupos');
            return;
        }
        const grupos = await response.json();
        renderizarTabla(grupos);
    } catch (error) {
        console.error('Error cargando grupos:', error);
        mostrarToast('advertencia', 'No se pudo conectar con el servidor');
    }
}

function nombreDocentePorId(idUsuario) {
    const doc = listaDocentes.find(d => d.idUsuario == idUsuario);
    return doc ? `${doc.nombre} ${doc.apellidoPaterno} ${doc.apellidoMaterno}` : 'Sin asignar';
}

function renderizarTabla(grupos) {
    const cuerpo = document.getElementById('cuerpo-grupos');
    if (!cuerpo) return;
    cuerpo.innerHTML = '';

    const esDirector = rolUsuarioActual === 'Director';

    grupos.forEach(grupo => {
        const idDocente = mapaAsignaciones[grupo.idGrupo];
        const nombreDocente = idDocente ? nombreDocentePorId(idDocente) : 'Sin asignar';

        const fila = document.createElement('tr');
        fila.dataset.idGrupo = grupo.idGrupo;
        fila.dataset.idDocente = idDocente || '';

        fila.innerHTML = `
            <td class="celda-centro celda-grado">${grupo.grado}</td>
            <td class="celda-centro celda-grupo">${grupo.grupo}</td>
            <td class="celda-docente">${nombreDocente}</td>
            <td class="celda-centro">
                <button class="btn-accion-icono btn-ver-grupo" title="Ver resumen"><img src="../../Img/IconosAlumnos/ver.svg" alt="Ver"></button>
                ${esDirector ? `
                <button class="btn-accion-icono btn-editar-grupo" title="Editar"><img src="../../Img/IconosAlumnos/editar.svg" alt="Editar"></button>
                <button class="btn-accion-icono btn-eliminar-grupo" title="Eliminar"><img src="../../Img/IconosAlumnos/eliminar.svg" alt="Eliminar"></button>
                ` : ''}
            </td>
        `;
        cuerpo.appendChild(fila);
    });
}

// ==========================================================
// EVENTOS DE TABLA (VER / EDITAR / ELIMINAR)
// ==========================================================

function inicializarEventosGlobales() {
    const modalRegistrarGrupo = document.getElementById('modalRegistrarGrupo');
    const modalEditarGrupo = document.getElementById('modalEditarGrupo');
    const modalConfirmarEliminar = document.getElementById('modalConfirmarEliminar');
    const modalVerResumen = document.getElementById('modalVerResumen');

    document.addEventListener('click', (e) => {

       const botonVer = e.target.closest('.btn-ver-grupo');
        if (botonVer) {
            e.preventDefault();
            const fila = botonVer.closest('tr');
            if (!fila) return;

            const grado = fila.querySelector('.celda-grado').innerText.trim();
            const grupo = fila.querySelector('.celda-grupo').innerText.trim();
            const docente = fila.querySelector('.celda-docente').innerText.trim();

            // Datos de rendimiento: pendientes de endpoint real, se muestran valores de ejemplo
            const promedio = "8.3";
            const aprobados = "23";
            const reprobados = "7";
            const porcentaje = "76.7";

            if (modalVerResumen) {
                document.getElementById('verTituloGrupo').innerText = `${grado} "${grupo}"`;
                document.getElementById('verPromedio').innerText = promedio;
                document.getElementById('verDocente').innerText = `Prof. ${docente}`;
                document.getElementById('verAprobados').innerText = `${aprobados} ✔`;
                document.getElementById('verReprobados').innerText = `${reprobados} ❌`;
                document.getElementById('verPorcentaje').innerText = porcentaje;

                const anillo = document.getElementById('anilloProgreso');
                if (anillo) {
                    const deesfaseMaximo = 251.2;
                    const offsetCalculado = deesfaseMaximo - (parseFloat(promedio) / 10) * deesfaseMaximo;
                    anillo.style.strokeDashoffset = offsetCalculado;
                }
                modalVerResumen.classList.add('modal-activo');
            }
            return;
        }

        // EDITAR (solo Director)
        const btnEditar = e.target.closest('.btn-editar-grupo');
        if (btnEditar) {
            e.preventDefault();
            const fila = btnEditar.closest('tr');
            if (!fila) return;

            grupoSeleccionadoId = fila.dataset.idGrupo;

            document.getElementById('editGrado').value = fila.querySelector('.celda-grado').innerText.trim();
            document.getElementById('editGrupo').value = fila.querySelector('.celda-grupo').innerText.trim();

            const idDocenteActual = fila.dataset.idDocente;
            if (idDocenteActual) document.getElementById('editDocente').value = idDocenteActual;

            modalEditarGrupo.classList.add('modal-activo');
            return;
        }

        // ELIMINAR (solo Director)
        const btnEliminar = e.target.closest('.btn-eliminar-grupo');
        if (btnEliminar) {
            e.preventDefault();
            const fila = btnEliminar.closest('tr');
            if (!fila) return;
            grupoSeleccionadoId = fila.dataset.idGrupo;
            modalConfirmarEliminar.classList.add('modal-activo');
            return;
        }

        // CERRAR AL CLICKEAR FUERA
        if (e.target === modalRegistrarGrupo) modalRegistrarGrupo.classList.remove('modal-activo');
        if (e.target === modalEditarGrupo) modalEditarGrupo.classList.remove('modal-activo');
        if (e.target === modalConfirmarEliminar) modalConfirmarEliminar.classList.remove('modal-activo');
        if (e.target === modalVerResumen) modalVerResumen.classList.remove('modal-activo');
    });
}

// ==========================================================
// MODALES: REGISTRAR / EDITAR / ELIMINAR
// ==========================================================

function inicializarModales() {
    const modalRegistrarGrupo = document.getElementById('modalRegistrarGrupo');
    const modalEditarGrupo = document.getElementById('modalEditarGrupo');
    const modalConfirmarEliminar = document.getElementById('modalConfirmarEliminar');
    const modalVerResumen = document.getElementById('modalVerResumen');

    // Abrir registrar
    const btnAbrirModal = document.getElementById('btnAbrirModal');
    if (btnAbrirModal) {
        btnAbrirModal.addEventListener('click', () => modalRegistrarGrupo.classList.add('modal-activo'));
    }

    // Cancelar registrar
    const btnCancelarModal = document.getElementById('btnCancelarModal');
    const formRegistrarGrupo = document.getElementById('formRegistrarGrupo');
    if (btnCancelarModal) {
        btnCancelarModal.addEventListener('click', () => {
            modalRegistrarGrupo.classList.remove('modal-activo');
            formRegistrarGrupo.reset();
        });
    }

    // Guardar nuevo grupo
    if (formRegistrarGrupo) {
        formRegistrarGrupo.addEventListener('submit', async (e) => {
            e.preventDefault();

            const grado = parseInt(document.getElementById('grado').value);
            const grupoLetra = document.getElementById('grupo').value.trim();
            const idDocente = document.getElementById('docente').value;

            try {
                const responseGrupo = await fetch(`${API_URL}/grupos`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    credentials: 'include',
                    body: JSON.stringify({ grado, grupo: grupoLetra })
                });

                if (!responseGrupo.ok) {
                    const texto = await responseGrupo.text();
                    mostrarToast('advertencia', texto || 'No se pudo crear el grupo');
                    return;
                }

                const grupoCreado = await responseGrupo.json();

                if (idDocente) {
                    await fetch(`${API_URL}/asignar-grupo`, {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        credentials: 'include',
                        body: JSON.stringify({ idUsuario: parseInt(idDocente), idGrupo: grupoCreado.idGrupo })
                    });
                }

                modalRegistrarGrupo.classList.remove('modal-activo');
                formRegistrarGrupo.reset();
                mostrarToast('exito', 'Grupo creado correctamente');

                await cargarAsignaciones();
                await cargarGrupos();
            } catch (error) {
                console.error('Error creando grupo:', error);
                mostrarToast('advertencia', 'No se pudo conectar con el servidor');
            }
        });
    }

    // Cancelar editar
    const btnCancelarEditar = document.getElementById('btnCancelarEditar');
    const formEditarGrupo = document.getElementById('formEditarGrupo');
    if (btnCancelarEditar) {
        btnCancelarEditar.addEventListener('click', () => modalEditarGrupo.classList.remove('modal-activo'));
    }

    // Guardar edición
    if (formEditarGrupo) {
        formEditarGrupo.addEventListener('submit', async (e) => {
            e.preventDefault();
            if (!grupoSeleccionadoId) return;

            const grado = parseInt(document.getElementById('editGrado').value);
            const grupoLetra = document.getElementById('editGrupo').value.trim();
            const idDocente = document.getElementById('editDocente').value;

            try {
                const responseGrupo = await fetch(`${API_URL}/grupos/${grupoSeleccionadoId}`, {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json' },
                    credentials: 'include',
                    body: JSON.stringify({ grado, grupo: grupoLetra })
                });

                if (!responseGrupo.ok) {
                    const texto = await responseGrupo.text();
                    mostrarToast('advertencia', texto || 'No se pudo actualizar el grupo');
                    return;
                }

                if (idDocente) {
                    const idDocenteActual = mapaAsignaciones[grupoSeleccionadoId];
                    if (idDocenteActual) {
                        await fetch(`${API_URL}/asignar-grupo/${idDocenteActual}`, {
                            method: 'PUT',
                            headers: { 'Content-Type': 'application/json' },
                            credentials: 'include',
                            body: JSON.stringify({ idUsuario: parseInt(idDocente), idGrupo: parseInt(grupoSeleccionadoId) })
                        });
                    } else {
                        await fetch(`${API_URL}/asignar-grupo`, {
                            method: 'POST',
                            headers: { 'Content-Type': 'application/json' },
                            credentials: 'include',
                            body: JSON.stringify({ idUsuario: parseInt(idDocente), idGrupo: parseInt(grupoSeleccionadoId) })
                        });
                    }
                }

                modalEditarGrupo.classList.remove('modal-activo');
                mostrarToast('exito', 'Grupo actualizado correctamente');

                await cargarAsignaciones();
                await cargarGrupos();
            } catch (error) {
                console.error('Error actualizando grupo:', error);
                mostrarToast('advertencia', 'No se pudo conectar con el servidor');
            }
        });
    }

    // Eliminar
    const btnCancelarEliminar = document.getElementById('btnCancelarEliminar');
    const btnConfirmarEliminar = document.getElementById('btnConfirmarEliminar');

    if (btnCancelarEliminar) {
        btnCancelarEliminar.addEventListener('click', () => {
            modalConfirmarEliminar.classList.remove('modal-activo');
            grupoSeleccionadoId = null;
        });
    }

    if (btnConfirmarEliminar) {
        btnConfirmarEliminar.addEventListener('click', async () => {
            if (!grupoSeleccionadoId) return;

            try {
                const response = await fetch(`${API_URL}/grupos/${grupoSeleccionadoId}`, {
                    method: 'DELETE',
                    credentials: 'include'
                });

                if (!response.ok) {
                    const texto = await response.text();
                    mostrarToast('advertencia', texto || 'No se pudo eliminar el grupo');
                    return;
                }

                modalConfirmarEliminar.classList.remove('modal-activo');
                mostrarToast('exito', 'Grupo eliminado correctamente');
                grupoSeleccionadoId = null;

                await cargarGrupos();
            } catch (error) {
                console.error('Error eliminando grupo:', error);
                mostrarToast('advertencia', 'No se pudo conectar con el servidor');
            }
        });
    }

    // Cerrar resumen
    const btnCerrarResumen = document.getElementById('btnCerrarResumen');
    if (btnCerrarResumen) {
        btnCerrarResumen.addEventListener('click', () => modalVerResumen.classList.remove('modal-activo'));
    }
}

// ==========================================================
// TOASTS
// ==========================================================

function mostrarToast(tipo, mensaje) {
    const toastExito = document.getElementById('toastExito');
    const toastCancelar = document.getElementById('toastCancelar');
    const toast = tipo === 'exito' ? toastExito : toastCancelar;

    if (!toast) return;

    if (mensaje) {
        const span = toast.querySelector('span');
        if (span) span.textContent = mensaje;
    }

    toast.classList.add('mostrar-toast');
    setTimeout(() => {
        toast.classList.remove('mostrar-toast');
    }, 3500);
}