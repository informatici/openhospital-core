/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2022 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.telemetry.envdatacollector.collectors;

import java.util.LinkedHashMap;
import java.util.Map;

import org.isf.telemetry.envdatacollector.AbstractDataCollector;
import org.isf.telemetry.envdatacollector.constants.CollectorsConst;
import org.isf.telemetry.manager.TelemetryManager;
import org.isf.utils.exception.OHException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import oshi.SystemInfo;

@Order(value = 10)
@Component
public class IdentifierDataCollector extends AbstractDataCollector {

	private static final String ID = "TEL_UUID";
	private static final Logger LOGGER = LoggerFactory.getLogger(IdentifierDataCollector.class);

	@Autowired
	private TelemetryManager telemetryManager;

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public String getDescription() {
		return "Identifier (ex. Installation ID: 2e961809-fe0e-4860-82a2-3bd185f9bb6e)";
	}

	@Override
	public Map<String, String> retrieveData() throws OHException {
		LOGGER.debug("Collecting Hardware data...");
		Map<String, String> result = new LinkedHashMap<>();
		try {
			SystemInfo si = new SystemInfo();
			result.put(CollectorsConst.TEL_UUID, telemetryManager.retrieveSettings().getId().getSoftwareUUID());
		} catch (RuntimeException e) {
			LOGGER.error("Something went wrong with " + ID);
			LOGGER.error(e.toString());
			throw new OHException("Data collector [" + ID + "]", e);
		}
		return result;
	}

}
