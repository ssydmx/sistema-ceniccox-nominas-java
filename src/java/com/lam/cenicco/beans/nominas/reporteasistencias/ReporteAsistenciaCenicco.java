/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lam.cenicco.beans.nominas.reporteasistencias;

import com.lam.cenicco.dao.ProcesoDAO;
import com.lam.cenicco.impl.ControladorWS;
import com.lam.cenicco.librerias.utilidades.CeniccoUtil;
import com.lam.cenicco.util.ControladorSesiones;
import com.lam.cenicco.util.ParametrosReportes;
import com.lam.cenicco.util.paths.MyPaths;
import com.lam.cenicco.ws.ReporteAsistencia;
import com.mysql.jdbc.StringUtils;
import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.primefaces.context.RequestContext;

/**
 *
 * @author erick
 */
public class ReporteAsistenciaCenicco implements ProcesoDAO<ReporteAsistencia> {

    private String fechaInicio;
    private String periodo;
    private String numeroempleado;
    private String localidad;
    private String rancho;
    private String lote;
    private List<ReporteAsistencia> vista;
    private Integer numeroPeriodoInicial;
    private Integer numeroPeriodoFinal;
    private Integer anioPeriodo;

    @Override
    public void init() {
        this.vista = new ArrayList<>();
    }

