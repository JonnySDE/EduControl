package com.educontrol.controllers;

import com.educontrol.Main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ValidacionPorcentajesUtil {

    // Suma los porcentajes de los 5 criterios para un docente + campo formativo específico.
    // Permite excluir una tabla del cálculo (para cuando se está insertando/actualizando esa tabla)
    public static int sumarPorcentajes(int idUsuario, int idCampoFormativo) throws SQLException {
        int suma = 0;

        suma += obtenerPorcentaje("config_tarea", "idCampoFormativo", idUsuario, idCampoFormativo);
        suma += obtenerPorcentajeExamen(idUsuario); // examen no depende de campo formativo
        suma += obtenerPorcentaje("config_participacion", "idCampoFormativo", idUsuario, idCampoFormativo);
        suma += obtenerPorcentaje("config_asistencia", "idCampoFormativo", idUsuario, idCampoFormativo);
        suma += obtenerPorcentaje("config_disciplina", "idCampoFormativo", idUsuario, idCampoFormativo);

        return suma;
    }

    private static int obtenerPorcentaje(String tabla, String columnaCampo, int idUsuario, int idCampoFormativo) throws SQLException {
        String sql = "SELECT porcentaje FROM " + tabla + " WHERE idUsuario = ? AND " + columnaCampo + " = ?";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idUsuario);
            stmt.setInt(2, idCampoFormativo);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("porcentaje");
            }
        }
        return 0;
    }

    private static int obtenerPorcentajeExamen(int idUsuario) throws SQLException {
        String sql = "SELECT porcentaje FROM config_examen WHERE idUsuario = ?";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idUsuario);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("porcentaje");
            }
        }
        return 0;
    }

    // Valida si, sumando un nuevo/actualizado porcentaje de una tabla específica, el total llegaría a 100
    // 'tablaQueSeEstaGuardando' se usa para no contar dos veces el valor viejo de esa tabla
    public static boolean validarSuma100(int idUsuario, int idCampoFormativo, String tablaQueSeEstaGuardando, int nuevoPorcentaje) throws SQLException {
        int sumaActual = 0;

        if (!tablaQueSeEstaGuardando.equals("config_tarea")) {
            sumaActual += obtenerPorcentaje("config_tarea", "idCampoFormativo", idUsuario, idCampoFormativo);
        }
        if (!tablaQueSeEstaGuardando.equals("config_examen")) {
            sumaActual += obtenerPorcentajeExamen(idUsuario);
        }
        if (!tablaQueSeEstaGuardando.equals("config_participacion")) {
            sumaActual += obtenerPorcentaje("config_participacion", "idCampoFormativo", idUsuario, idCampoFormativo);
        }
        if (!tablaQueSeEstaGuardando.equals("config_asistencia")) {
            sumaActual += obtenerPorcentaje("config_asistencia", "idCampoFormativo", idUsuario, idCampoFormativo);
        }
        if (!tablaQueSeEstaGuardando.equals("config_disciplina")) {
            sumaActual += obtenerPorcentaje("config_disciplina", "idCampoFormativo", idUsuario, idCampoFormativo);
        }

        int total = sumaActual + nuevoPorcentaje;
        return total == 100;
    }
}