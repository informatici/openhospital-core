package org.isf.serviceprinting.manager;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.isf.generaldata.MessageBundle;

import javax.swing.*;

public class ToPdf implements Action {

	@Override
	public void Print(JasperPrint jasperPrint, String filename) throws JRException {
		JasperExportManager.exportReportToPdfFile(jasperPrint,"rpt_base/PDF/"+
			JOptionPane.showInputDialog(null, MessageBundle.getMessage("angal.serviceprinting.selectapathforthepdffile.msg"), filename)
			+".pdf");
	}
}
