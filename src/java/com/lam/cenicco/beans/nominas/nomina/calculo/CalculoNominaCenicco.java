/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lam.cenicco.beans.nominas.nomina.calculo;

import com.lam.cenicco.dao.ProcesoDAO;
import com.lam.cenicco.impl.ControladorWS;
import com.lam.cenicco.librerias.mensajes.Mensajes;
import com.lam.cenicco.librerias.utilidades.CeniccoUtil;
import com.lam.cenicco.util.ControladorSesiones;
import com.lam.cenicco.util.ParametrosReportes;
import com.lam.cenicco.util.Util;
import com.lam.cenicco.util.paths.MyPaths;
import com.lam.cenicco.ws.Concepto;
import com.lam.cenicco.ws.Empleado;
import com.lam.cenicco.ws.GrupoConcepto;
import com.lam.cenicco.ws.GrupoPago;
import com.lam.cenicco.ws.Nomina;
import com.lam.cenicco.ws.Periodo;
import com.lam.cenicco.ws.RelacionLaboral;
import com.lam.cenicco.ws.RelacionLaboralPosicion;
import com.lam.cenicco.ws.TipoProceso;
import com.lam.cenicco.ws.Usuario;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.primefaces.context.RequestContext;

/**
 *
 * @author JoséAntonio
 */
public class CalculoNominaCenicco implements ProcesoDAO<Nomina> {
//

    protected List<Nomina> nominas;
//   
    protected List<Nomina> filteredNominas;
//
    protected List<GrupoConcepto> tiposNomina;
//
    protected Nomina periodoC;
//    
    protected Map<String, RelacionLaboral> mapaRelacionesLaborales;
//    
    protected List<String> relaciones;
//    
    protected Periodo auxPeriodo;
//    
    protected String[] selectedRelaciones;
//    
    protected Integer selectedTipoNomina;
//    
    protected Integer selectedGrupoPago;
//    
    protected Integer selectedestatus;
//    
    protected Integer periodo;
    protected Integer anio;
//    
    protected Double totalprovisiones;
    protected Double totalpercepciones;
    protected Double totaldeducciones;
    protected Double totalneto;
//    
    protected boolean bloqueo;
//    
    protected Usuario usuario;
//    
    protected Integer selectedtipo;
    protected Integer incidenciasSinAutorizar;
//    
    protected boolean preNomina;

    public CalculoNominaCenicco() {
        if (this.relaciones == null) {
            this.relaciones = new ArrayList<>();
        }
        if (this.nominas == null) {
            this.nominas = new ArrayList<>();
        }
        if (this.tiposNomina == null) {
            this.tiposNomina = ControladorWS.getInstance().getTiposNomina();
        }
    }

    @PostConstruct
    @Override
    public void init() {
        this.bloqueo = ControladorWS.getInstance().getBloqueoNominas();
//        
        if (this.usuario == null) {
            this.usuario = ControladorSesiones.getInstance().getUsuarioSession();
        }
    }

    @Override
    public void consultar(ActionEvent event) {
        this.nominas = ControladorWS.getInstance().findNominas(this.selectedGrupoPago, this.auxPeriodo.getIdperiodo(), this.selectedestatus);
        System.out.println("Número Nóminas... " + this.nominas.size());
    }

    @Override
    public void limpiar(ActionEvent event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void validarIncidencias(ActionEvent event) {


        this.incidenciasSinAutorizar = ControladorWS.getInstance().findIncidenciasCalculo(null, this.selectedGrupoPago, this.periodo, this.anio, this.selectedTipoNomina, BigDecimal.TEN.intValue()).size();
        if (this.incidenciasSinAutorizar != 0) {
            RequestContext context = RequestContext.getCurrentInstance();
            context.update("formConfirmSinAutorizar");
            context.execute("PF('confirmSinAutorizar').show();");
        } else {
            this.create(event);
        }

    }

    @Override
    public void create(ActionEvent event) {
        this.preNomina = false;
        FacesMessage msg = this.createNominas();
//        
        this.generarMsg(msg, FacesMessage.SEVERITY_INFO.equals(msg.getSeverity()));
    }

    private FacesMessage createNominas() {
        GrupoConcepto tipoGrupoConcepto = null;
        for (GrupoConcepto grupoconcepto : this.tiposNomina) {
            if (this.selectedTipoNomina.equals(grupoconcepto.getIdgrupoconcepto())) {
                tipoGrupoConcepto = grupoconcepto;
                break;
            }
        }
        TipoProceso tp = ControladorWS.getInstance().findTipoProcesoById(tipoGrupoConcepto.getIdtipoproceso());
        Periodo periodoCalculado = ControladorWS.getInstance().getPeriodosByTipoProceso(this.selectedGrupoPago).get(0);
        periodoCalculado.setPeriodo(this.periodo);
        periodoCalculado.setIdtipoproceso(tp);
        periodoCalculado = ControladorWS.getInstance().findPeriodoByTipoProcesoAndPeriodoAndAnio(periodoCalculado);
        System.out.println("Nominas Calculadas GP: " + selectedGrupoPago + " Periodo: " + periodoCalculado.getIdperiodo() + " Estatus: " + this.selectedestatus);
        List<Nomina> nominasCalculada = ControladorWS.getInstance().findNominas(selectedGrupoPago, periodoCalculado.getIdperiodo(), this.selectedestatus);
        if (!nominasCalculada.isEmpty()) {
            return Util.getFacesMessage(FacesMessage.SEVERITY_WARN, "Nomina ya calculada");
        } else {
            List<RelacionLaboral> relacionesAux = new ArrayList<>();
//
            for (String llave : this.selectedRelaciones) {
                relacionesAux.add(this.mapaRelacionesLaborales.get(llave));
            }
//        
            System.out.println("CalcularNomina... " + this.auxPeriodo.getPeriodo() + " | " + this.periodo + " | " + this.anio + " | " + this.selectedTipoNomina + " | " + this.selectedestatus);
//        
            this.nominas = ControladorWS.getInstance().calcularNomina(relacionesAux, this.auxPeriodo, this.periodo, this.selectedTipoNomina);
//        
            System.out.println("Numero de nóminas calculadas... " + this.nominas.size());
//        
            this.calcularTotales();
//        
            FacesMessage msg = !this.nominas.isEmpty() ? Util.getFacesMessage(FacesMessage.SEVERITY_INFO, Mensajes.EXITOSO_CALCULO_NOMINAS)
                    : Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, Mensajes.ERROR_CALCULO_NOMINAS);
//
            return msg;
        }
    }

    public void consultarNomina(ActionEvent event) {
        this.preNomina = true;
        this.nominas = ControladorWS.getInstance().consultarNomina(this.selectedGrupoPago, this.auxPeriodo, this.periodo, this.selectedTipoNomina);
        this.calcularTotales();
        FacesMessage msg = !this.nominas.isEmpty()
                ? Util.getFacesMessage(FacesMessage.SEVERITY_INFO, "Se consulto correctamente la pre N\u00f3mina")
                : Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, "No se encontro nominas previamente guardadas");
//        
        this.generarMsg(msg, FacesMessage.SEVERITY_INFO.equals(msg.getSeverity()));
    }

    public void rollBack(ActionEvent event) {
        boolean isValidate = ControladorWS.getInstance().rollBack(this.selectedGrupoPago, this.auxPeriodo, this.selectedTipoNomina);
        FacesMessage msg = isValidate ? Util.getFacesMessage(FacesMessage.SEVERITY_INFO, "Exitoso RollBack!")
                : Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, "Error en RollBack!");
//        
        if (isValidate) {
            this.nominas = new ArrayList<>();
        }
