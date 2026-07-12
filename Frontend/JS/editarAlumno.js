// /JS/editarAlumno.js

document.addEventListener('DOMContentLoaded', async () => {
    // 1. Obtener matrícula de la URL
    const params = new URLSearchParams(window.location.search);
    const matricula = params.get('matricula');

    if (matricula) {
        await cargarDatosAlumno(matricula);
    }
});

// Función para obtener datos y rellenar el formulario
async function cargarDatosAlumno(matricula) {
    try {
        const response = await fetch(`http://localhost:7000/alumnos/${matricula}`);
        const alumno = await response.json();

        // Rellenar campos del formulario
        document.getElementById('edit-nombre').value = alumno.nombre;
        document.getElementById('edit-grupo').value = alumno.idGrupo;
        document.getElementById('edit-lista').value = alumno.numeroLista;
        document.getElementById('edit-tareas').value = alumno.tareas || '';
        document.getElementById('edit-disciplina').value = alumno.disciplina || '';
        document.getElementById('edit-asistencia').value = alumno.asistencia || '';
        document.getElementById('edit-examen').value = alumno.examen || '';
        document.getElementById('edit-participacion').value = alumno.participacion || '';
        document.getElementById('edit-promedio').value = alumno.promedioFinal || '';
    } catch (error) {
        console.error("Error al cargar los datos:", error);
    }
}

// 2. Manejar el evento de guardar
document.getElementById('form-editar').addEventListener('submit', async (e) => {
    e.preventDefault(); // Evita que la página se recargue sola

    const matricula = new URLSearchParams(window.location.search).get('matricula');

    // Crear el objeto con los nuevos datos
    const datosActualizados = {
        nombre: document.getElementById('edit-nombre').value,
        idGrupo: parseInt(document.getElementById('edit-grupo').value),
        numeroLista: parseInt(document.getElementById('edit-lista').value),
        tareas: document.getElementById('edit-tareas').value,
        disciplina: document.getElementById('edit-disciplina').value,
        asistencia: document.getElementById('edit-asistencia').value,
        examen: document.getElementById('edit-examen').value,
        participacion: document.getElementById('edit-participacion').value,
        promedioFinal: parseFloat(document.getElementById('edit-promedio').value)
    };

    // 3. Enviar datos al Backend (PUT)
    try {
        const response = await fetch(`http://localhost:7000/alumnos/${matricula}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(datosActualizados)
        });

        if (response.ok) {
            alert("Alumno actualizado con éxito");
            window.location.href = '../Alumnos/Alumnos.html'; // Redirigir al listado
        } else {
            const errorText = await response.text();
            alert("Error al actualizar: " + errorText);
        }
    } catch (error) {
        console.error("Error al enviar la actualización:", error);
        alert("No se pudo conectar con el servidor.");
    }
});