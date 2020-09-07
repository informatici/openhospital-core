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
package org.isf.sms.providers;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Mwithi
 */
public class SkebbyParameters {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final String FILE_PROPERTIES = "Skebby.properties";

	public static String URL;
    private static String DEFAULT_URL = "";
    
    public static String USR;
    private static String DEFAULT_USR = "";
    
    public static String PWD;
    private static String DEFAULT_PWD = "";
    
    public static String TYPE;
    private static String DEFAULT_TYPE = "send_sms_basic";
    
    public static String SENDER_NUMBER;
    private static String DEFAULT_SENDER_NUMBER = "";
    
    public static String SENDER_STRING;
    private static String DEFAULT_SENDER_STRING = "";
    
    private static SkebbyParameters mySingleData;
	private Properties p;

    private SkebbyParameters() {
    	try	{
			p = new Properties();
			p.load(new FileInputStream("rsc" + File.separator + "SmsGateway" + File.separator + FILE_PROPERTIES));
			//logger.info("File " + FILE_PROPERTIES + " loaded. ");
			URL = myGetProperty("URL", DEFAULT_URL);
			USR = myGetProperty("USR", DEFAULT_USR);
			PWD = myGetProperty("PWD", DEFAULT_PWD);
			TYPE = myGetProperty("TYPE", DEFAULT_TYPE);
			SENDER_NUMBER = myGetProperty("SENDER_NUMBER", DEFAULT_SENDER_NUMBER);
			SENDER_STRING = myGetProperty("SENDER_STRING", DEFAULT_SENDER_STRING);
			
    	} catch (Exception e) {//no file
    		logger.error(">> " + FILE_PROPERTIES + " file not found.");
    		System.exit(1);
		}
    }
    
    public static SkebbyParameters getSkebbyParameters() {
        if (mySingleData == null){ 
        	mySingleData = new SkebbyParameters();        	
        }
        return mySingleData;
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
}

