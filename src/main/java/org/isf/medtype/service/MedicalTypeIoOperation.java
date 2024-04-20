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
package org.isf.medtype.service;

import java.util.List;

import org.isf.medtype.model.MedicalType;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Persistence class for the medical type module.
 */
@Service
@Transactional(rollbackFor=OHServiceException.class)
@TranslateOHServiceException
public class MedicalTypeIoOperation {

	private MedicalTypeIoOperationRepository repository;

	public MedicalTypeIoOperation(MedicalTypeIoOperationRepository medicalTypeIoOperationRepository) {
		this.repository = medicalTypeIoOperationRepository;
	}

	/**
	 * Retrieves all the stored {@link MedicalType}s.
	 * @return a list of all the stored {@link MedicalType}s.
	 * @throws OHServiceException if an error occurs retrieving the medical types.
	 */
	public List<MedicalType> getMedicalTypes() throws OHServiceException {
		return repository.findAllByOrderByDescriptionAsc();
	}

	/**
	 * Updates the specified {@link MedicalType}.
	 * @param medicalType the medical type to update.
	 * @return the newly saved {@link MedicalType} object.
	 * @throws OHServiceException if an error occurs updating the medical type.
	 */
	public MedicalType updateMedicalType(MedicalType medicalType) throws OHServiceException {
		return repository.save(medicalType);
	}

	/**
	 * Stores the specified {@link MedicalType}.
	 * @param medicalType the medical type to store.
	 * @return the newly saved {@link MedicalType} object.
	 * @throws OHServiceException if an error occurs storing the new medical type.
	 */
	public MedicalType newMedicalType(MedicalType medicalType) throws OHServiceException {
		return repository.save(medicalType);
	}

	/**
	 * Deletes the specified {@link MedicalType}.
	 * @param medicalType the medical type to delete.
	 * @throws OHServiceException if an error occurs deleting the medical type.
	 */
	public void deleteMedicalType(MedicalType medicalType) throws OHServiceException {
		repository.delete(medicalType);
	}

	/**
	 * Checks if the specified {@link MedicalType} code is already exists.
	 * @param code the {@link MedicalType} code to check.
	 * @return {@code true} if the medical code is already stored, {@code false} otherwise.
	 * @throws OHServiceException if an error occurs during the check.
	 */
	public boolean isCodePresent(String code) throws OHServiceException {
		return repository.existsById(code);
	}

}
