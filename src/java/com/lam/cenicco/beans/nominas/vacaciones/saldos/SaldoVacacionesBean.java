/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lam.cenicco.beans.nominas.vacaciones.saldos;

import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author Antonio Durán
 */
@Named(value = "saldovacacionesBean")
@SessionScoped
public class SaldoVacacionesBean extends SaldoVacacionesCenicco implements Serializable {
}
