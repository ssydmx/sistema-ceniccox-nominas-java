/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lam.cenicco.beans.nominas.reportesliquidaciones.consultacomprobantes;

/**
 *
 * @author Antonio Durán
 */

public class RespuestaTimbreTO {

//    private RegistroTimbre registro;
    private String version;
    private String descripcion;
    private String fechainicio;
    private String fechafin;
    private String numeroempleado;
    private String nss;
    private double totalpercepciones;
    private double totaldeducciones;

    public double getTotalpercepciones() {
        return totalpercepciones;
    }

    public void setTotalpercepciones(double totalpercepciones) {
        this.totalpercepciones = totalpercepciones;
    }

    public double getTotaldeducciones() {
        return totaldeducciones;
    }

    public void setTotaldeducciones(double totaldeducciones) {
        this.totaldeducciones = totaldeducciones;
    }

    public String getNss() {
        return nss;
    }

    public void setNss(String nss) {
        this.nss = nss;
    }

    public String getNumeroempleado() {
        return numeroempleado;
    }

    public void setNumeroempleado(String numeroempleado) {
        this.numeroempleado = numeroempleado;
    }

    public String getFechainicio() {
        return fechainicio;
    }

    public void setFechainicio(String fechainicio) {
        this.fechainicio = fechainicio;
    }

    public String getFechafin() {
        return fechafin;
    }

    public void setFechafin(String fechafin) {
        this.fechafin = fechafin;
    }

//    public RespuestaTimbreTO(RegistroTimbre registro) {
//        this.registro = registro;
//    }
//
//    public RegistroTimbre getRegistro() {
//        return registro;
//    }

//    public void setRegistro(RegistroTimbre registro) {
//        this.registro = registro;
//    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
