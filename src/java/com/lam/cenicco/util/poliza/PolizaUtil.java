/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lam.cenicco.util.poliza;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedHashTreeMap;
import com.google.gson.reflect.TypeToken;
import com.lam.cenicco.impl.ControladorWS;
import com.lam.cenicco.librerias.utilidades.CeniccoUtil;
import com.lam.cenicco.to.ConektaPolizaContableTO;
import com.lam.cenicco.ws.Parametro;
import com.lam.cenicco.util.ParametrosReportes;
import com.lam.cenicco.util.Util;
import com.lam.cenicco.util.paths.MyPaths;
import com.lam.cenicco.ws.CatalogoClaseCuentaContable;
import com.lam.cenicco.ws.CentroCostos;
import com.lam.cenicco.ws.CifrasNomina;
import com.lam.cenicco.ws.Departamento;
import com.lam.cenicco.ws.GrupoPago;
import com.lam.cenicco.ws.IntegracionPolizaContable;
import com.lam.cenicco.ws.Periodo;
import com.lam.cenicco.ws.TipoProceso;
import com.mysql.jdbc.StringUtils;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.faces.context.FacesContext;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.primefaces.context.RequestContext;

/**
 *
 * @author Antonio Durán
 */
public class PolizaUtil {

    public void crearLayoutYamana(List<PolizaTO> header, String nombrearchivo, int periodo, int anio, double totalcargo, double totalabono) {
        DecimalFormat nformat = new DecimalFormat("0.00");
        HSSFWorkbook workbook = new HSSFWorkbook();
//      
        HSSFSheet sheet = workbook.createSheet("PolizaContable" + periodo + anio);

        int j = 10;
        int i = 2;
//        
        HSSFRow row = sheet.createRow(j);
//        
        HSSFCell cell = row.createCell(i);
        cell.setCellValue("Ctro de Ctos Dpto");
        i++;

        cell = row.createCell(i);
        cell.setCellValue("Descripción Ctro de Ctos Dpto");
        i++;

        cell = row.createCell(i);
        cell.setCellValue("Debe");
        i++;

        cell = row.createCell(i);
        cell.setCellValue("Debe");
        i++;

        cell = row.createCell(i);
        cell.setCellValue("Haber");
        i++;

        cell = row.createCell(i);
        cell.setCellValue("Cta");
        i++;

        cell = row.createCell(i);
        cell.setCellValue("Descripción Cta");
        i++;

        cell = row.createCell(i);
        cell.setCellValue("Periodo");
        i++;


        j++;
//        
        for (PolizaTO p : header) {
//            
            Collections.sort(p.getDetalle(), new Comparator<CifrasNomina>() {
                @Override
                public int compare(CifrasNomina c1, CifrasNomina c2) {
                    return c1.getCuentacontable().compareTo(c2.getCuentacontable());
                }
            });

            for (CifrasNomina v : p.getDetalle()) {
                i = 2;
                String[] split = v.getCuentacontable().split("-");
                String depto = split[0];
                String cta = split[1] != null ? split[1] : "0";
                row = sheet.createRow(j);
//CCO+DTO                
                cell = row.createCell(i);
                cell.setCellValue(depto);
                i++;
//DESCRIPCION
                cell = row.createCell(i);
                cell.setCellValue(v.getCentrocostros());
                i++;
//DEBE
                cell = row.createCell(i);
                cell.setCellValue(v.getCargo());
                i++;
//HABER
                cell = row.createCell(i);
                cell.setCellValue(v.getAbono());
                i++;
//CTA
                cell = row.createCell(i);
                cell.setCellValue(cta);
                i++;
//DESC. CTA (Nombre concepto)
                cell = row.createCell(i);
                cell.setCellValue(v.getNombreconcepto());
                i++;
//PERIODO
                cell = row.createCell(i);
                cell.setCellValue("Periodo");
                i++;

                j++;
            }
        }
        i = 5;
//        
        row = sheet.createRow(j);
//        
        cell = row.createCell(i);
        cell.setCellValue("Suma Total");
        i++;
        this.escribirArchivo(workbook, nombrearchivo, ParametrosReportes.ARCHIVO_XLS);

    }

    public void crearLayoutMartilloAemsa(List<PolizaTO> header, String nombrearchivo, int periodo, int anio, double totalcargo, double totalabono) {
        DecimalFormat nformat = new DecimalFormat("0.00");
        HSSFWorkbook workbook = new HSSFWorkbook();
//      
        HSSFSheet sheet = workbook.createSheet("PolizaContable" + periodo + anio);
//        
        CellStyle styleazul = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setFontName(HSSFFont.FONT_ARIAL);
        font.setBoldweight(Short.MAX_VALUE);
        font.setColor(HSSFColor.BLUE.index);
        styleazul.setFont(font);
//        
        CellStyle styleazulnormal = workbook.createCellStyle();
        font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setFontName(HSSFFont.FONT_ARIAL);
        font.setColor(HSSFColor.BLUE.index);
        styleazulnormal.setFont(font);
//        
        CellStyle stylerojo = workbook.createCellStyle();
        font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setFontName(HSSFFont.FONT_ARIAL);
        font.setBoldweight(Short.MAX_VALUE);
        font.setColor(HSSFColor.VIOLET.index);
        stylerojo.setFont(font);
//        
        CellStyle stylerojonormal = workbook.createCellStyle();
        font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setFontName(HSSFFont.FONT_ARIAL);
        font.setColor(HSSFColor.VIOLET.index);
        stylerojonormal.setFont(font);
//        
        CellStyle stylenegro = workbook.createCellStyle();
        font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setFontName(HSSFFont.FONT_ARIAL);
        font.setBoldweight(Short.MAX_VALUE);
        stylenegro.setFont(font);
//        
        CellStyle stylenegronormal = workbook.createCellStyle();
        font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setFontName(HSSFFont.FONT_ARIAL);
        stylenegronormal.setFont(font);
//        
        int j = 6;
        int i = 0;
//        
        HSSFRow row = sheet.createRow(j);
//        
        HSSFCell cell = row.createCell(i);
        cell.setCellValue("Concepto de la Poliza Contable");
        cell.setCellStyle(stylenegro);
        i += 1;
//        
        cell = row.createCell(i);
        cell.setCellValue("Cuenta");
        cell.setCellStyle(stylenegro);
        i += 1;
//        
        cell = row.createCell(i);
        cell.setCellValue("Debito");
        cell.setCellStyle(stylerojo);
        i += 1;
//        
        cell = row.createCell(i);
        cell.setCellValue("Credito");
        cell.setCellStyle(styleazul);
        i += 1;
//        
        cell = row.createCell(i);
        cell.setCellValue("Centro Costos");
        cell.setCellStyle(stylenegro);
        i += 1;
//        
        j += 1;
//        
        for (PolizaTO p : header) {
//            
            Collections.sort(p.getDetalle(), new Comparator<CifrasNomina>() {
                @Override
                public int compare(CifrasNomina c1, CifrasNomina c2) {
                    return c1.getCuentacontable().compareTo(c2.getCuentacontable());
                }
            });

            for (CifrasNomina v : p.getDetalle()) {
                i = 0;
//
                row = sheet.createRow(j);
//                
                cell = row.createCell(i);
                cell.setCellValue(v.getNombreconcepto());
                i += 1;
//                
                cell = row.createCell(i);
                cell.setCellValue(v.getCuentacontable());
                cell.setCellStyle(stylenegronormal);
                i += 1;
//                
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(v.getCargo()));
                cell.setCellStyle(stylerojonormal);
                i += 1;
//                
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(v.getAbono()));
                cell.setCellStyle(styleazulnormal);
                i += 1;
//              
                cell = row.createCell(i);
                cell.setCellValue(v.getCentrocostros());
                cell.setCellStyle(stylenegronormal);
//                
                j += 1;
            }
        }
//        
        i = 1;
//        
        row = sheet.createRow(j);
//        
        cell = row.createCell(i);
        cell.setCellValue("Suma Total");
        cell.setCellStyle(stylenegro);
        i += 1;
//        
        cell = row.createCell(i);
        cell.setCellValue(nformat.format(totalcargo));
        cell.setCellStyle(stylerojo);
        i += 1;
//        
        cell = row.createCell(i);
        cell.setCellValue(nformat.format(totalabono));
        cell.setCellStyle(styleazul);


        this.escribirArchivo(workbook, nombrearchivo, ParametrosReportes.ARCHIVO_XLS);
    }

    public void crearLayoutKUA(List<PolizaTO> header, String nombrearchivo, int periodo, int anio, double totalcargo, double totalabono) {
        HSSFWorkbook workbook = new HSSFWorkbook();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String fecha = sdf.format(new Date());
        DecimalFormat nformat = new DecimalFormat("0.00");
//      
        HSSFSheet sheet = workbook.createSheet("GenJournalLine");
//        
        CellStyle stylenegro = workbook.createCellStyle();
        stylenegro.setFillForegroundColor(HSSFColor.BLUE_GREY.index);
        stylenegro.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
//        
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setFontName(HSSFFont.FONT_ARIAL);
        font.setColor(HSSFColor.WHITE.index);
        font.setBoldweight(Short.MAX_VALUE);
        stylenegro.setFont(font);
//        
        CellStyle stylenegronormal = workbook.createCellStyle();
        font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setFontName(HSSFFont.FONT_ARIAL);
        stylenegronormal.setFont(font);
//        
        int j = 2;
        int i = 0;
//        
        HSSFRow row = sheet.createRow(j);
//        
        HSSFCell cell = row.createCell(i);
        cell.setCellValue("Nombre libro diario");
        cell.setCellStyle(stylenegro);
        i += 1;
//        
        cell = row.createCell(i);
        cell.setCellValue("Nombre sección diario");
        cell.setCellStyle(stylenegro);
        i += 1;
//        
        cell = row.createCell(i);
        cell.setCellValue("Nº línea");
        cell.setCellStyle(stylenegro);
        i += 1;
//        
        cell = row.createCell(i);
        cell.setCellValue("Tipo mov.");
        cell.setCellStyle(stylenegro);
        i += 1;
//        
        cell = row.createCell(i);
        cell.setCellValue("Nº cuenta");
        cell.setCellStyle(stylenegro);
        i += 1;
//        
        cell = row.createCell(i);
        cell.setCellValue("Fecha registro");
        cell.setCellStyle(stylenegro);
        i += 1;
//        
        cell = row.createCell(i);
        cell.setCellValue("Tipo documento");
        cell.setCellStyle(stylenegro);
        i += 1;
//        
        cell = row.createCell(i);
        cell.setCellValue("Nº documento");
        cell.setCellStyle(stylenegro);
        i += 1;
//        
        cell = row.createCell(i);
        cell.setCellValue("Descripción");
        cell.setCellStyle(stylenegro);
        i += 1;
//        
        cell = row.createCell(i);
        cell.setCellValue("Cta. contrapartida");
        cell.setCellStyle(stylenegro);
        i += 1;
//        
        cell = row.createCell(i);
        cell.setCellValue("Cód. divisa");
        cell.setCellStyle(stylenegro);
        i += 1;
//        
        cell = row.createCell(i);
        cell.setCellValue("Importe");
        cell.setCellStyle(stylenegro);
        i += 1;
//        
        cell = row.createCell(i);
        cell.setCellValue("Importe debe");
        cell.setCellStyle(stylenegro);
        i += 1;
//        
        cell = row.createCell(i);
        cell.setCellValue("Importe haber");
        cell.setCellStyle(stylenegro);
        i += 1;
//        
        cell = row.createCell(i);
        cell.setCellValue("Importe ($)");
        cell.setCellStyle(stylenegro);
        i += 1;
//        
        cell = row.createCell(i);
        cell.setCellValue("Cód. dim. acceso dir. 2");
        cell.setCellStyle(stylenegro);
        i += 1;
//        
        cell = row.createCell(i);
        cell.setCellValue("Fecha vencimiento");
        cell.setCellStyle(stylenegro);
        i += 1;
//        
        cell = row.createCell(i);
        cell.setCellValue("Tipo contrapartida");
        cell.setCellStyle(stylenegro);
        i += 1;
//        
        cell = row.createCell(i);
        cell.setCellValue("Tipo regis. contrapartida");
        cell.setCellStyle(stylenegro);
        i += 1;
//        
        cell = row.createCell(i);
        cell.setCellValue("Gr. contable negocio contrap.");
        cell.setCellStyle(stylenegro);
        i += 1;
//        
        cell = row.createCell(i);
        cell.setCellValue("Gr. contable producto contrap.");
        cell.setCellStyle(stylenegro);
        i += 1;
//        
        cell = row.createCell(i);
        cell.setCellValue("Nº documento externo");
        cell.setCellStyle(stylenegro);
        i += 1;
//        
        cell = row.createCell(i);
        cell.setCellValue("Gr. registro IVA neg. contrap.");
        cell.setCellStyle(stylenegro);
        i += 1;
//        
        cell = row.createCell(i);
        cell.setCellValue("Gr. registro IVA prod. contrp.");
        cell.setCellStyle(stylenegro);
        i += 1;
//        
        cell = row.createCell(i);
        cell.setCellValue("UUID");
        cell.setCellStyle(stylenegro);
//        
        j += 1;
//        
        int k = 0;
//        
        Map<String, PolizaTO> mapapoliza = new HashMap<>();

        Collections.sort(header, new Comparator<PolizaTO>() {
            @Override
            public int compare(PolizaTO c1, PolizaTO c2) {
                return c1.getConcepto().compareTo(c2.getConcepto());
            }
        });

        String nodocumento = "PERIODO " + periodo + " " + anio;
//        
        String descripcion =
                header.get(0).getDetalle().get(0).getGrupopago()
                + "-" + header.get(0).getDetalle().get(0).getGrupoconcepto() + " "
                + periodo + "/" + anio;

        for (PolizaTO p : header) {

//
            for (CifrasNomina v : p.getDetalle()) {
                System.out.println("Concepto... " + v.getNumeroconcepto());
//                
                String llave = v.getNumeroconcepto() + " | " + v.getCuentacontable();
                if (mapapoliza.get(llave) == null) {
                    row = sheet.createRow(j);
                    mapapoliza.put(llave, new PolizaTO(v, j));

                    j += 1;
                    k += 1;

                } else {
                    row = sheet.getRow(mapapoliza.get(llave).getId());
//                   
                    mapapoliza.get(llave).addcargo(v.getCargo());
                    mapapoliza.get(llave).addabono(v.getAbono());
                }
//                
                i = 0;
//                
                cell = row.createCell(i);
                cell.setCellValue("GENERAL");
                i += 1;
//                
                cell = row.createCell(i);
                cell.setCellValue("Nomina");
                cell.setCellStyle(stylenegronormal);
                i += 1;
//                
                cell = row.createCell(i);
                cell.setCellValue(String.format("1%04d", k));
                cell.setCellStyle(stylenegronormal);
                i += 1;

//                
                cell = row.createCell(i);
                cell.setCellValue("Proveedor");
                cell.setCellStyle(stylenegronormal);
                i += 1;
//                
                cell = row.createCell(i);
                cell.setCellValue("PNOM0001");
                cell.setCellStyle(stylenegronormal);
                i += 1;
//                
                cell = row.createCell(i);
                cell.setCellValue(fecha);
                cell.setCellStyle(stylenegronormal);
                i += 1;
//                
                cell = row.createCell(i);
                cell.setCellValue("Factura");
                cell.setCellStyle(stylenegronormal);
                i += 1;
//                
                cell = row.createCell(i);
                cell.setCellValue(nodocumento);
                cell.setCellStyle(stylenegronormal);
                i += 1;
//                
                cell = row.createCell(i);
                cell.setCellValue(descripcion);
                cell.setCellStyle(stylenegronormal);
                i += 1;
//                
                cell = row.createCell(i);
                cell.setCellValue(v.getNumeroconcepto());
                cell.setCellStyle(stylenegronormal);
                i += 2;

                double total = mapapoliza.get(llave).getCargo() + mapapoliza.get(llave).getAbono();
//                
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(total));
                cell.setCellStyle(stylenegronormal);
                i += 1;

                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapapoliza.get(llave).getCargo()));
                cell.setCellStyle(stylenegronormal);
                i += 1;

                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapapoliza.get(llave).getAbono()));
                cell.setCellStyle(stylenegronormal);
                i += 1;

                cell = row.createCell(i);
                cell.setCellValue(nformat.format(0));
                cell.setCellStyle(stylenegronormal);
                i += 2;

                cell = row.createCell(i);
                cell.setCellValue(fecha);
                cell.setCellStyle(stylenegronormal);
                i += 1;

                cell = row.createCell(i);
                cell.setCellValue("Cuenta");
                cell.setCellStyle(stylenegronormal);
                i += 1;

                cell = row.createCell(i);
                cell.setCellValue("Compra");
                cell.setCellStyle(stylenegronormal);
                i += 1;

                cell = row.createCell(i);
                cell.setCellValue("NACIONAL");
                cell.setCellStyle(stylenegronormal);
                i += 3;

                cell = row.createCell(i);
                cell.setCellValue("NACIONAL");
                cell.setCellStyle(stylenegronormal);
                i += 1;

                cell = row.createCell(i);
                cell.setCellValue("IVA0%");
                cell.setCellStyle(stylenegronormal);
                i += 1;
