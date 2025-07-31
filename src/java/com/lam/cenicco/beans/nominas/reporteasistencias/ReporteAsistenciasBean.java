/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lam.cenicco.beans.nominas.reporteasistencias;

import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author erick
 */
@Named(value = "reporteAsistenciaBean")
@SessionScoped
public class ReporteAsistenciasBean extends ReporteAsistenciaCenicco implements Serializable{
}
