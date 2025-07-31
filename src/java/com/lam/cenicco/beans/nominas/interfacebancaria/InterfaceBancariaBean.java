/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lam.cenicco.beans.nominas.interfacebancaria;

import com.lam.cenicco.librerias.utilidades.CeniccoUtil;
import com.lam.cenicco.util.Util;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;

/**
 *
 * @author JoséAntonio
 */
@Named(value = "interfacebancariaBean")
@SessionScoped
public class InterfaceBancariaBean implements Serializable {

    private String tipoProceso;
    private String grupoPago;
    private String periodo;
    private List<String> registros;
//    
    private String informacion;

    public InterfaceBancariaBean() {
        if (this.registros == null) {
            this.registros = new ArrayList<>();
        }
    }

    public void ejecutarProceso() {
        FacesMessage msg = null;
        String nombreArchivo = this.tipoProceso.trim().toUpperCase() + this.grupoPago.trim().toUpperCase() + this.periodo + "2014.txt";
        FileWriter fichero = null;
        PrintWriter pw = null;

        File archivo = null;
        FileReader fr = null;
        BufferedReader br = null;
//        
        this.registros = new ArrayList<>();
        try {
            archivo = new File("C:\\" + nombreArchivo);
            fr = new FileReader(archivo);
            br = new BufferedReader(fr);

            // Lectura del fichero
            fichero = new FileWriter("C:\\Users\\JoséAntonio\\Documents\\" + this.tipoProceso.trim().toUpperCase() + " "
                    + this.grupoPago.trim().toUpperCase() + " " + this.periodo + " 2014.txt");
            pw = new PrintWriter(fichero);

            String linea;
            while ((linea = br.readLine()) != null) {
                pw.println(linea);
                this.registros.add(linea);
            }
            this.informacion = CeniccoUtil.getInformacion(this.registros.size());
            msg = Util.getFacesMessage(FacesMessage.SEVERITY_INFO, "Se ejecuto correctamento el proceso");
        } catch (Exception e) {
            e.printStackTrace();
            msg = Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, "No existen datos en el proceso");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public String getTipoProceso() {
        return tipoProceso;
    }

    public void setTipoProceso(String tipoProceso) {
        this.tipoProceso = tipoProceso;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public String getGrupoPago() {
        return grupoPago;
    }

    public void setGrupoPago(String grupoPago) {
        this.grupoPago = grupoPago;
    }

    public List<String> getRegistros() {
        return registros;
    }

    public String getInformacion() {
        return informacion;
    }
}
