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
package org.isf.telemetry.envdatacollector;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = OHServiceException.class)
@TranslateOHServiceException
public class DataCollectorProviderService {

	private static final Logger LOGGER = LoggerFactory.getLogger(DataCollectorProviderService.class);

	@Autowired
	private List<AbstractDataCollector> dataCollectors;

	/**
	 * Finds single data collector and retrieves data
	 * 
	 * @param dataCollectorFunction
	 * @return
	 * @throws OHException
	 */
	public Map<String, String> collectData(String dataCollectorFunction) throws OHException {
		Optional<AbstractDataCollector> itemOpt = this.dataCollectors.stream().filter(collector -> dataCollectorFunction.equals(collector.getId())).findFirst();
		if (itemOpt.isPresent()) {
			return itemOpt.get().retrieveData();
		}
		return null;
	}

	/**
	 * Retrieves data from a list of data collectors
	 * 
	 * @param listDataCollectorFunction
	 * @return
	 */
	public Map<String, Map<String, String>> collectData(List<String> listDataCollectorFunction, boolean ignoreErrors) throws OHException {
		Map<String, Map<String, String>> result = new LinkedHashMap<>();
		OHException[] exception = { null };
		this.dataCollectors.stream().forEach(collector -> {
			if (listDataCollectorFunction.contains(collector.getId())) {
				try {
					result.put(collector.getId(), collector.retrieveData());
				} catch (OHException e) {
					if (!ignoreErrors) {
						exception[0] = e;
						return;
					}
					LOGGER.error(e.getMessage(), e);
				}
			}
		});
		if (exception[0] != null) {
			throw exception[0];
		}
		return result;
	}

}
