package com.educontrol.controllers;

import com.educontrol.modelos.ConfigCriteriosRequest;

public class ValidacionPorcentajesUtil {

    public static String validar(ConfigCriteriosRequest request) {
        int tarea = request.getPorcentajeTarea();
        int examen = request.getPorcentajeExamen();
        int participacion = request.getPorcentajeParticipacion();
        int asistencia = request.getPorcentajeAsistencia();
        int disciplina = request.getPorcentajeDisciplina();

        if (tarea <= 0 || examen <= 0 || participacion <= 0 || asistencia <= 0 || disciplina <= 0) {
            return "Ningún porcentaje puede ser 0 o negativo.";
        }

        int suma = tarea + examen + participacion + asistencia + disciplina;

        if (suma != 100) {
            return "La suma de los 5 porcentajes debe ser exactamente 100. Suma actual: " + suma;
        }

        return null;
    }
}