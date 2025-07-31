/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lam.cenicco.beans.nominas.polizacontable;

import com.lam.cenicco.dao.ProcesoDAO;
import com.lam.cenicco.impl.ControladorWS;
import com.lam.cenicco.librerias.utilidades.CeniccoUtil;
import com.lam.cenicco.util.ControladorSesiones;
import com.lam.cenicco.util.Parametros;
import com.lam.cenicco.ws.Parametro;
import com.lam.cenicco.util.ParametrosReportes;
import com.lam.cenicco.util.Util;
import com.lam.cenicco.util.paths.MyPaths;
import com.lam.cenicco.util.poliza.PolizaTO;
import com.lam.cenicco.util.poliza.PolizaUtil;
import com.lam.cenicco.ws.CifrasNomina;
import com.lam.cenicco.ws.Compania;
import com.lam.cenicco.ws.GrupoPago;
import com.lam.cenicco.ws.IntegracionPolizaContable;
import com.lam.cenicco.ws.Periodo;
import com.lam.cenicco.ws.TipoProceso;
import com.mysql.jdbc.StringUtils;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.primefaces.context.RequestContext;

/**
 *
 * @author JoséAntonio
 */
public class PolizaContableCenicco implements ProcesoDAO<CifrasNomina> {
//

    protected String[] selectedRelaciones;
//    
    protected List<PolizaTO> header;
//    
    protected List<PolizaTO> filteredHeader;
//    
    protected List<CifrasNomina> poliza;
//    
    protected Integer[] selectedGruposPago;
//    
    protected Integer periodo;
//    
    protected Integer anio;
//    
    protected Double totalAbono;
//    
    protected Double totalCargo;
//  
    protected Double totalNeto;
//    
    protected List<String> relaciones;
//    
    protected Integer[] selectedTiposNomina;
//    
    protected Integer selectedestatus;
//
    protected List<Parametro> tiposPoliza;
//  
    protected List<String> tiposPolizaValues;
//
    protected String selectedTipoPoliza;
//
    protected Map<String, String> mapaRelacionesLaborales;
//    
    private PolizaUtil util;
//    
    private String consecutivo;
//    
    private boolean headerConceptos;

    public PolizaContableCenicco() {
        if (this.util == null) {
            this.util = new PolizaUtil();
        }
    }

    @PostConstruct
    @Override
    public void init() {
        this.limpiar(null);
    }

    @Override
    public void consultar(ActionEvent event) {
        this.totalAbono = 0.0;
        this.totalCargo = 0.0;
        this.totalNeto = 0.0;
//        
        this.header = this.calcularPolizaContable();
        this.filteredHeader = this.header;
    }

    public void updateEmpleados(ActionEvent event) {
        this.selectedRelaciones = null;
//        
        this.mapaRelacionesLaborales = new HashMap<>();
//  
        List<CifrasNomina> relacionesAux = new ArrayList<>();
        for (Integer gp : this.selectedGruposPago) {
            for (Integer tp : this.selectedTiposNomina) {
                List<CifrasNomina> aux = ControladorWS.getInstance().getRelacionesLaboralesXNomina(gp, tp,
                        this.selectedestatus, this.periodo, this.anio);
                for (CifrasNomina n : aux) {
                    relacionesAux.add(n);
                }
            }
        }
//        

        this.relaciones = new ArrayList<>();
//        
        for (CifrasNomina rl : relacionesAux) {
            String llave = rl.getNumeroempleado() + " - " + rl.getNombreempleado();
//            
            if (this.selectedestatus == null) {
                if (this.mapaRelacionesLaborales.get(llave) == null) {
                    System.out.println("Llave.... " + llave);
                    this.mapaRelacionesLaborales.put(llave, rl.getIdrellab().toString());
                    this.relaciones.add(llave);
                }
                continue;
            }
//            
            switch (this.selectedestatus.toString()) {
                case "0":
                    if (rl.getEstatus().equals("0")
                            && this.mapaRelacionesLaborales.get(llave) == null) {
                        System.out.println("Llave.... " + llave);
                        this.mapaRelacionesLaborales.put(llave, rl.getIdrellab().toString());
                        this.relaciones.add(llave);
                    }
                    break;
                case "1":
                    if (rl.getEstatus().equals("1")
                            && this.mapaRelacionesLaborales.get(llave) == null) {
                        System.out.println("Llave.... " + llave);
                        this.mapaRelacionesLaborales.put(llave, rl.getIdrellab().toString());
                        this.relaciones.add(llave);
                    }
                    break;
            }
        }
//        
    }

