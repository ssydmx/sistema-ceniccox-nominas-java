/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lam.cenicco.beans.nominas.reportesnominas.detalleexentosgravados;

import com.lam.cenicco.dao.ProcesoDAO;
import com.lam.cenicco.impl.ControladorWS;
import com.lam.cenicco.librerias.utilidades.CeniccoUtil;
import com.lam.cenicco.util.ParametrosReportes;
import com.lam.cenicco.util.Util;
import com.lam.cenicco.ws.CifrasNomina;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.event.ActionEvent;

/**
 * @author erick
 */
public class DetalleExentosGravadosCenicco implements ProcesoDAO<CifrasNomina> {

    //Datos necesarios de consulta
    protected Integer mesinicio;
    protected Integer mesfin;
    protected Integer anio;
    protected Integer selectedPeriodoMes;
    protected Integer idgrupopago;
//    
    //Totales Globales
    protected double percepciones;
    protected double percepcionesGravadas;
    protected double percepcionesExentas;
    protected double deducciones;
    protected double deduccionesGravadas;
    protected double deduccionesExentas;
    protected double provisiones;
// 
    //Listas de datos
    protected List<CifrasNomina> cifrasnomina;
    protected List<CifrasNomina> filteredcifrasnomina;

    public DetalleExentosGravadosCenicco() {
        if (this.cifrasnomina == null) {
            this.cifrasnomina = new ArrayList<>();
        }
        if (this.filteredcifrasnomina == null) {
            this.filteredcifrasnomina = new ArrayList<>();
        }
    }

    @Override
    public void consultar(ActionEvent event) {
        this.percepciones = 0.0;
        this.percepcionesExentas = 0.0;
        this.percepcionesGravadas = 0.0;
        this.deducciones = 0.0;
        this.deduccionesExentas = 0.0;
        this.deduccionesGravadas = 0.0;
        this.provisiones = 0.0;

        this.cifrasnomina = ControladorWS.getInstance().findDetalleCifrasNominaByNaturaleza(this.mesinicio, this.mesfin, this.anio, this.idgrupopago, this.selectedPeriodoMes);
        this.filteredcifrasnomina = this.cifrasnomina;
        Iterator<CifrasNomina> iter = this.cifrasnomina.iterator();
        while (iter.hasNext()) {
            CifrasNomina c = iter.next();

            if (c.getDetallePercepciones() != null) {
                this.percepciones += c.getDetallePercepciones();
            }
            if (c.getDetallePercepcionesExentas() != null) {
                this.percepcionesExentas += c.getDetallePercepcionesExentas();
            }
            if (c.getDetallePercepcionesGravadas() != null) {
                this.percepcionesGravadas += c.getDetallePercepcionesGravadas();
            }
            if (c.getDetalleDeducciones() != null) {
                this.deducciones += c.getDetalleDeducciones();
            }
            if (c.getDetalleDeduccionesExentas() != null) {
                this.deduccionesExentas += c.getDetalleDeduccionesExentas();
            }
            if (c.getDetalleDeduccionesGravadas() != null) {
                this.deduccionesGravadas += c.getDetalleDeduccionesGravadas();
            }
            if (c.getDetalleprovisiones() != null) {
                this.provisiones += c.getDetalleprovisiones();
            }
//            

        }

    }

