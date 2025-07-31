/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lam.cenicco.beans.administracion.turnos;

import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author Cenicco
 */
@Named(value = "turnosBean")
@SessionScoped
public class TurnosBean extends TurnosCenicco implements Serializable {

    public TurnosBean() {
    }
}