    public List<PolizaTO> calcularPolizaContable() {
        boolean isValidate = this.consultar();
//        
        if (!isValidate) {
            return new ArrayList<>();
        }
//        
        Map<String, PolizaTO> mapa = new HashMap<>();
        Map<String, CifrasNomina> mapaacum = new HashMap<>();
//      
        for (int gp : selectedGruposPago) {
            //Total neto de la póliza
            this.totalNeto += ControladorWS.getInstance().getNetoPagarPolizaContable(gp, this.periodo, this.anio);
        }
//      

        String conceptosStringParametro = null;
        if (!StringUtils.isNullOrEmpty(this.selectedTipoPoliza)) {
            for (Parametro p : this.tiposPoliza) {
                if (p.getParametro().equals(selectedTipoPoliza)) {
                    conceptosStringParametro = p.getValor();
                    break;
                }
            }
        }

        List<String> conceptosParametro = new ArrayList<>();
        if (conceptosStringParametro != null) {
            conceptosParametro = Arrays.asList(conceptosStringParametro.split(","));
        }

        for (CifrasNomina v : this.poliza) {
//          
            String llave = "";
            if (!conceptosParametro.isEmpty()) {
                for (String s : conceptosParametro) {
                    if (v.getNumeroconcepto().equals(s)) {
                        llave = v.getIdconcepto().toString();
                    }
                }
            } else {
                llave = v.getIdconcepto().toString();
            }
//                
            if (llave.equals("")) {
                continue;
            }
            this.totalAbono += v.getAbono();
            this.totalCargo += v.getCargo();
//                
            if (mapa.get(llave) == null) {
                PolizaTO p = new PolizaTO(v);
                mapa.put(llave, p);
            } else {
                mapa.get(llave).add(v);
            }
//          
            if (v.getAuxcuentacontable() != null
                    && !v.getAuxcuentacontable().equals("")) {
//                
                llave = v.getIdconcepto() + " | " + v.getAuxcuentacontable();
//                
                if (mapaacum.get(llave) == null) {
                    CifrasNomina vista = this.getVistaContable(v.getIdconcepto(), v.getNumeroconcepto(), v.getNombreconcepto(), v.getAuxcuentacontable());
                    mapaacum.put(llave, vista);
                }

                if (v.getCargo() != 0) {
                    double importeabono = mapaacum.get(llave).getAbono() + v.getCargo();
                    mapaacum.get(llave).setAbono(importeabono);
                    this.totalAbono += CeniccoUtil.redondear(v.getCargo());
                } else {
                    double importecargo = mapaacum.get(llave).getCargo() + v.getAbono();
                    mapaacum.get(llave).setCargo(importecargo);
                    this.totalCargo += CeniccoUtil.redondear(v.getAbono());
                }
            }

            if (!StringUtils.isNullOrEmpty(v.getAsociarcuentacontable())) {
                llave = v.getIdconcepto() + " | " + v.getAsociarcuentacontable();
                if (mapaacum.get(llave) == null) {
                    CifrasNomina vista = this.getVistaContable(v.getIdconcepto(), v.getNumeroconcepto(), v.getNombreconcepto(), v.getAsociarcuentacontable());
                    mapaacum.put(llave, vista);
                }

                if (v.getCargo() != 0) {
                    double importecargo = mapaacum.get(llave).getCargo() + v.getCargo();
                    mapaacum.get(llave).setCargo(importecargo);
                    this.totalCargo += CeniccoUtil.redondear(v.getCargo());
                } else {
                    double importeabono = mapaacum.get(llave).getAbono() + v.getAbono();
                    mapaacum.get(llave).setAbono(importeabono);
                    this.totalAbono += CeniccoUtil.redondear(v.getAbono());
                }
            }
        }

        this.totalCargo = CeniccoUtil.redondear(this.totalCargo);
        this.totalAbono = CeniccoUtil.redondear(this.totalAbono);
        this.totalNeto = CeniccoUtil.redondear(this.totalNeto);
//      
        Iterator<CifrasNomina> iter = mapaacum.values().iterator();
        while (iter.hasNext()) {
            CifrasNomina v = iter.next();
            mapa.get(v.getIdconcepto().toString()).getDetalle().add(v);
//            
            mapa.get(v.getIdconcepto().toString()).addabono(v.getAbono());
            mapa.get(v.getIdconcepto().toString()).addcargo(v.getCargo());
        }
//        
        return new ArrayList<>(mapa.values());
    }

