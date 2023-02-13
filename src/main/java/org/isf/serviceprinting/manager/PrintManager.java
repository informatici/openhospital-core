/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2023 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
 *
 * Open Hospital is a free and open source software for healthcare data management.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
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
				JasperReport jasperReport = (JasperReport) JRLoader
				.loadObject(jasperFile);
				JasperPrint jasperPrint = JasperFillManager.fillReport(
						jasperReport, parameters, dataSource);
				switch (action) {
				case 0:
					if (GeneralData.INTERNALVIEWER) {
						JasperViewer.viewReport(jasperPrint,false, new Locale(GeneralData.LANGUAGE));
					} else {
						String pdfFile = "rpt_base/PDF/" + filename + ".pdf";
						JasperExportManager.exportReportToPdfFile(jasperPrint, pdfFile);
						try {
							Runtime rt = Runtime.getRuntime();
							rt.exec(GeneralData.VIEWER +" "+ pdfFile);
						} catch(Exception exception) {
							LOGGER.error(exception.getMessage(), exception);
						}
					}
					break;
				case 1:
					JasperExportManager.exportReportToPdfFile(jasperPrint,"rpt_base/PDF/"+
							JOptionPane.showInputDialog(null,MessageBundle.getMessage("angal.serviceprinting.selectapathforthepdffile.msg"), filename)
							+".pdf");
					break;
				case 2:JasperPrintManager.printReport(jasperPrint, true);
					break;
				default:JOptionPane.showMessageDialog(null,MessageBundle.getMessage("angal.serviceprinting.selectacorrectaction.msg"));
					break;
				}
			} else {
				JOptionPane.showMessageDialog(null,MessageBundle.getMessage("angal.serviceprinting.notavalidfile.msg"));
			}
		} catch (JRException jrException) {
			LOGGER.error(jrException.getMessage(), jrException);
		}
	}
}
