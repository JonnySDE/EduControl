package com.educontrol.controllers;

import com.educontrol.dao.AlumnoGrupoDAO;
import com.educontrol.dao.AsignarGrupoDAO;
import com.educontrol.modelos.AlumnoGrupo;
import com.educontrol.modelos.AsignarGrupo;
import io.javalin.http.Context;

import java.sql.SQLException;

public class SeguridadUtil {

    private static final AsignarGrupoDAO asignarGrupoDAO = new AsignarGrupoDAO();
    private static final AlumnoGrupoDAO alumnoGrupoDAO = new AlumnoGrupoDAO();

    // Regresa el idGrupo del docente logueado. Si es Director, regresa null (sin restricción)
    public static Integer obtenerGrupoDelDocente(Context ctx) throws SQLException {
        String rol = ctx.sessionAttribute("rol");

        if ("Director".equals(rol)) {
            return null;
        }

        Integer idUsuario = ctx.sessionAttribute("idUsuario");
        if (idUsuario == null) {
            return null;
        }

        AsignarGrupo asignacion = asignarGrupoDAO.obtenerPorUsuario(idUsuario);
        return asignacion != null ? asignacion.getIdGrupo() : null;
    }

    // Verifica si un alumno pertenece al grupo del docente logueado (Director siempre puede)
    public static boolean alumnoPerteneceAGrupoDocente(Context ctx, int matricula) throws SQLException {
        String rol = ctx.sessionAttribute("rol");

        if ("Director".equals(rol)) {
            return true;
        }

        Integer idGrupoDocente = obtenerGrupoDelDocente(ctx);
        if (idGrupoDocente == null) {
            return false;
        }

        AlumnoGrupo ag = alumnoGrupoDAO.obtenerPorMatricula(matricula);
        return ag != null && ag.getIdGrupo() == idGrupoDocente;
    }
}