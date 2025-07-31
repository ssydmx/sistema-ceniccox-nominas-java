/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lam.cenicco.beans.nominas.vacaciones.saldos.extralegales;

import com.lam.cenicco.dao.ProcesoDAO;
import com.lam.cenicco.impl.ControladorWS;
import com.lam.cenicco.librerias.utilidades.CeniccoUtil;
import com.lam.cenicco.util.ParametrosReportes;
import com.lam.cenicco.util.Util;
import com.lam.cenicco.util.paths.MyPaths;
import com.lam.cenicco.ws.Periodo;
import com.lam.cenicco.ws.SaldoVacaciones;
import com.lam.cenicco.ws.SolicitudVacaciones;
import com.lam.cenicco.ws.Vacaciones;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author Cenicco
 */
public class SaldoVacacionesExtraLegalesCenicco implements ProcesoDAO<SaldoVacaciones>{

    private Map<String, Periodo> mapaperiodos;
//    
    private List<SaldoVacaciones> saldos;
    private List<SaldoVacaciones> filteredsaldos;
//    
    private List<Vacaciones> calendario;
//  
    private SaldoVacaciones selectedsaldo;
//    
    private Date fechainicio;
    private Date fechafin;
//    
    private Integer selectedgrupopago;
    private Integer solicitar;
//    
    private String numeroempleado;
    private String status;

    public SaldoVacacionesExtraLegalesCenicco() {
        if (this.saldos == null) {
            this.saldos = new ArrayList<>();
        }
        if (this.filteredsaldos == null) {
            this.filteredsaldos = new ArrayList<>();
        }
        if (this.mapaperiodos == null) {
            this.mapaperiodos = ControladorWS.getInstance().getMapaPeriodos();
        }
    }

