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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.isf.datacollector.AbstractDataCollector;
import org.isf.datacollector.constants.CollectorsConst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(value = 50)
@Component
public class TimeDataCollector extends AbstractDataCollector {

	private static final String ID = "FUN_TIME";
	private static final Logger LOGGER = LoggerFactory.getLogger(TimeDataCollector.class);

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public String getDescription() {
		return "Time information (ex. last used timestamp)";
	}

	@Override
	public Map<String, String> retrieveData() {
		LOGGER.debug("Collecting Time data...");
		Map<String, String> result = new HashMap<>();
		result.put(CollectorsConst.TIME_LAST_USED, (new Date()).toString());
		return result;
	}

}
