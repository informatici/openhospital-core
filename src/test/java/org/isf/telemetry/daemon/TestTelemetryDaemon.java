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
package org.isf.telemetry.daemon;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.isf.menu.manager.Context;
import org.isf.telemetry.envdatacollector.collectors.remote.common.GeoIpInfoCommonService;
import org.isf.telemetry.manager.TelemetryManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;
import org.springframework.test.util.ReflectionTestUtils;

class TestTelemetryDaemon {

	@Mock
	ApplicationContext applicationContextMock;
	@Mock
	Map<String, GeoIpInfoCommonService> geoIpServicesMapMock;
	@Mock
	TelemetryManager telemetryManagerMock;

	TelemetryDaemon telemetryDaemon;

	private AutoCloseable closeable;

	@BeforeEach
	void setUp() {
		closeable = MockitoAnnotations.openMocks(this);
		Context.setApplicationContext(applicationContextMock);
		when(applicationContextMock.getBeansOfType(GeoIpInfoCommonService.class)).thenReturn(geoIpServicesMapMock);
		when(applicationContextMock.getBean(TelemetryManager.class)).thenReturn(telemetryManagerMock);
		telemetryDaemon = TelemetryDaemon.getTelemetryDaemon();
	}

	@AfterEach
	void closeService() throws Exception {
		closeable.close();
	}

	@Test
	void testStartStop() {
		telemetryDaemon.start();
		telemetryDaemon.stop();
	}

	@Test
	void testGetGeoIpServiceSelected() {
		telemetryDaemon.start();
		ReflectionTestUtils.setField(telemetryDaemon, "reloadSettings", false);
		// depending on the testing setup the value is either null or a service name
		assertThat(telemetryDaemon.getGeoIpServiceSelected()).isIn(null, "geoiplookup-remote-service");
		telemetryDaemon.stop();
	}

	@Test
	void testGetGeoIpServicesUrlMap() {
		telemetryDaemon.start();
		ReflectionTestUtils.setField(telemetryDaemon, "reloadSettings", false);
		// depending on the testing setup the value is either null or empty
		assertThat(telemetryDaemon.getGeoIpServicesUrlMap()).isIn(null, new HashMap<String, String>());
		telemetryDaemon.stop();
	}

	@Test
	void testReload() {
		telemetryDaemon.start();
		telemetryDaemon.restart();
		telemetryDaemon.stop();
	}

	@Test
	void testRun() throws Exception {
		telemetryDaemon.start();
		new Thread(telemetryDaemon::run).start();
		Thread.sleep(2000);
		telemetryDaemon.stop();
	}

	@Test
	void testReloadSettings() throws Exception {
		telemetryDaemon.start();
		new Thread(telemetryDaemon::run).start();
		Thread.sleep(500);
		telemetryDaemon.reloadSettings();
		Thread.sleep(500);
		telemetryDaemon.stop();
	}

	@Test
	void testGetTelemetryDaemonTwice() {
		ReflectionTestUtils.setField(telemetryDaemon, "initialized", true);
		telemetryDaemon = TelemetryDaemon.getTelemetryDaemon();
	}
}
