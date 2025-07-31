/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lam.cenicco.beans.administracion.informacioncomplementaria;

import com.lam.cenicco.dao.ProcesoDAO;
import com.lam.cenicco.impl.ControladorWS;
import com.lam.cenicco.librerias.mensajes.Mensajes;
import com.lam.cenicco.util.Util;
import com.lam.cenicco.ws.Empleado;
import com.lam.cenicco.ws.InformacionComplementaria;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import org.primefaces.context.RequestContext;

/**
 *
 * @author JoséAntonio
 */
public class InformacionComplementariaCenicco implements ProcesoDAO<InformacionComplementaria> {

    protected InformacionComplementaria informacionComplementaria;
//
    protected Empleado empleado;
//    

    @Override
    public void consultar(ActionEvent event) {

        this.informacionComplementaria = ControladorWS.getInstance().getInformacionComplemenatariaByIdEmpleado(this.empleado.getIdempleado());

        if (this.informacionComplementaria == null) {
            this.informacionComplementaria = new InformacionComplementaria();
        }
    }

    @Override
    public void limpiar(ActionEvent event) {
        this.consultar(event);
    }

    @Override
    public void create(ActionEvent event) {
        boolean isValidate = ControladorWS.getInstance().editInformacionComplementaria(this.empleado, this.informacionComplementaria);
        System.out.println("IsValidate: " + isValidate);
//        
        FacesMessage msg = isValidate ? Util.getFacesMessage(FacesMessage.SEVERITY_INFO, Mensajes.EXITOSO_EDITAR_INFORMACION_COMPLEMENTARIA)
                : Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, Mensajes.ERROR_EDITAR_INFORMACION_COMPLEMENTARIA);
//      
        this.generarMsg(msg, isValidate);
    }

    @Override
    public void edit(ActionEvent event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void delete(InformacionComplementaria obj) {
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void init() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void setInformacionComplementaria(InformacionComplementaria informacionComplementaria) {
        this.informacionComplementaria = informacionComplementaria;
    }

    public InformacionComplementaria getInformacionComplementaria() {
        return informacionComplementaria;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }
}
