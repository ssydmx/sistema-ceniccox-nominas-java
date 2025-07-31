/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lam.cenicco.beans.administracion.catalogo;

import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author JoséAntonio
 */
@Named(value = "catalogoempleadoBean")
@SessionScoped
public class CatalogoEmpleadoBean extends CatalogoEmpleadoCenicco implements Serializable {

    public CatalogoEmpleadoBean() {
    }
    
}
