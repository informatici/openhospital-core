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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.isf.OHCoreTestCase;
import org.isf.telemetry.manager.TelemetryManager;
import org.isf.telemetry.model.Telemetry;
import org.isf.telemetry.model.TelemetryId;
import org.isf.utils.time.TimeTools;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class TestTelemetryDataCollector extends OHCoreTestCase {

	private TelemetryDataCollector telemetryDataCollector;

	@Mock
	TelemetryManager telemetryManagerMock;

	private AutoCloseable closeable;

	@BeforeEach
	void setUp() {
		closeable = MockitoAnnotations.openMocks(this);
		telemetryDataCollector = new TelemetryDataCollector(telemetryManagerMock);
	}

	@AfterEach
	void closeService() throws Exception {
		closeable.close();
	}

	@Test
	void testGetId() {
		assertThat(telemetryDataCollector.getId()).isEqualTo("TEL_ID");
	}

	@Test
	void testGetDescription() {
		assertThat(telemetryDataCollector.getDescription()).isEqualTo("Telemetry Unique ID (this instance)");
	}

	@Test
	void testRetrieveData() throws Exception {
		when(telemetryManagerMock.retrieveOrBuildNewTelemetry()).thenReturn(getTelemetry());
		Map<String, String> data = telemetryDataCollector.retrieveData();
		assertThat(data).isNotNull();
		assertThat(data).isNotEmpty();
		assertThat(data).hasSize(4);
	}

	private Telemetry getTelemetry() {
		Telemetry telemetry = new Telemetry();
		LocalDateTime now = TimeTools.getNow();
		telemetry.setSentTimestamp(now);
		telemetry.setActive(true);
		telemetry.setOptinDate(now.plusDays(1));
		telemetry.setOptoutDate(now.minusDays(1));
		telemetry.setConsentData("consentData");
		telemetry.setInfo("info");
		telemetry.setHashCode(-127);
		TelemetryId telemetryId = new TelemetryId();
		telemetry.setId(telemetryId);
		Map<String, Boolean> consentMap = new HashMap<>();
		consentMap.put("string", false);
		consentMap.put("string2", true);
		consentMap.put("string3", true);
		consentMap.put("string4", false);
		telemetry.setConsentMap(consentMap);
		return telemetry;
	}
}
