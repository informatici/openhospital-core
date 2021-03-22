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

import java.util.Properties;

import org.isf.generaldata.ConfigurationProperties;

/**
 * @author Mwithi
 */
public class SkebbyParameters extends ConfigurationProperties {
	
	private static final String FILE_PROPERTIES = "Skebby.properties";
	private final static boolean EXIT_ON_FAIL = false;

	public static String URL;
    private static final String DEFAULT_URL = "";
    
    public static String USR;
    private static final String DEFAULT_USR = "";
    
    public static String PWD;
    private static final String DEFAULT_PWD = "";
    
    public static String TYPE;
    private static final String DEFAULT_TYPE = "send_sms_basic";
    
    public static String SENDER_NUMBER;
    private static final String DEFAULT_SENDER_NUMBER = "";
    
    public static String SENDER_STRING;
    private static final String DEFAULT_SENDER_STRING = "";
    
    private static SkebbyParameters mySingleData;
	private Properties p;

    private SkebbyParameters(String fileProperties, boolean exitOnFail) {
    	super(fileProperties, exitOnFail);
			
		URL = myGetProperty("URL", DEFAULT_URL);
		USR = myGetProperty("USR", DEFAULT_USR);
		PWD = myGetProperty("PWD", DEFAULT_PWD);
		TYPE = myGetProperty("TYPE", DEFAULT_TYPE);
		SENDER_NUMBER = myGetProperty("SENDER_NUMBER", DEFAULT_SENDER_NUMBER);
		SENDER_STRING = myGetProperty("SENDER_STRING", DEFAULT_SENDER_STRING);
			
    }
    
    public static SkebbyParameters getSkebbyParameters() {
        if (mySingleData == null){ 
        	mySingleData = new SkebbyParameters(FILE_PROPERTIES, EXIT_ON_FAIL);        	
        }
        return mySingleData;
    }
    
}
