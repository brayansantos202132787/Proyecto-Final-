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
public class Calificacion {
    private int    idCalificacion;
    private int    idContrato;
    private int    estrellas;     // 1 - 5
    private String comentario;
    private Date   fecha;

    // Enriquecidos
    private String nombreCliente;
    private String tituloProyecto;

    public int    getIdCalificacion()           { return idCalificacion; }
    public void   setIdCalificacion(int v)      { this.idCalificacion = v; }
    public int    getIdContrato()               { return idContrato; }
    public void   setIdContrato(int v)          { this.idContrato = v; }
    public int    getEstrellas()                { return estrellas; }
    public void   setEstrellas(int v)           { this.estrellas = v; }
    public String getComentario()               { return comentario; }
    public void   setComentario(String v)       { this.comentario = v; }
    public Date   getFecha()                    { return fecha; }
    public void   setFecha(Date v)              { this.fecha = v; }
    public String getNombreCliente()            { return nombreCliente; }
    public void   setNombreCliente(String v)    { this.nombreCliente = v; }
    public String getTituloProyecto()           { return tituloProyecto; }
    public void   setTituloProyecto(String v)   { this.tituloProyecto = v; }
}
