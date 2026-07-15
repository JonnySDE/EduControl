package com.educontrol.controllers;

import com.educontrol.dao.PeriodoDAO;
import com.educontrol.dao.RegistroDisciplinaDAO;
import com.educontrol.modelos.RegistroDisciplina;
import io.javalin.Javalin;

import java.sql.SQLException;

public class RegistroDisciplinaController {

    private static final RegistroDisciplinaDAO dao = new RegistroDisciplinaDAO();
    private static final PeriodoDAO periodoDAO = new PeriodoDAO();

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

                if (registro == null) {
                    ctx.status(404).result("Registro de disciplina no encontrado");
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

        app.post("/registro-disciplina", ctx -> {
            try {
                RegistroDisciplina registro = ctx.bodyAsClass(RegistroDisciplina.class);

                if (!SeguridadUtil.alumnoPerteneceAGrupoDocente(ctx, registro.getMatricula())) {
                    ctx.status(403).result("No tienes permiso para registrar disciplina de este alumno");
                    return;
                }

                if (!periodoDAO.estaAbierto(registro.getIdPeriodo())) {
                    ctx.status(403).result("El periodo esta cerrado. No se pueden agregar registros.");
                    return;
                }

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

                if (!SeguridadUtil.alumnoPerteneceAGrupoDocente(ctx, registro.getMatricula())) {
                    ctx.status(403).result("No tienes permiso para editar este registro");
                    return;
                }

                if (!periodoDAO.estaAbierto(registro.getIdPeriodo())) {
                    ctx.status(403).result("El periodo esta cerrado. No se pueden editar registros.");
                    return;
                }

                dao.actualizar(registro);
                ctx.result("Registro de disciplina actualizado correctamente");
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        app.delete("/registro-disciplina/{id}", ctx -> {
            try {
                int id = Integer.parseInt(ctx.pathParam("id"));
                RegistroDisciplina registro = dao.obtenerPorId(id);

                if (registro == null) {
                    ctx.status(404).result("Registro de disciplina no encontrado");
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
                ctx.result("Registro de disciplina eliminado correctamente");
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });
    }
}