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
public class SolicitudCategoria {
    private int    idSolicitud;
    private int    idCliente;
    private String nombre;
    private String descripcion;
    private String estado;       // PENDIENTE | ACEPTADA | RECHAZADA
    private Date   fecha;

    // Enriquecido
    private String nombreCliente;

    public int    getIdSolicitud()             { return idSolicitud; }
    public void   setIdSolicitud(int v)        { this.idSolicitud = v; }
    public int    getIdCliente()               { return idCliente; }
    public void   setIdCliente(int v)          { this.idCliente = v; }
    public String getNombre()                  { return nombre; }
    public void   setNombre(String v)          { this.nombre = v; }
    public String getDescripcion()             { return descripcion; }
    public void   setDescripcion(String v)     { this.descripcion = v; }
    public String getEstado()                  { return estado; }
    public void   setEstado(String v)          { this.estado = v; }
    public Date   getFecha()                   { return fecha; }
    public void   setFecha(Date v)             { this.fecha = v; }
    public String getNombreCliente()           { return nombreCliente; }
    public void   setNombreCliente(String v)   { this.nombreCliente = v; }
}
