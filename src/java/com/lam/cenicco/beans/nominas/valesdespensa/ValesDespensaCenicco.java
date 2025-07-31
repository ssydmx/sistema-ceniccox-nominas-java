/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lam.cenicco.beans.nominas.valesdespensa;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import com.lam.cenicco.dao.ProcesoDAO;
import com.lam.cenicco.impl.ControladorWS;
import com.lam.cenicco.librerias.utilidades.CeniccoUtil;
import com.lam.cenicco.to.CuentaHerramientaTrabajoAlphaTO;
import com.lam.cenicco.util.ControladorSesiones;
import com.lam.cenicco.util.EnumProveedorVale;
import com.lam.cenicco.util.Parametros;
import com.lam.cenicco.util.ParametrosReportes;
import com.lam.cenicco.util.Util;
import com.lam.cenicco.util.paths.MyPaths;
import com.lam.cenicco.ws.Compania;
import com.lam.cenicco.ws.CompaniaProveedorVale;
import com.lam.cenicco.ws.GrupoPago;
import com.lam.cenicco.ws.Parametro;
import com.lam.cenicco.ws.ProveedorVale;
import com.lam.cenicco.ws.TipoVale;
import com.lam.cenicco.ws.VistaValesDespensa;
import com.mysql.jdbc.StringUtils;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
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
 * @author Antonio Durán
 */
public class ValesDespensaCenicco implements ProcesoDAO<VistaValesDespensa> {

    protected Integer selectedGrupopago;
//    
    protected Integer periodo;
//    
    protected Integer anio;
//    
    protected List<VistaValesDespensa> vista;
//    
    protected List<VistaValesDespensa> filteredVista;
//    
    protected Double totalimporte;
    protected Double totaltiempo;
//
    protected List<TipoVale> tiposVales;
//        
    protected Integer idtipovale;
//        
    protected CompaniaProveedorVale companiaProveedorVale;

    public ValesDespensaCenicco() {
        if (this.vista == null) {
            this.vista = new ArrayList<>();
            this.filteredVista = this.vista;
        }
    }

    @PostConstruct
    @Override
    public void init() {
        this.tiposVales = ControladorWS.getInstance().getTiposVales();
    }

    @Override
    public void consultar(ActionEvent event) {
        this.totalimporte = 0.0;
        this.totaltiempo = 0.0;
//
        GrupoPago gp = ControladorWS.getInstance().findGrupoPagoById(this.selectedGrupopago);
        Compania c = gp.getIdcompania();
        this.companiaProveedorVale = ControladorWS.getInstance().getCompaniaProveedorValeByIdCompaniaAndIdTipoVale(c.getIdcompania(), this.idtipovale);
        System.out.println("ID: " + companiaProveedorVale.getIdcompaniaproveedorvale()
                + " Compania proveedor vale: " + companiaProveedorVale.getCompania().getNombre()
                + " Tipo Vale: " + companiaProveedorVale.getTipovale().getNombre()
                + " Proveedor Vale: " + companiaProveedorVale.getProveedorvale().getNombre());
        TipoVale tv = companiaProveedorVale.getTipovale();
//        
        this.vista = ControladorWS.getInstance().findValesDespensa(this.selectedGrupopago, this.periodo, this.anio, tv.getIdtipovale());
        System.out.println("Consulta Vales Despensa Finalizado...");
        this.filteredVista = this.vista;
//       
        Iterator<VistaValesDespensa> iter = this.vista.iterator();
        while (iter.hasNext()) {
            VistaValesDespensa v = iter.next();
            this.totalimporte += v.getImporte();
            this.totaltiempo += v.getTiempo();
        }

    }

