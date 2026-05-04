/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.connectwork.model;

import java.math.BigDecimal;

/**
 *
 * @author braya
 */
public class Saldo {
     private int        idUsuario;
    private BigDecimal saldoDisponible;
    private BigDecimal saldoBloqueado;

    public int        getIdUsuario()                    { return idUsuario; }
    public void       setIdUsuario(int v)               { this.idUsuario = v; }
    public BigDecimal getSaldoDisponible()              { return saldoDisponible; }
    public void       setSaldoDisponible(BigDecimal v)  { this.saldoDisponible = v; }
    public BigDecimal getSaldoBloqueado()               { return saldoBloqueado; }
    public void       setSaldoBloqueado(BigDecimal v)   { this.saldoBloqueado = v; }
}
