/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lam.cenicco.beans.administracion.masivasaltas;

import com.lam.cenicco.impl.ControladorWS;
import com.lam.cenicco.util.Util;
import com.lam.cenicco.ws.Compania;
import com.lam.cenicco.ws.GrupoPago;
import com.lam.cenicco.ws.RegistroPatronal;
import com.lam.cenicco.ws.RelacionGrupoPagoUsuario;
import com.lam.cenicco.ws.RelacionLaboral;
import com.lam.cenicco.ws.SistemaAntiguedad;
import com.lam.cenicco.ws.TipoRelacionLaboral;
import com.lam.cenicco.ws.TipoSalarioIdse;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 *
 * @author Antonio Durán
 */
public class RelacionesDAO {

    private final int LONGITUD_ARCHIVO = 10;
    //
    private final int POSICION_NUMERO_EMPLEADO = 0;
    private final int POSICION_ESTATUS = 1;
    private final int POSICION_FECHA_ANTIGUEDAD = 2;
    private final int POSICION_FECHA_INGRESO = 3;
    private final int POSICION_COMPANIA = 4;
    private final int POSICION_GRUPO_PAGO = 5;
    private final int POSICION_REGISTRO_PATRONAL = 6;
    private final int POSICION_SISTEMA_ANTIGUEDAD = 7;
    private final int POSICION_TIPO_RELLAB = 8;
    private final int POSICION_SD = 9;
//
    private Map<String, RelacionLaboral> mapaNumeroEmpleados;
    private Map<String, Compania> mapaCompanias;
    private Map<String, GrupoPago> mapaGrupoPago;
    private Map<String, RegistroPatronal> mapaRegistroPatronal;
    private Map<String, SistemaAntiguedad> mapaSistemaAntiguedad;
    private Map<String, TipoRelacionLaboral> mapaTipoRelacionLaboral;
    private Map<String, TipoSalarioIdse> mapaTipoSueldoIdse;
//    
    private List<String> lista;

    public RelacionesDAO(List<String> lista) {
        this.lista = lista;
    }

