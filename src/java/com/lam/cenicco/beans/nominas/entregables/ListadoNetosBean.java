/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lam.cenicco.beans.nominas.entregables;

import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author JoséAntonio
 */
@Named(value = "listadonetosBean")
@SessionScoped
public class ListadoNetosBean extends ListadoNetosCenicco implements Serializable {
}