    public void addCalendar(SelectEvent event) {
//
        this.calendario = new ArrayList<>();
//        
        Calendar cal = Calendar.getInstance();
        cal.setTime(this.fechainicio);
//      
        Periodo p = (Periodo) ControladorWS.getInstance().getPeriodoFechaInter(this.fechainicio,
                selectedsaldo.getRellab().getIdgrupopago().getIdTipoproceso().getIdtipoproceso());
        System.out.println("PeriodoInicio... " + p.getIdperiodo().toString());
//      
        Vacaciones v = new Vacaciones();
        v.setIdperiodo(p.getIdperiodo());
        v.setFecha(CeniccoUtil.getDateToXmlCalendar(this.fechainicio));
        v.setEstatus(99);
        this.calendario.add(v);
//        
        int dias = solicitar;

        String[] diasDescanso = selectedsaldo.getRellab().getIdrelacionlaboralposicion().getIddepartamento().getDescripcion() == null ? null : selectedsaldo.getRellab().getIdrelacionlaboralposicion().getIddepartamento().getDescripcion().split(",");
        for (int i = 1; i < dias; i++) {
            cal.add(Calendar.DAY_OF_MONTH, 1);
            if (diasDescanso != null) {
                boolean descanso = false;
                for (int a = 0; a < diasDescanso.length; a++) {
                    switch (diasDescanso[a]) {
                        case "L":
                            if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
                                descanso = true;
                                dias++;
                            }
                            break;
                        case "M":
                            if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY) {
                                descanso = true;
                                dias++;
                            }
                            break;
                        case "X":
                            if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY) {
                                descanso = true;
                                dias++;
                            }
                            break;
                        case "J":
                            if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) {
                                descanso = true;
                                dias++;
                            }
                            break;
                        case "V":
                            if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
                                descanso = true;
                                dias++;
                            }
                            break;
                        case "S":
                            if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                                descanso = true;
                                dias++;
                            }
                            break;
                        case "D":
                            if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                                descanso = true;
                                dias++;
                            }
                            break;
                    }
                }
                if (!descanso) {
                    System.out.println("FechaRevisada:" + cal.getTime());
                    if (cal.getTime().after(p.getFechafin().toGregorianCalendar().getTime())) {
                        int periodo = p.getPeriodo() + 1;
                        String llave = p.getAnio().toString() + " | " + periodo + " | " + p.getIdtipoproceso().getIdtipoproceso().toString();
//                    
                        if (mapaperiodos.get(llave) != null) {
                            p = mapaperiodos.get(llave);
                        } else {
                            int anio = p.getAnio() + 1;
                            llave = anio + " | 1 | " + p.getIdtipoproceso().getIdtipoproceso().toString();
                            if (mapaperiodos.get(llave) != null) {
                                p = mapaperiodos.get(llave);
                            } else {
                                break;
                            }
                        }
                    }
                    v = new Vacaciones();
                    v.setIdperiodo(p.getIdperiodo());
                    v.setFecha(CeniccoUtil.getDateToXmlCalendar(cal.getTime()));
                    v.setEstatus(99);
                    this.calendario.add(v);
                }
            } else {
                //Por defecto se descansa sabado y domingo
                if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
                        || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                    dias++;
                } else {
                    System.out.println("FechaRevisada:" + cal.getTime());
                    if (cal.getTime().after(p.getFechafin().toGregorianCalendar().getTime())) {
                        int periodo = p.getPeriodo() + 1;
                        String llave = p.getAnio().toString() + " | " + periodo + " | " + p.getIdtipoproceso().getIdtipoproceso().toString();
//                    
                        if (mapaperiodos.get(llave) != null) {
                            p = mapaperiodos.get(llave);
                        } else {
                            int anio = p.getAnio() + 1;
                            llave = anio + " | 1 | " + p.getIdtipoproceso().getIdtipoproceso().toString();
                            if (mapaperiodos.get(llave) != null) {
                                p = mapaperiodos.get(llave);
                            } else {
                                break;
                            }
                        }
                    }
                    v = new Vacaciones();
                    v.setIdperiodo(p.getIdperiodo());
                    v.setFecha(CeniccoUtil.getDateToXmlCalendar(cal.getTime()));
                    v.setEstatus(99);
                    this.calendario.add(v);
                }
            }
        }
        this.fechafin = cal.getTime();
        System.out.println("Calendario Size: " + calendario.size());
    }

    public void limpiar() {
        this.solicitar = 0;
        this.fechainicio = null;
        this.fechafin = null;
    }

    public void solicitar() {
        FacesMessage msg = null;
        boolean isvalidate = true;

        for (Vacaciones v : this.calendario) {
            //System.out.println("Dia: " + CeniccoUtil.getSimpleDateFormatFromXMLGregorian(v.getFecha()) + " | Periodo: " + v.getIdperiodo());
            Periodo periodo = ControladorWS.getInstance().findPeriodoById(v.getIdperiodo());
            if (periodo.getEstatus() == 1) {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error en la Operación!", "Los Días a Solicitar estan en un periodo cerrado");
                isvalidate = false;
            }
        }

        if (isvalidate) {
            List<SolicitudVacaciones> solicitudesVacaciones = ControladorWS.getInstance().findByBetweenFechaInicioAndFechaFin(selectedsaldo.getRellab().getIdrellab(), fechainicio, fechafin);
            if (!solicitudesVacaciones.isEmpty()) {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error en la Operación!", "Los Días a Solicitar estan solicitados");
                isvalidate = false;
            } else {
//        
                if ((solicitar + selectedsaldo.getSolicitadas()) > selectedsaldo.getSaldo()) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error en la Operación!", "Los Días a Solicitar deben ser menor o igual al Saldo");
                    isvalidate = false;
                } else {
//            
                    selectedsaldo.setSolicitadas(solicitar);
//            
                    isvalidate = ControladorWS.getInstance().createVacaciones(calendario, selectedsaldo, fechainicio, fechafin);

                    if (isvalidate) {
                        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Exito en la Operación!", "La solicitud fué exitosa!");
//                
                    } else {
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error en la Operación!", "Intentelo más tarde!");
                    }
                }
            }
        }
        this.generarMsg(msg, isvalidate);
    }

    @Override
    public void consultar(ActionEvent event) {
        String concepto = ControladorWS.getInstance().getConceptoVacacionesExtraLegales();
        this.saldos = ControladorWS.getInstance().getSaldoVacaciones(numeroempleado, selectedgrupopago, status, concepto);
        this.filteredsaldos = this.saldos;
    }

    public List<SaldoVacaciones> getFilteredsaldos() {
        return filteredsaldos;
    }

    public void setFilteredsaldos(List<SaldoVacaciones> filteredsaldos) {
        this.filteredsaldos = filteredsaldos;
    }

    public Integer getSelectedgrupopago() {
        return selectedgrupopago;
    }

    public void setSelectedgrupopago(Integer selectedgrupopago) {
        this.selectedgrupopago = selectedgrupopago;
    }

    public String getNumeroempleado() {
        return numeroempleado;
    }

    public void setNumeroempleado(String numeroempleado) {
        this.numeroempleado = numeroempleado;
    }

    public List<SaldoVacaciones> getSaldos() {
        return saldos;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
    public void delete(SaldoVacaciones obj) {
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
        if (isValidate) {
            this.consultar(null);
        }
    }

    @Override
    public String getInformacion() {
        return CeniccoUtil.getInformacion(this.saldos.size());
    }

    @Override
    public void init() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public SaldoVacaciones getSelectedsaldo() {
        return selectedsaldo;
    }

    public void setSelectedsaldo(SaldoVacaciones selectedsaldo) {
        this.selectedsaldo = selectedsaldo;
    }

    public Date getFechainicio() {
        return fechainicio;
    }

    public void setFechainicio(Date fechainicio) {
        this.fechainicio = fechainicio;
    }

    public String getFechafin() {
        if (fechafin != null) {
            return new SimpleDateFormat("dd/MM/yyyy").format(fechafin);
        } else {
            return "";
        }
    }

    public Integer getSolicitar() {
        return solicitar;
    }

    public void setSolicitar(Integer solicitar) {
        this.solicitar = solicitar;
    }

    public List<Vacaciones> getCalendario() {
        return calendario;
    }

    public void setCalendario(List<Vacaciones> calendario) {
        this.calendario = calendario;
    }

    public void generarSaldos(ActionEvent event) {
        boolean isValidate = ControladorWS.getInstance().generarSaldos(numeroempleado, selectedgrupopago);

        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Exito en la Operación!", "La solicitud fué exitosa!");
        if (!isValidate) {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error en la Operación!", "Intentelo más tarde!");
        }
        this.generarMsg(msg, isValidate);
    }

    public void openSaldoAdelantado(SaldoVacaciones saldo) {
        System.out.println("Inicio Abrir Saldo Adelantado");
        /**
         * 0 Inactivo 1 Activo 2 Inactivo Futuro 3 Activo Futuro 4 Registrado
         */
        saldo.setEstatus(3);

        List<SaldoVacaciones> listSaldos = new ArrayList<>();
        listSaldos.add(saldo);

        System.out.println("Editar Saldo Adelantado");
        boolean isValidate = ControladorWS.getInstance().editSaldosVacaciones(listSaldos);
        if (isValidate) {
            this.consultar(null);
        } else {
            System.out.println("Error en Adelantar Saldo Vacaciones");
        }
    }

    public void generarReporteSaldoVacaciones() {
        try {
            byte[] data = ControladorWS.getInstance().generarReporteSaldoVacaciones(selectedgrupopago, numeroempleado);

            String nombreArchivo = "Reporte Saldo Vacaciones";

            RequestContext context = RequestContext.getCurrentInstance();
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, nombreArchivo);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, data);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_XLS);
            context.addCallbackParam("ruta", MyPaths.urlServletDescargarArchivoCsv());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getSupportedDays() {
        // add some days constants in respect to dynamic view data!
        List<String> days = new ArrayList<String>();
        days.add("3");
        days.add("4");

        System.out.println("o----- Dias escogidos :::: " + Util.join(",", days));
        return Util.join(",", days);
    }
}
