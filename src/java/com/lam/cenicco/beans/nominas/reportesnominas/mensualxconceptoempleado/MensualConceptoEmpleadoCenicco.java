/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lam.cenicco.beans.nominas.reportesnominas.mensualxconceptoempleado;

import com.lam.cenicco.dao.ProcesoDAO;
import com.lam.cenicco.impl.ControladorWS;
import com.lam.cenicco.librerias.mensajes.Mensajes;
import com.lam.cenicco.librerias.utilidades.CeniccoUtil;
import com.lam.cenicco.util.ParametrosReportes;
import com.lam.cenicco.util.Util;
import com.lam.cenicco.util.paths.MyPaths;
import com.lam.cenicco.ws.VistaMensual;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
public class MensualConceptoEmpleadoCenicco implements ProcesoDAO<VistaMensual> {

    protected List<VistaMensual> registros;
//    
    protected List<VistaMensual> filteredRegistros;
//
//  protected Integer idgrupopago;
    protected Integer mesinicio;
    protected Integer mesfin;
    protected Integer anio;
//    
    protected Integer selectedRegistroPatronal;
//    
    protected String numeroempleado;
//    
    protected Double totalTiempo;
    protected Double totalImporte;
//    
    private List<String> lineas;
//
    protected List<String> grupopago;
    protected String selectedTipoConsulta = "0";

    @PostConstruct
    @Override
    public void init() {
        this.limpiar(null);
    }

    @Override
    public void consultar(ActionEvent event) {
//        
        System.out.println("ConsultaMesEmpleado... ");
        if (this.anio == null) {
            FacesMessage msg = Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, Mensajes.CAMPO_VACIO_ANIO);
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } else {
            this.lineas = new ArrayList<>();
            this.totalImporte = 0.0;
            this.totalTiempo = 0.0;
//        
            this.registros = ControladorWS.getInstance().getReporteMensualXConceptoEmpleado(Integer.parseInt(this.grupopago.get(0)), this.mesinicio, this.mesfin, this.anio,
                    this.numeroempleado, this.selectedRegistroPatronal);
            this.filteredRegistros = this.registros;
//          
            String linea = "No. Empleado,Empleado,S.D.,S.D.I.,Grupo Pago,Tipo Proceso,Concepto,Naturaleza,Año,Mes,Tiempo,Importe";
            this.lineas.add(linea);
//            
            Iterator<VistaMensual> iter = this.registros.iterator();
            while (iter.hasNext()) {
                VistaMensual v = iter.next();
                this.totalImporte += v.getTotalImporte();
                this.totalTiempo += v.getTotalTiempo();
//                
                linea = v.getNumeroempleado() + "," + v.getNombreempleado() + "," + v.getSd() + "," + v.getSdi() + "," + v.getGrupopago() + "-" + v.getNombregrupopago() + ","
                        + v.getTipoproceso() + "," + v.getNumeroconcepto() + "-" + v.getNombreconcepto() + "," + (v.getNaturaleza() == null ? "" : v.getNaturaleza()) + "," + v.getAnio() + "," + v.getMes()
                        + "," + v.getTotalTiempo() + "," + v.getTotalImporte();
                this.lineas.add(linea);
//                
            }
//            
            Collections.sort(registros, new Comparator<VistaMensual>() {
                @Override
                public int compare(VistaMensual p1, VistaMensual p2) {

                    int resultado = p1.getNumeroempleado().compareTo(p2.getNumeroempleado());
                    if (resultado != 0) {
                        return resultado;
                    }
                    resultado = p1.getTipoproceso().compareTo(p2.getTipoproceso());
                    if (resultado != 0) {
                        return resultado;
                    }
                    return p1.getNumeroconcepto().compareTo(p2.getNumeroconcepto());
                }
            });
        }

    }

    @Override
    public void limpiar(ActionEvent event) {
        this.registros = new ArrayList<>();
        this.filteredRegistros = new ArrayList<>();
        this.grupopago = new ArrayList<>();
        this.mesinicio = null;
        this.mesfin = null;
        this.anio = null;
        //        
        this.totalImporte = 0.0;
        this.totalTiempo = 0.0;
    }

    public void descargarCsv() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new OutputStreamWriter(baos));
            Iterator<String> iter = this.lineas.iterator();
            while (iter.hasNext()) {
                bw.write(iter.next());
                bw.newLine();
            }
            bw.flush();
            System.out.println("Escribio Correctamente el Archivo....");
//            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (Exception e) {
            }
        }
