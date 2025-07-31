/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lam.cenicco.beans.administracion.maestro;

import com.lam.cenicco.dao.ProcesoDAO;
import com.lam.cenicco.impl.ControladorWS;
import com.lam.cenicco.librerias.utilidades.CeniccoUtil;
import com.lam.cenicco.util.Util;
import com.lam.cenicco.ws.VistaEmpleadosByjus;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import org.primefaces.context.RequestContext;

/**
 *
 * @author erick
 */
public class MaestroByjusCenicco implements ProcesoDAO<VistaEmpleadosByjus> {

    private List<VistaEmpleadosByjus> relaciones;
    private List<VistaEmpleadosByjus> filteredRelaciones;
//    
    private List<MaestroByjusCenicco.ColumnModel> columns;
//    
    protected List<String> opciones;
//    
    private String[] selectedOpciones;
//    
    private VistaEmpleadosByjus reporte;
//    
    protected Date fechaInicioAlta;
    protected Date fechaFinAlta;
    protected Date fechaInicioBaja;
    protected Date fechaFinBaja;
//    

    public MaestroByjusCenicco() {
        if (this.reporte == null) {
            this.reporte = new VistaEmpleadosByjus();
        }
        if (this.relaciones == null) {
            this.relaciones = new ArrayList<>();
            this.filteredRelaciones = this.relaciones;
        }
//
        if (this.opciones == null) {
            this.inicializarOpciones();
        }


    }

    private void inicializarOpciones() {
        this.opciones = Arrays.asList("No. Empleado", "Nombre", "Edo. Civil", "Sexo", "Fecha Nacimiento", "N.S.S.", "C.U.R.P.", "R.F.C.",
                "Estatus", "Grupo Pago", "Registro Patronal", "Tipo Relación Laboral", "Jefe Directo", "Sistema Antiguedad", "Zona Económica", "S.D.", "S.D.I.", "S. Mensual", "S. Anual",
                "S.D. Anterior", "S.D.I. Anterior", "Fecha Ingreso", "Fecha Antiguedad", "Fecha Vencimiento", "Fecha Baja", "Causa Baja", "Departamento",
                "Puesto", "Área", "Subarea", "Tipo Nómina", "Cuenta Contable", "Nivel", "Número Pensiones", "CCO", "Prestación", "Forma Pago", "Datos Bancarios", "Vales Despensa", "Subcontratación",
                "Jornada Laboral", "Turno", "Sistema Horario", "Sindicalizado", "Infonavit", "Mail", "Telefono", "Payroll ID", "Target Bonus", "Pto Externo", "Depto Externo", "Nivel Externo", "Dirección Nivel 1", "Departamento Nivel 2", "Area Nivel 3");

        this.selectedOpciones = (String[]) this.opciones.toArray();
    }

    @Override
    public void init() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void consultar(ActionEvent event) {
        this.relaciones = ControladorWS.getInstance().getReporteGeneralByjus(this.reporte, this.fechaInicioAlta, this.fechaFinAlta, this.fechaInicioBaja, this.fechaFinBaja,
                null, null);
        this.filteredRelaciones = this.relaciones;
//        
        this.updateColumns();
//        
        String info = CeniccoUtil.getInformacion(this.relaciones.size());
        FacesMessage msg = Util.getFacesMessage(FacesMessage.SEVERITY_INFO, info);
        this.generarMsg(msg, FacesMessage.SEVERITY_INFO.equals(msg.getSeverity()));
    }

