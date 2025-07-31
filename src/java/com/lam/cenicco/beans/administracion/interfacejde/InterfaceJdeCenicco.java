/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lam.cenicco.beans.administracion.interfacejde;

import com.lam.cenicco.dao.ProcesoDAO;
import com.lam.cenicco.impl.ControladorWS;
import com.lam.cenicco.librerias.utilidades.CeniccoUtil;
import com.lam.cenicco.util.ParametrosReportes;
import com.lam.cenicco.util.Util;
import com.lam.cenicco.ws.VistaInterfaceJDE;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.event.ActionEvent;

/**
 *
 * @author Antonio Durán
 */
public class InterfaceJdeCenicco implements ProcesoDAO<VistaInterfaceJDE> {

    protected List<VistaInterfaceJDE> vista;
//    
    protected List<VistaInterfaceJDE> filteredVista;
//    
    protected String selectedCompania;
//    
    protected String selectedGrupoPago;
//    
    protected String selectedEstatus;

    @PostConstruct
    @Override
    public void init() {
        this.limpiar(null);
    }

    @Override
    public void consultar(ActionEvent event) {
        this.vista = ControladorWS.getInstance().findInterfaceJDE(this.selectedCompania, this.selectedGrupoPago, this.selectedEstatus);
        this.filteredVista = this.vista;
    }

    @Override
    public void limpiar(ActionEvent event) {
        this.vista = new ArrayList<>();
        this.filteredVista = new ArrayList<>();
//        
        this.selectedCompania = null;
        this.selectedEstatus = null;
        this.selectedGrupoPago = null;
    }

    public void escribirFichero(ActionEvent event) {
        List<String> lineas = new ArrayList<>();
        for (VistaInterfaceJDE v : this.vista) {
            lineas.add(v.getRegistro());
        }
        String nombrearchivo = "InterfaceJDE";
        Util.escribirFichero(lineas, nombrearchivo, ParametrosReportes.ARCHIVO_TXT);
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
    public void delete(VistaInterfaceJDE obj) {
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
        return CeniccoUtil.getInformacion(this.vista.size());
    }

    public List<VistaInterfaceJDE> getFilteredVista() {
        return filteredVista;
    }

    public void setFilteredVista(List<VistaInterfaceJDE> filteredVista) {
        this.filteredVista = filteredVista;
    }

    public String getSelectedCompania() {
        return selectedCompania;
    }

    public void setSelectedCompania(String selectedCompania) {
        this.selectedCompania = selectedCompania;
    }

    public String getSelectedGrupoPago() {
        return selectedGrupoPago;
    }

    public void setSelectedGrupoPago(String selectedGrupoPago) {
        this.selectedGrupoPago = selectedGrupoPago;
    }

    public String getSelectedEstatus() {
        return selectedEstatus;
    }

    public void setSelectedEstatus(String selectedEstatus) {
        this.selectedEstatus = selectedEstatus;
    }

    public List<VistaInterfaceJDE> getVista() {
        return vista;
    }
}