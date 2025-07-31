/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lam.cenicco.beans.administracion.maestro;

import com.lam.cenicco.dao.ProcesoDAO;
import com.lam.cenicco.impl.ControladorWS;
import com.lam.cenicco.librerias.utilidades.CeniccoUtil;
import com.lam.cenicco.util.ControladorSesiones;
import com.lam.cenicco.util.ParametrosReportes;
import com.lam.cenicco.util.Util;
import com.lam.cenicco.ws.Empleado;
import com.lam.cenicco.ws.VistaEmpleados;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlGraphicImage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.swing.CellEditor;
import org.primefaces.component.export.CSVExporter;
import org.primefaces.context.RequestContext;

/**
 *
 * @author JoséAntonio
 */
public class MaestroCenicco implements ProcesoDAO<VistaEmpleados> {

    private List<VistaEmpleados> relaciones;
    private List<VistaEmpleados> filteredRelaciones;
//    
    private List<ColumnModel> columns;
//    
    protected List<String> opciones;
//    
    private String[] selectedOpciones;
//    
    private VistaEmpleados reporte;
//    
    protected Date fechaInicioAlta;
    protected Date fechaFinAlta;
    protected Date fechaInicioBaja;
    protected Date fechaFinBaja;
//    

    public MaestroCenicco() {
        if (this.reporte == null) {
            this.reporte = new VistaEmpleados();
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
        if (ControladorSesiones.getInstance().getCompaniaSession().getNombreCorto().equals("DELMONTE")) {
            this.opciones = Arrays.asList("No. Empleado", "Nombre", "Edo. Civil", "Sexo", "Fecha Nacimiento", "N.S.S.", "C.U.R.P.", "R.F.C.",
                    "Estatus", "Grupo Pago", "Registro Patronal", "Tipo Relación Laboral", "Jefe Directo", "Sistema Antiguedad", "Zona Económica", "S.D.", "S.D.I.",
                    "S.D. Anterior", "S.D.I. Anterior", "Fecha Ingreso", "Fecha Antiguedad", "Fecha Vencimiento", "Fecha Baja", "Causa Baja", "Departamento",
                    "Puesto", "Área", "Cuadrilla", "Tipo Nómina", "Cuenta Contable", "Nivel", "Número Pensiones", "CCO", "Prestación", "Forma Pago", "Datos Bancarios", "Vales Despensa", "Subcontratación",
                    "Sistema Horario", "Sindicalizado", "Infonavit", "Mail", "Domicilio Particular", "Domicilio Fiscal");
        } else if (ControladorSesiones.getInstance().getCompaniaSession().getNombreCorto().equals("KONECTA")) {
            this.opciones = Arrays.asList("No. Empleado", "Location", "Broker", "Nombre", "Edo. Civil", "Sexo", "Fecha Nacimiento", "N.S.S.", "C.U.R.P.", "R.F.C.",
                    "Estatus", "Grupo Pago", "Registro Patronal", "Tipo Relación Laboral", "Jefe Directo", "Sistema Antiguedad", "Zona Económica", "S.D.", "S.D.I.",
                    "S.D. Anterior", "S.D.I. Anterior", "Fecha Ingreso", "Fecha Antiguedad", "Fecha Vencimiento", "Fecha Baja", "Causa Baja", "Departamento",
                    "Puesto", "Área", "Cuadrilla", "Tipo Nómina", "Cuenta Contable", "Nivel Trabajador", "Número Pensiones", "CCO", "Prestación", "Forma Pago", "Datos Bancarios", "Vales Despensa", "Subcontratación",
                    "Sistema Horario", "Sindicalizado", "Infonavit", "Mail", "Domicilio Particular", "Domicilio Fiscal");
        } else if (ControladorSesiones.getInstance().getCompaniaSession().getNombreCorto().equals("ALPHA")) {
            this.opciones = Arrays.asList("Modalidad de Trabajo", "No. Empleado", "Nombre", "Edo. Civil", "Sexo", "Fecha Nacimiento", "N.S.S.", "C.U.R.P.", "R.F.C.",
                    "Estatus", "Grupo Pago", "Registro Patronal", "Tipo Relación Laboral", "Jefe Directo", "Sistema Antiguedad", "Zona Económica", "S.D.", "S.D.I.",
                    "S.D. Anterior", "S.D.I. Anterior", "Fecha Ingreso", "Fecha Antiguedad", "Fecha Vencimiento", "Fecha Baja", "Causa Baja", "Departamento",
                    "Puesto", "Área", "Cuadrilla", "Tipo Nómina", "Cuenta Contable", "Nivel", "Número Pensiones", "CCO", "Prestación", "Forma Pago", "Datos Bancarios", "Vales Despensa", "Subcontratación",
                    "Jornada Laboral", "Turno Laboral", "Sistema Horario", "Sindicalizado", "Infonavit", "Mail", "Domicilio Particular", "Domicilio Fiscal");
        } else {
            this.opciones = Arrays.asList("No. Empleado", "Nombre", "Edo. Civil", "Sexo", "Fecha Nacimiento", "N.S.S.", "C.U.R.P.", "R.F.C.",
                    "Estatus", "Grupo Pago", "Registro Patronal", "Tipo Relación Laboral", "Jefe Directo", "Sistema Antiguedad", "Zona Económica", "S.D.", "S.D.I.",
                    "S.D. Anterior", "S.D.I. Anterior", "Fecha Ingreso", "Fecha Antiguedad", "Fecha Vencimiento", "Fecha Baja", "Causa Baja", "Departamento",
                    "Puesto", "Área", "Subarea", "Tipo Nómina", "Cuenta Contable", "Nivel", "Número Pensiones", "CCO", "Prestación", "Forma Pago", "Datos Bancarios", "Vales Despensa", "Subcontratación",
                    "Sistema Horario", "Sindicalizado", "Infonavit", "Mail", "Domicilio Particular", "Domicilio Fiscal");
        }


        this.selectedOpciones = (String[]) this.opciones.toArray();
    }

    @Override
    public void init() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void consultar(ActionEvent event) {
        this.relaciones = ControladorWS.getInstance().getReporteGeneral(this.reporte, this.fechaInicioAlta, this.fechaFinAlta, this.fechaInicioBaja, this.fechaFinBaja,
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
        this.reporte = new VistaEmpleados();
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
    public void delete(VistaEmpleados obj) {
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
            if (key.equals("Modalidad de Trabajo")) {
                columns.add(new ColumnModel(columnKey, "modalidadTrabajo"));

            }
            if (key.equals("No. Empleado")) {
                columns.add(new ColumnModel(columnKey, "numeroempleado"));
            }
            if (key.equals("Location")) {
                columns.add(new ColumnModel(columnKey, "ubicacionTrabajo"));
            }
            if (key.equals("Broker")) {
                columns.add(new ColumnModel(columnKey, "broker"));
            }
            if (key.equals("Nombre")) {
                columns.add(new ColumnModel(columnKey, "nombreempleado"));
            }
            if (key.equals("Edo. Civil")) {
                columns.add(new ColumnModel(columnKey, "estadocivil"));
            }
            if (key.equals("Sexo")) {
                columns.add(new ColumnModel(columnKey, "sexo"));
            }
            if (key.equals("Fecha Nacimiento")) {
                columns.add(new ColumnModel(columnKey, "fechanacimientoStr"));
            }
            if (key.equals("N.S.S.")) {
                columns.add(new ColumnModel(columnKey, "nss"));
            }
            if (key.equals("C.U.R.P.")) {
                columns.add(new ColumnModel(columnKey, "curp"));
            }
            if (key.equals("R.F.C.")) {
                columns.add(new ColumnModel(columnKey, "rfc"));
            }
            if (key.equals("Estatus")) {
                columns.add(new ColumnModel(columnKey, "estatus"));
            }
            if (key.equals("Grupo Pago")) {
                columns.add(new ColumnModel(columnKey, "grupopago"));
                columns.add(new ColumnModel(columnKey, "nombregrupopago"));
            }
            if (key.equals("Registro Patronal")) {
                columns.add(new ColumnModel(columnKey, "registropatronal"));
            }
            if (key.equals("Tipo Relación Laboral")) {
                columns.add(new ColumnModel(columnKey, "tiporelacionlaboral"));
            }
            if (key.equals("Sistema Antiguedad")) {
                columns.add(new ColumnModel(columnKey, "sistemaantiguedad"));
            }
            if (key.equals("Zona Económica")) {
                columns.add(new ColumnModel(columnKey, "zonaeconomica"));
            }
            if (key.equals("S.D.")) {
                columns.add(new ColumnModel(columnKey, "sd"));
            }
            if (key.equals("S.D.I.")) {
                columns.add(new ColumnModel(columnKey, "sdi"));
            }
            if (key.equals("S.D. Anterior")) {
                columns.add(new ColumnModel(columnKey, "sdanterior"));
            }
            if (key.equals("S.D.I. Anterior")) {
                columns.add(new ColumnModel(columnKey, "sdianterior"));
            }
            if (key.equals("Fecha Ingreso")) {
                columns.add(new ColumnModel(columnKey, "fechaingresoStr"));
            }
            if (key.equals("Fecha Antiguedad")) {
                columns.add(new ColumnModel(columnKey, "fechaantiguedadStr"));
            }
            if (key.equals("Fecha Vencimiento")) {
                columns.add(new ColumnModel(columnKey, "fechavencimientoStr"));
            }
            if (key.equals("Fecha Baja")) {
                columns.add(new ColumnModel(columnKey, "fechabajaStr"));
            }
            if (key.equals("Causa Baja")) {
                columns.add(new ColumnModel(columnKey, "causabaja"));
            }
            if (key.equals("Departamento")) {
                columns.add(new ColumnModel(columnKey, "departamento"));
                columns.add(new ColumnModel("R. Departamento", "responsabledepartamento"));
            }
            if (key.equals("Puesto")) {
                columns.add(new ColumnModel(columnKey, "puesto"));
                columns.add(new ColumnModel(columnKey, "nombrepuesto"));
            }
            if (key.equals("Área")) {
                columns.add(new ColumnModel(columnKey, "area"));
            }
            if (key.equals("Subarea")) {
                columns.add(new ColumnModel(columnKey, "subarea"));
            }
            if (key.equals("Cuadrilla")) {
                columns.add(new ColumnModel(columnKey, "subarea"));
            }
            if (key.equals("Jefe Directo")) {
                columns.add(new ColumnModel("No. Jefe Directo", "noresponsablepuesto"));
                columns.add(new ColumnModel("Jefe Directo", "responsablepuesto"));
            }
            if (key.equals("Tipo Nómina")) {
                columns.add(new ColumnModel(columnKey, "tiponomina"));
            }
            if (key.equals("Cuenta Contable")) {
                columns.add(new ColumnModel(columnKey, "cuentacontable"));
            }
            if (key.equals("Nivel Trabajador")) {
                columns.add(new ColumnModel(columnKey, "nivelTrabajador"));
            }
            if (key.equals("Nivel")) {
                columns.add(new ColumnModel(columnKey, "nivelpuesto"));
            }
            if (key.equals("Número Pensiones")) {
                columns.add(new ColumnModel(columnKey, "numeropensiones"));
            }
            if (key.equals("CCO")) {
                columns.add(new ColumnModel(columnKey, "centrocostos"));
                columns.add(new ColumnModel(columnKey, "nombrecentrocostos"));
            }
            if (key.equals("Prestación")) {
                columns.add(new ColumnModel(columnKey, "division"));
            }
            if (key.equals("Forma Pago")) {
                columns.add(new ColumnModel(columnKey, "formapago"));
            }
            if (key.equals("Datos Bancarios")) {
                columns.add(new ColumnModel("No. Cuenta Bancaria", "numerocuentabancaria"));
                columns.add(new ColumnModel("No. CLABE Bancaria", "numerocuentaclabe"));
                columns.add(new ColumnModel("Banco", "cuentabancaria"));
            }
            if (key.equals("Vales Despensa")) {
                columns.add(new ColumnModel("Cta. Vales Despensa", "ctavales"));
                columns.add(new ColumnModel("Tarjeta Vales Despensa", "tarjetavales"));
            }
            if (key.equals("Jornada Laboral")) {
                columns.add(new ColumnModel(columnKey, "jornadalaboral"));
            }
            if (key.equals("Turno Laboral")) {
                columns.add(new ColumnModel(columnKey, "turnolaboral"));
            }
            if (key.equals("Sistema Horario")) {
                columns.add(new ColumnModel(columnKey, "sistemaantiguedad"));
            }
            if (key.equals("Subcontratación")) {
                columns.add(new ColumnModel(columnKey, "nombresubcontratacion"));
                columns.add(new ColumnModel(columnKey, "rfcsubcontratacion"));
            }
            if (key.equals("Sindicalizado")) {
                columns.add(new ColumnModel(columnKey, "sindicalizado"));
            }
            if (key.equals("Infonavit")) {
                columns.add(new ColumnModel("Fecha Inicio Infonavit", "fechainfonavitStr"));
                columns.add(new ColumnModel("No. Crédito Infonavit", "numerocreditoinfonavit"));
                columns.add(new ColumnModel("Tipo Descuento Infonavit", "tipodescuentoinfonavit"));
                columns.add(new ColumnModel("Descuento Infonavit", "descuentoinfonavit"));
                columns.add(new ColumnModel("Estatus Infonavit", "estatusinfonavit"));
            }
            if (key.equals("Mail")) {
                columns.add(new ColumnModel(columnKey, "mail"));
            }
            if (key.equals("Domicilio Particular")) {
                columns.add(new ColumnModel(columnKey, "domicilioParticular"));
            }
            if (key.equals("Domicilio Fiscal")) {
                columns.add(new ColumnModel(columnKey, "domicilioFiscal"));
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

    public List<ColumnModel> getColumns() {
        return columns;
    }

    public List<VistaEmpleados> getRelaciones() {
        return relaciones;
    }

    public VistaEmpleados getReporte() {
        return reporte;
    }

    public void setReporte(VistaEmpleados reporte) {
        this.reporte = reporte;
    }

    public List<VistaEmpleados> getFilteredRelaciones() {
        return filteredRelaciones;
    }

    public void setFilteredRelaciones(List<VistaEmpleados> filteredRelaciones) {
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

    private String valorCampo(VistaEmpleados empleado, String fieldName) {
        try {
            Field field = VistaEmpleados.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            Object value = field.get(empleado);
            return value != null ? value.toString() : "";
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return "";
        }
    }

    public void descargarReporteCsv() {
        List<String> lineas = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for (ColumnModel column : this.columns) {
            sb.append(column.getHeader()).append(",");
        }
        sb.setLength(sb.length() - 1);
        lineas.add(sb.toString());
        for (VistaEmpleados empleado : this.filteredRelaciones) {
            sb = new StringBuilder();
            for (ColumnModel column : this.columns) {
                String fieldValue = valorCampo(empleado, column.getProperty());
                fieldValue = fieldValue.replaceAll(",", "");
                sb.append(fieldValue != null ? fieldValue : "").append(",");
            }
            sb.setLength(sb.length() - 1);
            lineas.add(sb.toString());
        }
        Util.escribirFichero(lineas, "ReporteEmpleados", ParametrosReportes.ARCHIVO_CSV);
    }
}
