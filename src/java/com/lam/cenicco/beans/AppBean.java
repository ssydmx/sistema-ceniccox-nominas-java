/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lam.cenicco.beans;

import com.lam.cenicco.entidades.Compania;
import com.lam.cenicco.librerias.mensajes.Mensajes;
import com.lam.cenicco.util.ControladorSesiones;
import com.lam.cenicco.util.paths.MyPaths;
import com.lam.cenicco.util.Parametros;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.model.SelectItem;
import javax.inject.Named;

/**
 *
 * @author JoséAntonio
 */
@Named(value = "appBean")
@ApplicationScoped
public class AppBean {

    public String getSesionMin() {
        ControladorSesiones.getInstance().setTimeSessionMin();
        return "";
    }

    public String getSesionMax() {
        ControladorSesiones.getInstance().setTimeSessionMax();
        return "";
    }

    public Compania getCompania() {
        return ControladorSesiones.getInstance().getCompaniaSession();
    }

    public String getNombreCortoCompania() {
        return ControladorSesiones.getInstance().getCompaniaSession().getNombreCorto();
    }
//

    public String getBasePath() {
        return MyPaths.basepath();
    }

// URL ESTADOS
    public String getUrlEstadoActivo() {
        return MyPaths.URL_ESTADO_ACTIVO;
    }

    public String getUrlEstadoInactivo() {
        return MyPaths.URL_ESTADO_INACTIVO;
    }

    public String getUrlEstadoCancelado() {
        return MyPaths.URL_ESTADO_CANCELADO;
    }
//    

    public String getClaveNaturaleza() {
        return Parametros.CLAVE_NATURALEZA;
    }

//  CAMPOS LOGIN  
    public String getMensajeErrorUsuario() {
        return Mensajes.CAMPO_VACIO_USERNAME;
    }

    public String getMensajeErrorPassword() {
        return Mensajes.CAMPO_VACIO_PASSWORD;
    }

    public String getMensajeErrorServicio() {
        return Mensajes.CAMPO_VACIO_SERVICIO;
    }

//  CONTEO TIMBRES
    public String getMensajeNumeroCreditos() {
        return Parametros.MENSAJE_NUMERO_CREDITOS_NO_RESPONDE;
    }

//  RECIBOS
    public String getMensajeErrorClaseNomina() {
        return Mensajes.CAMPO_VACIO_CLASE_NOMINA;
    }

    public String getMensajeErrorTipoNomina() {
        return Mensajes.CAMPO_VACIO_TIPO_NOMINA;
    }

    public String getMensajeErrorAnioNomina() {
        return Mensajes.CAMPO_VACIO_ANIO;
    }

    public String getMensajeErrorPeriodoMesNomina() {
        return Mensajes.CAMPO_VACIO_PERIODO_MES;
    }

    public String getMensajeErrorFechaPagoNomina() {
        return Mensajes.CAMPO_VACIO_FECHA_PAGO;
    }

//  ADMINISTRADOR USUARIOS
    public String getMensajeErrorNombre() {
        return Mensajes.CAMPO_VACIO_NOMBRE;
    }

    public String getMensajeErrorApellidoPaterno() {
        return Mensajes.CAMPO_VACIO_APELLIDO_PATERNO;
    }

    public String getMensajeErrorApellidoMaterno() {
        return Mensajes.CAMPO_VACIO_APELLIDO_MATERNO;
    }

    public String getMensajeErrorPerfil() {
        return Mensajes.CAMPO_VACIO_PERFIL;
    }

    public String getMensajeErrorEmail() {
        return Mensajes.CAMPO_VACIO_EMAIL;
    }

//   KIOSKO
    public String getMensajeErrorRFC() {
        return Mensajes.CAMPO_VACIO_RFC;
    }

    public String getMensajeErrorNumeroEmpleado() {
        return Mensajes.CAMPO_VACIO_NUMERO_EMPLEADO;
    }

//  ADMINISTRADOR EMPLEADOS
    public String getMensajeErrorExpediente() {
        return Mensajes.CAMPO_VACIO_EXPEDIENTE;
    }

    public String getMensajeErrorEstado() {
        return Mensajes.CAMPO_VACIO_ESTADO;
    }

    public String getMensajeErrorFechaNacimiento() {
        return Mensajes.CAMPO_VACIO_FECHA_NACIMIENTO;
    }

    public String getMensajeErrorSexo() {
        return Mensajes.CAMPO_VACIO_SEXO;
    }

    public String getMensajeErrorEstadoCivil() {
        return Mensajes.CAMPO_VACIO_ESTADO_CIVIL;
    }

    public String getMensajeErrorCurp() {
        return Mensajes.CAMPO_VACIO_CURP;
    }

    public String getMensajeErrorAfiliacion() {
        return Mensajes.CAMPO_VACIO_AFILIACION;
    }

    public String getMensajeErrorEstatus() {
        return Mensajes.CAMPO_VACIO_ESTATUS;
    }

    public String getMensajeErrorTipoRelacionLaboral() {
        return Mensajes.CAMPO_VACIO_TIPO_RELACION_LABORAL;
    }

    public String getMensajeErrorCompania() {
        return Mensajes.CAMPO_VACIO_COMPANIA;
    }

    public String getMensajeErrorRegistroPatronal() {
        return Mensajes.CAMPO_VACIO_REGISTRO_PATRONAL;
    }

    public String getMensajeErrorGrupoPago() {
        return Mensajes.CAMPO_VACIO_GRUPO_PAGO;
    }

    public String getMensajeErrorFechaIngreso() {
        return Mensajes.CAMPO_VACIO_FECHA_INGRESO;
    }

    public String getMensajeErrorSistemaAntiguedad() {
        return Mensajes.CAMPO_VACIO_SISTEMA_ANTIGUEDAD;
    }

    public String getMensajeErrorFechaAntiguedadUno() {
        return Mensajes.CAMPO_VACIO_FECHA_ANTIGUEDAD_UNO;
    }

    public String getMensajeErrorTipoContrato() {
        return Mensajes.CAMPO_VACIO_TIPO_CONTRATO;
    }

    public String getMensajeErrorFechaInicioContrato() {
        return Mensajes.CAMPO_VACIO_FECHA_INICIO_CONTRATO;
    }

    public String getMensajeErrorCodigoPostal() {
        return Mensajes.CAMPO_VACIO_CODIGO_POSTAL;
    }

    public String getMensajeErrorNumeroExterior() {
        return Mensajes.CAMPO_VACIO_NUMERO_EXTERIOR;
    }

    public String getMensajeErrorAsentamiento() {
        return Mensajes.CAMPO_VACIO_ASENTAMIENTO;
    }

    public String getMensajeErrorCalle() {
        return Mensajes.CAMPO_VACIO_CALLE;
    }

    public String getMensajeErrorMunicipio() {
        return Mensajes.CAMPO_VACIO_MUNICIPIO;
    }

    public String getMensajeErrorCiudad() {
        return Mensajes.CAMPO_VACIO_CIUDAD;
    }

    public String getMensajeErrorTipoDocumento() {
        return Mensajes.CAMPO_VACIO_TIPO_DOCUMENTO;
    }

    public String getMensajeTablaVacia() {
        return Mensajes.MENSAJE_TABLA_VACIA;
    }

