/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.connectwork.service;

import com.connectwork.dao.*;
import com.connectwork.model.*;

import java.math.BigDecimal;
import java.sql.SQLException;

/**
 *
 * @author braya
 */
public class ContratoService {
    
    private final PropuestaDAO propuestaDAO = new PropuestaDAO();
    private final ContratoDAO  contratoDAO  = new ContratoDAO();
    private final ProyectoDAO  proyectoDAO  = new ProyectoDAO();
    private final SaldoDAO     saldoDAO     = new SaldoDAO();
    private final ComisionDAO  comisionDAO  = new ComisionDAO();

    
    public Contrato aceptarPropuesta(int idPropuesta, int idCliente) throws SQLException {
        Propuesta propuesta = propuestaDAO.findById(idPropuesta);
        if (propuesta == null) throw new IllegalArgumentException("Propuesta no encontrada");

        Proyecto proyecto = proyectoDAO.findById(propuesta.getIdProyecto());
        if (proyecto == null || proyecto.getIdCliente() != idCliente)
            throw new IllegalArgumentException("No autorizado");
        if (!"ABIERTO".equals(proyecto.getEstado()))
            throw new IllegalStateException("El proyecto no está abierto");

        // Verificar saldo del cliente
        BigDecimal saldoCliente = saldoDAO.getSaldo(idCliente);
        if (saldoCliente.compareTo(propuesta.getMontoOfertado()) < 0)
            throw new IllegalStateException("Saldo insuficiente");

        // Bloquear monto del saldo del cliente
        saldoDAO.bloquearMonto(idCliente, propuesta.getMontoOfertado());

        // Rechazar las demás propuestas del proyecto
        propuestaDAO.rechazarOtras(propuesta.getIdProyecto(), idPropuesta);

        // Marcar propuesta como aceptada
        propuestaDAO.updateEstado(idPropuesta, "ACEPTADA");

        // Crear contrato
        Contrato contrato = new Contrato();
        contrato.setIdPropuesta   (idPropuesta);
        contrato.setMontoBlockeado(propuesta.getMontoOfertado());
        int idContrato = contratoDAO.insert(contrato);
        contrato.setIdContrato(idContrato);

        // Cambiar estado del proyecto
        proyectoDAO.updateEstado(propuesta.getIdProyecto(), "EN_PROGRESO");

        return contrato;
    }

    // El cliente aprueba una entrega → liberar pago
    public void aprobarEntrega(int idContrato, int idCliente) throws SQLException {
        Contrato contrato = contratoDAO.findById(idContrato);
        if (contrato == null) throw new IllegalArgumentException("Contrato no encontrado");

        Propuesta propuesta = propuestaDAO.findById(contrato.getIdPropuesta());
        Proyecto  proyecto  = proyectoDAO.findById(propuesta.getIdProyecto());

        if (proyecto.getIdCliente() != idCliente)
            throw new IllegalArgumentException("No autorizado");

        // Obtener comisión activa
        BigDecimal porcentaje = comisionDAO.getPorcentajeActual();
        BigDecimal monto      = contrato.getMontoBlockeado();
        BigDecimal comision   = monto.multiply(porcentaje).divide(BigDecimal.valueOf(100));
        BigDecimal pagoFreelancer = monto.subtract(comision);

        // Acreditar al freelancer
        saldoDAO.acreditar(propuesta.getIdFreelancer(), pagoFreelancer);

        // Acumular comisión en saldo global
        saldoDAO.acumularComisionGlobal(comision);

        // Registrar historial de comisión en el contrato
        contratoDAO.registrarComision(idContrato, comision);

        // Cambiar estado del proyecto
        proyectoDAO.updateEstado(proyecto.getIdProyecto(), "COMPLETADO");
    }

    // El cliente cancela el contrato
    public void cancelarContrato(int idContrato, int idCliente, String motivo) throws SQLException {
        Contrato contrato = contratoDAO.findById(idContrato);
        if (contrato == null) throw new IllegalArgumentException("Contrato no encontrado");

        Propuesta propuesta = propuestaDAO.findById(contrato.getIdPropuesta());
        Proyecto  proyecto  = proyectoDAO.findById(propuesta.getIdProyecto());

        if (proyecto.getIdCliente() != idCliente)
            throw new IllegalArgumentException("No autorizado");

        // Devolver monto bloqueado al cliente
        saldoDAO.devolverMonto(idCliente, contrato.getMontoBlockeado());

        // Guardar motivo y cancelar
        contratoDAO.cancelar(idContrato, motivo);
        proyectoDAO.updateEstado(proyecto.getIdProyecto(), "CANCELADO");
    }
}
