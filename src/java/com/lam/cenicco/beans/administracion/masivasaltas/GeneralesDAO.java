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
public class GeneralesDAO {

    private static final int LONGITUD_ARCHIVO = 9;
    //
    private final int POSICION_NUMERO_EMPLEADO = 0;
    private final int POSICION_APELLIDO_PATERNO = 1;
    private final int POSICION_APELLIDO_MATERNO = 2;
    private final int POSICION_NOMBRE = 3;
    private final int POSICION_SEXO = 4;
    private final int POSICION_ESTADO_CIVIL = 5;
    private final int POSICION_FECHA_NACIMIENTO = 6;
    private final int POSICION_NSS = 7;
    private final int POSICION_RFC = 8;
    private final int POSICION_CURP = 9;
    private final int POSICION_EMAIL = 10;
    private final int POSICION_LUGAR_NACIMIENTO = 11;
//    
    private Map<String, RelacionLaboral> mapaNumeroEmpleados;
    private Map<String, RelacionLaboral> mapaRfc;
    private Map<String, RelacionLaboral> mapaNss;
    private Map<String, RelacionLaboral> mapaCurp;
//    
    private List<String> lista;

//
    public GeneralesDAO(List<String> lista) {
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
//        
        aux = Util.validarRequeridos(lista, POSICION_APELLIDO_PATERNO);
        if (!aux.equals("")) {
            return "Error campo requerido Apellido Paterno posicion " + (POSICION_APELLIDO_PATERNO + 1) + ", en las línea(s): " + aux;
        }
        aux = Util.validarRequeridos(lista, POSICION_NOMBRE);
        if (!aux.equals("")) {
            return "Error campo requerido Nombre posicion " + (POSICION_NOMBRE + 1) + ", en las línea(s): " + aux;
        }
        aux = Util.validarRequeridos(lista, POSICION_SEXO);
        if (!aux.equals("")) {
            return "Error campo requerido Sexo posicion " + (POSICION_SEXO + 1) + ", en las línea(s): " + aux;
        }
//        
        aux = Util.validarRequeridos(lista, POSICION_FECHA_NACIMIENTO);
        if (!aux.equals("")) {
            return "Error campo requerido Fecha Nacimiento posicion " + (POSICION_FECHA_NACIMIENTO + 1) + ", en las línea(s): " + aux;
        }
        aux = Util.validarRequeridos(lista, POSICION_NSS);
        if (!aux.equals("")) {
            return "Error campo requerido NSS posicion " + (POSICION_NSS + 1) + ", en las línea(s): " + aux;
        }
        aux = Util.validarRequeridos(lista, POSICION_RFC);
        if (!aux.equals("")) {
            return "Error campo requerido RFC posicion " + (POSICION_RFC + 1) + ", en las línea(s): " + aux;
        }
        aux = Util.validarRequeridos(lista, POSICION_CURP);
        if (!aux.equals("")) {
            return "Error campo requerido CURP posicion " + (POSICION_CURP + 1) + ", en las línea(s): " + aux;
        }
        aux = Util.validarRequeridos(lista, POSICION_EMAIL);
        if (!aux.equals("")) {
            return "Error campo requerido E-Mail posicion " + (POSICION_EMAIL + 1) + ", en las línea(s): " + aux;
        }
//      VALIDANDO PATRONES
//        aux = Util.validarPatrones(lista, Pattern.compile(Util.REGEX_NUMERO_EMPLEADO), POSICION_NUMERO_EMPLEADO);
//        if (!aux.equals("")) {
//            return "El Número de empleado no coincide en estructura [0-9]{2,5}, posición " + (POSICION_NUMERO_EMPLEADO + 1) + " en las línea(s): " + aux;
//        }
        aux = Util.validarPatrones(lista, Pattern.compile(Util.REGEX_SEXO), POSICION_SEXO);
        if (!aux.equals("")) {
            return "El Sexo no coincide en estructura (H-MASCULINO)|(M-FEMENINIO), posición " + (POSICION_SEXO + 1) + " en las línea(s): " + aux;
        }
        aux = Util.validarPatrones(lista, Pattern.compile(Util.REGEX_EDO_CIVIL), POSICION_ESTADO_CIVIL);
        if (!aux.equals("")) {
            return "El Estado Civil no coincide en estructura (SOLTERO)|(CASADO)|(DIVORCIADO)|(UNIONLIBRE)|(VIUDO)|(SEPARADO), posición " + (POSICION_ESTADO_CIVIL + 1) + " en las línea(s): " + aux;
        }
        aux = Util.validarPatrones(lista, Pattern.compile(Util.REGEX_FECHA), POSICION_FECHA_NACIMIENTO);
        if (!aux.equals("")) {
            return "La Fecha de Nacimiento no coincide en estructura DD/MM/AAAA, posición " + (POSICION_FECHA_NACIMIENTO + 1) + " en las línea(s): " + aux;
        }
        aux = Util.validarPatrones(lista, Pattern.compile(Util.REGEX_NSS), POSICION_NSS);
        if (!aux.equals("")) {
            return "El N.S.S. no coincide en estructura [0-9]{11}, posición " + (POSICION_NSS + 1) + " en las línea(s): " + aux;
        }
        aux = Util.validarPatrones(lista, Pattern.compile(Util.REGEX_RFC), POSICION_RFC);
        if (!aux.equals("")) {
            return "El R.F.C. no coincide en estructura, posición " + (POSICION_RFC + 1) + " en las línea(s): " + aux;
        }
        aux = Util.validarPatrones(lista, Pattern.compile(Util.REGEX_CURP), POSICION_CURP);
        if (!aux.equals("")) {
            return "El CURP no coincide en estructura, posición " + (POSICION_CURP + 1) + " en las línea(s): " + aux;
        }
        aux = Util.validarPatrones(lista, Pattern.compile(Util.REGEX_E_MAIL, Pattern.CASE_INSENSITIVE), POSICION_EMAIL);
        if (!aux.equals("")) {
            return "El E-Mail no coincide en estructura, posición " + (POSICION_EMAIL + 1) + " en las línea(s): " + aux;
        }
//      VALIDANDO VALORES REPETIDOS       
        inicializarMapas();
//        
        aux = validarRepetidos("noempleado", POSICION_NUMERO_EMPLEADO);
        if (!aux.equals("")) {
            return "El Número de Empleado ya se encuentra en Cenicco en las linea(s): " + aux;
        }
        aux = validarRepetidos("curp", POSICION_CURP);
        if (!aux.equals("")) {
            return "El C.U.R.P. ya se encuentra en Cenicco en las linea(s): " + aux;
        }
        aux = validarRepetidos("rfc", POSICION_RFC);
        if (!aux.equals("")) {
            return "El R.F.C. ya se encuentra en Cenicco en las linea(s): " + aux;
        }
        aux = validarRepetidos("nss", POSICION_NSS);
        if (!aux.equals("")) {
            return "El N.S.S. ya se encuentra en Cenicco en las linea(s): " + aux;
        }
        return "";

    }