    public String getMensajeTablaVaciaConceptos() {
        return Mensajes.MENSAJE_TABLA_VACIA_CONCEPTOS;
    }

    public String getMensajeErrorConcepto() {
        return Mensajes.CAMPO_VACIO_CONCEPTO;
    }

    public String getMensajeErrorNombreConcepto() {
        return Mensajes.CAMPO_VACIO_NOMBRE_CONCEPTO;
    }

    public String getMensajeErrorNombreCortoConcepto() {
        return Mensajes.CAMPO_VACIO_NOMBRE_CORTO_CONCEPTO;
    }

    public String getMensajeErrorTipoConcepto() {
        return Mensajes.CAMPO_VACIO_TIPO_CONCEPTO;
    }

    public String getMensajeErrorUnidadConcepto() {
        return Mensajes.CAMPO_VACIO_UNIDAD;
    }

//  MODULO COMPANIAS
    public String getMensajeErrorNombreCompania() {
        return Mensajes.CAMPO_VACIO_NOMBRE_COMPANIA;
    }

    public String getMensajeErrorClaveCompania() {
        return Mensajes.CAMPO_VACIO_CLAVE;
    }

    public String getMensajeErrorRFCCompania() {
        return Mensajes.CAMPO_VACIO_RFC_COMPANIA;
    }

    public String getMensajeErrorReciboCompania() {
        return Mensajes.CAMPO_VACIO_RECIBO;
    }

    public String getMensajeErrorLogoCompania() {
        return Mensajes.CAMPO_VACIO_LOGO;
    }

    public String getMensajeErrorTimbreCompania() {
        return Mensajes.CAMPO_VACIO_TIMBRE;
    }

    public String getMensajeErrorProcesadosCompania() {
        return Mensajes.CAMPO_VACIO_PROCESADOS;
    }

//  MODULO REGISTRO PATRONAL
    public String getMensajeErrorNombreRegistroPatronal() {
        return Mensajes.CAMPO_VACIO_NOMRE_REGISTRO_PATRONAL;
    }

    public String getMensajeErrorComentarioRegistroPatronal() {
        return Mensajes.CAMPO_VACIO_COMENTARIO_REGISTRO_PATRONAL;
    }

    public String getMensajeErrorPrincipalRegistroPatronal() {
        return Mensajes.CAMPO_VACIO_PRINCIPAL_REGISTRO_PATRONAL;
    }
//  MODULO CATALOGO GENERAL

    public String getMensajeErrorColumna() {
        return Mensajes.CAMPO_VACIO_COLUMNA;
    }

    public String getMensajeErrorTabla() {
        return Mensajes.CAMPO_VACIO_TABLA;
    }

    public String getMensajeErrorValor() {
        return Mensajes.CAMPO_VACIO_VALOR;
    }

//  MODULO GRUPO DE PAGO
    public String getMensajeErrorGrupoPagoNombre() {
        return Mensajes.CAMPO_VACIO_GRUPO_CONCEPTO;
    }

//  MODULO GRUPO CONCEPTO
    public String getMensajeErrorSigno() {
        return Mensajes.CAMPO_VACIO_SIGNO;
    }

    public String getMensajeErrorGrupoConcepto() {
        return Mensajes.CAMPO_VACIO_GRUPO_CONCEPTO;
    }

//  MODULO PAISES
    public String getMensajeErrorNombrePais() {
        return Mensajes.CAMPO_VACIO_NOMBRE_PAIS;
    }

    public String getMensajeErrorNombreEstado() {
        return Mensajes.CAMPO_VACIO_NOMBRE_ESTADO;
    }

//MODULO MUNICIPIOS
    public String getMensajeErrorNombreMunicipio() {
        return Mensajes.CAMPO_VACIO_NOMBRE_MUNICIPIO;
    }

//  MODULO CIUDADES
    public String getMensajeErrorNombreCiudad() {
        return Mensajes.CAMPO_VACIO_NOMBRE_CIUDAD;
    }

//  MODULO PENSIONES
    public String getMensajeErrorBanco() {
        return Mensajes.CAMPO_VACIO_BANCO;
    }

    public String getMensajeErrorRelacionLaboral() {
        return Mensajes.CAMPO_VACIO_RELACION_LABORAL;
    }

    public String getMensajeErrorOficio() {
        return Mensajes.CAMPO_VACIO_OFICIO;
    }

    public String getMensajeErrorCuenta() {
        return Mensajes.CAMPO_VACIO_CUENTA;
    }

    public String getMensajeErrorTipoCalculo() {
        return Mensajes.CAMPO_VACIO_TIPO_CALCULO;
    }

    public String getMensajeErrorFormaPago() {
        return Mensajes.CAMPO_VACIO_FORMA_PAGO;
    }

    public String getMensajeErrorClabe() {
        return Mensajes.CAMPO_VACIO_CLABE;
    }

    public String getMensajeErrorJuez() {
        return Mensajes.CAMPO_VACIO_JUEZ;
    }

//  MODULO CONTACTOS
    public String getMensajeErrorTipoContacto() {
        return Mensajes.CAMPO_VACIO_TIPO_CONTACTO;
    }

    public String getMensajeErrorValorTipoContacto() {
        return Mensajes.CAMPO_VACIO_VALOR_TIPO_CONTACTO;
    }

    public String getMensajeSeleccione() {
        return Mensajes.COMBO_SELECCIONE;
    }

//  MODULO CUENTAS BANCARIAS
    public String getMensajeErrorPorcentaje() {
        return Mensajes.CAMPO_VACIO_PORCENTAJE;
    }

    public String getMensajeErrorProrrateo() {
        return Mensajes.CAMPO_VACIO_PRORRATEO;
    }

    public String getMensajeErrorTipoCuenta() {
        return Mensajes.CAMPO_VACIO_TIPO_CUENTA;
    }

//  MODULO TIPO RELACIÓN LABORAL
    public String getMensajeErrorNombreTipoRelacionLaboral() {
        return Mensajes.CAMPO_VACIO_NOMBRE_TIPO_RELACION_LABORAL;
    }

//  MODULO TIPO DE DOCUMENTO
    public String getMensajeErrorNombreTipoDocumento() {
        return Mensajes.CAMPO_VACIO_NOMBRE_TIPO_DOCUMENTO;
    }

//  MODULO TIPO DE CONTACTO
    public String getMensajeErrorNombreTipoContacto() {
        return Mensajes.CAMPO_VACIO_NOMBRE_TIPO_CONTACTO;
    }

    public String getMensajeErrorPersonalTipoContacto() {
        return Mensajes.CAMPO_VACIO_PERSONAL_TIPO_CONTACTO;
    }

//  MODULO TURNO    
    public String getMensajeErrorTurno() {
        return Mensajes.CAMPO_VACIO_TURNO;
    }

    public String getMensajeErrorNombreTurno() {
        return Mensajes.CAMPO_VACIO_NOMBRE_TURNO;
    }

//  MODULO POSICIONES
    public String getMensajeErrorFechaInicio() {
        return Mensajes.CAMPO_VACIO_FECHA_INICIO;
    }

    public String getMensajeErrorDepartamento() {
        return Mensajes.CAMPO_VACIO_DEPARTAMENTO;
    }

    public String getMensajeErrorPuesto() {
        return Mensajes.CAMPO_VACIO_PUESTO;
    }

    public String getMensajeErrorNivel() {
        return Mensajes.CAMPO_VACIO_NIVEL;
    }