    @Override
    public void limpiar(ActionEvent event) {
        this.reporte = new VistaEmpleadosByjus();
        this.fechaInicioAlta = null;
        this.fechaFinAlta = null;
        this.fechaInicioBaja = null;
        this.fechaFinBaja = null;
        this.inicializarOpciones();
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
    public void delete(VistaEmpleadosByjus obj) {
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
    }

    public void updateColumns() {
        this.columns = new ArrayList<>();
//        
        for (String columnKey : this.selectedOpciones) {
            String key = columnKey.trim();
            if (key.equals("No. Empleado")) {
                columns.add(new MaestroByjusCenicco.ColumnModel(columnKey, "numeroempleado"));
            }
            if (key.equals("Nombre")) {
                columns.add(new MaestroByjusCenicco.ColumnModel(columnKey, "nombreempleado"));
            }
            if (key.equals("Edo. Civil")) {
                columns.add(new MaestroByjusCenicco.ColumnModel(columnKey, "estadocivil"));
            }
            if (key.equals("Sexo")) {
                columns.add(new MaestroByjusCenicco.ColumnModel(columnKey, "sexo"));
            }
            if (key.equals("Fecha Nacimiento")) {
                columns.add(new MaestroByjusCenicco.ColumnModel(columnKey, "fechanacimientoStr"));
            }
            if (key.equals("N.S.S.")) {
                columns.add(new MaestroByjusCenicco.ColumnModel(columnKey, "nss"));
            }
            if (key.equals("C.U.R.P.")) {
                columns.add(new MaestroByjusCenicco.ColumnModel(columnKey, "curp"));
            }
            if (key.equals("R.F.C.")) {
                columns.add(new MaestroByjusCenicco.ColumnModel(columnKey, "rfc"));
            }
            if (key.equals("Estatus")) {
                columns.add(new MaestroByjusCenicco.ColumnModel(columnKey, "estatus"));
            }
            if (key.equals("Grupo Pago")) {
                columns.add(new MaestroByjusCenicco.ColumnModel(columnKey, "grupopago"));
                columns.add(new MaestroByjusCenicco.ColumnModel(columnKey, "nombregrupopago"));
            }
            if (key.equals("Registro Patronal")) {
                columns.add(new MaestroByjusCenicco.ColumnModel(columnKey, "registropatronal"));
            }
            if (key.equals("Tipo Relación Laboral")) {
                columns.add(new MaestroByjusCenicco.ColumnModel(columnKey, "tiporelacionlaboral"));
            }
            if (key.equals("Sistema Antiguedad")) {
                columns.add(new MaestroByjusCenicco.ColumnModel(columnKey, "sistemaantiguedad"));
            }
            if (key.equals("Zona Económica")) {
                columns.add(new MaestroByjusCenicco.ColumnModel(columnKey, "zonaeconomica"));
            }
            if (key.equals("S.D.")) {
                columns.add(new MaestroByjusCenicco.ColumnModel(columnKey, "sd"));
            }
            if (key.equals("S.D.I.")) {
                columns.add(new MaestroByjusCenicco.ColumnModel(columnKey, "sdi"));
            }
            if (key.equals("S. Mensual")) {
                columns.add(new MaestroByjusCenicco.ColumnModel(columnKey, "sm"));
            }
            if (key.equals("S. Anual")) {
                columns.add(new MaestroByjusCenicco.ColumnModel(columnKey, "sa"));
            }
            if (key.equals("S.D. Anterior")) {
                columns.add(new MaestroByjusCenicco.ColumnModel(columnKey, "sdanterior"));
            }
            if (key.equals("S.D.I. Anterior")) {
                columns.add(new MaestroByjusCenicco.ColumnModel(columnKey, "sdianterior"));
            }
            if (key.equals("Fecha Ingreso")) {
                columns.add(new MaestroByjusCenicco.ColumnModel(columnKey, "fechaingresoStr"));
            }
            if (key.equals("Fecha Antiguedad")) {
                columns.add(new MaestroByjusCenicco.ColumnModel(columnKey, "fechaantiguedadStr"));
            }
            if (key.equals("Fecha Vencimiento")) {
                columns.add(new MaestroByjusCenicco.ColumnModel(columnKey, "fechavencimientoStr"));
            }
            if (key.equals("Fecha Baja")) {
                columns.add(new MaestroByjusCenicco.ColumnModel(columnKey, "fechabajaStr"));
            }
            if (key.equals("Causa Baja")) {
                columns.add(new MaestroByjusCenicco.ColumnModel(columnKey, "causabaja"));
            }
            if (key.equals("Departamento")) {
                columns.add(new MaestroByjusCenicco.ColumnModel(columnKey, "departamento"));
                columns.add(new MaestroByjusCenicco.ColumnModel("R. Departamento", "responsabledepartamento"));
            }
            if (key.equals("Puesto")) {
                columns.add(new MaestroByjusCenicco.ColumnModel(columnKey, "puesto"));
                columns.add(new MaestroByjusCenicco.ColumnModel(columnKey, "nombrepuesto"));
            }
            if (key.equals("Área")) {
                columns.add(new MaestroByjusCenicco.ColumnModel(columnKey, "area"));
            }
            if (key.equals("Subarea")) {
                columns.add(new MaestroByjusCenicco.ColumnModel(columnKey, "subarea"));
            }
            if (key.equals("Jefe Directo")) {
                columns.add(new MaestroByjusCenicco.ColumnModel("No. Jefe Directo", "noresponsablepuesto"));
                columns.add(new MaestroByjusCenicco.ColumnModel("Jefe Directo", "responsablepuesto"));
            }
            if (key.equals("Tipo Nómina")) {
                columns.add(new MaestroByjusCenicco.ColumnModel(columnKey, "tiponomina"));
            }
            if (key.equals("Cuenta Contable")) {
                columns.add(new MaestroByjusCenicco.ColumnModel(columnKey, "cuentacontable"));
            }
            if (key.equals("Nivel")) {
                columns.add(new MaestroByjusCenicco.ColumnModel(columnKey, "nivelpuesto"));
            }
            if (key.equals("Número Pensiones")) {
                columns.add(new MaestroByjusCenicco.ColumnModel(columnKey, "numeropensiones"));
            }
            if (key.equals("CCO")) {
                columns.add(new MaestroByjusCenicco.ColumnModel(columnKey, "centrocostos"));
                columns.add(new MaestroByjusCenicco.ColumnModel(columnKey, "nombrecentrocostos"));
            }
            if (key.equals("Prestación")) {
                columns.add(new MaestroByjusCenicco.ColumnModel(columnKey, "division"));
            }
            if (key.equals("Forma Pago")) {
                columns.add(new MaestroByjusCenicco.ColumnModel(columnKey, "formapago"));
            }
            if (key.equals("Datos Bancarios")) {
                columns.add(new MaestroByjusCenicco.ColumnModel("No. Cuenta Bancaria", "numerocuentabancaria"));
                columns.add(new MaestroByjusCenicco.ColumnModel("No. CLABE Bancaria", "numerocuentaclabe"));
                 columns.add(new MaestroByjusCenicco.ColumnModel("Banco Empleado", "cuentabancariaempleado"));
                columns.add(new MaestroByjusCenicco.ColumnModel("Banco CIA", "cuentabancaria"));
            }
            if (key.equals("Vales Despensa")) {
                columns.add(new MaestroByjusCenicco.ColumnModel("Cta. Vales Despensa", "ctavales"));
                columns.add(new MaestroByjusCenicco.ColumnModel("Tarjeta Vales Despensa", "tarjetavales"));
            }
            if (key.equals("Jornada Laboral")) {
                columns.add(new MaestroByjusCenicco.ColumnModel(columnKey, "jornada"));
            }
            if (key.equals("Turno")) {
                columns.add(new MaestroByjusCenicco.ColumnModel(columnKey, "turno"));
            }
            if (key.equals("Sistema Horario")) {
                columns.add(new MaestroByjusCenicco.ColumnModel(columnKey, "sistemaantiguedad"));
            }
            if (key.equals("Subcontratación")) {
                columns.add(new MaestroByjusCenicco.ColumnModel(columnKey, "nombresubcontratacion"));
                columns.add(new MaestroByjusCenicco.ColumnModel(columnKey, "rfcsubcontratacion"));
            }
            if (key.equals("Sindicalizado")) {
                columns.add(new MaestroByjusCenicco.ColumnModel(columnKey, "sindicalizado"));
            }
            if (key.equals("Infonavit")) {
                columns.add(new MaestroByjusCenicco.ColumnModel("Fecha Inicio Infonavit", "fechainfonavitStr"));
                columns.add(new MaestroByjusCenicco.ColumnModel("No. Crédito Infonavit", "numerocreditoinfonavit"));
                columns.add(new MaestroByjusCenicco.ColumnModel("Tipo Descuento Infonavit", "tipodescuentoinfonavit"));
                columns.add(new MaestroByjusCenicco.ColumnModel("Descuento Infonavit", "descuentoinfonavit"));
            }
            if (key.equals("Mail")) {
                columns.add(new MaestroByjusCenicco.ColumnModel(columnKey, "mail"));
            }
            if (key.equals("Telefono")) {
                columns.add(new MaestroByjusCenicco.ColumnModel(columnKey, "telefono"));
            }
            if (key.equals("Payroll ID")) {
                columns.add(new MaestroByjusCenicco.ColumnModel(columnKey, "payrollid"));
            }
            if (key.equals("Target Bonus")) {
                columns.add(new MaestroByjusCenicco.ColumnModel(columnKey, "porcentajebono"));
            }
            if (key.equals("Pto Externo")) {
                columns.add(new MaestroByjusCenicco.ColumnModel(columnKey, "puextoexterno"));
            }
            if (key.equals("Depto Externo")) {
                columns.add(new MaestroByjusCenicco.ColumnModel(columnKey, "deptoexterno"));
            }
            if (key.equals("Nivel Externo")) {
                columns.add(new MaestroByjusCenicco.ColumnModel(columnKey, "nivel"));
            }
            if (key.equals("Dirección Nivel 1")) {
                columns.add(new MaestroByjusCenicco.ColumnModel(columnKey, "direccionniveluno"));
            }
            if (key.equals("Departamento Nivel 2")) {
                columns.add(new MaestroByjusCenicco.ColumnModel(columnKey, "departamentoniveldos"));
            }
            if (key.equals("Area Nivel 3")) {
                columns.add(new MaestroByjusCenicco.ColumnModel(columnKey, "areaniveltres"));
            }
        }
    }

    @Override
    public String getInformacion() {
        return CeniccoUtil.getInformacion(this.relaciones.size());
    }

    public Date getFechaInicioAlta() {
        return fechaInicioAlta;
    }

    public void setFechaInicioAlta(Date fechaInicioAlta) {
        this.fechaInicioAlta = fechaInicioAlta;
    }

    public Date getFechaFinAlta() {
        return fechaFinAlta;
    }

    public void setFechaFinAlta(Date fechaFinAlta) {
        this.fechaFinAlta = fechaFinAlta;
    }

    public Date getFechaInicioBaja() {
        return fechaInicioBaja;
    }

    public void setFechaInicioBaja(Date fechaInicioBaja) {
        this.fechaInicioBaja = fechaInicioBaja;
    }

    public Date getFechaFinBaja() {
        return fechaFinBaja;
    }

    public void setFechaFinBaja(Date fechaFinBaja) {
        this.fechaFinBaja = fechaFinBaja;
    }

    public String[] getSelectedOpciones() {
        return selectedOpciones;
    }

    public void setSelectedOpciones(String[] selectedOpciones) {
        this.selectedOpciones = selectedOpciones;
    }

    public List<String> getOpciones() {
        return opciones;
    }

    public List<MaestroByjusCenicco.ColumnModel> getColumns() {
        return columns;
    }

    public List<VistaEmpleadosByjus> getRelaciones() {
        return relaciones;
    }

    public VistaEmpleadosByjus getReporte() {
        return reporte;
    }

    public void setReporte(VistaEmpleadosByjus reporte) {
        this.reporte = reporte;
    }

    public List<VistaEmpleadosByjus> getFilteredRelaciones() {
        return filteredRelaciones;
    }

    public void setFilteredRelaciones(List<VistaEmpleadosByjus> filteredRelaciones) {
        this.filteredRelaciones = filteredRelaciones;
    }

    static public class ColumnModel implements Serializable {

        private String header;
        private String property;

        public ColumnModel(String header, String property) {
            this.header = header;
            this.property = property;
        }

        public String getHeader() {
            return header;
        }

        public String getProperty() {
            return property;
        }
    }
}
