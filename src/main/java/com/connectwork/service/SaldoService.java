/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.connectwork.service;

import com.connectwork.dao.SaldoDAO;
import com.connectwork.model.RecargaSaldo;
import com.connectwork.model.Saldo;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author braya
 */
public class SaldoService {
     private final SaldoDAO saldoDAO = new SaldoDAO();

    public Saldo obtenerSaldo(int idUsuario) throws SQLException {
        Saldo s = saldoDAO.getSaldoCompleto(idUsuario);
        if (s == null) throw new IllegalArgumentException("Saldo no encontrado");
        return s;
    }

    public void recargar(int idUsuario, BigDecimal monto) throws SQLException {
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("El monto de recarga debe ser mayor a 0");
        saldoDAO.recargar(idUsuario, monto);
    }

    public List<RecargaSaldo> historialRecargas(int idUsuario) throws SQLException {
        return saldoDAO.getHistorialRecargas(idUsuario);
    }

    public BigDecimal obtenerSaldoGlobal() throws SQLException {
        return saldoDAO.getSaldoGlobal();
    }
}
