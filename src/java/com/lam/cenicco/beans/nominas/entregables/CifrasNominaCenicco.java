/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lam.cenicco.beans.nominas.entregables;

import com.lam.cenicco.dao.ProcesoDAO;
import com.lam.cenicco.impl.ControladorWS;
import com.lam.cenicco.librerias.mensajes.Mensajes;
import com.lam.cenicco.librerias.utilidades.CeniccoUtil;
import com.lam.cenicco.util.ParametrosReportes;
import com.lam.cenicco.util.Util;
import com.lam.cenicco.util.paths.MyPaths;
import com.lam.cenicco.ws.CifrasNomina;
import com.lam.cenicco.ws.VistaConceptosNomina;
import com.mysql.jdbc.StringUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlGraphicImage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.swing.CellEditor;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.export.CSVExporter;
import org.primefaces.component.export.Exporter;
import org.primefaces.component.export.PDFExporter;
import org.primefaces.context.RequestContext;

/**
 *
 * @author JoséAntonio
 */
public class CifrasNominaCenicco implements ProcesoDAO<VistaConceptosNomina> {

    protected List<CifrasNomina> cifrasnomina;
//    
    protected List<CifrasNomina> filteredCifrasnomina;
//    
    protected String[] selectedRelaciones;
//
    protected List<String> relaciones;
//    
    protected Integer[] selectedGruposPago;
//    
    protected Integer selectedTipoproceso;
//    
    protected Integer periodo;
//    
    protected Integer anio;
//    
    protected Integer selectedestatus;
//    
    protected Double totalPercepciones;
//    
    protected Double totalDeducciones;
//    
    protected Double totalProvisiones;
//
    protected Map<String, String> mapaRelacionesLaborales;
//    

    public CifrasNominaCenicco() {
        if (this.cifrasnomina == null) {
            this.cifrasnomina = new ArrayList<>();
        }
    }

    @Override
    public void init() {
    }

    @Override
    public void consultar(ActionEvent event) {
//        
        boolean isValidate = this.validate();
//        
        if (isValidate) {
//            
            List<String> relacionesAux = new ArrayList<>();
            for (String llave : this.selectedRelaciones) {
                relacionesAux.add(this.mapaRelacionesLaborales.get(llave));
            }
//            
            this.totalDeducciones = 0.0;
            this.totalPercepciones = 0.0;
            this.totalProvisiones = 0.0;
//        
            for (Integer gp : this.selectedGruposPago) {
                this.cifrasnomina.addAll(ControladorWS.getInstance().getCifrasNomina(gp, this.selectedTipoproceso, this.periodo, this.anio, this.selectedestatus, relacionesAux));
            }
            this.filteredCifrasnomina = this.cifrasnomina;
//        
            for (CifrasNomina c : this.cifrasnomina) {
//                
                if (c.getSuma() != 1) {
                    continue;
                }
//                
                this.totalPercepciones += c.getPercepciones();
                this.totalDeducciones += c.getDeducciones();
                this.totalProvisiones += c.getProvisiones();
//            
            }
        }
//        
    }

