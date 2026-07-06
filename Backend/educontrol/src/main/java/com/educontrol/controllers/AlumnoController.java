package com.educontrol.controllers;

import com.educontrol.dao.AlumnoDAO;
import com.educontrol.modelos.Alumno;
import io.javalin.Javalin;

import java.sql.SQLException;

public class AlumnoController {

    private static final AlumnoDAO dao = new AlumnoDAO();

    public static void registrarRutas(Javalin app) {

        // Listar todos los alumnos
        app.get("/alumnos", ctx -> {
            try {
                ctx.json(dao.listarTodos());
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        // Obtener un alumno por matrícula
        app.get("/alumnos/{matricula}", ctx -> {
            try {
                int matricula = Integer.parseInt(ctx.pathParam("matricula"));
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

        // Crear un alumno nuevo
        app.post("/alumnos", ctx -> {
            try {
                Alumno alumno = ctx.bodyAsClass(Alumno.class);
                dao.crear(alumno);
                ctx.status(201).result("Alumno creado correctamente");
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        // Actualizar un alumno existente
        app.put("/alumnos/{matricula}", ctx -> {
            try {
                int matricula = Integer.parseInt(ctx.pathParam("matricula"));
                Alumno alumno = ctx.bodyAsClass(Alumno.class);
                alumno.setMatricula(matricula);
                dao.actualizar(alumno);
                ctx.result("Alumno actualizado correctamente");
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        // Eliminar un alumno
        app.delete("/alumnos/{matricula}", ctx -> {
            try {
                int matricula = Integer.parseInt(ctx.pathParam("matricula"));
                dao.eliminar(matricula);
                ctx.result("Alumno eliminado correctamente");
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });
    }
}