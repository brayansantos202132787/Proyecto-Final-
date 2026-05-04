/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.connectwork.model;

/**
 *
 * @author braya
 */
public class Habilidad {
    private int    idHabilidad;
    private String nombre;
    private int    idCategoria;
    private String nombreCategoria;
    private boolean activa;

    public int    getIdHabilidad()              { return idHabilidad; }
    public void   setIdHabilidad(int v)         { this.idHabilidad = v; }
    public String getNombre()                   { return nombre; }
    public void   setNombre(String v)           { this.nombre = v; }
    public int    getIdCategoria()              { return idCategoria; }
    public void   setIdCategoria(int v)         { this.idCategoria = v; }
    public String getNombreCategoria()          { return nombreCategoria; }
    public void   setNombreCategoria(String v)  { this.nombreCategoria = v; }
    public boolean isActiva()                   { return activa; }
    public void   setActiva(boolean v)          { this.activa = v; }
}