    public String getMensajeErrorSistemaHorario() {
        return Mensajes.CAMPO_VACIO_SISTEMA_HORARIO;
    }

    public String getMensajeErrorJornadaLaboral() {
        return Mensajes.CAMPO_VACIO_JORNADA_LABORAL;
    }

    public String getMensajeErrorTurnoPosiciones() {
        return Mensajes.CAMPO_VACIO_TURNO_POSICIONES;
    }

    public String getMensajeErrorTipoSalario() {
        return Mensajes.CAMPO_VACIO_TIPO_SALARIO;
    }

    public String getMensajeErrorSindicalizado() {
        return Mensajes.CAMPO_VACIO_SINDICALIZADO;
    }

    public String getMensajeErrorNumeroCredito() {
        return Mensajes.CAMPO_VACIO_NUMERO_CREDITO;
    }

    public String getMensajeErrorTipoDescuento() {
        return Mensajes.CAMPO_VACIO_TIPO_DESCUENTO;
    }

    public String getMensajeErrorFechaVigencia() {
        return Mensajes.CAMPO_VACIO_FECHA_VIGENCIA;
    }

    public String getMensajeErrorZonaEconomica() {
        return Mensajes.CAMPO_VACIO_ZONA_ECONOMICA;
    }

//  MODULO POSICION SUELDOS
    public String getMensajeErrorTipoSueldo() {
        return Mensajes.CAMPO_VACIO_TIPO_SUELDO;
    }

    public String getMensajeErrorImporte() {
        return Mensajes.CAMPO_VACIO_IMPORTE;
    }

//  MODULO INCIDENCIAS
    public String getMensajeErrorEmpleado() {
        return Mensajes.CAMPO_VACIO_EMPLEADO;
    }

    public String getMensajeErrorDescuento() {
        return Mensajes.CAMPO_VACIO_DESCUENTO;
    }
//  MODULO CATALOGO CREDITOS

    public String getMensajeErrorAntiguedadMinima() {
        return Mensajes.CAMPO_VACIO_ANTIGUEDAD_MINIMA;
    }

    public String getMensajeErrorPlazoMinimo() {
        return Mensajes.CAMPO_VACIO_PLAZO_MINIMA;
    }

    public String getMensajeErrorPlazoMaximo() {
        return Mensajes.CAMPO_VACIO_PLAZO_MAXIMO;
    }

    public String getMensajeErrorTasaInteresMinimo() {
        return Mensajes.CAMPO_VACIO_TASA_INTERES_MINIMO;
    }

    public String getMensajeErrorTasaInteresMaximo() {
        return Mensajes.CAMPO_VACIO_TASA_INTERES_MAXIMO;
    }

    public String getMensajeErrorMontoMinimo() {
        return Mensajes.CAMPO_VACIO_MONTO_MINIMO;
    }

    public String getMensajeErrorMontoMaximo() {
        return Mensajes.CAMPO_VACIO_MONTO_MAXIMO;
    }

    public String getMensajeErrorDescuentoMinimo() {
        return Mensajes.CAMPO_VACIO_DESCUENTO_MINIMO;
    }

    public String getMensajeErrorDescuentoMaximo() {
        return Mensajes.CAMPO_VACIO_DESCUENTO_MAXIMO;
    }

    public String getMensajeErrorPagosAnticipados() {
        return Mensajes.CAMPO_VACIO_PAGOS_ANTICIPADOS;
    }

    public String getMensajeErrorMaximoCreditos() {
        return Mensajes.CAMPO_VACIO_MAXIMO_CREDITOS;
    }

//  MODULO PERIODO
    public String getMensajeErrorPeriodo() {
        return Mensajes.CAMPO_VACIO_PERIODO;
    }

    public String getMensajeErrorAnioMes() {
        return Mensajes.CAMPO_VACIO_ANIO_MES;
    }

//   
    public String getMensajeErrorFechaFin() {
        return Mensajes.CAMPO_VACIO_FECHA_FIN;
    }

    public String getMensajeErrorMes() {
        return Mensajes.CAMPO_VACIO_NOMBRE_MES;
    }

    public String getMensajeErrorBimestre() {
        return Mensajes.CAMPO_VACIO_BIMESTRE;
    }

//  MODULO TIPO PROCESO
    public String getMensajeErrorTipoProceso() {
        return Mensajes.CAMPO_VACIO_TIPO_PROCESO;
    }

    public String getMensajeErrorNombreTiProceso() {
        return Mensajes.CAMPO_VACIO_NOMBRE_TIPO_PROCESO;
    }

    public String getMensajeErrorPeriodicidad() {
        return Mensajes.CAMPO_VACIO_PERIODICIDAD;
    }

    public String getMensajeErrorDias() {
        return Mensajes.CAMPO_VACIO_DIAS;
    }

//  MODULO TIPO SUELDO
    public String getMensajeErrorNombreTipoSueldo() {
        return Mensajes.CAMPO_VACIO_NOMBRE_TIPO_SUELDO;
    }

    public String getMensajesErrorPeriodicidad() {
        return Mensajes.CAMPO_VACIO_PERIODICIDAD_TIPO_SUELDO;
    }

    public String getMensajesErrorTipoSueldo() {
        return Mensajes.CAMPO_VACIO_TIPO_SUELDO_STR;
    }

//  MODULO DEPARTAMENTO
    public String getMensajeErrorDepartamentoStr() {
        return Mensajes.CAMPO_VACIO_DEPARTAMENTO_STR;
    }

    public String getMensajeErrorNombreDepartamento() {
        return Mensajes.CAMPO_VACIO_NOMBRE_DEPARTAMENTO;
    }

//  MODULO PUESTO
    public String getMensajeErrorNombrePuesto() {
        return Mensajes.CAMPO_VACIO_NOMBRE_PUESTO;
    }

    public String getMensajeErrorPlazas() {
        return Mensajes.CAMPO_VACIO_PLAZAS;
    }

    public String getMensajeErrorPlazasDisponibles() {
        return Mensajes.CAMPO_VACIO_PLAZAS_DISPONIBLES;
    }

    public String getMensajeErrorPlazasReservadas() {
        return Mensajes.CAMPO_VACIO_PLAZAS_RESERVADAS;
    }

    public String getMensajeErrorPlazasOcupadas() {
        return Mensajes.CAMPO_VACIO_PLAZAS_OCUPADAS;
    }

    public String getMensajeErrorPuestoStr() {
        return Mensajes.CAMPO_VACIO_PUESTO_STR;
    }
//  MODULO SOLICITUD CREDITOS

    public String getMensajeErrorTipoCredito() {
        return Mensajes.CAMPO_VACIO_TIPO_CREDITO;
    }

    public String getMensajeErrorTipoDisposicion() {
        return Mensajes.CAMPO_VACIO_TIPO_DISPOSICION;
    }

    public String getMensajeErrorFechaCredito() {
        return Mensajes.CAMPO_VACIO_FECHA_CREDITO;
    }

    public String getMensajeErrorFechaPrimerVencimiento() {
        return Mensajes.CAMPO_VACIO_FECHA_PRIMER_VENCIMIENTO;
    }

    public String getMensajeErrorMontoCredito() {
        return Mensajes.CAMPO_VACIO_MONTO_CREDITO;
    }

