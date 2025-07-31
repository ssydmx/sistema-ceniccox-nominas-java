/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lam.cenicco.util;

/**
 *
 * @author JoséAntonio
 */
public enum Modulos {

    KARDEX_GENERALES("Kardex-Generales"),
    KARDEX_POSICIONES("Kardex-Posiciones"),
    KARDEX_RELACION_LABORAL("Kardex-RelaciónLaboral"),
    KARDEX_RELACION_LABORAL_MASIVOS("Kardex-RelaciónLaboral, Altas Masivas"),
    KARDEX_SUELDOS("Kardex-Sueldos"),
    KARDEX_CUENTAS_BANCARIAS("Kardex-CuentasBancarias"),
    KARDEX_INFONAVIT("Kardex-Infonavit"),
    KARDEX_PENSIONES("Kardex-Pensiones"),
    KARDEX_VARIABLES("Kardex-Variables"),
    KARDEX_ALTA_EMPLEADO("Kardex-AltaEmpleado"),
    KARDEX_DOMICILIO("Kardex-Domicilio"),
    CREDITOS_ALTAS("Créditos-Altas"),
    CREDITOS_MODIFICACIONES("Créditos-VistaPorCrédito"),
    FINIQUITOS_ALTAS("Finiquitos-Altas"),
    //    
    VARIABILIDAD_BIMESTRAL("VARIABILIDADBIMESTRAL"),
    MODIFICACION_IDSE("MODIFICACIONIDSE"),
    BAJAS_IDSE("BAJASIDSE"),
    ALTAS_IDSE("ALTASIDSE"),
    ENTREGABLES_LIQUIDACIONES("LIQUIDACIONES"),
    ENTREGABLES_NOMINA("NOMINAS"),
    ENTREGABLES_ACUMULADOS("ACUMULADOS"),
    VISTA_POR_CREDITOS("CONSULTACREDITOS"),
    VISTA_POR_AMORTIZACION("CONSULTAAMORTIZACION"),
    ALTAS_CREDITOS("ALTACREDITOS"),
    CONSULTA_FINIQUITOS("CONSULTAFINIQUITOS"),
    CALCULO_FINIQUITOS("CALCULOFINIQUITOS"),
    CALCULO_NOMINA("CALCULONOMINAS"),
    OPERACIONES_INCIDENCIAS("INCIDENCIAS"),
    TIMBRES_REPORTES("REPORTETIMBRES"),
    TIMBRES_RECIBOS("INTERFACETIMBRES"),
    REPORTE_MAESTRO("REPORTEMAESTRO"),
    CATALOGO_EMPLEADOS("CATALOGOEMPLEADOS"),
    VACACIONES("VACACIONES"),
    DISPERSION_BANCOMER("DISPERSIONBANCOMER");
    private String concepto;

    private Modulos(String concepto) {
        this.concepto = concepto;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }
}
