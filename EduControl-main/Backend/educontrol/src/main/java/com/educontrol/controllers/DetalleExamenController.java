package com.educontrol.controllers;

import com.educontrol.dao.DetalleExamenDAO;
import com.educontrol.modelos.DetalleExamen;
import io.javalin.Javalin;

import java.sql.SQLException;

public class DetalleExamenController {

    private static final DetalleExamenDAO dao = new DetalleExamenDAO();

    public static void registrarRutas(Javalin app) {

        app.get("/detalle-examen", ctx -> {
            try {
                ctx.json(dao.listarTodos());
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        app.get("/detalle-examen/{id}", ctx -> {
            try {
                int id = Integer.parseInt(ctx.pathParam("id"));
                DetalleExamen detalle = dao.obtenerPorId(id);

                if (detalle != null) {
                    ctx.json(detalle);
                } else {
                    ctx.status(404).result("Detalle de examen no encontrado");
                }
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        app.post("/detalle-examen", ctx -> {
            try {
                DetalleExamen detalle = ctx.bodyAsClass(DetalleExamen.class);
                dao.crear(detalle);
                ctx.status(201).result("Detalle de examen creado correctamente");
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        app.put("/detalle-examen/{id}", ctx -> {
            try {
                int id = Integer.parseInt(ctx.pathParam("id"));
                DetalleExamen detalle = ctx.bodyAsClass(DetalleExamen.class);
                detalle.setIdDetalleExamen(id);
                dao.actualizar(detalle);
                ctx.result("Detalle de examen actualizado correctamente");
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        app.delete("/detalle-examen/{id}", ctx -> {
            try {
                int id = Integer.parseInt(ctx.pathParam("id"));
                dao.eliminar(id);
                ctx.result("Detalle de examen eliminado correctamente");
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });
    }
}