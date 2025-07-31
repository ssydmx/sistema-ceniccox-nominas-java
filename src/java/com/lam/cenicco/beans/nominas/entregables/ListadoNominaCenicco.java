/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lam.cenicco.beans.nominas.entregables;

import com.lam.cenicco.dao.ProcesoDAO;
import com.lam.cenicco.impl.ControladorWS;
import com.lam.cenicco.librerias.mensajes.Mensajes;
import com.lam.cenicco.librerias.utilidades.CeniccoUtil;
import com.lam.cenicco.to.RespuestaTO;
import com.lam.cenicco.util.ControladorSesiones;
import com.lam.cenicco.util.Parametros;
import com.lam.cenicco.util.ParametrosReportes;
import com.lam.cenicco.util.Util;
import com.lam.cenicco.util.paths.MyPaths;
import com.lam.cenicco.ws.CifrasNomina;
import com.lam.cenicco.ws.Compania;
import com.lam.cenicco.ws.Concepto;
import com.lam.cenicco.ws.ConceptoAcumulado;
import com.lam.cenicco.ws.Departamento;
import com.lam.cenicco.ws.GrupoPago;
import com.lam.cenicco.ws.Parametro;
import com.lam.cenicco.ws.Periodo;
import com.lam.cenicco.ws.Puesto;
import com.lam.cenicco.ws.RelacionLaboral;
import com.lam.cenicco.ws.RelacionLaboralPosicion;
import com.lam.cenicco.ws.SaldoVacaciones;
import com.lam.cenicco.ws.TipoProceso;
import com.lam.cenicco.ws.VistaConceptosNomina;
import com.lam.cenicco.ws.VistaSaldoCreditos;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.primefaces.context.RequestContext;

/**
 *
 * @author JoséAntonio
 */
public class ListadoNominaCenicco implements ProcesoDAO<VistaConceptosNomina> {
//    
    //    

    protected String[] selectedRelaciones;
//
    protected List<String> relaciones;
    protected List<CifrasNomina> listadoNomina;
//    
    protected List<CifrasNomina> selectedListadoNomina;
//    
    protected List<CifrasNomina> filteredListadoNomina;
//    
    protected Integer selectedGrupoPago;
//    
    protected Integer selectedTipoproceso;
//    
    protected Integer selectedestatus;
//    
    protected Integer periodo;
//    
    //    
    protected Integer selectedtipo;
//    
    protected Integer anio;
//    
    protected Double totalDeducciones;
    protected Double totalPercepciones;
    protected Double totalProvisiones;
    protected Double totalNeto;
    protected Double totalGravadoIsr;
    protected Double totalGravadoIsn;
//    
    protected List<RespuestaTO> respuesta;
//    
    protected Map<String, String> mapaRelacionesLaborales;

    public ListadoNominaCenicco() {
        if (this.listadoNomina == null) {
            this.listadoNomina = new ArrayList<>();
        }
        if (this.respuesta == null) {
            this.respuesta = new ArrayList<>();
        }
    }

    @Override
    public void init() {
    }

    private void inicializarTotales() {
        this.totalPercepciones = 0.0;
        this.totalDeducciones = 0.0;
        this.totalProvisiones = 0.0;
        this.totalNeto = 0.0;
        this.totalGravadoIsr = 0.0;
        this.totalGravadoIsn = 0.0;
    }

    @Override
    public void consultar(ActionEvent event) {
        boolean isValidate = this.validate();
//        
        if (isValidate) {
            this.inicializarTotales();
//            
            List<String> relacionesAux = new ArrayList<>();
            for (String llave : this.selectedRelaciones) {
                relacionesAux.add(this.mapaRelacionesLaborales.get(llave));
            }
//            
            this.listadoNomina = ControladorWS.getInstance().getListadoNominas(this.selectedGrupoPago, this.selectedTipoproceso, this.periodo, this.anio,
                    this.selectedestatus, relacionesAux);
//            
            this.filteredListadoNomina = this.listadoNomina;
            for (CifrasNomina v : this.listadoNomina) {
//                
                double neto = v.getPercepciones() - v.getDeducciones();
                v.setNeto(neto);
//                
                this.totalPercepciones += v.getPercepciones();
                this.totalDeducciones += v.getDeducciones();
                this.totalProvisiones += v.getProvisiones();
//                
                this.totalNeto += (neto);
//                
                this.totalGravadoIsr += (v.getBgisr() == null ? 0.0 : v.getBgisr());
                this.totalGravadoIsn += (v.getBgisn() == null ? 0.0 : v.getBgisn());
//                
            }
        }

    }

    private boolean validate() {
        boolean isValidate = true;
//        
        if (this.selectedGrupoPago == null) {
            FacesMessage msg = Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, Mensajes.CAMPO_VACIO_GRUPO_PAGO);
            FacesContext.getCurrentInstance().addMessage(null, msg);
            isValidate = false;
        }
        if (this.periodo == null) {
            FacesMessage msg = Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, Mensajes.CAMPO_VACIO_PERIODO);
            FacesContext.getCurrentInstance().addMessage(null, msg);
            isValidate = false;
        }
        if (this.anio == null) {
            FacesMessage msg = Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, Mensajes.CAMPO_VACIO_ANIO);
            FacesContext.getCurrentInstance().addMessage(null, msg);
            isValidate = false;
        }
        return isValidate;
    }

    @Override
    public void limpiar(ActionEvent event) {
//        
        this.inicializarTotales();
//        
        this.listadoNomina = new ArrayList<>();
        this.filteredListadoNomina = new ArrayList<>();
//        
        this.selectedGrupoPago = null;
        this.periodo = null;
        this.anio = null;
    }

    public void descargarReporte() {
        System.out.println("Descarga Reporte...");
        RequestContext context = RequestContext.getCurrentInstance();

        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_LISTADO_NOMINAS_ENTREGABLES);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO_TIPO_PROCESO, null);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, ParametrosReportes.MODULO_LISTADO_NOMINAS);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.filteredListadoNomina);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.PARAMETRO_TOTAL, this.totalNeto);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.PARAMETRO_TOTAL_PERCEPCIONES, this.totalPercepciones);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.PARAMETRO_TOTAL_DEDUCCIONES, this.totalDeducciones);
//
        context.addCallbackParam("ruta", MyPaths.urlServletReporte());
    }

    public static boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    public void descargarReporteOrdas() {
        Collections.sort(this.filteredListadoNomina, new Comparator<CifrasNomina>() {
            @Override
            public int compare(CifrasNomina o1, CifrasNomina o2) {
                String num1 = o1.getNumeroempleado();
                String num2 = o2.getNumeroempleado();
                if (isNumeric(num1) && isNumeric(num2)) {
                    int numEmpleado1 = Integer.parseInt(num1);
                    int numEmpleado2 = Integer.parseInt(num2);
                    return Integer.compare(numEmpleado1, numEmpleado2);
                }
                return num1.compareTo(num2);
            }
        });
        System.out.println("Descarga Reporte...");
        RequestContext context = RequestContext.getCurrentInstance();

        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_LISTADO_NOMINAS_ENTREGABLES_ORDAS);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO_TIPO_PROCESO, null);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, ParametrosReportes.MODULO_LISTADO_NOMINAS);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.filteredListadoNomina);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.PARAMETRO_TOTAL, this.totalNeto);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.PARAMETRO_TOTAL_PERCEPCIONES, this.totalPercepciones);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.PARAMETRO_TOTAL_DEDUCCIONES, this.totalDeducciones);
//
        context.addCallbackParam("ruta", MyPaths.urlServletReporte());
    }

    public void descargarReportePorDepartamento() {
        System.out.println("Descarga Reporte...");
        RequestContext context = RequestContext.getCurrentInstance();

        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_LISTADO_NOMINAS_ENTREGABLES_X_DEPTO);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO_TIPO_PROCESO, null);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, ParametrosReportes.MODULO_LISTADO_NOMINAS);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.filteredListadoNomina);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.PARAMETRO_TOTAL, this.totalNeto);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.PARAMETRO_TOTAL_PERCEPCIONES, this.totalPercepciones);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.PARAMETRO_TOTAL_DEDUCCIONES, this.totalDeducciones);
//
        context.addCallbackParam("ruta", MyPaths.urlServletReporte());
    }

    public void descargarReporteCsv() {
        List<String> lineas = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        sb.append("No. Empleado,").append("Nombre,").append("R.F.C.,").append("Departamento,").append("Puesto,").append("Percepción,").append("Deducción,").append("Provisión,")
                .append("Importe Neto,").append("ISR,").append("Centro de Costos");
        lineas.add(sb.toString());

        for (CifrasNomina c : this.selectedListadoNomina) {
            sb = new StringBuilder();
            sb.append(c.getNumeroempleado()).append(",")
                    .append(c.getNombreempleado()).append(",")
                    .append(c.getRfc()).append(",")
                    .append(c.getDepto()).append(",")
                    .append(c.getPuesto()).append(",")
                    .append(c.getPercepciones()).append(",")
                    .append(c.getDeducciones()).append(",")
                    .append(c.getProvisiones()).append(",")
                    .append(c.getNeto()).append(",")
                    .append(c.getImpuestoretenido()).append(",")
                    .append(c.getCentrocostros());
            lineas.add(sb.toString());
        }
        Util.escribirFichero(lineas, "ListadoNominas", ParametrosReportes.ARCHIVO_CSV);
    }
