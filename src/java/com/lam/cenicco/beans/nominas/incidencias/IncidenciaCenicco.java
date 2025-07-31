/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lam.cenicco.beans.nominas.incidencias;

import com.lam.cenicco.beans.AppBean;
import com.lam.cenicco.dao.ProcesoDAO;
import com.lam.cenicco.impl.ControladorWS;
import com.lam.cenicco.librerias.mensajes.Mensajes;
import com.lam.cenicco.librerias.utilidades.CeniccoUtil;
import com.lam.cenicco.to.EmpleadoIncidenciaFaltaTO;
import com.lam.cenicco.util.ControladorSesiones;
import com.lam.cenicco.util.Parametros;
import com.lam.cenicco.util.ParametrosReportes;
import com.lam.cenicco.util.Util;
import com.lam.cenicco.util.paths.MyPaths;
import com.lam.cenicco.ws.Concepto;
import com.lam.cenicco.ws.GrupoPago;
import com.lam.cenicco.ws.HorarioLaboral;
import com.lam.cenicco.ws.Incidencia;
import com.lam.cenicco.ws.Parametro;
import com.lam.cenicco.ws.Periodo;
import com.lam.cenicco.ws.RelacionLaboral;
import com.lam.cenicco.ws.RelacionLaboralPosicion;
import com.lam.cenicco.ws.SaldoVacaciones;
import com.lam.cenicco.ws.SolicitudVacaciones;
import com.lam.cenicco.ws.TipoProceso;
import com.lam.cenicco.ws.Usuario;
import com.mysql.jdbc.StringUtils;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.xml.datatype.XMLGregorianCalendar;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author JoséAntonio
 */
public class IncidenciaCenicco implements ProcesoDAO<Incidencia> {

    private AppBean app = new AppBean();
//    
    private Map<String, RelacionLaboral> mapaRelacionesLaborales;
//    
    private Map<String, Concepto> mapaConceptos;
//    
    protected List<Incidencia> incidencias;
    protected List<Incidencia> selectedIncidencias;
    protected List<Incidencia> filteredIncidencias;
//    
    protected List<Incidencia> incidenciasGral;
//    
    protected List<Incidencia> filteredIncidenciasGral;
//    
    protected List<ResumenTo> resumen;
//    
    protected List<ResumenTo> filteredResumen;
//    
    protected List<String> relaciones;
    protected String[] selectedRelaciones;
//    
    protected String numeroEmpleado;
//    
    protected Integer selectedestatus;
//    
    protected GrupoPago grupoPago;
//    
    protected Periodo periodo;
    protected TipoProceso tipoproceso;
//
    protected Integer selectedGrupoPago;
    protected Integer anio;
    protected Integer selectedPeriodo;
//    
    protected Integer selectedTipoproceso;
//Generar Incidencias Del Monte    
    protected Integer selectedTipoprocesoIncidencias;
    protected Integer selectedPeriodoIncidencias;
    protected Integer selectedAnioIncidencias;
//    
    protected UploadedFile archivo;
//    
    protected boolean descargarPreliminar;
    protected Usuario usuario;
    protected String conceptoVacaciones;
//
    private String fechaInicio;
    private String fechaFin;
    private List<EmpleadoIncidenciaFaltaTO> faltas = new ArrayList<>();
    private List<EmpleadoIncidenciaFaltaTO> selectedFaltas = new ArrayList<>();
    private String notificacion;

    public IncidenciaCenicco() {
        this.descargarPreliminar = ControladorSesiones.getInstance().getUsuarioSession().isPermisoCalculoNomina();
        this.conceptoVacaciones = ControladorWS.getInstance().getConceptoVacaciones();
    }

    @PostConstruct
    @Override
    public void init() {
        this.mapaConceptos = new HashMap<>();
//        
        List<Concepto> conceptos = ControladorWS.getInstance().getConceptosIncidencias();
        for (Concepto c : conceptos) {
            if (this.mapaConceptos.get(c.getConcepto()) == null) {
                this.mapaConceptos.put(c.getConcepto(), c);
            }
        }

        if (this.usuario == null) {
            this.usuario = ControladorSesiones.getInstance().getUsuarioSession();
        }

        this.limpiar(null);
//        
    }

