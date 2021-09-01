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
package org.isf.envdatacollector.collectors;

import java.util.HashMap;
import java.util.Map;

import org.isf.envdatacollector.AbstractDataCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(value = 60)
@Component
public class LocationDataCollector extends AbstractDataCollector {

	private static final String ID = "FUN_LOCATION";
	private static final Logger LOGGER = LoggerFactory.getLogger(LocationDataCollector.class);

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public String getDescription() {
		return "Location information (ex. Italy)";
	}

	@Override
	public Map<String, String> retrieveData() {
		LOGGER.debug("Collecting location data...");
		Map<String, String> result = new HashMap<>();
		result.put("sample", "This is a sample message from unit called " + ID);
		// TODO 
		return result;
	}

}
