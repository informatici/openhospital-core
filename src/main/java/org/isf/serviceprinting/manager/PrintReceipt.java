/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2021 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.ServiceUI;
import javax.print.SimpleDoc;
import javax.print.attribute.Attribute;
import javax.print.attribute.DocAttributeSet;
import javax.print.attribute.HashDocAttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;

import org.isf.generaldata.TxtPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.export.JRTextExporter;
import net.sf.jasperreports.engine.export.JRTextExporterParameter;

/**
 * This class will read generic/text printer parameters and compile and
 * print given jasper report. A copy will be at given file path
 *
 * @author Mwithi
 */
public class PrintReceipt {

	private static final Logger LOGGER = LoggerFactory.getLogger(PrintReceipt.class);

	private PrintService defaultPrintService;

	/**
	 * @param jasperPrint
	 * @param fileName
	 */
	public PrintReceipt(JasperPrint jasperPrint, String fileName) {
				
		TxtPrinter.initialize();
		
		try {
			defaultPrintService = PrintServiceLookup.lookupDefaultPrintService();
			if (defaultPrintService != null) {
				if (TxtPrinter.MODE.equalsIgnoreCase("ZPL")) {
					
					JRTextExporter exporter = new JRTextExporter();
					exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
					exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, fileName);
					exporter.setParameter(JRTextExporterParameter.CHARACTER_WIDTH, TxtPrinter.TXT_CHAR_WIDTH);
					exporter.setParameter(JRTextExporterParameter.CHARACTER_HEIGHT, TxtPrinter.TXT_CHAR_HEIGHT);
					exporter.exportReport();
					
					printFileZPL(fileName, !TxtPrinter.USE_DEFAULT_PRINTER);
					
				} else if (TxtPrinter.MODE.equalsIgnoreCase("TXT")) {
						
					if (jasperPrint.getPages().size() > 1) {
						printReversPages(jasperPrint);
					} else {
						JasperPrintManager.printReport(jasperPrint, !TxtPrinter.USE_DEFAULT_PRINTER);
					}
				} else if (TxtPrinter.MODE.equalsIgnoreCase("PDF")) {
					
					if (jasperPrint.getPages().size() > 1) {
						printReversPages(jasperPrint);
					} else {
						JasperPrintManager.printReport(jasperPrint, !TxtPrinter.USE_DEFAULT_PRINTER);
					}

				} else {
					LOGGER.debug("invalid MODE");
					LOGGER.debug("MODE: {}", TxtPrinter.MODE);
				}
			} else {
				LOGGER.debug("printer was not found.");
			}
		} catch (Exception exception) {
			LOGGER.error(exception.getMessage(), exception);
		}
	}
	
	/**
	 * @param file
	 * @param showDialog
	 */
	private void printFileZPL(String file, boolean showDialog) {
		try {
			PrintService printService;
			if (showDialog) {
				PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
				DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
			    PrintService[] printServices = PrintServiceLookup.lookupPrintServices(flavor, pras);
			    PrintService defaultService = PrintServiceLookup.lookupDefaultPrintService();
			    printService = ServiceUI.printDialog(null, 200, 200, printServices, defaultService, flavor, pras);
			} else {
				printService = defaultPrintService;
			}
			if (printService == null) return;
			getPrinterDetails(printService);
			DocPrintJob job = printService.createPrintJob();
			DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
			PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
			DocAttributeSet das = new HashDocAttributeSet();
			
			try (FileReader frStream = new FileReader(file)) {
				try (BufferedReader brStream = new BufferedReader(frStream)) {

					int charH = TxtPrinter.ZPL_ROW_HEIGHT;
					String font = "^A" + TxtPrinter.ZPL_FONT_TYPE;
					String aLine = brStream.readLine();
					String header = "^XA^LH0,30" + aLine;//starting point

					StringBuilder zpl = new StringBuilder();
					int i = 0;
					while (!aLine.equals("")) {
						zpl.append("^FO0,").append(i * charH);         //line position
						zpl.append(font).append(",").append(charH);    //font size
						zpl.append("^FD").append(aLine).append("^FS"); //line field
						aLine = brStream.readLine();
						i++;
					}
					zpl.append("^XZ");//end
					String labelLength = "^LL" + charH * i;
					header += labelLength;
					String label = header + zpl;

					byte[] by = label.getBytes();
					Doc doc = new SimpleDoc(by, flavor, das);
					job.print(doc, pras);
				}
			}
		} catch (IOException | PrintException exception) {
			LOGGER.error(exception.getMessage(), exception);
		}
	}
	
	/**
	 * @param jasperPrint
	 */
	private void printReversPages(JasperPrint jasperPrint) {
		try {
			List<JRPrintPage> pages = jasperPrint.getPages();
			JasperPrintManager.printPages(jasperPrint, 0, pages.size()-1, !TxtPrinter.USE_DEFAULT_PRINTER);
		} catch (JRException jrException) {
			LOGGER.error(jrException.getMessage(), jrException);
		}
	}
	
	/**
	 * @param printService
	 */
	private void getPrinterDetails(PrintService printService) {
		LOGGER.debug("Printer: {}", printService.getName());
		LOGGER.debug("Supported flavors:");
		DocFlavor[] flavors = printService.getSupportedDocFlavors();
		if (flavors != null) {
			for (DocFlavor flavor : flavors) {
				LOGGER.debug(flavor.toString());
			}
		}
		Attribute[] attributes = printService.getAttributes().toArray();
		if (attributes != null) {
			for (Attribute attr : attributes) {
				LOGGER.debug("{}: {}", attr.getName(), attr.getClass());
			}
		}
	}
}
