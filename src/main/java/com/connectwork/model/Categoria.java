/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.connectwork.model;

/**
 *
 * @author braya
 */
public class Categoria {
    private int     idCategoria;
    private String  nombre;
    private boolean activa;

    public int     getIdCategoria()         { return idCategoria; }
    public void    setIdCategoria(int v)    { this.idCategoria = v; }
    public String  getNombre()              { return nombre; }
    public void    setNombre(String v)      { this.nombre = v; }
    public boolean isActiva()               { return activa; }
    public void    setActiva(boolean v)     { this.activa = v; }
}