//        
        this.generarMsg(msg, FacesMessage.SEVERITY_INFO.equals(msg.getSeverity()));
    }

    private void calcularTotales() {
//        
        this.totalprovisiones = 0.0;
        this.totalpercepciones = 0.0;
        this.totaldeducciones = 0.0;
        this.totalneto = 0.0;
//        
        if (!this.nominas.isEmpty()) {
            Iterator<Nomina> iter = this.nominas.iterator();
//            
            while (iter.hasNext()) {
                Nomina n = iter.next();
//                
                this.totalprovisiones += n.getTotalProvisiones();
                this.totaldeducciones += n.getTotalDeduccion();
                this.totalpercepciones += n.getTotalPercepcion();
//                
            }
//            
            this.totalneto = this.totalpercepciones - this.totaldeducciones;
//            
        }
    }

    @Override
    public void edit(ActionEvent event) {
        /*        if (!this.preNomina) {
         boolean isCleanPreNomina = ControladorWS.getInstance().deleteNomina(this.selectedGrupoPago, this.auxPeriodo, this.periodo, this.selectedTipoNomina);
         System.out.print("Eliminacion de pre nomina " + isCleanPreNomina);
         }
         */
        if (this.selectedGrupoPago == null) {
            String warning = "No se ha seleccionado grupo de pago";
            if (this.periodo == null) {
                warning += ", ni periodo";
            }
            FacesMessage msg = Util.getFacesMessage(FacesMessage.SEVERITY_WARN, warning);
            this.generarMsg(msg, true);
        } else {
//        
            boolean isValidate = ControladorWS.getInstance().validarCierreNomina(this.selectedGrupoPago, this.auxPeriodo.getIdperiodo());
//      
            boolean esnominaespecial = this.auxPeriodo.getPeriodo() != this.periodo;

            if (esnominaespecial) {
                isValidate = false;
            }
//        
            FacesMessage msg;
//        
            if (!isValidate) {
                if (!esnominaespecial) {
                    if (this.nominas.size() != this.mapaRelacionesLaborales.size()) {
                        msg = Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, "Favor de calcular la nómina completa!");
                    } else {
                        isValidate = ControladorWS.getInstance().cerrarNomina(this.nominas, esnominaespecial);
                        msg = isValidate ? Util.getFacesMessage(FacesMessage.SEVERITY_INFO, Mensajes.EXITOSO_CALCULO_NOMINA)
                                : Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, Mensajes.ERROR_CALCULO_NOMINA);
                    }
                } else {
                    isValidate = ControladorWS.getInstance().cerrarNomina(this.nominas, esnominaespecial);
                    msg = isValidate ? Util.getFacesMessage(FacesMessage.SEVERITY_INFO, Mensajes.EXITOSO_CALCULO_NOMINA)
                            : Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, Mensajes.ERROR_CALCULO_NOMINA);
                }
            } else {
                msg = Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, Mensajes.ERROR_CALCULO_NOMINA_CERRADA
                        + "Periodo: " + this.auxPeriodo.getPeriodo() + "; Año: " + this.auxPeriodo.getAnio());
            }
            this.generarMsg(msg, isValidate);
        }
    }

    @Override
    public void delete(Nomina obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void changeEstatus(ValueChangeEvent e) {
        this.selectedestatus = e.getNewValue() == null ? null : Integer.parseInt(e.getNewValue().toString());
//        
        if (this.selectedestatus != null && this.selectedGrupoPago != null) {
            this.updateEmpleados(this.selectedGrupoPago);
        } else {
            this.periodo = null;
            this.anio = null;
        }
//
    }

    public void changeGrupoPago(ValueChangeEvent e) {
        this.selectedGrupoPago = e.getNewValue() == null ? null : Integer.parseInt(e.getNewValue().toString());
//        
        if (this.selectedGrupoPago != null) {
            this.auxPeriodo = ControladorWS.getInstance().getPeriodosByTipoProceso(this.selectedGrupoPago).get(0);
            this.periodo = this.auxPeriodo.getPeriodo();
            this.anio = this.auxPeriodo.getAnio();
        } else {
            this.periodo = null;
            this.anio = null;
        }

        this.updateEmpleados(this.selectedGrupoPago);
    }

    public void nominaTemporal(ActionEvent event) {
        FacesMessage msg;
        boolean isValidate = false;
        if (this.mapaRelacionesLaborales.isEmpty()) {
            msg = Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, "Favor de calcular la nómina");
        } else {
            System.out.print("Entre a nominaTemporal");
            boolean esnominaespecial = this.auxPeriodo.getPeriodo() != this.periodo;
            isValidate = ControladorWS.getInstance().nominaTmp(this.nominas, esnominaespecial);
            msg = isValidate ? Util.getFacesMessage(FacesMessage.SEVERITY_INFO, "Reportes Temporales disponibles")
                    : Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, "Error al generar reportes temporales de la Nomina");
        }
        this.generarMsg(msg, isValidate);
    }

    public void createPreNomina(ActionEvent event) {
        FacesMessage msg;
        boolean isValidate = false;
        if (this.mapaRelacionesLaborales.isEmpty()) {
            msg = Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, "Favor de calcular la nómina");
        } else {
            System.out.print("Entre a createPreNomina");
            boolean esnominaespecial = this.auxPeriodo.getPeriodo() != this.periodo;
            isValidate = ControladorWS.getInstance().createNomina(this.nominas, esnominaespecial);
            msg = isValidate
                    ? Util.getFacesMessage(FacesMessage.SEVERITY_INFO, "Pre nomina disponible")
                    : Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, "Error al generar pre nomina");
        }
        this.generarMsg(msg, isValidate);
    }

    public void cerrarPeriodo() {
        if (this.auxPeriodo != null) {
            this.auxPeriodo.setEstatus(1);
//        
            boolean isValidate = ControladorWS.getInstance().editPeriodo(this.auxPeriodo, null);
//        
            if (isValidate) {
                this.auxPeriodo = ControladorWS.getInstance().getPeriodosByTipoProceso(this.selectedGrupoPago).get(0);
            }
//        
            FacesMessage msg = isValidate ? Util.getFacesMessage(FacesMessage.SEVERITY_INFO, Mensajes.EXITOSO_MODIFICAR_PERIODO)
                    : Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, Mensajes.ERROR_CREAR_PERIODO);
//        
            this.generarMsg(msg, isValidate);
        } else {
            FacesMessage msg = Util.getFacesMessage(FacesMessage.SEVERITY_WARN, "No se ha seleccionado un periodo");
            this.generarMsg(msg, true);
        }
    }

    private void updateEmpleados(Integer idGrupoPago) {
        this.selectedRelaciones = null;
//        
        this.mapaRelacionesLaborales = new HashMap<>();
//
        List<RelacionLaboral> relacionesAux =
                ControladorWS.getInstance().findRelacionesLaboralesByFiltrosXmlCalendar(idGrupoPago,
                this.selectedestatus == null ? 1 : this.selectedestatus, null, this.auxPeriodo.getFechafin(), null, null);
//        
        this.relaciones = new ArrayList<>();
//        
        for (RelacionLaboral rl : relacionesAux) {
            String apellidoMaterno = rl.getIdempleado().getApellidomaterno() == null ? null : rl.getIdempleado().getApellidomaterno();
            String llave = rl.getNumeroempleado() + " - " + rl.getIdempleado().getNombre() + " " + rl.getIdempleado().getApellidopaterno();
            if (rl.getIdempleado().getApellidomaterno() != null) {
                llave = llave + " " + apellidoMaterno;
            }
            System.out.println("Llave.... " + llave);
            if (this.mapaRelacionesLaborales.get(llave) == null) {
                this.mapaRelacionesLaborales.put(llave, rl);
                this.relaciones.add(llave);
            }
        }
//        
    }

    public void descargarArchivoAlpha(ActionEvent event) {
        GrupoPago gp = ControladorWS.getInstance().findGrupoPagoById(this.selectedGrupoPago);
        String nombreArchivo = gp.getGrupopago() + "_" + this.periodo + "_" + this.anio;
        this.archivoexcelAlpha(nombreArchivo, ParametrosReportes.ARCHIVO_XLS);
    }

    private void archivoexcelAlpha(String nombreArchivo, String tipoarchivo) {
        DecimalFormat nformat = new DecimalFormat("0.00");
//        
        HSSFWorkbook workbook = new HSSFWorkbook();
//        
        HSSFCellStyle styleamarillo = workbook.createCellStyle();
        styleamarillo.setFillForegroundColor(HSSFColor.YELLOW.index);
        styleamarillo.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
//        
        HSSFCellStyle stylegris = workbook.createCellStyle();
        stylegris.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
        stylegris.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
//        
        HSSFCellStyle styleazul = workbook.createCellStyle();
        HSSFFont font = workbook.createFont();
        font.setColor(HSSFColor.WHITE.index);
//        
        styleazul.setFont(font);
        styleazul.setFillForegroundColor(HSSFColor.BLUE.index);
        styleazul.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
//        
        HSSFSheet sheet = workbook.createSheet("Prenomina" + this.periodo + this.anio);
//        
        int i = 0;
        int j = 2;
//        
        HSSFRow row = sheet.createRow(j++);
        HSSFCell cell = row.createCell(i++);
        cell.setCellValue("NUMERO EMPLEADO");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("NOMBRE");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("REGISTRO PATRONAL");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("ZONA");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("AREA");
        cell.setCellStyle(styleamarillo);

        cell = row.createCell(i++);
        cell.setCellValue("CVE. CCO");
        cell.setCellStyle(styleamarillo);

        cell = row.createCell(i++);
        cell.setCellValue("DESC. CCO");
        cell.setCellStyle(styleamarillo);

        cell = row.createCell(i++);
        cell.setCellValue("CVE. DEPTO");
        cell.setCellStyle(styleamarillo);

        cell = row.createCell(i++);
        cell.setCellValue("DESCR. DEPTO");
        cell.setCellStyle(styleamarillo);

        cell = row.createCell(i++);
        cell.setCellValue("CVE. PUESTO");
        cell.setCellStyle(styleamarillo);

        cell = row.createCell(i++);
        cell.setCellValue("DESCR. PUESTO");
        cell.setCellStyle(styleamarillo);

        cell = row.createCell(i++);
        cell.setCellValue("SALARIO DIARIO");
        cell.setCellStyle(styleamarillo);

        cell = row.createCell(i++);
        cell.setCellValue("SALARIO DIARIO INTEGRADO");
        cell.setCellStyle(styleamarillo);

        cell = row.createCell(i++);
        cell.setCellValue("N.S.S.");
        cell.setCellStyle(styleamarillo);

        cell = row.createCell(i++);
        cell.setCellValue("1510/VALES DESPENSA");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("1511/VALES DESPENSA DE PERIODOS ANTERIORES");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("1512/VALES PENSION");
        cell.setCellStyle(styleamarillo);
//         
        cell = row.createCell(i++);
        cell.setCellValue("1513/VALE DESPENSA EXCEDENTE 40% IMSS");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("3500/PROVISION AGUINALDO");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("3510/PROVISION AGUINALDO EXENTO");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("3511/PROVISION AGUINALDO GRAVADO");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("3210/APORT FONDO AHORRO EMPRESA EMPLEADO");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("3220/PROVISION PRIMA VACACIONAL");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("1161/PRIMA VACACIONAL EXENTA");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("1162/PRIMA VACACIONAL GRAVABLE");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("3110/CUOTA PATRONAL IMSS");
        cell.setCellStyle(styleamarillo);
        //        
        cell = row.createCell(i++);
        cell.setCellValue("3107/RIESGOS DE TRABAJO");
        cell.setCellStyle(styleamarillo);
        //        
        cell = row.createCell(i++);
        cell.setCellValue("3106/GUARDERIAS");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("3101/EYM CUOTA FIJA");
        cell.setCellStyle(styleamarillo);

        cell = row.createCell(i++);
        cell.setCellValue("3102/EYM EXCED 3SM");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("3103/EYM PREST DINERO");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("3104/EYM GTOS MEDICOS");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("3105/INVALIDEZ Y VIDA");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("3140/INFONAVIT 5%");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("3150/SAR");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("3160/CESANTIA Y VEJEZ");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("3120/IMPUESTO ESTATAL NOMINAS");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("TOTAL CARGA SOCIAL");
        cell.setCellStyle(stylegris);
//        
        cell = row.createCell(i++);
        cell.setCellValue("3512/PROVISION DE PTU");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("3514/PROVISION DE PRIMA DE RIESGO");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("1110/SUELDO BASE");
        cell.setCellStyle(styleamarillo);
//       
        cell = row.createCell(i++);
        cell.setCellValue("1115/DIFERENCIA DE SUELDO");
        cell.setCellStyle(styleamarillo);
//    
        cell = row.createCell(i++);
        cell.setCellValue("1120/DIAS PENDIENTES DE SUELDO");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("1124/AYUDA RENTA");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("1130/RETROACTIVO DE SUELDO");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("1140/PERMISO CON GOCE DE SUELDO");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("1150/VACACIONES");
        cell.setCellStyle(styleamarillo);
//       
        cell = row.createCell(i++);
        cell.setCellValue("1151/VACACIONES PAGADAS");
        cell.setCellStyle(styleamarillo);
//     
        cell = row.createCell(i++);
        cell.setCellValue("1160/PRIMA VACACIONAL");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("1161/PRIMA VACACIONAL EXENTA");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("1162/PRIMA VACACIONAL GRAVABLE");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("1180/DIA FESTIVO TRABAJADO");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("1210/COMISIONES");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("1220/INCENTIVO");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("1240/BONO ESPECIAL");
        cell.setCellStyle(styleamarillo);
//
        cell = row.createCell(i++);
        cell.setCellValue("1250/BONO ESPECIAL");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("1251/COMPENSACION ESPECIAL");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("1260/BONO MENSUAL");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("1270/DEVOLUCIÓN DE DESCUENTO INCORRECTO");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("1450/PRIMA DOMINICAL");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("1451/PRIMA DOMINICAL EXENTA");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("1452/PRIMA DOMINICAL GRAVABLE");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("1480/SUBSIDIO AL EMPLEO");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("1490/PAGO POR FALTA JUSTIFICADA");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("1500/VIATICOS");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("1501/VIATICOS NO COMPROBADOS");
        cell.setCellStyle(styleamarillo);
//  
        cell = row.createCell(i++);
        cell.setCellValue("1560/PERMISO POR DEFUNCION");
        cell.setCellStyle(styleamarillo);
//  
        cell = row.createCell(i++);
        cell.setCellValue("1570/PERMISO MATRIMONIO");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("1580/PERMISO PATERNIDAD");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("1600/SUBSIDIO POR INCAPACIDAD");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("1610/COMPLEMENTO INCAPACIDAD");
        cell.setCellStyle(styleamarillo);
//      
        cell = row.createCell(i++);
        cell.setCellValue("1614/HERRAMIENTA DE TRABAJO");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("1620/REPARTO DE UTILIDADES");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("1621/REPARTO DE UTILIDADES EXENTO");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("1622/REPARTO DE UTILIDADES GRAVABLE");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("1630/ISPT A COMPENSAR POR AJUSTE ANUAL");
        cell.setCellStyle(styleamarillo);

        cell = row.createCell(i++);
        cell.setCellValue("1640/ISR A FAVOR AJUSTE ANUAL");
        cell.setCellStyle(styleamarillo);

        cell = row.createCell(i++);
        cell.setCellValue("1641/FONDO DE AHORRO EMPRESA");
        cell.setCellStyle(styleamarillo);

        cell = row.createCell(i++);
        cell.setCellValue("1670/LIQUIDACION FONDO DE AHORRO EMPLEADO");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("1701/EXENTO TPO EXT DOBLE FEST LAB Y DESC LAB");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("1702/GRAVADO TPO EXT DOBLE FEST LAB Y DESC LAB");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("1710/LIQUIDACION FONDO DE AHORRO EMPRESA");
        cell.setCellStyle(styleamarillo);

        cell = row.createCell(i++);
        cell.setCellValue("1720/INTERESES FONDO DE AHORRO");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("TOTAL PERCEPCIONES");
        cell.setCellStyle(stylegris);
//        
        cell = row.createCell(i++);
        cell.setCellValue("2110/FALTA JUSTIFICADA");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("2120/FALTA INJUSTIFICADA");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("2130/PERMISO SIN GOCE DE SUELDO");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("2210/INCAPACIDAD POR ENFERMEDAD GENERAL");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("2220/INCAPACIDAD POR ACCIDENTE DE TRABAJO");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("2230/INCAPACIDAD POR MATERNIDAD");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("2240/INCAPACIDAD POR ENFERMEDAD PROFESIONAL");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("2260/INCAPACIDAD POR ACCIDENTE EN TRAYECTO");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("2310/IMPUESTO SOBRE LA RENTA NETO");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("2311/IMPUESTO SOBRE LA RENTA ANTES SE");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("2410/CUOTA IMSS TRABAJADOR");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("2500/VIATICOS COMPROBADOS");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("2610/APORT. FONDO DE AHORRO EMPLEADO");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("2620/APORT FONDO DE AHORRO EMPRESA");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("2630/PRESTAMO PERSONAL");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("2631/PRESTAMO AXS");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("2637/AJUSTE PENSION ALIMENTICIA PERIODO ANTERIOR");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("2650/ANTICIPO DE SUELDO");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("2660/DESCUENTO GENERAL");
        cell.setCellStyle(styleamarillo);
        //        
        cell = row.createCell(i++);
        cell.setCellValue("2661/OTROS DESCUENTOS");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("2710/PENSION ALIMENTICIA");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("2715/ADEUDO INFONAVIT");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("2720/DESCUENTO INFONAVIT");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("2721/SEGURO DE DANOS INFONAVIT");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("2730/DESCUENTO FONACOT");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("2810/DESCUENTO POR COMEDOR");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("2820/DESCUENTO ESTACIONAMIENTO");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("2830/DESCUENTO VALES");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("TOTAL DEDUCCIONES");
        cell.setCellStyle(stylegris);
//        
        cell = row.createCell(i++);
        cell.setCellValue("NETO A PAGAR");
        cell.setCellStyle(styleamarillo);
//        
        i++;
//        cell = row.createCell(i++);
//        cell.setCellValue("PROVISION FINIQUITO+ISN");
//        cell.setCellStyle(styleazul);
//        
        cell = row.createCell(i++);
        cell.setCellValue("COSTO PERSONAL");
        cell.setCellStyle(styleazul);
//        
        cell = row.createCell(i++);
        cell.setCellValue("FEE");
        cell.setCellStyle(styleazul);
//        
        cell = row.createCell(i++);
        cell.setCellValue("TOTAL POR PERSONA");
        cell.setCellStyle(styleazul);
//        
        cell = row.createCell(i++);
        cell.setCellValue("I.V.A. 16%");
        cell.setCellStyle(styleazul);
//        
        cell = row.createCell(i++);
        cell.setCellValue("RETENCIÓN I.V.A. 6%");
        cell.setCellStyle(styleazul);
//  
        cell = row.createCell(i++);
        cell.setCellValue("TOTAL FACTURA");
        cell.setCellStyle(styleazul);
//        
        i++;
        cell = row.createCell(i++);
        cell.setCellValue("DESPENSA");
        cell.setCellStyle(styleazul);
//        
        cell = row.createCell(i++);
        cell.setCellValue("COMISIÓN DESPENSA");
        cell.setCellStyle(styleazul);
//        
        cell = row.createCell(i++);
        cell.setCellValue("FEE");
        cell.setCellStyle(styleazul);
//        
        cell = row.createCell(i++);
        cell.setCellValue("TOTAL VALES");
        cell.setCellStyle(styleazul);
//        
        cell = row.createCell(i++);
        cell.setCellValue("I.V.A. 16%");
        cell.setCellStyle(styleazul);
//    
        cell = row.createCell(i++);
        cell.setCellValue("TOTAL FACTURA");
        cell.setCellStyle(styleazul);
//////        
        Iterator<Nomina> iter = this.nominas.iterator();
        while (iter.hasNext()) {
            Nomina n = iter.next();
//            
            i = 0;
////            
            Map<String, Concepto> mapaconceptos = new HashMap<>();
            for (Concepto c : n.getPercepciones()) {
                if (mapaconceptos.get(c.getConcepto()) == null) {
                    mapaconceptos.put(c.getConcepto(), c);
                } else {
                    double importe = mapaconceptos.get(c.getConcepto()).getValor() + c.getValor();
                    mapaconceptos.get(c.getConcepto()).setValor(importe);
                }
            }
            for (Concepto c : n.getDeducciones()) {
                if (mapaconceptos.get(c.getConcepto()) == null) {
                    mapaconceptos.put(c.getConcepto(), c);
                } else {
                    double importe = mapaconceptos.get(c.getConcepto()).getValor() + c.getValor();
                    mapaconceptos.get(c.getConcepto()).setValor(importe);
                }

            }
            for (Concepto c : n.getProvisiones()) {
                if (mapaconceptos.get(c.getConcepto()) == null) {
                    mapaconceptos.put(c.getConcepto(), c);
                } else {
                    double importe = mapaconceptos.get(c.getConcepto()).getValor() + c.getValor();
                    mapaconceptos.get(c.getConcepto()).setValor(importe);
                }
            }
////            
            RelacionLaboral rellab = n.getRelacionlaboral();
            row = sheet.createRow(j);
//            
            cell = row.createCell(i++);
            cell.setCellValue(rellab.getNumeroempleado());
            cell = row.createCell(i++);
            String nombreempleado = Util.getApellidosNombre(rellab.getIdempleado());
            cell.setCellValue(nombreempleado);
////            
            String aux;
//            
            if (rellab.getIdregistropatronal() != null) {
                aux = rellab.getIdregistropatronal().getRegistropatronal();
            } else {
                aux = "";
            }
            cell = row.createCell(i++);
            cell.setCellValue(aux);
//          ZONA ECONOMICA
            RelacionLaboralPosicion pos = rellab.getIdrelacionlaboralposicion();
            if (pos != null
                    && pos.getIdZonaEconomica() != null) {
                aux = pos.getIdZonaEconomica().getZonaEconomica();
            } else {
                aux = "";
            }
            cell = row.createCell(i++);
            cell.setCellValue(aux);
//          AREA  
            if (rellab.getIdregistropatronal() != null) {
                aux = rellab.getIdregistropatronal().getComentarios();
            } else {
                aux = "";
            }
            cell = row.createCell(i++);
            cell.setCellValue(aux);
//          CVE CENTRO DE COSTOS
            if (pos != null
                    && pos.getIdcentrocosto() != null) {
                aux = pos.getIdcentrocosto().getCentroCosto();
            } else {
                aux = "";

            }
            cell = row.createCell(i++);
            cell.setCellValue(aux);
//          DESCR CENTRO DE COSTOS  
            if (pos != null
                    && pos.getIdcentrocosto() != null) {
                aux = pos.getIdcentrocosto().getNombre();
            } else {
                aux = "";

            }
            cell = row.createCell(i++);
            cell.setCellValue(aux);
//          CVE DEPTO  
            if (pos != null
                    && pos.getIddepartamento() != null) {
                aux = pos.getIddepartamento().getDepartamento();
            } else {
                aux = "";

            }
            cell = row.createCell(i++);
            cell.setCellValue(aux);
//          DESCR DEPTO  
            if (pos != null
                    && pos.getIddepartamento() != null) {
                aux = pos.getIddepartamento().getNombre();
            } else {
                aux = "";
            }
            cell = row.createCell(i++);
            cell.setCellValue(aux);
//          CVE PUESTO
            if (pos != null
                    && pos.getIdpuesto() != null) {
                aux = pos.getIdpuesto().getPuesto();
            } else {
                aux = "";
            }
            cell = row.createCell(i++);
            cell.setCellValue(aux);
//          DESCR PUESTO
            if (pos != null
                    && pos.getIdpuesto() != null) {
                aux = pos.getIdpuesto().getNombre();
            } else {
                aux = "";
            }
            cell = row.createCell(i++);
            cell.setCellValue(aux);
//          SALARIO DIARIO  
            cell = row.createCell(i++);
            cell.setCellValue(nformat.format(rellab.getSalarioDiario()));
//          SALARIO DIARIO INTEGRADO  
            cell = row.createCell(i++);
            cell.setCellValue(nformat.format(rellab.getSalarioDiarioIntegrado()));
//          NSS
            cell = row.createCell(i++);
            cell.setCellValue(rellab.getIdempleado().getAfiliacion());
//          VALES DESPENSA
            double valesdespensa = 0.0;
            if (mapaconceptos.get("1510") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1510").getValor()));
                valesdespensa = mapaconceptos.get("1510").getValor();
            }
            i++;
            if (mapaconceptos.get("1511") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1511").getValor()));
            }
            i++;
            if (mapaconceptos.get("1512") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1512").getValor()));
            }
            i++;
            if (mapaconceptos.get("1513") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1513").getValor()));
            }
            i++;
            if (mapaconceptos.get("3500") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("3500").getValor()));
            }
            i++;
            if (mapaconceptos.get("3510") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("3510").getValor()));
            }
            i++;
            if (mapaconceptos.get("3511") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("3511").getValor()));
            }
            i++;
            if (mapaconceptos.get("3210") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("3210").getValor()));
            }
            i++;
            if (mapaconceptos.get("3220") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("3220").getValor()));
            }
            i++;
            if (mapaconceptos.get("1161") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1161").getValor()));
            }
            i++;
            if (mapaconceptos.get("1162") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1162").getValor()));
            }
            i++;
            if (mapaconceptos.get("3110") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("3110").getValor()));
            }
            i++;

            if (mapaconceptos.get("3107") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("3107").getValor()));
            }
            i++;
            if (mapaconceptos.get("3106") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("3106").getValor()));
            }
            i++;
            if (mapaconceptos.get("3101") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("3101").getValor()));
            }
            i++;
            if (mapaconceptos.get("3102") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("3102").getValor()));
            }
            i++;
            if (mapaconceptos.get("3103") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("3103").getValor()));
            }
            i++;
            if (mapaconceptos.get("3104") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("3104").getValor()));
            }
            i++;
            if (mapaconceptos.get("3105") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("3105").getValor()));
            }
            i++;
            if (mapaconceptos.get("3140") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("3140").getValor()));
            }
            i++;
            if (mapaconceptos.get("3150") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("3150").getValor()));
            }
            i++;
            if (mapaconceptos.get("3160") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("3160").getValor()));
            }
            i++;
            if (mapaconceptos.get("3120") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("3120").getValor()));
            }
            i++;
            cell = row.createCell(i);
            cell.setCellValue(nformat.format(n.getTotalProvisiones()));
