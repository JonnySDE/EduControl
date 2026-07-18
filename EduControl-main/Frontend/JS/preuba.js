document.addEventListener('DOMContentLoaded', () => {
    
    // ==========================================
    // 1. LÓGICA DE ROLES (LOGIN)
    // ==========================================
    const btnDirector = document.getElementById('btn-director');
    const btnDocente = document.getElementById('btn-docente');
    function cambiarRolActivo(botonSeleccionado, botonInactivo) {
        if (botonSeleccionado && botonInactivo) {
            botonSeleccionado.classList.add('activo');
            botonInactivo.classList.remove('activo');
        }
    }

    if (btnDirector && btnDocente) {
        btnDirector.addEventListener('click', () => {
            cambiarRolActivo(btnDirector, btnDocente);
        });

        btnDocente.addEventListener('click', () => {
            cambiarRolActivo(btnDocente, btnDirector);
        });
    }

    const formulario = document.getElementById('formulario-login');
    if (formulario) {
        formulario.addEventListener('submit', (e) => {
            e.preventDefault();
            console.log("Intentando iniciar sesión...");
        });
    }

    // ==========================================
    // 2. LÓGICA DE MENSAJES EMERGENTES (TOAST)
    // ==========================================
    const alertaExito = document.getElementById("alertaExito");
    const toastExito = document.getElementById('toastExito');
    const toastCancelar = document.getElementById('toastCancelar');
    const botonGuardar = document.querySelector(".boton-guardar-verde");

    function lanzarToast(elementoToast) {
        if(elementoToast) {
            elementoToast.classList.add('mostrar-toast');
            setTimeout(() => {
                elementoToast.classList.remove('mostrar-toast');
            }, 3500);
        } else if (alertaExito) { // Respaldo para la alerta vieja
            alertaExito.classList.add("mostrar");
            setTimeout(() => {
                alertaExito.classList.remove("mostrar");
            }, 3500);
        }
    }

    if (botonGuardar) {
        botonGuardar.addEventListener("click", (e) => {
            e.preventDefault();
            lanzarToast(alertaExito);
        });
    }

    const btnGuardarGrupos = document.getElementById("btnGuardarGrupos");
    if (btnGuardarGrupos) {
        btnGuardarGrupos.addEventListener("click", (e) => {
            e.preventDefault();
            lanzarToast(alertaExito);
        });
    }

    // ==========================================
    // 3. CORTES Y BOTONES DE ACCIÓN (FIN DE PERIODO)
    // ==========================================
    const btnFinPeriodo = document.getElementById("btnFinPeriodo");
    if (btnFinPeriodo) {
        btnFinPeriodo.addEventListener("click", () => {
            const confirmar = confirm("¿Estás seguro de que deseas finalizar el periodo actual? Se realizará el corte definitivo de asistencias, tareas y evaluaciones para calcular los promedios finales.");
            
            if (!confirmar) return;

            btnFinPeriodo.disabled = true;
            btnFinPeriodo.style.backgroundColor = "#94a3b8";

            fetch("http://tu-servidor-backend/api/periodo/finalizar", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ fechaCierre: new Date().toISOString() })
            })
            .then(res => {
                if (!res.ok) throw new Error("Error en el servidor");
                return res.json();
            })
            .then(data => {
                alert("¡Corte de periodo realizado con éxito! Las calificaciones han sido generadas.");
                window.location.href = "../Calificacion/principal.html";
            })
            .catch(err => {
                console.error(err);
                alert("Ocurrió un error al procesar el fin de periodo.");
                btnFinPeriodo.disabled = false;
                btnFinPeriodo.style.backgroundColor = "#0b2545";
            });
        });
    }

    // ==========================================
    // 4. RENDIMIENTO Y VISTA DE DETALLES (resumen.html)
    // ==========================================
    const urlParams = new URLSearchParams(window.location.search);
    const elementoTitulo = document.getElementById('verTituloGrupo');
    if (elementoTitulo) {
        const grado = urlParams.get('grado') || '3';
        const grupo = urlParams.get('grupo') || 'A';
        const docente = urlParams.get('docente') || 'Docente 1';
        const promedio = urlParams.get('promedio') || '8.3';
        const aprobados = urlParams.get('aprobados') || '23';
        const reprobados = urlParams.get('reprobados') || '7';
        const porcentaje = urlParams.get('porcentaje') || '76.7';

        const elementoPromedio = document.getElementById('verPromedio');
        const elementoDocente = document.getElementById('verDocente');
        const elementoAprobados = document.getElementById('verAprobados');
        const elementoReprobados = document.getElementById('verReprobados');
        const elementoPorcentaje = document.getElementById('verPorcentaje');
        const graficoCirculo = document.getElementById('graficoCirculo');

        elementoTitulo.innerText = `${grado} "${grupo}"`;
        if (elementoPromedio) elementoPromedio.innerText = promedio;
        if (elementoDocente) elementoDocente.innerText = `Prof. ${docente}`;
        if (elementoAprobados) elementoAprobados.innerText = aprobados;
        if (elementoReprobados) elementoReprobados.innerText = reprobados;
        if (elementoPorcentaje) elementoPorcentaje.innerText = porcentaje;

        if (graficoCirculo) {
            const porcentajeGrafico = parseFloat(promedio) * 10;
            graficoCirculo.style.background = `conic-gradient(#1b2a4a ${porcentajeGrafico}%, #e0e0e0 0)`;
        }
    }

    // ==========================================
    // 5. REFERENCIAS DE MODALES E INPUTS PARA LA LISTA
    // ==========================================
    const modalRegistrarGrupo = document.getElementById('modalRegistrarGrupo');
    const modalEditarGrupo = document.getElementById('modalEditarGrupo');
    const modalConfirmarEliminar = document.getElementById('modalConfirmarEliminar');
    const modalVerResumen = document.getElementById('modalVerResumen');

    const editGrado = document.getElementById('editGrado');
    const editGrupo = document.getElementById('editGrupo');
    const editDocente = document.getElementById('editDocente');
    const editCicloEscolar = document.getElementById('editCicloEscolar');

    // ==========================================
    // 6. DELEGACIÓN DE EVENTOS GLOBAL (CLICS EN TABLA)
    // ==========================================
    document.addEventListener("click", (e) => {
        
        const botonVer = e.target.closest('.btn-ver-grupo');
        if (botonVer) {
            e.preventDefault();
            const fila = botonVer.closest('tr');
            if (!fila) return;

            const elementoGrado = fila.querySelector('.celda-grado');
            const elementoGrupo = fila.querySelector('.celda-grupo');
            const elementoDocente = fila.querySelector('.celda-docente');

            const grado = elementoGrado ? elementoGrado.innerText.trim() : '3';
            const grupo = elementoGrupo ? elementoGrupo.innerText.trim() : 'A';
            const docente = elementoDocente ? elementoDocente.innerText.trim() : 'Docente';

            const promedio = "8.3";
            const aprobados = "23";
            const reprobados = "7";
            const porcentaje = "76.7";

            if (modalVerResumen) {
                if(document.getElementById('verTituloGrupo')) document.getElementById('verTituloGrupo').innerText = `${grado} "${grupo}"`;
                if(document.getElementById('verPromedio')) document.getElementById('verPromedio').innerText = promedio;
                if(document.getElementById('verDocente')) document.getElementById('verDocente').innerText = `Prof. ${docente}`;
                if(document.getElementById('verAprobados')) document.getElementById('verAprobados').innerText = `${aprobados} ✔`;
                if(document.getElementById('verReprobados')) document.getElementById('verReprobados').innerText = `${reprobados} ❌`;
                if(document.getElementById('verPorcentaje')) document.getElementById('verPorcentaje').innerText = porcentaje;

                const anillo = document.getElementById('anilloProgreso');
                if (anillo) {
                    const deesfaseMaximo = 276.4; 
                    const offsetCalculado = deesfaseMaximo - (parseFloat(promedio) / 10) * deesfaseMaximo;
                    anillo.style.strokeDashoffset = offsetCalculado;
                }
                modalVerResumen.classList.add('modal-activo');
            } else {
                window.location.href = `resumen.html?grado=${grado}&grupo=${grupo}&docente=${encodeURIComponent(docente)}&promedio=${promedio}&aprobados=${aprobados}&reprobados=${reprobados}&porcentaje=${porcentaje}`;
            }
            return;
        }

        const btnEditar = e.target.closest('.btn-editar-grupo');
        if (btnEditar) {
            e.preventDefault();
            const fila = btnEditar.closest('tr');
            if (!fila) return;
            
            if(editGrado) editGrado.value = fila.querySelector('.celda-grado').innerText.trim();
            if(editGrupo) editGrupo.value = fila.querySelector('.celda-grupo').innerText.trim();
            if(editDocente) editDocente.value = fila.querySelector('.celda-docente').innerText.trim();
            if(editCicloEscolar) editCicloEscolar.value = fila.getAttribute('data-ciclo') || "2025 - 2026";
            
            if(modalEditarGrupo) modalEditarGrupo.classList.add('modal-activo');
            return;
        }

        const btnEliminar = e.target.closest('.btn-eliminar-grupo');
        if (btnEliminar) {
            e.preventDefault();
            if(modalConfirmarEliminar) modalConfirmarEliminar.classList.add('modal-activo');
            return;
        }

        if (e.target === modalRegistrarGrupo) modalRegistrarGrupo.classList.remove('modal-activo');
        if (e.target === modalEditarGrupo) modalEditarGrupo.classList.remove('modal-activo');
        if (e.target === modalConfirmarEliminar) modalConfirmarEliminar.classList.remove('modal-activo');
        if (e.target === modalVerResumen) modalVerResumen.classList.remove('modal-activo');
    });

    // ==========================================
    // 7. CONTROLADORES INTERNOS DE LOS FORMULARIOS DE LOS MODALES
    // ==========================================
    
    // Modal Registrar
    const btnAbrirModal = document.getElementById('btnAbrirModal');
    const btnCancelarModal = document.getElementById('btnCancelarModal');
    const formRegistrarGrupo = document.getElementById('formRegistrarGrupo');

    if(btnAbrirModal) btnAbrirModal.addEventListener('click', () => modalRegistrarGrupo.classList.add('modal-activo'));
    if(btnCancelarModal) {
        btnCancelarModal.addEventListener('click', () => {
            modalRegistrarGrupo.classList.remove('modal-activo');
            if(formRegistrarGrupo) formRegistrarGrupo.reset();
            lanzarToast(toastCancelar);
        });
    }
    if(formRegistrarGrupo) {
        formRegistrarGrupo.addEventListener('submit', (e) => {
            e.preventDefault();
            modalRegistrarGrupo.classList.remove('modal-activo');
            formRegistrarGrupo.reset();
            lanzarToast(toastExito);
        });
    }

    // Modal Editar
    const btnCancelarEditar = document.getElementById('btnCancelarEditar');
    const formEditarGrupo = document.getElementById('formEditarGrupo');

    if(btnCancelarEditar) btnCancelarEditar.addEventListener('click', () => { modalEditarGrupo.classList.remove('modal-activo'); lanzarToast(toastCancelar); });
    if(formEditarGrupo) {
        formEditarGrupo.addEventListener('submit', (e) => {
            e.preventDefault();
            modalEditarGrupo.classList.remove('modal-activo');
            lanzarToast(toastExito);
        });
    }

    // Modal Eliminar
    const btnCancelarEliminar = document.getElementById('btnCancelarEliminar');
    const btnConfirmarEliminar = document.getElementById('btnConfirmarEliminar');

    if(btnCancelarEliminar) btnCancelarEliminar.addEventListener('click', () => { modalConfirmarEliminar.classList.remove('modal-activo'); lanzarToast(toastCancelar); });
    if(btnConfirmarEliminar) btnConfirmarEliminar.addEventListener('click', () => { modalConfirmarEliminar.classList.remove('modal-activo'); lanzarToast(toastExito); });

    // Botón Cerrar del modal Ver Resumen
    const btnCerrarResumen = document.getElementById('btnCerrarResumen');
    if(btnCerrarResumen) btnCerrarResumen.addEventListener('click', () => modalVerResumen.classList.remove('modal-activo'));

    // ==========================================
    // 8. GRÁFICOS INFORMATIVOS (DASHBOARD)
    // ==========================================
    const ctxPromedio = document.getElementById('graficoPromedio');
    if (ctxPromedio) {
        const promedioVal = 8.3;
        const restante = 10 - promedioVal;

        new Chart(ctxPromedio, {
            type: 'doughnut',
            data: {
                datasets: [{
                    data: [promedioVal, restante],
                    backgroundColor: ['#102A43', '#f1f5f9'], 
                    borderWidth: 0
                }]
            },
            options: {
                cutout: '85%', 
                plugins: { tooltip: { enabled: false } },
                responsive: true,
                maintainAspectRatio: false
            }
        });
    }

    const ctxBarras = document.getElementById('graficoBarras');
    if (ctxBarras) {
        new Chart(ctxBarras, {
            type: 'bar',
            data: {
                labels: ['5', '6', '7', '8', '9', '10'], 
                datasets: [{
                    data: [7, 14, 20, 12, 18, 23], 
                    backgroundColor: ['#d9381e', '#9b51e0', '#f2e205', '#d645ac', '#62dc36', '#38bdf8'],
                    borderRadius: 3,
                    barPercentage: 0.6
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: { legend: { display: false } }, 
                scales: {
                    x: { grid: { display: false } }, 
                    y: { 
                        beginAtZero: true,
                        ticks: { stepSize: 5 }
                    }
                }
            }
        });
    }
});
document.addEventListener("DOMContentLoaded", () => {
    const btnGenerar = document.querySelector(".btn-generar-reporte");
    const notificacion = document.getElementById("notificacion-exito");

    if (btnGenerar && notificacion) {
        btnGenerar.addEventListener("click", () => {
            notificacion.classList.remove("mostrar");
            
            setTimeout(() => {
                notificacion.classList.add("mostrar");
                
            console.log("Generando reporte...");
            }, 100);

            setTimeout(() => {
                notificacion.classList.remove("mostrar");
            }, 3500);
        });
    }
});