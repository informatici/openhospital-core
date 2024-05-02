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
package org.isf.malnutrition.service;

import java.util.List;

import org.isf.malnutrition.model.Malnutrition;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Persistence class for the malnutrition module.
 */
@Service
@Transactional(rollbackFor=OHServiceException.class)
@TranslateOHServiceException
public class MalnutritionIoOperation {

	private MalnutritionIoOperationRepository repository;

	public MalnutritionIoOperation(MalnutritionIoOperationRepository malnutritionIoOperationRepository) {
		this.repository = malnutritionIoOperationRepository;
	}

	/**
	 * Returns all the available {@link Malnutrition} for the specified admission id.
	 * @param admissionId the admission id
	 * @return the retrieved malnutrition.
	 * @throws OHServiceException if an error occurs retrieving the malnutrition list.
	 */
	public List<Malnutrition> getMalnutritions(String admissionId) throws OHServiceException {
		return repository.findAllWhereAdmissionByOrderDate(Integer.parseInt(admissionId));
	}

	/**
	 * Stores a new {@link Malnutrition}. The malnutrition object is updated with the generated id.
	 * @param malnutrition the malnutrition to store.
	 * @return the newly stored new {@link Malnutrition} object.
	 * @throws OHServiceException if an error occurs storing the malnutrition.
	 */
	public Malnutrition newMalnutrition(Malnutrition malnutrition) throws OHServiceException {
		return repository.save(malnutrition);
	}

	/**
	 * Updates the specified {@link Malnutrition}.
	 * @param malnutrition the malnutrition to update.
	 * @return the updated {@link Malnutrition} object.
	 * @throws OHServiceException if an error occurs updating the malnutrition.
	 */
	public Malnutrition updateMalnutrition(Malnutrition malnutrition) throws OHServiceException {
		return repository.save(malnutrition);
	}
	
	/**
	 * Get the specified {@link Malnutrition}.
	 * @param code of the malnutrition.
	 * @return {@link Malnutrition}
	 * @throws OHServiceException if an error occurs updating the malnutrition.
	 */
	public Malnutrition getMalnutrition(int code) throws OHServiceException {
		return repository.getReferenceById(code);
	}

	/**
	 * Returns the last {@link Malnutrition} entry for specified patient ID
	 * @param patientID - the patient ID
	 * @return the last {@link Malnutrition} for specified patient ID. {@code null} if none.
	 * @throws OHServiceException
	 */
	public Malnutrition getLastMalnutrition(int patientID) throws OHServiceException {
		List<Malnutrition> malnutritions = repository.findAllWhereAdmissionByOrderDateDesc(patientID);
		if (malnutritions.isEmpty()) {
			return null;
		}
		return malnutritions.get(0);
	}

	/**
	 * Deletes the specified {@link Malnutrition}.
	 * @param malnutrition the malnutrition to delete.
	 * @throws OHServiceException if an error occurs deleting the specified malnutrition.
	 */
	public void deleteMalnutrition(Malnutrition malnutrition) throws OHServiceException {
		repository.delete(malnutrition);
	}

	/**
	 * Checks if the code is already in use.
	 *
	 * @param code - the malnutrition code
	 * @return {@code true} if the code is already in use, {@code false} otherwise
	 * @throws OHServiceException 
	 */
	public boolean isCodePresent(Integer code) throws OHServiceException {
		return repository.existsById(code);
	}
}
