package com.lam.cenicco.beans.administracion.catalogo;

import com.lam.cenicco.beans.administracion.dependientes.DependienteCenicco;
import com.lam.cenicco.beans.administracion.informacioncomplementaria.InformacionComplementariaCenicco;
import com.lam.cenicco.beans.configuracion.contactos.ContactoCenicco;
import com.lam.cenicco.beans.configuracion.cuentasbancarias.CuentaBancariaCenicco;
import com.lam.cenicco.beans.configuracion.documentos.DocumentosCenicco;
import com.lam.cenicco.beans.configuracion.domicilios.DomiciliosCenicco;
import com.lam.cenicco.beans.configuracion.infonavit.InfonavitCenicco;
import com.lam.cenicco.beans.configuracion.pensiones.PensionCenicco;
import com.lam.cenicco.beans.configuracion.posiciones.PosicionCenicco;
import com.lam.cenicco.dao.ProcesoDAO;
import com.lam.cenicco.impl.ControladorWS;
import com.lam.cenicco.librerias.mensajes.Mensajes;
import com.lam.cenicco.librerias.utilidades.CeniccoUtil;
import com.lam.cenicco.mail.ManejadorCorreo;
import com.lam.cenicco.servlets.reportes.GeneradorReportes;
import com.lam.cenicco.util.ActionRenders;
import com.lam.cenicco.util.ControladorSesiones;
import com.lam.cenicco.util.Modulos;
import com.lam.cenicco.util.Parametros;
import com.lam.cenicco.util.ParametrosReportes;
import com.lam.cenicco.util.Util;
import com.lam.cenicco.util.paths.MyPaths;
import com.lam.cenicco.ws.CompaniaProveedorVale;
import com.lam.cenicco.ws.Departamento;
import com.lam.cenicco.ws.Documento;
import com.lam.cenicco.ws.Domicilio;
import com.lam.cenicco.ws.Empleado;
import com.lam.cenicco.ws.EmpleadoVale;
import com.lam.cenicco.ws.Estado;
import com.lam.cenicco.ws.Estructura;
import com.lam.cenicco.ws.EstructuraDatos;
import com.lam.cenicco.ws.HistoricoSueldo;
import com.lam.cenicco.ws.JornadaLaboral;
import com.lam.cenicco.ws.ProveedorVale;
import com.lam.cenicco.ws.RelacionLaboral;
import com.lam.cenicco.ws.SubDepartamento;
import com.lam.cenicco.ws.TabuladorSistemaAntiguedad;
import com.lam.cenicco.ws.TipoVale;
import com.lam.cenicco.ws.TurnoLaboral;
import com.lam.cenicco.ws.VistaEmpleados;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import org.apache.poi.util.IOUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author JoséAntonio
 */
public class CatalogoEmpleadoCenicco implements ProcesoDAO<VistaEmpleados> {
//

    protected VistaEmpleados vista;
//    
    protected VistaEmpleados selectedVista;
//    
    protected RelacionLaboral relacionLaboral;
//
    protected List<VistaEmpleados> vistas;
//    
    protected List<VistaEmpleados> filteredVistas;
//    
    protected List<VistaEmpleados> selectedVistas;
//        
    protected List<EstructuraDatos> datos;
//    
    protected List<HistoricoSueldo> historicosueldos;
//    
    protected ActionRenders render;
//    
    protected ManejadorCorreo manejadorCorreo = new ManejadorCorreo();
//    
    protected Date fechaInicioSD;
    protected Date fechaInicioSDI;
    protected Date fechaInicioAlta;
    protected Date fechaFinAlta;
    protected Date fechaInicioBaja;
    protected Date fechaFinBaja;
    protected Date fechaNacimiento;
    protected Date fechaVencimiento;
    protected Date fechaAntiguedad;
    protected Date fechaAntiguedadDos;
    protected Date fechaAntiguedadTres;
//
    protected String colorEstatus;
    protected String estatusEmpleado;
    protected boolean estatusJefeDirecto;
    protected String compania;
//
    protected DomiciliosCenicco domicilioCenicco = new DomiciliosCenicco();
    protected DocumentosCenicco documentoCenicco = new DocumentosCenicco();
    protected ContactoCenicco contactoCenicco = new ContactoCenicco();
    protected PosicionCenicco posicionLaboral = new PosicionCenicco();
    protected CuentaBancariaCenicco cuentaBancaria = new CuentaBancariaCenicco();
    protected InfonavitCenicco infonavitLaboral = new InfonavitCenicco(this.manejadorCorreo);
    protected InformacionComplementariaCenicco complementaria = new InformacionComplementariaCenicco();
    protected PensionCenicco pensionLaboral = new PensionCenicco();
    protected DependienteCenicco dependienteCenicco = new DependienteCenicco();
//    
    protected Integer selectedEstructura;
//    
    protected UploadedFile archivo;
//    
    protected UploadedFile foto;
//        
    protected Double salarioDiario;
//    
    protected Double salarioDiarioIntegrado;
//        
    protected List<CompaniaProveedorVale> proveedoresVales;
//        
    protected Integer idcompaniaproveedorvale;
//
    protected String cuentavale;
//
    protected String numerotarjetavale;
//
    protected List<EmpleadoVale> empleadoVales;
//
    private Integer selectIdJornadaLaboral = null;
    private Integer selectIdDepartamento = null;
    private Integer selectIdTurnoLaboral = null;
    private List<JornadaLaboral> jornadaslaborales = new ArrayList<>();
    private List<TurnoLaboral> turnos = new ArrayList<>();
    private List<SubDepartamento> subDepartamentos;
    private SubDepartamento selectedSubdepartamento;

    public CatalogoEmpleadoCenicco() {
        if (this.vista == null) {
            this.vista = new VistaEmpleados();
        }
        if (this.relacionLaboral == null) {
            this.relacionLaboral = new RelacionLaboral();
        }
        if (this.manejadorCorreo == null) {
            this.manejadorCorreo = new ManejadorCorreo();
        }
        if (this.domicilioCenicco == null) {
            this.domicilioCenicco = new DomiciliosCenicco();
        }
        if (this.documentoCenicco == null) {
            this.documentoCenicco = new DocumentosCenicco();
        }
        if (this.contactoCenicco == null) {
            this.contactoCenicco = new ContactoCenicco();
        }
        if (this.posicionLaboral == null) {
            this.posicionLaboral = new PosicionCenicco(this.manejadorCorreo);
        }
        if (this.cuentaBancaria == null) {
            this.cuentaBancaria = new CuentaBancariaCenicco(this.manejadorCorreo);
        }
        if (this.infonavitLaboral == null) {
            this.infonavitLaboral = new InfonavitCenicco(this.manejadorCorreo);
        }
        if (this.complementaria == null) {
            this.complementaria = new InformacionComplementariaCenicco();
        }
        if (this.pensionLaboral == null) {
            this.pensionLaboral = new PensionCenicco();
        }
        if (this.dependienteCenicco == null) {
            this.dependienteCenicco = new DependienteCenicco();
        }
        this.compania = ControladorSesiones.getInstance().getCompaniaSession().getNombreCorto();
        this.jornadaslaborales = ControladorWS.getInstance().getJornadasLaborales();
        this.turnos = new ArrayList<>();
    }

    @PostConstruct
    @Override
    public void init() {
        this.vista.setIdestatus(1);
        this.consultar(null);
    }

    @Override
    public void consultar(ActionEvent event) {
        this.vistas = ControladorWS.getInstance().getReporteGeneral(this.vista, this.fechaInicioAlta,
                this.fechaFinAlta, this.fechaInicioBaja, this.fechaFinBaja, null, null);
        this.filteredVistas = this.vistas;
    }

    @Override
    public void limpiar(ActionEvent event) {
        this.vista = new VistaEmpleados();
        this.fechaInicioAlta = null;
        this.fechaFinAlta = null;
        this.fechaInicioBaja = null;
        this.fechaFinBaja = null;
    }

    public void limpiarRelacionLaboral() {
        this.relacionLaboral = new RelacionLaboral();
        this.fechaNacimiento = null;
    }

    public void enviarCorreoMasivo() {
        int enviados = 0;
        int noEnviados = 0;
        int contador = 1;
        Map<String, String> mapamails = ControladorWS.getInstance().getMails();
        System.out.println("Envio de contraseñas a: " + this.vistas.size() + " Empleados");
        for (VistaEmpleados v : this.vistas) {
            RelacionLaboral rl = ControladorWS.getInstance().findRelacionLaboralById(v.getIdrelacionlaboral());
            String mail = mapamails.get((rl.getIdempleado().getIdempleado().toString()));
            if (mail != null) {
                if (!mail.equals("")) {
                    boolean isValidate = new ManejadorCorreo(rl, mail).construirCorreoPrimerPasswordKiosko();
                    if (isValidate) {
                        System.out.println("[" + contador + "] Contraseña enviada para: " + mail);
                        enviados++;
                    } else {
                        System.out.println("[" + contador + "] Fallo envio de contraseña para: " + mail);
                        noEnviados++;
                    }
                } else {
                    System.out.println("[" + contador + "] Correo vacio empleado: " + Util.getNombre(rl.getIdempleado()) + " [" + rl.getNumeroempleado() + "]");
                }

            } else {
                System.out.println("[" + contador + "] No existe correo para empleado: " + Util.getNombre(rl.getIdempleado()) + " [" + rl.getNumeroempleado() + "]");
                noEnviados++;
            }
            contador++;
        }
        System.out.println("Total enviados: " + enviados + " | Total No enviados: " + noEnviados);
    }

    public void activarKiosco() {
        RelacionLaboral rl = this.relacionLaboral;
        Map<String, String> mapamails = ControladorWS.getInstance().getMails();
        String mail = mapamails.get((rl.getIdempleado().getIdempleado().toString()));
        if (mail != null) {
            if (!mail.equals("")) {
                boolean isValidate = new ManejadorCorreo(rl, mail).construirCorreoPrimerPasswordKiosko();
                if (isValidate) {
                    System.out.println("Correo enviado exitosamente a: " + mail);
                } else {
                    System.out.println("No se mandó correo activacion kiosco");
                }
            }
        }
    }

