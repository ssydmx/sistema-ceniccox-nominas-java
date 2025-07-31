/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lam.cenicco.beans.nominas.timbres.reportes;

import com.app.appfacturainteligente.RespuestaCancelacion;
import com.app.appfacturainteligente.RespuestaTFD33;
import com.lam.cenicco.beans.facturainteligente.FacturaIntegracion;
import com.lam.cenicco.dao.ProcesoDAO;
import com.lam.cenicco.impl.ControladorWS;
import com.lam.cenicco.librerias.utilidades.CeniccoUtil;
import com.lam.cenicco.mail.ManejadorCorreo;
import com.lam.cenicco.util.ParametrosReportes;
import com.lam.cenicco.util.Util;
import com.lam.cenicco.util.paths.MyPaths;
import com.lam.cenicco.ws.Compania;
import com.lam.cenicco.ws.GrupoPago;
import com.lam.cenicco.ws.Parametro;
import com.lam.cenicco.ws.Periodo;
import com.lam.cenicco.ws.ReciboNomina;
import com.lam.cenicco.ws.RelacionLaboral;
import com.lam.cenicco.ws.TipoProceso;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import org.primefaces.context.RequestContext;

/**
 *
 * @author JoséAntonio
 */
public class ReporteTimbreCenicco implements ProcesoDAO<ReciboNomina> {

    protected List<ReciboNomina> recibos;
//    
    protected List<ReciboNomina> filteredRecibos;
//    
    protected List<ReciboNomina> selectedRecibos;
//    
    protected ReciboNomina recibo;
//    
    protected List<RespuestaCancelacion> respuesta;
//    
    protected List<ReciboNomina> recibosErrores;
//    
    private static final int TOPE_RECIBOS = 1000000000;
//    
    protected String uuid;
//  
    //Variables para cancelacion por UUID
    protected String numeroempleado;
    protected int idCompania;
    protected TipoProceso tipoProcesoCancelacion;
    protected Periodo periodoCancelacion;

    public ReporteTimbreCenicco() {
        if (this.recibo == null) {
            this.recibo = new ReciboNomina();
        }
        if (this.recibos == null) {
            this.recibos = new ArrayList<>();
        }
        if (this.respuesta == null) {
            this.respuesta = new ArrayList<>();
        }
    }

    @PostConstruct
    @Override
    public void init() {
//        consultar(null);
    }

    @Override
    public void consultar(ActionEvent event) {
        this.recibos = ControladorWS.getInstance().findRecibosNominas(this.recibo);
        this.filteredRecibos = this.recibos;

        this.selectedRecibos = new ArrayList<>();
    }

    public void consultarxmes(ActionEvent event) {
        this.recibos = ControladorWS.getInstance().findRecibosNominasxmes(this.recibo);
        this.filteredRecibos = this.recibos;

        this.selectedRecibos = new ArrayList<>();
    }

