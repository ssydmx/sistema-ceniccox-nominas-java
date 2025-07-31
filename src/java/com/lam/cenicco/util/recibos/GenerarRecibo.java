    /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lam.cenicco.util.recibos;

import com.lam.cenicco.impl.ControladorWS;
import com.lam.cenicco.librerias.utilidades.CeniccoUtil;
import com.lam.cenicco.to.FacturaTO;
import com.lam.cenicco.util.EtiquetasSat;
import com.lam.cenicco.util.Util;
import com.lam.cenicco.ws.CifrasNomina;
import com.lam.cenicco.ws.Concepto;
import com.lam.cenicco.ws.Domicilio;
import com.lam.cenicco.ws.Empleado;
import com.lam.cenicco.ws.GrupoPago;
import com.lam.cenicco.ws.Parametro;
import com.lam.cenicco.ws.Periodo;
import com.lam.cenicco.ws.RelacionLaboral;
import com.lam.cenicco.ws.SaldoVacaciones;
import com.mysql.jdbc.StringUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author Diego
 */
public class GenerarRecibo {

    //
    public FacturaTO convertirxml(FacturaTO factura, String xml) {
        Document dom;
        try {
            dom = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                    .parse(new InputSource(new ByteArrayInputStream(xml.getBytes("utf-8"))));
//            
            Element rootElement = dom.getDocumentElement();
            NodeList nodeList = rootElement.getElementsByTagName("tfd:TimbreFiscalDigital");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Element element = (Element) nodeList.item(i);
                if (element.hasAttribute("SelloCFD")) {
//                    System.out.println("SelloCFD... " + element.getAttribute("SelloCFD"));
                    factura.setSellocfd(element.getAttribute("SelloCFD"));
                }
                if (element.hasAttribute("SelloSAT")) {
//                    System.out.println("SelloSAT... " + element.getAttribute("SelloSAT"));
                    factura.setSelloSAT(element.getAttribute("SelloSAT"));
                }
                if (element.hasAttribute("NoCertificadoSAT")) {
//                    System.out.println("NoCertificadoSAT... " + element.getAttribute("NoCertificadoSAT"));
                    factura.setNocertificadoSAT(element.getAttribute("NoCertificadoSAT"));
                }
                if (element.hasAttribute("UUID")) {
//                    System.out.println("UUID... " + element.getAttribute("UUID"));
                    factura.setUuid(element.getAttribute("UUID"));
                }
                if (element.hasAttribute("FechaTimbrado")) {
                    factura.setFechatimbrado(element.getAttribute("FechaTimbrado"));
                }
                if (element.hasAttribute("Version")) {
                    factura.setVersion(element.getAttribute("Version"));
                }
            }
            return factura;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            System.out.println(e);
            return null;
        }
    }

    public byte[] getPDF(FacturaTO factura, GrupoPago gp, Periodo periodo, byte[] qr, boolean aplicasaldos) {
        Map<String, Object> params = new HashMap<>();
//      
        byte[] xml =
                factura.getXml() != null
                ? factura.getXml()
                : factura.getRespuesta().getXmlTimbrado();
//        
        try {
//
            factura = this.convertirxml(factura, new String(xml, "utf-8"));
//          
            Integer diasSaldo = 0;
            CifrasNomina nomina = factura.getNomina();
            RelacionLaboral rl = factura.getRelacionlaboral();
            List<SaldoVacaciones> sv = null;
            if (rl.getIdgrupopago().getIdcompania().getNombreCorto().equals("ARTSANA")) {
                String concepto = ControladorWS.getInstance().getConceptoVacaciones();
                sv = ControladorWS.getInstance().getSaldoVacaciones(rl.getNumeroempleado(), rl.getIdgrupopago().getIdgrupopago(), "1", concepto);
                for (SaldoVacaciones vacacionesIteracion : sv) {
                    diasSaldo = diasSaldo + vacacionesIteracion.getSaldo().intValue();
                }
            }
            Empleado emp = rl.getIdempleado();

            double importeneto = nomina.getPercepciones() - nomina.getDeducciones();
            String decimal;
//            
            String plantilla;
//          
            if ((gp.getIdcompania().getNombreCorto().equals("AEMSA") || gp.getIdcompania().getNombreCorto().equals("MARTILLO")) && periodo.getIdtipoproceso().getNombre().equals("PAGO NOMINA ASIMILADOS")) {
                plantilla = "/com/lam/cenicco/templates/recibos/ReporteFacturasSinSaldosAemsa.jrxml";
            } else {
                if (aplicasaldos) {
                    plantilla = "/com/lam/cenicco/templates/recibos/ReporteFacturas.jrxml";
                } else {
                    plantilla = "/com/lam/cenicco/templates/recibos/ReporteFacturasSinSaldos.jrxml";
                }
            }

            if (gp.getIdcompania().getNombreCorto().equals("ARTSANA")) {
                plantilla = "/com/lam/cenicco/templates/recibos/ReporteFacturasArtsana.jrxml";
                if (sv != null && !sv.isEmpty()) {
                    params.put("saldoVacaciones", diasSaldo + " Días");
                } else {
                    params.put("saldoVacaciones", "0 Días");
                }
            }
//           
            if (gp.getIdcompania().getNombreCorto().equals("MINNT")) {
                plantilla = "/com/lam/cenicco/templates/recibos/ReporteFacturasSinSaldosMinntTeletrabajo.jrxml";
            }
//                  
            Parametro rgn = ControladorWS.getInstance().getParametro("ACTIVAR_RECIBO_GENERICO_NOMINA");
            boolean isCheckReciboGenericoNomina = (rgn != null && "S".equals(rgn.getValor()));
            if (isCheckReciboGenericoNomina) {
                plantilla = "/com/lam/cenicco/templates/recibos/ReciboGenericoNomina.jrxml";
                if (gp.getGrupopago().equals("NS") && gp.getIdcompania().getNombreCorto().equals("ABASTOHOTEL")) {
                    plantilla = "/com/lam/cenicco/templates/recibos/ReciboGenericoNominaAbasto.jrxml";
                }
            }
//
            String saldoVariable = "";
            Double otrasFormasPagosTimbrado = 0.0;
            Double especieTimbrado = 0.0;
            Double otrasFormasPagosInformativo = 0.0;
            Double especieInformativo = 0.0;

            Iterator<Concepto> iterator = factura.getRegistros().iterator();

            while (iterator.hasNext()) {
                Concepto concepto = iterator.next();
                if (concepto.getNombre().equals("SUELDO VARIABLE")) {
                    saldoVariable = Double.toString(concepto.getValor());
                    iterator.remove();
                }

                if (concepto.isOtrasFormasPagos() && concepto.getRecibonomina() == 1) {
                    otrasFormasPagosInformativo += concepto.getValor();
                    if ((!StringUtils.isNullOrEmpty(concepto.getAtributo()))) {
                        otrasFormasPagosTimbrado += concepto.getValor();
                    }
                }

                if (concepto.isEspecie() && concepto.getRecibonomina() == 1) {
                    especieInformativo += concepto.getValor();
                    if ((concepto.getAtributo() != null || !"".equals(concepto.getAtributo()))) {
                        especieTimbrado += concepto.getValor();
                    }
                }
            }

            JasperDesign jd = JRXmlLoader.load(this.getClass().getResourceAsStream(plantilla));
            params.put("sellocfd", factura.getSellocfd());
            params.put("sellosat", factura.getSelloSAT());
            params.put("nocertificadoSAT", factura.getNocertificadoSAT());
            params.put("lugarEmision", gp.getLugarexpedicion());
            params.put("fechaEmision", factura.getFechatimbrado());
            params.put("razonSocial", gp.getIdcompania().getNombre());
            if (gp.getIdcompania().getNombreCorto().equals("ARTSANA") && rl.getIdregistropatronal().getComentarios().equalsIgnoreCase("GUADALAJARA")) {
                params.put("domicilioEmpresa", gp.getDireccionfiscal());

            } else {
                params.put("domicilioEmpresa", rl.getIdcompania().getDireccion());
            }
            params.put("rfcEmisor", gp.getIdcompania().getRfc());
            params.put("registroPatronal", factura.getRelacionlaboral().getIdregistropatronal().getRegistropatronal());
            params.put("numeroEmpleado", rl.getNumeroempleado());
            params.put("curp", emp.getCurp());
            params.put("fechaAntiguedad", CeniccoUtil.convertDateToString(rl.getFechaantiguedad1().toGregorianCalendar().getTime()));
            params.put("nss", emp.getAfiliacion());
            params.put("fechaPago", CeniccoUtil.convertDateToString(periodo.getFechapago().toGregorianCalendar().getTime()));
            params.put("periodoPago", Util.concatPeriodoPago(periodo));
            params.put("periodo", periodo.getPeriodo().toString());
            params.put("diasPagados", periodo.getIdtipoproceso().getPeriodicidad().doubleValue());
            params.put("departamento", factura.getDepto());
            params.put("puesto", factura.getPuesto());
            params.put("perioricidad", factura.getPeriodicidad());
            params.put("salario", factura.getRelacionlaboral().getSalarioDiario());
            params.put("salarioIntegrado", factura.getRelacionlaboral().getSalarioDiarioIntegrado());
            params.put("rfcReceptor", emp.getRfc());
            params.put("nombreReceptor", nomina.getNombreempleado());
            params.put("regimenFiscal", gp.getIdcompania().getRegimenfiscal());
            params.put("subtotal", factura.getSubtotal());
            params.put("total", factura.getTotal() - otrasFormasPagosTimbrado - especieTimbrado);
            params.put("formaDePago", EtiquetasSat.PAGO_EXHIBICION.getConcepto());
            //decimal = CeniccoUtil.calcularDecimal(factura.getTotal() - otrasFormasPagosTimbrado - especieTimbrado);
            //String cantidadLetra = new numbertoletterconverter.NumberToLetterConverter().convertNumberToLetter(factura.getTotal() - otrasFormasPagosTimbrado - especieTimbrado) + ", " + decimal + "/100 M.N.";
            double importePercepciones = 0.0;
            double importeDeducciones = 0.0;
            for (Concepto c : factura.getRegistros()) {
                importePercepciones += c.getImportepercepcion();
                importeDeducciones += c.getImportededuccion();
            }
            double dispersionBancaria = (importePercepciones - importeDeducciones) - otrasFormasPagosInformativo;
            decimal = CeniccoUtil.calcularDecimal(dispersionBancaria);
            String cantidadLetra = new numbertoletterconverter.NumberToLetterConverter().convertNumberToLetter(dispersionBancaria) + ", " + decimal + "/100 M.N.";
            params.put("cantidadLetra", cantidadLetra);
            params.put("fechaTimbrado", factura.getFecharegistro());
            params.put("precioUnitario", factura.getSubtotal());
            params.put("importe", factura.getSubtotal());
            params.put("descripcion", factura.getDescripcion());
            params.put("unidad", EtiquetasSat.UNIDAD.getConcepto());
            params.put("cantidad", Double.parseDouble(EtiquetasSat.CANTIDAD.getConcepto()));
            params.put("isr", factura.getIsr());
            params.put("descuento", factura.getOtrasdeducciones());
            params.put("folio", factura.getUuid());
            params.put("domicilioObra", gp.getIdcompania().getDireccion());
            params.put("saldoVariable", saldoVariable);
            params.put("domicilio_NO_Sucursal", rl.getIdcompania().getDireccion());
            params.put("otrasFormasPagos", otrasFormasPagosInformativo);

            if (qr != null) {
                InputStream isqr = new ByteArrayInputStream(qr);
                params.put("timbre", isqr);
            }

            InputStream isLogo = new ByteArrayInputStream(gp.getIdcompania().getLogo());
            params.put("logo", isLogo);
            params.put(JRParameter.REPORT_LOCALE, new Locale("es", "MX"));
            if (isCheckReciboGenericoNomina) {
                List<Concepto> percepciones = new ArrayList<>();
                List<Concepto> deducciones = new ArrayList<>();
                List<Concepto> provisiones = new ArrayList<>();
                for (Concepto concepto : factura.getRegistros()) {
                    switch (concepto.getTipoconcepto()) {
                        case "01":
                            percepciones.add(concepto);
                            break;
                        case "02":
                            deducciones.add(concepto);
                            break;
                        case "03":
                            provisiones.add(concepto);
                            break;
                    }
                }
                Domicilio domicilioFiscal = null;
                List<Domicilio> domicilios = ControladorWS.getInstance().findDomicilioByIdEmpleado(emp.getIdempleado());
                for (Domicilio d : domicilios) {
                    if (d.getFiscal() == 1) {
                        domicilioFiscal = d;
                    }
                }
                params.put("fechaInicioPeriodo", periodo.getFechainicio() != null ? new SimpleDateFormat("dd/MMM/yyyy").format(periodo.getFechainicio().toGregorianCalendar().getTime()) : "");
                params.put("fechaFinPeriodo", periodo.getFechafin() != null ? new SimpleDateFormat("dd/MMM/yyyy").format(periodo.getFechafin().toGregorianCalendar().getTime()) : "");
                params.put("codigoPostalEmpleado", domicilioFiscal != null ? domicilioFiscal.getCp() : "NA");
                params.put("percepcionesData", percepciones);
                params.put("deduccionesData", deducciones);
                params.put("totalPercepcionesData", importePercepciones);
                params.put("totalDeduccionesData", importeDeducciones);
                String companiaNombreCorto = gp.getIdcompania().getNombreCorto();
                switch (companiaNombreCorto) {
                    case "EPAM":
                    case "EGELHOF":
                    case "BAKERTILLY":
                    case "IREP":
                    case "ESENTTIA":
                    case "MEXICIP":
                    case "GISS2P":
                    case "GIGAMON":
                    case "M7SSS":
                    case "CHOBANNI":
                    case "BLUEGROUND":
                    case "CARBON":
                    case "ELEVANTE":
                    case "HAZEL":
                    case "KASPERSKY":
                    case "MAQUIPRIME":
                    case "SENSEDIA":
                    case "DEMONOMINA":
                    case "ACTUALIZE":
                    case "FOLEY":
                    case "CIELO":
                    case "GLOWDX":
                    case "IKEA":
                    case "IMPERVA":
                    case "MIDCHEM":
                    case "OFFERWISE":
                    case "RNDRAGO":
                    case "SHIFT":
                    case "ULTRAFABRICS":
                    case "SMALLER":
                    case "TRACTORES":
                    case "SWIFT":
                    case "WILMAR":
                    case "HANDLING":
                    case "AFIS":
                        params.put("totalData", (importePercepciones - importeDeducciones));
                        break;
                    default:
                        params.put("totalData", (importePercepciones - importeDeducciones) - otrasFormasPagosInformativo);
                        break;
                }
                params.put("cadenasat", factura.getCadena());
                params.put("version", factura.getVersion());
                params.put("nocertificado", factura.getRelacionlaboral().getIdcompania().getNumerocertificado());
                params.put("CadenaOriginalCertificacionDigitalSAT", "||" + factura.getVersion() + "|" + factura.getUuid() + "|" + factura.getFechatimbrado() + "|" + factura.getSellocfd() + "|" + factura.getNocertificadoSAT());
                params.put("identificador", factura.getNomina().getIdnomina().toString());
            }
////          
////          EXPORTA A FORMATO PDF  
////            
            JRPdfExporter exporter = new JRPdfExporter();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            
            Collections.sort(factura.getRegistros(), new Comparator<Concepto>() {
                @Override
                public int compare(Concepto c1, Concepto c2) {
                    return c1.getConcepto().compareTo(c2.getConcepto());
                }
            });
//
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(factura.getRegistros());
            JasperReport jr = JasperCompileManager.compileReport(jd);
            JasperPrint jp = null;
            if (isCheckReciboGenericoNomina) {
                jp = JasperFillManager.fillReport(jr, params, new JREmptyDataSource());
            } else {
                jp = JasperFillManager.fillReport(jr, params, dataSource);
            }
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jp);
            exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, baos);
            exporter.exportReport();

//          CONVIERTE A BYTES  

            return baos.toByteArray();
        } catch (UnsupportedEncodingException | JRException | NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
    }
}
