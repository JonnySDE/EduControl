package com.educontrol;

import io.javalin.Javalin;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.educontrol.controllers.AlumnoController;
import com.educontrol.controllers.UsuarioController;
import com.educontrol.controllers.GrupoController;
import com.educontrol.controllers.AsignarGrupoController;
import com.educontrol.controllers.CampoFormativoController;
import com.educontrol.controllers.ConfigTareaController;
import com.educontrol.controllers.ConfigExamenController;
import com.educontrol.controllers.ConfigParticipacionController;
import com.educontrol.controllers.ConfigAsistenciaController;
import com.educontrol.controllers.ConfigDisciplinaController;
import com.educontrol.controllers.RegistroTareaController;
import com.educontrol.controllers.RegistroAsistenciaController;
import com.educontrol.controllers.RegistroParticipacionController;
import com.educontrol.controllers.RegistroDisciplinaController;
import com.educontrol.controllers.RegistroExamenController;
import com.educontrol.controllers.DetalleExamenController;
import com.educontrol.controllers.PeriodoController;
import com.educontrol.controllers.PromedioController;
import com.educontrol.controllers.AlumnoGrupoController;
import com.educontrol.controllers.LoginController;

import org.eclipse.jetty.http.HttpCookie;
import org.eclipse.jetty.server.session.SessionHandler;

public class Main {

    // Datos de conexión a MySQL
    private static final String DB_URL = "jdbc:mysql://localhost:3306/educontroldb";
    private static final String DB_USER = "educontrol";
    private static final String DB_PASSWORD = "educontroldatabe10";

    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    public static void main(String[] args) {

        Javalin app = Javalin.create(config -> {
            config.bundledPlugins.enableCors(cors -> {
                cors.addRule(it -> {
                    it.allowHost("http://127.0.0.1:5500", "http://localhost:5500");
                    it.allowCredentials = true;
                });
            });

            config.jetty.modifyServletContextHandler(handler -> {
                SessionHandler sessionHandler = new SessionHandler();
                sessionHandler.setHttpOnly(true);
                sessionHandler.setSameSite(HttpCookie.SameSite.NONE);
                sessionHandler.getSessionCookieConfig().setSecure(true);
                handler.setSessionHandler(sessionHandler);
            });

        }).start(7000);

        app.before(ctx -> {
            if (ctx.method().toString().equals("OPTIONS")) {
                return;
            }

            String path = ctx.path();
            if (path.equals("/login") || path.equals("/") || path.equals("/test-db")) {
                return;
            }

            Integer idUsuario = ctx.sessionAttribute("idUsuario");
            if (idUsuario == null) {
                throw new io.javalin.http.UnauthorizedResponse("No autorizado. Debes iniciar sesion.");
            }
        });

        AlumnoController.registrarRutas(app);
        UsuarioController.registrarRutas(app);
        GrupoController.registrarRutas(app);
        AsignarGrupoController.registrarRutas(app);
        CampoFormativoController.registrarRutas(app);
        ConfigTareaController.registrarRutas(app);
        ConfigExamenController.registrarRutas(app);
        ConfigParticipacionController.registrarRutas(app);
        ConfigAsistenciaController.registrarRutas(app);
        ConfigDisciplinaController.registrarRutas(app);
        RegistroTareaController.registrarRutas(app);
        RegistroAsistenciaController.registrarRutas(app);
        RegistroParticipacionController.registrarRutas(app);
        RegistroDisciplinaController.registrarRutas(app);
        RegistroExamenController.registrarRutas(app);
        DetalleExamenController.registrarRutas(app);
        PeriodoController.registrarRutas(app);
        PromedioController.registrarRutas(app);
        AlumnoGrupoController.registrarRutas(app);
        LoginController.registrarRutas(app);

        // Endpoint de prueba: verifica que la conexión a la BD funcione
        app.get("/", ctx -> ctx.result("EduControl backend corriendo... "));

        app.get("/test-db", ctx -> {
            try (Connection conn = conectar()) {
                ctx.result("Conexión exitosa a la base de datos EduControl ");
            } catch (SQLException e) {
                ctx.status(500).result("Error de conexión: " + e.getMessage());
            }
        });
        System.out.println("Servidor corriendo en http://localhost:7000");
    }
}