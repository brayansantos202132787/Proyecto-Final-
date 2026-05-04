/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.connectwork.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 *
 * @author braya
 */
public class Proyecto {
     private int        idProyecto;
    private int        idCliente;
    private String     titulo;
    private String     descripcion;
    private BigDecimal presupuestoMaximo;
    private Date       fechaLimite;
    private String     estado; 
    private int        idCategoria;

    
    private List<Integer> habilidadesRequeridas;
    private String        nombreCliente;
    private String        nombreCategoria;

    // Getters y Setters
    public int        getIdProyecto()                  { return idProyecto; }
    public void       setIdProyecto(int v)             { this.idProyecto = v; }

    public int        getIdCliente()                   { return idCliente; }
    public void       setIdCliente(int v)              { this.idCliente = v; }

    public String     getTitulo()                      { return titulo; }
    public void       setTitulo(String v)              { this.titulo = v; }

    public String     getDescripcion()                 { return descripcion; }
    public void       setDescripcion(String v)         { this.descripcion = v; }

    public BigDecimal getPresupuestoMaximo()           { return presupuestoMaximo; }
    public void       setPresupuestoMaximo(BigDecimal v){ this.presupuestoMaximo = v; }

    public Date       getFechaLimite()                 { return fechaLimite; }
    public void       setFechaLimite(Date v)           { this.fechaLimite = v; }

    public String     getEstado()                      { return estado; }
    public void       setEstado(String v)              { this.estado = v; }

    public int        getIdCategoria()                 { return idCategoria; }
    public void       setIdCategoria(int v)            { this.idCategoria = v; }

    public List<Integer> getHabilidadesRequeridas()            { return habilidadesRequeridas; }
    public void          setHabilidadesRequeridas(List<Integer> v){ this.habilidadesRequeridas = v; }

    public String     getNombreCliente()               { return nombreCliente; }
    public void       setNombreCliente(String v)       { this.nombreCliente = v; }

    public String     getNombreCategoria()             { return nombreCategoria; }
    public void       setNombreCategoria(String v)     { this.nombreCategoria = v; }
}