//        
        String nombrearchivo = "ReporteEmpleadoConceptoMensual_" + this.mesinicio + "_" + this.mesfin + "_" + this.anio;
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, nombrearchivo);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_CSV);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, baos.toByteArray());
        context.addCallbackParam("ruta", MyPaths.urlServletDescargarArchivoCsv());

    }

    public void descargarReporte() {

//        
        String nombrearchivo = "ReporteEmpleadoConceptoMensual_" + this.mesinicio + "_" + this.mesfin + "_" + this.anio;
//        
        System.out.println("Descarga Reporte ReporteEmpleadoConceptoMensual...");
        RequestContext context = RequestContext.getCurrentInstance();
//        
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_MENSUAL_EMPLEADO_CONCEPTO);
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
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().addMessage(null, msg);
        context.addCallbackParam("isValidate", isValidate);
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

    public String getNumeroempleado() {
        return numeroempleado;
    }

    public void setNumeroempleado(String numeroempleado) {
        this.numeroempleado = numeroempleado;
    }

    public Integer getSelectedRegistroPatronal() {
        return selectedRegistroPatronal;
    }

    public void setSelectedRegistroPatronal(Integer selectedRegistroPatronal) {
        this.selectedRegistroPatronal = selectedRegistroPatronal;
    }

    public List<String> getGrupopago() {
        return grupopago;
    }

    public void setGrupopago(List<String> grupopago) {
        this.grupopago = grupopago;
    }

    public String getSelectedTipoConsulta() {
        return selectedTipoConsulta;
    }

    public void setSelectedTipoConsulta(String selectedTipoConsulta) {
        this.selectedTipoConsulta = selectedTipoConsulta;
    }

    public void descargarReporteMensualCostoEmpresa() {
        String nombreArchivo = "ReporteMensualCostoEmpresa";

        System.out.println("Descarga Reporte ReporteMensualCostoEmpresa...");
        byte[] excel = ControladorWS.getInstance().getExcelReporteMensualCostoEmpresa(this.grupopago.get(0), this.mesinicio, this.mesfin, this.anio, this.selectedRegistroPatronal, this.numeroempleado);

        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_MENSUAL_EMPLEADO_CONCEPTO);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, nombreArchivo);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, excel);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_XLS);
        context.addCallbackParam("ruta", MyPaths.urlServletDescargarArchivoCsv());
    }

    public void descargarReporteMensualProvisionesCostoEmpresa() {
        String nombreArchivo = "ReporteMensualProvisionesCostoEmpresa";

        System.out.println("Descarga Reporte ReporteMensualProvisionesCostoEmpresa...");
        byte[] excel = ControladorWS.getInstance().getExcelReporteMensualProvisionesCostoEmpresa(this.grupopago.get(0), this.mesinicio, this.mesfin, this.anio, this.selectedRegistroPatronal, this.numeroempleado);

        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_MENSUAL_EMPLEADO_CONCEPTO);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, nombreArchivo);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, excel);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_XLS);
        context.addCallbackParam("ruta", MyPaths.urlServletDescargarArchivoCsv());
    }

    public void descargarReporteMensualCierreCostos() {
        String nombreArchivo = "ReporteMensualCierreCostos";

        System.out.println("Descarga Reporte ReporteMensualCierreCostos...");
        byte[] excel = ControladorWS.getInstance().getExcelReporteMensualCierreCostos(this.grupopago.get(0), null, this.mesinicio, this.mesfin, this.anio, this.numeroempleado);

        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_MENSUAL_EMPLEADO_CONCEPTO);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, nombreArchivo);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, excel);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_XLS);
        context.addCallbackParam("ruta", MyPaths.urlServletDescargarArchivoCsv());
    }

    public void descargarReporteNominaCostoEmpresa() {
        String nombreArchivo = "Nomina Costo Empresa - Mensual " + this.mesinicio + " a " + this.mesfin;

        System.out.println("Descarga Reporte ReporteNominaCostoEmpresa...");
        byte[] excel = ControladorWS.getInstance().getExcelReporteNominaCostoEmpresa(this.grupopago.get(0), null, null, this.mesinicio, this.mesfin, this.anio, this.numeroempleado);

        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_MENSUAL_EMPLEADO_CONCEPTO);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, nombreArchivo);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, excel);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_XLS);
        context.addCallbackParam("ruta", MyPaths.urlServletDescargarArchivoCsv());
    }

    public void descargarReporteNominaAcumuladoEmpleado() {
        String nombreArchivo = "Nomina Acumulado Empleado - Mensual " + this.mesinicio + " a " + this.mesfin;

        System.out.println("Descarga Reporte ReporteNominaAcumuladoEmpleado...");
        byte[] excel = ControladorWS.getInstance().getExcelReporteNominaAcumuladoEmpleado(this.grupopago.get(0), null, null, this.mesinicio, this.mesfin, this.anio, this.numeroempleado);

        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_LISTADO_NOMINAS);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, nombreArchivo);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, excel);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_XLS);
        context.addCallbackParam("ruta", MyPaths.urlServletDescargarArchivoCsv());
    }

    public void descargarReporteAnalisisIMSS() {
        String nombreArchivo = "Nomina Analisis IMSS - Mensual " + this.mesinicio + " a " + this.mesfin;

        System.out.println("Descarga Reporte AnalisisIMSS...");
        byte[] excel = ControladorWS.getInstance().getExcelReporteAnalisisIMSS(this.grupopago.get(0), null, null, this.mesinicio, this.mesfin, this.anio, this.numeroempleado);

        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_MENSUAL_EMPLEADO_CONCEPTO);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, nombreArchivo);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, excel);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_XLS);
        context.addCallbackParam("ruta", MyPaths.urlServletDescargarArchivoCsv());
    }

    public void descargaReporteNominaEmpresaBakertillyPeriodoMensual() {
        System.out.println("Descarga Reporte ReporteNominaEmpresaBakertillyPeriodoOMensual...");
        String gp = Util.join(", ", this.grupopago);
        String nombreArchivo = "";
        byte[] excel;
        if (this.selectedTipoConsulta.equalsIgnoreCase("0")) {
            nombreArchivo = "Acumulado Nomina - Mensual " + this.mesinicio + " a " + this.mesfin;
            excel = ControladorWS.getInstance().getExcelReporteNominaEmpresaBakertillyPeriodoMensual(gp, null, null, this.mesinicio, this.mesfin, this.anio, this.numeroempleado);
        } else {
            nombreArchivo = "Acumulado Nomina - Periodo " + this.mesinicio + " a " + this.mesfin;
            excel = ControladorWS.getInstance().getExcelReporteNominaEmpresaBakertillyPeriodo(gp, null, this.mesinicio, this.mesfin, this.anio, this.numeroempleado);
        }

        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_MENSUAL_EMPLEADO_CONCEPTO);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, nombreArchivo);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, excel);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_XLS);
        context.addCallbackParam("ruta", MyPaths.urlServletDescargarArchivoCsv());
    }
}
