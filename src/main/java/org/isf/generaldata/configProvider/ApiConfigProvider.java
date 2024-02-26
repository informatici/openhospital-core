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

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;

import org.isf.generaldata.GeneralData;
import org.isf.generaldata.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import feign.Feign;
import feign.gson.GsonDecoder;

class ApiConfigProvider implements ConfigProvider {

	private static final String VERSION = Version.getVersion().toString();

	private static final Logger LOGGER = LoggerFactory.getLogger(ApiConfigProvider.class);

	private final ParamsApi api;
	private final Map<String, Object> configData;

	public ApiConfigProvider() {
		this.api = createApi();
		this.configData = fetchData();
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

	@Override
	public String get(String paramName) {
		Object value = null;
		if (configData != null) {
			value = configData.get(paramName);
		}
		return (value != null) ? value.toString() : null;
	}

	@Override
	public Map<String, Object> getConfigData() {
		return configData;
	}

	@Override
	public void close() {
		if (api != null && api instanceof Closeable) {
			try {
				((Closeable) api).close();
			} catch (IOException e) {
				LOGGER.error("Error while closing Feign client: {}", e.getMessage());
			}
		}
	}

	private Map<String, Object> fetchData() {
		if (api != null) {
			try {
				Map<String, Object> response = api.getData();
				if (response != null) {
					Object versionData = response.getOrDefault(VERSION, response.get("default"));
					if (versionData instanceof Map) {
						return (Map<String, Object>) versionData;
					}
				}
			} catch (Exception e) {
				LOGGER.error("Error during API call: {}", e.getMessage());
			}
		}
		return null;
	}
}