    public String getMensajeErrorPeriodos() {
        return Mensajes.CAMPO_VACIO_PERIODOS;
    }

//FÓRMULAS
    public String getMensajeEtiquetaCalculo() {
        return Mensajes.ETIQUETA_CAMPO_CALCULO_FORMULA;
    }

    public String getMensajeEtiquetaFormula() {
        return Mensajes.ETIQUETA_CAMPO_SEARCH_FORMULA;
    }

    public String getMensajeErrorCompaniaFormula() {
        return Mensajes.CAMPO_VACIO_COMPANIA_FORMULA;
    }

    public String getMensajeErrorCalculo() {
        return Mensajes.CAMPO_VACIO_CALCULO;
    }

    public String getMensajeErrorPrioridad() {
        return Mensajes.CAMPO_VACIO_PRIORIDAD;
    }

    public String getMensajeErrorFormula() {
        return Mensajes.CAMPO_VACIO_FORMULA;
    }

    public String getMensajeErrorNaturalezaConcepto() {
        return Mensajes.CAMPO_VACIO_NATURALEZA;
    }

    public String getMensajeErrorPorcenVsmgz() {
        return Mensajes.CAMPO_VACIO_PORCENVSMGZ;
    }
//  MODULO FINIQUITOS

    public String getMensajeErrorSeleccionEmpleados() {
        return Mensajes.ERROR_REGSITROS_VACIOS_SOLICITUD_VACACIONES;
    }

    public String getMensajeErrorVacioMes() {
        return Mensajes.CAMPO_VACIO_MES;
    }

    public String getMensajeErrorTipoFiniquito() {
        return Mensajes.CAMPO_VACIO_FINIQUITO;
    }

    public String getMensajeErrorCausaBaja() {
        return Mensajes.CAMPO_VACIO_CAUSA_BAJA;
    }

    public String getMensajeErrorPeriodoGeneral() {
        return Mensajes.CAMPO_VACIO_GRAL_PERIODO;
    }

    public List<SelectItem> getComboModulos() {
        List<SelectItem> combo = new ArrayList<>();
        combo.add(new SelectItem("RELACION LABORAL", "RELACION LABORAL"));
        combo.add(new SelectItem("POSICION LABORAL", "POSICION LABORAL"));
        combo.add(new SelectItem("INCIDENCIAS", "INCIDENCIAS"));
        return combo;
    }

    public List<SelectItem> getComboAuditoria() {
        List<SelectItem> combo = new ArrayList<>();
        combo.add(new SelectItem("NUEVO", "NUEVO"));
        combo.add(new SelectItem("ACTUALIZA", "ACTUALIZA"));
        combo.add(new SelectItem("ELIMINA", "ELIMINA"));
        return combo;
    }

    public List<SelectItem> getComboTipoMovimiento() {
        List<SelectItem> combo = new ArrayList<>();
        combo.add(new SelectItem("AREA", "AREA"));
        combo.add(new SelectItem("CENTRO COSTOS", "CENTRO COSTOS"));
        combo.add(new SelectItem("COMPANIA", "COMPANIA"));
        combo.add(new SelectItem("DEPARTAMENTO", "DEPARTAMENTO"));
        combo.add(new SelectItem("GRUPO PAGO", "GRUPO PAGO"));
        combo.add(new SelectItem("PUESTO", "PUESTO"));
        combo.add(new SelectItem("REGISTRO PATRONAL", "REGISTRO PATRONAL"));
        combo.add(new SelectItem("SISTEMA ANTIGUEDAD", "SISTEMA ANTIGUEDAD"));
        return combo;
    }

    public List<SelectItem> getComboSecuencia() {
        List<SelectItem> combo = new ArrayList<>();
        combo.add(new SelectItem("01", "01"));
        combo.add(new SelectItem("02", "02"));
        combo.add(new SelectItem("03", "03"));
        combo.add(new SelectItem("04", "04"));
        combo.add(new SelectItem("05", "05"));
        combo.add(new SelectItem("06", "06"));
        combo.add(new SelectItem("07", "07"));
        combo.add(new SelectItem("08", "08"));
        combo.add(new SelectItem("09", "09"));
        combo.add(new SelectItem("10", "10"));
        combo.add(new SelectItem("11", "11"));
        combo.add(new SelectItem("12", "12"));
        combo.add(new SelectItem("13", "13"));
        combo.add(new SelectItem("14", "14"));
        combo.add(new SelectItem("15", "15"));
        combo.add(new SelectItem("16", "16"));
        combo.add(new SelectItem("17", "17"));
        combo.add(new SelectItem("18", "18"));
        combo.add(new SelectItem("19", "19"));
        combo.add(new SelectItem("20", "20"));
        return combo;
    }

    public List<SelectItem> getComboSigno() {
        List<SelectItem> combo = new ArrayList<>();
        combo.add(new SelectItem("", "N/A"));
        combo.add(new SelectItem("+", "(+)"));
        combo.add(new SelectItem("-", "(-)"));
        return combo;
    }

    public List<SelectItem> getComboSeleccion() {
        List<SelectItem> combo = new ArrayList<>();
        combo.add(new SelectItem(0, "No"));
        combo.add(new SelectItem(1, "Sí"));
        return combo;
    }

    public List<SelectItem> getComboEstatus() {
        List<SelectItem> combo = new ArrayList<>();
        combo.add(new SelectItem(1, "Activo"));
        combo.add(new SelectItem(0, "Inactivo"));
        return combo;
    }

    public List<SelectItem> getComboEstatusRecibos() {
        List<SelectItem> combo = new ArrayList<>();
        combo.add(new SelectItem(1, "Activo"));
        combo.add(new SelectItem(0, "Inactivo"));
        combo.add(new SelectItem(2, "Cancelado"));
        return combo;
    }

    public List<SelectItem> getComboEstatusJDE() {
        List<SelectItem> combo = new ArrayList<>();
        combo.add(new SelectItem("1", "Activo"));
        combo.add(new SelectItem("2", "Inactivo"));
        return combo;
    }

    public List<SelectItem> getComboEstatusSolicitudVacaciones() {
        List<SelectItem> combo = new ArrayList<>();
        combo.add(new SelectItem(0, "Cancelado"));
        combo.add(new SelectItem(1, "Autorizado"));
        combo.add(new SelectItem(99, "Solicitado"));
        return combo;
    }

    public List<SelectItem> getComboEstatusEmpleado() {
        List<SelectItem> combo = new ArrayList<>();
        combo.add(new SelectItem(1, "Activo"));
        combo.add(new SelectItem(0, "Inactivo"));
        combo.add(new SelectItem(99, "Registrado"));
        combo.add(new SelectItem(-1, "Suspendido"));

        return combo;
    }

    public List<SelectItem> getComboEstatusRelacionLaboral() {
        List<SelectItem> combo = new ArrayList<>();
        combo.add(new SelectItem(1, "Activos"));
        combo.add(new SelectItem(0, "Inactivos"));
        combo.add(new SelectItem(-1, "Suspendidos"));
        return combo;
    }

    public List<SelectItem> getComboTipoCreditos() {
        List<SelectItem> combo = new ArrayList<>();
        combo.add(new SelectItem(1, "Créditos Simples"));
        combo.add(new SelectItem(2, "Créditos Saldos Insolútos"));
        combo.add(new SelectItem(3, "Créditos Interés Fijo"));
        return combo;
    }

