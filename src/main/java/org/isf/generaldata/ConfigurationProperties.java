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
	 * @param defaultValue
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