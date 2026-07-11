package com.educontrol.dao;

import com.educontrol.Main;
import com.educontrol.modelos.Alumno;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AlumnoDAO {

    public List<Alumno> listarTodos() throws SQLException {
        List<Alumno> lista = new ArrayList<>();
        String sql = "SELECT * FROM alumno";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearAlumno(rs));
            }
        }
        return lista;
    }

    public Alumno obtenerPorMatricula(int matricula) throws SQLException {
        String sql = "SELECT * FROM alumno WHERE Matricula = ?";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, matricula);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapearAlumno(rs);
            }
        }
        return null;
    }

    public void crear(Alumno alumno) throws SQLException {
        String sql = "INSERT INTO alumno (Matricula, Nombre, ApellidoPaterno, ApellidoMaterno) VALUES (?, ?, ?, ?)";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, alumno.getMatricula());
            stmt.setString(2, alumno.getNombre());
            stmt.setString(3, alumno.getApellidoPaterno());
            stmt.setString(4, alumno.getApellidoMaterno());

            stmt.executeUpdate();
        }
    }

    public void actualizar(Alumno alumno) throws SQLException {
        String sql = "UPDATE alumno SET Nombre = ?, ApellidoPaterno = ?, ApellidoMaterno = ? WHERE Matricula = ?";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, alumno.getNombre());
            stmt.setString(2, alumno.getApellidoPaterno());
            stmt.setString(3, alumno.getApellidoMaterno());
            stmt.setInt(4, alumno.getMatricula());

            stmt.executeUpdate();
        }
    }

    public void eliminar(int matricula) throws SQLException {
        String sql = "DELETE FROM alumno WHERE Matricula = ?";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, matricula);
            stmt.executeUpdate();
        }
    }

    private Alumno mapearAlumno(ResultSet rs) throws SQLException {
        return new Alumno(
            rs.getInt("Matricula"),
            rs.getString("Nombre"),
            rs.getString("ApellidoPaterno"),
            rs.getString("ApellidoMaterno")
        );
    }
}