    public void descargarDocumentoPDF(ReciboNomina r) {
        Parametro rgn = ControladorWS.getInstance().getParametro("ACTIVAR_RECIBO_GENERICO_NOMINA");
        boolean isCheckReciboGenericoNomina = (rgn != null && "S".equals(rgn.getValor()));
        String nombreArchivo = r.getIdRellab().getIdempleado().getRfc();
        if (isCheckReciboGenericoNomina) {
            nombreArchivo = r.getIdRellab().getNumeroempleado() + "_" + r.getIdRellab().getIdempleado().getRfc() + "_" + r.getIdPeriodo().getPeriodo() + "_" + r.getUuid();
        }

        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, nombreArchivo);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_PDF);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, r.getArchivoPdf());
        context.addCallbackParam("ruta", MyPaths.urlServletDescargarRecibo());
    }

    public void descargarDocumentoXML(ReciboNomina r) {
        Parametro rgn = ControladorWS.getInstance().getParametro("ACTIVAR_RECIBO_GENERICO_NOMINA");
        boolean isCheckReciboGenericoNomina = (rgn != null && "S".equals(rgn.getValor()));
        String nombreArchivo = r.getIdRellab().getIdempleado().getRfc();
        if (isCheckReciboGenericoNomina) {
            nombreArchivo = r.getIdRellab().getNumeroempleado() + "_" + r.getIdRellab().getIdempleado().getRfc() + "_" + r.getIdPeriodo().getPeriodo() + "_" + r.getUuid();
        }

        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, nombreArchivo);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_XML);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, r.getArchivoXml());
        context.addCallbackParam("ruta", MyPaths.urlServletDescargarRecibo());
    }

    public void descargarDocumentoXMLCancelacion(ReciboNomina r) {
        if (r.getXmlAcuseCancelacion() == null) {
            FacturaIntegracion facturaintegracion = new FacturaIntegracion();
            RespuestaTFD33 cancelacion = facturaintegracion.obtenerAcuseCancelacion(r.getUuid());
            if (cancelacion.isOperacionExitosa()) {
                System.out.println("RecuperoXmlCancelacion... " + cancelacion.getXMLResultado().getValue());
                String xmlCancelacion = cancelacion.getXMLResultado().getValue();
                byte[] xml = xmlCancelacion.getBytes(StandardCharsets.UTF_8);

                RequestContext context = RequestContext.getCurrentInstance();
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, r.getIdRellab().getIdempleado().getRfc());
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_XML);
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, xml);
                context.addCallbackParam("ruta", MyPaths.urlServletDescargarRecibo());
            } else {
                System.out.println(cancelacion.getCodigoRespuesta().getValue() + " ... NoSeRecuperoXmlCancelacion... " + cancelacion.getMensajeError().getValue() + " ... " + cancelacion.getMensajeErrorDetallado().getValue());
                RequestContext context = RequestContext.getCurrentInstance();
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, r.getIdRellab().getIdempleado().getRfc());
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_XML);
                context.addCallbackParam("ruta", MyPaths.urlServletDescargarRecibo());
                context.addCallbackParam("isValidate", true);

                FacesMessage msg = Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, "Error al recuperar el xml de cancelacion.");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        } else {
            RequestContext context = RequestContext.getCurrentInstance();
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, r.getIdRellab().getIdempleado().getRfc());
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_XML);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, r.getXmlAcuseCancelacion());
            context.addCallbackParam("ruta", MyPaths.urlServletDescargarRecibo());
        }
    }

    public void descargarDocumentoPng(ReciboNomina r) {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, r.getIdRellab().getIdempleado().getRfc());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_PNG);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, r.getTimbre());
        context.addCallbackParam("ruta", MyPaths.urlServletDescargarRecibo());
    }

    public void descargarReporte() {
        System.out.println("Descarga Reporte...");
        RequestContext context = RequestContext.getCurrentInstance();
//        
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_REPORTE_TIMBRE);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, ParametrosReportes.MODULO_REPORTE_TIMBRE);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.recibos);
//
        context.addCallbackParam("ruta", MyPaths.urlServletReporte());

    }

    public void enviarCorreoMasivo() {
        Iterator<ReciboNomina> iter = this.selectedRecibos.iterator();
        Map<String, String> mapamails = ControladorWS.getInstance().getMails();
//        
        FacesMessage msg;
        String detalle = ControladorWS.getInstance().getMailDetalle();
        int contenviados = 0;
        int contnoenviados = 0;
        while (iter.hasNext()) {
            ReciboNomina r = iter.next();
            if (mapamails.get(r.getIdRellab().getIdempleado().getIdempleado().toString()) == null) {
                continue;
            }
            String mail = mapamails.get(r.getIdRellab().getIdempleado().getIdempleado().toString());
            boolean isValidate = new ManejadorCorreo(r, mail).enviarCorreoReciboNomina(detalle);
            if (isValidate) {
                contenviados++;
                System.out.println("EnvioReciboNomina... " + contenviados + " | " + r.getIdRellab().getNumeroempleado() + " | " + mail);
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "El Mail fué enviado exitosamente", null);
            } else {
                contnoenviados++;
                System.out.println("NoSeEnvioReciboNomina... " + r.getIdRellab().getNumeroempleado() + " | " + mail);
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al enviar el Mail", null);
            }
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
        System.out.println("TotalEnviados | NoEnviados... " + contenviados + " | " + contnoenviados);
    }

    public void enviarCorreo(ReciboNomina r) {
        Map<String, String> mapamails = ControladorWS.getInstance().getMails();
//        
        FacesMessage msg;
        String detalle = ControladorWS.getInstance().getMailDetalle();
//        
        if (mapamails.get(r.getIdRellab().getIdempleado().getIdempleado().toString()) == null) {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "No tiene asignado Mail", null);
        } else {
            String mail = mapamails.get(r.getIdRellab().getIdempleado().getIdempleado().toString());
            boolean isValidate = new ManejadorCorreo(r, mail).enviarCorreoReciboNomina(detalle);
            if (isValidate) {
                System.out.println("EnvioReciboNomina... " + r.getIdRellab().getNumeroempleado() + " | " + mail);
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "El Mail fué enviado exitosamente", mail);
            } else {
                System.out.println("NoSeEnvioReciboNomina... " + r.getIdRellab().getNumeroempleado() + " | " + mail);
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al enviar el Mail", mail);
            }
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    @Override
    public void limpiar(ActionEvent event) {
//        
        List<ReciboNomina> reciboscancelados = new ArrayList<>();
        Compania cia = this.selectedRecibos.get(0).getIdRellab().getIdcompania();
//        
        FacturaIntegracion facturaintegracion = new FacturaIntegracion();
//       
        if (cia.getLlavecancelacion() == null) {
            FacesMessage msg = Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, "La compañia no contiene llave de cancelación, favor de contactar a soporte");
            this.generarMsg(msg, true);
        } else {
            this.respuesta = new ArrayList<>();
//            
            if (this.selectedRecibos != null
                    && !this.selectedRecibos.isEmpty()) {
//                   
                this.respuesta = facturaintegracion.cancelarCFDIXuuid(this.selectedRecibos, cia);
                int exitosos = 0;
                int erroneos = 0;
//                    
                for (RespuestaCancelacion r : respuesta) {

                    if (r.isOperacionExitosa()) {
                        exitosos += 1;
                    } else {
                        erroneos += 1;
                        System.out.println("Error.... " + r.getMensajeError().getValue() + " | Detalle: " + r.getMensajeErrorDetallado().getValue() + " | " + r.getDetallesCancelacion().getValue().getDetalleCancelacion().get(0).getMensajeResultado().getValue());
                        FacesMessage msg = Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, "Error: " + r.getMensajeError().getValue());
                        this.generarMsg(msg, true);
                    }
//                   
                    if (r.isOperacionExitosa()) {
                        for (ReciboNomina rec : this.selectedRecibos) {
                            //De cada respuesta solo viene un valor en la lista, porque se mandan listas de recibos con un solo registro para saber el estatus de cada cancelacion, por eso se pone el valor 0
                            if (rec.getUuid().equals(r.getDetallesCancelacion().getValue().getDetalleCancelacion().get(0).getUUID().getValue())) {
                                rec.setEstatus(2);
                                reciboscancelados.add(rec);
                            }
                        }
                    }
                }
                System.out.println("Cancelacion Timbres.... | Enviados: " + this.selectedRecibos.size() + " | Exitosos: " + exitosos + " | ConError: " + erroneos);
//            
                boolean isValidate;
                if (!reciboscancelados.isEmpty()) {
                    isValidate = ControladorWS.getInstance().editRecibos(reciboscancelados);
                } else {
                    isValidate = false;
                }
                this.uuid = "";
                FacesMessage msg = isValidate ? Util.getFacesMessage(FacesMessage.SEVERITY_INFO, "Operación Exitosa!")
                        : Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, "La operación falló");
                this.generarMsg(msg, isValidate);

            }
        }
    }

    @Override
    public void create(ActionEvent event) {
    }

    @Override
    public void edit(ActionEvent event) {
        //Se borra la lista de respuestas de cancelaciones anteriores y se traen los objetos de cancelación y compañia
        this.respuesta = new ArrayList<>();
        FacturaIntegracion facturaintegracion = new FacturaIntegracion();
        Compania cia = ControladorWS.getInstance().getCompaniaXId(idCompania);
        RelacionLaboral rl = ControladorWS.getInstance().findRelacionLaboralByNumeroEmpleado(this.numeroempleado);
        Periodo p = ControladorWS.getInstance().findPeriodoByTipoProcesoAndPeriodoAndAnio(periodoCancelacion);

        if (this.uuid.isEmpty() || this.uuid.equals("")) {
            FacesMessage msg = Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, "Por favor, ingrese el UUID del recibo a cancelar.");
            this.generarMsg(msg, true);
        }
        if (cia.getLlavecancelacion() == null) {
            FacesMessage msg = Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, "La compañia no contiene llave de cancelación, favor de contactar a soporte");
            this.generarMsg(msg, true);
        }
        if (rl == null) {
            FacesMessage msg = Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, "Empleado no encontrado, favor de verificarlo");
            this.generarMsg(msg, true);
        }
        if (p == null) {
            FacesMessage msg = Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, "Ningun periodo coincide con los parametros ingresados");
            this.generarMsg(msg, true);
        } //Si la compañía tiene llave de cancelación sigue con el proceso
        else {
            this.respuesta = facturaintegracion.cancelarMasivo(this.uuid, cia);
            for (RespuestaCancelacion r : respuesta) {
                ReciboNomina reciboCancelado = new ReciboNomina();
                if (r.isOperacionExitosa()) {
                    reciboCancelado.setIdRellab(rl);
                    reciboCancelado.setIdPeriodo(p);
                    System.out.println("Numeroempleado recibo: " + reciboCancelado.getIdRellab().getNumeroempleado());
                    System.out.println("Cancelación exitosa para: " + this.uuid);
                    //System.out.println("Acuse de cancelacion: " + r.getXMLAcuse().getValue().toString());
                    reciboCancelado.setUuid(uuid);
                    reciboCancelado.setEstatus(99);
                    reciboCancelado.setXmlAcuseCancelacion(r.getDetallesCancelacion().getValue().getDetalleCancelacion().get(0).getCodigoResultado().getValue().getBytes());
                    //reciboCancelado.setXmlAcuseCancelacion(r.getXMLAcuse().getValue().getBytes());
                    ControladorWS.getInstance().createReciboCancelado(reciboCancelado);
                    FacesMessage msg = Util.getFacesMessage(FacesMessage.SEVERITY_INFO, "Operación exitosa ");
                    this.numeroempleado = "";
                    this.uuid = "";
                    this.periodoCancelacion = new Periodo();
                    this.generarMsg(msg, true);
                } else {
                    System.out.println("Error en cancelacion de timbre: " + r.getMensajeError() + " | " + r.getMensajeError());
                    FacesMessage msg = Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, "Error: " + r.getMensajeError().getValue());
                    this.generarMsg(msg, true);
                }

            }
        }
    }

    @Override
    public void delete(ReciboNomina obj) {
    }

    @Override
    public void listenerSelected() {
        if (this.recibos != null && !this.recibos.isEmpty()) {
            GrupoPago gp = this.recibos.get(0).getIdRellab().getIdgrupopago();
            Compania compania = gp.getIdcompania();
            if ((compania.getNombreCorto().equals("AEMSA") || compania.getNombreCorto().equals("MARTILLO")) && gp.getNombre().equals("SEMANAL MARTILLO OPERATIVO") || gp.getNombre().equals("SEMANAL MARTILLO ADMINISTRATIVOS")) {
                ControladorWS.getInstance().descargarPdfsXCCO(this.recibos, compania);
            } else {
                ControladorWS.getInstance().descargarPdfs(this.recibos, compania);
            }
            FacesMessage msg = Util.getFacesMessage(FacesMessage.SEVERITY_INFO, "Se descargaron los recibos");
            this.generarMsg(msg, true);
        }

    }

    public void listenerSelectedxmes() {
        if (this.recibos != null && !this.recibos.isEmpty()) {
            GrupoPago gp = this.recibos.get(0).getIdRellab().getIdgrupopago();
            Compania compania = ControladorWS.getInstance().consultarCompaniaXGrupoPago(gp.getGrupopago());
            ControladorWS.getInstance().descargarXmls(this.recibos, compania);
            FacesMessage msg = Util.getFacesMessage(FacesMessage.SEVERITY_INFO, "Se descargaron los recibos");
            this.generarMsg(msg, true);
        }

    }

    public void descargarPDF() {
        if (this.recibos != null && !this.recibos.isEmpty()) {
            GrupoPago gp = this.recibos.get(0).getIdRellab().getIdgrupopago();
            Compania compania = ControladorWS.getInstance().consultarCompaniaXGrupoPago(gp.getGrupopago());
            ControladorWS.getInstance().descargarSoloPDF(this.recibos, compania);
            FacesMessage msg = Util.getFacesMessage(FacesMessage.SEVERITY_INFO, "Se descargaron los recibos");
            this.generarMsg(msg, true);
        }

    }

    @Override
    public void generarMsg(FacesMessage msg, boolean isValidate) {
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("isValidate", isValidate);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    @Override
    public String getInformacion() {
        return CeniccoUtil.getInformacion(this.recibos.size());
    }

    public String getInformacionCancelados() {
        if (this.respuesta != null) {
            return CeniccoUtil.getInformacion(this.respuesta.size());
        } else {
            return CeniccoUtil.getInformacion(0);
        }
    }

    public List<ReciboNomina> getFilteredRecibos() {
        return filteredRecibos;
    }

    public void setFilteredRecibos(List<ReciboNomina> filteredRecibos) {
        this.filteredRecibos = filteredRecibos;
    }

    public ReciboNomina getRecibo() {
        return recibo;
    }

    public void setRecibo(ReciboNomina recibo) {
        this.recibo = recibo;
    }

    public List<ReciboNomina> getRecibos() {
        return recibos;
    }

    public List<ReciboNomina> getSelectedRecibos() {
        return selectedRecibos;
    }

    public void setSelectedRecibos(List<ReciboNomina> selectedRecibos) {
        this.selectedRecibos = selectedRecibos;
    }

    public List<RespuestaCancelacion> getRespuesta() {
        return respuesta;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public List<ReciboNomina> getRecibosErrores() {
        return recibosErrores;
    }

    public int getIdCompania() {
        return idCompania;
    }

    public void setIdCompania(int idCompania) {
        this.idCompania = idCompania;
    }

    public String getNumeroempleado() {
        return numeroempleado;
    }

    public void setNumeroempleado(String numeroempleado) {
        this.numeroempleado = numeroempleado;
    }

    public Periodo getPeriodoCancelacion() {
        return periodoCancelacion;
    }

    public void setPeriodoCancelacion(Periodo periodoCancelacion) {
        this.periodoCancelacion = periodoCancelacion;
    }

    public TipoProceso getTipoProcesoCancelacion() {
        return tipoProcesoCancelacion;
    }

    public void setTipoProcesoCancelacion(TipoProceso tipoProcesoCancelacion) {
        this.tipoProcesoCancelacion = tipoProcesoCancelacion;
    }
}