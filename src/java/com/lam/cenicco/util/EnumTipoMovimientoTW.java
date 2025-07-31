/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lam.cenicco.util;

/**
 *
 * @author Cenicco
 */
public enum EnumTipoMovimientoTW {

    REINGRESO("8"),
    MODIFICACION_SALARIO("7"),
    BAJA("2");
    private String id;

    private EnumTipoMovimientoTW(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }    
}
