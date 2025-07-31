/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lam.cenicco.beans.nominas.fondoahorro;

import com.lam.cenicco.dao.ProcesoDAO;
import com.lam.cenicco.impl.ControladorWS;
import com.lam.cenicco.librerias.utilidades.CeniccoUtil;
import com.lam.cenicco.util.ControladorSesiones;
import com.lam.cenicco.util.Parametros;
import com.lam.cenicco.util.ParametrosReportes;
import com.lam.cenicco.util.Util;
import com.lam.cenicco.util.paths.MyPaths;
import com.lam.cenicco.ws.VistaFondoAhorro;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
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
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.primefaces.context.RequestContext;

/**
 *
 * @author Antonio Durán
 */
public class FondoAhorroCenicco implements ProcesoDAO<VistaFondoAhorro> {

    private Integer periodo;
    private Integer anio;
    private Integer idgrupopago;
    private Integer[] selectedGrupospago;
//    
    private String secuencia;
//    
    private double total;
    private double totalempleado;
    private double totalempresa;
//    
    private String fechaaplicar;
//    
    private List<VistaFondoAhorro> vista;
    private List<VistaFondoAhorro> filteredvista;
    private List<VistaFondoAhorro> selectedvista;

    public FondoAhorroCenicco() {
        if (this.vista == null) {
            this.vista = new ArrayList<>();
        }
        if (this.filteredvista == null) {
            this.filteredvista = new ArrayList<>();
        }

    }

    @Override
    public void consultar(ActionEvent event) {
//        
        this.totalempleado = 0.0;
        this.totalempresa = 0.0;
        this.vista = new ArrayList<>();

        for (Integer i : this.selectedGrupospago) {
            List<VistaFondoAhorro> aux = ControladorWS.getInstance().getFondoAhorro(this.periodo, this.anio, i);
            this.vista.addAll(aux);
        }
//        
        //Solicitud de cambio alpha, el fondo de ahorro se hace con mas de un grupo de pago para la dispersion.
        //this.vista = ControladorWS.getInstance().getFondoAhorro(this.periodo, this.anio, this.idgrupopago);
        this.selectedvista = this.vista;
        this.filteredvista = this.vista;
//        
        Iterator<VistaFondoAhorro> iter = this.vista.iterator();
        while (iter.hasNext()) {
            VistaFondoAhorro v = iter.next();
            this.totalempleado += v.getValor();
            this.totalempresa += v.getValor();
        }
//        
        this.total = this.totalempleado + this.totalempresa;
    }

