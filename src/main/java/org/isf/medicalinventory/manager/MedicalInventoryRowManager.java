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

import java.util.List;

import org.isf.medicalinventory.model.MedicalInventoryRow;
import org.isf.medicalinventory.service.MedicalInventoryRowIoOperation;
import org.isf.medicalstock.manager.MovStockInsertingManager;
import org.isf.utils.exception.OHServiceException;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;

@Component
public class MedicalInventoryRowManager {

	private MedicalInventoryRowIoOperation iOoperation;
	private MovStockInsertingManager movStockInsertingManager;

	public MedicalInventoryRowManager(MedicalInventoryRowIoOperation medicalInventoryRowIoOperation, MovStockInsertingManager movStockInsertingManager) {
		this.iOoperation = medicalInventoryRowIoOperation;
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
		return iOoperation.newMedicalInventoryRow(medicalInventoryRow);
	}
	
	/**
	 * Update an existing {@link MedicalInventoryRow}.
	 *
	 * @param medicalInventoryRow - the {@link MedicalInventoryRow} to update.
	 * @return the updated {@link MedicalInventoryRow} object.
	 * @throws OHServiceException
	 */
	public MedicalInventoryRow updateMedicalInventoryRow(MedicalInventoryRow medicalInventoryRow) throws OHServiceException {
		return iOoperation.updateMedicalInventoryRow(medicalInventoryRow);
	}
	
	/**
	 * Delete the specified {@link MedicalInventoryRow}.
	 * @param medicalInventoryRow - the {@link MedicalInventoryRow} to delete.
	 * @throws OHServiceException
	 */
	public void deleteMedicalInventoryRow(MedicalInventoryRow medicalInventoryRow) throws OHServiceException {
		iOoperation.deleteMedicalInventoryRow(medicalInventoryRow);
	}
	
	/**
	 * Return a list of {@link MedicalInventoryRow}s for passed params.
	 *
	 * @param inventoryId - the Inventory Id.
	 * @return the list of {@link MedicalInventoryRow}s. It could be {@code empty}.
	 * @throws OHServiceException
	 */
	public List<MedicalInventoryRow> getMedicalInventoryRowByInventoryId(int inventoryId) throws OHServiceException {
		return iOoperation.getMedicalInventoryRowByInventoryId(inventoryId);
	}

	/**
	 * Return a list of {@link MedicalInventoryRow}s by {@link Inventory} Id and {@link Medical} code.
	 * 
	 * @param inventoryId - the Inventory Id.
	 * @param medicalCode - the medical code.
	 * @return the list of {@link MedicalInventoryRow}s.
	 * @throws OHServiceException
	 */
	public List<MedicalInventoryRow> getMedicalInventoryRowByInventoryIdAndMedicalCode(int inventoryId,
			String medicalCode) throws OHServiceException {
		return iOoperation.getMedicalInventoryRowByInventoryIdAndMedicalCode(inventoryId, medicalCode);
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
			if (invRow.isNewLot()) {
				this.deleteMedicalInventoryRow(invRow);
				movStockInsertingManager.deleteLot(invRow.getLot());		
			} else {
				this.deleteMedicalInventoryRow(invRow);
			}	
		}
	}
}
