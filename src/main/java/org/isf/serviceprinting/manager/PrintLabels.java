package org.isf.serviceprinting.manager;

import java.io.File;
import java.sql.Connection;
import java.util.HashMap;

import org.isf.utils.db.DbSingleJpaConn;
import org.isf.utils.exception.OHServiceException;

import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;

public class PrintLabels {
	public static final int toDisplay = 0;
	public static final int toPdf = 1;
	public static final int toPrint = 2;
	
	public PrintLabels(String filename, Integer patId  )throws OHServiceException {
		try{
		HashMap<String, String> parameters = new HashMap<>();

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
