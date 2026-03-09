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
package org.isf.cares.services;

import org.isf.cares.model.Care;
import org.isf.generaldata.MessageBundle;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CareIoOperation {

	private final CareIoOperationRepository careIoOperationRepository;

	public CareIoOperation(CareIoOperationRepository careIoOperationRepository) {
		this.careIoOperationRepository = careIoOperationRepository;
	}

	/**
	 * Method that inserts a new {@link Care}.
	 * @param care
	 * @return saved / updated {@link Care}
	 * @throws OHServiceException when validation failed
	 */
	public Care saveCare(Care care) throws OHServiceException {
		validateCare(care);
		return careIoOperationRepository.save(care);
	}

	/**
	 * Validate and update an existing {@link Care}.
	 *
	 * @param care - Care entity to validate and update
	 * @return updated {@link Care} if successful
	 * @throws OHServiceException When validation or update operation fails
	 */
	public Care updateCare(Care care) throws OHServiceException {
		validateCare(care);
		return careIoOperationRepository.save(care);
	}

	/**
	 * Method that returns the list of {@link Care}s with patient id.
	 * @param patientId - the patient id.
	 * @return the list of {@link Care}s.
	 * @throws OHServiceException
	 */
	public List<Care> getCaresByPatient(Integer patientId) throws OHServiceException {
		return careIoOperationRepository.findByPatient(patientId);
	}

	/**
	 * Method that returns the {@link Care} with id.
	 * @param careId - the care id.
	 * @return the {@link Care}.
	 * @throws OHServiceException
	 */
	public Care getCareById(int careId) throws OHServiceException {
		return careIoOperationRepository.findById(careId).orElse(null);
	}

	/**
	 * Verify if the object is valid for CRUD and return a list of errors, if any.
	 * @param care
	 * @throws OHDataValidationException
	 */
	protected void validateCare(Care care) throws OHServiceException {
		List<OHExceptionMessage> errors = new ArrayList<>();

		if (care.getPatient() == null) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.care.insertpatient.msg")));
		}

		if (care.getTeam() == null || care.getTeam().isEmpty()) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("Team must be provide")));
		}

		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
	}
}