    private boolean validate() {
        boolean isValidate = true;
//        
        if (this.selectedGruposPago.length == 0) {
            FacesMessage msg = Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, Mensajes.CAMPO_VACIO_GRUPO_PAGO);
            FacesContext.getCurrentInstance().addMessage(null, msg);
            isValidate = false;
        }
        if (this.periodo == null) {
            FacesMessage msg = Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, Mensajes.CAMPO_VACIO_PERIODO);
            FacesContext.getCurrentInstance().addMessage(null, msg);
            isValidate = false;
        }
        if (this.anio == null) {
            FacesMessage msg = Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, Mensajes.CAMPO_VACIO_ANIO);
            FacesContext.getCurrentInstance().addMessage(null, msg);
            isValidate = false;
        }
        return isValidate;
    }

    @Override
    public void limpiar(ActionEvent event) {
        this.cifrasnomina = new ArrayList<>();
        this.filteredCifrasnomina = new ArrayList<>();
//        
        this.selectedGruposPago = new Integer[]{};
        this.periodo = null;
        this.anio = null;
//        
        this.totalDeducciones = 0.0;
        this.totalPercepciones = 0.0;
        this.totalProvisiones = 0.0;
    }

    public void updateEmpleados(ActionEvent event) {
        this.selectedRelaciones = null;
//        
        this.mapaRelacionesLaborales = new HashMap<>();
//        
        List<CifrasNomina> relacionesAux = new ArrayList<>();
        for (Integer gp : this.selectedGruposPago) {
            relacionesAux.addAll(ControladorWS.getInstance().getRelacionesLaboralesXNomina(gp, this.selectedTipoproceso, this.selectedestatus, this.periodo, this.anio));
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
    public void delete(VistaConceptosNomina obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void listenerSelected() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void generarMsg(FacesMessage msg, boolean isValidate) {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().addMessage(null, msg);
        context.addCallbackParam("isValidate", isValidate);
    }

    public void descargarReporte() {
        if (!this.cifrasnomina.isEmpty()) {
            System.out.println("Descarga Reporte...");
            RequestContext context = RequestContext.getCurrentInstance();
//        
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.MODULO, ParametrosReportes.MODULO_CIFRAS_NOMINAS_ENTREGABLES);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, ParametrosReportes.MODULO_CIFRAS_NOMINAS);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, this.filteredCifrasnomina);
//
            context.addCallbackParam("ruta", MyPaths.urlServletReporte());
        }
    }

    @Override
    public String getInformacion() {
        return CeniccoUtil.getInformacion(this.cifrasnomina.size());
    }

    public List<CifrasNomina> getCifrasnomina() {
        return cifrasnomina;
    }

    public List<CifrasNomina> getFilteredCifrasnomina() {
        return filteredCifrasnomina;
    }

    public void setFilteredCifrasnomina(List<CifrasNomina> filteredCifrasnomina) {
        this.filteredCifrasnomina = filteredCifrasnomina;
    }

    public Integer[] getSelectedGruposPago() {
        return selectedGruposPago;
    }

    public void setSelectedGruposPago(Integer[] selectedGruposPago) {
        this.selectedGruposPago = selectedGruposPago;
    }

    public Map<String, String> getMapaRelacionesLaborales() {
        return mapaRelacionesLaborales;
    }

    public void setMapaRelacionesLaborales(Map<String, String> mapaRelacionesLaborales) {
        this.mapaRelacionesLaborales = mapaRelacionesLaborales;
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

    public Double getTotalPercepciones() {
        return totalPercepciones;
    }

    public Double getTotalDeducciones() {
        return totalDeducciones;
    }

    public Double getTotalProvisiones() {
        return totalProvisiones;
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

    public String[] getSelectedRelaciones() {
        return selectedRelaciones;
    }

    public void setSelectedRelaciones(String[] selectedRelaciones) {
        this.selectedRelaciones = selectedRelaciones;
    }

    public List<String> getRelaciones() {
        return relaciones;
    }

    public class ExtendedCSVExporter extends CSVExporter {

        @Override
        protected String exportValue(FacesContext context, UIComponent component) {
            if (component instanceof CellEditor) {
                return exportValue(context, ((org.primefaces.component.celleditor.CellEditor) component).getFacet("output"));
            } else if (component instanceof HtmlGraphicImage) {
                return (String) component.getAttributes().get("alt");
            } else {
                return super.exportValue(context, component);
            }
        }
    }

    public void descargarReporteCsv() {
        List<String> lineas = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        sb.append("No. Concepto,").append("Concepto,").append("No. Trabajadores,").append("Tiempo,").append("Percepción,").append("Deducción,").append("Provisión,").append("Totaliza,").append("Atributo,").append("Recibo");
        lineas.add(sb.toString());

        for (CifrasNomina c : this.filteredCifrasnomina) {
            sb = new StringBuilder();
            sb.append(c.getNumeroconcepto()).append(",")
                    .append(c.getNombreconcepto()).append(",")
                    .append(c.getContador()).append(",")
                    .append(c.getTiempo()).append(",")
                    .append(c.getPercepciones()).append(",")
                    .append(c.getDeducciones()).append(",")
                    .append(c.getProvisiones()).append(",")
                    .append(c.getSuma() == 1 ? "Si" : "No").append(",")
                    .append(StringUtils.isNullOrEmpty(c.getAtributoconcepto()) ? "Sin Asignar" : c.getAtributoconcepto()).append(",")
                    .append(c.getRecibonominaconcepto() == 1 ? "Si" : "No");
            lineas.add(sb.toString());
        }
        Util.escribirFichero(lineas, "CifrasNominas", ParametrosReportes.ARCHIVO_CSV);
    }
}