//            
            i++;
            if (mapaconceptos.get("3512") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("3512").getValor()));
            }
//            
            i++;
            if (mapaconceptos.get("3514") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("3514").getValor()));
            }
//            
            i++;
            if (mapaconceptos.get("1110") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1110").getValor()));
            }
            i++;
            if (mapaconceptos.get("1115") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1115").getValor()));
            }
            i++;
            if (mapaconceptos.get("1120") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1120").getValor()));
            }
            i++;
            if (mapaconceptos.get("1124") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1124").getValor()));
            }
            i++;
            if (mapaconceptos.get("1130") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1130").getValor()));
            }
            i++;
            if (mapaconceptos.get("1140") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1140").getValor()));
            }
            i++;
            if (mapaconceptos.get("1150") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1150").getValor()));
            }
            i++;
            if (mapaconceptos.get("1151") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1151").getValor()));
            }
            i++;
            if (mapaconceptos.get("1160") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1160").getValor()));
            }
            i++;
            if (mapaconceptos.get("1161") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1161").getValor()));
            }
            i++;
            if (mapaconceptos.get("1162") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1162").getValor()));
            }
            i++;
            if (mapaconceptos.get("1180") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1180").getValor()));
            }
            i++;
            if (mapaconceptos.get("1210") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1210").getValor()));
            }
            i++;
            if (mapaconceptos.get("1220") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1220").getValor()));
            }
            i++;
            if (mapaconceptos.get("1240") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1240").getValor()));
            }
            i++;
            if (mapaconceptos.get("1250") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1250").getValor()));
            }
            i++;
            if (mapaconceptos.get("1251") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1251").getValor()));
            }
            i++;
            if (mapaconceptos.get("1260") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1260").getValor()));
            }
            i++;
            if (mapaconceptos.get("1270") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1270").getValor()));
            }
            i++;
            if (mapaconceptos.get("1450") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1450").getValor()));
            }
            i++;
            if (mapaconceptos.get("1451") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1451").getValor()));
            }
            i++;
            if (mapaconceptos.get("1452") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1452").getValor()));
            }
            i++;
            if (mapaconceptos.get("1480") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1480").getValor()));
            }
            i++;
            if (mapaconceptos.get("1490") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1490").getValor()));
            }
            i++;
            if (mapaconceptos.get("1500") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1500").getValor()));
            }
            i++;
            if (mapaconceptos.get("1501") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1501").getValor()));
            }
            i++;
            if (mapaconceptos.get("1560") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1560").getValor()));
            }
            i++;
            if (mapaconceptos.get("1570") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1570").getValor()));
            }
            i++;
            if (mapaconceptos.get("1580") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1580").getValor()));
            }
            i++;
            if (mapaconceptos.get("1600") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1600").getValor()));
            }
            i++;
            if (mapaconceptos.get("1610") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1610").getValor()));
            }
            i++;
            if (mapaconceptos.get("1614") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1614").getValor()));
            }
            i++;
            if (mapaconceptos.get("1620") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1620").getValor()));
            }
            i++;
            if (mapaconceptos.get("1621") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1621").getValor()));
            }
            i++;
            if (mapaconceptos.get("1622") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1622").getValor()));
            }
            i++;
            if (mapaconceptos.get("1630") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1630").getValor()));
            }
            i++;
            if (mapaconceptos.get("1640") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1640").getValor()));
            }
            i++;
            if (mapaconceptos.get("1641") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1641").getValor()));
            }
            i++;
            if (mapaconceptos.get("1670") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1670").getValor()));
            }
            i++;
            if (mapaconceptos.get("1701") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1701").getValor()));
            }
            i++;
            if (mapaconceptos.get("1702") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1702").getValor()));
            }
            i++;
            if (mapaconceptos.get("1710") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1710").getValor()));
            }