    @Override
    public void limpiar(ActionEvent event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void create(ActionEvent event) {
        String servicio = ControladorSesiones.getInstance().getUsuarioSession().getServicio();
        switch (servicio) {
            case "ARTSANA":
            case "ARTSANA-TEST":
                createArchivoTexto();
                break;
            case "ALPHA":
                //createArchivoExcel();
                //createArchivoAportaciones();
                getDispersionCargaMovimientos();
                break;
            case Parametros.SERVICIO_KONECTA:
                this.getCsvDispersionBroxel();
                break;
        }
    }

    public void createArchivoTexto() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedWriter bw = null;
//        
        String nombrearchivo;

//            
        nombrearchivo = "A651" + this.fechaaplicar;
//            
        this.totalempleado = 0.0;
        this.totalempresa = 0.0;
//            
        Iterator<VistaFondoAhorro> iter = this.selectedvista.iterator();
        while (iter.hasNext()) {
            VistaFondoAhorro v = iter.next();
            this.totalempleado += v.getValor();
            this.totalempresa += v.getValor();
        }
        this.total = this.totalempleado + this.totalempresa;

        String header = "01|127693|ARTSANA MEXICO S.A DE C.V||" + this.selectedvista.size() + "|" + CeniccoUtil.redondear(this.totalempleado)
                + "|" + CeniccoUtil.redondear(this.totalempresa) + "|" + CeniccoUtil.redondear(this.total) + "|aportacion|" + this.fechaaplicar;
        try {
//            
            bw = new BufferedWriter(new OutputStreamWriter(baos));
            bw.write(header);
            bw.newLine();
            for (VistaFondoAhorro v : this.selectedvista) {
                String linea = "02|" + v.getNumeroempleado() + "|" + CeniccoUtil.redondear(v.getValor()) + "|" + CeniccoUtil.redondear(v.getValor());
                bw.write(linea);
                bw.newLine();
            }
            bw.flush();
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
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, nombrearchivo);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, this.secuencia);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, baos.toByteArray());
        context.addCallbackParam("ruta", MyPaths.urlServletDescargarArchivoTexto());
    }

    public void createArchivoExcel() {
        String nombreArchivo = "APORTACIONES FONDO AHORRO PERIODO " + this.periodo;
        String tipoarchivo = ParametrosReportes.ARCHIVO_XLS;

        DecimalFormat nformat = new DecimalFormat("0.00");
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
        HSSFSheet sheet = workbook.createSheet("Hoja1");

        int i = 0;
        int j = 0;
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
        cell.setCellValue("2610/APORT. FONDO DE AHORRO EMPLEADO");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("2620/APORT. FONDO DE AHORRO EMPRESA");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("RFC");
        cell.setCellStyle(styleamarillo);

        this.totalempleado = 0.0;
        this.totalempresa = 0.0;
        String nombreempleado;

        Iterator<VistaFondoAhorro> iter = this.selectedvista.iterator();
        while (iter.hasNext()) {
            i = 0;
            VistaFondoAhorro v = iter.next();
            this.totalempleado += v.getValor();
            this.totalempresa += v.getValor();
            if (v.getApellidomaterno() != null) {
                nombreempleado = v.getApellidopaterno() + " " + v.getApellidomaterno() + " " + v.getNombre();
            } else {
                nombreempleado = v.getApellidopaterno() + " " + v.getNombre();
            }

            row = sheet.createRow(j);
//            
            cell = row.createCell(i++);
            cell.setCellValue(v.getNumeroempleado());

            cell = row.createCell(i++);
            cell.setCellValue(nombreempleado);

            cell = row.createCell(i++);
            cell.setCellValue(v.getValor());

            cell = row.createCell(i++);
            cell.setCellValue(v.getValor());

            cell = row.createCell(i++);
            cell.setCellValue(v.getRfc());
            j++;
        }
        j++;
        row = sheet.createRow(j);
        cell = row.createCell(2);
        cell.setCellValue(totalempleado);
        cell = row.createCell(3);
        cell.setCellValue(totalempresa);
        j += 3;
        this.total = this.totalempleado + this.totalempresa;
        row = sheet.createRow(j);
        cell = row.createCell(2);
        cell.setCellValue(total);

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

    public void createArchivoAportaciones() {
        String nombreArchivo = "APORTACIONES FONDO AHORRO PERIODO " + this.periodo;
        String tipoarchivo = ParametrosReportes.ARCHIVO_XLS;

        String fechaFormato = fechaaplicar.substring(6, 8) + fechaaplicar.substring(4, 6) + fechaaplicar.substring(0, 4);

        DecimalFormat nformat = new DecimalFormat("0.00");
//        
        HSSFWorkbook workbook = new HSSFWorkbook();
//        
        HSSFSheet sheet = workbook.createSheet("Aportaciones");

        DataFormat fmt = workbook.createDataFormat();
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setDataFormat(fmt.getFormat("@"));

        int i = 0;
        int j = 0;
        int contador = 0;
//        
        HSSFRow row;
        HSSFCell cell;

        this.totalempleado = 0.0;
        this.totalempresa = 0.0;

        Iterator<VistaFondoAhorro> iter = this.selectedvista.iterator();
        while (iter.hasNext()) {
            i = 0;
            VistaFondoAhorro v = iter.next();
            this.totalempleado += (CeniccoUtil.redondear(v.getValor() * 100) / 100);
            this.totalempresa += (CeniccoUtil.redondear(v.getValor() * 100) / 100);

            String valor = "" + (CeniccoUtil.redondear(v.getValor() * 100) / 100);
            if (valor.contains(".")) {
                String[] aux = valor.split("\\.");
                switch (aux[1].length()) {
                    case 1:
                        valor = "0000000".substring(aux[0].length()) + aux[0] + "." + aux[1] + "0";
                        break;
                    default:
                        aux[1] = aux[1].substring(0, 2);
                        valor = "0000000".substring(aux[0].length()) + aux[0] + "." + "00".substring(aux[1].length()) + aux[1];
                        break;
                }
            } else {
                valor = "0000000".substring(v.getValor().toString().length()) + v.getValor().toString() + ".00";
            }

            row = sheet.createRow(j);
//          
            cell = row.createCell(i++);
            cell.setCellStyle(cellStyle);
            cell.setCellValue("04");
//
            cell = row.createCell(i++);
            cell.setCellValue("C00912");
//
            cell = row.createCell(i++);
            cell.setCellValue("");
//
            cell = row.createCell(i++);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(v.getNumeroempleado());
//
            cell = row.createCell(i++);
            cell.setCellValue(v.getRfc());
//
            cell = row.createCell(i++);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(fechaFormato);
//
            cell = row.createCell(i++);
            cell.setCellValue("S");
//
            cell = row.createCell(i++);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(valor);
//
            cell = row.createCell(i++);
            cell.setCellValue("T");
//
            cell = row.createCell(i++);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(valor);

            j++;
            contador++;
        }

        this.total = this.totalempleado + this.totalempresa;
        this.total = CeniccoUtil.redondear(this.total * 100) / 100;

        i = 0;
        row = sheet.createRow(j);
        cell = row.createCell(i++);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("54");

        String contadorStr = "" + contador;
        cell = row.createCell(i++);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("000000".substring(contadorStr.length()) + contadorStr);

        String valor;
        if (("" + this.total).contains(".")) {
            String[] aux = ("" + this.total).split("\\.");
            switch (aux[1].length()) {
                case 1:
                    valor = "0000000000000".substring(aux[0].length()) + aux[0] + "." + aux[1] + "0";
                    break;
                default:
                    aux[1] = aux[1].substring(0, 2);
                    valor = "0000000000000".substring(aux[0].length()) + aux[0] + "." + "00".substring(aux[1].length()) + aux[1];
                    break;
            }
        } else {
            valor = "0000000000000".substring(("" + this.total).length()) + ("" + this.total) + ".00";
        }

        cell = row.createCell(i++);
        cell.setCellStyle(cellStyle);
        cell.setCellValue(valor);

        //
        this.escribirArchivo(workbook, nombreArchivo, tipoarchivo);
    }

    @Override
    public void edit(ActionEvent event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void delete(VistaFondoAhorro obj) {
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

    @Override
    public void init() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public List<VistaFondoAhorro> getVista() {
        return vista;
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

    public List<VistaFondoAhorro> getFilteredvista() {
        return filteredvista;
    }

    public void setFilteredvista(List<VistaFondoAhorro> filteredvista) {
        this.filteredvista = filteredvista;
    }

    public double getTotal() {
        return total;
    }

    public double getTotalempleado() {
        return totalempleado;
    }

    public double getTotalempresa() {
        return totalempresa;
    }

    public String getFechaaplicar() {
        return fechaaplicar;
    }

    public void setFechaaplicar(String fechaaplicar) {
        this.fechaaplicar = fechaaplicar;
    }

    public String getSecuencia() {
        return secuencia;
    }

    public void setSecuencia(String secuencia) {
        this.secuencia = secuencia;
    }

    public List<VistaFondoAhorro> getSelectedvista() {
        return selectedvista;
    }

    public void setSelectedvista(List<VistaFondoAhorro> selectedvista) {
        this.selectedvista = selectedvista;
    }

    public Integer getIdgrupopago() {
        return idgrupopago;
    }

    public void setIdgrupopago(Integer idgrupopago) {
        this.idgrupopago = idgrupopago;
    }

    public Integer[] getSelectedGrupospago() {
        return selectedGrupospago;
    }

    public void setSelectedGrupospago(Integer[] selectedGrupospago) {
        this.selectedGrupospago = selectedGrupospago;
    }

    private void getExcelDispersionBroxel() {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("Aportaciones");

        HSSFRow row = null;
        HSSFCell cell = null;

        int i = 0;
        int j = 0;

        for (VistaFondoAhorro v : this.vista) {
            row = sheet.createRow(j);

            cell = row.createCell(i++);
            cell.setCellValue(v.getNumeroempleado());

            cell = row.createCell(i++);
            cell.setCellValue("1");

            cell = row.createCell(i++);
            cell.setCellValue(v.getValor());

            i = 0;
            j++;
        }

        try {
            String nombreArchivo = "Aportaciones_Fondo_Ahorro_Periodo" + "_" + this.periodo + "_" + this.anio + "_" + this.secuencia;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            byte[] data = baos.toByteArray();

            RequestContext context = RequestContext.getCurrentInstance();
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, nombreArchivo);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_CSV);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, data);
            context.addCallbackParam("ruta", MyPaths.urlServletDescargarArchivoCsv());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getCsvDispersionBroxel() {
        String nombreArchivo = "Aportaciones_Fondo_Ahorro_Periodo" + "_" + this.periodo + "_" + this.anio + "_" + this.secuencia;
        List<String> lineas = new ArrayList<>();
        for (VistaFondoAhorro v : this.vista) {
            StringBuilder fa = new StringBuilder();

            fa.append(v.getNumeroempleado())
                    .append(",")
                    .append("APEMP")
                    .append(",")
                    .append(v.getValor());

            lineas.add(fa.toString());
        }
        for (VistaFondoAhorro v : this.vista) {
            StringBuilder fa = new StringBuilder();

            fa.append(v.getNumeroempleado())
                    .append(",")
                    .append("APCIA")
                    .append(",")
                    .append(v.getValor());

            lineas.add(fa.toString());
        }
        Util.escribirFichero(lineas, nombreArchivo, ParametrosReportes.ARCHIVO_CSV);
    }

    public void getDispersionCargaMovimientos() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        String anio = sdf.format(new Date());

        byte[] movimientosAE = getDispersionCargaMovimientosAE();
        byte[] movimientosAP = getDispersionCargaMovimientosAP();

        try {
            ZipEntry entryAE = new ZipEntry("Formato Layout de Carga de Movimientos " + anio + " 0824 AE" + "." + ParametrosReportes.ARCHIVO_XLS);
            entryAE.setSize(movimientosAE.length);
            entryAE.setTime(System.currentTimeMillis());
            zos.putNextEntry(entryAE);
            zos.write(movimientosAE);
            zos.closeEntry();

            ZipEntry entryAP = new ZipEntry("Formato Layout de Carga de Movimientos " + anio + " 0824 AP" + "." + ParametrosReportes.ARCHIVO_XLS);
            entryAP.setSize(movimientosAP.length);
            entryAP.setTime(System.currentTimeMillis());
            zos.putNextEntry(entryAP);
            zos.write(movimientosAP);
            zos.closeEntry();

            zos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            String nombreArchivo = "Carga de Movimientos" + "_" + this.periodo + "_" + this.anio + "_" + this.secuencia;
            byte[] data = baos.toByteArray();

            RequestContext context = RequestContext.getCurrentInstance();
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, nombreArchivo);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_ZIP);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, data);
            context.addCallbackParam("ruta", MyPaths.urlServletDescargarArchivoCsv());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public byte[] getDispersionCargaMovimientosAE() {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("Movimientos AE");

        DecimalFormat df = new DecimalFormat("#.00");
        df.setRoundingMode(RoundingMode.DOWN);

        HSSFCellStyle styleTitulo = workbook.createCellStyle();
        styleTitulo.setAlignment(HorizontalAlignment.CENTER);
        styleTitulo.setVerticalAlignment(VerticalAlignment.CENTER);
        HSSFFont fontTitulo = workbook.createFont();
        fontTitulo.setFontName(HSSFFont.FONT_ARIAL);
        fontTitulo.setFontHeightInPoints((short) 12);
        fontTitulo.setBold(true);
        styleTitulo.setFont(fontTitulo);

        HSSFCellStyle styleDispersion = workbook.createCellStyle();
        styleDispersion.setAlignment(HorizontalAlignment.CENTER);
        styleDispersion.setVerticalAlignment(VerticalAlignment.CENTER);
        HSSFFont fontDispersion = workbook.createFont();
        fontDispersion.setFontName(HSSFFont.FONT_ARIAL);
        styleDispersion.setFont(fontDispersion);

        HSSFCellStyle styleFuente = workbook.createCellStyle();
        styleFuente.setAlignment(HorizontalAlignment.CENTER);
        styleFuente.setVerticalAlignment(VerticalAlignment.CENTER);
        HSSFFont fontFuente = workbook.createFont();
        fontFuente.setFontName(HSSFFont.FONT_ARIAL);
        fontFuente.setBold(true);
        styleFuente.setFont(fontFuente);

        HSSFCellStyle styleMonto = workbook.createCellStyle();
        styleMonto.setAlignment(HorizontalAlignment.RIGHT);
        styleMonto.setVerticalAlignment(VerticalAlignment.CENTER);
        HSSFFont fontMonto = workbook.createFont();
        fontMonto.setFontName(HSSFFont.FONT_ARIAL);
        styleMonto.setFont(fontMonto);

        int i = 0;
        int j = 0;
        HSSFRow row = sheet.createRow(j++);
        HSSFCell cell = row.createCell(i++);
        cell.setCellValue("Movimiento");
        cell.setCellStyle(styleTitulo);

        cell = row.createCell(i++);
        cell.setCellValue("ID Nomina");
        cell.setCellStyle(styleTitulo);

        cell = row.createCell(i++);
        cell.setCellValue("ID Empleado");
        cell.setCellStyle(styleTitulo);

        cell = row.createCell(i++);
        cell.setCellValue("ID Fuente");
        cell.setCellStyle(styleTitulo);

        cell = row.createCell(i++);
        cell.setCellValue("Monto");
        cell.setCellStyle(styleTitulo);

        cell = row.createCell(i++);
        cell.setCellValue("Intereses");
        cell.setCellStyle(styleTitulo);

        cell = row.createCell(i++);
        cell.setCellValue("Ref Prest");
        cell.setCellStyle(styleTitulo);

        cell = row.createCell(i++);
        cell.setCellValue("Tasa");
        cell.setCellStyle(styleTitulo);

        cell = row.createCell(i++);
        cell.setCellValue("Plazo");
        cell.setCellStyle(styleTitulo);

        i = 0;
        j = 1;

        Iterator<VistaFondoAhorro> iter = this.selectedvista.iterator();
        while (iter.hasNext()) {
            VistaFondoAhorro v = iter.next();
            row = sheet.createRow(j);

            cell = row.createCell(i++);
            cell.setCellValue("D");
            cell.setCellStyle(styleDispersion);

            cell = row.createCell(i++);
            cell.setCellValue("QNA");
            cell.setCellStyle(styleDispersion);

            cell = row.createCell(i++);
            cell.setCellValue(v.getNumeroempleado());
            cell.setCellStyle(styleDispersion);

            cell = row.createCell(i++);
            cell.setCellValue("A01");
            cell.setCellStyle(styleFuente);

            cell = row.createCell(i++);
            cell.setCellValue(df.format(v.getValor()));
            cell.setCellStyle(styleMonto);

            i = 0;
            j++;
        }

        /**
         * Inicio Sheet AutoSizeColumn *
         */
        int total_y = 255;
        for (int x = 0; x <= total_y; x++) {
            sheet.autoSizeColumn(x);
        }

        byte[] data = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            data = baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public byte[] getDispersionCargaMovimientosAP() {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("Movimientos AP");

        DecimalFormat df = new DecimalFormat("#.00");
        df.setRoundingMode(RoundingMode.DOWN);

        HSSFCellStyle styleTitulo = workbook.createCellStyle();
        styleTitulo.setAlignment(HorizontalAlignment.CENTER);
        styleTitulo.setVerticalAlignment(VerticalAlignment.CENTER);
        HSSFFont fontTitulo = workbook.createFont();
        fontTitulo.setFontName(HSSFFont.FONT_ARIAL);
        fontTitulo.setFontHeightInPoints((short) 12);
        fontTitulo.setBold(true);
        styleTitulo.setFont(fontTitulo);

        HSSFCellStyle styleDispersion = workbook.createCellStyle();
        styleDispersion.setAlignment(HorizontalAlignment.CENTER);
        styleDispersion.setVerticalAlignment(VerticalAlignment.CENTER);
        HSSFFont fontDispersion = workbook.createFont();
        fontDispersion.setFontName(HSSFFont.FONT_ARIAL);
        styleDispersion.setFont(fontDispersion);

        HSSFCellStyle styleFuente = workbook.createCellStyle();
        styleFuente.setAlignment(HorizontalAlignment.CENTER);
        styleFuente.setVerticalAlignment(VerticalAlignment.CENTER);
        HSSFFont fontFuente = workbook.createFont();
        fontFuente.setFontName(HSSFFont.FONT_ARIAL);
        fontFuente.setBold(true);
        styleFuente.setFont(fontFuente);

        HSSFCellStyle styleMonto = workbook.createCellStyle();
        styleMonto.setAlignment(HorizontalAlignment.RIGHT);
        styleMonto.setVerticalAlignment(VerticalAlignment.CENTER);
        HSSFFont fontMonto = workbook.createFont();
        fontMonto.setFontName(HSSFFont.FONT_ARIAL);
        styleMonto.setFont(fontMonto);

        int i = 0;
        int j = 0;
        HSSFRow row = sheet.createRow(j++);
        HSSFCell cell = row.createCell(i++);
        cell.setCellValue("Movimiento");
        cell.setCellStyle(styleTitulo);

        cell = row.createCell(i++);
        cell.setCellValue("ID Nomina");
        cell.setCellStyle(styleTitulo);

        cell = row.createCell(i++);
        cell.setCellValue("ID Empleado");
        cell.setCellStyle(styleTitulo);

        cell = row.createCell(i++);
        cell.setCellValue("ID Fuente");
        cell.setCellStyle(styleTitulo);

        cell = row.createCell(i++);
        cell.setCellValue("Monto");
        cell.setCellStyle(styleTitulo);

        cell = row.createCell(i++);
        cell.setCellValue("Intereses");
        cell.setCellStyle(styleTitulo);

        cell = row.createCell(i++);
        cell.setCellValue("Ref Prest");
        cell.setCellStyle(styleTitulo);

        cell = row.createCell(i++);
        cell.setCellValue("Tasa");
        cell.setCellStyle(styleTitulo);

        cell = row.createCell(i++);
        cell.setCellValue("Plazo");
        cell.setCellStyle(styleTitulo);

        i = 0;
        j = 1;

        Iterator<VistaFondoAhorro> iter = this.selectedvista.iterator();
        while (iter.hasNext()) {
            VistaFondoAhorro v = iter.next();
            row = sheet.createRow(j);

            cell = row.createCell(i++);
            cell.setCellValue("D");
            cell.setCellStyle(styleDispersion);

            cell = row.createCell(i++);
            cell.setCellValue("QNA");
            cell.setCellStyle(styleDispersion);

            cell = row.createCell(i++);
            cell.setCellValue(v.getNumeroempleado());
            cell.setCellStyle(styleDispersion);

            cell = row.createCell(i++);
            cell.setCellValue("A02");
            cell.setCellStyle(styleFuente);

            cell = row.createCell(i++);
            cell.setCellValue(df.format(v.getValor()));
            cell.setCellStyle(styleMonto);

            i = 0;
            j++;
        }

        /**
         * Inicio Sheet AutoSizeColumn *
         */
        int total_y = 255;
        for (int x = 0; x <= total_y; x++) {
            sheet.autoSizeColumn(x);
        }

        byte[] data = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            data = baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }
}