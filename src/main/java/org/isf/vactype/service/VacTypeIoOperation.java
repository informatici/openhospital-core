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
package org.isf.vactype.service;

/*------------------------------------------
 * IoOperation - methods to interact with DB
 * -----------------------------------------
 * modification history
 * 19/10/2011 - Cla - version is now 1.0
 *------------------------------------------*/

import java.util.ArrayList;

import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.isf.vaccine.model.Vaccine;
import org.isf.vactype.model.VaccineType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor=OHServiceException.class)
@TranslateOHServiceException
public class VacTypeIoOperation {

	@Autowired
	private VaccineTypeIoOperationRepository repository;
	
	/**
	 * returns all {@link VaccineType}s from DB	
	 * 	
	 * @return the list of {@link VaccineType}s
	 * @throws OHServiceException 
	 */
	public ArrayList<VaccineType> getVaccineType() throws OHServiceException {
		return new ArrayList<VaccineType>(repository.findAllByOrderByDescriptionAsc()); 
	}
	
	/**
	 * Inserts a new {@link VaccineType} into DB
	 * 
	 * @param vaccineType - the {@link VaccineType} to insert 
	 * @return <code>true</code> if the item has been inserted, <code>false</code> otherwise
	 * @throws OHServiceException 
	 */
	public boolean newVaccineType(VaccineType vaccineType) throws OHServiceException {
		return repository.save(vaccineType) != null;
	}
	
	/**
	 * Updates a {@link VaccineType} in the DB
	 *
	 * @param vaccineType - the item to update
	 * @return <code>true</code> if the item has been updated, <code>false</code> otherwise
	 * @throws OHServiceException 
	 */
	public boolean updateVaccineType(VaccineType vaccineType) throws OHServiceException	{
		return repository.save(vaccineType) != null;
	}
	
	/**
	 * Deletes a {@link VaccineType} in the DB
	 *
	 * @param vaccineType - the item to delete
	 * @return <code>true</code> if the item has been deleted, <code>false</code> otherwise
	 * @throws OHServiceException 
	 */
	public boolean deleteVaccineType(VaccineType vaccineType) throws OHServiceException {
		repository.delete(vaccineType);
		return true;
	}
	
	
	/**
	 * Checks if the code is already in use
	 *
	 * @param code - the {@link VaccineType} code
	 * @return <code>true</code> if the code is already in use, <code>false</code> otherwise
	 * @throws OHServiceException 
	 */
	public boolean isCodePresent(String code) throws OHServiceException {
		return repository.exists(code);
	}
	
	/**
	 * returns the {@link VaccineType} based on code
	 *
	 * @param code - the code, must not be {@literal null}
	 * @return the {@link VaccineType} or {@literal null} if none found
	 * @throws OHServiceException 
	 * @throws IllegalArgumentException if {@code code} is {@literal null}
	 */
	public VaccineType findVaccineType(String code) throws OHServiceException 
	{
		if (code != null) {
			return repository.findOne(code);
		}else
			throw new IllegalArgumentException("code must not be null");
	} 
}