    public void reestablecerContrasenaKiosco() {
        Map<String, String> mapamails = ControladorWS.getInstance().getMails();
        RelacionLaboral rl = this.relacionLaboral;
        String error = "";
        String mail = mapamails.get((rl.getIdempleado().getIdempleado().toString()));
        String contrasenaAux = Util.getpasswordRecuperacion(rl);
        boolean isValidate = false;
        if (mail != null) {
            if (contrasenaAux.equals("")) {
                error = "Error al asignar la contraseña al empleado";
            } else {
                isValidate = new ManejadorCorreo(rl, mail).construirCorreoRecuperacionPasswordKiosko(contrasenaAux);
            }
        } else {
            error = "El empleado no tiene correo electronico asignado";
        }
        if (isValidate) {
            System.out.println("Contraseña enviada correctamente a " + mail);
        }
        FacesMessage msg = isValidate ? Util.getFacesMessage(FacesMessage.SEVERITY_INFO, "Se reestableció la contraseña exitosamente")
                : Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, error);
//        
        this.generarMsg(msg, isValidate);
    }

    public void descargaDocumentos() {
        byte[] bytes = null;
        for (VistaEmpleados v : this.vistas) {
            RelacionLaboral rl = ControladorWS.getInstance().findRelacionLaboralById(v.getIdrelacionlaboral());
            List<Documento> docs = ControladorWS.getInstance().findDocumentoByIdEmpleado(rl.getIdempleado().getIdempleado());

            //Obtiene ruta para los archivos por empresa y genera carpeta por empleado
            String path =
                    ControladorWS.getInstance().getPathArchivosEmpleados()
                    + rl.getIdempleado().getExpediente() + "_" + rl.getIdempleado().getNombre()
                    + "_" + rl.getIdempleado().getApellidopaterno();
            File carpeta = new File(path);
            boolean crearcarpeta = carpeta.mkdir();
            if (crearcarpeta) {
                System.out.println("Carpeta creada con exito '" + path + "'");
            }
            for (Documento d : docs) {
                OutputStream outPdf;
                try {
                    if (d.getArchivo() != null) {
                        bytes = d.getArchivo();
                        outPdf = new FileOutputStream(path + "/" + d.getIdtipodocto().getNombre() + ".pdf");
                        outPdf.write(bytes);
                        outPdf.close();
                        d.setPatharchivo(path + "/" + d.getIdtipodocto().getNombre() + ".pdf");
                        ControladorWS.getInstance().editDocumento(d.getIdempleado(), d);
                    }
                } catch (Exception ex) {
                    Logger.getLogger(CatalogoEmpleadoCenicco.class.getName()).log(Level.SEVERE, null, ex);

                }

            }
        }
    }

    @Override
    public void create(ActionEvent event) {
        FacesMessage msg = this.validateCreate();
        if (msg.getSeverity().equals(FacesMessage.SEVERITY_INFO)) {
            String cadena = Util.getCadenasRelacionLaboralEmpleado(this.relacionLaboral, this.fechaNacimiento);
            this.manejadorCorreo.enviarCorreoNotificacion(Modulos.KARDEX_ALTA_EMPLEADO.getConcepto(), cadena);
        }
        this.generarMsg(msg, FacesMessage.SEVERITY_INFO.equals(msg.getSeverity()));
    }

    private FacesMessage validateCreate() {
        boolean isValidate = ControladorWS.getInstance().createRelacionLaboral(this.relacionLaboral.getIdempleado(), this.fechaNacimiento, true);
//        
        if (!isValidate) {
            return Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, Mensajes.ERROR_CREAR_EMPLEADO);
        }
//        
        return Util.getFacesMessage(FacesMessage.SEVERITY_INFO, Mensajes.EXITOSO_CREAR_EMPLEADO);
    }

    public void editSueldos(ActionEvent event) {
        boolean isValidate = this.validateSueldos();
        if (isValidate) {
            isValidate = ControladorWS.getInstance().editSueldos(this.relacionLaboral, this.salarioDiario, this.salarioDiarioIntegrado,
                    this.fechaInicioSD, this.fechaInicioSDI);
            if (isValidate) {
                String cadena = Util.getCadenasRelacionLaboralPosicionSueldo(this.relacionLaboral, this.fechaInicioSD, this.fechaInicioSDI);
                this.manejadorCorreo.enviarCorreoNotificacion(Modulos.KARDEX_SUELDOS.getConcepto(), cadena);
            }
//        
            FacesMessage msg = isValidate ? Util.getFacesMessage(FacesMessage.SEVERITY_INFO, Mensajes.EXITOSO_EDITAR_EMPLEADO)
                    : Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, Mensajes.ERROR_EDITAR_EMPLEADO);
//        
            this.generarMsg(msg, isValidate);
        }
    }

    private boolean validateCalculoSdi() {
//        
        this.relacionLaboral = ControladorWS.getInstance().findRelacionLaboralById(this.selectedVista.getIdrelacionlaboral());
//        
        /*if (this.relacionLaboral.getIdrelacionlaboralposicion().getIdrelacionlaboralposicion() == null) {
         FacesMessage msg = Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, "Favor de ingresar primero los módulos de: Relación Laboral y Posición Laboral");
         FacesContext.getCurrentInstance().addMessage(null, msg);
         return false;
         }*/
        if (this.salarioDiario == null) {
            FacesMessage msg = Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, "Favor de ingresar primero el SD");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return false;
        }
        if (this.fechaInicioSDI == null) {
            FacesMessage msg = Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, "Favor de ingresar primero la Fecha Inicio SDI");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return false;
        }
//        
        return true;
    }

    public void calcularSdi(ActionEvent event) {
        boolean isValidate = this.validateCalculoSdi();
//        
        if (isValidate) {
            this.salarioDiarioIntegrado =
                    ControladorWS.getInstance().calcularVariabilidadBimestralXempleado(this.relacionLaboral, this.salarioDiario, this.fechaInicioSDI);
        }
//        
    }

    private boolean validateSueldos() {
        boolean isValidate = true;
//        
        if (this.fechaInicioSD == null) {
            FacesMessage msg = Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, Mensajes.CAMPO_VACIO_FECHA_INICIO);
            FacesContext.getCurrentInstance().addMessage(null, msg);
            isValidate = false;
        }
        if (this.salarioDiario == null) {
            FacesMessage msg = Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, "Favor de Ingresar Sueldo Diario");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            isValidate = false;
        }
        if (this.salarioDiarioIntegrado == null) {
            FacesMessage msg = Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, "Favor de Ingresar Sueldo Diario Integrado");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            isValidate = false;
        }
        if (this.fechaInicioSDI == null) {
            FacesMessage msg = Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, Mensajes.CAMPO_VACIO_FECHA_INICIO);
            FacesContext.getCurrentInstance().addMessage(null, msg);
            isValidate = false;
        }
        /* Validacion de modificación de SD
         if (this.relacionLaboral.getSalarioDiario() != null
         && (CeniccoUtil.redondear(this.relacionLaboral.getSalarioDiario()) == CeniccoUtil.redondear(this.salarioDiario))) {
         FacesMessage msg = Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, "No hay modificación de SD");
         FacesContext.getCurrentInstance().addMessage(null, msg);
         isValidate = false;
         }
         * */
        if (!isValidate) {
            RequestContext context = RequestContext.getCurrentInstance();
            context.addCallbackParam("isValidate", isValidate);
        }
