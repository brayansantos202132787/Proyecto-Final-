/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.connectwork.dao;

import com.connectwork.config.DBConnection;
import com.connectwork.model.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author braya
 */
public class UsuarioDAO {
    public Usuario findByNombreUsuario(String nombreUsuario) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE nombre_usuario = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nombreUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public Usuario findById(int id) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE id_usuario = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public List<Usuario> findByTipo(String tipo) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE tipo_usuario = ?";
        List<Usuario> lista = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, tipo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapRow(rs));
            }
        }
        return lista;
    }

    public int insert(Usuario u) throws SQLException {
        String sql = """
            INSERT INTO usuarios
              (nombre_completo, nombre_usuario, password_hash,
               correo, telefono, cui, fecha_nacimiento, tipo_usuario, estado_cuenta)
            VALUES (?,?,?,?,?,?,?,?,1)
            """;
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getNombreCompleto());
            ps.setString(2, u.getNombreUsuario());
            ps.setString(3, u.getPasswordHash());
            ps.setString(4, u.getCorreo());
            ps.setString(5, u.getTelefono());
            ps.setString(6, u.getCui());
            ps.setDate  (7, u.getFechaNacimiento() != null
                            ? new java.sql.Date(u.getFechaNacimiento().getTime()) : null);
            ps.setString(8, u.getTipoUsuario());
            ps.executeUpdate();
            try (ResultSet gen = ps.getGeneratedKeys()) {
                if (gen.next()) return gen.getInt(1);
            }
        }
        return -1;
    }

    public boolean updateEstado(int idUsuario, boolean estado) throws SQLException {
        String sql = "UPDATE usuarios SET estado_cuenta = ? WHERE id_usuario = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setBoolean(1, estado);
            ps.setInt    (2, idUsuario);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean existeNombreUsuario(String nombreUsuario) throws SQLException {
        String sql = "SELECT 1 FROM usuarios WHERE nombre_usuario = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nombreUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean existeCorreo(String correo) throws SQLException {
        String sql = "SELECT 1 FROM usuarios WHERE correo = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, correo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private Usuario mapRow(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setIdUsuario      (rs.getInt    ("id_usuario"));
        u.setNombreCompleto (rs.getString ("nombre_completo"));
        u.setNombreUsuario  (rs.getString ("nombre_usuario"));
        u.setPasswordHash   (rs.getString ("password_hash"));
        u.setCorreo         (rs.getString ("correo"));
        u.setTelefono       (rs.getString ("telefono"));
        u.setCui            (rs.getString ("cui"));
        u.setFechaNacimiento(rs.getDate   ("fecha_nacimiento"));
        u.setTipoUsuario    (rs.getString ("tipo_usuario"));
        u.setEstadoCuenta   (rs.getBoolean("estado_cuenta"));
        return u;
    }
}
