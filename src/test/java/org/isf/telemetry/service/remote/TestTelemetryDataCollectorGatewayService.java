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
package org.isf.telemetry.service.remote;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import org.isf.OHCoreTestCase;
import org.isf.generaldata.ParamsData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.core.env.Environment;

class TestTelemetryDataCollectorGatewayService extends OHCoreTestCase {

	private TelemetryDataCollectorGatewayService telemetryDataCollectorGatewayService;

	@Mock
	Environment propertiesMock;
	@Mock
	ParamsData paramsDataClient;

	private AutoCloseable closeable;

	@BeforeEach
	void setUp() {
		closeable = MockitoAnnotations.openMocks(this);
		telemetryDataCollectorGatewayService = new TelemetryDataCollectorGatewayService(propertiesMock);
	}

	@AfterEach
	void closeService() throws Exception {
		closeable.close();
	}

	@Test
	void testSendWithBadURL() {
		try (MockedStatic<ParamsData> mockedParamsData = mockStatic(ParamsData.class)) {
			mockedParamsData.when(() -> ParamsData.getInstance()).thenReturn(paramsDataClient);
			when(paramsDataClient.getTelemetryUrl()).thenReturn("aBadURL");
			assertThat(telemetryDataCollectorGatewayService.send("stringOfData")).isFalse();
		}
	}

	@Test
	void testSendException() {
		try (MockedStatic<ParamsData> mockedParamsData = mockStatic(ParamsData.class)) {
			mockedParamsData.when(() -> ParamsData.getInstance()).thenReturn(paramsDataClient);
			when(paramsDataClient.getTelemetryUrl()).thenReturn("http://google.com");
			String dataToSend = "[{\"No\":\"17\",\"Name\":\"Andrew\"},{\"No\":\"18\",\"Name\":\"Peter\"}, {\"No\":\"19\",\"Name\":\"Tom\"}]";
			assertThatThrownBy(() -> telemetryDataCollectorGatewayService.send(dataToSend))
							.isInstanceOf(feign.FeignException.class);
		}
	}
}
