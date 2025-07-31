/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lam.cenicco.beans.nominas.sobregiros;

import com.lam.cenicco.dao.ProcesoDAO;
import com.lam.cenicco.impl.ControladorWS;
import com.lam.cenicco.librerias.utilidades.CeniccoUtil;
import com.lam.cenicco.ws.Sobregiro;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.event.ActionEvent;

/**
 *
 * @author Antonio Durán
 */
public class SobregirosCenicco implements ProcesoDAO<Sobregiro> {

    protected List<Sobregiro> sobregiros;
    protected List<Sobregiro> filteredSobregiros;
//    
    protected String numeroempleado;
//    
    protected Integer selectedIggrupopago;
//    
    protected Integer periodoinicio;
    protected Integer periodofin;
    protected Integer anio;

    @PostConstruct
    @Override
    public void init() {
        this.limpiar(null);
    }

    @Override
    public void limpiar(ActionEvent event) {
        this.sobregiros = new ArrayList<>();
        this.filteredSobregiros = new ArrayList<>();
//        
        this.selectedIggrupopago = null;
        this.periodoinicio = null;
        this.periodofin = null;
        this.anio = null;

    }

    @Override
    public void consultar(ActionEvent event) {
        this.sobregiros = ControladorWS.getInstance().findSobregiros(this.numeroempleado, this.selectedIggrupopago, this.periodoinicio, this.periodofin, this.anio);
        this.filteredSobregiros = this.sobregiros;
    }

    @Override
    public void create(ActionEvent event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void edit(ActionEvent event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void delete(Sobregiro obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void listenerSelected() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void generarMsg(FacesMessage msg, boolean isValidate) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getInformacion() {
        return CeniccoUtil.getInformacion(this.sobregiros.size());
    }

    public List<Sobregiro> getFilteredSobregiros() {
        return filteredSobregiros;
    }

    public void setFilteredSobregiros(List<Sobregiro> filteredSobregiros) {
        this.filteredSobregiros = filteredSobregiros;
    }

    public String getNumeroempleado() {
        return numeroempleado;
    }

    public void setNumeroempleado(String numeroempleado) {
        this.numeroempleado = numeroempleado;
    }

    public Integer getSelectedIggrupopago() {
        return selectedIggrupopago;
    }

    public void setSelectedIggrupopago(Integer selectedIggrupopago) {
        this.selectedIggrupopago = selectedIggrupopago;
    }

    public Integer getPeriodoinicio() {
        return periodoinicio;
    }

    public void setPeriodoinicio(Integer periodoinicio) {
        this.periodoinicio = periodoinicio;
    }

    public Integer getPeriodofin() {
        return periodofin;
    }

    public void setPeriodofin(Integer periodofin) {
        this.periodofin = periodofin;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public List<Sobregiro> getSobregiros() {
        return sobregiros;
    }
}