//            
            i++;
            if (mapaconceptos.get("1720") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1720").getValor()));
            }
//            
            i++;
            cell = row.createCell(i);
            cell.setCellValue(nformat.format(n.getTotalPercepcion()));
            i++;
            if (mapaconceptos.get("2110") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("2110").getValor()));
            }
            i++;
            if (mapaconceptos.get("2120") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("2120").getValor()));
            }
            i++;
            if (mapaconceptos.get("2130") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("2130").getValor()));
            }
            i++;
            if (mapaconceptos.get("2210") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("2210").getValor()));
            }
            i++;
            if (mapaconceptos.get("2220") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("2220").getValor()));
            }
            i++;
            if (mapaconceptos.get("2230") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("2230").getValor()));
            }
            i++;
            if (mapaconceptos.get("2240") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("2240").getValor()));
            }
            i++;
            if (mapaconceptos.get("2260") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("2260").getValor()));
            }
            i++;
            if (mapaconceptos.get("2310") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("2310").getValor()));
            }
            i++;
            if (mapaconceptos.get("2311") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("2311").getValor()));
            }
            i++;
            if (mapaconceptos.get("2410") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("2410").getValor()));
            }
            i++;
            if (mapaconceptos.get("2500") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("2500").getValor()));
            }
            i++;
            if (mapaconceptos.get("2610") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("2610").getValor()));
            }
            i++;
            if (mapaconceptos.get("2620") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("2620").getValor()));
            }
            i++;
            if (mapaconceptos.get("2630") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("2630").getValor()));
            }
            i++;
            if (mapaconceptos.get("2631") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("2631").getValor()));
            }
            i++;
            if (mapaconceptos.get("2637") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("2637").getValor()));
            }
            i++;
            if (mapaconceptos.get("2650") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("2650").getValor()));
            }
            i++;
            if (mapaconceptos.get("2660") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("2660").getValor()));
            }
            i++;
            if (mapaconceptos.get("2661") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("2661").getValor()));
            }
            i++;
            if (mapaconceptos.get("2710") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("2710").getValor()));
            }
            i++;
            if (mapaconceptos.get("2715") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("2715").getValor()));
            }
            i++;
            if (mapaconceptos.get("2720") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("2720").getValor()));
            }
            i++;
            if (mapaconceptos.get("2721") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("2721").getValor()));
            }
            i++;
            if (mapaconceptos.get("2730") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("2730").getValor()));
            }
            i++;
            if (mapaconceptos.get("2810") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("2810").getValor()));
            }
            i++;
            if (mapaconceptos.get("2820") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("2820").getValor()));
            }
            i++;
            if (mapaconceptos.get("2830") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("2830").getValor()));
            }
            i++;
            cell = row.createCell(i);
            cell.setCellValue(nformat.format(n.getTotalDeduccion()));
