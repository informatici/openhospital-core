package org.isf.serviceprinting.manager;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.JOptionPane;

import org.isf.generaldata.GeneralData;
import org.isf.generaldata.MessageBundle;
import org.isf.hospital.manager.HospitalBrowsingManager;
import org.isf.hospital.model.Hospital;
import org.isf.utils.exception.OHServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

@Component
public class PrintManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(PrintManager.class);
	public static final int toDisplay = 0;
	public static final int toPdf = 1;
	public static final int toPrint = 2;

	@Autowired
	private HospitalBrowsingManager hospitalManager;

	public PrintManager() {}

	public void print(String filename, List<?> toPrint, int action) throws OHServiceException {

		Map<String, Object> parameters = new HashMap<>();
		Hospital hospital = hospitalManager.getHospital();
		parameters.put("ospedaleNome", hospital.getDescription());
		parameters.put("ospedaleIndirizzo", hospital.getAddress());
		parameters.put("ospedaleCitta", hospital.getCity());
		parameters.put("ospedaleTel", hospital.getTelephone());
		parameters.put("ospedaleFax", hospital.getFax());
		parameters.put("ospedaleMail", hospital.getEmail());

		JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(toPrint);
		File jasperFile = new File("rpt_base/" + filename + ".jasper");
		try {
			if (jasperFile.isFile()) {
				JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperFile);
				JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);


				PrintAction printAction = selectPrintAction(action);
				printAction.execute(jasperPrint, filename);

			} else {
				JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.serviceprinting.notavalidfile.msg"));
			}
		} catch (JRException jrException) {
			LOGGER.error(jrException.getMessage(), jrException);
		}
	}

	/**
	 *
	 * @param action
	 * @return
	 */
	private PrintAction selectPrintAction(int action) {
		switch (action) {
			case toDisplay:
				return new DisplayReportAction();
			case toPdf:
				return new ExportToPdfAction();
			case toPrint:
				return new DirectPrintAction();
			default:
				throw new IllegalArgumentException("Invalid print action");
		}
	}


	private interface PrintAction {
		void execute(JasperPrint jasperPrint, String filename) throws JRException, OHServiceException;
	}

	private class DisplayReportAction implements PrintAction {
		@Override
		public void execute(JasperPrint jasperPrint, String filename) throws JRException, OHServiceException {
			JasperViewer.viewReport(jasperPrint, false, new Locale(GeneralData.LANGUAGE));
		}
	}

	private class ExportToPdfAction implements PrintAction {
		@Override
		public void execute(JasperPrint jasperPrint, String filename) throws JRException, OHServiceException {
			JasperExportManager.exportReportToPdfFile(jasperPrint, "rpt_base/PDF/" + filename + ".pdf");
		}
	}

	private class DirectPrintAction implements PrintAction {
		@Override
		public void execute(JasperPrint jasperPrint, String filename) throws JRException, OHServiceException {
			JasperPrintManager.printReport(jasperPrint, true);
		}
	}
}