//    

    public void descargarReporteFirmas() {
        System.out.println("Descarga Reporte...");
        RequestContext context = RequestContext.getCurrentInstance();

        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_LISTADO_NOMINAS_ENTREGABLES);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO_TIPO_PROCESO, Parametros.TIPO_NOMINA_PTU);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, ParametrosReportes.MODULO_LISTADO_NOMINAS);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.filteredListadoNomina);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.PARAMETRO_TOTAL, this.totalNeto);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.PARAMETRO_TOTAL_PERCEPCIONES, this.totalPercepciones);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.PARAMETRO_TOTAL_DEDUCCIONES, this.totalDeducciones);
//
        context.addCallbackParam("ruta", MyPaths.urlServletReporte());
    }

    public void descargarPreliminar() {
        GrupoPago gp = ControladorWS.getInstance().findGrupoPagoById(this.selectedGrupoPago);
        String nombreArchivo = gp.getGrupopago() + "_" + this.periodo + "_" + this.anio;
//        
        switch (this.selectedtipo) {
            case 0:
                this.archivocsv(nombreArchivo, ParametrosReportes.ARCHIVO_CSV);
                break;
            default:
                this.archivoexcel(nombreArchivo, ParametrosReportes.ARCHIVO_XLS);
                break;
        }
    }

    public void descargarArchivoNomina() {
        Periodo periodo = ControladorWS.getInstance().findPeriodoById(this.selectedListadoNomina.get(0).getIdperiodo());
        GrupoPago gp = ControladorWS.getInstance().findGrupoPagoById(this.selectedGrupoPago);
        if (ControladorSesiones.getInstance().getCompaniaSession().getNombreCorto().equals("KONECTA")) {
            String nombreArchivo = gp.getGrupopago() + "_" + this.periodo + "_" + this.anio;
            if (gp.getGrupopago().equals("NM")) {
                System.out.println(":: Reporte Nomina Extranjeros ::");
                this.getReporteGeneralNominaExtranjeros(periodo, nombreArchivo, ParametrosReportes.ARCHIVO_XLS);
            } else {
                this.getReporteGeneralNomina(nombreArchivo, ParametrosReportes.ARCHIVO_XLS);

            }
        } else {
            String nombreArchivo = gp.getGrupopago() + "_" + this.periodo + "_" + this.anio;
            this.getReporteGeneralNomina(nombreArchivo, ParametrosReportes.ARCHIVO_XLS);
        }

    }

    public void descargarArchivoAlpha(ActionEvent event) {
        GrupoPago gp = ControladorWS.getInstance().findGrupoPagoById(this.selectedGrupoPago);

        TipoProceso tp = ControladorWS.getInstance().findTipoProcesoById(
                this.selectedTipoproceso == null
                ? gp.getIdTipoproceso().getIdtipoproceso()
                : this.selectedTipoproceso);
//        
        boolean isaguinaldo = tp.getTipoproceso().equals("NF");

        String nombreArchivo = gp.getGrupopago() + "_" + this.periodo + "_" + this.anio;
        this.archivoexcelAlpha(nombreArchivo, isaguinaldo, ParametrosReportes.ARCHIVO_XLS);
    }

    private void archivocsv(String nombreArchivo, String tipoarchivo) {
        String file = ControladorWS.getInstance().getPathNomina() + nombreArchivo;
//        
        try {
//            
            Path path = Paths.get(file);
            byte[] data = Files.readAllBytes(path);
            RequestContext context = RequestContext.getCurrentInstance();
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, nombreArchivo);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, data);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, tipoarchivo);
            context.addCallbackParam("ruta", MyPaths.urlServletDescargarArchivoCsv());
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void getReporteGeneralNomina(String nombreArchivo, String tipoarchivo) {

        List<Concepto> conceptosPercepciones = new ArrayList<>();               //Lista de todos los conceptos (percepciones) de nómina
        List<Concepto> conceptosDeducciones = new ArrayList<>();                //Lista de todos los conceptos (deducciones) de nómina
        List<Concepto> conceptosProvisiones = new ArrayList<>();                //Lista de todos los conceptos (provisiones) de nómina
        Map<String, Concepto> mapaPercepciones = new HashMap<>();               //Mapa para filtrar los conceptos repetidos
        Map<String, Concepto> mapaDeducciones = new HashMap<>();                //Mapa para filtrar los conceptos repetidos
        Map<String, Concepto> mapaProvisiones = new HashMap<>();                //Mapa para filtrar los conceptos repetidos
        String llaveconcepto;
        for (CifrasNomina c : this.listadoNomina) {
//Ciclo Percepciones
            for (Concepto co : c.getConceptosPercepciones()) {
                llaveconcepto = co.getConcepto() + " | " + co.getNombre();
                if (mapaPercepciones.get(llaveconcepto) == null) {
                    mapaPercepciones.put(llaveconcepto, co);
                } else {
                    continue;
                }
            }
//Ciclo Deducciones
            for (Concepto co : c.getConceptosDeducciones()) {
                llaveconcepto = co.getConcepto() + " | " + co.getNombre();
                if (mapaDeducciones.get(llaveconcepto) == null) {
                    mapaDeducciones.put(llaveconcepto, co);
                } else {
                    continue;
                }
            }
//Ciclo Provisiones
            for (Concepto co : c.getConceptosProvisiones()) {
                llaveconcepto = co.getConcepto() + " | " + co.getNombre();
                if (mapaProvisiones.get(llaveconcepto) == null) {
                    mapaProvisiones.put(llaveconcepto, co);
                } else {
                    continue;
                }
            }
        }

        conceptosPercepciones.addAll(mapaPercepciones.values());
        conceptosDeducciones.addAll(mapaDeducciones.values());
        conceptosProvisiones.addAll(mapaProvisiones.values());

        Collections.sort(conceptosPercepciones, new Comparator<Concepto>() {
            @Override
            public int compare(Concepto p1, Concepto p2) {
                return new Integer(p1.getConcepto()).compareTo(new Integer(p2.getConcepto()));
            }
        });

        Collections.sort(conceptosDeducciones, new Comparator<Concepto>() {
            @Override
            public int compare(Concepto p1, Concepto p2) {
                return new Integer(p1.getConcepto()).compareTo(new Integer(p2.getConcepto()));
            }
        });

        Collections.sort(conceptosProvisiones, new Comparator<Concepto>() {
            @Override
            public int compare(Concepto p1, Concepto p2) {
                return new Integer(p1.getConcepto()).compareTo(new Integer(p2.getConcepto()));
            }
        });

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("Prenomina");
        int fila = 0;
        int columna = 0;
        DecimalFormat nformat = new DecimalFormat("0.00");
//        
        HSSFRow row = sheet.createRow(fila);
        HSSFCellStyle styleamarillo = workbook.createCellStyle();
        styleamarillo.setFillForegroundColor(HSSFColor.YELLOW.index);
        styleamarillo.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

        HSSFCell cell = row.createCell(columna);
        cell.setCellValue("NUMERO EMPLEADO");
        columna++;
        cell = row.createCell(columna);
        cell.setCellValue("NOMBRE");
        columna++;
        cell = row.createCell(columna);
        cell.setCellValue("F. ANTIGUEDAD");
        columna++;
        cell = row.createCell(columna);
        cell.setCellValue("DEPARTAMENTO");
        columna++;
        cell = row.createCell(columna);
        cell.setCellValue("PUESTO");
        columna++;
        cell = row.createCell(columna);
        cell.setCellValue("SALARIO DIARIO");
        columna++;
        cell = row.createCell(columna);
        cell.setCellValue("SALARIO DIARIO INTEGRADO");
        columna++;

        for (Concepto c : conceptosPercepciones) {
            if (c.getSuma() == 1 || ((c.getAtributo().equals("999") || c.getAtributo().equals("029")) && c.getSuma() == 0)) {
                cell = row.createCell(columna);
                cell.setCellValue(c.getConcepto() + "/" + c.getNombre());
                columna++;
            }
        }

        cell = row.createCell(columna);
        cell.setCellValue("TOTAL PERCEPCIONES");
        cell.setCellStyle(styleamarillo);
        columna++;

        for (Concepto c : conceptosDeducciones) {
            if (c.getSuma() == 1) {
                cell = row.createCell(columna);
                cell.setCellValue(c.getConcepto() + "/" + c.getNombre());
                columna++;
            }
        }

        cell = row.createCell(columna);
        cell.setCellValue("TOTAL DEDUCCIONES");
        cell.setCellStyle(styleamarillo);
        columna++;

        for (Concepto c : conceptosProvisiones) {
            cell = row.createCell(columna);
            cell.setCellValue(c.getConcepto() + "/" + c.getNombre());
            columna++;
        }

        cell = row.createCell(columna);
        cell.setCellValue("TOTAL PROVISIONES");
        cell.setCellStyle(styleamarillo);
        columna++;

        fila++;
        //Llenado de las primeras casilla del reporte (información de la nómina)
        for (CifrasNomina n : this.listadoNomina) {
            columna = 0;
            //NUMEROEMPLEADO
            row = sheet.createRow(fila);
            cell = row.createCell(columna);
            cell.setCellValue(n.getNumeroempleado());
            columna++;
//          NOMBRE  
            cell = row.createCell(columna);
            cell.setCellValue(n.getNombreempleado());
            columna++;
//          FECHA ANTIGUEDAD  
            cell = row.createCell(columna);
            cell.setCellValue(n.getFechaantiguedad());
            columna++;
//          DESCR DEPTO
            cell = row.createCell(columna);
            cell.setCellValue(n.getDepto());
            columna++;
//          DESCR PUESTO
            cell = row.createCell(columna);
            cell.setCellValue(n.getPuesto());
            columna++;
//          SALARIO DIARIO
            cell = row.createCell(columna);
            cell.setCellValue(nformat.format(n.getSalariodiario()));
            columna++;
//          SALARIO DIARIO INTEGRADO
            cell = row.createCell(columna);
            cell.setCellValue(nformat.format(n.getSalariodiariointegrado()));
            columna++;

            //Llenado de los valores de los conceptos que se agregaron respecto a las listas de conceptos percepciones, deducciones y provisiones aplicables en el reporte

            //Conceptos Percepciones
            for (Concepto c : conceptosPercepciones) {
                if (c.getSuma() == 1 || ((c.getAtributo().equals("999") || c.getAtributo().equals("029")) && c.getSuma() == 0)) {
                    cell = row.createCell(columna);
                    cell.setCellValue("");
                    for (Concepto ccn : n.getConceptosPercepciones()) {
                        if (ccn.getConcepto().equals(c.getConcepto())) {
                            cell.setCellValue(ccn.getValor());
                        }
                    }
                    columna++;
                }
            }

            cell = row.createCell(columna);
            cell.setCellValue(n.getPercepciones());
            cell.setCellStyle(styleamarillo);
            columna++;

            //Conceptos Deducciones
            for (Concepto c : conceptosDeducciones) {
                if (c.getSuma() == 1) {
                    cell = row.createCell(columna);
                    cell.setCellValue("");
                    for (Concepto ccn : n.getConceptosDeducciones()) {
                        if (ccn.getConcepto().equals(c.getConcepto())) {
                            cell.setCellValue(ccn.getValor());
                        }
                    }
                    columna++;
                }
            }

            cell = row.createCell(columna);
            cell.setCellValue(n.getDeducciones());
            cell.setCellStyle(styleamarillo);
            columna++;

            //Conceptos Provisiones
            for (Concepto c : conceptosProvisiones) {
                cell = row.createCell(columna);
                cell.setCellValue("");
                for (Concepto ccn : n.getConceptosProvisiones()) {
                    if (ccn.getConcepto().equals(c.getConcepto())) {
                        cell.setCellValue(ccn.getValor());
                    }
                }
                columna++;
            }

            cell = row.createCell(columna);
            cell.setCellValue(n.getProvisiones());
            cell.setCellStyle(styleamarillo);

            fila++;
        }

        this.escribirArchivo(workbook, nombreArchivo, tipoarchivo);

    }

    private void getReporteGeneralNominaExtranjeros(Periodo periodo, String nombreArchivo, String tipoarchivo) {
        Parametro p = ControladorWS.getInstance().getParametro("HEADER_REPORTE_GENERAL_NOMINA_EXTRANJEROS");
        List<String> conceptosExtranjeros = Arrays.asList(p.getValor().split(","));
        List<Concepto> conceptosExistentes = new ArrayList<>();                 //Lista de todos los conceptos existentes nominas
        Map<String, Concepto> mapaPercepciones = new HashMap<>();               //Mapa para filtrar los conceptos repetidos
        Map<String, Concepto> mapaDeducciones = new HashMap<>();                //Mapa para filtrar los conceptos repetidos
        Map<String, Concepto> mapaProvisiones = new HashMap<>();                //Mapa para filtrar los conceptos repetidos
        String llaveconcepto;
        for (CifrasNomina c : this.listadoNomina) {
//Ciclo Percepciones
            for (Concepto co : c.getConceptosPercepciones()) {
                llaveconcepto = co.getConcepto() + " | " + co.getNombre();
                if (mapaPercepciones.get(llaveconcepto) == null) {
                    mapaPercepciones.put(llaveconcepto, co);
                } else {
                    continue;
                }
            }
//Ciclo Deducciones
            for (Concepto co : c.getConceptosDeducciones()) {
                llaveconcepto = co.getConcepto() + " | " + co.getNombre();
                if (mapaDeducciones.get(llaveconcepto) == null) {
                    mapaDeducciones.put(llaveconcepto, co);
                } else {
                    continue;
                }
            }
//Ciclo Provisiones
            for (Concepto co : c.getConceptosProvisiones()) {
                llaveconcepto = co.getConcepto() + " | " + co.getNombre();
                if (mapaProvisiones.get(llaveconcepto) == null) {
                    mapaProvisiones.put(llaveconcepto, co);
                } else {
                    continue;
                }
            }
        }

        conceptosExistentes.addAll(mapaPercepciones.values());
        conceptosExistentes.addAll(mapaDeducciones.values());
        conceptosExistentes.addAll(mapaProvisiones.values());

        List<Concepto> conceptos = new ArrayList<>();
        for (String c : conceptosExtranjeros) {
            for (Concepto cp : conceptosExistentes) {
                if (c.equalsIgnoreCase(cp.getConcepto())) {
                    conceptos.add(cp);
                }
            }
        }

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("Prenomina Extranjero");
        int fila = 0;
        int columna = 0;
        DecimalFormat nformat = new DecimalFormat("0.00");
//        
        HSSFRow row = sheet.createRow(fila);
        HSSFCellStyle styleamarillo = workbook.createCellStyle();
        styleamarillo.setFillForegroundColor(HSSFColor.YELLOW.index);
        styleamarillo.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

        HSSFCell cell = row.createCell(columna);
        cell.setCellValue("MES");
        columna++;
        cell = row.createCell(columna);
        cell.setCellValue("ID");
        columna++;
        cell = row.createCell(columna);
        cell.setCellValue("LOCATION");
        columna++;
        cell = row.createCell(columna);
        cell.setCellValue("BROKER");
        columna++;
        cell = row.createCell(columna);
        cell.setCellValue("NOMBRE");
        columna++;
        cell = row.createCell(columna);
        cell.setCellValue("AREA");
        columna++;
        cell = row.createCell(columna);
        cell.setCellValue("PUESTO");
        columna++;
        cell = row.createCell(columna);
        cell.setCellValue("NIVEL");
        columna++;
        cell = row.createCell(columna);
        cell.setCellValue("FECHA ALTA");
        columna++;
        cell = row.createCell(columna);
        cell.setCellValue("FECHA BAJA");
        columna++;


        for (Concepto c : conceptos) {
            cell = row.createCell(columna);
            cell.setCellValue(c.getConcepto() + "/" + c.getNombre());
            columna++;
        }

        fila++;
        //Llenado de las primeras casilla del reporte (información de la nómina)
        for (CifrasNomina n : this.listadoNomina) {
            RelacionLaboral rellab = ControladorWS.getInstance().findRelacionLaboralById(n.getIdrellab());
            RelacionLaboralPosicion posicion = rellab.getIdrelacionlaboralposicion();
            Departamento departamento = posicion.getIddepartamento();
            Puesto puesto = posicion.getIdpuesto();
            columna = 0;
//          MES
            row = sheet.createRow(fila);
            cell = row.createCell(columna);
            cell.setCellValue(periodo.getMes());
            columna++;
//          ID  
            cell = row.createCell(columna);
            cell.setCellValue(n.getNumeroempleado());
            columna++;
//          LOCATION  
            cell = row.createCell(columna);
            cell.setCellValue(posicion.getUbicacionTrabajo());
            columna++;
//          BROKER  
            cell = row.createCell(columna);
            cell.setCellValue(posicion.getBroker());
            columna++;
//          NOMBRE  
            cell = row.createCell(columna);
            cell.setCellValue(n.getNombreempleado());
            columna++;
//          AREA 
            cell = row.createCell(columna);
            cell.setCellValue(departamento.getNombre());
            columna++;
//          PUESTO
            cell = row.createCell(columna);
            cell.setCellValue(puesto.getNombre());
            columna++;
//          NIVEL
            cell = row.createCell(columna);
            cell.setCellValue(posicion.getNivelTrabajador());
            columna++;
//          FECHA ALTA
            cell = row.createCell(columna);
            cell.setCellValue(n.getFechaingreso());
            columna++;
//          FECHA BAJA
            cell = row.createCell(columna);
            cell.setCellValue(n.getFechabaja());
            columna++;

            //Llenado de los valores de los conceptos que se agregaron respecto a las listas de conceptos percepciones, deducciones y provisiones aplicables en el reporte

            //Conceptos
            for (Concepto c : conceptos) {
                cell = row.createCell(columna);
                cell.setCellValue("");
                for (Concepto ccn : n.getConceptosPercepciones()) {
                    if (ccn.getConcepto().equals(c.getConcepto())) {
                        cell.setCellValue(ccn.getValor());
                    }
                }

                for (Concepto ccn : n.getConceptosDeducciones()) {
                    if (ccn.getConcepto().equals(c.getConcepto())) {
                        cell.setCellValue(ccn.getValor());
                    }
                }

                for (Concepto ccn : n.getConceptosProvisiones()) {
                    if (ccn.getConcepto().equals(c.getConcepto())) {
                        cell.setCellValue(ccn.getValor());
                    }
                }
                columna++;
            }

            fila++;
        }

        this.escribirArchivo(workbook, nombreArchivo, tipoarchivo);

    }

    private void archivoexcel(String nombreArchivo, String tipoarchivo) {
        DecimalFormat nformat = new DecimalFormat("0.00");
        Map<String, Integer> mapa = new HashMap<>();
////        
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("Prenomina");
//        
        HSSFRow row = sheet.createRow(0);
//        
        HSSFCell cell = row.createCell(0);
        cell.setCellValue("NUMERO EMPLEADO");
        cell = row.createCell(1);
        cell.setCellValue("NOMBRE");
        cell = row.createCell(2);
        cell.setCellValue("F. ANTIGUEDAD");
        cell = row.createCell(3);
        cell.setCellValue("DEPARTAMENTO");
        cell = row.createCell(4);
        cell.setCellValue("PUESTO");
        cell = row.createCell(5);
        cell.setCellValue("SALARIO DIARIO");
        cell = row.createCell(6);
        cell.setCellValue("SALARIO DIARIO INTEGRADO");
        cell = row.createCell(7);
        cell.setCellValue("NETO PAGAR");
////        
        int i = 1;
        int k = 8;
//        
        Iterator<CifrasNomina> iter = this.listadoNomina.iterator();
        while (iter.hasNext()) {
            CifrasNomina n = iter.next();
            int j = 0;
//          NUMEROEMPLEADO  
            row = sheet.createRow(i);
            cell = row.createCell(j);
            cell.setCellValue(n.getNumeroempleado());
            j++;
//          NOMBRE  
            cell = row.createCell(j);
            cell.setCellValue(n.getNombreempleado());
            j++;
//          FECHA ANTIGUEDAD  
            cell = row.createCell(j);
            cell.setCellValue(n.getFechaantiguedad());
            j++;
//          DESCR DEPTO
            cell = row.createCell(j);
            cell.setCellValue(n.getDepto());
            j++;
//          DESCR PUESTO
            cell = row.createCell(j);
            cell.setCellValue(n.getPuesto());
            j++;
//          SALARIO DIARIO
            cell = row.createCell(j);
            cell.setCellValue(nformat.format(n.getSalariodiario()));
            j++;
//          SALARIO DIARIO INTEGRADO
            cell = row.createCell(j);
            cell.setCellValue(nformat.format(n.getSalariodiariointegrado()));
            j++;
//          NETO A PAGAR
            cell = row.createCell(j);
            cell.setCellValue(nformat.format(n.getNeto()));
            j++;
//            
            List<Concepto> conceptos = new ArrayList<>();
            conceptos.addAll(n.getConceptosPercepciones());
            conceptos.addAll(n.getConceptosDeducciones());
            conceptos.addAll(n.getConceptosProvisiones());
//            
            Collections.sort(conceptos, new Comparator<Concepto>() {
                @Override
                public int compare(Concepto c1, Concepto c2) {
                    return c1.getConcepto().compareTo(c2.getConcepto());
                }
            });
//          REGISTRO  
            for (Concepto c : conceptos) {
//                
                if (c.getIncidencia() != 1 || c.getValor() == 0) {
                    continue;
                }
////                
                if (mapa.get(c.getConcepto()) == null) {
                    row = sheet.getRow(0);
                    mapa.put(c.getConcepto(), k);
                    switch (this.selectedtipo) {
                        case 1:
                            cell = row.createCell(k);
                            cell.setCellValue(c.getConcepto() + "-" + c.getNombre() + "/IMPORTE");
                            cell = row.createCell(k + 1);
                            cell.setCellValue(c.getConcepto() + "-" + c.getNombre() + "/TIEMPO");
                            k++;
                            break;
                        case 2:
                            cell = row.createCell(k);
                            cell.setCellValue(c.getConcepto() + "-" + c.getNombre() + "/IMPORTE");
                            break;
                        case 3:
                            cell = row.createCell(k);
                            cell.setCellValue(c.getConcepto() + "-" + c.getNombre() + "/TIEMPO");
                            break;
                    }
                    k++;
                }
////                
                int index = mapa.get(c.getConcepto());

                switch (this.selectedtipo) {
                    case 1:
                        row = sheet.getRow(i);
                        cell = row.createCell(index);
                        cell.setCellValue(c.getValor());
                        cell = row.createCell(index + 1);
                        cell.setCellValue(c.getTiempo());
                        break;
                    case 2:
                        row = sheet.getRow(i);
                        cell = row.createCell(index);
                        cell.setCellValue(c.getValor());
                        break;
                    case 3:
                        row = sheet.getRow(i);
                        cell = row.createCell(index);
                        cell.setCellValue(c.getTiempo());
                        break;
                }
////
            }

            i++;
        }
        this.escribirArchivo(workbook, nombreArchivo, tipoarchivo);
    }

    private void archivoexcelAlpha(String nombreArchivo, boolean isaguinaldo, String tipoarchivo) {
        DecimalFormat nformat = new DecimalFormat("0.00");
//        
        Map<String, RelacionLaboral> maparelaciones = ControladorWS.getInstance().getMapaRelacionesLaborales();
//        
        HSSFWorkbook workbook = new HSSFWorkbook();
//        
        HSSFCellStyle styleamarillo = workbook.createCellStyle();
        styleamarillo.setFillForegroundColor(HSSFColor.YELLOW.index);
        styleamarillo.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
//        
        HSSFCellStyle stylegris = workbook.createCellStyle();
        stylegris.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
        stylegris.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
//        
        HSSFCellStyle styleazul = workbook.createCellStyle();
        HSSFFont font = workbook.createFont();
        font.setColor(HSSFColor.WHITE.index);
//        
        styleazul.setFont(font);
        styleazul.setFillForegroundColor(HSSFColor.BLUE.index);
        styleazul.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
//        
        HSSFSheet sheet = workbook.createSheet("Prenomina" + this.periodo + this.anio);
//        
        int i = 0;
        int j = 2;
//        
        HSSFRow row = sheet.createRow(j++);
        HSSFCell cell = row.createCell(i++);
        cell.setCellValue("NUMERO EMPLEADO");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("NOMBRE");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("REGISTRO PATRONAL");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("ZONA");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("AREA");
        cell.setCellStyle(styleamarillo);

        cell = row.createCell(i++);
        cell.setCellValue("CVE. CCO");
        cell.setCellStyle(styleamarillo);

        cell = row.createCell(i++);
        cell.setCellValue("DESC. CCO");
        cell.setCellStyle(styleamarillo);

        cell = row.createCell(i++);
        cell.setCellValue("CVE. DEPTO");
        cell.setCellStyle(styleamarillo);

        cell = row.createCell(i++);
        cell.setCellValue("DESCR. DEPTO");
        cell.setCellStyle(styleamarillo);

        cell = row.createCell(i++);
        cell.setCellValue("CVE. PUESTO");
        cell.setCellStyle(styleamarillo);

        cell = row.createCell(i++);
        cell.setCellValue("DESCR. PUESTO");
        cell.setCellStyle(styleamarillo);

        cell = row.createCell(i++);
        cell.setCellValue("SALARIO DIARIO");
        cell.setCellStyle(styleamarillo);

        cell = row.createCell(i++);
        cell.setCellValue("SALARIO DIARIO INTEGRADO");
        cell.setCellStyle(styleamarillo);

        cell = row.createCell(i++);
        cell.setCellValue("N.S.S.");
        cell.setCellStyle(styleamarillo);
//        
        if (!isaguinaldo) {

            cell = row.createCell(i++);
            cell.setCellValue("1510/VALES DESPENSA");
            cell.setCellStyle(styleamarillo);
//        
            cell = row.createCell(i++);
            cell.setCellValue("1511/VALE DESPENSA EXCEDENTE 40% IMSS");
            cell.setCellStyle(styleamarillo);
//        
            cell = row.createCell(i++);
            cell.setCellValue("3500/PROVISION AGUINALDO");
            cell.setCellStyle(styleamarillo);
//        
            cell = row.createCell(i++);
            cell.setCellValue("3510/PROVISION AGUINALDO EXENTO");
            cell.setCellStyle(styleamarillo);
//        
            cell = row.createCell(i++);
            cell.setCellValue("3511/PROVISION AGUINALDO GRAVADO");
            cell.setCellStyle(styleamarillo);
//        
            cell = row.createCell(i++);
            cell.setCellValue("3220/PROVISION PRIMA VACACIONAL");
            cell.setCellStyle(styleamarillo);
//        
            cell = row.createCell(i++);
            cell.setCellValue("1161/PRIMA VACACIONAL EXENTA");
            cell.setCellStyle(styleamarillo);
//        
            cell = row.createCell(i++);
            cell.setCellValue("1162/PRIMA VACACIONAL GRAVABLE");
            cell.setCellStyle(styleamarillo);
//        
            cell = row.createCell(i++);
            cell.setCellValue("3110/CUOTA PATRONAL IMSS");
            cell.setCellStyle(styleamarillo);
            //        
            cell = row.createCell(i++);
            cell.setCellValue("3107/RIESGOS DE TRABAJO");
            cell.setCellStyle(styleamarillo);
            //        
            cell = row.createCell(i++);
            cell.setCellValue("3106/GUARDERIAS");
            cell.setCellStyle(styleamarillo);
//        
            cell = row.createCell(i++);
            cell.setCellValue("3101/EYM CUOTA FIJA");
            cell.setCellStyle(styleamarillo);

            cell = row.createCell(i++);
            cell.setCellValue("3102/EYM EXCED 3SM");
            cell.setCellStyle(styleamarillo);
//        
            cell = row.createCell(i++);
            cell.setCellValue("3103/EYM PREST DINERO");
            cell.setCellStyle(styleamarillo);
//        
            cell = row.createCell(i++);
            cell.setCellValue("3104/EYM GTOS MEDICOS");
            cell.setCellStyle(styleamarillo);
//        
            cell = row.createCell(i++);
            cell.setCellValue("3105/INVALIDEZ Y VIDA");
            cell.setCellStyle(styleamarillo);
//        
            cell = row.createCell(i++);
            cell.setCellValue("3140/INFONAVIT 5%");
            cell.setCellStyle(styleamarillo);
//        
            cell = row.createCell(i++);
            cell.setCellValue("3150/SAR");
            cell.setCellStyle(styleamarillo);
//        
            cell = row.createCell(i++);
            cell.setCellValue("3160/CESANTIA Y VEJEZ");
            cell.setCellStyle(styleamarillo);
//        
        }
//        
        cell = row.createCell(i++);
        cell.setCellValue("3120/IMPUESTO ESTATAL NOMINAS");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("TOTAL CARGA SOCIAL");
        cell.setCellStyle(stylegris);
//        
        if (!isaguinaldo) {
            cell = row.createCell(i++);
            cell.setCellValue("3512/PROVISION DE PTU");
            cell.setCellStyle(styleamarillo);
//        
            cell = row.createCell(i++);
            cell.setCellValue("3514/PROVISION DE PRIMA DE RIESGO");
            cell.setCellStyle(styleamarillo);
//        
            cell = row.createCell(i++);
            cell.setCellValue("1110/SUELDO BASE");
            cell.setCellStyle(styleamarillo);
//        
            cell = row.createCell(i++);
            cell.setCellValue("1115/DIFERENCIA DE SUELDO");
            cell.setCellStyle(styleamarillo);
//        
            cell = row.createCell(i++);
            cell.setCellValue("1120/DIAS PENDIENTES DE SUELDO");
            cell.setCellStyle(styleamarillo);
//        
            cell = row.createCell(i++);
            cell.setCellValue("1124/AYUDA RENTA");
            cell.setCellStyle(styleamarillo);
//            
            cell = row.createCell(i++);
            cell.setCellValue("1130/RETROACTIVO DE SUELDO");
            cell.setCellStyle(styleamarillo);
//        
            cell = row.createCell(i++);
            cell.setCellValue("1140/PERMISO CON GOCE DE SUELDO");
            cell.setCellStyle(styleamarillo);
//        
            cell = row.createCell(i++);
            cell.setCellValue("1150/VACACIONES");
            cell.setCellStyle(styleamarillo);
//        
            cell = row.createCell(i++);
            cell.setCellValue("1160/PRIMA VACACIONAL");
            cell.setCellStyle(styleamarillo);
//        
            cell = row.createCell(i++);
            cell.setCellValue("1161/PRIMA VACACIONAL EXENTA");
            cell.setCellStyle(styleamarillo);
//        
            cell = row.createCell(i++);
            cell.setCellValue("1162/PRIMA VACACIONAL GRAVABLE");
            cell.setCellStyle(styleamarillo);
//        
            cell = row.createCell(i++);
            cell.setCellValue("1210/COMISIONES");
            cell.setCellStyle(styleamarillo);
//        
            cell = row.createCell(i++);
            cell.setCellValue("1220/INCENTIVO");
            cell.setCellStyle(styleamarillo);
//        
            cell = row.createCell(i++);
            cell.setCellValue("1240/BONO ESPECIAL");
            cell.setCellStyle(styleamarillo);
//        
            cell = row.createCell(i++);
            cell.setCellValue("1250/BONO PRODUCTIVIDAD");
            cell.setCellStyle(styleamarillo);
//        
            cell = row.createCell(i++);
            cell.setCellValue("1260/BONO MENSUAL");
            cell.setCellStyle(styleamarillo);
//        
            cell = row.createCell(i++);
            cell.setCellValue("1270/DEVOLUCIÓN DE DESCUENTO INCORRECTO");
            cell.setCellStyle(styleamarillo);
//        
            cell = row.createCell(i++);
            cell.setCellValue("1480/SUBSIDIO AL EMPLEO");
            cell.setCellStyle(styleamarillo);
//        
            cell = row.createCell(i++);
            cell.setCellValue("1490/PAGO POR FALTA JUSTIFICADA");
            cell.setCellStyle(styleamarillo);
//        
            cell = row.createCell(i++);
            cell.setCellValue("1500/VIATICOS");
            cell.setCellStyle(styleamarillo);
//        
            cell = row.createCell(i++);
            cell.setCellValue("1501/VIATICOS NO COMPROBADOS");
            cell.setCellStyle(styleamarillo);
//        
            cell = row.createCell(i++);
            cell.setCellValue("1560/PERMISO POR DEFUNCION");
            cell.setCellStyle(styleamarillo);
//     
            cell = row.createCell(i++);
            cell.setCellValue("1570/PERMISO MATRIMONIO");
            cell.setCellStyle(styleamarillo);
//        
            cell = row.createCell(i++);
            cell.setCellValue("1580/PERMISO PATERNIDAD");
            cell.setCellStyle(styleamarillo);
//   
            cell = row.createCell(i++);
            cell.setCellValue("1600/SUBSIDIO POR INCAPACIDAD");
            cell.setCellStyle(styleamarillo);
//        
            cell = row.createCell(i++);
            cell.setCellValue("1610/COMPLEMENTO INCAPACIDAD");
            cell.setCellStyle(styleamarillo);
//        
            cell = row.createCell(i++);
            cell.setCellValue("1614/HERRAMIENTA DE TRABAJO");
            cell.setCellStyle(styleamarillo);
//            
            cell = row.createCell(i++);
            cell.setCellValue("1620/REPARTO DE UTILIDADES");
            cell.setCellStyle(styleamarillo);
//        
            cell = row.createCell(i++);
            cell.setCellValue("1621/REPARTO DE UTILIDADES EXENTO");
            cell.setCellStyle(styleamarillo);
//        
            cell = row.createCell(i++);
            cell.setCellValue("1622/REPARTO DE UTILIDADES GRAVABLE");
            cell.setCellStyle(styleamarillo);
//            
            cell = row.createCell(i++);
            cell.setCellValue("1630/ISPT A COMPENSAR POR AJUSTE ANUAL");
            cell.setCellStyle(styleamarillo);

            cell = row.createCell(i++);
            cell.setCellValue("1640/ISR A FAVOR AJUSTE ANUAL");
            cell.setCellStyle(styleamarillo);

            cell = row.createCell(i++);
            cell.setCellValue("1641/FONDO DE AHORRO EMPRESA");
            cell.setCellStyle(styleamarillo);

            cell = row.createCell(i++);
            cell.setCellValue("1670/LIQUIDACION FONDO DE AHORRO EMPLEADO");
            cell.setCellStyle(styleamarillo);
//            
            cell = row.createCell(i++);
            cell.setCellValue("1710/LIQUIDACION FONDO DE AHORRO EMPRESA");
            cell.setCellStyle(styleamarillo);
//            
            cell = row.createCell(i++);
            cell.setCellValue("1720/INTERESES FONDO DE AHORRO");
            cell.setCellStyle(styleamarillo);
//        
        } else {
            cell = row.createCell(i++);
            cell.setCellValue("1660/AGUINALDO");
            cell.setCellStyle(styleamarillo);
//        
            cell = row.createCell(i++);
            cell.setCellValue("1661/AGUINALDO EXENTO");
            cell.setCellStyle(styleamarillo);
//        
            cell = row.createCell(i++);
            cell.setCellValue("1662/AGUINALDO GRAVABLE");
            cell.setCellStyle(styleamarillo);
        }

        cell = row.createCell(i++);
        cell.setCellValue("TOTAL PERCEPCIONES");
        cell.setCellStyle(stylegris);
//        
        if (!isaguinaldo) {
            cell = row.createCell(i++);
            cell.setCellValue("2110/FALTA JUSTIFICADA");
            cell.setCellStyle(styleamarillo);
//        
            cell = row.createCell(i++);
            cell.setCellValue("2120/FALTA INJUSTIFICADA");
            cell.setCellStyle(styleamarillo);
//        
            cell = row.createCell(i++);
            cell.setCellValue("2130/PERMISO SIN GOCE DE SUELDO");
            cell.setCellStyle(styleamarillo);
//        
            cell = row.createCell(i++);
            cell.setCellValue("2210/INCAPACIDAD POR ENFERMEDAD GENERAL");
            cell.setCellStyle(styleamarillo);
//        
            cell = row.createCell(i++);
            cell.setCellValue("2220/INCAPACIDAD POR ACCIDENTE DE TRABAJO");
            cell.setCellStyle(styleamarillo);
//        
            cell = row.createCell(i++);
            cell.setCellValue("2230/INCAPACIDAD POR MATERNIDAD");
            cell.setCellStyle(styleamarillo);
//        
            cell = row.createCell(i++);
            cell.setCellValue("2240/INCAPACIDAD POR ENFERMEDAD PROFESIONAL");
            cell.setCellStyle(styleamarillo);
//        
//        
            cell = row.createCell(i++);
            cell.setCellValue("2650/ANTICIPO DE SUELDO");
            cell.setCellStyle(styleamarillo);
//        
            cell = row.createCell(i++);
            cell.setCellValue("2260/INCAPACIDAD POR ACCIDENTE EN TRAYECTO");
            cell.setCellStyle(styleamarillo);
        }
//        
        cell = row.createCell(i++);
        cell.setCellValue("2310/IMPUESTO SOBRE LA RENTA NETO");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("2710/PENSION ALIMENTICIA");
        cell.setCellStyle(styleamarillo);
//        
        if (!isaguinaldo) {
            cell = row.createCell(i++);
            cell.setCellValue("2311/IMPUESTO SOBRE LA RENTA ANTES SE");
            cell.setCellStyle(styleamarillo);
//        
            cell = row.createCell(i++);
            cell.setCellValue("2410/CUOTA IMSS TRABAJADOR");
            cell.setCellStyle(styleamarillo);
//        
            cell = row.createCell(i++);
            cell.setCellValue("2630/PRESTAMO PERSONAL");
            cell.setCellStyle(styleamarillo);
            //        
            cell = row.createCell(i++);
            cell.setCellValue("2631/PRESTAMO AXS");
            cell.setCellStyle(styleamarillo);
//        
            cell = row.createCell(i++);
            cell.setCellValue("2637/AJUSTE PENSION ALIMENTICIA PERIODO ANTERIOR");
            cell.setCellStyle(styleamarillo);
//            
            cell = row.createCell(i++);
            cell.setCellValue("2660/DESCUENTO GENERAL");
            cell.setCellStyle(styleamarillo);
            //        
            cell = row.createCell(i++);
            cell.setCellValue("2661/OTROS DESCUENTOS");
            cell.setCellStyle(styleamarillo);
//        
            cell = row.createCell(i++);
            cell.setCellValue("2710/PENSION ALIMENTICIA");
            cell.setCellStyle(styleamarillo);
//        
            cell = row.createCell(i++);
            cell.setCellValue("2715/ADEUDO INFONAVIT");
            cell.setCellStyle(styleamarillo);
//            
            cell = row.createCell(i++);
            cell.setCellValue("2720/DESCUENTO INFONAVIT");
            cell.setCellStyle(styleamarillo);
//        
            cell = row.createCell(i++);
            cell.setCellValue("2721/DESCUENTO FONACOT");
            cell.setCellStyle(styleamarillo);
//        
            cell = row.createCell(i++);
            cell.setCellValue("2730/SEGURO DE DANOS INFONAVIT");
            cell.setCellStyle(styleamarillo);
//        
            cell = row.createCell(i++);
            cell.setCellValue("2810/DESCUENTO POR COMEDOR");
            cell.setCellStyle(styleamarillo);
//        
            cell = row.createCell(i++);
            cell.setCellValue("2820/DESCUENTO ESTACIONAMIENTO");
            cell.setCellStyle(styleamarillo);
//        
            cell = row.createCell(i++);
            cell.setCellValue("2830/DESCUENTO VALES");
            cell.setCellStyle(styleamarillo);
        }
//        
        cell = row.createCell(i++);
        cell.setCellValue("TOTAL DEDUCCIONES");
        cell.setCellStyle(stylegris);
//        
        cell = row.createCell(i++);
        cell.setCellValue("NETO A PAGAR");
        cell.setCellStyle(styleamarillo);
//        
        i++;
//        cell = row.createCell(i++);
//        cell.setCellValue("PROVISION FINIQUITO+ISN");
//        cell.setCellStyle(styleazul);
//        
        cell = row.createCell(i++);
        cell.setCellValue("COSTO PERSONAL");
        cell.setCellStyle(styleazul);
//        
        cell = row.createCell(i++);
        cell.setCellValue("FEE");
        cell.setCellStyle(styleazul);
//        
        cell = row.createCell(i++);
        cell.setCellValue("TOTAL POR PERSONA");
        cell.setCellStyle(styleazul);
//        
        cell = row.createCell(i++);
        cell.setCellValue("I.V.A. 16%");
        cell.setCellStyle(styleazul);
//        
        cell = row.createCell(i++);
        cell.setCellValue("RETENCION I.V.A. 6%");
        cell.setCellStyle(styleazul);
//        
        cell = row.createCell(i++);
        cell.setCellValue("TOTAL FACTURA");
        cell.setCellStyle(styleazul);
//        
        if (!isaguinaldo) {
            i++;
            cell = row.createCell(i++);
            cell.setCellValue("DESPENSA");
            cell.setCellStyle(styleazul);
//        
            cell = row.createCell(i++);
            cell.setCellValue("COMISIÓN DESPENSA");
            cell.setCellStyle(styleazul);
//        
            cell = row.createCell(i++);
            cell.setCellValue("FEE");
            cell.setCellStyle(styleazul);
//        
            cell = row.createCell(i++);
            cell.setCellValue("TOTAL VALES");
            cell.setCellStyle(styleazul);
//        
            cell = row.createCell(i++);
            cell.setCellValue("I.V.A. 16%");
            cell.setCellStyle(styleazul);
//        
            cell = row.createCell(i++);
            cell.setCellValue("TOTAL FACTURA");
            cell.setCellStyle(styleazul);
        }
//////        
        Iterator<CifrasNomina> iter = this.listadoNomina.iterator();
        while (iter.hasNext()) {
            CifrasNomina n = iter.next();
            if (maparelaciones.get(n.getIdrellab().toString()) == null) {
                continue;
            }
//            
            i = 0;
////            
            Map<String, Concepto> mapaconceptos = new HashMap<>();
            for (Concepto c : n.getConceptosPercepciones()) {
                mapaconceptos.put(c.getConcepto(), c);
            }
            for (Concepto c : n.getConceptosDeducciones()) {
                mapaconceptos.put(c.getConcepto(), c);
            }
            for (Concepto c : n.getConceptosProvisiones()) {
                mapaconceptos.put(c.getConcepto(), c);
            }
////            
            RelacionLaboral rellab = maparelaciones.get(n.getIdrellab().toString());
            row = sheet.createRow(j);
//            
            cell = row.createCell(i++);
            cell.setCellValue(n.getNumeroempleado());
            cell = row.createCell(i++);
            cell.setCellValue(n.getNombreempleado());
////            
            String aux;
//            
            if (rellab.getIdregistropatronal() != null) {
                aux = rellab.getIdregistropatronal().getRegistropatronal();
            } else {
                aux = "";
            }
            cell = row.createCell(i++);
            cell.setCellValue(aux);
//          ZONA ECONOMICA
            RelacionLaboralPosicion pos = rellab.getIdrelacionlaboralposicion();
            if (pos != null
                    && pos.getIdZonaEconomica() != null) {
                aux = pos.getIdZonaEconomica().getZonaEconomica();
            } else {
                aux = "";
            }
            cell = row.createCell(i++);
            cell.setCellValue(aux);
//          AREA  
            if (rellab.getIdregistropatronal() != null) {
                aux = rellab.getIdregistropatronal().getComentarios();
            } else {
                aux = "";
            }
            cell = row.createCell(i++);
            cell.setCellValue(aux);
//          CVE CENTRO DE COSTOS
            if (pos != null
                    && pos.getIdcentrocosto() != null) {
                aux = pos.getIdcentrocosto().getCentroCosto();
            } else {
                aux = "";

            }
            cell = row.createCell(i++);
            cell.setCellValue(aux);
//          DESCR CENTRO DE COSTOS  
            if (pos != null
                    && pos.getIdcentrocosto() != null) {
                aux = pos.getIdcentrocosto().getNombre();
            } else {
                aux = "";

            }
            cell = row.createCell(i++);
            cell.setCellValue(aux);
//          CVE DEPTO  
            if (pos != null
                    && pos.getIddepartamento() != null) {
                aux = pos.getIddepartamento().getDepartamento();
            } else {
                aux = "";

            }
            cell = row.createCell(i++);
            cell.setCellValue(aux);
//          DESCR DEPTO  
            if (pos != null
                    && pos.getIddepartamento() != null) {
                aux = pos.getIddepartamento().getNombre();
            } else {
                aux = "";
            }
            cell = row.createCell(i++);
            cell.setCellValue(aux);
//          CVE PUESTO
            if (pos != null
                    && pos.getIdpuesto() != null) {
                aux = pos.getIdpuesto().getPuesto();
            } else {
                aux = "";
            }
            cell = row.createCell(i++);
            cell.setCellValue(aux);
//          DESCR PUESTO
            if (pos != null
                    && pos.getIdpuesto() != null) {
                aux = pos.getIdpuesto().getNombre();
            } else {
                aux = "";
            }
            cell = row.createCell(i++);
            cell.setCellValue(aux);
//          SALARIO DIARIO  
            cell = row.createCell(i++);
            cell.setCellValue(nformat.format(rellab.getSalarioDiario()));
//          SALARIO DIARIO INTEGRADO  
            cell = row.createCell(i++);
            cell.setCellValue(nformat.format(rellab.getSalarioDiarioIntegrado()));
//          NSS
            cell = row.createCell(i++);
            cell.setCellValue(rellab.getIdempleado().getAfiliacion());
//          VALES DESPENSA
            double valesdespensa = 0.0;
            if (!isaguinaldo) {
                if (mapaconceptos.get("1510") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("1510").getValor()));
                    valesdespensa = mapaconceptos.get("1510").getValor();
                }
                i++;
                if (mapaconceptos.get("1511") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("1511").getValor()));
                }
                i++;
                if (mapaconceptos.get("3500") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("3500").getValor()));
                }
                i++;
                if (mapaconceptos.get("3510") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("3510").getValor()));
                }
                i++;
                if (mapaconceptos.get("3511") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("3511").getValor()));
                }
                i++;
                if (mapaconceptos.get("3220") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("3220").getValor()));
                }
                i++;
                if (mapaconceptos.get("1161") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("1161").getValor()));
                }
                i++;
                if (mapaconceptos.get("1162") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("1162").getValor()));
                }
                i++;
                if (mapaconceptos.get("3110") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("3110").getValor()));
                }
                i++;

                if (mapaconceptos.get("3107") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("3107").getValor()));
                }
                i++;
                if (mapaconceptos.get("3106") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("3106").getValor()));
                }
                i++;
                if (mapaconceptos.get("3101") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("3101").getValor()));
                }
                i++;
                if (mapaconceptos.get("3102") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("3102").getValor()));
                }
                i++;
                if (mapaconceptos.get("3103") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("3103").getValor()));
                }
                i++;
                if (mapaconceptos.get("3104") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("3104").getValor()));
                }
                i++;
                if (mapaconceptos.get("3105") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("3105").getValor()));
                }
                i++;
                if (mapaconceptos.get("3140") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("3140").getValor()));
                }
                i++;
                if (mapaconceptos.get("3150") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("3150").getValor()));
                }
                i++;
                if (mapaconceptos.get("3160") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("3160").getValor()));
                }
                i++;
            }
            if (mapaconceptos.get("3120") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("3120").getValor()));
            }
            i++;
            cell = row.createCell(i);
            cell.setCellValue(nformat.format(n.getProvisiones()));
