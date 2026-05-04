/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.connectwork.model;

import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author braya
 */
public class Contrato {
    private int        idContrato;
    private int        idPropuesta;
    private Date       fechaInicio;
    private BigDecimal montoBlockeado;
    private String     estado;              // ACTIVO | COMPLETADO | CANCELADO
    private String     motivoCancelacion;
    private BigDecimal comisionAplicada;

    // Enriquecidos
    private String     tituloProyecto;
    private String     nombreFreelancer;
    private String     nombreCliente;

    public int        getIdContrato()                   { return idContrato; }
    public void       setIdContrato(int v)              { this.idContrato = v; }
    public int        getIdPropuesta()                  { return idPropuesta; }
    public void       setIdPropuesta(int v)             { this.idPropuesta = v; }
    public Date       getFechaInicio()                  { return fechaInicio; }
    public void       setFechaInicio(Date v)            { this.fechaInicio = v; }
    public BigDecimal getMontoBlockeado()               { return montoBlockeado; }
    public void       setMontoBlockeado(BigDecimal v)   { this.montoBlockeado = v; }
    public String     getEstado()                       { return estado; }
    public void       setEstado(String v)               { this.estado = v; }
    public String     getMotivoCancelacion()            { return motivoCancelacion; }
    public void       setMotivoCancelacion(String v)    { this.motivoCancelacion = v; }
    public BigDecimal getComisionAplicada()             { return comisionAplicada; }
    public void       setComisionAplicada(BigDecimal v) { this.comisionAplicada = v; }
    public String     getTituloProyecto()               { return tituloProyecto; }
    public void       setTituloProyecto(String v)       { this.tituloProyecto = v; }
    public String     getNombreFreelancer()             { return nombreFreelancer; }
    public void       setNombreFreelancer(String v)     { this.nombreFreelancer = v; }
    public String     getNombreCliente()                { return nombreCliente; }
    public void       setNombreCliente(String v)        { this.nombreCliente = v; }
}
