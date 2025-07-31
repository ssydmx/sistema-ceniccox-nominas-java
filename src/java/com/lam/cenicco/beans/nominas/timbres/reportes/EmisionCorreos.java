/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lam.cenicco.beans.nominas.timbres.reportes;

import com.lam.cenicco.impl.ControladorWS;
import com.lam.cenicco.mail.ManejadorCorreo;
import com.lam.cenicco.ws.ReciboNomina;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Antonio Durán
 */
public class EmisionCorreos {

//  
    private final Map<String, String> mapamails;
//    
    private final List<ReciboNomina> recibos;
    private List<ReciboNomina> recibosErrores;
//    
    private final String detalle;

    public EmisionCorreos(List<ReciboNomina> recibos) {
//        
        this.mapamails = ControladorWS.getInstance().getMails();
//        
        this.detalle = ControladorWS.getInstance().getMailDetalle();
//        
        this.recibos = recibos;
    }

    public void iniciaproceso() {
//        
        recibosErrores = new ArrayList<>();
        System.out.println("IniciaEmision.... " + new Date());
//        
        Iterator<ReciboNomina> iter = recibos.iterator();
        while (iter.hasNext()) {
//            
            ReciboNomina r = iter.next();
//            
            String mail =
                    mapamails.get(r.getIdRellab().getIdempleado().getIdempleado().toString());
//          
            ManejadorCorreo manejador = new ManejadorCorreo(r, mail);
//            
            boolean isValidate =
                    manejador.enviarCorreoReciboNomina(detalle);
//            
            if (!isValidate) {
                recibosErrores.add(r);
            }
//            
        }
        System.out.println("TerminoEmisión... " + new Date());
    }

    public List<ReciboNomina> getRecibosErrores() {
        return recibosErrores;
    }
}