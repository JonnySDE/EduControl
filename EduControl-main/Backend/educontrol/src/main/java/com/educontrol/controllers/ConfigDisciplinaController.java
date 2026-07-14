package com.educontrol.controllers;

import com.educontrol.dao.ConfigDisciplinaDAO;
import com.educontrol.modelos.ConfigDisciplina;
import io.javalin.Javalin;

import java.sql.SQLException;

public class ConfigDisciplinaController {

    private static final ConfigDisciplinaDAO dao = new ConfigDisciplinaDAO();

    public static void registrarRutas(Javalin app) {

        app.get("/config-disciplina", ctx -> {
            try {
                ctx.json(dao.listarTodos());
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        app.get("/config-disciplina/{id}", ctx -> {
            try {
                int id = Integer.parseInt(ctx.pathParam("id"));
                ConfigDisciplina config = dao.obtenerPorId(id);

                if (config != null) {
                    ctx.json(config);
                } else {
                    ctx.status(404).result("Configuración de disciplina no encontrada");
                }
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        app.post("/config-disciplina", ctx -> {
            try {
                ConfigDisciplina config = ctx.bodyAsClass(ConfigDisciplina.class);
                dao.crear(config);
                ctx.status(201).result("Configuración de disciplina creada correctamente");
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        app.put("/config-disciplina/{id}", ctx -> {
            try {
                int id = Integer.parseInt(ctx.pathParam("id"));
                ConfigDisciplina config = ctx.bodyAsClass(ConfigDisciplina.class);
                config.setIdConfigDisciplina(id);
                dao.actualizar(config);
                ctx.result("Configuración de disciplina actualizada correctamente");
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        app.delete("/config-disciplina/{id}", ctx -> {
            try {
                int id = Integer.parseInt(ctx.pathParam("id"));
                dao.eliminar(id);
                ctx.result("Configuración de disciplina eliminada correctamente");
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });
    }
}