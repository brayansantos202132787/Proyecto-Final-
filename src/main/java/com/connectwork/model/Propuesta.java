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
public class Propuesta {
    private int        idPropuesta;
    private int        idProyecto;
    private int        idFreelancer;
    private BigDecimal montoOfertado;
    private int        plazoEntregaDias;
    private String     cartaPresentacion;
    private String     estado; // PENDIENTE | ACEPTADA | RECHAZADA

    // Campos enriquecidos para respuestas
    private String     nombreFreelancer;
    private Double     calificacionPromedio;
    private String     tituloProyecto;

    public int        getIdPropuesta()                  { return idPropuesta; }
    public void       setIdPropuesta(int v)             { this.idPropuesta = v; }
    public int        getIdProyecto()                   { return idProyecto; }
    public void       setIdProyecto(int v)              { this.idProyecto = v; }
    public int        getIdFreelancer()                 { return idFreelancer; }
    public void       setIdFreelancer(int v)            { this.idFreelancer = v; }
    public BigDecimal getMontoOfertado()                { return montoOfertado; }
    public void       setMontoOfertado(BigDecimal v)    { this.montoOfertado = v; }
    public int        getPlazoEntregaDias()             { return plazoEntregaDias; }
    public void       setPlazoEntregaDias(int v)        { this.plazoEntregaDias = v; }
    public String     getCartaPresentacion()            { return cartaPresentacion; }
    public void       setCartaPresentacion(String v)    { this.cartaPresentacion = v; }
    public String     getEstado()                       { return estado; }
    public void       setEstado(String v)               { this.estado = v; }
    public String     getNombreFreelancer()             { return nombreFreelancer; }
    public void       setNombreFreelancer(String v)     { this.nombreFreelancer = v; }
    public Double     getCalificacionPromedio()         { return calificacionPromedio; }
    public void       setCalificacionPromedio(Double v) { this.calificacionPromedio = v; }
    public String     getTituloProyecto()               { return tituloProyecto; }
    public void       setTituloProyecto(String v)       { this.tituloProyecto = v; }
}
