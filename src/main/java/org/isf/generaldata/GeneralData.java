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
package org.isf.generaldata;

/**
 * ------------------------------------------
 * General Data
 * <p>
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
 * 06/07/2022 - Nicole   - added USERSLISTLOGIN to login by typing the username in a textbox (no) or selecting the user from a list (yes)
 * -------------------------------------------
 */
public final class GeneralData extends ConfigurationProperties {
	
	private static final String FILE_PROPERTIES = "settings.properties";
	private static final boolean EXIT_ON_FAIL = true;
	
	private final boolean SINGLEUSER;
	private final boolean USERSLISTLOGIN;
	
	public static String LANGUAGE;
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
	public static String DOC_DIR;
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
	public static String PATIENTPHOTOSTORAGE;
	public static Integer SESSIONTIMEOUT;

	public static boolean STRONGPASSWORD;
	public static int STRONGLENGTH;

	private static final String DEFAULT_LANGUAGE = "en";
	private static final boolean DEFAULT_SINGLEUSER = false;
	private static final boolean DEFAULT_USERSLISTLOGIN = false;
	private static final boolean DEFAULT_AUTOMATICLOT_IN = true;
	private static final boolean DEFAULT_AUTOMATICLOT_OUT = true;
	private static final boolean DEFAULT_AUTOMATICLOTWARD_TOWARD = true;
	private static final boolean DEFAULT_LOTWITHCOST = false;
	private static final String DEFAULT_PATIENTSHEET = "patient_clinical_sheet";
	private static final String DEFAULT_VISITSHEET = "WardVisits";
	private static final String DEFAULT_EXAMINATIONCHART = "patient_examination";
	private static final String DEFAULT_OPDCHART = "patient_opd_chart";
	private static final String DEFAULT_ADMCHART = "patient_adm_chart";
	private static final String DEFAULT_DISCHART = "patient_dis_chart";
	private static final String DEFAULT_PATIENTBILL = "PatientBill";
	private static final String DEFAULT_BILLSREPORT = "BillsReport";
	private static final String DEFAULT_BILLSREPORTPENDING = "BillsReportPending";
	private static final String DEFAULT_BILLSREPORTMONTHLY = "BillsReportMonthly";
	private static final String DEFAULT_PHARMACEUTICALORDER = "PharmaceuticalOrder";
	private static final String DEFAULT_PHARMACEUTICALSTOCK = "PharmaceuticalStock_ver4";
	private static final String DEFAULT_PHARMACEUTICALSTOCKLOT = "PharmaceuticalStock_ver5"; //TODO: verify if really used
	private static final String DEFAULT_PHARMACEUTICALAMC = "PharmaceuticalAMC";
	private static final boolean DEFAULT_PATIENTEXTENDED = false;
	private static final boolean DEFAULT_OPDEXTENDED = false;
	private static final boolean DEFAULT_MATERNITYRESTARTINJUNE = false;
	private static final boolean DEFAULT_LABEXTENDED = false;
	private static final boolean DEFAULT_INTERNALVIEWER = true;
	private static final String DEFAULT_DOC_DIR = "../doc";
	private static final boolean DEFAULT_LABMULTIPLEINSERT = false;
	private static final boolean DEFAULT_INTERNALPHARMACIES = false;
	private static final boolean DEFAULT_MERGEFUNCTION = false;
	private static final boolean DEFAULT_SMSENABLED = false;
	private static final boolean DEFAULT_MAINMENUALWAYSONTOP = false;
	private static final boolean DEFAULT_RECEIPTPRINTER = false;
	private static final boolean DEFAULT_VIDEOMODULEENABLED = false;
	private static final boolean DEFAULT_PATIENTVACCINEEXTENDED = false;
	private static final boolean DEFAULT_ENHANCEDSEARCH = false;
	private static final boolean DEFAULT_XMPPMODULEENABLED = false;
	private static final boolean DEFAULT_DICOMMODULEENABLED = false;
	private static final boolean DEFAULT_DICOMTHUMBNAILS = true;
	private static final boolean DEFAULT_ALLOWPRINTOPENEDBILL = false;
	private static final boolean DEFAULT_ALLOWMULTIPLEOPENEDBILL = false;
	private static final String DEFAULT_PATIENTBILLGROUPED = "PatientBillGrouped";
	private static final String DEFAULT_PATIENTBILLSTATEMENT = "PatientBillStatement";
	private static final boolean DEFAULT_DEBUG = false;
	private static final int DEFAULT_STRONGLENGTH = 10;
	private static final int DEFAULT_SESSIONTIMEOUT = 5;
	private static final boolean DEFAULT_STRONGPASSWORD = true;
	private static final String DEFAULT_PATIENTPHOTOSTORAGE = "DB";
	
