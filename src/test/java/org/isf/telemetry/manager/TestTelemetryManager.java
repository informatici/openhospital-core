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
package org.isf.telemetry.manager;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.isf.OHCoreTestCase;
import org.isf.telemetry.model.Telemetry;
import org.isf.telemetry.service.TelemetryRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

class TestTelemetryManager extends OHCoreTestCase {

	@Autowired
	TelemetryRepository telemetryRepository;

	TelemetryManager telemetryManager;

	Map<String, Boolean> consentMap;

	private AutoCloseable closeable;

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
		closeable = MockitoAnnotations.openMocks(this);
		telemetryManager = new TelemetryManager(telemetryRepository);
	}

	@AfterEach
	void closeService() throws Exception {
		closeable.close();
	}

	@Test
	void testEnable() {
		consentMap = new HashMap<>();
		consentMap.put("string", false);
		consentMap.put("string2", true);
		consentMap.put("string3", true);
		consentMap.put("string4", false);
		assertThat(telemetryManager.enable(consentMap)).isNotNull();
	}

	@Test
	void testDisableListEmpty() {
		consentMap = new HashMap<>();
		consentMap.put("string", false);
		consentMap.put("string2", true);
		consentMap.put("string3", true);
		consentMap.put("string4", false);
		assertThat(telemetryManager.disable(consentMap)).isNotNull();
	}

	@Test
	void testDisableListNotEmpty() {
		consentMap = new HashMap<>();
		consentMap.put("string", false);
		consentMap.put("string2", true);
		consentMap.put("string3", true);
		consentMap.put("string4", false);
		Telemetry telemetry = telemetryManager.enable(consentMap);
		Telemetry savedTelemetry = telemetryManager.save(telemetry);
		assertThat(savedTelemetry).isNotNull();
		assertThat(telemetryManager.disable(consentMap)).isNotNull();
	}

	@Test
	void testRetrieveSettings() {
		consentMap = new HashMap<>();
		consentMap.put("string", false);
		consentMap.put("string2", true);
		Telemetry telemetry = telemetryManager.enable(consentMap);
		Telemetry savedTelemetry = telemetryManager.save(telemetry);
		assertThat(savedTelemetry).isNotNull();
		Telemetry retrievedTelemetry = telemetryManager.retrieveSettings();
		assertThat(retrievedTelemetry).isNotNull();
	}

	@Test
	void testUpdateStatusSuccess() {
		Telemetry telemetry = new Telemetry();
		Telemetry updatedTelemetry = telemetryManager.updateStatusSuccess("infoString");
		assertThat(updatedTelemetry).isNotNull();
		assertThat(updatedTelemetry.getInfo()).isEqualTo("infoString");
	}

	@Test
	void testUpdateStatusFail() {
		Telemetry telemetry = new Telemetry();
		Telemetry updatedTelemetry = telemetryManager.updateStatusFail("failureString");
		assertThat(updatedTelemetry).isNotNull();
		assertThat(updatedTelemetry.getInfo()).isEqualTo("failureString");
	}
}
