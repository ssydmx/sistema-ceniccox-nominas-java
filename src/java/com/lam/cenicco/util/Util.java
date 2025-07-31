/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lam.cenicco.util;

import com.lam.cenicco.impl.ControladorWS;
import com.lam.cenicco.librerias.mensajes.Mensajes;
import com.lam.cenicco.librerias.utilidades.CeniccoUtil;
import com.lam.cenicco.to.FacturaTO;
import com.lam.cenicco.util.paths.MyPaths;
import com.lam.cenicco.ws.Banco;
import com.lam.cenicco.ws.CentroCostos;
import com.lam.cenicco.ws.CifrasNomina;
import com.lam.cenicco.ws.Concepto;
import com.lam.cenicco.ws.ConceptoAcumulado;
import com.lam.cenicco.ws.Credito;
import com.lam.cenicco.ws.CuentaBancaria;
import com.lam.cenicco.ws.Domicilio;
import com.lam.cenicco.ws.Empleado;
import com.lam.cenicco.ws.EstructuraDatos;
import com.lam.cenicco.ws.GrupoPago;
import com.lam.cenicco.ws.Infonavit;
import com.lam.cenicco.ws.Pension;
import com.lam.cenicco.ws.Periodo;
import com.lam.cenicco.ws.ReciboNomina;
import com.lam.cenicco.ws.RegistroPatronal;
import com.lam.cenicco.ws.RelacionLaboral;
import com.lam.cenicco.ws.RelacionLaboralPosicion;
import com.lam.cenicco.ws.TabuladorSistemaAntiguedad;
import com.lam.cenicco.ws.TipoProceso;
import com.lam.cenicco.ws.Usuario;
import com.lam.cenicco.ws.VistaSaldoCreditos;
import com.lam.cenicco.ws.WSCenicco;
import com.lam.cenicco.ws.WSCenicco_Service;
import com.mysql.jdbc.StringUtils;
import com.sun.xml.ws.api.message.Headers;
import com.sun.xml.ws.developer.WSBindingProvider;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.ws.handler.MessageContext;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.commons.codec.binary.Base64;
import org.apache.poi.util.IOUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author Diego
 */
public class Util {

    public static final String REGEX_NUMERO_EMPLEADO = "^[0-9]{1,9}$";
    public static final String REGEX_NSS = "^[0-9]{11}$";
    public static final String REGEX_SEXO = "^[H|M|h|m]$";
    public static final String REGEX_ESTATUS = "^ACTIVO$|^activo$|^INACTIVO$|^inactivo$|^REGISTRADO$|^registrado$";
    public static final String REGEX_EDO_CIVIL = "^SOLTERO$|^CASADO$|^DIVORCIADO$|^UNIONLIBRE$|^VIUDO$|^SEPARADO$|^soltero$|^casado$|^divorciado$|^unionlibre$|^viudo$|^separado$|^Soltero$|^Casado$|^Divorciado$|^Union Libre$|^Viudo$|^Separado$";
    public static final String REGEX_FECHA = "^(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])/((19|20)\\d\\d)$";
    public static final String REGEX_RFC = "^[a-zA-Z]{4}(\\d{6})((\\D|\\d){3})$";
    public static final String REGEX_DECIMAL = "^[0-9]+(\\.[0-9]{1,2})?$";
    public static final String REGEX_ENTERO = "^[0-9]{2}$";
    public static final String REGEX_CURP = "^[A-Z][A,E,I,O,U,X][A-Z]{2}[0-9]{2}[0-1][0-9][0-3][0-9][M,H][A-Z]{2}[B,C,D,F,G,H,J,K,L,M,N,Ñ,P,Q,R,S,T,V,W,X,Y,Z]{3}[0-9,A-Z][0-9]$";
    public static final String REGEX_CUENTA_BANCARIA = "^([0-9]{10})$|^((0){1})$";
    public static final String REGEX_CUENTA_BANCARIA_BANAMEX = "^([0-9]{11})$|^((0){1})$";
    public static final String REGEX_CUENTA_BANCARIA_BANBAJIO = "^([0-9]{12})$|^((0){1})$";
    public static final String REGEX_CUENTA_CLABE = "^([0-9]{18})$|^((0){1})$";
    public static final String REGEX_CUENTA_NUMER_TARJETA = "^([0-9]{12})$|^((0){1})$";
    public static final String REGEX_CLABE_BANCARIA = "^([0-9]{18})$|^((0){1})$";
    public static final String REGEX_E_MAIL = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";

    public static FacesMessage getFacesMessage(FacesMessage.Severity severity, String mensaje) {
        return new FacesMessage(severity, mensaje, null);
    }

    public static FacesMessage convertFiletoByte(UploadedFile archivo) {
        String extValidate;
        if (archivo != null) {
            String ext = archivo.getFileName();
            if (ext != null) {
                extValidate = ext.substring(ext.indexOf(".") + 1);
            } else {
                System.out.println("ExVAlidate: " + ext);
                return new FacesMessage(FacesMessage.SEVERITY_ERROR, Mensajes.ERROR_FORMATO_ARCHIVO, null);
            }
            if (extValidate.equals("pdf")) {
                try {
                    IOUtils.toByteArray(archivo.getInputstream());
                    return null;
                } catch (Exception e) {
                    return new FacesMessage(FacesMessage.SEVERITY_ERROR, Mensajes.ERROR_FORMATO_ARCHIVO, null);
                }
            } else {
                return new FacesMessage(FacesMessage.SEVERITY_ERROR, Mensajes.ERROR_FORMATO_ARCHIVO, null);
            }
        } else {
            return new FacesMessage(FacesMessage.SEVERITY_ERROR, Mensajes.CAMPO_VACIO_ARCHIVO, null);
        }
    }

    public static List<String> convertFileToStr(UploadedFile archivo) {
        BufferedReader bf = null;
        List<String> lista = new ArrayList<>();
        try {
            InputStream is = archivo.getInputstream();
            bf = new BufferedReader(new InputStreamReader(is));
            String linea;
            while ((linea = bf.readLine()) != null) {
                lista.add(linea);
            }
        } catch (Exception e) {
        } finally {
            if (bf != null) {
                try {
                    bf.close();
                } catch (Exception e) {
                }
            }
        }

        return lista;
    }

