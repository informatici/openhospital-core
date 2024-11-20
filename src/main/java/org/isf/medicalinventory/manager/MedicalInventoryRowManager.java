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
package org.isf.medicalinventory.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jakarta.transaction.Transactional;

import org.isf.generaldata.MessageBundle;
import org.isf.medicalinventory.model.MedicalInventoryRow;
import org.isf.medicalinventory.service.MedicalInventoryRowIoOperation;
import org.isf.medicalstock.manager.MovStockInsertingManager;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.springframework.stereotype.Component;

@Component
public class MedicalInventoryRowManager {

	private MedicalInventoryRowIoOperation ioOperation;
	private MovStockInsertingManager movStockInsertingManager;

	public MedicalInventoryRowManager(MedicalInventoryRowIoOperation medicalInventoryRowIoOperation, MovStockInsertingManager movStockInsertingManager) {
		this.ioOperation = medicalInventoryRowIoOperation;
		this.movStockInsertingManager = movStockInsertingManager;
	}

	/**
	 * Insert a new {@link MedicalInventoryRow}.
	 *
	 * @param medicalInventoryRow - the {@link MedicalInventoryRow} to insert.
	 * @return the newly persisted {@link MedicalInventoryRow} object.
	 * @throws OHServiceException
	 */
	public MedicalInventoryRow newMedicalInventoryRow(MedicalInventoryRow medicalInventoryRow) throws OHServiceException {
		validateMedicalInventoryRow(medicalInventoryRow);
		return ioOperation.newMedicalInventoryRow(medicalInventoryRow);
	}
	
	/**
	 * Update an existing {@link MedicalInventoryRow}.
	 *
	 * @param medicalInventoryRow - the {@link MedicalInventoryRow} to update.
	 * @return the updated {@link MedicalInventoryRow} object.
	 * @throws OHServiceException
	 */
	public MedicalInventoryRow updateMedicalInventoryRow(MedicalInventoryRow medicalInventoryRow) throws OHServiceException {
		validateMedicalInventoryRow(medicalInventoryRow);
		return ioOperation.updateMedicalInventoryRow(medicalInventoryRow);
	}
	
	/**
	 * Delete the specified {@link MedicalInventoryRow}.
	 * @param medicalInventoryRow - the {@link MedicalInventoryRow} to delete.
	 * @throws OHServiceException
	 */
	public void deleteMedicalInventoryRow(MedicalInventoryRow medicalInventoryRow) throws OHServiceException {
		ioOperation.deleteMedicalInventoryRow(medicalInventoryRow);
	}
	
	/**
	 * Return a list of {@link MedicalInventoryRow}s for passed params.
	 *
	 * @param inventoryId - the Inventory Id.
	 * @return the list of {@link MedicalInventoryRow}s. It could be {@code empty}.
	 * @throws OHServiceException
	 */
	public List<MedicalInventoryRow> getMedicalInventoryRowByInventoryId(int inventoryId) throws OHServiceException {
		return ioOperation.getMedicalInventoryRowByInventoryId(inventoryId);
	}

	/**
	 * Delete a list of inventory rows {@link MedicalInventoryRow}s
	 *
	 * @param inventoryRowsToDelete - the list of {@link MedicalInventoryRow}s
	 * 
	 * @throws OHServiceException
	 */
	@Transactional(rollbackOn = OHServiceException.class)
	public void deleteMedicalInventoryRows(List<MedicalInventoryRow> inventoryRowsToDelete) throws OHServiceException {
		for (MedicalInventoryRow invRow : inventoryRowsToDelete) {
			Optional<MedicalInventoryRow> medInvRow = ioOperation.getMedicalInventoryRowById(invRow.getId());
			if (medInvRow.isPresent()) {
				MedicalInventoryRow invRowDelete = medInvRow.get();
				if (invRowDelete.isNewLot()) {
					this.deleteMedicalInventoryRow(invRowDelete);	
					if (invRowDelete.getLot() != null) {
						movStockInsertingManager.deleteLot(invRowDelete.getLot());
					}
				}
				ioOperation.deleteMedicalInventoryRow(invRow);
			}	
		}
	}

	/**
	 * Return {@link MedicalInventoryRow} for passed param.
	 *
	 * @param invRowId - the MedicalInventoryRow Id.
	 * @return the {@link MedicalInventoryRow} object.
	 * @throws OHServiceException
	 */
	public MedicalInventoryRow getMedicalInventoryRowById(int invRowId) throws OHServiceException {
		Optional<MedicalInventoryRow> medInvRow = ioOperation.getMedicalInventoryRowById(invRowId);
		if (medInvRow.isPresent()) {
			return medInvRow.get();
		}
		return null;
	}
	
	/**
	 * Verify if the object is valid for CRUD and return a list of errors, if any.
	 *
	 * @param medicalInventoryRow
	 * @throws OHDataValidationException
	 */
	private void validateMedicalInventoryRow(MedicalInventoryRow medicalInventoryRow) throws OHDataValidationException {
		List<OHExceptionMessage> errors = new ArrayList<>();
		
		if (medicalInventoryRow.getInventory() == null) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.inventory.pleaseinsertinventory.msg")));
		}
		if (medicalInventoryRow.getMedical() == null) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.inventory.pleaseinsertmedical.msg")));
		}
		
		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
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
		return ioOperation.getMedicalInventoryRowByMedicalCodeAndLotCode(medicalCode, lotCode);
	}
}
