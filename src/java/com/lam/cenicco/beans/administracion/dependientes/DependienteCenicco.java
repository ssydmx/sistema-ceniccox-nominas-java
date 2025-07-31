/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lam.cenicco.beans.administracion.dependientes;

import com.lam.cenicco.dao.ProcesoDAO;
import com.lam.cenicco.impl.ControladorWS;
import com.lam.cenicco.librerias.utilidades.CeniccoUtil;
import com.lam.cenicco.util.Util;
import com.lam.cenicco.ws.Dependiente;
import com.lam.cenicco.ws.Empleado;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import org.primefaces.context.RequestContext;

/**
 *
 * @author Antonio Durán
 */
public class DependienteCenicco implements ProcesoDAO<Dependiente> {

    protected Dependiente dependiente;
//   
    protected List<Dependiente> dependientes;
//    
    protected Empleado empleado;
//
    protected String fechanacimiento;
//    

    public DependienteCenicco() {
        if (this.dependiente == null) {
            this.dependiente = new Dependiente();
        }
    }

    @PostConstruct
    @Override
    public void init() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void consultar(ActionEvent event) {
        this.dependientes = ControladorWS.getInstance().getDependientes(this.empleado.getIdempleado());
    }

    @Override
    public void limpiar(ActionEvent event) {
        this.dependiente = new Dependiente();
        this.fechanacimiento = null;
        this.consultar(event);
    }

    @Override
    public void create(ActionEvent event) {
        boolean validate = ControladorWS.getInstance().createDependiente(this.dependiente, this.empleado.getIdempleado(), this.fechanacimiento);
        FacesMessage msg = validate ? Util.getFacesMessage(FacesMessage.SEVERITY_INFO, "El Dependiente fué creado exitosamente!")
                : Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, "Error al crear al Dependiente");
        this.generarMsg(msg, validate);
    }

    public void editar(Dependiente d) {
        boolean validate = ControladorWS.getInstance().editDependiente(d);
        FacesMessage msg = validate ? Util.getFacesMessage(FacesMessage.SEVERITY_INFO, "El Dependiente fué modificado exitosamente!")
                : Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, "Error al modificar al Dependiente");
        this.generarMsg(msg, validate);
    }

    @Override
    public void delete(Dependiente d) {
        boolean validate = ControladorWS.getInstance().deleteDependiente(d);
        FacesMessage msg = validate ? Util.getFacesMessage(FacesMessage.SEVERITY_INFO, "El Dependiente fué eliminado exitosamente!")
                : Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, "Error al eliminar el Dependiente");
        this.generarMsg(msg, validate);
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
            this.limpiar(null);
        }
    }

    @Override
    public String getInformacion() {
        return CeniccoUtil.getInformacion(this.dependientes.size());
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    public Dependiente getDependiente() {
        return dependiente;
    }

    public void setDependiente(Dependiente dependiente) {
        this.dependiente = dependiente;
    }

    public List<Dependiente> getDependientes() {
        return dependientes;
    }

    public String getFechanacimiento() {
        return fechanacimiento;
    }

    public void setFechanacimiento(String fechanacimiento) {
        this.fechanacimiento = fechanacimiento;
    }
}