    public String validarArchivo() {
        String aux = Util.validarLogitud(lista, LONGITUD_ARCHIVO);
//      VALIDANDO CAMPOS REQUERIDOS 
        if (!aux.equals("")) {
            return "Error en minimo de campos requeridos (" + LONGITUD_ARCHIVO + "), en las línea(s): " + aux;
        }
//      VALIDANDO PATRONES
//        aux = Util.validarPatrones(lista, Pattern.compile(Util.REGEX_NUMERO_EMPLEADO), POSICION_NUMERO_EMPLEADO);
//        if (!aux.equals("")) {
//            return "El Número de empleado no coincide en estructura [0-9]{2,9}, posición " + (POSICION_NUMERO_EMPLEADO + 1) + " en las línea(s): " + aux;
//        }
        aux = Util.validarPatrones(lista, Pattern.compile(Util.REGEX_ESTATUS), POSICION_ESTATUS);
        if (!aux.equals("")) {
            return "El Estatus no coincide en estructura [ACTIVO|INACTIVO|REGISTRADO]{2,5}, posición " + (POSICION_ESTATUS + 1) + " en las línea(s): " + aux;
        }
        aux = Util.validarPatrones(lista, Pattern.compile(Util.REGEX_FECHA), POSICION_FECHA_ANTIGUEDAD);
        if (!aux.equals("")) {
            return "La Fecha de Antiguedad no coincide en estructura DD/MM/AAAA, posición " + (POSICION_FECHA_ANTIGUEDAD + 1) + " en las línea(s): " + aux;
        }
        aux = Util.validarPatrones(lista, Pattern.compile(Util.REGEX_FECHA), POSICION_FECHA_INGRESO);
        if (!aux.equals("")) {
            return "La Fecha de Ingreso no coincide en estructura DD/MM/AAAA, posición " + (POSICION_FECHA_INGRESO + 1) + " en las línea(s): " + aux;
        }
        aux = Util.validarPatrones(lista, Pattern.compile(Util.REGEX_DECIMAL), POSICION_SD);
        if (!aux.equals("")) {
            return "La Salario Diario no coincide en estructura decimal, posición " + (POSICION_SD + 1) + " en las línea(s): " + aux;
        }
//        
        inicializarMapas();
        aux = validarRequeridos("noempleado", POSICION_NUMERO_EMPLEADO);
        if (!aux.equals("")) {
            return "El Número de Empleado no existe en Cenicco, posición " + (POSICION_NUMERO_EMPLEADO + 1) + " en las línea(s): " + aux;
        }
        aux = validarRequeridos("compania", POSICION_COMPANIA);
        if (!aux.equals("")) {
            return "La Compañía no existe en Cenicco, posición " + (POSICION_COMPANIA + 1) + " en las línea(s): " + aux;
        }
        aux = validarRequeridos("grupopago", POSICION_GRUPO_PAGO);
        if (!aux.equals("")) {
            return "El Grupo de Pago no existe en Cenicco, posición " + (POSICION_GRUPO_PAGO + 1) + " en las línea(s): " + aux;
        }
        aux = validarRequeridos("registropatronal", POSICION_REGISTRO_PATRONAL);
        if (!aux.equals("")) {
            return "El Registro Patronal no existe en Cenicco, posición " + (POSICION_REGISTRO_PATRONAL + 1) + " en las línea(s): " + aux;
        }
        aux = validarRequeridos("sistema", POSICION_SISTEMA_ANTIGUEDAD);
        if (!aux.equals("")) {
            return "El Sistema de Antiguedad no existe en Cenicco, posición " + (POSICION_SISTEMA_ANTIGUEDAD + 1) + " en las línea(s): " + aux;
        }
        aux = validarRequeridos("tiporellab", POSICION_TIPO_RELLAB);
        if (!aux.equals("")) {
            return "El Tipo de Relación Laboral no existe en Cenicco, posición " + (POSICION_TIPO_RELLAB + 1) + " en las línea(s): " + aux;
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
                case "compania":
                    if (mapaCompanias.get(parts[posicion]) == null) {
                        stb.append(i + 1).append(" ");
                    }
                    break;
                case "grupopago":
                    if (mapaGrupoPago.get(parts[posicion]) == null) {
                        stb.append(i + 1).append(" ");
                    }
                    break;
                case "registropatronal":
                    if (mapaRegistroPatronal.get(parts[posicion]) == null) {
                        stb.append(i + 1).append(" ");
                    }
                    break;
                case "sistema":
                    if (mapaSistemaAntiguedad.get(parts[posicion]) == null) {
                        stb.append(i + 1).append(" ");
                    }
                    break;
                case "tiporellab":
                    if (mapaTipoRelacionLaboral.get(parts[posicion]) == null) {
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
//        
        this.mapaCompanias = new HashMap<>();
        List<Compania> companias = ControladorWS.getInstance().getCompanias();
        for (Compania c : companias) {
            this.mapaCompanias.put(c.getClave(), c);
        }
//        
        this.mapaGrupoPago = new HashMap<>();
        List<RelacionGrupoPagoUsuario> relacion = ControladorWS.getInstance().getGrupoPagoUsuarios();
        for (RelacionGrupoPagoUsuario r : relacion) {
            this.mapaGrupoPago.put(r.getIdGrupoPago().getGrupopago(), r.getIdGrupoPago());
        }
//        
        this.mapaRegistroPatronal = new HashMap<>();
        List<RegistroPatronal> registros = ControladorWS.getInstance().getRegistrosPatronales();
        for (RegistroPatronal r : registros) {
            this.mapaRegistroPatronal.put(r.getRegistropatronal(), r);
        }
//        
        this.mapaSistemaAntiguedad = new HashMap<>();
        List<SistemaAntiguedad> sistemas = ControladorWS.getInstance().getSistemasAntiguedad();
        for (SistemaAntiguedad s : sistemas) {
            this.mapaSistemaAntiguedad.put(s.getClave(), s);
        }
//       
        this.mapaTipoRelacionLaboral = new HashMap<>();
        List<TipoRelacionLaboral> tipos = ControladorWS.getInstance().getTiposRelacionesLaborales();
        for (TipoRelacionLaboral t : tipos) {
            this.mapaTipoRelacionLaboral.put(t.getNombre(), t);
        }
//        
        this.mapaTipoSueldoIdse = new HashMap<>();
        List<TipoSalarioIdse> tipossalarios = ControladorWS.getInstance().getTiposSalariosIdse();
        for (TipoSalarioIdse t : tipossalarios) {
            this.mapaTipoSueldoIdse.put(t.getValor().toString(), t);
        }
    }

    public Map<String, RelacionLaboral> getMapaNumeroEmpleados() {
        return mapaNumeroEmpleados;
    }

    public Map<String, Compania> getMapaCompanias() {
        return mapaCompanias;
    }

    public Map<String, GrupoPago> getMapaGrupoPago() {
        return mapaGrupoPago;
    }

    public Map<String, RegistroPatronal> getMapaRegistroPatronal() {
        return mapaRegistroPatronal;
    }

    public Map<String, SistemaAntiguedad> getMapaSistemaAntiguedad() {
        return mapaSistemaAntiguedad;
    }

    public Map<String, TipoRelacionLaboral> getMapaTipoRelacionLaboral() {
        return mapaTipoRelacionLaboral;
    }

    public Map<String, TipoSalarioIdse> getMapaTipoSueldoIdse() {
        return mapaTipoSueldoIdse;
    }

    public int getPOSICION_NUMERO_EMPLEADO() {
        return POSICION_NUMERO_EMPLEADO;
    }

    public int getPOSICION_ESTATUS() {
        return POSICION_ESTATUS;
    }

    public int getPOSICION_FECHA_ANTIGUEDAD() {
        return POSICION_FECHA_ANTIGUEDAD;
    }

    public int getPOSICION_FECHA_INGRESO() {
        return POSICION_FECHA_INGRESO;
    }

    public int getPOSICION_COMPANIA() {
        return POSICION_COMPANIA;
    }

    public int getPOSICION_GRUPO_PAGO() {
        return POSICION_GRUPO_PAGO;
    }

    public int getPOSICION_REGISTRO_PATRONAL() {
        return POSICION_REGISTRO_PATRONAL;
    }

    public int getPOSICION_SISTEMA_ANTIGUEDAD() {
        return POSICION_SISTEMA_ANTIGUEDAD;
    }

    public int getPOSICION_TIPO_RELLAB() {
        return POSICION_TIPO_RELLAB;
    }

    public int getPOSICION_SD() {
        return POSICION_SD;
    }
}
