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
package org.isf.pricesothers.service;

import java.util.List;

import org.isf.pricesothers.model.PricesOthers;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor=OHServiceException.class)
@TranslateOHServiceException
public class PriceOthersIoOperations {

	private PriceOthersIoOperationRepository repository;

	public PriceOthersIoOperations(PriceOthersIoOperationRepository priceOthersIoOperationRepository) {
		this.repository = priceOthersIoOperationRepository;
	}

	/**
	 * Return the list of {@link PricesOthers}s.
	 * 
	 * @return the list of {@link PricesOthers}s
	 * @throws OHServiceException 
	 */
	public List<PricesOthers> getOthers() throws OHServiceException {
		return repository.findAllByOrderByDescriptionAsc();
	}

	/**
	 * Insert a new {@link PricesOthers}.
	 * 
	 * @param other - the {@link PricesOthers} to insert
	 * @return the newly inserted {@link PricesOthers} object.
	 * @throws OHServiceException 
	 */
	public PricesOthers newOthers(PricesOthers other) throws OHServiceException {
		return repository.save(other);
	}

	/**
	 * Delete a {@link PricesOthers} in the DB
	 * 
	 * @param other - the {@link PricesOthers} to delete
	 * @throws OHServiceException
	 */
	public void deleteOthers(PricesOthers other) throws OHServiceException {
		repository.delete(other);
	}

	/**
	 * Update a {@link PricesOthers}.
	 * 
	 * @param other - the {@link PricesOthers} to update
	 * @return the updated {@link PricesOthers} object.
	 * @throws OHServiceException 
	 */
	public PricesOthers updateOther(PricesOthers other) throws OHServiceException {
		return repository.save(other);
	}

	/**
	 * Checks if the code is already in use.
	 *
	 * @param id - the price other code
	 * @return {@code true} if the code is already in use, {@code false} otherwise
	 * @throws OHServiceException 
	 */
	public boolean isCodePresent(Integer id) throws OHServiceException {
		return repository.existsById(id);
	}

}