    @Override
    public void consultar(ActionEvent event) {
        this.vista = new ArrayList<>();
        String token = ControladorWS.getInstance().getToken(null, ControladorSesiones.getInstance().getUsuarioSession());
        String strFechaInicio = null;
        if (!StringUtils.isNullOrEmpty(this.fechaInicio)) {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Date fInicio = null;
            try {
                fInicio = new SimpleDateFormat("dd/MM/yyyyy").parse(this.fechaInicio);
            } catch (ParseException ex) {
                Logger.getLogger(ReporteAsistenciaCenicco.class.getName()).log(Level.SEVERE, "Fecha ingresada incorrecta", ex);
            }
            strFechaInicio = df.format(fInicio);
        }
        List<ReporteAsistencia> aux = ControladorWS.getInstance().getReporteAsistencia(token, StringUtils.isNullOrEmpty(strFechaInicio) ? null : strFechaInicio, this.periodo.equals("") ? null : this.periodo, this.numeroempleado.equals("") ? null : this.numeroempleado,
                this.localidad.equals("") ? null : this.localidad, this.rancho.equals("") ? null : this.rancho, this.lote.equals("") ? null : this.lote);
        for (ReporteAsistencia r : aux) {
            r.setHoraEntrada(r.getHoraEntrada() == null ? "" : r.getHoraEntrada().substring(0, 19));
            r.setHoraSalida(r.getHoraSalida() == null ? "" : r.getHoraSalida().substring(0, 19));
            r.setFechaRegistro(r.getFechaRegistro() == null ? "" : r.getFechaRegistro().substring(0, 19));
            this.vista.add(r);
        }
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
    public void delete(ReporteAsistencia obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void listenerSelected() {
        String nombreArchivo = "REPORTE ASISTENCIA";
        String tipoarchivo = ParametrosReportes.ARCHIVO_XLS;
        HSSFWorkbook workbook = new HSSFWorkbook();
//        
        HSSFSheet sheet = workbook.createSheet("Asistencia");

        int i = 0;
        int j = 0;
        HSSFRow row = sheet.createRow(j++);
        HSSFCell cell = row.createCell(i++);
        cell.setCellValue("NUMERO EMPLEADO");
//        
        cell = row.createCell(i++);
        cell.setCellValue("NOMBRE");
//        
        cell = row.createCell(i++);
        cell.setCellValue("HORA DE ENTRADA");
//        
        cell = row.createCell(i++);
        cell.setCellValue("HORA DE SALIDA");
//
        cell = row.createCell(i++);
        cell.setCellValue("HORAS LABORADAS");
//        
        cell = row.createCell(i++);
        cell.setCellValue("PERIODO");
//
        cell = row.createCell(i++);
        cell.setCellValue("CONCEPTO");
//
        cell = row.createCell(i++);
        cell.setCellValue("RANCHO");
//
        cell = row.createCell(i++);
        cell.setCellValue("LOCALIDAD");
//
        cell = row.createCell(i++);
        cell.setCellValue("LOTE");
//
        cell = row.createCell(i++);
        cell.setCellValue("LLAVE");
//        
        cell = row.createCell(i++);
        cell.setCellValue("FECHA REGISTRO");
//        
        cell = row.createCell(i++);
        cell.setCellValue("ALIAS");

        Iterator<ReporteAsistencia> iter = this.vista.iterator();
        while (iter.hasNext()) {
            i = 0;
            ReporteAsistencia r = iter.next();

            row = sheet.createRow(j);
//          
            cell = row.createCell(i++);
            cell.setCellValue(r.getNumeroEmpleado());

            cell = row.createCell(i++);
            cell.setCellValue(r.getNombreEmpleado());

            cell = row.createCell(i++);
            cell.setCellValue("" + r.getHoraEntrada());

            cell = row.createCell(i++);
            cell.setCellValue("" + r.getHoraSalida());

            cell = row.createCell(i++);
            cell.setCellValue("" + r.getHorasLaboradas());

            cell = row.createCell(i++);
            cell.setCellValue("" + r.getPeriodo());

            cell = row.createCell(i++);
            cell.setCellValue("" + r.getConcepto());

            cell = row.createCell(i++);
            cell.setCellValue("" + r.getRancho());

            cell = row.createCell(i++);
            cell.setCellValue("" + r.getLocalidad());

            cell = row.createCell(i++);
            cell.setCellValue("" + r.getLote());

            cell = row.createCell(i++);
            cell.setCellValue("" + r.getLlave());

            cell = row.createCell(i++);
            cell.setCellValue("" + r.getFechaRegistro());

            cell = row.createCell(i++);
            cell.setCellValue("" + r.getAlias());

            j++;


        }
        this.escribirArchivo(workbook, nombreArchivo, tipoarchivo);
    }

    private void escribirArchivo(HSSFWorkbook book, String nombreArchivo, String tipoarchivo) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            book.write(baos);
            byte[] data = baos.toByteArray();
            RequestContext context = RequestContext.getCurrentInstance();
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, nombreArchivo);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, data);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, tipoarchivo);
            context.addCallbackParam("ruta", MyPaths.urlServletDescargarArchivoCsv());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openModalReporteAsistenciaImss(ActionEvent event) {
        this.numeroPeriodoInicial = null;
        this.numeroPeriodoFinal = null;
        this.anioPeriodo = null;

        RequestContext context = RequestContext.getCurrentInstance();
        context.update("fomrReporteAsistenciaImss");
        context.execute("PF('ReporteAsistenciaImss').show();");
    }

    public void generarReporteAsistenciaImss() {
        try {
            String token = ControladorWS.getInstance().getToken(null, ControladorSesiones.getInstance().getUsuarioSession());

            byte[] data = ControladorWS.getInstance().getReporteAsistenciaImss(token, numeroPeriodoInicial, numeroPeriodoFinal, anioPeriodo);

            String nombreArchivo = "Semana " + numeroPeriodoInicial + " a " + numeroPeriodoFinal + "_" + "Reporte_Asistencia_Imss";

            RequestContext context = RequestContext.getCurrentInstance();
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, nombreArchivo);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, data);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_XLSX);
            context.addCallbackParam("ruta", MyPaths.urlServletDescargarArchivoCsv());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void generarMsg(FacesMessage msg, boolean isValidate) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getInformacion() {
        if (this.vista != null) {
            return CeniccoUtil.getInformacion(this.vista.size());
        } else {
            return "Se encontraron: 0 Registro(s).";
        }
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public String getNumeroempleado() {
        return numeroempleado;
    }

    public void setNumeroempleado(String numeroempleado) {
        this.numeroempleado = numeroempleado;
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    public String getRancho() {
        return rancho;
    }

    public void setRancho(String rancho) {
        this.rancho = rancho;
    }

    public String getLote() {
        return lote;
    }

    public void setLote(String lote) {
        this.lote = lote;
    }

    public List<ReporteAsistencia> getVista() {
        return vista;
    }

    public void setVista(List<ReporteAsistencia> vista) {
        this.vista = vista;
    }

    public Integer getNumeroPeriodoInicial() {
        return numeroPeriodoInicial;
    }

    public void setNumeroPeriodoInicial(Integer numeroPeriodoInicial) {
        this.numeroPeriodoInicial = numeroPeriodoInicial;
    }

    public Integer getNumeroPeriodoFinal() {
        return numeroPeriodoFinal;
    }

    public void setNumeroPeriodoFinal(Integer numeroPeriodoFinal) {
        this.numeroPeriodoFinal = numeroPeriodoFinal;
    }

    public Integer getAnioPeriodo() {
        return anioPeriodo;
    }

    public void setAnioPeriodo(Integer anioPeriodo) {
        this.anioPeriodo = anioPeriodo;
    }
}
