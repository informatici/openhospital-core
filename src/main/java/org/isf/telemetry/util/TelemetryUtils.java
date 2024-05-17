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
package org.isf.telemetry.util;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.isf.telemetry.envdatacollector.DataCollectorProviderService;
import org.isf.telemetry.manager.TelemetryManager;
import org.isf.telemetry.service.remote.TelemetryDataCollectorGatewayService;
import org.isf.utils.exception.OHException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

@Component
public class TelemetryUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(TelemetryUtils.class);

	private static final boolean IGNORE_EXCEPTIONS = true;

	private DataCollectorProviderService dataCollectorProvider;

	private TelemetryDataCollectorGatewayService telemetryGatewayService;

	private TelemetryManager telemetryManager;

	public TelemetryUtils(DataCollectorProviderService dataCollectorProvider, TelemetryDataCollectorGatewayService telemetryGatewayService,
	                      TelemetryManager telemetryManager) {
		this.dataCollectorProvider = dataCollectorProvider;
		this.telemetryGatewayService = telemetryGatewayService;
		this.telemetryManager = telemetryManager;
	}

	public Map<String, Map<String, String>> retrieveDataToSend(Map<String, Boolean> consentMap) throws OHException {
		List<String> enabledCollectors = consentMap.keySet().stream()
						.filter(key -> Boolean.TRUE.equals(consentMap.get(key))).map(item -> item).collect(Collectors.toList());
		return this.dataCollectorProvider.collectData(enabledCollectors, IGNORE_EXCEPTIONS);
	}

	public void sendTelemetryData(Map<String, Map<String, String>> dataToSend, boolean isSimulation)
					throws OHException {
		boolean sent = true;
		String jsonToSend = (new Gson()).toJson(dataToSend);

		if (!isSimulation) {
			LOGGER.debug("Data to send: {}", jsonToSend);
			sent = this.telemetryGatewayService.send(jsonToSend);
		}
		if (sent) {
			this.telemetryManager.updateStatusSuccess(jsonToSend);
			LOGGER.debug("Data sent: {}", jsonToSend);
		} else {
			this.telemetryManager.updateStatusFail((new Gson()).toJson(dataToSend));
			LOGGER.error("Something strange happened while trying to send data.");
		}
	}

}
