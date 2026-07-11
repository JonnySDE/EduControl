package com.educontrol.controllers;

import com.educontrol.dao.UsuarioDAO;
import com.educontrol.modelos.LoginRequest;
import com.educontrol.modelos.Usuario;
import io.javalin.Javalin;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

public class LoginController {

    private static final UsuarioDAO dao = new UsuarioDAO();

    public static void registrarRutas(Javalin app) {

        app.post("/login", ctx -> {
            try {
                LoginRequest login = ctx.bodyAsClass(LoginRequest.class);
                Usuario usuario = dao.buscarPorNombreUsuario(login.getNombreUsuario());

                if (usuario == null) {
                    ctx.status(401).result("Usuario o contraseña incorrectos");
                    return;
                }

                boolean passwordValido = BCrypt.checkpw(login.getContrasena(), usuario.getContrasena());

                if (!passwordValido) {
                    ctx.status(401).result("Usuario o contraseña incorrectos");
                    return;
                }

                ctx.sessionAttribute("idUsuario", usuario.getIdUsuario());
                ctx.sessionAttribute("rol", usuario.getRol());
                ctx.sessionAttribute("nombreUsuario", usuario.getNombreUsuario());

                usuario.setContrasena(null);
                ctx.status(200).json(usuario);

            } catch (SQLException e) {
                ctx.status(500).result("Error: " + e.getMessage());
            }
        });

        app.post("/logout", ctx -> {
            ctx.req().getSession().invalidate();
            ctx.status(200).result("Sesión cerrada correctamente");
        });

        app.get("/session", ctx -> {
            Integer idUsuario = ctx.sessionAttribute("idUsuario");

            if (idUsuario == null) {
                ctx.status(401).result("No hay sesión activa");
                return;
            }

            String rol = ctx.sessionAttribute("rol");
            String nombreUsuario = ctx.sessionAttribute("nombreUsuario");

            ctx.json(new java.util.HashMap<String, Object>() {{
                put("idUsuario", idUsuario);
                put("rol", rol);
                put("nombreUsuario", nombreUsuario);
            }});
        });
    }
}