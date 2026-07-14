package com.educontrol.controllers;

import com.educontrol.dao.ConfigParticipacionDAO;
import com.educontrol.modelos.ConfigParticipacion;
import io.javalin.Javalin;

import java.sql.SQLException;

public class ConfigParticipacionController {

    private static final ConfigParticipacionDAO dao = new ConfigParticipacionDAO();

    public static void registrarRutas(Javalin app) {

        app.get("/config-participacion", ctx -> {
            try {
                ctx.json(dao.listarTodos());
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        app.get("/config-participacion/{id}", ctx -> {
            try {
                int id = Integer.parseInt(ctx.pathParam("id"));
                ConfigParticipacion config = dao.obtenerPorId(id);

                if (config != null) {
                    ctx.json(config);
                } else {
                    ctx.status(404).result("Configuración de participación no encontrada");
                }
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        app.post("/config-participacion", ctx -> {
            try {
                ConfigParticipacion config = ctx.bodyAsClass(ConfigParticipacion.class);
                dao.crear(config);
                ctx.status(201).result("Configuración de participación creada correctamente");
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        app.put("/config-participacion/{id}", ctx -> {
            try {
                int id = Integer.parseInt(ctx.pathParam("id"));
                ConfigParticipacion config = ctx.bodyAsClass(ConfigParticipacion.class);
                config.setIdParticipacion(id);
                dao.actualizar(config);
                ctx.result("Configuración de participación actualizada correctamente");
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        app.delete("/config-participacion/{id}", ctx -> {
            try {
                int id = Integer.parseInt(ctx.pathParam("id"));
                dao.eliminar(id);
                ctx.result("Configuración de participación eliminada correctamente");
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });
    }
}