//            
            if (!isaguinaldo) {
                i++;

                if (mapaconceptos.get("3512") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("3512").getValor()));
                }
                i++;
                if (mapaconceptos.get("3514") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("3514").getValor()));
                }
                i++;
                if (mapaconceptos.get("1110") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("1110").getValor()));
                }
                i++;
                if (mapaconceptos.get("1115") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("1110").getValor()));
                }
                i++;
                if (mapaconceptos.get("1120") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("1120").getValor()));
                }
                i++;
                if (mapaconceptos.get("1124") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("1124").getValor()));
                }
                i++;
                if (mapaconceptos.get("1130") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("1130").getValor()));
                }
                i++;
                if (mapaconceptos.get("1140") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("1140").getValor()));
                }
                i++;
                if (mapaconceptos.get("1150") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("1150").getValor()));
                }
                i++;
                if (mapaconceptos.get("1160") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("1160").getValor()));
                }
                i++;
                if (mapaconceptos.get("1161") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("1161").getValor()));
                }
                i++;
                if (mapaconceptos.get("1162") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("1162").getValor()));
                }
                i++;
                if (mapaconceptos.get("1210") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("1210").getValor()));
                }
                i++;
                if (mapaconceptos.get("1220") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("1220").getValor()));
                }
                i++;
                if (mapaconceptos.get("1240") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("1240").getValor()));
                }
                i++;
                if (mapaconceptos.get("1250") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("1250").getValor()));
                }
                i++;
                if (mapaconceptos.get("1260") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("1260").getValor()));
                }
                i++;
                if (mapaconceptos.get("1270") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("1270").getValor()));
                }
                i++;
                if (mapaconceptos.get("1480") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("1480").getValor()));
                }
                i++;
                if (mapaconceptos.get("1490") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("1490").getValor()));
                }
                i++;
                if (mapaconceptos.get("1500") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("1500").getValor()));
                }
                i++;
                if (mapaconceptos.get("1501") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("1501").getValor()));
                }
                i++;
                if (mapaconceptos.get("1560") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("1560").getValor()));
                }
                i++;
                if (mapaconceptos.get("1570") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("1570").getValor()));
                }
                i++;
                if (mapaconceptos.get("1580") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("1580").getValor()));
                }
                i++;
                if (mapaconceptos.get("1600") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("1600").getValor()));
                }
                i++;
                if (mapaconceptos.get("1610") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("1610").getValor()));
                }
                i++;
                if (mapaconceptos.get("1614") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("1614").getValor()));
                }
                i++;
                if (mapaconceptos.get("1620") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("1620").getValor()));
                }
                i++;
                if (mapaconceptos.get("1621") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("1621").getValor()));
                }
                i++;
                if (mapaconceptos.get("1622") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("1622").getValor()));
                }
                i++;
                if (mapaconceptos.get("1630") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("1630").getValor()));
                }
                i++;
                if (mapaconceptos.get("1640") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("1640").getValor()));
                }
                i++;
                if (mapaconceptos.get("1641") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("1641").getValor()));
                }
                i++;
                if (mapaconceptos.get("1670") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("1670").getValor()));
                }
                i++;
                if (mapaconceptos.get("1710") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("1710").getValor()));
                }
                i++;
                if (mapaconceptos.get("1720") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("1720").getValor()));
                }
            } else {
                i++;
                if (mapaconceptos.get("1660") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("1660").getValor()));
                }
                i++;
                if (mapaconceptos.get("1661") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("1661").getValor()));
                }
                i++;
                if (mapaconceptos.get("1662") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("1662").getValor()));
                }
            }
            i++;
            cell = row.createCell(i);
            cell.setCellValue(nformat.format(n.getPercepciones()));
            if (!isaguinaldo) {
                i++;
                if (mapaconceptos.get("2110") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("2110").getValor()));
                }
                i++;
                if (mapaconceptos.get("2120") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("2120").getValor()));
                }
                i++;
                if (mapaconceptos.get("2130") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("2130").getValor()));
                }
                i++;
                if (mapaconceptos.get("2210") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("2210").getValor()));
                }
                i++;
                if (mapaconceptos.get("2220") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("2220").getValor()));
                }
                i++;
                if (mapaconceptos.get("2230") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("2230").getValor()));
                }
                i++;
                if (mapaconceptos.get("2240") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("2240").getValor()));
                }
                i++;
                if (mapaconceptos.get("2650") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("2650").getValor()));
                }
                i++;
                if (mapaconceptos.get("2260") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("2260").getValor()));
                }
            }
            i++;
            if (mapaconceptos.get("2310") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("2310").getValor()));
            }
            i++;
            if (mapaconceptos.get("2710") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("2310").getValor()));
            }
            if (!isaguinaldo) {
                i++;
                if (mapaconceptos.get("2311") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("2311").getValor()));
                }
                i++;
                if (mapaconceptos.get("2410") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("2410").getValor()));
                }
                i++;
                if (mapaconceptos.get("2630") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("2630").getValor()));
                }
                i++;
                if (mapaconceptos.get("2631") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("2631").getValor()));
                }
                i++;
                if (mapaconceptos.get("2637") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("2637").getValor()));
                }
                i++;
                if (mapaconceptos.get("2660") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("2660").getValor()));
                }
                i++;
                if (mapaconceptos.get("2661") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("2661").getValor()));
                }
                i++;
                if (mapaconceptos.get("2710") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("2710").getValor()));
                }
                i++;
                if (mapaconceptos.get("2715") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("2715").getValor()));
                }
                i++;
                if (mapaconceptos.get("2720") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("2720").getValor()));
                }
                i++;
                if (mapaconceptos.get("2721") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("2721").getValor()));
                }
                i++;
                if (mapaconceptos.get("2730") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("2730").getValor()));
                }
                i++;
                if (mapaconceptos.get("2810") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("2810").getValor()));
                }
                i++;
                if (mapaconceptos.get("2820") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("2820").getValor()));
                }
                i++;
                if (mapaconceptos.get("2830") != null) {
                    cell = row.createCell(i);
                    cell.setCellValue(nformat.format(mapaconceptos.get("2830").getValor()));
                }
            }
            i++;
            cell = row.createCell(i);
            cell.setCellValue(nformat.format(n.getDeducciones()));
