package com.educontrol.controllers;

import com.educontrol.dao.PeriodoDAO;
import com.educontrol.dao.RegistroExamenDAO;
import com.educontrol.modelos.RegistroExamen;
import io.javalin.Javalin;

import java.sql.SQLException;

public class RegistroExamenController {

    private static final RegistroExamenDAO dao = new RegistroExamenDAO();
    private static final PeriodoDAO periodoDAO = new PeriodoDAO();

    public static void registrarRutas(Javalin app) {

        app.get("/registro-examen", ctx -> {
            try {
                ctx.json(dao.listarTodos());
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        app.get("/registro-examen/{id}", ctx -> {
            try {
                int id = Integer.parseInt(ctx.pathParam("id"));
                RegistroExamen registro = dao.obtenerPorId(id);

                if (registro == null) {
                    ctx.status(404).result("Registro de examen no encontrado");
                    return;
                }

                if (!SeguridadUtil.alumnoPerteneceAGrupoDocente(ctx, registro.getMatricula())) {
                    ctx.status(403).result("No tienes permiso para ver este registro");
                    return;
                }

                ctx.json(registro);
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        app.post("/registro-examen", ctx -> {
            try {
                RegistroExamen registro = ctx.bodyAsClass(RegistroExamen.class);

                if (!SeguridadUtil.alumnoPerteneceAGrupoDocente(ctx, registro.getMatricula())) {
                    ctx.status(403).result("No tienes permiso para registrar examenes de este alumno");
                    return;
                }

                if (!periodoDAO.estaAbierto(registro.getIdPeriodo())) {
                    ctx.status(403).result("El periodo esta cerrado. No se pueden agregar registros.");
                    return;
                }

                dao.crear(registro);
                ctx.status(201).result("Registro de examen creado correctamente");
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        app.put("/registro-examen/{id}", ctx -> {
            try {
                int id = Integer.parseInt(ctx.pathParam("id"));
                RegistroExamen registro = ctx.bodyAsClass(RegistroExamen.class);
                registro.setIdRegistroExamen(id);

                if (!SeguridadUtil.alumnoPerteneceAGrupoDocente(ctx, registro.getMatricula())) {
                    ctx.status(403).result("No tienes permiso para editar este registro");
                    return;
                }

                if (!periodoDAO.estaAbierto(registro.getIdPeriodo())) {
                    ctx.status(403).result("El periodo esta cerrado. No se pueden editar registros.");
                    return;
                }

                dao.actualizar(registro);
                ctx.result("Registro de examen actualizado correctamente");
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        app.delete("/registro-examen/{id}", ctx -> {
            try {
                int id = Integer.parseInt(ctx.pathParam("id"));
                RegistroExamen registro = dao.obtenerPorId(id);

                if (registro == null) {
                    ctx.status(404).result("Registro de examen no encontrado");
                    return;
                }

                if (!SeguridadUtil.alumnoPerteneceAGrupoDocente(ctx, registro.getMatricula())) {
                    ctx.status(403).result("No tienes permiso para eliminar este registro");
                    return;
                }

                dao.eliminar(id);
                ctx.result("Registro de examen eliminado correctamente");
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });
    }
}