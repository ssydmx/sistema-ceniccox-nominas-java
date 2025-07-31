/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lam.cenicco.beans.administracion.masivasaltas;

import com.lam.cenicco.impl.ControladorWS;
import com.lam.cenicco.util.Util;
import com.lam.cenicco.ws.Banco;
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
public class CuentasBancariasDAO {

    private static final int LONGITUD_ARCHIVO = 5;
//    
    private final int POSICION_NUMERO_EMPLEADO = 0;
    private final int POSICION_BANCO = 1;
    private final int POSICION_CUENTA_BANCARIA = 2;
    private final int POSICION_CLABE_BANCARIA = 3;
    private final int POSICION_BANCO_EMPLEADO = 4;
//    
    private Map<String, RelacionLaboral> mapaNumeroEmpleados;
    private Map<String, Banco> mapaBancos;
//    
    private List<String> lista;

    public CuentasBancariasDAO(List<String> lista) {
        this.lista = lista;
    }

    public String validarArchivo() {
        String aux = Util.validarLogitud(lista, LONGITUD_ARCHIVO);
        if (!aux.equals("")) {
            return "Error en minimo de campos requeridos (" + LONGITUD_ARCHIVO + "), en las línea(s): " + aux;
        }
//      VALIDANDO PATRONES 
//        aux = Util.validarPatrones(lista, Pattern.compile(Util.REGEX_NUMERO_EMPLEADO), POSICION_NUMERO_EMPLEADO);
//        if (!aux.equals("")) {
//            return "El Número de empleado no coincide en estructura [0-9]{2,5}, posición " + (POSICION_NUMERO_EMPLEADO + 1) + " en las línea(s): " + aux;
//        }
        aux = Util.validarPatrones(lista, Pattern.compile(Util.REGEX_CUENTA_BANCARIA), POSICION_CUENTA_BANCARIA);
        if (!aux.equals("")) {
            aux = Util.validarPatrones(lista, Pattern.compile(Util.REGEX_CUENTA_BANCARIA_BANBAJIO), POSICION_CUENTA_BANCARIA);
            if (!aux.equals("")) {
                aux = Util.validarPatrones(lista, Pattern.compile(Util.REGEX_CUENTA_BANCARIA_BANBAJIO), POSICION_CUENTA_BANCARIA);
                if (!aux.equals("")) {
                    aux = Util.validarPatrones(lista, Pattern.compile(Util.REGEX_CUENTA_NUMER_TARJETA), POSICION_CUENTA_BANCARIA);
                    if (!aux.equals("")) {
                        aux = Util.validarPatrones(lista, Pattern.compile(Util.REGEX_CUENTA_BANCARIA_BANAMEX), POSICION_CUENTA_BANCARIA);
                        if (!aux.equals("")) {
                            return "La Cuenta Bancaria no coincide en estructura [0]|[0-9]{10} o {11} o {12} o {16} o {18}, posición " + (POSICION_CUENTA_BANCARIA + 1) + " en las línea(s): " + aux;
                        }

                    }
                }

            }
        }
        aux = Util.validarPatrones(lista, Pattern.compile(Util.REGEX_CLABE_BANCARIA), POSICION_CLABE_BANCARIA);
        if (!aux.equals("")) {
            return "La CLABE Bancaria no coincide en estructura [0]|[0-9]{18}, posición " + (POSICION_CLABE_BANCARIA + 1) + " en las línea(s): " + aux;
        }
        inicializarMapas();
        aux = validarRequeridos("noempleado", POSICION_NUMERO_EMPLEADO);
        if (!aux.equals("")) {
            return "El Número de Empleado no existe en Cenicco, posición " + (POSICION_NUMERO_EMPLEADO + 1) + " en las línea(s): " + aux;
        }
        aux = validarRequeridos("banco", POSICION_BANCO);
        if (!aux.equals("")) {
            return "El Banco no existe en Cenicco, posición " + (POSICION_BANCO + 1) + " en las línea(s): " + aux;
        }
        aux = validarRequeridos("banco", POSICION_BANCO_EMPLEADO);
        if (!aux.equals("")) {
            return "El Banco no existe en Cenicco, posición " + (POSICION_BANCO_EMPLEADO + 1) + " en las línea(s): " + aux;
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
                case "banco":
                    if (mapaBancos.get(parts[posicion]) == null) {
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

        this.mapaBancos = new HashMap<>();
        List<Banco> bancos = ControladorWS.getInstance().getBancos();
        Iterator<Banco> iterbanco = bancos.iterator();
        while (iterbanco.hasNext()) {
            Banco b = iterbanco.next();
            if (this.mapaBancos.get(b.getBanco()) == null) {
                this.mapaBancos.put(b.getNombre(), b);
            }

        }
    }

    public int getPOSICION_NUMERO_EMPLEADO() {
        return POSICION_NUMERO_EMPLEADO;
    }

    public int getPOSICION_BANCO() {
        return POSICION_BANCO;
    }

    public int getPOSICION_CUENTA_BANCARIA() {
        return POSICION_CUENTA_BANCARIA;
    }

    public int getPOSICION_CLABE_BANCARIA() {
        return POSICION_CLABE_BANCARIA;
    }

    public Map<String, RelacionLaboral> getMapaNumeroEmpleados() {
        return mapaNumeroEmpleados;
    }

    public Map<String, Banco> getMapaBancos() {
        return mapaBancos;
    }

    public int getPOSICION_BANCO_EMPLEADO() {
        return POSICION_BANCO_EMPLEADO;
    }
}
