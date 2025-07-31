/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lam.cenicco.beans.nominas.creditos.consultas.creditos;

import com.lam.cenicco.dao.ProcesoDAO;
import com.lam.cenicco.impl.ControladorWS;
import com.lam.cenicco.librerias.mensajes.Mensajes;
import com.lam.cenicco.librerias.utilidades.CeniccoUtil;
import com.lam.cenicco.mail.ManejadorCorreo;
import com.lam.cenicco.util.Modulos;
import com.lam.cenicco.util.Util;
import com.lam.cenicco.ws.Concepto;
import com.lam.cenicco.ws.Credito;
import com.lam.cenicco.ws.CreditoAmortizacion;
import com.lam.cenicco.ws.Periodo;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import org.primefaces.context.RequestContext;

/**
 *
 * @author ysg
 */
public class ConsultaCreditoCCenicco implements ProcesoDAO<Credito> {
//    
//    

    protected List<Concepto> conceptos;
//    
    protected List<Credito> creditos;
//    
    protected List<Credito> filteredCreditos;
//    
    protected List<Periodo> periodos;
//    
    protected Integer selectedConcepto;
//    
    protected Integer selectedEstatus;
//    
    protected Integer selectedGrupoPago;
//    
    protected Integer selectedRelacionLaboral;
//    
    protected Integer anio;
//    
    protected Integer periodo;
//    
    protected String numeroEmpleado;
//    
    protected ManejadorCorreo manejadorCorreo;
//    
    protected Credito selectedCredito;
//    
    protected Credito creditoAmortizado;
//    
    protected Double importe;
//    
    protected Double importeoriginal;
//    
    protected Double aportacion;
//
    protected Integer noperiodos;
//      
    protected Integer selectedPeriodo;
//    

    @PostConstruct
    @Override
    public void init() {
        this.limpiar(null);
//        
        if (this.creditoAmortizado == null) {
            this.creditoAmortizado = new Credito();
        }
        if (this.conceptos == null) {
            this.conceptos = ControladorWS.getInstance().getConceptosCreditos();
        }
        if (this.manejadorCorreo == null) {
            this.manejadorCorreo = new ManejadorCorreo();
        }
    }

    public ConsultaCreditoCCenicco() {
    }

    @Override
    public void consultar(ActionEvent event) {
//      
        this.creditos = ControladorWS.getInstance().findCreditosByParametros(
                this.selectedGrupoPago, this.numeroEmpleado,
                this.selectedConcepto, this.selectedEstatus,
                this.anio, this.periodo);
//        
        System.out.println("NumeroCreditos.... " + this.creditos.size());
//        
        this.filteredCreditos = this.creditos;
    }

    public void cancelarCredito(Credito cr) {
//        
        boolean isValidate = ControladorWS.getInstance().editCredito(cr.getIdcredito(), 5);
        FacesMessage msg;
//        
        if (isValidate) {
            msg = Util.getFacesMessage(FacesMessage.SEVERITY_INFO, Mensajes.EXITOSO_EDITAR_CREDITO);
            String cadena = Util.getCadenasCreditosModificaciones(cr);
            this.manejadorCorreo.enviarCorreoNotificacion(Modulos.CREDITOS_MODIFICACIONES.getConcepto(), cadena);
//            
        } else {
            msg = Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, Mensajes.ERROR_EDITAR_CREDITO);
        }
//        
        this.generarMsg(msg, FacesMessage.SEVERITY_INFO.equals(msg.getSeverity()));
    }

    public void activarCredito(Credito cr) {
//        
        boolean isValidate = ControladorWS.getInstance().editCredito(cr.getIdcredito(), 1);
        FacesMessage msg;
//        
        if (isValidate) {
            msg = Util.getFacesMessage(FacesMessage.SEVERITY_INFO, Mensajes.EXITOSO_EDITAR_CREDITO);
            String cadena = Util.getCadenasCreditosModificaciones(cr);
            this.manejadorCorreo.enviarCorreoNotificacion(Modulos.CREDITOS_MODIFICACIONES.getConcepto(), cadena);
//            
        } else {
            msg = Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, Mensajes.ERROR_EDITAR_CREDITO);
        }
//        
        this.generarMsg(msg, FacesMessage.SEVERITY_INFO.equals(msg.getSeverity()));
    }

    public void liquidarCredito(Credito cr) {
//        
        boolean isValidate = ControladorWS.getInstance().editCredito(cr.getIdcredito(), 6);
        FacesMessage msg;
//        
        if (isValidate) {
            msg = Util.getFacesMessage(FacesMessage.SEVERITY_INFO, Mensajes.EXITOSO_EDITAR_CREDITO);
            String cadena = Util.getCadenasCreditosModificaciones(cr);
            this.manejadorCorreo.enviarCorreoNotificacion(Modulos.CREDITOS_MODIFICACIONES.getConcepto(), cadena);
//            
        } else {
            msg = Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, Mensajes.ERROR_EDITAR_CREDITO);
        }