//            
            i++;
            cell = row.createCell(i);
            cell.setCellValue(nformat.format(n.getTotalPercepcion() - n.getTotalDeduccion()));
            i++;
//            
//            i++;
//            double provisionfiniquito = ((n.getTotalPercepcion() / 24) * 1.03) + (n.getTotalPercepcion() * .02);
//            cell = row.createCell(i);
//            cell.setCellValue(nformat.format(provisionfiniquito));
//            
            i++;
            double costopersonal = n.getTotalProvisiones() + n.getTotalPercepcion();
            cell = row.createCell(i);
            cell.setCellValue(nformat.format(costopersonal));

            i++;
            double fee = costopersonal * .05;
            cell = row.createCell(i);
            cell.setCellValue(nformat.format(fee));

            i++;
            double costoporpersona = costopersonal + fee;
            cell = row.createCell(i);
            cell.setCellValue(nformat.format(costoporpersona));

            i++;
            double iva16 = costoporpersona * .16;
            cell = row.createCell(i);
            cell.setCellValue(nformat.format(iva16));
//
            i++;
            double iva6 = costoporpersona * .06;
            cell = row.createCell(i);
            cell.setCellValue(nformat.format(iva6));
//
            i++;
            double totalfactura = costoporpersona + iva16 - iva6;
            cell = row.createCell(i);
            cell.setCellValue(nformat.format(totalfactura));
            i++;

            i++;
            cell = row.createCell(i);
            cell.setCellValue(nformat.format(valesdespensa));

            i++;
            double comisionvales = valesdespensa * 0.025;
            cell = row.createCell(i);
            cell.setCellValue(nformat.format(comisionvales));

            i++;
            double feevales = (comisionvales + valesdespensa) * .01;
            cell = row.createCell(i);
            cell.setCellValue(nformat.format(feevales));

            i++;
            double totalvales = valesdespensa + comisionvales + feevales;
            cell = row.createCell(i);
            cell.setCellValue(nformat.format(totalvales));

            i++;
            double ivavales = totalvales * .16;
            cell = row.createCell(i);
            cell.setCellValue(nformat.format(ivavales));

            i++;
            double facturavales = totalvales + ivavales;
            cell = row.createCell(i);
            cell.setCellValue(nformat.format(facturavales));

