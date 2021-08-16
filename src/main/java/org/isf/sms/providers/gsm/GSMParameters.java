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
package org.isf.sms.providers.gsm;

import org.isf.generaldata.ConfigurationProperties;

public class GSMParameters extends ConfigurationProperties {

	public static final String FILE_PROPERTIES = "sms.properties";

	public static String PORT;
	private static final String DEFAULT_PORT = "";

	public static String DRIVERNAME;
	private static final String DEFAULT_DRIVERNAME = "com.sun.comm.Win32Driver";

	public static String CMGF;
	private static final String DEFAULT_CMGF = "AT+CMGF=1\r";

	public static String CSMP;
	private static final String DEFAULT_CSMP = "AT+CSMP=17,167,0,0\r";
	
	public static String GMM;
	private static final String DEFAULT_GMM = "AT+GMM\r\n";

	public static String CMGS;
	private static final String DEFAULT_CMGS = "AT+CMGS=\"";

	private static GSMParameters mySingleData;

	private GSMParameters(String fileProperties) {
		super(fileProperties);

		PORT = myGetProperty(GSMGatewayService.SERVICE_NAME + ".port", DEFAULT_PORT);
		DRIVERNAME = myGetProperty(GSMGatewayService.SERVICE_NAME + ".driver-name", DEFAULT_DRIVERNAME);
		CMGF = myGetProperty(GSMGatewayService.SERVICE_NAME + ".cmgf", DEFAULT_CMGF);
		CSMP = myGetProperty(GSMGatewayService.SERVICE_NAME + ".csmp", DEFAULT_CSMP);
		GMM = myGetProperty(GSMGatewayService.SERVICE_NAME + ".gmm", DEFAULT_GMM);
		CMGS = myGetProperty(GSMGatewayService.SERVICE_NAME + ".cmgs", DEFAULT_CMGS);

	}

	public static GSMParameters getGSMParameters() {
		if (mySingleData == null) {
			mySingleData = new GSMParameters(FILE_PROPERTIES);
		}
		return mySingleData;
	}

}