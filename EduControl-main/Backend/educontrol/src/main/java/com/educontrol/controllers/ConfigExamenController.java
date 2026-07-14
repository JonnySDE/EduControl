package com.educontrol.controllers;

import com.educontrol.dao.ConfigExamenDAO;
import com.educontrol.modelos.ConfigExamen;
import io.javalin.Javalin;

import java.sql.SQLException;

public class ConfigExamenController {

    private static final ConfigExamenDAO dao = new ConfigExamenDAO();

    public static void registrarRutas(Javalin app) {

        app.get("/config-examen", ctx -> {
            try {
                ctx.json(dao.listarTodos());
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        app.get("/config-examen/{id}", ctx -> {
            try {
                int id = Integer.parseInt(ctx.pathParam("id"));
                ConfigExamen config = dao.obtenerPorId(id);

                if (config != null) {
                    ctx.json(config);
                } else {
                    ctx.status(404).result("Configuración de examen no encontrada");
                }
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        app.post("/config-examen", ctx -> {
            try {
                ConfigExamen config = ctx.bodyAsClass(ConfigExamen.class);
                dao.crear(config);
                ctx.status(201).result("Configuración de examen creada correctamente");
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        app.put("/config-examen/{id}", ctx -> {
            try {
                int id = Integer.parseInt(ctx.pathParam("id"));
                ConfigExamen config = ctx.bodyAsClass(ConfigExamen.class);
                config.setIdExamen(id);
                dao.actualizar(config);
                ctx.result("Configuración de examen actualizada correctamente");
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        app.delete("/config-examen/{id}", ctx -> {
            try {
                int id = Integer.parseInt(ctx.pathParam("id"));
                dao.eliminar(id);
                ctx.result("Configuración de examen eliminada correctamente");
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });
    }
}