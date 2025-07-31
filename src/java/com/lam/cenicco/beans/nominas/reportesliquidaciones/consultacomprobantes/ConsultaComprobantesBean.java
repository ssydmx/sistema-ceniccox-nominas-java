/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lam.cenicco.beans.nominas.reportesliquidaciones.consultacomprobantes;

import com.lam.cenicco.librerias.utilidades.CeniccoUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Named;

/**
 *
 * @author Antonio Durán
 */
@Named(value = "consultacomprobantesBean")
@SessionScoped
public class ConsultaComprobantesBean implements Serializable {
//    

    private List<RespuestaTimbreTO> comprobantes;
//    
    private List<RespuestaTimbreTO> filteredcomprobantes;
//
    private String fechainicio;
//    
    private String fechafin;
//    
    private String selectedrfc;
    //

    public ConsultaComprobantesBean() {
        if (this.comprobantes == null) {
            this.comprobantes = new ArrayList<>();
            this.filteredcomprobantes = this.comprobantes;
        }
    }

    public void consultar(ActionEvent event) {
//        System.out.println("ConsultaComprobantes... ");
//        String servicio = ControladorSesiones.getInstance().getUsuarioSession().getServicio();
//        System.out.println("Servicio.... " + servicio);
//        this.comprobantes = FacturaCenicco.getInstance().consultarComprobantes(this.selectedrfc, this.fechainicio, this.fechafin);
//        try {
//            FacturaCenicco.getInstance().obtenerXML(this.comprobantes);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        this.filteredcomprobantes = this.comprobantes;
    }
//    

    public String getInformacion() {
        return CeniccoUtil.getInformacion(this.comprobantes.size());
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

    public List<RespuestaTimbreTO> getFilteredcomprobantes() {
        return filteredcomprobantes;
    }

    public void setFilteredcomprobantes(List<RespuestaTimbreTO> filteredcomprobantes) {
        this.filteredcomprobantes = filteredcomprobantes;
    }

    public List<RespuestaTimbreTO> getComprobantes() {
        return comprobantes;
    }

    public String getSelectedrfc() {
        return selectedrfc;
    }

    public void setSelectedrfc(String selectedrfc) {
        this.selectedrfc = selectedrfc;
    }
}
