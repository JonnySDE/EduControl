const API_URL = 'http://localhost:7000';
const UMBRAL_APROBATORIO = 6;

let rolUsuarioActual = null;
let listaDocentes = [];
let mapaAsignaciones = {}; // idGrupo -> idUsuario
let grupoSeleccionadoId = null;
let resumenActual = null; // { idGrupo, grado, grupoLetra, nombreDocente }
let idGrupoPropioDocente = null; // solo aplica si rolUsuarioActual !== 'Director'

document.addEventListener('DOMContentLoaded', async () => {
    const sesion = await obtenerSesion();
    if (!sesion) return;

    rolUsuarioActual = sesion.rol;

    if (rolUsuarioActual !== 'Director') {
        await cargarMiGrupo();
    }

    ajustarUIPorRol();
    await cargarDocentes();
    await cargarAsignaciones();
    await cargarGrupos();

    inicializarEventosGlobales();
    inicializarModales();
});

async function cargarMiGrupo() {
    try {
        const response = await fetch(`${API_URL}/mi-grupo`, { credentials: 'include' });
        if (!response.ok) return;
        const grupo = await response.json();
        idGrupoPropioDocente = grupo.idGrupo;
    } catch (error) {
        console.error('Error cargando mi grupo:', error);
    }
}

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
    } catch (error) {
        console.error('Error cargando docentes:', error);
    }
}

function llenarSelectRegistrar() {
    const select = document.getElementById('docente');
    if (!select) return;

    const idsAsignados = new Set(Object.values(mapaAsignaciones).map(v => parseInt(v)));
    const disponibles = listaDocentes.filter(d => !idsAsignados.has(d.idUsuario));

    select.innerHTML = '<option value="" disabled selected>Seleccione un docente...</option>';
    disponibles.forEach(doc => {
        const option = document.createElement('option');
        option.value = doc.idUsuario;
        option.textContent = `${doc.nombre} ${doc.apellidoPaterno} ${doc.apellidoMaterno}`;
        select.appendChild(option);
    });

    if (disponibles.length === 0) {
        const option = document.createElement('option');
        option.value = '';
        option.textContent = 'No hay docentes disponibles';
        option.disabled = true;
        select.appendChild(option);
    }
}

