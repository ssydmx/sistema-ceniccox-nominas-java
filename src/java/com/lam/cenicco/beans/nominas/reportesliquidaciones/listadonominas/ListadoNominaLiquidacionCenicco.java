/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lam.cenicco.beans.nominas.reportesliquidaciones.listadonominas;

import com.lam.cenicco.dao.ProcesoDAO;
import com.lam.cenicco.impl.ControladorWS;
import com.lam.cenicco.librerias.utilidades.CeniccoUtil;
import com.lam.cenicco.util.ParametrosReportes;
import com.lam.cenicco.util.paths.MyPaths;
import com.lam.cenicco.ws.GranTotalCargosAbonosTO;
import com.lam.cenicco.ws.Liquidacion;
import com.lam.cenicco.ws.ResumenLiquidacionTO;
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
public class ListadoNominaLiquidacionCenicco implements ProcesoDAO<Liquidacion> {

    protected List<ResumenLiquidacionTO> resumen;
    protected List<ResumenLiquidacionTO> filteredResumen;
//
    protected List<String> liquidacionesCenicco;
//
    protected GranTotalCargosAbonosTO granTotal;
//
    protected Liquidacion listadoNomina;
//
    protected String[] selectedLiquidaciones;

    public ListadoNominaLiquidacionCenicco() {
        if (this.listadoNomina == null) {
            this.listadoNomina = new Liquidacion();
        }
        if (this.resumen == null) {
            this.resumen = new ArrayList<>();
        }
        if (this.liquidacionesCenicco == null) {
            this.liquidacionesCenicco = new ArrayList<>();
        }
    }

    @PostConstruct
    @Override
    public void init() {
//        consultar(null);
    }

    @Override
    public void consultar(ActionEvent event) {
        List<String> aux = new ArrayList<>();
        aux.addAll(Arrays.asList(this.selectedLiquidaciones));
//
        this.resumen = ControladorWS.getInstance().findListadoNominasLiquidaciones(aux);
        for (ResumenLiquidacionTO r : this.resumen) {
            r.setTotalEmpleado(r.getTotal().getPercepcion() - r.getTotal().getDeduccion());
        }
        this.filteredResumen = this.resumen;
        if (this.resumen.size() > 0) {
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
        return CeniccoUtil.getInformacion(this.resumen.size());
    }

    public void descargarReporte() {
        System.out.println("Descarga Reporte...");
        RequestContext context = RequestContext.getCurrentInstance();
//        
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_LIQUIDACIONES_LISTADOS_NOMINAS);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, ParametrosReportes.MODULO_LIQUIDACIONES_LISTADOS_NOMINAS);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.resumen);
//
        context.addCallbackParam("ruta", MyPaths.urlServletReporte());
    }

    public List<ResumenLiquidacionTO> getResumen() {
        return resumen;
    }

    public void setResumen(List<ResumenLiquidacionTO> resumen) {
        this.resumen = resumen;
    }

    public List<ResumenLiquidacionTO> getFilteredResumen() {
        return filteredResumen;
    }

    public void setFilteredResumen(List<ResumenLiquidacionTO> filteredResumen) {
        this.filteredResumen = filteredResumen;
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

    public GranTotalCargosAbonosTO getGranTotal() {
        return granTotal;
    }

    public void setGranTotal(GranTotalCargosAbonosTO granTotal) {
        this.granTotal = granTotal;
    }

    public Liquidacion getListadoNomina() {
        return listadoNomina;
    }

    public void setListadoNomina(Liquidacion listadoNomina) {
        this.listadoNomina = listadoNomina;
    }

    public String[] getSelectedLiquidaciones() {
        return selectedLiquidaciones;
    }

    public void setSelectedLiquidaciones(String[] selectedLiquidaciones) {
        this.selectedLiquidaciones = selectedLiquidaciones;
    }
}
