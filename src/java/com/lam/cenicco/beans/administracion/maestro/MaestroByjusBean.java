/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lam.cenicco.beans.administracion.maestro;

import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author erick
 */
@Named(value = "maestroByjusBean")
@SessionScoped
public class MaestroByjusBean extends MaestroByjusCenicco implements Serializable {
}
