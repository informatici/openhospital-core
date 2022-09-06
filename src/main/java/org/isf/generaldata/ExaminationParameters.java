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
package org.isf.generaldata;

/**
 * Settings for Examination form
 *
 * @author Mwithi
 */
public final class ExaminationParameters extends ConfigurationProperties {
	
	private static final String FILE_PROPERTIES = "examination.properties";
	
	public static int HEIGHT_MIN;
	private static int DEFAULT_HEIGHT_MIN = 0;

	public static int HEIGHT_MAX;
	private static final int DEFAULT_HEIGHT_MAX = 250;
	
	public static int HEIGHT_INIT;
	private static final int DEFAULT_HEIGHT_INIT = 170;
	
	public static int WEIGHT_MIN;
	private static final int DEFAULT_WEIGHT_MIN = 0;
	
	public static int WEIGHT_MAX;
	private static final int DEFAULT_WEIGHT_MAX = 400;
	
	public static double WEIGHT_STEP;
	private static final double DEFAULT_WEIGHT_STEP = 0.1;
	
	public static int WEIGHT_INIT;
	private static int DEFAULT_WEIGHT_INIT = 80;
	
	public static int AP_MIN_INIT;
	private static final int DEFAULT_AP_MIN_INIT = 80;
	
	public static int AP_MAX_INIT;
	private static final int DEFAULT_AP_MAX_INIT = 120;
	
	public static int HR_MIN;
	private static final int DEFAULT_HR_MIN = 0;
	
	public static int HR_MAX;
	private static final int DEFAULT_HR_MAX = 240;
	
	public static int HR_INIT;
	private static final int DEFAULT_HR_INIT = 60;
	
	public static int RR_MIN;
	private static final int DEFAULT_RR_MIN = 0;
	
	public static int RR_MAX;
	private static final int DEFAULT_RR_MAX = 100;
	
	public static int RR_INIT;
	private static final int DEFAULT_RR_INIT = 15;

	public static String AUSCULTATION_INIT = "normal";
	
	public static String DIURESIS_DESC_INIT = "physiological";
	
	public static String BOWEL_DESC_INIT = "regular";
	
	public static int TEMP_MIN;
	private static final int DEFAULT_TEMP_MIN = 0;
	
	public static int TEMP_MAX;
	private static final int DEFAULT_TEMP_MAX = 50;
	
	public static int TEMP_INIT;
	private static final int DEFAULT_TEMP_INIT = 36;
	
	public static Double TEMP_STEP;
	private static final Double DEFAULT_TEMP_STEP = 0.1;
	
	public static int SAT_MIN;
	private static final int DEFAULT_SAT_MIN = 50;

	public static int SAT_INIT;
	private static final int DEFAULT_SAT_INIT = 90;
	
	public static double SAT_STEP;
	private static final double DEFAULT_SAT_STEP = 0.1;
	
	public static int HGT_MIN;
	private static final int DEFAULT_HGT_MIN = 30;
	
	public static int HGT_MAX;
	private static final int DEFAULT_HGT_MAX = 600;
	
	public static int HGT_INIT;
	private static final int DEFAULT_HGT_INIT = 80;
	
	public static int DIURESIS_MIN;
	private static final int DEFAULT_DIURESIS_MIN = 0;
	
	public static int DIURESIS_MAX;
	private static final int DEFAULT_DIURESIS_MAX = 2500;
	
	public static int DIURESIS_INIT;
	private static final int DEFAULT_DIURESIS_INIT = 100;
	
	public static int LIST_SIZE;
	private static final int DEFAULT_LIST_SIZE = 4;
	
	private static ExaminationParameters mySingleData;

	private ExaminationParameters(String fileProperties) {
		super(fileProperties);
			
		HEIGHT_MIN = myGetProperty("HEIGHT_MIN", DEFAULT_HEIGHT_MIN);
		HEIGHT_MAX = myGetProperty("HEIGHT_MAX", DEFAULT_HEIGHT_MAX);
		HEIGHT_INIT = myGetProperty("HEIGHT_INIT", DEFAULT_HEIGHT_INIT);
		
		WEIGHT_MIN = myGetProperty("WEIGHT_MIN", DEFAULT_WEIGHT_MIN);
		WEIGHT_MAX = myGetProperty("WEIGHT_MAX", DEFAULT_WEIGHT_MAX);
		WEIGHT_STEP = myGetProperty("WEIGHT_STEP", DEFAULT_WEIGHT_STEP);
		WEIGHT_INIT = myGetProperty("WEIGHT_INIT", DEFAULT_WEIGHT_INIT);
		
		AP_MIN_INIT = myGetProperty("AP_MIN_INIT", DEFAULT_AP_MIN_INIT);
		AP_MAX_INIT = myGetProperty("AP_MAX_INIT", DEFAULT_AP_MAX_INIT);
		
		HR_MIN = myGetProperty("HR_MIN", DEFAULT_HR_MIN);
		HR_MAX = myGetProperty("HR_MAX", DEFAULT_HR_MAX);
		HR_INIT = myGetProperty("HR_INIT", DEFAULT_HR_INIT);
		
		TEMP_MIN = myGetProperty("TEMP_MIN", DEFAULT_TEMP_MIN);
		TEMP_MAX = myGetProperty("TEMP_MAX", DEFAULT_TEMP_MAX);
		TEMP_INIT = myGetProperty("TEMP_INIT", DEFAULT_TEMP_INIT);
		TEMP_STEP = myGetProperty("TEMP_STEP", DEFAULT_TEMP_STEP);

		SAT_MIN = myGetProperty("SAT_MIN", DEFAULT_SAT_MIN);
		SAT_INIT = myGetProperty("SAT_INIT", DEFAULT_SAT_INIT);
		SAT_STEP = myGetProperty("SAT_STEP", DEFAULT_SAT_STEP);
		
		HGT_MIN = myGetProperty("HGT_MIN", DEFAULT_HGT_MIN);
		HGT_MAX = myGetProperty("HGT_MAX", DEFAULT_HGT_MAX);
		HGT_INIT = myGetProperty("HGT_INIT", DEFAULT_HGT_INIT);
		
		DIURESIS_MIN = myGetProperty("DIURESIS_MIN", DEFAULT_DIURESIS_MIN);
		DIURESIS_MAX = myGetProperty("DIURESIS_MAX", DEFAULT_DIURESIS_MAX);
		DIURESIS_INIT = myGetProperty("DIURESIS_INIT", DEFAULT_DIURESIS_INIT);

		RR_MIN = myGetProperty("RR_MIN", DEFAULT_RR_MIN);
		RR_MAX = myGetProperty("RR_MAX", DEFAULT_RR_MAX);
		RR_INIT = myGetProperty("RR_INIT", DEFAULT_RR_INIT);

		LIST_SIZE = myGetProperty("LIST_SIZE", DEFAULT_LIST_SIZE);
	}

	public static ExaminationParameters getExaminationParameters() {
		if (mySingleData == null) {
			initialize();
		}
		return mySingleData;
	}

	public static void initialize() {
		mySingleData = new ExaminationParameters(FILE_PROPERTIES);
	}

}
