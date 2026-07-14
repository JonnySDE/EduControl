package com.educontrol.controllers;

import com.educontrol.dao.PromedioDAO;
import com.educontrol.modelos.Promedio;
import io.javalin.Javalin;

import java.sql.SQLException;

public class PromedioController {

    private static final PromedioDAO dao = new PromedioDAO();

    public static void registrarRutas(Javalin app) {

        app.get("/promedios", ctx -> {
            try {
                ctx.json(dao.listarTodos());
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        app.get("/promedios/{id}", ctx -> {
            try {
                int id = Integer.parseInt(ctx.pathParam("id"));
                Promedio promedio = dao.obtenerPorId(id);

                if (promedio != null) {
                    ctx.json(promedio);
                } else {
                    ctx.status(404).result("Promedio no encontrado");
                }
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        app.post("/promedios", ctx -> {
            try {
                Promedio promedio = ctx.bodyAsClass(Promedio.class);
                dao.crear(promedio);
                ctx.status(201).result("Promedio creado correctamente");
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        app.put("/promedios/{id}", ctx -> {
            try {
                int id = Integer.parseInt(ctx.pathParam("id"));
                Promedio promedio = ctx.bodyAsClass(Promedio.class);
                promedio.setIdPromedio(id);
                dao.actualizar(promedio);
                ctx.result("Promedio actualizado correctamente");
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        app.delete("/promedios/{id}", ctx -> {
            try {
                int id = Integer.parseInt(ctx.pathParam("id"));
                dao.eliminar(id);
                ctx.result("Promedio eliminado correctamente");
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });
    }
}