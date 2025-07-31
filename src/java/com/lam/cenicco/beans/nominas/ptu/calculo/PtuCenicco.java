/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lam.cenicco.beans.nominas.ptu.calculo;

import com.lam.cenicco.dao.ProcesoDAO;
import com.lam.cenicco.impl.ControladorWS;
import com.lam.cenicco.librerias.utilidades.CeniccoUtil;
import com.lam.cenicco.util.ControladorSesiones;
import com.lam.cenicco.util.ParametrosReportes;
import com.lam.cenicco.util.Util;
import com.lam.cenicco.util.paths.MyPaths;
import com.lam.cenicco.ws.CifrasNominaAnual;
import com.lam.cenicco.ws.Compania;
import com.lam.cenicco.ws.Concepto;
import com.lam.cenicco.ws.Incidencia;
import com.lam.cenicco.ws.Parametro;
import com.lam.cenicco.ws.Periodo;
import com.lam.cenicco.ws.RelacionLaboral;
import com.lam.cenicco.ws.RepartoUtilidades;
import com.lam.cenicco.ws.TipoProceso;
import com.lam.cenicco.ws.Usuario;
import com.lam.cenicco.ws.VistaAnualPTU;
import com.mysql.jdbc.StringUtils;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.xml.datatype.XMLGregorianCalendar;
import org.primefaces.context.RequestContext;

/**
 *
 * @author Antonio Durán
 */
public class PtuCenicco implements ProcesoDAO<CifrasNominaAnual> {

    private List<CifrasNominaAnual> cifras;
//    
    private List<CifrasNominaAnual> filteredcifras;
//    
    private Integer anio;
//    
    private Integer diasminimos;
//    
    private Integer idcompania;
//    
    private Double importerepartir;
//    
    private Double topesueldos;
//    
    private Double importesd;
//
    private Double totalgeneraldias;
//
    private Double totalgeneralsueldos;
//
    private Integer selectedtipofecha;
//    
    private Double totalgeneral;
//

    public PtuCenicco() {
        if (this.cifras == null) {
            this.cifras = new ArrayList<>();

        }
        if (this.filteredcifras == null) {
            this.filteredcifras = new ArrayList<>();
        }
    }

    @Override
    public void consultar(ActionEvent event) {
//        
        this.totalgeneral = 0.0;
//        
        this.totalgeneraldias = 0.0;
        this.totalgeneralsueldos = 0.0;
//       
        Compania compania = ControladorWS.getInstance().getCompaniaXId(this.idcompania);
        boolean isServicioConekta = compania.getNombreCorto().equals("KONECTA");

        if (isServicioConekta) {
            this.topesueldos = this.anio % 4 == 0
                    ? this.importesd * 360
                    : this.importesd * 360;

        } else {
            this.topesueldos = this.anio % 4 == 0
                    ? this.importesd * 366
                    : this.importesd * 365;
        }
//        
        Integer diaseventual = ControladorWS.getInstance().getTiempoEventualPTU();
//        
        System.out.println("DiasTiempoPTUEventual.... " + diaseventual);
        this.cifras = ControladorWS.getInstance().getCifrasNominaPtu(this.idcompania, this.anio, this.selectedtipofecha);
//        
        Iterator<CifrasNominaAnual> iter = this.cifras.iterator();
        while (iter.hasNext()) {
            CifrasNominaAnual c = iter.next();
//            
            if (c.getTipocontrato().equals("2")
                    && c.getDiasnaturales() < diaseventual) {
                System.out.println("EmpleadoEventual: " + c.getDiasnaturales() + " | " + diaseventual);
                continue;
            }
//            
            if (c.getImporte() > this.topesueldos) {
                c.setSueldotopado(this.topesueldos);
            } else {
                c.setSueldotopado(c.getImporte());
            }
//            
            if (c.getTipocontrato().equals("99") || (c.getDiasnaturales() < this.diasminimos && !c.getTipocontrato().equals("1"))) {
                c.setSueldotopado(0.0);
                c.setFactorpordias(0.0);
                c.setMontopordias(0.0);
                c.setFactorporpesos(0.0);
                c.setMontoporpesos(0.0);
                c.setTotalAnio(0.0);
                c.setTotal(0.0);
            } else {
                c.setAplica(true);
//                
                this.totalgeneraldias += c.getDiasnaturales();
                this.totalgeneralsueldos += c.getSueldotopado();
            }

        }
//        
        iter = this.cifras.iterator();
        while (iter.hasNext()) {
            CifrasNominaAnual c = iter.next();
//            
            if (!c.isAplica()) {
                continue;
            }
//            
            double factor = c.getDiasnaturales() / this.totalgeneraldias;
            c.setFactorpordias(factor);
//            
            double monto = (this.importerepartir / 2) * factor;
            c.setMontopordias(monto);
//            
            factor = c.getSueldotopado() / this.totalgeneralsueldos;
            c.setFactorporpesos(factor);
//            
            monto = (this.importerepartir / 2) * factor;
            c.setMontoporpesos(monto);
//            
            double totalAnio = c.getMontopordias() + c.getMontoporpesos();
            c.setTotalAnio(totalAnio);

            double total = c.getMontopordias() + c.getMontoporpesos();
            if (total >= c.getTopePTU()) {
                total = c.getTopePTU();
            }
            c.setTotal(total);
//            
            this.totalgeneral += total;
        }

        this.filteredcifras = this.cifras;
//        
    }

