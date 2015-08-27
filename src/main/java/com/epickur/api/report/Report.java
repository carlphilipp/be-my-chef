package com.epickur.api.report;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import org.apache.commons.io.IOUtils;

import com.epickur.api.exception.EpickurException;

/**
 * @author cph
 * @version 1.0
 *
 */
public class Report {

	/** Parameters sent to Jasper APIs **/
	private Map<String, Object> parameters;

	public Report() {
		this.parameters = new HashMap<String, Object>();
	}

	/**
	 * Add param
	 * 
	 * @param param
	 *            the param
	 * @param object
	 *            the object
	 */
	public final void addParam(final String param, final Object object) {
		this.parameters.put(param, object);
	}

	/**
	 * Get report
	 * 
	 * @return a jasper print
	 * @throws EpickurException
	 */
	public final byte[] getReport() throws EpickurException {
		InputStream inputStream = null;
		try {
			inputStream = Report.class.getClassLoader().getResourceAsStream("report.jrxml");
			JasperDesign jasperDesign = JRXmlLoader.load(inputStream);
			JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());
			return JasperExportManager.exportReportToPdf(jasperPrint);
		} catch (Exception e) {
			throw new EpickurException("Error while generating the pdf report", e);
		} finally {
			IOUtils.closeQuietly(inputStream);
		}
	}
}