    public void editarIncidencia(Incidencia inc) {
        Usuario usuario = ControladorSesiones.getInstance().getUsuarioSession();
        boolean isValidate = ControladorWS.getInstance().editIncidencia(inc, usuario);
        FacesMessage msg;
        if (isValidate) {
            msg = Util.getFacesMessage(FacesMessage.SEVERITY_INFO, Mensajes.EXITOSO_EDITAR_INCIDENCIA);
        } else {
            msg = Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, Mensajes.ERROR_EDITAR_INCIDENCIA);
        }
        this.generarMsg(msg, isValidate);
    }

    public void descargarLayoutIncidecias(ActionEvent event) {
        List<String> lineas = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        sb.append("CLAVE COMPAÑÍA,").append("NUMERO EMPLEADO,").append("NUMERO CONCEPTO,").append("FECHA (dd/mm/yyyy),").append("TIEMPO,").
                append("IMPORTE,").append("REFERENCIA (OBSERVACIONES)");
        lineas.add(sb.toString());
        Util.escribirFichero(lineas, "LayoutIncidencias", ParametrosReportes.ARCHIVO_CSV);
    }

    public void generarIncidencias() {
        Periodo p = new Periodo();
        TipoProceso tp = ControladorWS.getInstance().findTipoProcesoById(this.selectedTipoprocesoIncidencias);
        p.setIdtipoproceso(tp);
        p.setAnio(this.selectedAnioIncidencias);
        p.setPeriodo(this.selectedPeriodoIncidencias);
        p = ControladorWS.getInstance().findPeriodoByTipoProcesoAndPeriodoAndAnio(p);
        String token = ControladorWS.getInstance().getToken(null, ControladorSesiones.getInstance().getUsuarioSession());
        System.out.println("Token: " + token);
        int i = ControladorWS.getInstance().generarIncidenicas(token, "" + p.getIdperiodo());
        FacesMessage msg = null;
        switch (i) {
            case 200:
                msg = Util.getFacesMessage(FacesMessage.SEVERITY_INFO, "LAS INCIDENCIAS FUERON CREADAS EXITOSAMENTE");
                break;
            case 500:
                msg = Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR INTERNO DEL SERVIDOR, INTENTELO MAS TARDE");
                break;
        }
        this.generarMsg(msg, true);
    }

    @Override
    public void consultar(ActionEvent event) {
        FacesMessage msg;
        String err = "";
        if (this.anio == null) {
            err = "Favor de ingresar el año";
        }
        if (this.selectedPeriodo == null) {
            err = "Favor de ingresar un periodo válido";
        }
        if (this.selectedGrupoPago == null) {
            err = "Favor de seleccionar Grupo Pago";
        }
        if (this.selectedestatus == null) {
            err = "Favor de seleccionar Estatus de Empleados";
        }
        if (err.equals("")) {
            this.incidencias = ControladorWS.getInstance().findIncidencias(this.numeroEmpleado, this.selectedGrupoPago, this.selectedPeriodo, this.anio, this.selectedTipoproceso);
            this.filteredIncidencias = this.incidencias;
        } else {
            msg = Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, err);
            this.generarMsg(msg, false);
        }
    }

    @Override
    public void limpiar(ActionEvent event) {
        this.incidencias = new ArrayList<>();
        this.filteredIncidencias = this.incidencias;
//        
        this.resumen = new ArrayList<>();
        this.filteredResumen = new ArrayList<>();
    }

    public void limpiarAlta(ActionEvent event) {
        this.grupoPago = ControladorWS.getInstance().findGrupoPagoById(this.selectedGrupoPago);
//        
        if (this.selectedTipoproceso != null) {
            this.tipoproceso = ControladorWS.getInstance().findTipoProcesoById(this.selectedTipoproceso);
        } else {
            this.tipoproceso = grupoPago.getIdTipoproceso();
        }
//        
        System.out.println("LimpiarAltaParametros... " + this.anio + " | " + this.selectedPeriodo + " | " + this.tipoproceso.getTipoproceso());
//        
        this.periodo = ControladorWS.getInstance().getPeriodoByAnioPeriodoTipoProceso(this.anio, this.selectedPeriodo, this.tipoproceso.getTipoproceso());
        FacesMessage msg;
//        
        if (this.periodo != null) {
//            
            this.updateEmpleados();
            this.updateIncidencias();
//            
            msg = Util.getFacesMessage(FacesMessage.SEVERITY_INFO, "La información es válida");
//            
        } else {
            msg = Util.getFacesMessage(FacesMessage.SEVERITY_INFO, "El periodo no existe");
        }
//        
        this.generarMsgAlta(msg, FacesMessage.SEVERITY_INFO.equals(msg.getSeverity()));
    }

    public void limpiarAltaArchivo(ActionEvent event) {
        this.grupoPago = ControladorWS.getInstance().findGrupoPagoById(this.selectedGrupoPago);
//        
        if (this.selectedTipoproceso != null) {
            this.tipoproceso = ControladorWS.getInstance().findTipoProcesoById(this.selectedTipoproceso);
        } else {
            this.tipoproceso = this.grupoPago.getIdTipoproceso();
        }
//        
        this.periodo = ControladorWS.getInstance().getPeriodoByAnioPeriodoTipoProceso(this.anio, this.selectedPeriodo, this.tipoproceso.getTipoproceso());
//        
        FacesMessage msg;
//        
        if (this.periodo != null) {
//            
            msg = Util.getFacesMessage(FacesMessage.SEVERITY_INFO, "La información es válida");
//          
            this.mapaRelacionesLaborales = new HashMap<>();
            List<RelacionLaboral> relacionesAux = ControladorWS.getInstance().findRelacionesLaboralesByFiltrosDates(this.selectedGrupoPago, this.selectedestatus, null, null, null, null);
            for (RelacionLaboral rl : relacionesAux) {
                System.out.println("RelacionLaboralArchivo... " + rl.getNumeroempleado());
                if (this.mapaRelacionesLaborales.get(rl.getNumeroempleado()) == null) {
                    this.mapaRelacionesLaborales.put(rl.getNumeroempleado(), rl);
                }
            }

//            
        } else {
            msg = Util.getFacesMessage(FacesMessage.SEVERITY_INFO, "El periodo no existe");
        }
//        
        this.generarMsgAlta(msg, FacesMessage.SEVERITY_INFO.equals(msg.getSeverity()));
    }

    private void updateIncidencias() {
//        
        this.incidenciasGral = new ArrayList<>();
//        
        Iterator<Concepto> iter = this.mapaConceptos.values().iterator();
        while (iter.hasNext()) {
            Incidencia incidenciaAux = new Incidencia();
            incidenciaAux.setIdconcepto(iter.next());
            this.incidenciasGral.add(incidenciaAux);
        }
//        
        this.filteredIncidenciasGral = this.incidenciasGral;
    }

    private void updateEmpleados() {
        this.selectedRelaciones = null;
//        
        this.mapaRelacionesLaborales = new HashMap<>();
//        
        List<RelacionLaboral> relacionesAux = ControladorWS.getInstance().findRelacionesLaboralesByFiltrosDates(this.selectedGrupoPago, this.selectedestatus, null, null, null, null);
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
    }

    private void generarMsgAlta(FacesMessage msg, boolean isValidate) {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().addMessage(null, msg);
        context.addCallbackParam("isValidate", isValidate);
    }

    public void descargarReporteCsv() {
//
        RelacionLaboral rl = this.incidencias.get(0).getIdrellab();
//        
        GrupoPago gp = rl.getIdgrupopago();
        Periodo per = this.incidencias.get(0).getIdperiodo();
//        
        String cadena = "FOLIO,AUTORIZADO,PERIODO,T. PROCESO,EMPLEADO,CONCEPTO,F. REGISTRO,F. INCIDENCIA,UNIDADES,IMPORTE,REFERENCIA\n";
        for (Incidencia i : this.incidencias) {
//            
            Concepto c = i.getIdconcepto();
//            
            String nombreempleado = Util.getNombre(i.getIdrellab().getIdempleado());
//            
            String aurizar = (i.getAutorizar() == BigDecimal.ONE.intValue() ? "Sí" : "No");

            cadena += i.getIdincidencia() + "," + aurizar;
            cadena += "," + per.getPeriodo() + " (" + per.getFechaInicioStr() + " - " + per.getFechaFinStr() + ")";
            cadena += "," + gp.getIdTipoproceso().getNombre() + ",";
            cadena += i.getIdrellab().getNumeroempleado() + " - " + nombreempleado + ",";
            cadena += c.getConcepto() + "-" + c.getNombre() + "," + i.getFActualiza();
            cadena += "," + i.getFAux01() + "," + i.getUnidades() + ",";
            cadena += i.getImporte() + ",";
            cadena += i.getReferencia01() + ",";
            if (i.getIdUsuarioAutorizar() != null) {
                cadena += i.getIdUsuarioAutorizar().getNombre() + " ";
                cadena += i.getIdUsuarioAutorizar().getApellidoPaterno() + "\n";
            } else {
                cadena += " " + "\n";
            }
        }
//        
        String nombreArchivo = "INCIDENCIAS_" + gp.getGrupopago() + "_" + per.getPeriodo() + "_"
                + per.getAnio();
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

    public void calcularresumen(ActionEvent event) {
        Map<String, ResumenTo> mapa = new HashMap<>();
        for (Incidencia i : this.incidencias) {
            String llave = i.getIdconcepto().getIdconcepto().toString();
            if (mapa.get(llave) == null) {
                ResumenTo r = new ResumenTo(i);
                mapa.put(llave, r);
            } else {
                mapa.get(llave).add(i);
            }
        }
        if (!mapa.isEmpty()) {
            this.resumen = new ArrayList<>(mapa.values());
            this.filteredResumen = this.resumen;
        } else {
            this.resumen = new ArrayList<>();
            this.filteredResumen = new ArrayList<>();
        }
    }

    @Override
    public void create(ActionEvent event) {
        FacesMessage msg = this.createIncidencia();
        this.generarMsg(msg, FacesMessage.SEVERITY_INFO.equals(msg.getSeverity()));
    }

    @Override
    public void edit(ActionEvent event) {
    }

    private FacesMessage createIncidencia() {
        if ((this.selectedRelaciones == null) || (this.selectedRelaciones.length == 0)) {
            return Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, Mensajes.ERROR_REGSITROS_VACIOS_SOLICITUD_VACACIONES);
        }
//      
        Usuario usuario = ControladorSesiones.getInstance().getUsuarioSession();
        Integer idtipoproceso;
        if (this.selectedTipoproceso != null) {
            idtipoproceso = this.selectedTipoproceso;
        } else {
            GrupoPago gp = ControladorWS.getInstance().findGrupoPagoById(this.selectedGrupoPago);
            idtipoproceso = gp.getIdTipoproceso().getIdtipoproceso();
        }
//        
        List<Incidencia> aux = new ArrayList<>();
//        
        Date fechaRegistro = new Date();
//        
        for (Incidencia inc : this.incidenciasGral) {
            boolean bandera = false;
            if (inc.getFAux01() != null && !inc.getFAux01().equals("")) {
                bandera = true;
            }
            if (inc.getImporte() != null && inc.getImporte() != 0) {
                bandera = true;
            }
            if (inc.getUnidades() != null) {
                bandera = true;
            }
            inc.setAutorizar(BigDecimal.ONE.intValue());
//          
            if (bandera) {
                for (String llave : this.selectedRelaciones) {
                    Incidencia i = this.clone(inc, this.mapaRelacionesLaborales.get(llave), fechaRegistro, idtipoproceso);
                    aux.add(i);
                }
            }
        }
//        
        boolean isValidate = ControladorWS.getInstance().createIncidencias(aux, usuario);
//        
        FacesMessage msg = isValidate ? Util.getFacesMessage(FacesMessage.SEVERITY_INFO, Mensajes.EXITOSO_CREAR_INCIDENCIAS)
                : Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, Mensajes.ERROR_EDITAR_INCIDENCIAS);
