package com.educontrol.controllers;

import com.educontrol.modelos.ConfigCriteriosRequest;

public class ValidacionPorcentajesUtil {

    private static final int MINIMO_POR_CRITERIO = 5;

    public static String validar(ConfigCriteriosRequest request) {
        int tarea = request.getPorcentajeTarea();
        int examen = request.getPorcentajeExamen();
        int participacion = request.getPorcentajeParticipacion();
        int asistencia = request.getPorcentajeAsistencia();
        int disciplina = request.getPorcentajeDisciplina();

        if (tarea < MINIMO_POR_CRITERIO || examen < MINIMO_POR_CRITERIO || participacion < MINIMO_POR_CRITERIO
                || asistencia < MINIMO_POR_CRITERIO || disciplina < MINIMO_POR_CRITERIO) {
            return "Cada criterio debe tener un valor mínimo de " + MINIMO_POR_CRITERIO + "%.";
        }

        int suma = tarea + examen + participacion + asistencia + disciplina;

        if (suma != 100) {
            return "La suma de los 5 porcentajes debe ser exactamente 100. Suma actual: " + suma;
        }

        return null;
    }
}