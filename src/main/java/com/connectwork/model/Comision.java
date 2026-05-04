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
public class Comision {
    private int        idComision;
    private BigDecimal porcentaje;
    private Date       fechaInicio;
    private Date       fechaFin;    // null = activa actualmente

    public int        getIdComision()              { return idComision; }
    public void       setIdComision(int v)         { this.idComision = v; }
    public BigDecimal getPorcentaje()              { return porcentaje; }
    public void       setPorcentaje(BigDecimal v)  { this.porcentaje = v; }
    public Date       getFechaInicio()             { return fechaInicio; }
    public void       setFechaInicio(Date v)       { this.fechaInicio = v; }
    public Date       getFechaFin()                { return fechaFin; }
    public void       setFechaFin(Date v)          { this.fechaFin = v; }
}