	public static final int IMAGE_THUMBNAIL_MAX_WIDTH = 140;
	public static final int MAX_PROFILE_IMAGE_FILE_SIZE_BYTES = 32768;
	
	private static GeneralData mySingleData;
	
	public static void reset() {
		mySingleData  = null;
	}
	
	
	private GeneralData(String fileProperties) {
		super(fileProperties, EXIT_ON_FAIL);
		SINGLEUSER = myGetProperty("SINGLEUSER", DEFAULT_SINGLEUSER);
		USERSLISTLOGIN = myGetProperty("USERSLISTLOGIN", DEFAULT_USERSLISTLOGIN);
		LANGUAGE = myGetProperty("LANGUAGE", DEFAULT_LANGUAGE);
		AUTOMATICLOT_IN = myGetProperty("AUTOMATICLOT_IN", DEFAULT_AUTOMATICLOT_IN);
		AUTOMATICLOT_OUT = myGetProperty("AUTOMATICLOT_OUT", DEFAULT_AUTOMATICLOT_OUT);
		AUTOMATICLOTWARD_TOWARD = myGetProperty("AUTOMATICLOTWARD_TOWARD", DEFAULT_AUTOMATICLOTWARD_TOWARD);
		LOTWITHCOST = myGetProperty("LOTWITHCOST", DEFAULT_LOTWITHCOST);
		PATIENTSHEET = myGetProperty("PATIENTSHEET", DEFAULT_PATIENTSHEET);
		VISITSHEET = myGetProperty("VISITSHEET", DEFAULT_VISITSHEET);
		EXAMINATIONCHART = myGetProperty("EXAMINATIONCHART", DEFAULT_EXAMINATIONCHART);
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
		if (!INTERNALVIEWER) {
			VIEWER = myGetProperty("INTERNALVIEWER");
		}
		DOC_DIR = myGetProperty("DOC_DIR", DEFAULT_DOC_DIR);
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
		STRONGPASSWORD = myGetProperty("STRONGPASSWORD", DEFAULT_STRONGPASSWORD);
		STRONGLENGTH = myGetProperty("STRONGLENGTH", DEFAULT_STRONGLENGTH);
		// set same reasonable minimum and ensure it isn't negative
		if (STRONGLENGTH < 6) {
			STRONGLENGTH = 6;
		}
		PATIENTPHOTOSTORAGE = myGetProperty("PATIENTPHOTOSTORAGE", DEFAULT_PATIENTPHOTOSTORAGE);
		SESSIONTIMEOUT = myGetProperty("SESSIONTIMEOUT", DEFAULT_SESSIONTIMEOUT);
}

	public static GeneralData getGeneralData() {
		if (mySingleData == null) {
			initialize();
		}
		return mySingleData;
	}

	
	public static void initialize() {
		mySingleData = new GeneralData(FILE_PROPERTIES);
	}


	/**
	 * @return the SINGLEUSER
	 */
	public boolean getSINGLEUSER() {
		return SINGLEUSER;
	}
	
	
	/**
	 * @return the USERSLISTLOGIN
	 */
	public boolean getUSERSLISTLOGIN() {
		return USERSLISTLOGIN;
	}
}
