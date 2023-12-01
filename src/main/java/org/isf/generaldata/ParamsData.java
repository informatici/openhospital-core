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

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import feign.Feign;
import feign.gson.GsonDecoder;

public class ParamsData {

	private static final Logger LOGGER = LoggerFactory.getLogger(ParamsData.class);

	private static ParamsData instance;
	private final ParamsApi api;
	private final String baseUrl;
	private final String version;

	private String telemetryUrl;
	private String otherParam; // placeholder

	private ParamsData() {
		GeneralData.getGeneralData();
		this.version = Version.getVersion().toString();
		this.baseUrl = GeneralData.PARAMSURL;
		if (baseUrl == null || baseUrl.isEmpty() || !baseUrl.startsWith("http://")) {
			LOGGER.warn("Missing or malformed configuration URL (must start with 'http://'): {}", baseUrl);
			this.api = null;
		} else {
			LOGGER.debug("Configuration URL is: {}", baseUrl);
			this.api = Feign.builder()
							.decoder(new GsonDecoder())
							.target(ParamsApi.class, baseUrl);
		}
	}

	public static ParamsData getInstance() {
		if (instance == null) {
			instance = new ParamsData();
		}
		return instance;
	}

	private void getParamsData() {
		Map<String, Object> response = getResponse();
		if (response != null) {
			// Access data dynamically based on the structure
			Object versionData = response.getOrDefault(version, response.get("default"));
			if (versionData instanceof Map) {
				Object param;

				param = ((Map<String, Object>) versionData).get("oh_telemetry_url");
				if (param instanceof String) {
					telemetryUrl = (String) param;
					LOGGER.debug("Telemetry URL for v{} is: {}", version, telemetryUrl);
				}

				param = ((Map<String, Object>) versionData).get("oh_other_param");
				if (param instanceof String) {
					otherParam = (String) param;
					LOGGER.debug("otherParam for v{} is: {}", version, otherParam);
				}
			}
		}
	}

	private Map<String, Object> getResponse() {
		if (api != null) {
			try {
				return api.getData();
			} catch (Exception e) {
				LOGGER.error("Error during API call: {}", e.getMessage());
			}
		}
		return null;
	}

	private interface ParamsApi {

		@feign.RequestLine("GET /")
		@feign.Headers("Content-Type: application/json")
		Map<String, Object> getData();
	}

	public String getTelemetryUrl() {
		if (telemetryUrl == null || telemetryUrl.isEmpty()) {
			getParamsData();
		}
		return telemetryUrl;
	}

	public String getOtherParam() {
		if (otherParam == null || otherParam.isEmpty()) {
			getParamsData();
		}
		return otherParam;
	}
}
