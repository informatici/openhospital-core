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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;

import org.isf.generaldata.GeneralData;
import org.isf.generaldata.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

class JsonFileConfigProvider implements ConfigProvider {

	private static final String VERSION = Version.getVersion().toString();

	private static final Logger LOGGER = LoggerFactory.getLogger(JsonFileConfigProvider.class);

	private final Map<String, Object> configData;

	public JsonFileConfigProvider() {
		this.configData = fetchDataFile();
	}

	private Map<String, Object> fetchDataFile() {
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

					// Parse the JSON string into a Map<String, String> using Gson
					Gson gson = new Gson();
					Map<String, Object> resultMap = gson.fromJson(response.toString(), Map.class);

					// Check if 'version' is present, otherwise use "default"
					return resultMap.containsKey(VERSION) ? (Map<String, Object>) resultMap.get(VERSION)
									: (Map<String, Object>) resultMap.get("default");
				}
			} else {
				LOGGER.error("Failed to fetch configuration URL. HTTP response code: {}", responseCode);
			}
		} catch (IOException e) {
			LOGGER.error("Error during HTTP request: {}", e.getMessage());
		}

		return Collections.emptyMap();
	}

	@Override
	public Map<String, Object> getConfigData() {
		return configData;
	}

	@Override
	public String get(String paramName) {
		Object value = null;
		if (configData != null) {
			value = configData.get(paramName);
		}
		return (value != null) ? value.toString() : null;
	}

	@Override
	public void close() throws IOException {
		// No operation, as there are no resources to explicitly close
	}

}