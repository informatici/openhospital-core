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
package org.isf.generaldata;

import java.io.Closeable;
import java.io.IOException;

import org.isf.generaldata.configProvider.ConfigProvider;
import org.isf.generaldata.configProvider.ConfigProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParamsData implements Closeable {

	private static final Logger LOGGER = LoggerFactory.getLogger(ParamsData.class);

	private static ParamsData instance;
	private ConfigProvider configProvider;

	// parameters
	private static final String OH_TELEMETRY_URL = "oh_telemetry_url";

	public String getTelemetryUrl() {
		return configProvider.get(OH_TELEMETRY_URL);
	}

	public static synchronized ParamsData getInstance() {
		if (instance == null) {
			instance = new ParamsData();
		}
		return instance;
	}

	private ParamsData() {
		GeneralData.getGeneralData();
		this.configProvider = ConfigProviderFactory.createConfigProvider();
	}

	@Override
	public void close() {
		try {
			configProvider.close();
		} catch (IOException e) {
			LOGGER.error("Error closing config provider: {}", e.getMessage());
		}
	}

}
