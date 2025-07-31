package com.lam.cenicco.beans.administracion.masivasaltas;

import com.lam.cenicco.impl.ControladorWS;
import com.lam.cenicco.util.Parametros;
import com.lam.cenicco.util.Util;
import com.lam.cenicco.ws.Estructura;
import java.util.List;
import java.util.regex.Pattern;

/**
 *
 * @author erick
 */
public class VariablesDAO {

    private final int LONGITUD_ARCHIVO = 3;
    //
    private final int POSICION_NUMERO_EMPLEADO = 0;
    private final int POSICION_VARIABLE = 1;
    private final int POSICION_VALOR = 2;
//    
    private final List<String> lista;
    private List<Estructura> estructuras;

    public VariablesDAO(List<String> lista) {
        this.lista = lista;
        estructuras = ControladorWS.getInstance().findEstructuraByNivel(Parametros.ESTRUCTURA_NIVEL);
    }

    public String validarArchivo() {
        String aux = Util.validarLogitud(lista, LONGITUD_ARCHIVO);
        //      VALIDANDO CAMPOS REQUERIDOS 
        if (!aux.equals("")) {
            return "Error en minimo de campos requeridos (" + LONGITUD_ARCHIVO + "), en las línea(s): " + aux;
        }
        //       VALIDANDO PATRONES
//        aux = Util.validarPatrones(lista, Pattern.compile(Util.REGEX_NUMERO_EMPLEADO), POSICION_NUMERO_EMPLEADO);
//        if (!aux.equals("")) {
//            return "El Número de empleado no coincide en estructura [0-9]{2,5}, posición " + (POSICION_NUMERO_EMPLEADO + 1) + " en las línea(s): " + aux;
//        }
        return "";
    }
}