    public static void descargarArchivosPdfs(List<ReciboNomina> recibos, com.lam.cenicco.ws.Compania compania) {
        for (ReciboNomina r : recibos) {
            byte[] pdf = r.getArchivoPdf();
            byte[] xml = r.getArchivoXml();
            byte[] timbre = r.getTimbre();
            try {
                String apellidoMaterno = r.getIdRellab().getIdempleado().getApellidomaterno() == null
                        ? "" : r.getIdRellab().getIdempleado().getApellidomaterno();
//                
                String nombreArchivo = r.getIdRellab().getNumeroempleado() + "-"
                        + r.getIdRellab().getIdempleado().getNombre() + " " + r.getIdRellab().getIdempleado().getApellidopaterno() + apellidoMaterno
                        + "_" + r.getIdPeriodo().getIdtipoproceso().getTipoproceso()
                        + "_" + r.getIdPeriodo().getPeriodo() + r.getIdPeriodo().getAnio();
//                
                if (compania.getNombreCorto().equals("MINNT")) {
                    TipoProceso tp = r.getIdPeriodo().getIdtipoproceso();
                    nombreArchivo = r.getIdRellab().getIdempleado().getRfc();
                    if (tp.getTipoproceso().equals("NLC")) {
                        nombreArchivo = "LIQ_" + nombreArchivo;
                    } else if (tp.getTipoproceso().equals("NF")) {
                        nombreArchivo = "FIN_" + nombreArchivo;
                    }
                }
                String nombreArchivoPdf = nombreArchivo + ".pdf";
                OutputStream outPdf = new FileOutputStream(compania.getPathCarpetaProcesados() + nombreArchivoPdf);
                outPdf.write(pdf);
                outPdf.close();
//                
                String nombreArchivoXml = nombreArchivo + ".xml";
                OutputStream outXml = new FileOutputStream(compania.getPathCarpetaProcesados() + nombreArchivoXml);
                outXml.write(xml);
                outXml.close();
//                
                String nombreArchivoImagen = nombreArchivo + ".png";
                OutputStream outImagen = new FileOutputStream(compania.getPathCarpetaProcesados() + nombreArchivoImagen);
                outImagen.write(timbre);
                outImagen.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void descargarSoloArchivosPdfsPorEmpleado(List<ReciboNomina> recibos, com.lam.cenicco.ws.Compania compania) {

        for (ReciboNomina r : recibos) {

            byte[] pdf = r.getArchivoPdf();
            try {
                String apellidoMaterno = r.getIdRellab().getIdempleado().getApellidomaterno() == null
                        ? "" : r.getIdRellab().getIdempleado().getApellidomaterno();
//                
                String nombreArchivo = r.getIdRellab().getNumeroempleado() + "-"
                        + r.getIdRellab().getIdempleado().getNombre() + " " + r.getIdRellab().getIdempleado().getApellidopaterno() + apellidoMaterno
                        + "_" + r.getIdPeriodo().getIdtipoproceso().getTipoproceso()
                        + "_" + r.getIdPeriodo().getPeriodo() + r.getIdPeriodo().getAnio();

                String pathCarpetaPorEmpleado = r.getIdRellab().getNumeroempleado() + "_"
                        + r.getIdRellab().getIdempleado().getNombre()
                        + r.getIdRellab().getIdempleado().getApellidopaterno()
                        + apellidoMaterno;

                File carpeta = new File(compania.getPathCarpetaProcesados() + pathCarpetaPorEmpleado);
                boolean crearcarpeta = carpeta.mkdir();
                if (crearcarpeta) {
                    System.out.println("Carpeta creada con exito '" + pathCarpetaPorEmpleado + "'");
                }
//                
                String nombreArchivoPdf = nombreArchivo + ".pdf";
                OutputStream outPdf = new FileOutputStream(compania.getPathCarpetaProcesados() + "/" + pathCarpetaPorEmpleado + "/" + nombreArchivoPdf);
                outPdf.write(pdf);
                outPdf.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void descargarSoloArchivosPdfsPorCCO(List<ReciboNomina> recibos, com.lam.cenicco.ws.Compania compania) {
        for (ReciboNomina r : recibos) {

            byte[] pdf = r.getArchivoPdf();
            byte[] xml = r.getArchivoXml();
            byte[] timbre = r.getTimbre();
            try {
                CentroCostos cco = r.getIdRellab().getIdrelacionlaboralposicion().getIdcentrocosto();
                String apellidoMaterno = r.getIdRellab().getIdempleado().getApellidomaterno() == null
                        ? "" : r.getIdRellab().getIdempleado().getApellidomaterno();
//                
                String nombreArchivo = r.getIdRellab().getNumeroempleado() + "-"
                        + r.getIdRellab().getIdempleado().getNombre() + " " + r.getIdRellab().getIdempleado().getApellidopaterno() + apellidoMaterno
                        + "_" + r.getIdPeriodo().getIdtipoproceso().getTipoproceso()
                        + "_" + r.getIdPeriodo().getPeriodo() + r.getIdPeriodo().getAnio();
//
                String pathCarpetaPorCCO = cco.getCentroCosto() + "_" + cco.getNombre().replaceAll(" ", "_");
//
                File carpeta = new File(compania.getPathCarpetaProcesados() + "/" + pathCarpetaPorCCO);
                boolean crearcarpeta = carpeta.mkdir();
                if (crearcarpeta) {
                    System.out.println("Carpeta creada con exito '" + pathCarpetaPorCCO + "'");
                } else {
                    System.out.println("Falló Crear Carpeta: '" + pathCarpetaPorCCO + "'");
                }
//                
                String nombreArchivoPdf = nombreArchivo + ".pdf";
                OutputStream outPdf = new FileOutputStream(compania.getPathCarpetaProcesados() + pathCarpetaPorCCO + "/" + nombreArchivoPdf);
                outPdf.write(pdf);
                outPdf.close();

                String nombreArchivoXml = nombreArchivo + ".xml";
                OutputStream outXml = new FileOutputStream(compania.getPathCarpetaProcesados() + pathCarpetaPorCCO + "/" + nombreArchivoXml);
                outXml.write(xml);
                outXml.close();

                String nombreArchivoPng = nombreArchivo + ".png";
                OutputStream outPng = new FileOutputStream(compania.getPathCarpetaProcesados() + pathCarpetaPorCCO + "/" + nombreArchivoPng);
                outPng.write(timbre);
                outPng.close();
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }
    }

    public static void descargarArchivosXml(List<ReciboNomina> recibos, com.lam.cenicco.ws.Compania compania) {
        for (ReciboNomina r : recibos) {

            byte[] xml = r.getArchivoXml();
            try {
                String apellidoMaterno = r.getIdRellab().getIdempleado().getApellidomaterno() == null
                        ? "" : r.getIdRellab().getIdempleado().getApellidomaterno();
//                
                String nombreArchivo = r.getIdRellab().getNumeroempleado() + "-"
                        + r.getIdRellab().getIdempleado().getNombre() + " " + r.getIdRellab().getIdempleado().getApellidopaterno() + apellidoMaterno
                        + "_" + r.getIdPeriodo().getIdtipoproceso().getTipoproceso()
                        + "_" + r.getIdPeriodo().getPeriodo() + r.getIdPeriodo().getAnio();

                String pathCarpetaPorEmpleado = r.getIdRellab().getNumeroempleado() + "_"
                        + r.getIdRellab().getIdempleado().getNombre()
                        + r.getIdRellab().getIdempleado().getApellidopaterno()
                        + apellidoMaterno;

                File carpeta = new File(compania.getPathCarpetaProcesados() + pathCarpetaPorEmpleado);
                boolean crearcarpeta = carpeta.mkdir();
                if (crearcarpeta) {
                    System.out.println("Carpeta creada con exito '" + pathCarpetaPorEmpleado + "'");
                }
//                
                String nombreArchivoXml = nombreArchivo + ".xml";
                OutputStream outXml = new FileOutputStream(compania.getPathCarpetaProcesados() + "/" + pathCarpetaPorEmpleado + "/" + nombreArchivoXml);
                outXml.write(xml);
                outXml.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static Date calcularFechaVencimiento(Date fechaInicio, Integer duracion) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(fechaInicio);
        cal.add(Calendar.DAY_OF_YEAR, -1);
        cal.add(Calendar.DAY_OF_YEAR, duracion);
        return cal.getTime();
    }

    public static String getNombre(Empleado emp) {
        if (emp == null) {
            return "";
        }
        String nombre = emp.getNombre() + " " + emp.getApellidopaterno();
        if (emp.getApellidomaterno() != null && !emp.getApellidomaterno().equals("")) {
            nombre += " " + emp.getApellidomaterno();
        }
        return nombre;
    }

    public static String getApellidosNombre(Empleado emp) {
        if (emp == null) {
            return "";
        }

        String nombre = emp.getApellidopaterno();
        if (emp.getApellidomaterno() != null
                && !emp.getApellidomaterno().equals("")) {
            nombre += " " + emp.getApellidomaterno();
        }
        return nombre + " " + emp.getNombre();
    }

    public static String normalizarNombre(Empleado emp) {
        if (emp == null) {
            return "";
        }
        String nombre = emp.getApellidopaterno();
        if (emp.getApellidomaterno() != null && !emp.getApellidomaterno().equals("")) {
            nombre += " " + emp.getApellidomaterno();
        }
        return nombre + " " + emp.getNombre();
    }

    public static String convertDateSimpleDateFormatTime(Date fecha) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        return sdf.format(fecha);
    }

    public static String normalizarCadenasAcentos(String str, String esp) {
        String original = "ÁáÉéÍíÓóÚúÑñÜü";
        String reemplazo;
        if (esp == null) {
            reemplazo = "AaEeIiOoUuNnUu";
        } else {
            reemplazo = esp;
        }
//        
        char[] array = str.toCharArray();
        for (int i = 0; i < array.length; i++) {
            int pos = original.indexOf(array[i]);
            if (pos > -1) {
                array[i] = reemplazo.charAt(pos);
            }
        }
        return new String(array);
    }

    public static String normalizarCadenasAcentos(String str) {
        String original = "ÁáÉéÍíÓóÚúÑñÜü";
        String reemplazo = "AaEeIiOoUuNnUu";
//        
        if (str == null) {
            return null;
        }
//        
        char[] array = str.toCharArray();
        for (int i = 0; i < array.length; i++) {
            int pos = original.indexOf(array[i]);
            if (pos > -1) {
                array[i] = reemplazo.charAt(pos);
            }
        }
        return new String(array);
    }

    public static String normalizarCadenas(String str) {
        String original = "ÁáÉéÍíÓóÚúÜü";
        String reemplazo = "AaEeIiOoUuUu";
//        
        if (str == null) {
            return null;
        }
//        
        char[] array = str.toCharArray();
        for (int i = 0; i < array.length; i++) {
            int pos = original.indexOf(array[i]);
            if (pos > -1) {
                array[i] = reemplazo.charAt(pos);
            }
        }
        return new String(array);
    }

    public static String getEstadoCivil(String edoCivil) {
        switch (edoCivil) {
            case "01":
                return "Soltero(a)";
            case "02":
                return "Casado(a)";
            case "03":
                return "Divorciado(a)";
            case "04":
                return "Unión Libre";
            case "05":
                return "Viudo(a)";
            case "1":
                return "Soltero(a)";
            case "2":
                return "Casado(a)";
            case "3":
                return "Divorciado(a)";
            case "4":
                return "Unión Libre";
            case "5":
                return "Viudo(a)";
        }
        return "";
    }

    public static String getFormaPago(String formaPago) {
        switch (formaPago) {
            case "1":
                return "Transferencia";
            case "2":
                return "Cheque";
            case "3":
                return "Depósito";
            case "4":
                return "Efectivo";
            case "5":
                return "Otro";
        }
        return "";
    }

    public static String getTipoDescuento(String tipoDescuento) {
        switch (tipoDescuento) {
            case "1":
                return "Porcentaje SDI";
            case "2":
                return "Veces SMGDF";
            case "3":
                return "Cuota Fija";
        }
        return null;
    }

    public static String getEstatusCreditos(Integer estatus) {
        switch (estatus) {
            case 1:
                return "Activo";
            case 2:
                return "Registrado";
            case 4:
                return "Congelado";
            case 5:
                return "Cancelado";
            case 6:
                return "Liquidado";
        }
        return "";
    }

    public static String getEstado(Integer idEstado) {
        switch (idEstado) {
            case 1:
                return "Aguascalientes";
            case 2:
                return "Baja California";
            case 3:
                return "Baja California Sur";
            case 4:
                return "Campeche";
            case 5:
                return "Coahuila de Zaragoza";
            case 6:
                return "Colima";
            case 7:
                return "Chiapas";
            case 8:
                return "Chihuahua";
            case 9:
                return "Distrito Federal";
            case 10:
                return "Durango";
            case 11:
                return "Guanajuato";
            case 12:
                return "Guerrero";
            case 13:
                return "Jalisco";
            case 14:
                return "México";
            case 15:
                return "Michoacán de Ocampo";
            case 16:
                return "Morelos";
            case 17:
                return "Nayarit";
            case 18:
                return "Nuevo León";
            case 19:
                return "Oaxaca";
            case 20:
                return "Puebla";
            case 21:
                return "Querétaro";
            case 22:
                return "Quintana Roo";
            case 23:
                return "San Luis Potosí";
            case 24:
                return "Sinaloa";
            case 25:
                return "Sonora";
            case 26:
                return "Tabasco";
            case 27:
                return "Tamaulipas";
            case 28:
                return "Tlaxcala";
            case 29:
                return "Veracruz de Ignacio de la LLave";
            case 30:
                return "Yucatán";
            case 31:
                return "Zacatecas";
            case 32:
                return "Hidalgo";
            case 33:
                return "Extranjero";
        }
        return "";
    }

    public static Date calcularDiasVacaciones(Date fecha, Integer periodicidad, Integer dias) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(fecha);
        for (int i = 1; i < dias; i++) {
            cal.add(Calendar.DAY_OF_MONTH, 1);
            switch (periodicidad) {
                case 15:
                case 30:
                    if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                        dias++;
                    }
                    break;
                case 7:
                    if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                        dias++;
                    }
                    break;
            }
        }
        return cal.getTime();
    }

    public static List<Date> calcularDiasHabiles(Date fechaInicio, Date fechaFin, Integer periodicidad) {
        Calendar calInicio = Calendar.getInstance();
        calInicio.setTime(fechaInicio);
//        
        Calendar calFin = Calendar.getInstance();
        calFin.setTime(fechaFin);
//        
        List<Date> fechas = new ArrayList<>();
        while (calInicio.before(calFin) || calInicio.equals(calFin)) {
            switch (periodicidad) {
                case 15:
                case 30:
                    if (calInicio.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY && calInicio.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) {
                        fechas.add(calInicio.getTime());
                    }
                    break;
                case 7:
                    if (calInicio.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                        fechas.add(calInicio.getTime());
                    }
                    break;
            }
            calInicio.add(Calendar.DATE, 1);
        }
        return fechas;
    }

    public static Map<String, String> getMapaDiasHabiles(Date fechaInicio, Date fechaFin, Integer periodicidad) {
        Map<String, String> mapasolicitudes = new HashMap<>();

        Calendar calInicio = Calendar.getInstance();
        calInicio.setTime(fechaInicio);
//        
        Calendar calFin = Calendar.getInstance();
        calFin.setTime(fechaFin);
//        
        while (calInicio.before(calFin) || calInicio.equals(calFin)) {
            String llave = CeniccoUtil.convertDateTimeToString(calInicio.getTime());
            switch (periodicidad) {
                case 15:
                case 30:
                    if (calInicio.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY && calInicio.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) {
                        mapasolicitudes.put(llave, llave);
                    }
                    break;
                case 7:
                    if (calInicio.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                        mapasolicitudes.put(llave, llave);
                    }
                    break;
            }
            calInicio.add(Calendar.DATE, 1);
        }
        return mapasolicitudes;
    }

    public static double calcularProporcionDiasXFechaAntiguedad(Date fechaInicio, Date fechaFin, TabuladorSistemaAntiguedad tabulador) {
//        
        System.out.println("CalculoProporcion... " + fechaInicio + " | " + fechaFin + " | " + tabulador.getDiasVacaciones());
//        
        double proporcion = tabulador.getDiasVacaciones().doubleValue() / 365;
//        
        Calendar calFin = Calendar.getInstance();
//        
        Calendar calInicio = Calendar.getInstance();
        calInicio.setTime(fechaInicio);
        calInicio.set(Calendar.YEAR, calFin.get(Calendar.YEAR)); //        
        long diferencia = calFin.getTimeInMillis() - calInicio.getTimeInMillis();
//        
        long dias = (diferencia / Parametros.MILLSECS_PER_DAY) + 1;

        if (dias <= 0) {
            dias = 364 + (dias);
        }
        System.out.println("ProporcionDias... " + dias + " | " + proporcion);
        return dias * proporcion;
    }

    public static String normalizarCadena(String cadena, String especial) {
        if (cadena == null) {
            return null;
        }
        String aux = normalizarCadenasAcentos(cadena, especial);
//        
        return aux.trim().toUpperCase();
    }
//    

    public static String normalizarCadena(String cadena) {
        if (cadena == null) {
            return null;
        }
        String aux = normalizarCadenasAcentos(cadena);
//        
        return aux.trim().toUpperCase();
    }

    public static String getCadenaRelacionLaboralExpediente(RelacionLaboral rellab) {
        Empleado emp = rellab.getIdempleado();

        return "<html><body><br><b>Número Empleado:</b> " + normalizarCadena(rellab.getNumeroempleado()) + "<br/>"
                + "<br><b>Número Empleado:</b> " + getNombre(emp) + "<br/>"
                + "<br><b>Nombre:</b> " + normalizarCadena(emp.getNombre()) + "<br/>"
                + "<br><b>A. Paterno:</b> " + normalizarCadena(emp.getApellidopaterno()) + "<br/>"
                + "<br><b>A. Materno:</b> " + normalizarCadena(emp.getApellidomaterno()) + "<br/>"
                + "<br><b>E. Nacimiento:</b> " + getEstado(emp.getIdestadonacimiento()) + "<br/>"
                + "<br><b>L. Nacimiento:</b> " + normalizarCadena(emp.getLugarnacimiento()) + "<br/>"
                + "<br><b>F. Nacimiento:</b> " + CeniccoUtil.getSimpleDateFormatFromXMLGregorian(emp.getFechanacimiento()) + "<br/>"
                + "<br><b>Sexo:</b> " + (emp.getSexo().equals("1") ? "Masculino" : "Femenino") + "<br/>"
                + "<br><b>E. Civil:</b> " + getEstadoCivil(emp.getEstadocivil()) + "<br/>"
                + "<br><b>N.S.S.:</b> " + normalizarCadena(emp.getAfiliacion()) + "<br/>"
                + "<br><b>R.F.C.:</b> " + normalizarCadena(emp.getRfc()) + "<br/>"
                + "<br><b>C.U.R.P.:</b> " + normalizarCadena(emp.getCurp()) + "<br/></body></html>";
    }

    public static String getCadenaRelacionLaboralDomicilio(RelacionLaboral rellab, Domicilio domicilio) {
        return "<html><body><br><b>Número Empleado:</b> " + normalizarCadena(rellab.getNumeroempleado()) + "</br>"
                + "<br><b>Nombre:</b> " + getNombre(rellab.getIdempleado()) + "</br>"
                + "<br><b>C.P.:</b> " + normalizarCadena(domicilio.getCp()) + "</br>"
                + "<br><b>Municipio:</b> " + domicilio.getMunicipio() + "</br>"
                + "<br><b>Colonia:</b> " + domicilio.getColonia() + "</br>"
                + "<br><b>Calle:</b> " + normalizarCadena(domicilio.getCalle()) + "</br>"
                + "<br><b>No. Exterior:</b> " + normalizarCadena(domicilio.getNumeroexterior()) + "</br>"
                + "<br><b>No. Interior:</b>" + normalizarCadena(domicilio.getNumerointerior()) + "</br></body></html>";
    }

    public static String getCadenasRelacionLaboralPosiciones(RelacionLaboral rellab, RelacionLaboralPosicion pos) {
        return "<html><body><br><b>Número Empleado:</b> " + normalizarCadena(rellab.getNumeroempleado()) + "</br>"
                + "<br><b>Nombre:</b> " + getNombre(rellab.getIdempleado()) + "</br>"
                + "<br><b>Puesto:</b> " + pos.getIdpuesto().getNombre() + "</br>"
                + "<br><b>Departamento:</b> " + pos.getIddepartamento().getDepartamento() + "-" + pos.getIddepartamento().getNombre() + "</br>"
                + "<br><b>Turno:</b> " + pos.getIdturno().getNombre() + "</br>"
                + "<br><b>F. Pago:</b> " + getFormaPago(pos.getFormapago()) + "</br>"
                + "<br><b>Sindicalizado:</b> " + (pos.getSindicalizado().equals("1") ? "Sí" : "No") + "</br>"
                + "<br><b>Zona Económica:</b> " + pos.getIdZonaEconomica().getZonaEconomica() + "</br>"
                + "<br><b>Centro Costos:</b> " + pos.getIdcentrocosto().getCentroCosto() + "-" + pos.getIdcentrocosto().getNombre() + "</br></body></html>";
    }

    public static String getCadenasRelacionLaboralRelacionLaboral(RelacionLaboral rellab) {
        return "<html><body><br><b>Número Empleado:</b> " + normalizarCadena(rellab.getNumeroempleado()) + "</br>"
                + "<br><b>Nombre:</b> " + getNombre(rellab.getIdempleado()) + "</br>"
                + "<br><b>Compañía:</b> " + rellab.getIdcompania().getClave() + "-" + rellab.getIdcompania().getNombre() + "</br>"
                + "<br><b>R. Patronal:</b> " + rellab.getIdregistropatronal().getComentarios() + "-" + rellab.getIdregistropatronal().getRegistropatronal() + "</br>"
                + "<br><b>Tipo Relación Laboral:</b> " + rellab.getIdtiporellab().getNombre() + "</br>"
                + "<br><b>Grupo Pago:</b> " + rellab.getIdgrupopago().getGrupopago() + "-" + rellab.getIdgrupopago().getNombre() + "</br>"
                + "<br><b>T. Salario:</b> " + rellab.getIdTipoSalarioIdse().getNombre() + "</br>"
                + "<br><b>Estatus:</b> " + (rellab.getEstatus().equals("1") ? "Activo" : "Inactivo") + "</br>"
                + "<br><b>F. Ingreso:</b> " + CeniccoUtil.getSimpleDateFormatFromXMLGregorian(rellab.getFechaingreso()) + "</br>"
                + "<br><b>F. Vencimiento:</b> " + CeniccoUtil.getSimpleDateFormatFromXMLGregorian(rellab.getFechaeventocontrato()) + "</br>"
                + "<br><b>Sistema Antiguedad:</b> " + rellab.getIdsistemaantiguedad().getNombre() + "</br>"
                + "<br><b>F. Antiguedad:</b> " + CeniccoUtil.getSimpleDateFormatFromXMLGregorian(rellab.getFechaantiguedad1()) + "</br>"
                + "<br><b>Causa Baja:</b> " + rellab.getCausabaja() + "</br>"
                + "<br><b>F. Baja:</b> " + CeniccoUtil.getSimpleDateFormatFromXMLGregorian(rellab.getFechabaja()) + "</br></body></html>";
    }

    public static String getCadenasRelacionLaboralPosicionSueldo(RelacionLaboral rellab, Date fechaInicioSD, Date fechaInicioSDI) {
        return "<html><body><br><b>Número Empleado:</b> " + normalizarCadena(rellab.getNumeroempleado()) + "</br>"
                + "<br><b>Nombre:</b> " + getNombre(rellab.getIdempleado()) + "</br>"
                + "<br><b>F. Inicio SD:</b> " + CeniccoUtil.convertDateToString(fechaInicioSD) + "</br>"
                + "<br><b>SD:</b> " + rellab.getSalarioDiario() + "</br>"
                + "<br><b>F. Inicio SDI:</b> " + CeniccoUtil.convertDateToString(fechaInicioSDI) + "</br>"
                + "<br><b>SDI:</b>" + rellab.getSalarioDiarioIntegrado() + "</br></body></html>";
    }

    public static String getCadenasRelacionLaboralCuentasBancarias(RelacionLaboral rellab, CuentaBancaria cuenta, Banco banco, Banco bancoempleado) {
        return "<html><body><br><b>Número Empleado:</b> " + normalizarCadena(rellab.getNumeroempleado()) + "</br>"
                + "<br><b>Nombre:</b> " + getNombre(rellab.getIdempleado()) + "</br>"
                + "<br><b>Banco CIA:</b> " + Parametros.MAPA_BANCOS.get(banco.getIdbanco().toString()).getNombre() + "</br>"
                + "<br><b>Banco empleado:</b> " + Parametros.MAPA_BANCOS.get(bancoempleado.getIdbanco().toString()).getNombre() + "</br>"
                + "<br><b>CLABE:</b> " + normalizarCadena(cuenta.getClabe()) + "</br>"
                + "<br><b>Cuenta:</b> " + normalizarCadena(cuenta.getCuenta()) + "</br>"
                + "<br><b>Procentaje:</b>" + cuenta.getPorcentaje() + "</br></body></html>";
    }

    public static String getCadenasRelacionLaboralCuentasBancariasEditar(RelacionLaboral rellab, CuentaBancaria cuenta) {
        return "<html><body><br><b>Se edito la siguiente Cuenta Bancaria:</b></br>"
                + "<br><b>Número Empleado:</b> " + normalizarCadena(rellab.getNumeroempleado()) + "</br>"
                + "<br><b>Nombre:</b> " + getNombre(rellab.getIdempleado()) + "</br>"
                + "<br><b>Banco:</b> " + Parametros.MAPA_BANCOS.get(cuenta.getIdbanco().getIdbanco().toString()).getNombre() + "</br>"
                + "<br><b>CLABE:</b> " + normalizarCadena(cuenta.getClabe()) + "</br>"
                + "<br><b>Cuenta:</b> " + normalizarCadena(cuenta.getCuenta()) + "</br>"
                + "<br><b>Procentaje:</b>" + cuenta.getPorcentaje() + "</br></body></html>";
    }

    public static String getCadenasRelacionLaboralInfonavit(RelacionLaboral rellab, Infonavit infonavit, Date fechaVigencia) {
        return "<html><body><br><b>Número Empleado:</b> " + normalizarCadena(rellab.getNumeroempleado()) + "</br>"
                + "<br><b>Nombre:</b> " + getNombre(rellab.getIdempleado()) + "</br>"
                + "<br><b>Número Crédito:</b> " + infonavit.getNumerocredito() + "</br>"
                + "<br><b>Tipo Descuento:</b> " + getTipoDescuento(infonavit.getTipodescuento()) + "</br>"
                + "<br><b>F. Vigencia:</b> " + CeniccoUtil.convertDateToString(fechaVigencia) + "</br>"
                + "<br><b>Descuento:</b> " + infonavit.getDescuento() + "</br>"
                + "<br><b>Estatus:</b> " + (infonavit.getEstatus().equals("1") ? "Activo" : "Pagado") + "</br></body></html>";
    }

    public static String getCadenasRelacionLaboralPension(RelacionLaboral rellab, Pension pension) {
        return "<html><body><br><b>Número Empleado:</b> " + normalizarCadena(rellab.getNumeroempleado()) + "</br>"
                + "<br><b>Nombre:</b> " + getNombre(rellab.getIdempleado()) + "</br>"
                + "<br><b>T. Cálculo:</b> " + (pension.getTipocalculo() == 1 ? "Porcentaje" : "Fijo") + "</br>"
                + "<br><b>Cant/Porcen:</b> " + pension.getCantidad() + "</br>"
                + "<br><b>Nombre:</b> " + normalizarCadena(pension.getNombre()) + "</br>"
                + "<br><b>A. Paterno:</b> " + normalizarCadena(pension.getApellidopaterno()) + "</br>"
                + "<br><b>A. Materno:</b> " + normalizarCadena(pension.getApellidomaterno()) + "</br>"
                + "<br><b>Oficio:</b> " + normalizarCadena(pension.getOficio()) + "</br>"
                + "<br><b>Juez:</b> " + normalizarCadena(pension.getJuez()) + "</br>"
                + "<br><b>F. Pago:</b> " + getFormaPago(pension.getFormadepago()) + "</br>"
                + "<br><b>Banco:</b> " + Parametros.MAPA_BANCOS.get(pension.getIdbanco().getIdbanco().toString()).getNombre() + "</br>"
                + "<br><b>Cuenta:</b> " + normalizarCadena(pension.getCuenta()) + "</br>"
                + "<br><b>CLABE:</b> " + normalizarCadena(pension.getClabe()) + "</br></body></html>";
    }

    public static String getCadenasRelacionLaboralPensionEliminar(RelacionLaboral rellab, Pension pension) {
        return "<html><body><br><b>Se eliminó la siguiente Pensión:</b></br>"
                + "<br><b>Nombre:</b> " + getNombre(rellab.getIdempleado()) + "</br>"
                + "<br><b>Número Empleado:</b> " + normalizarCadena(rellab.getNumeroempleado()) + "</br>"
                + "<br><b>T. Cálculo:</b> " + (pension.getTipocalculo() == 1 ? "Porcentaje" : "Fijo") + "</br>"
                + "<br><b>Cant/Porcen:</b> " + pension.getCantidad() + "</br>"
                + "<br><b>Nombre:</b> " + normalizarCadena(pension.getNombre()) + "</br>"
                + "<br><b>A. Paterno:</b> " + normalizarCadena(pension.getApellidopaterno()) + "</br>"
                + "<br><b>A. Materno:</b> " + normalizarCadena(pension.getApellidomaterno()) + "</br>"
                + "<br><b>Oficio:</b> " + normalizarCadena(pension.getOficio()) + "</br>"
                + "<br><b>Juez:</b> " + normalizarCadena(pension.getJuez()) + "</br>"
                + "<br><b>F. Pago:</b> " + getFormaPago(pension.getFormadepago()) + "</br>"
                + "<br><b>Banco:</b> " + Parametros.MAPA_BANCOS.get(pension.getIdbanco().getIdbanco().toString()).getNombre() + "</br>"
                + "<br><b>Cuenta:</b> " + normalizarCadena(pension.getCuenta()) + "</br>"
                + "<br><b>CLABE:</b> " + normalizarCadena(pension.getClabe()) + "</br></body></html>";
    }

    public static String getCadenasRelacionLaboralVariables(RelacionLaboral rellab, List<EstructuraDatos> datos) {
        String cadena = "<html><body><br><b>Número Empleado:</b> " + rellab.getNumeroempleado() + "</br>"
                + "<br><b>Nombre:</b> " + getNombre(rellab.getIdempleado()) + "</br>"
                + "<br><b>Datos:</b></br>";
        for (EstructuraDatos d : datos) {
            cadena += "<br>" + d.getIdcolumna().getNombre() + ": " + d.getValor() + "</br>";
        }
        return cadena + "</body></html>";
    }

    public static String getCadenasRelacionesLaborales(List<String> lineas) {
        String cadena = "<html><body>";
        for (String s : lineas) {
            cadena = cadena + "<br>" + s + "</br>";
        }
        return cadena + "</body></html>";
    }

    public static String getCadenasRelacionLaboralEmpleado(RelacionLaboral rellab, Date fechaNacimiento) {
        Empleado emp = rellab.getIdempleado();
        return "<html><body><br><b>Nombre:</b> " + normalizarCadena(emp.getNombre()) + "</br>"
                + "<br><b>Nombre:</b> " + getNombre(rellab.getIdempleado()) + "</br>"
                + "<br><b>A. Paterno:</b> " + normalizarCadena(emp.getApellidopaterno()) + "</br>"
                + "<br><b>A. Materno:</b> " + normalizarCadena(emp.getApellidomaterno()) + "</br>"
                + "<br><b>Fecha Nacimiento:</b> " + CeniccoUtil.convertDateToString(fechaNacimiento) + "</br>"
                + "<br><b>Estado Nacimiento:</b> " + getEstado(emp.getIdestadonacimiento()) + "</br>"
                + "<br><b>Ciudad Nacimiento:</b> " + normalizarCadena(emp.getLugarnacimiento()) + "</br>"
                + "<br><b>Sexo:</b> " + (emp.getSexo().equals("1") ? "Masculino" : "Femenino") + "</br>"
                + "<br><b>R.F.C.:</b> " + normalizarCadena(emp.getRfc()) + "</br>"
                + "<br><b>C.U.R.P.:</b> " + normalizarCadena(emp.getCurp()) + "</br>"
                + "<br><b>N.S.S.:</b> " + normalizarCadena(emp.getAfiliacion()) + "</br>"
                + "<br><b>Estado Civil:</b> " + getEstadoCivil(emp.getEstadocivil()) + "</br></body></html>";
    }

    public static String getCadenasCreditosAltas(List<Credito> creditos) {
        String cadena = "<html><body><br><b>Alta de Créditos:</b></br>";
        for (Credito c : creditos) {
            Empleado emp = c.getRelacionLaboral().getIdempleado();
            cadena += "<br>" + c.getRelacionLaboral().getNumeroempleado() + "," + emp.getNombre() + "," + emp.getApellidopaterno() + "," + emp.getApellidomaterno() + ","
                    + c.getNumeroPeriodos() + "," + c.getConcepto().getConcepto() + "- " + c.getConcepto().getNombre() + "," + c.getImporte() + "," + c.getAportacion() + ","
                    + c.getPeriodoInicio().getPeriodo() + "-" + c.getPeriodoInicio().getFechaInicioStr() + "," + c.getPeriodoFin().getPeriodo() + "-" + c.getPeriodoFin().getFechaFinStr() + "</br>";
        }
        return cadena + "</body></html>";
    }

    public static String getCadenasCreditosModificaciones(Credito credito) {
        String cadena = "<html><body><br><b>Modificación de Créditos:</b></br>";
        Empleado emp = credito.getRelacionLaboral().getIdempleado();
        cadena += "<br>" + credito.getRelacionLaboral().getNumeroempleado() + "," + emp.getNombre() + "," + emp.getApellidopaterno() + "," + emp.getApellidomaterno() + ","
                + credito.getNumeroPeriodos() + "," + credito.getConcepto().getConcepto() + "- " + credito.getConcepto().getNombre() + "," + credito.getImporte() + "," + credito.getAportacion() + ","
                + credito.getPeriodoInicio().getPeriodo() + "-" + credito.getPeriodoInicio().getFechaInicioStr() + "," + credito.getPeriodoFin().getPeriodo() + "-" + credito.getPeriodoFin().getFechaFinStr() + ","
                + getEstatusCreditos(credito.getEstatus()) + "</br>";
        return cadena + "</body></html>";
    }

    public static String getCadenaConEspacios(int espacios, String caracter) {
        String cadena = "";
        for (int i = 0; i < espacios; i++) {
            cadena = cadena + caracter;
        }
        return cadena;
    }

    public static Date getFechaInicioMes(Integer mes) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, (mes - 1));
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static Date getFechaFinMes(Integer mes) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, (mes - 1));
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
//    

    public static int calcularAnios(Date fechaInicio, Date fechaFin) {
//      
        Calendar calInicio = CeniccoUtil.getCalendar(fechaInicio);
        Calendar calFin = CeniccoUtil.getCalendar(fechaFin);
//        
        int anio = calFin.get(Calendar.YEAR) - calInicio.get(Calendar.YEAR);
        int mes = calFin.get(Calendar.MONTH) - calInicio.get(Calendar.MONTH);
        int dia = calFin.get(Calendar.DATE) - calInicio.get(Calendar.DATE);
//        
        if (mes < 0 || (mes == 0 && dia < 0)) {
            anio--;
        }

        return anio;
    }

    public static Date calcularFechaFin(Date fechaFin, Integer duracion) {
//      
        Calendar calFechaFin = CeniccoUtil.getCalendar(fechaFin);
        calFechaFin.add(Calendar.DATE, duracion - 1);
        Date fechaFinG = calFechaFin.getTime();
//        

        return fechaFinG;
    }

    public static List<FacturaTO> getXmlsPac(List<CifrasNomina> nominas, GrupoPago gp, Integer idtipoproceso,
            Map<String, ConceptoAcumulado> saldofondoahorro, Map<String, List<VistaSaldoCreditos>> saldocreditos,
            Map<String, ConceptoAcumulado> saldocajaahorro, Date fechahoy, boolean retimbrado) throws FileNotFoundException, GeneralSecurityException, IOException {

        List<Concepto> subsidios = new ArrayList<>();
        List<Concepto> incapacidades = new ArrayList<>();
//        
        System.out.println("saldos size... " + saldocreditos.size() + " | " + saldofondoahorro.size() + " | " + saldocajaahorro.size());
//        
        List<FacturaTO> timbres = new ArrayList<>();
//                
        BufferedWriter bw = null;
        DecimalFormat nformat = new DecimalFormat("0.00");
        SimpleDateFormat sdfpago = new SimpleDateFormat("yyyy-MM-dd");

//      
        TipoProceso tipoproceso;
        String tiponomina = "O";
        if (idtipoproceso == null) {
            tipoproceso = gp.getIdTipoproceso();
        } else {
            tipoproceso = ControladorWS.getInstance().findTipoProcesoById(idtipoproceso);
            tiponomina = "E";

        }
//        
        Map<String, RelacionLaboral> maparelaciones = ControladorWS.getInstance().getMapaRelacionesLaborales();
        Periodo periodo = ControladorWS.getInstance().findPeriodoById(nominas.get(0).getIdperiodo());
        String fechaactual = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(fechahoy);
//        
//      
        String pathplantilla = "";
        switch (tipoproceso.getTipoplantilla()) {
            case 1:
                pathplantilla = ControladorWS.getInstance().getPathPlantillaSat();
                System.out.println("Se timbra con plantilla tipo 1: Normal");
                break;
            case 2:
                pathplantilla = ControladorWS.getInstance().getPathPlantillaSatComplementarios();
                System.out.println("Se timbra con plantilla tipo 2: Complementario");
                break;
            case 3:
                pathplantilla = ControladorWS.getInstance().getPathPlantillaSatAsimilados();
                System.out.println("Se timbra con plantilla tipo 3: Asimilados");
                break;
        }

        // Timbrado v4
        File key = new File(gp.getIdcompania().getRutaarchivokey());
        String passLlavePrivada = gp.getIdcompania().getPasswordsat();
        PrivateKey llavePrivada = getPrivateKey(key, passLlavePrivada);
        try {
            BufferedReader br = new BufferedReader(new FileReader(pathplantilla));
//            
            String linea;
            StringBuilder stb = new StringBuilder();
            while ((linea = br.readLine()) != null) {
                stb.append(linea);
            }
//            
            for (CifrasNomina n : nominas) {

                XMLGregorianCalendar fechaIngresoTimbrado = CeniccoUtil.convertStringToXmlGregorian(n.getFechaantiguedad());

//                
                List<Concepto> concpetosrecibos = new ArrayList<>();
                Map<String, Concepto> mapConceptosRecibos = new HashMap<>();

                Map<String, Concepto> mapagral = new HashMap<>();
                Map<String, Concepto> mapaconceptos = new HashMap<>();
                double otrospagos = 0.0;
                double subsidioempleado = 0.0;
//
                if (maparelaciones.get(n.getIdrellab().toString()) == null) {
                    continue;
                }

                RelacionLaboral rl = maparelaciones.get(n.getIdrellab().toString());
//                
                Integer tiempoextra = 0;
                for (Concepto per : n.getConceptosPercepciones()) {
                    per.setImportepercepcion(per.getValor());
                    per.setImportededuccion(0.0);
                    per.setSaldo(0.0);
//                    
                    Concepto c = per.clone();
//
                    if (c.getNaturaleza() == null) {
                        continue;
                    }
//                    
                    if (c.getNaturaleza().equals("01")) {
                        if (mapaconceptos.get(c.getIdconcepto().toString()) == null) {
                            c.setExento(0.0);
                            c.setGravado(c.getValor());
                            mapaconceptos.put(c.getIdconcepto().toString(), c);
                        } else {
                            double gravado = c.getValor() + mapaconceptos.get(c.getIdconcepto().toString()).getGravado();
                            c.setGravado(gravado);
                            mapaconceptos.put(c.getIdconcepto().toString(), c);
                        }
                    }
//                    
                    if (c.getNaturaleza().equals("02")) {
                        if (mapaconceptos.get(c.getIdconcepto().toString()) == null) {
                            c.setExento(c.getValor());
                            c.setGravado(0.0);
                            mapaconceptos.put(c.getIdconcepto().toString(), c);
                        } else {
                            double exento = c.getValor() + mapaconceptos.get(c.getIdconcepto().toString()).getExento();
                            c.setExento(exento);
                            mapaconceptos.put(c.getIdconcepto().toString(), c);
                        }
                    }
//                    
                    if (c.getNaturaleza().equals("03")) {
                        c.setGravado(0.0);
                        c.setExento(0.0);
                        mapagral.put(c.getIdconcepto().toString(), c);
                    }
//                    
                    if (c.getTiponodo() != null
                            && c.getAtributo().equals("019")) {
                        tiempoextra += c.getTiempo().intValue();
                    }
//                    
                    if (c.getNaturaleza().equals("04")
                            && c.getTiponodo() != null) {
                        if (c.getAtributo().equals("002")) {
                            subsidioempleado += c.getValor();
                        }
                        otrospagos += c.getValor();
                        mapaconceptos.put(c.getIdconcepto().toString(), c);
                    }

                    if (c.getRecibonomina() != null && c.getRecibonomina() == 1) {
                        concpetosrecibos.add(c);
                        mapConceptosRecibos.put(c.getConcepto(), c);
                    }
                }
//                
                for (Concepto d : n.getConceptosDeducciones()) {
//                    
                    d.setImportepercepcion(0.0);
                    d.setImportededuccion(d.getValor());
                    d.setSaldo(0.0);
//                    
                    Concepto p = d.clone();
//                    
                    if (p.getNaturaleza() == null) {
                        continue;
                    }
//                    
                    if (saldocreditos.get(n.getIdrellab().toString()) != null) {
                        List<VistaSaldoCreditos> saldos = saldocreditos.get(n.getIdrellab().toString());
//                        
                        Iterator<VistaSaldoCreditos> iter = saldos.iterator();
                        List<VistaSaldoCreditos> removesaldos = new ArrayList<>();
                        while (iter.hasNext()) {
                            VistaSaldoCreditos s = iter.next();
                            boolean aplica =
                                    CeniccoUtil.redondear(s.getAportacion()) == CeniccoUtil.redondear(d.getValor())
                                    && s.getNumeroconcepto().equals(d.getConcepto());
                            if (aplica) {
                                p.setSaldo(CeniccoUtil.redondear(s.getSaldo()));
                                System.out.println("AplicaSaldo.... " + n.getIdrellab().toString() + " | " + p.getConcepto() + " | " + p.getValor() + " | " + p.getSaldo());
                                removesaldos.add(s);
                            }
                        }
                        saldocreditos.get(n.getIdrellab().toString()).removeAll(removesaldos);
                    }
//                    

                    if (saldofondoahorro.get(n.getIdrellab().toString()) != null) {
                        System.out.println("ConceptoFondoAhorro: " + saldofondoahorro.get(n.getIdrellab().toString()).getNumeroconcepto());

                        boolean aplica = saldofondoahorro.get(n.getIdrellab().toString()).getNumeroconcepto().equals(p.getConcepto());
                        if (aplica) {
                            System.out.println("AplicaFONDOAHORRO... " + aplica + " | " + saldofondoahorro.get(n.getIdrellab().toString()).getSaldo());
                            p.setSaldo(saldofondoahorro.get(n.getIdrellab().toString()).getSaldo());
                        }
                    }
//                    
                    if (saldocajaahorro.get(n.getIdrellab().toString()) != null) {
                        System.out.println("ConceptoCajaAhorro: " + saldocajaahorro.get(n.getIdrellab().toString()).getNumeroconcepto());

                        boolean aplica = saldocajaahorro.get(n.getIdrellab().toString()).getNumeroconcepto().equals(p.getConcepto());
                        if (aplica) {
                            p.setSaldo(saldocajaahorro.get(n.getIdrellab().toString()).getSaldo());
                        }
                    }
//                    
                    if (p.getNaturaleza().equals("01") || p.getNaturaleza().equals("02")) {
                        if (mapaconceptos.get(p.getIdconcepto().toString()) == null) {
                            mapaconceptos.put(p.getIdconcepto().toString(), p);
                        } else {
                            double valor = p.getValor() + mapaconceptos.get(p.getIdconcepto().toString()).getValor();
                            p.setValor(valor);
                            mapaconceptos.put(p.getIdconcepto().toString(), p);
                        }
                    }
                    if (p.getNaturaleza().equals("03")) {
                        p.setGravado(0.0);
                        p.setExento(0.0);
                        mapagral.put(p.getIdconcepto().toString(), p);
                    }
//                    
                    if (p.getRecibonomina() != null && p.getRecibonomina() == 1) {
                        concpetosrecibos.add(p);
                        mapConceptosRecibos.put(p.getConcepto(), p);
                    }
//            
                }
//                
                double subsidiotabla = 0.0;
                for (Concepto c : n.getConceptosProvisiones()) {
                    if (StringUtils.isNullOrEmpty(c.getAtributo())
                            && c.getTiponodo() != null
                            && c.getTiponodo().equals("002")) {
                        subsidiotabla = c.getValor();
                    }
                    if (c.getNombre().equals("SUELDO VARIABLE")) {
                        concpetosrecibos.add(c);
                        mapConceptosRecibos.put(c.getConcepto(), c);
                    }
                }
                StringBuilder stbdpercepciones = new StringBuilder();
                StringBuilder stbdeducciones = new StringBuilder();
                StringBuilder stbdincapacidades = new StringBuilder();
                StringBuilder stbdotrospagos = new StringBuilder();
                StringBuilder nomEspecial = new StringBuilder();
                stbdotrospagos.append(EtiquetasSat.ABRIR_OTROSPAGOS.getConcepto());
                boolean aplicaotrospagos = false;
//                
                String cadenaindemnizacion = "";
//                
                Iterator<Concepto> iter = mapagral.values().iterator();
                while (iter.hasNext()) {
                    Concepto c = iter.next();
//                    
                    if (c.getConceptogravado() == null
                            || c.getConceptoexento() == null) {
                        continue;
                    }

                    double gravado = mapaconceptos.get(c.getConceptogravado().toString()) != null
                            ? mapaconceptos.get(c.getConceptogravado().toString()).getValor() : 0.0;

                    double exento = mapaconceptos.get(c.getConceptoexento().toString()) != null
                            ? mapaconceptos.get(c.getConceptoexento().toString()).getValor() : 0.0;
//                    
                    if (gravado == 0.0 && exento == 0.0) {
                        continue;
                    }
//                      
                    c.setGravado(gravado);
                    c.setExento(exento);
//                    
                    mapaconceptos.remove(c.getConceptogravado().toString());
                    mapaconceptos.remove(c.getConceptoexento().toString());
//                    
                    mapaconceptos.put(c.getIdconcepto().toString(), c);
//                    
                }
//                

                Double importepercepcionesgravado = 0.0;
                Double importepercepcionesexento = 0.0;
                Double valesdespensa = 0.0;
                Double valesdespensadeducciones = 0.0;
                Double totalindemnizacion = 0.0;
//                
//                
                iter = mapaconceptos.values().iterator();
                while (iter.hasNext()) {
                    Concepto c = iter.next();
//                    
                    if (c.getAtributo() != null
                            && (c.getAtributo().equals("029") || c.getAtributo().equals("030") || c.getAtributo().equals("031") || c.getAtributo().equals("032"))
                            && c.getSuma() == 0) {
////                        
                        valesdespensa += c.getValor();
//                        
                    }

                    if (c.getAtributo() != null
                            && c.getAtributo().equals("999")
                            && c.getSuma() == 0 && c.getNombre().equals("HERRAMIENTA DE TRABAJO")) {
////                        
                        valesdespensa += c.getValor();
//                        
                    }

                    if (c.getAtributo() != null
                            && c.getAtributo().equals("999")
                            && c.getSuma() == 0 && c.getNombre().equals("TELETRABAJO")) {
////                        
                        valesdespensa += c.getValor();
//                        
                    }

                    if (c.getAtributo() != null
                            && c.getAtributo().equals("005")
                            && c.getSuma() == 0 && c.getNombre().equals("FONDO DE AHORRO EMPRESA")) {
                        valesdespensa += c.getValor();
                    }

                    if (c.getAtributo() != null
                            && c.getAtributo().equals("004")
                            && c.getSuma() == 0 && c.getNombre().equals("DESPENSA EN ESPECIE")) {
                        valesdespensadeducciones += c.getValor();
                    }

//                    
                    if (c.getAtributo() == null || c.getAtributo().equals("")) {
                        continue;
                    }
//                    
                    if (c.getTipoconcepto().equals("01")) {
//                                
                        switch (c.getNaturaleza()) {
                            case "01":
                            case "02":
                            case "03":
//                              
                                if (c.getTipoconcepto().equals("01") && c.getAtributo().equals("014")) {
                                    if (c.getTiponodo().equals("01") || c.getTiponodo().equals("02") || c.getTiponodo().equals("03") || c.getTiponodo().equals("04")) {
                                        subsidios.add(c);
                                        continue;
                                    }

                                }
                                importepercepcionesgravado += c.getGravado();
                                importepercepcionesexento += c.getExento();
//                                
                                String aux =
                                        EtiquetasSat.PERCEPCION.getConcepto().replaceAll(EtiquetasSat.NODO.getConcepto(), c.getAtributo()).
                                        replaceAll(EtiquetasSat.NUMERO_CONCEPTO.getConcepto(), c.getConcepto()).
                                        replaceAll(EtiquetasSat.NOMBRE_CONCEPTO.getConcepto(), c.getNombre()).
                                        replaceAll(EtiquetasSat.IMPORTE_GRAVADO.getConcepto(), nformat.format(c.getGravado())).
                                        replaceAll(EtiquetasSat.IMPORTE_EXENTO.getConcepto(), nformat.format(c.getExento()));
                                if (c.getAtributo().equals("019")) {
                                    double importe = c.getGravado() + c.getExento();
                                    Integer tiempo = tiempoextra == 0 ? 1 : tiempoextra;
//                            
                                    String cadenatiempo =
                                            EtiquetasSat.HORAS_EXTRAS.getConcepto().replaceAll(EtiquetasSat.TIPO_HORAS.getConcepto(), c.getTiponodo()).
                                            replaceAll(EtiquetasSat.TIEMPO_HORAS.getConcepto(), tiempo.toString()).
                                            replaceAll(EtiquetasSat.IMPORTE_HORAS.getConcepto(), nformat.format(importe));
                                    aux = aux + cadenatiempo;
                                }

                                if (c.getAtributo().equals("025") || c.getAtributo().equals("022")) {
                                    int anios = calcularAnios(fechaIngresoTimbrado.toGregorianCalendar().getTime(),
                                            n.getFecharegistro().toGregorianCalendar().getTime());
                                    double sueldomensual = n.getSalariodiario() * 30;
                                    double noacumulable = (c.getGravado() - sueldomensual) < 0 ? 0.0
                                            : (c.getGravado() - sueldomensual);
                                    totalindemnizacion = c.getValor();
//                                    
                                    cadenaindemnizacion =
                                            "<nomina12:SeparacionIndemnizacion TotalPagado=\"" + nformat.format(c.getValor()) + "\" "
                                            + "NumAñosServicio=\"" + anios + "\" UltimoSueldoMensOrd=\"" + nformat.format(sueldomensual)
                                            + "\" IngresoAcumulable=\"" + nformat.format(sueldomensual) + "\" "
                                            + "IngresoNoAcumulable=\"" + nformat.format(noacumulable) + "\"/>";
                                }
                                stbdpercepciones.append(aux).append(EtiquetasSat.CIERRE_PERCEPCIONES.getConcepto());

                                break;
//                                
                            case "04":
                                aplicaotrospagos = true;
                                if (c.getTiponodo().equals("002")) {
                                    String cadenasubsidio;
                                    if (c.getAtributo().equals("002")) {
                                        cadenasubsidio = EtiquetasSat.OTROS_PAGOS.getConcepto().
                                                replaceAll(EtiquetasSat.NODO.getConcepto(), c.getTiponodo()).
                                                replaceAll(EtiquetasSat.NUMERO_CONCEPTO.getConcepto(), c.getConcepto()).
                                                replaceAll(EtiquetasSat.NOMBRE_CONCEPTO.getConcepto(), c.getNombre()).
                                                replaceAll(EtiquetasSat.IMPORTE_EXENTO.getConcepto(), nformat.format(c.getValor())).
                                                replaceAll(EtiquetasSat.SUBSIDIO_CAUSADO.getConcepto(), nformat.format(subsidiotabla));
                                        stbdotrospagos.append(cadenasubsidio);
                                    } else {
                                        cadenasubsidio = EtiquetasSat.OTROS_PAGOS.getConcepto().
                                                replaceAll(EtiquetasSat.NODO.getConcepto(), c.getAtributo()).
                                                replaceAll(EtiquetasSat.NUMERO_CONCEPTO.getConcepto(), c.getConcepto()).
                                                replaceAll(EtiquetasSat.NOMBRE_CONCEPTO.getConcepto(), c.getNombre()).
                                                replaceAll(EtiquetasSat.IMPORTE_EXENTO.getConcepto(), nformat.format(c.getValor())).
                                                replaceAll("<nomina12:SubsidioAlEmpleo SubsidioCausado=\"#SUBSIDIOCAUSADO\"/>", "");
                                        stbdotrospagos.append(cadenasubsidio);
                                    }

                                }
                                break;
                        }

                    }
                    if (c.getTipoconcepto().equals("02")) {
                        if (c.getIncapacidad() != null && c.getIncapacidad() == 1) {
                            if (stbdincapacidades.lastIndexOf("TipoIncapacidad=" + "\"" + c.getTiponodo() + "\"") < 0) {
                                String aux =
                                        EtiquetasSat.INCAPACIDAD.getConcepto().replaceAll(EtiquetasSat.DIAS_INCAPACIDAD.getConcepto(), "" + c.getTiempo().intValue()).
                                        replaceAll(EtiquetasSat.TIPO_INCAPACIDAD.getConcepto(), c.getTiponodo()).
                                        replaceAll(EtiquetasSat.IMPORTE_INCAPACIDAD.getConcepto(), nformat.format(c.getValor()));
                                stbdincapacidades.append(aux);
                                incapacidades.add(c);
                            } else {
                                stbdincapacidades = new StringBuilder();
                                String aux =
                                        EtiquetasSat.INCAPACIDAD.getConcepto().replaceAll(EtiquetasSat.DIAS_INCAPACIDAD.getConcepto(), "" + c.getTiempo().intValue()).
                                        replaceAll(EtiquetasSat.TIPO_INCAPACIDAD.getConcepto(), c.getTiponodo()).
                                        replaceAll(EtiquetasSat.IMPORTE_INCAPACIDAD.getConcepto(), nformat.format(c.getValor()));
                                stbdincapacidades.append(aux);
                                incapacidades.add(c);
                            }
                        }
                        String aux = EtiquetasSat.DEDUCCION.getConcepto().replaceAll(EtiquetasSat.NODO.getConcepto(), c.getAtributo()).
                                replaceAll(EtiquetasSat.NUMERO_CONCEPTO.getConcepto(), c.getConcepto()).
                                replaceAll(EtiquetasSat.NOMBRE_CONCEPTO.getConcepto(), c.getNombre()).
                                replaceAll(EtiquetasSat.IMPORTE_EXENTO.getConcepto(), nformat.format(c.getValor()));
                        stbdeducciones.append(aux);

                    }
                }

                Empleado emp = rl.getIdempleado();
                RegistroPatronal rp = rl.getIdregistropatronal();
                RelacionLaboralPosicion pos = rl.getIdrelacionlaboralposicion();
//                
                Double netoaux = CeniccoUtil.redondear(n.getPercepciones() - n.getDeducciones() + valesdespensa - valesdespensadeducciones);

                Double otrasdeducciones = CeniccoUtil.redondear((n.getDeducciones() + valesdespensadeducciones) - n.getImpuestoretenido());
//                
                String tipocontrato = "";
                String riesgopuesto = "99";
//                
                if (rp.getClaseriesgo() != null) {
                    riesgopuesto = rp.getClaseriesgo().toString();
                }
//               

                Double impIncapacidades = 0.0;
                try {
                    for (Concepto sub : subsidios) {
                        if (!incapacidades.isEmpty()) {
                            for (Concepto inc : incapacidades) {
                                if (sub.getTiponodo().equals(inc.getTiponodo())) {
                                    if (sub.getValor() == inc.getValor()) {
                                        String aux =
                                                EtiquetasSat.PERCEPCION.getConcepto().replaceAll(EtiquetasSat.NODO.getConcepto(), sub.getAtributo()).
                                                replaceAll(EtiquetasSat.NUMERO_CONCEPTO.getConcepto(), sub.getConcepto()).
                                                replaceAll(EtiquetasSat.NOMBRE_CONCEPTO.getConcepto(), sub.getNombre()).
                                                replaceAll(EtiquetasSat.IMPORTE_GRAVADO.getConcepto(), nformat.format(sub.getGravado())).
                                                replaceAll(EtiquetasSat.IMPORTE_EXENTO.getConcepto(), nformat.format(sub.getExento()));
                                        stbdpercepciones.append(aux).append(EtiquetasSat.CIERRE_PERCEPCIONES.getConcepto());
                                        importepercepcionesgravado += sub.getGravado();
                                        importepercepcionesexento += sub.getExento();
                                    }
                                    if (sub.getValor() < inc.getValor()) {
                                        String aux = "<nomina12:Nomina Version=\"1.2\" TipoNomina=\"E\" FechaPago=\"#FECHAPAGO\" FechaInicialPago=\"#FECHAINICIO\" FechaFinalPago=\"#FECHAFIN\" NumDiasPagados=\"#DIASPAGADOS\" TotalPercepciones=\"#SUBTOTAL\" TotalOtrosPagos=\"0.00\"> \n"
                                                + "<nomina12:Emisor RegistroPatronal=\"#REGISTROPATRONAL\" RfcPatronOrigen=\"#RFCEMISOR\"/>\n"
                                                + "<nomina12:Receptor Curp=\"#CURPREPCEPTOR\" NumSeguridadSocial=\"#NSS\" FechaInicioRelLaboral=\"#FECHAINLABORAL\" #ANTIGUEDAD=\"P#ANIOSW\" TipoContrato=\"#TIPOCONTRATO\" Sindicalizado=\"#SINDICALIZADO\" TipoJornada=\"99\" TipoRegimen=\"02\" \n"
                                                + "NumEmpleado=\"#NUMEROEMPLEADO\" RiesgoPuesto=\"#RIESGOPUESTO\" PeriodicidadPago=\"99\" SalarioBaseCotApor=\"#SALARIODIARIO\" SalarioDiarioIntegrado=\"#SALARIOINTEGRADO\" ClaveEntFed=\"MEX\" Departamento=\"#DEPARTAMENTO\" Puesto=\"#PUESTO\">\n"
                                                + "#NODOSUBCONTRATACION"
                                                + "</nomina12:Receptor>\n"
                                                + "<nomina12:Percepciones TotalSueldos=\"#SUELDOSSALARIOS\" TotalGravado=\"#TOTALGRAVADOP\" TotalExento=\"#TOTALEXENTOP\">\n"
                                                + "#DETALLEPERCEP\n"
                                                + "</nomina12:Percepciones>\n"
                                                + "<nomina12:OtrosPagos>\n"
                                                + "<nomina12:OtroPago Clave=\"1480\" Concepto=\"SUBSIDIO AL EMPLEO PAGADO\" Importe=\"0.00\" TipoOtroPago=\"002\">\n"
                                                + "<nomina12:SubsidioAlEmpleo SubsidioCausado=\"0.00\"/>\n"
                                                + "</nomina12:OtroPago>\n"
                                                + "</nomina12:OtrosPagos>\n"
                                                + "#DETALLEINCAPACIDADES"
                                                + "</nomina12:Nomina>";
                                        aux = aux.replaceAll(EtiquetasSat.SUBTOTAL.getConcepto(), nformat.format(sub.getValor()));
                                        aux = aux.replaceAll(EtiquetasSat.TOTAL_GRAVADO_PERCEPCIONES.getConcepto(), sub.getGravado().toString());
                                        aux = aux.replaceAll(EtiquetasSat.TOTAL_EXENTO_PERCEPCIONES.getConcepto(), sub.getExento().toString());
                                        Double totalSueldos = sub.getGravado() + sub.getExento();
                                        aux = aux.replaceAll(EtiquetasSat.SUELDOS_SALARIOS.getConcepto(), totalSueldos.toString());
                                        StringBuilder detallePercepcion = new StringBuilder();
                                        detallePercepcion.append(EtiquetasSat.PERCEPCION.getConcepto().replaceAll(EtiquetasSat.NODO.getConcepto(), sub.getAtributo()).
                                                replaceAll(EtiquetasSat.NUMERO_CONCEPTO.getConcepto(), sub.getConcepto()).
                                                replaceAll(EtiquetasSat.NOMBRE_CONCEPTO.getConcepto(), sub.getNombre()).
                                                replaceAll(EtiquetasSat.IMPORTE_GRAVADO.getConcepto(), nformat.format(sub.getGravado())).
                                                replaceAll(EtiquetasSat.IMPORTE_EXENTO.getConcepto(), nformat.format(sub.getExento()))).
                                                append(EtiquetasSat.CIERRE_PERCEPCIONES.getConcepto());
                                        aux = aux.replaceAll(EtiquetasSat.DETALLE_PERCEPCIONES.getConcepto(), detallePercepcion.toString());
                                        String detalleIncapacidades = EtiquetasSat.INCAPACIDAD.getConcepto().replaceAll(EtiquetasSat.DIAS_INCAPACIDAD.getConcepto(), "" + sub.getTiempo().intValue()).
                                                replaceAll(EtiquetasSat.TIPO_INCAPACIDAD.getConcepto(), sub.getTiponodo()).
                                                replaceAll(EtiquetasSat.IMPORTE_INCAPACIDAD.getConcepto(), nformat.format(sub.getValor()));
                                        String cadenaincapacidades =
                                                EtiquetasSat.ABRIR_INCAPACIDADES.getConcepto() + detalleIncapacidades
                                                + EtiquetasSat.CIERRE_INCAPACIDADES.getConcepto();
                                        aux = aux.replaceAll(EtiquetasSat.DETALLE_INCAPACIDADES.getConcepto(), cadenaincapacidades);
                                        nomEspecial.append(aux);
                                        impIncapacidades = sub.getValor();
                                    }
                                    if (sub.getValor() > inc.getValor()) {
                                        String aux = "<nomina12:Nomina Version=\"1.2\" TipoNomina=\"E\" FechaPago=\"#FECHAPAGO\" FechaInicialPago=\"#FECHAINICIO\" FechaFinalPago=\"#FECHAFIN\" NumDiasPagados=\"#DIASPAGADOS\" TotalPercepciones=\"#SUBTOTAL\" TotalOtrosPagos=\"0.00\"> \n"
                                                + "<nomina12:Emisor RegistroPatronal=\"#REGISTROPATRONAL\" RfcPatronOrigen=\"#RFCEMISOR\"/>\n"
                                                + "<nomina12:Receptor Curp=\"#CURPREPCEPTOR\" NumSeguridadSocial=\"#NSS\" FechaInicioRelLaboral=\"#FECHAINLABORAL\" #ANTIGUEDAD=\"P#ANIOSW\" TipoContrato=\"#TIPOCONTRATO\" Sindicalizado=\"#SINDICALIZADO\" TipoJornada=\"99\" TipoRegimen=\"02\" \n"
                                                + "NumEmpleado=\"#NUMEROEMPLEADO\" RiesgoPuesto=\"#RIESGOPUESTO\" PeriodicidadPago=\"99\" SalarioBaseCotApor=\"#SALARIODIARIO\" SalarioDiarioIntegrado=\"#SALARIOINTEGRADO\" ClaveEntFed=\"MEX\" Departamento=\"#DEPARTAMENTO\" Puesto=\"#PUESTO\">\n"
                                                + "#NODOSUBCONTRATACION"
                                                + "</nomina12:Receptor>\n"
                                                + "<nomina12:Percepciones TotalSueldos=\"#SUELDOSSALARIOS\" TotalGravado=\"#TOTALGRAVADOP\" TotalExento=\"#TOTALEXENTOP\">\n"
                                                + "#DETALLEPERCEP\n"
                                                + "</nomina12:Percepciones>\n"
                                                + "<nomina12:OtrosPagos>\n"
                                                + "<nomina12:OtroPago Clave=\"1480\" Concepto=\"SUBSIDIO AL EMPLEO PAGADO\" Importe=\"0.00\" TipoOtroPago=\"002\">\n"
                                                + "<nomina12:SubsidioAlEmpleo SubsidioCausado=\"0.00\"/>\n"
                                                + "</nomina12:OtroPago>\n"
                                                + "</nomina12:OtrosPagos>\n"
                                                + "#DETALLEINCAPACIDADES"
                                                + "</nomina12:Nomina>";
                                        aux = aux.replaceAll(EtiquetasSat.SUBTOTAL.getConcepto(), nformat.format(sub.getValor()));
                                        aux = aux.replaceAll(EtiquetasSat.TOTAL_GRAVADO_PERCEPCIONES.getConcepto(), sub.getGravado().toString());
                                        aux = aux.replaceAll(EtiquetasSat.TOTAL_EXENTO_PERCEPCIONES.getConcepto(), sub.getExento().toString());
                                        Double totalSueldos = sub.getGravado() + sub.getExento();
                                        aux = aux.replaceAll(EtiquetasSat.SUELDOS_SALARIOS.getConcepto(), totalSueldos.toString());
                                        StringBuilder detallePercepcion = new StringBuilder();
                                        detallePercepcion.append(EtiquetasSat.PERCEPCION.getConcepto().replaceAll(EtiquetasSat.NODO.getConcepto(), sub.getAtributo()).
                                                replaceAll(EtiquetasSat.NUMERO_CONCEPTO.getConcepto(), sub.getConcepto()).
                                                replaceAll(EtiquetasSat.NOMBRE_CONCEPTO.getConcepto(), sub.getNombre()).
                                                replaceAll(EtiquetasSat.IMPORTE_GRAVADO.getConcepto(), nformat.format(sub.getGravado())).
                                                replaceAll(EtiquetasSat.IMPORTE_EXENTO.getConcepto(), nformat.format(sub.getExento()))).
                                                append(EtiquetasSat.CIERRE_PERCEPCIONES.getConcepto());
                                        aux = aux.replaceAll(EtiquetasSat.DETALLE_PERCEPCIONES.getConcepto(), detallePercepcion.toString());
                                        String detalleIncapacidades = EtiquetasSat.INCAPACIDAD.getConcepto().replaceAll(EtiquetasSat.DIAS_INCAPACIDAD.getConcepto(), "" + sub.getTiempo().intValue()).
                                                replaceAll(EtiquetasSat.TIPO_INCAPACIDAD.getConcepto(), sub.getTiponodo()).
                                                replaceAll(EtiquetasSat.IMPORTE_INCAPACIDAD.getConcepto(), nformat.format(sub.getValor()));
                                        String cadenaincapacidades =
                                                EtiquetasSat.ABRIR_INCAPACIDADES.getConcepto() + detalleIncapacidades
                                                + EtiquetasSat.CIERRE_INCAPACIDADES.getConcepto();
                                        aux = aux.replaceAll(EtiquetasSat.DETALLE_INCAPACIDADES.getConcepto(), cadenaincapacidades);
                                        nomEspecial.append(aux);
                                        impIncapacidades = sub.getValor();
                                    }
                                }
                            }
                        } else {
                            String aux =
                                    EtiquetasSat.PERCEPCION.getConcepto().replaceAll(EtiquetasSat.NODO.getConcepto(), sub.getAtributo()).
                                    replaceAll(EtiquetasSat.NUMERO_CONCEPTO.getConcepto(), sub.getConcepto()).
                                    replaceAll(EtiquetasSat.NOMBRE_CONCEPTO.getConcepto(), sub.getNombre()).
                                    replaceAll(EtiquetasSat.IMPORTE_GRAVADO.getConcepto(), nformat.format(sub.getGravado())).
                                    replaceAll(EtiquetasSat.IMPORTE_EXENTO.getConcepto(), nformat.format(sub.getExento()));
                            stbdpercepciones.append(aux).append(EtiquetasSat.CIERRE_PERCEPCIONES.getConcepto());
                            importepercepcionesgravado += sub.getGravado();
                            importepercepcionesexento += sub.getExento();

                            stbdincapacidades = new StringBuilder();
                            String auxIncapacidad =
                                    EtiquetasSat.INCAPACIDAD.getConcepto().
                                    replaceAll(EtiquetasSat.DIAS_INCAPACIDAD.getConcepto(), "" + sub.getTiempo().intValue()).
                                    replaceAll(EtiquetasSat.TIPO_INCAPACIDAD.getConcepto(), sub.getTiponodo()).
                                    replaceAll(EtiquetasSat.IMPORTE_INCAPACIDAD.getConcepto(), nformat.format(sub.getValor()));
                            stbdincapacidades.append(auxIncapacidad);
                        }
                    }
                } catch (NullPointerException npe) {
                    System.out.println("No hay Subsidio o Incapacidad");
                    nomEspecial = new StringBuilder();
                }
// 
                String curp = emp.getCurp() != null ? emp.getCurp() : " ";
                String nss = emp.getAfiliacion() != null ? emp.getAfiliacion() : " ";
                String rfc = emp.getRfc() != null ? emp.getRfc() : " ";
                Domicilio domicilioFiscal = ControladorWS.getInstance().getDomicilioFiscalByIdEmpleado(emp.getIdempleado());
                String domicilio = domicilioFiscal != null && !StringUtils.isNullOrEmpty(domicilioFiscal.getCp()) ? domicilioFiscal.getCp().trim() : "";
                String nombreempleado = getNombre(emp);
                String depto = "NA";
                String puesto = "NA";
                String sindicalizado = "No";
                double diasdiferencia = (calcularDiferenciaDias(fechaIngresoTimbrado.toGregorianCalendar().getTime(),
                        periodo.getFechafin().toGregorianCalendar().getTime()) + 1) / 7;

                Integer antiguedad = (int) diasdiferencia; //                
//                
                String folio = n.getAnio().toString() + n.getPeriodo().toString() + rl.getIdgrupopago().getGrupopago()
                        + tipoproceso.getTipoproceso() + n.getNumeroempleado();
//                
                if (rl.getIdtiporellab() != null
                        && !rl.getIdtiporellab().getTiposat().equals("")) {
                    tipocontrato = rl.getIdtiporellab().getTiposat();
                }
//                
                if (pos != null) {

                    if (pos.getIddepartamento() != null
                            && pos.getIddepartamento().getNombre() != null
                            && !pos.getIddepartamento().getNombre().equals("")) {
                        depto = Util.normalizarCadena(pos.getIddepartamento().getNombre()).replaceAll("\\.", "").replaceAll("/", "").replaceAll("\\(", "").replaceAll("\\)", "");
                    }
//            
                    if (pos.getSindicalizado().equals("1")) {
                        sindicalizado = "Sí";
                    }
                    if (pos.getIdpuesto() != null
                            && pos.getIdpuesto().getNombre() != null
                            && !pos.getIdpuesto().getNombre().equals("")) {
                        puesto = Util.normalizarCadena(pos.getIdpuesto().getNombre()).replaceAll("\\.", "").replaceAll("/", "").replaceAll("\\(", "").replaceAll("\\)", "").replaceAll("&", "y");
                    }
                }
//                
                String xml = stb.toString();
//                
                Double importesueldossalarios = (importepercepcionesgravado + importepercepcionesexento - totalindemnizacion) <= 0.0 ? 0.0 : (importepercepcionesgravado + importepercepcionesexento - totalindemnizacion);
                String cadenaincapacidades = "";
                if (stbdincapacidades.toString().length() > 0) {
                    cadenaincapacidades =
                            EtiquetasSat.ABRIR_INCAPACIDADES.getConcepto() + stbdincapacidades.toString()
                            + EtiquetasSat.CIERRE_INCAPACIDADES.getConcepto();
                }
//              
                boolean existendeducciones = (n.getDeducciones()) > 0;
                boolean existenPercepciones = (n.getPercepciones()) > 0;
                if (retimbrado) {
                    xml = xml.replaceAll(EtiquetasSat.RETIMBRADO.getConcepto(), "<cfdi:CfdiRelacionados TipoRelacion=\"04\"><cfdi:CfdiRelacionado UUID=\"" + n.getUuid() + "\"/></cfdi:CfdiRelacionados>");
                } else {
                    xml = xml.replaceAll(EtiquetasSat.RETIMBRADO.getConcepto(), "");
                }
                if (existendeducciones) {
                    xml = xml.replaceAll(EtiquetasSat.NODO_DEDUCCIONES.getConcepto(), EtiquetasSat.DEDUCCIONES.getConcepto()).
                            replaceAll(EtiquetasSat.DESCUENTO.getConcepto(), "Descuento=\"" + nformat.format((n.getDeducciones() + valesdespensadeducciones)) + "\"").
                            replaceAll(EtiquetasSat.TOTAL_DEDUCCIONES.getConcepto(), "TotalDeducciones=\"" + (nformat.format(n.getDeducciones() + valesdespensadeducciones)) + "\"");
                } else {
                    xml = xml.replaceAll(EtiquetasSat.NODO_DEDUCCIONES.getConcepto(), "").
                            replaceAll(EtiquetasSat.DESCUENTO.getConcepto(), "").
                            replaceAll(EtiquetasSat.TOTAL_DEDUCCIONES.getConcepto(), "");
                }
                if (existenPercepciones) {
                    xml = xml.replaceAll(EtiquetasSat.NODO_PERCEPCIONES.getConcepto(), EtiquetasSat.PERCEPCIONES.getConcepto());
                } else {
                    xml = xml.replaceAll(EtiquetasSat.NODO_PERCEPCIONES.getConcepto(), "");
                }
//                
                if (totalindemnizacion > 0) {
                    stbdpercepciones.append(cadenaindemnizacion);
                    xml = xml.replaceAll(EtiquetasSat.INDEMNIZACION.getConcepto(), "TotalSeparacionIndemnizacion=\"" + nformat.format(totalindemnizacion) + "\"");
                } else {
                    xml = xml.replaceAll(EtiquetasSat.INDEMNIZACION.getConcepto(), "");
                }
                if (otrasdeducciones > 0) {
                    xml = xml.replaceAll(EtiquetasSat.ENCABEZADO_OTRAS_DEDUCCIONES.getConcepto(), "TotalOtrasDeducciones=\"" + nformat.format(otrasdeducciones) + "\"");
                } else {
                    xml = xml.replaceAll(EtiquetasSat.ENCABEZADO_OTRAS_DEDUCCIONES.getConcepto(), "");
                }
//                
                if (n.getImpuestoretenido() > 0) {
                    xml = xml.replaceAll(EtiquetasSat.ENZABEZADO_ISR.getConcepto(), EtiquetasSat.ENCABEZADO_IMPUESTO_RETENIDO.getConcepto());
                } else {
                    xml = xml.replaceAll(EtiquetasSat.ENZABEZADO_ISR.getConcepto(), "");
                }
//                
                if (aplicaotrospagos) {
                    stbdotrospagos.append(EtiquetasSat.CIERRE_OTROSPAGOS.getConcepto());
                    xml = xml.replaceAll(EtiquetasSat.DETALLE_OTROS_PAGOS.getConcepto(), stbdotrospagos.toString()).
                            replaceAll(EtiquetasSat.TOTAL_OTROS_PAGOS.getConcepto(), "TotalOtrosPagos=\"" + nformat.format(otrospagos) + "\"");

                } else {
                    xml = xml.replaceAll(EtiquetasSat.DETALLE_OTROS_PAGOS.getConcepto(), "").
                            replaceAll(EtiquetasSat.TOTAL_OTROS_PAGOS.getConcepto(), "");
                }

                //Validacion para sustitucion de la etiqueta #RFCSUBCONTRATACION (solo para Alpha)
                String compania = gp.getIdcompania().getNombre();
                String rfcSubcontratacion;
                String nodoSubcontratacion;
                switch (compania) {
                    case "C CLARO S. DE R.L. DE C.V.":
                    case "BETA PLANEACION S.A.DE  C.V.":
                    case "GAMA":
                    case "ATLANGA HOLDINGS S.A. DE C.V.":
                    case "EXPLORA CAPITAL S.A. DE C.V.":
                        nodoSubcontratacion = EtiquetasSat.SUBCONTRATACION.getConcepto();
                        rfcSubcontratacion = "FIN100714U43";
                        break;
                    default:
                        rfcSubcontratacion = "";
                        nodoSubcontratacion = "";
                        break;
                }

                String curpEmisor;
                if (rl.getIdcompania().getRfc().length() > 12) {
                    curpEmisor = EtiquetasSat.ATRIBUTO_CURP_EMISOR.getConcepto().replaceAll(EtiquetasSat.BUSCAR_CURP_EMISOR.getConcepto(), rl.getIdcompania().getCurprepresentantelegal());
                } else {
                    curpEmisor = "";
                }

                xml = xml.replaceAll(EtiquetasSat.NOMINA_ESPECIAL.getConcepto(), nomEspecial.toString());
                xml = xml.replaceAll(EtiquetasSat.SERIE.getConcepto(), gp.getSeriesat());
                xml = xml.replaceAll(EtiquetasSat.FOLIO.getConcepto(), folio);
                xml = xml.replaceAll(EtiquetasSat.FECHA_ACTUAL.getConcepto(), fechaactual);
                xml = xml.replaceAll(EtiquetasSat.SUBTOTAL.getConcepto(), nformat.format(n.getPercepciones() + valesdespensa));
                xml = xml.replaceAll(EtiquetasSat.TOTAL_PERCEPCIONES.getConcepto(), nformat.format(n.getPercepciones() + valesdespensa - otrospagos - impIncapacidades));
                xml = xml.replaceAll(EtiquetasSat.TOTAL_NETO.getConcepto(), nformat.format(netoaux));
                xml = xml.replaceAll(EtiquetasSat.LUGAR_EXPEDICION.getConcepto(), gp.getDireccionsat());
                xml = xml.replaceAll(EtiquetasSat.RFC_EMISOR.getConcepto(), gp.getIdcompania().getRfc());
                xml = xml.replaceAll(EtiquetasSat.NOMBRE_EMISOR.getConcepto(), gp.getIdcompania().getNombreTimbre());
                xml = xml.replaceAll(EtiquetasSat.CLAVE_REGIMEN_FISCAL_EMISOR.getConcepto(), gp.getIdcompania().getClaveregimenfiscalemisor());
                xml = xml.replaceAll(EtiquetasSat.RFC_RECEPTOR.getConcepto(), rfc);
                xml = xml.replaceAll(EtiquetasSat.NOMBRE_RECEPTOR.getConcepto(), nombreempleado);
                xml = xml.replaceAll(EtiquetasSat.TIPO_PROCESO.getConcepto(), tipoproceso.getNombre());
                xml = xml.replaceAll(EtiquetasSat.FORMA_PAGO.getConcepto(), EtiquetasSat.PAGO_EXHIBICION.getConcepto());
                xml = xml.replaceAll(EtiquetasSat.FECHA_PAGO.getConcepto(), sdfpago.format(periodo.getFechapago().toGregorianCalendar().getTime()));
                xml = xml.replaceAll(EtiquetasSat.FECHA_INICIO.getConcepto(), sdfpago.format(periodo.getFechainicio().toGregorianCalendar().getTime()));
                xml = xml.replaceAll(EtiquetasSat.FECHA_FIN.getConcepto(), sdfpago.format(periodo.getFechafin().toGregorianCalendar().getTime()));
                xml = xml.replaceAll(EtiquetasSat.DIAS_PAGADOS.getConcepto(), periodo.getIdtipoproceso().getPeriodicidad().toString());
                xml = xml.replaceAll(EtiquetasSat.REGISTRO_PATRONAL.getConcepto(), rp.getRegistropatronal());
                xml = xml.replaceAll(EtiquetasSat.BUSCAR_ATRIBUTO_CURP_EMISOR.getConcepto(), curpEmisor);
                xml = xml.replaceAll(EtiquetasSat.CURP_RECEPTOR.getConcepto(), curp);
                xml = xml.replaceAll(EtiquetasSat.NUMERO_SEGURO_SOCIAL.getConcepto(), nss);
                xml = xml.replaceAll(EtiquetasSat.FECHA_INICIO_LABORAL.getConcepto(), sdfpago.format(fechaIngresoTimbrado.toGregorianCalendar().getTime()));
                xml = xml.replaceAll(EtiquetasSat.ANIOS_ANTIGUEDAD.getConcepto(), antiguedad.toString());
                xml = xml.replaceAll(EtiquetasSat.ANTIGUEDAD.getConcepto(), "Antigüedad");
                xml = xml.replaceAll(EtiquetasSat.TIPO_CONTRATO.getConcepto(), tipocontrato);
                xml = xml.replaceAll(EtiquetasSat.ES_SINDICALIZADO.getConcepto(), sindicalizado);
                xml = xml.replaceAll(EtiquetasSat.NUMERO_EMPLEADO.getConcepto(), rl.getNumeroempleado());
                xml = xml.replaceAll(EtiquetasSat.DEPARTAMENTO.getConcepto(), depto);
                xml = xml.replaceAll(EtiquetasSat.PUESTO.getConcepto(), puesto);
                xml = xml.replaceAll(EtiquetasSat.RIESGO_PUESTO.getConcepto(), riesgopuesto);
                xml = xml.replaceAll(EtiquetasSat.PERIODICIDAD_PAGO.getConcepto(), tipoproceso.getPeriodicidadsat());
                xml = xml.replaceAll(EtiquetasSat.SALARIO_DIARIO.getConcepto(), nformat.format(rl.getSalarioDiario()));
                xml = xml.replaceAll(EtiquetasSat.SALARIO_INTEGRADO.getConcepto(), nformat.format(rl.getSalarioDiarioIntegrado()));
                xml = xml.replaceAll(EtiquetasSat.SUELDOS_SALARIOS.getConcepto(), nformat.format(importesueldossalarios));
                xml = xml.replaceAll(EtiquetasSat.TOTAL_GRAVADO_PERCEPCIONES.getConcepto(), nformat.format(importepercepcionesgravado));
                xml = xml.replaceAll(EtiquetasSat.TOTAL_EXENTO_PERCEPCIONES.getConcepto(), nformat.format(importepercepcionesexento));
                xml = xml.replaceAll(EtiquetasSat.DETALLE_PERCEPCIONES.getConcepto(), stbdpercepciones.toString());
                xml = xml.replaceAll(EtiquetasSat.DETALLE_DEDUCCIONES.getConcepto(), stbdeducciones.toString());
                xml = xml.replaceAll(EtiquetasSat.IMPUESTO_RETENIDO.getConcepto(), nformat.format(n.getImpuestoretenido()));
                xml = xml.replaceAll(EtiquetasSat.TIPO_NOMINA.getConcepto(), tiponomina);
                xml = xml.replaceAll(EtiquetasSat.DESCRIPCION.getConcepto(), "Pago de nómina");
                xml = xml.replaceAll(EtiquetasSat.DETALLE_INCAPACIDADES.getConcepto(), cadenaincapacidades);
                xml = xml.replaceAll(EtiquetasSat.NODO_SUBCONTRATACION.getConcepto(), nodoSubcontratacion);
                xml = xml.replaceAll(EtiquetasSat.RFC_SUBCONTRATACION.getConcepto(), rfcSubcontratacion);
// Timbrado v4.0      
                xml = xml.replaceAll(EtiquetasSat.DOMICILIO_RECEPTOR.getConcepto(), domicilio);
                xml = xml.replaceAll(EtiquetasSat.CERTIFICADO.getConcepto(), new String(Base64.encodeBase64(gp.getIdcompania().getCertificadocsd())));
                xml = xml.replaceAll(EtiquetasSat.NUMEROCERTIFICADO.getConcepto(), gp.getIdcompania().getNumerocertificado());
                String cadenaOriginal = generarCadenaOriginal(xml);
                String selloDigital = generarSelloDigital(llavePrivada, cadenaOriginal);
                xml = xml.replace("Sello=\"\"", "Sello=\"" + selloDigital + "\"");

                System.out.println(rl.getNumeroempleado() + "xml" + xml);

//                
                timbres.add(new FacturaTO(rl, xml, n, puesto, depto,
                        tipoproceso.getPeriodicidadsat(), netoaux, tipoproceso.getNombre(), otrasdeducciones, valesdespensa, fechaactual,
                        importepercepcionesexento, importepercepcionesexento, new ArrayList<Concepto>(mapConceptosRecibos.values()), gp.getIdcompania().getRfc(), rfc));

                subsidios.clear();
                incapacidades.clear();
            }

        } catch (Exception e) {
            System.out.println(e);
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (Exception e) {
                System.out.println(e);
            }

        }
        return timbres;
    }

    public static void escribirFichero(List<String> lineas, String nombrearchivo, String tipoarchivo) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new OutputStreamWriter(baos));
            for (String l : lineas) {
                bw.write(l);
                bw.newLine();
            }
            bw.flush();
            System.out.println("Escribio Correctamente el Archivo....");
//            
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (Exception e) {
            }

        }
//        
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, nombrearchivo);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, tipoarchivo);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, baos.toByteArray());

        context.addCallbackParam("ruta", MyPaths.urlServletDescargarArchivoCsv());

    }

    public static double calcularDiferenciaDias(Date fechaInicio, Date fechaFin) {
        System.out.println("Fechas Diferencia... " + fechaInicio + " | " + fechaFin);
//        
        double diferencia = ((double) fechaFin.getTime() - (double) fechaInicio.getTime()) / Parametros.MILLSECS_PER_DAY;
//
        int dias = (int) Math.rint(diferencia);
//        
        return (double) dias;
    }

    public static String concatPeriodoPago(Periodo periodo) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", new Locale("es", "MX"));
        String cadena = sdf.format(periodo.getFechainicio().toGregorianCalendar().getTime()) + " al " + sdf.format(periodo.getFechafin().toGregorianCalendar().getTime());
        return cadena.toUpperCase();
    }

    public static String getSueldoNormalizado(Double sueldo) {
        if (sueldo > 1) {
            sueldo = CeniccoUtil.redondear(sueldo);
            String[] salario = sueldo.toString().split("\\.");
            String salarioentero = "";
            switch (salario[0].length()) {
                case 0:
                    salarioentero = "0000";
                    break;
                case 1:
                    salarioentero = "000" + salario[0];
                    break;
                case 2:
                    salarioentero = "00" + salario[0];
                    break;
                case 3:
                    salarioentero = "0" + salario[0];
                    break;
                default:
                    salarioentero = salario[0];
                    break;
            }
            String salariodecimal;
            switch (salario[1].length()) {
                case 1:
                    salariodecimal = salario[1] + "0";
                    break;
                default:
                    salariodecimal = salario[1].substring(0, 2);
                    break;
            }
            return salarioentero + salariodecimal;
        } else {
            System.out.println(String.format("%.2f", sueldo));
            String[] salario = String.format("%.2f", sueldo).split("\\.");
            String salarioentero = "";
            switch (salario[0].length()) {
                case 0:
                    salarioentero = "0000";
                    break;
                case 1:
                    salarioentero = "000" + salario[0];
                    break;
                case 2:
                    salarioentero = "00" + salario[0];
                    break;
                case 3:
                    salarioentero = "0" + salario[0];
                    break;
                default:
                    salarioentero = salario[0];
                    break;
            }
            String salariodecimal;
            switch (salario[1].length()) {
                case 1:
                    salariodecimal = salario[1] + "0";
                    break;
                default:
                    salariodecimal = salario[1].substring(0, 2);
                    break;
            }
            return salarioentero + salariodecimal;
        }
//        
    }

    public static byte[] encriptacion(String password) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-1");
            md.update(password.getBytes());
            return md.digest();
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public static Date getFechaUlitmoDia(Integer anio) {
        Calendar cal = Calendar.getInstance();
        cal.set(anio, 11, 31,
                cal.getMinimum(Calendar.HOUR_OF_DAY),
                cal.getMinimum(Calendar.MINUTE),
                cal.getMinimum(Calendar.SECOND));
        return cal.getTime();
    }

    public static Date getFechaInicioDia(Integer anio) {
        Calendar cal = Calendar.getInstance();
        cal.set(anio, 0, 1,
                cal.getMinimum(Calendar.HOUR_OF_DAY),
                cal.getMinimum(Calendar.MINUTE),
                cal.getMinimum(Calendar.SECOND));
        return cal.getTime();
    }

    public static String validarPatrones(List<String> lista, Pattern patron, int posicion) {
        StringBuilder stb = new StringBuilder();
//        
        for (int i = 0; i < lista.size(); i++) {
//            
            String[] parts = lista.get(i).split(",");
            try {
                if (!parts[posicion].equals("")
                        && !patron.matcher(parts[posicion]).find()) {
                    stb.append(i + 1).append(" ");
                }
            } catch (Exception e) {
            }
        }
//        
        return stb.toString();
    }

    public static String validarRequeridos(List<String> lista, int posicion) {
        StringBuilder stb = new StringBuilder();
//        
        for (int i = 0; i < lista.size(); i++) {
            String[] parts = lista.get(i).split(",");
            try {
                if (parts[posicion].equals("")) {
                    stb.append(i + 1).append(" ");
                    System.out.println("Validando posicion " + posicion + " | " + stb.toString());
                }
            } catch (Exception e) {
                stb.append(i + 1).append(" ");
                System.out.println("Validando posicion " + posicion + " | " + stb.toString());
            }
        }
//        
        return stb.toString();
    }

    public static String validarLogitud(List<String> lista, int longitud) {
//        
        StringBuilder stb = new StringBuilder();
//        
        StringTokenizer token;
        for (int i = 0; i < lista.size(); i++) {
            token = new StringTokenizer(lista.get(i), ",");
            if (token.countTokens() < longitud) {
                stb.append(i + 1).append(" ");
            }
        }
//        
        return stb.toString();
    }

    public static String encriptacionStr(String password) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-1");
            md.update(password.getBytes());
            return String.format("%040x", new BigInteger(1, md.digest()));
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public static String getpassword(RelacionLaboral rl) {
        String cadena = rl.getIdempleado().getNombre().charAt(0) + rl.getIdempleado().getApellidopaterno() + "054";
        String pass = Util.encriptacionStr(cadena);
        rl.setPassword(pass);
        List<RelacionLaboral> aux = new ArrayList<>();
        aux.add(rl);
        boolean isValidate = ControladorWS.getInstance().editRelacionesLaborales(aux);
        return isValidate ? cadena : "";
    }

    public static String getpasswordRecuperacion(RelacionLaboral rl) {
        char[] elementos = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
        char[] conjunto = new char[10];
        for (int i = 0; i < 10; i++) {
            int el = (int) (Math.random() * 33);
            conjunto[i] = (char) elementos[el];
        }
        String cadena = new String(conjunto);
        String pass = Util.encriptacionStr(cadena);
        rl.setPassword(pass);
        List<RelacionLaboral> aux = new ArrayList<>();
        aux.add(rl);
        boolean isValidate = ControladorWS.getInstance().editRelacionesLaborales(aux);
        return isValidate ? cadena : "";
    }

    public static void buildHeadersWSCenicco() {
        Usuario usuario = ControladorSesiones.getInstance().getUsuarioSession();
        WSCenicco port = ControladorWS.ControladorWSHolder.service.getWSCeniccoPort();
        if (usuario != null) {
            WSBindingProvider prov = (WSBindingProvider) port;
            prov.setOutboundHeaders(
                    // Simple string value as a header, like <simpleHeader>stringValue</simpleHeader>
                    Headers.create(new QName("headerUsuario"), String.valueOf(usuario.getIdUsuario())));
        }
        ControladorWS.ControladorWSHolder.port = port;
    }

    public static String join(String separator, List<String> input) {
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

    public static String joinInteger(String separator, List<Integer> input) {
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

    public static String generarCadenaOriginal(String xml) throws TransformerException {
        //Se obtiene el archivo de la cadena original del SAT v4.0 almacenada en el servidor (junto con las plantillas de timbrado)
        StreamSource streamsource = new StreamSource(ControladorWS.getInstance().getPathCadenaOriginal());
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer xlstransformer = transformerFactory.newTransformer(streamsource);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        xlstransformer.transform(new StreamSource(new StringReader(xml)), new StreamResult(output));
        String resultado = "";
        try {
            resultado = output.toString("UTF-8");
        } catch (Exception e) {
            System.out.println("Error al convertir la cadena original");
        }
        return resultado;
    }

    public static PrivateKey getPrivateKey(File keyFile, String password) throws FileNotFoundException, GeneralSecurityException, IOException {
        FileInputStream in = new FileInputStream(keyFile);
        org.apache.commons.ssl.PKCS8Key pkcs = new org.apache.commons.ssl.PKCS8Key(in, password.toCharArray());
        byte[] decrypter = pkcs.getDecryptedBytes();
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decrypter);
        PrivateKey pk = pkcs.getPrivateKey();
        return pk;
    }

    public static String generarSelloDigital(PrivateKey key, String cadenaOriginal) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature sing = Signature.getInstance("SHA256withRSA");
        sing.initSign(key, new SecureRandom());
        try {
            sing.update(cadenaOriginal.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException | SignatureException e) {
            System.out.println("Error al generar el sello");
        }

        byte[] signature = sing.sign();
        return new String(Base64.encodeBase64(signature));
    }

    public static String capitalize(final String str, final char... delimiters) {
        final int delimLen = delimiters == null ? -1 : delimiters.length;
        if (StringUtils.isNullOrEmpty(str) || delimLen == 0) {
            return str;
        }
        final char[] buffer = str.toCharArray();
        boolean capitalizeNext = true;
        for (int i = 0; i < buffer.length; i++) {
            final char ch = buffer[i];
            if (isDelimiter(ch, delimiters)) {
                capitalizeNext = true;
            } else if (capitalizeNext) {
                buffer[i] = Character.toTitleCase(ch);
                capitalizeNext = false;
            }
        }
        return new String(buffer);
    }

    private static boolean isDelimiter(final char ch, final char[] delimiters) {
        if (delimiters == null) {
            return Character.isWhitespace(ch);
        }
        for (final char delimiter : delimiters) {
            if (ch == delimiter) {
                return true;
            }
        }
        return false;
    }

    public static String getNombreMes(Integer numeroMes) {
        String mesString;
        switch (numeroMes) {
            case 1:
                mesString = "Enero";
                break;
            case 2:
                mesString = "Febrero";
                break;
            case 3:
                mesString = "Marzo";
                break;
            case 4:
                mesString = "Abril";
                break;
            case 5:
                mesString = "Mayo";
                break;
            case 6:
                mesString = "Junio";
                break;
            case 7:
                mesString = "Julio";
                break;
            case 8:
                mesString = "Agosto";
                break;
            case 9:
                mesString = "Septiembre";
                break;
            case 10:
                mesString = "Octubre";
                break;
            case 11:
                mesString = "Noviembre";
                break;
            case 12:
                mesString = "Diciembre";
                break;
            default:
                mesString = "Invalid month";
                break;
        }
        return mesString;
    }

    public static List<String> convertFileToStr(FileInputStream archivo) {
        BufferedReader bf = null;
        List<String> lista = new ArrayList<>();
        try {
            InputStream is = archivo;
            bf = new BufferedReader(new InputStreamReader(is));
            String linea;
            while ((linea = bf.readLine()) != null) {
                lista.add(linea);
            }
        } catch (Exception e) {
        } finally {
            if (bf != null) {
                try {
                    bf.close();
                } catch (Exception e) {
                }
            }
        }

        return lista;
    }
}
