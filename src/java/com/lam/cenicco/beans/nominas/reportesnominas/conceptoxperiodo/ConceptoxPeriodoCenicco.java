/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lam.cenicco.beans.nominas.reportesnominas.conceptoxperiodo;

import com.lam.cenicco.dao.ProcesoDAO;
import com.lam.cenicco.impl.ControladorWS;
import com.lam.cenicco.librerias.utilidades.CeniccoUtil;
import com.lam.cenicco.util.ParametrosReportes;
import com.lam.cenicco.util.paths.MyPaths;
import com.lam.cenicco.ws.Concepto;
import com.lam.cenicco.ws.GrupoPago;
import com.lam.cenicco.ws.Periodo;
import com.lam.cenicco.ws.TipoProceso;
import com.lam.cenicco.ws.VistaConceptosNomina;
import com.lam.cenicco.ws.VistaNominaMensual;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import org.primefaces.context.RequestContext;
import org.primefaces.event.TabChangeEvent;

/**
 *
 * @author JoséAntonio
 */
public class ConceptoxPeriodoCenicco implements ProcesoDAO<VistaConceptosNomina> {

    protected List<VistaConceptosNomina> reporte;
//    
    protected List<VistaConceptosNomina> filteredReporte;
//    
    protected List<String> conceptos;
//    
    protected String[] selectedConceptos;
//    
    protected Integer idgrupoapago;
//    
    protected Integer periodoinicio;
//    
    protected Integer periodofin;
//    
    protected Integer anio;
//
    protected List<Concepto> listConceptos;
//    
    protected List<String> selectedListConceptos;
//
    protected Integer grupoapagoComparar;
//
    protected Integer tipoProcesoComparar;
//
    protected Integer periodo1Comparar;
//
    protected Integer anio1Comparar;
//
    protected Integer periodo2Comparar;
//
    protected Integer anio2Comparar;
//
    protected Integer numeroEmpleado;
//
    protected String nombre;
//
    protected String apellidoP;
//
    protected String apellidoM;
//
    protected boolean validarTab1 = true;
//
    protected boolean validarTab2 = true;
//
    protected boolean validarTab3 = true;
//
    protected List<VistaNominaMensual> nominas;
//    
    protected List<VistaNominaMensual> filteredNominas;

    @PostConstruct
    @Override
    public void init() {
        this.limpiar(null);
    }

    @Override
    public void consultar(ActionEvent event) {
        this.reporte = ControladorWS.getInstance().getReporteConceptoXPeriodo(this.selectedConceptos, this.idgrupoapago,
                this.periodoinicio, this.periodofin, this.anio);
        this.filteredReporte = this.reporte;
    }

    public void consultarComparativa(ActionEvent event) {
        this.nominas = ControladorWS.getInstance().getReporteNominaComparativaByConceptoConcentrado(this.grupoapagoComparar, null, this.periodo1Comparar, this.anio1Comparar, this.periodo2Comparar, this.anio2Comparar, this.selectedListConceptos);
        this.filteredNominas = this.nominas;
    }

    public void consultarComparativaEmpleado(ActionEvent event) {
        this.nominas = ControladorWS.getInstance().getReporteNominaComparativaByConceptoConcentradoEmpleado(this.tipoProcesoComparar, this.periodo1Comparar, this.anio1Comparar, this.periodo2Comparar, this.anio2Comparar, null, null, null, null, null);
        this.filteredNominas = this.nominas;
    }