    public List<SelectItem> getComboEstatusCreditos() {
        List<SelectItem> combo = new ArrayList<>();
        combo.add(new SelectItem(1, "Activo"));
        combo.add(new SelectItem(2, "Registrado"));
        combo.add(new SelectItem(5, "Cancelado"));
        combo.add(new SelectItem(6, "Liquidado"));
        combo.add(new SelectItem(7, "Re-Estructurado"));
        return combo;
    }

    public List<SelectItem> getComboEstatusCreditosEdit() {
        List<SelectItem> combo = new ArrayList<>();
        combo.add(new SelectItem(1, "Activo"));
        combo.add(new SelectItem(2, "Registrado"));
        combo.add(new SelectItem(5, "Cancelado"));
        return combo;
    }

    public List<SelectItem> getComboEstatusNomina() {
        List<SelectItem> combo = new ArrayList<>();
        combo.add(new SelectItem(0, "Abierto"));
        combo.add(new SelectItem(1, "Cerrado"));
        return combo;
    }

    public List<SelectItem> getComboEstatusFiniquitos() {
        List<SelectItem> combo = new ArrayList<>();
        combo.add(new SelectItem(1, "Abierto"));
        combo.add(new SelectItem(2, "Activo"));
        combo.add(new SelectItem(3, "Cerrado"));
        return combo;
    }

    public List<SelectItem> getComboTamanioCompania() {
        List<SelectItem> combo = new ArrayList<>();
        combo.add(new SelectItem(1, "Pequeño"));
        combo.add(new SelectItem(2, "Mediano"));
        combo.add(new SelectItem(3, "Grande"));
        return combo;
    }

    public List<SelectItem> getComboAmbito() {
        List<SelectItem> combo = new ArrayList<>();
        combo.add(new SelectItem(1, "Local"));
        combo.add(new SelectItem(2, "Regional"));
        combo.add(new SelectItem(3, "Nacional"));
        combo.add(new SelectItem(4, "Multinacional"));
        return combo;
    }

    public List<SelectItem> getComboTipoCalculoPensiones() {
        List<SelectItem> combo = new ArrayList<>();
        combo.add(new SelectItem(0, "Desactivar"));
        combo.add(new SelectItem(1, "% Sobre Conceptos"));
        combo.add(new SelectItem(2, "% Sobre Ingresos Netos"));
        combo.add(new SelectItem(4, "% Sobre Ingresos Brutos"));
        combo.add(new SelectItem(3, "Cuota Fija"));
        return combo;
    }

    public List<SelectItem> getComboTiempoEjecucion() {
        List<SelectItem> combo = new ArrayList<>();
        combo.add(new SelectItem(0, "Todos"));
        combo.add(new SelectItem(1, "1° Ciclo"));
        combo.add(new SelectItem(2, "2° Ciclo"));
        combo.add(new SelectItem(3, "Segunda Semana"));
        combo.add(new SelectItem(4, "Ultima Semana"));
        return combo;
    }

    public List<SelectItem> getComboCargoAbono() {
        List<SelectItem> combo = new ArrayList<>();
        combo.add(new SelectItem(0, "Cargo"));
        combo.add(new SelectItem(1, "Abono"));
        return combo;
    }

    public List<SelectItem> getComboBimestres() {
        List<SelectItem> combo = new ArrayList<>();
        combo.add(new SelectItem(1, "1° (ENE - FEB)"));
        combo.add(new SelectItem(2, "2° (MAR - ABR)"));
        combo.add(new SelectItem(3, "3° (MAY - JUN)"));
        combo.add(new SelectItem(4, "4° (JUL - AGO)"));
        combo.add(new SelectItem(5, "5° (SEP - OCT)"));
        combo.add(new SelectItem(6, "6° (NOV - DIC)"));
        return combo;
    }

    public List<SelectItem> getComboEstatusVacaciones() {
        List<SelectItem> combo = new ArrayList<>();
        combo.add(new SelectItem(Parametros.LABEL_SOLICITAR_VACACIONES, Parametros.LABEL_SOLICITAR_VACACIONES));
        combo.add(new SelectItem(Parametros.LABEL_AUTORIZAR_VACACIONES, Parametros.LABEL_AUTORIZAR_VACACIONES));
        combo.add(new SelectItem(Parametros.LABEL_CANCELAR_VACACIONES, Parametros.LABEL_CANCELAR_VACACIONES));
        return combo;
    }

    public List<SelectItem> getComboMeses() {
        List<SelectItem> combo = new ArrayList<>();
        combo.add(new SelectItem(1, "ENERO"));
        combo.add(new SelectItem(2, "FEBRERO"));
        combo.add(new SelectItem(3, "MARZO"));
        combo.add(new SelectItem(4, "ABRIL"));
        combo.add(new SelectItem(5, "MAYO"));
        combo.add(new SelectItem(6, "JUNIO"));
        combo.add(new SelectItem(7, "JULIO"));
        combo.add(new SelectItem(8, "AGOSTO"));
        combo.add(new SelectItem(9, "SEPTIEMBRE"));
        combo.add(new SelectItem(10, "OCTUBRE"));
        combo.add(new SelectItem(11, "NOVIEMBRE"));
        combo.add(new SelectItem(12, "DICIEMBRE"));
        return combo;
    }

    public List<SelectItem> getComboDependientes() {
        List<SelectItem> combo = new ArrayList<>();
        combo.add(new SelectItem("Abuelo(a)"));
        combo.add(new SelectItem("Madre"));
        combo.add(new SelectItem("Padre"));
        combo.add(new SelectItem("Esposo(a)"));
        combo.add(new SelectItem("Hijo(a)"));
        combo.add(new SelectItem("Hermano(a)"));
        return combo;
    }

    public List<SelectItem> getComboPeriodoMes() {
        List<SelectItem> combo = new ArrayList<>();
        combo.add(new SelectItem(0, "Periodo"));
        combo.add(new SelectItem(1, "Mes"));
        return combo;
    }

    public List<SelectItem> getComboRedondeo() {
        List<SelectItem> combo = new ArrayList<>();
        combo.add(new SelectItem(100, "2 Decimales"));
        combo.add(new SelectItem(1000, "3 Decimales"));
        combo.add(new SelectItem(10000, "4 Decimales"));
        return combo;
    }

    public List<SelectItem> getComboTiposnodo() {
        List<SelectItem> combo = new ArrayList<>();
        combo.add(new SelectItem("HRSDOB", "Horas Dobles"));
        combo.add(new SelectItem("DESLAB", "Descanso Laborado"));
        combo.add(new SelectItem("HRSTRI", "Horas Triples"));
        return combo;
    }

    public List<SelectItem> getComboTiposalidaarchivo() {
        List<SelectItem> combo = new ArrayList<>();
        combo.add(new SelectItem(0, ".CSV"));
        combo.add(new SelectItem(1, "Importe/Tiempo"));
        combo.add(new SelectItem(2, "Importe"));
        combo.add(new SelectItem(3, "Tiempo"));
        return combo;
    }

    public List<SelectItem> getComboTiposalidaarchivoListado() {
        List<SelectItem> combo = new ArrayList<>();
        combo.add(new SelectItem(1, "Importe/Tiempo"));
        combo.add(new SelectItem(2, "Importe"));
        combo.add(new SelectItem(3, "Tiempo"));
        return combo;
    }

