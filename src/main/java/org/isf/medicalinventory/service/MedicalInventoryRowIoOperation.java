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
import java.util.Optional;

import org.isf.medicalinventory.model.MedicalInventoryRow;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = OHServiceException.class)
@TranslateOHServiceException
public class MedicalInventoryRowIoOperation {

	private MedicalInventoryRowIoOperationRepository repository;

	public MedicalInventoryRowIoOperation(MedicalInventoryRowIoOperationRepository medicalInventoryRowIoOperationRepository) {
		this.repository = medicalInventoryRowIoOperationRepository;
	}

	/**
	 * Insert a new {@link MedicalInventoryRow}.
	 *
	 * @param medicalInventoryRow - the {@link MedicalInventoryRow} to insert.
	 * @return the newly persisted {@link MedicalInventoryRow} object.
	 * @throws OHServiceException
	 */
	public MedicalInventoryRow newMedicalInventoryRow(MedicalInventoryRow medicalInventoryRow) throws OHServiceException {
		// a new row is built with id 0 by the GUI; since the @Id is an Integer, a non-null 0 makes Spring Data treat it
		// as an existing row (merge/update) and fail with a StaleObjectStateException - null it so it is inserted instead
		if (medicalInventoryRow.getId() != null && medicalInventoryRow.getId() == 0) {
			medicalInventoryRow.setId(null);
		}
		return repository.save(medicalInventoryRow);
	}
	
	/**
	 * Update an existing {@link MedicalInventoryRow}.
	 *
	 * @param medicalInventoryRow - the {@link MedicalInventoryRow} to update.
	 * @return the updated {@link MedicalInventoryRow} object.
	 * @throws OHServiceException
	 */
	public MedicalInventoryRow updateMedicalInventoryRow(MedicalInventoryRow medicalInventoryRow) throws OHServiceException {
		return repository.save(medicalInventoryRow);
	}
	
	/**
	 * Delete the specified {@link MedicalInventoryRow}.
	 * @param medicalInventoryRow - the {@link MedicalInventoryRow} to delete.
	 * @throws OHServiceException
	 */
	public void deleteMedicalInventoryRow(MedicalInventoryRow medicalInventoryRow) throws OHServiceException {
		repository.delete(medicalInventoryRow);
	}
	
	/**
	 * Return a list of {@link MedicalInventoryRow}s for passed params.
	 
	 * @param inventoryId - the Inventory Id.
	 * @return the list of {@link MedicalInventoryRow}s. It could be {@code empty}.
	 * @throws OHServiceException
	 */
	public List<MedicalInventoryRow> getMedicalInventoryRowByInventoryId(int inventoryId) throws OHServiceException {
		return repository.findByInventoryId(inventoryId);
	}

	/**
	 * Return {@link MedicalInventoryRow} for passed param.
	 
	 * @param id - the InventoryRow Id.
	 * @return {@link MedicalInventoryRow} with the specified id, {@code null} otherwise.
	 * @throws OHServiceException
	 */
	public Optional<MedicalInventoryRow> getMedicalInventoryRowById(int id) throws OHServiceException {
		return repository.findById(id);
	}

	/**
	 * Return {@link MedicalInventoryRow} for passed param.
	 *
	 * @param medicalCode - the medical code.
	 * @param lotCode - the lot code.
	 * @return the {@link MedicalInventoryRow} object.
	 * @throws OHServiceException
	 */
	public MedicalInventoryRow getMedicalInventoryRowByMedicalCodeAndLotCode(int medicalCode, String lotCode) throws OHServiceException {
		return repository.findByMedicalCodeAndLotCode(medicalCode, lotCode);
	}
}