//            
            i++;
            cell = row.createCell(i);
            cell.setCellValue(nformat.format(n.getPercepciones() - n.getDeducciones()));
            i++;
//            
//            i++;
//            double provisionfiniquito = ((n.getPercepciones() / 24) * 1.03) + (n.getPercepciones() * .02);
//            cell = row.createCell(i);
//            cell.setCellValue(nformat.format(provisionfiniquito));
//            
            i++;
            double costopersonal = n.getProvisiones() + n.getPercepciones();
            cell = row.createCell(i);
            cell.setCellValue(nformat.format(costopersonal));

            i++;
            double fee = costopersonal * .05;
            cell = row.createCell(i);
            cell.setCellValue(nformat.format(fee));

            i++;
            double costoporpersona = costopersonal + fee;
            cell = row.createCell(i);
            cell.setCellValue(nformat.format(costoporpersona));

            i++;
            double iva16 = costoporpersona * .16;
            cell = row.createCell(i);
            cell.setCellValue(nformat.format(iva16));

            i++;
            double iva6 = costoporpersona * .06;
            cell = row.createCell(i);
            cell.setCellValue(nformat.format(iva6));

            i++;
            double totalfactura = costoporpersona + iva16 - iva6;
            cell = row.createCell(i);
            cell.setCellValue(nformat.format(totalfactura));
            i++;
            if (!isaguinaldo) {
                i++;
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(valesdespensa));

                i++;
                double comisionvales = valesdespensa * 0.025;
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(comisionvales));

                i++;
                double feevales = (comisionvales + valesdespensa) * .01;
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(feevales));

                i++;
                double totalvales = valesdespensa + comisionvales + feevales;
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(totalvales));

                i++;
                double ivavales = totalvales * .16;
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(ivavales));

                i++;
                double facturavales = totalvales + ivavales;
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(facturavales));
            }
