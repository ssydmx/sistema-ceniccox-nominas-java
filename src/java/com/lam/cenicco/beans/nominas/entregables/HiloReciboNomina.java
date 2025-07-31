/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lam.cenicco.beans.nominas.entregables;

import com.facturainteligente.integracion.Respuesta;
import com.lam.cenicco.beans.facturainteligente.FacturaCenicco;
import com.lam.cenicco.beans.facturainteligente.FacturaIntegracion;
import com.lam.cenicco.impl.ControladorWS;
import com.lam.cenicco.librerias.utilidades.CeniccoUtil;
import com.lam.cenicco.to.FacturaTO;
import com.lam.cenicco.to.RespuestaTO;
import com.lam.cenicco.util.ControladorSesiones;
import com.lam.cenicco.util.EtiquetasSat;
import com.lam.cenicco.util.Util;
import com.lam.cenicco.util.recibos.GenerarRecibo;
import com.lam.cenicco.ws.CifrasNomina;
import com.lam.cenicco.ws.ConceptoAcumulado;
import com.lam.cenicco.ws.GrupoPago;
import com.lam.cenicco.ws.Periodo;
import com.lam.cenicco.ws.ReciboNomina;
import com.lam.cenicco.ws.SaldoVacaciones;
import com.lam.cenicco.ws.VistaSaldoCreditos;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Antonio Durán
 */
public class HiloReciboNomina {
////    

    private List<RespuestaTO> respuesta;
//    
    private int TOPE_FACTURAS;
    private String servicio;
//

    public HiloReciboNomina() {
        this.servicio = ControladorSesiones.getInstance().getUsuarioSession().getServicio();
        if (this.servicio.equals("YAMANAGOLD")) {
            TOPE_FACTURAS = 5;
        } else {
            TOPE_FACTURAS = 10;
        }
    }

