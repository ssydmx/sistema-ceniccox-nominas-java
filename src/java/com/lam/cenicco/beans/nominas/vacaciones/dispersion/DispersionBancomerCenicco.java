/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lam.cenicco.beans.nominas.vacaciones.dispersion;

import com.lam.cenicco.beans.AppBean;
import com.lam.cenicco.dao.ProcesoDAO;
import com.lam.cenicco.impl.ControladorWS;
import com.lam.cenicco.librerias.utilidades.CeniccoUtil;
import com.lam.cenicco.util.ControladorSesiones;
import com.lam.cenicco.util.Parametros;
import com.lam.cenicco.util.ParametrosReportes;
import com.lam.cenicco.util.Util;
import com.lam.cenicco.util.paths.MyPaths;
import com.lam.cenicco.ws.Banco;
import com.lam.cenicco.ws.Compania;
import com.lam.cenicco.ws.Contacto;
import com.lam.cenicco.ws.Domicilio;
import com.lam.cenicco.ws.GrupoPago;
import com.lam.cenicco.ws.Periodo;
import com.lam.cenicco.ws.RelacionLaboral;
import com.lam.cenicco.ws.RelacionLaboralPosicion;
import com.lam.cenicco.ws.TipoProceso;
import com.lam.cenicco.ws.VistaCuentaBancaria;
import com.mysql.jdbc.StringUtils;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import org.primefaces.context.RequestContext;

/**
 *
 * @author JoséAntonio
 */
public class DispersionBancomerCenicco implements ProcesoDAO<VistaCuentaBancaria> {
//    

    protected List<VistaCuentaBancaria> cuentasBancarias;
//    
    protected List<VistaCuentaBancaria> filteredCuentasBancarias;
//    
    protected List<VistaCuentaBancaria> selectedCuentasBancarias;
//    
    protected List<VistaCuentaBancaria> auxCuentasBancarias;
//
    protected Integer[] selectedCentrosCostos;
//    
    protected Integer[] selectedGruposPago;
//    
    protected Integer selectedTipoproceso;
//    
    protected Integer selectedBanco;
//    
    protected Integer anio;
//    
    protected Integer periodo;
//    
    protected String secuencia;
//    
    protected Double total;
//    
    protected Double totalselected;
//    
    protected String fechapago;
//    
    protected Integer selectedestatus;

    @PostConstruct
    @Override
    public void init() {
        this.cuentasBancarias = new ArrayList<>();
        this.filteredCuentasBancarias = new ArrayList<>();
        this.selectedCuentasBancarias = new ArrayList<>();
        this.auxCuentasBancarias = new ArrayList<>();
//        
        this.selectedGruposPago = new Integer[]{};
        this.selectedBanco = null;
        this.periodo = null;
        this.anio = null;
        this.selectedCentrosCostos = null;

    }

    @Override
    public void consultar(ActionEvent event) {
        auxCuentasBancarias = new ArrayList<>();
        this.cuentasBancarias = ControladorWS.getInstance().calcularDispersion(
                Arrays.asList(this.selectedGruposPago), this.selectedTipoproceso, this.selectedBanco, this.anio, this.periodo,
                this.selectedestatus == null ? 1 : this.selectedestatus);

        if (selectedCentrosCostos != null && selectedCentrosCostos.length != 0) {
            for (VistaCuentaBancaria v : this.cuentasBancarias) {
                RelacionLaboral rl = ControladorWS.getInstance().findRelacionLaboralById(v.getIdrellab());
                RelacionLaboralPosicion r = ControladorWS.getInstance().findPosicionLaboralByIdRellab(rl.getIdrelacionlaboralposicion().getIdrelacionlaboralposicion());
                for (Integer id : selectedCentrosCostos) {
                    if (r.getIdcentrocosto().getIdCentroCostos() == id) {
                        auxCuentasBancarias.add(v);
                    }
                }
            }
            this.cuentasBancarias = auxCuentasBancarias;
        }
//        
        this.filteredCuentasBancarias = this.cuentasBancarias;
        this.selectedCuentasBancarias = this.cuentasBancarias;
//        
        this.total = 0.0;
//        
        for (VistaCuentaBancaria v : this.cuentasBancarias) {
            this.total += CeniccoUtil.redondear(v.getImporte());
        }
        if (this.cuentasBancarias.isEmpty()) {
            System.out.println("No se encontraron nóminas...");
        }
    }