//
        return msg;
    }

    @Override
    public void delete(Incidencia obj) {
        FacesMessage msg = this.validarCamposDeleteIncidencias();
        this.generarMsg(msg, FacesMessage.SEVERITY_INFO.equals(msg.getSeverity()));
    }

    private FacesMessage validarCamposDeleteIncidencias() {
        if (this.selectedIncidencias.isEmpty()) {
            return Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, Mensajes.CAMPO_VACIO_SELECCION_INCIDENCIAS);
        }
//        
        boolean isValidate = true;
        Usuario usuario = ControladorSesiones.getInstance().getUsuarioSession();
//        
        while (this.selectedIncidencias.size() > 0) {
            Integer max = this.selectedIncidencias.size() > 100 ? 100 : this.selectedIncidencias.size();
//            
            List<Incidencia> listTemp = new ArrayList<>(this.selectedIncidencias.subList(0, max));
//            
            isValidate = ControladorWS.getInstance().deleteIncidencias(listTemp, usuario);
//            
            if (!isValidate) {
                break;
            }
            this.selectedIncidencias.removeAll(listTemp);
//
        }
//        
        return isValidate ? Util.getFacesMessage(FacesMessage.SEVERITY_INFO, Mensajes.EXITOSO_ELIMINAR_INCIDENCIAS)
                : Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, Mensajes.ERROR_ELIMINAR_INCIDENCIAS);
    }

    @Override
    public void listenerSelected() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void generarMsg(FacesMessage msg, boolean isValidate) {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().addMessage("", msg);
        context.addCallbackParam("isValidate", isValidate);
        if (isValidate) {
            this.consultar(null);
        }
    }

    public void descargarReporte() {
        this.grupoPago = ControladorWS.getInstance().findGrupoPagoById(this.selectedGrupoPago);
//        
        if (this.selectedTipoproceso != null) {
            this.tipoproceso = ControladorWS.getInstance().findTipoProcesoById(this.selectedTipoproceso);
        } else {
            this.tipoproceso = this.grupoPago.getIdTipoproceso();
        }
//        
        this.periodo = ControladorWS.getInstance().getPeriodoByAnioPeriodoTipoProceso(this.anio, this.selectedPeriodo, this.tipoproceso.getTipoproceso());
//        
        System.out.println("Descarga Reporte Incidencias...");
        RequestContext context = RequestContext.getCurrentInstance();

        String nombreincidencias = ParametrosReportes.MODULO_INCIDENCIAS + "_" + grupoPago.getGrupopago() + "_" + this.periodo.getPeriodo() + "_" + this.periodo.getAnio();
//        
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_INCIDENCIAS);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, nombreincidencias);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.incidencias);
//
        context.addCallbackParam("ruta", MyPaths.urlServletReporte());

    }

    public void fileUploadListener(FileUploadEvent event) {
        this.archivo = event.getFile();
//        
        GrupoPago gp = ControladorWS.getInstance().findGrupoPagoById(this.selectedGrupoPago);
        Usuario usuario = ControladorSesiones.getInstance().getUsuarioSession();
//        
        Integer idtipoproceso;
        if (this.selectedTipoproceso != null) {
            idtipoproceso = this.selectedTipoproceso;
        } else {
            idtipoproceso = gp.getIdTipoproceso().getIdtipoproceso();
        }
//        
        List<String> lista = Util.convertFileToStr(this.archivo);
//        
        List<Incidencia> aux = new ArrayList<>();
        List<Incidencia> auxVacaciones = new ArrayList<>();
//        
        boolean isValidate = true;

//        
        XMLGregorianCalendar fActual = CeniccoUtil.getDateToXmlCalendar(new Date());
//        
        for (String s : lista) {
//            
            String[] parts = s.split(",");
//            
            Incidencia i = new Incidencia();
            i.setUsuario(ControladorSesiones.getInstance().getUsuarioSession());
//            
            System.out.println("Linea.... " + s + " | " + parts.length);
//            
            RelacionLaboral rellab = this.mapaRelacionesLaborales.get(parts[1]);
//            
            System.out.println("Empleado.... " + parts[1]);
//          
            if (rellab == null) {
                isValidate = false;
                break;
            }
//            
            System.out.println("Concepto.... " + parts[2]);
//            
            Concepto concepto = this.mapaConceptos.get(parts[2]);
//            
            if (concepto == null) {
                isValidate = false;
                break;
            }
//            
            i.setIdrellab(rellab);
            i.setIdperiodo(this.periodo);
            i.setIdconcepto(concepto);
            i.setFechaact(fActual);
//            
            try {
                String fechaaux = parts[3];
                System.out.println("Fecha... " + fechaaux);
                XMLGregorianCalendar fInicio = CeniccoUtil.convertStringToXmlGregorian(fechaaux);
                i.setFechaaux01(fInicio);
            } catch (Exception e) {
                System.out.println(e);
            }

            try {
                String tiempoaux = parts[4];
                System.out.println("Tiempo... " + tiempoaux);
                i.setUnidades(Double.parseDouble(tiempoaux));
            } catch (Exception e) {
                System.out.println(e);
            }

            try {
                String importeaux = parts[5];
                System.out.println("Importe.... " + importeaux);
                if (!importeaux.equals("0") && !importeaux.equals("0.0")) {
                    i.setImporte(Double.parseDouble(importeaux));
                }

            } catch (Exception e) {
                System.out.println(e);
            }

            try {
                String referenciaaux = parts[6];
                System.out.println("Referencia.... " + referenciaaux);
                i.setReferencia01(referenciaaux);
            } catch (Exception e) {
                System.out.println(e);
            }
//           
            i.setIdtipoproceso(idtipoproceso);
//          
            i.setAutorizar(BigDecimal.ONE.intValue());
            if (app.isServicioGrupoBakertilly()) {
                if (!conceptoVacaciones.equals(concepto.getConcepto())) {
                    aux.add(i);
                } else {
                    auxVacaciones.add(i);
                }
            } else {
                aux.add(i);
            }
//            
        }
//       
        if (app.isServicioGrupoBakertilly()) {
            List<Incidencia> incidencias = ControladorWS.getInstance().findIncidencias(this.numeroEmpleado, this.grupoPago.getIdgrupopago(), this.periodo.getPeriodo(), this.periodo.getAnio(), this.tipoproceso.getIdtipoproceso());
            Map<String, Double> mapIncVaca = new HashMap<>();
            for (Incidencia i : incidencias) {
                String conceptoIncidencia = i.getIdconcepto().getConcepto();
                if (conceptoVacaciones.equals(conceptoIncidencia)) {
                    String key = i.getIdrellab().getNumeroempleado() + "|" + this.periodo.getPeriodo();
                    if (mapIncVaca.containsKey(key)) {
                        Double unidades = mapIncVaca.get(key);
                        mapIncVaca.put(key, unidades + i.getUnidades());
                    } else {
                        mapIncVaca.put(key, i.getUnidades());
                    }
                }
            }
            Iterator<Incidencia> itr = auxVacaciones.iterator();
            List<Incidencia> tabuladores = new ArrayList<>();
            Double periodicidad = this.tipoproceso.getPeriodicidad().doubleValue();
            while (itr.hasNext()) {
                Incidencia iv = itr.next();
                String key = iv.getIdrellab().getNumeroempleado() + "|" + iv.getIdperiodo().getPeriodo();
                Double unidadesSolicitar = iv.getUnidades();
                Double unidadesSolicitadas = mapIncVaca.containsKey(key) ? mapIncVaca.get(key) : 0.0;
                Double totalUnidades = unidadesSolicitadas + unidadesSolicitar;
                if (totalUnidades > periodicidad) {
                    Double unidadesRestantes = totalUnidades - periodicidad;
                    Double unidadesPeriodo = unidadesSolicitar - unidadesRestantes;
                    iv.setUnidades(unidadesPeriodo);
                    tabuladores.add(iv);
                    if (unidadesRestantes > 0) {
                        Integer periodo = this.periodo.getPeriodo();
                        Integer anio = this.periodo.getAnio();
                        Integer i = 0;
                        periodo++;
                        while (unidadesRestantes != 0) {
                            Incidencia incidencia = new Incidencia();
                            incidencia.setIdrellab(iv.getIdrellab());
                            incidencia.setIdperiodo(iv.getIdperiodo());
                            incidencia.setIdconcepto(iv.getIdconcepto());
                            incidencia.setEstatus(iv.getEstatus());
                            incidencia.setIdtipoproceso(iv.getIdtipoproceso());
                            incidencia.setUnidades(iv.getUnidades());
                            incidencia.setImporte(iv.getImporte());
                            incidencia.setReferencia01(iv.getReferencia01());
                            incidencia.setFechaaux01(iv.getFechaaux01());
                            incidencia.setFechaact(iv.getFechaact());
                            incidencia.setUsuario(iv.getUsuario());
                            incidencia.setAutorizar(iv.getAutorizar());

                            Periodo p = ControladorWS.getInstance().getPeriodoByAnioPeriodoTipoProceso(anio, periodo, this.tipoproceso.getTipoproceso());
                            if (p != null) {
                                key = iv.getIdrellab().getNumeroempleado() + "|" + p.getPeriodo();
                                List<Incidencia> incidenciasPeriodo = ControladorWS.getInstance().findIncidencias(iv.getIdrellab().getNumeroempleado(), this.grupoPago.getIdgrupopago(), p.getPeriodo(), p.getAnio(), this.tipoproceso.getIdtipoproceso());
                                for (Incidencia ip : incidenciasPeriodo) {
                                    String conceptoIncidencia = ip.getIdconcepto().getConcepto();
                                    if (conceptoVacaciones.equals(conceptoIncidencia)) {
                                        if (mapIncVaca.containsKey(key)) {
                                            Double unidades = mapIncVaca.get(key);
                                            mapIncVaca.put(key, unidades + ip.getUnidades());
                                        } else {
                                            mapIncVaca.put(key, ip.getUnidades());
                                        }
                                    }
                                }

                                Double solicitadas = mapIncVaca.containsKey(key) ? mapIncVaca.get(key) : 0.0;
                                Double total = unidadesRestantes + solicitadas;
                                if (total > periodicidad) {
                                    Double diferencia = total - periodicidad;

                                    incidencia.setUnidades(unidadesRestantes - diferencia);
                                    incidencia.setIdperiodo(p);
                                    tabuladores.add(incidencia);

                                    unidadesRestantes = diferencia;
                                    periodo++;
                                } else {
                                    incidencia.setUnidades(unidadesRestantes);
                                    incidencia.setIdperiodo(p);
                                    tabuladores.add(incidencia);

                                    unidadesRestantes = 0.0;
                                }
                            } else {
                                periodo = 1;
                                anio++;
                                i++;
                                if (i == 3) {
                                    break;
                                }
                            }
                        }
                    }
                } else {
                    iv.setUnidades(unidadesSolicitar);
                    tabuladores.add(iv);
                }
            }

            for (Incidencia iv : tabuladores) {
                System.out.println("VAC Empleado: " + iv.getIdrellab().getNumeroempleado()
                        + ", Periodicidad: " + periodicidad
                        + ", Concepto: " + iv.getIdconcepto().getConcepto()
                        + ", Unidades: " + iv.getUnidades()
                        + ", Periodo: " + iv.getIdperiodo().getPeriodo()
                        + ", Anio: " + iv.getIdperiodo().getAnio());
            }
            aux.addAll(tabuladores);
        }
//        
        while (aux.size() > 0) {
            Integer max = aux.size() > 100 ? 100 : aux.size();
//            
            List<Incidencia> listTemp = new ArrayList<>(aux.subList(0, max));
//            
            isValidate = ControladorWS.getInstance().createIncidencias(listTemp, usuario);
//            
            if (!isValidate) {
                break;
            }
//            
            aux.removeAll(listTemp);
        }

//        
        FacesMessage msg = isValidate ? Util.getFacesMessage(FacesMessage.SEVERITY_INFO, Mensajes.EXITOSO_CREAR_INCIDENCIAS)
                : Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, Mensajes.ERROR_EDITAR_INCIDENCIAS);
