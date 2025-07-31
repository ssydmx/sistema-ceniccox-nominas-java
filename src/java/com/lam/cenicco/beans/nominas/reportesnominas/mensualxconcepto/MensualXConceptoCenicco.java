/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lam.cenicco.beans.nominas.reportesnominas.mensualxconcepto;

import com.lam.cenicco.dao.ProcesoDAO;
import com.lam.cenicco.impl.ControladorWS;
import com.lam.cenicco.librerias.mensajes.Mensajes;
import com.lam.cenicco.librerias.utilidades.CeniccoUtil;
import com.lam.cenicco.util.ParametrosReportes;
import com.lam.cenicco.util.Util;
import com.lam.cenicco.util.paths.MyPaths;
import com.lam.cenicco.ws.VistaMensual;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import org.primefaces.context.RequestContext;

/**
 *
 * @author Antonio Durán
 */
public class MensualXConceptoCenicco implements ProcesoDAO<VistaMensual> {

    protected List<VistaMensual> registros;
//    
    protected List<VistaMensual> filteredRegistros;
//
    protected Integer idgrupopago;
    protected Integer mesinicio;
    protected Integer mesfin;
    protected Integer anio;
//    
    protected Double totalTiempo;
    protected Double totalImporte;
//    

    @PostConstruct
    @Override
    public void init() {
        this.limpiar(null);
    }

    @Override
    public void consultar(ActionEvent event) {
        System.out.println("ConsultaMesConcepto.... ");
//        
        if (this.anio == null) {
            FacesMessage msg = Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, Mensajes.CAMPO_VACIO_ANIO);
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } else {
            this.totalImporte = 0.0;
            this.totalTiempo = 0.0;
//        
            this.registros = ControladorWS.getInstance().getReporteMensualXConcepto(this.idgrupopago, this.mesinicio, this.mesfin, this.anio);
            this.filteredRegistros = this.registros;
//            
            Iterator<VistaMensual> iter = this.registros.iterator();
            while (iter.hasNext()) {
                VistaMensual v = iter.next();
                this.totalImporte += v.getTotalImporte();
                this.totalTiempo += v.getTotalTiempo();
            }
        }

    }

    @Override
    public void limpiar(ActionEvent event) {
        this.registros = new ArrayList<>();
        this.filteredRegistros = new ArrayList<>();
        this.idgrupopago = null;
        this.mesinicio = null;
        this.mesfin = null;
        this.anio = null;
        //        
        this.totalImporte = 0.0;
        this.totalTiempo = 0.0;
//   
    }

    public void descargarReporte() {

//        
        String nombrearchivo = "ReporteConceptoMensual";
//        
        System.out.println("Descarga Reporte ReporteConceptoMensual...");
        RequestContext context = RequestContext.getCurrentInstance();
//        
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_MENSUAL_CONCEPTO);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, nombrearchivo);

        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.registros);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.PARAMETRO_TOTAL_PERCEPCIONES, this.totalTiempo);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.PARAMETRO_TOTAL_DEDUCCIONES, this.totalImporte);
//
        context.addCallbackParam("ruta", MyPaths.urlServletReporte());

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
    public void delete(VistaMensual obj) {
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
        return CeniccoUtil.getInformacion(this.registros.size());
    }

    public List<VistaMensual> getFilteredRegistros() {
        return filteredRegistros;
    }

    public void setFilteredRegistros(List<VistaMensual> filteredRegistros) {
        this.filteredRegistros = filteredRegistros;
    }

    public List<VistaMensual> getRegistros() {
        return registros;
    }

    public Integer getIdgrupopago() {
        return idgrupopago;
    }

    public void setIdgrupopago(Integer idgrupopago) {
        this.idgrupopago = idgrupopago;
    }

    public Integer getMesinicio() {
        return mesinicio;
    }

    public void setMesinicio(Integer mesinicio) {
        this.mesinicio = mesinicio;
    }

    public Integer getMesfin() {
        return mesfin;
    }

    public void setMesfin(Integer mesfin) {
        this.mesfin = mesfin;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public Double getTotalTiempo() {
        return totalTiempo;
    }

    public Double getTotalImporte() {
        return totalImporte;
    }
}