    @Override
    public void limpiar(ActionEvent event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void create(ActionEvent event) {
        String servicio = ControladorSesiones.getInstance().getUsuarioSession().getServicio();
        TipoVale tv = companiaProveedorVale.getTipovale();
        switch (servicio) {
            case Parametros.SERVICIO_YAMANA:
                this.getDispersionYamana();
                break;
            /*case Parametros.SERVICIO_MELMEX:
             this.getDispersionMelmex();
             break;*/
            case Parametros.SERVICIO_FILTERS:
                this.getDispersionFilters();
                break;
            case Parametros.SERVICIO_ALPHA:
                switch (tv.getIdtipovale()) {
                    case 1:
                        //Vales despensa
                        this.getDispersionAlphaTxt();
                        break;
                    case 2:
                        //Herramienta de trabajo
                        this.getDispersionHerramientaTrabajoAlphaTxt();
                        break;
                }
                break;
            case Parametros.SERVICIO_AEMSA:
            case Parametros.SERVICIO_MARTILLO:
                this.getDispersionAemsa();
                break;
            case Parametros.SERVICIO_WHITEHAT:
                this.getDispersionWhitehat();
                break;
            case Parametros.SERVICIO_ARTSANA:
                this.getDispersionAlphaTxt();
                break;
            case Parametros.SERVICIO_KONECTA:
                this.getDispersionBroxel();
                break;
            case Parametros.SERVICIO_BAKERTILLY:
                this.getDispersionEdenredMexicoBakertilly();
                break;
            case Parametros.SERVICIO_EGELHOF:
                switch (tv.getIdtipovale()) {
                    case 1:
                        //Vales despensa
                        this.getDispersionValesDespensaSiValeMexicoEgelhof();
                        break;
                    case 2:
                        //Herramienta de trabajo
                        break;
                    case 3:
                        //Vales de gasolina
                        this.getDispersionValesGasolinaSiValeMexicoEgelhof();
                        break;
                }
                break;
            default:
                this.getDispersion();
                break;
        }

    }

    public void createDispersion() {
        ProveedorVale pv = this.companiaProveedorVale.getProveedorvale();
        EnumProveedorVale epv = EnumProveedorVale.getById(pv.getIdproveedorvale());
        switch (epv) {
            case SI_VALE_MEXICO:
                this.getDispersionSiValeMexico();
                break;
            case EDENRED_MEXICO:
                this.getDispersionEdenredMexico();
                break;
            case TOKA_INTERNACIONAL:
                this.getDispersionTokaInternacional();
                break;
            default:
                this.getDispersion();
                break;
        }
    }

    private void getDispersion() {
        GrupoPago gp = ControladorWS.getInstance().findGrupoPagoById(this.selectedGrupopago);
        DecimalFormat nformat = new DecimalFormat("0.00");
//        
        Iterator<VistaValesDespensa> iter = this.vista.iterator();
//        
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = this.getSheet(workbook);
//        
        int i = 0;

        while (iter.hasNext()) {
//
            int j = 0;
//
            VistaValesDespensa v = iter.next();
//
            String ctavales = "";
            String tarjeta = "";
            if (v.getCtavales() != null) {
                ctavales = v.getCtavales();
            }
            if (v.getTarjetavales() != null) {
                tarjeta = v.getTarjetavales();
            }
//          
            HSSFRow row = sheet.createRow(i);
            HSSFCell cell = row.createCell(j);
            cell.setCellValue(v.getNumeroempleado());
            j++;
//          
            cell = row.createCell(j);
            cell.setCellValue(v.getNombreempleado());
            j++;
//          
            cell = row.createCell(j);
            cell.setCellValue(v.getApellidopaterno());
            j++;
//          
            cell = row.createCell(j);
            cell.setCellValue(v.getApellidomaterno());
            j++;
//          
            cell = row.createCell(j);
            cell.setCellValue(ctavales);
            j++;
//          
            cell = row.createCell(j);
            cell.setCellValue(tarjeta);
            j++;
//          
            cell = row.createCell(j);
            cell.setCellValue(nformat.format(v.getImporte()));
//            
            i++;
        }

        this.escribirArchivo(workbook, gp);
    }

    private void getDispersionAemsa() {
        GrupoPago gp = ControladorWS.getInstance().findGrupoPagoById(this.selectedGrupopago);
        DecimalFormat nformat = new DecimalFormat("0.00");
//        
        Iterator<VistaValesDespensa> iter = this.vista.iterator();
//        
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = this.getSheet(workbook);

        HSSFRow row = sheet.createRow(0);
        HSSFCell cell = row.createCell(0);
        cell.setCellValue("ID");
        cell = row.createCell(1);
        cell.setCellValue("NOMINA");
        cell = row.createCell(2);
        cell.setCellValue("MONTO");
        cell = row.createCell(3);
        cell.setCellValue("PRODUCTO");
//        
        int i = 1;

        while (iter.hasNext()) {
//
            int j = 0;
//
            VistaValesDespensa v = iter.next();
//          

            row = sheet.createRow(i);
            cell = row.createCell(j);
            cell.setCellValue("15805");
            j++;
//          
            cell = row.createCell(j);
            cell.setCellValue(v.getNumeroempleado());
            j++;
//          

//          
            cell = row.createCell(j);
            cell.setCellValue(nformat.format(v.getImporte()));
            j++;
//            
            cell = row.createCell(j);
            cell.setCellValue("EASYVALE CHIP");
            i++;
        }

        this.escribirArchivo(workbook, gp);
    }

    private void getDispersionWhitehat() {
        GrupoPago gp = ControladorWS.getInstance().findGrupoPagoById(this.selectedGrupopago);
        DecimalFormat nformat = new DecimalFormat("0.00");
//        
        Iterator<VistaValesDespensa> iter = this.vista.iterator();
//        
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = this.getSheet(workbook);

        HSSFRow row = sheet.createRow(0);
        HSSFCell cell = row.createCell(0);
        cell.setCellValue("NOMBRE EMPLEADO");
        cell = row.createCell(1);
        cell.setCellValue("CUENTA");
        cell = row.createCell(2);
        cell.setCellValue("MONTO");
//        
        int i = 1;

        while (iter.hasNext()) {
//
            int j = 0;
//
            VistaValesDespensa v = iter.next();
//          

            row = sheet.createRow(i);
            cell = row.createCell(j);
            cell.setCellValue(v.getNombreempleado() + " " + v.getApellidopaterno());
            j++;
//          
            cell = row.createCell(j);
            cell.setCellValue(v.getCtavales());
            j++;

            cell = row.createCell(j);
            cell.setCellValue(nformat.format(v.getImporte()));
            i++;
        }

        this.escribirArchivo(workbook, gp);
    }

    private void getDispersionFilters() {
        GrupoPago gp = ControladorWS.getInstance().findGrupoPagoById(this.selectedGrupopago);
        DecimalFormat nformat = new DecimalFormat("0.00");
        Iterator<VistaValesDespensa> iter = this.vista.iterator();
//        
        //        
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = this.getSheet(workbook);
//        
        HSSFRow row = sheet.createRow(0);
//    
        HSSFCell cell = row.createCell(0);
        cell.setCellValue("XSUBTOTAL");
        cell = row.createCell(1);
        cell.setCellValue(nformat.format(this.totalimporte));
//        
        row = sheet.createRow(1);
        cell = row.createCell(0);
        cell.setCellValue("XCLIENTE");
        cell = row.createCell(1);
        cell.setCellValue("XTIPOPEDIDO");
        cell = row.createCell(2);
        cell.setCellValue("XNUMERO");
        cell = row.createCell(3);
        cell.setCellValue("XIMPORTE");
        cell = row.createCell(4);
        cell.setCellValue("XCOLONIA");
        cell = row.createCell(5);
        cell.setCellValue("XPOSTAL");
//        
        int i = 2;

        while (iter.hasNext()) {
            VistaValesDespensa v = iter.next();
//            
            row = sheet.createRow(i);
            int j = 0;
//            
            String ctavales = "";
            if (v.getCtavales() != null) {
                ctavales = v.getCtavales();
            }
//            
            cell = row.createCell(j);
            cell.setCellValue(ctavales);
            j++;
//            
            cell = row.createCell(j);
            cell.setCellValue("DISPERSION");
            j++;
//            
            cell = row.createCell(j);
            cell.setCellValue(v.getNumeroempleado());
            j++;
//            
            cell = row.createCell(j);
            cell.setCellValue(nformat.format(v.getImporte()));
//            
            i++;
        }
        this.escribirArchivo(workbook, gp);
    }

    private void getDispersionMelmex() {
        GrupoPago gp = ControladorWS.getInstance().findGrupoPagoById(this.selectedGrupopago);
        DecimalFormat nformat = new DecimalFormat("0.00");
//        
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = this.getSheet(workbook);
//        
        HSSFRow row = sheet.createRow(0);
        HSSFCell cell = row.createCell(0);
        cell.setCellValue("Suma de IMPORTE");
//        
        row = sheet.createRow(1);
        cell = row.createCell(2);
        cell.setCellValue("1510");
//        
        row = sheet.createRow(2);
        cell = row.createCell(0);
        cell.setCellValue("NUMERO EMPLEADO");
        cell = row.createCell(1);
        cell.setCellValue("NOMBRE");
        cell = row.createCell(2);
        cell.setCellValue("VALES DESPENSA");
//        
        Iterator<VistaValesDespensa> iter = this.vista.iterator();
//        
        int i = 3;
        while (iter.hasNext()) {
//
            VistaValesDespensa v = iter.next();
//            
            int j = 0;
//            
            row = sheet.createRow(i);
            cell = row.createCell(j);
            cell.setCellValue(v.getNumeroempleado());
            j++;
//             
            cell = row.createCell(j);
            cell.setCellValue(v.getApellidopaterno() + " " + v.getApellidomaterno() + " " + v.getNombreempleado());
            j++;
//             
            cell = row.createCell(j);
            cell.setCellValue(nformat.format(v.getImporte()));
//             
            i++;
//            
        }

        row = sheet.createRow(i);
        cell = row.createCell(0);
        cell.setCellValue("Total general");
        cell = row.createCell(2);
        cell.setCellValue(nformat.format(this.totalimporte));
        this.escribirArchivo(workbook, gp);
    }

    private void getDispersionAlpha() {
        GrupoPago gp = ControladorWS.getInstance().findGrupoPagoById(this.selectedGrupopago);
        String cliente = "";
        switch (gp.getIdcompania().getNombreCorto()) {
            //ALF
            case "ALF":
                cliente = "10817980";
                break;
            case "ALPHAHOLDING":
                cliente = "10810410";
                break;
            case "AXSSERVICIOS":
                cliente = "10818710";
                break;
            case "ALPHACAPITAL":
                cliente = "10818230";
                break;
            case "ALPHA":
                cliente = "10695250";
                break;
            case "BETA":
                cliente = "10695170";
                break;
            default:
                cliente = "N° de cliente no registrado";
                break;
        }
        DecimalFormat nformat = new DecimalFormat("0.00");
//        
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = this.getSheet(workbook);
        workbook.setSheetName(workbook.getSheetIndex(sheet), "STK.SALTIT");

        HSSFRow row = sheet.createRow(1);
        HSSFCell cell = row.createCell(2);
        cell.setCellValue("Proceso");
        cell = row.createCell(3);
        cell.setCellValue("STK.SALTIT.P30");
        row = sheet.createRow(2);
        cell = row.createCell(2);
        cell.setCellValue("No. Cliente");
        cell = row.createCell(3);
        cell.setCellValue(cliente);
        row = sheet.createRow(5);
        cell = row.createCell(0);
        cell.setCellValue("NOMBRE");
        cell = row.createCell(1);
        cell.setCellValue("IUT");
        cell = row.createCell(2);
        cell.setCellValue("MONTO DEPÓSITO");
//        
        Iterator<VistaValesDespensa> iter = this.vista.iterator();
//        
        int i = 6;
        while (iter.hasNext()) {
//
            VistaValesDespensa v = iter.next();
//            
            int j = 0;
//            
            row = sheet.createRow(i);
            cell = row.createCell(j);
            cell.setCellValue(v.getApellidopaterno() + " " + v.getApellidomaterno() + " " + v.getNombreempleado());
            j++;
//             
            cell = row.createCell(j);
            cell.setCellValue(v.getCtavales());
            j++;
//             
            cell = row.createCell(j);
            cell.setCellValue(nformat.format(v.getImporte()));
//             
            i++;
//            
        }
        this.escribirArchivoAlpha(workbook, gp);
    }

    private void getDispersionAlphaTxt() {
        GrupoPago gp = ControladorWS.getInstance().findGrupoPagoById(this.selectedGrupopago);
        TipoVale tv = companiaProveedorVale.getTipovale();
        Integer tipodispersion = 0;

        DecimalFormat nformat = new DecimalFormat("0.00");
        String cliente = "";
        String nombrearchivo = "";
        switch (tv.getIdtipovale()) {
            case 1:
                //Vales despensa
                tipodispersion = 30;
                nombrearchivo = "Dispersion_Vales_Despensa";
                break;
            case 2:
                //Herramienta de trabajo
                tipodispersion = 79;
                nombrearchivo = "Dispersion_Herramienta_Trabajo";
                break;
        }
        List<String> lineas = new ArrayList<>();
        String linea = "STK.SALTIT.P" + tipodispersion;
        lineas.add(linea);
        switch (gp.getIdcompania().getNombreCorto()) {
            //ALF
            case "ALF":
                cliente = "10817980";
                break;
            case "ALPHAHOLDING":
                cliente = "10810410";
                break;
            case "AXSSERVICIOS":
                cliente = "10818710";
                break;
            case "ALPHACAPITAL":
                cliente = "10818230";
                break;
            case "ALPHA":
                cliente = "10695250";
                break;
            case "BETA":
                cliente = "10695170";
                break;
            case "ARTSANA":
                cliente = "10221390";
                break;
            default:
                cliente = "N° de cliente no registrado";
                break;
        }
        String registros = "0000000".substring(("" + this.vista.size()).length()) + ("" + this.vista.size());
        String importe = "000000000000".substring(("" + nformat.format(this.totalimporte)).length()) + ("" + nformat.format(this.totalimporte));
        linea = "05" + cliente + Util.getCadenaConEspacios(30, "0") + registros + importe + tipodispersion;
        lineas.add(linea);
        int contador = 0;
        for (VistaValesDespensa v : this.vista) {
            contador++;
            linea = "06" + ("0000000").substring(("" + contador).length()) + contador + "    " + v.getCtavales() + "0000000000".substring(("" + nformat.format(v.getImporte())).length()) + ("" + nformat.format(v.getImporte()));
            lineas.add(linea);
        }
        this.escribirFichero(lineas, nombrearchivo);
    }

    private void getDispersionHerramientaTrabajoAlphaTxt() {
        System.out.println("Dispersion Herramienta Trabajo Alpha Txt");
        DecimalFormat nformat = new DecimalFormat("0.00");
        GrupoPago gp = ControladorWS.getInstance().findGrupoPagoById(this.selectedGrupopago);

        Map<String, CuentaHerramientaTrabajoAlphaTO> cuentasHTcompania = new HashMap<>();
        Parametro parametro = ControladorWS.getInstance().getParametro(Parametros.ALPHA_CUENTAS_HERRAMIENTA_TRABAJO);
        System.out.println("JSON Herramienta Trabajo Alpha: " + parametro.getValor());
        Type listType = new TypeToken<ArrayList<CuentaHerramientaTrabajoAlphaTO>>() {
        }.getType();
        List<CuentaHerramientaTrabajoAlphaTO> cuentasHerramientaTrabajo = new Gson().fromJson(parametro.getValor(), listType);
        for (CuentaHerramientaTrabajoAlphaTO to : cuentasHerramientaTrabajo) {
            cuentasHTcompania.put(to.getNombreCorto(), to);
        }

        String nombreCorto = gp.getIdcompania().getNombreCorto();
        CuentaHerramientaTrabajoAlphaTO cuenta = cuentasHTcompania.get(nombreCorto);

        Integer tipodispersion = 79;
        String nombrearchivo = "Dispersion_Herramienta_Trabajo";

        List<String> lineasBody = new ArrayList<>();
        int contador = 0;
        Double importeTotal = 0.0;
        for (VistaValesDespensa v : this.vista) {
            contador++;
            importeTotal += v.getImporte();

            String linea = "06" + (("0000000").substring(("" + contador).length()) + contador) + "    " + v.getCtavales() + "0000000000".substring(("" + nformat.format(v.getImporte())).length()) + ("" + nformat.format(v.getImporte()));
            lineasBody.add(linea);
        }

        List<String> lineas = new ArrayList<>();
        String header = "TSL.CNTTJA.TIT.P" + tipodispersion;
        String subHeader = "05"
                + cuenta.getNumeroCliente()
                + "    "
                + cuenta.getCuentaConcentradora() + (("0000000").substring(("" + contador).length()) + contador)
                + ("000000000000".substring(("" + nformat.format(importeTotal)).length()) + ("" + nformat.format(importeTotal)))
                + tipodispersion;

        lineas.add(header);
        lineas.add(subHeader);
        lineas.addAll(lineasBody);

        this.escribirFichero(lineas, nombrearchivo);
    }

    private void getDispersionYamana() {
        DecimalFormat nformat = new DecimalFormat("0.00");
//        
        String hoy = new SimpleDateFormat("yyyyMMdd").format(new Date());
//        
        GrupoPago gp = ControladorWS.getInstance().findGrupoPagoById(this.selectedGrupopago);
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = this.getSheet(workbook);
//        
        String size = "" + this.vista.size();
//        
        String son = "'" + "00000".substring(size.length()) + size;
//        
        HSSFRow row = sheet.createRow(0);
        HSSFCell cell = row.createCell(0);
        cell.setCellValue("'03");
        cell = row.createCell(1);
        cell.setCellValue("'007");
        cell = row.createCell(2);
        cell.setCellValue("032");
        cell = row.createCell(3);
        cell.setCellValue("'64927");
        cell = row.createCell(4);
        cell.setCellValue("'001");
        cell = row.createCell(5);
        cell.setCellValue(gp.getIdcompania().getNombre());
        cell = row.createCell(6);
        cell.setCellValue(son);
        cell = row.createCell(7);
        cell.setCellValue("'" + nformat.format(this.totalimporte));
        cell = row.createCell(8);
        cell.setCellValue(hoy);
//
        int i = 1;
        Iterator<VistaValesDespensa> iter = this.vista.iterator();
        while (iter.hasNext()) {
            VistaValesDespensa v = iter.next();
//
            int j = 0;
//            
            row = sheet.createRow(i);
            cell = row.createCell(j);
            cell.setCellValue(v.getNumeroempleado());
            j++;
//            
            cell = row.createCell(j);
            cell.setCellValue(v.getCtavales());
            j++;
//            
            cell = row.createCell(j);
            cell.setCellValue(nformat.format(v.getImporte()));
//            
            i++;
        }
//        
        this.escribirArchivo(workbook, gp);
//        
    }

    private HSSFSheet getSheet(HSSFWorkbook workbook) {
        GrupoPago gp = ControladorWS.getInstance().findGrupoPagoById(this.selectedGrupopago);
        return workbook.createSheet("ValesDespensa_" + gp.getGrupopago() + "_" + +this.periodo + "_" + this.anio);

    }

    private void escribirArchivo(HSSFWorkbook book, GrupoPago gp) {
        try {
            String nombrearchvio = "ValesDespensa_" + gp.getGrupopago() + "_" + this.periodo + "_" + this.anio;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            book.write(baos);
            byte[] data = baos.toByteArray();
            RequestContext context = RequestContext.getCurrentInstance();
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, nombrearchvio);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_XLS);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, data);
            context.addCallbackParam("ruta", MyPaths.urlServletDescargarArchivoCsv());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void escribirArchivoAlpha(HSSFWorkbook book, GrupoPago gp) {
        try {
            String nombrearchvio = "ValesDespensa_" + gp.getGrupopago() + "_" + this.periodo + "_" + this.anio;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            book.write(baos);
            byte[] data = baos.toByteArray();
            RequestContext context = RequestContext.getCurrentInstance();
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, nombrearchvio);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_XLS);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, data);
            context.addCallbackParam("ruta", MyPaths.urlServletDescargarArchivoCsv());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void escribirFichero(List<String> lineas, String nombrearchivo) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new OutputStreamWriter(baos));
            for (String l : lineas) {
                bw.write(l);
                bw.newLine();
            }
            bw.flush();
            System.out.println("Escribicio Correctamente el Archivo....");
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
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, nombrearchivo);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_TXT);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, baos.toByteArray());
        context.addCallbackParam("ruta", MyPaths.urlServletDescargarArchivoTexto());
    }

    @Override
    public void edit(ActionEvent event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void delete(VistaValesDespensa obj) {
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

    public Integer getSelectedGrupopago() {
        return selectedGrupopago;
    }

    public void setSelectedGrupopago(Integer selectedGrupopago) {
        this.selectedGrupopago = selectedGrupopago;
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

    public List<VistaValesDespensa> getFilteredVista() {
        return filteredVista;
    }

    public void setFilteredVista(List<VistaValesDespensa> filteredVista) {
        this.filteredVista = filteredVista;
    }

    public List<VistaValesDespensa> getVista() {
        return vista;
    }

    public Double getTotalimporte() {
        return totalimporte;
    }

    public Double getTotaltiempo() {
        return totaltiempo;
    }

    public List<TipoVale> getTiposVales() {
        return tiposVales;
    }

    public void setTiposVales(List<TipoVale> tiposVales) {
        this.tiposVales = tiposVales;
    }

    public Integer getIdtipovale() {
        return idtipovale;
    }

    public void setIdtipovale(Integer idtipovale) {
        this.idtipovale = idtipovale;
    }

    public CompaniaProveedorVale getCompaniaProveedorVale() {
        return companiaProveedorVale;
    }

    public void setCompaniaProveedorVale(CompaniaProveedorVale companiaProveedorVale) {
        this.companiaProveedorVale = companiaProveedorVale;
    }

    private void getDispersionSiValeMexico() {
        GrupoPago gp = ControladorWS.getInstance().findGrupoPagoById(this.selectedGrupopago);
        TipoVale tv = this.companiaProveedorVale.getTipovale();
        Integer tipodispersion = 0;

        DecimalFormat nformat = new DecimalFormat("0.00");
        String cliente = !StringUtils.isNullOrEmpty(this.companiaProveedorVale.getNumerocliente())
                ? this.companiaProveedorVale.getNumerocliente()
                : "N° de cliente no registrado";
        String nombrearchivo = "";
        switch (tv.getIdtipovale()) {
            case 1:
                //Vales despensa
                tipodispersion = 30;
                nombrearchivo = "Dispersion_Vales_Despensa";
                break;
            case 2:
                //Herramienta de trabajo
                tipodispersion = 79;
                nombrearchivo = "Dispersion_Herramienta_Trabajo";
                break;
        }
        List<String> lineas = new ArrayList<>();
        String linea = "STK.SALTIT.P" + tipodispersion;
        lineas.add(linea);

        String registros = "0000000".substring(("" + this.vista.size()).length()) + ("" + this.vista.size());
        String importe = "000000000000".substring(("" + nformat.format(this.totalimporte)).length()) + ("" + nformat.format(this.totalimporte));
        linea = "05" + cliente + Util.getCadenaConEspacios(30, "0") + registros + importe + tipodispersion;
        lineas.add(linea);
        int contador = 0;
        for (VistaValesDespensa v : this.vista) {
            contador++;
            linea = "06" + ("0000000").substring(("" + contador).length()) + contador + "    " + v.getCtavales() + "0000000000".substring(("" + nformat.format(v.getImporte())).length()) + ("" + nformat.format(v.getImporte()));
            lineas.add(linea);
        }
        this.escribirFichero(lineas, nombrearchivo);
    }

    private void getDispersionEdenredMexico() {
        DecimalFormat nformat = new DecimalFormat("0.00");
//        
        String hoy = new SimpleDateFormat("yyyyMMdd").format(new Date());
//        
        GrupoPago gp = ControladorWS.getInstance().findGrupoPagoById(this.selectedGrupopago);
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = this.getSheet(workbook);
//        
        String size = "" + this.vista.size();
//        
        String son = "'" + "00000".substring(size.length()) + size;
//        
        HSSFRow row = sheet.createRow(0);
        HSSFCell cell = row.createCell(0);
        cell.setCellValue("'03");
        cell = row.createCell(1);
        cell.setCellValue("'007");
        cell = row.createCell(2);
        cell.setCellValue("032");
        cell = row.createCell(3);
        cell.setCellValue("'64927");
        cell = row.createCell(4);
        cell.setCellValue("'001");
        cell = row.createCell(5);
        cell.setCellValue(this.companiaProveedorVale.getCompania().getNombre());
        cell = row.createCell(6);
        cell.setCellValue(son);
        cell = row.createCell(7);
        cell.setCellValue("'" + nformat.format(this.totalimporte));
        cell = row.createCell(8);
        cell.setCellValue(hoy);
//
        int i = 1;
        Iterator<VistaValesDespensa> iter = this.vista.iterator();
        while (iter.hasNext()) {
            VistaValesDespensa v = iter.next();
//
            int j = 0;
//            
            row = sheet.createRow(i);
            cell = row.createCell(j);
            cell.setCellValue(v.getNumeroempleado());
            j++;
//            
            cell = row.createCell(j);
            cell.setCellValue(v.getCtavales());
            j++;
//            
            cell = row.createCell(j);
            cell.setCellValue(nformat.format(v.getImporte()));
//            
            i++;
        }
//        
        this.escribirArchivo(workbook, gp);
    }

    private void getDispersionTokaInternacional() {
        GrupoPago gp = ControladorWS.getInstance().findGrupoPagoById(this.selectedGrupopago);
        DecimalFormat nformat = new DecimalFormat("0.00");
//        
        Iterator<VistaValesDespensa> iter = this.vista.iterator();
//        
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = this.getSheet(workbook);

        HSSFRow row = sheet.createRow(0);
        HSSFCell cell = row.createCell(0);
        cell.setCellValue("ID");
        cell = row.createCell(1);
        cell.setCellValue("NOMINA");
        cell = row.createCell(2);
        cell.setCellValue("MONTO");
        cell = row.createCell(3);
        cell.setCellValue("PRODUCTO");
//        
        int i = 1;

        while (iter.hasNext()) {
//
            int j = 0;
//
            VistaValesDespensa v = iter.next();
//          

            row = sheet.createRow(i);
            cell = row.createCell(j);
            cell.setCellValue(this.companiaProveedorVale.getNumerocliente());
            j++;
//          
            cell = row.createCell(j);
            cell.setCellValue(v.getNumeroempleado());
            j++;
//          

//          
            cell = row.createCell(j);
            cell.setCellValue(nformat.format(v.getImporte()));
            j++;
//            
            cell = row.createCell(j);
            cell.setCellValue("EASYVALE CHIP");
            i++;
        }

        this.escribirArchivo(workbook, gp);
    }

    private void getDispersionBroxel() {
        GrupoPago gp = ControladorWS.getInstance().findGrupoPagoById(this.selectedGrupopago);
        DecimalFormat nformat = new DecimalFormat("0.00");
        String nombrearchivo = "Asignacion_Linea_Sample_Vales_De_Despensa";

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = this.getSheet(workbook);

        int i = 0;
        int j = 0;

        HSSFRow row = sheet.createRow(0);
        HSSFCell cell = row.createCell(0);
        cell.setCellValue("CUENTA");

        cell = row.createCell(1);
        cell.setCellValue("INCREMENTO POS");

        cell = row.createCell(2);
        cell.setCellValue("INCREMENTO ATM");

        i = 0;
        j = 1;
        for (VistaValesDespensa v : this.vista) {
            row = sheet.createRow(j);

            cell = row.createCell(i++);
            cell.setCellValue(v.getCtavales());

            cell = row.createCell(i++);
            cell.setCellValue(v.getImporte());

            cell = row.createCell(i++);
            cell.setCellValue("0");

            i = 0;
            j++;
        }

        try {
            String nombreArchivo = "Asignacion_Linea_Sample_Vales_De_Despensa_" + gp.getGrupopago() + "_" + this.periodo + "_" + this.anio;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            byte[] data = baos.toByteArray();

            RequestContext context = RequestContext.getCurrentInstance();
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, nombreArchivo);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_XLS);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, data);
            context.addCallbackParam("ruta", MyPaths.urlServletDescargarArchivoCsv());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getDispersionEdenredMexicoBakertilly() {
        DecimalFormat nformat = new DecimalFormat("0.00");
//        
        String hoy = new SimpleDateFormat("yyyyMMdd").format(new Date());
//        
        GrupoPago gp = ControladorWS.getInstance().findGrupoPagoById(this.selectedGrupopago);
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = this.getSheet(workbook);
//        
        String size = "" + this.vista.size();
//        
        String son = "'" + "00000".substring(size.length()) + size;
//        
        HSSFRow row = sheet.createRow(0);
        HSSFCell cell = row.createCell(0);
        cell.setCellValue("'03");
        cell = row.createCell(1);
        cell.setCellValue("'001");
        cell = row.createCell(2);
        cell.setCellValue("032");
        cell = row.createCell(3);
        cell.setCellValue("'85902");
        cell = row.createCell(4);
        cell.setCellValue("'002");
        cell = row.createCell(5);
        cell.setCellValue(this.companiaProveedorVale.getCompania().getNombre());
        cell = row.createCell(6);
        cell.setCellValue(son);
        cell = row.createCell(7);
        cell.setCellValue("'" + nformat.format(this.totalimporte));
        cell = row.createCell(8);
        cell.setCellValue(hoy);
//
        int i = 1;
        Iterator<VistaValesDespensa> iter = this.vista.iterator();
        while (iter.hasNext()) {
            VistaValesDespensa v = iter.next();
//
            int j = 0;
//            
            row = sheet.createRow(i);
            cell = row.createCell(j);
            cell.setCellValue(v.getNumeroempleado());
            j++;
//            
            cell = row.createCell(j);
            cell.setCellValue(v.getCtavales());
            j++;
//            
            cell = row.createCell(j);
            cell.setCellValue(nformat.format(v.getImporte()));
//            
            i++;
        }
//        
        try {
            String nombrearchvio = "C-0185902002-" + hoy;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            byte[] data = baos.toByteArray();
            RequestContext context = RequestContext.getCurrentInstance();
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, nombrearchvio);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_XLS);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, data);
            context.addCallbackParam("ruta", MyPaths.urlServletDescargarArchivoCsv());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getDispersionValesDespensaSiValeMexicoEgelhof() {
        DecimalFormat nformat = new DecimalFormat("0.00");
        String hoy = new SimpleDateFormat("yyyyMMdd").format(new Date());

        GrupoPago gp = ControladorWS.getInstance().findGrupoPagoById(this.selectedGrupopago);
        TipoVale tv = this.companiaProveedorVale.getTipovale();
        Integer tipodispersion = 0;

        String cliente = !StringUtils.isNullOrEmpty(this.companiaProveedorVale.getNumerocliente())
                ? this.companiaProveedorVale.getNumerocliente()
                : "N° de cliente no registrado";

        String nombrearchivo = "";
        switch (tv.getIdtipovale()) {
            case 1:
                //Vales despensa
                tipodispersion = 34;
                nombrearchivo = "Dispersion_Vales_Despensa_" + hoy;
                break;
            case 2:
                //Herramienta de trabajo
                tipodispersion = 00;
                nombrearchivo = "Dispersion_Herramienta_Trabajo_" + hoy;
                break;
        }

        List<String> lineas = new ArrayList<>();
        String linea = "STK.SALTIT.P" + tipodispersion;
        lineas.add(linea);

        String registros = "0000000".substring(("" + this.vista.size()).length()) + ("" + this.vista.size());
        String importe = "000000000000".substring(("" + nformat.format(this.totalimporte)).length()) + ("" + nformat.format(this.totalimporte));
        linea = "05" + cliente + Util.getCadenaConEspacios(30, "0") + registros + importe + tipodispersion;
        lineas.add(linea);
        int contador = 0;
        for (VistaValesDespensa v : this.vista) {
            contador++;
            linea = "06" + ("0000000").substring(("" + contador).length()) + contador + "    " + v.getCtavales() + "0000000000".substring(("" + nformat.format(v.getImporte())).length()) + ("" + nformat.format(v.getImporte()));
            lineas.add(linea);
        }
        this.escribirFichero(lineas, nombrearchivo);
    }

    private void getDispersionValesGasolinaSiValeMexicoEgelhof() {
        DecimalFormat nformat = new DecimalFormat("0.00");
        GrupoPago gp = ControladorWS.getInstance().findGrupoPagoById(this.selectedGrupopago);
        String cliente = "10907730";
//        
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = this.getSheet(workbook);
        workbook.setSheetName(workbook.getSheetIndex(sheet), "STK.SALTIT");

        HSSFRow row = sheet.createRow(1);
        HSSFCell cell = row.createCell(2);
        cell.setCellValue("Proceso");
        cell = row.createCell(3);
        cell.setCellValue("STK.SALTIT.P34");
        row = sheet.createRow(2);
        cell = row.createCell(2);
        cell.setCellValue("No. Cliente");
        cell = row.createCell(3);
        cell.setCellValue(cliente);
        row = sheet.createRow(5);
        cell = row.createCell(0);
        cell.setCellValue("NOMBRE");
        cell = row.createCell(1);
        cell.setCellValue("IUT");
        cell = row.createCell(2);
        cell.setCellValue("MONTO DEPÓSITO");
//        
        Iterator<VistaValesDespensa> iter = this.vista.iterator();
//        
        int i = 6;
        while (iter.hasNext()) {
//
            VistaValesDespensa v = iter.next();
//            
            int j = 0;
//            
            row = sheet.createRow(i);
            cell = row.createCell(j);
            cell.setCellValue(v.getApellidopaterno() + " " + v.getApellidomaterno() + " " + v.getNombreempleado());
            j++;
//             
            cell = row.createCell(j);
            cell.setCellValue(v.getCtavales());
            j++;
//             
            cell = row.createCell(j);
            cell.setCellValue(nformat.format(v.getImporte()));
//             
            i++;
//            
        }

        /**
         * Inicio Sheet AutoSizeColumn *
         */
        int total_y = 255;
        for (int x = 0; x <= total_y; x++) {
            sheet.autoSizeColumn(x);
        }

        try {
            String hoy = new SimpleDateFormat("yyyyMMdd").format(new Date());
            String nombrearchvio = "Dispersion_Vales_Gasolina_" + hoy;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            byte[] data = baos.toByteArray();
            RequestContext context = RequestContext.getCurrentInstance();
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, nombrearchvio);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_XLS);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, data);
            context.addCallbackParam("ruta", MyPaths.urlServletDescargarArchivoCsv());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
