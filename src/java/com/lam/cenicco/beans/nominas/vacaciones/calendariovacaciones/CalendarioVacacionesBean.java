/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lam.cenicco.beans.nominas.vacaciones.calendariovacaciones;

import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author Antonio Durán
 */
@Named(value = "calendariovacacionesBean")
@SessionScoped
public class CalendarioVacacionesBean extends CalendarioVacacionesCenicco implements Serializable {
}
