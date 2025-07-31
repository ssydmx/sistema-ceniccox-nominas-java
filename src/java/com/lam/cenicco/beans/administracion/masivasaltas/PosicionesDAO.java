/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lam.cenicco.beans.administracion.masivasaltas;

import com.lam.cenicco.impl.ControladorWS;
import com.lam.cenicco.util.Util;
import com.lam.cenicco.ws.CentroCostos;
import com.lam.cenicco.ws.Departamento;
import com.lam.cenicco.ws.Puesto;
import com.lam.cenicco.ws.RelacionLaboral;
import com.lam.cenicco.ws.TurnoLaboral;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 *
 * @author Antonio Durán
 */
public class PosicionesDAO {

    private final int LONGITUD_ARCHIVO = 2;
//    
    private final int POSICION_NUMERO_EMPLEADO = 0;
    private final int POSICION_CCO = 1;
    private final int POSICION_PUESTO = 2;
    private final int POSICION_DEPARTAMENTO = 3;
    private final int POSICION_JEFE_DIRECTO = 4;
    private final int POSICION_NOMBRE_TURNO_LABORAL = 5;
//    
    private Map<String, RelacionLaboral> mapaNumeroEmpleados;
    private Map<String, CentroCostos> mapaCCos;
    private Map<String, Puesto> mapaPuesto;
    private Map<String, Departamento> mapaDepartamento;
    private Map<String, TurnoLaboral> mapaTurnosLaborales;
//    
    private List<String> lista;

    public PosicionesDAO(List<String> lista) {
        this.lista = lista;
    }

    public String validarArchivo() {
        String aux = Util.validarLogitud(lista, LONGITUD_ARCHIVO);

        if (!aux.equals("")) {
            return "Error en minimo de campos requeridos (" + LONGITUD_ARCHIVO + "), en las línea(s): " + aux;
        }

//        aux = Util.validarPatrones(lista, Pattern.compile(Util.REGEX_NUMERO_EMPLEADO), POSICION_NUMERO_EMPLEADO);
//        if (!aux.equals("")) {
//            return "El Número de empleado no coincide en estructura [0-9]{2,5}, posición " + (POSICION_NUMERO_EMPLEADO + 1) + " en las línea(s): " + aux;
//        }
//        
        inicializarMapas();
        try {
            aux = validarRequeridos("noempleado", POSICION_NUMERO_EMPLEADO);
            if (!aux.equals("")) {
                return "El Número de Empleado no existe en Cenicco, posición " + (POSICION_NUMERO_EMPLEADO + 1) + " en las línea(s): " + aux;
            }
            aux = validarRequeridos("cco", POSICION_CCO);
            if (!aux.equals("")) {
                return "La Clave CCO no existe en Cenicco, posición " + (POSICION_CCO + 1) + " en las línea(s): " + aux;
            }
            aux = validarRequeridos("puesto", POSICION_PUESTO);
            if (!aux.equals("")) {
                return "La Clave Puesto no existe en Cenicco, posición " + (POSICION_PUESTO + 1) + " en las línea(s): " + aux;
            }
            aux = validarRequeridos("depto", POSICION_DEPARTAMENTO);
            if (!aux.equals("")) {
                return "La Clave Departamento no existe en Cenicco, posición " + (POSICION_DEPARTAMENTO + 1) + " en las línea(s): " + aux;
            }
            aux = validarRequeridos("jefe", POSICION_JEFE_DIRECTO);
            if (!aux.equals("")) {
                return "La Número de Jefe Directo no existe en Cenicco, posición " + (POSICION_JEFE_DIRECTO + 1) + " en las línea(s): " + aux;
            }
        } catch (Exception e) {
        }
        return "";
    }

    private String validarRequeridos(String tag, int posicion) throws Exception {
        StringBuilder stb = new StringBuilder();
        for (int i = 0; i < lista.size(); i++) {
            String[] parts = lista.get(i).split(",");
//            
            switch (tag) {
                case "noempleado":
                    if (mapaNumeroEmpleados.get(parts[posicion]) == null) {
                        stb.append(i + 1).append(" ");
                    }
                    break;
                case "cco":
                    if (!parts[posicion].equals("")
                            && mapaCCos.get(parts[posicion]) == null) {
                        stb.append(i + 1).append(" ");
                    }
                    break;
                case "puesto":
                    if (!parts[posicion].equals("")
                            && mapaPuesto.get(parts[posicion]) == null) {
                        stb.append(i + 1).append(" ");
                    }
                    break;
                case "depto":
                    if (!parts[posicion].equals("")
                            && mapaDepartamento.get(parts[posicion]) == null) {
                        stb.append(i + 1).append(" ");
                    }
                    break;
                case "jefe":
                    if (parts.length > 4 && !parts[posicion].equals("")
                            && mapaNumeroEmpleados.get(parts[posicion]) == null) {
                        stb.append(i + 1).append(" ");
                    }
                    break;
            }

        }
        return stb.toString();
    }

