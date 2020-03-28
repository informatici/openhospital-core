package org.isf.generaldata;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Mwithi
 * 
 * settings for Examination form
 *
 */
public class ExaminationParameters {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public static int HEIGHT_MIN;
	private static int DEFAULT_HEIGHT_MIN = 0;
	
	public static int HEIGHT_MAX;
	private static int DEFAULT_HEIGHT_MAX = 250;
	
	public static int HEIGHT_INIT;
	private static int DEFAULT_HEIGHT_INIT = 170;
	
//	public static String HEIGHT_UNIT;
//	private static String DEFAULT_HEIGHT_UNIT = "cm";
	
//	public static double HEIGHT_STEP;
//	private static double DEFAULT_HEIGHT_STEP = 1;
	
//	public static String WEIGHT_UNIT;
//	private static String DEFAULT_WEIGHT_UNIT = "Kg";
	
	public static int WEIGHT_MIN;
	private static int DEFAULT_WEIGHT_MIN = 0;
	
	public static int WEIGHT_MAX;
	private static int DEFAULT_WEIGHT_MAX = 400;
	
	public static double WEIGHT_STEP;
	private static double DEFAULT_WEIGHT_STEP = 0.1;
	
	public static int WEIGHT_INIT;
	private static int DEFAULT_WEIGHT_INIT = 80;
	
//	public static String BP_UNIT;
//	private static String DEFAULT_BP_UNIT = "mmHg";
	
	public static int AP_MIN_INIT;
	private static int DEFAULT_AP_MIN_INIT = 80;
	
	public static int AP_MAX_INIT;
	private static int DEFAULT_AP_MAX_INIT = 120;
	
//	public static String HR_UNIT;
//	private static String DEFAULT_HR_UNIT = "bpm";
	
	public static int HR_MIN;
	private static int DEFAULT_HR_MIN = 0;
	
	public static int HR_MAX;
	private static int DEFAULT_HR_MAX = 240;
	
	public static int HR_INIT;
	private static int DEFAULT_HR_INIT = 60;
	
//	public static String TEMP_UNIT;
//	private static String DEFAULT_TEMP_UNIT = "°C";
	
	public static int TEMP_MIN;
	private static int DEFAULT_TEMP_MIN = 0;
	
	public static int TEMP_MAX;
	private static int DEFAULT_TEMP_MAX = 50;
	
	public static int TEMP_INIT;
	private static int DEFAULT_TEMP_INIT = 36;
	
	public static Double TEMP_STEP;
	private static Double DEFAULT_TEMP_STEP = 0.1;
	
//	public static String SAT_UNIT;
//	private static String DEFAULT_SAT_UNIT = "%";
	
	public static int SAT_MIN;
	private static int DEFAULT_SAT_MIN = 50;
	
	//deprecated
	//public static int SAT_MAX;
	//private static int DEFAULT_SAT_MAX = 100;
	
	public static int SAT_INIT;
	private static int DEFAULT_SAT_INIT = 90;
	
	public static double SAT_STEP;
	private static double DEFAULT_SAT_STEP = 0.1;
	
	public static int HGT_INIT;
	private static int DEFAULT_HGT_INIT = 0;
	
	public static int DIURESIS_INIT;
	private static int DEFAULT_DIURESIS_INIT = 0;
	
	public static int LIST_SIZE;
	private static int DEFAULT_LIST_SIZE = 4;
	
	private static ExaminationParameters mySingleData;
	private Properties p;

