/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lam.cenicco.util.poliza;

import com.lam.cenicco.ws.CifrasNomina;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Antonio Durán
 */
public class PolizaTO {
//

    private Integer id;
//        
    private String descripcion;
    private String concepto;
//        
    private String cco;
    private Double cargo;
    private Double abono;
//        
    private List<CifrasNomina> detalle;

    public PolizaTO(CifrasNomina v) {
//            
        this.id = v.getIdcifranomina();
//            
        this.concepto = v.getNumeroconcepto() + " - " + v.getNombreconcepto();
//            
        this.cco = v.getCentrocostros();
//            
        this.cargo = v.getCargo();
        this.abono = v.getAbono();
//            
        this.detalle = new ArrayList<>();
        this.detalle.add(v);
    }

    public PolizaTO(CifrasNomina v, int indice) {
        this.id = indice;
        this.cargo = v.getCargo();
        this.abono = v.getAbono();
    }
//        

    public void add(CifrasNomina v) {
        this.cargo += v.getCargo();
        this.abono += v.getAbono();
//            
        this.detalle.add(v);
    }
//        

    public void addcargo(double cargo) {
        this.cargo += cargo;
    }

    public void addabono(double abono) {
        this.abono += abono;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Double getCargo() {
        return cargo;
    }

    public Double getAbono() {
        return abono;
    }

    public Integer getId() {
        return id;
    }

    public List<CifrasNomina> getDetalle() {
        return detalle;
    }

    public String getConcepto() {
        return concepto;
    }

    public String getCco() {
        return cco;
    }

    public void setCco(String cco) {
        this.cco = cco;
    }

    public void setCargo(Double cargo) {
        this.cargo = cargo;
    }

    public void setAbono(Double abono) {
        this.abono = abono;
    }
}
