/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lam.cenicco.beans.nominas.vacaciones.solicitud;

import com.lam.cenicco.dao.ProcesoDAO;
import com.lam.cenicco.impl.ControladorWS;
import com.lam.cenicco.librerias.utilidades.CeniccoUtil;
import com.lam.cenicco.mail.ManejadorCorreo;
import com.lam.cenicco.util.ControladorSesiones;
import com.lam.cenicco.util.ParametrosReportes;
import com.lam.cenicco.util.Util;
import com.lam.cenicco.util.paths.MyPaths;
import com.lam.cenicco.ws.Concepto;
import com.lam.cenicco.ws.Empleado;
import com.lam.cenicco.ws.Incidencia;
import com.lam.cenicco.ws.RelacionLaboral;
import com.lam.cenicco.ws.SaldoVacaciones;
import com.lam.cenicco.ws.SolicitudVacaciones;
import com.lam.cenicco.ws.Usuario;
import com.lam.cenicco.ws.Vacaciones;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.xml.datatype.XMLGregorianCalendar;
import org.primefaces.context.RequestContext;

/**
 *
 * @author Antonio Durán
 */
public class SolicitudVacacionesCenicco implements ProcesoDAO<SolicitudVacaciones> {

    private List<SolicitudVacaciones> solicitudes;
    private List<SolicitudVacaciones> filteredsolicitudes;
//    
    private String fechainicio;
    private String fechafin;
//    
    private String numeroempleado;
    private Integer selectedestatus;
    private Integer selectedidgrupopago;

    public SolicitudVacacionesCenicco() {
        if (this.solicitudes == null) {
            this.solicitudes = new ArrayList<>();
        }
        if (this.filteredsolicitudes == null) {
            this.filteredsolicitudes = new ArrayList<>();
        }
    }