function llenarSelectEditar(idGrupoActual) {
    const select = document.getElementById('editDocente');
    if (!select) return;

    const idDocenteActual = mapaAsignaciones[idGrupoActual] ? parseInt(mapaAsignaciones[idGrupoActual]) : null;
    const idsAsignados = new Set(Object.values(mapaAsignaciones).map(v => parseInt(v)));

    const disponibles = listaDocentes.filter(d =>
        !idsAsignados.has(d.idUsuario) || d.idUsuario === idDocenteActual
    );

    select.innerHTML = '<option value="">Sin asignar</option>';
    disponibles.forEach(doc => {
        const option = document.createElement('option');
        option.value = doc.idUsuario;
        option.textContent = `${doc.nombre} ${doc.apellidoPaterno} ${doc.apellidoMaterno}`;
        if (doc.idUsuario === idDocenteActual) option.selected = true;
        select.appendChild(option);
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
// RESUMEN DEL GRUPO (con datos reales de /promedio/grupo)
// ==========================================================

async function cargarResumenGrupo(idGrupo, grado, grupoLetra, nombreDocente) {
    resumenActual = { idGrupo, grado, grupoLetra, nombreDocente };

    document.getElementById('verTituloGrupo').innerText = `${grado} "${grupoLetra}"`;
    document.getElementById('verDocente').innerText = `Prof. ${nombreDocente}`;
    document.getElementById('verPromedio').innerText = '...';
    document.getElementById('verTotalAlumnos').innerText = '...';
    document.getElementById('verAprobados').innerText = '...';
    document.getElementById('verReprobados').innerText = '...';
    document.getElementById('verPorcentaje').innerText = '...';

    const selectPeriodo = document.getElementById('select-periodo-resumen');
    const periodo = selectPeriodo ? selectPeriodo.value : '1';

    const endpoint = rolUsuarioActual === 'Director'
        ? `${API_URL}/promedio/grupo?idGrupo=${idGrupo}&periodo=${periodo}`
        : `${API_URL}/promedio/mi-grupo?periodo=${periodo}`;

    try {
        const response = await fetch(endpoint, { credentials: 'include' });
        if (!response.ok) {
            mostrarToast('advertencia', 'No se pudo cargar el rendimiento de este grupo');
            return;
        }

        const datos = await response.json();
        const promediosPorAlumno = calcularPromedioPorAlumno(datos);

        const totalAlumnos = promediosPorAlumno.length;
        const aprobados = promediosPorAlumno.filter(p => p >= UMBRAL_APROBATORIO).length;
        const reprobados = totalAlumnos - aprobados;
        const promedioGrupal = totalAlumnos > 0
            ? Math.round((promediosPorAlumno.reduce((a, b) => a + b, 0) / totalAlumnos) * 100) / 100
            : 0;
        const porcentaje = totalAlumnos > 0 ? Math.round((aprobados / totalAlumnos) * 1000) / 10 : 0;

        document.getElementById('verPromedio').innerText = promedioGrupal.toFixed(1);
        document.getElementById('verTotalAlumnos').innerText = totalAlumnos;
        document.getElementById('verAprobados').innerText = `${aprobados} ✔`;
        document.getElementById('verReprobados').innerText = `${reprobados} ❌`;
        document.getElementById('verPorcentaje').innerText = porcentaje;

        const anillo = document.getElementById('anilloProgreso');
        if (anillo) {
            const deesfaseMaximo = 251.2;
            const offsetCalculado = deesfaseMaximo - (promedioGrupal / 10) * deesfaseMaximo;
            anillo.style.strokeDashoffset = offsetCalculado;
        }
    } catch (error) {
        console.error('Error cargando resumen del grupo:', error);
        mostrarToast('advertencia', 'No se pudo conectar con el servidor');
    }
}

function calcularPromedioPorAlumno(datos) {
    const alumnosMap = new Map();
    datos.forEach(item => {
        if (!alumnosMap.has(item.matricula)) alumnosMap.set(item.matricula, []);
        if (item.promedioFinal !== null && item.promedioFinal !== undefined) {
            alumnosMap.get(item.matricula).push(item.promedioFinal);
        }
    });

    const resultado = [];
    alumnosMap.forEach((valores) => {
        if (valores.length === 0) return;
        resultado.push(valores.reduce((a, b) => a + b, 0) / valores.length);
    });
    return resultado;
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

            const idGrupo = fila.dataset.idGrupo;
            const grado = fila.querySelector('.celda-grado').innerText.trim();
            const grupoLetra = fila.querySelector('.celda-grupo').innerText.trim();
            const docente = fila.querySelector('.celda-docente').innerText.trim();

            if (rolUsuarioActual !== 'Director' && idGrupoPropioDocente && parseInt(idGrupo) !== idGrupoPropioDocente) {
                mostrarToast('advertencia', 'Solo puedes ver el resumen de tu propio grupo.');
                return;
            }

            if (modalVerResumen) {
                modalVerResumen.classList.add('modal-activo');
                cargarResumenGrupo(idGrupo, grado, grupoLetra, docente);
            }
            return;
        }

        const btnEditar = e.target.closest('.btn-editar-grupo');
        if (btnEditar) {
            e.preventDefault();
            const fila = btnEditar.closest('tr');
            if (!fila) return;

            grupoSeleccionadoId = fila.dataset.idGrupo;

            document.getElementById('editGrado').value = fila.querySelector('.celda-grado').innerText.trim();
            document.getElementById('editGrupo').value = fila.querySelector('.celda-grupo').innerText.trim();

            llenarSelectEditar(grupoSeleccionadoId);

            modalEditarGrupo.classList.add('modal-activo');
            return;
        }

        const btnEliminar = e.target.closest('.btn-eliminar-grupo');
        if (btnEliminar) {
            e.preventDefault();
            const fila = btnEliminar.closest('tr');
            if (!fila) return;
            grupoSeleccionadoId = fila.dataset.idGrupo;
            modalConfirmarEliminar.classList.add('modal-activo');
            return;
        }

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

    const btnAbrirModal = document.getElementById('btnAbrirModal');
    if (btnAbrirModal) {
        btnAbrirModal.addEventListener('click', () => {
            llenarSelectRegistrar();
            modalRegistrarGrupo.classList.add('modal-activo');
        });
    }

    const btnCancelarModal = document.getElementById('btnCancelarModal');
    const formRegistrarGrupo = document.getElementById('formRegistrarGrupo');
    if (btnCancelarModal) {
        btnCancelarModal.addEventListener('click', () => {
            modalRegistrarGrupo.classList.remove('modal-activo');
            formRegistrarGrupo.reset();
        });
    }

    if (formRegistrarGrupo) {
        formRegistrarGrupo.addEventListener('submit', async (e) => {
            e.preventDefault();

            const grado = parseInt(document.getElementById('grado').value);
            const grupoLetra = document.getElementById('grupo').value.trim();
            const idDocente = document.getElementById('docente').value;

            if (!idDocente) {
                mostrarToast('advertencia', 'Debes seleccionar un docente para el grupo');
                return;
            }

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

                const responseAsignar = await fetch(`${API_URL}/asignar-grupo`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    credentials: 'include',
                    body: JSON.stringify({ idUsuario: parseInt(idDocente), idGrupo: grupoCreado.idGrupo })
                });

                if (!responseAsignar.ok) {
                    const texto = await responseAsignar.text();
                    mostrarToast('advertencia', 'Grupo creado, pero no se pudo asignar el docente: ' + texto);
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

    const btnCancelarEditar = document.getElementById('btnCancelarEditar');
    const formEditarGrupo = document.getElementById('formEditarGrupo');
    if (btnCancelarEditar) {
        btnCancelarEditar.addEventListener('click', () => modalEditarGrupo.classList.remove('modal-activo'));
    }

    if (formEditarGrupo) {
        formEditarGrupo.addEventListener('submit', async (e) => {
            e.preventDefault();
            if (!grupoSeleccionadoId) return;

            const grado = parseInt(document.getElementById('editGrado').value);
            const grupoLetra = document.getElementById('editGrupo').value.trim();
            const idDocenteNuevoStr = document.getElementById('editDocente').value;
            const idDocenteNuevo = idDocenteNuevoStr ? parseInt(idDocenteNuevoStr) : null;
            const idDocenteActual = mapaAsignaciones[grupoSeleccionadoId] ? parseInt(mapaAsignaciones[grupoSeleccionadoId]) : null;

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

                if (idDocenteNuevo !== idDocenteActual) {

                    if (idDocenteActual) {
                        await fetch(`${API_URL}/asignar-grupo/${idDocenteActual}`, {
                            method: 'DELETE',
                            credentials: 'include'
                        });
                    }

                    if (idDocenteNuevo) {
                        const responseAsignar = await fetch(`${API_URL}/asignar-grupo`, {
                            method: 'POST',
                            headers: { 'Content-Type': 'application/json' },
                            credentials: 'include',
                            body: JSON.stringify({ idUsuario: idDocenteNuevo, idGrupo: parseInt(grupoSeleccionadoId) })
                        });

                        if (!responseAsignar.ok) {
                            const texto = await responseAsignar.text();
                            mostrarToast('advertencia', 'Grupo actualizado, pero no se pudo asignar el nuevo docente: ' + texto);
                        }
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
                    modalConfirmarEliminar.classList.remove('modal-activo');
                    return;
                }

                modalConfirmarEliminar.classList.remove('modal-activo');
                mostrarToast('exito', 'Grupo eliminado correctamente');
                grupoSeleccionadoId = null;

                await cargarAsignaciones();
                await cargarGrupos();
            } catch (error) {
                console.error('Error eliminando grupo:', error);
                mostrarToast('advertencia', 'No se pudo conectar con el servidor');
            }
        });
    }

    const btnCerrarResumen = document.getElementById('btnCerrarResumen');
    if (btnCerrarResumen) {
        btnCerrarResumen.addEventListener('click', () => modalVerResumen.classList.remove('modal-activo'));
    }

    const selectPeriodoResumen = document.getElementById('select-periodo-resumen');
    if (selectPeriodoResumen) {
        selectPeriodoResumen.addEventListener('change', () => {
            if (resumenActual) {
                cargarResumenGrupo(resumenActual.idGrupo, resumenActual.grado, resumenActual.grupoLetra, resumenActual.nombreDocente);
            }
        });
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