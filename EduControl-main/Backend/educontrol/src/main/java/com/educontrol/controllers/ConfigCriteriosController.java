package com.educontrol.controllers;

import com.educontrol.dao.ConfigAsistenciaDAO;
import com.educontrol.dao.ConfigDisciplinaDAO;
import com.educontrol.dao.ConfigExamenDAO;
import com.educontrol.dao.ConfigParticipacionDAO;
import com.educontrol.dao.ConfigTareaDAO;
import com.educontrol.modelos.ConfigAsistencia;
import com.educontrol.modelos.ConfigCriteriosRequest;
import com.educontrol.modelos.ConfigDisciplina;
import com.educontrol.modelos.ConfigExamen;
import com.educontrol.modelos.ConfigParticipacion;
import com.educontrol.modelos.ConfigTarea;
import io.javalin.Javalin;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ConfigCriteriosController {

    private static final ConfigTareaDAO tareaDAO = new ConfigTareaDAO();
    private static final ConfigExamenDAO examenDAO = new ConfigExamenDAO();
    private static final ConfigParticipacionDAO participacionDAO = new ConfigParticipacionDAO();
    private static final ConfigAsistenciaDAO asistenciaDAO = new ConfigAsistenciaDAO();
    private static final ConfigDisciplinaDAO disciplinaDAO = new ConfigDisciplinaDAO();

    // Arma el mapa de resultado a partir de un idUsuario dado (reutilizado por ambos endpoints GET)
    private static Map<String, Object> obtenerCriteriosDeUsuario(int idUsuario) throws SQLException {
        ConfigTarea tarea = tareaDAO.obtenerPorUsuario(idUsuario);
        ConfigExamen examen = examenDAO.obtenerPorUsuario(idUsuario);
        ConfigParticipacion participacion = participacionDAO.obtenerPorUsuario(idUsuario);
        ConfigAsistencia asistencia = asistenciaDAO.obtenerPorUsuario(idUsuario);
        ConfigDisciplina disciplina = disciplinaDAO.obtenerPorUsuario(idUsuario);

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("porcentajeTarea", tarea != null ? tarea.getPorcentaje() : null);
        resultado.put("porcentajeExamen", examen != null ? examen.getPorcentaje() : null);
        resultado.put("porcentajeParticipacion", participacion != null ? participacion.getPorcentaje() : null);
        resultado.put("porcentajeAsistencia", asistencia != null ? asistencia.getPorcentaje() : null);
        resultado.put("porcentajeDisciplina", disciplina != null ? disciplina.getPorcentaje() : null);
        return resultado;
    }

    public static void registrarRutas(Javalin app) {

        // Guardar/actualizar MIS PROPIOS criterios (solo el docente/director logueado, sobre sí mismo)
        app.post("/config-criterios", ctx -> {
            try {
                Integer idUsuario = ctx.sessionAttribute("idUsuario");
                if (idUsuario == null) {
                    ctx.status(401).result("No autorizado. Debes iniciar sesion.");
                    return;
                }

                ConfigCriteriosRequest request = ctx.bodyAsClass(ConfigCriteriosRequest.class);

                String errorValidacion = ValidacionPorcentajesUtil.validar(request);
                if (errorValidacion != null) {
                    ctx.status(400).result(errorValidacion);
                    return;
                }

                // Tarea
                ConfigTarea tareaExistente = tareaDAO.obtenerPorUsuario(idUsuario);
                if (tareaExistente == null) {
                    ConfigTarea nuevaTarea = new ConfigTarea(0, request.getPorcentajeTarea(), idUsuario);
                    tareaDAO.crear(nuevaTarea);
                } else {
                    tareaExistente.setPorcentaje(request.getPorcentajeTarea());
                    tareaDAO.actualizar(tareaExistente);
                }

                // Examen
                ConfigExamen examenExistente = examenDAO.obtenerPorUsuario(idUsuario);
                if (examenExistente == null) {
                    ConfigExamen nuevoExamen = new ConfigExamen(0, request.getPorcentajeExamen(), idUsuario);
                    examenDAO.crear(nuevoExamen);
                } else {
                    examenExistente.setPorcentaje(request.getPorcentajeExamen());
                    examenDAO.actualizar(examenExistente);
                }

                // Participacion
                ConfigParticipacion participacionExistente = participacionDAO.obtenerPorUsuario(idUsuario);
                if (participacionExistente == null) {
                    ConfigParticipacion nuevaParticipacion = new ConfigParticipacion(0, request.getPorcentajeParticipacion(), idUsuario);
                    participacionDAO.crear(nuevaParticipacion);
                } else {
                    participacionExistente.setPorcentaje(request.getPorcentajeParticipacion());
                    participacionDAO.actualizar(participacionExistente);
                }

                // Asistencia
                ConfigAsistencia asistenciaExistente = asistenciaDAO.obtenerPorUsuario(idUsuario);
                if (asistenciaExistente == null) {
                    ConfigAsistencia nuevaAsistencia = new ConfigAsistencia(0, request.getPorcentajeAsistencia(), idUsuario);
                    asistenciaDAO.crear(nuevaAsistencia);
                } else {
                    asistenciaExistente.setPorcentaje(request.getPorcentajeAsistencia());
                    asistenciaDAO.actualizar(asistenciaExistente);
                }

                // Disciplina
                ConfigDisciplina disciplinaExistente = disciplinaDAO.obtenerPorUsuario(idUsuario);
                if (disciplinaExistente == null) {
                    ConfigDisciplina nuevaDisciplina = new ConfigDisciplina(0, request.getPorcentajeDisciplina(), idUsuario);
                    disciplinaDAO.crear(nuevaDisciplina);
                } else {
                    disciplinaExistente.setPorcentaje(request.getPorcentajeDisciplina());
                    disciplinaDAO.actualizar(disciplinaExistente);
                }

                ctx.status(200).result("Criterios de evaluacion guardados correctamente");

            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        // Consultar MIS PROPIOS criterios (docente o director, sobre sí mismo)
        app.get("/config-criterios", ctx -> {
            try {
                Integer idUsuario = ctx.sessionAttribute("idUsuario");
                if (idUsuario == null) {
                    ctx.status(401).result("No autorizado. Debes iniciar sesion.");
                    return;
                }

                ctx.json(obtenerCriteriosDeUsuario(idUsuario));

            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        // Consultar los criterios de UN DOCENTE ESPECÍFICO (solo el Director puede usar esto, solo lectura)
        app.get("/config-criterios/{idUsuario}", ctx -> {
            String rolSesion = ctx.sessionAttribute("rol");
            if (!"Director".equals(rolSesion)) {
                ctx.status(403).result("Solo el Director puede consultar los criterios de otro usuario");
                return;
            }

            int idUsuarioConsultado;
            try {
                idUsuarioConsultado = Integer.parseInt(ctx.pathParam("idUsuario"));
            } catch (NumberFormatException e) {
                ctx.status(400).result("idUsuario inválido: " + ctx.pathParam("idUsuario"));
                return;
            }

            try {
                Map<String, Object> criterios = obtenerCriteriosDeUsuario(idUsuarioConsultado);
                ctx.json(criterios);
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });
    }
}