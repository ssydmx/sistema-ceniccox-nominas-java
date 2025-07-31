/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lam.cenicco.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author JoséAntonio
 */
@Named(value = "urlsBean")
@SessionScoped
public class UrlsBean implements Serializable {
    //    

    private Map<String, String> mapaUrls;
//    
    private List<SelectItem> comboUrl;
//    
    private String selectedModulo;

    public UrlsBean() {
        if (this.mapaUrls == null) {
            this.mapaUrls = new HashMap<>();
        }
        if (this.comboUrl == null) {
            this.comboUrl = new ArrayList<>();
        }
    }

    public void cambiarURL(ValueChangeEvent e) {
        String valor = e.getNewValue().toString();
        try {
            FacesContext fc = FacesContext.getCurrentInstance();
            fc.getExternalContext().redirect(valor);
        } catch (Exception ex) {
        }
    }

    private void actualizarCombo() {
        HttpServletRequest origRequest = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String modulo = null;
        if (origRequest.getRequestURL().toString().contains("fiscal/altasidse")) {
            modulo = "Altas IDSE";
        }
        if (origRequest.getRequestURL().toString().contains("fiscal/bajasidse")) {
            modulo = "Bajas IDSE";
        }
        if (origRequest.getRequestURL().toString().contains("fiscal/sueldosidse")) {
            modulo = "Modificación Salario IDSE";
        }
        if (origRequest.getRequestURL().toString().contains("fiscal/sueldosidse")) {
            modulo = "Modificación Salario IDSE";
        }
        if (origRequest.getRequestURL().toString().contains("fiscal/confrontaidse")) {
            modulo = "Confronta IDSE";
        }
        if (origRequest.getRequestURL().toString().contains("fiscal/ingresossua")) {
            modulo = "Ingresos SUA";
        }
        if (origRequest.getRequestURL().toString().contains("fiscal/movimientossua")) {
            modulo = "Movimientos SUA";
        }
        if (origRequest.getRequestURL().toString().contains("fiscal/variabilidadbimestral")) {
            modulo = "Variabilidad Bimestral";
        }
        if (origRequest.getRequestURL().toString().contains("empleados/catalogo")) {
            modulo = "Catálogo Empleados";
        }
        if (origRequest.getRequestURL().toString().contains("empleados/reportes")) {
            modulo = "Reporte Maestro";
        }
        if (origRequest.getRequestURL().toString().contains("empleados/sueldos")) {
            modulo = "Histórico Sueldos";
        }
        if (origRequest.getRequestURL().toString().contains("empleados/reportedependientes")) {
            modulo = "Reporte Dependientes";
        }
        if (origRequest.getRequestURL().toString().contains("empleados/rotacionpersonal")) {
            modulo = "Rotación Personal";
        }
        if (origRequest.getRequestURL().toString().contains("nominas/timbres_reportes")) {
            modulo = "Reporte Timbres";
        }
        if (origRequest.getRequestURL().toString().contains("nominas/consultacomprobantes")) {
            modulo = "Consulta Comprobantes";
        }
        if (origRequest.getRequestURL().toString().contains("nominas/incidencias")) {
            modulo = "Incidencias";
        }
        if (origRequest.getRequestURL().toString().contains("nominas/calculonomina")) {
            modulo = "Cálculo Nóminas";
        }
        if (origRequest.getRequestURL().toString().contains("nominas/calculonomina")) {
            modulo = "Cálculo Nóminas";
        }
        if (origRequest.getRequestURL().toString().contains("nominas/comparacion")) {
            modulo = "Resúmen Nóminas";
        }
        if (origRequest.getRequestURL().toString().contains("nominas/calculoptu")) {
            modulo = "Cálculo PTU";
        }
        if (origRequest.getRequestURL().toString().contains("nominas/calculofiniquitos")) {
            modulo = "Cálculo Finiquitos";
        }
        if (origRequest.getRequestURL().toString().contains("nominas/consultasfiniquitos")) {
            modulo = "Consulta Finiquitos";
        }
        if (origRequest.getRequestURL().toString().contains("nominas/calculoaguinaldo")) {
            modulo = "Cálculo Aguinaldo";
        }
        if (origRequest.getRequestURL().toString().contains("nominas/vacaciones")) {
            modulo = "Vacaciones";
        }
        if (origRequest.getRequestURL().toString().contains("nominas/incapacidades")) {
            modulo = "Registro Incapacidades";
        }
        if (origRequest.getRequestURL().toString().contains("nominas/consultasincapacidades")) {
            modulo = "Consulta Incapacidades";
        }
        if (origRequest.getRequestURL().toString().contains("nominas/altascreditos")) {
            modulo = "Alta Créditos";
        }
        if (origRequest.getRequestURL().toString().contains("nominas/consultascreditos")) {
            modulo = "Consulta Créditos";
        }
        if (origRequest.getRequestURL().toString().contains("nominas/consultasamortizacion")) {
            modulo = "Consulta Amortización";
        }
        if (origRequest.getRequestURL().toString().contains("nominas/saldocreditos")) {
            modulo = "Saldos Créditos";
        }
        if (origRequest.getRequestURL().toString().contains("nominas/sobregiros")) {
            modulo = "Sobregiros";
        }
        if (origRequest.getRequestURL().toString().contains("nominas/entregables")) {
            modulo = "Entregables";
        }
        if (origRequest.getRequestURL().toString().contains("nominas/dispersion")) {
            modulo = "Dispersión";
        }
        if (origRequest.getRequestURL().toString().contains("nominas/polizacontable")) {
            modulo = "Póliza Contable";
        }
        if (origRequest.getRequestURL().toString().contains("nominas/valesdespensa")) {
            modulo = "Vales de Despensa";
        }
        if (origRequest.getRequestURL().toString().contains("nominas/conceptoxperiodo")) {
            modulo = "Conceptos Por Periodo";
        }
        if (origRequest.getRequestURL().toString().contains("nominas/mensualxconcepto")) {
            modulo = "Reportes Mensuales";
        }
        if (origRequest.getRequestURL().toString().contains("configuracion/timbres")) {
            modulo = "Información PAC";
        }
        if (origRequest.getRequestURL().toString().contains("configuracion/companias")) {
            modulo = "Catálogo Compañías";
        }
        if (origRequest.getRequestURL().toString().contains("configuracion/catalogorecursoshumanos")) {
            modulo = "Catálogo Puestos/Deptos";
        }
        if (origRequest.getRequestURL().toString().contains("configuracion/bancos")) {
            modulo = "Catálogo Bancos";
        }
        if (origRequest.getRequestURL().toString().contains("configuracion/catalogoexpedientes")) {
            modulo = "Catálogo Expedientes";
        }
        if (origRequest.getRequestURL().toString().contains("configuracion/catalogogeneral")) {
            modulo = "Catálogo General";
        }
        if (origRequest.getRequestURL().toString().contains("configuracion/catalogogeografico")) {
            modulo = "Catálogo Geográfico";
        }
        if (origRequest.getRequestURL().toString().contains("configuracion/catalogoconstanciassat")) {
            modulo = "Catálogo Constancias SAT";
        }
        if (origRequest.getRequestURL().toString().contains("configuracion/catalogoprocesos")) {
            modulo = "Catálogo Tipos Procesos";
        }
        if (origRequest.getRequestURL().toString().contains("configuracion/catalogorelacionlaboral")) {
            modulo = "Catálogo Relación Laboral";
        }
        if (origRequest.getRequestURL().toString().contains("configuracion/usuarios")) {
            modulo = "Administración Usuarios";
        }
        if (origRequest.getRequestURL().toString().contains("configuracion/usuarios")) {
            modulo = "Administración Usuarios";
        }
        if (origRequest.getRequestURL().toString().contains("configuracion/perfiles")) {
            modulo = "Administración Perfiles";
        }
        if (origRequest.getRequestURL().toString().contains("configuracion/parametros")) {
            modulo = "Parámetros";
        }
        if (origRequest.getRequestURL().toString().contains("configuracion/catalogoperiodos")) {
            modulo = "Calendario Procesos";
        }
        if (origRequest.getRequestURL().toString().contains("configuracion/sistemaantiguedad")) {
            modulo = "Catálogo Sistemas Antigüedad";
        }
        if (origRequest.getRequestURL().toString().contains("configuracion/tarifas")) {
            modulo = "Catálogo Tarifas";
        }
        if (origRequest.getRequestURL().toString().contains("configuracion/conceptos")) {
            modulo = "Catálogo Conceptos";
        }
        if (origRequest.getRequestURL().toString().contains("configuracion/grupoconceptos")) {
            modulo = "Catálogo Grupos Conceptos";
        }
        if (origRequest.getRequestURL().toString().contains("configuracion/variablesformula")) {
            modulo = "Catálogo Variables";
        }
        if (origRequest.getRequestURL().toString().contains("configuracion/sesiones")) {
            modulo = "Consola Sesiones";
        }
        if (origRequest.getRequestURL().toString().contains("configuracion/conexion")) {
            modulo = "Conexiones";
        }
//        
        if (modulo != null && this.mapaUrls.get(modulo) == null) {
            this.comboUrl.add(new SelectItem(origRequest.getRequestURL().toString(), modulo));
            this.mapaUrls.put(modulo, origRequest.getRequestURL().toString());
        }
        this.selectedModulo = origRequest.getRequestURL().toString();
    }

    public List<SelectItem> getComboUrl() {
        this.actualizarCombo();
        return comboUrl;
    }

    public String getSelectedModulo() {
        return selectedModulo;
    }

    public void setSelectedModulo(String selectedModulo) {
        this.selectedModulo = selectedModulo;
    }
}