    public void descargarReporte() {
        List<String> lineas = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        sb.append("EMPLEADO,").append("CONCEPTO,").append("PERCEPCIONES,").append("PERCEP. EXCENTAS,").append("PERCEP. GRAVADAS,").
                append("DEDUCCIONES,").append("DEDUCC. EXCENTAS,").append("DEDUCC. GRAVADAS,").append("PROVISIONES");
        lineas.add(sb.toString());

        String empleado = this.cifrasnomina.get(0).getNumeroempleado();
        double per = 0.0;
        double perEx = 0.0;
        double perGra = 0.0;
        double dedu = 0.0;
        double deduEx = 0.0;
        double deduGra = 0.0;
        double prov = 0.0;
        boolean bandera = true;
        for (CifrasNomina c : this.cifrasnomina) {
            if (!c.getNumeroempleado().equals(empleado)) {
                sb = new StringBuilder();
                sb.append(",").append("Total:,").append(per).append(",")
                        .append(perEx).append(",")
                        .append(perGra).append(",")
                        .append(dedu).append(",")
                        .append(deduEx).append(",")
                        .append(deduGra).append(",")
                        .append(prov);

                lineas.add(sb.toString());
                lineas.add("");

                per = 0.0;
                perEx = 0.0;
                perGra = 0.0;
                dedu = 0.0;
                deduEx = 0.0;
                deduGra = 0.0;
                prov = 0.0;
            }

            sb = new StringBuilder();
            sb.append(c.getNumeroempleado()).append(" - ").append(c.getNombreempleado().replaceAll("Ñ", "N")).append(",");
            sb.append(c.getNumeroconcepto()).append(" - ").append(c.getNombreconcepto()).append(",");
            //Percepciones
            if (c.getDetallePercepciones() != null) {
                sb.append(c.getDetallePercepciones()).append(",");
                per += c.getDetallePercepciones();
            } else {
                sb.append(",");
            }
            //Percepciones Exentas
            if (c.getDetallePercepcionesExentas() != null) {
                sb.append(c.getDetallePercepcionesExentas()).append(",");
                perEx += c.getDetallePercepcionesExentas();
            } else {
                sb.append(",");
            }
            //Percepciones Gravadas
            if (c.getDetallePercepcionesGravadas() != null) {
                sb.append(c.getDetallePercepcionesGravadas()).append(",");
                perGra += c.getDetallePercepcionesGravadas();
            } else {
                sb.append(",");
            }
            //Deducciones
            if (c.getDetalleDeducciones() != null) {
                sb.append(c.getDetalleDeducciones()).append(",");
                dedu += c.getDetalleDeducciones();
            } else {
                sb.append(",");
            }
            //Deducciones Exentas
            if (c.getDetalleDeduccionesExentas() != null) {
                sb.append(c.getDetalleDeduccionesExentas()).append(",");
                deduEx += c.getDetalleDeduccionesExentas();
            } else {
                sb.append(",");
            }
            //Deducciones Gravadas
            if (c.getDetalleDeduccionesGravadas() != null) {
                deduGra += c.getDetalleDeduccionesGravadas();
                sb.append(c.getDetalleDeduccionesGravadas()).append(",");
            } else {
                sb.append(",");
            }
            //Provisiones
            if (c.getDetalleprovisiones() != null) {
                sb.append(c.getDetalleprovisiones());
                prov += c.getDetalleprovisiones();
            }

            empleado = c.getNumeroempleado();
            lineas.add(sb.toString());
        }
        sb = new StringBuilder();
        sb.append(",").append("Total:,").append(per).append(",")
                .append(perEx).append(",")
                .append(perGra).append(",")
                .append(dedu).append(",")
                .append(deduEx).append(",")
                .append(deduGra).append(",")
                .append(prov);

        lineas.add(sb.toString());
        lineas.add("");
        sb = new StringBuilder();
        lineas.add("");
        sb.append(",").append("Totales:,").append(this.percepciones).append(",").append(this.percepcionesExentas).append(",")
                .append(this.percepcionesGravadas).append(",").append(this.deducciones).append(",")
                .append(this.deduccionesExentas).append(",").append(this.deduccionesGravadas).append(",").append(this.provisiones);
        lineas.add(sb.toString());


        Util.escribirFichero(lineas, "DetalleGravadosExcentos", ParametrosReportes.ARCHIVO_CSV);
    }