    @Override
    public void limpiar(ActionEvent event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void create(ActionEvent event) {
        Parametro concepto = ControladorWS.getInstance().getParametro("CONCEPTO_PTU");
        if (concepto != null && !StringUtils.isNullOrEmpty(concepto.getValor())) {
            Integer anioPTUCalculado = this.anio + 1;
            XMLGregorianCalendar fechaRegistro = CeniccoUtil.getDateToXmlCalendar(new Date());
            Usuario usuario = ControladorSesiones.getInstance().getUsuarioSession();
            Concepto conceptoPTU = ControladorWS.getInstance().findConceptoByConcepto(concepto.getValor());
            TipoProceso tipoProcesoPTU = ControladorWS.getInstance().findTipoProcesoByTipo("NP");
            Periodo periodoPTU = null;
            List<Periodo> periodos = ControladorWS.getInstance().findCalendarioProceso(anioPTUCalculado, tipoProcesoPTU.getIdtipoproceso());
            for (Periodo p : periodos) {
                System.out.println("Periodo Encontrado " + p.getIdperiodo() + " | " + p.getEstatus());
                if (p.getEstatus() == 0) {
                    periodoPTU = p;
                    System.out.println("Periodo PTU " + p.getIdperiodo());
                    break;
                }
            }
            if (periodoPTU != null) {
                RepartoUtilidades utilidades = ControladorWS.getInstance().getRepartoUtilidadesByIdCompaniaAndAnio(this.idcompania, this.anio);
                if (utilidades == null) {
                    RepartoUtilidades ru = new RepartoUtilidades();
                    ru.setIdcompania(this.idcompania);
                    ru.setTotalrepartir(this.importerepartir);
                    ru.setTotaltopesalariodiario(this.importesd);
                    ru.setTotalminimodias(this.diasminimos);
                    ru.setIdtipofecha(this.selectedtipofecha);
                    ru.setAnio(this.anio);
                    ru.setTotaltopeanual(this.topesueldos);
                    ru.setTotalgeneraldias(this.totalgeneraldias);
                    ru.setTotalgeneralsueldos(this.totalgeneralsueldos);
                    ru.setTotalgeneral(this.totalgeneral);
                    ru.getCifras().addAll(this.filteredcifras);
                    ru.setEstatus(1);
                    boolean isValidate = ControladorWS.getInstance().saveRepartoUtilidades(ru);
                    if (isValidate) {
                        if (conceptoPTU != null) {
                            List<Incidencia> incidencias = new ArrayList<>();
                            for (CifrasNominaAnual c : this.filteredcifras) {
                                if (c.isAplica()) {
                                    RelacionLaboral rl = new RelacionLaboral();
                                    rl.setIdrellab(c.getIdrellab());

                                    Incidencia i = new Incidencia();
                                    i.setIdrellab(rl);
                                    i.setIdperiodo(periodoPTU);
                                    i.setIdconcepto(conceptoPTU);
                                    i.setEstatus("01");
                                    i.setIdtipoproceso(tipoProcesoPTU.getIdtipoproceso());
                                    i.setUnidades(null);
                                    i.setImporte(c.getTotal());
                                    i.setReferencia01("REPARTO DE UTILIDADES AUTORIZADOS POR " + usuario.getNombre() + " " + usuario.getApellidoPaterno());
                                    i.setFechaaux01(fechaRegistro);
                                    i.setFechaact(fechaRegistro);
                                    i.setUsuario(usuario);
                                    i.setAutorizar(BigDecimal.ONE.intValue());
                                    incidencias.add(i);
                                }
                            }
                            ControladorWS.getInstance().createIncidencias(incidencias, usuario);
                        }
                        generarMsg(Util.getFacesMessage(FacesMessage.SEVERITY_INFO, "Se guardaron exitosamente las utilidades e incidencias"), isValidate);
                    } else {
                        generarMsg(Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, "Error al guardar las utilidades e incidencias"), isValidate);
                    }
                } else {
                    generarMsg(Util.getFacesMessage(FacesMessage.SEVERITY_INFO, "Ya existe un guardado de las utilidades del año " + this.anio), true);
                }
            } else {
                generarMsg(Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, "No existe un periodo configurado para el PTU"), false);
            }
        } else {
            generarMsg(Util.getFacesMessage(FacesMessage.SEVERITY_ERROR, "No se encuentra configurado el parametro CONCEPTO_PTU"), false);
        }
    }

    @Override
    public void edit(ActionEvent event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void delete(CifrasNominaAnual obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void listenerSelected() {
        RepartoUtilidades u = ControladorWS.getInstance().getRepartoUtilidadesByIdCompaniaAndAnio(this.idcompania, this.anio);
        if (u != null) {
            this.idcompania = u.getIdcompania();
            this.importerepartir = u.getTotalrepartir();
            this.importesd = u.getTotaltopesalariodiario();
            this.diasminimos = u.getTotalminimodias();
            this.selectedtipofecha = u.getIdtipofecha();
            this.anio = u.getAnio();
            this.topesueldos = u.getTotaltopeanual();
            this.totalgeneraldias = u.getTotalgeneraldias();
            this.totalgeneralsueldos = u.getTotalgeneralsueldos();
            this.filteredcifras = u.getCifras();
            this.cifras = u.getCifras();
            this.totalgeneral = u.getTotalgeneral();
            this.getInformacion();

            RequestContext context = RequestContext.getCurrentInstance();
            context.update("form");
            generarMsg(Util.getFacesMessage(FacesMessage.SEVERITY_INFO, "Exito al consultar las utilidades del año " + this.anio), true);
        } else {
            generarMsg(Util.getFacesMessage(FacesMessage.SEVERITY_INFO, "No hay utilidades del año " + this.anio), true);
        }
    }

    @Override
    public void generarMsg(FacesMessage msg, boolean isValidate) {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().addMessage(null, msg);
        context.addCallbackParam("isValidate", isValidate);
    }

    @Override
    public String getInformacion() {
        return CeniccoUtil.getInformacion(this.cifras.size());
    }

    @Override
    public void init() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public Double getImporterepartir() {
        return importerepartir;
    }

    public void setImporterepartir(Double importerepartir) {
        this.importerepartir = importerepartir;
    }

    public Double getImportesd() {
        return importesd;
    }

    public void setImportesd(Double importesd) {
        this.importesd = importesd;
    }

    public Integer getDiasminimos() {
        return diasminimos;
    }

    public void setDiasminimos(Integer diasminimos) {
        this.diasminimos = diasminimos;
    }

    public Integer getSelectedtipofecha() {
        return selectedtipofecha;
    }

    public void setSelectedtipofecha(Integer selectedtipofecha) {
        this.selectedtipofecha = selectedtipofecha;
    }

    public List<CifrasNominaAnual> getFilteredcifras() {
        return filteredcifras;
    }

    public void setFilteredcifras(List<CifrasNominaAnual> filteredcifras) {
        this.filteredcifras = filteredcifras;
    }

    public List<CifrasNominaAnual> getCifras() {
        return cifras;
    }

    public Double getTotalgeneraldias() {
        return totalgeneraldias;
    }

    public Double getTotalgeneralsueldos() {
        return totalgeneralsueldos;
    }

    public Double getTotalgeneral() {
        return totalgeneral;
    }

    public Double getTopesueldos() {
        return topesueldos;
    }

    public Integer getIdcompania() {
        return idcompania;
    }

    public void setIdcompania(Integer idcompania) {
        this.idcompania = idcompania;
    }

    public void generarCsvPTU() {
        List<Integer> anios = new ArrayList<>();
        for (int i = 2; i >= 0; i--) {
            Integer a = (anio - i);
            anios.add(a);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedWriter bw = null;
        DecimalFormat df = new DecimalFormat("#.00");
        DecimalFormat df8 = new DecimalFormat("#.00000000");
        try {
            bw = new BufferedWriter(new OutputStreamWriter(baos, StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            sb.append("Aplica PTU?")
                    .append(",")
                    .append("No. Empleado")
                    .append(",")
                    .append("Nombre")
                    .append(",")
                    .append("Grupo Pago")
                    .append(",")
                    .append("F. Antiguedad")
                    .append(",")
                    .append("F. Ingreso")
                    .append(",")
                    .append("F. Baja")
                    .append(",")
                    .append("E. Empleado")
                    .append(",")
                    .append("F. Inicio PTU")
                    .append(",")
                    .append("F. Fin PTU")
                    .append(",")
                    .append("Tiempo Dias")
                    .append(",")
                    .append("Sueldo Anual PTU")
                    .append(",")
                    .append("Sueldo Anual Topado")
                    .append(",")
                    .append("F. Dias")
                    .append(",")
                    .append("M. Dias")
                    .append(",")
                    .append("F. Pesos")
                    .append(",")
                    .append("M. Pesos")
                    .append(",");

            for (Integer anio : anios) {
                sb.append("REPARTO UTILIDADES " + anio)
                        .append(",");
            }

            sb.append("ANIOS DE PTU")
                    .append(",")
                    .append("PROMEDIO 3 PTU")
                    .append(",")
                    .append("3 MESES SUELDO")
                    .append(",")
                    .append("TOPE PTU")
                    .append(",")
                    .append("PTU " + (this.anio + 1))
                    .append(",")
                    .append("TOTAL")
                    .append(",")
                    .append("Aplica TOPE?");
            bw.write(sb.toString());
            bw.newLine();
            for (CifrasNominaAnual cna : this.filteredcifras) {
                Map<Integer, Double> mapPTU = new HashMap<>();
                for (VistaAnualPTU ptu : cna.getPtus()) {
                    mapPTU.put(ptu.getAnio(), ptu.getValor());
                }

                sb = new StringBuilder();
                sb.append(cna.isAplica() ? "Si" : "No")
                        .append(",")
                        .append(cna.getNumeroempleado())
                        .append(",")
                        .append(cna.getNombreempleado())
                        .append(",")
                        .append(cna.getGrupopago())
                        .append(",")
                        .append(cna.getFechaantiguedadstr())
                        .append(",")
                        .append(cna.getFechaingresostr())
                        .append(",")
                        .append(cna.getFechabajastr())
                        .append(",")
                        .append(cna.getEstatus().equals("0") ? "Inactivo" : "Activo")
                        .append(",")
                        .append(cna.getFechainicioPtu())
                        .append(",")
                        .append(cna.getFechafinPtu())
                        .append(",")
                        .append(cna.getDiasnaturales())
                        .append(",")
                        .append(df.format(cna.getImporte()))
                        .append(",")
                        .append(df.format(cna.getSueldotopado()))
                        .append(",")
                        .append(cna.getFactorpordias())
                        .append(",")
                        .append(df.format(cna.getMontopordias()))
                        .append(",")
                        .append(df8.format(cna.getFactorporpesos()))
                        .append(",")
                        .append(df.format(cna.getMontoporpesos()))
                        .append(",");

                for (Integer anio : anios) {
                    Double importe = 0.0;
                    if (mapPTU.containsKey(anio)) {
                        importe = mapPTU.get(anio);
                    }
                    sb.append(df.format(importe))
                            .append(",");
                }
                //System.out.println("NU :: " + cna.getNumeroempleado() + " | " + cna.getTotalAnio());
                sb.append(cna.getPtus().size())
                        .append(",")
                        .append(df.format(cna.getPromedioPTU()))
                        .append(",")
                        .append(df.format(cna.getSueldo3Meses()))
                        .append(",")
                        .append(df.format(cna.getTopePTU()))
                        .append(",")
                        .append(df.format(cna.getTotalAnio()))
                        .append(",")
                        .append(cna.getTotalAnio() <= cna.getTopePTU() ? df.format(cna.getTotalAnio()) : df.format(cna.getTopePTU()))
                        .append(",")
                        .append(cna.getTotalAnio() <= cna.getTopePTU() ? "No" : "Si");
                bw.write(sb.toString());
                bw.newLine();
            }
            sb = new StringBuilder();
            sb.append(",,,,,,,,,,,,,,,,,,,,,,,,Total Gral:," + df.format(this.totalgeneral) + ",");

            bw.write(sb.toString());
            bw.newLine();

            bw.flush();
            System.out.println("Escribio Correctamente el Archivo....");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (Exception e) {
            }

        }
        //        
        String nombrearchivo = "PTU";
        RequestContext context = RequestContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.NOMBRE_ARCHIVO, nombrearchivo);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.TIPO_ARCHIVO, ParametrosReportes.ARCHIVO_CSV);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ParametrosReportes.REGISTROS, baos.toByteArray());
        context.addCallbackParam("ruta", MyPaths.urlServletDescargarArchivoCsv());
    }
}
