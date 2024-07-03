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

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.isf.generaldata.MessageBundle;
import org.isf.medicalinventory.model.MedicalInventory;
import org.isf.medicalinventory.model.MedicalInventoryRow;
import org.isf.medicalinventory.service.MedicalInventoryIoOperation;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.ward.model.Ward;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class MedicalInventoryManager {

	private MedicalInventoryIoOperation ioOperations;

	private MedicalInventoryRowManager medicalInventoryRowManager;

	public MedicalInventoryManager(MedicalInventoryIoOperation medicalInventoryIoOperation, MedicalInventoryRowManager medicalInventoryRowManager) {
		this.ioOperations = medicalInventoryIoOperation;
		this.medicalInventoryRowManager = medicalInventoryRowManager;
	}

	/**
	 * Insert a new {@link MedicalInventory}.
	 *
	 * @param medicalInventory - the {@link MedicalInventory} to insert.
	 * @return the newly persisted {@link MedicalInventory} object.
	 * @throws OHServiceException
	 */
	public MedicalInventory newMedicalInventory(MedicalInventory medicalInventory) throws OHServiceException {
		validationMedicalInventory(medicalInventory);
		return ioOperations.newMedicalInventory(medicalInventory);
	}
	
	/**
	 * Update an existing {@link MedicalInventory}.
	 *
	 * @param medicalInventory - the {@link MedicalInventory} to update.
	 * @return the updated {@link MedicalInventory} object.
	 * @throws OHServiceException
	 */
	public MedicalInventory updateMedicalInventory(MedicalInventory medicalInventory) throws OHServiceException {
		validationMedicalInventory(medicalInventory);
		return ioOperations.updateMedicalInventory(medicalInventory);
	}
	
	/**
	 * Delete the specified {@link MedicalInventory}.
	 * @param medicalInventory - the {@link MedicalInventory} to delete.
	 * @throws OHServiceException
	 */
	@Transactional(rollbackFor = OHServiceException.class)
	public void deleteMedicalInventory(MedicalInventory medicalInventory) throws OHServiceException {
		List<MedicalInventoryRow> inventoryRowList = medicalInventoryRowManager.getMedicalInventoryRowByInventoryId(medicalInventory.getId());
		for (MedicalInventoryRow invRow: inventoryRowList) {
			medicalInventoryRowManager.deleteMedicalInventoryRow(invRow);
		}
		ioOperations.deleteMedicalInventory(medicalInventory);
	}
	
	/**
	 * Check if the reference number is already used.
	 * 
	 * @param reference - the {@link MedicalInventory} reference.
	 * @return {@code true} if the code is already in use, {@code false} otherwise.
	 * @throws OHServiceException
	 */
	public boolean referenceExists(String reference) throws OHServiceException {
		return ioOperations.referenceExists(reference);
	}
	
	/**
	 * Return a list of {@link MedicalInventory}s for passed params.
	 *
	 * @param status - the {@link MedicalInventory} status.
	 * @param wardCode - the {@link Ward} code.
	 * @return the list of {@link MedicalInventory}s. It could be {@code empty}.
	 * @throws OHServiceException
	 */
	public List<MedicalInventory> getMedicalInventoryByStatusAndWard(String status, String wardCode) throws OHServiceException {
		return ioOperations.getMedicalInventoryByStatusAndWard(status, wardCode);
	}
	
	/**
	 * Return a list {@link MedicalInventory}s for passed params.
	 *
	 * @param status - the {@link MedicalInventory} status.
	 * @return the list of {@link MedicalInventory}s. It could be {@code empty}.
	 * @throws OHServiceException
	 */
	public List<MedicalInventory> getMedicalInventoryByStatus(String status) throws OHServiceException {
		return ioOperations.getMedicalInventoryByStatus(status);
	}
	
	/**
	 * Return a list of results {@link MedicalInventory}s for passed params.
	 *
	 * @return the list of {@link MedicalInventory}s. It could be {@code empty}.
	 * @throws OHServiceException
	 */
	public List<MedicalInventory> getMedicalInventory() throws OHServiceException {
		return ioOperations.getMedicalInventory();
	}
	
	/**
	 * Return a list of {@link MedicalInventory}s for passed params.
	 * 
	 * @param dateFrom - the lowest date for the range.
	 * @param dateTo - the highest date for the range.
	 * @param status - the {@link MedicalInventory} status.
	 * @param type - the {@link MedicalInventory} type.
	 * @return the list of {@link MedicalInventory}s. It could be {@code empty}.
	 * @throws OHServiceException
	 */
	public List<MedicalInventory> getMedicalInventoryByParams(LocalDateTime dateFrom, LocalDateTime dateTo, String status, String type) throws OHServiceException {
		dateFrom = LocalDateTime.of(dateFrom.toLocalDate(), LocalTime.MIN);
		dateTo = LocalDateTime.of(dateTo.toLocalDate(), LocalTime.MAX);
		return ioOperations.getMedicalInventoryByParams(dateFrom, dateTo, status, type);
	}
	
	/**
	 * Return a list of {@link MedicalInventory}s for passed params.
	 
	 * @param dateFrom - the lower date for the range.
	 * @param dateTo - the highest date for the range.
	 * @param status - the {@link MedicalInventory} status.
	 * @param type - the {@link MedicalInventory} type.
	 * @param page - the page number.
	 * @param size - the page size.
	 * @return the list of {@link MedicalInventory}s. It could be {@code empty}.
	 * @throws OHServiceException
	 */
	public Page<MedicalInventory> getMedicalInventoryByParamsPageable(LocalDateTime dateFrom, LocalDateTime dateTo, String status, String type, int page, int size) throws OHServiceException {
		dateFrom = LocalDateTime.of(dateFrom.toLocalDate(), LocalTime.MIN);
		dateTo = LocalDateTime.of(dateTo.toLocalDate(), LocalTime.MAX);
		return ioOperations.getMedicalInventoryByParamsPageable(dateFrom, dateTo, status, type, page, size);
	}
	
	/**
	 * Fetch {@link MedicalInventory} with param.
	 * 
	 * @param inventoryId - the {@link MedicalInventory} id.
	 * @return {@link MedicalInventory}. It could be {@code empty}.
	 * @throws OHServiceException
	 */
	public MedicalInventory getInventoryById(Integer inventoryId) throws OHServiceException {
		return ioOperations.getInventoryById(inventoryId);
	}
	
	/**
	 * Verify if the object is valid for CRUD and return a list of errors, if any.
	 *
	 * @param medInventory
	 * @throws OHDataValidationException
	 */
	private void validationMedicalInventory(MedicalInventory medInventory) throws OHServiceException {
		List<OHExceptionMessage> errors = new ArrayList<>();
		LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);
		String reference = medInventory.getInventoryReference();
		Integer inventoryId = medInventory.getId();
		if (medInventory.getInventoryDate() == null) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.inventory.pleaseinsertavalidinventorydate.msg")));
		}
		if (medInventory.getInventoryDate() != null && medInventory.getInventoryDate().isAfter(tomorrow)) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.inventory.notdateinfuture.msg")));
		}
		if (reference == null || reference.equals("")) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.inventory.mustenterareference.msg")));
		}
		boolean exist = ioOperations.referenceExists(reference);
		if (exist) {
			MedicalInventory medInv = ioOperations.getInventoryByReference(reference);
			if (medInv != null && medInv.getId() != inventoryId) {
				System.out.println(inventoryId+" "+medInv.getId());
				errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.inventory.referencealreadyused.msg")));
			}
		}
		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
	}
}
