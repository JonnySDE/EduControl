const API_URL = 'http://localhost:7000';

let rolActual = null;
let idMiGrupo = null;
let chartBarras = null;

document.addEventListener('DOMContentLoaded', async () => {
    const sesion = await obtenerSesion();
    if (!sesion) return;
    rolActual = sesion.rol;

    if (rolActual !== 'Director') {
        const resGrupo = await fetch(`${API_URL}/mi-grupo`, { credentials: 'include' });
        if (resGrupo.ok) {
            const grupo = await resGrupo.json();
            idMiGrupo = grupo.idGrupo;
        }
    } else {
        await inicializarSelectorGrupoDirector();
    }

    document.querySelector('.btn-generar-reporte').addEventListener('click', generarGrafica);

    generarGrafica();
});

async function obtenerSesion() {
    try {
        const response = await fetch(`${API_URL}/session`, { credentials: 'include' });
        if (response.status === 401) {
            window.location.href = '../../index.html';
            return null;
        }
        return await response.json();
    } catch (error) {
        console.error('Error obteniendo sesión:', error);
        return null;
    }
}

async function inicializarSelectorGrupoDirector() {
    const barraFiltros = document.querySelector('.barra-filtros-graficas');

    const contenedor = document.createElement('div');
    contenedor.className = 'grupo-filtro';
    contenedor.id = 'contenedor-select-grupo-director';
    contenedor.innerHTML = `<label for="select-grupo-director">Grupo:</label>`;

    const select = document.createElement('select');
    select.id = 'select-grupo-director';
    select.innerHTML = '<option value="">Selecciona...</option>';

    const wrapperSelect = document.createElement('div');
    wrapperSelect.className = 'select-personalizado';
    wrapperSelect.appendChild(select);

    try {
        const response = await fetch(`${API_URL}/grupos`, { credentials: 'include' });
        const grupos = await response.json();
        grupos.forEach(g => {
            const opt = document.createElement('option');
            opt.value = g.idGrupo;
            opt.textContent = `${g.grado}° ${g.grupo}`;
            select.appendChild(opt);
        });
        if (grupos.length > 0) select.value = grupos[0].idGrupo;
    } catch (error) {
        console.error('Error cargando grupos:', error);
    }

    contenedor.appendChild(wrapperSelect);
    barraFiltros.insertBefore(contenedor, barraFiltros.lastElementChild);

    document.getElementById('tipoReporte').addEventListener('change', () => {
        contenedor.style.display = document.getElementById('tipoReporte').value === 'grupo' ? 'flex' : 'none';
    });
}

async function generarGrafica() {
    const tipo = document.getElementById('tipoReporte').value;
    const periodo = document.getElementById('periodo').value;

    let huboDatos = false;

    if (tipo === 'grupo') {
        huboDatos = await graficarPorCampoFormativo(periodo);
    } else {
        huboDatos = await graficarComparativoGrupos(periodo);
    }

    if (huboDatos) {
        mostrarNotificacionExito();
    } else {
        mostrarNotificacionSinDatos();
    }
}

async function graficarPorCampoFormativo(periodo) {
    let idGrupo = idMiGrupo;

    if (rolActual === 'Director') {
        const select = document.getElementById('select-grupo-director');
        idGrupo = select && select.value ? parseInt(select.value) : null;
        if (!idGrupo) return false;
    }

    if (!idGrupo) return false;

    const endpoint = rolActual === 'Director'
        ? `${API_URL}/promedio/grupo?idGrupo=${idGrupo}&periodo=${periodo}`
        : `${API_URL}/promedio/mi-grupo?periodo=${periodo}`;

    try {
        const response = await fetch(endpoint, { credentials: 'include' });
        const datos = await response.json();

        const acumulado = new Map();
        datos.forEach(item => {
            if (item.promedioFinal === null || item.promedioFinal === undefined) return;
            if (!acumulado.has(item.nombreCampoFormativo)) {
                acumulado.set(item.nombreCampoFormativo, { suma: 0, cuenta: 0 });
            }
            const entry = acumulado.get(item.nombreCampoFormativo);
            entry.suma += item.promedioFinal;
            entry.cuenta += 1;
        });

        if (acumulado.size === 0) {
            limpiarGrafica();
            return false;
        }

        const labels = Array.from(acumulado.keys());
        const valores = labels.map(l => {
            const { suma, cuenta } = acumulado.get(l);
            return cuenta > 0 ? Math.round((suma / cuenta) * 100) / 100 : 0;
        });

        dibujarGrafica(labels, valores, 'Promedio por Campo Formativo', 'Campo Formativo');
        return true;
    } catch (error) {
        console.error('Error generando gráfica por grupo:', error);
        return false;
    }
}

