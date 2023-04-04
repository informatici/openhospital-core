package org.isf.serviceprinting.manager;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;
import org.isf.generaldata.GeneralData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

public class ToDisplay implements Action {
	private static final Logger LOGGER = LoggerFactory.getLogger(ToDisplay.class);

	@Override
	public void Print(JasperPrint jasperPrint, String filename) throws JRException {
		if (GeneralData.INTERNALVIEWER) {
			JasperViewer.viewReport(jasperPrint, false, new Locale(GeneralData.LANGUAGE));
		} else {
			String pdfFile = "rpt_base/PDF/" + filename + ".pdf";
			JasperExportManager.exportReportToPdfFile(jasperPrint, pdfFile);
			try {
				Runtime rt = Runtime.getRuntime();
				rt.exec(GeneralData.VIEWER + " " + pdfFile);
			} catch (Exception exception) {
				LOGGER.error(exception.getMessage(), exception);
			}
		}
	}
}
