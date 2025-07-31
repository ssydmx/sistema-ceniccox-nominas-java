/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lam.cenicco.beans.administracion.turnos;

import com.lam.cenicco.dao.ProcesoDAO;
import com.lam.cenicco.impl.ControladorWS;
import com.lam.cenicco.util.Util;
import com.lam.cenicco.ws.HorarioLaboral;
import com.lam.cenicco.ws.JornadaLaboral;
import com.lam.cenicco.ws.TurnoLaboral;
import com.mysql.jdbc.StringUtils;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import org.primefaces.context.RequestContext;
import com.lam.cenicco.util.ParametrosReportes;
import java.util.stream.Collectors;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author Cenicco
 */
public class TurnosCenicco implements ProcesoDAO<TurnoLaboral> {

    private static String[] dias = new String[]{"Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sabado", "Domingo"};
    //
    private List<TurnoLaboral> turnos = null;
    private List<TurnoLaboral> filterTurnos = null;
    private TurnoLaboral turnoCreate = null;
    private List<HorarioLaboral> horariosCreate = null;
    //
    private TurnoLaboral turnoLaboralDelete = null;
    //
    private List<JornadaLaboral> jornadaslaborales = null;
    private Map<String, JornadaLaboral> mapJornadas = new HashMap<>();
    //
    private String filterValueTurno;
    //
    private UploadedFile archivo;

    @PostConstruct
    @Override
    public void init() {
        this.consultar(null);
        jornadaslaborales = ControladorWS.getInstance().getJornadasLaborales();
        for (JornadaLaboral jl : jornadaslaborales) {
            mapJornadas.put(jl.getNombre().toUpperCase(), jl);
        }
    }

    @Override
    public void consultar(ActionEvent event) {
        turnos = ControladorWS.getInstance().getTurnosLaborales();
        filterTurnos = turnos;
    }

