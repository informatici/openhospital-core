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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.isf.telemetry.envdatacollector.collectors;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.isf.generaldata.Version;
import org.isf.telemetry.envdatacollector.AbstractDataCollector;
import org.isf.telemetry.envdatacollector.constants.CollectorsConst;
import org.isf.utils.db.DbSingleConn;
import org.isf.utils.exception.OHException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(value = 10)
@Component
public class ApplicationDataCollector extends AbstractDataCollector {

	private static final String ID = "FUN_APPLICATION";
	private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationDataCollector.class);

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public String getDescription() {
		return "Application technical information (ex. OH version)";
	}

	@Override
	public Map<String, String> retrieveData() throws OHException {
		LOGGER.debug("Collecting application data...");
		Map<String, String> result = new HashMap<>();
		try {
			Version.initialize();
			result.put(CollectorsConst.APP_VER_MAJOR, Version.VER_MAJOR);
			result.put(CollectorsConst.APP_VER_MINOR, Version.VER_MINOR);
			result.put(CollectorsConst.APP_RELEASE, Version.VER_RELEASE);
			return result;
		} catch (Exception e) {
			LOGGER.error("Something went wrong with " + ID);
			LOGGER.error(e.toString());
			throw new OHException("Data collector [" + ID + "]", e);
		}
	}

}
