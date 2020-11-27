/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2020 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.generaldata;

import java.io.FileInputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*-------------------------------------------
 *    12/2007 - isf bari - added resource bundle for internationalization
 * 19/06/2008 - isf bari - added patientsheet jasper report name
 * 20/12/2008 - isf bari - added patientextended
 * 01/01/2009 - Fabrizio - added OPDEXTENDED
 * 20/01/2009 - Chiara   - added attribute MATERNITYRESTARTINJUNE to reset progressive number of maternity ward
 * 25/02/2011 - Claudia  - added attribute MAINMENUALWAYSONTOP to handle main menu always on Top 
 * 01/05/2011 - Vito 	 - added attribute VIDEOMODULEENABLED to enable/disable video module
 * 10/08/2011 - Claudia  - added PATIENTVACCINEEXTENDED to show patient on Patient Vaccine 
 * 19/10/2011 - Mwithi   - GeneralData 2.0: catching exception on single property and assign DEFAULT value  
 * 29/12/2011 - Nicola   - added XMPPMODULEENABLED to enable/disable communication module
 -------------------------------------------*/

public class GeneralData {
	
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final String FILE_PROPERTIES = "generalData.properties";
	public static String LANGUAGE;
	public static boolean SINGLEUSER;
	public static boolean AUTOMATICLOT_IN;
	public static boolean AUTOMATICLOT_OUT;
	public static boolean AUTOMATICLOTWARD_TOWARD;
	public static boolean LOTWITHCOST;
	public static String PATIENTSHEET;
	public static String VISITSHEET;
	public static String EXAMINATIONCHART;
	public static String OPDCHART;
	public static String ADMCHART;
	public static String DISCHART;
	public static String PATIENTBILL;
	public static String BILLSREPORT;
	public static String BILLSREPORTPENDING;
	public static String BILLSREPORTMONTHLY;
	public static String PHARMACEUTICALORDER;
	public static String PHARMACEUTICALSTOCK;
	public static String PHARMACEUTICALSTOCKLOT;
	public static String PHARMACEUTICALAMC;
	public static boolean PATIENTEXTENDED;
	public static boolean OPDEXTENDED;
	public static boolean MATERNITYRESTARTINJUNE;
	public static boolean LABEXTENDED;
	public static boolean INTERNALVIEWER;
	public static boolean LABMULTIPLEINSERT;
	public static boolean INTERNALPHARMACIES;
	public static boolean MERGEFUNCTION;
	public static boolean SMSENABLED;
	public static String VIEWER;
	public static boolean MAINMENUALWAYSONTOP;
	public static boolean RECEIPTPRINTER;
	public static boolean VIDEOMODULEENABLED;
	public static boolean PATIENTVACCINEEXTENDED;
	public static boolean ENHANCEDSEARCH;
	public static boolean XMPPMODULEENABLED;
	public static boolean DICOMMODULEENABLED;
	public static boolean DICOMTHUMBNAILS;
	public static boolean ALLOWPRINTOPENEDBILL;
	public static boolean ALLOWMULTIPLEOPENEDBILL;
	public static String PATIENTBILLGROUPED;
	public static String PATIENTBILLSTATEMENT;
	public static boolean DEBUG;
	
