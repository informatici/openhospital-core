/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2024 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.isf.generaldata;

/**
 * @author Silevester D.
 * @since 1.15
 */
public final class OrthancConfig extends ConfigurationProperties {

	private static final String FILE_PROPERTIES = "orthanc.properties";

	/**
	 * Enable ORTHANC integration
	 */
	public static boolean ORTHANC_ENABLED;

	/**
	 * Base URL of the ORTHANC server
	 */
	public static String ORTHANC_BASE_URL;

	/**
	 * Name of the user to use for ORTHANC APIs calls
	 */
	public static String ORTHANC_USERNAME;

	/**
	 * Password of the user to use for ORTHANC APIs calls
	 */
	public static String ORTHANC_PASSWORD;

	/**
	 * Full URL of the ORTHANC explorer
	 */
	public static String ORTHANC_EXPLORER_URL;

	private static final boolean DEFAULT_ORTHANC_ENABLED = false;
	private static final String DEFAULT_ORTHANC_BASE_URL = "";
	private static final String DEFAULT_ORTHANC_USERNAME = "admin";
	private static final String DEFAULT_ORTHANC_PASSWORD = "adminADMIN!123#";
	private static final String DEFAULT_ORTHANC_EXPLORER_URL = "";

	private OrthancConfig(String fileProperties) {
		super(fileProperties);
		ORTHANC_ENABLED = myGetProperty("orthanc.enabled", DEFAULT_ORTHANC_ENABLED);
		ORTHANC_BASE_URL = myGetProperty("orthanc.base-url", DEFAULT_ORTHANC_BASE_URL);
		ORTHANC_USERNAME = myGetProperty("orthanc.username", DEFAULT_ORTHANC_USERNAME);
		ORTHANC_PASSWORD = myGetProperty("orthanc.password", DEFAULT_ORTHANC_PASSWORD);
		ORTHANC_EXPLORER_URL = myGetProperty("orthanc.explorer-url", DEFAULT_ORTHANC_EXPLORER_URL);
	}

	public static void initialize() {
		new OrthancConfig(FILE_PROPERTIES);
	}
}