    public boolean iniciarProceso(List<CifrasNomina> listadonomina, GrupoPago gp, Periodo periodo, Integer idtipoproceso,
            Map<String, List<VistaSaldoCreditos>> saldocreditos, Map<String, ConceptoAcumulado> saldofondoahorro,
            Map<String, ConceptoAcumulado> saldocajaahorro, Map<String, SaldoVacaciones> saldovacaciones,
            boolean aplicasaldos, boolean retimbrado) throws FileNotFoundException, GeneralSecurityException, IOException {
//        
        GenerarRecibo generador = new GenerarRecibo();
//        
        FacturaIntegracion facturacionpac = new FacturaIntegracion();
//      
        this.respuesta = new ArrayList<>();
//        
        List<ReciboNomina> recibosacrear;
//        
        List<ReciboNomina> recibosaeditar;
//      

        Date hoy = new Date();

        List<FacturaTO> timbres = Util.getXmlsPac(listadonomina, gp, idtipoproceso, saldofondoahorro,
                saldocreditos, saldocajaahorro, hoy, retimbrado);
//        
        System.out.println("TimbreIniciaProceso... " + timbres.size() + " | " + hoy);
//        
        String user = ControladorWS.getInstance().getTimbresProveedorUsuario();
        String password = ControladorWS.getInstance().getTimbresProveedorPassword();
//        
        int cont = 1;
        while (!timbres.isEmpty()) {
//            
            List<FacturaTO> temp = timbres.size() < TOPE_FACTURAS ? timbres.subList(0, timbres.size())
                    : timbres.subList(0, TOPE_FACTURAS);
//            
            recibosacrear = new ArrayList<>();
            recibosaeditar = new ArrayList<>();
//               
            System.out.println("TimbreProceso... " + temp.size() + " | " + new Date());
            Iterator<FacturaTO> iter = temp.iterator();
//            
            while (iter.hasNext()) {
                FacturaTO factura = iter.next();
//            
                if (factura.isDuplicado()) {
//                
                    factura.setRespuesta(new RespuestaTO(factura.getUuid(), "Ya fue timbrado", false));
//                
                } else {
//                  
                    if (user != null && !user.equals("")) {
                        if (password != null && !password.equals("")) {
                            //Aqui se manda a timbrar
                            //Respuesta r = facturacionpac.sellar(user, password, factura.getCadena());
//Pruebas de timbrado 4.0
                            com.app.appfacturainteligente.RespuestaTFD33 rp = facturacionpac.sellarv4(user, password, factura.getCadena());

                            //factura.setRespuesta(new RespuestaTO(r, factura.getRelacionlaboral().getNumeroempleado().toString()));
                            factura.setRespuesta(new RespuestaTO(rp, factura.getRelacionlaboral().getNumeroempleado().toString()));
                        } else {
                            factura.setRespuesta(new RespuestaTO(null, "No se puede timbrar, no existe contraseña de sistema de timbrado", false));
                        }
                    } else {
                        factura.setRespuesta(new RespuestaTO(null, "No se puede timbrar, no existe usuario de sistema de timbrado", false));
                    }
//                    
                }
//                
                System.out.println("TimbreRespuesta... " + factura.getRespuesta().getErrorEspecifico() + " | " + factura.getRespuesta().getErrorGeneral() + " | " + cont);
//                

//                
                if (factura.isDuplicado()) {
                    this.respuesta.add(factura.getRespuesta());
                    byte[] xml = factura.getXml();

                    try {
                        factura = generador.convertirxml(factura, new String(xml, "utf-8"));
                    } catch (UnsupportedEncodingException ex) {
                        Logger.getLogger(HiloReciboNomina.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    byte[] qr = FacturaCenicco.getInstance().generarQR(factura.getRfcemisor(), factura.getRfcreceptor(),
                            factura.getUuid(), factura.getTotal(), factura.getSellocfd().substring(factura.getSellocfd().length() - 8));
//                
                    byte[] pdf = generador.getPDF(factura, gp, periodo, qr, aplicasaldos);
//                
                    factura.setRespuesta(null);
                    ReciboNomina recibonomina = new ReciboNomina();
                    recibonomina.setIdPeriodo(periodo);
                    recibonomina.setIdrecibocfdi(factura.getIdrecibonomina());
                    recibonomina.setArchivoPdf(pdf);
                    recibonomina.setTimbre(qr);
                    recibosaeditar.add(recibonomina);
//                
                    cont++;
                    continue;
                }
//                
                if (factura.getRespuesta().isExito()) {
//                    
                    System.out.println("TimbreRecibo... " + factura.getRespuesta().getUuid());

                    byte[] xml =
                            factura.getXml() != null
                            ? factura.getXml()
                            : factura.getRespuesta().getXmlTimbrado();
                    try {
                        factura = generador.convertirxml(factura, new String(xml, "utf-8"));
                    } catch (UnsupportedEncodingException ex) {
                        Logger.getLogger(HiloReciboNomina.class.getName()).log(Level.SEVERE, null, ex);
                    }
//                  
                    byte[] qr = FacturaCenicco.getInstance().generarQR(factura.getRfcemisor(), factura.getRfcreceptor(),
                            factura.getRespuesta().getUuid(), factura.getTotal(), factura.getSellocfd().substring(factura.getSellocfd().length() - 8));
//                    
                    byte[] pdf = generador.getPDF(factura, gp, periodo, qr, aplicasaldos);
//                    
                    ReciboNomina recibo = new ReciboNomina();
//                    
                    recibo.setTimbre(qr);
                    recibo.setIdRellab(factura.getRelacionlaboral());
                    recibo.setArchivoPdf(pdf);
                    recibo.setArchivoXml(factura.getRespuesta().getXmlTimbrado());
                    recibo.setCantidad(1.0);
                    recibo.setDescripcion(factura.getDescripcion());
                    recibo.setDescuento(factura.getOtrasdeducciones());
                    recibo.setDiaspagados(periodo.getIdtipoproceso().getPeriodicidad().doubleValue());
                    recibo.setFechaPago(periodo.getFechapago());
                    recibo.setFechaemision(CeniccoUtil.getDateToXmlCalendar(hoy));
                    recibo.setTimbrefecha(CeniccoUtil.getDateToXmlCalendar(hoy));
                    recibo.setFormaPago(EtiquetasSat.PAGO_EXHIBICION.getConcepto());
                    recibo.setImporte(factura.getTotal());
                    recibo.setTotal(factura.getTotal());
                    recibo.setIsr(factura.getIsr());
                    recibo.setPreciounitario(factura.getSubtotal());
                    recibo.setRegimenfiscal(gp.getIdcompania().getRegimenfiscal());
                    recibo.setSd(factura.getRelacionlaboral().getSalarioDiario());
                    recibo.setSdi(factura.getRelacionlaboral().getSalarioDiarioIntegrado());
                    recibo.setSubtotal(factura.getSubtotal());
                    recibo.setTimbreversion(EtiquetasSat.VERSION.getConcepto());
                    recibo.setTotaldeduccionexento(factura.getTotaldeduccion());
                    recibo.setTotalpercepcionexento(factura.getPercepcionexento());
                    recibo.setTotalpercepciongravado(factura.getPercepciongravado());
                    recibo.setUnidad(EtiquetasSat.UNIDAD.getConcepto());
                    recibo.setUuid(factura.getRespuesta().getUuid());
                    recibo.setIdPeriodo(periodo);
                    recibo.setIdRellab(factura.getRelacionlaboral());
                    recibo.setEstatus(1);
//                    
                    factura.getRespuesta().setUuid(factura.getRelacionlaboral().getNumeroempleado() + "/" + factura.getRespuesta().getUuid());
//                    
                    recibosacrear.add(recibo);

                } else {
                    factura.getRespuesta().setUuid(factura.getRelacionlaboral().getNumeroempleado());
                }
//                
                this.respuesta.add(factura.getRespuesta());
                cont++;
//
            }
//            
            boolean exitoso = true;
            if (!recibosacrear.isEmpty()) {
                exitoso = ControladorWS.getInstance().createRecibos(recibosacrear, gp.getIdgrupopago(), periodo.getIdperiodo());
            }
            System.out.println("TimbreExitosoCrear... " + exitoso + " | " + recibosacrear.size());
            if (!exitoso) {
                return false;
            }
            if (!recibosaeditar.isEmpty()) {
                exitoso = ControladorWS.getInstance().editPdfRecibos(recibosaeditar);
            }
            System.out.println("TimbreExitosoEditar... " + exitoso + " | " + recibosaeditar.size());
            if (!exitoso) {
                return false;
            }
//            
            timbres.removeAll(temp);
            try {
                System.out.println("TimbreDormir... " + new Date());
                Thread.sleep(5000);
            } catch (Exception e) {
                System.out.println(e);
            }
//            
        }
        return true;
    }

    public List<RespuestaTO> getRespuesta() {
        return respuesta;
    }
}