	private ExaminationParameters() {
		try {
			p = new Properties();
			p.load(new FileInputStream("rsc" + File.separator + "examination.properties"));
			HEIGHT_MIN = myGetProperty("HEIGHT_MIN", DEFAULT_HEIGHT_MIN);
			HEIGHT_MAX = myGetProperty("HEIGHT_MAX", DEFAULT_HEIGHT_MAX);
			HEIGHT_INIT = myGetProperty("HEIGHT_INIT", DEFAULT_HEIGHT_INIT);
//			HEIGHT_STEP = myGetProperty("HEIGHT_STEP", DEFAULT_HEIGHT_STEP);
//			HEIGHT_UNIT = myGetProperty("HEIGHT_UNIT", DEFAULT_HEIGHT_UNIT);
			
			WEIGHT_MIN = myGetProperty("WEIGHT_MIN", DEFAULT_WEIGHT_MIN);
			WEIGHT_MAX = myGetProperty("WEIGHT_MAX", DEFAULT_WEIGHT_MAX);
			WEIGHT_STEP = myGetProperty("WEIGHT_STEP", DEFAULT_WEIGHT_STEP);
			WEIGHT_INIT = myGetProperty("WEIGHT_INIT", DEFAULT_WEIGHT_INIT);
//			WEIGHT_UNIT = myGetProperty("WEIGHT_UNIT", DEFAULT_WEIGHT_UNIT);
			
			AP_MIN_INIT = myGetProperty("AP_MIN_INIT", DEFAULT_AP_MIN_INIT);
			AP_MAX_INIT = myGetProperty("AP_MAX_INIT", DEFAULT_AP_MAX_INIT);
//			BP_UNIT = myGetProperty("BP_UNIT", DEFAULT_BP_UNIT);
			
			HR_MIN = myGetProperty("HR_MIN", DEFAULT_HR_MIN);
			HR_MAX = myGetProperty("HR_MAX", DEFAULT_HR_MAX);
			HR_INIT = myGetProperty("HR_INIT", DEFAULT_HR_INIT);
//			HR_UNIT = myGetProperty("HR_UNIT", DEFAULT_HR_UNIT);
			
			TEMP_MIN = myGetProperty("TEMP_MIN", DEFAULT_TEMP_MIN);
			TEMP_MAX = myGetProperty("TEMP_MAX", DEFAULT_TEMP_MAX);
			TEMP_INIT = myGetProperty("TEMP_INIT", DEFAULT_TEMP_INIT);
			TEMP_STEP = myGetProperty("TEMP_STEP", DEFAULT_TEMP_STEP);
//			TEMP_UNIT = myGetProperty("TEMP_UNIT", DEFAULT_TEMP_UNIT);
			
			SAT_MIN = myGetProperty("SAT_MIN", DEFAULT_SAT_MIN);
			//SAT_MAX = myGetProperty("SAT_MAX", DEFAULT_SAT_MAX);
			SAT_INIT = myGetProperty("SAT_INIT", DEFAULT_SAT_INIT);
			SAT_STEP = myGetProperty("SAT_STEP", DEFAULT_SAT_STEP);
//			SAT_UNIT = myGetProperty("SAT_UNIT", DEFAULT_SAT_UNIT);
			
			HGT_INIT = myGetProperty("HGT_INIT", DEFAULT_HGT_INIT);
			DIURESIS_INIT = myGetProperty("DIURESIS_INIT", DEFAULT_DIURESIS_INIT);
			
			LIST_SIZE = myGetProperty("LIST_SIZE", DEFAULT_LIST_SIZE);
			
		} catch (Exception e) { //no file
			logger.warn("examination.properties file not found.");
		}
		//MessageBundle.initialize();
	}

	/**
     * Method to retrieve an integer property
     * 
     * @param property
     * @param defaultValue
     * @return
     */
    private int myGetProperty(String property, int defaultValue) {
    	int value;
		try {
			value = Integer.parseInt(p.getProperty(property));
		} catch (Exception e) {
			logger.warn(property + " property not found: default is " + defaultValue);
			return defaultValue;
		}
		return value;
	}
    
    /**
     * Method to retrieve an integer property
     * 
     * @param property
     * @param defaultValue
     * @return
     */
    private double myGetProperty(String property, double defaultValue) {
    	double value;
		try {
			value = Double.parseDouble(p.getProperty(property));
		} catch (Exception e) {
			logger.warn(property + " property not found: default is " + defaultValue);
			return defaultValue;
		}
		return value;
	}
    
    public static ExaminationParameters getExaminationParameters() {
        if (mySingleData == null){ 
        	mySingleData = new ExaminationParameters();        	
        }
        return mySingleData;
    }
    
    
}

