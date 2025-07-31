/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lam.cenicco.beans.nominas.reporteimpuestos;

import com.lam.cenicco.dao.ProcesoDAO;
import com.lam.cenicco.impl.ControladorWS;
import com.lam.cenicco.librerias.mensajes.Mensajes;
import com.lam.cenicco.librerias.utilidades.CeniccoUtil;
import com.lam.cenicco.util.ParametrosReportes;
import com.lam.cenicco.util.Util;
import com.lam.cenicco.util.paths.MyPaths;
import com.lam.cenicco.ws.VistaNominaMensual;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import org.primefaces.context.RequestContext;
import org.primefaces.event.TabChangeEvent;

/**
 *
 * @author Alejandro
 */
public class ReporteImpuestosCenicco implements ProcesoDAO<VistaNominaMensual> {

    protected List<VistaNominaMensual> registros;
    protected List<VistaNominaMensual> filteredRegistros;
    protected List<String> grupopago;
    protected Integer mesinicio;
    protected Integer mesfin;
    protected Integer anio;
    protected Integer idregistropatronal;
    protected String numeroempleado;
    protected Double totalTiempo;
    protected Double totalImporte;

    @PostConstruct
    @Override
    public void init() {
        this.limpiar(null);
    }

    @Override
    public void consultar(ActionEvent event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void consultarIsnGlobal(ActionEvent event) {
        if (this.anio == null) {
            FacesMessage msg = Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, Mensajes.CAMPO_VACIO_ANIO);
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } else {
            this.totalImporte = 0.0;
            this.totalTiempo = 0.0;
            String gp = join(", ", this.grupopago);

            this.registros = ControladorWS.getInstance().getReporteMensualIsnGlobal(gp, this.mesinicio, this.mesfin, this.anio);
            this.filteredRegistros = this.registros;

            Iterator<VistaNominaMensual> iter = this.registros.iterator();
            while (iter.hasNext()) {
                VistaNominaMensual v = iter.next();
                this.totalImporte += v.getTotalImporte();
                this.totalTiempo += v.getTotalTiempo();
            }
        }
    }

    public void consultarAcumuladoMensual(ActionEvent event) {
        if (this.anio == null) {
            FacesMessage msg = Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, Mensajes.CAMPO_VACIO_ANIO);
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } else {
            this.totalImporte = 0.0;
            this.totalTiempo = 0.0;
            String gp = join(", ", this.grupopago);

            this.registros = ControladorWS.getInstance().getReporteMensualEmpleadoConcepto(gp, this.mesinicio, this.mesfin, this.anio, this.idregistropatronal, this.numeroempleado);
            this.filteredRegistros = this.registros;

            Iterator<VistaNominaMensual> iter = this.registros.iterator();
            while (iter.hasNext()) {
                VistaNominaMensual v = iter.next();
                this.totalImporte += v.getTotalImporte();
                this.totalTiempo += v.getTotalTiempo();
            }
        }
    }

    public void consultarIsrRetenido(ActionEvent event) {
        if (this.anio == null) {
            FacesMessage msg = Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, Mensajes.CAMPO_VACIO_ANIO);
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } else {
            this.totalImporte = 0.0;
            this.totalTiempo = 0.0;
            String gp = join(", ", this.grupopago);

            this.registros = ControladorWS.getInstance().getReporteMensualEmpleadoIsrRetenido(gp, this.mesinicio, this.mesfin, this.anio);
            this.filteredRegistros = this.registros;

            Iterator<VistaNominaMensual> iter = this.registros.iterator();
            while (iter.hasNext()) {
                VistaNominaMensual v = iter.next();
                this.totalImporte += v.getTotalImporte();
                this.totalTiempo += v.getTotalTiempo();
            }
        }
    }

    @Override
    public void limpiar(ActionEvent event) {
        this.registros = new ArrayList<>();
        this.filteredRegistros = new ArrayList<>();
        this.grupopago = null;
        this.mesinicio = null;
        this.mesfin = null;
        this.anio = null;
        this.idregistropatronal = null;
        this.numeroempleado = null;
        this.totalImporte = 0.0;
        this.totalTiempo = 0.0;
    }

    public void descargarReporteEstadoIsnMensual() {
        String nombreArchivo = "ReporteMensualIsnGlobal";
        String gp = join(", ", this.grupopago);

        System.out.println("Descarga Reporte ReporteMensualIsnGlobal...");
        byte[] excel = ControladorWS.getInstance().getExcelReporteMensualIsnGlobal(this.registros, gp, this.mesinicio, this.mesfin, this.anio);
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_IMPUESTOS);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, nombreArchivo);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, excel);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_XLS);
        context.addCallbackParam("ruta", MyPaths.urlServletDescargarArchivoCsv());
    }

    public void descargarReporteMensualAcumuladosEmpleado() {
        String nombreArchivo = "ReporteMensualAcumuladosEmpleado";

        System.out.println("Descarga Reporte ReporteMensualAcumuladosEmpleado...");
        byte[] excel = ControladorWS.getInstance().getExcelReporteMensualAcumuladosEmpleado(this.registros);
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_IMPUESTOS);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, nombreArchivo);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, excel);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_XLS);
        context.addCallbackParam("ruta", MyPaths.urlServletDescargarArchivoCsv());
    }

    public void descargarReporteMensualBaseIsn() {
        String nombreArchivo = "ReporteMensualBaseIsn";

        System.out.println("Descarga Reporte ReporteMensualBaseIsn...");
        byte[] excel = ControladorWS.getInstance().getExcelReporteMensualBaseIsn(this.registros);
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_IMPUESTOS);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, nombreArchivo);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, excel);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_XLS);
        context.addCallbackParam("ruta", MyPaths.urlServletDescargarArchivoCsv());
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
    public void delete(VistaNominaMensual obj) {
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

    public List<VistaNominaMensual> getRegistros() {
        return registros;
    }

    public void setRegistros(List<VistaNominaMensual> registros) {
        this.registros = registros;
    }

    public List<VistaNominaMensual> getFilteredRegistros() {
        return filteredRegistros;
    }

    public void setFilteredRegistros(List<VistaNominaMensual> filteredRegistros) {
        this.filteredRegistros = filteredRegistros;
    }

    public List<String> getGrupopago() {
        return grupopago;
    }

    public void setGrupopago(List<String> grupopago) {
        this.grupopago = grupopago;
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

    public Integer getIdregistropatronal() {
        return idregistropatronal;
    }

    public void setIdregistropatronal(Integer idregistropatronal) {
        this.idregistropatronal = idregistropatronal;
    }

    public String getNumeroempleado() {
        return numeroempleado;
    }

    public void setNumeroempleado(String numeroempleado) {
        this.numeroempleado = numeroempleado;
    }

    public Double getTotalTiempo() {
        return totalTiempo;
    }

    public void setTotalTiempo(Double totalTiempo) {
        this.totalTiempo = totalTiempo;
    }

    public Double getTotalImporte() {
        return totalImporte;
    }

    public void setTotalImporte(Double totalImporte) {
        this.totalImporte = totalImporte;
    }

    public void onTabChange(TabChangeEvent event) {
        if (event.getTab().getId().equals("tab1")) {
            this.limpiar(null);
        } else if (event.getTab().getId().equals("tab2")) {
            this.limpiar(null);
        } else if (event.getTab().getId().equals("tab3")) {
            this.limpiar(null);
        }
    }

    private static String join(String separator, List<String> input) {
        if (input == null || input.size() <= 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.size(); i++) {
            sb.append(input.get(i));

            if (i != input.size() - 1) {
                sb.append(separator);
            }
        }

        return sb.toString();
    }
}
