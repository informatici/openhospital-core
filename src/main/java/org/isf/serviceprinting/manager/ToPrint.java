package org.isf.serviceprinting.manager;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;

public class ToPrint implements Action {

	@Override
	public void Print(JasperPrint jasperPrint, String filename) throws JRException {
		JasperPrintManager.printReport(jasperPrint, true);
	}
}
