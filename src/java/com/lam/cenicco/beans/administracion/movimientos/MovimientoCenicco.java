/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lam.cenicco.beans.administracion.movimientos;

import com.lam.cenicco.dao.ProcesoDAO;
import com.lam.cenicco.impl.ControladorWS;
import com.lam.cenicco.librerias.utilidades.CeniccoUtil;
import com.lam.cenicco.ws.Movimiento;
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.event.ActionEvent;

/**
 *
 * @author Antonio Durán
 */
public class MovimientoCenicco implements ProcesoDAO<Movimiento> {
//

    private Integer selectedusuario;
    private Integer selectedgrupopago;
//    
    private String numeroempleado;
    private String tipomovimiento;
    private String modulo;
    private String fechainicio;
    private String fechafin;
//    
    private List<Movimiento> movimientos;
    private List<Movimiento> filteredmovimientos;

    //
    public MovimientoCenicco() {
        if (this.movimientos == null) {
            this.movimientos = new ArrayList<>();
        }
        if (this.filteredmovimientos == null) {
            this.filteredmovimientos = new ArrayList<>();
        }
    }

    @Override
    public void consultar(ActionEvent event) {
        this.movimientos = ControladorWS.getInstance().findMovimientos(this.tipomovimiento, this.selectedusuario, this.selectedgrupopago,
                this.numeroempleado, this.modulo, this.fechainicio, this.fechafin);
        this.filteredmovimientos = this.movimientos;
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
    public void delete(Movimiento obj) {
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
        return CeniccoUtil.getInformacion(this.movimientos.size());
    }

    @Override
    public void init() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Integer getSelectedusuario() {
        return selectedusuario;
    }

    public void setSelectedusuario(Integer selectedusuario) {
        this.selectedusuario = selectedusuario;
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

    public String getTipomovimiento() {
        return tipomovimiento;
    }

    public void setTipomovimiento(String tipomovimiento) {
        this.tipomovimiento = tipomovimiento;
    }

    public String getModulo() {
        return modulo;
    }

    public void setModulo(String modulo) {
        this.modulo = modulo;
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

    public List<Movimiento> getFilteredmovimientos() {
        return filteredmovimientos;
    }

    public void setFilteredmovimientos(List<Movimiento> filteredmovimientos) {
        this.filteredmovimientos = filteredmovimientos;
    }

    public List<Movimiento> getMovimientos() {
        return movimientos;
    }
}
