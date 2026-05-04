/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.connectwork.model;

/**
 *
 * @author braya
 */
public class PerfilCliente {
    private int    idUsuario;
    private String descripcionEmpresa;
    private String sector;
    private String sitioWeb;

    public int    getIdUsuario()                  { return idUsuario; }
    public void   setIdUsuario(int v)             { this.idUsuario = v; }
    public String getDescripcionEmpresa()         { return descripcionEmpresa; }
    public void   setDescripcionEmpresa(String v) { this.descripcionEmpresa = v; }
    public String getSector()                     { return sector; }
    public void   setSector(String v)             { this.sector = v; }
    public String getSitioWeb()                   { return sitioWeb; }
    public void   setSitioWeb(String v)           { this.sitioWeb = v; }
}

