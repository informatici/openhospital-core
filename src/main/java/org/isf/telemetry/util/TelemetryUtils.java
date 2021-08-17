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
package org.isf.telemetry.util;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.isf.envdatacollector.DataCollectorProviderService;
import org.isf.telemetry.manager.TelemetryManager;
import org.isf.telemetry.service.remote.TelemetryDataCollectorGatewayService;
import org.isf.utils.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

@Component
public class TelemetryUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(TelemetryUtils.class);

	@Autowired
	private DataCollectorProviderService dataCollectorProvider;

	@Autowired
	private TelemetryDataCollectorGatewayService telemetryGatewayService;

	@Autowired
	private TelemetryManager telemetryManager;

	public Map<String, Map<String, String>> retrieveDataToSend(Map<String, Boolean> consentMap) {
		List<String> enabledCollectors = consentMap.keySet().stream().filter(key -> BooleanUtils.isTrue(consentMap.get(key))).map(item -> item)
						.collect(Collectors.toList());
		return this.dataCollectorProvider.collectData(enabledCollectors);
	}

	public void sendTelemetryData(Map<String, Boolean> consentMap) {
		Map<String, Map<String, String>> info = this.retrieveDataToSend(consentMap);
		boolean sent = this.telemetryGatewayService.send(info);
		if (sent) {
			this.telemetryManager.updateStatusSuccess((new Gson()).toJson(info));
			LOGGER.debug("Data sent");
		} else {
			this.telemetryManager.updateStatusFail((new Gson()).toJson(info));
			LOGGER.debug("Something strange happened while trying to send data.");
		}
	}

}
