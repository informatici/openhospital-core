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
package org.isf.medicalinventory.service;

import java.util.List;

import org.isf.medicalinventory.model.MedicalInventoryRow;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = OHServiceException.class)
@TranslateOHServiceException
public class MedicalInventoryRowIoOperation {

	@Autowired
	private MedicalInventoryRowIoOperationRepository repository;
	
	/**
	 * Insert a new {@link MedicalInventoryRow}.
	 *
	 * @param medicalInventoryRow - the {@link MedicalInventoryRow} to insert
	 * @return the newly persisted {@link MedicalInventoryRow} object.
	 * @throws OHServiceException
	 */
	public MedicalInventoryRow newMedicalInventoryRow(MedicalInventoryRow medicalinventoryRow) throws OHServiceException {
		return repository.save(medicalinventoryRow);
	}
	
	/**
	 * Update an existing {@link MedicalInventoryRow}.
	 *
	 * @param medicalInventoryRow - the {@link MedicalInventoryRow} to update
	 * @return the updated {@link MedicalInventoryRow} object.
	 * @throws OHServiceException
	 */
	public MedicalInventoryRow updateMedicalInventoryRow(MedicalInventoryRow medicalInventoryRow) throws OHServiceException {
		return repository.save(medicalInventoryRow);
	}
	
	/**
	 * Delete the specified {@link MedicalInventoryRow}.
	 * @param medicalInventoryRow - the {@link MedicalInventoryRow} to delete.
	 * @throws OHServiceException if an error occurs during the medicalInventoryRow deletion.
	 */
	public void deleteMedicalInventoryRow(MedicalInventoryRow medicalInventoryRow) throws OHServiceException {
		repository.delete(medicalInventoryRow);
	}
	
	/**
	 * Return a list of {@link MedicalInventoryRow}s for passed params.
	 
	 * @param inventoryId - the Invetory Id
	 * @return the list of {@link MedicalInventoryRow}s. It could be {@code empty}
	 * @throws OHServiceException
	 */
	public List<MedicalInventoryRow> getMedicalInventoryRowByInventoryId(int inventoryId) throws OHServiceException {
		return repository.findByInventoryId(inventoryId);
	}
	
	/**
	 * Return a list of {@link MedicalInventoryRow}s for passed params.
	 
	 * @param inventoryId - the Invetory Id.
	 * @param medicalCode - the medical code.
	 * @return the list of {@link MedicalInventoryRow}s. It could be {@code empty}
	 * @throws OHServiceException
	 */
	public List<MedicalInventoryRow> getMedicalInventoryRowByInventoryIdAndMedicalCode(int inventoryId, int medicalCode) throws OHServiceException {
		return repository.findByInventoryIdAndMedicalCode(inventoryId, medicalCode);
	}
}
