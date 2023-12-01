/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2022 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.generaldata;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import feign.Feign;
import feign.gson.GsonDecoder;

public class ParamsData implements Closeable {

	private static final Logger LOGGER = LoggerFactory.getLogger(ParamsData.class);

	private static ParamsData instance;
	private final ParamsApi api;
	private final String version;

	// parameters
	private static final String OH_TELEMETRY_URL = "oh_telemetry_url";
	private final String telemetryUrl;

	public String getTelemetryUrl() {
		return telemetryUrl;
	}

	public static synchronized ParamsData getInstance() {
		if (instance == null) {
			instance = new ParamsData();
		}
		return instance;
	}

	private ParamsData() {
		GeneralData.getGeneralData();
		this.version = Version.getVersion().toString();
		if (GeneralData.PARAMSURL.endsWith(".json")) {
			this.api = null;
			String configFile = fetchDataFile();
			this.telemetryUrl = parseJson(configFile, OH_TELEMETRY_URL);
		} else {
			this.api = createApi();
			this.telemetryUrl = fetchData(OH_TELEMETRY_URL);
		}
	}

	private interface ParamsApi {

		@feign.RequestLine("GET /")
		@feign.Headers("Content-Type: application/json")
		Map<String, Object> getData();
	}

	private ParamsApi createApi() {
		String baseUrl = GeneralData.PARAMSURL;
		if (baseUrl == null || baseUrl.isEmpty() || (!baseUrl.startsWith("http://") && !baseUrl.startsWith("https://"))) {
			LOGGER.warn("Missing or malformed configuration URL (must start with 'http:// or https://'): {}", baseUrl);
			return null;
		} else {
			LOGGER.debug("Configuration URL is: {}", baseUrl);
			return Feign.builder()
							.decoder(new GsonDecoder())
							.target(ParamsApi.class, baseUrl);
		}
	}

	private String fetchDataFile() {
		try {
			URL url = new URL(GeneralData.PARAMSURL);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");

			int responseCode = connection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
					StringBuilder response = new StringBuilder();
					String line;
					while ((line = reader.readLine()) != null) {
						response.append(line);
					}
					return response.toString();
				}
			} else {
				LOGGER.error("Failed to fetch configuration URL. HTTP response code: {}", responseCode);
			}
		} catch (IOException e) {
			LOGGER.error("Error during HTTP request: {}", e.getMessage());
		}

		return null;
	}

	private String parseJson(String json, String param) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode rootNode = objectMapper.readTree(json);

			JsonNode versionNode = rootNode.path(Version.getVersion().toString());
			if (versionNode.isMissingNode()) {
				versionNode = rootNode.path("default");
			}

			JsonNode paramNode = versionNode.path(param);
			if (paramNode.isTextual()) {
				return paramNode.textValue();
			} else {
				LOGGER.error("Invalid JSON structure: {} not found.", param);
			}
		} catch (IOException e) {
			LOGGER.error("Error parsing JSON: {}", e.getMessage());
		}
		return null;
	}

	private String fetchData(String paramName) {
		if (api != null) {
			try {
				Map<String, Object> response = api.getData();
				if (response != null) {
					Object versionData = response.getOrDefault(version, response.get("default"));
					if (versionData instanceof Map) {
						Object param = ((Map<String, Object>) versionData).get(paramName);
						if (param instanceof String) {
							return (String) param;
						}
					}
				}
			} catch (Exception e) {
				LOGGER.error("Error during API call: {}", e.getMessage());
			}
		}
		return null;
	}

	@Override
	public void close() {
		if (api != null && api instanceof Closeable) {
			try {
				((Closeable) api).close();
			} catch (Exception e) {
				LOGGER.error("Error while closing Feign client: {}", e.getMessage());
			}
		}
	}

}
