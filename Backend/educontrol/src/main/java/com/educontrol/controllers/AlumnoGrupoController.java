package com.educontrol.controllers;

import com.educontrol.dao.AlumnoGrupoDAO;
import com.educontrol.modelos.AlumnoGrupo;
import io.javalin.Javalin;

import java.sql.SQLException;

public class AlumnoGrupoController {

    private static final AlumnoGrupoDAO dao = new AlumnoGrupoDAO();

    public static void registrarRutas(Javalin app) {

        app.get("/alumno-grupo", ctx -> {
            try {
                ctx.json(dao.listarTodos());
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        app.get("/alumno-grupo/{matricula}", ctx -> {
            try {
                int matricula = Integer.parseInt(ctx.pathParam("matricula"));
                AlumnoGrupo ag = dao.obtenerPorMatricula(matricula);

                if (ag != null) {
                    ctx.json(ag);
                } else {
                    ctx.status(404).result("El alumno no tiene grupo asignado");
                }
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        app.get("/alumno-grupo/grupo/{idGrupo}", ctx -> {
            try {
                int idGrupo = Integer.parseInt(ctx.pathParam("idGrupo"));
                ctx.json(dao.listarPorGrupo(idGrupo));
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        app.post("/alumno-grupo", ctx -> {
            try {
                AlumnoGrupo ag = ctx.bodyAsClass(AlumnoGrupo.class);
                dao.crear(ag);
                ctx.status(201).result("Alumno asignado a grupo correctamente");
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        // Cambiar de grupo (promoción o reasignación)
        app.put("/alumno-grupo/{matricula}", ctx -> {
            try {
                int matricula = Integer.parseInt(ctx.pathParam("matricula"));
                AlumnoGrupo ag = ctx.bodyAsClass(AlumnoGrupo.class);
                dao.cambiarGrupo(matricula, ag.getIdGrupo(), ag.getNumeroLista());
                ctx.result("Alumno cambiado de grupo correctamente");
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        app.delete("/alumno-grupo/{matricula}", ctx -> {
            try {
                int matricula = Integer.parseInt(ctx.pathParam("matricula"));
                dao.eliminar(matricula);
                ctx.result("Asignación de grupo eliminada correctamente");
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });
    }
}