package com.educontrol.dao;

import com.educontrol.Main;
import com.educontrol.modelos.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.mindrot.jbcrypt.BCrypt;

public class UsuarioDAO {

    public List<Usuario> listarTodos() throws SQLException {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuario";

        try (Connection conn = Main.conectar();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearUsuario(rs));
            }
        }
        return lista;
    }

    public Usuario obtenerPorId(int idUsuario) throws SQLException {
        String sql = "SELECT * FROM usuario WHERE idUsuario = ?";

        try (Connection conn = Main.conectar();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idUsuario);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapearUsuario(rs);
            }
        }
        return null;
    }


    public void crear(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuario (NombreUsuario, Nombre, ApellidoPaterno, ApellidoMaterno, Contrasena, Rol) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

        String contrasenaHasheada = BCrypt.hashpw(usuario.getContrasena(), BCrypt.gensalt());

        try (Connection conn = Main.conectar();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario.getNombreUsuario());
            stmt.setString(2, usuario.getNombre());   
            stmt.setString(3, usuario.getApellidoPaterno());
            stmt.setString(4, usuario.getApellidoMaterno());
            stmt.setString(5, contrasenaHasheada);
            stmt.setString(6, usuario.getRol());

            stmt.executeUpdate();
        }      
}

    public void actualizar(Usuario usuario) throws SQLException {
        String sql = "UPDATE usuario SET NombreUsuario = ?, Nombre = ?, ApellidoPaterno = ?, " +
                    "ApellidoMaterno = ?, Contrasena = ?, Rol = ? WHERE idUsuario = ?";

        try (Connection conn = Main.conectar();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario.getNombreUsuario());
            stmt.setString(2, usuario.getNombre());
            stmt.setString(3, usuario.getApellidoPaterno());
            stmt.setString(4, usuario.getApellidoMaterno());
            stmt.setString(5, usuario.getContrasena());
            stmt.setString(6, usuario.getRol());
            stmt.setInt(7, usuario.getIdUsuario());

            stmt.executeUpdate();
        }
    }

    public void eliminar(int idUsuario) throws SQLException {
        String sql = "DELETE FROM usuario WHERE idUsuario = ?";

        try (Connection conn = Main.conectar();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idUsuario);
            stmt.executeUpdate();
        }
    }

    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        return new Usuario(
            rs.getInt("idUsuario"),
            rs.getString("NombreUsuario"),
            rs.getString("Nombre"),
            rs.getString("ApellidoPaterno"),
            rs.getString("ApellidoMaterno"),
            rs.getString("Contrasena"),
            rs.getString("Rol")
        );
    }

    public Usuario buscarPorNombreUsuario(String nombreUsuario) throws SQLException {
    String sql = "SELECT * FROM usuario WHERE NombreUsuario = ?";

    try (Connection conn = Main.conectar();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, nombreUsuario);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            return mapearUsuario(rs);
        }
    }
    return null;
}
}