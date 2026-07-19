package com.educontrol.controllers;

import com.educontrol.dao.AlumnoDAO;
import com.educontrol.dao.AlumnoGrupoDAO;
import com.educontrol.dao.ConfigAsistenciaDAO;
import com.educontrol.dao.ConfigDisciplinaDAO;
import com.educontrol.dao.ConfigExamenDAO;
import com.educontrol.dao.ConfigParticipacionDAO;
import com.educontrol.dao.ConfigTareaDAO;
import com.educontrol.dao.DetalleExamenDAO;
import com.educontrol.dao.CampoFormativoDAO;
import com.educontrol.dao.GrupoDAO;
import com.educontrol.dao.PeriodoDAO;
import com.educontrol.dao.PromedioDAO;
import com.educontrol.dao.RegistroAsistenciaDAO;
import com.educontrol.dao.RegistroDisciplinaDAO;
import com.educontrol.dao.RegistroExamenDAO;
import com.educontrol.dao.RegistroParticipacionDAO;
import com.educontrol.dao.RegistroTareaDAO;
import com.educontrol.modelos.Alumno;
import com.educontrol.modelos.AlumnoGrupo;
import com.educontrol.modelos.CampoFormativo;
import com.educontrol.modelos.ConfigAsistencia;
import com.educontrol.modelos.ConfigDisciplina;
import com.educontrol.modelos.ConfigExamen;
import com.educontrol.modelos.ConfigParticipacion;
import com.educontrol.modelos.ConfigTarea;
import com.educontrol.modelos.DetalleExamen;
import com.educontrol.modelos.Grupo;
import com.educontrol.modelos.Periodo;
import com.educontrol.modelos.Promedio;
import com.educontrol.modelos.RegistroAsistencia;
import com.educontrol.modelos.RegistroExamen;
import com.educontrol.modelos.RegistroParticipacion;
import com.educontrol.modelos.RegistroTarea;
import io.javalin.Javalin;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.ArrayList;
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

    private static final PeriodoDAO periodoDAO = new PeriodoDAO();
    private static final PromedioDAO promedioDAO = new PromedioDAO();
    private static final GrupoDAO grupoDAO = new GrupoDAO();
    private static final CampoFormativoDAO campoFormativoDAO = new CampoFormativoDAO();
    private static final AlumnoGrupoDAO alumnoGrupoDAO = new AlumnoGrupoDAO();
    private static final AlumnoDAO alumnoDAO = new AlumnoDAO();

    public static void registrarRutas(Javalin app) {

        // Da los promedios YA CALCULADOS del grupo del Docente logueado, para un periodo especifico
        // (1, 2 o 3). Un registro por alumno x Campo Formativo. Sirve para las 3 pantallas de Calificaciones.
        app.get("/promedio/mi-grupo", ctx -> {
            try {
                Integer idUsuario = ctx.sessionAttribute("idUsuario");
                if (idUsuario == null) {
                    ctx.status(401).result("No autorizado. Debes iniciar sesion.");
                    return;
                }

                Integer idGrupo = SeguridadUtil.obtenerGrupoDelDocente(ctx);
                if (idGrupo == null) {
                    ctx.status(403).result("Este endpoint es solo para Docentes con grupo asignado");
                    return;
                }

                String periodoTexto = ctx.queryParam("periodo");
                if (periodoTexto == null || periodoTexto.isBlank()) {
                    periodoTexto = "1";
                }

                ctx.json(obtenerPromediosDeGrupo(idGrupo, periodoTexto));

            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        // Igual que /promedio/mi-grupo, pero para que el Director consulte CUALQUIER grupo
        // especificando el idGrupo (solo lectura).
        app.get("/promedio/grupo", ctx -> {
            try {
                Integer idUsuario = ctx.sessionAttribute("idUsuario");
                String rol = ctx.sessionAttribute("rol");
                if (idUsuario == null) {
                    ctx.status(401).result("No autorizado. Debes iniciar sesion.");
                    return;
                }
                if (!"Director".equals(rol)) {
                    ctx.status(403).result("Solo el Director puede consultar el promedio de cualquier grupo");
                    return;
                }

                String idGrupoParam = ctx.queryParam("idGrupo");
                if (idGrupoParam == null || idGrupoParam.isBlank()) {
                    ctx.status(400).result("Falta el parametro idGrupo");
                    return;
                }
                int idGrupo = Integer.parseInt(idGrupoParam);

                String periodoTexto = ctx.queryParam("periodo");
                if (periodoTexto == null || periodoTexto.isBlank()) {
                    periodoTexto = "1";
                }

                ctx.json(obtenerPromediosDeGrupo(idGrupo, periodoTexto));

            } catch (NumberFormatException e) {
                ctx.status(400).result("idGrupo invalido");
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

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

                BigDecimal promedioFinal = calcularYGuardarPromedio(idUsuario, matricula, idCampoFormativo, idPeriodo, grado);
                Map<String, Object> resultado = new HashMap<>();
                resultado.put("matricula", matricula);
                resultado.put("idCampoFormativo", idCampoFormativo);
                resultado.put("idPeriodo", idPeriodo);
                resultado.put("promedioFinal", promedioFinal);
                ctx.json(resultado);

            } catch (NumberFormatException e) {
                ctx.status(400).result("Parametros invalidos. Se requiere: matricula, idCampoFormativo, idPeriodo, grado (todos numericos).");
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });
    }

    // Arma la lista de promedios de un grupo para un periodo dado. Itera desde los Campos Formativos
    // del GRADO (que siempre existen), no desde las filas de PERIODO (que pueden no existir todavia
    // si ese periodo nunca se ha abierto) -- asi los alumnos siempre aparecen, aunque el promedio
    // de ese periodo aun no se haya calculado (sale null / "-").
    private static List<Map<String, Object>> obtenerPromediosDeGrupo(int idGrupo, String periodoTexto) throws SQLException {
        Grupo grupo = grupoDAO.obtenerPorId(idGrupo);
        if (grupo == null) {
            throw new SQLException("No se encontro el grupo " + idGrupo);
        }

        List<CampoFormativo> campos = campoFormativoDAO.listarPorGrado(grupo.getGrado());
        List<Periodo> periodosDelTexto = periodoDAO.listarPorGrupoYPeriodo(idGrupo, periodoTexto);
        List<AlumnoGrupo> alumnosDelGrupo = alumnoGrupoDAO.listarPorGrupo(idGrupo);

        List<Map<String, Object>> resultado = new ArrayList<>();

        for (AlumnoGrupo ag : alumnosDelGrupo) {
            Alumno alumno = alumnoDAO.obtenerPorMatricula(ag.getMatricula());
            String nombreAlumno = alumno != null
                ? alumno.getNombre() + " " + alumno.getApellidoPaterno() + " " + alumno.getApellidoMaterno()
                : ("Matricula " + ag.getMatricula());

            for (CampoFormativo campo : campos) {
                Periodo periodoDeMateria = periodosDelTexto.stream()
                    .filter(p -> p.getIdCampoFormativo() == campo.getIdCampoFormativo())
                    .findFirst().orElse(null);

                BigDecimal promedioFinal = null;
                if (periodoDeMateria != null) {
                    Promedio prom = promedioDAO.obtenerPorMatriculaCampoPeriodo(
                        ag.getMatricula(), campo.getIdCampoFormativo(), periodoDeMateria.getIdPeriodo());
                    promedioFinal = prom != null ? prom.getPromedioFinal() : null;
                }

                Map<String, Object> item = new HashMap<>();
                item.put("matricula", ag.getMatricula());
                item.put("nombreAlumno", nombreAlumno);
                item.put("idCampoFormativo", campo.getIdCampoFormativo());
                item.put("nombreCampoFormativo", campo.getNombre());
                item.put("promedioFinal", promedioFinal);
                resultado.add(item);
            }
        }

        return resultado;
    }

    // Calcula el promedio Y lo guarda (upsert). Publico y estatico para que CierrePeriodoController lo reutilice.
    public static BigDecimal calcularYGuardarPromedio(int idUsuario, int matricula, int idCampoFormativo, int idPeriodo, int grado) throws SQLException {
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

        return promedioFinal;
    }

    public static Map<String, Object> calcularPromedio(int idUsuario, int matricula, int idCampoFormativo, int idPeriodo, int grado) throws SQLException {

        // --- Resolver el periodo ANCLA (para Asistencia/Disciplina) ---
        // idPeriodo llega como el especifico de ESTA materia; necesitamos el idGrupo y el texto
        // de periodo para encontrar el ancla real (menor idCampoFormativo) del grupo+periodo.
        Periodo periodoMateria = periodoDAO.obtenerPorId(idPeriodo);
        if (periodoMateria == null) {
            throw new SQLException("El periodo especificado (" + idPeriodo + ") no existe.");
        }
        Integer idPeriodoAncla = periodoDAO.obtenerIdPeriodoAncla(periodoMateria.getIdGrupo(), periodoMateria.getPeriodo());
        if (idPeriodoAncla == null) {
            throw new SQLException("No se encontro el periodo ancla para el grupo " + periodoMateria.getIdGrupo() + " y periodo " + periodoMateria.getPeriodo());
        }

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

        // --- 1. TAREA (usa idPeriodo especifico de la materia) ---
        // "Entrego" cuenta como entrega completa (peso 1.0). "Incompleta" cuenta como
        // media entrega (peso 0.5, decision confirmada con el cliente). "No entrego" no suma nada.
        List<RegistroTarea> tareas = registroTareaDAO.listarPorAlumnoCampoPeriodo(matricula, idCampoFormativo, idPeriodo);
        double resultadoTarea = 0;
        if (!tareas.isEmpty()) {
            double sumaPonderada = tareas.stream().mapToDouble(t -> {
                if ("Entrego".equals(t.getEstatus())) return 1.0;
                if ("Incompleta".equals(t.getEstatus())) return 0.5;
                return 0.0; // "No entrego"
            }).sum();
            double proporcion = sumaPonderada / tareas.size();
            resultadoTarea = proporcion * 10 * (pctTarea / 100.0);
        }

        // --- 2. EXAMEN (usa idPeriodo especifico de la materia) ---
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

        // --- 3. PARTICIPACION (usa idPeriodo especifico de la materia) ---
        List<RegistroParticipacion> participaciones = registroParticipacionDAO.listarPorAlumnoCampoPeriodo(matricula, idCampoFormativo, idPeriodo);
        double resultadoParticipacion = 0;
        if (!participaciones.isEmpty()) {
            double sumaPuntuacionesReales = participaciones.stream()
                .mapToDouble(p -> (p.getPuntuacion() - 5.0) * 2.0)
                .sum();
            double promedioPuntuacionReal = sumaPuntuacionesReales / participaciones.size();
            resultadoParticipacion = promedioPuntuacionReal * (pctParticipacion / 100.0);
        }

        // --- 4. ASISTENCIA (usa el idPeriodo ANCLA, NO el de la materia) ---
        List<RegistroAsistencia> asistencias = registroAsistenciaDAO.listarPorAlumnoPeriodo(matricula, idPeriodoAncla);
        double resultadoAsistencia = 0;
        if (!asistencias.isEmpty()) {
            long diasAsistio = asistencias.stream().filter(a -> "Asistencia".equals(a.getEstado())).count();
            double proporcion = (double) diasAsistio / asistencias.size();
            resultadoAsistencia = proporcion * 10 * (pctAsistencia / 100.0);
        }

        // --- 5. DISCIPLINA (usa el idPeriodo ANCLA, NO el de la materia) ---
        int sumaPuntosMenos = registroDisciplinaDAO.sumarPuntosMenosPorAlumnoPeriodo(matricula, idPeriodoAncla);
        double calificacionDisciplina = Math.max(0, 10 - sumaPuntosMenos);
        double resultadoDisciplina = calificacionDisciplina * (pctDisciplina / 100.0);

        // --- SUMA FINAL, con clamping a [5, 10] (regla de negocio del cliente + limite fisico del CHECK de la BD) ---
        double sumaFinal = resultadoTarea + resultadoExamen + resultadoParticipacion + resultadoAsistencia + resultadoDisciplina;
        double promedioFinalDouble = Math.min(10.0, Math.max(5.0, sumaFinal));

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