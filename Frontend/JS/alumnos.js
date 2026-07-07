// Variable global
let listaAlumnosOriginal = [];

document.addEventListener('DOMContentLoaded', () => {
    // 1. Sidebar activo (funciona en ambas páginas)
    const items = document.querySelectorAll('.item-menu');
    items.forEach(item => {
        if(item.innerText.trim() === "Alumno") item.classList.add('active');
    });

    // Solo cargar si estamos en la página de la tabla
    if (document.getElementById('tabla-cuerpo')) cargarAlumnos();
});

// --- SISTEMA DE NOTIFICACIONES ---
function mostrarMensaje(texto, tipo) {
    // 1. Buscamos o creamos el contenedor principal
    let container = document.getElementById('toast-container');
    if (!container) {
        container = document.createElement('div');
        container.id = 'toast-container';
        document.body.appendChild(container);
    }

    // 2. Creamos el elemento toast
    const toast = document.createElement('div');
    // Usamos las clases que ya tenías
    toast.className = `mensaje-toast ${tipo === 'error' ? 'toast-advertencia' : 'toast-exito'}`;
    
    const icono = tipo === 'error' ? '../../Img/IconosAlumnos/advertencia.svg' : '../../Img/IconosAlumnos/correcto.svg';
    toast.innerHTML = `<img src="${icono}"> <span>${texto}</span>`;
    
    // 3. Lo metemos en el contenedor
    container.appendChild(toast);
    
    // 4. Lo borramos después de 3 segundos
    setTimeout(() => {
        toast.remove();
    }, 3000);
}

// --- LÓGICA DE ALUMNOS ---
function obtenerNombreGrupo(idGrupo) {
    const grupos = { 1: "4A", 2: "4B", 3: "5A" };
    return grupos[idGrupo] || "Sin asignar";
}

async function cargarAlumnos() {
    try {
        const response = await fetch('http://localhost:7000/alumnos');
        if (!response.ok) throw new Error('Error');
        
        let datos = await response.json();
        
        // ORDENAMIENTO: Asegura que el número de lista sea secuencial (1, 2, 3...)
        listaAlumnosOriginal = datos.sort((a, b) => a.numeroLista - b.numeroLista);
        
        renderizarTabla(listaAlumnosOriginal);
    } catch (error) {
        console.error("Error al cargar:", error);
    }
}

function renderizarTabla(alumnos) {
    const tablaCuerpo = document.getElementById('tabla-cuerpo');
    if (!tablaCuerpo) return; // Blindaje: no hacer nada si no hay tabla

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

// --- BÚSQUEDA ---
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

// --- FORMULARIO REGISTRO ---
const formRegistrar = document.getElementById('formRegistrarAlumno');
if (formRegistrar) {
    formRegistrar.addEventListener('submit', async (e) => {
        e.preventDefault();

        // Validamos vacíos manualmente (ya no usamos 'required' en el HTML)
        const inputs = formRegistrar.querySelectorAll('input');
        let camposVacios = false;
        inputs.forEach(input => { if (input.value.trim() === "") camposVacios = true; });

        if (camposVacios) {
            mostrarMensaje("Campos vacíos, asegúrese de llenar todos los campos", "error");
            return;
        }

        const nuevoAlumno = {
            matricula: parseInt(document.getElementById('matricula').value),
            nombre: document.getElementById('nombre').value,
            apellidoPaterno: document.getElementById('apellidoPaterno').value,
            apellidoMaterno: document.getElementById('apellidoMaterno').value,
            numeroLista: parseInt(document.getElementById('numeroLista').value),
            idGrupo: parseInt(document.getElementById('idGrupo').value)
        };

        try {
            const response = await fetch('http://localhost:7000/alumnos', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(nuevoAlumno)
            });

            if (response.ok) {
                mostrarMensaje("Alumno guardado correctamente", "exito");
                setTimeout(() => window.location.href = "Alumnos.html", 1500);
            } else {
                mostrarMensaje("Error al guardar en la base de datos", "error");
            }
        } catch (error) {
            mostrarMensaje("No se pudo conectar con el servidor", "error");
        }
    });
}

// --- ACCIONES ---
function eliminarAlumno(matricula) {
    if(confirm("¿Estás seguro de eliminar este alumno?")) {
        fetch(`http://localhost:7000/alumnos/${matricula}`, { method: 'DELETE' })
        .then(res => {
            if(res.ok) {
                mostrarMensaje("Alumno eliminado correctamente", "exito");
                cargarAlumnos();
            } else {
                mostrarMensaje("Error al eliminar", "error");
            }
        });
    }
}

function verAlumno(matricula) { alert("Ver: " + matricula); }
function editarAlumno(matricula) { alert("Editar: " + matricula); }