    @Override
    public void limpiar(ActionEvent event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void create(ActionEvent event) {
        boolean isDiurna = false;
        boolean isNocturno = false;
        boolean isMixta = false;
        Map<String, HashMap<String, Double>> horariosMixtos = new HashMap<>();
        for (HorarioLaboral hl : horariosCreate) {
            System.out.println("o---------- Nuevo horario " + hl.getHorarioInicial1() + " - " + hl.getHorarioFinal2() + " ----------");
            if (!StringUtils.isNullOrEmpty(hl.getHorarioInicial1())
                    && !StringUtils.isNullOrEmpty(hl.getHorarioFinal2())) {
                HashMap<String, Double> horario = new HashMap<>();
                for (JornadaLaboral jl : jornadaslaborales) {
                    System.out.println("o---------- Nuevo jornada " + jl.getNombre() + " ----------");
                    String[] partsJornadaHoraInicial = jl.getHorarioInicial().split(":");
                    Calendar calJornadaHoraInicial = Calendar.getInstance();
                    calJornadaHoraInicial.set(Calendar.HOUR_OF_DAY, Integer.parseInt(partsJornadaHoraInicial[0]));
                    calJornadaHoraInicial.set(Calendar.MINUTE, Integer.parseInt(partsJornadaHoraInicial[1]));

                    String[] partsJornadaHoraFinal = jl.getHorarioFinal().split(":");
                    Calendar calJornadaHoraFinal = Calendar.getInstance();
                    calJornadaHoraFinal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(partsJornadaHoraFinal[0]));
                    calJornadaHoraFinal.set(Calendar.MINUTE, Integer.parseInt(partsJornadaHoraFinal[1]));

                    String[] partsJornadaHoraInicialAnterior = jl.getHorarioInicial().split(":");
                    Calendar calJornadaHoraInicialAnterior = Calendar.getInstance();
                    calJornadaHoraInicialAnterior.set(Calendar.HOUR_OF_DAY, Integer.parseInt(partsJornadaHoraInicialAnterior[0]));
                    calJornadaHoraInicialAnterior.set(Calendar.MINUTE, Integer.parseInt(partsJornadaHoraInicialAnterior[1]));

                    String[] partsJornadaHoraFinalAnterior = jl.getHorarioFinal().split(":");
                    Calendar calJornadaHoraFinalAnterior = Calendar.getInstance();
                    calJornadaHoraFinalAnterior.set(Calendar.HOUR_OF_DAY, Integer.parseInt(partsJornadaHoraFinalAnterior[0]));
                    calJornadaHoraFinalAnterior.set(Calendar.MINUTE, Integer.parseInt(partsJornadaHoraFinalAnterior[1]));

                    SimpleDateFormat objSDF = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    System.out.println("o---------- Horario jornada " + objSDF.format(calJornadaHoraInicial.getTime()) + " - " + objSDF.format(calJornadaHoraFinal.getTime()) + " ----------");
                    if (jl.getNombre().equalsIgnoreCase("Nocturna")) {
                        System.out.println("o---------- HF Jornada Nocturna... se añade un dia");
                        calJornadaHoraFinal.add(Calendar.DAY_OF_YEAR, 1);

                        System.out.println("o---------- HI Jornada Nocturna anterior... se quita un dia");
                        calJornadaHoraInicialAnterior.add(Calendar.DAY_OF_YEAR, -1);
                    }
                    System.out.println("o---------- Horario jornada anterior " + objSDF.format(calJornadaHoraInicialAnterior.getTime()) + " - " + objSDF.format(calJornadaHoraFinalAnterior.getTime()) + " ----------");

                    String[] partsHi1 = hl.getHorarioInicial1().split(":");
                    Calendar calHi1 = Calendar.getInstance();
                    calHi1.set(Calendar.HOUR_OF_DAY, Integer.parseInt(partsHi1[0]));
                    calHi1.set(Calendar.MINUTE, Integer.parseInt(partsHi1[1]));

                    String[] partsHf2 = hl.getHorarioFinal2().split(":");
                    Calendar calHf2 = Calendar.getInstance();
                    calHf2.set(Calendar.HOUR_OF_DAY, Integer.parseInt(partsHf2[0]));
                    calHf2.set(Calendar.MINUTE, Integer.parseInt(partsHf2[1]));

                    System.out.println("o---------- Horario reg. no modificado " + objSDF.format(calHi1.getTime()) + " - " + objSDF.format(calHf2.getTime()) + " ----------");
                    if (!calHi1.before(calHf2)) {
                        System.out.println("o---------- HF Horario reg. es nocturna... se añade un dia");
                        calHf2.add(Calendar.DAY_OF_YEAR, 1);
                    }
                    System.out.println("o---------- Horario reg. modificado " + objSDF.format(calHi1.getTime()) + " - " + objSDF.format(calHf2.getTime()) + " ----------");

                    if ((calJornadaHoraInicial.before(calHi1) || calJornadaHoraInicial.equals(calHi1))
                            && (calJornadaHoraFinal.after(calHf2) || calJornadaHoraFinal.equals(calHf2))) {
                        System.out.println("Entre turno " + jl.getNombre());
                        if (jl.getNombre().equalsIgnoreCase("Diurna")) {
                            isDiurna = true;
                            break;
                        }
                        if (jl.getNombre().equalsIgnoreCase("Nocturna")) {
                            isNocturno = true;
                            break;
                        }
                        if (jl.getNombre().equalsIgnoreCase("Mixta")) {
                            isMixta = true;
                            break;
                        }
                    } else {
                        System.out.println("o---------- Validacion especial Horario ----------");
                        Double horasJornada = null;
                        if (jl.getNombre().equalsIgnoreCase("Diurna")) {
                            if ((calJornadaHoraInicial.before(calHi1) || calJornadaHoraInicial.equals(calHi1))
                                    && (calJornadaHoraFinal.after(calHi1) || calJornadaHoraFinal.equals(calHi1))) {
                                Long diferencia = calJornadaHoraFinal.getTime().getTime() - calHi1.getTime().getTime();
                                horasJornada = diferencia.doubleValue() / (60 * 60 * 1000);
                                System.out.println("o---------- Jornada " + jl.getNombre() + " Horas Ocupadas: " + horasJornada + " ----------");

                            }
                            if ((calJornadaHoraInicial.before(calHf2) || calJornadaHoraInicial.equals(calHf2))
                                    && (calJornadaHoraFinal.after(calHf2) || calJornadaHoraFinal.equals(calHf2))) {
                                Long diferencia = calHf2.getTime().getTime() - calJornadaHoraInicial.getTime().getTime();
                                horasJornada = diferencia.doubleValue() / (60 * 60 * 1000);
                                System.out.println("o---------- Jornada " + jl.getNombre() + " Horas Ocupadas: " + horasJornada + " ----------");
                            }
                        }

                        if (jl.getNombre().equalsIgnoreCase("Nocturna")) {
                            if ((calJornadaHoraInicialAnterior.before(calHi1) || calJornadaHoraInicialAnterior.equals(calHi1))
                                    && (calJornadaHoraFinalAnterior.after(calHi1) || calJornadaHoraFinalAnterior.equals(calHi1))) {
                                Long diferencia = calJornadaHoraFinalAnterior.getTime().getTime() - calHi1.getTime().getTime();
                                horasJornada = diferencia.doubleValue() / (60 * 60 * 1000);
                                System.out.println("o---------- Jornada " + jl.getNombre() + " Horas Ocupadas: " + horasJornada + " ----------");
                            }

                            if ((calJornadaHoraInicial.before(calHf2) || calJornadaHoraInicial.equals(calHf2))
                                    && (calJornadaHoraFinal.after(calHf2) || calJornadaHoraFinal.equals(calHf2))) {
                                Long diferencia = calHf2.getTime().getTime() - calJornadaHoraInicial.getTime().getTime();
                                horasJornada = diferencia.doubleValue() / (60 * 60 * 1000);
                                System.out.println("o---------- Jornada " + jl.getNombre() + " Horas Ocupadas: " + horasJornada + " ----------");
                            }
                        }

                        if (horasJornada != null) {
                            double horasJornadaRedondeadas = Math.round(horasJornada * 100.0) / 100.0;
                            horario.put(jl.getNombre(), horasJornadaRedondeadas);
                        }
                    }
                }

                if (!horario.isEmpty()) {
                    horariosMixtos.put(hl.getHorarioInicial1() + " - " + hl.getHorarioFinal2(), horario);
                }
            }
        }

        Double limiteMixta = 3.5;
        for (Map.Entry<String, HashMap<String, Double>> hm : horariosMixtos.entrySet()) {
            Double horasDiurna = hm.getValue().get("Diurna");
            Double horasNocturna = hm.getValue().get("Nocturna");
            System.out.println("o----------" + hm.getKey() + " ---------- Horas Diurna " + horasDiurna + " Horas Nocturna: " + horasNocturna + " ----------");

            if (horasNocturna != null) {
                if (horasNocturna >= limiteMixta) {
                    isNocturno = true;
                } else {
                    isMixta = true;
                }
            }
        }

        if (isMixta) {
            turnoCreate.setJornadaLaboral(mapJornadas.get("MIXTA"));//Mixta
        } else {
            if (isDiurna) {
                turnoCreate.setJornadaLaboral(mapJornadas.get("DIURNA"));//Diurna
                if (isNocturno) {
                    turnoCreate.setJornadaLaboral(mapJornadas.get("MIXTA"));//Mixta
                }
            } else {
                if (isNocturno) {
                    turnoCreate.setJornadaLaboral(mapJornadas.get("NOCTURNA"));//Nocturna
                }
            }
        }
        turnoCreate.getHorarios().addAll(horariosCreate);
        boolean isValidate = ControladorWS.getInstance().crearTurnoLaboral(turnoCreate);

        FacesMessage msg = isValidate ? Util.getFacesMessage(FacesMessage.SEVERITY_INFO, "El turno " + turnoCreate.getNombre() + " fu\u00e9 creado exitosamente!")
                : Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, "Error al crear el turno " + turnoCreate.getNombre());

