/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lam.cenicco.beans.nominas.terminales;

import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author erick
 */
@Named(value = "incidenciaTerminalBean")
@SessionScoped
public class IncidenciasTerminalesBean extends IncidenciasTerminalesCenicco implements Serializable {
}
