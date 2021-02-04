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

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;

public class PropertyReader {

	private Logger logger;

	private static Map<Class, PropertyExtractor> typePropertyExtractorMap = new HashMap<Class, PropertyExtractor>();

	public PropertyReader(Map properties, Logger logger) {
		this.logger = logger;

		typePropertyExtractorMap.put(String.class, new StringPropertyExtractor(
				properties));
		typePropertyExtractorMap.put(int.class, new IntPropertyExtractor(
				properties));
		typePropertyExtractorMap.put(boolean.class, new BooleanPropertyExtractor(
				properties));
	}

	/**
	 * Method to retrieve a property
	 * 
	 * @param propertyName
	 * @param defaultValue
	 * @return
	 */
	public <T> T readProperty(String propertyName, T defaultValue) {
		T value;

		try {
			value = (T) typePropertyExtractorMap.get(defaultValue.getClass())
					.getProperty(propertyName);
		} catch (Exception e) {
			this.logger.warn(">> {} property not found: default is {}", propertyName, defaultValue);
			return defaultValue;
		}
		
		return value;
	}
	
	interface PropertyExtractor<T> {
		T getProperty(String propertyName);
	}

	class StringPropertyExtractor implements PropertyExtractor<String> {
		private Map properties;

		public StringPropertyExtractor(Map properties) {
			this.properties = properties;
		}

		@Override
		public String getProperty(String propertyName) {
			return this.properties.get(propertyName).toString();
		}
	}

	class IntPropertyExtractor implements PropertyExtractor<Integer> {
		private Map properties;

		public IntPropertyExtractor(Map properties) {
			this.properties = properties;
		}

		@Override
		public Integer getProperty(String propertyName) {
			return (Integer)(this.properties.get(propertyName));
		}
	}

	class BooleanPropertyExtractor implements PropertyExtractor<Boolean> {
		private Map properties;

		public BooleanPropertyExtractor(Map properties) {
			this.properties = properties;
		}

		@Override
		public Boolean getProperty(String propertyName) {
			return this.properties.get(propertyName).toString().equalsIgnoreCase("YES");
		}
	}
}
