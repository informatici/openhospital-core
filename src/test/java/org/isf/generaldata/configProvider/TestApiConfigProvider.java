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
package org.isf.generaldata.configProvider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import java.util.concurrent.TimeUnit;

import org.isf.generaldata.GeneralData;
import org.isf.generaldata.Version;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.MediaType;

public class TestApiConfigProvider {

	private static final String CONFIG_JSON = """
		{
		  "1.15.0": {
		    "parameter": "https://version-url.com"
		  },
		  "default": {
		    "parameter": "https://default-url.com"
		  }
		}""";

	private ClientAndServer mockServer;

	@BeforeEach
	public void startServer() {
		mockServer = startClientAndServer(1080); // Start the MockServer
	}

	@AfterEach
	public void stopServer() {
		mockServer.stop(); // Stop the MockServer
	}

	@Test
	void testGetTestParamsDataDefaultVersion() throws Exception {
		// Set up the mock server to respond with the desired JSON
		mockServer.when(request().withMethod("GET").withPath("/test"))
			.respond(response().withStatusCode(200)
				.withContentType(MediaType.APPLICATION_JSON)
				.withBody(CONFIG_JSON)
				.withDelay(TimeUnit.MILLISECONDS, 200)); // Adding fixed delay to ensure response stability

		// Use MockedStatic to mock GeneralData
		try (MockedStatic<GeneralData> mockedGeneralData = mockStatic(GeneralData.class);
			MockedStatic<Version> mockedVersion = Mockito.mockStatic(Version.class)) {

			// Mock the Version.getVersion() method to return a specific version
			Version mockVersion = mock(Version.class);
			mockedVersion.when(Version::getVersion).thenReturn(mockVersion);
			when(mockVersion.toString()).thenReturn("");

			// Mock the initialization to set PARAMSURL to the mock server URL
			mockedGeneralData.when(GeneralData::initialize).thenAnswer(invocation -> {
				GeneralData.PARAMSURL = "http://localhost:1080/test"; // Use the mock server's URL
				return null;
			});

			// Initialize GeneralData with the mocked PARAMSURL
			GeneralData.initialize();

			ApiConfigProvider apiConfigProvider = new ApiConfigProvider();

			assertThat(apiConfigProvider.get("parameter")).isEqualTo("https://default-url.com");

			apiConfigProvider.close();
		}
	}

	@Test
	void testGetTestParamsDataWithVersion() throws Exception {
		// Set up the mock server to respond with the desired JSON
		mockServer.when(request().withMethod("GET").withPath("/test"))
			.respond(response().withStatusCode(200)
				.withContentType(MediaType.APPLICATION_JSON)
				.withBody(CONFIG_JSON)
				.withDelay(TimeUnit.MILLISECONDS, 200)); // Adding fixed delay to ensure response stability

		// Use MockedStatic to mock GeneralData
		try (MockedStatic<GeneralData> mockedGeneralData = mockStatic(GeneralData.class);
			MockedStatic<Version> mockedVersion = Mockito.mockStatic(Version.class)) {

			// Mock the Version.getVersion() method to return a specific version
			Version mockVersion = mock(Version.class);
			mockedVersion.when(Version::getVersion).thenReturn(mockVersion);
			when(mockVersion.toString()).thenReturn("1.15.0");

			// Mock the initialization to set PARAMSURL to the mock server URL
			mockedGeneralData.when(GeneralData::initialize).thenAnswer(invocation -> {
				GeneralData.PARAMSURL = "http://localhost:1080/test"; // Use the mock server's URL
				return null;
			});

			// Initialize GeneralData with the mocked PARAMSURL
			GeneralData.initialize();

			ApiConfigProvider apiConfigProvider = new ApiConfigProvider();

			assertThat(apiConfigProvider.get("parameter")).isEqualTo("https://version-url.com");

			apiConfigProvider.close();
		}
	}

	@Test
	void testGetTestParamsDataEmptyParmsUrl() {

		try (MockedStatic<GeneralData> mockedGeneralData = mockStatic(GeneralData.class)) {

			// Mock the initialization to set PARAMSURL to the mock server URL
			mockedGeneralData.when(GeneralData::initialize).thenAnswer(invocation -> {
				GeneralData.PARAMSURL = ""; // Use the mock server's URL
				return null;
			});

			// Initialize GeneralData with the mocked PARAMSURL
			GeneralData.initialize();

			ApiConfigProvider apiConfigProvider = new ApiConfigProvider();

			assertThat(apiConfigProvider.getConfigData()).isNull();

			apiConfigProvider.close();
		}
	}
}
