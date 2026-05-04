/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.connectwork.dao;

import com.connectwork.config.DBConnection;
import com.connectwork.model.Categoria;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author braya
 */
public class CategoriaDAO {
     public List<Categoria> findAll() throws SQLException {
        String sql = "SELECT * FROM categorias WHERE activa = 1";
        List<Categoria> lista = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapRow(rs));
        }
        return lista;
    }

    public Categoria findById(int id) throws SQLException {
        String sql = "SELECT * FROM categorias WHERE id_categoria = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public int insert(Categoria cat) throws SQLException {
        String sql = "INSERT INTO categorias (nombre, activa) VALUES (?, 1)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, cat.getNombre());
            ps.executeUpdate();
            try (ResultSet gen = ps.getGeneratedKeys()) {
                if (gen.next()) return gen.getInt(1);
            }
        }
        return -1;
    }

    public boolean update(Categoria cat) throws SQLException {
        String sql = "UPDATE categorias SET nombre=? WHERE id_categoria=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, cat.getNombre());
            ps.setInt   (2, cat.getIdCategoria());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean toggleActiva(int idCategoria, boolean activa) throws SQLException {
        String sql = "UPDATE categorias SET activa=? WHERE id_categoria=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setBoolean(1, activa);
            ps.setInt    (2, idCategoria);
            return ps.executeUpdate() > 0;
        }
    }

    private Categoria mapRow(ResultSet rs) throws SQLException {
        Categoria cat = new Categoria();
        cat.setIdCategoria(rs.getInt    ("id_categoria"));
        cat.setNombre     (rs.getString ("nombre"));
        cat.setActiva     (rs.getBoolean("activa"));
        return cat;
    }
}
