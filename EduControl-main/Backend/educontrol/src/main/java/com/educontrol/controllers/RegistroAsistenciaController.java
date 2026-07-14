package com.educontrol.controllers;

import com.educontrol.dao.RegistroAsistenciaDAO;
import com.educontrol.modelos.RegistroAsistencia;
import io.javalin.Javalin;

import java.sql.SQLException;

public class RegistroAsistenciaController {

    private static final RegistroAsistenciaDAO dao = new RegistroAsistenciaDAO();

    public static void registrarRutas(Javalin app) {

        app.get("/registro-asistencia", ctx -> {
            try {
                ctx.json(dao.listarTodos());
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        app.get("/registro-asistencia/{id}", ctx -> {
            try {
                int id = Integer.parseInt(ctx.pathParam("id"));
                RegistroAsistencia registro = dao.obtenerPorId(id);

                if (registro != null) {
                    ctx.json(registro);
                } else {
                    ctx.status(404).result("Registro de asistencia no encontrado");
                }
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        app.post("/registro-asistencia", ctx -> {
            try {
                RegistroAsistencia registro = ctx.bodyAsClass(RegistroAsistencia.class);
                dao.crear(registro);
                ctx.status(201).result("Registro de asistencia creado correctamente");
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        app.put("/registro-asistencia/{id}", ctx -> {
            try {
                int id = Integer.parseInt(ctx.pathParam("id"));
                RegistroAsistencia registro = ctx.bodyAsClass(RegistroAsistencia.class);
                registro.setIdRegistroAsis(id);
                dao.actualizar(registro);
                ctx.result("Registro de asistencia actualizado correctamente");
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        app.delete("/registro-asistencia/{id}", ctx -> {
            try {
                int id = Integer.parseInt(ctx.pathParam("id"));
                dao.eliminar(id);
                ctx.result("Registro de asistencia eliminado correctamente");
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });
    }
}