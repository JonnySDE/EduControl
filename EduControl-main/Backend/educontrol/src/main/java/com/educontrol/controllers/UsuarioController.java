package com.educontrol.controllers;

import com.educontrol.dao.UsuarioDAO;
import com.educontrol.modelos.Usuario;
import io.javalin.Javalin;

import java.sql.SQLException;
import java.util.Map;

public class UsuarioController {

    private static final UsuarioDAO dao = new UsuarioDAO();

    public static void registrarRutas(Javalin app) {

        app.get("/usuarios", ctx -> {
            try {
                ctx.json(dao.listarTodos());
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        app.get("/usuarios/{id}", ctx -> {
            try {
                int id = Integer.parseInt(ctx.pathParam("id"));
                Usuario usuario = dao.obtenerPorId(id);

                if (usuario != null) {
                    usuario.setContrasena(null); // nunca mandamos el hash al front
                    ctx.json(usuario);
                } else {
                    ctx.status(404).result("Usuario no encontrado");
                }
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        app.post("/usuarios", ctx -> {
            try {
                Usuario usuario = ctx.bodyAsClass(Usuario.class);
                dao.crear(usuario);
                ctx.status(201).result("Usuario creado correctamente");
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        app.put("/usuarios/{id}", ctx -> {
            try {
                int id = Integer.parseInt(ctx.pathParam("id"));
                Usuario usuario = ctx.bodyAsClass(Usuario.class);
                usuario.setIdUsuario(id);
                dao.actualizar(usuario);
                ctx.result("Usuario actualizado correctamente");
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        app.delete("/usuarios/{id}", ctx -> {
            try {
                int id = Integer.parseInt(ctx.pathParam("id"));
                dao.eliminar(id);
                ctx.result("Usuario eliminado correctamente");
            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        // --- NUEVOS ENDPOINTS PARA EL MÓDULO CUENTA ---

        // Cambiar nombre de usuario (solo el propio usuario logueado)
        app.put("/usuarios/{id}/nombre-usuario", ctx -> {
            try {
                int id = Integer.parseInt(ctx.pathParam("id"));

                Integer idSesion = ctx.sessionAttribute("idUsuario");
                if (idSesion == null || idSesion != id) {
                    ctx.status(403).result("No puedes editar la cuenta de otro usuario");
                    return;
                }

                Map<String, String> body = ctx.bodyAsClass(Map.class);
                String nuevoNombreUsuario = body.get("nombreUsuario");

                if (nuevoNombreUsuario == null || nuevoNombreUsuario.trim().isEmpty()) {
                    ctx.status(400).result("El nombre de usuario no puede estar vacío");
                    return;
                }
                nuevoNombreUsuario = nuevoNombreUsuario.trim();

                // Verificamos que no exista ya otro usuario con ese nombre
                Usuario existente = dao.buscarPorNombreUsuario(nuevoNombreUsuario);
                if (existente != null && existente.getIdUsuario() != id) {
                    ctx.status(409).result("Ese nombre de usuario ya está en uso");
                    return;
                }

                dao.actualizarNombreUsuario(id, nuevoNombreUsuario);

                // Actualizamos también el dato en la sesión activa
                ctx.sessionAttribute("nombreUsuario", nuevoNombreUsuario);

                ctx.status(200).result("Nombre de usuario actualizado correctamente");

            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        // Cambiar contraseña (solo el propio usuario logueado, valida la contraseña actual)
        app.put("/usuarios/{id}/contrasena", ctx -> {
            try {
                int id = Integer.parseInt(ctx.pathParam("id"));

                Integer idSesion = ctx.sessionAttribute("idUsuario");
                if (idSesion == null || idSesion != id) {
                    ctx.status(403).result("No puedes editar la cuenta de otro usuario");
                    return;
                }

                Map<String, String> body = ctx.bodyAsClass(Map.class);
                String contrasenaActual = body.get("contrasenaActual");
                String contrasenaNueva = body.get("contrasenaNueva");

                if (contrasenaActual == null || contrasenaNueva == null
                        || contrasenaActual.isEmpty() || contrasenaNueva.isEmpty()) {
                    ctx.status(400).result("Debes llenar todos los campos");
                    return;
                }

                Usuario usuario = dao.obtenerPorId(id);
                if (usuario == null) {
                    ctx.status(404).result("Usuario no encontrado");
                    return;
                }

                boolean coincide = contrasenaActual.equals(usuario.getContrasena());
                if (!coincide) {
                    ctx.status(401).result("La contraseña actual no es correcta");
                    return;
                }

                dao.actualizarContrasena(id, contrasenaNueva);

                ctx.status(200).result("Contraseña actualizada correctamente");

            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

    }
}