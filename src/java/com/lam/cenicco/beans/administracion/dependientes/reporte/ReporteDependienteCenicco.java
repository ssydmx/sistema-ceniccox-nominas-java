/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lam.cenicco.beans.administracion.dependientes.reporte;

import com.lam.cenicco.dao.ProcesoDAO;
import com.lam.cenicco.impl.ControladorWS;
import com.lam.cenicco.librerias.utilidades.CeniccoUtil;
import com.lam.cenicco.ws.VistaDependientes;
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.event.ActionEvent;

/**
 *
 * @author Antonio Durán
 */
public class ReporteDependienteCenicco implements ProcesoDAO<VistaDependientes> {

    protected List<VistaDependientes> dependientes;
    protected List<VistaDependientes> filtereddependientes;
//    
    protected Integer selectedgrupopago;
//    
    protected Integer selectedestatus;

    public ReporteDependienteCenicco() {
        if (this.dependientes == null) {
            this.dependientes = new ArrayList<>();
            this.filtereddependientes = new ArrayList<>();
        }
    }

    @Override
    public void consultar(ActionEvent event) {
        this.dependientes = ControladorWS.getInstance().findDependientes(this.selectedgrupopago, this.selectedestatus);
        this.filtereddependientes = this.dependientes;
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
    public void delete(VistaDependientes obj) {
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
        return CeniccoUtil.getInformacion(this.dependientes.size());
    }

    @Override
    public void init() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public List<VistaDependientes> getFiltereddependientes() {
        return filtereddependientes;
    }

    public void setFiltereddependientes(List<VistaDependientes> filtereddependientes) {
        this.filtereddependientes = filtereddependientes;
    }

    public Integer getSelectedgrupopago() {
        return selectedgrupopago;
    }

    public void setSelectedgrupopago(Integer selectedgrupopago) {
        this.selectedgrupopago = selectedgrupopago;
    }

    public Integer getSelectedestatus() {
        return selectedestatus;
    }

    public void setSelectedestatus(Integer selectedestatus) {
        this.selectedestatus = selectedestatus;
    }

    public List<VistaDependientes> getDependientes() {
        return dependientes;
    }
}
