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
public class Usuario {
    private int     idUsuario;
    private String  nombreCompleto;
    private String  nombreUsuario;
    private String  passwordHash;
    private String  correo;
    private String  telefono;
    private String  cui;
    private Date    fechaNacimiento;
    private String  tipoUsuario;   
    private boolean estadoCuenta;
    
    public int     getIdUsuario()        { return idUsuario; }
    public void    setIdUsuario(int v)   { this.idUsuario = v; }

    public String  getNombreCompleto()           { return nombreCompleto; }
    public void    setNombreCompleto(String v)   { this.nombreCompleto = v; }

    public String  getNombreUsuario()            { return nombreUsuario; }
    public void    setNombreUsuario(String v)    { this.nombreUsuario = v; }

    public String  getPasswordHash()             { return passwordHash; }
    public void    setPasswordHash(String v)     { this.passwordHash = v; }

    public String  getCorreo()                   { return correo; }
    public void    setCorreo(String v)           { this.correo = v; }

    public String  getTelefono()                 { return telefono; }
    public void    setTelefono(String v)         { this.telefono = v; }

    public String  getCui()                      { return cui; }
    public void    setCui(String v)              { this.cui = v; }

    public Date    getFechaNacimiento()          { return fechaNacimiento; }
    public void    setFechaNacimiento(Date v)    { this.fechaNacimiento = v; }

    public String  getTipoUsuario()              { return tipoUsuario; }
    public void    setTipoUsuario(String v)      { this.tipoUsuario = v; }

    public boolean isEstadoCuenta()              { return estadoCuenta; }
    public void    setEstadoCuenta(boolean v)    { this.estadoCuenta = v; }

}