	private static String DEFAULT_LANGUAGE = "en";
	private static boolean DEFAULT_SINGLEUSER = false;
	private static boolean DEFAULT_AUTOMATICLOT_IN = true;
	private static boolean DEFAULT_AUTOMATICLOT_OUT = true;
	private static boolean DEFAULT_AUTOMATICLOTWARD_TOWARD=true;
	private static boolean DEFAULT_LOTWITHCOST = false;
	private static String DEFAULT_PATIENTSHEET = "patient_clinical_sheet";
	private static String DEFAULT_VISITSHEET = "WardVisits";
	private static String DEFAULT_EXAMINATIONCHART = "patient_examination";
	private static String DEFAULT_OPDCHART = "patient_opd_chart";
	private static String DEFAULT_ADMCHART = "patient_adm_chart";
	private static String DEFAULT_DISCHART = "patient_dis_chart";
	private static String DEFAULT_PATIENTBILL = "PatientBill";
	private static String DEFAULT_BILLSREPORT = "BillsReport";
	private static String DEFAULT_BILLSREPORTPENDING = "BillsReportPending";
	private static String DEFAULT_BILLSREPORTMONTHLY = "BillsReportMonthly";
	private static String DEFAULT_PHARMACEUTICALORDER = "PharmaceuticalOrder";
	private static String DEFAULT_PHARMACEUTICALSTOCK = "PharmaceuticalStock_ver4";
	private static String DEFAULT_PHARMACEUTICALSTOCKLOT = "PharmaceuticalStock_ver5"; //TODO: verify if really used
	private static String DEFAULT_PHARMACEUTICALAMC = "PharmaceuticalAMC";
	private static boolean DEFAULT_PATIENTEXTENDED = false;
	private static boolean DEFAULT_OPDEXTENDED = false;
	private static boolean DEFAULT_MATERNITYRESTARTINJUNE = false;
	private static boolean DEFAULT_LABEXTENDED = false;
	private static boolean DEFAULT_INTERNALVIEWER = true;
	private static boolean DEFAULT_LABMULTIPLEINSERT = false;
	private static boolean DEFAULT_INTERNALPHARMACIES = false;
	private static boolean DEFAULT_MERGEFUNCTION = false;
	private static boolean DEFAULT_SMSENABLED = false;
	private static boolean DEFAULT_MAINMENUALWAYSONTOP = false;
	private static boolean DEFAULT_RECEIPTPRINTER = false;
	private static boolean DEFAULT_VIDEOMODULEENABLED = false;
	private static boolean DEFAULT_PATIENTVACCINEEXTENDED = false;
	private static boolean DEFAULT_ENHANCEDSEARCH = false;
	private static boolean DEFAULT_XMPPMODULEENABLED=false;
    private static boolean DEFAULT_DICOMMODULEENABLED=false;
    private static boolean DEFAULT_DICOMTHUMBNAILS=true;
    private static boolean DEFAULT_ALLOWPRINTOPENEDBILL = false;
    private static boolean DEFAULT_ALLOWMULTIPLEOPENEDBILL=false;
	private static String DEFAULT_PATIENTBILLGROUPED = "PatientBillGrouped";
	private static String DEFAULT_PATIENTBILLSTATEMENT = "PatientBillStatement";
	private static boolean DEFAULT_DEBUG = false;

	private static GeneralData mySingleData;
	private Properties p;
	
