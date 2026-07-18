package com.educontrol.controllers;

import com.educontrol.dao.ConfigTareaDAO;
import com.educontrol.modelos.ConfigTarea;
import io.javalin.Javalin;

import java.sql.SQLException;

public class ConfigTareaController {

    private static final ConfigTareaDAO dao = new ConfigTareaDAO();

    public static void registrarRutas(Javalin app) {

        app.get("/config-tarea", ctx -> {
            try {
                ctx.json(dao.listarTodos());
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        app.get("/config-tarea/{id}", ctx -> {
            try {
                int id = Integer.parseInt(ctx.pathParam("id"));
                ConfigTarea config = dao.obtenerPorId(id);

                if (config != null) {
                    ctx.json(config);
                } else {
                    ctx.status(404).result("Configuración de tarea no encontrada");
                }
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        app.post("/config-tarea", ctx -> {
            try {
                ConfigTarea config = ctx.bodyAsClass(ConfigTarea.class);
                dao.crear(config);
                ctx.status(201).result("Configuración de tarea creada correctamente");
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        app.put("/config-tarea/{id}", ctx -> {
            try {
                int id = Integer.parseInt(ctx.pathParam("id"));
                ConfigTarea config = ctx.bodyAsClass(ConfigTarea.class);
                config.setIdTarea(id);
                dao.actualizar(config);
                ctx.result("Configuración de tarea actualizada correctamente");
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        app.delete("/config-tarea/{id}", ctx -> {
            try {
                int id = Integer.parseInt(ctx.pathParam("id"));
                dao.eliminar(id);
                ctx.result("Configuración de tarea eliminada correctamente");
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });
    }
}