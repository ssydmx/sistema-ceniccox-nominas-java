/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lam.cenicco.beans.nominas.finiquitos;

import com.lam.cenicco.dao.ProcesoDAO;
import com.lam.cenicco.impl.ControladorWS;
import com.lam.cenicco.librerias.mensajes.Mensajes;
import com.lam.cenicco.librerias.utilidades.CeniccoUtil;
import com.lam.cenicco.mail.ManejadorCorreo;
import com.lam.cenicco.util.ControladorSesiones;
import com.lam.cenicco.util.ParametrosReportes;
import com.lam.cenicco.util.Util;
import com.lam.cenicco.util.paths.MyPaths;
import com.lam.cenicco.ws.Concepto;
import com.lam.cenicco.ws.Finiquito;
import com.lam.cenicco.ws.GrupoConcepto;
import com.lam.cenicco.ws.GrupoPago;
import com.lam.cenicco.ws.RelacionLaboral;
import com.lam.cenicco.ws.RelacionLaboralPosicion;
import com.lam.cenicco.ws.VistaAcumuladoConceptos;
import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.primefaces.context.RequestContext;

/**
 *
 * @author JoséAntonio
 */
public class FiniquitoCenicco implements ProcesoDAO<Finiquito> {

    protected List<GrupoConcepto> tiposFiniquitos;
//    
    protected List<Finiquito> finiquitos;
    protected List<Finiquito> filteredFiniquitos;
//
    protected List<Finiquito> selectedFiniquitos;
//
    protected List<Concepto> conceptosReportados;
    protected List<Concepto> filteredConceptosReportados;
    protected List<Concepto> selectedConceptos;
//
    protected Finiquito finiquito;
//
    protected Map<String, RelacionLaboral> mapaRelacionesLaborales;
//    
    protected List<String> relaciones;
//    
    protected String[] selectedRelaciones;
//    
    protected Integer selectedGrupoPago;
//    
    protected Integer mesaaplicar;
    protected Integer anioaaplicar;
//    
    protected String fechaPago;
//    
    protected Integer selectedTipoFiniquito;
//    
    protected String selectedCausaBaja;
//    
    protected String selectedTipoSalario;
//    
    private ManejadorCorreo manejadorCorreo;
//    
    private Integer periodonomina;
//    
    private Integer aniononimina;

    public FiniquitoCenicco() {
        if (this.relaciones == null) {
            this.relaciones = new ArrayList<>();
        }
        if (this.finiquito == null) {
            this.finiquito = new Finiquito();
        }
        if (this.finiquitos == null) {
            this.finiquitos = new ArrayList<>();
        }
        if (this.manejadorCorreo == null) {
            this.manejadorCorreo = new ManejadorCorreo();
        }
        if (this.conceptosReportados == null) {
            this.conceptosReportados = new ArrayList<>();
        }
        if (this.tiposFiniquitos == null) {
            this.tiposFiniquitos = ControladorWS.getInstance().getTiposFiniquitos();
        }
    }

    @Override
    public void init() {
    }

    private void updateEmpleados(Integer idGrupoPago) {
        this.selectedRelaciones = null;
//        
        this.mapaRelacionesLaborales = new HashMap<>();
//
        List<RelacionLaboral> relacionesAux = ControladorWS.getInstance().findRelacionesLaboralesByFiltrosDates(idGrupoPago, null, null, null, null, null);
//        

        this.relaciones = new ArrayList<>();
//        
        for (RelacionLaboral rl : relacionesAux) {
            String apellidoMaterno = rl.getIdempleado().getApellidomaterno() == null ? null : rl.getIdempleado().getApellidomaterno();
            String llave = rl.getNumeroempleado() + " - " + rl.getIdempleado().getNombre() + " " + rl.getIdempleado().getApellidopaterno();
            if (rl.getIdempleado().getApellidomaterno() != null) {
                llave = llave + " " + apellidoMaterno;
            }
            System.out.println("Llave.... " + llave);
            if (this.mapaRelacionesLaborales.get(llave) == null) {
                this.mapaRelacionesLaborales.put(llave, rl);
                this.relaciones.add(llave);
            }
        }
//        
    }