    private CifrasNomina getVistaContable(Integer idconcepto, String numeroconcepto, String nombreconcepto, String cuentacontable) {
        CifrasNomina v = new CifrasNomina();
        v.setIdconcepto(idconcepto);
        v.setNumeroconcepto(numeroconcepto);
        v.setNombreconcepto(nombreconcepto);
        v.setCuentacontable(cuentacontable);
        v.setCargo(0.0);
        v.setAbono(0.0);
        return v;
    }

    private boolean consultar() {
//
        List<String> relacionesAux = new ArrayList<>();
        for (String llave : this.selectedRelaciones) {
            relacionesAux.add(this.mapaRelacionesLaborales.get(llave));
        }
//      
        //Por cada grupo de pago seleccionado se hace una consulta por cada tipo de nómina seleccionada
        this.poliza = new ArrayList<>();
        for (Integer gp : this.selectedGruposPago) {
            for (Integer tp : this.selectedTiposNomina) {
                List<CifrasNomina> aux = ControladorWS.getInstance().getPolizaContable(gp, this.periodo,
                        this.anio, tp, this.selectedestatus, relacionesAux);
                this.poliza.addAll(aux);
            }
        }
        return !this.poliza.isEmpty();
    }

    @Override
    public void limpiar(ActionEvent event) {
        this.selectedGruposPago = null;
        this.periodo = null;
        this.anio = null;
//        
        this.header = new ArrayList<>();
        this.filteredHeader = new ArrayList<>();
//      
        this.tiposPoliza = ControladorWS.getInstance().getParametroslikeKey("SEPARAR_PROVISION_");
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
    public void delete(CifrasNomina obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void listenerSelectedHeader() {
        this.headerConceptos = false;
        listenerSelected();
    }

    public void listenerSelectedHeaderConceptos() {
        this.headerConceptos = true;
        listenerSelected();
    }

    @Override
    public void listenerSelected() {
//      
        if (!this.header.isEmpty()) {
//            
            String grupospago = "";
            GrupoPago gp;
            if (this.selectedGruposPago != null) {
                for (Integer grupopago : selectedGruposPago) {
                    gp = ControladorWS.getInstance().findGrupoPagoById(grupopago);
                    grupospago = grupospago + "_" + gp.getGrupopago();
                }
            }
//            
            String servicio = ControladorSesiones.getInstance().getUsuarioSession().getServicio();
//            
            List<String> lineas;
            String nombrearchivo = "PolizaContable_" + this.selectedTipoPoliza + "_" + grupospago + this.periodo + this.anio;

            switch (servicio) {
                /*case Parametros.SERVICIO_MELMEX:
                 lineas = this.util.crearLayoutMelmex(this.header);
                 Util.escribirFichero(lineas, nombrearchivo, ParametrosReportes.ARCHIVO_CSV);
                 break;
                 case Parametros.SERVICIO_MEAX:
                 TipoProceso tp = ControladorWS.getInstance().findTipoProcesoById(this.selectedTipoNomina);
                 //                    
                 this.util.crearLayoutMeax(this.header, nombrearchivo, tp, this.periodo.intValue(),
                 this.anio.intValue(), this.totalCargo.doubleValue(), this.totalAbono.doubleValue());
                 break;
                 * */
                case Parametros.SERVICIO_FILTERS:
                    lineas = this.util.crearLayoutFilters(this.header, this.periodo.intValue(), this.anio.intValue());
                    Util.escribirFichero(lineas, nombrearchivo, ParametrosReportes.ARCHIVO_CSV);
                    break;
                case Parametros.SERVICIO_ARTSANA:
                    lineas = this.util.crearLayoutArtsana(this.header, this.periodo.intValue(), this.anio.intValue(), this.selectedGruposPago[0], this.selectedTiposNomina[0]);
                    Util.escribirFichero(lineas, nombrearchivo, ParametrosReportes.ARCHIVO_TXT);
                    break;
                case Parametros.SERVICIO_ALPHA:
                    this.util.crearLayoutAlpha(this.header, nombrearchivo, this.periodo.intValue(), this.anio.intValue(),
                            this.totalCargo.doubleValue(), this.totalAbono.doubleValue());
                    break;
                case Parametros.SERVICIO_INTERNAS:
                    this.util.crearLayoutKUA(this.header, nombrearchivo, this.periodo.intValue(), this.anio.intValue(),
                            this.totalCargo.doubleValue(), this.totalAbono.doubleValue());
                    break;
                case Parametros.SERVICIO_WHITEHAT:
                    this.util.crearLayoutByjus(this.header, nombrearchivo, this.periodo.intValue(), this.anio.intValue(),
                            this.totalCargo.doubleValue(), this.totalAbono.doubleValue());
                    break;
                case Parametros.SERVICIO_AEMSA:
                case Parametros.SERVICIO_MARTILLO:
                    this.util.crearLayoutMartilloAemsa(this.header, nombrearchivo, this.periodo.intValue(), this.anio.intValue(),
                            this.totalCargo.doubleValue(), this.totalAbono.doubleValue());
                    break;
                case Parametros.SERVICIO_YAMANA:
                    this.util.crearLayoutMartilloAemsa(this.header, nombrearchivo, this.periodo.intValue(), this.anio.intValue(),
                            this.totalCargo.doubleValue(), this.totalAbono.doubleValue());
                    break;
                case Parametros.SERVICIO_GRUPO_ORDAS:
                    nombrearchivo = "REGISTRO NOMINA PERIODO " + periodo + "/" + anio;
                    this.util.crearLayoutPolizaGrupoOrdas(this.header, nombrearchivo, this.periodo.intValue(), this.anio.intValue(), this.totalCargo.doubleValue(), this.totalAbono.doubleValue(), this.consecutivo);
                    //this.util.crearLayoutGrupoOrdas(this.header, nombrearchivo, this.periodo.intValue(), this.anio.intValue(), this.totalCargo.doubleValue(), this.totalAbono.doubleValue(), this.consecutivo);
                    break;
                case Parametros.SERVICIO_NEWCENI:
                case Parametros.SERVICIO_MINNT:
                    //this.util.crearLayoutMartilloAemsa(this.header, nombrearchivo, this.periodo.intValue(), this.anio.intValue(), this.totalCargo.doubleValue(), this.totalAbono.doubleValue());
                    List<CifrasNomina> cifrasNominaAux = new ArrayList<>();
                    for (Integer sgp : this.selectedGruposPago) {
                        for (Integer tp : this.selectedTiposNomina) {
                            List<CifrasNomina> aux = ControladorWS.getInstance().getCifrasNominaPeriodo(sgp, this.periodo, this.anio, tp);
                            for (String s : selectedRelaciones) {
                                String rellab = this.mapaRelacionesLaborales.get(s);
                                for (CifrasNomina cn : aux) {
                                    if (cn.getIdrellab() == Integer.parseInt(rellab)) {
                                        cifrasNominaAux.add(cn);
                                    }
                                }
                            }
                        }
                    }
                    this.util.crearLayoutMinntCentrosCostos(cifrasNominaAux, nombrearchivo, this.periodo.toString(), this.anio.intValue(), this.totalCargo.doubleValue(), this.totalAbono.doubleValue(), this.headerConceptos, this.selectedTipoPoliza);
                    break;
                case Parametros.SERVICIO_KONECTA:
                    List<CifrasNomina> nominaAux = new ArrayList<>();
                    for (Integer sgp : this.selectedGruposPago) {
                        for (Integer tp : this.selectedTiposNomina) {
                            List<CifrasNomina> aux = ControladorWS.getInstance().getCifrasNominaPeriodo(sgp, this.periodo, this.anio, tp);
                            for (String s : selectedRelaciones) {
                                String rellab = this.mapaRelacionesLaborales.get(s);
                                for (CifrasNomina cn : aux) {
                                    if (cn.getIdrellab() == Integer.parseInt(rellab)) {
                                        nominaAux.add(cn);
                                    }
                                }
                            }
                        }
                    }
                    this.util.crearLayoutConekta(nominaAux, nombrearchivo, true, this.periodo, this.anio, this.totalCargo, this.totalAbono, this.selectedTiposNomina[0]);
                    break;
                case Parametros.SERVICIO_REQUORDIT:
                    List<CifrasNomina> cifrasNominaAuxReq = new ArrayList<>();
                    for (Integer sgp : this.selectedGruposPago) {
                        for (Integer tp : this.selectedTiposNomina) {
                            List<CifrasNomina> aux = ControladorWS.getInstance().getCifrasNominaPeriodo(sgp, this.periodo, this.anio, tp);
                            for (String s : selectedRelaciones) {
                                String rellab = this.mapaRelacionesLaborales.get(s);
                                for (CifrasNomina cn : aux) {
                                    if (cn.getIdrellab() == Integer.parseInt(rellab)) {
                                        cifrasNominaAuxReq.add(cn);
                                    }
                                }
                            }
                        }
                    }
                    this.util.crearLayoutRequorditDepartamentos(cifrasNominaAuxReq, nombrearchivo, this.periodo, this.anio.intValue(), this.totalCargo.doubleValue(), this.totalAbono.doubleValue(), true, this.selectedTiposNomina[0]);
                    break;
                case Parametros.SERVICIO_MELONN:
                    List<CifrasNomina> cifrasNominaAuxMel = new ArrayList<>();
                    for (Integer sgp : this.selectedGruposPago) {
                        for (Integer tp : this.selectedTiposNomina) {
                            List<CifrasNomina> aux = ControladorWS.getInstance().getCifrasNominaPeriodo(sgp, this.periodo, this.anio, tp);
                            for (String s : selectedRelaciones) {
                                String rellab = this.mapaRelacionesLaborales.get(s);
                                for (CifrasNomina cn : aux) {
                                    if (cn.getIdrellab() == Integer.parseInt(rellab)) {
                                        cifrasNominaAuxMel.add(cn);
                                    }
                                }
                            }
                        }
                    }
                    this.util.crearLayoutMelonnCuentaContableCentrosCostos(cifrasNominaAuxMel, nombrearchivo, this.periodo, this.anio.intValue(), this.totalCargo.doubleValue(), this.totalAbono.doubleValue(), this.selectedTiposNomina[0]);
                    break;
                default:
                    this.util.crearLayoutYamana(this.header, nombrearchivo, this.periodo.intValue(), this.anio.intValue(),
                            this.totalCargo.doubleValue(), this.totalAbono.doubleValue());
                    break;
            }
//            
        }

    }

    public void descargarReporte() {
        if (!this.header.isEmpty()) {
            String grupospago = "";
            GrupoPago gp;
            Compania compania = new Compania();
            if (this.selectedGruposPago != null) {
                for (Integer grupopago : selectedGruposPago) {
                    gp = ControladorWS.getInstance().findGrupoPagoById(grupopago);
                    grupospago = grupospago + "_" + gp.getGrupopago();
                    compania = gp.getIdcompania();
                }
            }
//            
            String nombrearchivo = "PolizaContable_" + grupospago + "_" + this.periodo + this.anio;
            List<CifrasNomina> vista = new ArrayList<>();
            for (PolizaTO p : this.header) {
                vista.addAll(p.getDetalle());
            }
//           
            System.out.println("Descarga Reporte...");
            RequestContext context = RequestContext.getCurrentInstance();
//        
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_POLIZA);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, nombrearchivo);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, vista);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.PARAMETRO_TOTAL_CARGO, this.totalCargo);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.PARAMETRO_TOTAL_ABONO, this.totalAbono);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.PARAMETRO_COMPANIA, compania.getNombre());
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.PARAMETRO_LOGO, compania.getLogo());
//
            context.addCallbackParam("ruta", MyPaths.urlServletReporte());
        }
    }

    public void descargarReporteXls() {
        DecimalFormat nformat = new DecimalFormat("0.00");
//        
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("PolizaContable");
//        
        HSSFRow row = sheet.createRow(0);
//        
        HSSFCell cell = row.createCell(0);
        cell.setCellValue("CONCEPTO");
        cell = row.createCell(1);
        cell.setCellValue("CARGO");
        cell = row.createCell(2);
        cell.setCellValue("ABONO");
//        
        int i = 1;
//        
        Iterator<PolizaTO> iter = this.header.iterator();
        while (iter.hasNext()) {
            int j = 0;
//            
            PolizaTO p = iter.next();
//            
            row = sheet.createRow(i);

            cell = row.createCell(j);
            cell.setCellValue(p.getConcepto());
            j++;
//            
            cell = row.createCell(j);
            cell.setCellValue(nformat.format(p.getCargo()));
            j++;
//            
            cell = row.createCell(j);
            cell.setCellValue(nformat.format(p.getAbono()));
            j++;
//            
            i++;
        }
//        
        String nombreArchivo = "PolizaContable_" + this.periodo + "_" + this.anio;
        this.util.escribirArchivo(workbook, nombreArchivo, ParametrosReportes.ARCHIVO_XLS);
    }
