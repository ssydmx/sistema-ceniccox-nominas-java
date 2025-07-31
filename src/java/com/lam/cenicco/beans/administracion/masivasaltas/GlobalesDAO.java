/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lam.cenicco.beans.administracion.masivasaltas;

import com.lam.cenicco.impl.ControladorWS;
import com.lam.cenicco.util.Util;
import com.lam.cenicco.ws.Banco;
import com.lam.cenicco.ws.CentroCostos;
import com.lam.cenicco.ws.Compania;
import com.lam.cenicco.ws.Departamento;
import com.lam.cenicco.ws.GrupoPago;
import com.lam.cenicco.ws.Puesto;
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
public class GlobalesDAO {

    public static final int LONGITUD_ARCHIVO = 24;
//
    public static final int POSICION_NUMERO_EMPLEADO = 0;
    public static final int POSICION_APELLIDO_PATERNO = 1;
    public static final int POSICION_APELLIDO_MATERNO = 2;
    public static final int POSICION_NOMBRE = 3;
    public static final int POSICION_SEXO = 4;
    public static final int POSICION_ESTADO_CIVIL = 5;
    public static final int POSICION_FECHA_NACIMIENTO = 6;
    public static final int POSICION_NSS = 7;
    public static final int POSICION_RFC = 8;
    public static final int POSICION_CURP = 9;
    public static final int POSICION_EMAIL = 10;
    public static final int POSICION_LUGAR_NACIMIENTO = 11;
//
    public static final int POSICION_CALLE = 12;
    public static final int POSICION_COLONIA = 13;
    public static final int POSICION_MUNICIPIO = 14;
    public static final int POSICION_ESTADO = 15;
    public static final int POSICION_CP = 16;
    public static final int POSICION_NUMERO_EXTERIOR = 17;
    public static final int POSICION_NUMERO_INTERIOR = 18;
    public static final int POSICION_DOMICILIO_FISCAL = 19;
//
    public static final int POSICION_ESTATUS = 20;
    public static final int POSICION_FECHA_ANTIGUEDAD = 21;
    public static final int POSICION_FECHA_INGRESO = 22;
    public static final int POSICION_COMPANIA = 23;
    public static final int POSICION_GRUPO_PAGO = 24;
    public static final int POSICION_REGISTRO_PATRONAL = 25;
    public static final int POSICION_SISTEMA_ANTIGUEDAD = 26;
    public static final int POSICION_TIPO_RELLAB = 27;
    public static final int POSICION_SD = 28;
//
    public static final int POSICION_CCO = 29;
    public static final int POSICION_PUESTO = 30;
    public static final int POSICION_DEPARTAMENTO = 31;
    public static final int POSICION_JEFE_DIRECTO = 32;
//
    public static final int POSICION_BANCO = 33;
    public static final int POSICION_CUENTA_BANCARIA = 34;
    public static final int POSICION_CLABE_BANCARIA = 35;
    public static final int POSICION_BANCO_EMPLEADO = 36;
//
    private Map<String, RelacionLaboral> mapaNumeroEmpleados;
    private Map<String, Compania> mapaCompanias;
    private Map<String, GrupoPago> mapaGrupoPago;
    private Map<String, RegistroPatronal> mapaRegistroPatronal;
    private Map<String, SistemaAntiguedad> mapaSistemaAntiguedad;
    private Map<String, TipoRelacionLaboral> mapaTipoRelacionLaboral;
    private Map<String, TipoSalarioIdse> mapaTipoSueldoIdse;
    private Map<String, CentroCostos> mapaCCos;
    private Map<String, Puesto> mapaPuesto;
    private Map<String, Departamento> mapaDepartamento;
    private Map<String, Banco> mapaBancos;
    private Map<String, RelacionLaboral> mapaRfc;
    private Map<String, RelacionLaboral> mapaNss;
    private Map<String, RelacionLaboral> mapaCurp;
//    
    private List<String> lista;
//

    public GlobalesDAO(List<String> lista) {
        this.lista = lista;
    }

    public String validarArchivo() {
        inicializarMapas();
        String aux = Util.validarLogitud(lista, LONGITUD_ARCHIVO);
        if (!aux.equals("")) {
            return "Error en minimo de campos requeridos (" + LONGITUD_ARCHIVO + "), en las línea(s): " + aux;
        }

        aux = Util.validarRequeridos(lista, POSICION_NUMERO_EMPLEADO);
        if (!aux.equals("")) {
            return "Error campo requerido Número de Empleado posicion " + (POSICION_NUMERO_EMPLEADO + 1) + ", en las línea(s): " + aux;
        }
        /**
         * TODO: Empleado
         */
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

        /**
         * TODO: Domicilio
         */
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
        }
        /*aux = validarRequeridos("numeroexterior", POSICION_NUMERO_EXTERIOR);
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

        /**
         * TODO: Relacion Laboral
         */
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

        /**
         * TODO: Posicion Laboral
         */
        /*aux = validarRequeridos("cco", POSICION_CCO);
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
        }*/

