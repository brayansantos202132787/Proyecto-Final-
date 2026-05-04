/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.connectwork.dao;

import com.connectwork.config.DBConnection;

import java.sql.*;
import java.util.*;

/**
 *
 * @author braya
 */
public class ReporteDAO {
    public List<Map<String, Object>> topFreelancers(String desde, String hasta) throws SQLException {
        String sql = """
            SELECT u.nombre_completo, COUNT(ct.id_contrato) AS contratos_completados,
                   SUM(ct.monto_bloqueado) AS total_generado,
                   SUM(ct.comision_aplicada) AS comision_plataforma
            FROM contratos ct
            JOIN propuestas p ON ct.id_propuesta = p.id_propuesta
            JOIN usuarios u   ON p.id_freelancer = u.id_usuario
            WHERE ct.estado = 'COMPLETADO'
              AND ct.fecha_inicio BETWEEN ? AND ?
            GROUP BY u.id_usuario, u.nombre_completo
            ORDER BY total_generado DESC
            LIMIT 5
            """;
        return ejecutarReporte(sql, desde, hasta);
    }

    // TOP 5 categorías con más actividad
    public List<Map<String, Object>> topCategorias(String desde, String hasta) throws SQLException {
        String sql = """
            SELECT cat.nombre AS categoria, COUNT(ct.id_contrato) AS total_contratos,
                   SUM(ct.comision_aplicada) AS total_comisiones
            FROM contratos ct
            JOIN propuestas p   ON ct.id_propuesta  = p.id_propuesta
            JOIN proyectos pr   ON p.id_proyecto    = pr.id_proyecto
            JOIN categorias cat ON pr.id_categoria  = cat.id_categoria
            WHERE ct.estado = 'COMPLETADO'
              AND ct.fecha_inicio BETWEEN ? AND ?
            GROUP BY cat.id_categoria, cat.nombre
            ORDER BY total_contratos DESC
            LIMIT 5
            """;
        return ejecutarReporte(sql, desde, hasta);
    }

