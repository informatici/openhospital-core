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
package org.isf.telemetry.envdatacollector.collectors;

import java.util.LinkedHashMap;
import java.util.Map;

import org.isf.telemetry.envdatacollector.AbstractDataCollector;
import org.isf.telemetry.envdatacollector.constants.CollectorsConstants;
import org.isf.telemetry.manager.TelemetryManager;
import org.isf.telemetry.model.Telemetry;
import org.isf.utils.exception.OHException;
import org.isf.utils.time.TimeTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(value = 1)
@Component
public class TelemetryDataCollector extends AbstractDataCollector {

	private static final String ID = "TEL_ID";
	private static final Logger LOGGER = LoggerFactory.getLogger(TelemetryDataCollector.class);

	private TelemetryManager telemetryManager;

	public TelemetryDataCollector(TelemetryManager telemetryManager) {
		this.telemetryManager = telemetryManager;
	}

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public String getDescription() {
		return "Telemetry Unique ID (this instance)";
	}

	@Override
	public Map<String, String> retrieveData() throws OHException {
		LOGGER.debug("Collecting Telemetry data...");
		Map<String, String> result = new LinkedHashMap<>();
		try {
			Telemetry telemetry = telemetryManager.retrieveOrBuildNewTelemetry();
			result.put(CollectorsConstants.TEL_UUID, telemetry.getId().getSoftwareUUID());
			if (null != telemetry.getSentTimestamp()) {
				result.put(CollectorsConstants.TEL_SENT_DATE, TimeTools.formatDateTimeReport(telemetry.getSentTimestamp()));
			}
			if (null != telemetry.getOptinDate()) {
				result.put(CollectorsConstants.TEL_OPTIN_DATE, TimeTools.formatDateTimeReport(telemetry.getOptinDate()));
			}
			if (null != telemetry.getOptoutDate()) {
				result.put(CollectorsConstants.TEL_OPTOUT_DATE, TimeTools.formatDateTimeReport(telemetry.getOptoutDate()));
			}
		} catch (RuntimeException e) {
			LOGGER.error("Something went wrong with " + ID);
			LOGGER.error(e.toString());
			throw new OHException("Data collector [" + ID + ']', e);
		}
		return result;
	}

}