async function graficarComparativoGrupos(periodo) {
    try {
        const resGrupos = await fetch(`${API_URL}/grupos`, { credentials: 'include' });
        const todosLosGrupos = await resGrupos.json();

        const grupos = rolActual === 'Director'
            ? todosLosGrupos
            : todosLosGrupos.filter(g => g.idGrupo === idMiGrupo);

        const labels = [];
        const valores = [];
        let algunGrupoConDatos = false;

        for (const grupo of grupos) {
            const endpoint = rolActual === 'Director'
                ? `${API_URL}/promedio/grupo?idGrupo=${grupo.idGrupo}&periodo=${periodo}`
                : `${API_URL}/promedio/mi-grupo?periodo=${periodo}`;

            const response = await fetch(endpoint, { credentials: 'include' });
            if (!response.ok) continue;
            const datos = await response.json();

            const valoresPromedio = datos.map(d => d.promedioFinal).filter(v => v !== null && v !== undefined);
            if (valoresPromedio.length > 0) algunGrupoConDatos = true;

            const promedio = valoresPromedio.length > 0
                ? Math.round((valoresPromedio.reduce((a, b) => a + b, 0) / valoresPromedio.length) * 100) / 100
                : 0;

            labels.push(`${grupo.grado}${grupo.grupo}`);
            valores.push(promedio);
        }

        if (!algunGrupoConDatos) {
            limpiarGrafica();
            return false;
        }

        dibujarGrafica(labels, valores, 'Promedio por Grupo', 'Grupo');
        return true;
    } catch (error) {
        console.error('Error generando gráfica general:', error);
        return false;
    }
}

function limpiarGrafica() {
    if (chartBarras) {
        chartBarras.destroy();
        chartBarras = null;
    }
}

function mostrarNotificacionSinDatos() {
    const notificacion = document.getElementById('notificacion-exito');
    if (!notificacion) {
        alert('Todavía no hay calificaciones registradas para ese periodo.');
        return;
    }
    const txtPrincipal = notificacion.querySelector('.txt-principal');
    const txtSecundario = notificacion.querySelector('.txt-secundario');
    const icono = notificacion.querySelector('.icon-check');

    if (txtPrincipal) txtPrincipal.textContent = 'Sin datos';
    if (txtSecundario) txtSecundario.textContent = 'para ese periodo todavía';
    if (icono) icono.style.stroke = '#e74c3c';

    notificacion.classList.remove('mostrar');
    setTimeout(() => {
        notificacion.classList.add('mostrar');
    }, 100);
    setTimeout(() => {
        notificacion.classList.remove('mostrar');
        if (txtPrincipal) txtPrincipal.textContent = 'Datos Generados';
        if (txtSecundario) txtSecundario.textContent = 'con éxito';
        if (icono) icono.style.stroke = '#0F5132';
    }, 3500);
}

function dibujarGrafica(labels, valores, tituloEjeY, tituloEjeX) {
    const ctx = document.getElementById('canvasGraficaBarras');
    if (!ctx) return;

    if (chartBarras) chartBarras.destroy();
    chartBarras = new Chart(ctx, {
        type: 'bar',
        data: {
            labels,
            datasets: [{
                label: tituloEjeY,
                data: valores,
                backgroundColor: ['#f1c40f', '#95a5a6', '#8e44ad', '#e74c3c', '#3498db', '#1abc9c', '#e91e63', '#2ecc71', '#34495e'],
                borderRadius: 4,
                barPercentage: 0.6
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: { legend: { display: false } },
            scales: {
                y: { beginAtZero: true, max: 10, title: { display: true, text: 'Calificación' } },
                x: { title: { display: true, text: tituloEjeX } }
            }
        }
    });
}

function mostrarNotificacionExito() {
    const notificacion = document.getElementById('notificacion-exito');
    if (!notificacion) return;
    notificacion.classList.remove('mostrar');
    setTimeout(() => {
        notificacion.classList.add('mostrar');
    }, 100);
    setTimeout(() => {
        notificacion.classList.remove('mostrar');
    }, 3500);
}