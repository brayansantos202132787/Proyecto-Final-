/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.connectwork.dao;

import com.connectwork.config.DBConnection;
import com.connectwork.model.PerfilFreelancer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author braya
 */
public class PerfilFreelancerDAO {
    public PerfilFreelancer findById(int idUsuario) throws SQLException {
        String sql = "SELECT * FROM perfil_freelancer WHERE id_usuario = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    PerfilFreelancer pf = mapRow(rs);
                    pf.setHabilidades(findHabilidades(idUsuario));
                    return pf;
                }
            }
        }
        return null;
    }

    public boolean insert(PerfilFreelancer p) throws SQLException {
        String sql = """
            INSERT INTO perfil_freelancer (id_usuario, biografia, nivel_experiencia, tarifa_hora)
            VALUES (?,?,?,?)
            """;
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt       (1, p.getIdUsuario());
            ps.setString    (2, p.getBiografia());
            ps.setString    (3, p.getNivelExperiencia());
            ps.setBigDecimal(4, p.getTarifaHora());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean update(PerfilFreelancer p) throws SQLException {
        String sql = """
            UPDATE perfil_freelancer
            SET biografia=?, nivel_experiencia=?, tarifa_hora=?
            WHERE id_usuario=?
            """;
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString    (1, p.getBiografia());
            ps.setString    (2, p.getNivelExperiencia());
            ps.setBigDecimal(3, p.getTarifaHora());
            ps.setInt       (4, p.getIdUsuario());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean exists(int idUsuario) throws SQLException {
        String sql = "SELECT 1 FROM perfil_freelancer WHERE id_usuario = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    // Habilidades del freelancer
    public void insertHabilidad(int idUsuario, int idHabilidad) throws SQLException {
        String sql = "INSERT IGNORE INTO freelancer_habilidades (id_usuario, id_habilidad) VALUES (?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idUsuario); ps.setInt(2, idHabilidad);
            ps.executeUpdate();
        }
    }

    public void deleteHabilidades(int idUsuario) throws SQLException {
        String sql = "DELETE FROM freelancer_habilidades WHERE id_usuario = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idUsuario); ps.executeUpdate();
        }
    }

    public List<Integer> findHabilidades(int idUsuario) throws SQLException {
        String sql = "SELECT id_habilidad FROM freelancer_habilidades WHERE id_usuario = ?";
        List<Integer> lista = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(rs.getInt("id_habilidad"));
            }
        }
        return lista;
    }

    private PerfilFreelancer mapRow(ResultSet rs) throws SQLException {
        PerfilFreelancer p = new PerfilFreelancer();
        p.setIdUsuario       (rs.getInt       ("id_usuario"));
        p.setBiografia       (rs.getString    ("biografia"));
        p.setNivelExperiencia(rs.getString    ("nivel_experiencia"));
        p.setTarifaHora      (rs.getBigDecimal("tarifa_hora"));
        return p;
    }
}