    @Override
    public void limpiar(ActionEvent event) {
//        
        this.selectedConceptos = null;
//        
        List<Concepto> aux = ControladorWS.getInstance().getConceptosReporte();
//
        listConceptos = new ArrayList<>();
        listConceptos.addAll(aux);
        nominas = new ArrayList<>();
        filteredNominas = new ArrayList<>();
        selectedListConceptos = new ArrayList<>();
        this.grupoapagoComparar = null;
        this.periodo1Comparar = null;
        this.anio1Comparar = null;
        this.periodo2Comparar = null;
        this.anio2Comparar = null;
        this.tipoProcesoComparar = null;
//
        this.conceptos = new ArrayList<>();
        for (Concepto c : aux) {
            String llave = c.getConcepto() + " - " + c.getNombre();
            this.conceptos.add(llave);
        }
//        
        this.reporte = new ArrayList<>();
        this.filteredReporte = new ArrayList<>();
//        
        this.idgrupoapago = null;
        this.periodoinicio = null;
        this.periodofin = null;
        this.anio = null;

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
    public void delete(VistaConceptosNomina obj) {
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
        return CeniccoUtil.getInformacion(this.reporte.size());
    }

    public List<VistaConceptosNomina> getFilteredReporte() {
        return filteredReporte;
    }

    public void setFilteredReporte(List<VistaConceptosNomina> filteredReporte) {
        this.filteredReporte = filteredReporte;
    }

    public Integer getIdgrupoapago() {
        return idgrupoapago;
    }

    public void setIdgrupoapago(Integer idgrupoapago) {
        this.idgrupoapago = idgrupoapago;
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

    public List<VistaConceptosNomina> getReporte() {
        return reporte;
    }

    public String[] getSelectedConceptos() {
        return selectedConceptos;
    }

    public void setSelectedConceptos(String[] selectedConceptos) {
        this.selectedConceptos = selectedConceptos;
    }

    public List<String> getConceptos() {
        return conceptos;
    }

    public List<VistaNominaMensual> getNominas() {
        return nominas;
    }

    public void setNominas(List<VistaNominaMensual> nominas) {
        this.nominas = nominas;
    }

    public List<VistaNominaMensual> getFilteredNominas() {
        return filteredNominas;
    }

    public void setFilteredNominas(List<VistaNominaMensual> filteredNominas) {
        this.filteredNominas = filteredNominas;
    }

    public List<Concepto> getListConceptos() {
        return listConceptos;
    }

    public void setListConceptos(List<Concepto> listConceptos) {
        this.listConceptos = listConceptos;
    }

    public List<String> getSelectedListConceptos() {
        return selectedListConceptos;
    }

    public void setSelectedListConceptos(List<String> selectedListConceptos) {
        this.selectedListConceptos = selectedListConceptos;
    }

    public Integer getGrupoapagoComparar() {
        return grupoapagoComparar;
    }

    public void setGrupoapagoComparar(Integer grupoapagoComparar) {
        this.grupoapagoComparar = grupoapagoComparar;
    }

    public Integer getTipoProcesoComparar() {
        return tipoProcesoComparar;
    }

    public void setTipoProcesoComparar(Integer tipoProcesoComparar) {
        this.tipoProcesoComparar = tipoProcesoComparar;
    }

    public Integer getPeriodo1Comparar() {
        return periodo1Comparar;
    }

    public void setPeriodo1Comparar(Integer periodo1Comparar) {
        this.periodo1Comparar = periodo1Comparar;
    }

    public Integer getAnio1Comparar() {
        return anio1Comparar;
    }

    public void setAnio1Comparar(Integer anio1Comparar) {
        this.anio1Comparar = anio1Comparar;
    }

    public Integer getPeriodo2Comparar() {
        return periodo2Comparar;
    }

    public void setPeriodo2Comparar(Integer periodo2Comparar) {
        this.periodo2Comparar = periodo2Comparar;
    }

    public Integer getAnio2Comparar() {
        return anio2Comparar;
    }

    public void setAnio2Comparar(Integer anio2Comparar) {
        this.anio2Comparar = anio2Comparar;
    }

    public boolean isValidarTab1() {
        return validarTab1;
    }

    public void setValidarTab1(boolean validarTab1) {
        this.validarTab1 = validarTab1;
    }

    public boolean isValidarTab2() {
        return validarTab2;
    }

    public void setValidarTab2(boolean validarTab2) {
        this.validarTab2 = validarTab2;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidoP() {
        return apellidoP;
    }

    public void setApellidoP(String apellidoP) {
        this.apellidoP = apellidoP;
    }

    public String getApellidoM() {
        return apellidoM;
    }

    public void setApellidoM(String apellidoM) {
        this.apellidoM = apellidoM;
    }

    public boolean isValidarTab3() {
        return validarTab3;
    }

    public void setValidarTab3(boolean validarTab3) {
        this.validarTab3 = validarTab3;
    }

    public void onTabChange(TabChangeEvent event) {
        this.limpiar(null);
        if (event.getTab().getId().equals("tab1")) {
            this.validarTab1 = true;
            this.validarTab2 = false;
        } else if (event.getTab().getId().equals("tab2")) {
            this.validarTab1 = false;
            this.validarTab2 = true;
        } else if (event.getTab().getId().equals("tab3")) {
            this.validarTab1 = false;
            this.validarTab2 = false;
            this.validarTab3 = true;
        }
        FacesContext.getCurrentInstance().renderResponse();
    }

    public void descargarExcelReporteNominaComparativaByConceptoConcentrado() {
        String nombreArchivo = "ReporteNominaComparativaConceptoConcentrado";

        System.out.println("Descarga Reporte ReporteNominaComparativaConceptoConcentrado...");
        byte[] excel = ControladorWS.getInstance().getExcelReporteNominaComparativaByConceptoConcentrado(this.grupoapagoComparar, null, this.periodo1Comparar, this.anio1Comparar, this.periodo2Comparar, this.anio2Comparar, this.selectedListConceptos);

        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_MENSUAL_EMPLEADO_CONCEPTO);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, nombreArchivo);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, excel);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_XLS);
        context.addCallbackParam("ruta", MyPaths.urlServletDescargarArchivoCsv());
    }

    public void descargarExcelReporteNominaComparativaByConceptoConcentradoEmpleado() {
        String nombreArchivo = "ReporteNominaComparativaConceptoConcentradoEmpleado.xls";

        TipoProceso idtp = ControladorWS.getInstance().findTipoProcesoById(this.tipoProcesoComparar);
        Periodo periodo1 = ControladorWS.getInstance().getPeriodoByAnioPeriodoTipoProceso(this.anio1Comparar, this.periodo1Comparar, idtp.getTipoproceso());
        Periodo periodo2 = ControladorWS.getInstance().getPeriodoByAnioPeriodoTipoProceso(this.anio2Comparar, this.periodo2Comparar, idtp.getTipoproceso());

        byte[] excel = ControladorWS.getInstance().getExcelReporteNominaComparativaByConceptoConcentradoEmpleado(this.tipoProcesoComparar, this.periodo1Comparar, this.anio1Comparar, this.periodo2Comparar, this.anio2Comparar,
                null, null, null, null, null);

        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_MENSUAL_EMPLEADO_CONCEPTO);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, nombreArchivo);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, excel);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_XLS);
        context.addCallbackParam("ruta", MyPaths.urlServletDescargarArchivoCsv());
    }

    public void descargarPdfReporteNominaComparativaByConceptoConcentrado() {
        GrupoPago gp = ControladorWS.getInstance().findGrupoPagoById(this.grupoapagoComparar);
        Periodo periodo1 = ControladorWS.getInstance().getPeriodoByAnioPeriodoTipoProceso(this.anio1Comparar, this.periodo1Comparar, gp.getIdTipoproceso().getTipoproceso());
        Periodo periodo2 = ControladorWS.getInstance().getPeriodoByAnioPeriodoTipoProceso(this.anio2Comparar, this.periodo2Comparar, gp.getIdTipoproceso().getTipoproceso());

        String nombreArchivo = "ReporteNominaComparativaConceptoConcentrado";
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.REPORTE_NOMINA_COMPARATIVA);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, nombreArchivo);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_PDF);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.nominas);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.PARAMETRO_GRUPO_PAGO, gp);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.PARAMETRO_PERIODO + "1", periodo1);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.PARAMETRO_PERIODO + "2", periodo2);
        context.addCallbackParam("ruta", MyPaths.urlServletReporte());
    }
}
