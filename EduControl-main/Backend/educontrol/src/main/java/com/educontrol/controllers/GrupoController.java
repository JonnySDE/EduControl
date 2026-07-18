package com.educontrol.controllers;

import com.educontrol.dao.GrupoDAO;
import com.educontrol.modelos.Grupo;
import io.javalin.Javalin;

import java.sql.SQLException;

public class GrupoController {

    private static final GrupoDAO dao = new GrupoDAO();

    public static void registrarRutas(Javalin app) {

        app.get("/grupos", ctx -> {
            try {
                ctx.json(dao.listarTodos());
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        app.get("/grupos/{id}", ctx -> {
            try {
                int id = Integer.parseInt(ctx.pathParam("id"));
                Grupo grupo = dao.obtenerPorId(id);

                if (grupo != null) {
                    ctx.json(grupo);
                } else {
                    ctx.status(404).result("Grupo no encontrado");
                }
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        app.post("/grupos", ctx -> {
            try {
                Grupo grupo = ctx.bodyAsClass(Grupo.class);
                int idGenerado = dao.crear(grupo);
                grupo.setIdGrupo(idGenerado);
                ctx.status(201).json(grupo);
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        app.put("/grupos/{id}", ctx -> {
            try {
                int id = Integer.parseInt(ctx.pathParam("id"));
                Grupo grupo = ctx.bodyAsClass(Grupo.class);
                grupo.setIdGrupo(id);
                dao.actualizar(grupo);
                ctx.result("Grupo actualizado correctamente");
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        app.delete("/grupos/{id}", ctx -> {
            try {
                int id = Integer.parseInt(ctx.pathParam("id"));
                dao.eliminar(id);
                ctx.result("Grupo eliminado correctamente");
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });
    }
}