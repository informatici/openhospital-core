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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.isf.medstockmovtype.service;

import java.util.List;

import org.isf.medstockmovtype.model.MovementType;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Persistence class for the medstockmovtype module.
 */
@Service
@Transactional(rollbackFor=OHServiceException.class)
@TranslateOHServiceException
public class MedicalStockMovementTypeIoOperation {

	@Autowired
	private MedicalStockMovementTypeIoOperationRepository repository;
	
	/**
	 * Retrieves all the stored {@link MovementType}.
	 * @return all the stored {@link MovementType}s.
	 * @throws OHServiceException if an error occurs retrieving the medical stock movement types.
	 */
	public List<MovementType> getMedicaldsrstockmovType() throws OHServiceException {
		return repository.findAllByOrderByDescriptionAsc();
	}

	/**
	 * Updates the specified {@link MovementType}.
	 * @param medicaldsrstockmovType the medical stock movement type to update.
	 * @return <code>true</code> if the specified stock movement type has been updated, <code>false</code> otherwise.
	 * @throws OHServiceException if an error occurs during the update.
	 */
	public MovementType updateMedicaldsrstockmovType(MovementType medicaldsrstockmovType) throws OHServiceException {
		return repository.save(medicaldsrstockmovType);
	}

	/**
	 * Stores the specified {@link MovementType}.
	 * @param medicaldsrstockmovType the medical stock movement type to store.
	 * @return <code>true</code> if the medical movement type has been stored, <code>false</code> otherwise.
	 * @throws OHServiceException if an error occurs during the store operation.
	 */
	public MovementType newMedicaldsrstockmovType(MovementType medicaldsrstockmovType) throws OHServiceException {
		return repository.save(medicaldsrstockmovType);
	}

	/**
	 * Deletes the specified {@link MovementType}.
	 * @param medicaldsrstockmovType the medical stock movement type to delete.
	 * @return <code>true</code> if the medical stock movement type has been deleted, <code>false</code> otherwise.
	 * @throws OHServiceException if an error occurs during the delete operation.
	 */
	public boolean deleteMedicaldsrstockmovType(MovementType medicaldsrstockmovType) throws OHServiceException {
		repository.delete(medicaldsrstockmovType);
		return true;
	}

	/**
	 * Checks if the specified medical stock movement type is already used.
	 * @param code the code to check.
	 * @return <code>true</code> if the code is used, <code>false</code> otherwise.
	 * @throws OHServiceException if an error occurs during the check.
	 */
	public boolean isCodePresent(String code) throws OHServiceException {
		return repository.existsById(code);
	}

	/**
	 * Get an existing movementType.
	 *
	 * @param code the code to check.
	 * @return MovementType object whose code is provided as parameter.
	 */
	public MovementType findOneByCode(String code) {
		List<MovementType> results = repository.findAllByCode(code);
		if (!results.isEmpty()) {
			return results.get(0);
		}
		return null;
	}

}
