package com.educontrol.controllers;

import com.educontrol.dao.ConfigParticipacionDAO;
import com.educontrol.dao.GrupoDAO;
import com.educontrol.dao.PeriodoDAO;
import com.educontrol.dao.RegistroParticipacionDAO;
import com.educontrol.modelos.ConfigParticipacion;
import com.educontrol.modelos.Grupo;
import com.educontrol.modelos.RegistroParticipacion;
import io.javalin.Javalin;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class RegistroParticipacionController {

    private static final RegistroParticipacionDAO dao = new RegistroParticipacionDAO();
    private static final GrupoDAO grupoDAO = new GrupoDAO();
    private static final ConfigParticipacionDAO configParticipacionDAO = new ConfigParticipacionDAO();
    private static final PeriodoDAO periodoDAO = new PeriodoDAO();

    // Revierte la conversion 5-10 -> 0-10 SOLO para exponerla en las respuestas GET.
    // No toca la base de datos: opera sobre el objeto ya leido en memoria antes de mandarlo como JSON.
    private static RegistroParticipacion conPuntuacionReal(RegistroParticipacion registro) {
        if (registro == null) return null;
        float puntuacionReal = (registro.getPuntuacion() - 5f) * 2f;
        registro.setPuntuacion(puntuacionReal);
        return registro;
    }

    public static void registrarRutas(Javalin app) {

        app.get("/registro-participacion", ctx -> {
            try {
                List<RegistroParticipacion> lista = dao.listarTodos();
                lista.forEach(RegistroParticipacionController::conPuntuacionReal);
                ctx.json(lista);
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

                ctx.json(conPuntuacionReal(registro));
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        // Body esperado: { matricula, idCampoFormativo, puntuacion } (rango 0-10, ver nota abajo)
        app.post("/registro-participacion", ctx -> {
            try {
                Integer idUsuarioSesion = ctx.sessionAttribute("idUsuario");
                if (idUsuarioSesion == null) {
                    ctx.status(401).result("No autorizado. Debes iniciar sesion.");
                    return;
                }

                Map<String, Object> body = ctx.bodyAsClass(Map.class);
                int matricula = ((Number) body.get("matricula")).intValue();
                int idCampoFormativo = ((Number) body.get("idCampoFormativo")).intValue();
                float puntuacionReal = ((Number) body.get("puntuacion")).floatValue();

                if (puntuacionReal < 0 || puntuacionReal > 10) {
                    ctx.status(400).result("La puntuación debe estar entre 0 y 10");
                    return;
                }

                float puntuacionParaGuardar = 5f + (puntuacionReal / 10f) * 5f;

                if (!SeguridadUtil.alumnoPerteneceAGrupoDocente(ctx, matricula)) {
                    ctx.status(403).result("No tienes permiso para registrar participación de este alumno");
                    return;
                }

                Integer idGrupoDocente = SeguridadUtil.obtenerGrupoDelDocente(ctx);
                if (idGrupoDocente == null) {
                    ctx.status(403).result("Solo un Docente con grupo asignado puede registrar participación");
                    return;
                }

                Grupo grupo = grupoDAO.obtenerPorId(idGrupoDocente);
                if (grupo == null) {
                    ctx.status(500).result("No se pudo determinar el grado del grupo");
                    return;
                }

                ConfigParticipacion configParticipacion = configParticipacionDAO.obtenerPorUsuario(idUsuarioSesion);
                if (configParticipacion == null) {
                    ctx.status(400).result("Primero debes configurar tu criterio de Participación en el modulo de Evaluacion");
                    return;
                }

                int idPeriodo = PeriodoUtil.obtenerIdPeriodoParaMateria(idGrupoDocente, grupo.getGrado(), idCampoFormativo);

                RegistroParticipacion registro = new RegistroParticipacion(
                    0,
                    puntuacionParaGuardar,
                    LocalDate.now(),
                    matricula,
                    configParticipacion.getIdParticipacion(),
                    idUsuarioSesion,
                    idCampoFormativo,
                    idPeriodo
                );

                dao.crear(registro);
                ctx.status(201).result("Registro de participación creado correctamente");
            } catch (SQLException e) {
                ctx.status(400).result("Error: " + e.getMessage());
            } catch (Exception e) {
                ctx.status(400).result("Datos invalidos: " + e.getMessage());
            }
        });

        app.put("/registro-participacion/{id}", ctx -> {
            try {
                int id = Integer.parseInt(ctx.pathParam("id"));
                RegistroParticipacion existente = dao.obtenerPorId(id);
                if (existente == null) {
                    ctx.status(404).result("Registro de participación no encontrado");
                    return;
                }

                if (!SeguridadUtil.alumnoPerteneceAGrupoDocente(ctx, existente.getMatricula())) {
                    ctx.status(403).result("No tienes permiso para editar este registro");
                    return;
                }

                if (!periodoDAO.estaAbierto(existente.getIdPeriodo())) {
                    ctx.status(403).result("El periodo ya esta cerrado. No se puede editar este registro.");
                    return;
                }

                Map<String, Object> body = ctx.bodyAsClass(Map.class);
                float puntuacionReal = ((Number) body.get("puntuacion")).floatValue();

                if (puntuacionReal < 0 || puntuacionReal > 10) {
                    ctx.status(400).result("La puntuación debe estar entre 0 y 10");
                    return;
                }

                float puntuacionParaGuardar = 5f + (puntuacionReal / 10f) * 5f;
                existente.setPuntuacion(puntuacionParaGuardar);

                dao.actualizar(existente);
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
                    ctx.status(403).result("El periodo ya esta cerrado. No se puede eliminar este registro.");
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