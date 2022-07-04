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
package org.isf.telemetry.envdatacollector.collectors;

import java.util.HashMap;
import java.util.Map;

import org.isf.menu.service.MenuIoOperations;
import org.isf.patient.service.PatientIoOperations;
import org.isf.telemetry.envdatacollector.AbstractDataCollector;
import org.isf.telemetry.envdatacollector.constants.CollectorsConst;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.isf.ward.service.WardIoOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(value = 40)
@Component
public class OpenHospitalDataCollector extends AbstractDataCollector {

	private static final String ID = "FUN_OH";
	private static final Logger LOGGER = LoggerFactory.getLogger(OpenHospitalDataCollector.class);

	@Autowired
	private PatientIoOperations patientIoOperations;

	@Autowired
	private MenuIoOperations menuIoOperations;

	@Autowired
	private WardIoOperations wardIoOperations;

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public String getDescription() {
		return "Hospital information (ex. 100 beds)";
	}

	@Override
	public Map<String, String> retrieveData() throws OHException {
		LOGGER.debug("Collecting Open Hospital data...");
		Map<String, String> result = new HashMap<>();
		try {
			result.put(CollectorsConst.OH_TOTAL_ACTIVE_PATIENTS, String.valueOf(patientIoOperations.countAllActivePatients()));
			result.put(CollectorsConst.OH_TOTAL_ACTIVE_USERS, String.valueOf(this.menuIoOperations.countAllActive()));
			result.put(CollectorsConst.OH_TOTAL_ACTIVE_WARDS, String.valueOf(this.wardIoOperations.countAllActiveWards()));
			result.put(CollectorsConst.OH_TOTAL_ACTIVE_BEDS, String.valueOf(this.wardIoOperations.countAllActiveBeds()));
		} catch (OHServiceException e) {
			LOGGER.error("Something went wrong with " + ID);
			LOGGER.error(e.toString());
			throw new OHException("Data collector [" + ID + "]", e);
		}
		return result;
	}

}
