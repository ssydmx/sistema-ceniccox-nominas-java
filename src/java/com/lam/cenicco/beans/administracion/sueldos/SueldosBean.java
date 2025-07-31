/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lam.cenicco.beans.administracion.sueldos;

import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author Antonio Durán
 */
@Named(value = "sueldosBean")
@SessionScoped
public class SueldosBean extends SueldosCenicco implements Serializable {
}
