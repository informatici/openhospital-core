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
package org.isf.envdatacollector.collectors;

import java.util.HashMap;
import java.util.Map;

import org.isf.envdatacollector.AbstractDataCollector;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(value = 10)
@Component
public class ApplicationDataCollector extends AbstractDataCollector {

	private static final String ID = "FUN_APPLICATION";

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public String getDescription() {
		return "Application technical information (ex. OH version)";
	}

	@Override
	public Map<String, String> retrieveData() {
		// TODO retrieve all information and make a text message
		Map<String, String> result = new HashMap<>();
		result.put("sample", "This is a sample message from unit called " + ID);
		return result;
	}

}
