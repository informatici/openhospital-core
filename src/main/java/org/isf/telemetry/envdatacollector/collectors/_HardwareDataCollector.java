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

import java.util.HashMap;
import java.util.Map;

import org.isf.telemetry.envdatacollector.constants.CollectorsConst;
import org.isf.utils.exception.OHException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.HardwareAbstractionLayer;

@Order(value = 30)
@Component
public class _HardwareDataCollector {

	private static final String ID = "FUN_HW";
	private static final Logger LOGGER = LoggerFactory.getLogger(_HardwareDataCollector.class);

	public String getId() {
		return ID;
	}

	public String getDescription() {
		return "Hardware information (ex. CPU Intel)";
	}

	public Map<String, String> retrieveData() throws OHException {
		LOGGER.debug("Collecting Hardware data...");
		Map<String, String> result = new HashMap<>();
		try {
			SystemInfo si = new SystemInfo();
			HardwareAbstractionLayer hard = si.getHardware();
			CentralProcessor cpu = hard.getProcessor();
			result.put(CollectorsConst.HW_CPU_NUM_PHYSICAL_PROCESSES, String.valueOf(cpu.getPhysicalProcessorCount()));
			result.put(CollectorsConst.HW_CPU_NUM_LOGICAL_PROCESSES, String.valueOf(cpu.getLogicalProcessorCount()));
			result.put(CollectorsConst.HW_CPU_NAME, cpu.getProcessorIdentifier().getName());
			result.put(CollectorsConst.HW_CPU_IDENTIFIER, cpu.getProcessorIdentifier().getIdentifier());
			result.put(CollectorsConst.HW_CPU_MODEL, cpu.getProcessorIdentifier().getModel());
			result.put(CollectorsConst.HW_CPU_ARCHITECTURE, cpu.getProcessorIdentifier().getMicroarchitecture());
			result.put(CollectorsConst.HW_CPU_VENDOR, cpu.getProcessorIdentifier().getVendor());
			result.put(CollectorsConst.HW_CPU_CTX_SWITCHES, String.valueOf(cpu.getContextSwitches()));
		} catch (RuntimeException e) {
			LOGGER.error("Something went wrong with " + ID);
			LOGGER.error(e.toString());
			throw new OHException("Data collector [" + ID + "]", e);
		}
		return result;
	}

}
