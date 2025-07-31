/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lam.cenicco.beans.nominas.nomina.calculo;

import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author JoséAntonio
 */
@Named(value = "calculonominaBean")
@SessionScoped
public class CalculoNominaBean extends CalculoNominaCenicco implements Serializable {
}