    private String validarRepetidos(String tag, int posicion) {
        StringBuilder stb = new StringBuilder();
        for (int i = 0; i < lista.size(); i++) {
            String[] parts = lista.get(i).split(",");
//            
            switch (tag) {
                case "noempleado":
                    if (mapaNumeroEmpleados.get(parts[posicion]) != null) {
                        stb.append(i + 1).append(" ");
                    }
                    break;
                case "curp":
                    if (mapaCurp.get(parts[posicion]) != null) {
                        stb.append(i + 1).append(" ");
                    }
                    break;
                case "rfc":
                    if (mapaRfc.get(parts[posicion]) != null) {
                        stb.append(i + 1).append(" ");
                    }
                    break;
                case "nss":
                    if (mapaNss.get(parts[posicion]) != null) {
                        stb.append(i + 1).append(" ");
                    }
                    break;

            }

        }
        return stb.toString();
    }

    private void inicializarMapas() {
        this.mapaNumeroEmpleados = new HashMap<>();
        this.mapaRfc = new HashMap<>();
        this.mapaNss = new HashMap<>();
        this.mapaCurp = new HashMap<>();
//        
        List<RelacionLaboral> relaciones = ControladorWS.getInstance().findRelacionesLaborales();
//        
        Iterator<RelacionLaboral> iter = relaciones.iterator();
        while (iter.hasNext()) {
            RelacionLaboral rl = iter.next();

            if (this.mapaNumeroEmpleados.get(rl.getNumeroempleado()) == null) {
                this.mapaNumeroEmpleados.put(rl.getNumeroempleado(), rl);
            }

            if (this.mapaRfc.get(rl.getIdempleado().getRfc()) == null) {
                this.mapaRfc.put(rl.getIdempleado().getRfc(), rl);
            }

            if (this.mapaNss.get(rl.getIdempleado().getAfiliacion()) == null) {
                this.mapaNss.put(rl.getIdempleado().getAfiliacion(), rl);
            }

            if (this.mapaCurp.get(rl.getIdempleado().getCurp()) == null) {
                this.mapaCurp.put(rl.getIdempleado().getCurp(), rl);
            }
        }
    }

    public int getPOSICION_NUMERO_EMPLEADO() {
        return POSICION_NUMERO_EMPLEADO;
    }

    public int getPOSICION_APELLIDO_PATERNO() {
        return POSICION_APELLIDO_PATERNO;
    }

    public int getPOSICION_NOMBRE() {
        return POSICION_NOMBRE;
    }

    public int getPOSICION_SEXO() {
        return POSICION_SEXO;
    }

    public int getPOSICION_ESTADO_CIVIL() {
        return POSICION_ESTADO_CIVIL;
    }

    public int getPOSICION_FECHA_NACIMIENTO() {
        return POSICION_FECHA_NACIMIENTO;
    }

    public int getPOSICION_NSS() {
        return POSICION_NSS;
    }

    public int getPOSICION_RFC() {
        return POSICION_RFC;
    }

    public int getPOSICION_CURP() {
        return POSICION_CURP;
    }

    public int getPOSICION_APELLIDO_MATERNO() {
        return POSICION_APELLIDO_MATERNO;
    }

    public int getPOSICION_LUGAR_NACIMIENTO() {
        return POSICION_LUGAR_NACIMIENTO;
    }

    public int getPOSICION_EMAIL() {
        return POSICION_EMAIL;
    }
}
