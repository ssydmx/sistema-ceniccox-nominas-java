/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lam.cenicco.util;

import com.lam.cenicco.ws.Banco;
import com.lam.cenicco.ws.Compania;
import com.lam.cenicco.ws.Modulo;
import com.lam.cenicco.ws.Usuario;
import java.util.Map;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Antonio
 */
public class Parametros {
//    

    public static String CMD_PING_BD = "nmap 192.168.1.251 -p 3306";
    public static String CMD_PING_NUBE = "ping -c 10 8.8.8.8";
    //201.175.24.167 IP de www.appfacturainteligente.com
    public static String CMD_PING_TIMBRE = "ping -c 5 201.175.24.167";
    public static String CMD_GREP = "grep ";
//
    public static Map<String, HttpSession> MAPA_SESIONES;
    public static Map<String, Usuario> MAPA_USUARIOS_EJECUTIVOS;
    public static Map<String, Usuario> MAPA_USUARIOS_NOMINAS;
    public static Map<String, Banco> MAPA_BANCOS;
    public static Map<String, Compania> MAPA_COMPANIAS;
    public static Map<String, Modulo> MAPA_MODULOS;
//    
    public static Integer CLAVE_ID_SDN = 1;
    public static Integer CLAVE_ID_SDI = 2;
    public static String CLAVE_NATURALEZA = "03";
//    
    public static final String TIPO_NOMINA_FINIQUITOS = "NL";
//  PARAMETROS DE TABLAS
    public static final String TABLA_RHEMPLEADOSTB = "RHEMPLEADOSTB";
    public static final String TABLA_RHRELACIONLABORALTB = "RHRELACIONLABORALTB";
    public static final String TABLA_ADMESTRUCTURASTB = "ADMESTRUCTURASTB";
    public static final String TABLA_ADMPUESTOSTB = "ADMPUESTOSTB";
    public static final String TABLA_RHEMPLEADOSPOSICIONTB = "RHEMPLEADOSPOSICIONTB";
    public static final String TABLA_RHPOSICIONESSUELDOSTB = "RHPOSICIONESSUELDOSTB";
    public static final String TABLA_RHEMPLEADOSDOMICILIOSTB = "RHEMPLEADOSDOMICILIOSTB";
    public static final String TABLA_RHRELACIONLABORALCUENTASTB = "RHRELACIONLABORALCUENTASTB";
    public static final String TABLA_NOMCONCEPTOSTB = "NOMCONCEPTOSTB";
    public static final String TABLA_RHEMPLEADOSPENSIONESTB = "RHEMPLEADOSPENSIONESTB";
    public static final String TABLA_FISINFONAVITTB = "FISINFONAVITTB";
    public static final String TABLA_NOMINCIDENCIASTB = "NOMINCIDENCIASTB";
    public static final String TABLA_VACSOLICITUDESTB = "VACSOLICITUDESTB";
    public static final String TABLA_CRESOLICITUDESTB = "CRESOLICITUDESTB";
    public static final String TABLA_PRIORIDAD = "PRIORIDAD";
    public static final String TABLA_FORMULASTB = "FORMULASTB";
//  PARAMETROS DE COLUMNAS
    public static final String COLUMNA_ESTATUS = "ESTATUS";
    public static final String COLUMNA_SEXO = "SEXO";
    public static final String COLUMNA_ESTADOCIVIL = "ESTADOCIVIL";
    public static final String COLUMNA_TIPOCONTRATO = "TIPOCONTRATO";
    public static final String COLUMNA_IDMODALIDADTRABAJO = "IDMODALIDADTRABAJO";
    public static final String COLUMNA_BROKER = "BROKER";
    public static final String COLUMNA_JORNADATRABAJADOR = "JORNADATRABAJADOR";
    public static final String COLUMNA_TURNOTRABAJADOR = "TURNOTRABAJADOR";
    public static final String COLUMNA_NIVELTRABAJADOR = "NIVELTRABAJADOR";
    public static final String COLUMNA_UBICACION_TRABAJO = "UBICACIONTRABAJO";
    public static final String COLUMNA_SISTEMAANTIGUEDAD = "SISTEMAANTIGUEDAD";
    public static final String COLUMNA_CAUSABAJAIMSS = "CAUSABAJAIMSS";
    public static final String COLUMNA_CAUSABAJA = "CAUSABAJA";
    public static final String COLUMNA_NIVEL = "NIVEL";
    public static final String COLUMNA_JERARQUIA = "JERARQUIA";
    public static final String COLUMNA_SISTEMAHORARIO = "SISTEMAHORARIO";
    public static final String COLUMNA_JORNADALABORAL = "JORNADALABORAL";
    public static final String COLUMNA_TIPOSALARIO = "TIPOSALARIO";
    public static final String COLUMNA_FORMAPAGO = "FORMAPAGO";
    public static final String COLUMNA_TIPODOMICILIO = "TIPODOMICILIO";
    public static final String COLUMNA_TIPOCUENTA = "TIPOCUENTA";
    public static final String COLUMNA_TIPOCONCEPTO = "TIPOCONCEPTO";
    public static final String COLUMNA_UNIDAD = "UNIDAD";
    public static final String COLUMNA_TIPOCALCULO = "TIPOCALCULO";
    public static final String COLUMNA_SISTEMA_HORARIO = "SISTEMAHORARIO";
    public static final String COLUMNA_TIPODESCUENTO = "TIPODESCUENTO";
    public static final String COLUMNA_TIPODISPOSICION = "TIPODISPOSICION";
    public static final String COLUMNA_CALCULO = "CALCULO";
    public static final String COLUMNA_DIASMENSUAL = "DIASMENSUAL";
    public static final String COLUMNA_NATURALEZA = "NATURALEZA";
    public static final String COLUMNA_PORCENVSMGZ = "PORCENVSMGZ";
//    
    public static final String TIPO_SUELDO_SDI = "SDI";
    public static final String TIPO_SUELDO_SDN = "SDN";
//    
    public static final String TIPO_CATALOGO_CONCEPTO_INCIDENCIAS = "01";
    public static final String TIPO_CATALOGO_CONCEPTO_CREDITOS = "02";
    public static final String TIPO_CATALOGO_CONCEPTO_FORMULAS = "03";
//    
    public static final String MENSAJE_NUMERO_CREDITOS = "Timbres Disponibles: ";
    public static final String MENSAJE_NUMERO_CREDITOS_NO_RESPONDE = "No responde el servicio";
//    
    public static final String ESTRUCTURA_NIVEL = "6";
//    
    public static final String ID_ERROR_SOLICITUD_VACACIONES_DUPLICADA = "1";
    public static final String ID_ERROR_SOLICITUD_VACACIONES_SALDO = "2";
    public static final String ID_EXITOSO_VACACIONES_SALDO = "0";
//    
    public static final String LABEL_SOLICITAR_VACACIONES = "Solicitar";
    public static final String LABEL_AUTORIZAR_VACACIONES = "Autorizar";
    public static final String LABEL_CANCELAR_VACACIONES = "Cancelar";
//    
    public static final String ID_REGSITRO_INCIDENCIA = "01";
//    
    public static final String MODULO_INCIDENCIAS = "Incidencias";
    public static final String MODULO_CREDITOS = "Créditos";
    public static final String MODULO_FORMULAS = "Fórmulas";
//
    public static final String CONCEPTO_LISTADO_NETO = "9999";
//    
    public static final String TIPO_NOMINA_PTU = "NP";
//    
    public static final int MIN_TIME_SESSION = 60 * 60;
    public static final int MAX_TIME_SESSION = 60 * 60;
//    
    public static final long MILLSECS_PER_DAY = 1000 * 60 * 60 * 24;
//    
    public static final String SERVICIO_YAMANA = "YAMANAGOLD";
//
    public static final String SERVICIO_MINNT = "MINNT";
    public static final String SERVICIO_MELONN = "MELONN";
//    
    //public static final String SERVICIO_WORKKABAH = "WORK KABAH";
//    
    public static final String SERVICIO_ALPHA = "ALPHA";
//  
    public static final String SERVICIO_EXMAPE = "EXMAPE";
//    
    //public static final String SERVICIO_MELMEX = "MITSUBISHI";
//    
    //public static final String SERVICIO_MEAX = "MEAX";
//    
    public static final String SERVICIO_FILTERS = "FILTERS";
//    
    public static final String SERVICIO_PUMASA = "WORK KABAH";
//    
    public static final String SERVICIO_INTERNAS = "INTERNAS";
//    
    public static final String SERVICIO_INTERNAS_DOS = "INTERNASV2";
//    
    public static final String SERVICIO_SUAT = "SUAT";
//    
    public static final String SERVICIO_STONE = "STONE";
//    
    public static final String SERVICIO_AEMSA = "AEMSA";
//    
    public static final String SERVICIO_NUTRITION = "NUTRITION";
//  
    public static final String SERVICIO_STONE_COVIDBUS = "STONE COVID-BUS";
//    
    public static final String SERVICIO_NEWCENI = "NEWCENI";
//    
    public static final String SERVICIO_WHITEHAT = "WHITEHAT";
//    
    public static final String SERVICIO_ENERFENIX = "ENERFENIX";
//    
    public static final String SERVICIO_ARTSANA = "ARTSANA";
//    
    public static final String SERVICIO_ABASTOHOTEL = "ABASTOHOTEL";
//    
    public static final String SERVICIO_DELMONTE = "DELMONTE";
//    
    public static final String SERVICIO_LIMONYCEREZA = "LIMONYCEREZA";
//  
    public static final String SERVICIO_DECOPLANTAS = "DECO";
//  
    public static final String SERVICIO_GRUPO_ORDAS = "GRUPOORDAS";
//
    public static final String SERVICIO_KONECTA = "KONECTA";
//
    public static final String SERVICIO_MARTILLO = "MARTILLO";
// 
    public static final String SERVICIO_ENOMINA = "ENOMINA";
//
    public static final String SERVICIO_URBANHUB = "URBANHUB";
//
    public static final String SERVICIO_GOBIERNO = "GOBIERNO";
//
    public static final String SERVICIO_REQUORDIT = "REQUORDIT";
//
    public static final String SERVICIO_BAKERTILLY = "BAKERTILLY";
//
    public static final String SERVICIO_EPAM = "EPAM";
//
    public static final String SERVICIO_DEMOBAKERTELY = "DEMOBAKERTELY";
//
    public static final String SERVICIO_EGELHOF = "EGELHOF";
//
    public static final String SERVICIO_IREP = "IREP";
//
    public static final String SERVICIO_TOROTO = "TOROTO";
//
    public static final String SERVICIO_SQUARED = "SQUARED";
// 
    public static final String SERVICIO_GISS2P = "GISS2P";
//
    public static final String SERVICIO_MEXICIP = "MEXICIP";
// 
    public static final String SERVICIO_ESENTTIA = "ESENTTIA";
//
    public static final String SERVICIO_GIGAMON = "GIGAMON";
//
    public static final String SERVICIO_BLUEGROUND = "BLUEGROUND";
//
    public static final String SERVICIO_CARBON = "CARBON";
//
    public static final String SERVICIO_ELEVANTE = "ELEVANTE";
//
    public static final String SERVICIO_HAZEL = "HAZEL";
//
    public static final String SERVICIO_KASPERSKY = "KASPERSKY";
//
    public static final String SERVICIO_MAQUIPRIME = "MAQUIPRIME";
//
    public static final String SERVICIO_SENSEDIA = "SENSEDIA";
//
    public static final String SERVICIO_CHOBANNI = "CHOBANNI";
//
    public static final String SERVICIO_M7SSS = "M7SSS";
//
    public static final String SERVICIO_DEMONOMINA = "DEMONOMINA";
//
    public static final String SERVICIO_ACTUALIZE = "ACTUALIZE";
//
    public static final String SERVICIO_CIELO = "CIELO";
//
    public static final String SERVICIO_IKEA = "IKEA";
//
    public static final String SERVICIO_FOLEY = "FOLEY";
//
    public static final String SERVICIO_GLOWDX = "GLOWDX";
//
    public static final String SERVICIO_IMPERVA = "IMPERVA";
//
    public static final String SERVICIO_MIDCHEM = "MIDCHEM";
//
    public static final String SERVICIO_OFFERWISE = "OFFERWISE";
//
    public static final String SERVICIO_RNDRAGO = "RNDRAGO";
//
    public static final String SERVICIO_SHIFT = "SHIFT";
//
    public static final String SERVICIO_ULTRAFABRICS = "ULTRAFABRICS";
//
    public static final String SERVICIO_AFIS = "AFIS";
// 
    public static final String SERVICIO_HANDLING = "HANDLING";
//
    public static final String SERVICIO_SMALLER = "SMALLER";
//
    public static final String SERVICIO_TRACTORES = "TRACTORES";
//
    public static final String SERVICIO_SWIFT = "SWIFT";
//
    public static final String SERVICIO_WILMAR = "WILMAR";    
    //public static final String SERVICIO_CENTRAL = "CENTRAL";
//    
    public static final String BANCO_BANCOMER = "BANCOMER";
//    
    public static final String BANCO_BANAMEX = "BANAMEX";
//    
    public static final String BANCO_BANAMEX_PROVEEDORES = "BANAMEXPROVEEDORES";
//        
    public static final String BANCO_INBURSA = "INBURSA";
//        
    public static final String BANCO_GRUPO_FINANCIERO_INBURSA = "GFINBURSA";
//        
    public static final String BANCO_GRUPO_FINANCIERO_INBURSA_PAGO_TERCEROS = "GFINBURSATER";
//    
    public static final String BANCO_INTERBANCARIA_BANCOMER = "INTERBANCARIABANCOMER";
//    
    public static final String BANCO_BANORTE = "BANORTE";
//    
    public static final String BANCO_SANTANDER = "SANTANDER";
//    
    public static final String BANCO_SANTANDER_PAGO_TERCEROS = "SANTANTER";
//
    public static final String BANCO_BANBAJIO = "BANBAJIO";
//    
    public static final String BANCO_MONEX = "MONEX";
//    
    public static final String BANCO_INTERBANCARIA_BANORTE = "INTERBANCARIABANORTE";
//    
    public static final String BANCO_HSBC = "HSBC";
//    
    public static final String BANCO_HSBC_INTERBANCARIA = "INTERBANCARIAHSBC";
//    
    public static final int NUMERO_HILOS_TIMBRES = 1000;
//    
    public static final String TIPO_MOVIMIENTO_IMSS_ALTA = "08";
//
    public static final String TIPO_MOVIMIENTO_IMSS_BAJA = "02";
//
    public static final String TIPO_MOVIMIENTO_IMSS_SALARIO = "07";
//
    public static final String TIPO_MOVIMIENTO_IMSS_AUSENTISMO = "11";
//
    public static final String TIPO_MOVIMIENTO_IMSS_INCAPACIDAD = "12";
//    
    public static final String ALPHA_CUENTAS_HERRAMIENTA_TRABAJO = "ALPHA_CUENTAS_HERRAMIENTA_TRABAJO";
//    
    public static final String ALPHA_PATH_REPORTE_ASISTENCIAS_TERMINAL = "ALPHA_PATH_REPORTE_ASISTENCIAS_TERMINAL";
//
    public static final String FECHAS_DESCANSO = "FECHAS_DESCANSO";
//    
    public static final String BANCO_CB_INTERCAM = "CBINTERCAM";
}