////            
            j++;
        }
//
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

    public void descargarPAC(boolean retimbrado) throws FileNotFoundException, GeneralSecurityException, IOException {
//        
        GrupoPago gp = ControladorWS.getInstance().findGrupoPagoById(this.selectedGrupoPago);
//        
        boolean aplicasaldos = ControladorWS.getInstance().aplicaSaldosReciboNominas();
//         
        Map<String, List<VistaSaldoCreditos>> saldocreditos;
        Map<String, ConceptoAcumulado> saldofondoahorro;
        Map<String, ConceptoAcumulado> saldocajaahorro;
        Map<String, SaldoVacaciones> saldovacaciones;
//         
        if (aplicasaldos) {
            saldocreditos = ControladorWS.getInstance().getSaldoCreditos(gp.getIdgrupopago());
            saldofondoahorro = ControladorWS.getInstance().getImportesFondoAhorro(gp.getIdgrupopago());
            saldocajaahorro = ControladorWS.getInstance().getImportesCajaAhorro(gp.getIdgrupopago());
            saldovacaciones = ControladorWS.getInstance().getImportesSaldoVacaciones(selectedGrupoPago);
        } else {
            saldocreditos = new HashMap<>();
            saldofondoahorro = new HashMap<>();
            saldocajaahorro = new HashMap<>();
            saldovacaciones = new HashMap<>();
        }
//            
        Periodo per = ControladorWS.getInstance().findPeriodoById(this.selectedListadoNomina.get(0).getIdperiodo());
//            
        HiloReciboNomina hilo = new HiloReciboNomina();
        FacesMessage msg = null;
        boolean isValido = true;
//      
        if (retimbrado) {
            for (CifrasNomina c : this.selectedListadoNomina) {
                if (c.getEstatusrecibo() != 2) {
                    msg = Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, "Uno o mas recibos no contiene estatus CANCELADO");
                    isValido = false;
                }
            }
        } else {
            for (CifrasNomina c : this.selectedListadoNomina) {
                if (c.getEstatusrecibo() == 2) {
                    msg = Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, "Uno o mas recibos seleccionados contiene estatus CANCELADO");
                    isValido = false;
                }
            }
        }
        if (isValido) {
            boolean isvalidate = hilo.iniciarProceso(this.selectedListadoNomina, gp, per, this.selectedTipoproceso,
                    saldocreditos, saldofondoahorro, saldocajaahorro, saldovacaciones, aplicasaldos, retimbrado);
            this.respuesta = new ArrayList<>(hilo.getRespuesta());
//        
            if (isvalidate) {
                msg = Util.getFacesMessage(FacesMessage.SEVERITY_INFO, "Operación Exitosa!");
            } else {
                msg = Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, "La operación falló");
            }
