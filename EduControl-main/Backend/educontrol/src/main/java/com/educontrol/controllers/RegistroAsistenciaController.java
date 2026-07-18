package com.educontrol.controllers;

import com.educontrol.dao.PeriodoDAO;
import com.educontrol.dao.RegistroAsistenciaDAO;
import com.educontrol.modelos.RegistroAsistencia;
import io.javalin.Javalin;

import java.sql.SQLException;

public class RegistroAsistenciaController {

    private static final RegistroAsistenciaDAO dao = new RegistroAsistenciaDAO();
    private static final PeriodoDAO periodoDAO = new PeriodoDAO();

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

                if (registro == null) {
                    ctx.status(404).result("Registro de asistencia no encontrado");
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

        app.post("/registro-asistencia", ctx -> {
            try {
                RegistroAsistencia registro = ctx.bodyAsClass(RegistroAsistencia.class);

                if (!SeguridadUtil.alumnoPerteneceAGrupoDocente(ctx, registro.getMatricula())) {
                    ctx.status(403).result("No tienes permiso para registrar asistencia de este alumno");
                    return;
                }

                if (!periodoDAO.estaAbierto(registro.getIdPeriodo())) {
                    ctx.status(403).result("El periodo esta cerrado. No se pueden agregar registros.");
                    return;
                }

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

                if (!SeguridadUtil.alumnoPerteneceAGrupoDocente(ctx, registro.getMatricula())) {
                    ctx.status(403).result("No tienes permiso para editar este registro");
                    return;
                }

                if (!periodoDAO.estaAbierto(registro.getIdPeriodo())) {
                    ctx.status(403).result("El periodo esta cerrado. No se pueden editar registros.");
                    return;
                }

                dao.actualizar(registro);
                ctx.result("Registro de asistencia actualizado correctamente");
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        app.delete("/registro-asistencia/{id}", ctx -> {
            try {
                int id = Integer.parseInt(ctx.pathParam("id"));
                RegistroAsistencia registro = dao.obtenerPorId(id);

                if (registro == null) {
                    ctx.status(404).result("Registro de asistencia no encontrado");
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
                ctx.result("Registro de asistencia eliminado correctamente");
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });
    }
}