package com.educontrol;

import io.javalin.Javalin;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.educontrol.controllers.AlumnoController;

public class Main {

    // Datos de conexión a MySQL - AJUSTA ESTOS VALORES
    private static final String DB_URL = "jdbc:mysql://localhost:3306/educontrol";
    private static final String DB_USER = "EduControlUser";
    private static final String DB_PASSWORD = "253EduControlMJC";

    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    public static void main(String[] args) {

        Javalin app = Javalin.create(config -> {
            config.bundledPlugins.enableCors(cors -> {
                cors.addRule(it -> it.anyHost()); // para que el frontend pueda pegarle sin bronca de CORS
            });
        }).start(7000);
        AlumnoController.registrarRutas(app);

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