        this.generarMsg(msg, isValidate);
        this.consultar(null);
    }

    @Override
    public void edit(ActionEvent event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void delete(TurnoLaboral obj) {
        System.out.println("Eliminando turno laboral :::::::::::::::::");
        List<TurnoLaboral> deleteTurnos = new ArrayList<>();
        deleteTurnos.add(this.turnoLaboralDelete);
        boolean isValidate = ControladorWS.getInstance().eliminarTurnoLaboral(deleteTurnos);

        FacesMessage msg = isValidate ? Util.getFacesMessage(FacesMessage.SEVERITY_INFO, "El turno fu\u00e9 eliminado exitosamente!")
                : Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, "Error al eliminar el turno");

        this.generarMsg(msg, isValidate);
        this.consultar(null);
    }

    @Override
    public void listenerSelected() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void generarMsg(FacesMessage msg, boolean isValidate) {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().addMessage(null, msg);
        context.addCallbackParam("isValidate", isValidate);
        if (isValidate) {
            this.consultar(null);
        }
    }

    @Override
    public String getInformacion() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public TurnoLaboral getTurnoCreate() {
        return turnoCreate;
    }

    public void setTurnoCreate(TurnoLaboral turnoCreate) {
        this.turnoCreate = turnoCreate;
    }

    public List<TurnoLaboral> getTurnos() {
        return turnos;
    }

    public void setTurnos(List<TurnoLaboral> turnos) {
        this.turnos = turnos;
    }

    public List<HorarioLaboral> getHorariosCreate() {
        return horariosCreate;
    }

    public void setHorariosCreate(List<HorarioLaboral> horariosCreate) {
        this.horariosCreate = horariosCreate;
    }

    public TurnoLaboral getTurnoLaboralDelete() {
        return turnoLaboralDelete;
    }

    public void setTurnoLaboralDelete(TurnoLaboral turnoLaboralDelete) {
        this.turnoLaboralDelete = turnoLaboralDelete;
    }

    public String getFilterValueTurno() {
        return filterValueTurno;
    }

    public void setFilterValueTurno(String filterValueTurno) {
        this.filterValueTurno = filterValueTurno;
    }

    public UploadedFile getArchivo() {
        return archivo;
    }

    public void setArchivo(UploadedFile archivo) {
        this.archivo = archivo;
    }

    public void openModalCreateTurno(ActionEvent event) {
        turnoCreate = new TurnoLaboral();
        horariosCreate = new ArrayList<>();

        int i = 0;
        for (String dia : dias) {
            HorarioLaboral tb = new HorarioLaboral();
            tb.setNumeroDia(++i);
            tb.setDia(dia);
            horariosCreate.add(tb);
        }

        RequestContext context = RequestContext.getCurrentInstance();
        context.update("formCreateTurno");
        context.execute("PF('createTurnoDialog').show();");
    }

    public void openModalDeleteTurno(TurnoLaboral turnoLaboral) {
        this.turnoLaboralDelete = turnoLaboral;

        RequestContext context = RequestContext.getCurrentInstance();
        context.update("formConfimDeleteTurno");
        context.execute("PF('confirmDeleteTurno').show();");
    }

