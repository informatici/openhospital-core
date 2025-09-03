/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2025 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.conditioning.manager;

import org.isf.conditioning.model.Conditioning;
import org.isf.conditioning.service.ConditioningIoOperations;
import org.isf.generaldata.MessageBundle;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ConditioningBrowserManager {
	private final ConditioningIoOperations conditioningIoOperations;

	public ConditioningBrowserManager(ConditioningIoOperations conditioningOperations) {
		this.conditioningIoOperations = conditioningOperations;
	}

	/**
	 * Inserts a new conditioning.
	 *
	 * @param conditioning - the conditioning to insert.
	 * @return {@code true} if the conditioning has been successfully inserted, {@code false} otherwise.
	 * @throws OHServiceException
	 */
	public Conditioning newConditioning(Conditioning conditioning) throws OHServiceException {
		validateConditioning(conditioning);
		return conditioningIoOperations.newConditioning(conditioning);
	}

	/**
	 * Retrieve an existing {@link Conditioning} by its ID.
	 *
	 * @param id - The conditioning id
	 * @return found {@link Conditioning} if present, or {@code null} if not found
	 * @throws OHServiceException When the retrieval operation fails
	 */
	public Conditioning getConditioningById(int id) throws OHServiceException {
		return conditioningIoOperations.getConditioningById(id);
	}

	/**
	 * Retrieve all existing {@link Conditioning} by patient code.
	 *
	 * @param patientCode - the patient code.
	 * @return a list of {@link Conditioning} objects, empty if none found
	 * @throws OHServiceException When the retrieval operation fails
	 */
	public List<Conditioning> getConditioningByPatientCode(int patientCode) throws OHServiceException {
		return conditioningIoOperations.getConditioningByPatientCode(patientCode);
	}


	/**
	 * Validate and update an existing {@link Conditioning}.
	 *
	 * @param conditioning - Conditioning entity to validate and update
	 * @return updated {@link Conditioning} if successful
	 * @throws OHServiceException When validation or update operation fails
	 */
	public Conditioning updateConditioning(Conditioning conditioning) throws OHServiceException {
		validateConditioning(conditioning);
		return conditioningIoOperations.updateConditioning(conditioning);
	}


	/**
	 * Validate a {@link Conditioning} object before saving.
	 *
	 * @param conditioning the object to validate
	 * @throws OHServiceException if any validation rule is violated
	 */
	private void validateConditioning(Conditioning conditioning) throws OHServiceException {
		List<OHExceptionMessage> errors = new ArrayList<>();

		if (conditioning == null) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("Conditioning most not be null.")));
		}

		if (conditioning.getMceDuree() != null && conditioning.getMceDuree() < 0) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("mceDuree should be positif.")));
		}
		if (conditioning.getVentilationDuree() != null && conditioning.getVentilationDuree() < 0) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("ventilationDuree should be positif.")));
		}

		if (conditioning.getOxygeneDebit() != null && conditioning.getOxygeneDebit() < 0) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("oxygeneDebit should be positif.")));
		}
		if (conditioning.getSgVolume() != null && conditioning.getSgVolume() < 0) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("sgVolume should be positif.")));
		}
		if (conditioning.getDiazepamDose() != null && conditioning.getDiazepamDose() < 0) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("diazepamDose should be positif.")));
		}
		if (conditioning.getBolusSsVolume() != null && conditioning.getBolusSsVolume() < 0) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("bolusSsVolume should be positif.")));
		}

		if (conditioning.getDate() == null) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("performAt is required.")));
		}

		if (conditioning.getSngNumero() != null && conditioning.getSngNumero().length() > 50) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("sngNumero should have 50 caraters max.")));
		}

		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
	}
}
