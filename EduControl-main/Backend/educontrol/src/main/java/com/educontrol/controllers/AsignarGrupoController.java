package com.educontrol.controllers;

import com.educontrol.dao.AsignarGrupoDAO;
import com.educontrol.modelos.AsignarGrupo;
import io.javalin.Javalin;

import java.sql.SQLException;

public class AsignarGrupoController {

    private static final AsignarGrupoDAO dao = new AsignarGrupoDAO();

    public static void registrarRutas(Javalin app) {

        app.get("/asignar-grupo", ctx -> {
            try {
                ctx.json(dao.listarTodos());
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        app.get("/asignar-grupo/{idUsuario}", ctx -> {
            try {
                int idUsuario = Integer.parseInt(ctx.pathParam("idUsuario"));
                AsignarGrupo asignacion = dao.obtenerPorUsuario(idUsuario);

                if (asignacion != null) {
                    ctx.json(asignacion);
                } else {
                    ctx.status(404).result("Asignación no encontrada para ese usuario");
                }
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        app.post("/asignar-grupo", ctx -> {
            try {
                AsignarGrupo asignacion = ctx.bodyAsClass(AsignarGrupo.class);
                dao.crear(asignacion);
                ctx.status(201).result("Grupo asignado correctamente");
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        app.put("/asignar-grupo/{idUsuario}", ctx -> {
            try {
                int idUsuario = Integer.parseInt(ctx.pathParam("idUsuario"));
                AsignarGrupo asignacion = ctx.bodyAsClass(AsignarGrupo.class);
                asignacion.setIdUsuario(idUsuario);
                dao.actualizar(asignacion);
                ctx.result("Asignación actualizada correctamente");
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        app.delete("/asignar-grupo/{idUsuario}", ctx -> {
            try {
                int idUsuario = Integer.parseInt(ctx.pathParam("idUsuario"));
                dao.eliminar(idUsuario);
                ctx.result("Asignación eliminada correctamente");
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });
    }
}