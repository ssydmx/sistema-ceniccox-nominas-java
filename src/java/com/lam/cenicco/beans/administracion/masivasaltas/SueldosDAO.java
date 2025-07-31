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
public class SueldosDAO {

    private final int LONGITUD_ARCHIVO = 3;
//    
    private final int POSICION_NUMERO_EMPLEADO = 0;
    private final int POSICION_SD = 1;
    private final int POSICION_FECHA_SD = 2;
    private final int POSICION_SDI = 3;
    private final int POSICION_FECHA_SDI = 4;
//    
    private Map<String, RelacionLaboral> mapaNumeroEmpleados;
//    
    private List<String> lista;

    public SueldosDAO(List<String> lista) {
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
        aux = Util.validarRequeridos(lista, POSICION_SDI);
        if (!aux.equals("")) {
            return "Error campo requerido Salario Diario Integrado posicion " + (POSICION_SDI + 1) + ", en las línea(s): " + aux;
        }
        aux = Util.validarRequeridos(lista, POSICION_FECHA_SDI);
        if (!aux.equals("")) {
            return "Error campo requerido Fecha Salario Diario Integrado posicion " + (POSICION_FECHA_SDI + 1) + ", en las línea(s): " + aux;
        }
//        
//        aux = Util.validarPatrones(lista, Pattern.compile(Util.REGEX_NUMERO_EMPLEADO), POSICION_NUMERO_EMPLEADO);
//        if (!aux.equals("")) {
//            return "El Número de empleado no coincide en estructura [0-9]{2,5}, posición " + (POSICION_NUMERO_EMPLEADO + 1) + " en las línea(s): " + aux;
//        }
        aux = Util.validarPatrones(lista, Pattern.compile(Util.REGEX_DECIMAL), POSICION_SD);
        if (!aux.equals("")) {
            return "La Salario Diario no coincide en estructura decimal, posición " + (POSICION_SD + 1) + " en las línea(s): " + aux;
        }
        aux = Util.validarPatrones(lista, Pattern.compile(Util.REGEX_FECHA), POSICION_FECHA_SD);
        if (!aux.equals("")) {
            return "La Fecha de SD no coincide en estructura DD/MM/AAAA, posición " + (POSICION_FECHA_SD + 1) + " en las línea(s): " + aux;
        }
        aux = Util.validarPatrones(lista, Pattern.compile(Util.REGEX_DECIMAL), POSICION_SDI);
        if (!aux.equals("")) {
            return "La Salario Diario Integrado no coincide en estructura decimal, posición " + (POSICION_SDI + 1) + " en las línea(s): " + aux;
        }
        aux = Util.validarPatrones(lista, Pattern.compile(Util.REGEX_FECHA), POSICION_FECHA_SDI);
        if (!aux.equals("")) {
            return "La Fecha de SD no coincide en estructura DD/MM/AAAA, posición " + (POSICION_FECHA_SDI + 1) + " en las línea(s): " + aux;
        }
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

    public int getPOSICION_NUMERO_EMPLEADO() {
        return POSICION_NUMERO_EMPLEADO;
    }

    public int getPOSICION_SD() {
        return POSICION_SD;
    }

    public int getPOSICION_FECHA_SD() {
        return POSICION_FECHA_SD;
    }

    public int getPOSICION_SDI() {
        return POSICION_SDI;
    }

    public int getPOSICION_FECHA_SDI() {
        return POSICION_FECHA_SDI;
    }

    public RelacionLaboral getRelacionLaboral(String parts[]) {
        return mapaNumeroEmpleados.get(parts[POSICION_NUMERO_EMPLEADO]);
    }
}
