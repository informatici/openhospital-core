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
package org.isf.telemetry.envdatacollector.collectors;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.isf.OHCoreTestCase;
import org.junit.jupiter.api.Test;

class TestHardwareDataCollector extends OHCoreTestCase {

	@Test
	void testGetId() {
		HardwareDataCollector hardwareDataCollector = new HardwareDataCollector();
		assertThat(hardwareDataCollector.getId()).isEqualTo("TEL_HW");
	}

	@Test
	void testGetDescription() {
		HardwareDataCollector hardwareDataCollector = new HardwareDataCollector();
		assertThat(hardwareDataCollector.getDescription()).isEqualTo("Hardware information (CPU, RAM)");
	}

	@Test
	void testRetrieveData() throws Exception {
		HardwareDataCollector hardwareDataCollector = new HardwareDataCollector();
		Map<String, String> data = hardwareDataCollector.retrieveData();
		assertThat(data).isNotNull();
		assertThat(data).isNotEmpty();
		assertThat(data).hasSize(9);
	}

	@Test
	void testIsSelected() {
		HardwareDataCollector hardwareDataCollector = new HardwareDataCollector();
		Map<String, Boolean> checkboxesStatus = new HashMap<>();
		checkboxesStatus.put("string", false);
		checkboxesStatus.put("string2", true);
		checkboxesStatus.put("TEL_HW", true);
		checkboxesStatus.put("string4", false);
		assertThat(hardwareDataCollector.isSelected(checkboxesStatus)).isTrue();
	}
}
