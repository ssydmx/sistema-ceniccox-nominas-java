/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lam.cenicco.beans.administracion.rotacionpersonal;

import com.lam.cenicco.dao.ProcesoDAO;
import com.lam.cenicco.impl.ControladorWS;
import com.lam.cenicco.librerias.utilidades.CeniccoUtil;
import com.lam.cenicco.ws.VistaEmpleados;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.event.ActionEvent;
import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.ChartSeries;

/**
 *
 * @author Antonio Durán
 */
public class RotacionPersonalCenicco implements ProcesoDAO<VistaEmpleados> {

    private List<VistaEmpleados> vista;
    private List<VistaEmpleados> filteredVista;
//    
    private Integer selectedGrupoPago;
    private Integer mesinicio;
    private Integer mesfin;
    private Integer anio;
//    
    private Integer totalIngresos;
//    
    private Integer totalBajas;
    //    
    private CartesianChartModel barModel;

    @Override
    @PostConstruct
    public void init() {
        this.limpiar(null);
    }

    @Override
    public void limpiar(ActionEvent event) {
        this.vista = new ArrayList<>();
        this.filteredVista = new ArrayList<>();
//        
        this.selectedGrupoPago = null;
        this.mesinicio = null;
        this.mesfin = null;
        this.anio = null;
//        
        this.totalBajas = 0;
        this.totalIngresos = 0;
//
        this.barModel = new CartesianChartModel();
    }

    @Override
    public void consultar(ActionEvent event) {
        this.vista = ControladorWS.getInstance().getRotacionPersonal(selectedGrupoPago, mesinicio, mesfin, anio);
        this.filteredVista = this.vista;
//        
        this.barModel = new CartesianChartModel();
//        
        this.totalBajas = 0;
        this.totalIngresos = 0;
//        
        if (!this.vista.isEmpty()) {
//            
            ChartSeries seriesA = new ChartSeries();
            seriesA.setLabel("Ingresos");
//        
            ChartSeries seriesB = new ChartSeries();
            seriesB.setLabel("Bajas");
//            
            for (VistaEmpleados v : this.vista) {
                seriesA.set(v.getMesStr(), v.getContadorIngresos());
                seriesB.set(v.getMesStr(), v.getContadorBajas());
//                
                this.totalIngresos += v.getContadorIngresos();
                this.totalBajas += v.getContadorBajas();
//                
            }
//            
            this.barModel.addSeries(seriesA);
            this.barModel.addSeries(seriesB);
//            
        }
//        
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
    public void delete(VistaEmpleados obj) {
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
        return CeniccoUtil.getInformacion(this.vista.size());
    }

    public List<VistaEmpleados> getFilteredVista() {
        return filteredVista;
    }

    public void setFilteredVista(List<VistaEmpleados> filteredVista) {
        this.filteredVista = filteredVista;
    }

    public Integer getSelectedGrupoPago() {
        return selectedGrupoPago;
    }

    public void setSelectedGrupoPago(Integer selectedGrupoPago) {
        this.selectedGrupoPago = selectedGrupoPago;
    }

    public Integer getMesinicio() {
        return mesinicio;
    }

    public void setMesinicio(Integer mesinicio) {
        this.mesinicio = mesinicio;
    }

    public Integer getMesfin() {
        return mesfin;
    }

    public void setMesfin(Integer mesfin) {
        this.mesfin = mesfin;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public List<VistaEmpleados> getVista() {
        return vista;
    }

    public CartesianChartModel getBarModel() {
        return barModel;
    }

    public Integer getTotalBajas() {
        return totalBajas;
    }

    public Integer getTotalIngresos() {
        return totalIngresos;
    }
}
