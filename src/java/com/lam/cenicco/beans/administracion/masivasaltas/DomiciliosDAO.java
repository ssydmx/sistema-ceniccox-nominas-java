package com.lam.cenicco.beans.administracion.masivasaltas;

import com.lam.cenicco.impl.ControladorWS;
import com.lam.cenicco.util.Util;
import com.lam.cenicco.ws.RelacionLaboral;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author erick
 */
public class DomiciliosDAO {

    private final int LONGITUD_ARCHIVO = 3;
    //
    private final int POSICION_NUMERO_EMPLEADO = 0;
    private final int POSICION_CALLE = 1;
    private final int POSICION_COLONIA = 2;
    private final int POSICION_MUNICIPIO = 3;
    private final int POSICION_ESTADO = 4;
    private final int POSICION_CP = 5;
    private final int POSICION_NUMERO_EXTERIOR = 6;
    private final int POSICION_NUMERO_INTERIOR = 7;
    private final int POSICION_DOMICILIO_FISCAL = 8;
//    
    private Map<String, RelacionLaboral> mapaNumeroEmpleados;
//    
    private List<String> lista;

    public DomiciliosDAO(List<String> lista) {
        this.lista = lista;
    }

    public String validarArchivo() {
        String aux = Util.validarLogitud(lista, LONGITUD_ARCHIVO);
//      VALIDANDO CAMPOS REQUERIDOS 
        if (!aux.equals("")) {
            return "Error en minimo de campos requeridos (" + LONGITUD_ARCHIVO + "), en las línea(s): " + aux;
        }
        inicializarMapas();
        aux = validarRequeridos("noempleado", POSICION_NUMERO_EMPLEADO);
        if (!aux.equals("")) {
            return "El Número de Empleado no existe en Cenicco, posición " + (POSICION_NUMERO_EMPLEADO + 1) + " en las línea(s): " + aux;
        }
        /*aux = validarRequeridos("calle", POSICION_CALLE);
        if (!aux.equals("")) {
            return "El campo CALLE se encuentra vacío, posición " + (POSICION_CALLE + 1) + " en las línea(s): " + aux;
        }
        aux = validarRequeridos("colonia", POSICION_COLONIA);
        if (!aux.equals("")) {
            return "El campo COLONIA se encuentra vacío, posición " + (POSICION_COLONIA + 1) + " en las línea(s): " + aux;
        }
        aux = validarRequeridos("municipio", POSICION_MUNICIPIO);
        if (!aux.equals("")) {
            return "El campo MUNICIPIO se encuentra vacío, posición " + (POSICION_MUNICIPIO + 1) + " en las línea(s): " + aux;
        }
        aux = validarRequeridos("estado", POSICION_ESTADO);
        if (!aux.equals("")) {
            return "El campo ESTADO se encuentra vacío, posición " + (POSICION_ESTADO + 1) + " en las línea(s): " + aux;
        }*/
        aux = validarRequeridos("cp", POSICION_CP);
        if (!aux.equals("")) {
            return "El campo CP se encuentra vacío, posición " + (POSICION_CP + 1) + " en las línea(s): " + aux;
        }/*
        aux = validarRequeridos("numeroexterior", POSICION_NUMERO_EXTERIOR);
        if (!aux.equals("")) {
            return "El campo NUMERO EXTERIOR se encuentra vacío, posición " + (POSICION_NUMERO_EXTERIOR + 1) + " en las línea(s): " + aux;
        }
        aux = validarRequeridos("numerointerior", POSICION_NUMERO_INTERIOR);
        if (!aux.equals("")) {
            return "El campo NUMERO INTERIOR se encuentra vacío, posición " + (POSICION_NUMERO_INTERIOR + 1) + " en las línea(s): " + aux;
        }*/
        aux = validarRequeridos("fiscal", POSICION_DOMICILIO_FISCAL);
        if (!aux.equals("")) {
            return "El campo FISCAL se encuentra vacío, posición " + (POSICION_DOMICILIO_FISCAL + 1) + " en las línea(s): " + aux;
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
                case "calle":
                    if (parts[posicion] == null || "".equals(parts[posicion])) {
                        stb.append(i + 1).append(" ");
                    }
                    break;
                case "colonia":
                    if (parts[posicion] == null || "".equals(parts[posicion])) {
                        stb.append(i + 1).append(" ");
                    }
                    break;
                case "municipio":
                    if (parts[posicion] == null || "".equals(parts[posicion])) {
                        stb.append(i + 1).append(" ");
                    }
                    break;
                case "estado":
                    if (parts[posicion] == null || "".equals(parts[posicion])) {
                        stb.append(i + 1).append(" ");
                    }
                    break;
                case "cp":
                    if (parts[posicion] == null || "".equals(parts[posicion])) {
                        stb.append(i + 1).append(" ");
                    }
                    break;
                case "numeroexterior":
                    if (parts[posicion] == null || "".equals(parts[posicion])) {
                        stb.append(i + 1).append(" ");
                    }
                case "numerointerior":
                    if (parts[posicion] == null || "".equals(parts[posicion])) {
                        stb.append(i + 1).append(" ");
                    }
                case "fiscal":
                    if (parts[posicion] == null || "".equals(parts[posicion])) {
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
        List<RelacionLaboral> relaciones = ControladorWS.getInstance().findRelacionesLaboralesActivas();
//        
        Iterator<RelacionLaboral> iter = relaciones.iterator();

        while (iter.hasNext()) {
            RelacionLaboral rl = iter.next();
            if (this.mapaNumeroEmpleados.get(rl.getNumeroempleado()) == null) {
                this.mapaNumeroEmpleados.put(rl.getNumeroempleado(), rl);
            }
        }

    }

    public int getLONGITUD_ARCHIVO() {
        return LONGITUD_ARCHIVO;
    }

    public int getPOSICION_NUMERO_EMPLEADO() {
        return POSICION_NUMERO_EMPLEADO;
    }

    public int getPOSICION_CALLE() {
        return POSICION_CALLE;
    }

    public int getPOSICION_COLONIA() {
        return POSICION_COLONIA;
    }

    public int getPOSICION_MUNICIPIO() {
        return POSICION_MUNICIPIO;
    }

    public int getPOSICION_ESTADO() {
        return POSICION_ESTADO;
    }

    public int getPOSICION_CP() {
        return POSICION_CP;
    }

    public int getPOSICION_NUMERO_EXTERIOR() {
        return POSICION_NUMERO_EXTERIOR;
    }

    public int getPOSICION_NUMERO_INTERIOR() {
        return POSICION_NUMERO_INTERIOR;
    }

    public int getPOSICION_DOMICILIO_FISCAL() {
        return POSICION_DOMICILIO_FISCAL;
    }

    public Map<String, RelacionLaboral> getMapaNumeroEmpleados() {
        return mapaNumeroEmpleados;
    }

    public List<String> getLista() {
        return lista;
    }
}
