package com.epickur.api.utils.report;

import com.epickur.api.exception.EpickurException;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author cph
 * @version 1.0
 */
public class Report {

	/**
	 * Parameters sent to Jasper APIs
	 **/
	private final Map<String, Object> parameters;

	/**
	 * Construct a report
	 */
	public Report() {
		this.parameters = new HashMap<>();
	}

	/**
	 * Add param
	 *
	 * @param param  the param
	 * @param object the object
	 */
	public void addParam(final String param, final Object object) {
		parameters.put(param, object);
	}

	/**
	 * Get report
	 *
	 * @return a jasper print
	 * @throws EpickurException If an epickur exception occurred
	 */
	public byte[] getReport() throws EpickurException {
		try (final InputStream inputStream = Report.class.getClassLoader().getResourceAsStream("report.jrxml")) {
			final JasperDesign jasperDesign = JRXmlLoader.load(inputStream);
			final JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
			final JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());
			return JasperExportManager.exportReportToPdf(jasperPrint);
		} catch (final Exception e) {
			throw new EpickurException("Error while generating the pdf report", e);
		}
	}
}