//        
        return isValidate;
    }

    @Override
    public void edit(ActionEvent event) {
        boolean isValidate = this.validateRelacionLaboral();
//        
        if (isValidate) {
            isValidate = ControladorWS.getInstance().editRelacionLaboral(this.relacionLaboral, this.fechaInicioAlta, this.fechaAntiguedad,
                    this.fechaAntiguedadDos, this.fechaAntiguedadTres, this.fechaVencimiento, this.fechaInicioBaja);
//        
            ControladorWS.getInstance().editEmpleado(this.relacionLaboral.getIdempleado());

            if (isValidate) {
                String cadena = Util.getCadenasRelacionLaboralRelacionLaboral(this.relacionLaboral);
                this.manejadorCorreo.enviarCorreoNotificacion(Modulos.KARDEX_RELACION_LABORAL.getConcepto(), cadena);
            }
//        
            FacesMessage msg = isValidate ? Util.getFacesMessage(FacesMessage.SEVERITY_INFO, Mensajes.EXITOSO_EDITAR_EMPLEADO)
                    : Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, Mensajes.ERROR_EDITAR_EMPLEADO);
//        
            this.generarMsg(msg, isValidate);
        }
    }

    private boolean validateRelacionLaboral() {
        boolean isValidate = true;
//        
        if (this.relacionLaboral.getIdcompania().getIdcompania() == null) {
            FacesMessage msg = Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, Mensajes.CAMPO_VACIO_COMPANIA);
            FacesContext.getCurrentInstance().addMessage(null, msg);
            isValidate = false;
        }
        if (this.relacionLaboral.getIdregistropatronal().getIdregistropatronal() == null) {
            FacesMessage msg = Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, Mensajes.CAMPO_VACIO_REGISTRO_PATRONAL);
            FacesContext.getCurrentInstance().addMessage(null, msg);
            isValidate = false;
        }
        if (this.relacionLaboral.getIdtiporellab().getIdtiporelacionlaboral() == null) {
            FacesMessage msg = Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, Mensajes.CAMPO_VACIO_TIPO_RELACION_LABORAL);
            FacesContext.getCurrentInstance().addMessage(null, msg);
            isValidate = false;
        }
        if (this.relacionLaboral.getIdgrupopago().getIdgrupopago() == null) {
            FacesMessage msg = Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, Mensajes.CAMPO_VACIO_GRUPO_PAGO);
            FacesContext.getCurrentInstance().addMessage(null, msg);
            isValidate = false;
        }
        if (this.relacionLaboral.getIdTipoSalarioIdse().getIdTipoSalarioIdse() == null) {
            FacesMessage msg = Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, Mensajes.CAMPO_VACIO_TIPO_SALARIO);
            FacesContext.getCurrentInstance().addMessage(null, msg);
            isValidate = false;
        }
        if (this.relacionLaboral.getEstatus() == null || this.relacionLaboral.getEstatus().equals("")) {
            FacesMessage msg = Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, Mensajes.CAMPO_VACIO_ESTATUS);
            FacesContext.getCurrentInstance().addMessage(null, msg);
            isValidate = false;
        }
        if (this.fechaInicioAlta == null) {
            FacesMessage msg = Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, Mensajes.CAMPO_VACIO_FECHA_INGRESO);
            FacesContext.getCurrentInstance().addMessage(null, msg);
            isValidate = false;
        }
        if (this.relacionLaboral.getNumeroempleado() == null || this.relacionLaboral.getNumeroempleado().equals("")) {
            FacesMessage msg = Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, Mensajes.CAMPO_VACIO_NUMERO_EMPLEADO);
            FacesContext.getCurrentInstance().addMessage(null, msg);
            isValidate = false;
        }
        if (this.relacionLaboral.getIdsistemaantiguedad().getIdsistemaantiguedad() == null) {
            FacesMessage msg = Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, Mensajes.CAMPO_VACIO_SISTEMA_ANTIGUEDAD);
            FacesContext.getCurrentInstance().addMessage(null, msg);
            isValidate = false;
        }
        if (this.fechaAntiguedad == null) {
            FacesMessage msg = Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, Mensajes.CAMPO_VACIO_FECHA_ANTIGUEDAD_UNO);
            FacesContext.getCurrentInstance().addMessage(null, msg);
            isValidate = false;
        }

        if (this.relacionLaboral.getNumeroempleado() != null && !this.relacionLaboral.getNumeroempleado().equals("")) {
            RelacionLaboral aux = ControladorWS.getInstance().findRelacionLaboralByNumeroEmpleado(this.relacionLaboral.getNumeroempleado());
//            
            if (aux != null && (aux.getIdrellab().intValue() != this.relacionLaboral.getIdrellab().intValue())) {
                FacesMessage msg = Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, "El Número de Empleado ya se encuentra asignado a otro empleado");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                isValidate = false;
            }
        }


        if (!isValidate) {
            RequestContext context = RequestContext.getCurrentInstance();
            context.addCallbackParam("isValidate", isValidate);
        }
        return isValidate;
    }

    public void editVales(ActionEvent event) {
        boolean isValidate = false;
        EmpleadoVale ctavales = ControladorWS.getInstance().findCtaVales(this.cuentavale);
        System.out.println("cuentavales : " + ctavales);
        if (ctavales != null) {
            FacesMessage msg = isValidate ? Util.getFacesMessage(FacesMessage.SEVERITY_INFO, "Se creo exitosamente")
                    : Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, "La cuenta de vales ya existe");
            this.generarMsg(msg, isValidate);
            return;
        }
        EmpleadoVale empleadoVale = ControladorWS.getInstance().getEmpleadosValesByIdRelLabAndIdCompaniaProveedorVale(this.relacionLaboral.getIdrellab(), this.idcompaniaproveedorvale);
        CompaniaProveedorVale proveedorVale = null;
        for (CompaniaProveedorVale p : this.proveedoresVales) {
            if (p.getIdcompaniaproveedorvale().equals(this.idcompaniaproveedorvale)) {
                proveedorVale = p;
                break;
            }
        }
        System.out.println("ID: " + proveedorVale.getIdcompaniaproveedorvale()
                + " Compania proveedor vale: " + proveedorVale.getCompania().getNombre()
                + " Tipo Vale: " + proveedorVale.getTipovale().getNombre()
                + " Proveedor Vale: " + proveedorVale.getProveedorvale().getNombre());
        if (empleadoVale == null) {
            List<EmpleadoVale> vales = new ArrayList<>();

            EmpleadoVale vale = new EmpleadoVale();
            vale.setIdrellab(relacionLaboral.getIdrellab());
            vale.setCompaniaProveedorVale(proveedorVale);
            vale.setCuentavale(this.cuentavale);
            vale.setNumerotarjetavale(this.numerotarjetavale);

            vales.add(vale);
            isValidate = ControladorWS.getInstance().editEmpleadosVales(vales);
        } else {
            List<EmpleadoVale> vales = new ArrayList<>();

            empleadoVale.setCuentavale(this.cuentavale);
            empleadoVale.setNumerotarjetavale(this.numerotarjetavale);

            vales.add(empleadoVale);
            isValidate = ControladorWS.getInstance().editEmpleadosVales(vales);
        }

        this.consultarEmpleadosValesByIdRelLab();

        this.idcompaniaproveedorvale = null;
        this.cuentavale = null;
        this.numerotarjetavale = null;

        FacesMessage msg = isValidate ? Util.getFacesMessage(FacesMessage.SEVERITY_INFO, "Los Vales de Despensa fueron modificados exitosamente")
                : Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, "Error al modificar los Vales de Despensa");
        this.generarMsg(msg, isValidate);

        /*isValidate = this.validateCamposDespensa();
         if (isValidate) {
         isValidate = ControladorWS.getInstance().editValesDespensa(this.relacionLaboral);
         FacesMessage msg = isValidate ? Util.getFacesMessage(FacesMessage.SEVERITY_INFO, "Los Vales de Despensa fueron modificados exitosamente")
         : Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, "Error al modificar los Vales de Despensa");
         this.generarMsg(msg, isValidate);
         }*/
    }

    public void deleteEmpleadoVale(EmpleadoVale vale) {
        List<EmpleadoVale> vales = new ArrayList<>();
        vales.add(vale);
        System.out.println("Eliminando Empleado Vale... " + vale.getNumerotarjetavale());
        boolean isValidate = ControladorWS.getInstance().deleteEmpleadosVales(vales);
        if (isValidate) {
            this.consultarEmpleadosValesByIdRelLab();
        }
    }

    public void editEmpleadoVale(EmpleadoVale vale) {
        CompaniaProveedorVale proveedorVale = null;
        for (CompaniaProveedorVale p : this.proveedoresVales) {
            if (p.getIdcompaniaproveedorvale().equals(vale.getCompaniaProveedorVale().getIdcompaniaproveedorvale())) {
                proveedorVale = p;
                break;
            }
        }

        this.idcompaniaproveedorvale = proveedorVale != null ? proveedorVale.getIdcompaniaproveedorvale() : null;
        this.cuentavale = vale.getCuentavale();
        this.numerotarjetavale = vale.getNumerotarjetavale();

        RequestContext context = RequestContext.getCurrentInstance();
        context.update("fomrEditarValeEmpleado");
        context.execute("PF('EditarValeEmpleado').show();");
    }

    public void closeEditarEmpleadoVale() {
        this.idcompaniaproveedorvale = null;
        this.cuentavale = null;
        this.numerotarjetavale = null;

        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('EditarValeEmpleado').hide();");
    }

    public void editEmpleado(ActionEvent event) {
        boolean isValidate = this.validateCampos();
//        
        if (isValidate) {
            FacesMessage msg = this.validateEdit();
            if (FacesMessage.SEVERITY_INFO.equals(msg.getSeverity())) {
                this.listenerSelected();
                String cadena = Util.getCadenaRelacionLaboralExpediente(this.relacionLaboral);
                manejadorCorreo.enviarCorreoNotificacion(Modulos.KARDEX_GENERALES.getConcepto(), cadena);
            }
            this.generarMsg(msg, FacesMessage.SEVERITY_INFO.equals(msg.getSeverity()));
        }
    }

    private boolean validateCampos() {
        Empleado emp = this.relacionLaboral.getIdempleado();
        boolean isValidate = true;
//        
        if (emp.getNombre() == null || emp.getNombre().equals("")) {
            FacesMessage msg = Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, Mensajes.CAMPO_VACIO_NOMBRE);
            FacesContext.getCurrentInstance().addMessage(null, msg);
            isValidate = false;
        }
        if (emp.getApellidopaterno() == null || emp.getApellidopaterno().equals("")) {
            FacesMessage msg = Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, Mensajes.CAMPO_VACIO_APELLIDO_PATERNO);
            FacesContext.getCurrentInstance().addMessage(null, msg);
            isValidate = false;
        }
        if (emp.getIdestadonacimiento() == null) {
            FacesMessage msg = Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, Mensajes.CAMPO_VACIO_ESTADO);
            FacesContext.getCurrentInstance().addMessage(null, msg);
            isValidate = false;
        }
        if (this.fechaNacimiento == null) {
            FacesMessage msg = Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, Mensajes.CAMPO_VACIO_FECHA_NACIMIENTO);
            FacesContext.getCurrentInstance().addMessage(null, msg);
            isValidate = false;
        }
        if (emp.getSexo() == null || emp.getSexo().equals("")) {
            FacesMessage msg = Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, Mensajes.CAMPO_VACIO_SEXO);
            FacesContext.getCurrentInstance().addMessage(null, msg);
            isValidate = false;
        }
        if (emp.getAfiliacion() == null || emp.getAfiliacion().equals("")) {
            FacesMessage msg = Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, Mensajes.CAMPO_VACIO_AFILIACION);
            FacesContext.getCurrentInstance().addMessage(null, msg);
            isValidate = false;
        }
        if (emp.getRfc() == null || emp.getRfc().equals("")) {
            FacesMessage msg = Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, Mensajes.CAMPO_VACIO_RFC);
            FacesContext.getCurrentInstance().addMessage(null, msg);
            isValidate = false;
        }
        if (emp.getCurp() == null || emp.getCurp().equals("")) {
            FacesMessage msg = Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, Mensajes.CAMPO_VACIO_CURP);
            FacesContext.getCurrentInstance().addMessage(null, msg);
            isValidate = false;
        }
        if (!isValidate) {
            RequestContext context = RequestContext.getCurrentInstance();
            context.addCallbackParam("isValidate", isValidate);
        }
        return isValidate;
    }

    private boolean validateCamposDespensa() {
        boolean isValidate = true;
//        
        if (this.relacionLaboral.getCtavales() == null || this.relacionLaboral.getCtavales().equals("")) {
            FacesMessage msg = Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, "Favor de Ingresar Cuenta de Vales Despensa");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            isValidate = false;
        }
        if (this.relacionLaboral.getTarjetavales() == null || this.relacionLaboral.getTarjetavales().equals("")) {
            FacesMessage msg = Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, "Favor de Ingresar Tarjeta de Vales Despensa");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            isValidate = false;
        }
        if (!isValidate) {
            RequestContext context = RequestContext.getCurrentInstance();
            context.addCallbackParam("isValidate", isValidate);
        }
        return isValidate;
    }

    private FacesMessage validateEdit() {
        Empleado emp = ControladorWS.getInstance().findEmpleadoByRfc(this.relacionLaboral.getIdempleado());
//        
        if (emp != null && !emp.getIdempleado().toString().equals(this.relacionLaboral.getIdempleado().getIdempleado().toString())) {
            return Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, Mensajes.ERROR_RFC_DUPLICADO);
        }
//        
        boolean isValidate = ControladorWS.getInstance().editEmpleado(this.relacionLaboral.getIdempleado(), this.fechaNacimiento);
//        
        if (!isValidate) {
            return Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, Mensajes.ERROR_EDITAR_EMPLEADO);
        }
