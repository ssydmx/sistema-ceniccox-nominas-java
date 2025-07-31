/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lam.cenicco.beans.nominas.terminales;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lam.cenicco.dao.ProcesoDAO;
import com.lam.cenicco.impl.ControladorWS;
import com.lam.cenicco.librerias.mensajes.Mensajes;
import com.lam.cenicco.librerias.utilidades.CeniccoUtil;
import com.lam.cenicco.to.ComodinEmpleadoTO;
import com.lam.cenicco.to.ComodinTO;
import com.lam.cenicco.to.CuadrillaMarcajesTO;
import com.lam.cenicco.to.CuadrillaTO;
import com.lam.cenicco.to.MarcajesTO;
import com.lam.cenicco.util.ControladorSesiones;
import com.lam.cenicco.util.ParametrosReportes;
import com.lam.cenicco.util.Util;
import com.lam.cenicco.util.paths.MyPaths;
import com.lam.cenicco.ws.ProcesoTempIncidencias;
import com.lam.cenicco.ws.Producto;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author erick
 */
public class IncidenciasTerminalesCenicco implements ProcesoDAO<ProcesoTempIncidencias> {

    private String fechaInicio;
    private String fechaFin;
    private List<ProcesoTempIncidencias> incidencias;
    private List<ProcesoTempIncidencias> filteredIncidencias;
    private List<ProcesoTempIncidencias> selectedIncidencias;
//    
    private String fechaReporte;
    private String fechaInicioReporteGrl;
    private String fechaFinReporteGrl;
    private Integer numeroPeriodo;
    private Integer anioPeriodo;
    private Double tipoCambioUSD;
    private String numeroComodin;
    private String numeroEmpleado;
    private String fechaRegistro;
    private UploadedFile archivo;
    private Integer periodoMarcajes;
    protected List<Producto> productos;
    protected String[] selectedProductos;
    
    @PostConstruct
    @Override
    public void init() {
        productos = ControladorWS.getInstance().getProductos();
    }