	public static void reset() {
		mySingleData  = null;
	}
	
	
	private GeneralData() {
		try {
			p = new Properties();
			
			ClassLoader classLoader = getClass().getClassLoader();
			URL url = classLoader.getResource(FILE_PROPERTIES);
			Path path = Paths.get(url.toURI());
			p.load(new FileInputStream(path.toFile()));
			
			logger.info("File generalData.properties loaded. ");
			LANGUAGE = myGetProperty("LANGUAGE", DEFAULT_LANGUAGE);
			SINGLEUSER = myGetProperty("SINGLEUSER", DEFAULT_SINGLEUSER);
			AUTOMATICLOT_IN = myGetProperty("AUTOMATICLOT_IN", DEFAULT_AUTOMATICLOT_IN);
			AUTOMATICLOT_OUT = myGetProperty("AUTOMATICLOT_OUT", DEFAULT_AUTOMATICLOT_OUT);
			AUTOMATICLOTWARD_TOWARD = myGetProperty("AUTOMATICLOTWARD_TOWARD", DEFAULT_AUTOMATICLOTWARD_TOWARD);
			LOTWITHCOST = myGetProperty("LOTWITHCOST", DEFAULT_LOTWITHCOST);
			PATIENTSHEET = myGetProperty("PATIENTSHEET", DEFAULT_PATIENTSHEET);
			VISITSHEET = myGetProperty("VISITSHEET", DEFAULT_VISITSHEET);
			EXAMINATIONCHART =myGetProperty("EXAMINATIONCHART", DEFAULT_EXAMINATIONCHART);
			OPDCHART = myGetProperty("OPDCHART", DEFAULT_OPDCHART);
			ADMCHART = myGetProperty("ADMCHART", DEFAULT_ADMCHART);
			DISCHART = myGetProperty("DISCHART", DEFAULT_DISCHART);
			PATIENTBILL = myGetProperty("PATIENTBILL", DEFAULT_PATIENTBILL);
			BILLSREPORT = myGetProperty("BILLSREPORT", DEFAULT_BILLSREPORT);
			BILLSREPORTPENDING = myGetProperty("BILLSREPORTPENDING", DEFAULT_BILLSREPORTPENDING);
			BILLSREPORTMONTHLY = myGetProperty("BILLSREPORTMONTHLY", DEFAULT_BILLSREPORTMONTHLY);
			PHARMACEUTICALORDER = myGetProperty("PHARMACEUTICALORDER", DEFAULT_PHARMACEUTICALORDER);
			PHARMACEUTICALSTOCK = myGetProperty("PHARMACEUTICALSTOCK", DEFAULT_PHARMACEUTICALSTOCK);
			PHARMACEUTICALSTOCKLOT = myGetProperty("PHARMACEUTICALSTOCKLOT", DEFAULT_PHARMACEUTICALSTOCKLOT);
			PHARMACEUTICALAMC = myGetProperty("PHARMACEUTICALAMC", DEFAULT_PHARMACEUTICALAMC);
			PATIENTEXTENDED = myGetProperty("PATIENTEXTENDED", DEFAULT_PATIENTEXTENDED);
			OPDEXTENDED = myGetProperty("OPDEXTENDED", DEFAULT_OPDEXTENDED);
			MATERNITYRESTARTINJUNE = myGetProperty("MATERNITYRESTARTINJUNE", DEFAULT_MATERNITYRESTARTINJUNE);
			LABEXTENDED = myGetProperty("LABEXTENDED", DEFAULT_LABEXTENDED);
			LABMULTIPLEINSERT = myGetProperty("LABMULTIPLEINSERT", DEFAULT_LABMULTIPLEINSERT);
			INTERNALPHARMACIES = myGetProperty("INTERNALPHARMACIES", DEFAULT_INTERNALPHARMACIES);
			INTERNALVIEWER = myGetProperty("INTERNALVIEWER", DEFAULT_INTERNALVIEWER);
			if (!INTERNALVIEWER) VIEWER = p.getProperty("INTERNALVIEWER");
			MERGEFUNCTION = myGetProperty("MERGEFUNCTION", DEFAULT_MERGEFUNCTION);
			SMSENABLED = myGetProperty("SMSENABLED", DEFAULT_SMSENABLED);
			MAINMENUALWAYSONTOP = myGetProperty("MAINMENUALWAYSONTOP", DEFAULT_MAINMENUALWAYSONTOP);
			RECEIPTPRINTER = myGetProperty("RECEIPTPRINTER", DEFAULT_RECEIPTPRINTER);
			VIDEOMODULEENABLED = myGetProperty("VIDEOMODULEENABLED", DEFAULT_VIDEOMODULEENABLED);
			PATIENTVACCINEEXTENDED = myGetProperty("PATIENTVACCINEEXTENDED", DEFAULT_PATIENTVACCINEEXTENDED);
			ENHANCEDSEARCH = myGetProperty("ENHANCEDSEARCH", DEFAULT_ENHANCEDSEARCH);
			XMPPMODULEENABLED = myGetProperty("XMPPMODULEENABLED", DEFAULT_XMPPMODULEENABLED);
			DICOMMODULEENABLED = myGetProperty("DICOMMODULEENABLED", DEFAULT_DICOMMODULEENABLED);
			DICOMTHUMBNAILS = myGetProperty("DICOMTHUMBNAILS", DEFAULT_DICOMTHUMBNAILS);
			ALLOWPRINTOPENEDBILL = myGetProperty("ALLOWPRINTOPENEDBILL", DEFAULT_ALLOWPRINTOPENEDBILL);
			ALLOWMULTIPLEOPENEDBILL = myGetProperty("ALLOWMULTIPLEOPENEDBILL", DEFAULT_ALLOWMULTIPLEOPENEDBILL);
			PATIENTBILLGROUPED = myGetProperty("PATIENTBILLGROUPED", DEFAULT_PATIENTBILLGROUPED);
			PATIENTBILLSTATEMENT = myGetProperty("PATIENTBILLSTATEMENT", DEFAULT_PATIENTBILLSTATEMENT);
			DEBUG = myGetProperty("DEBUG", DEFAULT_DEBUG);
			
		} catch (Exception e) { //no file
			logger.error(">> " + FILE_PROPERTIES + " file not found.");
			System.exit(1);
		}
		
		MessageBundle.initialize();
		
	}
	
	/**
	 * 
	 * Method to retrieve a boolean property
	 * 
	 * @param property
	 * @param defaultValue
	 * @return
	 */
	private boolean myGetProperty(String property, boolean defaultValue) {
		boolean value;
		try {
			value = p.getProperty(property).equalsIgnoreCase("YES");
		} catch (Exception e) {
			logger.warn(">> {} property not found: default is {}", property, defaultValue);
			return defaultValue;
		}
		return value;
	}

	/**
	 * 
	 * Method to retrieve a string property
	 * 
	 * @param property
	 * @param defaultValue
	 * @return
	 */
	private String myGetProperty(String property, String defaultValue) {
		String value;
		value = p.getProperty(property);
		if (value == null) {
			logger.warn(">> {} property not found: default is {}", property, defaultValue);
			return defaultValue;
		}
		return value;
	}

	public static GeneralData getGeneralData() {
		if (mySingleData == null) {
			mySingleData = new GeneralData();
		}
		return mySingleData;
	}
	
}

