package com.educontrol.controllers;

import com.educontrol.dao.PeriodoDAO;
import com.educontrol.dao.RegistroParticipacionDAO;
import com.educontrol.modelos.RegistroParticipacion;
import io.javalin.Javalin;

import java.sql.SQLException;

public class RegistroParticipacionController {

    private static final RegistroParticipacionDAO dao = new RegistroParticipacionDAO();
    private static final PeriodoDAO periodoDAO = new PeriodoDAO();

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

                if (registro == null) {
                    ctx.status(404).result("Registro de participación no encontrado");
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

        app.post("/registro-participacion", ctx -> {
            try {
                RegistroParticipacion registro = ctx.bodyAsClass(RegistroParticipacion.class);

                if (!SeguridadUtil.alumnoPerteneceAGrupoDocente(ctx, registro.getMatricula())) {
                    ctx.status(403).result("No tienes permiso para registrar participación de este alumno");
                    return;
                }

                if (!periodoDAO.estaAbierto(registro.getIdPeriodo())) {
                    ctx.status(403).result("El periodo esta cerrado. No se pueden agregar registros.");
                    return;
                }

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

                if (!SeguridadUtil.alumnoPerteneceAGrupoDocente(ctx, registro.getMatricula())) {
                    ctx.status(403).result("No tienes permiso para editar este registro");
                    return;
                }

                if (!periodoDAO.estaAbierto(registro.getIdPeriodo())) {
                    ctx.status(403).result("El periodo esta cerrado. No se pueden editar registros.");
                    return;
                }

                dao.actualizar(registro);
                ctx.result("Registro de participación actualizado correctamente");
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        app.delete("/registro-participacion/{id}", ctx -> {
            try {
                int id = Integer.parseInt(ctx.pathParam("id"));
                RegistroParticipacion registro = dao.obtenerPorId(id);

                if (registro == null) {
                    ctx.status(404).result("Registro de participación no encontrado");
                    return;
                }

                if (!SeguridadUtil.alumnoPerteneceAGrupoDocente(ctx, registro.getMatricula())) {
                    ctx.status(403).result("No tienes permiso para eliminar este registro");
                    return;
                }

                if (!periodoDAO.estaAbierto(registro.getIdPeriodo())) {
                    ctx.status(403).result("El periodo esta cerrado. No se pueden eliminar registros.");
                    return;
                }

                dao.eliminar(id);
                ctx.result("Registro de participación eliminado correctamente");
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });
    }
}