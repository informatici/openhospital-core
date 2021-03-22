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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ConfigurationProperties {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private Properties prop;
	
	protected ConfigurationProperties(String fileProperties, boolean exitOnFail) {
		this.prop = loadPropertiesFile(fileProperties, logger, exitOnFail);
	}
	
	public static Properties loadPropertiesFile(String fileProperties, Logger logger) {
		return loadPropertiesFile(fileProperties, logger, false);
	}

	private static Properties loadPropertiesFile(String fileProperties, Logger logger, boolean exitOnFail) {
		Properties prop = new Properties();
		InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileProperties);
		try {
			prop.load(in);
			logger.info("File {} loaded.", fileProperties);
			in.close();
		} catch (IOException e) {
			logger.error(">> {} file not found.", fileProperties);
			if (exitOnFail) System.exit(1);
		}
		return prop;
	}
	
	/**
	 * Method to retrieve a property
	 * 
	 * @param property
	 * @return
	 */
	protected String myGetProperty(String property) {
		return prop.getProperty(property);
	}
	
	/**
     * Method to retrieve an integer property
     * 
     * @param property
     * @param defaultValue
     * @return
     */
	protected int myGetProperty(String property, int defaultValue) {
    	int value;
		try {
			value = Integer.parseInt(prop.getProperty(property));
		} catch (Exception e) {
			logger.warn("{} property not found: default is {}", property, defaultValue);
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
	protected boolean myGetProperty(String property, boolean defaultValue) {
		boolean value;
		try {
			value = prop.getProperty(property).equalsIgnoreCase("YES");
		} catch (Exception e) {
			return defaultValue;
		}
		return value;
	}
	
	/**
     * Method to retrieve an double property
     * 
     * @param property
     * @param defaultValue
     * @return
     */
	protected double myGetProperty(String property, double defaultValue) {
    	double value;
		try {
			value = Double.parseDouble(prop.getProperty(property));
		} catch (Exception e) {
			logger.warn("{} property not found: default is {}", property, defaultValue);
			return defaultValue;
		}
		return value;
	}
	
	/**
	 * Method to retrieve a string property
	 * 
	 * @param property
	 * @param defaultValue
	 * @return
	 */
	protected String myGetProperty(String property, String defaultValue) {
		String value;
		value = prop.getProperty(property);
		if (value == null) {
			logger.warn(">> {} property not found: default is {}", property, defaultValue);
			return defaultValue;
		}
		return value;
	}
}