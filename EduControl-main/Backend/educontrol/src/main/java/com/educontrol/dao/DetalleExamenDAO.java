package com.educontrol.dao;

import com.educontrol.Main;
import com.educontrol.modelos.DetalleExamen;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DetalleExamenDAO {

    public List<DetalleExamen> listarTodos() throws SQLException {
        List<DetalleExamen> lista = new ArrayList<>();
        String sql = "SELECT * FROM detalle_examen";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public DetalleExamen obtenerPorId(int idDetalleExamen) throws SQLException {
        String sql = "SELECT * FROM detalle_examen WHERE idDetalle_examen = ?";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idDetalleExamen);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapear(rs);
            }
        }
        return null;
    }

    public void crear(DetalleExamen detalle) throws SQLException {
        String sql = "INSERT INTO detalle_examen (idRegistroExamen, TotalPreguntas, Aciertos) VALUES (?, ?, ?)";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, detalle.getIdRegistroExamen());
            stmt.setInt(2, detalle.getTotalPreguntas());
            stmt.setFloat(3, detalle.getAciertos());

            stmt.executeUpdate();
        }
    }

    public void actualizar(DetalleExamen detalle) throws SQLException {
        String sql = "UPDATE detalle_examen SET idRegistroExamen = ?, TotalPreguntas = ?, Aciertos = ? WHERE idDetalle_examen = ?";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, detalle.getIdRegistroExamen());
            stmt.setInt(2, detalle.getTotalPreguntas());
            stmt.setFloat(3, detalle.getAciertos());
            stmt.setInt(4, detalle.getIdDetalleExamen());

            stmt.executeUpdate();
        }
    }

    public void eliminar(int idDetalleExamen) throws SQLException {
        String sql = "DELETE FROM detalle_examen WHERE idDetalle_examen = ?";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idDetalleExamen);
            stmt.executeUpdate();
        }
    }

    private DetalleExamen mapear(ResultSet rs) throws SQLException {
        return new DetalleExamen(
            rs.getInt("idDetalle_examen"),
            rs.getInt("idRegistroExamen"),
            rs.getInt("TotalPreguntas"),
            rs.getFloat("Aciertos")
        );
    }
}