    // Ingresos totales de la plataforma
    public Map<String, Object> ingresosTotales(String desde, String hasta) throws SQLException {
        String sql = """
            SELECT COUNT(*) AS contratos_completados,
                   COALESCE(SUM(comision_aplicada), 0) AS total_comisiones
            FROM contratos
            WHERE estado = 'COMPLETADO'
              AND fecha_inicio BETWEEN ? AND ?
            """;
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, desde); ps.setString(2, hasta);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rsToMap(rs);
            }
        }
        return Collections.emptyMap();
    }

    // Proyectos del cliente en intervalo
    public List<Map<String, Object>> proyectosPorCliente(int idCliente, String desde, String hasta) throws SQLException {
        String sql = """
            SELECT pr.titulo, pr.estado, pr.presupuesto_maximo,
                   p.monto_ofertado, u.nombre_completo AS freelancer
            FROM proyectos pr
            LEFT JOIN propuestas p ON p.id_proyecto = pr.id_proyecto AND p.estado = 'ACEPTADA'
            LEFT JOIN usuarios u   ON p.id_freelancer = u.id_usuario
            WHERE pr.id_cliente = ?
              AND pr.fecha_limite BETWEEN ? AND ?
            ORDER BY pr.fecha_limite DESC
            """;
        List<Map<String, Object>> lista = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idCliente); ps.setString(2, desde); ps.setString(3, hasta);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(rsToMap(rs));
            }
        }
        return lista;
    }

    // Gasto por categoría del cliente
    public List<Map<String, Object>> gastoPorCategoria(int idCliente, String desde, String hasta) throws SQLException {
        String sql = """
            SELECT cat.nombre AS categoria, COUNT(*) AS contratos,
                   SUM(ct.monto_bloqueado) AS total_gastado
            FROM contratos ct
            JOIN propuestas p   ON ct.id_propuesta = p.id_propuesta
            JOIN proyectos pr   ON p.id_proyecto   = pr.id_proyecto
            JOIN categorias cat ON pr.id_categoria = cat.id_categoria
            WHERE pr.id_cliente = ? AND ct.estado = 'COMPLETADO'
              AND ct.fecha_inicio BETWEEN ? AND ?
            GROUP BY cat.id_categoria, cat.nombre
            ORDER BY total_gastado DESC
            """;
        List<Map<String, Object>> lista = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idCliente); ps.setString(2, desde); ps.setString(3, hasta);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(rsToMap(rs));
            }
        }
        return lista;
    }

    // Contratos completados del freelancer
    public List<Map<String, Object>> contratosFreelancer(int idFreelancer, String desde, String hasta) throws SQLException {
        String sql = """
            SELECT pr.titulo AS proyecto, uc.nombre_completo AS cliente,
                   ct.monto_bloqueado AS monto_recibido,
                   COALESCE(cal.estrellas, 0) AS calificacion
            FROM contratos ct
            JOIN propuestas p  ON ct.id_propuesta = p.id_propuesta
            JOIN proyectos pr  ON p.id_proyecto   = pr.id_proyecto
            JOIN usuarios uc   ON pr.id_cliente   = uc.id_usuario
            LEFT JOIN calificaciones cal ON cal.id_contrato = ct.id_contrato
            WHERE p.id_freelancer = ? AND ct.estado = 'COMPLETADO'
              AND ct.fecha_inicio BETWEEN ? AND ?
            ORDER BY ct.fecha_inicio DESC
            """;
        List<Map<String, Object>> lista = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idFreelancer); ps.setString(2, desde); ps.setString(3, hasta);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(rsToMap(rs));
            }
        }
        return lista;
    }

    // Propuestas enviadas por freelancer en intervalo
    public List<Map<String, Object>> propuestasFreelancer(int idFreelancer, String desde, String hasta) throws SQLException {
        String sql = """
            SELECT pr.titulo AS proyecto, p.monto_ofertado, p.estado,
                   p.plazo_entrega_dias
            FROM propuestas p
            JOIN proyectos pr ON p.id_proyecto = pr.id_proyecto
            WHERE p.id_freelancer = ?
              AND pr.fecha_limite BETWEEN ? AND ?
            ORDER BY pr.fecha_limite DESC
            """;
        List<Map<String, Object>> lista = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idFreelancer); ps.setString(2, desde); ps.setString(3, hasta);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(rsToMap(rs));
            }
        }
        return lista;
    }

    // Top 5 categorías del freelancer
    public List<Map<String, Object>> topCategoriaFreelancer(int idFreelancer) throws SQLException {
        String sql = """
            SELECT cat.nombre AS categoria, COUNT(*) AS contratos,
                   SUM(ct.monto_bloqueado) AS total_ingresos
            FROM contratos ct
            JOIN propuestas p   ON ct.id_propuesta = p.id_propuesta
            JOIN proyectos pr   ON p.id_proyecto   = pr.id_proyecto
            JOIN categorias cat ON pr.id_categoria = cat.id_categoria
            WHERE p.id_freelancer = ? AND ct.estado = 'COMPLETADO'
            GROUP BY cat.id_categoria, cat.nombre
            ORDER BY contratos DESC
            LIMIT 5
            """;
        List<Map<String, Object>> lista = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idFreelancer);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(rsToMap(rs));
            }
        }
        return lista;
    }

    // Utilidades privadas
    private List<Map<String, Object>> ejecutarReporte(String sql, String desde, String hasta) throws SQLException {
        List<Map<String, Object>> lista = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, desde); ps.setString(2, hasta);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(rsToMap(rs));
            }
        }
        return lista;
    }

    private Map<String, Object> rsToMap(ResultSet rs) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        Map<String, Object> row = new LinkedHashMap<>();
        for (int i = 1; i <= meta.getColumnCount(); i++) {
            row.put(meta.getColumnLabel(i), rs.getObject(i));
        }
        return row;
    }
}
