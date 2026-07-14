package com.educontrol.controllers;

import com.educontrol.dao.RegistroParticipacionDAO;
import com.educontrol.modelos.RegistroParticipacion;
import io.javalin.Javalin;

import java.sql.SQLException;

public class RegistroParticipacionController {

    private static final RegistroParticipacionDAO dao = new RegistroParticipacionDAO();

    public static void registrarRutas(Javalin app) {

        app.get("/registro-participacion", ctx -> {
            try {
                ctx.json(dao.listarTodos());
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        app.get("/registro-participacion/{id}", ctx -> {
            try {
                int id = Integer.parseInt(ctx.pathParam("id"));
                RegistroParticipacion registro = dao.obtenerPorId(id);

                if (registro != null) {
                    ctx.json(registro);
                } else {
                    ctx.status(404).result("Registro de participación no encontrado");
                }
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        app.post("/registro-participacion", ctx -> {
            try {
                RegistroParticipacion registro = ctx.bodyAsClass(RegistroParticipacion.class);
                dao.crear(registro);
                ctx.status(201).result("Registro de participación creado correctamente");
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        app.put("/registro-participacion/{id}", ctx -> {
            try {
                int id = Integer.parseInt(ctx.pathParam("id"));
                RegistroParticipacion registro = ctx.bodyAsClass(RegistroParticipacion.class);
                registro.setIdRegistroParticipacion(id);
                dao.actualizar(registro);
                ctx.result("Registro de participación actualizado correctamente");
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        app.delete("/registro-participacion/{id}", ctx -> {
            try {
                int id = Integer.parseInt(ctx.pathParam("id"));
                dao.eliminar(id);
                ctx.result("Registro de participación eliminado correctamente");
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });
    }
}