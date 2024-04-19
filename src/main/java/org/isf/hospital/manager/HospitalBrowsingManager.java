/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2023 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.hospital.manager;

import org.isf.hospital.model.Hospital;
import org.isf.hospital.service.HospitalIoOperations;
import org.isf.utils.exception.OHServiceException;
import org.springframework.stereotype.Component;

/**
 * Class that provides gui separation from database operations and gives some
 * useful logic manipulations of the dynamic data (memory)
 *
 * @author bob
 */
@Component
public class HospitalBrowsingManager {

	private HospitalIoOperations ioOperations;

	public HospitalBrowsingManager(HospitalIoOperations hospitalIoOperations) {
		this.ioOperations = hospitalIoOperations;
	}

	/**
	 * Reads from the database the {@link Hospital} information.
	 *
	 * @return the {@link Hospital} object
	 * @throws OHServiceException
	 */
	public Hospital getHospital() throws OHServiceException {
		return ioOperations.getHospital();
	}

	/**
	 * Reads from the database the currency code.
	 *
	 * @return the currency code
	 * @throws OHServiceException
	 */
	public String getHospitalCurrencyCod() throws OHServiceException {
		return ioOperations.getHospitalCurrencyCod();
	}

	/**
	 * Updates the {@link Hospital} information.
	 *
	 * @return return the updated {@link Hospital} object.
	 * @throws OHServiceException
	 */
	public Hospital updateHospital(Hospital hospital) throws OHServiceException {
		return ioOperations.updateHospital(hospital);
	}
}
	
