/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.connectwork.dao;

import com.connectwork.config.DBConnection;
import com.connectwork.model.Habilidad;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author braya
 */
public class HabilidadDAO {
     public List<Habilidad> findAll() throws SQLException {
        String sql = """
                     
            SELECT h.*, c.nombre AS nombre_categoria
            FROM habilidades h
            LEFT JOIN categorias c ON h.id_categoria = c.id_categoria
            WHERE h.activa = 1
            """;
        List<Habilidad> lista = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapRow(rs));
        }
        return lista;
    }

    public List<Habilidad> findByCategoria(int idCategoria) throws SQLException {
        String sql = """
            SELECT h.*, c.nombre AS nombre_categoria
            FROM habilidades h
            LEFT JOIN categorias c ON h.id_categoria = c.id_categoria
            WHERE h.id_categoria = ? AND h.activa = 1
            """;
        List<Habilidad> lista = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idCategoria);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapRow(rs));
            }
        }
        return lista;
    }

    public int insert(Habilidad h) throws SQLException {
        String sql = "INSERT INTO habilidades (nombre, id_categoria, activa) VALUES (?,?,1)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, h.getNombre());
            ps.setInt   (2, h.getIdCategoria());
            ps.executeUpdate();
            try (ResultSet gen = ps.getGeneratedKeys()) {
                if (gen.next()) return gen.getInt(1);
            }
        }
        return -1;
    }

    public boolean update(Habilidad h) throws SQLException {
        String sql = "UPDATE habilidades SET nombre=?, id_categoria=? WHERE id_habilidad=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, h.getNombre());
            ps.setInt   (2, h.getIdCategoria());
            ps.setInt   (3, h.getIdHabilidad());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean toggleActiva(int idHabilidad, boolean activa) throws SQLException {
        String sql = "UPDATE habilidades SET activa=? WHERE id_habilidad=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setBoolean(1, activa);
            ps.setInt    (2, idHabilidad);
            return ps.executeUpdate() > 0;
        }
    }

    private Habilidad mapRow(ResultSet rs) throws SQLException {
        Habilidad h = new Habilidad();
        h.setIdHabilidad    (rs.getInt    ("id_habilidad"));
        h.setNombre         (rs.getString ("nombre"));
        h.setIdCategoria    (rs.getInt    ("id_categoria"));
        h.setActiva         (rs.getBoolean("activa"));
        try { h.setNombreCategoria(rs.getString("nombre_categoria")); } catch (SQLException ignored) {}
        return h;
    }
}
