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
package org.isf.telemetry.envdatacollector.collectors.remote.ipapi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.isf.OHCoreTestCase;
import org.isf.menu.manager.Context;
import org.isf.telemetry.envdatacollector.collectors.remote.common.GeoIpInfoCommonService;
import org.isf.telemetry.envdatacollector.collectors.remote.common.GeoIpInfoSettings;
import org.isf.telemetry.manager.TelemetryManager;
import org.isf.telemetry.model.Telemetry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;

class TestIpApiService extends OHCoreTestCase {

	@Mock
	GeoIpInfoSettings settings;
	@Mock
	private ApplicationContext applicationContextMock;
	@Mock
	Map<String, GeoIpInfoCommonService> geoIpServicesMapMock;
	@Mock
	TelemetryManager telemetryManagerMock;
	@Mock
	Telemetry telemetryMock;
	IpApiService ipApiService;

	private AutoCloseable closeable;

	@BeforeEach
	void setup() {
		closeable = MockitoAnnotations.openMocks(this);
		Context.setApplicationContext(applicationContextMock);
		when(applicationContextMock.getBeansOfType(GeoIpInfoCommonService.class)).thenReturn(geoIpServicesMapMock);
		when(applicationContextMock.getBean(TelemetryManager.class)).thenReturn(telemetryManagerMock);
		when(telemetryManagerMock.retrieveSettings()).thenReturn(telemetryMock);
		when(settings.retrieveBaseUrl(any(String.class))).thenReturn("http://someURL");
		ipApiService = new IpApiService(settings);
	}

	@AfterEach
	void closeService() throws Exception {
		closeable.close();
	}

	@Test
	void testGetServiceName() {
		assertThat(ipApiService.getServiceName()).isEqualTo("ipapi-remote-service");
	}

	@Test
	void testRetrieveIpInfo() {
		assertThatThrownBy(() -> ipApiService.retrieveIpInfo())
						.isInstanceOf(feign.FeignException.class);
	}
}
