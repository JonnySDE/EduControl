package com.educontrol.controllers;

import com.educontrol.dao.ConfigAsistenciaDAO;
import com.educontrol.dao.ConfigDisciplinaDAO;
import com.educontrol.dao.ConfigExamenDAO;
import com.educontrol.dao.ConfigParticipacionDAO;
import com.educontrol.dao.ConfigTareaDAO;
import com.educontrol.dao.DetalleExamenDAO;
import com.educontrol.dao.PromedioDAO;
import com.educontrol.dao.RegistroAsistenciaDAO;
import com.educontrol.dao.RegistroDisciplinaDAO;
import com.educontrol.dao.RegistroExamenDAO;
import com.educontrol.dao.RegistroParticipacionDAO;
import com.educontrol.dao.RegistroTareaDAO;
import com.educontrol.modelos.ConfigAsistencia;
import com.educontrol.modelos.ConfigDisciplina;
import com.educontrol.modelos.ConfigExamen;
import com.educontrol.modelos.ConfigParticipacion;
import com.educontrol.modelos.ConfigTarea;
import com.educontrol.modelos.DetalleExamen;
import com.educontrol.modelos.Promedio;
import com.educontrol.modelos.RegistroAsistencia;
import com.educontrol.modelos.RegistroExamen;
import com.educontrol.modelos.RegistroParticipacion;
import com.educontrol.modelos.RegistroTarea;
import io.javalin.Javalin;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PromedioController {

    private static final ConfigTareaDAO configTareaDAO = new ConfigTareaDAO();
    private static final ConfigExamenDAO configExamenDAO = new ConfigExamenDAO();
    private static final ConfigParticipacionDAO configParticipacionDAO = new ConfigParticipacionDAO();
    private static final ConfigAsistenciaDAO configAsistenciaDAO = new ConfigAsistenciaDAO();
    private static final ConfigDisciplinaDAO configDisciplinaDAO = new ConfigDisciplinaDAO();

    private static final RegistroTareaDAO registroTareaDAO = new RegistroTareaDAO();
    private static final RegistroExamenDAO registroExamenDAO = new RegistroExamenDAO();
    private static final DetalleExamenDAO detalleExamenDAO = new DetalleExamenDAO();
    private static final RegistroParticipacionDAO registroParticipacionDAO = new RegistroParticipacionDAO();
    private static final RegistroAsistenciaDAO registroAsistenciaDAO = new RegistroAsistenciaDAO();
    private static final RegistroDisciplinaDAO registroDisciplinaDAO = new RegistroDisciplinaDAO();

    private static final PromedioDAO promedioDAO = new PromedioDAO();

    public static void registrarRutas(Javalin app) {

        // Calcula el promedio SIN guardarlo (para previsualizar antes de cerrar periodo)
        app.get("/promedio/calcular", ctx -> {
            try {
                Integer idUsuario = ctx.sessionAttribute("idUsuario");
                if (idUsuario == null) {
                    ctx.status(401).result("No autorizado. Debes iniciar sesion.");
                    return;
                }

                int matricula = Integer.parseInt(ctx.queryParam("matricula"));
                int idCampoFormativo = Integer.parseInt(ctx.queryParam("idCampoFormativo"));
                int idPeriodo = Integer.parseInt(ctx.queryParam("idPeriodo"));
                int grado = Integer.parseInt(ctx.queryParam("grado"));

                Map<String, Object> resultado = calcularPromedio(idUsuario, matricula, idCampoFormativo, idPeriodo, grado);
                ctx.json(resultado);

            } catch (NumberFormatException e) {
                ctx.status(400).result("Parametros invalidos. Se requiere: matricula, idCampoFormativo, idPeriodo, grado (todos numericos).");
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        // Calcula el promedio Y lo guarda (upsert) en la tabla PROMEDIO
        app.post("/promedio/guardar", ctx -> {
            try {
                Integer idUsuario = ctx.sessionAttribute("idUsuario");
                if (idUsuario == null) {
                    ctx.status(401).result("No autorizado. Debes iniciar sesion.");
                    return;
                }

                int matricula = Integer.parseInt(ctx.queryParam("matricula"));
                int idCampoFormativo = Integer.parseInt(ctx.queryParam("idCampoFormativo"));
                int idPeriodo = Integer.parseInt(ctx.queryParam("idPeriodo"));
                int grado = Integer.parseInt(ctx.queryParam("grado"));

                Map<String, Object> resultado = calcularPromedio(idUsuario, matricula, idCampoFormativo, idPeriodo, grado);
                BigDecimal promedioFinal = (BigDecimal) resultado.get("promedioFinal");

                Promedio existente = promedioDAO.obtenerPorMatriculaCampoPeriodo(matricula, idCampoFormativo, idPeriodo);
                if (existente == null) {
                    Promedio nuevo = new Promedio(0, matricula, idCampoFormativo, promedioFinal, grado, idPeriodo);
                    promedioDAO.crear(nuevo);
                } else {
                    existente.setPromedioFinal(promedioFinal);
                    promedioDAO.actualizar(existente);
                }

                ctx.json(resultado);

            } catch (NumberFormatException e) {
                ctx.status(400).result("Parametros invalidos. Se requiere: matricula, idCampoFormativo, idPeriodo, grado (todos numericos).");
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });
    }

    private static Map<String, Object> calcularPromedio(int idUsuario, int matricula, int idCampoFormativo, int idPeriodo, int grado) throws SQLException {

        // --- Obtener los 5 porcentajes configurados por el docente ---
        ConfigTarea configTarea = configTareaDAO.obtenerPorUsuario(idUsuario);
        ConfigExamen configExamen = configExamenDAO.obtenerPorUsuario(idUsuario);
        ConfigParticipacion configParticipacion = configParticipacionDAO.obtenerPorUsuario(idUsuario);
        ConfigAsistencia configAsistencia = configAsistenciaDAO.obtenerPorUsuario(idUsuario);
        ConfigDisciplina configDisciplina = configDisciplinaDAO.obtenerPorUsuario(idUsuario);

        int pctTarea = configTarea != null ? configTarea.getPorcentaje() : 0;
        int pctExamen = configExamen != null ? configExamen.getPorcentaje() : 0;
        int pctParticipacion = configParticipacion != null ? configParticipacion.getPorcentaje() : 0;
        int pctAsistencia = configAsistencia != null ? configAsistencia.getPorcentaje() : 0;
        int pctDisciplina = configDisciplina != null ? configDisciplina.getPorcentaje() : 0;

        // --- 1. TAREA: (entregadas / total) x 10 x (%tarea/100) ---
        List<RegistroTarea> tareas = registroTareaDAO.listarPorAlumnoCampoPeriodo(matricula, idCampoFormativo, idPeriodo);
        double resultadoTarea = 0;
        if (!tareas.isEmpty()) {
            long entregadas = tareas.stream().filter(t -> "Entrego".equals(t.getEstatus())).count();
            double proporcion = (double) entregadas / tareas.size();
            resultadoTarea = proporcion * 10 * (pctTarea / 100.0);
        }

        // --- 2. EXAMEN: promedio de (aciertos/totalPreguntas) x 10 x (%examen/100) ---
        List<RegistroExamen> examenes = registroExamenDAO.listarPorAlumnoCampoPeriodo(matricula, idCampoFormativo, idPeriodo);
        double resultadoExamen = 0;
        if (!examenes.isEmpty()) {
            double sumaProporciones = 0;
            int examenesConDetalle = 0;
            for (RegistroExamen re : examenes) {
                DetalleExamen detalle = detalleExamenDAO.obtenerPorRegistroExamen(re.getIdRegistroExamen());
                if (detalle != null && detalle.getTotalPreguntas() > 0) {
                    sumaProporciones += (double) detalle.getAciertos() / detalle.getTotalPreguntas();
                    examenesConDetalle++;
                }
            }
            if (examenesConDetalle > 0) {
                double proporcionPromedio = sumaProporciones / examenesConDetalle;
                resultadoExamen = proporcionPromedio * 10 * (pctExamen / 100.0);
            }
        }

        // --- 3. PARTICIPACION: promedio de Puntuacion x (%participacion/100) ---
        List<RegistroParticipacion> participaciones = registroParticipacionDAO.listarPorAlumnoCampoPeriodo(matricula, idCampoFormativo, idPeriodo);
        double resultadoParticipacion = 0;
        if (!participaciones.isEmpty()) {
            double sumaPuntuaciones = participaciones.stream().mapToDouble(RegistroParticipacion::getPuntuacion).sum();
            double promedioPuntuacion = sumaPuntuaciones / participaciones.size();
            resultadoParticipacion = promedioPuntuacion * (pctParticipacion / 100.0);
        }

        // --- 4. ASISTENCIA: (dias con Asistencia / total dias) x 10 x (%asistencia/100) ---
        List<RegistroAsistencia> asistencias = registroAsistenciaDAO.listarPorAlumnoPeriodo(matricula, idPeriodo);
        double resultadoAsistencia = 0;
        if (!asistencias.isEmpty()) {
            long diasAsistio = asistencias.stream().filter(a -> "Asistencia".equals(a.getEstado())).count();
            double proporcion = (double) diasAsistio / asistencias.size();
            resultadoAsistencia = proporcion * 10 * (pctAsistencia / 100.0);
        }

        // --- 5. DISCIPLINA: (10 - sumaPuntosMenos, minimo 0) x (%disciplina/100) ---
        int sumaPuntosMenos = registroDisciplinaDAO.sumarPuntosMenosPorAlumnoPeriodo(matricula, idPeriodo);
        double calificacionDisciplina = Math.max(0, 10 - sumaPuntosMenos);
        double resultadoDisciplina = calificacionDisciplina * (pctDisciplina / 100.0);

        // --- SUMA FINAL, con clamping a minimo 5 ---
        double sumaFinal = resultadoTarea + resultadoExamen + resultadoParticipacion + resultadoAsistencia + resultadoDisciplina;
        double promedioFinalDouble = Math.max(5.0, sumaFinal);

        BigDecimal promedioFinal = BigDecimal.valueOf(promedioFinalDouble).setScale(2, RoundingMode.HALF_UP);

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("matricula", matricula);
        resultado.put("idCampoFormativo", idCampoFormativo);
        resultado.put("idPeriodo", idPeriodo);
        resultado.put("resultadoTarea", BigDecimal.valueOf(resultadoTarea).setScale(2, RoundingMode.HALF_UP));
        resultado.put("resultadoExamen", BigDecimal.valueOf(resultadoExamen).setScale(2, RoundingMode.HALF_UP));
        resultado.put("resultadoParticipacion", BigDecimal.valueOf(resultadoParticipacion).setScale(2, RoundingMode.HALF_UP));
        resultado.put("resultadoAsistencia", BigDecimal.valueOf(resultadoAsistencia).setScale(2, RoundingMode.HALF_UP));
        resultado.put("resultadoDisciplina", BigDecimal.valueOf(resultadoDisciplina).setScale(2, RoundingMode.HALF_UP));
        resultado.put("sumaSinClamp", BigDecimal.valueOf(sumaFinal).setScale(2, RoundingMode.HALF_UP));
        resultado.put("promedioFinal", promedioFinal);

        return resultado;
    }
}