//        
        return Util.getFacesMessage(FacesMessage.SEVERITY_INFO, Mensajes.EXITOSO_EDITAR_EMPLEADO);
    }

    @Override
    public void delete(VistaEmpleados obj) {
    }

    @Override
    public void listenerSelected() {
        this.relacionLaboral = ControladorWS.getInstance().findRelacionLaboralById(this.selectedVista.getIdrelacionlaboral());
//        
        this.fechaNacimiento = CeniccoUtil.getXmlCalendarToDate(this.relacionLaboral.getIdempleado().getFechanacimiento());
        this.fechaInicioAlta = CeniccoUtil.getXmlCalendarToDate(this.relacionLaboral.getFechaingreso());
        this.fechaInicioBaja = CeniccoUtil.getXmlCalendarToDate(this.relacionLaboral.getFechabaja());
        this.fechaAntiguedad = CeniccoUtil.getXmlCalendarToDate(this.relacionLaboral.getFechaantiguedad1());
        this.fechaAntiguedadDos = CeniccoUtil.getXmlCalendarToDate(this.relacionLaboral.getFechaantiguedad2());
        this.fechaAntiguedadTres = CeniccoUtil.getXmlCalendarToDate(this.relacionLaboral.getFechaantiguedad3());
        this.fechaVencimiento = CeniccoUtil.getXmlCalendarToDate(this.relacionLaboral.getFechaeventocontrato());
//        
        this.salarioDiario = this.relacionLaboral.getSalarioDiario();
        this.salarioDiarioIntegrado = this.relacionLaboral.getSalarioDiarioIntegrado();
//        
        if (this.fechaInicioBaja != null && this.relacionLaboral.getEstatus().equals("1")) {
            this.estatusEmpleado = "(" + CeniccoUtil.convertDateToString(this.fechaInicioBaja) + ") REINGRESO";
            this.colorEstatus = "#FF8000";
        }
        if (this.fechaInicioBaja != null && this.relacionLaboral.getEstatus().equals("0")) {
            this.estatusEmpleado = "(" + CeniccoUtil.convertDateToString(this.fechaInicioBaja) + ") INACTIVO";
            this.colorEstatus = "red";
        }
        if (this.fechaInicioBaja == null) {
            this.estatusEmpleado = "";
            this.colorEstatus = "black";
        }
//        
        this.selectedEstructura = null;
        this.datos = new ArrayList<>();
//        
        this.fechaInicioSD = CeniccoUtil.getXmlCalendarToDate(this.relacionLaboral.getFechaSalarioDiario());
        this.fechaInicioSDI = CeniccoUtil.getXmlCalendarToDate(this.relacionLaboral.getFechaSalarioDiarioIntegrado());
//        
        this.consultarHistoricoSueldos();
//        
        this.domicilioCenicco.setIdempleado(this.relacionLaboral.getIdempleado());
        this.domicilioCenicco.limpiar(null);
//        
        this.documentoCenicco.setEmpleado(this.relacionLaboral.getIdempleado());
        this.documentoCenicco.limpiar(null);
//        
        this.contactoCenicco.setEmpleado(this.relacionLaboral.getIdempleado());
        this.contactoCenicco.limpiar(null);
//        
        this.posicionLaboral.setRellab(this.relacionLaboral);
        this.posicionLaboral.limpiar(null);
//        
        this.cuentaBancaria.setRellab(this.relacionLaboral);
        this.cuentaBancaria.limpiar(null);
//        
        this.infonavitLaboral.setRellab(this.relacionLaboral);
        this.infonavitLaboral.limpiar(null);
//        
        this.complementaria.setEmpleado(this.relacionLaboral.getIdempleado());
        this.complementaria.limpiar(null);
//        
        this.pensionLaboral.setRellab(this.relacionLaboral);
        this.pensionLaboral.limpiar(null);
//        
        this.dependienteCenicco.setEmpleado(this.relacionLaboral.getIdempleado());
        this.dependienteCenicco.limpiar(null);

        this.proveedoresVales = ControladorWS.getInstance().getCompaniasProveedoresVales(this.relacionLaboral.getIdgrupopago().getIdcompania().getIdcompania());
        this.consultarEmpleadosValesByIdRelLab();
        this.consultarJornadasTurnosLaborales();
        this.selectedSubdepartamento = posicionLaboral.getPosicion().getSubDepartamento();
        if (posicionLaboral.getPosicion().getSubDepartamento() != null) {
            this.selectedSubdepartamento = posicionLaboral.getPosicion().getSubDepartamento();
        } else {
            selectedSubdepartamento = new SubDepartamento();
            selectedSubdepartamento.setIdSubDepartamento(0);
            selectedSubdepartamento.setSubDepartamento("");
            selectedSubdepartamento.setNombre("");
        }
        subDepartamentos = ControladorWS.getInstance().findSubDepartamentoByIdDepartamento(this.posicionLaboral.getPosicion().getIddepartamento().getIddepartamento());
    }

    public void consultarEmpleadosValesByIdRelLab() {
        this.empleadoVales = ControladorWS.getInstance().getEmpleadosValesByIdRelLab(this.relacionLaboral.getIdrellab());
    }

    public void consultarJornadasTurnosLaborales() {
        this.selectIdJornadaLaboral = null;
        this.turnos = new ArrayList<>();
        if (this.relacionLaboral.getIdrelacionlaboralposicion() != null && this.relacionLaboral.getIdrelacionlaboralposicion().getTurnoLaboral() != null) {
            JornadaLaboral jornadaLaboral = this.relacionLaboral.getIdrelacionlaboralposicion().getTurnoLaboral().getJornadaLaboral();
            this.selectIdJornadaLaboral = jornadaLaboral != null ? jornadaLaboral.getIdJornadaLaboral() : null;
            turnos = ControladorWS.getInstance().findTurnoLaboralByJornadaLaboral(this.selectIdJornadaLaboral);
            this.selectIdTurnoLaboral = this.relacionLaboral.getIdrelacionlaboralposicion().getTurnoLaboral().getIdTunoLaboral();
        }
    }

    public void consultarHistoricoSueldos() {
        this.relacionLaboral = ControladorWS.getInstance().findRelacionLaboralById(this.selectedVista.getIdrelacionlaboral());
        this.historicosueldos = ControladorWS.getInstance().findHistoricoSueldoByIdRellab(this.relacionLaboral.getIdrellab());
    }

    public void actionPestana(ActionEvent event) {
        String valor = (String) event.getComponent().getAttributes().get("value");
        this.render.actionPestana(valor);
    }

    public void calcularFechaVencimiento() {
        if (this.relacionLaboral.getDuracion() != null) {
            this.fechaVencimiento = Util.calcularFechaVencimiento(this.fechaInicioAlta, this.relacionLaboral.getDuracion());
        }
    }

    public void listenerEstructura() {
        if (this.selectedEstructura == null) {
            this.datos = new ArrayList<>();
        } else {
            this.datos = ControladorWS.getInstance().findEstructuraDatosByIdRellabAndIdEstructura(this.relacionLaboral.getIdrellab(),
                    this.selectedEstructura);
        }
    }

    public void editarEstructura(ActionEvent event) {
        boolean isValidate = ControladorWS.getInstance().editEstructuraByEmpleado(this.datos);
//        
        if (isValidate) {
            String cadena = Util.getCadenasRelacionLaboralVariables(this.relacionLaboral, this.datos);
            this.manejadorCorreo.enviarCorreoNotificacion(Modulos.KARDEX_VARIABLES.getConcepto(), cadena);
        }
//        
        FacesMessage msg = isValidate ? Util.getFacesMessage(FacesMessage.SEVERITY_INFO, Mensajes.EXITOSO_EDITAR_ESTRUCTURA_DATOS)
                : Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, Mensajes.ERROR_EDITAR_ESTRUCTURA_DATOS);
        this.generarMsg(msg, isValidate);
    }

    public void generarCurpRfc(ActionEvent event) {
        Estado estado = ControladorWS.getInstance().findEstadoById(this.relacionLaboral.getIdempleado().getIdestadonacimiento());
        String fNacimientoStr = CeniccoUtil.convertDateToString(this.fechaNacimiento);
//
        String rfc = CeniccoUtil.generarRfc(this.relacionLaboral.getIdempleado().getNombre(),
                this.relacionLaboral.getIdempleado().getApellidopaterno(),
                this.relacionLaboral.getIdempleado().getApellidomaterno(),
                fNacimientoStr);

        String curp = CeniccoUtil.generarCurp(this.relacionLaboral.getIdempleado().getNombre(),
                this.relacionLaboral.getIdempleado().getApellidopaterno(),
                this.relacionLaboral.getIdempleado().getApellidomaterno(),
                this.relacionLaboral.getIdempleado().getSexo(),
                estado.getEstado(), rfc);
//
        this.relacionLaboral.getIdempleado().setRfc(rfc);
        this.relacionLaboral.getIdempleado().setCurp(curp);
    }

    public void fileUploadListenerFoto(FileUploadEvent event) {
        this.foto = event.getFile();
        FacesMessage msg;
//        
        try {
            this.relacionLaboral.getIdempleado().setPathfoto(IOUtils.toByteArray(this.foto.getInputstream()));
            boolean isvalidate = ControladorWS.getInstance().editEmpleado(this.relacionLaboral.getIdempleado());
            msg = isvalidate ? Util.getFacesMessage(FacesMessage.SEVERITY_INFO, Mensajes.EXITOSO_EDITAR_EMPLEADO)
                    : Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, Mensajes.ERROR_EDITAR_EMPLEADO);
        } catch (Exception e) {
            msg = Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, Mensajes.ERROR_EDITAR_EMPLEADO);
        }
