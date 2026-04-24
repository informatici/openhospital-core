/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2026 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.cares.manager;

import org.isf.cares.model.Care;
import org.isf.cares.services.CareIoOperation;
import org.isf.conditioning.model.Conditioning;
import org.isf.encounter.model.Encounter;
import org.isf.utils.exception.OHServiceException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CareManager {

	private final CareIoOperation careIoOperation;


	public CareManager(CareIoOperation careIoOperation) {
		this.careIoOperation = careIoOperation;
	}

	/**
	 * Method that inserts a new {@link Care}.
	 * @param care
	 * @return saved / updated {@link Care}
	 * @throws OHServiceException when validation failed
	 */
	public Care saveCare(Care care) throws OHServiceException {
		return careIoOperation.saveCare(care);
	}

	/**
	 * Validate and update an existing {@link Care}.
	 *
	 * @param care - Care entity to validate and update
	 * @return updated {@link Care} if successful
	 * @throws OHServiceException When validation or update operation fails
	 */
	public Care updateCare(Care care) throws OHServiceException {
		return careIoOperation.updateCare(care);
	}

	/**
	 * Method that returns the list of {@link Care}s with patient id.
	 * @param patientId - the patient id.
	 * @return the list of {@link Care}s.
	 * @throws OHServiceException
	 */
	public List<Care> getCaresByPatient(Integer patientId) throws OHServiceException {
		return careIoOperation.getCaresByPatient(patientId);
	}

	/**
	 * Method that returns the {@link Care} with id.
	 * @param careId - the care id.
	 * @return the {@link Care}.
	 * @throws OHServiceException
	 */
	public Care getCareById(int careId) throws OHServiceException {
		return careIoOperation.getCareById(careId);
	}

	/**
	 * Returns the list of Care for a given patient's encounter
	 *
	 * @param encounter encounter during which Care were created.
	 * @return the list of {@link Care}.
	 * @throws OHServiceException if an error occurs during database request.
	 */
	public List<Care> getCareByPatientEncounter(Encounter encounter) throws OHServiceException {
		return careIoOperation.getCareByPatientEncounter(encounter);
	}
}