//        
        this.generarMsg(msg, FacesMessage.SEVERITY_INFO.equals(msg.getSeverity()));
    }

    public void reestructura(Credito cr) {
        this.selectedCredito = cr;
        this.noperiodos = 0;
//        
        this.importe = 0.0;
        this.importeoriginal = 0.0;
        this.creditoAmortizado = new Credito();
//        
        for (CreditoAmortizacion ca : this.selectedCredito.getAmortizacion()) {
//            
            if (ca.getEstatus() == 0) {
                this.creditoAmortizado.getAmortizacion().add(ca);
                this.importe += ca.getAportacion();
                this.importeoriginal += ca.getAportacion();
                this.noperiodos++;
            }
//
        }
//        
        this.selectedPeriodo = null;
        this.aportacion = 0.0;
//        
        Integer idGrupoPago = this.selectedCredito.getRelacionLaboral().getIdgrupopago().getIdgrupopago();
//        
        if (idGrupoPago != null) {
            this.periodos = ControladorWS.getInstance().getPeriodosByTipoProceso(idGrupoPago);

        }
//        
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("isValidate", true);
//        
    }

    public void calcular(ActionEvent event) {
        this.importe = 0.0;
        this.importeoriginal = 0.0;
        for (CreditoAmortizacion ca : this.selectedCredito.getAmortizacion()) {
            if (ca.getEstatus() == 0) {
                this.importe += ca.getAportacion();
                this.importeoriginal += ca.getAportacion();
            }

        }
//        
        this.importe = this.importe - this.aportacion;
//        
        if (this.importe != 0.0) {

            this.creditoAmortizado = ControladorWS.getInstance().calcularCreditosSimples(this.selectedCredito.getRelacionLaboral(), this.importe, this.noperiodos,
                    this.selectedPeriodo, this.selectedCredito.getConcepto().getIdconcepto(),"Re-estructurado del Folio: " + this.selectedCredito.getIdcredito(), 1);
//            
        }
    }

    public void aplicarreestructura(ActionEvent event) {
//            
        boolean isValidate = ControladorWS.getInstance().editCredito(this.selectedCredito.getIdcredito(), 7);
//        
        if (isValidate) {
            isValidate = ControladorWS.getInstance().createCredito(this.creditoAmortizado);
        }
        FacesMessage msg = isValidate ? Util.getFacesMessage(FacesMessage.SEVERITY_INFO, "La re-estructuración fué ejecutada exitosamente")
                : Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, "La re-estructuración no fue ejecutada exitosamente");
//        
        this.generarMsg(msg, isValidate);

    }

    @Override
    public void limpiar(ActionEvent event) {
        this.creditos = new ArrayList<>();
        this.filteredCreditos = this.creditos;
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
        return CeniccoUtil.getInformacion(this.creditos.size());
    }

    @Override
    public void delete(Credito obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Integer getSelectedGrupoPago() {
        return selectedGrupoPago;
    }

    public void setSelectedGrupoPago(Integer selectedGrupoPago) {
        this.selectedGrupoPago = selectedGrupoPago;
    }

    public List<Credito> getFilteredCreditos() {
        return filteredCreditos;
    }

    public void setFilteredCreditos(List<Credito> filteredCreditos) {
        this.filteredCreditos = filteredCreditos;
    }

    public Integer getSelectedConcepto() {
        return selectedConcepto;
    }

    public void setSelectedConcepto(Integer selectedConcepto) {
        this.selectedConcepto = selectedConcepto;
    }

    public Integer getSelectedEstatus() {
        return selectedEstatus;
    }

    public void setSelectedEstatus(Integer selectedEstatus) {
        this.selectedEstatus = selectedEstatus;
    }

    public Integer getSelectedRelacionLaboral() {
        return selectedRelacionLaboral;
    }

    public void setSelectedRelacionLaboral(Integer selectedRelacionLaboral) {
        this.selectedRelacionLaboral = selectedRelacionLaboral;
    }

    public List<Concepto> getConceptos() {
        return conceptos;
    }

    public List<Credito> getCreditos() {
        return creditos;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public Integer getPeriodo() {
        return periodo;
    }

    public void setPeriodo(Integer periodo) {
        this.periodo = periodo;
    }

    public String getNumeroEmpleado() {
        return numeroEmpleado;
    }

    public void setNumeroEmpleado(String numeroEmpleado) {
        this.numeroEmpleado = numeroEmpleado;
    }

    public Credito getSelectedCredito() {
        return selectedCredito;
    }

    public void setSelectedCredito(Credito selectedCredito) {
        this.selectedCredito = selectedCredito;
    }

    public Double getImporte() {
        return importe;
    }

    public void setImporte(Double importe) {
        this.importe = importe;
    }

    public Integer getNoperiodos() {
        return noperiodos;
    }

    public void setNoperiodos(Integer noperiodos) {
        this.noperiodos = noperiodos;
    }

    public Integer getSelectedPeriodo() {
        return selectedPeriodo;
    }

    public void setSelectedPeriodo(Integer selectedPeriodo) {
        this.selectedPeriodo = selectedPeriodo;
    }

    public List<Periodo> getPeriodos() {
        return periodos;
    }

    public void setPeriodos(List<Periodo> periodos) {
        this.periodos = periodos;
    }

    public Double getAportacion() {
        return aportacion;
    }

    public void setAportacion(Double aportacion) {
        this.aportacion = aportacion;
    }

    public Credito getCreditoAmortizado() {
        return creditoAmortizado;
    }

    public void setCreditoAmortizado(Credito creditoAmortizado) {
        this.creditoAmortizado = creditoAmortizado;
    }

    public Double getImporteoriginal() {
        return importeoriginal;
    }

    public String getInformacionAmortizacion() {
        return CeniccoUtil.getInformacion(this.creditoAmortizado.getAmortizacion().size());
    }
}
