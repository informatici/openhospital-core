package org.isf.serviceprinting.manager;

import java.io.File;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

import javax.swing.JOptionPane;

import org.isf.generaldata.GeneralData;
import org.isf.generaldata.MessageBundle;
import org.isf.hospital.manager.HospitalBrowsingManager;
import org.isf.hospital.model.Hospital;
import org.isf.lab.model.Laboratory;
import org.isf.utils.db.DbSingleConn;
import org.isf.utils.db.DbSingleJpaConn;
import org.isf.utils.exception.OHServiceException;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

public class PrintLabels {
	public static final int toDisplay = 0;

	public static final int toPdf = 1;

	public static final int toPrint = 2;

	public PrintLabels(String filename, Integer patId  )throws OHServiceException {
		try{
		HashMap<String, String> parameters = new HashMap<String, String>();
		HospitalBrowsingManager hospManager = new HospitalBrowsingManager();
		Hospital hosp = hospManager.getHospital();
		
		parameters.put("patientID", String.valueOf(patId));

		StringBuilder sbFilename = new StringBuilder();
		sbFilename.append("rpt");
		sbFilename.append(File.separator);
		String jasperFileName = filename;
	
		sbFilename.append(jasperFileName);
		sbFilename.append(".jasper");
		File jasperFile = new File(sbFilename.toString());
		Connection conn = DbSingleJpaConn.getConnection();
		JasperReport jasperReport = (JasperReport)JRLoader.loadObject(jasperFile);
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, conn);
		JasperPrintManager.printReport(jasperPrint, true);

				

	} catch (Exception e) {
		e.printStackTrace();
	}
		
		
	}
}