    public List<SelectItem> getComboSexo() {
        List<SelectItem> combo = new ArrayList<>();
        combo.add(new SelectItem(0, "Femenino"));
        combo.add(new SelectItem(1, "Masculino"));
        return combo;
    }

    public List<SelectItem> getTipoFecha() {
        List<SelectItem> combo = new ArrayList<>();
        combo.add(new SelectItem(0, "Fecha Antigüedad"));
        combo.add(new SelectItem(1, "Fecha Ingreso"));
        return combo;
    }

    public List<SelectItem> getMovimientosSua() {
        List<SelectItem> combo = new ArrayList<>();
        combo.add(new SelectItem(0, "Movimientos Sua"));
        combo.add(new SelectItem(1, "Bajas"));
        combo.add(new SelectItem(2, "Modificacion de Salarios"));
        combo.add(new SelectItem(3, "Ausentismos"));
        combo.add(new SelectItem(4, "Movimientos Infonavit"));
        return combo;
    }

    public List<SelectItem> getTipoMovimientoInfonavit() {
        List<SelectItem> combo = new ArrayList<>();
        combo.add(new SelectItem(15, "Inicio de Crédito (ICV)"));
        combo.add(new SelectItem(16, "Fecha de Suspensión de Descuento (FS)"));
        combo.add(new SelectItem(17, "Reinicio de Descuento (RD)"));
        combo.add(new SelectItem(18, "Modificación de Tipo de Descuento (MTD)"));
        combo.add(new SelectItem(19, "Modificación de Valor de Descuento (MVD)"));
        combo.add(new SelectItem(20, "Modificación de Número de Crédito (MND)"));
        return combo;
    }

    public List<SelectItem> getFechasAjuste() {
        List<SelectItem> combo = new ArrayList<>();
        combo.add(new SelectItem(0, "Por Fecha Ingreso"));
        combo.add(new SelectItem(1, "Por Fecha Antigüedad"));
        return combo;
    }

    public Date getFechaActual() {
        return new Date();
    }

    public boolean isServicioPumasa() {
        String nombrecorto = ControladorSesiones.getInstance().getCompaniaSession().getNombreCorto();
        return Parametros.SERVICIO_PUMASA.equals(nombrecorto);
    }

    public boolean isServicioInternas() {
        String nombrecorto = ControladorSesiones.getInstance().getCompaniaSession().getNombreCorto();
        return Parametros.SERVICIO_INTERNAS.equals(nombrecorto);
    }

    public boolean isServicioInternasDos() {
        String nombrecorto = ControladorSesiones.getInstance().getCompaniaSession().getNombreCorto();
        return Parametros.SERVICIO_INTERNAS_DOS.equals(nombrecorto);
    }

    public boolean isServicioSUAT() {
        String nombrecorto = ControladorSesiones.getInstance().getCompaniaSession().getNombreCorto();
        return Parametros.SERVICIO_SUAT.equals(nombrecorto);
    }

    public boolean isServicioStone() {
        String nombrecorto = ControladorSesiones.getInstance().getCompaniaSession().getNombreCorto();
        return Parametros.SERVICIO_STONE.equals(nombrecorto);
    }

    public boolean isServicioYamana() {
        String nombrecorto = ControladorSesiones.getInstance().getCompaniaSession().getNombreCorto();
        return Parametros.SERVICIO_YAMANA.equals(nombrecorto);
    }

    public boolean isServicioAlpha() {
        String nombrecorto = ControladorSesiones.getInstance().getCompaniaSession().getNombreCorto();
        return Parametros.SERVICIO_ALPHA.equals(nombrecorto);
    }

    public boolean isServicioExmape() {
        String nombrecorto = ControladorSesiones.getInstance().getCompaniaSession().getNombreCorto();
        return Parametros.SERVICIO_EXMAPE.equals(nombrecorto);
    }

    public boolean isServicioAemsa() {
        String nombrecorto = ControladorSesiones.getInstance().getCompaniaSession().getNombreCorto();
        return Parametros.SERVICIO_AEMSA.equals(nombrecorto);
    }

    public boolean isServicioNutrition() {
        String nombrecorto = ControladorSesiones.getInstance().getCompaniaSession().getNombreCorto();
        return Parametros.SERVICIO_NUTRITION.equals(nombrecorto);
    }

    public boolean isServicioStoneCovidBus() {
        String nombrecorto = ControladorSesiones.getInstance().getCompaniaSession().getNombreCorto();
        return Parametros.SERVICIO_STONE_COVIDBUS.equals(nombrecorto);
    }

    public boolean isServicioNewceni() {
        String nombrecorto = ControladorSesiones.getInstance().getCompaniaSession().getNombreCorto();
        return Parametros.SERVICIO_NEWCENI.equals(nombrecorto);
    }

    public boolean isServicioWhiteHat() {
        String nombrecorto = ControladorSesiones.getInstance().getCompaniaSession().getNombreCorto();
        return Parametros.SERVICIO_WHITEHAT.equals(nombrecorto);
    }

    public boolean isServicioEnerfenix() {
        String nombrecorto = ControladorSesiones.getInstance().getCompaniaSession().getNombreCorto();
        return Parametros.SERVICIO_ENERFENIX.equals(nombrecorto);
    }

    public boolean isServicioArtsana() {
        String nombrecorto = ControladorSesiones.getInstance().getCompaniaSession().getNombreCorto();
        return Parametros.SERVICIO_ARTSANA.equals(nombrecorto);
    }

    public boolean isServicioAbastohotel() {
        String nombrecorto = ControladorSesiones.getInstance().getCompaniaSession().getNombreCorto();
        return Parametros.SERVICIO_ABASTOHOTEL.equals(nombrecorto);
    }

    public boolean isServicioDelMonte() {
        String nombrecorto = ControladorSesiones.getInstance().getCompaniaSession().getNombreCorto();
        return Parametros.SERVICIO_DELMONTE.equals(nombrecorto);
    }

    public boolean isServicioLimonYCereza() {
        String nombrecorto = ControladorSesiones.getInstance().getCompaniaSession().getNombreCorto();
        return Parametros.SERVICIO_LIMONYCEREZA.equals(nombrecorto);
    }

    public boolean isServicioDecoplantas() {
        String nombrecorto = ControladorSesiones.getInstance().getCompaniaSession().getNombreCorto();
        return Parametros.SERVICIO_DECOPLANTAS.equals(nombrecorto);
    }

    public boolean isServicioGrupoOrdas() {
        String nombrecorto = ControladorSesiones.getInstance().getCompaniaSession().getNombreCorto();
        return Parametros.SERVICIO_GRUPO_ORDAS.equals(nombrecorto);
    }

    public boolean isServicioMinnt() {
        String nombrecorto = ControladorSesiones.getInstance().getCompaniaSession().getNombreCorto();
        return Parametros.SERVICIO_MINNT.equals(nombrecorto);
    }

    public boolean isServicioMelonn() {
        String nombrecorto = ControladorSesiones.getInstance().getCompaniaSession().getNombreCorto();
        return Parametros.SERVICIO_MELONN.equals(nombrecorto);
    }

    public boolean isServicioKonecta() {
        String nombrecorto = ControladorSesiones.getInstance().getCompaniaSession().getNombreCorto();
        return Parametros.SERVICIO_KONECTA.equals(nombrecorto);
    }

