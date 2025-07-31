/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lam.cenicco.beans.nominas.polizacontable.mes;

import com.lam.cenicco.dao.ProcesoDAO;
import com.lam.cenicco.impl.ControladorWS;
import com.lam.cenicco.librerias.utilidades.CeniccoUtil;
import com.lam.cenicco.util.ControladorSesiones;
import com.lam.cenicco.util.Parametros;
import com.lam.cenicco.util.ParametrosReportes;
import com.lam.cenicco.util.Util;
import com.lam.cenicco.util.paths.MyPaths;
import com.lam.cenicco.util.poliza.PolizaTO;
import com.lam.cenicco.util.poliza.PolizaUtil;
import com.lam.cenicco.ws.CifrasNomina;
import com.lam.cenicco.ws.Compania;
import com.lam.cenicco.ws.GrupoPago;
import com.lam.cenicco.ws.Parametro;
import java.text.DecimalFormat;
import java.util.ArrayList;
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
 * @author Antonio Durán
 */
public class PolizaContableMesCenicco implements ProcesoDAO<CifrasNomina> {

    protected String[] selectedRelaciones;
//    
    protected List<PolizaTO> header;
//    
    protected List<PolizaTO> filteredHeader;
//    
    protected List<CifrasNomina> poliza;
//  
    protected Integer[] selectedTiposNomina;
//    
    protected Integer[] selectedGruposPago;
//
    protected List<Parametro> tiposPoliza;
//
    protected Integer mesinicio;
//    
    protected Integer mesfin;
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
    protected String selectedTipoPoliza;
//    
    protected Integer selectedestatus;
//    
    protected Map<String, CifrasNomina> mapaRelacionesLaborales;
//    
    private PolizaUtil util;

    public PolizaContableMesCenicco() {
        if (this.util == null) {
            this.util = new PolizaUtil();
        }
    }
    private boolean headerConceptos;

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

