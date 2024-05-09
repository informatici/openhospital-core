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
package org.isf.telemetry.service;

import org.isf.telemetry.model.Telemetry;
import org.isf.utils.exception.OHServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class TelemetryOperations {

	private TelemetryRepository telemetryRepository;

	public TelemetryOperations(TelemetryRepository telemetryRepository) {
		this.telemetryRepository = telemetryRepository;
	}

	public boolean saveOrUpdate(Telemetry telemetry) throws OHServiceException {
		return telemetryRepository.save(telemetry) != null;
	}

	public Telemetry retrieveFirst() {
		return this.telemetryRepository.findFirstByOrderById_SoftwareUUIDAsc();
	}
}