    public boolean isServicioMartillo() {
        String nombrecorto = ControladorSesiones.getInstance().getCompaniaSession().getNombreCorto();
        return Parametros.SERVICIO_MARTILLO.equals(nombrecorto);
    }

    public boolean isServicioEnomina() {
        String nombrecorto = ControladorSesiones.getInstance().getCompaniaSession().getNombreCorto();
        return Parametros.SERVICIO_ENOMINA.equals(nombrecorto);
    }

    public boolean isServicioUrbanHub() {
        String nombrecorto = ControladorSesiones.getInstance().getCompaniaSession().getNombreCorto();
        return Parametros.SERVICIO_URBANHUB.equals(nombrecorto);
    }

    public boolean isServicioGobierno() {
        String nombrecorto = ControladorSesiones.getInstance().getCompaniaSession().getNombreCorto();
        return Parametros.SERVICIO_GOBIERNO.equals(nombrecorto);
    }

    public boolean isServicioRequordit() {
        String nombrecorto = ControladorSesiones.getInstance().getCompaniaSession().getNombreCorto();
        return Parametros.SERVICIO_REQUORDIT.equals(nombrecorto);
    }

    public boolean isServicioBakertilly() {
        String nombrecorto = ControladorSesiones.getInstance().getCompaniaSession().getNombreCorto();
        return Parametros.SERVICIO_BAKERTILLY.equals(nombrecorto);
    }

    public boolean isServicioDemoBakertilly() {
        String nombrecorto = ControladorSesiones.getInstance().getCompaniaSession().getNombreCorto();
        return Parametros.SERVICIO_DEMOBAKERTELY.equals(nombrecorto);
    }

    public boolean isServicioEPAM() {
        String nombrecorto = ControladorSesiones.getInstance().getCompaniaSession().getNombreCorto();
        return Parametros.SERVICIO_EPAM.equals(nombrecorto);
    }

    public boolean isServicioEgelhof() {
        String nombrecorto = ControladorSesiones.getInstance().getCompaniaSession().getNombreCorto();
        return Parametros.SERVICIO_EGELHOF.equals(nombrecorto);
    }

    public boolean isServicioIREP() {
        String nombrecorto = ControladorSesiones.getInstance().getCompaniaSession().getNombreCorto();
        return Parametros.SERVICIO_IREP.equals(nombrecorto);
    }

    public boolean isServicioMEXICIP() {
        String nombrecorto = ControladorSesiones.getInstance().getCompaniaSession().getNombreCorto();
        return Parametros.SERVICIO_MEXICIP.equals(nombrecorto);
    }

    public boolean isServicioESENTTIA() {
        String nombrecorto = ControladorSesiones.getInstance().getCompaniaSession().getNombreCorto();
        return Parametros.SERVICIO_ESENTTIA.equals(nombrecorto);
    }

    public boolean isServicioGISS2P() {
        String nombrecorto = ControladorSesiones.getInstance().getCompaniaSession().getNombreCorto();
        return Parametros.SERVICIO_GISS2P.equals(nombrecorto);
    }

    public boolean isServicioTOROTO() {
        String nombrecorto = ControladorSesiones.getInstance().getCompaniaSession().getNombreCorto();
        return Parametros.SERVICIO_TOROTO.equals(nombrecorto);
    }

    public boolean isServicioSQUARED() {
        String nombrecorto = ControladorSesiones.getInstance().getCompaniaSession().getNombreCorto();
        return Parametros.SERVICIO_SQUARED.equals(nombrecorto);
    }

    public boolean isServicioGIGAMON() {
        String nombrecorto = ControladorSesiones.getInstance().getCompaniaSession().getNombreCorto();
        return Parametros.SERVICIO_GIGAMON.equals(nombrecorto);
    }

    public boolean isServicioCHOBANNI() {
        String nombrecorto = ControladorSesiones.getInstance().getCompaniaSession().getNombreCorto();
        return Parametros.SERVICIO_CHOBANNI.equals(nombrecorto);
    }

    public boolean isServicioM7SSS() {
        String nombrecorto = ControladorSesiones.getInstance().getCompaniaSession().getNombreCorto();
        return Parametros.SERVICIO_M7SSS.equals(nombrecorto);
    }

    public boolean isServicioBLUEGROUND() {
        String nombrecorto = ControladorSesiones.getInstance().getCompaniaSession().getNombreCorto();
        return Parametros.SERVICIO_BLUEGROUND.equals(nombrecorto);
    }

    public boolean isServicioCARBON() {
        String nombrecorto = ControladorSesiones.getInstance().getCompaniaSession().getNombreCorto();
        return Parametros.SERVICIO_CARBON.equals(nombrecorto);
    }

    public boolean isServicioELEVANTE() {
        String nombrecorto = ControladorSesiones.getInstance().getCompaniaSession().getNombreCorto();
        return Parametros.SERVICIO_ELEVANTE.equals(nombrecorto);
    }

    public boolean isServicioHAZEL() {
        String nombrecorto = ControladorSesiones.getInstance().getCompaniaSession().getNombreCorto();
        return Parametros.SERVICIO_HAZEL.equals(nombrecorto);
    }

    public boolean isServicioKASPERSKY() {
        String nombrecorto = ControladorSesiones.getInstance().getCompaniaSession().getNombreCorto();
        return Parametros.SERVICIO_KASPERSKY.equals(nombrecorto);
    }

    public boolean isServicioMAQUIPRIME() {
        String nombrecorto = ControladorSesiones.getInstance().getCompaniaSession().getNombreCorto();
        return Parametros.SERVICIO_MAQUIPRIME.equals(nombrecorto);
    }

    public boolean isServicioSENSEDIA() {
        String nombrecorto = ControladorSesiones.getInstance().getCompaniaSession().getNombreCorto();
        return Parametros.SERVICIO_SENSEDIA.equals(nombrecorto);
    }

    public boolean isServicioDEMONOMINA() {
        String nombrecorto = ControladorSesiones.getInstance().getCompaniaSession().getNombreCorto();
        return Parametros.SERVICIO_DEMONOMINA.equals(nombrecorto);
    }

    public boolean isServicioACTUALIZE() {
        String nombrecorto = ControladorSesiones.getInstance().getCompaniaSession().getNombreCorto();
        return Parametros.SERVICIO_ACTUALIZE.equals(nombrecorto);
    }

    public boolean isServicioCIELO() {
        String nombrecorto = ControladorSesiones.getInstance().getCompaniaSession().getNombreCorto();
        return Parametros.SERVICIO_CIELO.equals(nombrecorto);
    }

    public boolean isServicioGLOWDX() {
        String nombrecorto = ControladorSesiones.getInstance().getCompaniaSession().getNombreCorto();
        return Parametros.SERVICIO_GLOWDX.equals(nombrecorto);
    }

    public boolean isServicioFOLEY() {
        String nombrecorto = ControladorSesiones.getInstance().getCompaniaSession().getNombreCorto();
        return Parametros.SERVICIO_FOLEY.equals(nombrecorto);
    }

    public boolean isServicioSERVICIO_IKEA() {
        String nombrecorto = ControladorSesiones.getInstance().getCompaniaSession().getNombreCorto();
        return Parametros.SERVICIO_IKEA.equals(nombrecorto);
    }