//                
                if (v.getUuid() != null && !v.getUuid().equals("")) {
                    cell = row.createCell(i);
                    cell.setCellValue(v.getUuid());
                    cell.setCellStyle(stylenegronormal);
                }

            }
        }
//        
        this.escribirArchivo(workbook, nombrearchivo, ParametrosReportes.ARCHIVO_XLS);

    }

    public void crearLayoutMeax(List<PolizaTO> header,
            String nombrearchivo, TipoProceso tp, int periodo, int anio, double totalcargo, double totalabono) {
        System.out.println("CreaPolizaMeax.... ");
//        
        String tpperiodo = tp.getTipoproceso() + periodo;
//        
        DecimalFormat nformat = new DecimalFormat("0.00");
        HSSFWorkbook workbook = new HSSFWorkbook();
//      
        HSSFSheet sheet = workbook.createSheet("PolizaContable" + periodo + anio);
//        
        CellStyle stylenegro = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setFontName(HSSFFont.FONT_ARIAL);
        font.setBoldweight(Short.MAX_VALUE);
        stylenegro.setFont(font);
//        
        int j = 0;
//        
        HSSFRow row = sheet.createRow(j);
//        
        HSSFCell cell = row.createCell(0);
        cell.setCellValue("COMPANY");
        cell.setCellStyle(stylenegro);
//        
        cell = row.createCell(1);
        cell.setCellValue("DEPARTMENT");
        cell.setCellStyle(stylenegro);
//        
        cell = row.createCell(2);
        cell.setCellValue("ACCOUNT");
        cell.setCellStyle(stylenegro);
//        
        cell = row.createCell(3);
        cell.setCellValue("SUBACCOUNT");
        cell.setCellStyle(stylenegro);
//        
        cell = row.createCell(4);
        cell.setCellValue("BUSINESS_UNIT");
        cell.setCellStyle(stylenegro);
//        
        cell = row.createCell(5);
        cell.setCellValue("INTER-COMPANY");
        cell.setCellStyle(stylenegro);
//        
        cell = row.createCell(6);
        cell.setCellValue("RESERVED1");
        cell.setCellStyle(stylenegro);
//        
        cell = row.createCell(7);
        cell.setCellValue("RESERVED2");
        cell.setCellStyle(stylenegro);
//        
        cell = row.createCell(8);
        cell.setCellValue("Debit");
        cell.setCellStyle(stylenegro);
//        
        cell = row.createCell(9);
        cell.setCellValue("Credit");
        cell.setCellStyle(stylenegro);
//        
        j = j + 1;
//        
        for (PolizaTO p : header) {
//            
            Collections.sort(p.getDetalle(), new Comparator<CifrasNomina>() {
                @Override
                public int compare(CifrasNomina c1, CifrasNomina c2) {
                    return c1.getNumeroconcepto().compareTo(c2.getNumeroconcepto());
                }
            });
//            
            for (CifrasNomina v : p.getDetalle()) {
                String split[] = v.getCuentacontable().split("/");
//                
                if (split.length < 5) {
                    System.out.println("ErrorPoliza... " + v.getCuentacontable());
                    continue;
                }
//                
                row = sheet.createRow(j);

                cell = row.createCell(0);
                cell.setCellValue(split[0]);

                cell = row.createCell(1);
                cell.setCellValue(split[1]);

                cell = row.createCell(2);
                cell.setCellValue(split[2]);

                cell = row.createCell(3);
                cell.setCellValue(split[3]);
//                
                cell = row.createCell(4);
                cell.setCellValue("0");
//                
                cell = row.createCell(5);
                cell.setCellValue("0");
//                
                cell = row.createCell(6);
                cell.setCellValue("0");
//                
                cell = row.createCell(7);
                cell.setCellValue("0");
//                
                cell = row.createCell(8);
                cell.setCellValue(nformat.format(v.getCargo()));
//                
                cell = row.createCell(9);
                cell.setCellValue(nformat.format(v.getAbono()));

                String descripcion = tpperiodo + " - " + split[4] + " - " + v.getNombreconcepto();

                cell = row.createCell(10);
                cell.setCellValue(descripcion);
//                
                j++;
            }
        }
//        
        row = sheet.createRow(j);
//        
        cell = row.createCell(8);
        cell.setCellValue(nformat.format(totalcargo));
        cell.setCellStyle(stylenegro);
//        
        cell = row.createCell(9);
        cell.setCellValue(nformat.format(totalabono));
        cell.setCellStyle(stylenegro);


        this.escribirArchivo(workbook, nombrearchivo, ParametrosReportes.ARCHIVO_XLS);

    }

    public void crearLayoutAlpha(List<PolizaTO> header, String nombrearchivo, int periodo, int anio, double totalcargo, double totalabono) {
        DecimalFormat nformat = new DecimalFormat("0.00");
        HSSFWorkbook workbook = new HSSFWorkbook();
//      
        HSSFSheet sheet = workbook.createSheet("PolizaContable" + periodo + anio);
//        
        CellStyle styleazul = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setFontName(HSSFFont.FONT_ARIAL);
        font.setBoldweight(Short.MAX_VALUE);
        font.setColor(HSSFColor.BLUE.index);
        styleazul.setFont(font);
//        
        CellStyle styleazulnormal = workbook.createCellStyle();
        font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setFontName(HSSFFont.FONT_ARIAL);
        font.setColor(HSSFColor.BLUE.index);
        styleazulnormal.setFont(font);
//        
        CellStyle stylerojo = workbook.createCellStyle();
        font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setFontName(HSSFFont.FONT_ARIAL);
        font.setBoldweight(Short.MAX_VALUE);
        font.setColor(HSSFColor.VIOLET.index);
        stylerojo.setFont(font);
//        
        CellStyle stylerojonormal = workbook.createCellStyle();
        font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setFontName(HSSFFont.FONT_ARIAL);
        font.setColor(HSSFColor.VIOLET.index);
        stylerojonormal.setFont(font);
//        
        CellStyle stylenegro = workbook.createCellStyle();
        font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setFontName(HSSFFont.FONT_ARIAL);
        font.setBoldweight(Short.MAX_VALUE);
        stylenegro.setFont(font);
//        
        CellStyle stylenegronormal = workbook.createCellStyle();
        font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setFontName(HSSFFont.FONT_ARIAL);
        stylenegronormal.setFont(font);
//        
        int j = 0;

//        
        HSSFRow row = sheet.createRow(j);
//        
        HSSFCell cell = row.createCell(0);
        cell.setCellValue("Cuenta");
        cell.setCellStyle(stylenegro);
//        
        cell = row.createCell(1);
        cell.setCellValue("Cargo");
        cell.setCellStyle(stylerojo);
//        
        cell = row.createCell(2);
        cell.setCellValue("Abono");
        cell.setCellStyle(styleazul);
//        
        j = j + 1;
//        
        Map<String, PolizaTO> mapapoliza = new HashMap<>();
//        
        for (PolizaTO p : header) {
//            
            Collections.sort(p.getDetalle(), new Comparator<CifrasNomina>() {
                @Override
                public int compare(CifrasNomina c1, CifrasNomina c2) {
                    return c1.getCuentacontable().compareTo(c2.getCuentacontable());
                }
            });

            for (CifrasNomina v : p.getDetalle()) {
//                
                String llave = v.getCuentacontable();
//                
                if (mapapoliza.get(llave) == null) {
                    row = sheet.createRow(j);
                    mapapoliza.put(llave, new PolizaTO(v, j));
                    j = j + 1;
                } else {
                    row = sheet.getRow(mapapoliza.get(llave).getId());
//                   
                    mapapoliza.get(llave).addcargo(v.getCargo());
                    mapapoliza.get(llave).addabono(v.getAbono());
                }
//
                cell = row.createCell(0);
                cell.setCellValue(v.getCuentacontable());
                cell.setCellStyle(stylenegronormal);
//                    
                cell = row.createCell(1);
                cell.setCellValue(nformat.format(mapapoliza.get(llave).getCargo()));
                cell.setCellStyle(stylerojonormal);
//                    
                cell = row.createCell(2);
                cell.setCellValue(nformat.format(mapapoliza.get(llave).getAbono()));
                cell.setCellStyle(styleazulnormal);
//                

            }
        }
//        
        j += 1;
//        
        row = sheet.createRow(j);
//        
        cell = row.createCell(0);
        cell.setCellValue("Suma Total");
        cell.setCellStyle(stylenegro);
//        
        cell = row.createCell(1);
        cell.setCellValue(nformat.format(totalcargo));
        cell.setCellStyle(stylerojo);
//        
        cell = row.createCell(2);
        cell.setCellValue(nformat.format(totalabono));
        cell.setCellStyle(styleazul);


        this.escribirArchivo(workbook, nombrearchivo, ParametrosReportes.ARCHIVO_XLS);

    }

    public void escribirArchivo(HSSFWorkbook book, String nombreArchivo, String tipoarchivo) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            book.write(baos);
            byte[] data = baos.toByteArray();
            RequestContext context = RequestContext.getCurrentInstance();
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, nombreArchivo);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, tipoarchivo);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, data);
            context.addCallbackParam("ruta", MyPaths.urlServletDescargarArchivoCsv());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> crearLayoutMelmex(List<PolizaTO> header) {
        List<String> lineas = new ArrayList<>();
        DecimalFormat df = new DecimalFormat("###,###,##0.00");
//        
        String encabezado = "Codificacion,No. Concepto,Concepto,Cargo,Abono";
        lineas.add(encabezado);
        for (PolizaTO p : header) {
            for (CifrasNomina v : p.getDetalle()) {
//                
                String cuentacontable = "          ";
                String departamento = "              ";
                String centrocostosap = "              ";
                String tiponomina = "             ";
                String division = "            ";

                if (!v.getCuentacontable().contains("/")) {
                    cuentacontable = v.getCuentacontable() + cuentacontable.substring(v.getCuentacontable().length());
                } else {
                    String[] split = v.getCuentacontable().split("/");
                    cuentacontable = split[0] + cuentacontable.substring(split[0].length());

                    switch (split.length) {
                        case 2:
                            switch (split[1]) {
                                case "TLP":
                                case "SJR":
                                    tiponomina = tiponomina.substring(split[1].length()) + split[1];
                                    break;
                                default:
                                    division = division.substring(split[1].length()) + split[1];
                                    break;
                            }
                            break;
                        case 3:
                            tiponomina = tiponomina.substring(split[1].length()) + split[1];
                            division = division.substring(split[2].length()) + split[2];
                            break;
                        case 4:
                            departamento = departamento.substring(split[1].length()) + split[1];
                            tiponomina = tiponomina.substring(split[2].length()) + split[2];
                            division = division.substring(split[3].length()) + split[3];
                            break;
                        case 5:
                            departamento = departamento.substring(split[1].length()) + split[1];
                            centrocostosap = centrocostosap.substring(split[2].length()) + split[2];
                            tiponomina = tiponomina.substring(split[3].length()) + split[3];
                            division = division.substring(split[4].length()) + split[4];
                            break;
                    }
                }
//                
                StringBuilder sb = new StringBuilder();
                sb.append(cuentacontable).append(departamento).append(centrocostosap).append(tiponomina).append(division).append(",").
                        append(v.getNumeroconcepto()).append(",").append(v.getNombreconcepto()).append(",").
                        append("\"").append(df.format(CeniccoUtil.redondear(v.getCargo()))).append("\"").append(",")
                        .append("\"").append(df.format(CeniccoUtil.redondear(v.getAbono()))).append("\"");

                lineas.add(sb.toString());
            }

        }
        return lineas;
    }

    public List<String> crearLayoutFilters(List<PolizaTO> header, int periodo, int anio) {
        List<String> lineas = new ArrayList<>();
//      
        String titulo = "Poliza Contable :" + periodo + "/" + anio;
        lineas.add(titulo);
//        
        String encabezado = "Concepto de la Poliza,Cuenta,Cargo,Abono";
        lineas.add(encabezado);
//        
        for (PolizaTO p : header) {
            for (CifrasNomina v : p.getDetalle()) {
                StringBuilder sb = new StringBuilder();
                sb.append(v.getNumeroconcepto()).append("-").append(v.getNombreconcepto()).append(",");
                sb.append(v.getCuentacontable()).append(",").append(v.getCargo()).append(",").append(v.getAbono());
                lineas.add(sb.toString());
            }
        }
        return lineas;
    }

    public List<String> crearLayoutArtsana(List<PolizaTO> header, int periodo, int anio, int idgrupopago, int idtipoproceso) {
        List<String> lineas = new ArrayList<>();
//      
        Periodo per = new Periodo();
        TipoProceso tp = ControladorWS.getInstance().findTipoProcesoById(idtipoproceso);
        per.setAnio(anio);
        per.setIdtipoproceso(tp);
        per.setPeriodo(periodo);
        Periodo periodopoliza = ControladorWS.getInstance().findPeriodoByTipoProcesoAndPeriodoAndAnio(per);

        Date fechaHoy = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String fechapago = sdf.format(periodopoliza.getFechapago().toGregorianCalendar().getTime()).replaceAll("-", "");
        String fecha;
        if (fechaHoy.after(periodopoliza.getFechafin().toGregorianCalendar().getTime())) {
            fecha = sdf.format(periodopoliza.getFechafin().toGregorianCalendar().getTime()).replaceAll("-", "");
        } else {
            fecha = sdf.format(fechaHoy).replaceAll("-", "");
        }
        sdf = new SimpleDateFormat("dd");
        String dia = sdf.format(fechaHoy);
        sdf = new SimpleDateFormat("MM");
        String mes = sdf.format(fechaHoy);
//        
        String linea = "SIPERP002" + fecha + "120000X"; //Parte fija + FechaActual  + 2da Parte fija
        lineas.add(linea);
        linea = "T00MX10  FB01" + anio + mes + dia + fechapago + "GP   MXN" + Util.getCadenaConEspacios(58, " ") + "X";
        lineas.add(linea);
        fecha = anio + sdf.format(periodopoliza.getFechapago().toGregorianCalendar().getTime());
        List<CentroCostos> cc = ControladorWS.getInstance().getCentroCostos();
        Map<String, CentroCostos> mapaCco = ControladorWS.getInstance().getMapaCentroCostos();
        int contador = 0;

//        
        for (PolizaTO p : header) {
            for (CifrasNomina v : p.getDetalle()) {
                StringBuilder sb = new StringBuilder();
                sb.append("");
                String centrocosto = "";
                for (CentroCostos c : cc) {
                    if (c.getIdCentroCostos() == v.getIdcentrocostos()) {
                        centrocosto = c.getNombre();
                    }
                }
                //En la cuenta contable de Artsana primero va la cuenta contable y se separa con un guion el centro de costos
                String[] cco = new String[2];
                if (v.getCuentacontable().contains("-")) {
                    cco = v.getCuentacontable().split("-");
                    if (cco[1].charAt(0) == '3') {
                        centrocosto = mapaCco.get(cco[0]).getNombre();
                        cco[0] = "        ";
                    }
                } else {
                    cco[0] = "        ";
                    cco[1] = v.getCuentacontable();
                    centrocosto = "";
                }
                String cadena = v.getNombreconcepto() + " " + centrocosto;

                String nombreconcepto;
                if (cadena.length() > 35) {
                    nombreconcepto = cadena.substring(0, 35);
                } else {
                    nombreconcepto = Util.getCadenaConEspacios(35, " ").substring(cadena.length());
                    nombreconcepto = cadena + nombreconcepto;
                }

                //Cargo
                if (v.getCargo() != null && v.getCargo() > 0.0) {
                    String line = Util.getCadenaConEspacios(20, " ").substring(cco[1].length());
                    sb.append("PS40  ").append(cco[1]).append(line);
                    double s = Math.round(v.getCargo() * 100.0) / 100.0;
                    String entero = "" + s;
                    if (!entero.contains(".")) {
                        entero = entero + "00";
                    } else {
                        String[] spl = entero.split("\\.");
                        entero = spl[0];
                        String decimal = spl[1];
                        if (decimal.length() < 2) {
                            decimal = decimal + "0";
                        }
                        entero = entero + decimal;
                    }
                    line = "000000000000".substring(entero.length());
                    sb.append(line).append(entero);
                    line = "00000000000000000000".substring(fecha.length());
                    sb.append(line).append(fecha).append(Util.getCadenaConEspacios(46, " "))
                            .append(cco[0]).append(Util.getCadenaConEspacios(81, " "))
                            .append(nombreconcepto);
                    String fin = Util.getCadenaConEspacios(364, " ").substring(sb.toString().length());
                    sb.append(fin).append("X");
                }
                //Abono
                if (v.getAbono() != null && v.getAbono() > 0.0) {
                    String line = Util.getCadenaConEspacios(20, " ").substring(cco[1].length());
                    sb.append("PS50  ").append(cco[1]).append(line);
                    double s = Math.round(v.getAbono() * 100.0) / 100.0;
                    String entero = "" + s;
                    if (!entero.contains(".")) {
                        entero = v.getAbono().toString() + "00";
                    } else {
                        String[] spl = entero.split("\\.");
                        entero = spl[0];
                        String decimal = spl[1];
                        if (decimal.length() < 2) {
                            decimal = decimal + "0";
                        }
                        entero = entero + decimal;
                    }
                    line = "000000000000".substring(entero.length());
                    sb.append(line).append(entero);
                    line = "00000000000000000000".substring(fecha.length());
                    sb.append(line).append(fecha).append(Util.getCadenaConEspacios(46, " "))
                            .append(cco[0]).append(Util.getCadenaConEspacios(81, " "))
                            .append(nombreconcepto);
                    String fin = Util.getCadenaConEspacios(364, " ").substring(sb.toString().length());
                    sb.append(fin).append("X");
                }
                if (!sb.toString().equals("")) {
                    lineas.add(sb.toString());
                    contador++;
                }
            }
        }
        contador++;
        String numeroRegistros = "" + contador;
        String cadena = "000000".substring(numeroRegistros.length());
        linea = "SIPERP002" + cadena + numeroRegistros + "X";
        lineas.add(linea);
        return lineas;
    }

    public void crearLayoutByjus(List<PolizaTO> header, String nombrearchivo, int periodo, int anio, double totalcargo, double totalabono) {
        DecimalFormat nformat = new DecimalFormat("0.00");
        Periodo per = ControladorWS.getInstance().findPeriodoById(header.get(0).getDetalle().get(0).getIdperiodo());
        String perStr = per.getAniomes() + " " + per.getAnio();
        HSSFWorkbook workbook = new HSSFWorkbook();
        double neto = 0.0;
//      
        HSSFSheet sheet = workbook.createSheet("PolizaContable" + periodo + anio);
//        
        int j = 1;
        int i = 4;
//        
        HSSFRow row = sheet.createRow(j);
//        
        HSSFCell cell = row.createCell(i);
        cell.setCellValue("BYJUS S.A. DE C.V.");
        j++;
        row = sheet.createRow(j);
        cell = row.createCell(i);
        cell.setCellValue("Contabilidad" + perStr);

        j = 11;
        i = 2;
        row = sheet.createRow(j);
        cell = row.createCell(i);
        cell.setCellValue("Ctro de Ctos Dpto");
        i++;
//        
        cell = row.createCell(i);
        cell.setCellValue("Descripción Ctro de Ctos Dpto");
        i++;
//        
        cell = row.createCell(i);
        cell.setCellValue("Debe");
        i++;
//        
        cell = row.createCell(i);
        cell.setCellValue("Haber");
        i++;
//
        cell = row.createCell(i);
        cell.setCellValue("Cta");
        i++;
//
        cell = row.createCell(i);
        cell.setCellValue("Descripción Cta");
        i++;
//
        cell = row.createCell(i);
        cell.setCellValue("Periodo");
//        
        j++;
//        
        for (PolizaTO p : header) {
//            
            Collections.sort(p.getDetalle(), new Comparator<CifrasNomina>() {
                @Override
                public int compare(CifrasNomina c1, CifrasNomina c2) {
                    return c1.getCuentacontable().compareTo(c2.getCuentacontable());
                }
            });

            for (CifrasNomina v : p.getDetalle()) {
                if (v.getNumeroconcepto().equals("9999")) {
                    neto += v.getAbono();
                    continue;
                }

                i = 2;
                per = ControladorWS.getInstance().findPeriodoById(v.getIdperiodo());
                if (per != null) {
                    perStr = per.getIdtipoproceso().getNombre() + " PERIODO " + per.getPeriodo() + " " + per.getAnio();
                } else {
                    perStr = "PAGO NOMINA(S)";
                }

//
                String[] split = v.getCuentacontable().split("-");
                String cco = split[0];
                String cta = split[1] != null ? split[1] : "0";
                row = sheet.createRow(j);

//Ctro de Ctos Dpto                
                cell = row.createCell(i);
                cell.setCellValue(cco);
                i++;

//Descripcion centro costos (Nombre del departamento)                
                cell = row.createCell(i);
                cell.setCellValue(v.getDepto());
                i++;
//Debe                
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(v.getCargo()));
                i++;
//Haber                
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(v.getAbono()));
                i++;
//Cta                
                cell = row.createCell(i);
                cell.setCellValue(cta);
                i++;
//Descripcion cuenta (nombreconcepto)
                cell = row.createCell(i);
                cell.setCellValue(v.getNombreconcepto());
                i++;

                cell = row.createCell(i);
                cell.setCellValue(perStr);
                i++;

                j++;
            }
        }
