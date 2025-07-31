/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lam.cenicco.beans.administracion.sueldos;

import com.lam.cenicco.dao.ProcesoDAO;
import com.lam.cenicco.impl.ControladorWS;
import com.lam.cenicco.librerias.utilidades.CeniccoUtil;
import com.lam.cenicco.ws.HistoricoSueldo;
import com.lam.cenicco.ws.VistaHistoricoSueldo;
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.event.ActionEvent;

/**
 *
 * @author Antonio Durán
 */
public class SueldosCenicco implements ProcesoDAO<HistoricoSueldo> {

    protected List<VistaHistoricoSueldo> historico;
//    
    protected List<VistaHistoricoSueldo> filteredHistorico;
//    
    protected Integer selectedGrupoPago;
//    
    protected Integer estatus;

    public SueldosCenicco() {
        if (this.historico == null) {
            this.historico = new ArrayList<>();
            this.filteredHistorico = this.historico;
        }
    }

    @Override
    public void consultar(ActionEvent event) {
        this.historico = ControladorWS.getInstance().findHistoricoSueldo(this.selectedGrupoPago, this.estatus);

        this.filteredHistorico = this.historico;
        System.out.println("HistoricoSueldos... " + this.historico.size());
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
    public void delete(HistoricoSueldo obj) {
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
        return CeniccoUtil.getInformacion(this.historico.size());
    }

    @Override
    public void init() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Integer getSelectedGrupoPago() {
        return selectedGrupoPago;
    }

    public void setSelectedGrupoPago(Integer selectedGrupoPago) {
        this.selectedGrupoPago = selectedGrupoPago;
    }

    public Integer getEstatus() {
        return estatus;
    }

    public void setEstatus(Integer estatus) {
        this.estatus = estatus;
    }

    public List<VistaHistoricoSueldo> getFilteredHistorico() {
        return filteredHistorico;
    }

    public void setFilteredHistorico(List<VistaHistoricoSueldo> filteredHistorico) {
        this.filteredHistorico = filteredHistorico;
    }

    public List<VistaHistoricoSueldo> getHistorico() {
        return historico;
    }
}