    @Override
    public void limpiar(ActionEvent event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void create(ActionEvent event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void edit(ActionEvent event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void delete(VistaCuentaBancaria obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private List<String> layoutBanamex(Banco banco, boolean prueba) {
        GrupoPago gp = ControladorWS.getInstance().findGrupoPagoById(this.selectedGruposPago[0]);
        Compania cia = gp.getIdcompania();
//        
        String nombrecompania = Util.normalizarCadena(cia.getNombre(),
                banco.getCaracterespecial());
//        
        String cadena = "000000000000".substring(banco.getNocliente().length());
        String encabezado = "1" + cadena + banco.getNocliente().trim().toUpperCase() + this.fechapago;
//        
        cadena = "0000".substring(this.secuencia.length());
        encabezado = encabezado + cadena + this.secuencia;
//        
        nombrecompania = nombrecompania.length() > 36 ? nombrecompania.substring(36) : nombrecompania;
//                
        cadena = "                                    ".substring(nombrecompania.length());
        encabezado = encabezado + nombrecompania + cadena;
//        
        String referencia = "NOM " + gp.getGrupopago() + " PER " + this.periodo + " " + this.anio;
        cadena = "                    ".substring(referencia.length());
//        
        encabezado = encabezado + referencia + cadena + "15D01";
//        
        List<String> lineas = new ArrayList<>();
        lineas.add(encabezado);
//      ENCABEZADO DOS  
//        
        this.totalselected = 0.0;
//        
//        
        encabezado = "21001#TOTALNETO01";
//        
        referencia = banco.getSucursal() + banco.getCuenta();
//                

        cadena = Util.getCadenaConEspacios(20, "0");
        cadena = cadena.substring(referencia.length());
//        
        encabezado = encabezado + cadena + referencia;
//        
        Integer size = this.selectedCuentasBancarias.size();
        String numeroregistros = "000000".substring(size.toString().length()) + size;
//        
        encabezado = encabezado + numeroregistros;
        lineas.add(encabezado);
//        
        for (VistaCuentaBancaria v : this.selectedCuentasBancarias) {
            AppBean bean = new AppBean();
            String linea = "3000101001";
            if (bean.isServicioWhiteHat() && v.getCuenta().length() == 18) {
                linea = "3000201001";
            }

//            
            Double importe = !prueba ? CeniccoUtil.redondear(v.getImporte()) : 0.01;
            importe = CeniccoUtil.redondear(importe);
//            
            this.totalselected += importe;
//  
            String entero;
            if (!importe.toString().contains(".")) {
                entero = importe.toString() + "00";
            } else {
                String spl[] = importe.toString().split("\\.");
                entero = spl[0];
                String decimal = spl[1];
//    
                if (decimal.length() == 1) {
                    decimal = decimal + "0";
                }
                entero = entero + decimal;
            }
//
            cadena = "000000000000000000".substring(entero.length());
//           
            linea = linea + cadena + entero;
//            
            switch (v.getCuenta().length()) {
                case 11:
                    linea = linea + "01";
                    break;
                case 18:
                    linea = linea + "40";
                    break;
                case 16:
                    linea = linea + "03";
                    break;
            }
//            
            cadena = "00000000000000000000".substring(v.getCuenta().length());
            linea = linea + cadena + v.getCuenta();
//            
            cadena = "0000000000".substring(v.getNumeroempleado().length());
//          
            if (bean.isServicioWhiteHat()) {
                linea = linea + v.getNumeroempleado() + "            ";
            } else {
                linea = linea + cadena + v.getNumeroempleado() + "      ";
            }

//          
            String nombre = Util.normalizarCadena(v.getNombre(), banco.getCaracterespecial()) + ","
                    + Util.normalizarCadena(v.getApaterno(), banco.getCaracterespecial()) + "/"
                    + Util.normalizarCadena(v.getAmaterno(), banco.getCaracterespecial());
//            
            cadena = "                                                       ".substring(nombre.length());
//            
            linea = linea + nombre + cadena + "NOMINA" + Util.getCadenaConEspacios(29, " ") + "NOMINA";
//            
            cadena = Util.getCadenaConEspacios(99, " ");

            if (v.getCuenta().length() == 18 && bean.isServicioWhiteHat()) {
                linea = linea + cadena + "0" + v.getCuenta().charAt(0) + v.getCuenta().charAt(1) + v.getCuenta().charAt(2) + "00";
            } else {
                linea = linea + cadena + "000000";
            }

//            
            cadena = Util.getCadenaConEspacios(152, " ");
//            
            linea = linea + cadena;
//            
            lineas.add(linea);

        }
        Double previo = !prueba ? CeniccoUtil.redondear(this.totalselected) : (1 * this.selectedCuentasBancarias.size());
        String neto;
        if (!previo.toString().contains(".")) {
            neto = previo.toString() + "00";
        } else {
            String spl[] = previo.toString().split("\\.");
            neto = spl[0];
            String decimal = spl[1];
            if (decimal.length() == 1) {
                decimal = decimal + "0";
            }
            neto = neto + decimal;
        }
//        
        cadena = Util.getCadenaConEspacios(18, "0");
        String totalneto = cadena.substring(neto.length()) + neto;
        encabezado = lineas.get(1).replace("#TOTALNETO", totalneto);
        lineas.set(1, encabezado);
//        

        cadena = "4001" + numeroregistros + totalneto + "000001" + totalneto;
        lineas.add(cadena);
//        
        return lineas;
    }

    private List<String> layoutBanamexProvedores(Banco banco, boolean prueba) {
        List<String> lineas = new ArrayList<>();
//      ENCABEZADO DOS  
//        
        this.totalselected = 0.0;
//        
        String cadena = "";
//        
        Integer size = this.selectedCuentasBancarias.size();
        int contador = 1;
        boolean clabeBanamex = false;
//        
        for (VistaCuentaBancaria v : this.selectedCuentasBancarias) {

            if (v.getCuenta().substring(0, 3).equals("002")) {
                clabeBanamex = true;
            } else {
                clabeBanamex = false;
            }

            String linea;

            if (clabeBanamex) {
                linea = "0301"; //Campo 1 y 2
            } else {
                linea = "0901"; //Campo 1 y 2
            }
            String sucOrigen = "0000".substring(banco.getSucursal().length()) + banco.getSucursal();
            String ctaOrigen = "00000000000000000000".substring(banco.getCuenta().length()) + banco.getCuenta();

            linea = linea + sucOrigen + ctaOrigen;

            //Aqui termina la diferencia de los que son bancos diferentes a banamex

            if (clabeBanamex) {
                linea = linea + "01";
                String sucursal = v.getCuenta().substring(6, 10);
                String cuenta = v.getCuenta().substring(10, 17);
                cadena = "00000000000000000000".substring(cuenta.length());
                linea = linea + sucursal + cadena + cuenta;
                Double importe = !prueba ? CeniccoUtil.redondear(v.getImporte()) : 0.01;
                importe = CeniccoUtil.redondear(importe);
//            
                this.totalselected += importe;
//  
                String entero;
                if (!importe.toString().contains(".")) {
                    entero = importe.toString() + "00";
                } else {
                    String spl[] = importe.toString().split("\\.");
                    entero = spl[0];
                    String decimal = spl[1];
//    
                    if (decimal.length() == 1) {
                        decimal = decimal + "0";
                    }
                    entero = entero + decimal;
                }
//
                cadena = "00000000000000".substring(entero.length());
                linea = linea + cadena + entero + "001";
                cadena = Util.getCadenaConEspacios(24, " ");
                if (v.getNombreempleado().length() > 24) {
                    cadena = v.getNombreempleado().substring(0, 24);
                    linea = linea + cadena;
                } else {
                    cadena = cadena.substring(v.getNombreempleado().length());
                    linea = linea + v.getNombreempleado() + cadena;
                }
                linea = linea.replaceAll("Ñ", "N");
                String concepto = prueba ? "PRUEBA DISPERSION" : "PAGO DISPERSION PROGRAMADA";
                cadena = Util.getCadenaConEspacios(34, " ").substring(concepto.length());
                linea = linea + concepto + cadena;

                String cont = "0000000000".substring(("" + contador).toString().length());
                linea = linea + cont + contador + "0000000000000";

                contador++;
                lineas.add(linea);
            } else {
                Double importe = !prueba ? CeniccoUtil.redondear(v.getImporte()) : 0.01;
                importe = CeniccoUtil.redondear(importe);
//            
                this.totalselected += importe;
//  
                String entero;
                if (!importe.toString().contains(".")) {
                    entero = importe.toString() + "00";
                } else {
                    String spl[] = importe.toString().split("\\.");
                    entero = spl[0];
                    String decimal = spl[1];
//    
                    if (decimal.length() == 1) {
                        decimal = decimal + "0";
                    }
                    entero = entero + decimal;
                }
//
                cadena = "00000000000000".substring(entero.length());
//           
                linea = linea + cadena + entero + "00140";

                cadena = "00000000000000000000".substring(v.getClabe().length());

                linea = linea + cadena + v.getClabe();

                String conceptoPago = prueba ? "PRUEBA DE DISPERSION" : "PAGOS A TERCEROS";

                cadena = Util.getCadenaConEspacios(40, " ").substring(conceptoPago.length());

                String cont = "0000000".substring(("" + contador).toString().length());

                linea = linea + conceptoPago + cadena + cont + contador;
                //Beneficiario
                String beneficiario;
                if (v.getNombre().length() > 18) {
                    String[] nom = v.getNombre().split(" ");
                    if (v.getApaterno().length() > 15) {
                        String[] apa = v.getApaterno().split(" ");
                        if (v.getAmaterno().length() > 15) {
                            String[] ama = v.getApaterno().split(" ");
                            beneficiario = nom[0] + "," + apa[0] + "/" + ama[0];
                        } else {
                            beneficiario = nom[0] + "," + apa[0] + "/" + v.getAmaterno();
                        }
                    } else {
                        beneficiario = nom[0] + "," + v.getApaterno() + "/" + v.getAmaterno();
                    }
                } else {
                    beneficiario = v.getNombre() + "," + v.getApaterno() + "/" + v.getAmaterno();
                }
                String aux = Util.getCadenaConEspacios(55, " ").substring(beneficiario.length());
                beneficiario = beneficiario.replaceAll("Ñ", "N") + aux; //Detalle 7 Beneficiario

                linea = linea + beneficiario + "00";

                cadena = Util.getCadenaConEspacios(14, " ") + Util.getCadenaConEspacios(12, "0");
                //Clave de banco (se agrega un 0 y luego los 3 primeros digitos dela CLABE)
                linea = linea + cadena + "0" + v.getClabe().charAt(0) + v.getClabe().charAt(1) + v.getClabe().charAt(2);
                //Los siguientes ceros son la fecha y hore pero siempre son lo mismo
                cadena = "0000000000";
                linea = linea + cadena;
//            
                contador++;
                lineas.add(linea);
            }
        }
//        
        return lineas;
    }

    private List<String> layoutHsbc(Banco banco, boolean prueba) {
        List<String> lineas = new ArrayList<>();
//      
        String tipoProceso;
        String ref = Util.getCadenaConEspacios(34, " ");
        if (this.selectedTipoproceso != null) {
            TipoProceso tp = ControladorWS.getInstance().findTipoProcesoById(this.selectedTipoproceso);
            tipoProceso = tp.getNombre();
        } else {
            GrupoPago gp = ControladorWS.getInstance().findGrupoPagoById(this.selectedGruposPago[0]);
            tipoProceso = gp.getIdTipoproceso().getNombre();
        }

        if (tipoProceso.length() <= 34) {
            ref = ref.substring(tipoProceso.length());
            ref = tipoProceso + ref;
        } else {
            ref = tipoProceso.substring(0, 34);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
//        
        SimpleDateFormat sdfbanco = new SimpleDateFormat("ddMMyyyy");
//        
        String fpago = "";
        try {
            fpago = sdfbanco.format(sdf.parse(this.fechapago));
        } catch (Exception e) {
            System.out.println(e.toString());
        }
//      
        String aux = "" + this.selectedCuentasBancarias.size();
        String numeroregistros = "0000000".substring(aux.length()) + aux;
        this.totalselected = 0.0;
        for (VistaCuentaBancaria a : this.selectedCuentasBancarias) {
            this.totalselected += CeniccoUtil.redondear(a.getImporte());
        }
        Double cantidad = !prueba ? CeniccoUtil.redondear(this.totalselected) : (1 * this.selectedCuentasBancarias.size());

        //Reutilizamos la variable aux dandole valor nuevo
        if (!cantidad.toString().contains(".")) {
            aux = cantidad.toString() + "00";
        } else {
            String spl[] = cantidad.toString().split("\\.");
            aux = spl[0];
            String decimal = spl[1];
//    
            if (decimal.length() == 1) {
                decimal = decimal + "0";
            }
            aux = aux + decimal;
        }

        String montoTotal = "00000000000000".substring(aux.length()) + aux;
        String cuenta = "0000000000".substring(banco.getCuenta().length()) + banco.getCuenta();

        String referencia = "                                  ";
        String encabezado = "MXPRLFF" + cuenta + montoTotal + numeroregistros + fpago + "     " + referencia;

        lineas.add(encabezado);
//
        StringBuilder stb;
        for (VistaCuentaBancaria v : this.selectedCuentasBancarias) {
//            
            stb = new StringBuilder();
            String cuentaB = v.getCuenta().length() == 10 ? v.getCuenta() : "0" + v.getCuenta();
            if (prueba) {
                aux = "100";
            } else {
                if (!v.getImporte().toString().contains(".")) {
                    aux = v.getImporte().toString() + "00";
                } else {
                    String spl2[] = v.getImporte().toString().split("\\.");
                    aux = spl2[0];
                    String decimal = spl2[1];
//    
                    if (decimal.length() == 1) {
                        decimal = decimal + "0";
                    }
                    aux = aux + decimal;
                }
            }

            String monto = "00000000000000".substring(aux.length()) + aux;

            String nombreempleado;
            if (v.getNombreempleado().length() <= 35) {
                aux = Util.getCadenaConEspacios(35, " ").substring(v.getNombreempleado().length());
                nombreempleado = Util.normalizarCadena(v.getNombreempleado() + aux, null);
            } else {
                nombreempleado = Util.normalizarCadena(v.getNombreempleado().substring(0, 35), null);
            }
            stb.append(cuentaB).append(monto).append(ref).append(nombreempleado);
            lineas.add(stb.toString());
        }
//        
        return lineas;
    }

    private List<String> layoutHsbcInterbancarias(Banco banco, boolean prueba) {
        List<String> lineas = new ArrayList<>();
//      
        String tipoProceso;
        String ref = Util.getCadenaConEspacios(40, " ");
        if (this.selectedTipoproceso != null) {
            TipoProceso tp = ControladorWS.getInstance().findTipoProcesoById(this.selectedTipoproceso);
            tipoProceso = tp.getNombre();
        } else {
            GrupoPago gp = ControladorWS.getInstance().findGrupoPagoById(this.selectedGruposPago[0]);
            tipoProceso = gp.getIdTipoproceso().getNombre();
        }

        if (tipoProceso.length() <= 40) {
            ref = ref.substring(tipoProceso.length());
            ref = tipoProceso + ref;
        } else {
            ref = tipoProceso.substring(0, 40);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
//        
        SimpleDateFormat sdfencabezado = new SimpleDateFormat("ddMMyyyy");
        SimpleDateFormat sdfdetalle = new SimpleDateFormat("ddMMyy");
//        
        String fpago = "";
        String fdetalle = "";
        try {
            fpago = sdfencabezado.format(sdf.parse(this.fechapago));
            fdetalle = "0" + sdfdetalle.format(sdf.parse(this.fechapago));
        } catch (Exception e) {
            System.out.println(e.toString());
        }
//      
        String aux;

        String cuenta = "0000000000".substring(banco.getCuenta().length()) + banco.getCuenta();

        String rfcbanco = Util.getCadenaConEspacios(18, " ");

//Encabezado: 1.Cuenta Origen (10)  |  2.Fecha (8)  |   3.RFC (18)  |    4.Moneda (3) |  5.Referencia (40)

        String encabezado = cuenta + fpago + banco.getCaracterespecial() + "MXN" + ref;

        lineas.add(encabezado);
//
        StringBuilder stb;
        for (VistaCuentaBancaria v : this.selectedCuentasBancarias) {

//Listado de cuentas

            stb = new StringBuilder();
            String tipoCuenta = "CLA";
            String codigoBanco = v.getClabe() != null ? v.getClabe().substring(0, 3) : "CLABE NO ENCONTRADA: " + v.getNumeroempleado();
            String cuentaB = v.getCuenta().length() == 18 ? ("00" + v.getClabe()) : "VALOR INVALIDO: " + v.getNumeroempleado();
            String nombreempleado;
            if (v.getNombreempleado().length() <= 40) {
                aux = Util.getCadenaConEspacios(40, " ").substring(v.getNombreempleado().length());
                nombreempleado = Util.normalizarCadena(v.getNombreempleado() + aux, null);
            } else {
                nombreempleado = Util.normalizarCadena(v.getNombreempleado().substring(0, 40), null);
            }

            if (prueba) {
                aux = "100";
            } else {
                if (!v.getImporte().toString().contains(".")) {
                    aux = v.getImporte().toString() + "00";
                } else {
                    String spl2[] = v.getImporte().toString().split("\\.");
                    aux = spl2[0];
                    String decimal = spl2[1];
//    
                    if (decimal.length() == 1) {
                        decimal = decimal + "0";
                    }
                    aux = aux + decimal;
                }
            }
            String monto = "00000000000000".substring(aux.length()) + aux;
            String rfcempleado = Util.getCadenaConEspacios(18, " ").substring(v.getRfc().length()) + v.getRfc();



//Detalles: 1.Tipo de cuenta (3) | 2.Codigo de banco (3) | 3.Cuenta Empleado (20) | 4.Nombre beneficiario (40) | 5.Monto (14) | 6.Referencia numerica (7) |
//          7.Referencia Alfanumerica (40) | 8.RFC beneficiario (18) | 9.IVA (14)

            stb.append(tipoCuenta).append(codigoBanco).append(cuentaB).append(nombreempleado).append(monto).append(fdetalle)
                    .append(ref).append(rfcempleado).append(Util.getCadenaConEspacios(14, "0"));
            lineas.add(stb.toString());
        }
//        
        return lineas;
    }

    private List<String> layoutAltasBanamex(Banco banco, boolean prueba) {
        GrupoPago gp = ControladorWS.getInstance().findGrupoPagoById(this.selectedGruposPago[0]);
        Compania cia = gp.getIdcompania();
//        
        String nombrecompania = Util.normalizarCadena(cia.getNombre(),
                banco.getCaracterespecial());
//        
        String cadena = "000000000000".substring(banco.getNocliente().length());
        String encabezado = "1" + cadena + banco.getNocliente().trim().toUpperCase() + this.fechapago;
//        
        cadena = "0000".substring(this.secuencia.length());
        encabezado = encabezado + cadena + this.secuencia;
//        
        nombrecompania = nombrecompania.length() > 36 ? nombrecompania.substring(36) : nombrecompania;
//                
        cadena = "                                    ".substring(nombrecompania.length());
        encabezado = encabezado + nombrecompania + cadena;
//        
        String referencia = "NOM " + gp.getGrupopago() + " PER " + this.periodo + " " + this.anio;
        cadena = "                    ".substring(referencia.length());
//        
        encabezado = encabezado + referencia + cadena + "15D01";
//        
        List<String> lineas = new ArrayList<>();
        lineas.add(encabezado);
//      ENCABEZADO DOS  
//        
        this.totalselected = 0.0;
//        
//        
        encabezado = "21001#TOTALNETO01";
//        
        referencia = banco.getSucursal() + banco.getCuenta();
//                

        cadena = Util.getCadenaConEspacios(20, "0");
        cadena = cadena.substring(referencia.length());
//        
        encabezado = encabezado + cadena + referencia;
//        
        Integer size = this.selectedCuentasBancarias.size();
        String numeroregistros = "000000".substring(size.toString().length()) + size;
//        
        encabezado = encabezado + numeroregistros;
        lineas.add(encabezado);
//        
        for (VistaCuentaBancaria v : this.selectedCuentasBancarias) {
            AppBean bean = new AppBean();
            String linea = "A0";
            if (bean.isServicioWhiteHat()) {
                linea = "3000201001";
            }

//            
            Double importe = !prueba ? CeniccoUtil.redondear(v.getImporte()) : 0.01;
            importe = CeniccoUtil.redondear(importe);
//            
            this.totalselected += importe;
//  
            String entero;
            if (!importe.toString().contains(".")) {
                entero = importe.toString() + "00";
            } else {
                String spl[] = importe.toString().split("\\.");
                entero = spl[0];
                String decimal = spl[1];
//    
                if (decimal.length() == 1) {
                    decimal = decimal + "0";
                }
                entero = entero + decimal;
            }
//
            cadena = "000000000000000000".substring(entero.length());
//           
            linea = linea + cadena + entero;
//            
            switch (v.getCuenta().length()) {
                case 11:
                    linea = linea + "01";
                    break;
                case 18:
                    linea = linea + "40";
                    break;
                case 16:
                    linea = linea + "03";
                    break;
            }
//            
            cadena = "00000000000000000000".substring(v.getCuenta().length());
            linea = linea + cadena + v.getCuenta();
//            
            cadena = "0000000000".substring(v.getNumeroempleado().length());
//            
            linea = linea + cadena + v.getNumeroempleado() + "      ";
//          
            String nombre = Util.normalizarCadena(v.getNombre(), banco.getCaracterespecial()) + ","
                    + Util.normalizarCadena(v.getApaterno(), banco.getCaracterespecial()) + "/"
                    + Util.normalizarCadena(v.getAmaterno(), banco.getCaracterespecial());
//            
            cadena = "                                                       ".substring(nombre.length());
//            
            linea = linea + nombre + cadena;
//            
            cadena = Util.getCadenaConEspacios(140, " ");

            if (v.getCuenta().length() == 18 && bean.isServicioWhiteHat()) {
                linea = linea + cadena + "00" + v.getCuenta().charAt(1) + v.getCuenta().charAt(2) + "00";
            } else {
                linea = linea + cadena + "000000";
            }

//            
            cadena = Util.getCadenaConEspacios(152, " ");
//            
            linea = linea + cadena;
//            
            lineas.add(linea);

        }
        Double previo = !prueba ? CeniccoUtil.redondear(this.totalselected) : (1 * this.selectedCuentasBancarias.size());
        String neto;
        if (!previo.toString().contains(".")) {
            neto = previo.toString() + "00";
        } else {
            String spl[] = previo.toString().split("\\.");
            neto = spl[0];
            String decimal = spl[1];
            if (decimal.length() == 1) {
                decimal = decimal + "0";
            }
            neto = neto + decimal;
        }
//        
        cadena = Util.getCadenaConEspacios(18, "0");
        String totalneto = cadena.substring(neto.length()) + neto;
        encabezado = lineas.get(1).replace("#TOTALNETO", totalneto);
        lineas.set(1, encabezado);
//        

        cadena = "4001" + numeroregistros + totalneto + "000001" + totalneto;
        lineas.add(cadena);
//        
        return lineas;
    }

    private List<String> layoutBancomer(Banco bancoCompania, boolean prueba) {
        List<Banco> listaBancos = ControladorWS.getInstance().findBancos(new Banco());
        List<String> lineas = new ArrayList<>();
//
        System.out.println("DispersionBancomer... ");
//        

        for (VistaCuentaBancaria v : this.selectedCuentasBancarias) {
            Banco bancoEmpleado = new Banco();
            for (Banco b : listaBancos) {
                if (b.getIdbanco().equals(v.getIdbancoempleado())) {
                    bancoEmpleado = b;
                }
            }
            StringBuilder stb = new StringBuilder();
//            
            Double importe = !prueba ? CeniccoUtil.redondear(v.getImporte()) : 0.01;
//            
            String cadena = "000000000".substring(v.getNumeroempleado().length());
            stb.append(cadena).append(v.getNumeroempleado());
//            
            String cuentaClabe = "                    ";
            if (bancoCompania.getIdbanco() == bancoEmpleado.getIdbanco()) {
                stb.append("                99");
                stb.append(v.getCuenta()).append(cuentaClabe.substring(v.getCuenta().length()));
            } else {
                stb.append("                40");
                stb.append(v.getClabe()).append(cuentaClabe.substring(v.getClabe().length()));
            }
//          
            String neto;
//            
            if (!importe.toString().contains(".")) {
                neto = importe.toString() + "00";
            } else {
                String spl[] = importe.toString().split("\\.");
                neto = spl[0];
                String decimal = spl[1];
//                
                if (decimal.length() == 1) {
                    decimal = decimal + "0";
                }
                neto = neto + decimal;
            }
//          
            cadena = "000000000000000".substring(neto.length());
            stb.append(cadena).append(neto);
//          
            String nombreempleado = v.getApaterno() + " " + v.getNombre();
            nombreempleado = nombreempleado.replaceAll("  ", " ");
            if (nombreempleado.length() > 39) {
                nombreempleado = nombreempleado.substring(0, 39);
            }

            nombreempleado = Util.normalizarCadena(nombreempleado, null);
//            
            cadena = "                                        ".substring(nombreempleado.length());
            stb.append(nombreempleado).append(cadena);

            if (bancoCompania.getIdbanco() == bancoEmpleado.getIdbanco()) {
                stb.append("001001");
            } else {
                String claveBanco = ("0" + v.getClabe().substring(1, 3));
                stb.append(claveBanco).append("001");
            }
//            
            System.out.println("Linea" + nombreempleado + ": " + stb.toString());
            lineas.add(stb.toString());
        }
        return lineas;
    }

    private List<String> layoutBancomerMelonn(Banco bancoCompania, boolean prueba) {
        List<Banco> listaBancos = ControladorWS.getInstance().findBancos(new Banco());
        List<String> lineas = new ArrayList<>();
//
        System.out.println("DispersionBancomer... ");
//        

        for (VistaCuentaBancaria v : this.selectedCuentasBancarias) {
            Banco bancoEmpleado = new Banco();
            for (Banco b : listaBancos) {
                if (b.getIdbanco().equals(v.getIdbancoempleado())) {
                    bancoEmpleado = b;
                }
            }
            StringBuilder stb = new StringBuilder();
//            
            Double importe = !prueba ? CeniccoUtil.redondear(v.getImporte()) : 0.01;
//            
            String cadena = "000000000".substring(v.getNumeroempleado().length());
            stb.append(cadena).append(v.getNumeroempleado());
//            
            String cuentaClabe = "                    ";
            if (bancoCompania.getIdbanco() == bancoEmpleado.getIdbanco()) {
                stb.append("                99");
                stb.append(v.getCuenta()).append(cuentaClabe.substring(v.getCuenta().length()));
            } else {
                stb.append("                40");
                stb.append(v.getClabe()).append(cuentaClabe.substring(v.getClabe().length()));
            }
//          
            String neto;
//            
            if (!importe.toString().contains(".")) {
                neto = importe.toString() + "00";
            } else {
                String spl[] = importe.toString().split("\\.");
                neto = spl[0];
                String decimal = spl[1];
//                
                if (decimal.length() == 1) {
                    decimal = decimal + "0";
                }
                neto = neto + decimal;
            }
//          
            cadena = "000000000000000".substring(neto.length());
            stb.append(cadena).append(neto);
//          
            String nombreempleado = v.getApaterno() + " " + v.getAmaterno() + " " + v.getNombre();
            nombreempleado = nombreempleado.replaceAll("  ", " ");
            if (nombreempleado.length() > 39) {
                nombreempleado = nombreempleado.substring(0, 39);
            }

            nombreempleado = Util.normalizarCadena(nombreempleado, null);
//            
            cadena = "                                        ".substring(nombreempleado.length());
            stb.append(nombreempleado).append(cadena);

            if (bancoCompania.getIdbanco() == bancoEmpleado.getIdbanco()) {
                stb.append("001001");
            } else {
                String claveBanco = ("0" + v.getClabe().substring(1, 3));
                stb.append(claveBanco).append("001");
            }
//            
            //System.out.println("Linea" + nombreempleado + ": " + stb.toString());
            lineas.add(stb.toString());
        }
        return lineas;
    }

    private List<String> layoutInbursa() {
        int cont = 1;
        List<String> lineas = new ArrayList<>();
        for (VistaCuentaBancaria v : this.selectedCuentasBancarias) {
            String linea = cont + "," + cont + "," + v.getNombreempleado() + "," + v.getCuenta();
            Double importe = CeniccoUtil.redondear(v.getImporte());
            importe = CeniccoUtil.redondear(importe);
//  
            String entero;
            if (!importe.toString().contains(".")) {
                entero = importe.toString() + "00";
            } else {
                String spl[] = importe.toString().split("\\.");
                entero = spl[0];
                String decimal = spl[1];
//    
                if (decimal.length() == 1) {
                    decimal = decimal + "0";
                }
                entero = entero + "." + decimal;
            }
//            
            linea = linea + "," + entero;
            System.out.println("LineaDispersion... " + linea);
            cont++;
            lineas.add(linea);

        }
        return lineas;
    }

    private List<String> layoutBanorte(Banco banco, boolean prueba) {
        List<Banco> listaBancos = new ArrayList<>();
        listaBancos = ControladorWS.getInstance().findBancos(new Banco());
        List<String> lineas = new ArrayList<>();
//
        String numregistros = "" + this.selectedCuentasBancarias.size();
//
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
//
        String fpago = "";
        try {
            fpago = new SimpleDateFormat("yyyyMMdd").format(sdf.parse(this.fechapago));

        } catch (Exception e) {
            System.out.println(e.toString());
        }
//        
        StringBuilder strencabezado = new StringBuilder();
        strencabezado.append(banco.getSucursal()).append(banco.getNocliente()).append(fpago).append("01");
//        
        String cadena = "000000".substring(numregistros.length());
        strencabezado.append(cadena).append(numregistros);
//          
        Double tneto = 0.00;
        if (prueba) {
            tneto = new Double(this.selectedCuentasBancarias.size()) / 100;
            System.out.println("Prueba Neto:" + tneto);
        } else {
            for (VistaCuentaBancaria v : this.selectedCuentasBancarias) {
                tneto += v.getImporte();
            }
        }

//        
        String importetotal = Util.getSueldoNormalizado(tneto);
        System.out.println("ImporteTotalBanorte... " + importetotal + " | " + this.total);
        cadena = "000000000000000".substring(importetotal.length());
        strencabezado.append(cadena).append(importetotal);
//        
//        
        String filleruno = Util.getCadenaConEspacios(49, "0");
        String fillerdos = Util.getCadenaConEspacios(77, " ");
//        
        strencabezado.append(filleruno).append(fillerdos);
//    
        lineas.add(strencabezado.toString());
//        
        for (VistaCuentaBancaria v : this.selectedCuentasBancarias) {

            Banco bancoEmpleado = new Banco();
            StringBuilder strdetalle = new StringBuilder();
            if (v.getNumerodispersion() != null) {
                cadena = "0000000000".substring(v.getNumerodispersion().length());
                strdetalle.append("D").append(fpago).append(cadena).append(v.getNumerodispersion());
            } else {
                cadena = "0000000000".substring(v.getNumeroempleado().length());
                strdetalle.append("D").append(fpago).append(cadena).append(v.getNumeroempleado());
            }

            filleruno = Util.getCadenaConEspacios(80, " ");
            strdetalle.append(filleruno);
//          
            if (prueba) {
                cadena = Util.getSueldoNormalizado(0.01);
            } else {
                cadena = Util.getSueldoNormalizado(v.getImporte());
            }

            filleruno = "000000000000000".substring(cadena.length());
            for (Banco b : listaBancos) {
                if (b.getIdbanco().equals(v.getIdbancoempleado())) {
                    bancoEmpleado = b;
                }
            }
            strdetalle.append(filleruno).append(cadena).append(bancoEmpleado.getSat()).append(bancoEmpleado.getSat().equals("072") ? "01" : "40");
            String cuentaOClabe = bancoEmpleado.getSat().equals("072") ? v.getCuenta() : v.getClabe();
            cadena = "000000000000000000".substring(cuentaOClabe.length());
            strdetalle.append(cadena).append(cuentaOClabe).append("0 00000000                ");
//            
            lineas.add(strdetalle.toString());
        }
//        
//        
        return lineas;
    }

    private List<String> layoutBanbajio(Banco banco) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
        String fpago = "";
        String fgeneracion = "";
        try {
            fpago = new SimpleDateFormat("yyyyMMdd").format(sdf.parse(this.fechapago));
            fgeneracion = new SimpleDateFormat("yyyyMMdd").format(new Date());
        } catch (Exception e) {
            System.out.println(e.toString());
        }
//        
        List<String> lineas = new ArrayList<>();
        StringBuilder stb = new StringBuilder();
//        
        String cadena = "00000000000000000000".substring(banco.getCuenta().length());
        stb.append("010000001030S900").append(banco.getNocliente()).append(fgeneracion).append(cadena).append(banco.getCuenta()).
                append("                                                                                                                                  ");
//        
        String descripcion = "PAGO DE NOMINA " + this.periodo + " " + this.anio;
        String referencia = Util.getCadenaConEspacios(40, " ");
//        
        lineas.add(stb.toString());
        Integer i = 2;
        double importetotal = 0.0;
        for (VistaCuentaBancaria v : this.selectedCuentasBancarias) {
            stb = new StringBuilder("02");

            cadena = "0000000".substring(i.toString().length());
            stb.append(cadena).append(i.toString()).append("90").append(fgeneracion).append("000030");
//            
            String importe = Util.getSueldoNormalizado(v.getImporte());
            cadena = "000000000000000".substring(importe.length());
            stb.append(cadena).append(importe).append(fpago).append("00");
//
            cadena = "00000000000000000000".substring(banco.getCuenta().length());
            stb.append(cadena).append(banco.getCuenta()).append(" 00");

            cadena = "00000000000000000000".substring(v.getCuenta().length());
            stb.append(cadena).append(v.getCuenta()).append(" ");
//         
            cadena = "0000000".substring(i.toString().length());
            stb.append(cadena).append(i.toString());
//            
            cadena = referencia.substring(descripcion.length());
            stb.append(descripcion).append(cadena)
                    .append("0000000000000000000000000000000000000000");
            importetotal += v.getImporte();
//          
            i++;
            lineas.add(stb.toString());
        }
        stb = new StringBuilder("09");
        cadena = "0000000".substring(i.toString().length());
        stb.append(cadena).append(i.toString()).append("90");
//        
        String numeroregistros = "" + this.selectedCuentasBancarias.size();
        cadena = "0000000".substring(numeroregistros.length());
        stb.append(cadena).append(numeroregistros);
//        
        String neto = Util.getSueldoNormalizado(importetotal);
        cadena = "000000000000000000".substring(neto.length());
        stb.append(cadena).append(neto).
                append("                                                                                                                                                 ");
        lineas.add(stb.toString());
//        
        return lineas;
    }

    private List<String> layoutSantander(Banco banco, boolean prueba) {
        List<String> lineas = new ArrayList<>();
//        
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
//        
        SimpleDateFormat sdfbanco = new SimpleDateFormat("MMddyyyy");
//        
        String fpago = "";
        try {
            fpago = sdfbanco.format(sdf.parse(this.fechapago));
        } catch (Exception e) {
            System.out.println(e.toString());
        }
//        
        String encabezado = "100001E" + sdfbanco.format(new Date()) + banco.getCuenta() + "     " + fpago;
        lineas.add(encabezado);
//
        Integer i = 2;
//
        StringBuilder stb;
        Double importetotal = 0.0;
        for (VistaCuentaBancaria v : this.selectedCuentasBancarias) {
//            
            stb = new StringBuilder();
            stb.append("2");
            String cadena = "00000".substring(i.toString().length());
            stb.append(cadena).append(i.toString());
            cadena = "       ".substring(v.getNumeroempleado().length());
            stb.append(v.getNumeroempleado()).append(cadena);
//            
            if (v.getApaterno() != null && !v.getApaterno().equals("")) {
                cadena = "                              ";
                String apaterno = Util.normalizarCadena(v.getApaterno(), null);
                cadena = cadena.substring(apaterno.length());
                stb.append(apaterno).append(cadena);
            } else {
                cadena = "NA                            ";
                stb.append(cadena);
            }
//
            if (v.getAmaterno() != null && !v.getAmaterno().equals("")) {
                cadena = "                    ";
                String amaterno = Util.normalizarCadena(v.getAmaterno(), null);
                cadena = cadena.substring(amaterno.length());
                stb.append(amaterno).append(cadena);
            } else {
                cadena = "NA                  ";
                stb.append(cadena);
            }
//            
            String nombre = Util.normalizarCadena(v.getNombre(), null);
            cadena = "                              ".substring(nombre.length());
            stb.append(nombre).append(cadena);
//            
            cadena = "                ".substring(v.getCuenta().length());
            stb.append(v.getCuenta()).append(cadena);
            String importe = !prueba ? Util.getSueldoNormalizado(v.getImporte()) : Util.getSueldoNormalizado(0.01);
//          String importe = Util.getSueldoNormalizado(v.getImporte());
            cadena = "000000000000000000".substring(importe.length());
            stb.append(cadena).append(importe).append("01");
//            
            importetotal += v.getImporte();
//            
            lineas.add(stb.toString());
//            
            i++;
        }
        String numeroregistros = "" + this.selectedCuentasBancarias.size();
        stb = new StringBuilder();
        stb.append("3");
//        
        String cadena = "00000".substring(i.toString().length());
        stb.append(cadena).append(i.toString());
//        
        cadena = "00000".substring(numeroregistros.length());
        stb.append(cadena).append(numeroregistros);
//        
        String importenormalizado = Util.getSueldoNormalizado(importetotal);
        cadena = "000000000000000000".substring(importenormalizado.length());
        stb.append(cadena).append(importenormalizado);
        lineas.add(stb.toString());
//        
        return lineas;
    }

    private List<String> layoutSantanderPagoTerceros(Banco banco, boolean prueba) {
        List<Banco> listaBancos = ControladorWS.getInstance().findBancos(new Banco());
        List<String> lineas = new ArrayList<>();

        com.lam.cenicco.ws.GrupoPago gp = ControladorWS.getInstance().findGrupoPagoById(this.selectedGruposPago[0]);
        Compania compania = gp.getIdcompania();

        com.lam.cenicco.ws.TipoProceso tp;
        if (this.selectedTipoproceso == null) {
            tp = ControladorWS.getInstance().findTipoProcesoById(gp.getIdTipoproceso().getIdtipoproceso());
        } else {
            tp = ControladorWS.getInstance().findTipoProcesoById(this.selectedTipoproceso);
        }

        com.lam.cenicco.ws.Periodo p = new Periodo();
        p.setAnio(this.anio);
        p.setPeriodo(this.periodo);
        p.setIdtipoproceso(tp);
        p = ControladorWS.getInstance().findPeriodoByTipoProcesoAndPeriodoAndAnio(p);

        String referencia = compania.getRfc().trim() + "NOM";
        if (p.getPeriodo() % 2 == 1) {
            referencia = referencia + "Q1" + p.getAniomes().substring(0, 3) + p.getAnio().toString().substring(2, 4);
        } else {
            referencia = referencia + "Q2" + p.getAniomes().substring(0, 3) + p.getAnio().toString().substring(2, 4);
        }
//
        for (VistaCuentaBancaria v : this.selectedCuentasBancarias) {
            Banco bancoEmpleado = new Banco();
            for (Banco b : listaBancos) {
                if (b.getIdbanco().equals(v.getIdbancoempleado())) {
                    bancoEmpleado = b;
                }
            }
//          
            String nombreEmpleado = v.getNombre() + " " + v.getApaterno() + " " + v.getAmaterno();
            if (v.getNombreempleado().length() <= 40) {
                String aux = Util.getCadenaConEspacios(40, " ").substring(nombreEmpleado.length());
                nombreEmpleado = Util.normalizarCadena(nombreEmpleado, null) + aux;
            } else {
                nombreEmpleado = Util.normalizarCadena(nombreEmpleado.substring(0, 40), null);
            }

            String prefijoBancoEmpleado = "";
            if (bancoEmpleado.getCaracterespecial().length() <= 5) {
                String aux = Util.getCadenaConEspacios(5, " ").substring(bancoEmpleado.getCaracterespecial().length());
                prefijoBancoEmpleado = Util.normalizarCadena(bancoEmpleado.getCaracterespecial(), null) + aux;
            } else {
                prefijoBancoEmpleado = Util.normalizarCadena(bancoEmpleado.getCaracterespecial().substring(0, 5), null);
            }

            DecimalFormat df = new DecimalFormat("#.00");
            df.setRoundingMode(RoundingMode.DOWN);
            Double importe = (!prueba ? v.getImporte() : 0.01);
            String importeNormalizado = df.format(importe);
            if (importeNormalizado.contains(".")) {
                importeNormalizado = importeNormalizado.replaceAll("\\p{Punct}", "");
            }
            String cadenaImporte = "0000000000000000000".substring(importeNormalizado.length());

            StringBuilder stb = new StringBuilder();
            stb.append(banco.getCuenta());
            stb.append("     ");
            stb.append(v.getClabe());
            stb.append("  ");
            stb.append(prefijoBancoEmpleado);
            stb.append(nombreEmpleado);
            stb.append(cadenaImporte).append(importeNormalizado);
            stb.append("01001").append(this.fechapago);
            stb.append(referencia);
            stb.append("                                                                                                             1");
            lineas.add(stb.toString());
        }
//        
        return lineas;
    }

    private List<String> layoutInterbancariaBanorte(Banco banco, boolean prueba) {
        List<String> lineas = new ArrayList<>();
//        
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
//       
        String fpago = "";
        try {
            fpago = new SimpleDateFormat("ddMMyyyy").format(sdf.parse(this.fechapago));

        } catch (Exception e) {
            System.out.println(e.toString());
        }
//        
        String descripcion = "PAGO DE NOMINA " + this.periodo + " " + this.anio;
//        
        for (VistaCuentaBancaria v : this.selectedCuentasBancarias) {
            StringBuilder stb = new StringBuilder();
            stb.append("041");
            String cadena;
            if (v.getNumerodispersion() != null) {
                cadena = "000000000000".substring(v.getNumerodispersion().length());
                stb.append(cadena).append(v.getNumerodispersion());
            } else {
                cadena = "000000000000".substring(v.getNumeroempleado().length());
                stb.append(cadena).append(v.getNumeroempleado());
            }
            cadena = "00000000000000000000".substring(banco.getCuenta().length());
            stb.append(cadena).append(banco.getCuenta());
            cadena = "00000000000000000000".substring(v.getClabe().length());
            stb.append(cadena).append(v.getClabe());
            String importe;
            if (prueba) {
                importe = Util.getSueldoNormalizado(0.01);
            } else {
                importe = Util.getSueldoNormalizado(v.getImporte());
            }

            cadena = "00000000000000".substring(importe.length());
            stb.append(cadena).append(importe).append("1");
            cadena = "000000000".substring(v.getNumeroempleado().length());
            stb.append(cadena).append(v.getNumeroempleado());
            cadena = "                              ".substring(descripcion.length());
            stb.append(descripcion).append(cadena)
                    .append("11")
                    .append(v.getRfc())
                    .append("00000000000000                                       ")
                    .append(fpago)
                    .append("                                                                      ");

//            
            lineas.add(stb.toString());
        }
//        
//        
        return lineas;
    }

    private List<String> layoutInterbancariaBancomer(Banco banco, boolean prueba) {
        List<String> lineas = new ArrayList<>();
        System.out.println("Entra Interbancaria Bancomer..... ");

        for (VistaCuentaBancaria v : this.selectedCuentasBancarias) {
            String sucursal = "0" + v.getClabe().substring(1, 3);
            System.out.println("Sucursal... " + sucursal);
//            
            Double importe = !prueba ? CeniccoUtil.redondear(v.getImporte()) : 0.01;
//            
            String cadena = "000000000".substring(v.getNumeroempleado().length());
            String linea = cadena + Util.normalizarCadena(v.getNumeroempleado(), null);
//            
            cadena = "0000000000000000".substring(v.getRfc().length());
            linea = linea + cadena + Util.normalizarCadena(v.getRfc(), null) + "40";
//            
            cadena = "                    ".substring(v.getClabe().length());
            linea = linea + v.getClabe() + cadena;
//          
            String neto;
//            
            if (!importe.toString().contains(".")) {
                neto = importe.toString() + "00";
            } else {
                String spl[] = importe.toString().split("\\.");
                neto = spl[0];
                String decimal = spl[1];
//                
                if (decimal.length() == 1) {
                    decimal = decimal + "0";
                }
                neto = neto + decimal;
            }
//          
            cadena = "000000000000000".substring(neto.length());
            linea = linea + cadena + neto;
//            
            String nombre;
            if (v.getNombreempleado().length() > 40) {
                nombre = v.getNombreempleado().substring(0, 39);
            } else {
                nombre = v.getNombreempleado();
            }
//            
            cadena = "                                        ".substring(nombre.length());
            linea = linea + Util.normalizarCadena(nombre, null) + cadena + sucursal + "001";
//
            lineas.add(linea);
        }
        return lineas;
    }

    private List<String> layoutInterbancariaBancomerOrdas(Banco banco, boolean prueba) {
        List<String> lineas = new ArrayList<>();
        System.out.println("Entra Interbancaria Bancomer..... ");
        com.lam.cenicco.ws.TipoProceso tp;
        if (this.selectedTipoproceso == null) {
            com.lam.cenicco.ws.GrupoPago gp = ControladorWS.getInstance().findGrupoPagoById(this.selectedGruposPago[0]);
            tp = ControladorWS.getInstance().findTipoProcesoById(gp.getIdTipoproceso().getIdtipoproceso());
        } else {
            tp = ControladorWS.getInstance().findTipoProcesoById(this.selectedTipoproceso);
        }

        com.lam.cenicco.ws.Periodo p = new Periodo();
        p.setAnio(anio);
        p.setPeriodo(periodo);
        p.setIdtipoproceso(tp);
        p = ControladorWS.getInstance().findPeriodoByTipoProcesoAndPeriodoAndAnio(p);

        String ref = "NOM";

        if (p.getPeriodo() % 2 == 1) {
            ref = ref + "1QNA" + p.getAniomes().substring(0, 3) + p.getAnio().toString().substring(2, 4);
        } else {
            ref = ref + "2QNA" + p.getAniomes().substring(0, 3) + p.getAnio().toString().substring(2, 4);
        }



        for (VistaCuentaBancaria v : this.selectedCuentasBancarias) {
            String linea = v.getClabe() + "00000000" + banco.getCuenta() + "MXP";

            Double importe = !prueba ? CeniccoUtil.redondear(v.getImporte()) : 0.01;

            String neto;

            if (!importe.toString().contains(".")) {
                neto = importe.toString() + ".00";
            } else {
                String spl[] = importe.toString().split("\\.");
                neto = spl[0];
                String decimal = spl[1];
//                
                if (decimal.length() == 1) {
                    decimal = decimal + "0";
                }

                neto = neto + "." + decimal;
            }
            String aux = Util.getCadenaConEspacios(16, "0").substring(neto.length());

            linea = linea + aux + neto;
            if (v.getNombreempleado().length() >= 30) {
                aux = v.getNombreempleado().substring(0, 30).replaceAll("Ñ", "N");
                linea = linea + aux;
            } else {
                aux = Util.getCadenaConEspacios(30, " ").substring(v.getNombreempleado().length());
                linea = linea + v.getNombreempleado().replaceAll("Ñ", "N") + aux;
            }

            linea = linea + "40" + v.getClabe().substring(0, 3);

            if (v.getNumeroempleado().length() >= 4) {
                aux = v.getNumeroempleado().substring(0, 4);
                linea = linea + aux;
            } else {
                aux = "0000".substring(v.getNumeroempleado().length());
                linea = linea + aux + v.getNumeroempleado();
            }

            linea = linea + " " + ref + Util.getCadenaConEspacios(13, " ");

            if (v.getNumeroempleado().length() >= 7) {
                aux = v.getNumeroempleado().substring(0, 7);
                linea = linea + aux + "H";
            } else {
                aux = "0000000".substring(v.getNumeroempleado().length());
                linea = linea + aux + v.getNumeroempleado() + "H";
            }

            lineas.add(linea);
        }
        return lineas;
    }

    private List<String> layoutMonex(Banco banco, boolean prueba) {
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyy");
        SimpleDateFormat sdfRef = new SimpleDateFormat("yyMMdd");
        Map<String, RelacionLaboral> relaciones = ControladorWS.getInstance().getMapaRelacionLaboralActivosByGrupoPago(this.selectedGruposPago[0]);
        Map<String, String> correos = ControladorWS.getInstance().getMails();
        List<String> lineas = new ArrayList<>();
        System.out.println("Entra Transferencia Nacional Monex.....");

        Date fechaPago = null;
        try {
            fechaPago = sdfRef.parse(this.fechapago);
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        com.lam.cenicco.ws.TipoProceso tp;
        if (this.selectedTipoproceso == null) {
            com.lam.cenicco.ws.GrupoPago gp = ControladorWS.getInstance().findGrupoPagoById(this.selectedGruposPago[0]);
            tp = ControladorWS.getInstance().findTipoProcesoById(gp.getIdTipoproceso().getIdtipoproceso());
        } else {
            tp = ControladorWS.getInstance().findTipoProcesoById(this.selectedTipoproceso);
        }

        com.lam.cenicco.ws.Periodo p = new Periodo();
        p.setAnio(anio);
        p.setPeriodo(periodo);
        p.setIdtipoproceso(tp);
        p = ControladorWS.getInstance().findPeriodoByTipoProcesoAndPeriodoAndAnio(p);

        String referencia = sdf.format(fechaPago);
        String conceptoPago = "DISPERSION NOMINA ";

        if (p.getIdtipoproceso().getTipoproceso().equalsIgnoreCase("NA")) {
            conceptoPago = "DISPERSION AGUINALDO";
        }
        if (p.getPeriodo() % 2 == 1) {
            conceptoPago = conceptoPago + " 1RA " + Util.getNombreMes(p.getMes()).toUpperCase() + " " + p.getAnio().toString().substring(2, 4);
        } else {
            conceptoPago = conceptoPago + " 2DA " + Util.getNombreMes(p.getMes()).toUpperCase() + " " + p.getAnio().toString().substring(2, 4);
        }

        for (VistaCuentaBancaria v : this.selectedCuentasBancarias) {
            RelacionLaboral rl = relaciones.get(v.getIdrellab().toString());
            String correo = rl != null ? correos.get(rl.getIdempleado().getIdempleado().toString()) : "correo@correo.com";

            String importe = (prueba) ? Util.getSueldoNormalizado(0.01) : Util.getSueldoNormalizado(v.getImporte());
            String parteEntera;
            String parteDecimal;

            if (importe.length() < 3) {
                int numeroCeros = 3 - importe.length();
                StringBuilder ceros = new StringBuilder();
                for (int i = 0; i < numeroCeros; i++) {
                    ceros.append("0");
                }
                parteEntera = ceros.toString() + importe;
            } else {
                parteEntera = importe.substring(0, importe.length() - 2);
            }
            parteDecimal = importe.substring(importe.length() - 2);

            importe = parteEntera + "." + parteDecimal;

            String linea = v.getClabe()
                    + ","
                    + importe
                    + ","
                    + correo
                    + ","
                    + conceptoPago
                    + ","
                    + referencia;

            lineas.add(linea);
        }

        return lineas;
    }

    private List<String> layoutGrupoFinancieroInbursa(Banco banco, boolean prueba) {
        int i = 0;
        List<String> lineas = new ArrayList<>();
        for (VistaCuentaBancaria v : this.selectedCuentasBancarias) {
            String original = v.getNombre() + " " + v.getApaterno() + " " + v.getAmaterno();
            String cadenaNormalize = Normalizer.normalize(original, Normalizer.Form.NFD);
            String cadenaSinAcentos = cadenaNormalize.replaceAll("[^\\p{ASCII}]", "");
//
            String linea = ++i + "," + v.getNumeroempleado() + "," + cadenaSinAcentos + "," + v.getCuenta();
//  
            DecimalFormat df = new DecimalFormat("#.00");
            df.setRoundingMode(RoundingMode.DOWN);
            Double importe = (!prueba ? v.getImporte() : 1.01);
            String importeNormalizado = (!prueba ? df.format(importe) : importe.toString());
            if (importeNormalizado.contains(".00")) {
                importeNormalizado = importeNormalizado.replace(".00", "");
            }
//            
            linea = linea + "," + importeNormalizado;
            lineas.add(linea);
        }
        return lineas;
    }

    private List<String> layoutGrupoFinancieroInbursaPagoTerceros(Banco banco, boolean prueba) {
        DecimalFormat df = new DecimalFormat("#.00");
        df.setRoundingMode(RoundingMode.DOWN);

        com.lam.cenicco.ws.TipoProceso tp;
        if (this.selectedTipoproceso == null) {
            com.lam.cenicco.ws.GrupoPago gp = ControladorWS.getInstance().findGrupoPagoById(this.selectedGruposPago[0]);
            tp = ControladorWS.getInstance().findTipoProcesoById(gp.getIdTipoproceso().getIdtipoproceso());
        } else {
            tp = ControladorWS.getInstance().findTipoProcesoById(this.selectedTipoproceso);
        }

        com.lam.cenicco.ws.Periodo p = new Periodo();
        p.setAnio(anio);
        p.setPeriodo(periodo);
        p.setIdtipoproceso(tp);
        p = ControladorWS.getInstance().findPeriodoByTipoProcesoAndPeriodoAndAnio(p);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat sdfPago = new SimpleDateFormat("yyMMdd");
        Date fechaPago = null;
        try {
            fechaPago = sdfPago.parse(this.fechapago);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        String fechaAutorizacionPago = sdf.format(fechaPago);

        Integer i = 0;
        Double total = 0.0;
        List<String> lineas = new ArrayList<>();
        for (VistaCuentaBancaria v : this.selectedCuentasBancarias) {
            i++;
            String linea = "";
//
            Integer fechaAutorizacion = 8;
            linea += (fechaAutorizacionPago.length() > fechaAutorizacion ? fechaAutorizacionPago.substring(0, fechaAutorizacion) : fechaAutorizacionPago) + "\t";
//  
            Integer cuentaCargo = 18;
            linea += (banco.getCuenta().length() > cuentaCargo ? banco.getCuenta().substring(0, cuentaCargo) : banco.getCuenta()) + "\t";
//
            Integer cuentaAbono = 18;
            linea += (v.getClabe().length() > cuentaAbono ? v.getClabe().substring(0, cuentaAbono) : v.getClabe()) + "\t";
//
            Double importeTransaccion = (!prueba ? v.getImporte() : 0.01);
            total += importeTransaccion;
            String importeNormalizado = (!prueba ? df.format(importeTransaccion) : importeTransaccion.toString());

            Integer importe = 15;
            linea += (importeNormalizado.length() > importe ? importeNormalizado.substring(0, importe) : importeNormalizado) + "\t";
//
            String concepto = "DISPERSION NOMINA";
            if (p.getPeriodo() % 2 == 1) {
                concepto = concepto + " 1RA " + Util.getNombreMes(p.getMes()).toUpperCase() + " " + p.getAnio().toString().substring(2, 4);
            } else {
                concepto = concepto + " 2DA " + Util.getNombreMes(p.getMes()).toUpperCase() + " " + p.getAnio().toString().substring(2, 4);
            }

            Integer conceptoPago = 40;
            linea += (concepto.length() > conceptoPago ? concepto.substring(0, conceptoPago) : concepto) + "\t";
//
            String referencia = "NOM";
            if (p.getPeriodo() % 2 == 1) {
                referencia = referencia + "Q1" + p.getAniomes().substring(0, 3) + p.getAnio().toString().substring(2, 4);
            } else {
                referencia = referencia + "Q2" + p.getAniomes().substring(0, 3) + p.getAnio().toString().substring(2, 4);
            }

            Integer referenciaExterna = 20;
            linea += (referencia.length() > referenciaExterna ? referencia.substring(0, referenciaExterna) : referencia) + "\t";
//
            Integer referenciaNumerica = 7;
            linea += (this.secuencia.length() > referenciaNumerica ? this.secuencia.substring(0, referenciaNumerica) : this.secuencia) + "\t";
//           
            Integer rfcBeneficiario = 13;
            linea += (v.getRfc().length() > rfcBeneficiario ? v.getRfc().substring(0, rfcBeneficiario) : v.getRfc()) + "\t";
//
            String identificador = "2";
            linea += identificador;

            lineas.add(linea);
        }
        Integer numeroRegistros = 5;
        Integer montoTotal = 15;
        String totalTransaccion = df.format(total);
        String lineaHeader = (i.toString().length() > numeroRegistros ? i.toString().substring(0, numeroRegistros) : i.toString())
                + "\t"
                + (totalTransaccion.length() > montoTotal ? totalTransaccion.substring(0, montoTotal) : totalTransaccion);
        lineas.add(0, lineaHeader);

        return lineas;
    }

    public void lanzarPrueba() {
        if (this.selectedCuentasBancarias != null
                && !this.selectedCuentasBancarias.isEmpty()) {
//            
            List<String> lineas;
            String gp = "";
            if (this.selectedGruposPago[0] != null) {
                GrupoPago grupopago = ControladorWS.getInstance().findGrupoPagoById(this.selectedGruposPago[0]);
                gp = grupopago.getGrupopago();
            }
            String nombrearchivo = "DispersionBancaria_" + gp + "_" + this.periodo + this.anio;
//        
            Banco banco = ControladorWS.getInstance().findBancoById(this.selectedBanco);
//        
            AppBean bean = new AppBean();
            switch (banco.getBanco()) {
                case Parametros.BANCO_BANCOMER:
                    if (bean.isServicioMelonn()) {
                        lineas = this.layoutBancomerMelonn(banco, true);
                    } else {
                        lineas = this.layoutBancomer(banco, true);
                    }
                    break;
                case Parametros.BANCO_BANAMEX:
                    if (bean.isServicioEPAM()) {
                        lineas = this.layoutPaylink1024Citibanamex(banco, true);
                        nombrearchivo = "EPAM_Quincena " + this.periodo + " de " + this.anio;
                    } else {
                        lineas = this.layoutBanamex(banco, true);
                        String numeroCliente = "000000000000".substring(banco.getNocliente().length()) + banco.getNocliente();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
                        SimpleDateFormat sdfBanco = new SimpleDateFormat("ddMMyy");
                        String fechaPago = "";
                        try {
                            fechaPago = sdfBanco.format(sdf.parse(this.fechapago));
                        } catch (Exception e) {
                            System.out.println(e.toString());
                        }
                        nombrearchivo = "S274000007.27409E01" + ".04" + "." + fechaPago + "." + numeroCliente + "." + this.secuencia + ".NOM_CFX";
                    }
                    break;
                case Parametros.BANCO_BANAMEX_PROVEEDORES:
                    lineas = this.layoutBanamexProvedores(banco, true);
                    break;
                case Parametros.BANCO_BANORTE:
                    lineas = this.layoutBanorte(banco, true);
                    break;
                case Parametros.BANCO_INBURSA:
                    lineas = this.layoutInbursa();
                    break;
                case Parametros.BANCO_INTERBANCARIA_BANCOMER:
                    if (bean.isServicioMelonn()) {
                        lineas = this.layoutInterbancariaBancomerSPEI(banco, true);
                    } else if (bean.isServicioGrupoOrdas()) {
                        lineas = this.layoutInterbancariaBancomerOrdas(banco, true);
                    } else {
                        lineas = this.layoutInterbancariaBancomer(banco, true);
                    }
                    break;
                case Parametros.BANCO_INTERBANCARIA_BANORTE:
                    lineas = this.layoutInterbancariaBanorte(banco, true);
                    break;
                case Parametros.BANCO_HSBC:
                    lineas = this.layoutHsbc(banco, true);
                    break;
                case Parametros.BANCO_MONEX:
                    lineas = this.layoutMonex(banco, true);
                    break;
                case Parametros.BANCO_SANTANDER:
                    lineas = this.layoutSantander(banco, true);
                    break;
                case Parametros.BANCO_SANTANDER_PAGO_TERCEROS:
                    lineas = this.layoutSantanderPagoTerceros(banco, true);
                    break;
                case Parametros.BANCO_GRUPO_FINANCIERO_INBURSA:
                    lineas = this.layoutGrupoFinancieroInbursa(banco, true);
                    break;
                case Parametros.BANCO_CB_INTERCAM:
                    lineas = this.layoutCBIntercam(banco, true);
                    break;
                default:
                    lineas = new ArrayList<>();
                    break;
            }
//        
            Util.escribirFichero(lineas, nombrearchivo, ParametrosReportes.ARCHIVO_TXT);
//            
        }
    }

    @Override
    public void listenerSelected() {
        if (this.selectedCuentasBancarias != null
                && !this.selectedCuentasBancarias.isEmpty()) {
//            
            List<String> lineas = new ArrayList<>();
            String gp = "";
            if (this.selectedGruposPago[0] != null) {
                GrupoPago grupopago = ControladorWS.getInstance().findGrupoPagoById(this.selectedGruposPago[0]);
                gp = grupopago.getGrupopago();
            }
            String nombrearchivo = "DispersionBancaria_" + gp + "_" + this.periodo + this.anio;
//        
            Banco banco = ControladorWS.getInstance().findBancoById(this.selectedBanco);
//        
            AppBean bean = new AppBean();
            switch (banco.getBanco()) {
                case Parametros.BANCO_BANCOMER:
                    if (bean.isServicioMelonn()) {
                        lineas = this.layoutBancomerMelonn(banco, false);
                    } else {
                        lineas = this.layoutBancomer(banco, false);
                    }
                    break;
                case Parametros.BANCO_BANAMEX:
                    lineas = this.layoutBanamex(banco, false);
                    String numeroCliente = "000000000000".substring(banco.getNocliente().length()) + banco.getNocliente();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
                    SimpleDateFormat sdfBanco = new SimpleDateFormat("ddMMyy");
                    String fechaPago = "";
                    try {
                        fechaPago = sdfBanco.format(sdf.parse(this.fechapago));
                    } catch (Exception e) {
                        System.out.println(e.toString());
                    }
                    nombrearchivo = "S274000007.27409E01" + ".04" + "." + fechaPago + "." + numeroCliente + "." + this.secuencia + ".NOM_CFX";
                    break;
                case Parametros.BANCO_BANAMEX_PROVEEDORES:
                    lineas = this.layoutBanamexProvedores(banco, false);
                    break;
                case Parametros.BANCO_INBURSA:
                    lineas = this.layoutInbursa();
                    break;
                case Parametros.BANCO_INTERBANCARIA_BANCOMER:
                    if (bean.isServicioMelonn()) {
                        lineas = this.layoutInterbancariaBancomerSPEI(banco, false);
                    } else if (bean.isServicioGrupoOrdas()) {
                        lineas = this.layoutInterbancariaBancomerOrdas(banco, false);
                    } else {
                        lineas = this.layoutInterbancariaBancomer(banco, false);
                    }
                    break;
                case Parametros.BANCO_BANORTE:
                    lineas = this.layoutBanorte(banco, false);
                    break;
                case Parametros.BANCO_INTERBANCARIA_BANORTE:
                    lineas = this.layoutBanorte(banco, false);
                    break;
                case Parametros.BANCO_SANTANDER:
                    lineas = this.layoutSantander(banco, false);
                    break;
                case Parametros.BANCO_BANBAJIO:
                    lineas = this.layoutBanbajio(banco);
                    break;
                case Parametros.BANCO_HSBC:
                    lineas = this.layoutHsbc(banco, false);
                    break;
                case Parametros.BANCO_HSBC_INTERBANCARIA:
                    lineas = this.layoutHsbcInterbancarias(banco, false);
                    break;
                case Parametros.BANCO_MONEX:
                    lineas = this.layoutMonex(banco, false);
                    break;
                case Parametros.BANCO_SANTANDER_PAGO_TERCEROS:
                    lineas = this.layoutSantanderPagoTerceros(banco, false);
                    break;
                case Parametros.BANCO_GRUPO_FINANCIERO_INBURSA:
                    lineas = this.layoutGrupoFinancieroInbursa(banco, false);
                    break;
                case Parametros.BANCO_GRUPO_FINANCIERO_INBURSA_PAGO_TERCEROS:
                    lineas = this.layoutGrupoFinancieroInbursaPagoTerceros(banco, false);
                    break;
                case Parametros.BANCO_CB_INTERCAM:
                    lineas = this.layoutCBIntercam(banco, false);
                    break;
                default:
                    lineas = new ArrayList<>();
                    break;
            }
//        
            Util.escribirFichero(lineas, nombrearchivo, ParametrosReportes.ARCHIVO_TXT);
//            
        }

    }

    public void descargarReporte() {

        GrupoPago gp = ControladorWS.getInstance().findGrupoPagoById(this.selectedGruposPago[0]);
        Banco banco = ControladorWS.getInstance().findBancoById(this.selectedBanco);
//        
        String nombrearchivo = "DispersionBancaria_" + gp.getGrupopago() + "_" + this.periodo + this.anio;
//        
        System.out.println("Descarga Reporte...");
        RequestContext context = RequestContext.getCurrentInstance();
//        
        this.totalselected = 0.0;
        for (VistaCuentaBancaria v : this.selectedCuentasBancarias) {
            this.totalselected += v.getImporte();
        }
//        
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_DISPERSION_BANCARIA);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, nombrearchivo);

        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.selectedCuentasBancarias);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.PARAMETRO_PERIODO, this.periodo.toString());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.PARAMETRO_BANCO, banco.getBanco());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.PARAMETRO_GRUPO_PAGO, gp.getNombre());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.PARAMETRO_COMPANIA, gp.getIdcompania().getNombre());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.PARAMETRO_TIPO_NOMINA, gp.getGrupopago());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.PARAMETRO_FECHA_PAGO, this.fechapago);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.PARAMETRO_TOTAL, this.totalselected);
