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
package org.isf.vactype.service;

import java.util.List;

import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.isf.vactype.model.VaccineType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor=OHServiceException.class)
@TranslateOHServiceException
public class VacTypeIoOperation {

	private VaccineTypeIoOperationRepository repository;

	public VacTypeIoOperation(VaccineTypeIoOperationRepository vaccineTypeIoOperationRepository) {
		this.repository = vaccineTypeIoOperationRepository;
	}

	/**
	 * Returns all {@link VaccineType}s from the DB.
	 * 	
	 * @return the list of {@link VaccineType}s
	 * @throws OHServiceException 
	 */
	public List<VaccineType> getVaccineType() throws OHServiceException {
		return repository.findAllByOrderByDescriptionAsc();
	}
	
	/**
	 * Inserts a new {@link VaccineType} into the DB.
	 * 
	 * @param vaccineType - the {@link VaccineType} to insert 
	 * @return the newly inserted {@link VaccineType} object.
	 * @throws OHServiceException 
	 */
	public VaccineType newVaccineType(VaccineType vaccineType) throws OHServiceException {
		return repository.save(vaccineType);
	}
	
	/**
	 * Updates a {@link VaccineType} in the DB,
	 *
	 * @param vaccineType - the item to update
	 * @return the updated {@link VaccineType} object.
	 * @throws OHServiceException 
	 */
	public VaccineType updateVaccineType(VaccineType vaccineType) throws OHServiceException	{
		return repository.save(vaccineType);
	}
	
	/**
	 * Deletes a {@link VaccineType} in the DB.
	 *
	 * @param vaccineType - the item to delete
	 * @throws OHServiceException
	 */
	public void deleteVaccineType(VaccineType vaccineType) throws OHServiceException {
		repository.delete(vaccineType);
	}
	
	
	/**
	 * Checks if the code is already in use.
	 *
	 * @param code - the {@link VaccineType} code
	 * @return {@code true} if the code is already in use, {@code false} otherwise
	 * @throws OHServiceException 
	 */
	public boolean isCodePresent(String code) throws OHServiceException {
		return repository.existsById(code);
	}
	
	/**
	 * Returns the {@link VaccineType} based on code
	 *
	 * @param code - the code, must not be {@literal null}
	 * @return the {@link VaccineType} or {@literal null} if none found
	 * @throws IllegalArgumentException if {@code code} is {@literal null}
	 */
	public VaccineType findVaccineType(String code) {
		if (code != null) {
			return repository.findById(code).orElse(null);
		}
		throw new IllegalArgumentException("VaccineType code must not be null.");
	}

}
