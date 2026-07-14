package com.educontrol.controllers;

import com.educontrol.dao.ConfigAsistenciaDAO;
import com.educontrol.modelos.ConfigAsistencia;
import io.javalin.Javalin;

import java.sql.SQLException;

public class ConfigAsistenciaController {

    private static final ConfigAsistenciaDAO dao = new ConfigAsistenciaDAO();

    public static void registrarRutas(Javalin app) {

        app.get("/config-asistencia", ctx -> {
            try {
                ctx.json(dao.listarTodos());
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        app.get("/config-asistencia/{id}", ctx -> {
            try {
                int id = Integer.parseInt(ctx.pathParam("id"));
                ConfigAsistencia config = dao.obtenerPorId(id);

                if (config != null) {
                    ctx.json(config);
                } else {
                    ctx.status(404).result("Configuración de asistencia no encontrada");
                }
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        app.post("/config-asistencia", ctx -> {
            try {
                ConfigAsistencia config = ctx.bodyAsClass(ConfigAsistencia.class);
                dao.crear(config);
                ctx.status(201).result("Configuración de asistencia creada correctamente");
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        app.put("/config-asistencia/{id}", ctx -> {
            try {
                int id = Integer.parseInt(ctx.pathParam("id"));
                ConfigAsistencia config = ctx.bodyAsClass(ConfigAsistencia.class);
                config.setIdConfigAsistencia(id);
                dao.actualizar(config);
                ctx.result("Configuración de asistencia actualizada correctamente");
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        app.delete("/config-asistencia/{id}", ctx -> {
            try {
                int id = Integer.parseInt(ctx.pathParam("id"));
                dao.eliminar(id);
                ctx.result("Configuración de asistencia eliminada correctamente");
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });
    }
}