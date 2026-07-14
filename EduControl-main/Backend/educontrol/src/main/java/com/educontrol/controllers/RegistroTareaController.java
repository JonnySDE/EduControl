package com.educontrol.controllers;

import com.educontrol.dao.RegistroTareaDAO;
import com.educontrol.modelos.RegistroTarea;
import io.javalin.Javalin;

import java.sql.SQLException;

public class RegistroTareaController {

    private static final RegistroTareaDAO dao = new RegistroTareaDAO();

    public static void registrarRutas(Javalin app) {

        app.get("/registro-tarea", ctx -> {
            try {
                ctx.json(dao.listarTodos());
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        app.get("/registro-tarea/{id}", ctx -> {
            try {
                int id = Integer.parseInt(ctx.pathParam("id"));
                RegistroTarea registro = dao.obtenerPorId(id);

                if (registro != null) {
                    ctx.json(registro);
                } else {
                    ctx.status(404).result("Registro de tarea no encontrado");
                }
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        app.post("/registro-tarea", ctx -> {
            try {
                RegistroTarea registro = ctx.bodyAsClass(RegistroTarea.class);
                dao.crear(registro);
                ctx.status(201).result("Registro de tarea creado correctamente");
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        app.put("/registro-tarea/{id}", ctx -> {
            try {
                int id = Integer.parseInt(ctx.pathParam("id"));
                RegistroTarea registro = ctx.bodyAsClass(RegistroTarea.class);
                registro.setIdRegistroTarea(id);
                dao.actualizar(registro);
                ctx.result("Registro de tarea actualizado correctamente");
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        app.delete("/registro-tarea/{id}", ctx -> {
            try {
                int id = Integer.parseInt(ctx.pathParam("id"));
                dao.eliminar(id);
                ctx.result("Registro de tarea eliminado correctamente");
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });
    }
}