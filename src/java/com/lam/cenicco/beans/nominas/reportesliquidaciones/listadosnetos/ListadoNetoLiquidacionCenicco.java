/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lam.cenicco.beans.nominas.reportesliquidaciones.listadosnetos;

import com.lam.cenicco.dao.ProcesoDAO;
import com.lam.cenicco.impl.ControladorWS;
import com.lam.cenicco.librerias.utilidades.CeniccoUtil;
import com.lam.cenicco.util.ParametrosReportes;
import com.lam.cenicco.util.paths.MyPaths;
import com.lam.cenicco.ws.Liquidacion;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import org.primefaces.context.RequestContext;

/**
 *
 * @author iperez
 */
public class ListadoNetoLiquidacionCenicco implements ProcesoDAO<Liquidacion> {
//

    protected List<Liquidacion> listadosNetos;
    protected List<Liquidacion> filteredListadosNetos;
//
    protected List<String> liquidacionesCenicco;
//
    protected Liquidacion listadoNeto;
//
    protected String[] selectedLiquidaciones;

    public ListadoNetoLiquidacionCenicco() {
        if (this.listadosNetos == null) {
            this.listadosNetos = new ArrayList<>();
        }
        if (this.liquidacionesCenicco == null) {
            this.liquidacionesCenicco = new ArrayList<>();
        }
        if (this.listadoNeto == null) {
            this.listadoNeto = new Liquidacion();
        }
    }

    @PostConstruct
    @Override
    public void init() {
    }

    @Override
    public void consultar(ActionEvent event) {
        List<String> aux = new ArrayList<>();
        aux.addAll(Arrays.asList(this.selectedLiquidaciones));
        this.listadosNetos = ControladorWS.getInstance().findListadosNetosLiquidaciones(aux);
        this.filteredListadosNetos = this.listadosNetos;
        if (this.listadosNetos.size() > 0) {
            this.selectedLiquidaciones = new String[0];
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
    public void delete(Liquidacion obj) {
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
        return CeniccoUtil.getInformacion(this.listadosNetos.size());
    }
    
    public void descargarReporte() {
        System.out.println("Descarga Reporte...");
        RequestContext context = RequestContext.getCurrentInstance();
//        
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_LIQUIDACIONES_LISTADOS_NETOS);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, ParametrosReportes.MODULO_LIQUIDACIONES_LISTADOS_NETOS);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.listadosNetos);
//
        context.addCallbackParam("ruta", MyPaths.urlServletReporte());
    }

    public List<Liquidacion> getListadosNetos() {
        return listadosNetos;
    }

    public void setListadosNetos(List<Liquidacion> listadosNetos) {
        this.listadosNetos = listadosNetos;
    }

    public List<Liquidacion> getFilteredListadosNetos() {
        return filteredListadosNetos;
    }

    public void setFilteredListadosNetos(List<Liquidacion> filteredListadosNetos) {
        this.filteredListadosNetos = filteredListadosNetos;
    }

    public List<String> getLiquidacionesCenicco() {
        Map<Integer, String> mapaLiquidaciones = new HashMap<>();
        this.liquidacionesCenicco = new ArrayList<>();
//        
        List<Liquidacion> liquidacionesAux = ControladorWS.getInstance().getLiquidaciones();
//        
        for (Liquidacion l : liquidacionesAux) {
            Integer llave = l.getNumeroliquidacion();
            if (mapaLiquidaciones.get(llave) == null) {
                this.liquidacionesCenicco.add(l.getNumeroliquidacion().toString());
                mapaLiquidaciones.put(llave, l.getNumeroliquidacion().toString());
            }
        }
        return liquidacionesCenicco;
    }
    
    public void setLiquidacionesCenicco(List<String> liquidacionesCenicco) {
        this.liquidacionesCenicco = liquidacionesCenicco;
    }

    public Liquidacion getListadoNeto() {
        return listadoNeto;
    }

    public void setListadoNeto(Liquidacion listadoNeto) {
        this.listadoNeto = listadoNeto;
    }

    public String[] getSelectedLiquidaciones() {
        return selectedLiquidaciones;
    }

    public void setSelectedLiquidaciones(String[] selectedLiquidaciones) {
        this.selectedLiquidaciones = selectedLiquidaciones;
    }
}