//
            this.consultar(null);
        }

//        
        this.generarMsg(msg, msg.getSeverity().equals(FacesMessage.SEVERITY_INFO));
//        
    }

    public void updateEmpleados(ActionEvent event) {
        this.selectedRelaciones = null;
//        
        this.mapaRelacionesLaborales = new HashMap<>();
//        
        List<CifrasNomina> relacionesAux =
                ControladorWS.getInstance().getRelacionesLaboralesXNomina(this.selectedGrupoPago, this.selectedTipoproceso,
                this.selectedestatus, this.periodo, this.anio);
//        
        this.relaciones = new ArrayList<>();
//        
        for (CifrasNomina rl : relacionesAux) {
            String llave = rl.getNumeroempleado() + " - " + rl.getNombreempleado();
//            
            if (this.selectedestatus == null) {
                if (this.mapaRelacionesLaborales.get(llave) == null) {
                    System.out.println("Llave.... " + llave);
                    this.mapaRelacionesLaborales.put(llave, rl.getIdrellab().toString());
                    this.relaciones.add(llave);
                }
                continue;
            }
//            
            switch (this.selectedestatus.toString()) {
                case "0":
                    if (rl.getEstatus().equals("0")
                            && this.mapaRelacionesLaborales.get(llave) == null) {
                        System.out.println("Llave.... " + llave);
                        this.mapaRelacionesLaborales.put(llave, rl.getIdrellab().toString());
                        this.relaciones.add(llave);
                    }
                    break;
                case "1":
                    if (rl.getEstatus().equals("1")
                            && this.mapaRelacionesLaborales.get(llave) == null) {
                        System.out.println("Llave.... " + llave);
                        this.mapaRelacionesLaborales.put(llave, rl.getIdrellab().toString());
                        this.relaciones.add(llave);
                    }
                    break;
            }
        }
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
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("isValidate", isValidate);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    @Override
    public String getInformacion() {
        return CeniccoUtil.getInformacion(this.listadoNomina.size());
    }

    public List<CifrasNomina> getFilteredListadoNomina() {
        return filteredListadoNomina;
    }

    public void setFilteredListadoNomina(List<CifrasNomina> filteredListadoNomina) {
        this.filteredListadoNomina = filteredListadoNomina;
    }

    public Integer getSelectedGrupoPago() {
        return selectedGrupoPago;
    }

    public void setSelectedGrupoPago(Integer selectedGrupoPago) {
        this.selectedGrupoPago = selectedGrupoPago;
    }

    public Integer getPeriodo() {
        return periodo;
    }

    public void setPeriodo(Integer periodo) {
        this.periodo = periodo;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public List<CifrasNomina> getListadoNomina() {
        return listadoNomina;
    }

    public Double getTotalDeducciones() {
        return totalDeducciones;
    }

    public Double getTotalPercepciones() {
        return totalPercepciones;
    }

    public Double getTotalProvisiones() {
        return totalProvisiones;
    }

    public Double getTotalGravadoIsr() {
        return totalGravadoIsr;
    }

    public Double getTotalGravadoIsn() {
        return totalGravadoIsn;
    }

    public Double getTotalNeto() {
        return totalNeto;
    }

    public Integer getSelectedTipoproceso() {
        return selectedTipoproceso;
    }

    public void setSelectedTipoproceso(Integer selectedTipoproceso) {
        this.selectedTipoproceso = selectedTipoproceso;
    }

    public List<CifrasNomina> getSelectedListadoNomina() {
        return selectedListadoNomina;
    }

    public void setSelectedListadoNomina(List<CifrasNomina> selectedListadoNomina) {
        this.selectedListadoNomina = selectedListadoNomina;
    }

    public String getInformaciontimbres() {
        return CeniccoUtil.getInformacion(this.respuesta.size());
    }

    public List<RespuestaTO> getRespuesta() {
        return respuesta;
    }

    public Integer getSelectedtipo() {
        return selectedtipo;
    }

    public void setSelectedtipo(Integer selectedtipo) {
        this.selectedtipo = selectedtipo;
    }

    public Integer getSelectedestatus() {
        return selectedestatus;
    }

    public void setSelectedestatus(Integer selectedestatus) {
        this.selectedestatus = selectedestatus;
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

    public void descargarReporteNominaCostoEmpresa() {
        String nombreArchivo = "Nomina Costo Empresa - Bind Periodo " + this.periodo;

        System.out.println("Descarga Reporte ReporteNominaCostoEmpresa...");
        byte[] excel = ControladorWS.getInstance().getExcelReporteNominaCostoEmpresa(this.selectedGrupoPago.toString(), this.selectedTipoproceso, this.periodo, null, null, this.anio, null);

        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_LISTADO_NOMINAS);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, nombreArchivo);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, excel);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_XLS);
        context.addCallbackParam("ruta", MyPaths.urlServletDescargarArchivoCsv());
    }

    public void descargarReporteNominaEmpresaBakertilly() {
        GrupoPago gp = ControladorWS.getInstance().findGrupoPagoById(this.selectedGrupoPago);
        Compania compania = gp.getIdcompania();
        String nombreArchivo = "Reporte Nomina Empresa - " + compania.getNombreCorto() + " Periodo " + this.periodo;

        System.out.println("Descarga Reporte ReporteNominaEmpresaBakertilly...");
        byte[] excel = ControladorWS.getInstance().getExcelReporteNominaEmpresaBakertilly(this.selectedGrupoPago.toString(), this.selectedTipoproceso, this.periodo, null, null, this.anio, null);

        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_LISTADO_NOMINAS);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, nombreArchivo);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, excel);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_XLS);
        context.addCallbackParam("ruta", MyPaths.urlServletDescargarArchivoCsv());
    }
}
