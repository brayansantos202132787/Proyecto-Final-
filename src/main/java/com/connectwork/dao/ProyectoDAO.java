/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.connectwork.dao;

import com.connectwork.config.DBConnection;
import com.connectwork.model.Proyecto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author braya
 */
public class ProyectoDAO {
    public int insert(Proyecto p) throws SQLException {
        String sql = """
                     
            INSERT INTO proyectos
              (id_cliente, titulo, descripcion, presupuesto_maximo, fecha_limite, estado, id_categoria)
            VALUES (?,?,?,?,?,?,?)
            """;
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt   (1, p.getIdCliente());
            ps.setString(2, p.getTitulo());
            ps.setString(3, p.getDescripcion());
            ps.setBigDecimal(4, p.getPresupuestoMaximo());
            ps.setDate  (5, p.getFechaLimite() != null
                            ? new java.sql.Date(p.getFechaLimite().getTime()) : null);
            ps.setString(6, "ABIERTO");
            ps.setInt   (7, p.getIdCategoria());
            ps.executeUpdate();
            try (ResultSet gen = ps.getGeneratedKeys()) {
                if (gen.next()) return gen.getInt(1);
            }
        }
        return -1;
    }

    public List<Proyecto> findAbiertos(Integer idCategoria, Integer idHabilidad,
                                       Double presMinimo, Double presMaximo) throws SQLException {
        StringBuilder sql = new StringBuilder("""
            SELECT p.*, u.nombre_completo AS nombre_cliente, cat.nombre AS nombre_categoria
            FROM proyectos p
            JOIN usuarios u ON p.id_cliente = u.id_usuario
            LEFT JOIN categorias cat ON p.id_categoria = cat.id_categoria
            WHERE p.estado = 'ABIERTO'
            """);
        List<Object> params = new ArrayList<>();

        if (idCategoria != null) {
            sql.append(" AND p.id_categoria = ?"); params.add(idCategoria);
        }
        if (idHabilidad != null) {
            sql.append(" AND EXISTS (SELECT 1 FROM proyecto_habilidades ph WHERE ph.id_proyecto = p.id_proyecto AND ph.id_habilidad = ?)");
            params.add(idHabilidad);
        }
        if (presMinimo != null) {
            sql.append(" AND p.presupuesto_maximo >= ?"); params.add(presMinimo);
        }
        if (presMaximo != null) {
            sql.append(" AND p.presupuesto_maximo <= ?"); params.add(presMaximo);
        }

        List<Proyecto> lista = new ArrayList<>();
        try (Connection c  = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapRow(rs));
            }
        }
        return lista;
    }

    public List<Proyecto> findByCliente(int idCliente) throws SQLException {
        String sql = """
            SELECT p.*, u.nombre_completo AS nombre_cliente, cat.nombre AS nombre_categoria
            FROM proyectos p
            JOIN usuarios u ON p.id_cliente = u.id_usuario
            LEFT JOIN categorias cat ON p.id_categoria = cat.id_categoria
            WHERE p.id_cliente = ?
            """;
        List<Proyecto> lista = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapRow(rs));
            }
        }
        return lista;
    }

    public Proyecto findById(int id) throws SQLException {
        String sql = """
            SELECT p.*, u.nombre_completo AS nombre_cliente, cat.nombre AS nombre_categoria
            FROM proyectos p
            JOIN usuarios u ON p.id_cliente = u.id_usuario
            LEFT JOIN categorias cat ON p.id_categoria = cat.id_categoria
            WHERE p.id_proyecto = ?
            """;
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public boolean updateEstado(int idProyecto, String estado) throws SQLException {
        String sql = "UPDATE proyectos SET estado = ? WHERE id_proyecto = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setInt   (2, idProyecto);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean update(Proyecto p) throws SQLException {
        String sql = """
            UPDATE proyectos
            SET titulo=?, descripcion=?, presupuesto_maximo=?, fecha_limite=?, id_categoria=?
            WHERE id_proyecto=? AND estado='ABIERTO'
            """;
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString    (1, p.getTitulo());
            ps.setString    (2, p.getDescripcion());
            ps.setBigDecimal(3, p.getPresupuestoMaximo());
            ps.setDate      (4, p.getFechaLimite() != null
                               ? new java.sql.Date(p.getFechaLimite().getTime()) : null);
            ps.setInt       (5, p.getIdCategoria());
            ps.setInt       (6, p.getIdProyecto());
            return ps.executeUpdate() > 0;
        }
    }

    // Habilidades requeridas del proyecto
    public void insertHabilidad(int idProyecto, int idHabilidad) throws SQLException {
        String sql = "INSERT IGNORE INTO proyecto_habilidades (id_proyecto, id_habilidad) VALUES (?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idProyecto);
            ps.setInt(2, idHabilidad);
            ps.executeUpdate();
        }
    }

    public List<Integer> findHabilidades(int idProyecto) throws SQLException {
        String sql = "SELECT id_habilidad FROM proyecto_habilidades WHERE id_proyecto = ?";
        List<Integer> lista = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idProyecto);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(rs.getInt("id_habilidad"));
            }
        }
        return lista;
    }

    private Proyecto mapRow(ResultSet rs) throws SQLException {
        Proyecto p = new Proyecto();
        p.setIdProyecto       (rs.getInt       ("id_proyecto"));
        p.setIdCliente        (rs.getInt       ("id_cliente"));
        p.setTitulo           (rs.getString    ("titulo"));
        p.setDescripcion      (rs.getString    ("descripcion"));
        p.setPresupuestoMaximo(rs.getBigDecimal("presupuesto_maximo"));
        p.setFechaLimite      (rs.getDate      ("fecha_limite"));
        p.setEstado           (rs.getString    ("estado"));
        p.setIdCategoria      (rs.getInt       ("id_categoria"));
        
        try { p.setNombreCliente  (rs.getString("nombre_cliente"));   } catch (SQLException ignored) {}
        try { p.setNombreCategoria(rs.getString("nombre_categoria")); } catch (SQLException ignored) {}
        return p;
    }
}
