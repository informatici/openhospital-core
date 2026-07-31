/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2026 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import java.util.Map;

import org.isf.generaldata.GeneralData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.MediaType;

public class TestJsonFileConfigProvider {

	private static final String CONFIG_JSON = """
		{
		  "default": {
		    "oh_telemetry_url": "https://default-url.com"
		  }
		}""";

	private ClientAndServer mockServer;

	@BeforeEach
	public void startServer() {
		mockServer = startClientAndServer();
	}

	@AfterEach
	public void stopServer() {
		mockServer.stop();
	}

	@Test
	void testJsonFileConfigProvider() throws Exception {
		mockServer.when(request().withMethod("GET").withPath("/oh-conf.json"))
			.respond(response().withStatusCode(200)
				.withContentType(MediaType.APPLICATION_JSON)
				.withBody(CONFIG_JSON));

		GeneralData.initialize();
		GeneralData.PARAMSURL = "http://localhost:" + mockServer.getLocalPort() + "/oh-conf.json";

		JsonFileConfigProvider jsonFileConfigProvider = new JsonFileConfigProvider();

		Map<String, Object> configData = jsonFileConfigProvider.getConfigData();

		assertThat(configData).containsKey("oh_telemetry_url");
		assertThat(jsonFileConfigProvider.get("oh_telemetry_url")).isEqualTo("https://default-url.com");
		assertThat(jsonFileConfigProvider.get("someParam")).isNull();

		// The request must carry a custom User-Agent: the migrated server rejects the default Java one with HTTP 403.
		mockServer.verify(request().withPath("/oh-conf.json").withHeader("User-Agent", "OpenHospital/.+"));

		// void method
		jsonFileConfigProvider.close();
	}

	@Test
	void testJsonFileConfigProviderServerRejects() throws Exception {
		// Characterizes the reported bug: a non-200 response (such as the 403 the migrated server returned to
		// requests without a proper User-Agent) must yield an empty configuration rather than a failure.
		mockServer.when(request().withMethod("GET").withPath("/oh-conf.json"))
			.respond(response().withStatusCode(403));

		GeneralData.initialize();
		GeneralData.PARAMSURL = "http://localhost:" + mockServer.getLocalPort() + "/oh-conf.json";

		JsonFileConfigProvider jsonFileConfigProvider = new JsonFileConfigProvider();

		assertThat(jsonFileConfigProvider.getConfigData()).isEmpty();
		assertThat(jsonFileConfigProvider.get("someParam")).isNull();

		// void method
		jsonFileConfigProvider.close();
	}

	@Test
	void testJsonFileConfigProviderBadUrl() throws Exception {
		GeneralData.initialize();
		GeneralData.PARAMSURL = "https://somebadaddress.xxx";

		JsonFileConfigProvider jsonFileConfigProvider = new JsonFileConfigProvider();

		Map<String, Object> configData = jsonFileConfigProvider.getConfigData();

		assertThat(configData).isEmpty();

		assertThat(jsonFileConfigProvider.get("someParam")).isNull();

		// void method
		jsonFileConfigProvider.close();
	}
}