//        
        this.generarMsg(msg, FacesMessage.SEVERITY_INFO.equals(msg.getSeverity()));
//        
        System.out.println("Se crearon las incidencias... " + isValidate);
//        
    }

    public void descargarPreliminar() {
//        
        GrupoPago gp = ControladorWS.getInstance().findGrupoPagoById(this.selectedGrupoPago);
        ControladorWS.getInstance().calcularNominaXIncidencias(this.mapaRelacionesLaborales, gp, this.periodo);
//        
        String nombrearchivo = gp.getGrupopago()
                + "_" + this.periodo.getPeriodo().toString() + "_" + this.periodo.getAnio().toString();
//        
        String preliminar = ControladorWS.getInstance().getPathNomina() + nombrearchivo;
        try {
            Path path = Paths.get(preliminar);
            byte[] data = Files.readAllBytes(path);
            RequestContext context = RequestContext.getCurrentInstance();
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, nombrearchivo);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, data);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_CSV);
            context.addCallbackParam("ruta", MyPaths.urlServletDescargarArchivoCsv());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getInformacion() {
        return CeniccoUtil.getInformacion(this.incidencias.size());
    }

    public String getInformacionResumen() {
        return CeniccoUtil.getInformacion(this.resumen.size());
    }

    public String getNumeroEmpleado() {
        return numeroEmpleado;
    }

    public void setNumeroEmpleado(String numeroEmpleado) {
        this.numeroEmpleado = numeroEmpleado;
    }

    public Integer getSelectedGrupoPago() {
        return selectedGrupoPago;
    }

    public void setSelectedGrupoPago(Integer selectedGrupoPago) {
        this.selectedGrupoPago = selectedGrupoPago;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public Integer getSelectedPeriodo() {
        return selectedPeriodo;
    }

    public void setSelectedPeriodo(Integer selectedPeriodo) {
        this.selectedPeriodo = selectedPeriodo;
    }

    public List<Incidencia> getFilteredIncidencias() {
        return filteredIncidencias;
    }

    public void setFilteredIncidencias(List<Incidencia> filteredIncidencias) {
        this.filteredIncidencias = filteredIncidencias;
    }

    public List<Incidencia> getIncidencias() {
        return incidencias;
    }

    public Periodo getPeriodo() {
        return periodo;
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

    public List<Incidencia> getFilteredIncidenciasGral() {
        return filteredIncidenciasGral;
    }

    public void setFilteredIncidenciasGral(List<Incidencia> filteredIncidenciasGral) {
        this.filteredIncidenciasGral = filteredIncidenciasGral;
    }

    public List<Incidencia> getIncidenciasGral() {
        return incidenciasGral;
    }

    public List<Incidencia> getSelectedIncidencias() {
        return selectedIncidencias;
    }

    public void setSelectedIncidencias(List<Incidencia> selectedIncidencias) {
        this.selectedIncidencias = selectedIncidencias;
    }

    public UploadedFile getArchivo() {
        return archivo;
    }

    public void setArchivo(UploadedFile archivo) {
        this.archivo = archivo;
    }

    public Integer getSelectedTipoproceso() {
        return selectedTipoproceso;
    }

    public void setSelectedTipoproceso(Integer selectedTipoproceso) {
        this.selectedTipoproceso = selectedTipoproceso;
    }

    private Incidencia clone(Incidencia inc, RelacionLaboral rl, Date fechaRegistro, Integer idtipoproceso) {
        Incidencia i = new Incidencia();
//
        XMLGregorianCalendar fAux1 = CeniccoUtil.convertStringToXmlGregorian(inc.getFAux01());
        XMLGregorianCalendar fRegistro = CeniccoUtil.getDateToXmlCalendar(fechaRegistro);
        String referencia = inc.getReferencia01() != null ? inc.getReferencia01().trim().toUpperCase() : null;
//        
        i.setIdrellab(rl);
        i.setIdperiodo(this.periodo);
        i.setIdconcepto(inc.getIdconcepto());
        i.setEstatus("01");
//        
        i.setIdtipoproceso(idtipoproceso);
//
        i.setUnidades(inc.getUnidades());
        i.setImporte(inc.getImporte());
        i.setReferencia01(referencia);

        i.setFechaaux01(fAux1);
        i.setFechaact(fRegistro);
        i.setUsuario(ControladorSesiones.getInstance().getUsuarioSession());
        i.setAutorizar(inc.getAutorizar());
//        
        return i;
    }

    public boolean isDescargarPreliminar() {
        return descargarPreliminar;
    }

    public GrupoPago getGrupoPago() {
        return grupoPago;
    }

    public TipoProceso getTipoproceso() {
        return tipoproceso;
    }

    public List<ResumenTo> getFilteredResumen() {
        return filteredResumen;
    }

    public void setFilteredResumen(List<ResumenTo> filteredResumen) {
        this.filteredResumen = filteredResumen;
    }

    public List<ResumenTo> getResumen() {
        return resumen;
    }

    public Integer getSelectedestatus() {
        return selectedestatus;
    }

    public void setSelectedestatus(Integer selectedestatus) {
        this.selectedestatus = selectedestatus;
    }

    public Integer getSelectedPeriodoIncidencias() {
        return selectedPeriodoIncidencias;
    }

    public void setSelectedPeriodoIncidencias(Integer selectedPeriodoIncidencias) {
        this.selectedPeriodoIncidencias = selectedPeriodoIncidencias;
    }

    public Integer getSelectedAnioIncidencias() {
        return selectedAnioIncidencias;
    }

    public void setSelectedAnioIncidencias(Integer selectedAnioIncidencias) {
        this.selectedAnioIncidencias = selectedAnioIncidencias;
    }

    public Integer getSelectedTipoprocesoIncidencias() {
        return selectedTipoprocesoIncidencias;
    }

    public void setSelectedTipoprocesoIncidencias(Integer selectedTipoprocesoIncidencias) {
        this.selectedTipoprocesoIncidencias = selectedTipoprocesoIncidencias;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getConceptoVacaciones() {
        return conceptoVacaciones;
    }

    public void setConceptoVacaciones(String conceptoVacaciones) {
        this.conceptoVacaciones = conceptoVacaciones;
    }

    public class ResumenTo {

        private String descripcion;
        private Double tiempo;
        private Double importe;

        public ResumenTo(Incidencia i) {
            this.descripcion = i.getIdconcepto().getConcepto() + " - " + i.getIdconcepto().getNombre();
            this.tiempo = (i.getUnidades() != null ? i.getUnidades() : 0.0);
            this.importe = (i.getImporte() != null ? i.getImporte() : 0.0);
        }

        public void add(Incidencia i) {
            this.tiempo += (i.getUnidades() != null ? i.getUnidades() : 0.0);
            this.importe += (i.getImporte() != null ? i.getImporte() : 0.0);
        }

        public String getDescripcion() {
            return descripcion;
        }

        public void setDescripcion(String descripcion) {
            this.descripcion = descripcion;
        }

        public Double getTiempo() {
            return tiempo;
        }

        public void setTiempo(Double tiempo) {
            this.tiempo = tiempo;
        }

        public Double getImporte() {
            return importe;
        }

        public void setImporte(Double importe) {
            this.importe = importe;
        }
    }

    public void autorizarIncidencias() {
        Parametro parametro = ControladorWS.getInstance().getParametro("ACTIVAR_GENERADOR_VACACIONES");
        boolean isCheckVacaciones = (parametro != null && "S".equals(parametro.getValor()));

        FacesMessage msg = Util.getFacesMessage(FacesMessage.SEVERITY_INFO, "Las incidencias fueron autorizadas exitosamente!");
        if (this.selectedIncidencias.isEmpty()) {
            msg = Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, "Favor de seleccionar la(s) Incidencia(s) para autorizar");
        } else {
            Map<String, List<Incidencia>> vacaciones = new HashMap<>();
            for (Incidencia i : this.selectedIncidencias) {
                if (i.getAutorizar() == BigDecimal.TEN.intValue()) {
                    i.setAutorizar(BigDecimal.ONE.intValue());
                    i.setIdUsuarioAutorizar(this.usuario);
                    ControladorWS.getInstance().editIncidencia(i, this.usuario);
                }
                if (i.getReferencia02() != null) {
                    if (conceptoVacaciones.equals(i.getIdconcepto().getConcepto())) {
                        String key = i.getIdrellab().getNumeroempleado();
                        System.out.println("Numero Empleado: " + key + " Incidencia Vacaciones ID: " + i.getIdincidencia());
                        if (vacaciones.get(key) == null) {
                            List<Incidencia> listVacaciones = new ArrayList<>();
                            listVacaciones.add(i);
                            vacaciones.put(key, listVacaciones);
                        } else {
                            List<Incidencia> listVacaciones = vacaciones.get(key);
                            listVacaciones.add(i);
                            vacaciones.put(key, listVacaciones);
                        }
                    }
                }
            }

            if (isCheckVacaciones) {
                for (Map.Entry<String, List<Incidencia>> mapa : vacaciones.entrySet()) {
                    String numeroEmpleado = mapa.getKey();
                    Integer saldoSolicitado = 0;
                    for (Incidencia i : mapa.getValue()) {
                        saldoSolicitado += i.getUnidades().intValue();
                    }

                    System.out.println("Numero Empleado: " + numeroEmpleado + " Vacaciones Saldo Solicitado: " + saldoSolicitado);
                    String estatus = Util.join(",", Arrays.asList(new String[]{"1", "3"}));
                    List<SaldoVacaciones> saldoVacaciones = ControladorWS.getInstance().getSaldoVacaciones(numeroEmpleado, null, estatus, conceptoVacaciones);
                    for (SaldoVacaciones sv : saldoVacaciones) {
                        if (saldoSolicitado <= 0) {
                            break;
                        }
                        Integer saldo = sv.getSaldo().intValue();
                        Integer disfrutadas = sv.getDisfrutadas();
                        System.out.println(sv.getIdsaldo() + " Numero Empleado: " + numeroEmpleado + " SV Saldo: " + saldo);
                        System.out.println(sv.getIdsaldo() + " Numero Empleado: " + numeroEmpleado + " SV Saldo Disfrutadas: " + disfrutadas);
                        if (saldo >= saldoSolicitado) {
                            Integer saldoFinal = saldo - saldoSolicitado;
                            sv.setSaldo(saldoFinal.doubleValue());
                            sv.setDisfrutadas(disfrutadas + saldoSolicitado);
                            saldoSolicitado = 0;
                        } else {
                            saldoSolicitado -= saldo;

                            sv.setSaldo(0.0);
                            sv.setDisfrutadas(disfrutadas + saldo);
                        }
                    }

                    System.out.println("Numero Empleado: " + numeroEmpleado + " Vacaciones Saldo Pendiente: " + saldoSolicitado);
                    if (saldoSolicitado == 0) {
                        ControladorWS.getInstance().editSaldosVacaciones(saldoVacaciones);
                    } else {
                        for (Incidencia i : mapa.getValue()) {
                            i.setAutorizar(BigDecimal.TEN.intValue());
                            i.setIdUsuarioAutorizar(null);
                            ControladorWS.getInstance().editIncidencia(i, this.usuario);
                        }
                        msg = Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, "Revisar incidencias que no se autorizaron.");
                    }
                }
            }
        }

        this.generarMsg(msg, FacesMessage.SEVERITY_INFO.equals(msg.getSeverity()));
    }

    public String getInformacionAux() {
        return CeniccoUtil.getInformacion(this.faltas.size());
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

    public List<EmpleadoIncidenciaFaltaTO> getFaltas() {
        return faltas;
    }

    public void setFaltas(List<EmpleadoIncidenciaFaltaTO> faltas) {
        this.faltas = faltas;
    }

    public List<EmpleadoIncidenciaFaltaTO> getSelectedFaltas() {
        return selectedFaltas;
    }

    public void setSelectedFaltas(List<EmpleadoIncidenciaFaltaTO> selectedFaltas) {
        this.selectedFaltas = selectedFaltas;
    }

    public String getNotificacion() {
        return notificacion;
    }

    public void setNotificacion(String notificacion) {
        this.notificacion = notificacion;
    }

    public void generarFaltasAsistencias() {
        FacesMessage msg = Util.getFacesMessage(FacesMessage.SEVERITY_INFO, "Las faltas fueron generadas exitosamente!");
        faltas = new ArrayList<>();
        notificacion = null;
        TreeMap<String, List<Date>> asistencias = new TreeMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat formatoDiaFeriado = new SimpleDateFormat("yyyy-MM-dd");
        boolean isValidate = true;

        Date fechaInicio = null;
        Date fechaFin = null;
        try {
            fechaInicio = sdf.parse(this.fechaInicio);
            fechaFin = sdf.parse(this.fechaFin);
        } catch (ParseException ex) {
            isValidate = false;
            msg = Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, "Fechas ingresadas no validas");
        }

        Map<String, List<SolicitudVacaciones>> mapHomeOfficeVacaciones = ControladorWS.getInstance().getSolicitudesHomeOfficeByIdRellabAndIdSaldoVacaciones(fechaInicio, fechaFin);
        Map<Integer, List<HorarioLaboral>> mapHorariosNoLaborales = ControladorWS.getInstance().getHorariosNoLaboralesByIdTurnoLaboral();
        if (isValidate) {
            Parametro path = ControladorWS.getInstance().getParametro(Parametros.ALPHA_PATH_REPORTE_ASISTENCIAS_TERMINAL);
            Parametro fechasDescanso = ControladorWS.getInstance().getParametro(Parametros.FECHAS_DESCANSO);
            if (path != null && !StringUtils.isNullOrEmpty(path.getValor())) {
                List<String> empleadosInvalidos = new ArrayList<>();
                List<RelacionLaboral> relaciones = ControladorWS.getInstance().findRelacionesLaboralesActivas();
                Map<String, RelacionLaboral> empleados = new HashMap<>();
                for (RelacionLaboral r : relaciones) {
                    RelacionLaboralPosicion rlp = r.getIdrelacionlaboralposicion();
                    if (rlp.getIdModalidadTrabajo() == null || rlp.getTurnoLaboral() == null) {
                        empleadosInvalidos.add(r.getNumeroempleado());
                    } else if (rlp.getIdModalidadTrabajo() == 1 || rlp.getIdModalidadTrabajo() == 3) {
                        empleados.put(r.getNumeroempleado(), r);
                    }
                }
                if (empleadosInvalidos.isEmpty()) {
                    File file = new File(path.getValor());
                    if (file.exists()) {
                        try {
                            FileInputStream fis = new FileInputStream(file);
                            List<String> marcajes = Util.convertFileToStr(fis);
                            for (String data : marcajes) {
                                String[] row = data.split(",");
                                String numeroEmpleado = row[0];
                                String nombreEmpleado = row[1];
                                String fechaIngreso = row[2];

                                if (!numeroEmpleado.equals("ID")) {
                                    //System.out.println(numeroEmpleado + "\t\t" + nombreEmpleado + "\t\t" + fechaIngreso + "\t\t");
                                    List<Date> fechas = new ArrayList<>();
                                    Date fecha = sdf.parse(fechaIngreso);
                                    if (asistencias.containsKey(numeroEmpleado)) {
                                        fechas = asistencias.get(numeroEmpleado);
                                        if (!fechas.contains(fecha)) {
                                            fechas.add(fecha);
                                            asistencias.put(numeroEmpleado, fechas);
                                        }
                                    } else {
                                        fechas.add(fecha);
                                        asistencias.put(numeroEmpleado, fechas);
                                    }
                                }
                            }

                            /*HSSFWorkbook wb = new HSSFWorkbook(fis);
                             HSSFSheet sheet = wb.getSheetAt(0);
                             for (Row row : sheet) {
                             Cell numeroEmpleado = row.getCell(0);
                             Cell nombreEmpleado = row.getCell(1);
                             Cell fechaIngreso = row.getCell(2);

                             if (!numeroEmpleado.getStringCellValue().equals("ID")) {
                             System.out.println(numeroEmpleado + "\t\t" + nombreEmpleado + "\t\t" + fechaIngreso + "\t\t");
                             List<Date> fechas = new ArrayList<>();
                             Date fecha = sdf.parse(fechaIngreso.getStringCellValue());
                             if (asistencias.containsKey(numeroEmpleado.getStringCellValue())) {
                             fechas = asistencias.get(numeroEmpleado.getStringCellValue());
                             if (!fechas.contains(fecha)) {
                             fechas.add(fecha);
                             asistencias.put(numeroEmpleado.getStringCellValue(), fechas);
                             }
                             } else {
                             fechas.add(fecha);
                             asistencias.put(numeroEmpleado.getStringCellValue(), fechas);
                             }
                             }
                             }*/
                        } catch (Exception ex) {
                            Logger.getLogger(IncidenciaCenicco.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        if (!empleados.isEmpty()) {
                            for (Map.Entry<String, RelacionLaboral> entry : empleados.entrySet()) {
                                String numeroEmpleado = entry.getKey();
                                RelacionLaboral rellab = entry.getValue();
                                RelacionLaboralPosicion rlp = rellab.getIdrelacionlaboralposicion();
                                //System.out.println("Numero empleado: " + rellab.getNumeroempleado());
                                List<SolicitudVacaciones> homeOfficeVacaciones = new ArrayList<>();
                                if (mapHomeOfficeVacaciones.containsKey(rellab.getNumeroempleado())) {
                                    homeOfficeVacaciones = mapHomeOfficeVacaciones.get(rellab.getNumeroempleado());
                                }
                                /*for (SolicitudVacaciones h : homeOfficeVacaciones) {
                                    System.out.println(rellab.getNumeroempleado() + " - HO | V :: " + sdf.format(h.getFechainicio().toGregorianCalendar().getTime()));
                                }*/
                                List<HorarioLaboral> descansos = new ArrayList<>();
                                if (mapHorariosNoLaborales.containsKey(rlp.getTurnoLaboral().getIdTunoLaboral())) {
                                    descansos = mapHorariosNoLaborales.get(rlp.getTurnoLaboral().getIdTunoLaboral());
                                }
                                /*for (HorarioLaboral d : descansos) {
                                    System.out.println(rellab.getNumeroempleado() + " - Descansos :: " + d.getDia() + " | " + d.getNumeroDia());
                                }*/
                                List<Date> asistenciasOficina = new ArrayList<>();
                                if (asistencias.containsKey(numeroEmpleado)) {
                                    asistenciasOficina = asistencias.get(numeroEmpleado);
                                    /*for (Date aof : asistenciasOficina) {
                                     System.out.println("Asistencias :: " + sdf.format(aof));
                                     }*/
                                }
                                List<Date> diasFeriados = new ArrayList<>();
                                if (fechasDescanso != null && !StringUtils.isNullOrEmpty(fechasDescanso.getValor())) {
                                    List<String> fechas = Arrays.asList(fechasDescanso.getValor().split(","));
                                    for (String diaF : fechas) {
                                        Date fechaFeriado = null;
                                        try {
                                            fechaFeriado = formatoDiaFeriado.parse(diaF);
                                            diasFeriados.add(fechaFeriado);
                                        } catch (ParseException ex) {
                                            System.err.println(ex.getMessage());
                                        }
                                    }
                                }

                                List<Date> rangoFechas = new ArrayList<>();
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTime(fechaInicio);
                                calendar.add(Calendar.DAY_OF_MONTH, -1);
                                while (calendar.getTime().before(fechaFin)) {
                                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                                    int diaSemana = (calendar.get(Calendar.DAY_OF_WEEK) - 1);
                                    boolean isDescanso = false;
                                    boolean isHomeOffice = false;
                                    boolean isAsistenciaOficina = false;
                                    boolean isFeriado = false;
                                    if (!descansos.isEmpty()) {
                                        for (HorarioLaboral d : descansos) {
                                            int dia = d.getNumeroDia() == 7 ? 0 : d.getNumeroDia();
                                            if (dia == diaSemana) {
                                                isDescanso = true;
                                            }
                                        }
                                    }

                                    if (!homeOfficeVacaciones.isEmpty()) {
                                        for (SolicitudVacaciones hv : homeOfficeVacaciones) {
                                            List<Date> fechasHOVacaciones = new ArrayList<>();
                                            Calendar calendarHOVInicio = Calendar.getInstance();
                                            calendarHOVInicio.setTime(hv.getFechainicio().toGregorianCalendar().getTime());
                                            Date fechaHOVInicio = calendarHOVInicio.getTime();

                                            Calendar calendarHOVFin = Calendar.getInstance();
                                            calendarHOVFin.setTime(hv.getFechafin().toGregorianCalendar().getTime());
                                            Date fechaHOVFin = calendarHOVFin.getTime();

                                            Calendar calendarHOV = Calendar.getInstance();
                                            calendarHOV.setTime(fechaHOVInicio);
                                            calendarHOV.add(Calendar.DAY_OF_MONTH, -1);
                                            while (calendarHOV.getTime().before(fechaHOVFin)) {
                                                calendarHOV.add(Calendar.DAY_OF_MONTH, 1);
                                                fechasHOVacaciones.add(calendarHOV.getTime());
                                            }

                                            for (Date d : fechasHOVacaciones) {
                                                if (d.equals(calendar.getTime())) {
                                                    isHomeOffice = true;
                                                }
                                            }
                                        }
                                    }

                                    if (!asistenciasOficina.isEmpty()) {
                                        for (Date aof : asistenciasOficina) {
                                            if (aof.equals(calendar.getTime())) {
                                                isAsistenciaOficina = true;
                                            }
                                        }
                                    }

                                    if (!diasFeriados.isEmpty()) {
                                        for (Date df : diasFeriados) {
                                            if (df.equals(calendar.getTime())) {
                                                isFeriado = true;
                                            }
                                        }
                                    }

                                    if (!isDescanso && !isHomeOffice && !isAsistenciaOficina && !isFeriado) {
                                        rangoFechas.add(calendar.getTime());
                                    }
                                }

                                if (!rangoFechas.isEmpty()) {
                                    EmpleadoIncidenciaFaltaTO to = new EmpleadoIncidenciaFaltaTO();
                                    to.setRellab(rellab);
                                    to.setFechas(rangoFechas);
                                    StringBuilder sb = new StringBuilder();
                                    for (int i = 0; i < rangoFechas.size(); i++) {
                                        //System.out.println("Fechas Faltas :: " + rangoFechas.get(i));
                                        sb.append(sdf.format(rangoFechas.get(i)));

                                        if (i != rangoFechas.size() - 1) {
                                            sb.append(", ");
                                        }
                                    }
                                    to.setFaltas(sb.toString());
                                    faltas.add(to);
                                }
                            }
                        } else {
                            msg = Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, "No existe asistencias para procesar");
                        }
                    } else {
                        msg = Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, "No existe el archivo en el directorio especificado");
                    }
                } else {
                    notificacion = "Los siguientes empleados les falta modalidad de trabajo o turno laboral: " + Util.join(", ", empleadosInvalidos);
                }
            } else {
                msg = Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, "No esta configurado el directorio para las asistencias");
            }
        }
        RequestContext context = RequestContext.getCurrentInstance();
        context.update("formGenerarFaltas");
        this.generarMsg(msg, FacesMessage.SEVERITY_INFO.equals(msg.getSeverity()));
    }

    public void generarIncidenciasFaltas() {
        FacesMessage msg = Util.getFacesMessage(FacesMessage.SEVERITY_INFO, "Las incidencias fueron creadas exitosamente!");
        if (this.selectedTipoproceso != null && this.selectedPeriodo != null && this.anio != null) {
            List<Incidencia> incidenciasFaltas = new ArrayList<>();
            TipoProceso tipoProceso = ControladorWS.getInstance().findTipoProcesoById(this.selectedTipoproceso);
            Concepto conceptoFalta = ControladorWS.getInstance().findConceptoByConcepto("2110");
            Periodo periodoFalta = ControladorWS.getInstance().getPeriodoByAnioPeriodoTipoProceso(this.anio, this.selectedPeriodo, tipoProceso.getTipoproceso());
            if (!this.selectedFaltas.isEmpty()) {
                for (EmpleadoIncidenciaFaltaTO f : this.selectedFaltas) {
                    XMLGregorianCalendar fechaAux1 = CeniccoUtil.getDateToXmlCalendar(new Date());
                    XMLGregorianCalendar fechaRegistro = CeniccoUtil.getDateToXmlCalendar(new Date());
                    Incidencia i = new Incidencia();
                    i.setIdrellab(f.getRellab());
                    i.setIdperiodo(periodoFalta);
                    i.setIdconcepto(conceptoFalta);
                    i.setEstatus("01");
                    i.setIdtipoproceso(tipoProceso.getIdtipoproceso());
                    i.setUnidades(new Double(f.getFechas().size()));
                    i.setImporte(null);
                    i.setReferencia01("Falta sincronizada desde la terminal - " + f.getFaltas());
                    i.setFechaaux01(fechaAux1);
                    i.setFechaact(fechaRegistro);
                    i.setUsuario(usuario);
                    i.setAutorizar(BigDecimal.TEN.intValue());
                    incidenciasFaltas.add(i);
                }

                if (!incidenciasFaltas.isEmpty()) {
                    boolean isValidate = ControladorWS.getInstance().createIncidencias(incidenciasFaltas, usuario);
                    if (!isValidate) {
                        msg = Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, "Las incidencias no fueron procesadas correctamente!");
                    }
                }
            } else {
                msg = Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, "No hay faltas seleccionadas!");
            }
        } else {
            msg = Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, "Falta ingresar tipo de proceso o periodo o anio!");
        }
        this.generarMsg(msg, FacesMessage.SEVERITY_INFO.equals(msg.getSeverity()));
    }
}
