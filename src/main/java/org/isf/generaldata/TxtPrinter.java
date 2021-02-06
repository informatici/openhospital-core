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

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TxtPrinter {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final String FILE_PROPERTIES = "txtPrinter.properties";

	public static boolean USE_DEFAULT_PRINTER;
	public static boolean PRINT_AS_PAID;
    public static boolean PRINT_WITHOUT_ASK;
    public static int TXT_CHAR_HEIGHT;
    public static int TXT_CHAR_WIDTH;
    public static String MODE;
    public static String ZPL_FONT_TYPE;
    public static int ZPL_ROW_HEIGHT;
    public static String PRINTER;
    
    private static boolean DEFAULT_USE_DEFAULT_PRINTER = true;
    private static boolean DEFAULT_PRINT_AS_PAID = false;
    private static boolean DEFAULT_PRINT_WITHOUT_ASK = false;
    private static int DEFAULT_TXT_CHAR_HEIGHT = 10;
    private static int DEFAULT_TXT_CHAR_WIDTH = 10;
    private static String DEFAULT_MODE = "PDF";
    private static String DEFAULT_ZPL_FONT_TYPE = "A";
    private static int DEFAULT_ZPL_ROW_HEIGHT = 9;
    
    private static TxtPrinter mySingleData;
	private Properties p;

    private TxtPrinter() {
    	try	{
			p = new Properties();
			p.load(new FileInputStream("rsc" + File.separator + FILE_PROPERTIES));
			logger.info("File txtPrinter.properties loaded. ");
			USE_DEFAULT_PRINTER = myGetProperty("USE_DEFAULT_PRINTER", DEFAULT_USE_DEFAULT_PRINTER);
			if (!USE_DEFAULT_PRINTER) PRINTER = p.getProperty("USE_DEFAULT_PRINTER"); 
			PRINT_AS_PAID = myGetProperty("PRINT_AS_PAID", DEFAULT_PRINT_AS_PAID);
			PRINT_WITHOUT_ASK = myGetProperty("PRINT_WITHOUT_ASK", DEFAULT_PRINT_WITHOUT_ASK);
			TXT_CHAR_HEIGHT = myGetProperty("TXT_CHAR_HEIGHT", DEFAULT_TXT_CHAR_HEIGHT);
			TXT_CHAR_WIDTH = myGetProperty("TXT_CHAR_WIDTH", DEFAULT_TXT_CHAR_WIDTH);
			MODE = p.getProperty("MODE", DEFAULT_MODE);
			ZPL_FONT_TYPE = myGetProperty("ZPL_FONT_TYPE", DEFAULT_ZPL_FONT_TYPE);
			ZPL_ROW_HEIGHT = myGetProperty("ZPL_ROW_HEIGHT", DEFAULT_ZPL_ROW_HEIGHT);
			
    	} catch (Exception e) {//no file
    		logger.error(">> " + FILE_PROPERTIES + " file not found.");
			System.exit(1);
		}
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
			logger.warn(">> {} property not found: default is {}", property, defaultValue);
			return defaultValue;
		}
		return value;
	}

	/**
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

    public static TxtPrinter getTxtPrinter() {
        if (mySingleData == null){ 
        	mySingleData = new TxtPrinter();        	
        }
        return mySingleData;
    }
    
    /**
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

}
