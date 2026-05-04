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
public class RecargaSaldo {
    private int        idRecarga;
    private int        idUsuario;
    private BigDecimal monto;
    private Date       fechaRecarga;

    public int        getIdRecarga()               { return idRecarga; }
    public void       setIdRecarga(int v)          { this.idRecarga = v; }
    public int        getIdUsuario()               { return idUsuario; }
    public void       setIdUsuario(int v)          { this.idUsuario = v; }
    public BigDecimal getMonto()                   { return monto; }
    public void       setMonto(BigDecimal v)       { this.monto = v; }
    public Date       getFechaRecarga()            { return fechaRecarga; }
    public void       setFechaRecarga(Date v)      { this.fechaRecarga = v; }
}
