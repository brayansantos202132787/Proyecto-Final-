/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.connectwork.dao;

import com.connectwork.config.DBConnection;
import com.connectwork.model.PerfilCliente;

import java.sql.*;

/**
 *
 * @author braya
 */
public class PerfilClienteDAO {
    public PerfilCliente findById(int idUsuario) throws SQLException {
        String sql = "SELECT * FROM perfil_cliente WHERE id_usuario = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public boolean insert(PerfilCliente p) throws SQLException {
        String sql = """
            INSERT INTO perfil_cliente (id_usuario, descripcion_empresa, sector, sitio_web)
            VALUES (?,?,?,?)
            """;
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt   (1, p.getIdUsuario());
            ps.setString(2, p.getDescripcionEmpresa());
            ps.setString(3, p.getSector());
            ps.setString(4, p.getSitioWeb());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean update(PerfilCliente p) throws SQLException {
        String sql = """
            UPDATE perfil_cliente
            SET descripcion_empresa=?, sector=?, sitio_web=?
            WHERE id_usuario=?
            """;
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, p.getDescripcionEmpresa());
            ps.setString(2, p.getSector());
            ps.setString(3, p.getSitioWeb());
            ps.setInt   (4, p.getIdUsuario());
            return ps.executeUpdate() > 0;
        }
    }
    
    
     public boolean exists(int idUsuario) throws SQLException {
        String sql = "SELECT 1 FROM perfil_cliente WHERE id_usuario = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private PerfilCliente mapRow(ResultSet rs) throws SQLException {
        PerfilCliente p = new PerfilCliente();
        p.setIdUsuario         (rs.getInt   ("id_usuario"));
        p.setDescripcionEmpresa(rs.getString("descripcion_empresa"));
        p.setSector            (rs.getString("sector"));
        p.setSitioWeb          (rs.getString("sitio_web"));
        return p;
    }
}