    public void descargarReporteSimplificado() {
        List<String> lineas = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        sb.append("EMPLEADO,").append("CONCEPTO,").append("PERCEPCIONES,").append("DEDUCCIONES,").append("PROVISIONES");
        lineas.add(sb.toString());

        String empleado = this.cifrasnomina.get(0).getNumeroempleado();
        double per = 0.0;
        double dedu = 0.0;
        double prov = 0.0;
        boolean bandera = true;
        for (CifrasNomina c : this.cifrasnomina) {
            if (!c.getNumeroempleado().equals(empleado)) {
                sb = new StringBuilder();
                sb.append(",").append("Total:,").append(per).append(",")
                        .append(dedu).append(",")
                        .append(prov);

                lineas.add(sb.toString());
                lineas.add("");

                per = 0.0;
                dedu = 0.0;
                prov = 0.0;
            }

            sb = new StringBuilder();
            sb.append(c.getNumeroempleado()).append(" - ").append(c.getNombreempleado().replaceAll("Ñ", "N")).append(",");
            sb.append(c.getNumeroconcepto()).append(" - ").append(c.getNombreconcepto()).append(",");
            //Percepciones
            if (c.getDetallePercepciones() != null) {
                sb.append(c.getDetallePercepciones()).append(",");
                per += c.getDetallePercepciones();
            } else {
                sb.append(",");
            }
            //Deducciones
            if (c.getDetalleDeducciones() != null) {
                sb.append(c.getDetalleDeducciones()).append(",");
                dedu += c.getDetalleDeducciones();
            } else {
                sb.append(",");
            }
            //Provisiones
            if (c.getDetalleprovisiones() != null) {
                sb.append(c.getDetalleprovisiones());
                prov += c.getDetalleprovisiones();
            }

            empleado = c.getNumeroempleado();
            lineas.add(sb.toString());
        }
        sb = new StringBuilder();
        sb.append(",").append("Total:,").append(per).append(",")
                .append(dedu).append(",")
                .append(prov);

        lineas.add(sb.toString());
        lineas.add("");
        sb = new StringBuilder();
        lineas.add("");
        sb.append(",").append("Totales:,").append(this.percepciones).append(",").append(this.deducciones).append(",").append(this.provisiones);
        lineas.add(sb.toString());


        Util.escribirFichero(lineas, "DetallePercepcionesDeducciones", ParametrosReportes.ARCHIVO_CSV);
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
    public void delete(CifrasNomina obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void listenerSelected() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void generarMsg(FacesMessage msg, boolean isValidate) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getInformacion() {
        return CeniccoUtil.getInformacion(this.cifrasnomina.size());
    }

    @Override
    public void init() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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

    public Integer getSelectedPeriodoMes() {
        return selectedPeriodoMes;
    }

    public void setSelectedPeriodoMes(Integer selectedPeriodoMes) {
        this.selectedPeriodoMes = selectedPeriodoMes;
    }

    public double getPercepciones() {
        return percepciones;
    }

    public void setPercepciones(double percepciones) {
        this.percepciones = percepciones;
    }

    public double getPercepcionesGravadas() {
        return percepcionesGravadas;
    }

    public void setPercepcionesGravadas(double percepcionesGravadas) {
        this.percepcionesGravadas = percepcionesGravadas;
    }

    public double getPercepcionesExentas() {
        return percepcionesExentas;
    }

    public void setPercepcionesExentas(double percepcionesExentas) {
        this.percepcionesExentas = percepcionesExentas;
    }

    public double getDeducciones() {
        return deducciones;
    }

    public void setDeducciones(double deducciones) {
        this.deducciones = deducciones;
    }

    public double getDeduccionesGravadas() {
        return deduccionesGravadas;
    }

    public void setDeduccionesGravadas(double deduccionesGravadas) {
        this.deduccionesGravadas = deduccionesGravadas;
    }

    public double getDeduccionesExentas() {
        return deduccionesExentas;
    }

    public void setDeduccionesExentas(double deduccionesExentas) {
        this.deduccionesExentas = deduccionesExentas;
    }

    public double getProvisiones() {
        return provisiones;
    }

    public void setProvisiones(double provisiones) {
        this.provisiones = provisiones;
    }

    public List<CifrasNomina> getCifrasnomina() {
        return cifrasnomina;
    }

    public void setCifrasnomina(List<CifrasNomina> cifrasnomina) {
        this.cifrasnomina = cifrasnomina;
    }

    public List<CifrasNomina> getFilteredcifrasnomina() {
        return filteredcifrasnomina;
    }

    public void setFilteredcifrasnomina(List<CifrasNomina> filteredcifrasnomina) {
        this.filteredcifrasnomina = filteredcifrasnomina;
    }

    public Integer getIdgrupopago() {
        return idgrupopago;
    }

    public void setIdgrupopago(Integer idgrupopago) {
        this.idgrupopago = idgrupopago;
    }
}