//
        context.addCallbackParam("ruta", MyPaths.urlServletReporte());

    }

    @Override
    public void generarMsg(FacesMessage msg, boolean isValidate) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getInformacion() {
        return CeniccoUtil.getInformacion(this.cuentasBancarias.size());
    }

    public List<VistaCuentaBancaria> getFilteredCuentasBancarias() {
        return filteredCuentasBancarias;
    }

    public void setFilteredCuentasBancarias(List<VistaCuentaBancaria> filteredCuentasBancarias) {
        this.filteredCuentasBancarias = filteredCuentasBancarias;
    }

    public List<VistaCuentaBancaria> getSelectedCuentasBancarias() {
        return selectedCuentasBancarias;
    }

    public void setSelectedCuentasBancarias(List<VistaCuentaBancaria> selectedCuentasBancarias) {
        this.selectedCuentasBancarias = selectedCuentasBancarias;
    }

    public Integer[] getSelectedGruposPago() {
        return selectedGruposPago;
    }

    public void setSelectedGruposPago(Integer[] selectedGruposPago) {
        this.selectedGruposPago = selectedGruposPago;
    }

    public Integer getSelectedBanco() {
        return selectedBanco;
    }

    public void setSelectedBanco(Integer selectedBanco) {
        this.selectedBanco = selectedBanco;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public Integer getPeriodo() {
        return periodo;
    }

    public void setPeriodo(Integer periodo) {
        this.periodo = periodo;
    }

    public List<VistaCuentaBancaria> getCuentasBancarias() {
        return cuentasBancarias;
    }

    public String getFechapago() {
        return fechapago;
    }

    public void setFechapago(String fechapago) {
        this.fechapago = fechapago;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public String getSecuencia() {
        return secuencia;
    }

    public void setSecuencia(String secuencia) {
        this.secuencia = secuencia;
    }

    public Integer getSelectedTipoproceso() {
        return selectedTipoproceso;
    }

    public void setSelectedTipoproceso(Integer selectedTipoproceso) {
        this.selectedTipoproceso = selectedTipoproceso;
    }

    public Integer getSelectedestatus() {
        return selectedestatus;
    }

    public void setSelectedestatus(Integer selectedestatus) {
        this.selectedestatus = selectedestatus;
    }

    public Double getTotalselected() {
        return totalselected;
    }

    public void setTotalselected(Double totalselected) {
        this.totalselected = totalselected;
    }

    public Integer[] getSelectedCentrosCostos() {
        return selectedCentrosCostos;
    }

    public void setSelectedCentrosCostos(Integer[] selectedCentrosCostos) {
        this.selectedCentrosCostos = selectedCentrosCostos;
    }

    private List<String> layoutPaylink1024Citibanamex(Banco bancoCompania, boolean prueba) {
        List<Banco> listaBancos = ControladorWS.getInstance().findBancos(new Banco());
        Collections.sort(this.selectedCuentasBancarias, new Comparator<VistaCuentaBancaria>() {
            @Override
            public int compare(VistaCuentaBancaria s1, VistaCuentaBancaria s2) {
                int n1 = Integer.parseInt(s1.getNumeroempleado());
                int n2 = Integer.parseInt(s2.getNumeroempleado());
                return Integer.compare(n1, n2);
            }
        });
        Integer i = 0;
        Double total = 0.0;
        StringBuilder linea;
        List<String> lineas = new ArrayList<>();
        for (VistaCuentaBancaria v : this.selectedCuentasBancarias) {
            i++;

            Banco bancoEmpleado = new Banco();
            for (Banco b : listaBancos) {
                if (b.getIdbanco().equals(v.getIdbancoempleado())) {
                    bancoEmpleado = b;
                }
            }

            linea = new StringBuilder();
            linea.append("PAY485          ");
            linea.append(this.fechapago);
            linea.append((bancoCompania.getIdbanco() == bancoEmpleado.getIdbanco()) ? "072" : "002");
            linea.append("NOMINA");
            linea.append(this.periodo);
            linea.append(v.getNumeroempleado());
            linea.append(" ");

            String contador = "00000000".substring(i.toString().length());
            linea.append(contador);
            linea.append(i.toString());

            linea.append(v.getRfc());
            linea.append("       ");

            linea.append("MXN");
            linea.append(v.getRfc().substring(0, 4));
            linea.append("                ");

            DecimalFormat df = new DecimalFormat("#.00");
            df.setRoundingMode(RoundingMode.DOWN);
            Double importe = (!prueba ? v.getImporte() : 0.01);
            total += importe;
            String importeNormalizado = df.format(importe);
            if (importeNormalizado.contains(".")) {
                importeNormalizado = importeNormalizado.replaceAll("\\p{Punct}", "");
            }
            String cadenaImporte = "000000000000000".substring(importeNormalizado.length());
            linea.append(cadenaImporte);
            linea.append(importeNormalizado);

            linea.append("                                         ");
            linea.append("NOMINA");
            linea.append("                                                                                                   ");

            linea.append("07");
            linea.append("01");

            String original = v.getNombre();
            String cadenaNormalize = Normalizer.normalize(original, Normalizer.Form.NFD);
            String cadenaSinAcentos = cadenaNormalize.replaceAll("[^\\p{ASCII}]", "");
            String cadenaNombre = "                                                                                ".substring(cadenaSinAcentos.length());
            linea.append(cadenaSinAcentos);
            linea.append(cadenaNombre);

            linea.append("AV AMERICAS                        MEXICO");
            linea.append("                                                                          ");

            linea.append(v.getClabe().substring(0, 3));
            linea.append("        ");
            linea.append(v.getClabe());
            linea.append("                 ");

            linea.append("05MEXICO                        00000000000000000NN0070108283643                                                                        ");
            linea.append("001                                                  00000                                                  ");
            linea.append("999999999999999 NONE                                                                                                                                                                                                                                                                      ");

            lineas.add(linea.toString());
        }

        linea = new StringBuilder();
        linea.append("TRL");
        String cadenaFinal = "000000000000000".substring(i.toString().length());
        linea.append(cadenaFinal);
        linea.append(i.toString());

        DecimalFormat df = new DecimalFormat("#.00");
        Double importe = total;
        String importeNormalizado = df.format(importe);
        if (importeNormalizado.contains(".")) {
            importeNormalizado = importeNormalizado.replaceAll("\\p{Punct}", "");
        }
        cadenaFinal = "000000000000000".substring(importeNormalizado.length());
        linea.append(cadenaFinal);
        linea.append(importeNormalizado);

        cadenaFinal = "000000000000000000000000000000".substring(i.toString().length());
        linea.append(cadenaFinal);
        linea.append(i.toString());
        lineas.add(linea.toString());

        return lineas;
    }

    private List<String> layoutPayLinkPlus500Banamex(Banco bancoCompania, boolean prueba) {
        List<Banco> listaBancos = ControladorWS.getInstance().findBancos(new Banco());
        Collections.sort(this.selectedCuentasBancarias, new Comparator<VistaCuentaBancaria>() {
            @Override
            public int compare(VistaCuentaBancaria s1, VistaCuentaBancaria s2) {
                int n1 = Integer.parseInt(s1.getNumeroempleado());
                int n2 = Integer.parseInt(s2.getNumeroempleado());
                return Integer.compare(n1, n2);
            }
        });
        Integer i = 0;
        Double total = 0.0;
        StringBuilder linea;
        List<String> lineas = new ArrayList<>();
        for (VistaCuentaBancaria v : this.selectedCuentasBancarias) {
            i++;
            Domicilio domicilioFiscal = ControladorWS.getInstance().getDomicilioFiscalByIdEmpleado(v.getIdbancoempleado());
            Banco bancoEmpleado = new Banco();
            for (Banco b : listaBancos) {
                if (b.getIdbanco().equals(v.getIdbancoempleado())) {
                    bancoEmpleado = b;
                }
            }

            linea = new StringBuilder();
            linea.append("BBN");
            linea.append("485");
            linea.append(bancoCompania.getCuenta());
            linea.append("01");

            String contador = "0000000000".substring(i.toString().length());
            linea.append(contador);
            linea.append(i.toString());

            String nombre = v.getNombre();
            String nombreNormalize = Normalizer.normalize(nombre, Normalizer.Form.NFD);
            String nombreSinAcentos = nombreNormalize.replaceAll("[^\\p{ASCII}]", "");
            String cadenaNombre = "                                                                                ".substring(nombreSinAcentos.length());
            linea.append(nombreSinAcentos);
            linea.append(cadenaNombre);

            String domicilio = domicilioFiscal.getCp();
            String domicilioNormalize = Normalizer.normalize(domicilio, Normalizer.Form.NFD);
            String domicilioSinAcentos = domicilioNormalize.replaceAll("[^\\p{ASCII}]", "");
            String cadenaDomicilio = "                              ".substring(domicilioSinAcentos.length());
            linea.append(domicilioSinAcentos);
            linea.append(cadenaDomicilio);

            linea.append(cadenaDomicilio);

            linea.append("               ");
            linea.append("        ");
            linea.append("  ");

            String telefono = "9999999999";
            String cadenaTelefono = "           ".substring(telefono.length());
            linea.append(telefono);
            linea.append(cadenaTelefono);

            linea.append((bancoCompania.getIdbanco() == bancoEmpleado.getIdbanco()) ? "   " : v.getClabe().substring(0, 3));

            linea.append((bancoCompania.getIdbanco() == bancoEmpleado.getIdbanco()) ? "        " : "001     ");

            String cuenta = (bancoCompania.getIdbanco() == bancoEmpleado.getIdbanco()) ? v.getCuenta() : v.getClabe();
            String cadenaCuenta = "                  ".substring(cuenta.length());
            linea.append(cuenta);
            linea.append(cadenaCuenta);

            linea.append((bancoCompania.getIdbanco() == bancoEmpleado.getIdbanco()) ? "03" : "05");

            linea.append("                                                  ");

            DecimalFormat df = new DecimalFormat("#.00");
            df.setRoundingMode(RoundingMode.DOWN);
            Double importe = (!prueba ? v.getImporte() : 0.01);
            total += importe;
            String importeNormalizado = df.format(importe);
            if (importeNormalizado.contains(".")) {
                importeNormalizado = importeNormalizado.replaceAll("\\p{Punct}", "");
            }
            String cadenaImporte = "000000000000000".substring(importeNormalizado.length());
            linea.append(cadenaImporte);
            linea.append(importeNormalizado);

            linea.append("          ");
            linea.append("                    ");
            linea.append("               ");

            String rfc = v.getRfc();
            String cadenaRFC = "              ".substring(rfc.length());
            linea.append(rfc);
            linea.append(cadenaRFC);

            linea.append("2");

            linea.append("                  ");
            linea.append("  ");

            linea.append((bancoCompania.getIdbanco() == bancoEmpleado.getIdbanco()) ? "003" : "001");

            linea.append((bancoCompania.getIdbanco() == bancoEmpleado.getIdbanco()) ? "  " : "01");

            linea.append((bancoCompania.getIdbanco() == bancoEmpleado.getIdbanco()) ? "   " : "001");

            String localidad = "MEXICO";
            String cadenaLocalidad = "              ".substring(localidad.length());
            linea.append(localidad);
            linea.append(cadenaLocalidad);

            linea.append((bancoCompania.getIdbanco() == bancoEmpleado.getIdbanco()) ? "   " : "001");

            String sucursal = "MEXICO";
            String cadenaSucursal = "                   ".substring(sucursal.length());
            linea.append(sucursal);
            linea.append(cadenaSucursal);

            linea.append("                                                  ");
            linea.append("     ");
            linea.append("                                            ");

            lineas.add(linea.toString());
        }


        linea = new StringBuilder();
        linea.append("TRL");

        String cadenaFinal = "000000000000000".substring(i.toString().length());
        linea.append(cadenaFinal);
        linea.append(i.toString());

        DecimalFormat df = new DecimalFormat("#.00");
        Double importe = total;
        String importeNormalizado = df.format(importe);
        if (importeNormalizado.contains(".")) {
            importeNormalizado = importeNormalizado.replaceAll("\\p{Punct}", "");
        }
        cadenaFinal = "000000000000000".substring(importeNormalizado.length());
        linea.append(cadenaFinal);
        linea.append(importeNormalizado);

        cadenaFinal = "000000000000000".substring(i.toString().length());
        linea.append(cadenaFinal);
        linea.append(i.toString());

        cadenaFinal = "000000000000000".substring(i.toString().length());
        linea.append(cadenaFinal);
        linea.append(i.toString());

        linea.append("                                     ");

        lineas.add(linea.toString());

        return lineas;
    }

    private List<String> layoutCBIntercam(Banco bancoCompania, boolean prueba) {
        SimpleDateFormat sdfDia = new SimpleDateFormat("dd");
        Map<String, RelacionLaboral> relaciones = ControladorWS.getInstance().getMapaRelacionLaboralActivosByGrupoPago(this.selectedGruposPago[0]);
        Map<String, String> correos = ControladorWS.getInstance().getMails();
        com.lam.cenicco.ws.TipoProceso tp;
        if (this.selectedTipoproceso == null) {
            com.lam.cenicco.ws.GrupoPago gp = ControladorWS.getInstance().findGrupoPagoById(this.selectedGruposPago[0]);
            tp = ControladorWS.getInstance().findTipoProcesoById(gp.getIdTipoproceso().getIdtipoproceso());
        } else {
            tp = ControladorWS.getInstance().findTipoProcesoById(this.selectedTipoproceso);
        }

        com.lam.cenicco.ws.Periodo p = new Periodo();
        p.setAnio(anio);
        p.setPeriodo(periodo);
        p.setIdtipoproceso(tp);
        p = ControladorWS.getInstance().findPeriodoByTipoProcesoAndPeriodoAndAnio(p);

        Integer i = 0;
        Double total = 0.0;
        List<String> lineas = new ArrayList<>();

        StringBuilder encabezado = new StringBuilder();
        encabezado.append("TIP_REG,SECUENCIA,NUMERO,CLABE,RAZON_SOCIAL,IMPORTE,DESCRIPCION,CORREO,FILLER1,FILLER2");
        lineas.add(encabezado.toString());

        StringBuilder linea;
        for (VistaCuentaBancaria v : this.selectedCuentasBancarias) {
            RelacionLaboral rl = relaciones.get(v.getIdrellab().toString());
            String correo = rl != null ? correos.get(rl.getIdempleado().getIdempleado().toString()) : "correo@correo.com";
            i++;

            linea = new StringBuilder();
            linea.append("01");

            linea.append(",");

            String contador = "000000".substring(i.toString().length());
            linea.append(contador);
            linea.append(i.toString());

            linea.append(",");

            linea.append("40");
            String claveBanco = "EEE";
            try {
                claveBanco = v.getClabe().substring(0, 3);
            } catch (Exception e) {
            }
            linea.append(claveBanco);

            linea.append(",");

            linea.append(v.getClabe());

            linea.append(",");

            String nombre = v.getNombre();
            String nombreNormalize = Normalizer.normalize(nombre, Normalizer.Form.NFD);
            String nombreSinAcentos = nombreNormalize.replaceAll("[^\\p{ASCII}]", "");
            linea.append(nombreSinAcentos);

            linea.append(",");

            DecimalFormat df = new DecimalFormat("#.00");
            df.setRoundingMode(RoundingMode.DOWN);
            Double importe = (!prueba ? v.getImporte() : 0.01);
            total += importe;
            String importeNormalizado = df.format(importe);
            linea.append(importeNormalizado);

            linea.append(",");

            String diaInicio = sdfDia.format(p.getFechainicio().toGregorianCalendar().getTime());
            String diaFin = sdfDia.format(p.getFechafin().toGregorianCalendar().getTime());
            String descripcion = "NOMINA DEL " + diaInicio + " AL " + diaFin + " DE " + Util.getNombreMes(p.getMes()).toUpperCase() + " " + p.getAnio().toString().substring(2, 4);
            linea.append(descripcion);

            linea.append(",");

            linea.append(correo);

            linea.append(",");

            linea.append(v.getRfc());

            linea.append(",");

            lineas.add(linea.toString());
        }

        return lineas;
    }

    private List<String> layoutInterbancariaBancomerSPEI(Banco bancoCompania, boolean prueba) {
        com.lam.cenicco.ws.TipoProceso tp;
        if (this.selectedTipoproceso == null) {
            com.lam.cenicco.ws.GrupoPago gp = ControladorWS.getInstance().findGrupoPagoById(this.selectedGruposPago[0]);
            tp = ControladorWS.getInstance().findTipoProcesoById(gp.getIdTipoproceso().getIdtipoproceso());
        } else {
            tp = ControladorWS.getInstance().findTipoProcesoById(this.selectedTipoproceso);
        }

        com.lam.cenicco.ws.Periodo p = new Periodo();
        p.setAnio(anio);
        p.setPeriodo(periodo);
        p.setIdtipoproceso(tp);
        p = ControladorWS.getInstance().findPeriodoByTipoProcesoAndPeriodoAndAnio(p);

        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
        SimpleDateFormat sdfLayout = new SimpleDateFormat("yyyy-MM-dd");
        String fechaPago = "";
        try {
            fechaPago = sdfLayout.format(sdf.parse(this.fechapago));
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        List<String> lineas = new ArrayList<>();
        StringBuilder encabezado = new StringBuilder();
        encabezado.append("H");
        encabezado.append("001871617");
        encabezado.append(fechaPago);
        encabezado.append("01");

        String referencia = "NOMINA" + tp.getTipoproceso() + p.getPeriodo() + "SPEI";
        String cadena = Util.getCadenaConEspacios(30, " ").substring(referencia.length());
        encabezado.append(referencia + cadena);

        encabezado.append("00");
        encabezado.append(Util.getCadenaConEspacios(20, " "));
        encabezado.append(Util.getCadenaConEspacios(3, " "));
        encabezado.append(Util.getCadenaConEspacios(35, " "));
        encabezado.append(Util.getCadenaConEspacios(3, " "));
        encabezado.append(Util.getCadenaConEspacios(1251, " "));
        lineas.add(encabezado.toString());

        Integer i = 0;
        Double total = 0.0;
        StringBuilder linea;
        for (VistaCuentaBancaria v : this.selectedCuentasBancarias) {
            i++;

            linea = new StringBuilder();
            linea.append("DAP");

            String contador = i.toString();
            if (contador.length() <= 1) {
                contador = "0" + contador;
            }
            String referenciaSIT = "2003" + contador;
            String cadenaReferenciaSITNormalizado = Util.getCadenaConEspacios(20, " ").substring(referenciaSIT.length());
            linea.append(referenciaSIT + cadenaReferenciaSITNormalizado);

            String conceptoPago = !StringUtils.isNullOrEmpty(v.getNumerodispersion()) ? v.getNumerodispersion() : "ND";
            String cadenaConceptoPagoNormalizado = Util.getCadenaConEspacios(30, " ").substring(conceptoPago.length());
            linea.append(conceptoPago + cadenaConceptoPagoNormalizado);

            linea.append("PDA");
            linea.append("6");
            linea.append(Util.getCadenaConEspacios(20, "0"));
            linea.append(Util.getCadenaConEspacios(15, " "));

            String cadenaReferenciaNumericaNormalizado = Util.getCadenaConEspacios(25, " ").substring(referenciaSIT.length());
            linea.append(referenciaSIT + cadenaReferenciaNumericaNormalizado);

            String conceptoPagoReferencia = tp.getNombre().toUpperCase().replace(" ", "");
            String cadenaConceptoPagoReferencia = Util.getCadenaConEspacios(37, " ").substring(conceptoPagoReferencia.length());
            linea.append(conceptoPagoReferencia + cadenaConceptoPagoReferencia);

            linea.append(Util.getCadenaConEspacios(15, " "));
            linea.append(Util.getCadenaConEspacios(25, " "));
            linea.append(Util.getCadenaConEspacios(37, " "));
            linea.append("N");
            linea.append(Util.getCadenaConEspacios(8, " "));
            linea.append(Util.getCadenaConEspacios(2, " "));
            linea.append(Util.getCadenaConEspacios(35, " "));
            linea.append(Util.getCadenaConEspacios(40, " "));
            linea.append(Util.getCadenaConEspacios(1, " "));
            linea.append(Util.getCadenaConEspacios(30, " "));
            linea.append(Util.getCadenaConEspacios(40, " "));
            linea.append(Util.getCadenaConEspacios(1, " "));
            linea.append(Util.getCadenaConEspacios(30, " "));
            linea.append("MXP");
            linea.append(Util.getCadenaConEspacios(11, " "));
            linea.append(Util.getCadenaConEspacios(9, " "));
            linea.append("OT");

            DecimalFormat df = new DecimalFormat("#.00");
            df.setRoundingMode(RoundingMode.DOWN);
            Double importe = (!prueba ? v.getImporte() : 0.01);
            total += importe;
            String importeNormalizado = df.format(importe);
            if (importeNormalizado.contains(".")) {
                importeNormalizado = importeNormalizado.replaceAll("\\p{Punct}", "");
            }
            String cadenaImporteNormalizado = Util.getCadenaConEspacios(15, "0").substring(importeNormalizado.length());
            String importeDocumento = cadenaImporteNormalizado + importeNormalizado;
            linea.append(importeDocumento);

            linea.append(Util.getCadenaConEspacios(15, "0"));
            linea.append(Util.getCadenaConEspacios(2, "0"));
            linea.append(Util.getCadenaConEspacios(50, " "));
            linea.append("N");
            linea.append(Util.getCadenaConEspacios(8, "0"));
            linea.append(Util.getCadenaConEspacios(4, "0"));
            linea.append(fechaPago);
            linea.append("0001-01-01");
            linea.append("0001-01-01");
            linea.append("0001-01-01");
            linea.append(fechaPago);
            linea.append("N");
            linea.append(Util.getCadenaConEspacios(1, " "));
            linea.append("0001-01-01");
            linea.append("700");
            linea.append(Util.getCadenaConEspacios(700, " "));
            linea.append(Util.getCadenaConEspacios(10, " "));
            linea.append(Util.getCadenaConEspacios(10, " "));
            linea.append(Util.getCadenaConEspacios(2, " "));
            linea.append(Util.getCadenaConEspacios(30, " "));
            linea.append("0001-01-01");

            lineas.add(linea.toString());
        }

        StringBuilder piePagina = new StringBuilder();
        piePagina.append("T");

        String cadenaTotalRegistros = Util.getCadenaConEspacios(10, "0").substring(i.toString().length());
        String totalRegistros = cadenaTotalRegistros + i.toString();
        piePagina.append(totalRegistros);

        DecimalFormat df = new DecimalFormat("#.00");
        Double importe = total;
        String importeNormalizado = df.format(importe);
        if (importeNormalizado.contains(".")) {
            importeNormalizado = importeNormalizado.replaceAll("\\p{Punct}", "");
        }
        String cadenaTotalImporte = Util.getCadenaConEspacios(15, "0").substring(importeNormalizado.length());
        String totalImporte = cadenaTotalImporte + importeNormalizado;
        piePagina.append(totalImporte);

        piePagina.append(Util.getCadenaConEspacios(10, "0"));
        piePagina.append(Util.getCadenaConEspacios(15, "0"));
        piePagina.append(Util.getCadenaConEspacios(10, "0"));
        piePagina.append(Util.getCadenaConEspacios(15, "0"));
        piePagina.append(Util.getCadenaConEspacios(10, "0"));
        piePagina.append(Util.getCadenaConEspacios(15, "0"));
        piePagina.append(Util.getCadenaConEspacios(10, "0"));
        piePagina.append(Util.getCadenaConEspacios(15, "0"));
        piePagina.append(Util.getCadenaConEspacios(10, "0"));
        piePagina.append(Util.getCadenaConEspacios(15, "0"));
        piePagina.append(Util.getCadenaConEspacios(10, "0"));
        piePagina.append(Util.getCadenaConEspacios(15, "0"));
        piePagina.append(Util.getCadenaConEspacios(10, "0"));
        piePagina.append(Util.getCadenaConEspacios(15, "0"));
        piePagina.append(Util.getCadenaConEspacios(10, "0"));
        piePagina.append(Util.getCadenaConEspacios(15, "0"));
        piePagina.append(Util.getCadenaConEspacios(10, "0"));
        piePagina.append(Util.getCadenaConEspacios(15, "0"));
        piePagina.append(Util.getCadenaConEspacios(10, "0"));
        piePagina.append(Util.getCadenaConEspacios(15, "0"));
        piePagina.append(Util.getCadenaConEspacios(10, "0"));
        piePagina.append(Util.getCadenaConEspacios(15, "0"));
        piePagina.append(Util.getCadenaConEspacios(10, "0"));
        piePagina.append(Util.getCadenaConEspacios(15, "0"));
        piePagina.append(Util.getCadenaConEspacios(10, "0"));
        piePagina.append(Util.getCadenaConEspacios(15, "0"));
        piePagina.append(Util.getCadenaConEspacios(10, "0"));
        piePagina.append(Util.getCadenaConEspacios(15, "0"));
        piePagina.append(Util.getCadenaConEspacios(10, "0"));
        piePagina.append(Util.getCadenaConEspacios(15, "0"));
        piePagina.append(Util.getCadenaConEspacios(10, "0"));
        piePagina.append(Util.getCadenaConEspacios(15, "0"));
        piePagina.append(Util.getCadenaConEspacios(10, "0"));
        piePagina.append(Util.getCadenaConEspacios(15, "0"));
        piePagina.append(Util.getCadenaConEspacios(10, "0"));
        piePagina.append(Util.getCadenaConEspacios(15, "0"));
        piePagina.append(Util.getCadenaConEspacios(10, "0"));
        piePagina.append(Util.getCadenaConEspacios(15, "0"));
        piePagina.append(Util.getCadenaConEspacios(10, "0"));
        piePagina.append(Util.getCadenaConEspacios(15, "0"));
        piePagina.append(Util.getCadenaConEspacios(10, "0"));
        piePagina.append(Util.getCadenaConEspacios(15, "0"));
        piePagina.append(Util.getCadenaConEspacios(10, "0"));
        piePagina.append(Util.getCadenaConEspacios(15, "0"));
        piePagina.append(Util.getCadenaConEspacios(10, "0"));
        piePagina.append(Util.getCadenaConEspacios(15, "0"));
        piePagina.append(Util.getCadenaConEspacios(10, "0"));
        piePagina.append(Util.getCadenaConEspacios(15, "0"));
        piePagina.append(Util.getCadenaConEspacios(10, "0"));
        piePagina.append(Util.getCadenaConEspacios(15, "0"));
        piePagina.append(Util.getCadenaConEspacios(10, "0"));
        piePagina.append(Util.getCadenaConEspacios(15, "0"));
        piePagina.append(Util.getCadenaConEspacios(10, "0"));
        piePagina.append(Util.getCadenaConEspacios(15, "0"));
        piePagina.append(Util.getCadenaConEspacios(10, "0"));
        piePagina.append(Util.getCadenaConEspacios(15, "0"));
        piePagina.append(Util.getCadenaConEspacios(10, "0"));
        piePagina.append(Util.getCadenaConEspacios(15, "0"));
        piePagina.append(Util.getCadenaConEspacios(10, "0"));
        piePagina.append(Util.getCadenaConEspacios(15, "0"));
        piePagina.append(Util.getCadenaConEspacios(10, "0"));
        piePagina.append(Util.getCadenaConEspacios(15, "0"));
        piePagina.append(Util.getCadenaConEspacios(10, "0"));
        piePagina.append(Util.getCadenaConEspacios(15, "0"));
        piePagina.append(Util.getCadenaConEspacios(10, "0"));
        piePagina.append(Util.getCadenaConEspacios(15, "0"));
        piePagina.append(Util.getCadenaConEspacios(10, "0"));
        piePagina.append(Util.getCadenaConEspacios(15, "0"));
        piePagina.append(Util.getCadenaConEspacios(10, "0"));
        piePagina.append(Util.getCadenaConEspacios(15, "0"));
        piePagina.append(Util.getCadenaConEspacios(10, "0"));
        piePagina.append(Util.getCadenaConEspacios(15, "0"));
        piePagina.append(Util.getCadenaConEspacios(10, "0"));
        piePagina.append(Util.getCadenaConEspacios(15, "0"));
        piePagina.append(Util.getCadenaConEspacios(10, "0"));
        piePagina.append(Util.getCadenaConEspacios(15, "0"));
        piePagina.append(Util.getCadenaConEspacios(10, "0"));
        piePagina.append(Util.getCadenaConEspacios(15, "0"));
        piePagina.append(Util.getCadenaConEspacios(10, "0"));
        piePagina.append(Util.getCadenaConEspacios(15, "0"));
        piePagina.append(Util.getCadenaConEspacios(10, "0"));
        piePagina.append(Util.getCadenaConEspacios(15, "0"));
        piePagina.append(Util.getCadenaConEspacios(10, "0"));
        piePagina.append(Util.getCadenaConEspacios(15, "0"));
        piePagina.append(Util.getCadenaConEspacios(10, "0"));
        piePagina.append(Util.getCadenaConEspacios(15, "0"));
        piePagina.append(Util.getCadenaConEspacios(10, "0"));
        piePagina.append(Util.getCadenaConEspacios(15, "0"));
        piePagina.append(Util.getCadenaConEspacios(10, "0"));
        piePagina.append(Util.getCadenaConEspacios(15, "0"));
        piePagina.append(Util.getCadenaConEspacios(10, "0"));
        piePagina.append(Util.getCadenaConEspacios(15, "0"));
        piePagina.append(Util.getCadenaConEspacios(10, "0"));
        piePagina.append(Util.getCadenaConEspacios(15, "0"));
        piePagina.append(Util.getCadenaConEspacios(10, "0"));
        piePagina.append(Util.getCadenaConEspacios(15, "0"));
        piePagina.append(Util.getCadenaConEspacios(10, "0"));
        piePagina.append(Util.getCadenaConEspacios(15, "0"));
        piePagina.append(Util.getCadenaConEspacios(10, "0"));
        piePagina.append(Util.getCadenaConEspacios(15, "0"));
        piePagina.append(Util.getCadenaConEspacios(10, "0"));
        piePagina.append(Util.getCadenaConEspacios(15, "0"));
        piePagina.append(Util.getCadenaConEspacios(65, " "));
        lineas.add(piePagina.toString());

        return lineas;
    }
}
