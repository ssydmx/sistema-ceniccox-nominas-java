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
public class AcumuladosDAO {

    private final int LONGITUD_ARCHIVO = 2;
//    
    private final int POSICION_NUMERO_EMPLEADO = 0;
    private final int POSICION_TIEMPO = 1;
    private final int POSICION_BG = 2;
    private final int POSICION_ISR = 3;
    private final int POSICION_SUBSIDIO = 4;
    private final int POSICION_BG_AGUINALDO = 5;
    private final int POSICION_ISR_AGUINALDO = 6;
//    
    private Map<String, RelacionLaboral> mapaNumeroEmpleados;
//    
    private final Integer idgrupopago;
//    
    private final List<String> lista;

    public AcumuladosDAO(List<String> lista, Integer idgrupopago) {
        this.lista = lista;
        this.idgrupopago = idgrupopago;
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
        aux = Util.validarPatrones(lista, Pattern.compile(Util.REGEX_ENTERO), POSICION_TIEMPO);
        if (!aux.equals("")) {
            return "El Tiempo no coincide en estructura [NÚMERO ENTERO], posición " + (POSICION_TIEMPO + 1) + " en las línea(s): " + aux;
        }
        aux = Util.validarPatrones(lista, Pattern.compile(Util.REGEX_DECIMAL), POSICION_BG);
        if (!aux.equals("")) {
            return "La Base Gravada no coincide en estructura decimal, posición " + (POSICION_BG + 1) + " en las línea(s): " + aux;
        }
        aux = Util.validarPatrones(lista, Pattern.compile(Util.REGEX_DECIMAL), POSICION_ISR);
        if (!aux.equals("")) {
            return "El I.S.R Retenido no coincide en estructura decimal, posición " + (POSICION_ISR + 1) + " en las línea(s): " + aux;
        }
        aux = Util.validarPatrones(lista, Pattern.compile(Util.REGEX_DECIMAL), POSICION_SUBSIDIO);
        if (!aux.equals("")) {
            return "El Subsidio no coincide en estructura decimal, posición " + (POSICION_SUBSIDIO + 1) + " en las línea(s): " + aux;
        }
        aux = Util.validarPatrones(lista, Pattern.compile(Util.REGEX_DECIMAL), POSICION_BG_AGUINALDO);
        if (!aux.equals("")) {
            return "La Base Gravada Aguinaldo no coincide en estructura decimal, posición " + (POSICION_BG_AGUINALDO + 1) + " en las línea(s): " + aux;
        }
        aux = Util.validarPatrones(lista, Pattern.compile(Util.REGEX_DECIMAL), POSICION_ISR_AGUINALDO);
        if (!aux.equals("")) {
            return "El I.S.R. Aguinaldo no coincide en estructura decimal, posición " + (POSICION_ISR_AGUINALDO + 1) + " en las línea(s): " + aux;
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
        List<RelacionLaboral> relaciones = ControladorWS.getInstance().findRelacionesLaboralesByGrupoPago(idgrupopago, null);
//        
        Iterator<RelacionLaboral> iter = relaciones.iterator();
        while (iter.hasNext()) {
            RelacionLaboral rl = iter.next();
            if (this.mapaNumeroEmpleados.get(rl.getNumeroempleado()) == null) {
                this.mapaNumeroEmpleados.put(rl.getNumeroempleado(), rl);
            }
        }
//        
    }

    public RelacionLaboral getRelacionLaboral(String parts[]) {
        return mapaNumeroEmpleados.get(parts[POSICION_NUMERO_EMPLEADO]);
    }

    public int getPOSICION_NUMERO_EMPLEADO() {
        return POSICION_NUMERO_EMPLEADO;
    }

    public int getPOSICION_TIEMPO() {
        return POSICION_TIEMPO;
    }

    public int getPOSICION_BG() {
        return POSICION_BG;
    }

    public int getPOSICION_ISR() {
        return POSICION_ISR;
    }

    public int getPOSICION_SUBSIDIO() {
        return POSICION_SUBSIDIO;
    }

    public int getPOSICION_BG_AGUINALDO() {
        return POSICION_BG_AGUINALDO;
    }

    public int getPOSICION_ISR_AGUINALDO() {
        return POSICION_ISR_AGUINALDO;
    }
}
