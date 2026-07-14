package com.educontrol.controllers;

import com.educontrol.dao.CampoFormativoDAO;
import com.educontrol.modelos.CampoFormativo;
import io.javalin.Javalin;

import java.sql.SQLException;

public class CampoFormativoController {

    private static final CampoFormativoDAO dao = new CampoFormativoDAO();

    public static void registrarRutas(Javalin app) {

        app.get("/campos-formativos", ctx -> {
            try {
                ctx.json(dao.listarTodos());
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        app.get("/campos-formativos/{id}", ctx -> {
            try {
                int id = Integer.parseInt(ctx.pathParam("id"));
                CampoFormativo campo = dao.obtenerPorId(id);

                if (campo != null) {
                    ctx.json(campo);
                } else {
                    ctx.status(404).result("Campo formativo no encontrado");
                }
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        app.post("/campos-formativos", ctx -> {
            try {
                CampoFormativo campo = ctx.bodyAsClass(CampoFormativo.class);
                dao.crear(campo);
                ctx.status(201).result("Campo formativo creado correctamente");
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        app.put("/campos-formativos/{id}", ctx -> {
            try {
                int id = Integer.parseInt(ctx.pathParam("id"));
                CampoFormativo campo = ctx.bodyAsClass(CampoFormativo.class);
                campo.setIdCampoFormativo(id);
                dao.actualizar(campo);
                ctx.result("Campo formativo actualizado correctamente");
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        app.delete("/campos-formativos/{id}", ctx -> {
            try {
                int id = Integer.parseInt(ctx.pathParam("id"));
                dao.eliminar(id);
                ctx.result("Campo formativo eliminado correctamente");
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });
    }
}