//        
        this.generarMsg(msg, msg.getSeverity().equals(FacesMessage.SEVERITY_INFO));
    }

    public void descargarCartaPatronalImss() {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_CARTA_PATRONAL_IMSS);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, "CARTA_PATRONAL_IMSS_" + this.relacionLaboral.getNumeroempleado());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_PDF);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.relacionLaboral);
        context.addCallbackParam("ruta", MyPaths.urlServletReporte());
    }

    public void descargarCartaDeReuncia() {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_CARTA_DE_RENUNCIA);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, "CARTA_RENUNCIA_" + this.relacionLaboral.getNumeroempleado());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_PDF);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.relacionLaboral);
        context.addCallbackParam("ruta", MyPaths.urlServletReporte());
    }

    public void descargarCartaDeReunciaAlpha() {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_CARTA_DE_RENUNCIA_ALPHA);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, "CARTA_RENUNCIA_" + this.relacionLaboral.getNumeroempleado());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_PDF);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.relacionLaboral);
        context.addCallbackParam("ruta", MyPaths.urlServletReporte());
    }

    public void descargarKitInicioAlpha() {
        TabuladorSistemaAntiguedad tabulador = ControladorWS.getInstance().getTabuladorSistemaAntiguedad(this.relacionLaboral);
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_KIT_INICIACION_ALPHA);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, "KIT_INICIO" + this.relacionLaboral.getNumeroempleado());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_PDF);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.relacionLaboral);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO_SALDO_VACACIONES, tabulador);
        if (!this.domicilioCenicco.getDomicilios().isEmpty()) {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.DOMICILIO, this.domicilioCenicco.getDomicilios().get(0));
        }
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.SALARIO_MENSUAL, this.selectedVista.getSm());
        context.addCallbackParam("ruta", MyPaths.urlServletReporte());
    }

    public void descargarKitInicioAlphaTeletrabajo() {
        TabuladorSistemaAntiguedad tabulador = ControladorWS.getInstance().getTabuladorSistemaAntiguedad(this.relacionLaboral);
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_KIT_INICIACION_ALPHA_TELETRABAJO);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, "KIT_INICIO_TELETRABAJO_" + this.relacionLaboral.getNumeroempleado());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_PDF);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.relacionLaboral);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO_SALDO_VACACIONES, tabulador);
        if (!this.domicilioCenicco.getDomicilios().isEmpty()) {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.DOMICILIO, this.domicilioCenicco.getDomicilios().get(0));
        }
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.SALARIO_MENSUAL, this.selectedVista.getSm());
        context.addCallbackParam("ruta", MyPaths.urlServletReporte());
    }

    public void decargarCartaEvaluacionAlpha() {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_CARTA_EVALUACION_PRUEBA_ALPHA);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, "CARTA_EVALUACION_" + this.relacionLaboral.getNumeroempleado());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_PDF);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.relacionLaboral);
        context.addCallbackParam("ruta", MyPaths.urlServletReporte());
    }

    public void descargarCartaDeReunciaWhiteHat() {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_CARTA_DE_RENUNCIA_WHITEHAT);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, "CARTA_RENUNCIA_" + this.relacionLaboral.getNumeroempleado());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_PDF);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.relacionLaboral);
        if (!this.domicilioCenicco.getDomicilios().isEmpty()) {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.DOMICILIO, this.domicilioCenicco.getDomicilios().get(0));
        } else {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.DOMICILIO, null);
        }
        context.addCallbackParam("ruta", MyPaths.urlServletReporte());
    }

    public void descargarCartaDeEmbajada() {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_CARTA_DE_EMBAJADA);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, "CARTA_EMBAJADA_" + this.relacionLaboral.getNumeroempleado());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_PDF);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.relacionLaboral);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.SALARIO_MENSUAL, this.selectedVista.getSm());
        context.addCallbackParam("ruta", MyPaths.urlServletReporte());
    }

    public void descargarCartaDeGuarderia() {
        TabuladorSistemaAntiguedad tabulador = ControladorWS.getInstance().getTabuladorSistemaAntiguedad(this.relacionLaboral);
        Map<String, String> mapanum = ControladorWS.getInstance().getNumeroContacto();
        String num = "NA";
        if (mapanum.get(this.relacionLaboral.getIdempleado().getIdempleado().toString()) != null) {
            num = mapanum.get(this.relacionLaboral.getIdempleado().getIdempleado().toString());
        }
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_CARTA_DE_GUARDERIA);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, "CARTA_GUARDERIA_" + this.relacionLaboral.getNumeroempleado());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_PDF);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.relacionLaboral);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS_AUX, tabulador);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TELEFONO, num);
        if (!this.domicilioCenicco.getDomicilios().isEmpty()) {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.DOMICILIO, this.domicilioCenicco.getDomicilios().get(0));
        } else {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.DOMICILIO, null);
        }
        context.addCallbackParam("ruta", MyPaths.urlServletReporte());
    }

    public void descargarCartaFonacot(String responsable) {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_CARTA_FONACOT);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, "CARTA_FONACOT_" + this.relacionLaboral.getNumeroempleado());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_PDF);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.relacionLaboral);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("responsable", responsable);
        context.addCallbackParam("ruta", MyPaths.urlServletReporte());
    }

    public void descargarCartaNoAjuste() {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_CARTA_NO_AJUSTE);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, "CARTA_NO_AJUSTE_" + this.relacionLaboral.getNumeroempleado());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_PDF);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.relacionLaboral);
        context.addCallbackParam("ruta", MyPaths.urlServletReporte());
    }

    public void descargarCartaLaboral(Boolean sueldo) {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_CARTA_LABORAL);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, "CARTA_LABORAL_" + this.relacionLaboral.getNumeroempleado());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_PDF);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.relacionLaboral);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.SALARIO_MENSUAL, this.selectedVista.getSm());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS_AUX, sueldo);
        context.addCallbackParam("ruta", MyPaths.urlServletReporte());
    }

    public void descargarTiempoDeterminado() {
        if (this.relacionLaboral.getDuracion() == null) {
            FacesMessage msg = Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, "Error, no se ha determinado el tiempo del contrato");
            this.generarMsg(msg, true);
        } else {
            RequestContext context = RequestContext.getCurrentInstance();
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_CARTA_DE_TIEMPO_DETERMINADO);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, "CONTRATO_TIEMPO_DETERMINADO_" + this.relacionLaboral.getNumeroempleado());
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_PDF);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.relacionLaboral);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.SALARIO_MENSUAL, this.selectedVista.getSm());
            if (!this.domicilioCenicco.getDomicilios().isEmpty()) {
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.DOMICILIO, this.domicilioCenicco.getDomicilios().get(0));
            } else {
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.DOMICILIO, null);
            }

            context.addCallbackParam("ruta", MyPaths.urlServletReporte());
        }
    }

    public void descargarTiempoDeterminadoAbastoHotel() {
        if (this.relacionLaboral.getDuracion() == null) {
            FacesMessage msg = Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, "Error, no se ha determinado el tiempo del contrato");
            this.generarMsg(msg, true);
        } else {
            RequestContext context = RequestContext.getCurrentInstance();
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_CARTA_DE_TIEMPO_DETERMINADO_ABASTOHOTEL);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, "CONTRATO_TIEMPO_DETERMINADO_" + this.relacionLaboral.getNumeroempleado());
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_PDF);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.relacionLaboral);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.SALARIO_MENSUAL, this.selectedVista.getSm());
            if (!this.domicilioCenicco.getDomicilios().isEmpty()) {
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.DOMICILIO, this.domicilioCenicco.getDomicilios().get(0));
            } else {
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.DOMICILIO, null);
            }

            context.addCallbackParam("ruta", MyPaths.urlServletReporte());
        }
    }

    public void descargarTiempoInDeterminado() {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_CARTA_DE_TIEMPO_INDETERMINADO);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, "CONTRATO_TIEMPO_INDETERMINADO_" + this.relacionLaboral.getNumeroempleado());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_PDF);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.relacionLaboral);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.SALARIO_MENSUAL, this.selectedVista.getSm());
        if (!this.domicilioCenicco.getDomicilios().isEmpty()) {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.DOMICILIO, this.domicilioCenicco.getDomicilios().get(0));
        } else {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.DOMICILIO, null);
        }
        context.addCallbackParam("ruta", MyPaths.urlServletReporte());
    }

    public void descargarTiempoDeterminadoCovid() {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_CARTA_DE_TIEMPO_DETERMINADO_COVID);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, "CONTRATO_TIEMPO_DETERMINADO_COVID_" + this.relacionLaboral.getNumeroempleado());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_PDF);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.relacionLaboral);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.SALARIO_MENSUAL, this.selectedVista.getSm());
        if (!this.domicilioCenicco.getDomicilios().isEmpty()) {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.DOMICILIO, this.domicilioCenicco.getDomicilios().get(0));
        } else {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.DOMICILIO, null);
        }
        context.addCallbackParam("ruta", MyPaths.urlServletReporte());
    }

    public void descargarTiempoInDeterminadoNutrition() {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_CARTA_DE_TIEMPO_INDETERMINADO_NUTRITION);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, "CONTRATO_TIEMPO_INDETERMINADO_" + this.relacionLaboral.getNumeroempleado());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_PDF);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.relacionLaboral);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.SALARIO_MENSUAL, this.selectedVista.getSm());
        if (!this.domicilioCenicco.getDomicilios().isEmpty()) {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.DOMICILIO, this.domicilioCenicco.getDomicilios().get(0));
        } else {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.DOMICILIO, null);
        }
        context.addCallbackParam("ruta", MyPaths.urlServletReporte());
    }

    public void descargarTiempoInDeterminadoEnerfenix() {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_CARTA_DE_TIEMPO_INDETERMINADO_ENERFENIX);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, "CONTRATO_INDIVIDUAL_" + this.relacionLaboral.getNumeroempleado());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_PDF);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.relacionLaboral);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.SALARIO_MENSUAL, this.selectedVista.getSm());
        if (!this.domicilioCenicco.getDomicilios().isEmpty()) {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.DOMICILIO, this.domicilioCenicco.getDomicilios().get(0));
        } else {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.DOMICILIO, null);
        }
        context.addCallbackParam("ruta", MyPaths.urlServletReporte());
    }

    public void descargarTiempoDeterminadoNutrition() {
        if (this.relacionLaboral.getDuracion() == null) {
            FacesMessage msg = Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, "Error, no se ha determinado el tiempo del contrato");
            this.generarMsg(msg, true);
        } else {
            RequestContext context = RequestContext.getCurrentInstance();
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_CARTA_DE_TIEMPO_DETERMINADO_NUTRITION);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, "CONTRATO_TIEMPO_DETERMINADO_" + this.relacionLaboral.getNumeroempleado());
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_PDF);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.relacionLaboral);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.SALARIO_MENSUAL, this.selectedVista.getSm());
            if (!this.domicilioCenicco.getDomicilios().isEmpty()) {
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.DOMICILIO, this.domicilioCenicco.getDomicilios().get(0));
            } else {
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.DOMICILIO, null);
            }
            context.addCallbackParam("ruta", MyPaths.urlServletReporte());
        }
    }

    public void descargarTiempoDeterminadoArtsana(String tipoContrato) {
        if (this.relacionLaboral.getDuracion() == null) {
            FacesMessage msg = Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, "Error, no se ha determinado el tiempo del contrato");
            this.generarMsg(msg, true);
        } else {
            RequestContext context = RequestContext.getCurrentInstance();
            String modulo = "";
            switch (tipoContrato) {
                case "ADMINISTRATIVO":
                    modulo = ParametrosReportes.MODULO_CARTA_DE_TIEMPO_DETERMINADO_ADMINISTRATIVO_ARTSANA;
                    break;
                case "BECARIO":
                    modulo = ParametrosReportes.MODULO_CARTA_DE_TIEMPO_DETERMINADO_BECARIO_ARTSANA;
                    break;
                case "VENTAS":
                    modulo = ParametrosReportes.MODULO_CARTA_DE_TIEMPO_DETERMINADO_VENTAS_ARTSANA;
                    break;
            }
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, modulo);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, "CONTRATO_TIEMPO_DETERMINADO_" + this.relacionLaboral.getNumeroempleado());
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_PDF);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.relacionLaboral);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.SALARIO_MENSUAL, this.selectedVista.getSm());
            if (!this.domicilioCenicco.getDomicilios().isEmpty()) {
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.DOMICILIO, this.domicilioCenicco.getDomicilios().get(0));
            } else {
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.DOMICILIO, null);
            }
            context.addCallbackParam("ruta", MyPaths.urlServletReporte());
        }
    }

    public void descargarTiempoInDeterminadoArtsana(String tipoContrato) {
        RequestContext context = RequestContext.getCurrentInstance();
        String modulo = "";
        switch (tipoContrato) {
            case "ADMINISTRATIVO":
                modulo = ParametrosReportes.MODULO_CARTA_DE_TIEMPO_INDETERMINADO_ADMINISTRATIVO_ARTSANA;
                break;
            case "BECARIO":
                modulo = ParametrosReportes.MODULO_CARTA_DE_TIEMPO_INDETERMINADO_BECARIO_ARTSANA;
                break;
            case "VENTAS":
                modulo = ParametrosReportes.MODULO_CARTA_DE_TIEMPO_INDETERMINADO_VENTAS_ARTSANA;
                break;
        }
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, modulo);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, "CONTRATO_TIEMPO_INDETERMINADO_" + this.relacionLaboral.getNumeroempleado());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_PDF);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.relacionLaboral);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.SALARIO_MENSUAL, this.selectedVista.getSm());
        if (!this.domicilioCenicco.getDomicilios().isEmpty()) {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.DOMICILIO, this.domicilioCenicco.getDomicilios().get(0));
        } else {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.DOMICILIO, null);
        }
        context.addCallbackParam("ruta", MyPaths.urlServletReporte());

    }

    public void descargarConvenioConfidencialidad() {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_CARTA_DE_CONVENIO_CONFIDENCILIDAD);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, "CONTRATO_CONVENIO_CONFIDENCILIDAD_" + this.relacionLaboral.getNumeroempleado());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_PDF);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.relacionLaboral);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.SALARIO_MENSUAL, this.selectedVista.getSm());
        context.addCallbackParam("ruta", MyPaths.urlServletReporte());
    }

    public void descargarConvenioConfidencialidadNutrition() {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_CARTA_DE_CONVENIO_CONFIDENCILIDAD_NUTRITION);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, "CONTRATO_CONVENIO_CONFIDENCILIDAD_" + this.relacionLaboral.getNumeroempleado());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_PDF);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.relacionLaboral);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.SALARIO_MENSUAL, this.selectedVista.getSm());
        context.addCallbackParam("ruta", MyPaths.urlServletReporte());
    }

    public void descargarCartaRenunciaKUA() {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_CARTA_DE_RENUNCIA_KUA);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, "CARTA_DE_RENUNCIA" + this.relacionLaboral.getNumeroempleado());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_PDF);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.relacionLaboral);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.SALARIO_MENSUAL, this.selectedVista.getSm());
        context.addCallbackParam("ruta", MyPaths.urlServletReporte());
    }

    public void descargarServiciosProfesionales() {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_CONTRATO_PRESTACION_SERVICIOS_PROFESIONALES);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, "CONTRATO_PRESTACIÓN_SERVICIOS_PROFESIONALES" + this.relacionLaboral.getNumeroempleado());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_PDF);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.relacionLaboral);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.SALARIO_MENSUAL, this.selectedVista.getSm());
        context.addCallbackParam("ruta", MyPaths.urlServletReporte());
    }

    public void descargarCartaRecomendacionYamana() {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_CARTA_DE_RECOMENDACION_YAMANA);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, "CARTA_RECOMENDACION_" + this.relacionLaboral.getNumeroempleado());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_PDF);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.relacionLaboral);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.SALARIO_MENSUAL, this.selectedVista.getSm());
        if (!this.domicilioCenicco.getDomicilios().isEmpty()) {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.DOMICILIO, this.domicilioCenicco.getDomicilios().get(0));
        } else {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.DOMICILIO, null);
        }
        context.addCallbackParam("ruta", MyPaths.urlServletReporte());
    }

    public void descargarCartaProgress() {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_CARTA_PROGRESS);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, "CARTA_PROGRESS" + this.relacionLaboral.getNumeroempleado());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_PDF);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.relacionLaboral);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.SALARIO_MENSUAL, this.selectedVista.getSm());
        if (!this.domicilioCenicco.getDomicilios().isEmpty()) {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.DOMICILIO, this.domicilioCenicco.getDomicilios().get(0));
        } else {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.DOMICILIO, null);
        }
        context.addCallbackParam("ruta", MyPaths.urlServletReporte());
    }

    public void descargarCartaPeriodoPrueba() {
        RequestContext context = RequestContext.getCurrentInstance();
        Map<String, String> mapamails = ControladorWS.getInstance().getMails();
        String mail = mapamails.get(this.relacionLaboral.getIdempleado().getIdempleado().toString());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_CARTA_DE_PERIODO_PRUEBA_WHITEHAT);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, "CONTRATO_POR_PERIODO_DE_PRUEBA_" + this.relacionLaboral.getNumeroempleado());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_PDF);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.relacionLaboral);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.SALARIO_MENSUAL, this.selectedVista.getSm());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MAIL, mail);
        if (!this.domicilioCenicco.getDomicilios().isEmpty()) {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.DOMICILIO, this.domicilioCenicco.getDomicilios().get(0));
        } else {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.DOMICILIO, null);
        }
        context.addCallbackParam("ruta", MyPaths.urlServletReporte());
    }

    public void descargarCredencialDelMonte() {
        RequestContext context = RequestContext.getCurrentInstance();
        String token = ControladorWS.getInstance().getToken(this.relacionLaboral, ControladorSesiones.getInstance().getUsuarioSession());
        List<String> empleados = new ArrayList<>();
        empleados.add(this.relacionLaboral.getNumeroempleado());
        byte[] pdf = ControladorWS.getInstance().getCredenciales(token, empleados);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_CREDENCIAL_DELMONTE);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, "CREDENCIAL_" + this.relacionLaboral.getNumeroempleado());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, pdf);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_PDF);
        context.addCallbackParam("ruta", MyPaths.urlServletReporte());
    }

    public void descargarCredencialesDelMonte() {
        RequestContext context = RequestContext.getCurrentInstance();
        String token = ControladorWS.getInstance().getToken(null, ControladorSesiones.getInstance().getUsuarioSession());
        List<String> empleados = new ArrayList<>();
        for (VistaEmpleados v : this.selectedVistas) {
            empleados.add(v.getNumeroempleado());
        }
        byte[] pdf = ControladorWS.getInstance().getCredenciales(token, empleados);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_CREDENCIAL_DELMONTE);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, "CREDENCIALES");
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, pdf);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_PDF);
        context.addCallbackParam("ruta", MyPaths.urlServletReporte());
    }

    public void descargarContratoCosechadorDelMonte() {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_CONTRATO_DELMONTE_COSECHADOR);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, "CONTRATO_COSECHADOR_" + this.relacionLaboral.getNumeroempleado());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.relacionLaboral);
        if (!this.domicilioCenicco.getDomicilios().isEmpty()) {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.DOMICILIO, this.domicilioCenicco.getDomicilios().get(0));
        } else {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.DOMICILIO, null);
        }
        context.addCallbackParam("ruta", MyPaths.urlServletReporte());
    }

    public void descargarContratoCosechadorBonoDelMonte() {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_CONTRATO_DELMONTE_COSECHADOR_BONO);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, "CONTRATO_COSECHADOR_" + this.relacionLaboral.getNumeroempleado());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.relacionLaboral);
        if (!this.domicilioCenicco.getDomicilios().isEmpty()) {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.DOMICILIO, this.domicilioCenicco.getDomicilios().get(0));
        } else {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.DOMICILIO, null);
        }
        context.addCallbackParam("ruta", MyPaths.urlServletReporte());
    }

    public void descargarContratoMayordomoDelMonte() {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_CONTRATO_DELMONTE_MAYORDOMO);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, "CONTRATO_MAYORDOMO_" + this.relacionLaboral.getNumeroempleado());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.relacionLaboral);
        if (!this.domicilioCenicco.getDomicilios().isEmpty()) {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.DOMICILIO, this.domicilioCenicco.getDomicilios().get(0));
        } else {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.DOMICILIO, null);
        }
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS_AUX, 2);
        context.addCallbackParam("ruta", MyPaths.urlServletReporte());
    }

    public void descargarContratoMayordomoBonoDelMonte() {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_CONTRATO_DELMONTE_MAYORDOMO_BONO);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, "CONTRATO_MAYORDOMO_BONO_" + this.relacionLaboral.getNumeroempleado());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.relacionLaboral);
        if (!this.domicilioCenicco.getDomicilios().isEmpty()) {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.DOMICILIO, this.domicilioCenicco.getDomicilios().get(0));
        } else {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.DOMICILIO, null);
        }
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS_AUX, 2);
        context.addCallbackParam("ruta", MyPaths.urlServletReporte());
    }

    public void descargarContratoPesadorDelMonte() {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_CONTRATO_DELMONTE_MAYORDOMO);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, "CONTRATO_PESADOR_" + this.relacionLaboral.getNumeroempleado());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.relacionLaboral);
        if (!this.domicilioCenicco.getDomicilios().isEmpty()) {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.DOMICILIO, this.domicilioCenicco.getDomicilios().get(0));
        } else {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.DOMICILIO, null);
        }
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS_AUX, 1);
        context.addCallbackParam("ruta", MyPaths.urlServletReporte());
    }

    public void descargarContratoPesadorBonoDelMonte() {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_CONTRATO_DELMONTE_MAYORDOMO);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, "CONTRATO_PESADOR_Bono" + this.relacionLaboral.getNumeroempleado());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.relacionLaboral);
        if (!this.domicilioCenicco.getDomicilios().isEmpty()) {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.DOMICILIO, this.domicilioCenicco.getDomicilios().get(0));
        } else {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.DOMICILIO, null);
        }
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS_AUX, 1);
        context.addCallbackParam("ruta", MyPaths.urlServletReporte());
    }

    public void descargarContratoSorteadorDelMonte() {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_CONTRATO_DELMONTE_SORTEADOR);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, "CONTRATO_SORTEADOR_" + this.relacionLaboral.getNumeroempleado());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.relacionLaboral);
        if (!this.domicilioCenicco.getDomicilios().isEmpty()) {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.DOMICILIO, this.domicilioCenicco.getDomicilios().get(0));
        } else {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.DOMICILIO, null);
        }
        context.addCallbackParam("ruta", MyPaths.urlServletReporte());
    }

    public void descargarContratoSorteadorBonoDelMonte() {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_CONTRATO_DELMONTE_SORTEADOR_BONO);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, "CONTRATO_SORTEADOR_BONO" + this.relacionLaboral.getNumeroempleado());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.relacionLaboral);
        if (!this.domicilioCenicco.getDomicilios().isEmpty()) {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.DOMICILIO, this.domicilioCenicco.getDomicilios().get(0));
        } else {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.DOMICILIO, null);
        }
        context.addCallbackParam("ruta", MyPaths.urlServletReporte());
    }

    public void descargarContratoOperativoDelMonte() {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_CONTRATO_DELMONTE_OPERATIVO);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, "CONTRATO_OPERATIVO_" + this.relacionLaboral.getNumeroempleado());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.relacionLaboral);
        if (!this.domicilioCenicco.getDomicilios().isEmpty()) {
            Domicilio dom = null;
            for (Domicilio d : this.domicilioCenicco.getDomicilios()) {
                if (d.getFiscal() != 1) {
                    dom = d;
                    break;
                }
            }
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.DOMICILIO, dom);
        } else {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.DOMICILIO, null);
        }
        context.addCallbackParam("ruta", MyPaths.urlServletReporte());
    }