    private void inicializarMapas() {
        this.mapaNumeroEmpleados = new HashMap<>();
//        
        Iterator<RelacionLaboral> iter = ControladorWS.getInstance().findRelacionesLaborales().iterator();
        while (iter.hasNext()) {
            RelacionLaboral rl = iter.next();
            if (this.mapaNumeroEmpleados.get(rl.getNumeroempleado()) == null) {
                this.mapaNumeroEmpleados.put(rl.getNumeroempleado(), rl);
            }
        }
//        
        this.mapaCCos = new HashMap<>();
        for (CentroCostos cco : ControladorWS.getInstance().getCentroCostos()) {
            if (this.mapaCCos.get(cco.getCentroCosto()) == null) {
                this.mapaCCos.put(cco.getCentroCosto(), cco);
            }
        }
//        
        this.mapaPuesto = new HashMap<>();
        for (Puesto p : ControladorWS.getInstance().getPuestos()) {
            if (this.mapaPuesto.get(p.getPuesto()) == null) {
                this.mapaPuesto.put(p.getPuesto(), p);
            }
        }
//        
        this.mapaDepartamento = new HashMap<>();
        for (Departamento d : ControladorWS.getInstance().getDepartamentos()) {
            if (this.mapaDepartamento.get(d.getDepartamento()) == null) {
                this.mapaDepartamento.put(d.getDepartamento(), d);
            }
        }
//
        this.mapaTurnosLaborales = new HashMap<>();
        for (TurnoLaboral t : ControladorWS.getInstance().getTurnosLaborales() ) {
            if (this.mapaTurnosLaborales.get(t.getNombre())== null) {
                this.mapaTurnosLaborales.put(t.getNombre(), t);                
            }         
        }
    }

    public RelacionLaboral getRelacionLaboral(String[] parts) {
        return mapaNumeroEmpleados.get(parts[POSICION_NUMERO_EMPLEADO]);
    }

    public RelacionLaboral getJefeDirecto(String[] parts) throws Exception {
        return mapaNumeroEmpleados.get(parts[POSICION_JEFE_DIRECTO]);
    }

    public CentroCostos getCCo(String[] parts) throws Exception {
        return mapaCCos.get(parts[POSICION_CCO]);
    }

//    public SubArea getSubarea(String[] parts) throws Exception {
//        return mapaSubareas.get(parts[POSICION_SUBAREA]);
//    }
//    public Area getArea(String[] parts) throws Exception {
//        return mapaArea.get(parts[POSICION_AREA]);
//    }
    public Puesto getPuesto(String[] parts) throws Exception {
        return mapaPuesto.get(parts[POSICION_PUESTO]);
    }

    public Departamento getDepartamento(String[] parts) throws Exception {
        return mapaDepartamento.get(parts[POSICION_DEPARTAMENTO]);
    }
    
    public TurnoLaboral getTurnoLaboral (String[] parts) throws Exception{
        return mapaTurnosLaborales.get(parts[POSICION_NOMBRE_TURNO_LABORAL]);
    }

    public int getPOSICION_NUMERO_EMPLEADO() {
        return POSICION_NUMERO_EMPLEADO;
    }

    public int getPOSICION_CCO() {
        return POSICION_CCO;
    }

    public int getPOSICION_PUESTO() {
        return POSICION_PUESTO;
    }

    public int getPOSICION_DEPARTAMENTO() {
        return POSICION_DEPARTAMENTO;
    }

    public int getPOSICION_JEFE_DIRECTO() {
        return POSICION_JEFE_DIRECTO;
    }

    public int getPOSICION_NOMBRE_TURNO_LABORAL() {
        return POSICION_NOMBRE_TURNO_LABORAL;
    }
}
