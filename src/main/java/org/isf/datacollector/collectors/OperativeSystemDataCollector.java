/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2021 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.datacollector.collectors;

import java.util.HashMap;
import java.util.Map;

import org.isf.datacollector.AbstractDataCollector;
import org.isf.datacollector.constants.CollectorsConst;
import org.isf.utils.exception.OHException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import oshi.SystemInfo;
import oshi.software.os.OperatingSystem;

@Order(value = 30)
@Component
public class OperativeSystemDataCollector extends AbstractDataCollector {

	private static final String ID = "FUN_OS";
	private static final Logger LOGGER = LoggerFactory.getLogger(OperativeSystemDataCollector.class);

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public String getDescription() {
		return "Operative System information (ex. Ubuntu 12.04)";
	}

	@Override
	public Map<String, String> retrieveData() throws OHException {
		LOGGER.debug("Collecting OS data...");
		Map<String, String> result = new HashMap<>();
		try {
			SystemInfo si = new SystemInfo();
			OperatingSystem os = si.getOperatingSystem();
			result.put(CollectorsConst.OS_FAMILY, os.getFamily());
			result.put(CollectorsConst.OS_VERSION, os.getVersionInfo().getVersion());
			result.put(CollectorsConst.OS_MANUFACTURER, os.getManufacturer());
			result.put(CollectorsConst.OS_BITNESS, String.valueOf(os.getBitness()));
			result.put(CollectorsConst.OS_CODENAME, os.getVersionInfo().getCodeName());
		} catch (RuntimeException e) {
			LOGGER.error("Something went wrong with " + ID);
			LOGGER.error(e.toString());
			throw new OHException("Data collector [" + ID + "]", e);
		}
		return result;
	}

}
