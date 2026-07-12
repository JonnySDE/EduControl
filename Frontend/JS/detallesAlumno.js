// /JS/detallesAlumno.js

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

document.addEventListener('DOMContentLoaded', async () => {
    const params = new URLSearchParams(window.location.search);
    const matricula = params.get('matricula');

    if (matricula) {
        try {
            const response = await fetch(`http://localhost:7000/alumnos/${matricula}`);
            
            if (!response.ok) {
                throw new Error("No se pudo obtener la información del alumno.");
            }

            const alumno = await response.json();
            
            // Log para verificar qué trae el servidor
            console.log("Datos recibidos:", alumno);

            // 1. Nombre completo
            document.getElementById('info-nombre').innerText = 
                `${alumno.nombre || ''} ${alumno.apellidoPaterno || ''} ${alumno.apellidoMaterno || ''}`.trim();

            // 2. Grupo: Usamos la función para convertir ID a nombre (ej. 1 -> 4A)
            document.getElementById('info-grupo').innerText = obtenerNombreGrupo(alumno.idGrupo);

            // 3. Otros campos
            document.getElementById('info-lista').innerText = alumno.numeroLista || "---";
            
            // Campos que aún no tienen datos en BD
            document.getElementById('info-tareas').innerText = alumno.tareas || "---";
            document.getElementById('info-disciplina').innerText = alumno.disciplina || "---";
            document.getElementById('info-asistencia').innerText = alumno.asistencia || "---";
            document.getElementById('info-examen').innerText = alumno.examen || "---";
            document.getElementById('info-participacion').innerText = alumno.participacion || "---";
            document.getElementById('info-promedio').innerText = alumno.promedioFinal || "---";

        } catch (error) {
            console.error("Error al cargar datos:", error);
            document.getElementById('info-nombre').innerText = "Error al cargar la información";
        }
    }
});

/**
 * Función para traducir el ID del grupo a su nombre real
 */
function obtenerNombreGrupo(idGrupo) {
    // Puedes actualizar este objeto conforme crees más grupos
    const grupos = {
        1: "4 A",
        2: "4B",
        3: "5A",
        4: "5B"
    };
    return grupos[idGrupo] || "Desconocido";
}