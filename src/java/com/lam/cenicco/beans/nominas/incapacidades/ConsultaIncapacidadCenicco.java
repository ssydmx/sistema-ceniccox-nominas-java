/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lam.cenicco.beans.nominas.incapacidades;

import com.lam.cenicco.dao.ProcesoDAO;
import com.lam.cenicco.impl.ControladorWS;
import com.lam.cenicco.librerias.utilidades.CeniccoUtil;
import com.lam.cenicco.util.Util;
import com.lam.cenicco.ws.Concepto;
import com.lam.cenicco.ws.VistaIncidencia;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import org.primefaces.context.RequestContext;

/**
 *
 * @author JoséAntonio
 */
public class ConsultaIncapacidadCenicco implements ProcesoDAO<VistaIncidencia> {

    protected List<VistaIncidencia> incidencias;
//    
    protected List<VistaIncidencia> filteredIncidencias;
//    
    protected Integer anio;
//    
    protected Integer selectedConcepto;
//    
    protected Integer selectedGrupopago;
//    
    protected Integer periodo;
//    
    protected String numeroempleado;
//    
    protected String folio;
//    
    protected String referencia;
//    
    protected List<Concepto> conceptos;
//
    protected List<String> referencias;

    public ConsultaIncapacidadCenicco() {
    }

    @PostConstruct
    @Override
    public void init() {
        if (this.conceptos == null) {
            this.conceptos = ControladorWS.getInstance().getConceptosIncapacidades();
        }
        this.limpiar(null);
    }

    public void changeIncapacidad(ValueChangeEvent e) {
        this.selectedConcepto = e.getNewValue() == null ? null : Integer.parseInt(e.getNewValue().toString());
        System.out.println("ConceptoSeleccionado... " + e.getNewValue() + " | " + this.selectedConcepto);
        if (this.selectedConcepto != null) {
            this.referencias = ControladorWS.getInstance().getComboReferenciaIncapacidades(this.selectedConcepto);
        }
    }

    @Override
    public void consultar(ActionEvent event) {
//        
        this.incidencias = ControladorWS.getInstance().findVistaIncidencias(anio, selectedConcepto, selectedGrupopago,
                periodo, numeroempleado, folio, referencia);
        this.filteredIncidencias = this.incidencias;
//        
    }

    @Override
    public void limpiar(ActionEvent event) {
        this.incidencias = new ArrayList<>();
        this.filteredIncidencias = this.incidencias;
//        
        this.anio = null;
        this.folio = null;
        this.numeroempleado = null;
        this.periodo = null;
        this.referencia = null;
        this.selectedConcepto = null;
        this.selectedGrupopago = null;
//        
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
    public void delete(VistaIncidencia obj) {
        boolean isValidate = ControladorWS.getInstance().deleteIncapacidad(obj);
        FacesMessage msg = isValidate ? Util.getFacesMessage(FacesMessage.SEVERITY_INFO, "Se eliminó la incapacidad Exitosamente")
                : Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, "Error al eliminar la incapacidad");
        this.generarMsg(msg, isValidate);
    }

    @Override
    public void listenerSelected() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void generarMsg(FacesMessage msg, boolean isValidate) {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().addMessage(null, msg);
        context.addCallbackParam("isValidate", isValidate);
        if(isValidate){
            this.consultar(null);
        }
    }

    @Override
    public String getInformacion() {
        return CeniccoUtil.getInformacion(this.incidencias.size());
    }

    public List<VistaIncidencia> getFilteredIncidencias() {
        return filteredIncidencias;
    }

    public void setFilteredIncidencias(List<VistaIncidencia> filteredIncidencias) {
        this.filteredIncidencias = filteredIncidencias;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public Integer getSelectedConcepto() {
        return selectedConcepto;
    }

    public void setSelectedConcepto(Integer selectedConcepto) {
        this.selectedConcepto = selectedConcepto;
    }

    public Integer getSelectedGrupopago() {
        return selectedGrupopago;
    }

    public void setSelectedGrupopago(Integer selectedGrupopago) {
        this.selectedGrupopago = selectedGrupopago;
    }

    public Integer getPeriodo() {
        return periodo;
    }

    public void setPeriodo(Integer periodo) {
        this.periodo = periodo;
    }

    public String getNumeroempleado() {
        return numeroempleado;
    }

    public void setNumeroempleado(String numeroempleado) {
        this.numeroempleado = numeroempleado;
    }

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public List<VistaIncidencia> getIncidencias() {
        return incidencias;
    }

    public List<Concepto> getConceptos() {
        return conceptos;
    }

    public List<String> getReferencias() {
        return referencias;
    }
}
