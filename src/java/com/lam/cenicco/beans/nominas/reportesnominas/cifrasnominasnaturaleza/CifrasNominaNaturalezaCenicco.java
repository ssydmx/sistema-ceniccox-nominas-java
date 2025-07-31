/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lam.cenicco.beans.nominas.reportesnominas.cifrasnominasnaturaleza;

import com.lam.cenicco.dao.ProcesoDAO;
import com.lam.cenicco.impl.ControladorWS;
import com.lam.cenicco.librerias.utilidades.CeniccoUtil;
import com.lam.cenicco.util.ParametrosReportes;
import com.lam.cenicco.util.paths.MyPaths;
import com.lam.cenicco.ws.CifrasNomina;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import org.primefaces.context.RequestContext;

/**
 *
 * @author Antonio Durán
 */
public class CifrasNominaNaturalezaCenicco implements ProcesoDAO<CifrasNomina> {

    protected Integer mesinicio;
    protected Integer mesfin;
    protected Integer anio;
    protected Integer idgrupopago;
//    
    protected double importeexento;
    protected double importegravado;
//    
    protected Integer selectedPeriodoMes;
//    
    protected List<CifrasNomina> cifrasnomina;
    protected List<CifrasNomina> filteredcifrasnomina;

    public CifrasNominaNaturalezaCenicco() {
//        
        if (this.cifrasnomina == null) {
            this.cifrasnomina = new ArrayList<>();
        }
        if (this.filteredcifrasnomina == null) {
            this.filteredcifrasnomina = new ArrayList<>();
        }
//        
    }

    @Override
    public void consultar(ActionEvent event) {
        this.importeexento = 0.0;
        this.importegravado = 0.0;
//        
        this.cifrasnomina = ControladorWS.getInstance().findCifrasNominaByNaturaleza(this.mesinicio, this.mesfin, this.anio, this.idgrupopago, this.selectedPeriodoMes);
//        
        System.out.println("CifrasNominaNaturaleza... " + this.cifrasnomina.size());
//        
        this.filteredcifrasnomina = this.cifrasnomina;
        Iterator<CifrasNomina> iter = this.cifrasnomina.iterator();
        while (iter.hasNext()) {
            CifrasNomina c = iter.next();
//            
            this.importeexento += c.getAbono();
            this.importegravado += c.getCargo();
//            
        }
    }

    public void descargarReporte() {
        if (!this.cifrasnomina.isEmpty()) {
            System.out.println("Descarga Reporte Gravados/Exentos...");
            RequestContext context = RequestContext.getCurrentInstance();
//        
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_CIFRAS_NOMINAS_GRAVADOS_EXENTOS);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, ParametrosReportes.MODULO_CIFRAS_NOMINAS_GRAVADOS_EXENTOS);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.PARAMETRO_IMPORTE_GRAVADO, this.importegravado);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.PARAMETRO_IMPORTE_EXENTO, this.importeexento);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.PARAMETRO_PERIODO, this.mesinicio + "/" + this.mesfin + "/" + this.anio);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.filteredcifrasnomina);
//
            context.addCallbackParam("ruta", MyPaths.urlServletReporte());
        }
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
    public void delete(CifrasNomina obj) {
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
        return CeniccoUtil.getInformacion(this.cifrasnomina.size());
    }

    @Override
    public void init() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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

    public List<CifrasNomina> getFilteredcifrasnomina() {
        return filteredcifrasnomina;
    }

    public void setFilteredcifrasnomina(List<CifrasNomina> filteredcifrasnomina) {
        this.filteredcifrasnomina = filteredcifrasnomina;
    }

    public double getImporteexento() {
        return importeexento;
    }

    public double getImportegravado() {
        return importegravado;
    }

    public List<CifrasNomina> getCifrasnomina() {
        return cifrasnomina;
    }

    public Integer getSelectedPeriodoMes() {
        return selectedPeriodoMes;
    }

    public void setSelectedPeriodoMes(Integer selectedPeriodoMes) {
        this.selectedPeriodoMes = selectedPeriodoMes;
    }

    public Integer getIdgrupopago() {
        return idgrupopago;
    }

    public void setIdgrupopago(Integer idgrupopago) {
        this.idgrupopago = idgrupopago;
    }
}