    @Override
    public void consultar(ActionEvent event) {
        Date finicio;
        Date ffin;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        String token = ControladorWS.getInstance().getToken(null, ControladorSesiones.getInstance().getUsuarioSession());
        if (this.fechaInicio != null && !this.fechaInicio.equals("")) {
            try {
                finicio = new SimpleDateFormat("dd/MM/yyyyy").parse(this.fechaInicio);
                if (this.fechaFin != null && !this.fechaFin.equals("")) {
                    ffin = new SimpleDateFormat("dd/MM/yyyyy").parse(this.fechaFin);
                    /*c.setTime(ffin);
                     c.add(Calendar.DATE, 1);
                     ffin = c.getTime();*/
                    this.incidencias = ControladorWS.getInstance().findIncidenciasTerminales(df.format(finicio), df.format(ffin), null, null, token);
                } else {
                    this.incidencias = ControladorWS.getInstance().findIncidenciasTerminales(df.format(finicio), fechaFin, null, null, token);
                }
            } catch (ParseException ex) {
                Logger.getLogger(IncidenciasTerminalesCenicco.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("Fechas Ingresadas no validas");
            }
        } else {
            this.incidencias = ControladorWS.getInstance().findIncidenciasTerminales(null, null, null, null, token);
        }
        System.out.println("Lista: " + this.incidencias.size());
    }

    public void descargarReporteCsv() {

        String cadena = "NUMERO EMPLEADO, NOMBRE, CANTIDAD, PERIODO, CONCEPTO, REFERENCIA, CUADRILLA, RANCHO, LOCALIDAD, LOTE, LLAVE, ESTATUS, FECHA REGISTRO, IMPORTE, COSTO, ALIAS, NOTIFICACION\n";
        for (ProcesoTempIncidencias i : this.incidencias) {
            String estatus = "";
            switch (i.getEstatusproceso()) {
                case "1":
                    estatus = "Registrada";
                    break;
                case "0":
                    estatus = "Recibida";
                    break;
            }
            cadena += i.getNumeroEmpleado().replaceAll(",", "-") + ",";
            cadena += i.getNombreEmpleado().replaceAll(",", "-") + ",";
            cadena += i.getCantidad().replaceAll(",", "-") + ",";
            cadena += i.getPeriodo().replaceAll(",", "-") + ",";
            cadena += i.getConcepto().replaceAll(",", "-") + ",";
            cadena += i.getReferencia().replaceAll(",", "-") + ",";
            cadena += i.getCuadrilla().replaceAll(",", "-") + ",";
            cadena += i.getRancho().replaceAll(",", "-") + ",";
            cadena += i.getLocalidad().replaceAll(",", "-") + ",";
            cadena += i.getLote().replaceAll(",", "-") + ",";
            cadena += i.getLlave().replace(",", "-") + ",";
            cadena += estatus + ",";
            cadena += i.getFechaRegistroPizca() + ",";
            cadena += i.getImporte() + ",";
            cadena += i.getCosto() + ",";
            cadena += i.getAlias() != null ? i.getAlias().replaceAll(",", "-") + "," : ",";
            cadena += i.getNotificacion() != null ? i.getNotificacion().replaceAll(",", "-") : "";
            cadena = Util.normalizarCadena(cadena) + "\n";
        }
//        
        String nombreArchivo = "CONSULTA_MARCAJES";
//        
        try {
            byte[] data = cadena.getBytes();
            RequestContext context = RequestContext.getCurrentInstance();
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, nombreArchivo);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, data);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_CSV);
            context.addCallbackParam("ruta", MyPaths.urlServletDescargarArchivoCsv());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void generarReporte() {
        try {
            String token = ControladorWS.getInstance().getToken(null, ControladorSesiones.getInstance().getUsuarioSession());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date fecha = new SimpleDateFormat("dd/MM/yyyyy").parse(this.fechaReporte);
            byte[] data = ControladorWS.getInstance().getReporteCosecha(token, "" + sdf.format(fecha));
            String nombreArchivo = "Reporte_Marcajes_";
            RequestContext context = RequestContext.getCurrentInstance();
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, nombreArchivo);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, data);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_XLSX);
            context.addCallbackParam("ruta", MyPaths.urlServletDescargarArchivoCsv());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void generarReporteGeneralPizca() {
        try {
            String token = ControladorWS.getInstance().getToken(null, ControladorSesiones.getInstance().getUsuarioSession());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date fechaInicioGrl = new SimpleDateFormat("dd/MM/yyyyy").parse(this.fechaInicioReporteGrl);
            Date fechaFinGrl = new SimpleDateFormat("dd/MM/yyyyy").parse(this.fechaFinReporteGrl);
            byte[] data = ControladorWS.getInstance().getReporteGeneralPizca(token, "" + sdf.format(fechaInicioGrl), "" + sdf.format(fechaFinGrl), Arrays.asList(selectedProductos));
            String nombreArchivo = "Reporte_General_Ajuste_Pizca";
            RequestContext context = RequestContext.getCurrentInstance();
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, nombreArchivo);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, data);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_XLSX);
            context.addCallbackParam("ruta", MyPaths.urlServletDescargarArchivoCsv());
            this.fechaInicioReporteGrl = null;
            this.fechaFinReporteGrl = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void generarReporteGeneral() {
        try {
            String token = ControladorWS.getInstance().getToken(null, ControladorSesiones.getInstance().getUsuarioSession());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date fechaInicioGrl = new SimpleDateFormat("dd/MM/yyyyy").parse(this.fechaInicioReporteGrl);
            Date fechaFinGrl = new SimpleDateFormat("dd/MM/yyyyy").parse(this.fechaFinReporteGrl);
            byte[] data = ControladorWS.getInstance().getReporteGeneral(token, "" + sdf.format(fechaInicioGrl), "" + sdf.format(fechaFinGrl));
            String nombreArchivo = "Reporte_General_";
            RequestContext context = RequestContext.getCurrentInstance();
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, nombreArchivo);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, data);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_XLSX);
            context.addCallbackParam("ruta", MyPaths.urlServletDescargarArchivoCsv());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void generarReporteCuadrillaLoteCosto() {
        try {
            String token = ControladorWS.getInstance().getToken(null, ControladorSesiones.getInstance().getUsuarioSession());

            byte[] data = ControladorWS.getInstance().getReporteCostoGeneralAjusteBonoPizca(token, numeroPeriodo, anioPeriodo, tipoCambioUSD, Arrays.asList(selectedProductos));

            String nombreArchivo = "Semana " + numeroPeriodo + "_" + "Reporte_Marcajes_Cuadrilla_Lote_Costo";

            this.numeroPeriodo = null;
            this.anioPeriodo = null;
            this.tipoCambioUSD = null;

            RequestContext context = RequestContext.getCurrentInstance();
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, nombreArchivo);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, data);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_XLSX);
            context.addCallbackParam("ruta", MyPaths.urlServletDescargarArchivoCsv());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openModalPeriodoCuadrillaLoteCosto(ActionEvent event) {
        this.numeroPeriodo = null;
        this.anioPeriodo = null;
        this.tipoCambioUSD = null;

        RequestContext context = RequestContext.getCurrentInstance();
        context.update("fomrReporteCuadrillaLoteCosto");
        context.execute("PF('PeriodoCuadrillaLoteCosto').show();");
    }

    public void openModalUpdateComodinNumeroEmpleadoCosecha(ActionEvent event) {
        this.numeroComodin = null;
        this.numeroEmpleado = null;
        this.fechaRegistro = null;

        RequestContext context = RequestContext.getCurrentInstance();
        context.update("fomrUpdateComodinNumeroEmpleadoCosecha");
        context.execute("PF('UpdateComodinNumeroEmpleadoCosecha').show();");
    }

    public void modificarMarcajes() {
        try {
            String token = ControladorWS.getInstance().getToken(null, ControladorSesiones.getInstance().getUsuarioSession());
            String respuesta = ControladorWS.getInstance().getRespuestaActualizarMarcajes(token, this.selectedIncidencias);
            System.out.println("Respuesta:" + respuesta);

        } catch (Exception e) {
            System.out.println("Error: " + e.toString());
        }
    }

    public void modificarComodinNumeroEmpleadoCosecha() {
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Date fRegistro = null;
            try {
                fRegistro = new SimpleDateFormat("dd/MM/yyyy").parse(this.fechaRegistro);
            } catch (ParseException ex) {
                Logger.getLogger(IncidenciasTerminalesCenicco.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("Fechas Ingresadas no validas");
            }

            boolean validate = ControladorWS.getInstance().modificarComodinNumeroEmpleadoCosecha(this.numeroComodin, this.numeroEmpleado, df.format(fRegistro));
            System.out.println("Respuesta: " + validate);
            FacesMessage msg = validate ? Util.getFacesMessage(FacesMessage.SEVERITY_INFO, "Marcajes y asistencia actualidos exitosamente!")
                    : Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, "Error al actualizar");
            this.generarMsg(msg, validate);
        } catch (Exception e) {
            System.out.println("Error: " + e.toString());
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
    public void delete(ProcesoTempIncidencias obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void listenerSelected() {
        for (ProcesoTempIncidencias p : this.selectedIncidencias) {
            System.out.println("Id:" + p.getId() + " | NumeroEmpleado: " + p.getNumeroEmpleado() + " | Cantidad: " + p.getCantidad());
        }
    }

    @Override
    public void generarMsg(FacesMessage msg, boolean isValidate) {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().addMessage(null, msg);
        context.addCallbackParam("isValidate", isValidate);
    }

    @Override
    public String getInformacion() {
        if (this.incidencias != null) {
            return CeniccoUtil.getInformacion(this.incidencias.size());
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

    public String getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(String fechaFin) {
        this.fechaFin = fechaFin;
    }

    public List<ProcesoTempIncidencias> getIncidencias() {
        return incidencias;
    }

    public void setIncidencias(List<ProcesoTempIncidencias> incidencias) {
        this.incidencias = incidencias;
    }

    public List<ProcesoTempIncidencias> getFilteredIncidencias() {
        return filteredIncidencias;
    }

    public void setFilteredIncidencias(List<ProcesoTempIncidencias> filteredIncidencias) {
        this.filteredIncidencias = filteredIncidencias;
    }

    public String getFechaReporte() {
        return fechaReporte;
    }

    public void setFechaReporte(String fechaReporte) {
        this.fechaReporte = fechaReporte;
    }

    public String getFechaInicioReporteGrl() {
        return fechaInicioReporteGrl;
    }

    public void setFechaInicioReporteGrl(String fechaInicioReporteGrl) {
        this.fechaInicioReporteGrl = fechaInicioReporteGrl;
    }

    public String getFechaFinReporteGrl() {
        return fechaFinReporteGrl;
    }

    public void setFechaFinReporteGrl(String fechaFinReporteGrl) {
        this.fechaFinReporteGrl = fechaFinReporteGrl;
    }

    public List<ProcesoTempIncidencias> getSelectedIncidencias() {
        return selectedIncidencias;
    }

    public void setSelectedIncidencias(List<ProcesoTempIncidencias> selectedIncidencias) {
        this.selectedIncidencias = selectedIncidencias;
    }

    public Integer getNumeroPeriodo() {
        return numeroPeriodo;
    }

    public void setNumeroPeriodo(Integer numeroPeriodo) {
        this.numeroPeriodo = numeroPeriodo;
    }

    public Integer getAnioPeriodo() {
        return anioPeriodo;
    }

    public void setAnioPeriodo(Integer anioPeriodo) {
        this.anioPeriodo = anioPeriodo;
    }

    public Double getTipoCambioUSD() {
        return tipoCambioUSD;
    }

    public void setTipoCambioUSD(Double tipoCambioUSD) {
        this.tipoCambioUSD = tipoCambioUSD;
    }

    public String getNumeroComodin() {
        return numeroComodin;
    }

    public void setNumeroComodin(String numeroComodin) {
        this.numeroComodin = numeroComodin;
    }

    public String getNumeroEmpleado() {
        return numeroEmpleado;
    }

    public void setNumeroEmpleado(String numeroEmpleado) {
        this.numeroEmpleado = numeroEmpleado;
    }

    public String getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(String fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public UploadedFile getArchivo() {
        return archivo;
    }

    public void setArchivo(UploadedFile archivo) {
        this.archivo = archivo;
    }

    public Integer getPeriodoMarcajes() {
        return periodoMarcajes;
    }

    public void setPeriodoMarcajes(Integer periodoMarcajes) {
        this.periodoMarcajes = periodoMarcajes;
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }

    public String[] getSelectedProductos() {
        return selectedProductos;
    }

    public void setSelectedProductos(String[] selectedProductos) {
        this.selectedProductos = selectedProductos;
    }

    public void openModalCargaMarcajes(ActionEvent event) {
        this.periodoMarcajes = null;

        RequestContext context = RequestContext.getCurrentInstance();
        context.update("formUploadMarcajes");
        context.execute("PF('UploadMarcajesDialog').show();");
    }

    public void descargarLayoutCargaMarcajes(ActionEvent event) {
        List<String> lineas = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        sb.append("NUMERO EMPLEADO,")
                .append("NOMBRE EMPLEADO,")
                .append("PUESTO(Mayordomo/Pesador/Sorteador1/Sorteador2/Cosechador),")
                .append("CANTIDAD,")
                .append("CONCEPTO(Destajo/Tiempo),")
                .append("ID USUARIO,")
                .append("CUADRILLA,")
                .append("LOCALIDAD,")
                .append("RANCHO,")
                .append("LOTE,")
                .append("PRODUCTO(Chicharo/Ejote convencional chico/Ejote convencional grande/Ejote organico chico/Ejote organico grande/Broccolini),")
                .append("FECHA REGISTRO(dd/MM/yyyy HH:mm),")
                .append("ALIAS");
        lineas.add(sb.toString());
        Util.escribirFichero(lineas, "LayoutCargaMarcajes", ParametrosReportes.ARCHIVO_CSV);
    }

    public void fileUploadListenerMarcajes(FileUploadEvent event) {
        CuadrillaMarcajesTO to = new CuadrillaMarcajesTO();
        to.setPeriodo(this.periodoMarcajes);
        archivo = event.getFile();

        List<String> lista = Util.convertFileToStr(this.archivo);
        to.setLista(lista);
        String error = to.validarArchivo();

        List<String> puestosCuadrilla = Arrays.asList(new String[]{"Mayordomo", "Pesador", "Sorteador1", "Sorteador2"});
        List<String> puestosCosecha = Arrays.asList(new String[]{"Cosechador"});

        if (error.equals("")) {
            for (int i = 0; i < lista.size(); i++) {
                String[] data = lista.get(i).split(",");
                if (data.length > 0) {
                    if (puestosCuadrilla.contains(data[CuadrillaMarcajesTO.POSICION_PUESTO])) {
                        CuadrillaTO c = new CuadrillaTO();
                        c.setNumeroempleado(data[CuadrillaMarcajesTO.POSICION_NUMERO_EMPLEADO]);
                        c.setNombreempleado(data[CuadrillaMarcajesTO.POSICION_NOMBRE_EMPLEADO]);
                        c.setPuesto(data[CuadrillaMarcajesTO.POSICION_PUESTO]);
                        c.setUsuario(data[CuadrillaMarcajesTO.POSICION_ID_USUARIO]);
                        c.setCuadrilla(data[CuadrillaMarcajesTO.POSICION_CUADRILLA]);
                        c.setFecha(data[CuadrillaMarcajesTO.POSICION_FECHA_REGISTRO]);
                        //System.out.println("XXXXXXX :: Datos CSV Cuadrilla: " + c.toString());
                        to.getFactorPuestos().add(c);
                    }

                    if (puestosCosecha.contains(data[CuadrillaMarcajesTO.POSICION_PUESTO])) {
                        MarcajesTO m = new MarcajesTO();
                        m.setNumeroempleado(data[CuadrillaMarcajesTO.POSICION_NUMERO_EMPLEADO]);
                        m.setNombreempleado(data[CuadrillaMarcajesTO.POSICION_NOMBRE_EMPLEADO]);
                        m.setValor(data[CuadrillaMarcajesTO.POSICION_CANTIDAD]);
                        m.setConcepto(data[CuadrillaMarcajesTO.POSICION_CONCEPTO]);
                        m.setUsuario(data[CuadrillaMarcajesTO.POSICION_ID_USUARIO]);
                        m.setCuadrilla(data[CuadrillaMarcajesTO.POSICION_CUADRILLA]);
                        m.setLocalidad(data[CuadrillaMarcajesTO.POSICION_LOCALIDAD]);
                        m.setRancho(data[CuadrillaMarcajesTO.POSICION_RANCHO]);
                        m.setLote(data[CuadrillaMarcajesTO.POSICION_LOTE]);
                        m.setLlave("P-01");
                        m.setProducto(data[CuadrillaMarcajesTO.POSICION_PRODUCTO]);
                        m.setFecha(data[CuadrillaMarcajesTO.POSICION_FECHA_REGISTRO]);
                        m.setAlias(CuadrillaMarcajesTO.POSICION_ALIAS < data.length ? data[CuadrillaMarcajesTO.POSICION_ALIAS] : "");
                        //System.out.println("XXXXXXX :: Datos CSV Marcajes: " + m.toString());
                        to.getIncidencias().add(m);
                    }
                }
            }
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            System.out.println("Datos JSON Marcajes ::: " + gson.toJson(to));
            String token = ControladorWS.getInstance().getToken(null, ControladorSesiones.getInstance().getUsuarioSession());
            boolean isValidate = ControladorWS.getInstance().registrarMarcajesCuadrilla(token, gson.toJson(to));
            if (isValidate) {
                generarMsg(Util.getFacesMessage(FacesMessage.SEVERITY_INFO, "Exitoso al registrar marcajes"), isValidate);
            } else {
                generarMsg(Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, "Error al registrar marcajes"), isValidate);
            }
        } else {
            generarMsg(Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, "Error al registrar marcajes: " + error), false);
        }
    }

    public void descargarLayoutActualizarComodines(ActionEvent event) {
        List<String> lineas = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        sb.append("NUMERO COMODIN,")
                .append("NUMERO EMPLEADO,")
                .append("FECHA REGISTRO(dd/MM/yyyy),");
        lineas.add(sb.toString());
        Util.escribirFichero(lineas, "LayoutActualizarComodines", ParametrosReportes.ARCHIVO_CSV);
    }

    public void fileUploadListenerActualizarComodines(FileUploadEvent event) {
        ComodinEmpleadoTO to = new ComodinEmpleadoTO();
        archivo = event.getFile();

        List<String> headersComodin = Arrays.asList(new String[]{"NUMERO COMODIN", "NUMERO COMODIN", "FECHA REGISTRO(dd/MM/yyyy)"});
        List<String> lista = Util.convertFileToStr(this.archivo);
        to.setLista(lista);
        String error = to.validarArchivo();

        if (error.equals("")) {
            for (int i = 0; i < lista.size(); i++) {
                String[] data = lista.get(i).split(",");
                if (data.length > 0) {
                    if (!headersComodin.contains(data[ComodinEmpleadoTO.POSICION_NUMERO_COMODIN])
                            && !headersComodin.contains(data[ComodinEmpleadoTO.POSICION_NUMERO_EMPLEADO])) {
                        ComodinTO c = new ComodinTO();
                        c.setNumeroComodin(data[ComodinEmpleadoTO.POSICION_NUMERO_COMODIN]);
                        c.setNumeroEmpleado(data[ComodinEmpleadoTO.POSICION_NUMERO_EMPLEADO]);
                        c.setFechaRegistro(data[ComodinEmpleadoTO.POSICION_FECHA_REGISTRO]);
                        to.getComodines().add(c);
                    }
                }
            }

            StringBuilder errorEjecucion = new StringBuilder();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            for (ComodinTO c : to.getComodines()) {
                Date fRegistro = null;
                try {
                    fRegistro = new SimpleDateFormat("dd/MM/yyyy").parse(c.getFechaRegistro());
                } catch (ParseException ex) {
                    Logger.getLogger(IncidenciasTerminalesCenicco.class.getName()).log(Level.SEVERE, null, ex);
                    System.out.println("Fechas Ingresada no validas");
                }

                boolean isValidate = ControladorWS.getInstance().modificarComodinNumeroEmpleadoCosecha(c.getNumeroComodin(), c.getNumeroComodin(), df.format(fRegistro));
                if (!isValidate) {
                    if (errorEjecucion.length() > 0) {
                        errorEjecucion.append(", ");
                    }
                    errorEjecucion.append("Error al actualizar el comodin").append(" ").append(c.getNumeroComodin());
                }
            }
            if (errorEjecucion.length() > 0) {
                generarMsg(Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, errorEjecucion.toString()), true);
            } else {
                generarMsg(Util.getFacesMessage(FacesMessage.SEVERITY_INFO, "Exitoso al actualizar el comodin"), true);
            }
        } else {
            generarMsg(Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, "Error al actualizar el comodin: " + error), false);
        }
    }
}