public void filterListTurno() {
    if (!StringUtils.isNullOrEmpty(filterValueTurno)) {
        List<TurnoLaboral> turnos = filterTurnos;
        // Usando Java 8 Streams para filtrar la lista
        List<TurnoLaboral> filteredList = turnos.stream()
            .filter(turno -> turno.getNombre().equals(filterValueTurno))
            .collect(Collectors.toList());
        setTurnos(filteredList);
    } else {
        this.consultar(null);
    }
}


    public void descargarLayout(ActionEvent event) {
        List<String> lineas = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        sb.append("Nombre,");
        sb.append("Descripcion,");
        for (String dia : dias) {
            sb.append(dia + " (24hrs)Horario E - S(HH:MM-HH:MM),");
            sb.append(dia + " (24hrs)Horario Comida(HH:MM-HH:MM),");
        }
        lineas.add(sb.toString());
        Util.escribirFichero(lineas, "LayoutTurnos", ParametrosReportes.ARCHIVO_CSV);
    }

    public void fileUploadListenerGlobalTurnos(FileUploadEvent event) {
        archivo = event.getFile();
        boolean isValidate = false;
        List<String> lista = Util.convertFileToStr(this.archivo);
        for (int i = 0; i < lista.size(); i++) {
            String[] parts = lista.get(i).split(",");
            TurnoLaboral tl = new TurnoLaboral();
            List<HorarioLaboral> hls = new ArrayList<>();

            String nombre = parts[0].trim().toUpperCase();
            String descripcion = parts[1].trim().toUpperCase();
            tl.setNombre(nombre);
            tl.setDescripcion(descripcion);
            System.out.println("Nombre " + tl.getNombre() + " | Descripcion " + tl.getDescripcion());

            int n = 0;
            int m = 0;
            for (String dia : dias) {
                HorarioLaboral hl = new HorarioLaboral();
                hl.setNumeroDia(++n);
                hl.setDia(dia);
                try {
                    String horario = parts[m + 2].trim();
                    String horarioComida = parts[m + 3].trim();

                    String[] h = horario.split("-");
                    hl.setHorarioInicial1(h[0]);
                    hl.setHorarioFinal2(h[1]);

                    String[] hc = horarioComida.split("-");
                    hl.setHorarioFinal1(hc[0]);
                    hl.setHorarioInicial2(hc[1]);
                } catch (ArrayIndexOutOfBoundsException e) {
                }
                m += 2;
                System.out.println("NDia " + hl.getNumeroDia() + " | Dia " + hl.getDia() + " | HorarioInicial1 " + hl.getHorarioInicial1() + " | HorarioFinal1 " + hl.getHorarioFinal1() + " | HorarioInicial2 " + hl.getHorarioInicial2() + " | HorarioFinal2 " + hl.getHorarioFinal2());
                hls.add(hl);
            }

            this.turnoCreate = tl;
            this.horariosCreate = hls;
            this.create(null);
        }
    }
}