////            
            j++;
        }

        this.escribirArchivo(workbook, nombreArchivo, tipoarchivo);
    }

    private void escribirArchivo(HSSFWorkbook book, String nombreArchivo, String tipoarchivo) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            book.write(baos);
            byte[] data = baos.toByteArray();
            RequestContext context = RequestContext.getCurrentInstance();
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, nombreArchivo);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, data);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, tipoarchivo);
            context.addCallbackParam("ruta", MyPaths.urlServletDescargarArchivoCsv());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void descargarArchivoCuotasImss(ActionEvent event) {
        GrupoPago gp = ControladorWS.getInstance().findGrupoPagoById(this.selectedGrupoPago);
        String nombreArchivo = "Cuotas_IMSS_" + gp.getGrupopago();
        this.archivoCuotasImss(nombreArchivo, ParametrosReportes.ARCHIVO_XLS);
    }

    private void archivoCuotasImss(String nombreArchivo, String tipoarchivo) {
        DecimalFormat nformat = new DecimalFormat("0.00");
//        
        HSSFWorkbook workbook = new HSSFWorkbook();
//        
        HSSFCellStyle styleamarillo = workbook.createCellStyle();
        styleamarillo.setFillForegroundColor(HSSFColor.YELLOW.index);
        styleamarillo.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
//        
        HSSFCellStyle stylegris = workbook.createCellStyle();
        stylegris.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
        stylegris.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
//        
        HSSFCellStyle styleazul = workbook.createCellStyle();
        HSSFFont font = workbook.createFont();
        font.setColor(HSSFColor.WHITE.index);
//        
        styleazul.setFont(font);
        styleazul.setFillForegroundColor(HSSFColor.BLUE.index);
        styleazul.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
//        
        HSSFSheet sheet = workbook.createSheet("Prenomina" + this.periodo + this.anio);
//        
        int i = 0;
        int j = 2;
//        
        HSSFRow row = sheet.createRow(j++);
        HSSFCell cell = row.createCell(i++);
        cell.setCellValue("NUMERO EMPLEADO");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("NOMBRE");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("SALARIO DIARIO");
        cell.setCellStyle(styleamarillo);

        cell = row.createCell(i++);
        cell.setCellValue("SALARIO DIARIO INTEGRADO");
        cell.setCellStyle(styleamarillo);

        cell = row.createCell(i++);
        cell.setCellValue("N.S.S.");
        cell.setCellStyle(styleamarillo);

        cell = row.createCell(i++);
        cell.setCellValue("1510/VALES DESPENSA");
        cell.setCellStyle(styleamarillo);
//          
        cell = row.createCell(i++);
        cell.setCellValue("NETO A PAGAR");
        cell.setCellStyle(styleamarillo);
//        
        cell = row.createCell(i++);
        cell.setCellValue("COSTO PERSONAL");
        cell.setCellStyle(styleazul);
//        
        cell = row.createCell(i++);
        cell.setCellValue("FEE");
        cell.setCellStyle(styleazul);
//        
        cell = row.createCell(i++);
        cell.setCellValue("TOTAL POR PERSONA");
        cell.setCellStyle(styleazul);
//        
        cell = row.createCell(i++);
        cell.setCellValue("I.V.A. 16%");
        cell.setCellStyle(styleazul);
//        
        cell = row.createCell(i++);
        cell.setCellValue("TOTAL FACTURA");
        cell.setCellStyle(styleazul);
//        
        i++;
        cell = row.createCell(i++);
        cell.setCellValue("DESPENSA");
        cell.setCellStyle(styleazul);
//        
        cell = row.createCell(i++);
        cell.setCellValue("COMISIÓN DESPENSA");
        cell.setCellStyle(styleazul);
//        
        cell = row.createCell(i++);
        cell.setCellValue("FEE");
        cell.setCellStyle(styleazul);
//        
        cell = row.createCell(i++);
        cell.setCellValue("TOTAL VALES");
        cell.setCellStyle(styleazul);
//        
        cell = row.createCell(i++);
        cell.setCellValue("I.V.A. 16%");
        cell.setCellStyle(styleazul);
//        
        cell = row.createCell(i++);
        cell.setCellValue("TOTAL FACTURA");
        cell.setCellStyle(styleazul);
//////        
        Iterator<Nomina> iter = this.nominas.iterator();
        while (iter.hasNext()) {
            Nomina n = iter.next();
//            
            i = 0;
////            
            Map<String, Concepto> mapaconceptos = new HashMap<>();
            for (Concepto c : n.getPercepciones()) {
                if (mapaconceptos.get(c.getConcepto()) == null) {
                    mapaconceptos.put(c.getConcepto(), c);
                } else {
                    double importe = mapaconceptos.get(c.getConcepto()).getValor() + c.getValor();
                    mapaconceptos.get(c.getConcepto()).setValor(importe);
                }
            }
            for (Concepto c : n.getDeducciones()) {
                if (mapaconceptos.get(c.getConcepto()) == null) {
                    mapaconceptos.put(c.getConcepto(), c);
                } else {
                    double importe = mapaconceptos.get(c.getConcepto()).getValor() + c.getValor();
                    mapaconceptos.get(c.getConcepto()).setValor(importe);
                }

            }
            for (Concepto c : n.getProvisiones()) {
                if (mapaconceptos.get(c.getConcepto()) == null) {
                    mapaconceptos.put(c.getConcepto(), c);
                } else {
                    double importe = mapaconceptos.get(c.getConcepto()).getValor() + c.getValor();
                    mapaconceptos.get(c.getConcepto()).setValor(importe);
                }
            }
////            
            RelacionLaboral rellab = n.getRelacionlaboral();
            row = sheet.createRow(j);
//          NUMERO EMPLEADO
            cell = row.createCell(i++);
            cell.setCellValue(rellab.getNumeroempleado());
//          NOMBRE EMPLEADO
            cell = row.createCell(i++);
            String nombreempleado = Util.getApellidosNombre(rellab.getIdempleado());
            cell.setCellValue(nombreempleado);
//          SALARIO DIARIO  
            cell = row.createCell(i++);
            cell.setCellValue(nformat.format(rellab.getSalarioDiario()));
//          SALARIO DIARIO INTEGRADO  
            cell = row.createCell(i++);
            cell.setCellValue(nformat.format(rellab.getSalarioDiarioIntegrado()));
//          NSS
            cell = row.createCell(i++);
            cell.setCellValue(rellab.getIdempleado().getAfiliacion());
//          VALES DESPENSA
            double valesdespensa = 0.0;
            if (mapaconceptos.get("1510") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1510").getValor()));
                valesdespensa = mapaconceptos.get("1510").getValor();
            }
            i++;
            if (mapaconceptos.get("1511") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1511").getValor()));
            }
            i++;
            if (mapaconceptos.get("1512") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1512").getValor()));
            }
            i++;
            if (mapaconceptos.get("1513") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1513").getValor()));
            }
            i++;
            if (mapaconceptos.get("3500") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("3500").getValor()));
            }
            i++;
            if (mapaconceptos.get("3510") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("3510").getValor()));
            }
            i++;
            if (mapaconceptos.get("3511") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("3511").getValor()));
            }
            i++;
            if (mapaconceptos.get("3210") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("3210").getValor()));
            }
            i++;
            if (mapaconceptos.get("3220") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("3220").getValor()));
            }
            i++;
            if (mapaconceptos.get("1161") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1161").getValor()));
            }
            i++;
            if (mapaconceptos.get("1162") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1162").getValor()));
            }
            i++;
            if (mapaconceptos.get("3110") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("3110").getValor()));
            }
            i++;

            if (mapaconceptos.get("3107") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("3107").getValor()));
            }
            i++;
            if (mapaconceptos.get("3106") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("3106").getValor()));
            }
            i++;
            if (mapaconceptos.get("3101") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("3101").getValor()));
            }
            i++;
            if (mapaconceptos.get("3102") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("3102").getValor()));
            }
            i++;
            if (mapaconceptos.get("3103") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("3103").getValor()));
            }
            i++;
            if (mapaconceptos.get("3104") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("3104").getValor()));
            }
            i++;
            if (mapaconceptos.get("3105") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("3105").getValor()));
            }
            i++;
            if (mapaconceptos.get("3140") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("3140").getValor()));
            }
            i++;
            if (mapaconceptos.get("3150") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("3150").getValor()));
            }
            i++;
            if (mapaconceptos.get("3160") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("3160").getValor()));
            }
            i++;
            if (mapaconceptos.get("3120") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("3120").getValor()));
            }
            i++;
            cell = row.createCell(i);
            cell.setCellValue(nformat.format(n.getTotalProvisiones()));
//            
            i++;
            if (mapaconceptos.get("3512") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("3512").getValor()));
            }
//            
            i++;
            if (mapaconceptos.get("3513") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("3513").getValor()));
            }

            i++;
            if (mapaconceptos.get("3514") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("3514").getValor()));
            }
//            
            i++;
            if (mapaconceptos.get("1110") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1110").getValor()));
            }
            i++;
            if (mapaconceptos.get("1115") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1110").getValor()));
            }
            i++;
            if (mapaconceptos.get("1120") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1120").getValor()));
            }
            i++;
            if (mapaconceptos.get("1130") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1130").getValor()));
            }
            i++;
            if (mapaconceptos.get("1140") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1140").getValor()));
            }
            i++;
            if (mapaconceptos.get("1150") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1150").getValor()));
            }
            i++;
            if (mapaconceptos.get("1160") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1160").getValor()));
            }
            i++;
            if (mapaconceptos.get("1161") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1161").getValor()));
            }
            i++;
            if (mapaconceptos.get("1162") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1162").getValor()));
            }
            i++;
            if (mapaconceptos.get("1180") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1180").getValor()));
            }
            i++;
            if (mapaconceptos.get("1210") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1210").getValor()));
            }
            i++;
            if (mapaconceptos.get("1220") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1220").getValor()));
            }
            i++;
            if (mapaconceptos.get("1240") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1240").getValor()));
            }
            i++;
            if (mapaconceptos.get("1250") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1250").getValor()));
            }
            i++;
            if (mapaconceptos.get("1251") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1251").getValor()));
            }
            i++;
            if (mapaconceptos.get("1260") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1260").getValor()));
            }
            i++;
            if (mapaconceptos.get("1270") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1270").getValor()));
            }
            i++;
            if (mapaconceptos.get("1450") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1450").getValor()));
            }
            i++;
            if (mapaconceptos.get("1451") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1451").getValor()));
            }
            i++;
            if (mapaconceptos.get("1452") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1452").getValor()));
            }
            i++;
            if (mapaconceptos.get("1480") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1480").getValor()));
            }
            i++;
            if (mapaconceptos.get("1490") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1490").getValor()));
            }
            i++;
            if (mapaconceptos.get("1500") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1500").getValor()));
            }
            i++;
            if (mapaconceptos.get("1501") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1501").getValor()));
            }
            i++;
            if (mapaconceptos.get("1600") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1600").getValor()));
            }
            i++;
            if (mapaconceptos.get("1610") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1610").getValor()));
            }
            i++;
            if (mapaconceptos.get("1620") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1620").getValor()));
            }
            i++;
            if (mapaconceptos.get("1621") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1621").getValor()));
            }
            i++;
            if (mapaconceptos.get("1622") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1622").getValor()));
            }
            i++;
            if (mapaconceptos.get("1630") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1630").getValor()));
            }
            i++;
            if (mapaconceptos.get("1701") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1701").getValor()));
            }
            i++;
            if (mapaconceptos.get("1702") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("1702").getValor()));
            }
