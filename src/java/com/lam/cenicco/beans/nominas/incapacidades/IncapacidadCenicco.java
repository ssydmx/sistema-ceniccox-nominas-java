/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lam.cenicco.beans.nominas.incapacidades;

import com.lam.cenicco.dao.ProcesoDAO;
import com.lam.cenicco.impl.ControladorWS;
import com.lam.cenicco.librerias.mensajes.Mensajes;
import com.lam.cenicco.librerias.utilidades.CeniccoUtil;
import com.lam.cenicco.util.ControladorSesiones;
import com.lam.cenicco.util.Util;
import com.lam.cenicco.ws.Concepto;
import com.lam.cenicco.ws.Incidencia;
import com.lam.cenicco.ws.RelacionLaboral;
import com.lam.cenicco.ws.Usuario;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import org.primefaces.context.RequestContext;

/**
 *
 * @author JoséAntonio
 */
public class IncapacidadCenicco implements ProcesoDAO<RelacionLaboral> {
//

    protected List<RelacionLaboral> incapacidades;
//    
    protected List<RelacionLaboral> filteredIncapacidades;
//    
    protected List<String> referencias;
//    
    protected String fechainicioIncidencia;
//    
    protected String fechainicioImss;
//
    protected String[] selectedRelaciones;
//    
    protected String folio;
////    
    protected String selectedReferencia;
//    
    protected String observaciones;
//    
    protected List<RelacionLaboral> relaciones;
//    
    protected Integer selectedRelacionLaboral;
//    
    protected List<Concepto> conceptos;
//    
    protected Integer dias;
//    
    protected Integer selectedConcepto;

    @PostConstruct
    @Override
    public void init() {
        this.limpiar(null);
    }

    public void changeGrupoPago(ValueChangeEvent e) {
        Integer idGrupoPago = e.getNewValue() == null ? null : Integer.parseInt(e.getNewValue().toString());
        this.updateEmpleados(idGrupoPago);
    }

    public void changeIncapacidad(ValueChangeEvent e) {
        this.selectedConcepto = e.getNewValue() == null ? null : Integer.parseInt(e.getNewValue().toString());
        if (this.selectedConcepto != null) {
            this.referencias = ControladorWS.getInstance().getComboReferenciaIncapacidades(this.selectedConcepto);
        }
    }

    private void updateEmpleados(Integer idGrupoPago) {
        this.selectedRelaciones = null;
//        
        this.relaciones = ControladorWS.getInstance().findRelacionesLaboralesByFiltrosDates(idGrupoPago, 1, null, null, null, null);
//        
    }