        /**
         * TODO: Cuenta Bancaria
         */
        aux = validarRequeridos("banco", POSICION_BANCO);
        if (!aux.equals("")) {
            return "El Banco no existe en Cenicco, posición " + (POSICION_BANCO + 1) + " en las línea(s): " + aux;
        }
        aux = validarRequeridos("banco", POSICION_BANCO_EMPLEADO);
        if (!aux.equals("")) {
            return "El Banco no existe en Cenicco, posición " + (POSICION_BANCO_EMPLEADO + 1) + " en las línea(s): " + aux;
        }

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

        return "";

    }

    public void inicializarMapasRelacionesLaborales() {
        this.mapaNumeroEmpleados = new HashMap<>();
        this.mapaRfc = new HashMap<>();
        this.mapaNss = new HashMap<>();
        this.mapaCurp = new HashMap<>();
//        
        List<RelacionLaboral> relaciones = ControladorWS.getInstance().findRelacionesLaborales();
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

    public void inicializarMapas() {
        this.mapaCompanias = new HashMap<>();
        List<Compania> companias = ControladorWS.getInstance().getCompanias();
        for (Compania c : companias) {
            this.mapaCompanias.put(c.getClave(), c);
        }

        this.mapaGrupoPago = new HashMap<>();
        List<RelacionGrupoPagoUsuario> relacion = ControladorWS.getInstance().getGrupoPagoUsuarios();
        for (RelacionGrupoPagoUsuario r : relacion) {
            this.mapaGrupoPago.put(r.getIdGrupoPago().getGrupopago(), r.getIdGrupoPago());
        }

        this.mapaRegistroPatronal = new HashMap<>();
        List<RegistroPatronal> registros = ControladorWS.getInstance().getRegistrosPatronales();
        for (RegistroPatronal r : registros) {
            this.mapaRegistroPatronal.put(r.getRegistropatronal(), r);
        }

        this.mapaSistemaAntiguedad = new HashMap<>();
        List<SistemaAntiguedad> sistemas = ControladorWS.getInstance().getSistemasAntiguedad();
        for (SistemaAntiguedad s : sistemas) {
            this.mapaSistemaAntiguedad.put(s.getClave(), s);
        }

        this.mapaTipoRelacionLaboral = new HashMap<>();
        List<TipoRelacionLaboral> tipos = ControladorWS.getInstance().getTiposRelacionesLaborales();
        for (TipoRelacionLaboral t : tipos) {
            this.mapaTipoRelacionLaboral.put(t.getNombre(), t);
        }

        this.mapaTipoSueldoIdse = new HashMap<>();
        List<TipoSalarioIdse> tipossalarios = ControladorWS.getInstance().getTiposSalariosIdse();
        for (TipoSalarioIdse t : tipossalarios) {
            this.mapaTipoSueldoIdse.put(t.getValor().toString(), t);
        }

        this.mapaCCos = new HashMap<>();
        for (CentroCostos cco : ControladorWS.getInstance().getCentroCostos()) {
            if (this.mapaCCos.get(cco.getCentroCosto()) == null) {
                this.mapaCCos.put(cco.getCentroCosto(), cco);
            }
        }

        this.mapaPuesto = new HashMap<>();
        for (Puesto p : ControladorWS.getInstance().getPuestos()) {
            if (this.mapaPuesto.get(p.getPuesto()) == null) {
                this.mapaPuesto.put(p.getPuesto(), p);
            }
        }

        this.mapaDepartamento = new HashMap<>();
        for (Departamento d : ControladorWS.getInstance().getDepartamentos()) {
            if (this.mapaDepartamento.get(d.getDepartamento()) == null) {
                this.mapaDepartamento.put(d.getDepartamento(), d);
            }
        }

        this.mapaBancos = new HashMap<>();
        for (Banco b : ControladorWS.getInstance().getBancos()) {
            if (this.mapaBancos.get(b.getBanco()) == null) {
                this.mapaBancos.put(b.getNombre(), b);
            }
        }
    }

    private String validarRequeridos(String tag, int posicion) {
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
//
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
//
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
//
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
//
                case "banco":
                    if (mapaBancos.get(parts[posicion]) == null) {
                        stb.append(i + 1).append(" ");
                    }
                    break;
            }

        }
        return stb.toString();
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

    public Map<String, Banco> getMapaBancos() {
        return mapaBancos;
    }

    public RelacionLaboral getJefeDirecto(String[] parts) throws Exception {
        return mapaNumeroEmpleados.get(parts[POSICION_JEFE_DIRECTO]);
    }

    public CentroCostos getCCo(String[] parts) throws Exception {
        return mapaCCos.get(parts[POSICION_CCO]);
    }

    public Puesto getPuesto(String[] parts) throws Exception {
        return mapaPuesto.get(parts[POSICION_PUESTO]);
    }

    public Departamento getDepartamento(String[] parts) throws Exception {
        return mapaDepartamento.get(parts[POSICION_DEPARTAMENTO]);
    }
}