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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.isf.pricesothers.service;

import java.util.List;

import org.isf.pricesothers.model.PricesOthers;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor=OHServiceException.class)
@TranslateOHServiceException
public class PriceOthersIoOperations {

	@Autowired
	private PriceOthersIoOperationRepository repository;
	
	/**
	 * Return the list of {@link PricesOthers}s in the DB
	 * 
	 * @return the list of {@link PricesOthers}s
	 * @throws OHServiceException 
	 */
	public List<PricesOthers> getOthers() throws OHServiceException {
		return repository.findAllByOrderByDescriptionAsc();
	}

	/**
	 * Insert a new {@link PricesOthers} in the DB
	 * 
	 * @param other - the {@link PricesOthers} to insert
	 * @return <code>true</code> if the list has been inserted, <code>false</code> otherwise
	 * @throws OHServiceException 
	 */
	public PricesOthers newOthers(PricesOthers other) throws OHServiceException {
		return repository.save(other);
	}

	/**
	 * Delete a {@link PricesOthers} in the DB
	 * 
	 * @param other - the {@link PricesOthers} to delete
	 * @return <code>true</code> if the list has been deleted, <code>false</code> otherwise
	 * @throws OHServiceException 
	 */
	public boolean deleteOthers(PricesOthers other) throws OHServiceException {
		repository.delete(other);
		return true;
	}

	/**
	 * Update a {@link PricesOthers} in the DB
	 * 
	 * @param other - the {@link PricesOthers} to update
	 * @return <code>true</code> if the list has been updated, <code>false</code> otherwise
	 * @throws OHServiceException 
	 */
	public PricesOthers updateOther(PricesOthers other) throws OHServiceException {
		return repository.save(other);
	}

	/**
	 * Checks if the code is already in use
	 *
	 * @param id - the price other code
	 * @return <code>true</code> if the code is already in use, <code>false</code> otherwise
	 * @throws OHServiceException 
	 */
	public boolean isCodePresent(Integer id) throws OHServiceException {
		return repository.existsById(id);
	}

}