//    Artsana
    public void descargaAsigancionHerramientaTrabajo() {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_ASIGNACION_HERRAMIENTA_TRABAJO);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, "ASIGNACION_HERRAMIENTA_TRABAJO_" + this.relacionLaboral.getNumeroempleado());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_PDF);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.relacionLaboral);
        context.addCallbackParam("ruta", MyPaths.urlServletReporte());
    }

    public void descargaEntregaEquipoResguardo() {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_ENTREGA_EQUIPO_RESGUARDO);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, "CARTA_ENTREGA_EQUIPO_RESGUARDO_" + this.relacionLaboral.getNumeroempleado());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_PDF);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.relacionLaboral);
        context.addCallbackParam("ruta", MyPaths.urlServletReporte());
    }

    public void descargaCartaPatronalArtsana() {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_CARTA_PATRONAL_ARTSANA);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.SALARIO_MENSUAL, this.selectedVista.getSm());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, "CARTA_PATRONAL_" + this.relacionLaboral.getNumeroempleado());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_PDF);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.relacionLaboral);
        context.addCallbackParam("ruta", MyPaths.urlServletReporte());
    }

    public void descargaCartaRenunciaArtsana() {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_CARTA_RENUNCIA_ARTSANA);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, "CARTA_RENUNCIA_" + this.relacionLaboral.getNumeroempleado());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_PDF);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.relacionLaboral);
        context.addCallbackParam("ruta", MyPaths.urlServletReporte());
    }

    public void descargaEntregaTarjetaNomina() {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_CARTA_ENTREGA_TARJETA_NOMINA);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, "CARTA_ENTREGA_TARJETA_NOMINA_" + this.relacionLaboral.getNumeroempleado());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_PDF);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.relacionLaboral);
        context.addCallbackParam("ruta", MyPaths.urlServletReporte());
    }

    public void descargaConstanciaLaboralMinnt() {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_CONSTANCIA_LABORAL_MINNT);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, "CONSTANCIA_LABORAL_MINNT" + this.relacionLaboral.getNumeroempleado());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_PDF);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.relacionLaboral);
        context.addCallbackParam("ruta", MyPaths.urlServletReporte());
    }

    public void descargaCartaLiberacionArtsana() {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_LIBERACION_ARTSANA);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, "CARTA_LIBERACION_" + this.relacionLaboral.getNumeroempleado());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_PDF);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.relacionLaboral);
        context.addCallbackParam("ruta", MyPaths.urlServletReporte());
    }

    public void descargaCartaPoliticasArtsana(String tipo) {
        RequestContext context = RequestContext.getCurrentInstance();
        String modulo = "";
        String nombrearchivo = "";
        switch (tipo) {
            case "USOUNIFORME":
                modulo = ParametrosReportes.MODULO_POLITICAS_USO_UNIFORME;
                nombrearchivo = "CARTA_POLITICAS_USO_UNIFORME_" + this.relacionLaboral.getNumeroempleado();
                break;
            case "GENERALESADMIN":
                modulo = ParametrosReportes.MODULO_POLITICAS_GENERALES_ADMINITRATIVOS_ARTSANA;
                nombrearchivo = "CARTA_POLITICAS_GENERALES_" + this.relacionLaboral.getNumeroempleado();
                break;

            case "TIENDAS":
                modulo = ParametrosReportes.MODULO_POLITICAS_TIENDAS_ARTSANA;
                nombrearchivo = "CARTA_POLITICAS_TIENDAS_" + this.relacionLaboral.getNumeroempleado();
                break;
        }
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, modulo);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, nombrearchivo);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_PDF);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.relacionLaboral);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.SALARIO_MENSUAL, this.selectedVista.getSm());
        if (!this.domicilioCenicco.getDomicilios().isEmpty()) {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.DOMICILIO, this.domicilioCenicco.getDomicilios().get(0));
        } else {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.DOMICILIO, null);
        }
        context.addCallbackParam("ruta", MyPaths.urlServletReporte());

    }

    public void descargaContratos(String nombrearchivo) {
        GeneradorReportes r = new GeneradorReportes();
        String ruta = ControladorWS.getInstance().getPathContratosMasivos();
        byte[] pdf = null;
        Map<String, Domicilio> domicilios = null;
        List<RelacionLaboral> relaciones = new ArrayList<>();

        for (VistaEmpleados v : selectedVistas) {
            RelacionLaboral rl = ControladorWS.getInstance().findRelacionLaboralById(v.getIdrelacionlaboral());
            if (rl.getFechaingreso() == null) {
                continue;
            }
            relaciones.add(rl);
        }
//Aqui se separa el tipo de contrato (pendiente) por nombrearchivo "Tipo de contrato"
        switch (nombrearchivo) {
            case "CONCENTRADO_CONTRATOS_COSECHADOR":
                pdf = r.getArchivoContratoCosechadorDelMonte(relaciones);
                break;
            case "CONCENTRADO_CONTRATOS_MAYORDOMO":
                pdf = r.getArchivoContratoMayordomoDelMonte(relaciones, 2);
                break;
            case "CONCENTRADO_CONTRATOS_SORTEADOR":
                pdf = r.getArchivoContratoSorteadorDelMonte(relaciones);
                break;
            case "CONCENTRADO_CONTRATOS_OPERATIVO":
                pdf = r.getArchivoContratoOperativoDelMonte(relaciones);
                break;
            case "CONCENTRADO_CONTRATOS_PESADOR":
                pdf = r.getArchivoContratoMayordomoDelMonte(relaciones, 1);
                break;
        }

        RequestContext context = RequestContext.getCurrentInstance();
//Se manda con el modulo de credencial para descargar directamente el PDF generado
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_CREDENCIAL_DELMONTE);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, nombrearchivo);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, pdf);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_PDF);
        context.addCallbackParam("ruta", MyPaths.urlServletReporte());
    }

    public void descargarGOCartaLaboral() {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_GO_CARTA_LABORAL);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, "CARTA_LABORAL_" + this.relacionLaboral.getNumeroempleado());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_PDF);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.relacionLaboral);
        context.addCallbackParam("ruta", MyPaths.urlServletCartas());
    }

    public void descargarGOCartaImss() {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_GO_CARTA_IMSS);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, "CARTA_IMSS_" + this.relacionLaboral.getNumeroempleado());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_PDF);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.relacionLaboral);
        context.addCallbackParam("ruta", MyPaths.urlServletCartas());
    }

    public void descargarGOCartaMaternidad() {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_GO_CARTA_MATERNIDAD);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, "CARTA_MATERNIDAD_" + this.relacionLaboral.getNumeroempleado());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_PDF);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.relacionLaboral);
        context.addCallbackParam("ruta", MyPaths.urlServletCartas());
    }

    public void descargarGOCartaGuarderia() {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_GO_CARTA_GUARDERIA);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, "CARTA_GUARDERIA_" + this.relacionLaboral.getNumeroempleado());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_PDF);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.relacionLaboral);
        context.addCallbackParam("ruta", MyPaths.urlServletCartas());
    }

    public void descargarGOCartaEmbajada() {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_GO_CARTA_EMBAJADA);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, "CARTA_EMBAJADA_" + this.relacionLaboral.getNumeroempleado());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_PDF);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.relacionLaboral);
        context.addCallbackParam("ruta", MyPaths.urlServletCartas());
    }

    public void descargarGOCartaBancoBBVA() {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_GO_CARTA_BANCO_BBVA);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, "CARTA_BANCO_BBVA_" + this.relacionLaboral.getNumeroempleado());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_PDF);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.relacionLaboral);
        context.addCallbackParam("ruta", MyPaths.urlServletCartas());
    }

    public void descargarGOCartaFonacotGOH() {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_GO_CARTA_FONACOT_GOH);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, "CARTA_FONACOT_GOH_" + this.relacionLaboral.getNumeroempleado());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_PDF);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.relacionLaboral);
        context.addCallbackParam("ruta", MyPaths.urlServletCartas());
    }

    public void descargarGOCartaFonacotSO() {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_GO_CARTA_FONACOT_SO);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, "CARTA_FONACOT_SO_" + this.relacionLaboral.getNumeroempleado());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_PDF);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.relacionLaboral);
        context.addCallbackParam("ruta", MyPaths.urlServletCartas());
    }

    public void descargarGOCartaSolicitudVacaciones() {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_GO_SOLICITUD_VACACIONES);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, "CARTA_SOLICITUD_VACACIONES_" + this.relacionLaboral.getNumeroempleado());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_PDF);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.relacionLaboral);
        context.addCallbackParam("ruta", MyPaths.urlServletCartas());
    }

    public void descargarCKContratoTrabajoIndeterminadoModalidadPrueba() {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_CK_CONTRATO_TRABAJO_INDETERMINADO_MODALIDAD_PRUEBA);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, "CONTRATO_TRABAJO_PRESENCIAL_" + this.relacionLaboral.getNumeroempleado());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_PDF);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.relacionLaboral);
        context.addCallbackParam("ruta", MyPaths.urlServletCartas());
    }

    public void descargarCKContratoTrabajoTeletrabajo() {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_CK_CONTRATO_TRABAJO_TELETRABAJO);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, "CONTRATO_TRABAJO_TELETRABAJO_" + this.relacionLaboral.getNumeroempleado());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_PDF);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.relacionLaboral);
        context.addCallbackParam("ruta", MyPaths.urlServletCartas());
    }

    public void descargarCKContratoTrabajoGrowthPresencial() {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_CK_CONTRATO_TRABAJO_GROWTH_PRECENCIAL);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, "CONTRATO_TRABAJO_GROWTH_PRESENCIAL_" + this.relacionLaboral.getNumeroempleado());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_PDF);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.relacionLaboral);
        context.addCallbackParam("ruta", MyPaths.urlServletCartas());
    }

    public void descargarCKContratoTrabajoGrowthTeletrabajo() {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_CK_CONTRATO_TRABAJO_GROWTH_TELETRABAJO);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, "CONTRATO_TRABAJO_GROWTH_TELETRABAJO_" + this.relacionLaboral.getNumeroempleado());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_PDF);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.relacionLaboral);
        context.addCallbackParam("ruta", MyPaths.urlServletCartas());
    }

    public void descargargCKContratoConfidencialidadNda() {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_CK_CONTRATO_CONFIDENCIALIDAD_NDA);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, "CONTRATO_CONFIDENCIALIDAD_NDA_" + this.relacionLaboral.getNumeroempleado());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_PDF);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.relacionLaboral);
        context.addCallbackParam("ruta", MyPaths.urlServletCartas());
    }

    public void descargarCKConstanciaLaboralActivo() {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_CK_CONSTANCIA_LABORAL_ACTIVO);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, "CONSTANCIA_LABORAL_ACTIVO_" + this.relacionLaboral.getNumeroempleado());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_PDF);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.relacionLaboral);
        context.addCallbackParam("ruta", MyPaths.urlServletCartas());
    }

    public void descargarCKConstanciaLaboralBaja() {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_CK_CONSTANCIA_LABORAL_BAJA);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, "CONSTANCIA_LABORAL_BAJA_" + this.relacionLaboral.getNumeroempleado());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_PDF);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.relacionLaboral);
        context.addCallbackParam("ruta", MyPaths.urlServletCartas());
    }

    public void descargarCKCartaMaternidad() {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, "cartaMaternidadConekta");
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, "CARTA_MATERNIDAD_" + this.relacionLaboral.getNumeroempleado());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_PDF);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.relacionLaboral);
        context.addCallbackParam("ruta", MyPaths.urlServletCartas());
    }

    public void descargarCartaLaboralKonecta(Boolean sueldo) {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_CK_CARTA_LABORAL_KONECTA);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, "CARTA_LABORAL_" + this.relacionLaboral.getNumeroempleado());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_PDF);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.relacionLaboral);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.SALARIO_MENSUAL, this.selectedVista.getSm());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS_AUX, sueldo);
        context.addCallbackParam("ruta", MyPaths.urlServletCartas());
    }

    public void descargarContratoTeletrabajoOperativo() {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_CONTRATO_TELETRABAJO_OPERATIVO);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, "MODULO_CONTRATO_TELETRABAJO_OPERATIVO" + this.relacionLaboral.getNumeroempleado());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_PDF);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.relacionLaboral);
        context.addCallbackParam("ruta", MyPaths.urlServletReporte());
    }

    public void descargarContratoTeletrabajoAdmin() {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_CONTRATO_TELETRABAJO_ADMIN);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, "MODULO_CONTRATO_TELETRABAJO_ADMIN" + this.relacionLaboral.getNumeroempleado());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_PDF);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.relacionLaboral);
        context.addCallbackParam("ruta", MyPaths.urlServletReporte());
    }

    public void descargarPoliticaTeletrabajoModalidadHibrida() {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_POLITICA_TELETRABAJO_MODALIDAD_HIBRIDA);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, "MODULO_POLITICA_TELETRABAJO_MODALIDAD_HIBRIDA" + this.relacionLaboral.getNumeroempleado());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_PDF);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.relacionLaboral);
        context.addCallbackParam("ruta", MyPaths.urlServletReporte());
    }

    public void descargarAddendumContratoTeletrabajo() {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_ADDENDUM_CONTRATO_TELETRABAJO);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, "CONTRATO_TELETRABAJO" + this.relacionLaboral.getNumeroempleado());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_PDF);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.relacionLaboral);
        context.addCallbackParam("ruta", MyPaths.urlServletReporte());
    }

    public void descargarCartaRenunciaSolicitudPago() {
        System.out.println("bajarcartabaker");
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_CARTA_RENUNCIA);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, "CARTA_RENUNCIA_SOLICITUD_PAGO" + this.relacionLaboral.getNumeroempleado());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_PDF);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.relacionLaboral);
        context.addCallbackParam("ruta", MyPaths.urlServletReporte());

    }

    public void descargarSolicitudPago() {
        System.out.println("bajarcartabaker");
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_SOLICITUD_PAGO);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, "SOLICITUD_DE_PAGO" + this.relacionLaboral.getNumeroempleado());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_PDF);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.relacionLaboral);
        context.addCallbackParam("ruta", MyPaths.urlServletReporte());
    }

    @Override
    public void generarMsg(FacesMessage msg, boolean isValidate) {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().addMessage(null, msg);
        context.addCallbackParam("isValidate", isValidate);
    }

    @Override
    public String getInformacion() {
        return CeniccoUtil.getInformacion(this.vistas.size());
    }

    public void descargarDetalle() {
        List<Integer> ids = new ArrayList<>();
        for (VistaEmpleados ve : filteredVistas) {
            ids.add(ve.getIdrelacionlaboral());
        }
        List<String> jsons = ControladorWS.getInstance().generarJsonDetalleRelacionLaboral(ids);
        Util.escribirFichero(jsons, "DetalleRelacion", ParametrosReportes.ARCHIVO_CSV);
    }

    public VistaEmpleados getVista() {
        return vista;
    }

    public void setVista(VistaEmpleados vista) {
        this.vista = vista;
    }

    public VistaEmpleados getSelectedVista() {
        return selectedVista;
    }

    public void setSelectedVista(VistaEmpleados selectedVista) {
        this.selectedVista = selectedVista;
    }

    public RelacionLaboral getRelacionLaboral() {
        return relacionLaboral;
    }

    public void setRelacionLaboral(RelacionLaboral relacionLaboral) {
        this.relacionLaboral = relacionLaboral;
    }

    public List<VistaEmpleados> getFilteredVistas() {
        return filteredVistas;
    }

    public void setFilteredVistas(List<VistaEmpleados> filteredVistas) {
        this.filteredVistas = filteredVistas;
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

    public List<VistaEmpleados> getVistas() {
        return vistas;
    }

    public ActionRenders getRender() {
        return render;
    }

    public void setRender(ActionRenders render) {
        this.render = render;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public Date getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(Date fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public Date getFechaAntiguedad() {
        return fechaAntiguedad;
    }

    public void setFechaAntiguedad(Date fechaAntiguedad) {
        this.fechaAntiguedad = fechaAntiguedad;
    }

    public Date getFechaAntiguedadDos() {
        return fechaAntiguedadDos;
    }

    public void setFechaAntiguedadDos(Date fechaAntiguedadDos) {
        this.fechaAntiguedadDos = fechaAntiguedadDos;
    }

    public Date getFechaAntiguedadTres() {
        return fechaAntiguedadTres;
    }

    public void setFechaAntiguedadTres(Date fechaAntiguedadTres) {
        this.fechaAntiguedadTres = fechaAntiguedadTres;
    }

    public Date getFechaInicioSD() {
        return fechaInicioSD;
    }

    public void setFechaInicioSD(Date fechaInicioSD) {
        this.fechaInicioSD = fechaInicioSD;
    }

    public Date getFechaInicioSDI() {
        return fechaInicioSDI;
    }

    public void setFechaInicioSDI(Date fechaInicioSDI) {
        this.fechaInicioSDI = fechaInicioSDI;
    }

    public StreamedContent getImagen() {
        if (this.relacionLaboral == null) {
            return null;
        }
        if (this.relacionLaboral.getIdempleado() == null) {
            return null;
        }
        if (this.relacionLaboral.getIdempleado().getPathfoto() == null) {
            return null;
        }
        return new DefaultStreamedContent(new ByteArrayInputStream(this.relacionLaboral.getIdempleado().getPathfoto()));
    }

    public Integer getEdad() {
        if (this.relacionLaboral == null) {
            return 0;
        }
        if (this.relacionLaboral.getIdempleado() == null) {
            return 0;
        }
        if (this.relacionLaboral.getIdempleado().getFechanacimiento() == null) {
            return 0;
        }
        return CeniccoUtil.calcularEdad(this.relacionLaboral.getIdempleado().getFechanacimiento());

    }

    public void selectionChangedJornadaLaboral(final AjaxBehaviorEvent event) {
        System.out.println("Selecciono Jornada Laboral " + this.selectIdJornadaLaboral);

        turnos = ControladorWS.getInstance().findTurnoLaboralByJornadaLaboral(this.selectIdJornadaLaboral);
    }

    public void selectionChangedTurnoLaboral(final AjaxBehaviorEvent event) {
        Integer value = (Integer) ((UIOutput) event.getSource()).getValue();
        System.out.println("Selecciono Turno Laboral " + value);
        if (value != null) {
            TurnoLaboral tl = ControladorWS.getInstance().findTurnoLaboralById(value);
            if (tl != null) {
                this.posicionLaboral.getPosicion().setTurnoLaboral(tl);
            }
        }
        System.out.println("turno Lobral " + selectIdTurnoLaboral);
    }

    public void selectionChangedDepartamento(final AjaxBehaviorEvent event) {
        System.out.println("Selecciono Departamento " + this.posicionLaboral.getPosicion().getIddepartamento().getIddepartamento());

        subDepartamentos = ControladorWS.getInstance().findSubDepartamentoByIdDepartamento(this.posicionLaboral.getPosicion().getIddepartamento().getIddepartamento());
    }

    public void selectionChangedSubDepartamento(AjaxBehaviorEvent event) {
        System.out.println("Entro A evento:");
        Integer value = (Integer) ((UIOutput) event.getSource()).getValue();
        if (value != null) {
            for (SubDepartamento subDep : subDepartamentos) {
                if (subDep.getIdSubDepartamento() == value) {
                    this.posicionLaboral.getPosicion().setSubDepartamento(subDep);
                    System.out.println("Selecciono Subdepa " + posicionLaboral.getPosicion().getSubDepartamento().getNombre());
                    System.out.println("Selecciono Subdepa selccionado" + selectedSubdepartamento.getNombre());
                    break;
                }
            }
        }
    }

    public String getColorEstatus() {
        return colorEstatus;
    }

    public String getEstatusEmpleado() {
        return estatusEmpleado;
    }

    public DomiciliosCenicco getDomicilioCenicco() {
        return domicilioCenicco;
    }

    public DocumentosCenicco getDocumentoCenicco() {
        return documentoCenicco;
    }

    public ContactoCenicco getContactoCenicco() {
        return contactoCenicco;
    }

    public PosicionCenicco getPosicionLaboral() {
        return posicionLaboral;
    }

    public CuentaBancariaCenicco getCuentaBancaria() {
        return cuentaBancaria;
    }

    public InfonavitCenicco getInfonavitLaboral() {
        return infonavitLaboral;
    }

    public InformacionComplementariaCenicco getComplementaria() {
        return complementaria;
    }

    public Integer getSelectedEstructura() {
        return selectedEstructura;
    }

    public void setSelectedEstructura(Integer selectedEstructura) {
        this.selectedEstructura = selectedEstructura;
    }

    public List<EstructuraDatos> getDatos() {
        return datos;
    }

    public PensionCenicco getPensionLaboral() {
        return pensionLaboral;
    }

    public void setPensionLaboral(PensionCenicco pensionLaboral) {
        this.pensionLaboral = pensionLaboral;
    }

    public List<Estructura> getEstructuras() {
        return ControladorWS.getInstance().findEstructuraByNivel(Parametros.ESTRUCTURA_NIVEL);
    }

    public List<HistoricoSueldo> getHistoricosueldos() {
        return historicosueldos;
    }

    public UploadedFile getArchivo() {
        return archivo;
    }

    public void setArchivo(UploadedFile archivo) {
        this.archivo = archivo;
    }

    public UploadedFile getFoto() {
        return foto;
    }

    public void setFoto(UploadedFile foto) {
        this.foto = foto;
    }

    public Double getSalarioDiario() {
        return salarioDiario;
    }

    public void setSalarioDiario(Double salarioDiario) {
        this.salarioDiario = salarioDiario;
    }

    public Double getSalarioDiarioIntegrado() {
        return salarioDiarioIntegrado;
    }

    public void setSalarioDiarioIntegrado(Double salarioDiarioIntegrado) {
        this.salarioDiarioIntegrado = salarioDiarioIntegrado;
    }

    public DependienteCenicco getDependienteCenicco() {
        return dependienteCenicco;
    }

    public List<VistaEmpleados> getSelectedVistas() {
        return selectedVistas;
    }

    public void setSelectedVistas(List<VistaEmpleados> selectedVistas) {
        this.selectedVistas = selectedVistas;
    }

    public String getCompania() {
        return compania;
    }

    public void setCompania(String compania) {
        this.compania = compania;
    }

    public List<CompaniaProveedorVale> getProveedoresVales() {
        return proveedoresVales;
    }

    public void setProveedoresVales(List<CompaniaProveedorVale> proveedoresVales) {
        this.proveedoresVales = proveedoresVales;
    }

    public Integer getIdcompaniaproveedorvale() {
        return idcompaniaproveedorvale;
    }

    public void setIdcompaniaproveedorvale(Integer idcompaniaproveedorvale) {
        this.idcompaniaproveedorvale = idcompaniaproveedorvale;
    }

    public String getCuentavale() {
        return cuentavale;
    }

    public void setCuentavale(String cuentavale) {
        this.cuentavale = cuentavale;
    }

    public String getNumerotarjetavale() {
        return numerotarjetavale;
    }

    public void setNumerotarjetavale(String numerotarjetavale) {
        this.numerotarjetavale = numerotarjetavale;
    }

    public List<EmpleadoVale> getEmpleadoVales() {
        return empleadoVales;
    }

    public void setEmpleadoVales(List<EmpleadoVale> empleadoVales) {
        this.empleadoVales = empleadoVales;
    }

    public Integer getSelectIdJornadaLaboral() {
        return selectIdJornadaLaboral;
    }

    public void setSelectIdJornadaLaboral(Integer selectIdJornadaLaboral) {
        this.selectIdJornadaLaboral = selectIdJornadaLaboral;
    }

    public Integer getSelectIdTurnoLaboral() {
        return selectIdTurnoLaboral;
    }

    public void setSelectIdTurnoLaboral(Integer selectIdTurnoLaboral) {
        this.selectIdTurnoLaboral = selectIdTurnoLaboral;
    }

    public List<JornadaLaboral> getJornadaslaborales() {
        return jornadaslaborales;
    }

    public void setJornadaslaborales(List<JornadaLaboral> jornadaslaborales) {
        this.jornadaslaborales = jornadaslaborales;
    }

    public List<TurnoLaboral> getTurnos() {
        return turnos;
    }

    public void setTurnos(List<TurnoLaboral> turnos) {
        this.turnos = turnos;
    }

    public List<SubDepartamento> getSubDepartamentos() {
        return subDepartamentos;
    }

    public void setSubDepartamentos(List<SubDepartamento> subDepartamentos) {
        this.subDepartamentos = subDepartamentos;
    }

    public Integer getSelectIdDepartamento() {
        return selectIdDepartamento;
    }

    public void setSelectIdDepartamento(Integer selectIdDepartamento) {
        this.selectIdDepartamento = selectIdDepartamento;
    }

    public SubDepartamento getSelectedSubdepartamento() {
        return selectedSubdepartamento;
    }

    public void setSelectedSubdepartamento(SubDepartamento selectedSubdepartamento) {
        this.selectedSubdepartamento = selectedSubdepartamento;
    }
}