    @Override
    public void limpiar(ActionEvent event) {
        this.selectedGruposPago = null;
        this.mesinicio = null;
        this.mesfin = null;
        this.anio = null;
//        
        this.header = new ArrayList<>();
        this.filteredHeader = new ArrayList<>();

        this.tiposPoliza = ControladorWS.getInstance().getParametroslikeKey("SEPARAR_PROVISION_");
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
        this.totalNeto = 0.0;
//        
        //Para cada grupo de pago seleccionado trae los periodos de todo el año ingresado
/*        
         for (Integer gp : selectedGruposPago) {
         List<Periodo> periodosAux = ControladorWS.getInstance().findCalendarioProceso(this.anio, this.selectedTipoNomina);
         List<Integer> periodos = new ArrayList<>();
         for (Periodo p : periodosAux) {
         //Para cada periodo traido de la base se valida que el mes coincida dentro de los valores de MesInicio y MesFin ingresados
         if (p.getMes() >= this.mesinicio && p.getMes() <= this.mesfin) {
         periodos.add(p.getPeriodo());
         }
         }
         for (Integer i : periodos) {
         //Se hace un barrido de las nóminas que coincidan con el grupo de pago y el periodo de la lista periodos y se suman los totales a la variable totalNeto
         this.totalNeto += ControladorWS.getInstance().getNetoPagarPolizaContable(gp, i, this.anio);
         }
         }
         */
        for (CifrasNomina v : this.poliza) {
//            
            String llave = v.getIdconcepto().toString();
//            
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
        }
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

    private boolean consultar() {
//        
        this.poliza = new ArrayList<>();
//
        List<String> relacionesAux = new ArrayList<>();
//
        for (String llave : this.selectedRelaciones) {
            relacionesAux.add(this.mapaRelacionesLaborales.get(llave).getIdrellab().toString());
        }
//      
        for (Integer gp : this.selectedGruposPago) {
            for (Integer tp : this.selectedTiposNomina) {
                List<CifrasNomina> aux = ControladorWS.getInstance().getPolizaContableMes(gp, this.mesinicio, this.mesfin,
                        this.anio, tp, this.selectedestatus, relacionesAux);
                for (CifrasNomina n : aux) {
                    if (n != null) {
                        this.poliza.add(n);
                    }
                }
            }
        }
        return !this.poliza.isEmpty();
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

    public void updateEmpleados(ActionEvent event) {
        this.selectedRelaciones = null;
//        
        this.mapaRelacionesLaborales = new HashMap<>();
//
        List<CifrasNomina> relacionesAux = new ArrayList<>();
        for (Integer gp : selectedGruposPago) {
            for (Integer tp : this.selectedTiposNomina) {
                List<CifrasNomina> aux = ControladorWS.getInstance().getRelacionesLaboralesXNominaMes(gp, tp,
                        this.selectedestatus, this.mesinicio, this.mesfin, this.anio);
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
                    this.mapaRelacionesLaborales.put(llave, rl);
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
                        this.mapaRelacionesLaborales.put(llave, rl);
                        this.relaciones.add(llave);
                    }
                    break;
                case "1":
                    if (rl.getEstatus().equals("1")
                            && this.mapaRelacionesLaborales.get(llave) == null) {
                        System.out.println("Llave.... " + llave);
                        this.mapaRelacionesLaborales.put(llave, rl);
                        this.relaciones.add(llave);
                    }
                    break;
            }
        }
//        
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
            String nombrearchivo = "PolizaContable_Mensual" + grupospago + "_" + this.mesinicio + "_" + this.mesfin + "_" + this.anio;

            switch (servicio) {
                /*case Parametros.SERVICIO_MELMEX:
                 lineas = this.util.crearLayoutMelmex(this.header);
                 Util.escribirFichero(lineas, nombrearchivo, ParametrosReportes.ARCHIVO_CSV);
                 break;
                 
                 case Parametros.SERVICIO_MEAX:
                 TipoProceso tp = ControladorWS.getInstance().findTipoProcesoById(this.selectedTipoNomina);
                 //                    
                 this.util.crearLayoutMeax(this.header, nombrearchivo, tp, this.mesinicio.intValue(),
                 this.anio.intValue(), this.totalCargo.doubleValue(), this.totalAbono.doubleValue());
                 break;
                 */
                case Parametros.SERVICIO_FILTERS:
                    lineas = this.util.crearLayoutFilters(this.header, this.mesinicio.intValue(), this.anio.intValue());
                    Util.escribirFichero(lineas, nombrearchivo, ParametrosReportes.ARCHIVO_CSV);
                    break;
                case Parametros.SERVICIO_ALPHA:
                    this.util.crearLayoutAlpha(this.header, nombrearchivo, this.mesinicio.intValue(), this.anio.intValue(),
                            this.totalCargo.doubleValue(), this.totalAbono.doubleValue());
                    break;
                case Parametros.SERVICIO_WHITEHAT:
                    this.util.crearLayoutByjus(this.header, nombrearchivo, this.mesinicio.intValue(), this.anio.intValue(),
                            this.totalCargo.doubleValue(), this.totalAbono.doubleValue());
                    break;
                case Parametros.SERVICIO_ARTSANA:
                    lineas = this.util.crearLayoutArtsana(this.header, this.mesinicio.intValue(), this.anio.intValue(), this.selectedGruposPago[0], this.selectedTiposNomina[0]);
                    Util.escribirFichero(lineas, nombrearchivo, ParametrosReportes.ARCHIVO_TXT);
                    break;
                case Parametros.SERVICIO_MINNT:
                    //this.util.crearLayoutMartilloAemsa(this.header, nombrearchivo, this.periodo.intValue(), this.anio.intValue(), this.totalCargo.doubleValue(), this.totalAbono.doubleValue());
                    List<CifrasNomina> cifrasNominaAux = new ArrayList<>();
                    for (Integer sgp : this.selectedGruposPago) {
                        for (Integer tp : this.selectedTiposNomina) {
                            List<CifrasNomina> aux = ControladorWS.getInstance().getCifrasNominaMensual(sgp, this.mesinicio, this.mesfin, this.anio, tp);
                            cifrasNominaAux.addAll(aux);
                        }
                    }
                    this.util.crearLayoutMinntCentrosCostos(cifrasNominaAux, nombrearchivo, mesinicio + " - " + mesfin, this.anio.intValue(), this.totalCargo.doubleValue(), this.totalAbono.doubleValue(), this.headerConceptos, this.selectedTipoPoliza);
                    break;
                case Parametros.SERVICIO_KONECTA:
                    List<CifrasNomina> nominaAux = new ArrayList<>();
                    for (Integer sgp : this.selectedGruposPago) {
                        for (Integer tp : this.selectedTiposNomina) {
                            List<CifrasNomina> aux = ControladorWS.getInstance().getCifrasNominaMensual(sgp, this.mesinicio, this.mesfin, this.anio, tp);
                            nominaAux.addAll(aux);
                        }
                    }
                    this.util.crearLayoutConekta(nominaAux, nombrearchivo, false, this.mesinicio, this.anio, this.totalCargo, this.totalAbono, this.selectedTiposNomina[0]);
                    break;
                default:
                    this.util.crearLayoutYamana(this.header, nombrearchivo, this.mesinicio.intValue(), this.anio.intValue(),
                            this.totalCargo.doubleValue(), this.totalAbono.doubleValue());
                    break;
            }
//            
        }
    }

