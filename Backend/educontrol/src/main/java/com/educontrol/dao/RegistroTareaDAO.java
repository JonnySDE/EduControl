package com.educontrol.dao;

import com.educontrol.Main;
import com.educontrol.modelos.RegistroTarea;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RegistroTareaDAO {

    public List<RegistroTarea> listarTodos() throws SQLException {
        List<RegistroTarea> lista = new ArrayList<>();
        String sql = "SELECT * FROM registro_tarea";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public RegistroTarea obtenerPorId(int idRegistroTarea) throws SQLException {
        String sql = "SELECT * FROM registro_tarea WHERE idRegistroTarea = ?";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idRegistroTarea);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapear(rs);
            }
        }
        return null;
    }

    public List<RegistroTarea> listarPorAlumnoCampoPeriodo(int matricula, int idCampoFormativo, int idPeriodo) throws SQLException {
        List<RegistroTarea> lista = new ArrayList<>();
        String sql = "SELECT * FROM registro_tarea WHERE Matricula = ? AND idCampoFormativo = ? AND idPeriodo = ?";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, matricula);
            stmt.setInt(2, idCampoFormativo);
            stmt.setInt(3, idPeriodo);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public void crear(RegistroTarea registro) throws SQLException {
        String sql = "INSERT INTO registro_tarea (Nombre, Observaciones, estatus, Matricula, idTarea, idUsuario, idCampoFormativo, idPeriodo) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, registro.getNombre());
            stmt.setString(2, registro.getObservaciones());
            stmt.setString(3, registro.getEstatus());
            stmt.setInt(4, registro.getMatricula());
            stmt.setInt(5, registro.getIdTarea());
            stmt.setInt(6, registro.getIdUsuario());
            stmt.setInt(7, registro.getIdCampoFormativo());
            stmt.setInt(8, registro.getIdPeriodo());

            stmt.executeUpdate();
        }
    }

    public void actualizar(RegistroTarea registro) throws SQLException {
        String sql = "UPDATE registro_tarea SET Nombre = ?, Observaciones = ?, estatus = ?, " +
                     "Matricula = ?, idTarea = ?, idUsuario = ?, idCampoFormativo = ?, idPeriodo = ? WHERE idRegistroTarea = ?";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, registro.getNombre());
            stmt.setString(2, registro.getObservaciones());
            stmt.setString(3, registro.getEstatus());
            stmt.setInt(4, registro.getMatricula());
            stmt.setInt(5, registro.getIdTarea());
            stmt.setInt(6, registro.getIdUsuario());
            stmt.setInt(7, registro.getIdCampoFormativo());
            stmt.setInt(8, registro.getIdPeriodo());
            stmt.setInt(9, registro.getIdRegistroTarea());

            stmt.executeUpdate();
        }
    }

    public void eliminar(int idRegistroTarea) throws SQLException {
        String sql = "DELETE FROM registro_tarea WHERE idRegistroTarea = ?";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idRegistroTarea);
            stmt.executeUpdate();
        }
    }

    private RegistroTarea mapear(ResultSet rs) throws SQLException {
        return new RegistroTarea(
            rs.getInt("idRegistroTarea"),
            rs.getString("Nombre"),
            rs.getString("Observaciones"),
            rs.getString("estatus"),
            rs.getInt("Matricula"),
            rs.getInt("idTarea"),
            rs.getInt("idUsuario"),
            rs.getInt("idCampoFormativo"),
            rs.getInt("idPeriodo")
        );
    }
}