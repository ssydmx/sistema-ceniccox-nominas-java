/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lam.cenicco.beans.nominas.acumulados.consulta;

import com.lam.cenicco.dao.ProcesoDAO;
import com.lam.cenicco.impl.ControladorWS;
import com.lam.cenicco.librerias.mensajes.Mensajes;
import com.lam.cenicco.librerias.utilidades.CeniccoUtil;
import com.lam.cenicco.util.ParametrosReportes;
import com.lam.cenicco.util.Util;
import com.lam.cenicco.util.paths.MyPaths;
import com.lam.cenicco.ws.Acumulado;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import org.primefaces.context.RequestContext;

/**
 *
 * @author JoséAntonio
 */
public class AcumuladosCenicco implements ProcesoDAO<Acumulado> {

    protected List<Acumulado> acumulados;
//    
    protected List<Acumulado> filteredAcumulados;
//    
    protected Integer selectedIdGrupoPago;
//    
    protected Integer anio;
//    
    protected Integer mes;
//    
    protected String numeroEmpleado;

    public AcumuladosCenicco() {
    }

    @PostConstruct
    @Override
    public void init() {
        this.limpiar(null);
    }

    @Override
    public void consultar(ActionEvent event) {
        if (this.anio == null) {
            FacesMessage msg = Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, Mensajes.CAMPO_VACIO_ANIO);
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } else {
            this.acumulados = ControladorWS.getInstance().findAcumulados(this.selectedIdGrupoPago, this.mes, this.anio, this.numeroEmpleado);
            this.filteredAcumulados = this.acumulados;
        }
    }

    @Override
    public void limpiar(ActionEvent event) {
        this.acumulados = new ArrayList<>();
        this.filteredAcumulados = new ArrayList<>();
        this.selectedIdGrupoPago = null;
        this.anio = null;
        this.mes = null;
        this.numeroEmpleado = null;
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
    public void delete(Acumulado obj) {
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
        return CeniccoUtil.getInformacion(this.acumulados.size());
    }

    public List<Acumulado> getFilteredAcumulados() {
        return filteredAcumulados;
    }

    public void setFilteredAcumulados(List<Acumulado> filteredAcumulados) {
        this.filteredAcumulados = filteredAcumulados;
    }

    public Integer getSelectedIdGrupoPago() {
        return selectedIdGrupoPago;
    }

    public void setSelectedIdGrupoPago(Integer selectedIdGrupoPago) {
        this.selectedIdGrupoPago = selectedIdGrupoPago;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public String getNumeroEmpleado() {
        return numeroEmpleado;
    }

    public void setNumeroEmpleado(String numeroEmpleado) {
        this.numeroEmpleado = numeroEmpleado;
    }

    public List<Acumulado> getAcumulados() {
        return acumulados;
    }

    public void descargarReporteRotacionEmpleados() {
        String nombreArchivo = "ReporteRotacionEmpleados";

        System.out.println("Descarga Reporte ReporteRotacionEmpleados...");
        byte[] excel = ControladorWS.getInstance().getExcelReporteRotacionEmpleados(this.selectedIdGrupoPago, this.mes, this.anio, this.numeroEmpleado);

        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_MENSUAL_EMPLEADO_CONCEPTO);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, nombreArchivo);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, excel);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_XLS);
        context.addCallbackParam("ruta", MyPaths.urlServletDescargarArchivoCsv());
    }

    public void descargarReporteConsolidadoPrestaciones() {
        String nombreArchivo = "ReporteConsolidadoPrestaciones";

        System.out.println("Descarga Reporte ConsolidadoPrestaciones...");
        byte[] excel = ControladorWS.getInstance().getExcelReporteConsolidadoPrestaciones(this.selectedIdGrupoPago, this.mes, this.anio, this.numeroEmpleado);

        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_MENSUAL_EMPLEADO_CONCEPTO);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, nombreArchivo);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, excel);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_XLS);
        context.addCallbackParam("ruta", MyPaths.urlServletDescargarArchivoCsv());
    }
}
