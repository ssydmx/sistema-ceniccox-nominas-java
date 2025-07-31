/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lam.cenicco.beans.nominas.incidencias;

import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author JoséAntonio
 */
@Named(value = "incidenciaBean")
@SessionScoped
public class IncidenciaBean extends IncidenciaCenicco implements Serializable {
}