    @Override
    public void consultar(ActionEvent event) {
        this.solicitudes = ControladorWS.getInstance().findSolicitudVacaciones(numeroempleado, selectedidgrupopago, selectedestatus, fechainicio, fechafin);
        this.filteredsolicitudes = this.solicitudes;
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

    public void editar(SolicitudVacaciones s) {
        Map<String, String> mapamails = ControladorWS.getInstance().getMails();
        if (s.getSaldoVacaciones() == null) {
            System.out.println("Solicitud Home Office... " + s.getIdsolicitud());
            String mail = mapamails.get(s.getRellab().getIdempleado().getIdempleado().toString());
            String fechaInicio = CeniccoUtil.convertDateToString(s.getFechainicio().toGregorianCalendar().getTime());
            String fechaFin = CeniccoUtil.convertDateToString(s.getFechafin().toGregorianCalendar().getTime());
            String fechaRegistro = CeniccoUtil.convertDateToString(s.getFecharegistro().toGregorianCalendar().getTime());
            boolean isValidate = ControladorWS.getInstance().editSolicitud(s);
            switch (s.getEstatus()) {
                case 1:
                    if (isValidate) {
                        new ManejadorCorreo().enviarCorreoSolicitudHomeOffice(s.getRellab(), mail, s.getEstatus(), s.getDias(), fechaInicio, fechaFin, fechaRegistro);
                    }
                    break;
                case 0:
                    if (isValidate) {
                        new ManejadorCorreo().enviarCorreoSolicitudHomeOffice(s.getRellab(), mail, s.getEstatus(), s.getDias(), fechaInicio, fechaFin, fechaRegistro);
                    }
                    break;
                case 99:
                    break;
            }
        } else {
            System.out.println("Solicitud Vacaciones... " + s.getIdsolicitud());
            SaldoVacaciones saldo = ControladorWS.getInstance().editSolicitudVacaciones(s);
//      
            String mail = mapamails.get(s.getRellab().getIdempleado().getIdempleado().toString());
            Usuario usuario = ControladorSesiones.getInstance().getUsuarioSession();
            Concepto concepto = s.getSaldoVacaciones().getTabuladorSistemaAntiguedad().getConcepto();
            boolean isValidate = false;
            String finicio = CeniccoUtil.convertDateToString(s.getFechainicio().toGregorianCalendar().getTime());
            String ffin = CeniccoUtil.convertDateToString(s.getFechafin().toGregorianCalendar().getTime());
            String fregistro = CeniccoUtil.convertDateToString(s.getFecharegistro().toGregorianCalendar().getTime());
            switch (s.getEstatus()) {
                case 1:
//Autorizada
                    List<Vacaciones> vac = ControladorWS.getInstance().getCalendarioVacacionesxidSolicitud(s.getIdsolicitud());
                    List<Incidencia> inc = new ArrayList<>();
                    XMLGregorianCalendar fRegistro = CeniccoUtil.getDateToXmlCalendar(new Date());
                    for (Vacaciones v : vac) {
                        Incidencia i = new Incidencia();
                        i.setIdperiodo(v.getPeriodo());
                        i.setFechaaux01(v.getFecha());
                        i.setEstatus("01");
                        i.setUsuario(usuario);
                        i.setFechaact(fRegistro);
                        i.setReferencia01(concepto.getConcepto() + " - " + concepto.getNombre() + " AUTORIZADAS POR " + usuario.getNombre() + " " + usuario.getApellidoPaterno());
                        i.setIdrellab(v.getSolicitud().getRellab());
                        i.setIdconcepto(concepto);
                        i.setIdtipoproceso(v.getSolicitud().getRellab().getIdgrupopago().getIdTipoproceso().getIdtipoproceso());
                        i.setUnidades(1.0);
                        i.setAutorizar(BigDecimal.ONE.intValue());
                        i.setIdUsuarioAutorizar(usuario);
                        inc.add(i);
                    }
                    isValidate = ControladorWS.getInstance().createIncidencias(inc, usuario);
                    if (isValidate) {
                        isValidate = new ManejadorCorreo().enviarCorreoSolicitudVacaciones(s.getRellab(), mail, saldo,
                                s.getEstatus(), s.getDias(), finicio, ffin, fregistro);
                    }

                    break;
                case 0:
//Rechazada
                    isValidate = new ManejadorCorreo().enviarCorreoSolicitudVacaciones(s.getRellab(), mail, saldo,
                            s.getEstatus(), s.getDias(), finicio, ffin, fregistro);
                    break;
                case 99:
//Registrada

                    break;
            }
            if (s.getEstatus() != 99) {
//            

                System.out.println("IsValidateEDit... " + isValidate);
            }
        }
    }

    @Override
    public void delete(SolicitudVacaciones obj) {
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
        return CeniccoUtil.getInformacion(this.solicitudes.size());
    }

    @Override
    public void init() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public List<SolicitudVacaciones> getFilteredsolicitudes() {
        return filteredsolicitudes;
    }

    public void setFilteredsolicitudes(List<SolicitudVacaciones> filteredsolicitudes) {
        this.filteredsolicitudes = filteredsolicitudes;
    }

    public String getFechainicio() {
        return fechainicio;
    }

    public void setFechainicio(String fechainicio) {
        this.fechainicio = fechainicio;
    }

    public String getFechafin() {
        return fechafin;
    }

    public void setFechafin(String fechafin) {
        this.fechafin = fechafin;
    }

    public String getNumeroempleado() {
        return numeroempleado;
    }

    public void setNumeroempleado(String numeroempleado) {
        this.numeroempleado = numeroempleado;
    }

    public Integer getSelectedestatus() {
        return selectedestatus;
    }

    public void setSelectedestatus(Integer selectedestatus) {
        this.selectedestatus = selectedestatus;
    }

    public Integer getSelectedidgrupopago() {
        return selectedidgrupopago;
    }

    public void setSelectedidgrupopago(Integer selectedidgrupopago) {
        this.selectedidgrupopago = selectedidgrupopago;
    }

    public List<SolicitudVacaciones> getSolicitudes() {
        return solicitudes;
    }

    public void generarCsvHomeOffice() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new OutputStreamWriter(baos));
            String linea = "Folio, No. Empleado, Empleado, Grupo Pago, Referencia, F. Registro, D. Solicitar, F. Inicio, F. Fin, Estatus";
            bw.write(linea);
            bw.newLine();
            for (SolicitudVacaciones sv : this.solicitudes) {
                if (sv.getSaldoVacaciones() == null) {
                    RelacionLaboral rellab = sv.getRellab();
                    Empleado empleado = sv.getRellab().getIdempleado();
                    String nombre = Util.getNombre(empleado);
                    linea = sv.getIdsolicitud()
                            + "," + rellab.getNumeroempleado()
                            + "," + nombre
                            + "," + rellab.getIdgrupopago().getNombre()
                            + ", Día Home Office"
                            + "," + sdf.format(sv.getFecharegistro().toGregorianCalendar().getTime())
                            + "," + sv.getDias()
                            + "," + sdf.format(sv.getFechainicio().toGregorianCalendar().getTime())
                            + "," + sdf.format(sv.getFechafin().toGregorianCalendar().getTime())
                            + "," + (sv.getEstatus() == 0 ? "Cancelado" : (sv.getEstatus() == 1 ? "Autorizado" : (sv.getEstatus() == 2 ? "Eliminado" : "Solicitado")));
                    bw.write(linea);
                    bw.newLine();
                }
            }
            bw.flush();
            System.out.println("Escribio Correctamente el Archivo....");
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
        String nombrearchivo = "Solicitudes Home Office";
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, nombrearchivo);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_CSV);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, baos.toByteArray());
        context.addCallbackParam("ruta", MyPaths.urlServletDescargarArchivoCsv());
    }
}
