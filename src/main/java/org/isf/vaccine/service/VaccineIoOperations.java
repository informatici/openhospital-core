/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2021 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.vaccine.service;

import java.util.List;

import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.isf.vaccine.model.Vaccine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class offers the io operations for recovering and managing
 * vaccine records from the database
 *
 * @author Eva
 * 
 * modification history
 * 20/10/2011 - Cla - insert vaccinetype managment
 */
@Service
@Transactional(rollbackFor=OHServiceException.class)
@TranslateOHServiceException
public class VaccineIoOperations {

	@Autowired
	private VaccineIoOperationRepository repository;
	
	/**
	 * Returns the list of {@link Vaccine}s based on vaccine type code
	 *
	 * @param vaccineTypeCode - the type code. If <code>null</code> returns all {@link Vaccine}s in the DB
	 * @return the list of {@link Vaccine}s
	 * @throws OHServiceException 
	 */
	public List<Vaccine> getVaccine(String vaccineTypeCode) throws OHServiceException {
		return vaccineTypeCode != null ?
				repository.findByVaccineType_CodeOrderByDescriptionAsc(vaccineTypeCode) :
				repository.findAllByOrderByDescriptionAsc();
	}

	/**
	 * Inserts a new {@link Vaccine} in the DB
	 *
	 * @param vaccine - the item to insert
	 * @return vaccine that has been inserted
	 * @throws OHServiceException 
	 */
	public Vaccine newVaccine(Vaccine vaccine) throws OHServiceException {
		return repository.save(vaccine);
	}
	
	/**
	 * Updates a {@link Vaccine} in the DB
	 *
	 * @param vaccine - the item to update
	 * @return vaccine that has been updated
	 * @throws OHServiceException 
	 */
	public Vaccine updateVaccine(Vaccine vaccine) throws OHServiceException {
		return repository.save(vaccine);
	}

	/**
	 * Deletes a {@link Vaccine} in the DB
	 *
	 * @param vaccine - the item to delete
	 * @return <code>true</code> if the item has been deleted, <code>false</code> otherwise
	 * @throws OHServiceException 
	 */
	public boolean deleteVaccine(Vaccine vaccine) throws OHServiceException {
		repository.delete(vaccine);
		return true;
	}

	/**
	 * Checks if the code is already in use
	 *
	 * @param code - the vaccine code
	 * @return <code>true</code> if the code is already in use, <code>false</code> otherwise
	 * @throws OHServiceException 
	 */
	public boolean isCodePresent(String code) throws OHServiceException {
		return repository.existsById(code);
	}
	
	/**
	 * Returns the {@link Vaccine} based on code
	 *
	 * @param code - the code, must not be {@literal null}
	 * @return the {@link Vaccine} or {@literal null} if none found
	 * @throws IllegalArgumentException if {@code code} is {@literal null}
	 */
	public Vaccine findVaccine(String code) {
		if (code != null) {
			return repository.findById(code).orElse(null);
		}
		throw new IllegalArgumentException("code must not be null");
	}

}