    public void changeGrupoPago(ValueChangeEvent e) {
        this.selectedGrupoPago = e.getNewValue() == null ? null : Integer.parseInt(e.getNewValue().toString());
        this.updateEmpleados(this.selectedGrupoPago);
    }

    @Override
    public void consultar(ActionEvent event) {
    }

    @Override
    public void limpiar(ActionEvent event) {
//
        this.finiquitos = new ArrayList<>();
        this.filteredFiniquitos = new ArrayList<>();
//        
        this.selectedTipoFiniquito = null;
        this.selectedCausaBaja = null;
        this.selectedTipoSalario = "0";
    }

    @Override
    public void create(ActionEvent event) {
        this.selectedFiniquitos = null;

        FacesMessage msg = this.createFiniquitos();

        this.generarMsg(msg, FacesMessage.SEVERITY_INFO.equals(msg.getSeverity()));
    }

    public void createIncidencias(ActionEvent event) {
        FacesMessage msg = this.validarCampos();
        if (msg.getSeverity().equals(FacesMessage.SEVERITY_INFO)) {
            for (Finiquito n : this.selectedFiniquitos) {
////                
                Iterator<Concepto> iter = n.getConceptos().iterator();
//                
                while (iter.hasNext()) {
                    Concepto c = iter.next();
                    if (c.getReportado() == 1) {
                        iter.remove();
                    }
                }
////                
            }
        }
        this.generarMsg(msg, FacesMessage.SEVERITY_INFO.equals(msg.getSeverity()));
    }

    public void agregarConceptosReportados(ActionEvent event) {
        for (Finiquito n : this.selectedFiniquitos) {
            n.getConceptos().addAll(this.selectedConceptos);
        }
        this.finiquitos = ControladorWS.getInstance().reCalcularFiniquitos(this.finiquitos, this.fechaPago);
    }

