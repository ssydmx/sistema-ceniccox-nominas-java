/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lam.cenicco.beans.administracion.masivasaltas;

import com.lam.cenicco.impl.ControladorWS;
import com.lam.cenicco.util.Util;
import com.lam.cenicco.ws.RelacionLaboral;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 *
 * @author Antonio Durán
 */
public class BajasDAO {

    private final int LONGITUD_ARCHIVO = 3;
//    
    private final int POSICION_NUMERO_EMPLEADO = 0;
    private final int POSICION_FECHA_BAJA = 1;
    private final int POSICION_CAUSA_BAJA = 2;
//    
    private Map<String, RelacionLaboral> mapaNumeroEmpleados;
//    
    private List<String> lista;

    public BajasDAO(List<String> lista) {
        this.lista = lista;
    }

    public String validarArchivo() {
        String aux = Util.validarLogitud(lista, LONGITUD_ARCHIVO);
//      VALIDANDO CAMPOS REQUERIDOS 
        if (!aux.equals("")) {
            return "Error en minimo de campos requeridos (" + LONGITUD_ARCHIVO + "), en las línea(s): " + aux;
        }
//        
        aux = Util.validarRequeridos(lista, POSICION_NUMERO_EMPLEADO);
        if (!aux.equals("")) {
            return "Error campo requerido Número de Empleado posicion " + (POSICION_NUMERO_EMPLEADO + 1) + ", en las línea(s): " + aux;
        }
        aux = Util.validarRequeridos(lista, POSICION_FECHA_BAJA);
        if (!aux.equals("")) {
            return "Error campo requerido Fecha de Baja posicion " + (POSICION_FECHA_BAJA + 1) + ", en las línea(s): " + aux;
        }
        aux = Util.validarPatrones(lista, Pattern.compile(Util.REGEX_FECHA), POSICION_FECHA_BAJA);
        if (!aux.equals("")) {
            return "La Fecha de Baja no coincide en estructura DD/MM/AAAA, posición " + (POSICION_FECHA_BAJA + 1) + " en las línea(s): " + aux;
        }
        aux = validarRequeridos("causabaja", POSICION_CAUSA_BAJA);
        if (!aux.equals("")) {
            return "Error campo requerido Causa Baja posición" + (POSICION_FECHA_BAJA + 1) + " en las línea(s): " + aux;
        }
        aux = validarCausaBaja("causabaja", POSICION_CAUSA_BAJA);
        if (!aux.equals("")) {
            return "Error Causa Baja no registrada en Cenicco posición " + (POSICION_FECHA_BAJA + 1) + " en las línea(s): " + aux;
        }
        //validar causa baja
        inicializarMapas();
        aux = validarRequeridos("noempleado", POSICION_NUMERO_EMPLEADO);
        if (!aux.equals("")) {
            return "El Número de Empleado no existe en Cenicco, posición " + (POSICION_NUMERO_EMPLEADO + 1) + " en las línea(s): " + aux;
        }
        return "";
    }

    private String validarRequeridos(String tag, int posicion) {
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
                case "causabaja":
                    if (parts[posicion] == null && parts[posicion].equals("")) {
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
        List<RelacionLaboral> relaciones = ControladorWS.getInstance().findRelacionesLaborales();
//        
        Iterator<RelacionLaboral> iter = relaciones.iterator();
        while (iter.hasNext()) {
            RelacionLaboral rl = iter.next();
            if (this.mapaNumeroEmpleados.get(rl.getNumeroempleado()) == null) {
                this.mapaNumeroEmpleados.put(rl.getNumeroempleado(), rl);
            }
        }
    }

    private String validarCausaBaja(String tag, int posicion) {
        StringBuilder stb = new StringBuilder();
        for (int i = 0; i < lista.size(); i++) {
            String[] parts = lista.get(i).split(",");
//            
            switch (tag) {
                case "causabaja":
                    switch (parts[posicion].toUpperCase()) {
                        case "ABANDONOEMPLEO":
                        case "DEFUNCION":
                        case "OTRAS":
                        case "PENSION":
                        case "RECISIONCONTRATO":
                        case "SEPARACIONVOLUNTARIA":
                        case "TERMINOCONTRATO":
                        case "AUSENTISMO":
                        case "CONFLICTOSLABORALES":
                        case "RENUNCIATRABAJADOR":
                        case "CLAUSURA":
                        case "JUBILACION":
                            stb.append("");
                            break;

                        default:
                            stb.append(i + 1).append(" ");
                            break;

                    }
                    break;
            }

        }
        return stb.toString();
    }

    public RelacionLaboral getRelacionLaboral(String parts[]) {
        return mapaNumeroEmpleados.get(parts[POSICION_NUMERO_EMPLEADO]);
    }

    public int getPOSICION_FECHA_BAJA() {
        return POSICION_FECHA_BAJA;
    }

    public int getPOSICION_CAUSA_BAJA() {
        return POSICION_CAUSA_BAJA;
    }
    
}