/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.connectwork.model;

import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author braya
 */
public class PerfilFreelancer {
    private int        idUsuario;
    private String     biografia;
    private String     nivelExperiencia; // Junior | Semi-Senior | Senior
    private BigDecimal tarifaHora;
    private List<Integer> habilidades;   // ids del catálogo

    public int        getIdUsuario()                   { return idUsuario; }
    public void       setIdUsuario(int v)              { this.idUsuario = v; }
    public String     getBiografia()                   { return biografia; }
    public void       setBiografia(String v)           { this.biografia = v; }
    public String     getNivelExperiencia()            { return nivelExperiencia; }
    public void       setNivelExperiencia(String v)    { this.nivelExperiencia = v; }
    public BigDecimal getTarifaHora()                  { return tarifaHora; }
    public void       setTarifaHora(BigDecimal v)      { this.tarifaHora = v; }
    public List<Integer> getHabilidades()              { return habilidades; }
    public void       setHabilidades(List<Integer> v)  { this.habilidades = v; }
}