    private FacesMessage createFiniquitos() {
//
        if (this.selectedTipoSalario == null) {
            this.selectedTipoSalario = "0";
        }
//        
        System.out.println("SelectedTipoSalario.... " + this.selectedTipoSalario);
//        
        List<RelacionLaboral> relacionesAux = new ArrayList<>();
//
        for (String llave : this.selectedRelaciones) {
            relacionesAux.add(this.mapaRelacionesLaborales.get(llave));
        }
//        
        this.finiquitos = ControladorWS.getInstance().calcularFiniquitos(relacionesAux, this.fechaPago, this.selectedTipoFiniquito, this.selectedCausaBaja,
                ControladorSesiones.getInstance().getUsuarioSession(), this.selectedTipoSalario, this.periodonomina, this.aniononimina);
//        
        FacesMessage msg = !this.finiquitos.isEmpty() ? Util.getFacesMessage(FacesMessage.SEVERITY_INFO, Mensajes.EXITOSO_CALCULO_FINIQUITOS)
                : Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, Mensajes.ERROR_CALCULO_FINIQUITOS);
//
        return msg;
    }

    private FacesMessage validarCampos() {
        if (this.selectedFiniquitos.isEmpty() && this.selectedFiniquitos != null) {
            return Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, Mensajes.ERROR_REGSITROS_VACIOS_SOLICITUD_VACACIONES);
        }
        this.conceptosReportados = ControladorWS.getInstance().getConceptosFiniquitos();
        this.filteredConceptosReportados = this.conceptosReportados;
        this.selectedConceptos = null;
        //       
        return !this.conceptosReportados.isEmpty() ? Util.getFacesMessage(FacesMessage.SEVERITY_INFO, "Puede Registrar Incidencias")
                : Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, "No se encontraron Conceptos para Reportar");
    }

    public void descargarDocumentoXls(Finiquito f) {
        List<VistaAcumuladoConceptos> vista = ControladorWS.getInstance().getVariablesIndemnizacion(f.getRelacionlaboral().getIdrellab(), this.fechaPago);
        System.out.println("VistaVariables... " + vista.size());
//        
        String nombreArchivo = "VariablesIndemnizacion_" + f.getRelacionlaboral().getNumeroempleado() + "_" + f.getRelacionlaboral().getIdempleado().getRfc();
//        
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet(nombreArchivo);
//        
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setFontName(HSSFFont.FONT_ARIAL);
//        
        CellStyle style = workbook.createCellStyle();
        style.setFont(font);
//        
        int j = 0;
        int i = 0;
//        
        HSSFRow row = sheet.createRow(j);
//        
        HSSFCell cell = row.createCell(i);
        cell.setCellValue("NUMERO EMPLEADO");
        cell.setCellStyle(style);
        i++;
//        
        cell = row.createCell(i);
        cell.setCellValue("NOMBRE DEL TRABAJADOR");
        cell.setCellStyle(style);
        i++;
//      
        cell = row.createCell(i);
        cell.setCellValue("ANIO");
        cell.setCellStyle(style);
        i++;
//        
        cell = row.createCell(i);
        cell.setCellValue("MES");
        cell.setCellStyle(style);
        i++;
//        
        cell = row.createCell(i);
        cell.setCellValue("PERIODO");
        cell.setCellStyle(style);
        i++;
//        
        cell = row.createCell(i);
        cell.setCellValue("TIPO PROCESO");
        cell.setCellStyle(style);
        i++;
//        
        cell = row.createCell(i);
        cell.setCellValue("NO. CONCEPTO");
        cell.setCellStyle(style);
        i++;
//        
        cell = row.createCell(i);
        cell.setCellValue("CONCEPTO");
        cell.setCellStyle(style);
        i++;
//        
        cell = row.createCell(i);
        cell.setCellValue("IMPORTE");
        cell.setCellStyle(style);

        j++;
//      
        String nombre = Util.getNombre(f.getRelacionlaboral().getIdempleado());

//        
        for (VistaAcumuladoConceptos v : vista) {
            i = 0;
//
            row = sheet.createRow(j);
//            
            cell = row.createCell(i);
            cell.setCellValue(f.getRelacionlaboral().getNumeroempleado());
            i++;
//            
            cell = row.createCell(i);
            cell.setCellValue(nombre);
            i++;
//            
            cell = row.createCell(i);
            cell.setCellValue(v.getAnio());
            i++;
//            
            cell = row.createCell(i);
            cell.setCellValue(v.getMes());
            i++;
//            
            cell = row.createCell(i);
            cell.setCellValue(v.getPeriodo());
            i++;
//            
            cell = row.createCell(i);
            cell.setCellValue(v.getTipoproceso());
            i++;
//            
            cell = row.createCell(i);
            cell.setCellValue(v.getNumeroconcepto());
            i++;
//            
            cell = row.createCell(i);
            cell.setCellValue(v.getNombreconcepto());
            i++;
//            
            cell = row.createCell(i);
            cell.setCellValue(v.getImporte());
//            
            j++;
        }


        this.escribirArchivo(workbook, nombreArchivo, ParametrosReportes.ARCHIVO_XLS);
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

    public void descargarReporte() {
//        
        GrupoPago gp = this.finiquitos.get(0).getRelacionlaboral().getIdgrupopago();
//        
        String nombreArchivo = "FINIQUITO_" + gp.getGrupopago();
        //
        List<String> lineas = new ArrayList<>();
//        
        StringBuilder sb = new StringBuilder();
        sb.append("NUMERO EMPLEADO,").append("NOMBRE,").append("F. ANTIGUEDAD,").append("F. INGRESO,")
                .append("CVE. CCO,").append("DESC. CCO,").append("CVE. DEPTO,").append("DESCR. DEPTO,").append("CVE. PUESTO,")
                .append("DESCR. PUESTO,").append("DESCR. REG. PATRONAL,").append("REG. PATRONAL,").append("SALARIO DIARIO,")
                .append("SALARIO DIARIO INTEGRADO,").append("N.S.S.,").append("NETO PAGAR,").append("TIPO CONCEPTO,").append("CONCEPTO,")
                .append("DESCRIPCION CONCEPTO,").append("TIEMPO,").append("IMPORTE");
//        
        lineas.add(sb.toString());

        for (Finiquito f : this.finiquitos) {
            sb = new StringBuilder();
            RelacionLaboralPosicion posicion = f.getRelacionlaboral().getIdrelacionlaboralposicion();
//            
            String nombre = Util.getNombre(f.getRelacionlaboral().getIdempleado());
            String fechaantiguedada = CeniccoUtil.getSimpleDateFormatFromXMLGregorian(f.getRelacionlaboral().getFechaantiguedad1());
            String fechaingreso = CeniccoUtil.getSimpleDateFormatFromXMLGregorian(f.getRelacionlaboral().getFechaingreso());
//            
            sb.append(f.getRelacionlaboral().getNumeroempleado()).append(",").append(nombre).append(",").append(fechaantiguedada).append(",")
                    .append(fechaingreso).append(",").append(posicion.getIdcentrocosto().getCentroCosto()).append(",")
                    .append(posicion.getIdcentrocosto().getNombre()).append(",")
                    .append(posicion.getIddepartamento().getDepartamento()).append(",").append(posicion.getIddepartamento().getNombre()).append(",")
                    .append(posicion.getIdpuesto().getPuesto()).append(",").append(posicion.getIdpuesto().getNombre()).append(",")
                    .append(f.getRelacionlaboral().getIdregistropatronal().getComentarios()).append(",")
                    .append(f.getRelacionlaboral().getIdregistropatronal().getRegistropatronal()).append(",")
                    .append(f.getRelacionlaboral().getSalarioDiario()).append(",").append(f.getRelacionlaboral().getSalarioDiarioIntegrado()).append(",")
                    .append(f.getRelacionlaboral().getIdempleado().getAfiliacion()).append(",")
                    .append(f.getNetoPagar()).append(",");
//            
            for (Concepto c : f.getPercepciones()) {
                if (c.getValor() == 0.0) {
                    continue;
                }
                StringBuilder sbconcepto = new StringBuilder();
                sbconcepto.append("PERCEPCION").append(",").append(c.getConcepto()).append(",").
                        append(c.getNombre()).append(",").append(c.getTiempo()).append(",").append(c.getValor());
//                
                String linea = sb.toString() + sbconcepto.toString();

                lineas.add(linea);
            }
            for (Concepto c : f.getDeducciones()) {
                if (c.getValor() == 0.0) {
                    continue;
                }
                StringBuilder sbconcepto = new StringBuilder();
                sbconcepto.append("DEDUCCION").append(",").append(c.getConcepto()).append(",").
                        append(c.getNombre()).append(",").append(c.getTiempo()).append(",").append(c.getValor());
//                
                String linea = sb.toString() + sbconcepto.toString();

                lineas.add(linea);
            }
            for (Concepto c : f.getProvisiones()) {
                if (c.getValor() == 0.0) {
                    continue;
                }
                StringBuilder sbconcepto = new StringBuilder();
                sbconcepto.append("PROVISION").append(",").append(c.getConcepto()).append(",").
                        append(c.getNombre()).append(",").append(c.getTiempo()).append(",").append(c.getValor());
//                
                String linea = sb.toString() + sbconcepto.toString();

                lineas.add(linea);
            }

        }
//        
        if (!lineas.isEmpty()) {
            Util.escribirFichero(lineas, nombreArchivo, ParametrosReportes.ARCHIVO_CSV);
        }
    }

    @Override
    public void edit(ActionEvent event) {
        boolean isValidate = ControladorWS.getInstance().cerrarFiniquito(finiquitos,
                ControladorSesiones.getInstance().getUsuarioSession(), this.selectedGrupoPago, this.fechaPago, this.selectedTipoFiniquito,
                this.mesaaplicar, this.anioaaplicar);
        FacesMessage msg = isValidate ? Util.getFacesMessage(FacesMessage.SEVERITY_INFO, "Se guardaron exitosamente los finiquitos!")
                : Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, "Error al guardar los finiquitos");
        this.generarMsg(msg, isValidate);
    }

    @Override
    public void delete(Finiquito obj) {
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
        return CeniccoUtil.getInformacion(this.finiquitos.size());
    }

    public String getInformacionConceptosReportados() {
        return CeniccoUtil.getInformacion(this.conceptosReportados.size());
    }

    public String[] getSelectedRelaciones() {
        return selectedRelaciones;
    }

    public void setSelectedRelaciones(String[] selectedRelaciones) {
        this.selectedRelaciones = selectedRelaciones;
    }

    public List<String> getRelaciones() {
        return relaciones;
    }

    public String getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(String fechaPago) {
        this.fechaPago = fechaPago;
    }

    public Map<String, RelacionLaboral> getMapaRelacionesLaborales() {
        return mapaRelacionesLaborales;
    }

    public void setMapaRelacionesLaborales(Map<String, RelacionLaboral> mapaRelacionesLaborales) {
        this.mapaRelacionesLaborales = mapaRelacionesLaborales;
    }

    public String getSelectedCausaBaja() {
        return selectedCausaBaja;
    }

    public void setSelectedCausaBaja(String selectedCausaBaja) {
        this.selectedCausaBaja = selectedCausaBaja;
    }

    public Integer getSelectedTipoFiniquito() {
        return selectedTipoFiniquito;
    }

    public void setSelectedTipoFiniquito(Integer selectedTipoFiniquito) {
        this.selectedTipoFiniquito = selectedTipoFiniquito;
    }

    public List<GrupoConcepto> getTiposFiniquitos() {
        return tiposFiniquitos;
    }

    public void setTiposFiniquitos(List<GrupoConcepto> tiposFiniquitos) {
        this.tiposFiniquitos = tiposFiniquitos;
    }

    public List<Concepto> getFilteredConceptosReportados() {
        return filteredConceptosReportados;
    }

    public void setFilteredConceptosReportados(List<Concepto> filteredConceptosReportados) {
        this.filteredConceptosReportados = filteredConceptosReportados;
    }

    public List<Concepto> getSelectedConceptos() {
        return selectedConceptos;
    }

    public void setSelectedConceptos(List<Concepto> selectedConceptos) {
        this.selectedConceptos = selectedConceptos;
    }

    public List<Concepto> getConceptosReportados() {
        return conceptosReportados;
    }

    public List<Finiquito> getFilteredFiniquitos() {
        return filteredFiniquitos;
    }

    public void setFilteredFiniquitos(List<Finiquito> filteredFiniquitos) {
        this.filteredFiniquitos = filteredFiniquitos;
    }

    public List<Finiquito> getSelectedFiniquitos() {
        return selectedFiniquitos;
    }

    public void setSelectedFiniquitos(List<Finiquito> selectedFiniquitos) {
        this.selectedFiniquitos = selectedFiniquitos;
    }

    public Finiquito getFiniquito() {
        return finiquito;
    }

    public void setFiniquito(Finiquito finiquito) {
        this.finiquito = finiquito;
    }

    public List<Finiquito> getFiniquitos() {
        return finiquitos;
    }

    public String getSelectedTipoSalario() {
        return selectedTipoSalario;
    }

    public void setSelectedTipoSalario(String selectedTipoSalario) {
        this.selectedTipoSalario = selectedTipoSalario;
    }

    public Integer getPeriodonomina() {
        return periodonomina;
    }

    public void setPeriodonomina(Integer periodonomina) {
        this.periodonomina = periodonomina;
    }

    public Integer getAniononimina() {
        return aniononimina;
    }

    public void setAniononimina(Integer aniononimina) {
        this.aniononimina = aniononimina;
    }

    public Integer getMesaaplicar() {
        return mesaaplicar;
    }

    public void setMesaaplicar(Integer mesaaplicar) {
        this.mesaaplicar = mesaaplicar;
    }

    public Integer getAnioaaplicar() {
        return anioaaplicar;
    }

    public void setAnioaaplicar(Integer anioaaplicar) {
        this.anioaaplicar = anioaaplicar;
    }

    public void descargarDetalleISR() {
        GrupoPago gp = ControladorWS.getInstance().findGrupoPagoById(this.selectedGrupoPago);
        String nombreArchivo = "DETALLE ISR_FINIQUITO_" + gp.getGrupopago() + "_" + this.periodonomina + "_" + this.aniononimina;
        String pathDetalleISR = "_DETALLE ISR_FINIQUITO_" + gp.getGrupopago() + "_" + this.periodonomina + "_" + this.aniononimina + "." + ParametrosReportes.ARCHIVO_XLS;
        String file = ControladorWS.getInstance().getPathNomina() + pathDetalleISR;
        try {
            Path path = Paths.get(file);
            byte[] data = Files.readAllBytes(path);
            RequestContext context = RequestContext.getCurrentInstance();
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, nombreArchivo);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, data);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_XLS);
            context.addCallbackParam("ruta", MyPaths.urlServletDescargarArchivoCsv());
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
