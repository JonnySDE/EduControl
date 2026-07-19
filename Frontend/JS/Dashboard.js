const API_URL = 'http://localhost:7000';
const UMBRAL_APROBATORIO = 6;

let chartRendimientoCampos = null;
let rolActual = null;

document.addEventListener('DOMContentLoaded', async () => {
    const sesion = await obtenerSesion();
    if (!sesion) return;

    rolActual = sesion.rol;

    const selectPeriodo = document.getElementById('select-periodo-dashboard');
    if (selectPeriodo) {
        selectPeriodo.addEventListener('change', () => cargarDashboard(selectPeriodo.value));
    }

    await cargarDashboard('1');
});

async function cargarDashboard(periodo) {
    if (rolActual === 'Director') {
        await cargarDashboardDirector(periodo);
    } else {
        await cargarDashboardDocente(periodo);
    }
}

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

// ==========================================================
// DOCENTE: solo su propio grupo
// ==========================================================

async function cargarDashboardDocente(periodo) {
    try {
        const resGrupo = await fetch(`${API_URL}/mi-grupo`, { credentials: 'include' });
        if (!resGrupo.ok) return;
        const grupo = await resGrupo.json();
        const nombreGrupo = `${grupo.grado}° ${grupo.grupo}`;

        const resPromedios = await fetch(`${API_URL}/promedio/mi-grupo?periodo=${periodo}`, { credentials: 'include' });
        const datos = await resPromedios.json();

        const totalAlumnos = new Set(datos.map(d => d.matricula)).size;

        document.getElementById('numAlumnos').textContent = totalAlumnos;
        document.getElementById('numGrupos').textContent = '1';
        document.getElementById('grupoMayor').textContent = nombreGrupo;
        document.getElementById('grupoMenor').textContent = nombreGrupo;

        dibujarRendimientoPorCampo(datos);
    } catch (error) {
        console.error('Error cargando dashboard:', error);
    }
}

// ==========================================================
// DIRECTOR: toda la escuela, comparando todos los grupos
// ==========================================================

async function cargarDashboardDirector(periodo) {
    try {
        const resGrupos = await fetch(`${API_URL}/grupos`, { credentials: 'include' });
        const grupos = await resGrupos.json();

        document.getElementById('numGrupos').textContent = grupos.length;

        let totalAlumnosEscuela = 0;
        let mejorGrupo = null;
        let peorGrupo = null;
        const acumuladoPorCampo = new Map();

        for (const grupo of grupos) {
            const response = await fetch(`${API_URL}/promedio/grupo?idGrupo=${grupo.idGrupo}&periodo=${periodo}`, { credentials: 'include' });
            if (!response.ok) continue;
            const datos = await response.json();

            const matriculasUnicas = new Set(datos.map(d => d.matricula));
            totalAlumnosEscuela += matriculasUnicas.size;

            const promedioGrupo = promedioGeneral(datos);
            const nombreGrupo = `${grupo.grado}° ${grupo.grupo}`;

            if (promedioGrupo !== null) {
                if (!mejorGrupo || promedioGrupo > mejorGrupo.promedio) {
                    mejorGrupo = { nombre: nombreGrupo, promedio: promedioGrupo };
                }
                if (!peorGrupo || promedioGrupo < peorGrupo.promedio) {
                    peorGrupo = { nombre: nombreGrupo, promedio: promedioGrupo };
                }
            }

            datos.forEach(item => {
                if (item.promedioFinal === null || item.promedioFinal === undefined) return;
                if (!acumuladoPorCampo.has(item.nombreCampoFormativo)) {
                    acumuladoPorCampo.set(item.nombreCampoFormativo, { suma: 0, cuenta: 0 });
                }
                const entry = acumuladoPorCampo.get(item.nombreCampoFormativo);
                entry.suma += item.promedioFinal;
                entry.cuenta += 1;
            });
        }

        document.getElementById('numAlumnos').textContent = totalAlumnosEscuela;
        document.getElementById('grupoMayor').textContent = mejorGrupo ? mejorGrupo.nombre : '—';
        document.getElementById('grupoMenor').textContent = peorGrupo ? peorGrupo.nombre : '—';

        const datosGrafica = Array.from(acumuladoPorCampo.entries()).map(([nombre, { suma, cuenta }]) => ({
            nombreCampoFormativo: nombre,
            promedioFinal: cuenta > 0 ? suma / cuenta : null
        }));
        dibujarRendimientoPorCampo(datosGrafica);

    } catch (error) {
        console.error('Error cargando dashboard del director:', error);
    }
}

function promedioGeneral(datos) {
    const valores = datos.map(d => d.promedioFinal).filter(v => v !== null && v !== undefined);
    if (valores.length === 0) return null;
    return valores.reduce((a, b) => a + b, 0) / valores.length;
}

function dibujarRendimientoPorCampo(datos) {
    const ctx = document.getElementById('canvasRendimientoCampos');
    if (!ctx) return;

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

    const labels = Array.from(acumulado.keys());
    const valores = labels.map(l => {
        const { suma, cuenta } = acumulado.get(l);
        return cuenta > 0 ? Math.round((suma / cuenta) * 100) / 100 : 0;
    });

    if (chartRendimientoCampos) chartRendimientoCampos.destroy();
    chartRendimientoCampos = new Chart(ctx, {
        type: 'bar',
        data: {
            labels,
            datasets: [{
                data: valores,
                backgroundColor: ['#102A43', '#2E86AB', '#38bdf8', '#62dc36'],
                borderRadius: 4,
                barPercentage: 0.6
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: { legend: { display: false } },
            scales: {
                y: { beginAtZero: true, max: 10 }
            }
        }
    });
}