    public boolean isServicioIMPERVA() {
        String nombrecorto = ControladorSesiones.getInstance().getCompaniaSession().getNombreCorto();
        return Parametros.SERVICIO_IMPERVA.equals(nombrecorto);
    }

    public boolean isServicioMIDCHEM() {
        String nombrecorto = ControladorSesiones.getInstance().getCompaniaSession().getNombreCorto();
        return Parametros.SERVICIO_MIDCHEM.equals(nombrecorto);
    }

    public boolean isServicioOFFERWISE() {
        String nombrecorto = ControladorSesiones.getInstance().getCompaniaSession().getNombreCorto();
        return Parametros.SERVICIO_OFFERWISE.equals(nombrecorto);
    }

    public boolean isServicioRNDRAGO() {
        String nombrecorto = ControladorSesiones.getInstance().getCompaniaSession().getNombreCorto();
        return Parametros.SERVICIO_RNDRAGO.equals(nombrecorto);
    }

    public boolean isServicioSHIFT() {
        String nombrecorto = ControladorSesiones.getInstance().getCompaniaSession().getNombreCorto();
        return Parametros.SERVICIO_FOLEY.equals(nombrecorto);
    }

    public boolean isServicioSERVICIO_ULTRAFABRICS() {
        String nombrecorto = ControladorSesiones.getInstance().getCompaniaSession().getNombreCorto();
        return Parametros.SERVICIO_IKEA.equals(nombrecorto);
    }

    public boolean isServicioSERVICIO_AFIS() {
        String nombrecorto = ControladorSesiones.getInstance().getCompaniaSession().getNombreCorto();
        return Parametros.SERVICIO_AFIS.equals(nombrecorto);
    }

    public boolean isServicioHANDLING() {
        String nombrecorto = ControladorSesiones.getInstance().getCompaniaSession().getNombreCorto();
        return Parametros.SERVICIO_HANDLING.equals(nombrecorto);
    }

    public boolean isServicioSMALLER() {
        String nombrecorto = ControladorSesiones.getInstance().getCompaniaSession().getNombreCorto();
        return Parametros.SERVICIO_SMALLER.equals(nombrecorto);
    }

    public boolean isServicioTRACTORES() {
        String nombrecorto = ControladorSesiones.getInstance().getCompaniaSession().getNombreCorto();
        return Parametros.SERVICIO_TRACTORES.equals(nombrecorto);
    }

    public boolean isServicioSWIFT() {
        String nombrecorto = ControladorSesiones.getInstance().getCompaniaSession().getNombreCorto();
        return Parametros.SERVICIO_SWIFT.equals(nombrecorto);
    }

    public boolean isServicioWILMAR() {
        String nombrecorto = ControladorSesiones.getInstance().getCompaniaSession().getNombreCorto();
        return Parametros.SERVICIO_WILMAR.equals(nombrecorto);
    }

    public boolean isServicioGrupoBakertilly() {
        String nombrecorto = ControladorSesiones.getInstance().getCompaniaSession().getNombreCorto();
        boolean isServicio = false;
        switch (nombrecorto) {
            case Parametros.SERVICIO_EPAM:
            case Parametros.SERVICIO_EGELHOF:
            case Parametros.SERVICIO_BAKERTILLY:
            case Parametros.SERVICIO_IREP:
            case Parametros.SERVICIO_ESENTTIA:
            case Parametros.SERVICIO_MEXICIP:
            case Parametros.SERVICIO_GISS2P:
            case Parametros.SERVICIO_TOROTO:
            case Parametros.SERVICIO_GIGAMON:
            case Parametros.SERVICIO_SQUARED:
            case Parametros.SERVICIO_M7SSS:
            case Parametros.SERVICIO_CHOBANNI:
            case Parametros.SERVICIO_BLUEGROUND:
            case Parametros.SERVICIO_CARBON:
            case Parametros.SERVICIO_ELEVANTE:
            case Parametros.SERVICIO_HAZEL:
            case Parametros.SERVICIO_KASPERSKY:
            case Parametros.SERVICIO_MAQUIPRIME:
            case Parametros.SERVICIO_SENSEDIA:
            case Parametros.SERVICIO_DEMONOMINA:
            case Parametros.SERVICIO_ACTUALIZE:
            case Parametros.SERVICIO_CIELO:
            case Parametros.SERVICIO_GLOWDX:
            case Parametros.SERVICIO_FOLEY:
            case Parametros.SERVICIO_IKEA:
            case Parametros.SERVICIO_IMPERVA:
            case Parametros.SERVICIO_MIDCHEM:
            case Parametros.SERVICIO_OFFERWISE:
            case Parametros.SERVICIO_RNDRAGO:
            case Parametros.SERVICIO_SHIFT:
            case Parametros.SERVICIO_ULTRAFABRICS:
            case Parametros.SERVICIO_AFIS:
            case Parametros.SERVICIO_HANDLING:
            case Parametros.SERVICIO_SMALLER:
            case Parametros.SERVICIO_TRACTORES:
            case Parametros.SERVICIO_WILMAR:
            case Parametros.SERVICIO_SWIFT:
                isServicio = true;
                break;
        }
        return isServicio;
    }

    public boolean isServicioReporteriaNominas() {
        String nombrecorto = ControladorSesiones.getInstance().getCompaniaSession().getNombreCorto();
        boolean isServicio = false;
        switch (nombrecorto) {
            case Parametros.SERVICIO_EGELHOF:
            case Parametros.SERVICIO_IREP:
            case Parametros.SERVICIO_ESENTTIA:
            case Parametros.SERVICIO_MEXICIP:
            case Parametros.SERVICIO_GISS2P:
            case Parametros.SERVICIO_TOROTO:
            case Parametros.SERVICIO_GIGAMON:
            case Parametros.SERVICIO_SQUARED:
            case Parametros.SERVICIO_M7SSS:
            case Parametros.SERVICIO_CHOBANNI:
            case Parametros.SERVICIO_BLUEGROUND:
            case Parametros.SERVICIO_CARBON:
            case Parametros.SERVICIO_ELEVANTE:
            case Parametros.SERVICIO_HAZEL:
            case Parametros.SERVICIO_KASPERSKY:
            case Parametros.SERVICIO_MAQUIPRIME:
            case Parametros.SERVICIO_SENSEDIA:
            case Parametros.SERVICIO_DEMONOMINA:
            case Parametros.SERVICIO_ACTUALIZE:
            case Parametros.SERVICIO_CIELO:
            case Parametros.SERVICIO_GLOWDX:
            case Parametros.SERVICIO_FOLEY:
            case Parametros.SERVICIO_IKEA:
            case Parametros.SERVICIO_IMPERVA:
            case Parametros.SERVICIO_MIDCHEM:
            case Parametros.SERVICIO_OFFERWISE:
            case Parametros.SERVICIO_RNDRAGO:
            case Parametros.SERVICIO_SHIFT:
            case Parametros.SERVICIO_ULTRAFABRICS:
            case Parametros.SERVICIO_AFIS:
            case Parametros.SERVICIO_HANDLING:
            case Parametros.SERVICIO_SMALLER:
            case Parametros.SERVICIO_TRACTORES:
            case Parametros.SERVICIO_WILMAR:
            case Parametros.SERVICIO_SWIFT:
                isServicio = true;
                break;
        }
        return isServicio;
    }

    public Date getHoy() {
        return new Date();
    }
}