//

    @Override
    public void generarMsg(FacesMessage msg, boolean isValidate) {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().addMessage(null, msg);
        context.addCallbackParam("isValidate", isValidate);
    }

    @Override
    public String getInformacion() {
        return CeniccoUtil.getInformacion(this.header.size());
    }

    public Integer getPeriodo() {
        return periodo;
    }

    public void setPeriodo(Integer periodo) {
        this.periodo = periodo;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public List<PolizaTO> getFilteredHeader() {
        return filteredHeader;
    }

    public void setFilteredHeader(List<PolizaTO> filteredHeader) {
        this.filteredHeader = filteredHeader;
    }

    public List<PolizaTO> getHeader() {
        return header;
    }

    public Double getTotalAbono() {
        return this.totalAbono != null ? this.totalAbono : 0.0;
    }

    public Double getTotalCargo() {
        return this.totalCargo != null ? this.totalCargo : 0.0;
    }

    public List<String> getRelaciones() {
        return relaciones;
    }

    public Integer getSelectedestatus() {
        return selectedestatus;
    }

    public void setSelectedestatus(Integer selectedestatus) {
        this.selectedestatus = selectedestatus;
    }

    public String[] getSelectedRelaciones() {
        return selectedRelaciones;
    }

    public void setSelectedRelaciones(String[] selectedRelaciones) {
        this.selectedRelaciones = selectedRelaciones;
    }

    public Integer[] getSelectedGruposPago() {
        return selectedGruposPago;
    }

    public void setSelectedGruposPago(Integer[] selectedGruposPago) {
        this.selectedGruposPago = selectedGruposPago;
    }

    public Integer[] getSelectedTiposNomina() {
        return selectedTiposNomina;
    }

    public void setSelectedTiposNomina(Integer[] selectedTiposNomina) {
        this.selectedTiposNomina = selectedTiposNomina;
    }

    public Double getTotalNeto() {
        return totalNeto;
    }

    public void setTotalNeto(Double totalNeto) {
        this.totalNeto = totalNeto;
    }

    public String getConsecutivo() {
        return consecutivo;
    }

    public void setConsecutivo(String consecutivo) {
        this.consecutivo = consecutivo;
    }

    public boolean isHeaderConceptos() {
        return headerConceptos;
    }

    public void setHeaderConceptos(boolean headerConceptos) {
        this.headerConceptos = headerConceptos;
    }

    public String getSelectedTipoPoliza() {
        return selectedTipoPoliza;
    }

    public void setSelectedTipoPoliza(String selectedTipoPoliza) {
        this.selectedTipoPoliza = selectedTipoPoliza;
    }

    public List<Parametro> getTiposPoliza() {
        return tiposPoliza;
    }

    public void setTiposPoliza(List<Parametro> tiposPoliza) {
        this.tiposPoliza = tiposPoliza;
    }

    public void sincronizarNetSuite(ActionEvent event) {
        System.out.println("Inicio Integracion NetSuite");
        Periodo per = new Periodo();
        TipoProceso tp = ControladorWS.getInstance().findTipoProcesoById(this.selectedTiposNomina[0]);
        per.setAnio(anio);
        per.setIdtipoproceso(tp);
        per.setPeriodo(periodo);
        Periodo periodo = ControladorWS.getInstance().findPeriodoByTipoProcesoAndPeriodoAndAnio(per);
        System.out.println("Integracion NetSuite Periodo | " + periodo.getIdperiodo());
        boolean isValidate = ControladorWS.getInstance().doIntegracionConektaPolizaContable(periodo);

        IntegracionPolizaContable ipc = ControladorWS.getInstance().findIntegracionPolizaContableByIdPeriodo(periodo.getIdperiodo());
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Exito en la Operación!", "La solicitud fué exitosa! ID Integracion " + ipc.getIdIntegracion());
        if (!isValidate) {
            String respuesta = ipc.getRespuestaIntegracion();
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error en la Operación!", "Intentelo más tarde! " + respuesta.substring(respuesta.indexOf("detail") + 8, respuesta.indexOf("o:errorCode") - 2));
        }
        this.generarMsg(msg, isValidate);
    }
}
