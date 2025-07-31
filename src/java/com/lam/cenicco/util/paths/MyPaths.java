/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lam.cenicco.util.paths;

/**
 *
 * @author JoséAntonio
 */
public class MyPaths {

    public static String URL_ESTADO_ACTIVO = "../../../resources/images/icons/check.png";
    public static String URL_ESTADO_INACTIVO = "../../../resources/images/icons/cancel.png";
    public static String URL_ESTADO_CANCELADO = "../../../resources/images/icons/cancel-icon.png";
//
    public static String URL_CAJA_AHORRO = "v.1/cajaahorro/cenicco.xhtml";
    public static String URL_FISCAL = "v.1/fiscal/cenicco.xhtml";
    public static String URL_NOMINAS = "v.1/nominas/cenicco.xhtml";
    public static String URL_CONFIGURACION = "v.1/configuracion/cenicco.xhtml";
    public static String URL_RECURSOS_HUMANOS = "v.1/recursoshumanos/cenicco.xhtml";
    public static String URL_EMPLEADOS = "v.1/empleados/cenicco.xhtml";
    public static String URL_TIEMPO_ASISTENCIAS = "v.1/tiempoasistencias/cenicco.xhtml";
    public static String URL_LOGIN = "login.xhtml";

    public static String basepath() {
        return "/nomina/v.1/";
    }

    public static String basepathKiosko() {
        return "/Ceniccox/nomina/v.1/kiosko/portal.xhtml";
    }

    public static String basepathLogoutKiosko() {
        return "/Ceniccox/nomina/kiosko.xhtml";
    }

    public static String baseurl() {
        return "http://127.0.0.1:8080/Ceniccox/";
    }

    public static String basepathLogin() {
        return "/Ceniccox/nomina/";
    }

    public static String urlServletDocumentoEmpleado() {
        return "../../../VisorDocumento";
    }

    public static String urlServletDescargarRecibo() {
        return "../../../DescargarRecibo";
    }

    public static String urlServletDescargarArchivoTexto() {
        return "../../../DescargarArchivoTexto";
    }

    public static String urlServletDescargarArchivoCsv() {
        return "../../../DescargarArchivoCsv";
    }

    public static String urlServletReporte() {
        return "../../../GeneradorReportes";
    }

    public static String urlServletCartas() {
        return "../../../GeneradorCartas";
    }
}
