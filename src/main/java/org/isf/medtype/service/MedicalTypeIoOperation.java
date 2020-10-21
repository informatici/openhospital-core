/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2020 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.medtype.service;

import org.isf.medtype.model.MedicalType;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

/**
 * Persistence class for the medical type module.
 */
@Service
@Transactional(rollbackFor=OHServiceException.class)
@TranslateOHServiceException
public class MedicalTypeIoOperation {

	@Autowired
	private MedicalTypeIoOperationRepository repository;

	/**
	 * Retrieves all the stored {@link MedicalType}s.
	 * @return the stored medical types.
	 * @throws OHServiceException if an error occurs retrieving the medical types.
	 */
	public ArrayList<MedicalType> getMedicalTypes() throws OHServiceException 
	{
		return new ArrayList<MedicalType>(repository.findAllByOrderByDescriptionAsc()); 
	}

	/**
	 * Updates the specified {@link MedicalType}.
	 * @param medicalType the medical type to update.
	 * @return <code>true</code> if the medical type has been updated, <code>false</code> otherwise.
	 * @throws OHServiceException if an error occurs updating the medical type.
	 */
	public boolean updateMedicalType(
			MedicalType medicalType) throws OHServiceException 
	{
		boolean result = true;
	

		MedicalType savedMedicalType = repository.save(medicalType);
		result = (savedMedicalType != null);
		
		return result;
	}

	/**
	 * Stores the specified {@link MedicalType}.
	 * @param medicalType the medical type to store.
	 * @return <code>true</code> if the medical type has been stored, <code>false</code> otherwise.
	 * @throws OHServiceException if an error occurs storing the new medical type.
	 */
	public boolean newMedicalType(
			MedicalType medicalType) throws OHServiceException 
	{
		boolean result = true;
	

		MedicalType savedMedicalType = repository.save(medicalType);
		result = (savedMedicalType != null);
		
		return result;
	}

	/**
	 * Deletes the specified {@link MedicalType}.
	 * @param medicalType the medical type to delete.
	 * @return <code>true</code> if the medical type has been deleted, <code>false</code> otherwise.
	 * @throws OHServiceException if an error occurs deleting the medical type.
	 */
	public boolean deleteMedicalType(
			MedicalType medicalType) throws OHServiceException 
	{
		boolean result = true;
	
		
		repository.delete(medicalType);
		
		return result;
	}

	/**
	 * Checks if the specified {@link MedicalType} code is already stored.
	 * @param code the {@link MedicalType} code to check.
	 * @return <code>true</code> if the medical code is already stored, <code>false</code> otherwise.
	 * @throws OHServiceException if an error occurs during the check.
	 */
	public boolean isCodePresent(
			String code) throws OHServiceException 
	{
		return repository.existsById(code);
	}
}