    @Override
    public void consultar(ActionEvent event) {
        boolean isValidate = ControladorWS.getInstance().findFolioIncapacidades(this.folio);
        if (!isValidate) {
            this.incapacidades = ControladorWS.getInstance().calcularIncapacidades(this.selectedRelacionLaboral, this.fechainicioIncidencia, this.fechainicioImss,
                    this.dias, this.folio, this.selectedConcepto, this.selectedReferencia, this.observaciones);
//            
            if (this.incapacidades == null || this.incapacidades.isEmpty()) {
                FacesMessage msg = Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, "Error al calcular, las fechas ya se encuentran registradas");
                this.generarMsg(msg, isValidate);
                System.out.println("Fechas duplicadas!!!");
//                
                this.incapacidades = new ArrayList<>();
            }
//        
            this.filteredIncapacidades = this.incapacidades;
        } else {
            FacesMessage msg = Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, "El Folio ya existe!");
            this.generarMsg(msg, isValidate);
        }
    }

    @Override
    public void limpiar(ActionEvent event) {
        this.conceptos = ControladorWS.getInstance().getConceptosIncapacidades();
        this.incapacidades = new ArrayList<>();
        this.filteredIncapacidades = this.incapacidades;
    }

    @Override
    public void create(ActionEvent event) {
        if (!this.incapacidades.isEmpty()) {
            Usuario usuario = ControladorSesiones.getInstance().getUsuarioSession();
            Pattern pattern = Pattern.compile("(?=.*Inicial)|(?=.*Temporal)|(?=.*Prenatal)");
            if (!this.incapacidades.get(0).getIncidencias().isEmpty() && pattern.matcher(this.selectedReferencia).find()) {
                List<String> consIncas = Arrays.asList(new String[]{"2210", "2220", "2230", "2240", "2260"});
                Incidencia incidenciaInicial = this.incapacidades.get(0).getIncidencias().get(0);
                Concepto concepto = null;
                for (Concepto c : this.conceptos) {
                    if (c.getIdconcepto() == this.selectedConcepto) {
                        concepto = c;
                        break;
                    }
                }
                Double totalIncidenciasSinSubsidio = incidenciaInicial.getUnidades();
                if (concepto != null && consIncas.contains(concepto.getConcepto())) {
                    Concepto conceptoSubsiodio3Dias = ControladorWS.getInstance().findConceptoByConcepto("1149");
                    if (conceptoSubsiodio3Dias != null) {
                        Incidencia subsiodio3Dias = new Incidencia();
                        subsiodio3Dias.setIdrellab(incidenciaInicial.getIdrellab());
                        subsiodio3Dias.setIdperiodo(incidenciaInicial.getIdperiodo());
                        subsiodio3Dias.setIdconcepto(conceptoSubsiodio3Dias);
                        subsiodio3Dias.setEstatus(incidenciaInicial.getEstatus());
                        subsiodio3Dias.setIdtipoproceso(incidenciaInicial.getIdtipoproceso());
                        subsiodio3Dias.setUnidades(totalIncidenciasSinSubsidio < 3.0 ? totalIncidenciasSinSubsidio : 3.0);
                        subsiodio3Dias.setImporte(null);
                        subsiodio3Dias.setReferencia01(incidenciaInicial.getReferencia01());
                        subsiodio3Dias.setFechaaux01(incidenciaInicial.getFechaaux01());
                        subsiodio3Dias.setFechaact(incidenciaInicial.getFechaact());
                        subsiodio3Dias.setUsuario(incidenciaInicial.getUsuario());
                        subsiodio3Dias.setAutorizar(incidenciaInicial.getAutorizar());
                        this.incapacidades.get(0).getIncidencias().add(subsiodio3Dias);
                        //System.out.println(concepto.getConcepto() + " | Se crearon subsiodio3Dias | " + this.selectedReferencia);
                    }
                }
            }
            boolean isValidate = ControladorWS.getInstance().crearIncapacidades(this.incapacidades.get(0), usuario);
//        
            FacesMessage msg = isValidate ? Util.getFacesMessage(FacesMessage.SEVERITY_INFO, Mensajes.EXITOSO_CREAR_INCIDENCIAS)
                    : Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, Mensajes.ERROR_EDITAR_INCIDENCIAS);
//        
            this.generarMsg(msg, isValidate);
        }

    }

    @Override
    public void edit(ActionEvent event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void delete(RelacionLaboral obj) {
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
    }

    @Override
    public String getInformacion() {
        return CeniccoUtil.getInformacion(this.incapacidades.size());
    }

    public String[] getSelectedRelaciones() {
        return selectedRelaciones;
    }

    public void setSelectedRelaciones(String[] selectedRelaciones) {
        this.selectedRelaciones = selectedRelaciones;
    }

    public List<RelacionLaboral> getRelaciones() {
        return relaciones;
    }

    public Integer getDias() {
        return dias;
    }

    public void setDias(Integer dias) {
        this.dias = dias;
    }

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public Integer getSelectedConcepto() {
        return selectedConcepto;
    }

    public void setSelectedConcepto(Integer selectedConcepto) {
        this.selectedConcepto = selectedConcepto;
    }

    public List<Concepto> getConceptos() {
        return conceptos;
    }

    public List<RelacionLaboral> getFilteredIncapacidades() {
        return filteredIncapacidades;
    }

    public void setFilteredIncapacidades(List<RelacionLaboral> filteredIncapacidades) {
        this.filteredIncapacidades = filteredIncapacidades;
    }

    public List<RelacionLaboral> getIncapacidades() {
        return incapacidades;
    }

    public String getSelectedReferencia() {
        return selectedReferencia;
    }

    public void setSelectedReferencia(String selectedReferencia) {
        this.selectedReferencia = selectedReferencia;
    }

    public List<String> getReferencias() {
        return referencias;
    }

    public String getFechainicioIncidencia() {
        return fechainicioIncidencia;
    }

    public void setFechainicioIncidencia(String fechainicioIncidencia) {
        this.fechainicioIncidencia = fechainicioIncidencia;
    }

    public String getFechainicioImss() {
        return fechainicioImss;
    }

    public void setFechainicioImss(String fechainicioImss) {
        this.fechainicioImss = fechainicioImss;
    }

    public Integer getSelectedRelacionLaboral() {
        return selectedRelacionLaboral;
    }

    public void setSelectedRelacionLaboral(Integer selectedRelacionLaboral) {
        this.selectedRelacionLaboral = selectedRelacionLaboral;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
