package com.educontrol.controllers;

import com.educontrol.dao.PeriodoDAO;
import com.educontrol.modelos.Periodo;
import io.javalin.Javalin;

import java.sql.SQLException;

public class PeriodoController {

    private static final PeriodoDAO dao = new PeriodoDAO();

    public static void registrarRutas(Javalin app) {

        app.get("/periodos", ctx -> {
            try {
                ctx.json(dao.listarTodos());
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        app.get("/periodos/{id}", ctx -> {
            try {
                int id = Integer.parseInt(ctx.pathParam("id"));
                Periodo periodo = dao.obtenerPorId(id);

                if (periodo != null) {
                    ctx.json(periodo);
                } else {
                    ctx.status(404).result("Periodo no encontrado");
                }
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        app.post("/periodos", ctx -> {
            try {
                Periodo periodo = ctx.bodyAsClass(Periodo.class);
                dao.crear(periodo);
                ctx.status(201).result("Periodo creado correctamente");
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        app.put("/periodos/{id}", ctx -> {
            try {
                int id = Integer.parseInt(ctx.pathParam("id"));
                Periodo periodo = ctx.bodyAsClass(Periodo.class);
                periodo.setIdPeriodo(id);
                dao.actualizar(periodo);
                ctx.result("Periodo actualizado correctamente");
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        app.delete("/periodos/{id}", ctx -> {
            try {
                int id = Integer.parseInt(ctx.pathParam("id"));
                dao.eliminar(id);
                ctx.result("Periodo eliminado correctamente");
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        // Cerrar un periodo (bloquea nuevos registros para ese periodo)
        app.put("/periodos/{id}/cerrar", ctx -> {
            try {
                int id = Integer.parseInt(ctx.pathParam("id"));
                dao.cerrarPeriodo(id);
                ctx.result("Periodo cerrado correctamente");
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });
    }
}