//            
            i++;
            cell = row.createCell(i);
            cell.setCellValue(nformat.format(n.getTotalPercepcion()));
            i++;
            if (mapaconceptos.get("2110") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("2110").getValor()));
            }
            i++;
            if (mapaconceptos.get("2120") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("2120").getValor()));
            }
            i++;
            if (mapaconceptos.get("2130") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("2130").getValor()));
            }
            i++;
            if (mapaconceptos.get("2210") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("2210").getValor()));
            }
            i++;
            if (mapaconceptos.get("2220") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("2220").getValor()));
            }
            i++;
            if (mapaconceptos.get("2230") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("2230").getValor()));
            }
            i++;
            if (mapaconceptos.get("2240") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("2240").getValor()));
            }
            i++;
            if (mapaconceptos.get("2260") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("2260").getValor()));
            }
            i++;
            if (mapaconceptos.get("2310") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("2310").getValor()));
            }
            i++;
            if (mapaconceptos.get("2311") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("2311").getValor()));
            }
            i++;
            if (mapaconceptos.get("2410") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("2410").getValor()));
            }
            i++;
            if (mapaconceptos.get("2500") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("2500").getValor()));
            }
            i++;
            if (mapaconceptos.get("2610") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("2610").getValor()));
            }
            i++;
            if (mapaconceptos.get("2620") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("2620").getValor()));
            }
            i++;
            if (mapaconceptos.get("2630") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("2630").getValor()));
            }
            i++;
            if (mapaconceptos.get("2631") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("2631").getValor()));
            }
            i++;
            if (mapaconceptos.get("2650") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("2650").getValor()));
            }
            i++;
            if (mapaconceptos.get("2660") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("2660").getValor()));
            }
            i++;
            if (mapaconceptos.get("2661") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("2661").getValor()));
            }
            i++;
            if (mapaconceptos.get("2710") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("2710").getValor()));
            }
            i++;
            if (mapaconceptos.get("2715") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("2715").getValor()));
            }
            i++;
            if (mapaconceptos.get("2720") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("2720").getValor()));
            }
            i++;
            if (mapaconceptos.get("2721") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("2721").getValor()));
            }
            i++;
            if (mapaconceptos.get("2730") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("2730").getValor()));
            }
            i++;
            if (mapaconceptos.get("2810") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("2810").getValor()));
            }
            i++;
            if (mapaconceptos.get("2820") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("2820").getValor()));
            }
            i++;
            if (mapaconceptos.get("2830") != null) {
                cell = row.createCell(i);
                cell.setCellValue(nformat.format(mapaconceptos.get("2830").getValor()));
            }
            i++;
            cell = row.createCell(i);
            cell.setCellValue(nformat.format(n.getTotalDeduccion()));
//            
            i++;
            cell = row.createCell(i);
            cell.setCellValue(nformat.format(n.getTotalPercepcion() - n.getTotalDeduccion()));
            i++;
//            
//            i++;
//            double provisionfiniquito = ((n.getTotalPercepcion() / 24) * 1.03) + (n.getTotalPercepcion() * .02);
//            cell = row.createCell(i);
//            cell.setCellValue(nformat.format(provisionfiniquito));
//            
            i++;
            double costopersonal = n.getTotalProvisiones() + n.getTotalPercepcion();
            cell = row.createCell(i);
            cell.setCellValue(nformat.format(costopersonal));

            i++;
            double fee = costopersonal * .05;
            cell = row.createCell(i);
            cell.setCellValue(nformat.format(fee));

            i++;
            double costoporpersona = costopersonal + fee;
            cell = row.createCell(i);
            cell.setCellValue(nformat.format(costoporpersona));

            i++;
            double iva16 = costoporpersona * .16;
            cell = row.createCell(i);
            cell.setCellValue(nformat.format(iva16));

            i++;
            double iva6 = costoporpersona * .06;
            cell = row.createCell(i);
            cell.setCellValue(nformat.format(iva6));

            i++;
            double totalfactura = costoporpersona + iva16 - iva6;
            cell = row.createCell(i);
            cell.setCellValue(nformat.format(totalfactura));
            i++;

            i++;
            cell = row.createCell(i);
            cell.setCellValue(nformat.format(valesdespensa));

            i++;
            double comisionvales = valesdespensa * 0.025;
            cell = row.createCell(i);
            cell.setCellValue(nformat.format(comisionvales));

            i++;
            double feevales = (comisionvales + valesdespensa) * .01;
            cell = row.createCell(i);
            cell.setCellValue(nformat.format(feevales));

            i++;
            double totalvales = valesdespensa + comisionvales + feevales;
            cell = row.createCell(i);
            cell.setCellValue(nformat.format(totalvales));

            i++;
            double ivavales = totalvales * .16;
            cell = row.createCell(i);
            cell.setCellValue(nformat.format(ivavales));

            i++;
            double facturavales = totalvales + ivavales;
            cell = row.createCell(i);
            cell.setCellValue(nformat.format(facturavales));

