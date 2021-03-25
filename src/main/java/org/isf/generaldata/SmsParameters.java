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

public class SmsParameters extends ConfigurationProperties {
	
	private static final String FILE_PROPERTIES = "sms.properties";

	public static String MODE;
	private static final String DEFAULT_MODE = "GSM";
	
	public static String GATEWAY;
	private static final String DEFAULT_GATEWAY = "";
	
	public static int TIMEOUT;
	private static final int DEFAULT_TIMEOUT = 3000;
	
    public static int LOOP;
    private static final int DEFAULT_LOOP = 300;
    
    public static String ICC;
    private static final String DEFAULT_ICC = "";
    
    private static SmsParameters mySingleData;
    
    private SmsParameters(String fileProperties) {
    	super(fileProperties);
			
		MODE = myGetProperty("MODE", DEFAULT_MODE);
		GATEWAY = myGetProperty("GATEWAY", DEFAULT_GATEWAY);
		TIMEOUT = myGetProperty("TIMEOUT", DEFAULT_TIMEOUT);
		LOOP = myGetProperty("LOOP", DEFAULT_LOOP);
		ICC = myGetProperty("ICC", DEFAULT_ICC);
			
    }
    
    public static SmsParameters getSmsParameters() {
        if (mySingleData == null){ 
        	initialize();        	
        }
        return mySingleData;
    }
    
    public static void initialize() {
    	new SmsParameters(FILE_PROPERTIES);        	
    }
}
