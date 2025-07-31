/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lam.cenicco.beans.administracion.masivasaltas;

import com.lam.cenicco.impl.ControladorWS;
import com.lam.cenicco.librerias.utilidades.CeniccoUtil;
import com.lam.cenicco.util.ParametrosReportes;
import com.lam.cenicco.util.Util;
import com.lam.cenicco.ws.Acumulado;
import com.lam.cenicco.ws.Banco;
import com.lam.cenicco.ws.CuentaBancaria;
import com.lam.cenicco.ws.Empleado;
import com.lam.cenicco.ws.RelacionLaboral;
import com.lam.cenicco.ws.RelacionLaboralPosicion;
import com.lam.cenicco.ws.Domicilio;
import com.lam.cenicco.ws.Estado;
import com.lam.cenicco.ws.Turno;
import com.lam.cenicco.ws.TurnoLaboral;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import javax.xml.datatype.XMLGregorianCalendar;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author Antonio Durán
 */
@Named(value = "altamasivaBean")
@SessionScoped
public class AltaMasivaBean implements Serializable {
//    

    private UploadedFile archivo;
//    
    private Integer mes;
//    
    private Integer anio;
//    
    private Integer selectedGrupoPago;
//    
    private String hide;

    public void fileUploadListener(FileUploadEvent event) {
        archivo = event.getFile();
//        
        boolean isvalidate = false;
//        
        List<String> lista = Util.convertFileToStr(this.archivo);
//        
        List<String> mails = new ArrayList<>();
//        
        GeneralesDAO dao = new GeneralesDAO(lista);
//        
        List<Estado> estados = ControladorWS.getInstance().getEstados();
//        
        String error = dao.validarArchivo();
//        
        String estadoNacimiento;
//        
        if (error.equals("")) {
            List<Empleado> empleados = new ArrayList<>();
            for (int i = 0; i < lista.size(); i++) {
                String[] parts = lista.get(i).split(",");
                Empleado emp = new Empleado();
                emp.setExpediente(parts[dao.getPOSICION_NUMERO_EMPLEADO()].trim().toUpperCase());
                emp.setApellidopaterno(parts[dao.getPOSICION_APELLIDO_PATERNO()].trim().toUpperCase());
                if (!parts[dao.getPOSICION_APELLIDO_MATERNO()].equals("")) {
                    emp.setApellidomaterno(parts[dao.getPOSICION_APELLIDO_MATERNO()].trim().toUpperCase());
                }
                emp.setNombre(parts[dao.getPOSICION_NOMBRE()].trim().toUpperCase());
                switch (parts[dao.getPOSICION_SEXO()]) {
                    case "h":
                    case "H":
                        emp.setSexo("1");
                        break;
                    case "m":
                    case "M":
                        emp.setSexo("0");
                        break;
                }
                if (!parts[dao.getPOSICION_ESTADO_CIVIL()].equals("")) {
                    switch (parts[dao.getPOSICION_ESTADO_CIVIL()]) {
                        case "SOLTERO":
                        case "soltero":
                            emp.setEstadocivil("1");
                            break;
                        case "CASADO":
                        case "casado":
                            emp.setEstadocivil("2");
                            break;
                        case "DIVORCIADO":
                        case "divorciado":
                            emp.setEstadocivil("3");
                            break;
                        case "UNIONLIBRE":
                        case "unionlibre":
                            emp.setEstadocivil("4");
                            break;
                        case "VIUDO":
                        case "viudo":
                            emp.setEstadocivil("5");
                            break;
                        case "SEPARADO":
                        case "separado":
                            emp.setEstadocivil("6");
                            break;
                    }
                }

                if (!parts[dao.getPOSICION_CURP()].equals("")) {
                    estadoNacimiento = parts[dao.getPOSICION_CURP()].charAt(11) + "" + parts[dao.getPOSICION_CURP()].charAt(12);
                    for (Estado e : estados) {
                        if (e.getEstado().equals(estadoNacimiento)) {
                            emp.setIdestadonacimiento(e.getIdestado());
                            continue;
                        }
                    }
                }
                XMLGregorianCalendar fecha =
                        CeniccoUtil.convertStringToXmlGregorian(parts[dao.getPOSICION_FECHA_NACIMIENTO()]);
//                
                emp.setFechanacimiento(fecha);
                emp.setAfiliacion(parts[dao.getPOSICION_NSS()].trim().toUpperCase());
                emp.setRfc(parts[dao.getPOSICION_RFC()].trim().toUpperCase());
                emp.setCurp(parts[dao.getPOSICION_CURP()].trim().toUpperCase());
                emp.setEstatus(1);
//                
                emp.setIdInformacionComplementaria(null);
//                
                if (!parts[dao.getPOSICION_LUGAR_NACIMIENTO()].equals("")) {
                    emp.setLugarnacimiento(parts[dao.getPOSICION_LUGAR_NACIMIENTO()].trim().toUpperCase());
                }
                mails.add(parts[dao.getPOSICION_EMAIL()]);
                empleados.add(emp);
            }
            isvalidate = ControladorWS.getInstance().crearEmpleados(empleados, mails);
            System.out.println("CrearEmpleados. " + isvalidate);
        }
        if (error.equals("") && isvalidate) {
            generarMsg(Util.getFacesMessage(FacesMessage.SEVERITY_INFO, "Exitoso al crear Empleados!"), isvalidate);
        } else {
            generarMsg(Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, "Error al crear Empleados: " + error), isvalidate);
        }
//        
    }

    public void fileUploadListenerRelacionesLaborales(FileUploadEvent event) {
        archivo = event.getFile();
        List<String> lista = Util.convertFileToStr(this.archivo);
//        
        RelacionesDAO dao = new RelacionesDAO(lista);
//        
        String error = dao.validarArchivo();
//        
        boolean isvalidate = false;
//        
        if (error.equals("")) {
            List<RelacionLaboral> relaciones = new ArrayList<>();
            for (int i = 0; i < lista.size(); i++) {
                String[] parts = lista.get(i).split(",");
                RelacionLaboral rellab = dao.getMapaNumeroEmpleados().get(parts[dao.getPOSICION_NUMERO_EMPLEADO()]);
                switch (parts[dao.getPOSICION_ESTATUS()]) {
                    case "ACTIVO":
                    case "activo":
                        rellab.setEstatus("1");
                        break;
                    case "INACTIVO":
                    case "inactivo":
                        rellab.setEstatus("0");
                        break;
                    case "REGISTRADO":
                    case "registrado":
                        rellab.setEstatus("99");
                        break;
                }
                XMLGregorianCalendar fechaantiguedad = CeniccoUtil.convertStringToXmlGregorian(parts[dao.getPOSICION_FECHA_ANTIGUEDAD()]);
                rellab.setFechaantiguedad1(fechaantiguedad);
                XMLGregorianCalendar fechaingreso = CeniccoUtil.convertStringToXmlGregorian(parts[dao.getPOSICION_FECHA_INGRESO()]);
                rellab.setFechaingreso(fechaingreso);
                rellab.setFechaSalarioDiario(fechaingreso);
                rellab.setFechaSalarioDiarioIntegrado(fechaingreso);
                rellab.setIdcompania(dao.getMapaCompanias().get(parts[dao.getPOSICION_COMPANIA()]));
                rellab.setIdgrupopago(dao.getMapaGrupoPago().get(parts[dao.getPOSICION_GRUPO_PAGO()]));
                rellab.setIdregistropatronal(dao.getMapaRegistroPatronal().get(parts[dao.getPOSICION_REGISTRO_PATRONAL()]));
                rellab.setIdsistemaantiguedad(dao.getMapaSistemaAntiguedad().get(parts[dao.getPOSICION_SISTEMA_ANTIGUEDAD()]));
                rellab.setIdtiporellab(dao.getMapaTipoRelacionLaboral().get(parts[dao.getPOSICION_TIPO_RELLAB()]));
                rellab.setSalarioDiario(Double.parseDouble(parts[dao.getPOSICION_SD()]));
                rellab.setIdTipoSalarioIdse(dao.getMapaTipoSueldoIdse().get("2"));
//                
                relaciones.add(rellab);

            }
            isvalidate = ControladorWS.getInstance().editarRelacionLaborales(relaciones);
            System.out.println("IsValidate: " + isvalidate);
        }
//        
        if (error.equals("") && isvalidate) {
            generarMsg(Util.getFacesMessage(FacesMessage.SEVERITY_INFO, "Exitoso al crear Relaciones Laborales!"), isvalidate);
        } else {
            generarMsg(Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, "Error al crear Relaciones Laborales: " + error), isvalidate);
        }
    }

    public void fileUploadListenerCuentasBancarias(FileUploadEvent event) {
        archivo = event.getFile();
//        
        boolean isvalidate = false;
//                
        List<String> lista = Util.convertFileToStr(this.archivo);
//        
        CuentasBancariasDAO dao = new CuentasBancariasDAO(lista);
        String error = dao.validarArchivo();
        if (error.equals("")) {
            List<CuentaBancaria> cuentas = new ArrayList<>();
            for (int i = 0; i < lista.size(); i++) {
                String[] parts = lista.get(i).split(",");
                RelacionLaboral rellab =
                        dao.getMapaNumeroEmpleados().get(parts[dao.getPOSICION_NUMERO_EMPLEADO()]);
//                
                Banco banco = dao.getMapaBancos().get(parts[dao.getPOSICION_BANCO()]);
                Banco bancoempleado = dao.getMapaBancos().get(parts[dao.getPOSICION_BANCO_EMPLEADO()]);
//            
                CuentaBancaria cta = new CuentaBancaria();
                cta.setIdrellab(rellab);
                cta.setIdbanco(banco);
                cta.setClabe(parts[dao.getPOSICION_CLABE_BANCARIA()]);
                cta.setCuenta(parts[dao.getPOSICION_CUENTA_BANCARIA()]);
                cta.setIdbancoempleado(bancoempleado);
                cta.setEstatus(1);
//                
                cuentas.add(cta);
            }
            isvalidate = ControladorWS.getInstance().crearCuentasBancarias(cuentas);
            System.out.println("IsValidate: " + isvalidate);
        }
        if (error.equals("") && isvalidate) {
            generarMsg(Util.getFacesMessage(FacesMessage.SEVERITY_INFO, "Exitoso al crear Cuentas Bancarias!"), isvalidate);
        } else {
            generarMsg(Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, "Error al crear Cuentas Bancarias: " + error), isvalidate);
        }
    }

    public void fileUploadListenerPosiciones(FileUploadEvent event) {
        archivo = event.getFile();
//        
        boolean isvalidate = false;
//                
        List<String> lista = Util.convertFileToStr(this.archivo);
//        
        PosicionesDAO dao = new PosicionesDAO(lista);
        String error = dao.validarArchivo();
        if (error.equals("")) {
            List<RelacionLaboralPosicion> posiciones = new ArrayList<>();
//            
            for (int i = 0; i < lista.size(); i++) {
                String[] parts = lista.get(i).split(",");
//                
                RelacionLaboralPosicion posicion = dao.getRelacionLaboral(parts).getIdrelacionlaboralposicion();
//             
                try {
                    if (!parts[dao.getPOSICION_CCO()].equals("")) {
                        posicion.setIdcentrocosto(dao.getCCo(parts));
                    }

                } catch (Exception e) {
                   posicion.setIdcentrocosto(null);
                }
                try {
                    if (!parts[dao.getPOSICION_PUESTO()].equals("")) {
                        posicion.setIdpuesto(dao.getPuesto(parts));
                    }
                } catch (Exception e) {
                     posicion.setIdpuesto(null);
                }
                if (posicion.getIdpuesto() != null && posicion.getIdpuesto().getIdpuesto() == null) {
                    posicion.setIdpuesto(null);
                }

                try {
                    if (!parts[dao.getPOSICION_DEPARTAMENTO()].equals("")) {
                        posicion.setIddepartamento(dao.getDepartamento(parts));
                    }
                } catch (Exception e) {
                  posicion.setIddepartamento(null);
                }
                try {
                    if (!parts[dao.getPOSICION_JEFE_DIRECTO()].equals("")) {
                        Empleado emp = dao.getJefeDirecto(parts).getIdempleado();
                        posicion.setIdrepresentante(emp.getIdempleado());
                    }
                } catch (Exception e) {
                    posicion.setIdrepresentante(null);
                }
                
                try {
                    if (!parts[dao.getPOSICION_NOMBRE_TURNO_LABORAL()].equals("")) {
                        posicion.setTurnoLaboral(dao.getTurnoLaboral(parts));
                    }
                } catch (Exception e) {
                    posicion.setTurnoLaboral(null);
                }
//                
                posicion.setIdturno(null);
                if (posicion.getGrupopago() != null && posicion.getGrupopago().getIdgrupopago() == null) {
                    posicion.setGrupopago(null);
                }
                if (posicion.getSubarea() != null && posicion.getSubarea().getIdsubarea() == null) {
                    posicion.setSubarea(null);
                }
                if (posicion.getArea() != null && posicion.getArea().getIdarea() == null) {
                    posicion.setArea(null);
                }
//                
                posiciones.add(posicion);
            }
            isvalidate = ControladorWS.getInstance().editarPosiciones(posiciones);
            System.out.println("IsValidate: " + isvalidate);
        }
        if (error.equals("") && isvalidate) {
            generarMsg(Util.getFacesMessage(FacesMessage.SEVERITY_INFO, "Exitoso al editar Posiciones!"), isvalidate);
        } else {
            generarMsg(Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, "Error al editar Posiciones: " + error), isvalidate);
        }
    }

    public void fileUploadListenerSueldos(FileUploadEvent event) {
        archivo = event.getFile();
//        
        boolean isvalidate = false;
//                
        List<String> lista = Util.convertFileToStr(this.archivo);
//        
        SueldosDAO dao = new SueldosDAO(lista);
        String error = dao.validarArchivo();
        if (error.equals("")) {
            List<RelacionLaboral> relaciones = new ArrayList<>();
            for (int i = 0; i < lista.size(); i++) {
                String[] parts = lista.get(i).split(",");
                RelacionLaboral rellab = dao.getRelacionLaboral(parts);
                if (!parts[dao.getPOSICION_SD()].equals("")) {
                    rellab.setSalarioDiarioAnterior(rellab.getSalarioDiario());
                    rellab.setSalarioDiario(Double.parseDouble(parts[dao.getPOSICION_SD()]));
                }
                if (!parts[dao.getPOSICION_FECHA_SD()].equals("")) {
                    rellab.setFechaSalarioDiarioAnterior(rellab.getFechaSalarioDiario());
                    rellab.setFechaSalarioDiario(CeniccoUtil.convertStringToXmlGregorian(parts[dao.getPOSICION_FECHA_SD()]));
                }
                if (!parts[dao.getPOSICION_SDI()].equals("")) {
                    rellab.setSalarioDiarioIntegradoAnterior(rellab.getSalarioDiarioIntegrado());
                    rellab.setSalarioDiarioIntegrado(Double.parseDouble(parts[dao.getPOSICION_SDI()]));
                }
                if (!parts[dao.getPOSICION_FECHA_SDI()].equals("")) {
                    rellab.setFechaSalarioDiarioIntegradoAnterior(rellab.getFechaSalarioDiarioIntegrado());
                    rellab.setFechaSalarioDiarioIntegrado(CeniccoUtil.convertStringToXmlGregorian(parts[dao.getPOSICION_FECHA_SDI()]));
                }
//                
                relaciones.add(rellab);
            }
            //                
            isvalidate = ControladorWS.getInstance().editarRelacionLaborales(relaciones);
        }
        if (error.equals("") && isvalidate) {
            generarMsg(Util.getFacesMessage(FacesMessage.SEVERITY_INFO, "Exitoso al editar Sueldos!"), isvalidate);
        } else {
            generarMsg(Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, "Error al editar Sueldos: " + error), isvalidate);
        }
    }

    public void fileUploadListenerBajas(FileUploadEvent event) {
        archivo = event.getFile();
//        
        boolean isvalidate = false;
//                
        List<String> lista = Util.convertFileToStr(this.archivo);
//        
        BajasDAO dao = new BajasDAO(lista);
        String error = dao.validarArchivo();
        if (error.equals("")) {
            List<RelacionLaboral> relaciones = new ArrayList<>();
            for (int i = 0; i < lista.size(); i++) {
                String[] parts = lista.get(i).split(",");
                RelacionLaboral rellab = dao.getRelacionLaboral(parts);
                rellab.setEstatus("0");
                switch (parts[dao.getPOSICION_CAUSA_BAJA()].toUpperCase()) {
                    case "1":/*"ABANDONOEMPLEO":*/
                        rellab.setCausabaja("3");
                        break;
                    case "2":/*"DEFUNCION":*/
                        rellab.setCausabaja("4");
                        break;
                    case "3":/*"OTRAS":*/
                        rellab.setCausabaja("6");
                        break;
                    case "4":/*"PENSION":*/
                        rellab.setCausabaja("A");
                        break;
                    case "5":/*"RECISIONCONTRATO":*/
                        rellab.setCausabaja("8");
                        break;
                    case "6":/*"SEPARACIONVOLUNTARIA":*/
                        rellab.setCausabaja("2");
                        break;
                    case "7":/*"TERMINOCONTRATO":*/
                        rellab.setCausabaja("1");
                        break;
                    case "8":/*"AUSENTISMO":*/
                        rellab.setCausabaja("7");
                        break;
                    case "9":/*"CONFLICTOSLABORALES":*/
                        rellab.setCausabaja("73");
                        break;
                    case "10":/*"RENUNCIATRABAJADOR":*/
                        rellab.setCausabaja("71");
                        break;
                    case "11":/*"CLAUSURA":*/
                        rellab.setCausabaja("5");
                        break;
                    case "12":/*"JUBILACION":*/
                        rellab.setCausabaja("9");
                        break;
                }
                rellab.setFechabaja(CeniccoUtil.convertStringToXmlGregorian(parts[dao.getPOSICION_FECHA_BAJA()]));
//                
                relaciones.add(rellab);
            }
            //                
            isvalidate = ControladorWS.getInstance().editarRelacionLaborales(relaciones);
        }
        if (error.equals("") && isvalidate) {
            generarMsg(Util.getFacesMessage(FacesMessage.SEVERITY_INFO, "Exitoso al editar Bajas!"), isvalidate);
        } else {
            generarMsg(Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, "Error al editar Bajas: " + error), isvalidate);
        }
    }

    public void fileUploadListenerAcumulados(FileUploadEvent event) {

        archivo = event.getFile();
        String error = validarParametros();
//        
        if (error.equals("")) {
            List<String> lista = Util.convertFileToStr(this.archivo);
            AcumuladosDAO dao = new AcumuladosDAO(lista, this.selectedGrupoPago);
            error = dao.validarArchivo();
            if (error.equals("")) {
                List<Acumulado> acumulados = new ArrayList<>();
                for (int i = 0; i < lista.size(); i++) {
                    String[] parts = lista.get(i).split(",");
                    RelacionLaboral rellab = dao.getRelacionLaboral(parts);
                    Acumulado a = new Acumulado();
                    a.setMes(mes);
                    a.setAnio(anio);
                    a.setFiniquito(0);
                    a.setGrupopago(rellab.getIdgrupopago());
                    a.setRelacionlaboral(rellab);
//                
                    try {
                        a.setTiempo(0);
                        if (!parts[dao.getPOSICION_TIEMPO()].equals("")) {
                            a.setTiempo(Integer.parseInt(parts[dao.getPOSICION_TIEMPO()]));
                        }
                    } catch (Exception e) {
                    }

//                
                    try {
                        a.setBasegravada(0.0);
                        if (!parts[dao.getPOSICION_BG()].equals("")) {
                            a.setBasegravada(Double.parseDouble(parts[dao.getPOSICION_BG()]));
                        }
                    } catch (Exception e) {
                    }

                    try {
                        a.setImpuestoretenido(0.0);
                        if (!parts[dao.getPOSICION_ISR()].equals("")) {
                            a.setImpuestoretenido(Double.parseDouble(parts[dao.getPOSICION_ISR()]));
                        }
                    } catch (Exception e) {
                    }

                    try {
                        a.setSubsidioempleado(0.0);
                        if (!parts[dao.getPOSICION_SUBSIDIO()].equals("")) {
                            a.setSubsidioempleado(Double.parseDouble(parts[dao.getPOSICION_SUBSIDIO()]));
                        }
                    } catch (Exception e) {
                    }

                    try {
                        a.setBasegravadaaguinaldo(0.0);
                        if (!parts[dao.getPOSICION_BG_AGUINALDO()].equals("")) {
                            a.setBasegravadaaguinaldo(Double.parseDouble(parts[dao.getPOSICION_BG_AGUINALDO()]));
                        }
                    } catch (Exception e) {
                    }

                    try {
                        a.setIsrretenidoaguinaldo(0.0);
                        if (!parts[dao.getPOSICION_ISR_AGUINALDO()].equals("")) {
                            a.setIsrretenidoaguinaldo(Double.parseDouble(parts[dao.getPOSICION_ISR_AGUINALDO()]));
                        }
                    } catch (Exception e) {
                    }

//                
                    acumulados.add(a);
                }
                for (Acumulado a : acumulados) {
                    System.out.println(
                            "Acumulado: " + a.getRelacionlaboral().getNumeroempleado()
                            + " | " + a.getGrupopago().getGrupopago()
                            + " | Mes: " + a.getMes()
                            + " | Anio: " + a.getAnio()
                            + " | Tiempo: " + a.getTiempo()
                            + " | BG: " + a.getBasegravada()
                            + " | ISR: " + a.getImpuestoretenido()
                            + " | Subsidio: " + a.getSubsidioempleado()
                            + " | BG Aguinaldo: " + a.getBasegravadaaguinaldo()
                            + " | ISR Aguinaldo: " + a.getIsrretenidoaguinaldo());
                }
                boolean isvalidate = ControladorWS.getInstance().crearAcumulados(acumulados, mes, anio, selectedGrupoPago);
                if (isvalidate) {
                    generarMsg(Util.getFacesMessage(FacesMessage.SEVERITY_INFO, "Exitoso al editar Acumulados!"), isvalidate);
                } else {
                    generarMsg(Util.getFacesMessage(FacesMessage.SEVERITY_INFO, "Error al editar Acumulados!"), isvalidate);
                }

            } else {
                generarMsg(Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, "Error al editar Acumulados: " + error), false);
            }

        } else {
            generarMsg(Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, error), false);
        }


    }

    public void fileUploadListenerDomicilios(FileUploadEvent event) {
        archivo = event.getFile();
        List<String> lista = Util.convertFileToStr(this.archivo);
//        
        DomiciliosDAO dao = new DomiciliosDAO(lista);
//        
        String error = dao.validarArchivo();
//        
        boolean isvalidate = false;
//        
        if (error.equals("")) {
            List<Domicilio> domicilios = new ArrayList<>();
            for (int i = 0; i < lista.size(); i++) {
                String[] parts = lista.get(i).split(",");
                RelacionLaboral rellab = dao.getMapaNumeroEmpleados().get(parts[dao.getPOSICION_NUMERO_EMPLEADO()]);
                Domicilio dom = new Domicilio();
                dom.setIdempleado(rellab.getIdempleado().getIdempleado());
                dom.setCalle(parts[dao.getPOSICION_CALLE()]);
                dom.setColonia(parts[dao.getPOSICION_COLONIA()]);
                dom.setMunicipio(parts[dao.getPOSICION_MUNICIPIO()]);
                dom.setEstado(parts[dao.getPOSICION_ESTADO()]);
                dom.setCp(parts[dao.getPOSICION_CP()]);
                dom.setNumeroexterior(parts[dao.getPOSICION_NUMERO_EXTERIOR()]);
                if (parts[dao.getPOSICION_NUMERO_INTERIOR()].equals("0")) {
                    dom.setNumerointerior("");
                } else {
                    dom.setNumerointerior(parts[dao.getPOSICION_NUMERO_INTERIOR()]);
                }
                String fiscal = parts[dao.getPOSICION_DOMICILIO_FISCAL()];
                if (fiscal.equalsIgnoreCase("SI")) {
                    dom.setFiscal(1);

                    Domicilio domicilioFiscal = ControladorWS.getInstance().getDomicilioFiscalByIdEmpleado(rellab.getIdempleado().getIdempleado());
                    if (domicilioFiscal != null) {
                        domicilioFiscal.setFiscal(0);
                        ControladorWS.getInstance().editDomicilio(domicilioFiscal);
                    }
                } else {
                    dom.setFiscal(0);
                }
                domicilios.add(dom);
            }
            for (Domicilio dom : domicilios) {
                isvalidate = ControladorWS.getInstance().createDomicilio(dom, dom.getIdempleado());
                if (!isvalidate) {
                    System.out.println("Error Crear Domicilio IdEmpleado:  " + dom.getIdempleado());
                    break;
                }
            }
            System.out.println("IsValidate: " + isvalidate);
        }
//        
        if (error.equals("") && isvalidate) {
            generarMsg(Util.getFacesMessage(FacesMessage.SEVERITY_INFO, "Exitoso al crear Domicilios!"), isvalidate);
        } else {
            generarMsg(Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, "Error al crear Domicilios: " + error), isvalidate);
        }
    }

    public void fileUploadListenerVariables(FileUploadEvent event) {
        archivo = event.getFile();
        List<String> lista = Util.convertFileToStr(this.archivo);
    }

    public void fileUploadListenerFactorInfonavit(FileUploadEvent event) {
        archivo = event.getFile();
        List<String> lista = Util.convertFileToStr(this.archivo);
    }

    private String validarParametros() {
        boolean bandera = false;
        String error = "";
        if (this.mes == null) {
            error = "Favor de Seleccionar Mes a Aplicar";
            bandera = true;
        }
        if (this.anio == null) {
            if (!bandera) {
                error = "Favor de Seleccionar Año a Aplicar";
                bandera = true;
            } else {
                error += ", Año a Aplicar";
            }
        }
        if (this.selectedGrupoPago == null) {
            if (!bandera) {
                error = "Favor de Seleccionar Grupo de Pago";
            } else {
                error += ", Grupo de Pago";
            }
        }
        return error;
    }

    public void fileUploadListenerEstatus(FileUploadEvent event) {
        this.archivo = event.getFile();
        List<String> lista = Util.convertFileToStr(this.archivo);
        boolean isValidate = false;
        RelacionLaboral rl;
        for (String l : lista) {
            String[] line = l.split(",");
            if (line[0] != null) {
                rl = ControladorWS.getInstance().findRelacionLaboralByNumeroEmpleado(line[0]);
                if (rl != null && line[1] != null) {
                    switch (line[1]) {
                        case "ACTIVO":
                        case "activo":
                        case "Activo":
                            rl.setEstatus("1");
                            break;
                        case "INACTIVO":
                        case "inactivo":
                        case "Inactivo":
                            rl.setEstatus("0");
                            break;
                        case "REGISTRADO":
                        case "registrado":
                        case "Registrado":
                            rl.setEstatus("99");
                            break;
                    }
                    isValidate = ControladorWS.getInstance().editRelacionLaboralGenerico(rl);
                } else {
                    System.out.println("Empleado no encontrado: " + line[0]);
                }
            }
        }
        if (isValidate) {
            generarMsg(Util.getFacesMessage(FacesMessage.SEVERITY_INFO, "Exitoso al cambiar estatus"), isValidate);
        } else {
            generarMsg(Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, "Error al cambiar estatus"), isValidate);
        }

    }

    public void fileUploadListenerFechaIngreso(FileUploadEvent event) {
        this.archivo = event.getFile();
        List<String> lista = Util.convertFileToStr(this.archivo);
        boolean isValidate = false;
        RelacionLaboral rl;
        for (String l : lista) {
            String[] line = l.split(",");
            if (line[0] != null) {
                rl = ControladorWS.getInstance().findRelacionLaboralByNumeroEmpleado(line[0]);
                if (rl != null && line[1] != null) {
                    try {
                        if (line[1] != null && !line[2].trim().isEmpty()) {
                            XMLGregorianCalendar fechaIngreso = CeniccoUtil.convertStringToXmlGregorian(line[2]);
                            rl.setFechaingreso(fechaIngreso);
                        } else {
                            System.out.println("Fecha de ingreso inválida o no proporcionada: " + line[2]);
                        }
                        if (line[2] != null && !line[1].trim().isEmpty()) {
                            XMLGregorianCalendar fechaAntiguedad = CeniccoUtil.convertStringToXmlGregorian(line[1]);
                            rl.setFechaantiguedad1(fechaAntiguedad);
                        } else {
                            System.out.println("Fecha de antigüedad inválida o no proporcionada: " + line[1]);
                        }
                        isValidate = ControladorWS.getInstance().editRelacionLaboralGenerico(rl);
                    } catch (Exception e) {
                        System.out.println("Error al procesar las fechas: " + e.getMessage());
                    }
                } else {
                    System.out.println("Empleado no encontrado: " + line[0]);
                }
            }
            if (isValidate) {
                generarMsg(Util.getFacesMessage(FacesMessage.SEVERITY_INFO, "Exitoso al cambiar Fechas"), isValidate);
            } else {
                generarMsg(Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, "Error al cambiar Fechas"), isValidate);
            }
        }
    }

    public void iniciarParametros() {
        mes = null;
        anio = null;
        selectedGrupoPago = null;
    }

    private void generarMsg(FacesMessage msg, boolean isvalidate) {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().addMessage(null, msg);
        context.addCallbackParam("isValidate", isvalidate);
    }

    public void descargarLayoutGlobal(ActionEvent event) {
        List<String> lineas = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        sb.append("NUMERO EMPLEADO,")
                .append("A. PATERNO,")
                .append("A. MATERNO,")
                .append("NOMBRE,")
                .append("SEXO (H o M),")
                .append("ESTADO CIVIL,")
                .append("FECHA NACIMIENTO (dd/mm/yyyy),")
                .append("N.S.S.,")
                .append("RFC,")
                .append("CURP,")
                .append("EMAIL,")
                .append("LUGAR DE NACIMIENTO,")
                .append("CALLE(Opcional),")
                .append("COLONIA(Opcional),")
                .append("MUNICIPIO(Opcional),")
                .append("ESTADO(Opcional),")
                .append("CODIGO POSTAL,")
                .append("NUM. EXT.(Opcional),")
                .append("NUM. INT.(Opcional),")
                .append("FISCAL(SI o NO),")
                .append("ESTATUS,")
                .append("FECHA ANTIGUEDAD,")
                .append("FECHA INGRESO,")
                .append("COMPANIA,")
                .append("GRUPO PAGO,")
                .append("REGISTRO PATRONAL,")
                .append("SISTEMA ANTIGUEDAD,")
                .append("TIPO RELLAB (EVENTUAL - PERMANENTE - OTRO CONTRATO),")
                .append("SD,")
                .append("CENTRO DE COSTOS(Opcional),")
                .append("PUESTO(Opcional),")
                .append("DEPARTAMENTO(Opcional),")
                .append("NUMERO EMPLEADO DEL JEFE DIRECTO(Opcional),")
                .append("BANCO DE DISPERSION,")
                .append("CUENTA BANCARIA,")
                .append("CLABE,")
                .append("BANCO DEL EMPLEADO");
        lineas.add(sb.toString());
        Util.escribirFichero(lineas, "LayoutGlobales", ParametrosReportes.ARCHIVO_CSV);
    }

    public void descargarLayoutGenerales(ActionEvent event) {
        List<String> lineas = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        sb.append("NUMERO EMPLEADO,").append("A. PATERNO,").append("A. MATERNO,").append("NOMBRE,").append("SEXO (H o M),").
                append("ESTADO CIVIL,").append("FECHA NACIMIENTO (dd/mm/yyyy),").append("N.S.S.,").append("RFC,").append("CURP,").append("EMAIL,").append("LUGAR DE NACIMIENTO");
        lineas.add(sb.toString());
        Util.escribirFichero(lineas, "LayoutGenerales", ParametrosReportes.ARCHIVO_CSV);
    }

    public void descargarLayoutRelacionesLaborales(ActionEvent event) {
        List<String> lineas = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        sb.append("NUMEROEMPLEADO,").append("ESTATUS,").append("FECHA ANTIGUEDAD,").append("FECHA INGRESO,").append("COMPANIA,").append("GRUPO PAGO,").append("REGISTRO PATRONAL,")
                .append("SISTEMA ANTIGUEDAD,").append("TIPO RELLAB (EVENTUAL - PERMANENTE - OTRO CONTRATO),").append("SD");
        lineas.add(sb.toString());
        Util.escribirFichero(lineas, "LayoutRelacionesLaborales", ParametrosReportes.ARCHIVO_CSV);
    }

    public void descargarLayoutCuentasBancarias(ActionEvent event) {
        List<String> lineas = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        List<Banco> bancosdispersion = ControladorWS.getInstance().getBancosDispersion();
        List<Banco> bancos = ControladorWS.getInstance().getBancos();
        String bdispersion = "";
        String bempleados = "";
        for (Banco b : bancosdispersion) {
            if (bdispersion.equals("")) {
                bdispersion = bdispersion + b.getNombre();
            } else {
                bdispersion = bdispersion + " | " + b.getNombre();
            }
        }
        for (Banco b : bancos) {
            if (bempleados.equals("")) {
                bempleados = bempleados + b.getNombre();
            } else {
                bempleados = bempleados + " | " + b.getNombre();
            }
        }
        sb.append("NUMEROEMPLEADO,").append("BANCO DE DISPERSION,").append("CUENTA BANCARIA,").append("CLABE,").append("BANCO DEL EMPLEADO");
        lineas.add(sb.toString());
        Util.escribirFichero(lineas, "LayoutCuentasBancarias", ParametrosReportes.ARCHIVO_CSV);
    }

    public void descargarLayoutPosicionesLaborales(ActionEvent event) {
        List<String> lineas = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        sb.append("NUMEROEMPLEADO,").append("CENTRO DE COSTOS,").append("PUESTO,").append("DEPARTAMENTO (OPCIONAL),").append("NUMERO EMPLEADO DEL JEFE DIRECTO (OPCIONAL),").append("TURNOS (OPCIONAL)");
        lineas.add(sb.toString());
        Util.escribirFichero(lineas, "LayoutPosicionesLaborales", ParametrosReportes.ARCHIVO_CSV);
    }

    public void descargarLayoutSueldos(ActionEvent event) {
        List<String> lineas = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        sb.append("NUMEROEMPLEADO,").append("SD,").append("FECHA SD,").append("SDI,").append("FECHA SDI");
        lineas.add(sb.toString());
        Util.escribirFichero(lineas, "LayoutSueldos", ParametrosReportes.ARCHIVO_CSV);
    }

    public void descargarLayoutBajas(ActionEvent event) {
        List<String> lineas = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        sb.append("NUMEROEMPLEADO,").append("FECHA BAJA (dd/mm/yyyy),").append("CAUSA BAJA");
        lineas.add(sb.toString());
        Util.escribirFichero(lineas, "LayoutBajas", ParametrosReportes.ARCHIVO_CSV);
    }

    public void descargarLayoutAcumulados(ActionEvent event) {
        List<String> lineas = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        sb.append("NUMEROEMPLEADO,").append("TIEMPO,").append("BASE GRAVADA,").append("ISR,").append("SUBSIDIO,").append("BASE GRAVADA AGUINALDO,").append("ISR AGUINALDO");
        lineas.add(sb.toString());
        Util.escribirFichero(lineas, "LayoutAcumulados", ParametrosReportes.ARCHIVO_CSV);
    }

    public void descargarLayoutDomicilios(ActionEvent event) {
        List<String> lineas = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        sb.append("NUMEROEMPLEADO,").append("CALLE(Opcional),").append("COLONIA(Opcional),").append("MUNICIPIO(Opcional),").append("ESTADO(Opcional),")
                .append("CODIGO POSTAL,").append("NUM. EXT.(Opcional),").append("NUM. INT.(Opcional),").append("FISCAL(SI o NO)");
        lineas.add(sb.toString());
        Util.escribirFichero(lineas, "LayoutDomicilios", ParametrosReportes.ARCHIVO_CSV);
    }

    public void descargarLayoutCambioEstatus(ActionEvent event) {
        List<String> lineas = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        sb.append("NUMERO EMPLEADO,").append("ESTATUS (ACTIVO-INACTIVO-REGISTRADO)");
        lineas.add(sb.toString());
        Util.escribirFichero(lineas, "LayoutGenerales", ParametrosReportes.ARCHIVO_CSV);
    }

    public void descargarLayoutCambioFechaIngreso(ActionEvent event) {
        List<String> lineas = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        sb.append("NUMERO EMPLEADO,").append("FECHA INGRESO,").append("FECHA ANTIGUEDAD");
        lineas.add(sb.toString());
        Util.escribirFichero(lineas, "LayoutGenerales", ParametrosReportes.ARCHIVO_CSV);
    }

    public UploadedFile getArchivo() {
        return archivo;
    }

    public void setArchivo(UploadedFile archivo) {
        this.archivo = archivo;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public Integer getSelectedGrupoPago() {
        return selectedGrupoPago;
    }

    public void setSelectedGrupoPago(Integer selectedGrupoPago) {
        this.selectedGrupoPago = selectedGrupoPago;
    }

    public String getHide() {
        return hide;
    }

    public void fileUploadListenerGlobal(FileUploadEvent event) {
        archivo = event.getFile();
//        
        boolean isValidate = false;
//        
        List<String> lista = Util.convertFileToStr(this.archivo);
//        
        List<String> mails = new ArrayList<>();
//        
        GlobalesDAO dao = new GlobalesDAO(lista);
//        
        List<Estado> estados = ControladorWS.getInstance().getEstados();
//        
        String error = dao.validarArchivo();
//        
        String estadoNacimiento;
//        
        if (error.equals("")) {
            List<Empleado> empleados = new ArrayList<>();
            for (int i = 0; i < lista.size(); i++) {
                String[] parts = lista.get(i).split(",");
                /**
                 * TODO: Empleado
                 */
                Empleado emp = new Empleado();
                emp.setExpediente(parts[dao.POSICION_NUMERO_EMPLEADO].trim().toUpperCase());
                emp.setApellidopaterno(parts[dao.POSICION_APELLIDO_PATERNO].trim().toUpperCase());
                if (!parts[dao.POSICION_APELLIDO_MATERNO].equals("")) {
                    emp.setApellidomaterno(parts[dao.POSICION_APELLIDO_MATERNO].trim().toUpperCase());
                }
                emp.setNombre(parts[dao.POSICION_NOMBRE].trim().toUpperCase());
                switch (parts[dao.POSICION_SEXO]) {
                    case "h":
                    case "H":
                        emp.setSexo("1");
                        break;
                    case "m":
                    case "M":
                        emp.setSexo("0");
                        break;
                }
                if (!parts[dao.POSICION_ESTADO_CIVIL].equals("")) {
                    switch (parts[dao.POSICION_ESTADO_CIVIL]) {
                        case "SOLTERO":
                        case "soltero":
                            emp.setEstadocivil("1");
                            break;
                        case "CASADO":
                        case "casado":
                            emp.setEstadocivil("2");
                            break;
                        case "DIVORCIADO":
                        case "divorciado":
                            emp.setEstadocivil("3");
                            break;
                        case "UNIONLIBRE":
                        case "unionlibre":
                            emp.setEstadocivil("4");
                            break;
                        case "VIUDO":
                        case "viudo":
                            emp.setEstadocivil("5");
                            break;
                        case "SEPARADO":
                        case "separado":
                            emp.setEstadocivil("6");
                            break;
                    }
                }
                if (!parts[dao.POSICION_CURP].equals("")) {
                    estadoNacimiento = parts[dao.POSICION_CURP].charAt(11) + "" + parts[dao.POSICION_CURP].charAt(12);
                    for (Estado e : estados) {
                        if (e.getEstado().equals(estadoNacimiento)) {
                            emp.setIdestadonacimiento(e.getIdestado());
                            continue;
                        }
                    }
                }
                XMLGregorianCalendar fecha = CeniccoUtil.convertStringToXmlGregorian(parts[dao.POSICION_FECHA_NACIMIENTO]);
                emp.setFechanacimiento(fecha);
                emp.setAfiliacion(parts[dao.POSICION_NSS].trim().toUpperCase());
                emp.setRfc(parts[dao.POSICION_RFC].trim().toUpperCase());
                emp.setCurp(parts[dao.POSICION_CURP].trim().toUpperCase());
                emp.setEstatus(1);
                emp.setIdInformacionComplementaria(null);
//                
                if (!parts[dao.POSICION_LUGAR_NACIMIENTO].equals("")) {
                    emp.setLugarnacimiento(parts[dao.POSICION_LUGAR_NACIMIENTO].trim().toUpperCase());
                }
//
                mails.add(parts[dao.POSICION_EMAIL]);
                empleados.add(emp);
            }
            isValidate = ControladorWS.getInstance().crearEmpleados(empleados, mails);

            if (isValidate) {
                dao.inicializarMapasRelacionesLaborales();
                /**
                 * TODO: Domicilio
                 */
                List<Domicilio> domicilios = new ArrayList<>();
                for (int i = 0; i < lista.size(); i++) {
                    String[] parts = lista.get(i).split(",");
                    RelacionLaboral rellab = dao.getMapaNumeroEmpleados().get(parts[dao.POSICION_NUMERO_EMPLEADO]);
                    Domicilio dom = new Domicilio();
                    dom.setIdempleado(rellab.getIdempleado().getIdempleado());
                    dom.setCalle(parts[dao.POSICION_CALLE]);
                    dom.setColonia(parts[dao.POSICION_COLONIA]);
                    dom.setMunicipio(parts[dao.POSICION_MUNICIPIO]);
                    dom.setEstado(parts[dao.POSICION_ESTADO]);
                    dom.setCp(parts[dao.POSICION_CP]);
                    dom.setNumeroexterior(parts[dao.POSICION_NUMERO_EXTERIOR]);
                    if (parts[dao.POSICION_NUMERO_INTERIOR].equals("0")) {
                        dom.setNumerointerior("");
                    } else {
                        dom.setNumerointerior(parts[dao.POSICION_NUMERO_INTERIOR]);
                    }
                    String fiscal = parts[dao.POSICION_DOMICILIO_FISCAL];
                    if (fiscal.equalsIgnoreCase("SI")) {
                        dom.setFiscal(1);
                    } else {
                        dom.setFiscal(0);
                    }
                    domicilios.add(dom);
                }
                for (Domicilio dom : domicilios) {
                    ControladorWS.getInstance().createDomicilio(dom, dom.getIdempleado());
                }

                /**
                 * TODO: Relacion Laboral
                 */
                List<RelacionLaboral> relaciones = new ArrayList<>();
                for (int i = 0; i < lista.size(); i++) {
                    String[] parts = lista.get(i).split(",");
                    RelacionLaboral rellab = dao.getMapaNumeroEmpleados().get(parts[dao.POSICION_NUMERO_EMPLEADO]);
                    switch (parts[dao.POSICION_ESTATUS]) {
                        case "ACTIVO":
                        case "activo":
                            rellab.setEstatus("1");
                            break;
                        case "INACTIVO":
                        case "inactivo":
                            rellab.setEstatus("0");
                            break;
                        case "REGISTRADO":
                        case "registrado":
                            rellab.setEstatus("99");
                            break;
                    }
                    XMLGregorianCalendar fechaantiguedad = CeniccoUtil.convertStringToXmlGregorian(parts[dao.POSICION_FECHA_ANTIGUEDAD]);
                    rellab.setFechaantiguedad1(fechaantiguedad);
                    XMLGregorianCalendar fechaingreso = CeniccoUtil.convertStringToXmlGregorian(parts[dao.POSICION_FECHA_INGRESO]);
                    rellab.setFechaingreso(fechaingreso);
                    rellab.setFechaSalarioDiario(fechaingreso);
                    rellab.setFechaSalarioDiarioIntegrado(fechaingreso);
                    String claveCompania = parts[dao.POSICION_COMPANIA].trim();
                    if (dao.getMapaCompanias().containsKey(claveCompania)) {
                        rellab.setIdcompania(dao.getMapaCompanias().get(claveCompania));
                    }
                    rellab.setIdgrupopago(dao.getMapaGrupoPago().get(parts[dao.POSICION_GRUPO_PAGO]));
                    rellab.setIdregistropatronal(dao.getMapaRegistroPatronal().get(parts[dao.POSICION_REGISTRO_PATRONAL]));
                    rellab.setIdsistemaantiguedad(dao.getMapaSistemaAntiguedad().get(parts[dao.POSICION_SISTEMA_ANTIGUEDAD]));
                    rellab.setIdtiporellab(dao.getMapaTipoRelacionLaboral().get(parts[dao.POSICION_TIPO_RELLAB]));
                    rellab.setSalarioDiario(Double.parseDouble(parts[dao.POSICION_SD]));
                    rellab.setIdTipoSalarioIdse(dao.getMapaTipoSueldoIdse().get("2"));//                
                    relaciones.add(rellab);
                }
                ControladorWS.getInstance().editarRelacionLaborales(relaciones);

                /**
                 * TODO: Posicion Laboral
                 */
                List<RelacionLaboralPosicion> posiciones = new ArrayList<>();
                for (int i = 0; i < lista.size(); i++) {
                    String[] parts = lista.get(i).split(",");
                    RelacionLaboralPosicion posicion = dao.getMapaNumeroEmpleados().get(parts[dao.POSICION_NUMERO_EMPLEADO]).getIdrelacionlaboralposicion();
                    try {
                        if (!parts[dao.POSICION_CCO].equals("")) {
                            posicion.setIdcentrocosto(dao.getCCo(parts));
                        }

                    } catch (Exception e) {
                        if (posicion.getIdcentrocosto() != null && posicion.getIdcentrocosto().getIdCentroCostos() == null) {
                            posicion.setIdcentrocosto(null);
                        }
                    }
                    try {
                        if (!parts[dao.POSICION_PUESTO].equals("")) {
                            posicion.setIdpuesto(dao.getPuesto(parts));
                        }
                    } catch (Exception e) {
                    }
                    if (posicion.getIdpuesto() != null && posicion.getIdpuesto().getIdpuesto() == null) {
                        posicion.setIdpuesto(null);
                    }

                    try {
                        if (!parts[dao.POSICION_DEPARTAMENTO].equals("")) {
                            posicion.setIddepartamento(dao.getDepartamento(parts));
                        }
                    } catch (Exception e) {
                        if (posicion.getIddepartamento() != null && posicion.getIddepartamento().getIddepartamento() == null) {
                            posicion.setIddepartamento(null);
                        }
                    }
                    try {
                        if (!parts[dao.POSICION_JEFE_DIRECTO].equals("")) {
                            Empleado emp = dao.getJefeDirecto(parts).getIdempleado();
                            posicion.setIdrepresentante(emp.getIdempleado());
                        }
                    } catch (Exception e) {
                    }
                    posicion.setIdturno(null);
                    if (posicion.getGrupopago() != null && posicion.getGrupopago().getIdgrupopago() == null) {
                        posicion.setGrupopago(null);
                    }
                    if (posicion.getSubarea() != null && posicion.getSubarea().getIdsubarea() == null) {
                        posicion.setSubarea(null);
                    }
                    if (posicion.getArea() != null && posicion.getArea().getIdarea() == null) {
                        posicion.setArea(null);
                    }
                    posiciones.add(posicion);
                }
                ControladorWS.getInstance().editarPosiciones(posiciones);

                /**
                 * TODO: Cuenta Bancaria
                 */
                List<CuentaBancaria> cuentas = new ArrayList<>();
                for (int i = 0; i < lista.size(); i++) {
                    String[] parts = lista.get(i).split(",");
                    RelacionLaboral rellab = dao.getMapaNumeroEmpleados().get(parts[dao.POSICION_NUMERO_EMPLEADO]);
//                
                    Banco banco = dao.getMapaBancos().get(parts[dao.POSICION_BANCO]);
                    Banco bancoempleado = dao.getMapaBancos().get(parts[dao.POSICION_BANCO_EMPLEADO]);
//            
                    CuentaBancaria cta = new CuentaBancaria();
                    cta.setIdrellab(rellab);
                    cta.setIdbanco(banco);
                    cta.setClabe(parts[dao.POSICION_CLABE_BANCARIA]);
                    cta.setCuenta(parts[dao.POSICION_CUENTA_BANCARIA]);
                    cta.setIdbancoempleado(bancoempleado);
                    cta.setEstatus(1);
                    cuentas.add(cta);
                }
                ControladorWS.getInstance().crearCuentasBancarias(cuentas);
            }
        }
        if (error.equals("") && isValidate) {
            generarMsg(Util.getFacesMessage(FacesMessage.SEVERITY_INFO, "Exitoso al crear los empleados!"), isValidate);
        } else {
            generarMsg(Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, "Error al crear los empleados: " + error), isValidate);
        }
    }
}