//        
        i = 5;
//        
        row = sheet.createRow(j);
//        
        cell = row.createCell(i);
        cell.setCellValue(neto);
        i++;

        cell = row.createCell(i);
        cell.setCellValue("23115");
        i++;

        cell = row.createCell(i);
        cell.setCellValue("NETO");
        i++;


        this.escribirArchivo(workbook, nombrearchivo, ParametrosReportes.ARCHIVO_XLS);

    }

    public void crearLayoutGrupoOrdas(List<PolizaTO> header, String nombrearchivo, int periodo, int anio, double totalcargo, double totalabono, String consecutivo) {
        DecimalFormat nformat = new DecimalFormat("0.00");
        HSSFWorkbook workbook = new HSSFWorkbook();
        GrupoPago gp = ControladorWS.getInstance().findGrupoPagoById(header.get(0).getDetalle().get(0).getIdgrupopago());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/YYYY");
        Periodo per = ControladorWS.getInstance().findPeriodoById(header.get(0).getDetalle().get(0).getIdperiodo());

//      
        HSSFSheet sheet = workbook.createSheet("U4F Upload Journal");

        int j = 2;  // Filaz
        int i = 1;  // Columna
//        
        HSSFRow row = sheet.createRow(j);
//        
        HSSFCell cell = row.createCell(i);
        cell.setCellValue("Document Header Description");
        i++;

        cell = row.createCell(i);
        cell.setCellValue(nombrearchivo);
        i = 1;
        j += 2;

        row = sheet.createRow(j);

        cell = row.createCell(i);
        cell.setCellValue("Company");
        i++;

        cell = row.createCell(i);
        cell.setCellValue(gp.getGrupopago() + " - " + gp.getNombre());
        i = 1;
        j += 2;

        row = sheet.createRow(j);

        cell = row.createCell(i);
        cell.setCellValue("Document day");
        i++;

        cell = row.createCell(i);
        cell.setCellValue(sdf.format(per.getFechapago().toGregorianCalendar().getTime()));
        i = 1;
        j += 2;

        row = sheet.createRow(j);

        cell = row.createCell(i);
        cell.setCellValue("Period/Year");
        cell.setCellType(CellType.STRING);
        i++;

        cell = row.createCell(i);
        cell.setCellValue(anio + "/" + periodo);
        i = 8;
        j += 2;

        row = sheet.createRow(j);

        cell = row.createCell(i);
        cell.setCellValue("Local Check Totals (Must equal 0)");
        i++;

        cell = row.createCell(i);
        cell.setCellValue((CeniccoUtil.redondear(totalcargo - totalabono)));
        i++;

        cell = row.createCell(i);
        cell.setCellValue("Debit:");
        i++;

        cell = row.createCell(i);
        cell.setCellValue(totalcargo);
        i += 2;

        cell = row.createCell(i);
        cell.setCellValue("Credit:");
        i++;

        cell = row.createCell(i);
        cell.setCellValue(totalabono);


        i = 1;
        j = 13;

        row = sheet.createRow(j);

        cell = row.createCell(i);
        cell.setCellValue("Posting Batch");
        i++;

        cell = row.createCell(i);
        cell.setCellValue("Company Destination");
        i++;

        cell = row.createCell(i);
        cell.setCellValue("Company Code");
        i++;

        cell = row.createCell(i);
        cell.setCellValue("EL1");
        i++;

        cell = row.createCell(i);
        cell.setCellValue("EL2");
        i++;

        cell = row.createCell(i);
        cell.setCellValue("EL3");
        i++;

        cell = row.createCell(i);
        cell.setCellValue("EL4");
        i++;

        cell = row.createCell(i);
        cell.setCellValue("EL5");
        i++;

        cell = row.createCell(i);
        cell.setCellValue("EL6");
        i++;

        cell = row.createCell(i);
        cell.setCellValue("EL7");
        i++;

        cell = row.createCell(i);
        cell.setCellValue("EL8");
        i++;

        cell = row.createCell(i);
        cell.setCellValue("Transaction Amount");
        i++;

        cell = row.createCell(i);
        cell.setCellValue("Base Amount");
        i++;

        cell = row.createCell(i);
        cell.setCellValue("Debit/Credit");
        i++;

        cell = row.createCell(i);
        cell.setCellValue("Line description");
        i++;

        cell = row.createCell(i);
        cell.setCellValue("Ext Ref 1");
        i++;

        cell = row.createCell(i);
        cell.setCellValue("Ext Ref 2");
        i++;

        cell = row.createCell(i);
        cell.setCellValue("Ext Ref 3");
        i++;

        cell = row.createCell(i);
        cell.setCellValue("Ext Ref 4");
        i++;

        cell = row.createCell(i);
        cell.setCellValue("Ext Ref 5");
        i++;

        cell = row.createCell(i);
        cell.setCellValue("Ext Ref 6");
        i++;

        cell = row.createCell(i);
        cell.setCellValue("Doc Code");
        i++;

        cell = row.createCell(i);
        cell.setCellValue("Yr");
        i++;

        cell = row.createCell(i);
        cell.setCellValue("Period");
        i++;

        cell = row.createCell(i);
        cell.setCellValue("Value Date");
        i++;

        cell = row.createCell(i);
        cell.setCellValue("Doc Date");
        i++;

        cell = row.createCell(i);
        cell.setCellValue("Posted Doc No.");
        i++;

        cell = row.createCell(i);
        cell.setCellValue("Post Journal");
        i++;

        j++;
//        
        for (PolizaTO p : header) {
//            
            Collections.sort(p.getDetalle(), new Comparator<CifrasNomina>() {
                @Override
                public int compare(CifrasNomina c1, CifrasNomina c2) {
                    return c1.getCuentacontable().compareTo(c2.getCuentacontable());
                }
            });

            for (CifrasNomina v : p.getDetalle()) {
                i = 1;
                System.out.println("Concepto: " + v.getNumeroconcepto() + " - " + v.getNombreconcepto());
                String[] split = v.getCuentacontable().split("-");
                String cco = split[0];
                String cta = split[1] != null ? split[1] : "0";
                String ctaAux = "";
                if (v.getCuentacontable().contains("[") && v.getCuentacontable().contains("]")) {
                    ctaAux = v.getCuentacontable().substring(v.getCuentacontable().indexOf("[") + 1, v.getCuentacontable().indexOf("]"));
                    if (ctaAux.contains("-")) {
                        String splitcc[] = ctaAux.split("-");
                        switch (gp.getIdcompania().getNombre()) {
                            case "GRUPO ORDAS HOWDEN AGENTE DE SEGUROS Y DE FINANZAS":
                                ctaAux = splitcc[0];
                                break;
                            case "SERVICIOS ORDAS  S.A. DE C.V":
                                ctaAux = splitcc[1];
                                break;
                        }
                    }
                }
                row = sheet.createRow(j);

// Posting Batch                
                cell = row.createCell(i);
                cell.setCellValue(gp.getGrupopago() + "MXN");
                i++;
// Company Destination
                cell = row.createCell(i);
                cell.setCellValue(gp.getGrupopago());
                i++;
// Company Code
                cell = row.createCell(i);
                cell.setCellValue(gp.getGrupopago());
                i++;
// EL1                 
                cell = row.createCell(i);
                cell.setCellValue(gp.getGrupopago());
                i++;
// EL2
                cell = row.createCell(i);
                cell.setCellValue("+");
                i++;
// EL3 (Cuenta Contable)                
                cell = row.createCell(i);
                cell.setCellValue(cta);
                i++;
// EL4 (Centro de costos)
                cell = row.createCell(i);
                cell.setCellValue(cco);
                i++;
// EL5
                cell = row.createCell(i);
                cell.setCellValue("MXN");
                i++;
// EL6
                cell = row.createCell(i);
                cell.setCellValue("SPAY");
                i++;
// EL7  (Cuenta auxiliar)      
                cell = row.createCell(i);
                cell.setCellValue(ctaAux);
                i++;
// EL8 (Vacia)
                cell = row.createCell(i);
                cell.setCellValue("");
                i++;
// Transaction Amount
                cell = row.createCell(i);
                cell.setCellValue(v.getCargo() + v.getAbono());
                i++;
// Base Amount
                cell = row.createCell(i);
                cell.setCellValue(v.getCargo() + v.getAbono());
                i++;
// Debit/Credit           
                String ca;
                if (v.getCargo() > v.getAbono()) {
                    ca = "DEBIT";
                } else {
                    ca = "CREDIT";
                }
                cell = row.createCell(i);
                cell.setCellValue(ca);
                i++;
// Line Description                
                cell = row.createCell(i);
                cell.setCellValue(v.getNombreconcepto());
                i++;
// Ext Ref 1 
                cell = row.createCell(i);
                cell.setCellValue("");
                i++;
// Ext Ref 2
                cell = row.createCell(i);
                cell.setCellValue("");
                i++;
// Ext Ref 3
                cell = row.createCell(i);
                cell.setCellValue("");
                i++;
// Ext Ref 4
                cell = row.createCell(i);
                cell.setCellValue("");
                i++;
// Ext Ref 5
                cell = row.createCell(i);
                cell.setCellValue("");
                i++;
// Ext Ref 6
                cell = row.createCell(i);
                cell.setCellValue("");
                i++;
// Doc Code
                cell = row.createCell(i);
                cell.setCellValue("MGJNLPYR");
                i++;
// Yr                
                cell = row.createCell(i);
                cell.setCellType(CellType.STRING);
                cell.setCellValue(anio);
                i++;
// Period               
                cell = row.createCell(i);
                cell.setCellType(CellType.STRING);
                cell.setCellValue(periodo);
                i++;
// Value Date
                cell = row.createCell(i);
                cell.setCellValue(sdf.format(per.getFechapago().toGregorianCalendar().getTime()));
                i++;
// Doc Date               
                cell = row.createCell(i);
                cell.setCellValue(sdf.format(per.getFechapago().toGregorianCalendar().getTime()));
                i++;
// Posted Doc No.
                cell = row.createCell(i);
                cell.setCellType(CellType.STRING);
                cell.setCellValue(consecutivo);
                i++;
// Post Journal               
                cell = row.createCell(i);
                cell.setCellValue("FALSE");
                i++;

                j++;
            }
        }
        this.escribirArchivo(workbook, nombrearchivo, ParametrosReportes.ARCHIVO_XLS);
    }

    public void crearLayoutPolizaGrupoOrdas(List<PolizaTO> header, String nombrearchivo, int periodo, int anio, double totalcargo, double totalabono, String consecutivo) {
        DecimalFormat nformat = new DecimalFormat("0.00");
        HSSFWorkbook workbook = new HSSFWorkbook();
        GrupoPago gp = ControladorWS.getInstance().findGrupoPagoById(header.get(0).getDetalle().get(0).getIdgrupopago());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/YYYY");
        Periodo per = ControladorWS.getInstance().findPeriodoById(header.get(0).getDetalle().get(0).getIdperiodo());

//      
        HSSFSheet sheet = workbook.createSheet("Data");

        int j = 0;  // Filaz
        int i = 0;  // Columna
//        
        HSSFRow row = sheet.createRow(j);
//        
        HSSFCell cell = row.createCell(i);
        cell.setCellValue("COMPAÑIA");
        i++;

        cell = row.createCell(i);
        cell.setCellValue("TIPO TRAN");
        i++;

        cell = row.createCell(i);
        cell.setCellValue("Poliza");
        i++;

        cell = row.createCell(i);
        cell.setCellValue("Asiento");
        i++;

        cell = row.createCell(i);
        cell.setCellValue("CONCEPTO_POL");
        i++;

        cell = row.createCell(i);
        cell.setCellValue("Fecha");
        i++;

        cell = row.createCell(i);
        cell.setCellValue("Cuenta");
        i++;

        cell = row.createCell(i);
        cell.setCellValue("CENTRO DE COSTO");
        i++;

        cell = row.createCell(i);
        cell.setCellValue("UEN");
        i++;

        cell = row.createCell(i);
        cell.setCellValue("Auxiliar");
        i++;

        cell = row.createCell(i);
        cell.setCellValue("Proyecto");
        i++;

        cell = row.createCell(i);
        cell.setCellValue("Libro");
        i++;

        cell = row.createCell(i);
        cell.setCellValue("Moneda");
        i++;

        cell = row.createCell(i);
        cell.setCellValue("IMPTE_CAPTURADO");
        i++;

        cell = row.createCell(i);
        cell.setCellValue("IMPTE_CONVERT");
        i++;

        cell = row.createCell(i);
        cell.setCellValue("CARGO_CREDITO");
        i++;

        cell = row.createCell(i);
        cell.setCellValue("Unidades");
        i++;

        cell = row.createCell(i);
        cell.setCellValue("TIPO CAMBIO");
        i++;

        cell = row.createCell(i);
        cell.setCellValue("CONCEPTO_MOV");
        i++;

        cell = row.createCell(i);
        cell.setCellValue("Cheque");
        i++;

        cell = row.createCell(i);
        cell.setCellValue("Referencia");
        i++;

        cell = row.createCell(i);
        cell.setCellValue("COMENTARIO");
        i++;

        j++;
//        
        for (PolizaTO p : header) {
//            
            Collections.sort(p.getDetalle(), new Comparator<CifrasNomina>() {
                @Override
                public int compare(CifrasNomina c1, CifrasNomina c2) {
                    return c1.getCuentacontable().compareTo(c2.getCuentacontable());
                }
            });

            for (CifrasNomina v : p.getDetalle()) {
                i = 0;
                System.out.println("Concepto: " + v.getNumeroconcepto() + " - " + v.getNombreconcepto());
                String[] split = v.getCuentacontable().split(",");
                String cta = split[0];
                String cco = "";
                try {
                    cco = split[1] != null ? split[1] : "";
                } catch (Exception e) {
                    cco = "";
                }
                String ctaAux = "";
                if (v.getCuentacontable().contains("[") && v.getCuentacontable().contains("]")) {
                    ctaAux = v.getCuentacontable().substring(v.getCuentacontable().indexOf("[") + 1, v.getCuentacontable().indexOf("]"));
                    if (ctaAux.contains("-")) {
                        String splitcc[] = ctaAux.split("-");
                        switch (gp.getIdcompania().getNombre()) {
                            case "GRUPO ORDAS HOWDEN AGENTE DE SEGUROS Y DE FINANZAS":
                                ctaAux = splitcc[0];
                                break;
                            case "SERVICIOS ORDAS  S.A. DE C.V":
                                ctaAux = splitcc[1];
                                break;
                        }
                    }
                }
                row = sheet.createRow(j);

// COMPAÑIA
                cell = row.createCell(i);
                cell.setCellValue("1");
                i++;
// TIPO TRAN                
                cell = row.createCell(i);
                cell.setCellValue("DI");
                i++;
// Poliza                
                cell = row.createCell(i);
                cell.setCellValue("");
                i++;
// Asiento
                cell = row.createCell(i);
                cell.setCellValue(j);
                i++;
// CONCEPTO_POL
                cell = row.createCell(i);
                cell.setCellValue(nombrearchivo);
                i++;
// Fecha
                cell = row.createCell(i);
                cell.setCellValue(sdf.format(per.getFechapago().toGregorianCalendar().getTime()));
                i++;
// Cuenta
                cell = row.createCell(i);
                cell.setCellValue(cta);
                i++;
// CENTRO DE COSTO
                cell = row.createCell(i);
                cell.setCellValue(cco);
                i++;
// UEN
                cell = row.createCell(i);
                cell.setCellValue("");
                i++;
// Auxiliar
                cell = row.createCell(i);
                cell.setCellValue(ctaAux);
                i++;
// Proyecto
                cell = row.createCell(i);
                cell.setCellValue("*");
                i++;
// Libro
                cell = row.createCell(i);
                cell.setCellValue("CG");
                i++;
// Moneda
                cell = row.createCell(i);
                cell.setCellValue("1");
                i++;
// IMPTE_CAPTURADO
                cell = row.createCell(i);
                cell.setCellValue(v.getCargo() + v.getAbono());
                i++;
// IMPTE_CONVERT
                cell = row.createCell(i);
                cell.setCellValue(v.getCargo() + v.getAbono());
                i++;
//CARGO_CREDITO
                String ca;
                if (v.getCargo() > v.getAbono()) {
                    ca = "1";
                } else {
                    ca = "0";
                }
                cell = row.createCell(i);
                cell.setCellValue(ca);
                i++;
// Unidades
                cell = row.createCell(i);
                cell.setCellValue("0");
                i++;
// TIPO CAMBIO
                cell = row.createCell(i);
                cell.setCellValue("1");
                i++;
// CONCEPTO_MOV
                cell = row.createCell(i);
                cell.setCellValue(v.getNombreconcepto());
                i++;
// Cheque
                cell = row.createCell(i);
                cell.setCellValue("");
                i++;
// Referencia
                cell = row.createCell(i);
                cell.setCellValue("");
                i++;
// COMENTARIO
                cell = row.createCell(i);
                cell.setCellValue("");
                i++;

                j++;
            }
        }
        this.escribirArchivo(workbook, nombrearchivo, ParametrosReportes.ARCHIVO_XLS);
    }

    public void crearLayoutMinntDepartamentos(List<CifrasNomina> cifrasNomina, String nombrearchivo, String periodoMes, int anio, double totalcargo, double totalabono, boolean headerConceptos, String tipoPoliza) {
        System.out.println("::::::::: Cifras Nomina MINNT :: Tamano :: " + cifrasNomina.size());
        Parametro parametro = ControladorWS.getInstance().getParametro("ACTIVAR_SEPARAR_PROVISIONES_REPORTES");
        System.out.println("::::::::: Cifras Nomina MINNT :: PARAMETRO :: " + parametro.getValor());
        Parametro parametroTipoPoliza = ControladorWS.getInstance().getParametro(tipoPoliza);
        System.out.println("::::::::: Cifras Nomina MINNT :: PARAMETRO :: " + parametroTipoPoliza.getParametro());
        Boolean separarProvisiones = false;
        if (parametro.getValor().equals("S")) {
            separarProvisiones = true;
        }

        List<String> valoresPoliza = Arrays.asList(parametroTipoPoliza.getValor().split(","));

        Map<Integer, Departamento> mapDepartamentos = ControladorWS.getInstance().getMapDepartamentosById();

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("PolizaContable " + periodoMes + anio);

        CellStyle styleazul = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setFontName(HSSFFont.FONT_ARIAL);
        font.setBoldweight(Short.MAX_VALUE);
        font.setColor(HSSFColor.BLUE.index);
        styleazul.setFont(font);
//        
        CellStyle stylerojo = workbook.createCellStyle();
        font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setFontName(HSSFFont.FONT_ARIAL);
        font.setBoldweight(Short.MAX_VALUE);
        font.setColor(HSSFColor.VIOLET.index);
        stylerojo.setFont(font);
//        
        CellStyle stylenegro = workbook.createCellStyle();
        font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setFontName(HSSFFont.FONT_ARIAL);
        font.setBoldweight(Short.MAX_VALUE);
        stylenegro.setFont(font);

        int j = 0;
        int i = 0;
//        
        HSSFRow row = null;
        HSSFCell cell = null;

        if (headerConceptos) {
            row = sheet.createRow(j);//        
            cell = row.createCell(i++);
            cell.setCellValue("Concepto de la Poliza Contable");
            cell.setCellStyle(stylenegro);
//        
            cell = row.createCell(i++);
            cell.setCellValue("Cuenta");
            cell.setCellStyle(stylenegro);
//        
            cell = row.createCell(i++);
            cell.setCellValue("Debito");
            cell.setCellStyle(stylerojo);
//        
            cell = row.createCell(i++);
            cell.setCellValue("Credito");
            cell.setCellStyle(styleazul);
//        
            cell = row.createCell(i++);
            cell.setCellValue("Departamento");
            cell.setCellStyle(stylenegro);
        }

        Map<String, Double> groupoNetosAbonos = new HashMap<>();
        Map<String, Double> groupoNetosCargos = new HashMap<>();
        Map<Integer, List<CifrasNomina>> groupoDepartamentos = new HashMap<>();
        for (CifrasNomina cn : cifrasNomina) {

            if (separarProvisiones) {
                if (tipoPoliza.contains("SEPARAR_PROVISION_NORMAL")) {
                    if (valoresPoliza.contains(cn.getNumeroconcepto())) {
                        continue;
                    }
                } else {
                    if (!valoresPoliza.contains(cn.getNumeroconcepto())) {
                        continue;
                    }
                }
            }

            Integer key = cn.getIddepartamento();
            if (cn.getPolizacontable().equals(BigDecimal.ONE.intValue())) {
                if (cn.getCuentacontable().startsWith("2")) {
                    if (BigDecimal.ZERO.toString().equals(cn.getCargoabono().toString())) {
                        String cuenta = cn.getCuentacontable() + "|" + BigDecimal.ZERO.toString();
                        if (groupoNetosCargos.containsKey(cuenta)) {
                            Double neto = groupoNetosCargos.get(cuenta);
                            neto += cn.getImporte();
                            groupoNetosCargos.put(cuenta, neto);
                        } else {
                            groupoNetosCargos.put(cuenta, cn.getImporte());
                        }
                    } else {
                        String cuenta = cn.getCuentacontable() + "|" + BigDecimal.ONE.toString();
                        if (groupoNetosAbonos.containsKey(cuenta)) {
                            Double neto = groupoNetosAbonos.get(cuenta);
                            neto += cn.getImporte();
                            groupoNetosAbonos.put(cuenta, neto);
                        } else {
                            groupoNetosAbonos.put(cuenta, cn.getImporte());
                        }
                    }
                    if (!(cn.getAuxcuentacontable().isEmpty() || cn.getAuxcuentacontable() == null || cn.getAuxcuentacontable().trim().isEmpty())) {
                        if (groupoDepartamentos.containsKey(key)) {
                            List<CifrasNomina> list = groupoDepartamentos.get(key);
                            list.add(cn);
                            groupoDepartamentos.put(key, list);
                        } else {
                            List<CifrasNomina> list = new ArrayList<>();
                            list.add(cn);
                            groupoDepartamentos.put(key, list);
                        }
                    }

                } else {
                    if (groupoDepartamentos.containsKey(key)) {
                        List<CifrasNomina> list = groupoDepartamentos.get(key);
                        list.add(cn);
                        groupoDepartamentos.put(key, list);
                    } else {
                        List<CifrasNomina> list = new ArrayList<>();
                        list.add(cn);
                        groupoDepartamentos.put(key, list);
                    }

                    if (cn.getAuxcuentacontable().startsWith("2")) {
                        if (BigDecimal.ZERO.toString().equals(cn.getCargoabono().toString())) {
                            String cuenta = cn.getAuxcuentacontable() + "|" + BigDecimal.ONE.toString();
                            if (groupoNetosCargos.containsKey(cuenta)) {
                                Double neto = groupoNetosCargos.get(cuenta);
                                neto += cn.getImporte();
                                groupoNetosCargos.put(cuenta, neto);
                            } else {
                                groupoNetosCargos.put(cuenta, cn.getImporte());
                            }
                        } else {
                            String cuenta = cn.getAuxcuentacontable() + "|" + BigDecimal.ZERO.toString();
                            if (groupoNetosAbonos.containsKey(cuenta)) {
                                Double neto = groupoNetosAbonos.get(cuenta);
                                neto += cn.getImporte();
                                groupoNetosAbonos.put(cuenta, neto);
                            } else {
                                groupoNetosAbonos.put(cuenta, cn.getImporte());
                            }
                        }
                    }
                }
            }
        }

        Map<Integer, HashMap<String, Double>> groupDepartamentoCuentaContable = new HashMap<>();
        for (Map.Entry<Integer, List<CifrasNomina>> entry : groupoDepartamentos.entrySet()) {
            Departamento departamento = mapDepartamentos.get(entry.getKey());

            HashMap<String, Double> groupCuentaContableImporte = new HashMap<>();
            for (CifrasNomina cn : entry.getValue()) {
                String key = cn.getCuentacontable().trim() + "|" + cn.getNombreconcepto() + "|" + cn.getCargoabono() + "|" + cn.getAuxcuentacontable().trim();
                if (groupCuentaContableImporte.containsKey(key)) {
                    Double importe = groupCuentaContableImporte.get(key);
                    importe += cn.getImporte();
                    groupCuentaContableImporte.put(key, importe);
                } else {
                    groupCuentaContableImporte.put(key, cn.getImporte());
                }
            }
            groupDepartamentoCuentaContable.put(departamento.getIddepartamento(), groupCuentaContableImporte);
        }

        if (headerConceptos) {
            j = 1;
            i = 0;
        }

        for (Map.Entry<Integer, List<CifrasNomina>> entry : groupoDepartamentos.entrySet()) {
            Departamento departamento = mapDepartamentos.get(entry.getKey());
            HashMap<String, Double> cifrasNominaDepa = groupDepartamentoCuentaContable.get(departamento.getIddepartamento());
            SortedMap<String, Double> sortedCuentaContableImporte = new TreeMap<>(cifrasNominaDepa);
            for (Map.Entry<String, Double> data : sortedCuentaContableImporte.entrySet()) {
                if (data.getValue() > 0) {
                    String[] cuentaContBono = data.getKey().split("\\|");
                    if (headerConceptos) {
                        row = sheet.createRow(j);
                        cell = row.createCell(i++);
                        cell.setCellValue(cuentaContBono[1]);
                    } else {
                        row = sheet.createRow(j);
                        cell = row.createCell(i);
                    }

                    cell = row.createCell(i++);
                    cell.setCellValue(cuentaContBono[0]);

                    String cargoBono = cuentaContBono[2];
                    if (BigDecimal.ZERO.toString().equals(cargoBono)) {
                        cell = row.createCell(i++);
                        cell.setCellValue(data.getValue());
                        i++;
                    } else if (BigDecimal.ONE.toString().equals(cargoBono)) {
                        i++;
                        cell = row.createCell(i++);
                        cell.setCellValue(data.getValue());
                    }

                    cell = row.createCell(i++);
                    cell.setCellValue(departamento.getNombre());

                    i = 0;
                    j++;

                    try {
                        if (groupoNetosCargos.containsKey(cuentaContBono[3]) || groupoNetosAbonos.containsKey(cuentaContBono[3])) {
                            if (headerConceptos) {
                                row = sheet.createRow(j);
                                cell = row.createCell(i++);
                                cell.setCellValue(cuentaContBono[1]);
                            } else {
                                row = sheet.createRow(j);
                                cell = row.createCell(i);
                            }

                            cell = row.createCell(i++);
                            cell.setCellValue(cuentaContBono[3]);

                            if (BigDecimal.ONE.toString().equals(cargoBono)) {
                                cell = row.createCell(i++);
                                cell.setCellValue(data.getValue());
                                i++;
                            } else if (BigDecimal.ZERO.toString().equals(cargoBono)) {
                                i++;
                                cell = row.createCell(i++);
                                cell.setCellValue(data.getValue());
                            }

                            cell = row.createCell(i++);
                            cell.setCellValue(departamento.getNombre());

                            i = 0;
                            j++;
                        }
                    } catch (Exception ex) {
                        System.out.println("No Encontro CuentaAuxiliar " + cuentaContBono[1]);
                    }
                }
            }
        }

        if (headerConceptos) {
            i = 1;
        } else {
            i = 0;
        }
        SortedMap<String, Double> sortedGroupoNetosAbonos = new TreeMap<>(groupoNetosAbonos);
        for (Map.Entry<String, Double> entry : sortedGroupoNetosAbonos.entrySet()) {
            String[] llave = entry.getKey().split("\\|");
            String cargoAbono = llave[1];
            String cuentacontable = llave[0];

            row = sheet.createRow(j);
            cell = row.createCell(i++);

            cell.setCellValue(cuentacontable);

            if (BigDecimal.ZERO.toString().equals(cargoAbono)) {
                cell = row.createCell(i++);
                cell.setCellValue(entry.getValue());
                i++;
            } else if (BigDecimal.ONE.toString().equals(cargoAbono)) {
                i++;
                cell = row.createCell(i++);
                cell.setCellValue(entry.getValue());
            }

            if (headerConceptos) {
                i = 1;
            } else {
                i = 0;
            }
            j++;
        }

        SortedMap<String, Double> sortedGroupoNetosCargos = new TreeMap<>(groupoNetosCargos);
        for (Map.Entry<String, Double> entry : sortedGroupoNetosCargos.entrySet()) {
            String[] llave = entry.getKey().split("\\|");
            String cargoAbono = llave[1];
            String cuentacontable = llave[0];

            row = sheet.createRow(j);
            cell = row.createCell(i++);

            cell.setCellValue(cuentacontable);

            if (BigDecimal.ZERO.toString().equals(cargoAbono)) {
                cell = row.createCell(i++);
                cell.setCellValue(entry.getValue());
                i++;
            } else if (BigDecimal.ONE.toString().equals(cargoAbono)) {
                i++;
                cell = row.createCell(i++);
                cell.setCellValue(entry.getValue());
            }

            if (headerConceptos) {
                i = 1;
            } else {
                i = 0;
            }
            j++;
        }

        /**
         * Inicio Sheet AutoSizeColumn *
         */
        int total_y = 10;
        for (int x = 0; x <= total_y; x++) {
            sheet.autoSizeColumn(x);
        }
        /**
         * Fin Sheet AutoSizeColumn *
         */
        this.escribirArchivo(workbook, nombrearchivo, ParametrosReportes.ARCHIVO_XLS);
    }

    public void crearLayoutMinntCentrosCostos(List<CifrasNomina> cifrasNomina, String nombrearchivo, String periodoMes, int anio, double totalcargo, double totalabono, boolean headerConceptos, String tipoPoliza) {
        System.out.println("::::::::: Cifras Nomina MINNT :: Tamano :: " + cifrasNomina.size());
        Parametro parametro = ControladorWS.getInstance().getParametro("ACTIVAR_SEPARAR_PROVISIONES_REPORTES");
        System.out.println("::::::::: Cifras Nomina MINNT :: PARAMETRO :: " + parametro.getValor());
        Parametro parametroTipoPoliza = ControladorWS.getInstance().getParametro(tipoPoliza);
        System.out.println("::::::::: Cifras Nomina MINNT :: PARAMETRO :: " + parametroTipoPoliza.getParametro());
        Boolean separarProvisiones = false;
        if (parametro.getValor().equals("S")) {
            separarProvisiones = true;
        }

        List<String> valoresPoliza = Arrays.asList(parametroTipoPoliza.getValor().split(","));

        Map<Integer, CentroCostos> mapCentrosCostos = ControladorWS.getInstance().getMapCentrosCostosById();

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("PolizaContable " + periodoMes + anio);

        CellStyle styleazul = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setFontName(HSSFFont.FONT_ARIAL);
        font.setBoldweight(Short.MAX_VALUE);
        font.setColor(HSSFColor.BLUE.index);
        styleazul.setFont(font);
//        
        CellStyle stylerojo = workbook.createCellStyle();
        font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setFontName(HSSFFont.FONT_ARIAL);
        font.setBoldweight(Short.MAX_VALUE);
        font.setColor(HSSFColor.VIOLET.index);
        stylerojo.setFont(font);
//        
        CellStyle stylenegro = workbook.createCellStyle();
        font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setFontName(HSSFFont.FONT_ARIAL);
        font.setBoldweight(Short.MAX_VALUE);
        stylenegro.setFont(font);

        int j = 0;
        int i = 0;
//        
        HSSFRow row = null;
        HSSFCell cell = null;

        if (headerConceptos) {
            row = sheet.createRow(j);//        
            cell = row.createCell(i++);
            cell.setCellValue("Concepto de la Poliza Contable");
            cell.setCellStyle(stylenegro);
//        
            cell = row.createCell(i++);
            cell.setCellValue("Cuenta");
            cell.setCellStyle(stylenegro);
//        
            cell = row.createCell(i++);
            cell.setCellValue("Debito");
            cell.setCellStyle(stylerojo);
//        
            cell = row.createCell(i++);
            cell.setCellValue("Credito");
            cell.setCellStyle(styleazul);
//        
            cell = row.createCell(i++);
            cell.setCellValue("Centro Costos");
            cell.setCellStyle(stylenegro);
        }

        Map<String, Double> groupoNetosAbonos = new HashMap<>();
        Map<String, Double> groupoNetosCargos = new HashMap<>();
        Map<Integer, List<CifrasNomina>> grupoCentroCostos = new HashMap<>();
        for (CifrasNomina cn : cifrasNomina) {

            if (separarProvisiones) {
                if (tipoPoliza.contains("SEPARAR_PROVISION_NORMAL")) {
                    if (valoresPoliza.contains(cn.getNumeroconcepto())) {
                        continue;
                    }
                } else {
                    if (!valoresPoliza.contains(cn.getNumeroconcepto())) {
                        continue;
                    }
                }
            }

            Integer key = cn.getIdcentrocostos();
            if (cn.getPolizacontable().equals(BigDecimal.ONE.intValue())) {
                if (cn.getCuentacontable().startsWith("2")) {
                    if (BigDecimal.ZERO.toString().equals(cn.getCargoabono().toString())) {
                        String cuenta = cn.getCuentacontable() + "|" + BigDecimal.ZERO.toString();
                        if (groupoNetosCargos.containsKey(cuenta)) {
                            Double neto = groupoNetosCargos.get(cuenta);
                            neto += cn.getImporte();
                            groupoNetosCargos.put(cuenta, neto);
                        } else {
                            groupoNetosCargos.put(cuenta, cn.getImporte());
                        }
                    } else {
                        String cuenta = cn.getCuentacontable() + "|" + BigDecimal.ONE.toString();
                        if (groupoNetosAbonos.containsKey(cuenta)) {
                            Double neto = groupoNetosAbonos.get(cuenta);
                            neto += cn.getImporte();
                            groupoNetosAbonos.put(cuenta, neto);
                        } else {
                            groupoNetosAbonos.put(cuenta, cn.getImporte());
                        }
                    }
                    if (!(cn.getAuxcuentacontable().isEmpty() || cn.getAuxcuentacontable() == null || cn.getAuxcuentacontable().trim().isEmpty())) {
                        if (grupoCentroCostos.containsKey(key)) {
                            List<CifrasNomina> list = grupoCentroCostos.get(key);
                            list.add(cn);
                            grupoCentroCostos.put(key, list);
                        } else {
                            List<CifrasNomina> list = new ArrayList<>();
                            list.add(cn);
                            grupoCentroCostos.put(key, list);
                        }
                    }

                } else {
                    if (grupoCentroCostos.containsKey(key)) {
                        List<CifrasNomina> list = grupoCentroCostos.get(key);
                        list.add(cn);
                        grupoCentroCostos.put(key, list);
                    } else {
                        List<CifrasNomina> list = new ArrayList<>();
                        list.add(cn);
                        grupoCentroCostos.put(key, list);
                    }

                    if (cn.getAuxcuentacontable().startsWith("2")) {
                        if (BigDecimal.ZERO.toString().equals(cn.getCargoabono().toString())) {
                            String cuenta = cn.getAuxcuentacontable() + "|" + BigDecimal.ONE.toString();
                            if (groupoNetosCargos.containsKey(cuenta)) {
                                Double neto = groupoNetosCargos.get(cuenta);
                                neto += cn.getImporte();
                                groupoNetosCargos.put(cuenta, neto);
                            } else {
                                groupoNetosCargos.put(cuenta, cn.getImporte());
                            }
                        } else {
                            String cuenta = cn.getAuxcuentacontable() + "|" + BigDecimal.ZERO.toString();
                            if (groupoNetosAbonos.containsKey(cuenta)) {
                                Double neto = groupoNetosAbonos.get(cuenta);
                                neto += cn.getImporte();
                                groupoNetosAbonos.put(cuenta, neto);
                            } else {
                                groupoNetosAbonos.put(cuenta, cn.getImporte());
                            }
                        }
                    }
                }
            }
        }

        Map<Integer, HashMap<String, Double>> groupCentrosCostosCuentaContable = new HashMap<>();
        for (Map.Entry<Integer, List<CifrasNomina>> entry : grupoCentroCostos.entrySet()) {
            CentroCostos cc = mapCentrosCostos.get(entry.getKey());

            HashMap<String, Double> groupCuentaContableImporte = new HashMap<>();
            for (CifrasNomina cn : entry.getValue()) {
                String key = cn.getCuentacontable().trim() + "|" + cn.getNombreconcepto() + "|" + cn.getCargoabono() + "|" + cn.getAuxcuentacontable().trim();
                if (groupCuentaContableImporte.containsKey(key)) {
                    Double importe = groupCuentaContableImporte.get(key);
                    importe += cn.getImporte();
                    groupCuentaContableImporte.put(key, importe);
                } else {
                    groupCuentaContableImporte.put(key, cn.getImporte());
                }
            }
            groupCentrosCostosCuentaContable.put(cc.getIdCentroCostos(), groupCuentaContableImporte);
        }

        if (headerConceptos) {
            j = 1;
            i = 0;
        }

        for (Map.Entry<Integer, List<CifrasNomina>> entry : grupoCentroCostos.entrySet()) {
            CentroCostos cc = mapCentrosCostos.get(entry.getKey());
            HashMap<String, Double> cifrasNominaDepa = groupCentrosCostosCuentaContable.get(cc.getIdCentroCostos());
            SortedMap<String, Double> sortedCuentaContableImporte = new TreeMap<>(cifrasNominaDepa);
            for (Map.Entry<String, Double> data : sortedCuentaContableImporte.entrySet()) {
                if (data.getValue() > 0) {
                    String[] cuentaContBono = data.getKey().split("\\|");
                    if (headerConceptos) {
                        row = sheet.createRow(j);
                        cell = row.createCell(i++);
                        cell.setCellValue(cuentaContBono[1]);
                    } else {
                        row = sheet.createRow(j);
                        cell = row.createCell(i);
                    }

                    cell = row.createCell(i++);
                    cell.setCellValue(cuentaContBono[0]);

                    String cargoBono = cuentaContBono[2];
                    if (BigDecimal.ZERO.toString().equals(cargoBono)) {
                        cell = row.createCell(i++);
                        cell.setCellValue(data.getValue());
                        i++;
                    } else if (BigDecimal.ONE.toString().equals(cargoBono)) {
                        i++;
                        cell = row.createCell(i++);
                        cell.setCellValue(data.getValue());
                    }

                    cell = row.createCell(i++);
                    cell.setCellValue(cc.getNombre());

                    i = 0;
                    j++;

                    try {
                        if (groupoNetosCargos.containsKey(cuentaContBono[3]) || groupoNetosAbonos.containsKey(cuentaContBono[3])) {
                            if (headerConceptos) {
                                row = sheet.createRow(j);
                                cell = row.createCell(i++);
                                cell.setCellValue(cuentaContBono[1]);
                            } else {
                                row = sheet.createRow(j);
                                cell = row.createCell(i);
                            }

                            cell = row.createCell(i++);
                            cell.setCellValue(cuentaContBono[3]);

                            if (BigDecimal.ONE.toString().equals(cargoBono)) {
                                cell = row.createCell(i++);
                                cell.setCellValue(data.getValue());
                                i++;
                            } else if (BigDecimal.ZERO.toString().equals(cargoBono)) {
                                i++;
                                cell = row.createCell(i++);
                                cell.setCellValue(data.getValue());
                            }

                            cell = row.createCell(i++);
                            cell.setCellValue(cc.getNombre());

                            i = 0;
                            j++;
                        }
                    } catch (Exception ex) {
                        System.out.println("No Encontro CuentaAuxiliar " + cuentaContBono[1]);
                    }
                }
            }
        }

        if (headerConceptos) {
            i = 1;
        } else {
            i = 0;
        }
        SortedMap<String, Double> sortedGroupoNetosAbonos = new TreeMap<>(groupoNetosAbonos);
        for (Map.Entry<String, Double> entry : sortedGroupoNetosAbonos.entrySet()) {
            String[] llave = entry.getKey().split("\\|");
            String cargoAbono = llave[1];
            String cuentacontable = llave[0];

            row = sheet.createRow(j);
            cell = row.createCell(i++);

            cell.setCellValue(cuentacontable);

            if (BigDecimal.ZERO.toString().equals(cargoAbono)) {
                cell = row.createCell(i++);
                cell.setCellValue(entry.getValue());
                i++;
            } else if (BigDecimal.ONE.toString().equals(cargoAbono)) {
                i++;
                cell = row.createCell(i++);
                cell.setCellValue(entry.getValue());
            }

            if (headerConceptos) {
                i = 1;
            } else {
                i = 0;
            }
            j++;
        }

        SortedMap<String, Double> sortedGroupoNetosCargos = new TreeMap<>(groupoNetosCargos);
        for (Map.Entry<String, Double> entry : sortedGroupoNetosCargos.entrySet()) {
            String[] llave = entry.getKey().split("\\|");
            String cargoAbono = llave[1];
            String cuentacontable = llave[0];

            row = sheet.createRow(j);
            cell = row.createCell(i++);

            cell.setCellValue(cuentacontable);

            if (BigDecimal.ZERO.toString().equals(cargoAbono)) {
                cell = row.createCell(i++);
                cell.setCellValue(entry.getValue());
                i++;
            } else if (BigDecimal.ONE.toString().equals(cargoAbono)) {
                i++;
                cell = row.createCell(i++);
                cell.setCellValue(entry.getValue());
            }

            if (headerConceptos) {
                i = 1;
            } else {
                i = 0;
            }
            j++;
        }

        /**
         * Inicio Sheet AutoSizeColumn *
         */
        int total_y = 10;
        for (int x = 0; x <= total_y; x++) {
            sheet.autoSizeColumn(x);
        }
        /**
         * Fin Sheet AutoSizeColumn *
         */
        this.escribirArchivo(workbook, nombrearchivo, ParametrosReportes.ARCHIVO_XLS);
    }

    public void crearLayoutConekta(List<CifrasNomina> cifrasNomina, String nombrearchivo, boolean isPeriodo, Integer periodoMes, Integer anio, Double totalcargo, Double totalabono, Integer idtipoproceso) {
        Gson gson = new Gson();
        Map<Integer, Departamento> mapDepartamentos = ControladorWS.getInstance().getMapDepartamentosById();
        Map<String, CatalogoClaseCuentaContable> mapClases = ControladorWS.getInstance().getMapClasesByCuentaContable();
        Periodo periodo = null;
        if (isPeriodo) {
            Periodo per = new Periodo();
            TipoProceso tp = ControladorWS.getInstance().findTipoProcesoById(idtipoproceso);
            per.setAnio(anio);
            per.setIdtipoproceso(tp);
            per.setPeriodo(periodoMes);
            periodo = ControladorWS.getInstance().findPeriodoByTipoProcesoAndPeriodoAndAnio(per);
        }

        Map<String, LinkedList<CifrasNomina>> mapGruposCifrasNomina = new LinkedHashTreeMap<>();
        if (false) {
            Parametro parametro = ControladorWS.getInstance().getParametro("GRUPO_CONCEPTOS_POLIZA_CONTABLE_CONEKTA");
            Map<String, LinkedList<String>> mapGruposConceptos = gson.fromJson(parametro.getValor(), new TypeToken<Map<String, LinkedList<String>>>() {
            }.getType());

            for (Map.Entry<String, LinkedList<String>> entry : mapGruposConceptos.entrySet()) {
                String key = entry.getKey();
                LinkedList<String> conceptos = entry.getValue();
                for (CifrasNomina cn : cifrasNomina) {
                    if (conceptos.contains(cn.getNumeroconcepto())) {
                        if (mapGruposCifrasNomina.containsKey(key)) {
                            LinkedList<CifrasNomina> list = mapGruposCifrasNomina.get(key);
                            list.add(cn);
                        } else {
                            LinkedList<CifrasNomina> list = new LinkedList<>();
                            list.add(cn);
                            mapGruposCifrasNomina.put(key, list);
                        }
                    }
                }
            }
        } else {
            String key = "Conceptos";
            for (CifrasNomina cn : cifrasNomina) {
                if (mapGruposCifrasNomina.containsKey(key)) {
                    LinkedList<CifrasNomina> list = mapGruposCifrasNomina.get(key);
                    list.add(cn);
                } else {
                    LinkedList<CifrasNomina> list = new LinkedList<>();
                    list.add(cn);
                    mapGruposCifrasNomina.put(key, list);
                }
            }
        }

        Map<String, HashMap<String, Double>> mapCifrasNomina = new LinkedHashTreeMap<>();
        for (Map.Entry<String, LinkedList<CifrasNomina>> entry : mapGruposCifrasNomina.entrySet()) {
            String key = entry.getKey();

            LinkedList<CifrasNomina> cifras = entry.getValue();
            Collections.sort(cifras, new Comparator<CifrasNomina>() {
                @Override
                public int compare(CifrasNomina d1, CifrasNomina d2) {
                    return d1.getNumeroconcepto().compareTo(d2.getNumeroconcepto());
                }
            });

            HashMap<String, Double> mapConceptosDepartamentos = new LinkedHashMap<>();
            for (CifrasNomina cn : cifras) {
                String[] cc = !StringUtils.isNullOrEmpty(cn.getCuentacontable()) ? cn.getCuentacontable().split(",") : new String[1];
                String[] axcc = !StringUtils.isNullOrEmpty(cn.getAuxcuentacontable()) ? cn.getAuxcuentacontable().split(",") : new String[1];
                String[] asocc = !StringUtils.isNullOrEmpty(cn.getAsociarcuentacontable()) ? cn.getAsociarcuentacontable().split(",") : new String[1];
                boolean isSumatoriaCCConcepto = (cc.length > 1 ? "CTADEP".equals(cc[1]) : false);
                boolean isSumatoriaAXCCConcepto = (axcc.length > 1 ? "CTADEP".equals(axcc[1]) : false);
                boolean isSumatoriaASOCCConcepto = (asocc.length > 1 ? "CTADEP".equals(asocc[1]) : false);

                if (!StringUtils.isNullOrEmpty(cc[0])) {
                    String keyConceptoDepartamento = cn.getNumeroconcepto()
                            + "|" + cn.getNombreconcepto()
                            + "|" + (isSumatoriaCCConcepto ? cn.getIddepartamento() : 0)
                            + "|" + cc[0]
                            + "|" + cn.getCargoabono();

                    if (mapConceptosDepartamentos.containsKey(keyConceptoDepartamento)) {
                        Double importe = mapConceptosDepartamentos.get(keyConceptoDepartamento);
                        importe += cn.getImporte();
                        mapConceptosDepartamentos.put(keyConceptoDepartamento, importe);
                    } else {
                        mapConceptosDepartamentos.put(keyConceptoDepartamento, cn.getImporte());
                    }
                }

                if (!StringUtils.isNullOrEmpty(axcc[0])) {
                    String keyConceptoDepartamentoAuxiliar = cn.getNumeroconcepto()
                            + "|" + cn.getNombreconcepto()
                            + "|" + (isSumatoriaAXCCConcepto ? cn.getIddepartamento() : 0)
                            + "|" + axcc[0]
                            + "|" + (BigDecimal.ZERO.toString().equals(cn.getCargoabono().toString()) ? BigDecimal.ONE.toString() : BigDecimal.ZERO.toString());

                    if (mapConceptosDepartamentos.containsKey(keyConceptoDepartamentoAuxiliar)) {
                        Double importeAuxiliar = mapConceptosDepartamentos.get(keyConceptoDepartamentoAuxiliar);
                        importeAuxiliar += cn.getImporte();
                        mapConceptosDepartamentos.put(keyConceptoDepartamentoAuxiliar, importeAuxiliar);
                    } else {
                        mapConceptosDepartamentos.put(keyConceptoDepartamentoAuxiliar, cn.getImporte());
                    }
                }

                if (!StringUtils.isNullOrEmpty(asocc[0])) {
                    String keyConceptoDepartamentoAuxiliar = cn.getNumeroconcepto()
                            + "|" + cn.getNombreconcepto()
                            + "|" + (isSumatoriaASOCCConcepto ? cn.getIddepartamento() : 0)
                            + "|" + asocc[0]
                            + "|" + cn.getCargoabono()
                            + "|" + cc[0] + "/" + axcc[0];

                    if (mapConceptosDepartamentos.containsKey(keyConceptoDepartamentoAuxiliar)) {
                        Double importeAuxiliar = mapConceptosDepartamentos.get(keyConceptoDepartamentoAuxiliar);
                        importeAuxiliar += cn.getImporte();
                        mapConceptosDepartamentos.put(keyConceptoDepartamentoAuxiliar, importeAuxiliar);
                    } else {
                        mapConceptosDepartamentos.put(keyConceptoDepartamentoAuxiliar, cn.getImporte());
                    }
                }
            }
            mapCifrasNomina.put(key, mapConceptosDepartamentos);
        }

        Date hoy = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd'.'MM'.'yyyy", new Locale("es", "MX"));

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("PolizaContable " + periodoMes + anio);

        CellStyle styleazul = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setFontName(HSSFFont.FONT_ARIAL);
        font.setBoldweight(Short.MAX_VALUE);
        font.setColor(HSSFColor.BLUE.index);
        styleazul.setFont(font);
//        
        CellStyle stylerojo = workbook.createCellStyle();
        font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setFontName(HSSFFont.FONT_ARIAL);
        font.setBoldweight(Short.MAX_VALUE);
        font.setColor(HSSFColor.VIOLET.index);
        stylerojo.setFont(font);
//        
        CellStyle stylenegro = workbook.createCellStyle();
        font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setFontName(HSSFFont.FONT_ARIAL);
        font.setBoldweight(Short.MAX_VALUE);
        stylenegro.setFont(font);

        int j = 1;
        int i = 0;
//        
        HSSFRow row = null;
        HSSFCell cell = null;

        row = sheet.createRow(j);
        cell = row.createCell(i++);
        cell.setCellValue("Poliza");
        cell.setCellStyle(stylenegro);
//        
        cell = row.createCell(i++);
        cell.setCellValue("Fecha");
        cell.setCellStyle(stylenegro);
//        
        cell = row.createCell(i++);
        cell.setCellValue("Cuenta");
        cell.setCellStyle(stylenegro);
//        
        cell = row.createCell(i++);
        cell.setCellValue("ID Cuenta");
        cell.setCellStyle(stylenegro);
//        
        cell = row.createCell(i++);
        cell.setCellValue("Nombre de la Cuenta");
        cell.setCellStyle(stylenegro);
//
        cell = row.createCell(i++);
        cell.setCellValue("Debito");
        cell.setCellStyle(stylerojo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("Credito");
        cell.setCellStyle(styleazul);
//        
        cell = row.createCell(i++);
        cell.setCellValue("Nota");
        cell.setCellStyle(stylenegro);
//        
        cell = row.createCell(i++);
        cell.setCellValue("Departamento");
        cell.setCellStyle(stylenegro);
//        
        cell = row.createCell(i++);
        cell.setCellValue("ID Departamento");
        cell.setCellStyle(stylenegro);
//
        cell = row.createCell(i++);
        cell.setCellValue("Clase");
        cell.setCellStyle(stylenegro);
//        
        cell = row.createCell(i++);
        cell.setCellValue("ID Clase");
        cell.setCellStyle(stylenegro);

        j = 2;
        i = 0;
        Map<String, Double> mapTotalCargosAbonos = new HashMap<>();
        mapTotalCargosAbonos.put(BigDecimal.ZERO.toString(), 0.0);
        mapTotalCargosAbonos.put(BigDecimal.ONE.toString(), 0.0);
        List<ConektaPolizaContableTO> polizaContable = new ArrayList<>();
        for (Map.Entry<String, HashMap<String, Double>> entry : mapCifrasNomina.entrySet()) {
            String key = entry.getKey();
            HashMap<String, Double> cifrasCuentaContables = entry.getValue();

            if (!key.equals("Conceptos")) {
                row = sheet.createRow(j++);
                cell = row.createCell(4);
                cell.setCellValue(key);
                cell.setCellStyle(stylenegro);
            }

            i = 0;
            for (Map.Entry<String, Double> cifrasEntry : cifrasCuentaContables.entrySet()) {
                ConektaPolizaContableTO to = new ConektaPolizaContableTO();
                String[] llave = cifrasEntry.getKey().split("\\|");
                String nombreConcepto = llave[1];
                String idDepartamento = llave[2];

                String cuentaContableId = llave[3];
                String[] ccId = cuentaContableId.split("/");
                String cuentaContable = ccId[0];
                String idCuentaContable = ccId.length > 1 ? ccId[1] : null;

                String cargoAbono = llave[4];

                Double importeCifraNomina = cifrasEntry.getValue();

                row = sheet.createRow(j);
                cell = row.createCell(i++);
                cell.setCellValue(isPeriodo ? (periodo.getFechafin().getDay() == 15 ? 1 : 2) : periodoMes);
                to.setPoliza(isPeriodo ? (periodo.getFechafin().getDay() == 15 ? 1 : 2) : periodoMes);

                cell = row.createCell(i++);
                cell.setCellValue(isPeriodo ? sdf.format(periodo.getFechapago().toGregorianCalendar().getTime()) : sdf.format(hoy));
                to.setFecha(isPeriodo ? sdf.format(periodo.getFechapago().toGregorianCalendar().getTime()) : sdf.format(hoy));

                cell = row.createCell(i++);
                cell.setCellValue(cuentaContable);
                to.setCuenta(cuentaContable);

                cell = row.createCell(i++);
                cell.setCellValue(!StringUtils.isNullOrEmpty(idCuentaContable) ? idCuentaContable : "N/D");
                to.setIdCuenta(!StringUtils.isNullOrEmpty(idCuentaContable) ? idCuentaContable : "N/D");

                cell = row.createCell(i++);
                cell.setCellValue(cuentaContable + " - " + Util.capitalize(nombreConcepto.toLowerCase(), null));
                to.setNombreCuenta(cuentaContable + " - " + Util.capitalize(nombreConcepto.toLowerCase(), null));

                if (BigDecimal.ZERO.toString().equals(cargoAbono)) {
                    cell = row.createCell(i++);
                    cell.setCellValue(importeCifraNomina);
                    to.setDebito(importeCifraNomina);

                    cell = row.createCell(i++);
                    cell.setCellValue("");
                    to.setCredito(0.0);

                    if (mapTotalCargosAbonos.containsKey(BigDecimal.ZERO.toString())) {
                        Double importe = mapTotalCargosAbonos.get(BigDecimal.ZERO.toString());
                        importe += importeCifraNomina;
                        mapTotalCargosAbonos.put(BigDecimal.ZERO.toString(), importe);
                    }
                } else if (BigDecimal.ONE.toString().equals(cargoAbono)) {
                    cell = row.createCell(i++);
                    cell.setCellValue("");
                    to.setDebito(0.0);

                    cell = row.createCell(i++);
                    cell.setCellValue(importeCifraNomina);
                    to.setCredito(importeCifraNomina);

                    if (mapTotalCargosAbonos.containsKey(BigDecimal.ONE.toString())) {
                        Double importe = mapTotalCargosAbonos.get(BigDecimal.ONE.toString());
                        importe += importeCifraNomina;
                        mapTotalCargosAbonos.put(BigDecimal.ONE.toString(), importe);
                    }
                }

                String nota = "Registro de Nomina e impuestos a la nomina " + (isPeriodo
                        ? ((periodo.getFechafin().getDay() == 15 ? "1ra." : "2da.") + " Quincena " + Util.capitalize(periodo.getAniomes().toLowerCase(), null) + " " + periodo.getAnio())
                        : Util.getNombreMes(periodoMes) + " " + anio);
                cell = row.createCell(i++);
                cell.setCellValue(nota);
                to.setNota(nota);

                Departamento departamento = mapDepartamentos.get(Integer.parseInt(idDepartamento));
                if (departamento != null) {
                    cell = row.createCell(i++);
                    cell.setCellValue(Util.capitalize(departamento.getNombre().toLowerCase(), null));
                    to.setDepartamento(Util.capitalize(departamento.getNombre().toLowerCase(), null));

                    cell = row.createCell(i++);
                    cell.setCellValue(departamento.getDepartamento());
                    to.setIdDepartamento(departamento.getDepartamento());
                } else {
                    cell = row.createCell(i++);
                    cell.setCellValue("");

                    cell = row.createCell(i++);
                    cell.setCellValue("");
                }

                CatalogoClaseCuentaContable clase = mapClases.get(cuentaContable);
                if (clase != null) {
                    cell = row.createCell(i++);
                    cell.setCellValue(clase.getClase());
                    to.setClase(clase.getClase());

                    cell = row.createCell(i++);
                    cell.setCellValue(clase.getIdclase());
                    to.setIdClase(clase.getIdclase());
                }

                j++;
                i = 0;
                polizaContable.add(to);
            }

            j++;
            i = 0;
        }

        /**
         * Totales Cargo Abono *
         */
        row = sheet.createRow(0);
        cell = row.createCell(5);
        cell.setCellValue(mapTotalCargosAbonos.get(BigDecimal.ZERO.toString()));
        cell.setCellStyle(stylerojo);

        cell = row.createCell(6);
        cell.setCellValue(mapTotalCargosAbonos.get(BigDecimal.ONE.toString()));
        cell.setCellStyle(styleazul);

        /**
         * Inicio Sheet AutoSizeColumn *
         */
        int total_y = 10;
        for (int x = 0; x <= total_y; x++) {
            sheet.autoSizeColumn(x);
        }
        /**
         * Fin Sheet AutoSizeColumn *
         */
        if (isPeriodo) {
            IntegracionPolizaContable ipc = ControladorWS.getInstance().findIntegracionPolizaContableByIdPeriodo(periodo.getIdperiodo());
            if (ipc == null) {
                ipc = new IntegracionPolizaContable();
                ipc.setIdPeriodo(periodo.getIdperiodo());
                ipc.setIdEstatus(BigDecimal.ONE.intValue());
                ipc.setPolizaContable(gson.toJson(polizaContable));
                ControladorWS.getInstance().saveIntegracionPolizaContable(ipc);
            } else {
                if (ipc.getIdEstatus().equals(BigDecimal.ONE.intValue())) {
                    ipc.setPolizaContable(gson.toJson(polizaContable));
                    ControladorWS.getInstance().updateIntegracionPolizaContable(ipc);
                }
            }
        }
        this.escribirArchivo(workbook, nombrearchivo, ParametrosReportes.ARCHIVO_XLS);
    }

    public void crearLayoutRequorditDepartamentos(List<CifrasNomina> cifrasNomina, String nombrearchivo, Integer periodoMes, int anio, double totalcargo, double totalabono, boolean headerConceptos, Integer idtipoproceso) {
        System.out.println("::::::::: Cifras Nomina Requordit :: Tamano :: " + cifrasNomina.size());
        Map<Integer, Departamento> mapDepartamentos = ControladorWS.getInstance().getMapDepartamentosById();
        TipoProceso tp = ControladorWS.getInstance().findTipoProcesoById(idtipoproceso);
        Periodo per = new Periodo();
        per.setAnio(anio);
        per.setIdtipoproceso(tp);
        per.setPeriodo(periodoMes);
        Periodo periodo = ControladorWS.getInstance().findPeriodoByTipoProcesoAndPeriodoAndAnio(per);

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("PolizaContable " + periodoMes + anio);

        CellStyle styleazul = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setFontName(HSSFFont.FONT_ARIAL);
        font.setBoldweight(Short.MAX_VALUE);
        font.setColor(HSSFColor.BLUE.index);
        styleazul.setFont(font);
//        
        CellStyle stylerojo = workbook.createCellStyle();
        font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setFontName(HSSFFont.FONT_ARIAL);
        font.setBoldweight(Short.MAX_VALUE);
        font.setColor(HSSFColor.VIOLET.index);
        stylerojo.setFont(font);
//        
        CellStyle stylenegro = workbook.createCellStyle();
        font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setFontName(HSSFFont.FONT_ARIAL);
        font.setBoldweight(Short.MAX_VALUE);
        stylenegro.setFont(font);

        int j = 0;
        int i = 0;
//        
        HSSFRow row = null;
        HSSFCell cell = null;

        if (headerConceptos) {
            row = sheet.createRow(j);//        
            cell = row.createCell(i++);
            cell.setCellValue("*");
            cell.setCellStyle(stylenegro);
//        
            cell = row.createCell(i++);
            cell.setCellValue("Cuenta contable");
            cell.setCellStyle(stylenegro);
//        
            cell = row.createCell(i++);
            cell.setCellValue("Nombre cuenta");
            cell.setCellStyle(stylenegro);
//        
            cell = row.createCell(i++);
            cell.setCellValue("Cargo");
            cell.setCellStyle(stylerojo);
//        
            cell = row.createCell(i++);
            cell.setCellValue("Abono");
            cell.setCellStyle(styleazul);
//        
            cell = row.createCell(i++);
            cell.setCellValue("Referencia");
            cell.setCellStyle(stylenegro);
//        
            cell = row.createCell(i++);
            cell.setCellValue("Concepto");
            cell.setCellStyle(stylenegro);
//        
            cell = row.createCell(i++);
            cell.setCellValue("Segmento");
            cell.setCellStyle(stylenegro);
//        
            cell = row.createCell(i++);
            cell.setCellValue("Tipo movimiento");
            cell.setCellStyle(stylenegro);
        }

        Map<String, Double> groupoNetosAbonos = new HashMap<>();
        Map<String, Double> groupoNetosCargos = new HashMap<>();
        Map<Integer, List<CifrasNomina>> groupoDepartamentos = new HashMap<>();
        for (CifrasNomina cn : cifrasNomina) {
            Integer key = cn.getIddepartamento();
            if (cn.getPolizacontable().equals(BigDecimal.ONE.intValue())) {
                if (cn.getCuentacontable().startsWith("2")) {
                    if (BigDecimal.ZERO.toString().equals(cn.getCargoabono().toString())) {
                        String cuenta = cn.getCuentacontable() + "|" + BigDecimal.ZERO.toString();
                        if (groupoNetosCargos.containsKey(cuenta)) {
                            Double neto = groupoNetosCargos.get(cuenta);
                            neto += cn.getImporte();
                            groupoNetosCargos.put(cuenta, neto);
                        } else {
                            groupoNetosCargos.put(cuenta, cn.getImporte());
                        }
                    } else {
                        String cuenta = cn.getCuentacontable() + "|" + BigDecimal.ONE.toString();
                        if (groupoNetosAbonos.containsKey(cuenta)) {
                            Double neto = groupoNetosAbonos.get(cuenta);
                            neto += cn.getImporte();
                            groupoNetosAbonos.put(cuenta, neto);
                        } else {
                            groupoNetosAbonos.put(cuenta, cn.getImporte());
                        }
                    }
                    if (!(cn.getAuxcuentacontable().isEmpty() || cn.getAuxcuentacontable() == null || cn.getAuxcuentacontable().trim().isEmpty())) {
                        if (groupoDepartamentos.containsKey(key)) {
                            List<CifrasNomina> list = groupoDepartamentos.get(key);
                            list.add(cn);
                            groupoDepartamentos.put(key, list);
                        } else {
                            List<CifrasNomina> list = new ArrayList<>();
                            list.add(cn);
                            groupoDepartamentos.put(key, list);
                        }
                    }

                } else {
                    if (groupoDepartamentos.containsKey(key)) {
                        List<CifrasNomina> list = groupoDepartamentos.get(key);
                        list.add(cn);
                        groupoDepartamentos.put(key, list);
                    } else {
                        List<CifrasNomina> list = new ArrayList<>();
                        list.add(cn);
                        groupoDepartamentos.put(key, list);
                    }

                    if (cn.getAuxcuentacontable().startsWith("2")) {
                        if (BigDecimal.ZERO.toString().equals(cn.getCargoabono().toString())) {
                            String cuenta = cn.getAuxcuentacontable() + "|" + BigDecimal.ONE.toString();
                            if (groupoNetosCargos.containsKey(cuenta)) {
                                Double neto = groupoNetosCargos.get(cuenta);
                                neto += cn.getImporte();
                                groupoNetosCargos.put(cuenta, neto);
                            } else {
                                groupoNetosCargos.put(cuenta, cn.getImporte());
                            }
                        } else {
                            String cuenta = cn.getAuxcuentacontable() + "|" + BigDecimal.ZERO.toString();
                            if (groupoNetosAbonos.containsKey(cuenta)) {
                                Double neto = groupoNetosAbonos.get(cuenta);
                                neto += cn.getImporte();
                                groupoNetosAbonos.put(cuenta, neto);
                            } else {
                                groupoNetosAbonos.put(cuenta, cn.getImporte());
                            }
                        }
                    }
                }
            }
        }

        Map<Integer, HashMap<String, Double>> groupDepartamentoCuentaContable = new HashMap<>();
        for (Map.Entry<Integer, List<CifrasNomina>> entry : groupoDepartamentos.entrySet()) {
            Departamento departamento = mapDepartamentos.get(entry.getKey());

            HashMap<String, Double> groupCuentaContableImporte = new HashMap<>();
            for (CifrasNomina cn : entry.getValue()) {
                String key = cn.getCuentacontable().trim() + "|" + cn.getNombreconcepto() + "|" + cn.getCargoabono() + "|" + cn.getAuxcuentacontable().trim();
                if (groupCuentaContableImporte.containsKey(key)) {
                    Double importe = groupCuentaContableImporte.get(key);
                    importe += cn.getImporte();
                    groupCuentaContableImporte.put(key, importe);
                } else {
                    groupCuentaContableImporte.put(key, cn.getImporte());
                }
            }
            groupDepartamentoCuentaContable.put(departamento.getIddepartamento(), groupCuentaContableImporte);
        }

        if (headerConceptos) {
            j = 1;
            i = 0;
        }

        for (Map.Entry<Integer, List<CifrasNomina>> entry : groupoDepartamentos.entrySet()) {
            Departamento departamento = mapDepartamentos.get(entry.getKey());
            HashMap<String, Double> cifrasNominaDepa = groupDepartamentoCuentaContable.get(departamento.getIddepartamento());
            SortedMap<String, Double> sortedCuentaContableImporte = new TreeMap<>(cifrasNominaDepa);
            for (Map.Entry<String, Double> data : sortedCuentaContableImporte.entrySet()) {
                if (data.getValue() > 0) {
                    String[] cuentaContBono = data.getKey().split("\\|");

                    row = sheet.createRow(j);
                    cell = row.createCell(i++);
                    cell.setCellValue("C/M");

                    cell = row.createCell(i++);
                    cell.setCellValue(cuentaContBono[0]);

                    cell = row.createCell(i++);
                    cell.setCellValue(cuentaContBono[1]);

                    String cargoBono = cuentaContBono[2];
                    if (BigDecimal.ZERO.toString().equals(cargoBono)) {
                        cell = row.createCell(i++);
                        cell.setCellValue(data.getValue());
                        i++;
                    } else if (BigDecimal.ONE.toString().equals(cargoBono)) {
                        i++;
                        cell = row.createCell(i++);
                        cell.setCellValue(data.getValue());
                    }

                    cell = row.createCell(i++);
                    cell.setCellValue((periodo.getFechafin().getDay() == 15 ? "1ST" : "2ND") + " BE-WEEKLY");

                    cell = row.createCell(i++);
                    cell.setCellValue("PAYROLL PROVISION FROM " + Util.getNombreMes(periodoMes) + " " + departamento.getNombre() + " " + (periodo.getFechafin().getDay() == 15 ? "1" : "2"));

                    cell = row.createCell(i++);
                    cell.setCellValue("099");

                    cell = row.createCell(i++);
                    cell.setCellValue(cargoBono.toString());

                    i = 0;
                    j++;

                    try {
                        if (groupoNetosCargos.containsKey(cuentaContBono[3]) || groupoNetosAbonos.containsKey(cuentaContBono[3])) {

                            row = sheet.createRow(j);

                            cell = row.createCell(i++);
                            cell.setCellValue("C/M");

                            cell = row.createCell(i++);
                            cell.setCellValue(cuentaContBono[3]);

                            cell = row.createCell(i++);
                            cell.setCellValue(cuentaContBono[1]);

                            if (BigDecimal.ONE.toString().equals(cargoBono)) {
                                cell = row.createCell(i++);
                                cell.setCellValue(data.getValue());
                                i++;
                            } else if (BigDecimal.ZERO.toString().equals(cargoBono)) {
                                i++;
                                cell = row.createCell(i++);
                                cell.setCellValue(data.getValue());
                            }

                            cell = row.createCell(i++);
                            cell.setCellValue((periodo.getFechafin().getDay() == 15 ? "1ST" : "2ND") + " BE-WEEKLY");

                            cell = row.createCell(i++);
                            cell.setCellValue("PAYROLL PROVISION FROM " + Util.getNombreMes(periodoMes).toUpperCase() + " " + departamento.getNombre() + " " + (periodo.getFechafin().getDay() == 15 ? "1" : "2"));

                            cell = row.createCell(i++);
                            cell.setCellValue("099");

                            cell = row.createCell(i++);
                            cell.setCellValue(cargoBono.toString());

                            i = 0;
                            j++;
                        }
                    } catch (Exception ex) {
                        System.out.println("No Encontro CuentaAuxiliar " + cuentaContBono[1]);
                    }
                }
            }
        }

        SortedMap<String, Double> sortedGroupoNetosAbonos = new TreeMap<>(groupoNetosAbonos);
        for (Map.Entry<String, Double> entry : sortedGroupoNetosAbonos.entrySet()) {
            String[] llave = entry.getKey().split("\\|");
            String cargoAbono = llave[1];
            String cuentacontable = llave[0];

            row = sheet.createRow(j);

            cell = row.createCell(i++);
            cell.setCellValue("");

            cell = row.createCell(i++);
            cell.setCellValue(cuentacontable);

            cell = row.createCell(i++);
            cell.setCellValue("");

            if (BigDecimal.ZERO.toString().equals(cargoAbono)) {
                cell = row.createCell(i++);
                cell.setCellValue(entry.getValue());
                i++;
            } else if (BigDecimal.ONE.toString().equals(cargoAbono)) {
                i++;
                cell = row.createCell(i++);
                cell.setCellValue(entry.getValue());
            }

            i = 0;
            j++;
        }

        SortedMap<String, Double> sortedGroupoNetosCargos = new TreeMap<>(groupoNetosCargos);
        for (Map.Entry<String, Double> entry : sortedGroupoNetosCargos.entrySet()) {
            String[] llave = entry.getKey().split("\\|");
            String cargoAbono = llave[1];
            String cuentacontable = llave[0];

            row = sheet.createRow(j);

            cell = row.createCell(i++);
            cell.setCellValue("");

            cell = row.createCell(i++);
            cell.setCellValue(cuentacontable);

            cell = row.createCell(i++);
            cell.setCellValue("");

            if (BigDecimal.ZERO.toString().equals(cargoAbono)) {
                cell = row.createCell(i++);
                cell.setCellValue(entry.getValue());
                i++;
            } else if (BigDecimal.ONE.toString().equals(cargoAbono)) {
                i++;
                cell = row.createCell(i++);
                cell.setCellValue(entry.getValue());
            }

            i = 0;
            j++;
        }

        /**
         * Inicio Sheet AutoSizeColumn *
         */
        int total_y = 10;
        for (int x = 0; x <= total_y; x++) {
            sheet.autoSizeColumn(x);
        }
        /**
         * Fin Sheet AutoSizeColumn *
         */
        this.escribirArchivo(workbook, nombrearchivo, ParametrosReportes.ARCHIVO_XLS);
    }

    public void crearLayoutMelonnCuentaContableCentrosCostos(List<CifrasNomina> cifrasNomina, String nombrearchivo, Integer periodoMes, int anio, double totalcargo, double totalabono, Integer idtipoproceso) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        TipoProceso tp = ControladorWS.getInstance().findTipoProcesoById(idtipoproceso);
        Periodo per = new Periodo();
        per.setAnio(anio);
        per.setIdtipoproceso(tp);
        per.setPeriodo(periodoMes);
        Periodo periodo = ControladorWS.getInstance().findPeriodoByTipoProcesoAndPeriodoAndAnio(per);
        Map<Integer, CentroCostos> mapCentrosCostos = ControladorWS.getInstance().getMapCentrosCostosById();

        Map<Integer, List<CifrasNomina>> grupoCentroCostos = new HashMap<>();
        for (CifrasNomina cn : cifrasNomina) {
            if (cn.getIdcentrocostos() != null) {
                Integer key = cn.getIdcentrocostos();
                if (cn.getPolizacontable().equals(BigDecimal.ONE.intValue())) {
                    if (grupoCentroCostos.containsKey(key)) {
                        List<CifrasNomina> list = grupoCentroCostos.get(key);
                        list.add(cn);
                        grupoCentroCostos.put(key, list);
                    } else {
                        List<CifrasNomina> list = new ArrayList<>();
                        list.add(cn);
                        grupoCentroCostos.put(key, list);
                    }
                }
            }
        }

        Map<Integer, HashMap<String, Double>> groupCentrosCostosCuentaContable = new HashMap<>();
        for (Map.Entry<Integer, List<CifrasNomina>> entry : grupoCentroCostos.entrySet()) {
            CentroCostos cc = mapCentrosCostos.get(entry.getKey());
            HashMap<String, Double> groupCuentaContableImporte = new HashMap<>();
            for (CifrasNomina cn : entry.getValue()) {
                String key = cn.getCuentacontable().trim() + "|" + cn.getNombreconcepto() + "|" + cn.getCargoabono();
                if (groupCuentaContableImporte.containsKey(key)) {
                    Double importe = groupCuentaContableImporte.get(key);
                    importe += cn.getImporte();
                    groupCuentaContableImporte.put(key, importe);
                } else {
                    groupCuentaContableImporte.put(key, cn.getImporte());
                }
            }
            if (cc != null) {
                groupCentrosCostosCuentaContable.put(cc.getIdCentroCostos(), groupCuentaContableImporte);
            }
        }

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("PolizaContable " + periodoMes + anio);

        HSSFFont fontBlack = workbook.createFont();
        fontBlack.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        fontBlack.setColor(HSSFColor.BLACK.index);
        HSSFCellStyle styleBlack = workbook.createCellStyle();
        styleBlack.setFont(fontBlack);

        HSSFCellStyle styleCenter = workbook.createCellStyle();
        styleCenter.setAlignment(HorizontalAlignment.CENTER);

        int j = 0;
        int i = 0;

        HSSFRow row = sheet.createRow(j);
        HSSFCell cell = row.createCell(i++);
        cell.setCellValue("Filas Tipo Campo");
        cell.setCellStyle(styleBlack);

        cell = row.createCell(i++);
        cell.setCellValue("Numero de registro");
        cell.setCellStyle(styleBlack);

        cell = row.createCell(i++);
        cell.setCellValue("Tipo de registro");
        cell.setCellStyle(styleBlack);

        cell = row.createCell(i++);
        cell.setCellValue("Subtipo de registro");
        cell.setCellStyle(styleBlack);

        cell = row.createCell(i++);
        cell.setCellValue("Versión del tipo de registro");
        cell.setCellStyle(styleBlack);

        cell = row.createCell(i++);
        cell.setCellValue("Compañía");
        cell.setCellStyle(styleBlack);

        cell = row.createCell(i++);
        cell.setCellValue("Centro de operación del documento");
        cell.setCellStyle(styleBlack);

        cell = row.createCell(i++);
        cell.setCellValue("Tipo de documento");
        cell.setCellStyle(styleBlack);

        cell = row.createCell(i++);
        cell.setCellValue("Numero de documento");
        cell.setCellStyle(styleBlack);

        cell = row.createCell(i++);
        cell.setCellValue("Auxiliar de cuenta contable");
        cell.setCellStyle(styleBlack);

        cell = row.createCell(i++);
        cell.setCellValue("Tercero");
        cell.setCellStyle(styleBlack);

        cell = row.createCell(i++);
        cell.setCellValue("Centro de operación del movimiento");
        cell.setCellStyle(styleBlack);

        cell = row.createCell(i++);
        cell.setCellValue("Unidad de negocio");
        cell.setCellStyle(styleBlack);

        cell = row.createCell(i++);
        cell.setCellValue("Auxiliar de centro de costos");
        cell.setCellStyle(styleBlack);

        cell = row.createCell(i++);
        cell.setCellValue("Auxiliar de concepto de fuljo de efectivo");
        cell.setCellStyle(styleBlack);

        cell = row.createCell(i++);
        cell.setCellValue("Valor debito");
        cell.setCellStyle(styleBlack);

        cell = row.createCell(i++);
        cell.setCellValue("Valor crédito");
        cell.setCellStyle(styleBlack);

        cell = row.createCell(i++);
        cell.setCellValue("Valor debito alterno");
        cell.setCellStyle(styleBlack);

        cell = row.createCell(i++);
        cell.setCellValue("Valor crédito alterno");
        cell.setCellStyle(styleBlack);

        cell = row.createCell(i++);
        cell.setCellValue("Valor base gravable");
        cell.setCellStyle(styleBlack);

        cell = row.createCell(i++);
        cell.setCellValue("Tipo de documento de banco");
        cell.setCellStyle(styleBlack);

        cell = row.createCell(i++);
        cell.setCellValue("Numero de documento de banco");
        cell.setCellStyle(styleBlack);

        cell = row.createCell(i++);
        cell.setCellValue("Observaciones del movimiento");
        cell.setCellStyle(styleBlack);

        j = 1;
        i = 1;
        row = sheet.createRow(j);
        cell = row.createCell(i++);
        cell.setCellValue("Num");
        cell.setCellStyle(styleCenter);

        cell = row.createCell(i++);
        cell.setCellValue("Num");
        cell.setCellStyle(styleCenter);

        cell = row.createCell(i++);
        cell.setCellValue("Num");
        cell.setCellStyle(styleCenter);

        cell = row.createCell(i++);
        cell.setCellValue("Num");
        cell.setCellStyle(styleCenter);

        cell = row.createCell(i++);
        cell.setCellValue("Num");
        cell.setCellStyle(styleCenter);

        cell = row.createCell(i++);
        cell.setCellValue("Alf");
        cell.setCellStyle(styleCenter);

        cell = row.createCell(i++);
        cell.setCellValue("Alf");
        cell.setCellStyle(styleCenter);

        cell = row.createCell(i++);
        cell.setCellValue("Num");
        cell.setCellStyle(styleCenter);

        cell = row.createCell(i++);
        cell.setCellValue("Alf");
        cell.setCellStyle(styleCenter);

        cell = row.createCell(i++);
        cell.setCellValue("Alf");
        cell.setCellStyle(styleCenter);

        cell = row.createCell(i++);
        cell.setCellValue("Alf");
        cell.setCellStyle(styleCenter);

        cell = row.createCell(i++);
        cell.setCellValue("Alf");
        cell.setCellStyle(styleCenter);

        cell = row.createCell(i++);
        cell.setCellValue("Alf");
        cell.setCellStyle(styleCenter);

        cell = row.createCell(i++);
        cell.setCellValue("Alf");
        cell.setCellStyle(styleCenter);

        cell = row.createCell(i++);
        cell.setCellValue("Num");
        cell.setCellStyle(styleCenter);

        cell = row.createCell(i++);
        cell.setCellValue("Num");
        cell.setCellStyle(styleCenter);

        cell = row.createCell(i++);
        cell.setCellValue("Num");
        cell.setCellStyle(styleCenter);

        cell = row.createCell(i++);
        cell.setCellValue("Num");
        cell.setCellStyle(styleCenter);

        cell = row.createCell(i++);
        cell.setCellValue("Num");
        cell.setCellStyle(styleCenter);

        cell = row.createCell(i++);
        cell.setCellValue("Alf");
        cell.setCellStyle(styleCenter);

        cell = row.createCell(i++);
        cell.setCellValue("Num");
        cell.setCellStyle(styleCenter);

        cell = row.createCell(i++);
        cell.setCellValue("Alf");
        cell.setCellStyle(styleCenter);

        j = 2;
        i = 1;
        row = sheet.createRow(j);
        cell = row.createCell(i++);
        cell.setCellValue("7");
        cell.setCellStyle(styleCenter);

        cell = row.createCell(i++);
        cell.setCellValue("4");
        cell.setCellStyle(styleCenter);

        cell = row.createCell(i++);
        cell.setCellValue("2");
        cell.setCellStyle(styleCenter);

        cell = row.createCell(i++);
        cell.setCellValue("2");
        cell.setCellStyle(styleCenter);

        cell = row.createCell(i++);
        cell.setCellValue("3");
        cell.setCellStyle(styleCenter);

        cell = row.createCell(i++);
        cell.setCellValue("3");
        cell.setCellStyle(styleCenter);

        cell = row.createCell(i++);
        cell.setCellValue("3");
        cell.setCellStyle(styleCenter);

        cell = row.createCell(i++);
        cell.setCellValue("8");
        cell.setCellStyle(styleCenter);

        cell = row.createCell(i++);
        cell.setCellValue("20");
        cell.setCellStyle(styleCenter);

        cell = row.createCell(i++);
        cell.setCellValue("15");
        cell.setCellStyle(styleCenter);

        cell = row.createCell(i++);
        cell.setCellValue("3");
        cell.setCellStyle(styleCenter);

        cell = row.createCell(i++);
        cell.setCellValue("20");
        cell.setCellStyle(styleCenter);

        cell = row.createCell(i++);
        cell.setCellValue("15");
        cell.setCellStyle(styleCenter);

        cell = row.createCell(i++);
        cell.setCellValue("10");
        cell.setCellStyle(styleCenter);

        cell = row.createCell(i++);
        cell.setCellValue("21");
        cell.setCellStyle(styleCenter);

        cell = row.createCell(i++);
        cell.setCellValue("21");
        cell.setCellStyle(styleCenter);

        cell = row.createCell(i++);
        cell.setCellValue("21");
        cell.setCellStyle(styleCenter);

        cell = row.createCell(i++);
        cell.setCellValue("21");
        cell.setCellStyle(styleCenter);

        cell = row.createCell(i++);
        cell.setCellValue("21");
        cell.setCellStyle(styleCenter);

        cell = row.createCell(i++);
        cell.setCellValue("2");
        cell.setCellStyle(styleCenter);

        cell = row.createCell(i++);
        cell.setCellValue("8");
        cell.setCellStyle(styleCenter);

        cell = row.createCell(i++);
        cell.setCellValue("255");
        cell.setCellStyle(styleCenter);

        int n = 3;
        int m = 1;
        j = 3;
        i = 0;
        for (Map.Entry<Integer, List<CifrasNomina>> entry : grupoCentroCostos.entrySet()) {
            CentroCostos cc = mapCentrosCostos.get(entry.getKey());
            HashMap<String, Double> cccc = groupCentrosCostosCuentaContable.get(cc.getIdCentroCostos());
            SortedMap<String, Double> sortedCuentaContableImporte = new TreeMap<>(cccc);
            for (Map.Entry<String, Double> data : sortedCuentaContableImporte.entrySet()) {
                row = sheet.createRow(j);
                cell = row.createCell(i++);
                cell.setCellValue(m++);

                cell = row.createCell(i++);
                cell.setCellValue(n++);

                cell = row.createCell(i++);
                cell.setCellValue("0351");

                cell = row.createCell(i++);
                cell.setCellValue("00");

                cell = row.createCell(i++);
                cell.setCellValue("02");

                cell = row.createCell(i++);
                cell.setCellValue("001");

                cell = row.createCell(i++);
                cell.setCellValue("001");

                cell = row.createCell(i++);
                cell.setCellValue(tp.getTipoproceso());

                cell = row.createCell(i++);
                cell.setCellValue("2");

                String[] cuentaContBono = data.getKey().split("\\|");
                cell = row.createCell(i++);
                cell.setCellValue(cuentaContBono[0]);

                cell = row.createCell(i++);
                cell.setCellValue("");

                cell = row.createCell(i++);
                cell.setCellValue("700");

                cell = row.createCell(i++);
                cell.setCellValue("001");

                cell = row.createCell(i++);
                cell.setCellValue(cc.getCentroCosto());

                cell = row.createCell(i++);
                cell.setCellValue("");

                String cargoBono = cuentaContBono[2];
                if (BigDecimal.ZERO.toString().equals(cargoBono)) {
                    cell = row.createCell(i++);
                    cell.setCellValue(data.getValue());

                    cell = row.createCell(i++);
                    cell.setCellValue("0.00");
                } else if (BigDecimal.ONE.toString().equals(cargoBono)) {
                    cell = row.createCell(i++);
                    cell.setCellValue("0.00");

                    cell = row.createCell(i++);
                    cell.setCellValue(data.getValue());
                }

                cell = row.createCell(i++);
                cell.setCellValue("0.00");

                cell = row.createCell(i++);
                cell.setCellValue("0.00");

                cell = row.createCell(i++);
                cell.setCellValue("0");

                cell = row.createCell(i++);
                cell.setCellValue("");

                cell = row.createCell(i++);
                cell.setCellValue("");

                cell = row.createCell(i++);
                cell.setCellValue("COMPROBANTE DE NOMINA " + sdf.format(periodo.getFechapago().toGregorianCalendar().getTime()));

                j++;
                i = 0;
            }
        }

        /**
         * Inicio Sheet AutoSizeColumn *
         */
        int total_y = 255;
        for (int x = 0; x <= total_y; x++) {
            sheet.autoSizeColumn(x);
        }
        /**
         * Fin Sheet AutoSizeColumn *
         */
        this.escribirArchivo(workbook, nombrearchivo, ParametrosReportes.ARCHIVO_XLS);
    }
}
