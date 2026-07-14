package com.educontrol.controllers;

import com.educontrol.dao.PeriodoDAO;
import com.educontrol.dao.RegistroTareaDAO;
import com.educontrol.modelos.RegistroTarea;
import io.javalin.Javalin;

import java.sql.SQLException;

public class RegistroTareaController {

    private static final RegistroTareaDAO dao = new RegistroTareaDAO();
    private static final PeriodoDAO periodoDAO = new PeriodoDAO();

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

                if (registro == null) {
                    ctx.status(404).result("Registro de tarea no encontrado");
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

        app.post("/registro-tarea", ctx -> {
            try {
                RegistroTarea registro = ctx.bodyAsClass(RegistroTarea.class);

                if (!SeguridadUtil.alumnoPerteneceAGrupoDocente(ctx, registro.getMatricula())) {
                    ctx.status(403).result("No tienes permiso para registrar tareas de este alumno");
                    return;
                }

                if (!periodoDAO.estaAbierto(registro.getIdPeriodo())) {
                    ctx.status(403).result("El periodo esta cerrado. No se pueden agregar registros.");
                    return;
                }

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

                if (!SeguridadUtil.alumnoPerteneceAGrupoDocente(ctx, registro.getMatricula())) {
                    ctx.status(403).result("No tienes permiso para editar este registro");
                    return;
                }

                if (!periodoDAO.estaAbierto(registro.getIdPeriodo())) {
                    ctx.status(403).result("El periodo esta cerrado. No se pueden editar registros.");
                    return;
                }

                dao.actualizar(registro);
                ctx.result("Registro de tarea actualizado correctamente");
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        app.delete("/registro-tarea/{id}", ctx -> {
            try {
                int id = Integer.parseInt(ctx.pathParam("id"));
                RegistroTarea registro = dao.obtenerPorId(id);

                if (registro == null) {
                    ctx.status(404).result("Registro de tarea no encontrado");
                    return;
                }

                if (!SeguridadUtil.alumnoPerteneceAGrupoDocente(ctx, registro.getMatricula())) {
                    ctx.status(403).result("No tienes permiso para eliminar este registro");
                    return;
                }

                dao.eliminar(id);
                ctx.result("Registro de tarea eliminado correctamente");
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });
    }
}