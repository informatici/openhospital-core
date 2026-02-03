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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.isf.OHCoreTestCase;
import org.isf.telemetry.envdatacollector.DataCollectorProviderService;
import org.isf.telemetry.manager.TelemetryManager;
import org.isf.telemetry.service.remote.TelemetryDataCollectorGatewayService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class TestTelemetryUtil extends OHCoreTestCase {

	TelemetryUtils telemetryUtils;

	@Mock
	DataCollectorProviderService dataCollectorProviderMock;
	@Mock
	TelemetryDataCollectorGatewayService telemetryGatewayServiceMock;
	@Mock
	TelemetryManager telemetryManagerMock;

	private AutoCloseable closeable;

	@BeforeEach
	void setUp() {
		closeable = MockitoAnnotations.openMocks(this);
		telemetryUtils = new TelemetryUtils(dataCollectorProviderMock, telemetryGatewayServiceMock, telemetryManagerMock);
	}

	@AfterEach
	void closeService() throws Exception {
		closeable.close();
	}

	@Test
	void testRetrieveDataToSend() throws Exception {
		Map<String, Boolean> consentMap = new HashMap<>();
		consentMap.put("String1", Boolean.TRUE);
		consentMap.put("String2", Boolean.FALSE);
		consentMap.put("String3", Boolean.TRUE);
		consentMap.put("String4", Boolean.FALSE);
		Map<String, Map<String, String>> dataToSend = telemetryUtils.retrieveDataToSend(consentMap);
		assertThat(dataToSend).isNotNull();
		assertThat(dataToSend).isEmpty();
	}

	@Test
	void testSendTelemetryDataSimulation() throws Exception {
		Map<String, Map<String, String>> dataToSend = new LinkedHashMap<>();
		Map<String, String> consentMap = new HashMap<>();
		consentMap.put("String1", "true");
		consentMap.put("String2", "false");
		dataToSend.put("one", consentMap);
		telemetryUtils.sendTelemetryData(dataToSend, true);
	}

	@Test
	void testSendTelemetryData() throws Exception {
		Map<String, Map<String, String>> dataToSend = new LinkedHashMap<>();
		Map<String, String> consentMap = new HashMap<>();
		consentMap.put("String1", "true");
		consentMap.put("String2", "false");
		dataToSend.put("one", consentMap);
		telemetryUtils.sendTelemetryData(dataToSend, false);
	}
}
