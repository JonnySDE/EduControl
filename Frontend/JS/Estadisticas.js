const API_URL = 'http://localhost:7000';
const UMBRAL_APROBATORIO = 6;

let rolActual = null;
let idGrupoSeleccionado = null;
let periodoSeleccionado = '1';
let chartAprobados = null;
let chartReprobados = null;
let chartPeriodos = null;

document.addEventListener('DOMContentLoaded', async () => {
    const sesion = await obtenerSesion();
    if (!sesion) return;
    rolActual = sesion.rol;

    inicializarSelectorPeriodo();

    if (rolActual === 'Director') {
        await inicializarComoDirector();
    } else {
        idGrupoSeleccionado = 'mi-grupo';
        await cargarEstadisticas();
    }
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

function inicializarSelectorPeriodo() {
    const titulo = document.querySelector('.titulo-formulario');

    const contenedorSelect = document.createElement('div');
    contenedorSelect.id = 'contenedor-select-periodo';
    contenedorSelect.style.cssText = 'display:flex; align-items:center; gap:8px; margin-bottom:20px;';
    contenedorSelect.innerHTML = `<label style="font-weight:600; color:#0b2545; font-size:14px;">Periodo:</label>`;

    const selectPeriodo = document.createElement('select');
    selectPeriodo.id = 'select-periodo-estadisticas';
    selectPeriodo.style.cssText = 'padding:6px 12px; border-radius:20px; border:1px solid #0b2545;';
    selectPeriodo.innerHTML = `
        <option value="1">Primer periodo</option>
        <option value="2">Segundo periodo</option>
        <option value="3">Tercer periodo</option>
    `;
    contenedorSelect.appendChild(selectPeriodo);

    titulo.insertAdjacentElement('afterend', contenedorSelect);

    selectPeriodo.addEventListener('change', () => {
        periodoSeleccionado = selectPeriodo.value;
        if (idGrupoSeleccionado) cargarEstadisticas();
    });
}

async function inicializarComoDirector() {
    const contenedorSelectPeriodo = document.getElementById('contenedor-select-periodo');

    const contenedorSelect = document.createElement('div');
    contenedorSelect.style.cssText = 'display:flex; align-items:center; gap:8px; margin-bottom:20px;';
    contenedorSelect.innerHTML = `
        <label style="font-weight:600; color:#0b2545; font-size:14px;">Grupo:</label>
    `;

    const selectGrupos = document.createElement('select');
    selectGrupos.id = 'select-grupo-director';
    selectGrupos.style.cssText = 'padding:6px 12px; border-radius:20px; border:1px solid #0b2545;';
    selectGrupos.innerHTML = '<option value="">Selecciona un grupo...</option>';
    contenedorSelect.appendChild(selectGrupos);

    contenedorSelectPeriodo.insertAdjacentElement('afterend', contenedorSelect);

    try {
        const response = await fetch(`${API_URL}/grupos`, { credentials: 'include' });
        const grupos = await response.json();
        grupos.forEach(g => {
            const opt = document.createElement('option');
            opt.value = g.idGrupo;
            opt.textContent = `${g.grado}° ${g.grupo}`;
            selectGrupos.appendChild(opt);
        });
    } catch (error) {
        console.error('Error cargando grupos:', error);
    }

    selectGrupos.addEventListener('change', () => {
        idGrupoSeleccionado = selectGrupos.value ? parseInt(selectGrupos.value) : null;
        if (idGrupoSeleccionado) cargarEstadisticas();
    });
}

async function cargarEstadisticas() {
    if (!idGrupoSeleccionado) return;

    try {
        const [datosSeleccionado, datosP1, datosP2, datosP3, asistencia] = await Promise.all([
            obtenerPromedios(periodoSeleccionado),
            obtenerPromedios('1'),
            obtenerPromedios('2'),
            obtenerPromedios('3'),
            obtenerAsistencia(periodoSeleccionado)
        ]);

        const promediosAlumno = calcularPromedioPorAlumno(datosSeleccionado);
        const totalAlumnos = promediosAlumno.length;
        const aprobados = promediosAlumno.filter(p => p >= UMBRAL_APROBATORIO).length;
        const reprobados = totalAlumnos - aprobados;
        const promedioGeneral = totalAlumnos > 0
            ? Math.round((promediosAlumno.reduce((a, b) => a + b, 0) / totalAlumnos) * 100) / 100
            : 0;
        const pctAprobados = totalAlumnos > 0 ? Math.round((aprobados / totalAlumnos) * 100) : 0;
        const pctReprobados = totalAlumnos > 0 ? 100 - pctAprobados : 0;

        document.getElementById('txtPromedio').textContent = promedioGeneral.toFixed(1);
        document.getElementById('txtAprobados').textContent = pctAprobados + '%';
        document.getElementById('txtReprobados').textContent = pctReprobados + '%';
        document.getElementById('txtAsistencia').textContent = asistencia.porcentajeAsistencia + '%';

        dibujarDona('canvasAprobados', pctAprobados, '#1cc88a');
        dibujarDona('canvasReprobados', pctReprobados, '#e74c3c');

        // La gráfica de Periodos siempre compara los 3, sin importar cuál esté seleccionado arriba
        const promedioP1 = promedioGeneralDe(datosP1);
        const promedioP2 = promedioGeneralDe(datosP2);
        const promedioP3 = promedioGeneralDe(datosP3);
        dibujarPeriodos(promedioP1, promedioP2, promedioP3);

    } catch (error) {
        console.error('Error cargando estadísticas:', error);
    }
}

async function obtenerPromedios(periodo) {
    const endpoint = rolActual === 'Director'
        ? `${API_URL}/promedio/grupo?idGrupo=${idGrupoSeleccionado}&periodo=${periodo}`
        : `${API_URL}/promedio/mi-grupo?periodo=${periodo}`;
    const response = await fetch(endpoint, { credentials: 'include' });
    if (!response.ok) return [];
    return await response.json();
}

async function obtenerAsistencia(periodo) {
    const endpoint = rolActual === 'Director'
        ? `${API_URL}/reportes/asistencia-grupo?idGrupo=${idGrupoSeleccionado}&periodo=${periodo}`
        : `${API_URL}/reportes/asistencia?periodo=${periodo}`;
    const response = await fetch(endpoint, { credentials: 'include' });
    if (!response.ok) return { porcentajeAsistencia: 0 };
    return await response.json();
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

function promedioGeneralDe(datos) {
    const promedios = calcularPromedioPorAlumno(datos);
    if (promedios.length === 0) return 0;
    return Math.round((promedios.reduce((a, b) => a + b, 0) / promedios.length) * 100) / 100;
}

function dibujarDona(canvasId, porcentaje, color) {
    const ctx = document.getElementById(canvasId);
    if (!ctx) return;

    const chartVar = canvasId === 'canvasAprobados' ? chartAprobados : chartReprobados;
    if (chartVar) chartVar.destroy();

    const nuevoChart = new Chart(ctx, {
        type: 'doughnut',
        data: {
            datasets: [{
                data: [porcentaje, 100 - porcentaje],
                backgroundColor: [color, '#f1f5f9'],
                borderWidth: 0
            }]
        },
        options: {
            cutout: '75%',
            plugins: { tooltip: { enabled: false } },
            responsive: true,
            maintainAspectRatio: false
        }
    });

    if (canvasId === 'canvasAprobados') chartAprobados = nuevoChart;
    else chartReprobados = nuevoChart;
}

function dibujarPeriodos(p1, p2, p3) {
    const ctx = document.getElementById('canvasPeriodos');
    if (!ctx) return;

    if (chartPeriodos) chartPeriodos.destroy();
    chartPeriodos = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: ['1er periodo', '2do periodo', '3er periodo'],
            datasets: [{
                data: [p1, p2, p3],
                backgroundColor: ['#62dc36', '#9b51e0', '#f2e205'],
                borderRadius: 4,
                barPercentage: 0.6
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: { legend: { display: false } },
            scales: { y: { beginAtZero: true, max: 10 } }
        }
    });
}