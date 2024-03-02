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

import java.time.LocalDateTime;
import java.util.List;

import org.isf.medicalinventory.model.MedicalInventory;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.isf.ward.model.Ward;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = OHServiceException.class)
@TranslateOHServiceException
public class MedicalInventoryIoOperation {

	@Autowired
	private MedicalInventoryIoOperationRepository repository;
	
	/**
	 * Insert a new MedicalInventory exam {@link MedicalInventory} and return the generated MedicalInventory.
	 *
	 * @param medicalInventory - the {@link MedicalInventory} to insert
	 * @return the newly persisted {@link MedicalInventory} object.
	 * @throws OHServiceException
	 */
	public MedicalInventory newMedicalInventory(MedicalInventory medicalinventory) throws OHServiceException {
		return repository.save(medicalinventory);
	}
	
	/**
	 * Update an existing MedicalInventory {@link MedicalInventory}.
	 *
	 * @param medicalInventory - the {@link MedicalInventory} to update
	 * @return the updated {@link MedicalInventory} object.
	 * @throws OHServiceException
	 */
	public MedicalInventory updateMedicalInventoryRow(MedicalInventory medicalInventory) throws OHServiceException {
		return repository.save(medicalInventory);
	}
	
	/**
	 * Deletes the specified {@link MedicalInventory}.
	 * @param medicalInventory - the {@link MedicalInventory} to delete.
	 * @throws OHServiceException if an error occurs during the medicalInventory deletion.
	 */
	public void deleteMedicalInventory(MedicalInventory medicalInventory) throws OHServiceException {
		repository.delete(medicalInventory);
	}
	
	/**
	 * check if the reference number is already used
	 * 
	 * @param reference - the MedicalInventory reference
	 * @return {@code true} if the code is already in use, {@code false} otherwise
	 * @throws OHServiceException
	 */
	public boolean referenceExists(String reference) {
		MedicalInventory medInv = repository.findByReference(reference);
		if (medInv != null)  {
			return true;
		}
		return false;
	}
	
	/**
	 * Return a list of results ({@link MedicalInventory}s) for passed params.
	 *
	 * @param status - the {@link MedicalInventory} status.
	 * @param wardCode - the {@link Ward} code.
	 * @return the list of {@link MedicalInventory}s. It could be {@code empty}
	 * @throws OHServiceException
	 */
	public List<MedicalInventory> getMedicalInventoryByStatusAndWard(String status, String wardCode) throws OHException {
		return repository.findInventoryByStatusAnvWardCode(status, wardCode);
	}
	
	/**
	 * Return a list of results ({@link MedicalInventory}s) for passed params.
	 *
	 * @param status - the {@link MedicalInventory} status.
	 * @return the list of {@link MedicalInventory}s. It could be {@code empty}
	 * @throws OHServiceException
	 */
	public List<MedicalInventory> getMedicalInventoryByStatus(String status) throws OHException {
		return repository.findInventoryByStatus(status);
	}
	
	/**
	 * Return a list of results ({@link MedicalInventory}s) for passed params.
	 *
	 * @return the list of {@link MedicalInventory}s. It could be {@code empty}
	 * @throws OHServiceException
	 */
	public List<MedicalInventory> getMedicalInventory() throws OHException {
		return repository.findAll();
	}
	
	/**
	 * Return a list of results ({@link MedicalInventory}s) for passed params.
	 * 
	 * @param dateFrom - the lower date for the range
	 * @param dateTo - the highest date for the range
	 * @param status - the {@link MedicalInventory} status.
	 * @param type - the {@link MedicalInventory} type.
	 * @return the list of {@link MedicalInventory}s. It could be {@code empty}
	 * @throws OHServiceException
	 */
	public List<MedicalInventory> getMedicalInventoryByParams(LocalDateTime dateFrom, LocalDateTime dateTo, String status, String type) throws OHException {
		return repository.findInventoryByParams(dateFrom, dateTo, status, type);
	}
	
	/**
	 * Return a list of results ({@link MedicalInventory}s) for passed params.
	 
	 * @param dateFrom - the lower date for the range
	 * @param dateTo - the highest date for the range
	 * @param status - the {@link MedicalInventory} status.
	 * @param type - the {@link MedicalInventory} type.
	 * @param page - the page number.
	 * @param size - the page size.
	 * @return the list of {@link MedicalInventory}s. It could be {@code empty}
	 * @throws OHServiceException
	 */
	public Page<MedicalInventory> getMedicalInventoryByParamsPageable(LocalDateTime dateFrom, LocalDateTime dateTo, String status, String type, int page, int size) throws OHException {
		Pageable pageable = PageRequest.of(page, size);
		return repository.findInventoryByParamsPageable(dateFrom, dateTo, status, type, pageable);
	}
	
	/**
	 * Checks if the code is already in use
	 *
	 * @param id - the MedicalInventory code
	 * @return {@code true} if the code is already in use, {@code false} otherwise
	 * @throws OHServiceException 
	 */
	public boolean isCodePresent(Integer id) throws OHServiceException {
		return repository.existsById(id);
	}
}