    public void descargarReporte() {
        if (!this.header.isEmpty()) {
            String grupospago = "";
            Compania compania = new Compania();
            GrupoPago gp;
            if (this.selectedGruposPago != null) {
                for (Integer grupopago : selectedGruposPago) {
                    gp = ControladorWS.getInstance().findGrupoPagoById(grupopago);
                    grupospago = grupospago + "_" + gp.getGrupopago();
                    compania = gp.getIdcompania();
                }
            }
//            
            String nombrearchivo = "PolizaContable" + grupospago + "_" + this.mesinicio + "_" + this.mesfin + "_" + this.anio;
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
        String nombreArchivo = "PolizaContable_" + this.mesinicio + "_" + this.mesfin + "_" + this.anio;
        this.util.escribirArchivo(workbook, nombreArchivo, ParametrosReportes.ARCHIVO_XLS);
    }

    @Override
    public void generarMsg(FacesMessage msg, boolean isValidate) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getInformacion() {
        return CeniccoUtil.getInformacion(this.header.size());
    }

    public String[] getSelectedRelaciones() {
        return selectedRelaciones;
    }

    public void setSelectedRelaciones(String[] selectedRelaciones) {
        this.selectedRelaciones = selectedRelaciones;
    }

    public List<PolizaTO> getFilteredHeader() {
        return filteredHeader;
    }

    public void setFilteredHeader(List<PolizaTO> filteredHeader) {
        this.filteredHeader = filteredHeader;
    }

    public Integer getMesinicio() {
        return mesinicio;
    }

    public void setMesinicio(Integer mesinicio) {
        this.mesinicio = mesinicio;
    }

    public Integer getMesfin() {
        return mesfin;
    }

    public void setMesfin(Integer mesfin) {
        this.mesfin = mesfin;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public Integer getSelectedestatus() {
        return selectedestatus;
    }

    public void setSelectedestatus(Integer selectedestatus) {
        this.selectedestatus = selectedestatus;
    }

    public List<PolizaTO> getHeader() {
        return header;
    }

    public List<CifrasNomina> getPoliza() {
        return poliza;
    }

    public Double getTotalAbono() {
        return totalAbono;
    }

    public Double getTotalCargo() {
        return totalCargo;
    }

    public Double getTotalNeto() {
        return totalNeto;
    }

    public List<String> getRelaciones() {
        return relaciones;
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

    public List<Parametro> getTiposPoliza() {
        return tiposPoliza;
    }

    public void setTiposPoliza(List<Parametro> tiposPoliza) {
        this.tiposPoliza = tiposPoliza;
    }

    public String getSelectedTipoPoliza() {
        return selectedTipoPoliza;
    }

    public void setSelectedTipoPoliza(String selectedTipoPoliza) {
        this.selectedTipoPoliza = selectedTipoPoliza;
    }
}
