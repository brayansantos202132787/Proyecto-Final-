/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.connectwork.service;

import com.connectwork.dao.ComisionDAO;
import com.connectwork.dao.ReporteDAO;
import com.connectwork.dao.SaldoDAO;
import com.connectwork.model.Comision;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 *
 * @author braya
 */
public class ReporteService {
    private final ReporteDAO  reporteDAO  = new ReporteDAO();
    private final ComisionDAO comisionDAO = new ComisionDAO();
    private final SaldoDAO    saldoDAO    = new SaldoDAO();

    public List<Map<String, Object>> topFreelancers(String desde, String hasta) throws SQLException {
        validarFechas(desde, hasta);
        return reporteDAO.topFreelancers(desde, hasta);
    }

    public List<Map<String, Object>> topCategorias(String desde, String hasta) throws SQLException {
        validarFechas(desde, hasta);
        return reporteDAO.topCategorias(desde, hasta);
    }

    public Map<String, Object> ingresosTotales(String desde, String hasta) throws SQLException {
        validarFechas(desde, hasta);
        return reporteDAO.ingresosTotales(desde, hasta);
    }

    public List<Comision> historialComisiones() throws SQLException {
        return comisionDAO.getHistorial();
    }

    public List<Map<String, Object>> proyectosPorCliente(int idCliente, String desde, String hasta) throws SQLException {
        validarFechas(desde, hasta);
        return reporteDAO.proyectosPorCliente(idCliente, desde, hasta);
    }

    public List<?> recargas(int idCliente) throws SQLException {
        return saldoDAO.getHistorialRecargas(idCliente);
    }

    public List<Map<String, Object>> gastoPorCategoria(int idCliente, String desde, String hasta) throws SQLException {
        validarFechas(desde, hasta);
        return reporteDAO.gastoPorCategoria(idCliente, desde, hasta);
    }

    public List<Map<String, Object>> contratosFreelancer(int idFreelancer, String desde, String hasta) throws SQLException {
        validarFechas(desde, hasta);
        return reporteDAO.contratosFreelancer(idFreelancer, desde, hasta);
    }

    public List<Map<String, Object>> propuestasFreelancer(int idFreelancer, String desde, String hasta) throws SQLException {
        validarFechas(desde, hasta);
        return reporteDAO.propuestasFreelancer(idFreelancer, desde, hasta);
    }

    public List<Map<String, Object>> topCategoriaFreelancer(int idFreelancer) throws SQLException {
        return reporteDAO.topCategoriaFreelancer(idFreelancer);
    }

    private void validarFechas(String desde, String hasta) {
        if (desde == null || hasta == null || desde.isBlank() || hasta.isBlank())
            throw new IllegalArgumentException("Los parámetros 'desde' y 'hasta' son obligatorios");
    }
}
