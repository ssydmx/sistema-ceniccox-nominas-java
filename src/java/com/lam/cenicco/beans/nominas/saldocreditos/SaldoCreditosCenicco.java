/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lam.cenicco.beans.nominas.saldocreditos;

import com.lam.cenicco.dao.ProcesoDAO;
import com.lam.cenicco.impl.ControladorWS;
import com.lam.cenicco.librerias.utilidades.CeniccoUtil;
import com.lam.cenicco.ws.VistaSaldoCreditos;
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.event.ActionEvent;

/**
 *
 * @author Antonio Durán
 */
public class SaldoCreditosCenicco implements ProcesoDAO<VistaSaldoCreditos> {
//    

    protected List<VistaSaldoCreditos> saldos;
//    
    protected List<VistaSaldoCreditos> filteredsaldos;
//    
    protected Integer selectedgrupopago;

    //
    public SaldoCreditosCenicco() {
//        
        if (this.saldos == null) {
            this.saldos = new ArrayList<>();
            this.filteredsaldos = this.saldos;
        }
//        
    }

    @Override
    public void consultar(ActionEvent event) {
        this.saldos = ControladorWS.getInstance().findSaldoCreditos(this.selectedgrupopago);
        this.filteredsaldos = this.saldos;
    }

    @Override
    public void limpiar(ActionEvent event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
    public void delete(VistaSaldoCreditos obj) {
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
    public void init() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public List<VistaSaldoCreditos> getFilteredsaldos() {
        return filteredsaldos;
    }

    public void setFilteredsaldos(List<VistaSaldoCreditos> filteredsaldos) {
        this.filteredsaldos = filteredsaldos;
    }

    public Integer getSelectedgrupopago() {
        return selectedgrupopago;
    }

    public void setSelectedgrupopago(Integer selectedgrupopago) {
        this.selectedgrupopago = selectedgrupopago;
    }

    public List<VistaSaldoCreditos> getSaldos() {
        return saldos;
    }

    @Override
    public String getInformacion() {
        return CeniccoUtil.getInformacion(this.saldos.size());
    }
}