////            
            j++;
        }

        this.escribirArchivo(workbook, nombreArchivo, tipoarchivo);
    }

    public void bloquearUsuarios() {
        boolean isValidate = ControladorWS.getInstance().bloqueoNominas(1);
        FacesMessage msg = isValidate ? Util.getFacesMessage(FacesMessage.SEVERITY_INFO, "Los usuarios fueron BLOQUEADOS exitosamente!")
                : Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, "Los usuarios no fueron BLOQUEADOS");
        if (isValidate) {
            this.bloqueo = ControladorWS.getInstance().getBloqueoNominas();
        }
        this.generarMsg(msg, FacesMessage.SEVERITY_INFO.equals(msg.getSeverity()));
    }

    public void desBloquearUsuarios() {
        boolean isValidate = ControladorWS.getInstance().bloqueoNominas(0);
        FacesMessage msg = isValidate ? Util.getFacesMessage(FacesMessage.SEVERITY_INFO, "Los usuarios fueron DESBLOQUEADOS exitosamente!")
                : Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, "Los usuarios no fueron DESBLOQUEADOS");
        if (isValidate) {
            this.bloqueo = ControladorWS.getInstance().getBloqueoNominas();
        }
        this.generarMsg(msg, FacesMessage.SEVERITY_INFO.equals(msg.getSeverity()));
    }

    @Override
    public void listenerSelected() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void generarMsg(FacesMessage msg, boolean isValidate) {
        System.out.println("Genera MSG... " + msg.getSeverity() + " | " + isValidate);
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().addMessage(null, msg);
        context.addCallbackParam("isValidate", isValidate);
    }

    public void descargarReporte() {
        GrupoPago gp = ControladorWS.getInstance().findGrupoPagoById(this.selectedGrupoPago);
        String nombreArchivo = gp.getGrupopago() + "_" + this.periodo + "_" + auxPeriodo.getAnio();
//        
        switch (this.selectedtipo) {
            case 0:
                this.archivocsv(nombreArchivo, ParametrosReportes.ARCHIVO_CSV);
                break;
            default:
                this.archivoexcel(nombreArchivo, ParametrosReportes.ARCHIVO_XLS);
                break;
        }
    }

    private void archivocsv(String nombreArchivo, String tipoarchivo) {
        String file = ControladorWS.getInstance().getPathNomina() + nombreArchivo + "." + tipoarchivo;
//        
        try {
//            
            Path path = Paths.get(file);
            byte[] data = Files.readAllBytes(path);
            RequestContext context = RequestContext.getCurrentInstance();
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, nombreArchivo);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, data);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, tipoarchivo);
            context.addCallbackParam("ruta", MyPaths.urlServletDescargarArchivoCsv());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void archivoexcel(String nombreArchivo, String tipoarchivo) {
        DecimalFormat nformat = new DecimalFormat("0.00");
        Map<String, Integer> mapa = new HashMap<>();
////        
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("Prenomina");
//        
        HSSFRow row = sheet.createRow(0);
//        
        HSSFCell cell = row.createCell(0);
        cell.setCellValue("NUMERO EMPLEADO");
        cell = row.createCell(1);
        cell.setCellValue("NOMBRE");
        cell = row.createCell(2);
        cell.setCellValue("F. ANTIGUEDAD");
        cell = row.createCell(3);
        cell.setCellValue("CVE. CCO");
        cell = row.createCell(4);
        cell.setCellValue("DESC. CCO");
        cell = row.createCell(5);
        cell.setCellValue("CVE. DEPTO");
        cell = row.createCell(6);
        cell.setCellValue("DESCR. DEPTO");
        cell = row.createCell(7);
        cell.setCellValue("CVE. PUESTO");
        cell = row.createCell(8);
        cell.setCellValue("DESCR. PUESTO");
        cell = row.createCell(9);
        cell.setCellValue("REG. PATRONAL");
        cell = row.createCell(10);
        cell.setCellValue("DESCR. REG. PATRONAL");
        cell = row.createCell(11);
        cell.setCellValue("N.S.S.");
        cell = row.createCell(12);
        cell.setCellValue("SALARIO DIARIO");
        cell = row.createCell(13);
        cell.setCellValue("SALARIO DIARIO INTEGRADO");
        cell = row.createCell(14);
        cell.setCellValue("NETO PAGAR");
////        
        int i = 1;
        int k = 15;
//        
        Iterator<Nomina> iter = this.nominas.iterator();
        while (iter.hasNext()) {
            Nomina n = iter.next();
            int j = 0;
//            
            RelacionLaboral rl = n.getRelacionlaboral();
            Empleado emp = rl.getIdempleado();
            RelacionLaboralPosicion pos = rl.getIdrelacionlaboralposicion();
//          NUMEROEMPLEADO  
            row = sheet.createRow(i);
            cell = row.createCell(j);
            cell.setCellValue(rl.getNumeroempleado());
            j++;
//          NOMBRE  
            StringBuilder stb = new StringBuilder();
            stb.append(emp.getApellidopaterno());
            if (emp.getApellidomaterno() != null) {
                stb.append(" ").append(emp.getApellidomaterno());
            }
            stb.append(" ").append(emp.getNombre());
            cell = row.createCell(j);
            cell.setCellValue(stb.toString());
            j++;
//          FECHA ANTIGUEDAD  
            cell = row.createCell(j);
            cell.setCellValue(rl.getFechaAntiguedadStr());
            j++;
//          CVE CCO  
            String aux = pos.getIdcentrocosto() != null && pos.getIdcentrocosto().getCentroCosto() != null ? pos.getIdcentrocosto().getCentroCosto() : "S/A";
            cell = row.createCell(j);
            cell.setCellValue(aux);
            j++;
//          CVE CCO  
            aux = pos.getIdcentrocosto() != null && pos.getIdcentrocosto().getNombre() != null ? pos.getIdcentrocosto().getNombre() : "S/A";
            cell = row.createCell(j);
            cell.setCellValue(aux);
            j++;
//          CVE DEPTO
            aux = pos.getIddepartamento() != null && pos.getIddepartamento().getDepartamento() != null ? pos.getIddepartamento().getDepartamento() : "S/A";
            cell = row.createCell(j);
            cell.setCellValue(aux);
            j++;
//          DESCR DEPTO
            aux = pos.getIddepartamento() != null && pos.getIddepartamento().getNombre() != null ? pos.getIddepartamento().getNombre() : "S/A";
            cell = row.createCell(j);
            cell.setCellValue(aux);
            j++;
//          CVE PUESTO
            aux = pos.getIdpuesto() != null && pos.getIdpuesto().getPuesto() != null ? pos.getIdpuesto().getPuesto() : "S/A";
            cell = row.createCell(j);
            cell.setCellValue(aux);
            j++;
//          DESCR PUESTO
            aux = pos.getIdpuesto() != null && pos.getIdpuesto().getNombre() != null ? pos.getIdpuesto().getNombre() : "S/A";
            cell = row.createCell(j);
            cell.setCellValue(aux);
            j++;
//          REGISTRO PATRONAL
            aux = rl.getIdregistropatronal() != null && rl.getIdregistropatronal().getRegistropatronal() != null ? rl.getIdregistropatronal().getRegistropatronal() : "S/A";
            cell = row.createCell(j);
            cell.setCellValue(aux);
            j++;
//          REGISTRO PATRONAL
            aux = rl.getIdregistropatronal() != null && rl.getIdregistropatronal().getComentarios() != null ? rl.getIdregistropatronal().getComentarios() : "S/A";
            cell = row.createCell(j);
            cell.setCellValue(aux);
            j++;
//          NSS
            aux = emp.getAfiliacion() != null ? emp.getAfiliacion() : "S/A";
            cell = row.createCell(j);
            cell.setCellValue(aux);
            j++;
//          SALARIO DIARIO
            cell = row.createCell(j);
            cell.setCellValue(nformat.format(rl.getSalarioDiario()));
            j++;
//          SALARIO DIARIO INTEGRADO
            cell = row.createCell(j);
            cell.setCellValue(nformat.format(rl.getSalarioDiarioIntegrado()));
            j++;
//          NETO A PAGAR
            cell = row.createCell(j);
            cell.setCellValue(nformat.format(n.getNetoPagar()));
            j++;
//            
            List<Concepto> conceptos = new ArrayList<>();
            conceptos.addAll(n.getPercepciones());
            conceptos.addAll(n.getDeducciones());
            conceptos.addAll(n.getProvisiones());
//            
            Collections.sort(conceptos, new Comparator<Concepto>() {
                @Override
                public int compare(Concepto c1, Concepto c2) {
                    return c1.getConcepto().compareTo(c2.getConcepto());
                }
            });
//          REGISTRO  
            for (Concepto c : conceptos) {
//                
                if (c.getIncidencia() != 1 || c.getValor() == 0) {
                    continue;
                }
////                
                if (mapa.get(c.getConcepto()) == null) {
                    row = sheet.getRow(0);
                    mapa.put(c.getConcepto(), k);
                    switch (this.selectedtipo) {
                        case 1:
                            cell = row.createCell(k);
                            cell.setCellValue(c.getConcepto() + "-" + c.getNombre() + "/IMPORTE");
                            cell = row.createCell(k + 1);
                            cell.setCellValue(c.getConcepto() + "-" + c.getNombre() + "/TIEMPO");
                            k++;
                            break;
                        case 2:
                            cell = row.createCell(k);
                            cell.setCellValue(c.getConcepto() + "-" + c.getNombre() + "/IMPORTE");
                            break;
                        case 3:
                            cell = row.createCell(k);
                            cell.setCellValue(c.getConcepto() + "-" + c.getNombre() + "/TIEMPO");
                            break;
                    }
                    k++;
                }
////                
                int index = mapa.get(c.getConcepto());

                switch (this.selectedtipo) {
                    case 1:
                        row = sheet.getRow(i);
                        cell = row.createCell(index);
                        cell.setCellValue(c.getValor());
                        cell = row.createCell(index + 1);
                        cell.setCellValue(c.getTiempo());
                        break;
                    case 2:
                        row = sheet.getRow(i);
                        cell = row.createCell(index);
                        cell.setCellValue(c.getValor());
                        break;
                    case 3:
                        row = sheet.getRow(i);
                        cell = row.createCell(index);
                        cell.setCellValue(c.getTiempo());
                        break;
                }
////
            }

            i++;
        }
        this.escribirArchivo(workbook, nombreArchivo, tipoarchivo);
    }

    @Override
    public String getInformacion() {
        return CeniccoUtil.getInformacion(this.nominas.size());
    }

    public String[] getSelectedRelaciones() {
        return selectedRelaciones;
    }

    public void setSelectedRelaciones(String[] selectedRelaciones) {
        this.selectedRelaciones = selectedRelaciones;
    }

    public List<String> getRelaciones() {
        return relaciones;
    }

    public List<GrupoConcepto> getTiposNomina() {
        return tiposNomina;
    }

    public Integer getSelectedTipoNomina() {
        return selectedTipoNomina;
    }

    public void setSelectedTipoNomina(Integer selectedTipoNomina) {
        this.selectedTipoNomina = selectedTipoNomina;
    }

    public List<Nomina> getFilteredNominas() {
        return filteredNominas;
    }

    public void setFilteredNominas(List<Nomina> filteredNominas) {
        this.filteredNominas = filteredNominas;
    }

    public List<Nomina> getNominas() {
        return nominas;
    }

    public Double getTotalprovisiones() {
        return totalprovisiones;
    }

    public Double getTotalpercepciones() {
        return totalpercepciones;
    }

    public Double getTotaldeducciones() {
        return totaldeducciones;
    }

    public Double getTotalneto() {
        return totalneto;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Integer getPeriodo() {
        return periodo;
    }

    public void setPeriodo(Integer periodo) {
        this.periodo = periodo;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public boolean isBloqueo() {
        return bloqueo;
    }

    public Integer getSelectedtipo() {
        return selectedtipo;
    }

    public void setSelectedtipo(Integer selectedtipo) {
        this.selectedtipo = selectedtipo;
    }

    public Integer getSelectedestatus() {
        return selectedestatus;
    }

    public void setSelectedestatus(Integer selectedestatus) {
        this.selectedestatus = selectedestatus;
    }

    public Integer getIncidenciasSinAutorizar() {
        return incidenciasSinAutorizar;
    }

    public void setIncidenciasSinAutorizar(Integer incidenciasSinAutorizar) {
        this.incidenciasSinAutorizar = incidenciasSinAutorizar;
    }

    public void descargarDetalleISR() {
        GrupoPago gp = ControladorWS.getInstance().findGrupoPagoById(this.selectedGrupoPago);
        String nombreArchivo = "DETALLE ISR_" + gp.getGrupopago() + "_" + this.periodo + "_" + this.anio;
        String pathDetalleISR = "_DETALLE ISR_" + gp.getGrupopago() + "_" + this.periodo + "_" + this.anio + "." + ParametrosReportes.ARCHIVO_XLS;
        String file = ControladorWS.getInstance().getPathNomina() + pathDetalleISR;
        try {
            Path path = Paths.get(file);
            byte[] data = Files.readAllBytes(path);
            RequestContext context = RequestContext.getCurrentInstance();
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, nombreArchivo);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, data);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_XLS);
            context.addCallbackParam("ruta", MyPaths.urlServletDescargarArchivoCsv());
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void descargarAnalisisIMSS() {
        GrupoPago gp = ControladorWS.getInstance().findGrupoPagoById(this.selectedGrupoPago);
        String nombreArchivo = "ANALISIS IMSS_" + gp.getGrupopago() + "_" + this.periodo + "_" + this.anio;
        String pathAnalisisIMSS = "_ANALISIS IMSS_" + gp.getGrupopago() + "_" + this.periodo + "_" + this.anio + "." + ParametrosReportes.ARCHIVO_XLS;
        String file = ControladorWS.getInstance().getPathNomina() + pathAnalisisIMSS;
        try {
            Path path = Paths.get(file);
            byte[] data = Files.readAllBytes(path);
            RequestContext context = RequestContext.getCurrentInstance();
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, nombreArchivo);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, data);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_XLS);
            context.addCallbackParam("ruta", MyPaths.urlServletDescargarArchivoCsv());
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}