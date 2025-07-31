/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lam.cenicco.beans.nominas.vacaciones.calendariovacaciones;

import com.lam.cenicco.dao.ProcesoDAO;
import com.lam.cenicco.impl.ControladorWS;
import com.lam.cenicco.util.Util;
import com.lam.cenicco.ws.Vacaciones;
import java.util.Iterator;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.event.ActionEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleModel;

/**
 *
 * @author Antonio Durán
 */
public class CalendarioVacacionesCenicco implements ProcesoDAO<Vacaciones> {

    private ScheduleModel eventModel;
//    
    private String numeroempleado;
//    
    private Integer selectedestatus;
    private Integer selectedgrupopago;

    public CalendarioVacacionesCenicco() {
        if (eventModel == null) {
            eventModel = new DefaultScheduleModel();
        }
    }

    @Override
    public void init() {
    }

    @Override
    public void consultar(ActionEvent event) {
        System.out.println("Selection... " + numeroempleado + " | " + selectedestatus + " | " + selectedgrupopago);

        List<Vacaciones> calendario = ControladorWS.getInstance().getCalendarioVacaciones(numeroempleado, selectedgrupopago, selectedestatus);
        eventModel = new DefaultScheduleModel();
        Iterator<Vacaciones> iter = calendario.iterator();
        while (iter.hasNext()) {
            Vacaciones v = iter.next();
            String label = v.getSolicitud().getRellab().getNumeroempleado() + " | " + v.getSolicitud().getRellab().getIdgrupopago().getGrupopago()
                    + " | " + Util.getNombre(v.getSolicitud().getRellab().getIdempleado());
            String style = "";
//            
            switch (v.getEstatus()) {
                case 0:
                    style = "style_schedule_cancelada";
                    break;
                case 1:
                    style = "style_schedule_autorizada";
                    break;
                case 99:
                    style = "style_schedule_solicitada";
                    break;
            }
//            
            eventModel.addEvent(new DefaultScheduleEvent(label, v.getFecha().toGregorianCalendar().getTime(),
                    v.getFecha().toGregorianCalendar().getTime(), style));

        }

    }

    @Override
    public void limpiar(ActionEvent event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void create(ActionEvent event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void edit(ActionEvent event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void delete(Vacaciones obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void listenerSelected() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void generarMsg(FacesMessage msg, boolean isValidate) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getInformacion() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public ScheduleModel getEventModel() {
        return eventModel;
    }

    public void setEventModel(ScheduleModel eventModel) {
        this.eventModel = eventModel;
    }

    public String getNumeroempleado() {
        return numeroempleado;
    }

    public void setNumeroempleado(String numeroempleado) {
        this.numeroempleado = numeroempleado;
    }

    public Integer getSelectedestatus() {
        return selectedestatus;
    }

    public void setSelectedestatus(Integer selectedestatus) {
        this.selectedestatus = selectedestatus;
    }

    public Integer getSelectedgrupopago() {
        return selectedgrupopago;
    }

    public void setSelectedgrupopago(Integer selectedgrupopago) {
        this.selectedgrupopago = selectedgrupopago;
    }
}
