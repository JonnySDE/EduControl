package com.educontrol.controllers;

import com.educontrol.dao.RegistroDisciplinaDAO;
import com.educontrol.modelos.RegistroDisciplina;
import io.javalin.Javalin;

import java.sql.SQLException;

public class RegistroDisciplinaController {

    private static final RegistroDisciplinaDAO dao = new RegistroDisciplinaDAO();

    public static void registrarRutas(Javalin app) {

        app.get("/registro-disciplina", ctx -> {
            try {
                ctx.json(dao.listarTodos());
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        app.get("/registro-disciplina/{id}", ctx -> {
            try {
                int id = Integer.parseInt(ctx.pathParam("id"));
                RegistroDisciplina registro = dao.obtenerPorId(id);

                if (registro != null) {
                    ctx.json(registro);
                } else {
                    ctx.status(404).result("Registro de disciplina no encontrado");
                }
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        app.post("/registro-disciplina", ctx -> {
            try {
                RegistroDisciplina registro = ctx.bodyAsClass(RegistroDisciplina.class);
                dao.crear(registro);
                ctx.status(201).result("Registro de disciplina creado correctamente");
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        app.put("/registro-disciplina/{id}", ctx -> {
            try {
                int id = Integer.parseInt(ctx.pathParam("id"));
                RegistroDisciplina registro = ctx.bodyAsClass(RegistroDisciplina.class);
                registro.setIdRegistroDisciplina(id);
                dao.actualizar(registro);
                ctx.result("Registro de disciplina actualizado correctamente");
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        app.delete("/registro-disciplina/{id}", ctx -> {
            try {
                int id = Integer.parseInt(ctx.pathParam("id"));
                dao.eliminar(id);
                ctx.result("Registro de disciplina eliminado correctamente");
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });
    }
}