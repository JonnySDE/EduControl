package com.educontrol.controllers;

import com.educontrol.dao.AlumnoDAO;
import com.educontrol.dao.AlumnoGrupoDAO;
import com.educontrol.modelos.Alumno;
import com.educontrol.modelos.AlumnoGrupo;
import io.javalin.Javalin;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AlumnoController {

    private static final AlumnoDAO dao = new AlumnoDAO();
    private static final AlumnoGrupoDAO alumnoGrupoDAO = new AlumnoGrupoDAO();

    // Combina los datos de Alumno + AlumnoGrupo en un solo objeto para el frontend
    private static Map<String, Object> combinar(Alumno alumno, AlumnoGrupo ag) {
        Map<String, Object> resultado = new LinkedHashMap<>();
        resultado.put("matricula", alumno.getMatricula());
        resultado.put("nombre", alumno.getNombre());
        resultado.put("apellidoPaterno", alumno.getApellidoPaterno());
        resultado.put("apellidoMaterno", alumno.getApellidoMaterno());
        resultado.put("idGrupo", ag != null ? ag.getIdGrupo() : null);
        resultado.put("numeroLista", ag != null ? ag.getNumeroLista() : null);
        return resultado;
    }

    public static void registrarRutas(Javalin app) {

        app.get("/alumnos", ctx -> {
            try {
                Integer idGrupoDocente = SeguridadUtil.obtenerGrupoDelDocente(ctx);

                if (idGrupoDocente == null) {
                    // Director: todos los alumnos, con su grupo real
                    List<Alumno> todos = dao.listarTodos();
                    List<Map<String, Object>> resultado = todos.stream()
                        .map(a -> {
                            try {
                                AlumnoGrupo ag = alumnoGrupoDAO.obtenerPorMatricula(a.getMatricula());
                                return combinar(a, ag);
                            } catch (SQLException e) {
                                return combinar(a, null);
                            }
                        })
                        .collect(Collectors.toList());
                    ctx.json(resultado);
                } else {
                    // Docente: solo su grupo
                    List<AlumnoGrupo> asignaciones = alumnoGrupoDAO.listarPorGrupo(idGrupoDocente);
                    List<Map<String, Object>> resultado = asignaciones.stream()
                        .map(ag -> {
                            try {
                                Alumno a = dao.obtenerPorMatricula(ag.getMatricula());
                                return a != null ? combinar(a, ag) : null;
                            } catch (SQLException e) {
                                return null;
                            }
                        })
                        .filter(m -> m != null)
                        .collect(Collectors.toList());
                    ctx.json(resultado);
                }
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        app.get("/alumnos/{matricula}", ctx -> {
            try {
                int matricula = Integer.parseInt(ctx.pathParam("matricula"));

                if (!SeguridadUtil.alumnoPerteneceAGrupoDocente(ctx, matricula)) {
                    ctx.status(403).result("No tienes permiso para ver este alumno");
                    return;
                }

                Alumno alumno = dao.obtenerPorMatricula(matricula);
                if (alumno == null) {
                    ctx.status(404).result("Alumno no encontrado");
                    return;
                }

                AlumnoGrupo ag = alumnoGrupoDAO.obtenerPorMatricula(matricula);
                ctx.json(combinar(alumno, ag));

            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        // Crear alumno Y asignarlo a un grupo en la misma operación
        app.post("/alumnos", ctx -> {
            try {
                Map<String, Object> body = ctx.bodyAsClass(Map.class);

                int matricula = ((Number) body.get("matricula")).intValue();
                String nombre = (String) body.get("nombre");
                String apellidoPaterno = (String) body.get("apellidoPaterno");
                String apellidoMaterno = (String) body.get("apellidoMaterno");
                int numeroLista = ((Number) body.get("numeroLista")).intValue();


                Integer idGrupoDocente = SeguridadUtil.obtenerGrupoDelDocente(ctx);
                int idGrupoFinal;

                if (idGrupoDocente != null) {
                    idGrupoFinal = idGrupoDocente;
                } else {
                    if (body.get("idGrupo") == null) {
                        ctx.status(400).result("Debes especificar el grupo (idGrupo)");
                        return;
                    }
                    idGrupoFinal = ((Number) body.get("idGrupo")).intValue();
                }

                // Validacion 1: la matricula es unica en TODA la escuela, no puede repetirse
                if (dao.obtenerPorMatricula(matricula) != null) {
                    ctx.status(409).result("Ya existe un alumno con la matrícula " + matricula + ". La matrícula debe ser única.");
                    return;
                }

                // Validacion 2: el numero de lista debe ser unico DENTRO del mismo grupo
                AlumnoGrupo yaOcupado = alumnoGrupoDAO.obtenerPorGrupoYNumeroLista(idGrupoFinal, numeroLista);
                if (yaOcupado != null) {
                    ctx.status(409).result("Ya existe otro alumno con el número de lista " + numeroLista + " en este grupo. Elige otro número.");
                    return;
                }

                Alumno alumno = new Alumno(matricula, nombre, apellidoPaterno, apellidoMaterno);
                dao.crear(alumno);

                AlumnoGrupo ag = new AlumnoGrupo(0, matricula, numeroLista, idGrupoFinal);
                alumnoGrupoDAO.crear(ag);

                ctx.status(201).result("Alumno creado y asignado correctamente");

            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            } catch (Exception e) {
                ctx.status(400).result("Datos inválidos: " + e.getMessage());
            }
        });

        app.put("/alumnos/{matricula}", ctx -> {
            try {
                int matricula = Integer.parseInt(ctx.pathParam("matricula"));

                if (!SeguridadUtil.alumnoPerteneceAGrupoDocente(ctx, matricula)) {
                    ctx.status(403).result("No tienes permiso para editar este alumno");
                    return;
                }

                Map<String, Object> body = ctx.bodyAsClass(Map.class);

                String nombre = (String) body.get("nombre");
                String apellidoPaterno = (String) body.get("apellidoPaterno");
                String apellidoMaterno = (String) body.get("apellidoMaterno");

                Alumno alumno = new Alumno(matricula, nombre, apellidoPaterno, apellidoMaterno);
                dao.actualizar(alumno);

                // Manejo del grupo/numeroLista (si vienen en el body)
                if (body.get("numeroLista") != null) {
                    int numeroLista = ((Number) body.get("numeroLista")).intValue();

                    Integer idGrupoDocente = SeguridadUtil.obtenerGrupoDelDocente(ctx);
                    int idGrupoFinal;

                    if (idGrupoDocente != null) {
                        // Docente: NUNCA puede cambiar el grupo de un alumno, se fuerza el suyo
                        idGrupoFinal = idGrupoDocente;
                    } else {
                        // Director: puede mover al alumno de grupo libremente
                        idGrupoFinal = body.get("idGrupo") != null
                            ? ((Number) body.get("idGrupo")).intValue()
                            : alumnoGrupoDAO.obtenerPorMatricula(matricula).getIdGrupo();
                    }

                    // Validacion: el numero de lista debe ser unico en el grupo destino
                    AlumnoGrupo yaOcupado = alumnoGrupoDAO.obtenerPorGrupoYNumeroLista(idGrupoFinal, numeroLista);
                    if (yaOcupado != null && yaOcupado.getMatricula() != matricula) {
                        ctx.status(409).result("Ya existe otro alumno con el número de lista " + numeroLista + " en ese grupo. Elige otro número.");
                        return;
                    }

                    alumnoGrupoDAO.cambiarGrupo(matricula, idGrupoFinal, numeroLista);
                }

                ctx.result("Alumno actualizado correctamente");

            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            } catch (Exception e) {
                ctx.status(400).result("Datos inválidos: " + e.getMessage());
            }
        });

        app.delete("/alumnos/{matricula}", ctx -> {
            try {
                int matricula = Integer.parseInt(ctx.pathParam("matricula"));

                if (!SeguridadUtil.alumnoPerteneceAGrupoDocente(ctx, matricula)) {
                    ctx.status(403).result("No tienes permiso para eliminar este alumno");
                    return;
                }

                dao.eliminar(matricula);
                ctx.result("Alumno eliminado correctamente");
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });
    }
}