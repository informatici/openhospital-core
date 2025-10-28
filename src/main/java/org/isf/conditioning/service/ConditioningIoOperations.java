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
package org.isf.conditioning.service;

import java.time.LocalDateTime;
import java.util.List;

import org.isf.conditioning.model.Conditioning;
import org.isf.encounter.model.Encounter;
import org.isf.encounter.model.EncounterStatus;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = OHServiceException.class)
@TranslateOHServiceException
public class ConditioningIoOperations {
	
	private final ConditioningIoOperationRepository conditioningIoOperationRepository;

	public ConditioningIoOperations(ConditioningIoOperationRepository operationRepository) {
		this.conditioningIoOperationRepository = operationRepository;
	}

	/**
	 * Inserts a new conditioning.
	 *
	 * @param conditioning the conditioning to insert.
	 * @return the new/saved Conditioning object.
	 * @throws OHServiceException if an error occurs during the insertion.
	 */
	public Conditioning newConditioning(Conditioning conditioning) throws OHServiceException {
		return conditioningIoOperationRepository.save(conditioning);
	}

	/**
	 * Retrieve an existing {@link Conditioning} by its ID.
	 *
	 * @param id - The conditioning id
	 * @return found {@link Conditioning} if present, or {@code null} if not found
	 * @throws OHServiceException When the retrieval operation fails
	 */
	public Conditioning getConditioningById(int id) throws OHServiceException {
		return conditioningIoOperationRepository.findById(id).orElse(null);
	}

	/**
	 * Update an existing {@link Conditioning}.
	 *
	 * @param conditioning - The Conditioning entity to update
	 * @return updated {@link Conditioning} if successful
	 * @throws OHServiceException When the update operation fails
	 */
	public Conditioning updateConditioning(Conditioning conditioning) throws OHServiceException {
		return conditioningIoOperationRepository.save(conditioning);
	}


	/**
	 * Returns all the stored {@link Conditioning} with the specified patient code.
	 *
	 * @param patientCode - the patient code.
	 * @return the retrieved conditionings.
	 * @throws OHServiceException
	 */
	public List<Conditioning> getConditioningByPatientCode(int patientCode) throws OHServiceException {
		return conditioningIoOperationRepository.findByPatientCode(patientCode);
	}

	/**
	 * Returns the list of Conditioning for a given patient's encounter
	 *
	 * @param encounter encounter during which Conditioning were created.
	 * @return the list of {@link Conditioning}.
	 * @throws OHServiceException if an error occurs during database request.
	 */
	public List<Conditioning> getConditioningByPatientEncounter(Encounter encounter) throws OHServiceException {
		if (encounter.getClosedAt() != null) {
			return conditioningIoOperationRepository.findByDateBetweenAndPatientCode(encounter.getPerformedAt(), encounter.getClosedAt(), encounter.getPatient().getCode());
		}
		return conditioningIoOperationRepository.findByDateBetweenAndPatientCode(encounter.getPerformedAt(), LocalDateTime.now(), encounter.getPatient().getCode());
	}
}
