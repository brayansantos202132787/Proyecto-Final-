/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.connectwork.model;

import java.util.Date;

/**
 *
 * @author braya
 */
public class Entrega {
    private int    idEntrega;
    private int    idContrato;
    private String descripcion;
    private String urlArchivo;
    private Date   fechaEntrega;
    private String estado;          // PENDIENTE | APROBADA | RECHAZADA
    private String motivoRechazo;

    public int    getIdEntrega()              { return idEntrega; }
    public void   setIdEntrega(int v)         { this.idEntrega = v; }
    public int    getIdContrato()             { return idContrato; }
    public void   setIdContrato(int v)        { this.idContrato = v; }
    public String getDescripcion()            { return descripcion; }
    public void   setDescripcion(String v)    { this.descripcion = v; }
    public String getUrlArchivo()             { return urlArchivo; }
    public void   setUrlArchivo(String v)     { this.urlArchivo = v; }
    public Date   getFechaEntrega()           { return fechaEntrega; }
    public void   setFechaEntrega(Date v)     { this.fechaEntrega = v; }
    public String getEstado()                 { return estado; }
    public void   setEstado(String v)         { this.estado = v; }
    public String getMotivoRechazo()          { return motivoRechazo; }
    public void   setMotivoRechazo(String v)  { this.motivoRechazo = v; }
}
