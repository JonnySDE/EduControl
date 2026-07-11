package com.educontrol.controllers;

import com.educontrol.dao.AlumnoDAO;
import com.educontrol.dao.AlumnoGrupoDAO;
import com.educontrol.modelos.Alumno;
import com.educontrol.modelos.AlumnoGrupo;
import io.javalin.Javalin;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class AlumnoController {

    private static final AlumnoDAO dao = new AlumnoDAO();
    private static final AlumnoGrupoDAO alumnoGrupoDAO = new AlumnoGrupoDAO();

    public static void registrarRutas(Javalin app) {

        app.get("/alumnos", ctx -> {
            try {
                Integer idGrupoDocente = SeguridadUtil.obtenerGrupoDelDocente(ctx);

                if (idGrupoDocente == null) {
                    ctx.json(dao.listarTodos());
                } else {
                    List<AlumnoGrupo> asignaciones = alumnoGrupoDAO.listarPorGrupo(idGrupoDocente);
                    List<Alumno> alumnosFiltrados = asignaciones.stream()
                        .map(ag -> {
                            try {
                                return dao.obtenerPorMatricula(ag.getMatricula());
                            } catch (SQLException e) {
                                return null;
                            }
                        })
                        .filter(a -> a != null)
                        .collect(Collectors.toList());

                    ctx.json(alumnosFiltrados);
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
                if (alumno != null) {
                    ctx.json(alumno);
                } else {
                    ctx.status(404).result("Alumno no encontrado");
                }
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        app.post("/alumnos", ctx -> {
            try {
                Alumno alumno = ctx.bodyAsClass(Alumno.class);
                dao.crear(alumno);
                ctx.status(201).result("Alumno creado correctamente");
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        app.put("/alumnos/{matricula}", ctx -> {
            try {
                int matricula = Integer.parseInt(ctx.pathParam("matricula"));

                if (!SeguridadUtil.alumnoPerteneceAGrupoDocente(ctx, matricula)) {
                    ctx.status(403).result("No tienes permiso para editar este alumno");
                    return;
                }

                Alumno alumno = ctx.bodyAsClass(Alumno.class);
                alumno.setMatricula(matricula);
                dao.actualizar(alumno);
                ctx.result("Alumno actualizado correctamente");
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
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