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
package org.isf.supplier.service;

import java.util.List;

import org.isf.generaldata.ExaminationParameters;
import org.isf.supplier.model.Supplier;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Mwithi
 */
@Service
@Transactional(rollbackFor=OHServiceException.class)
@TranslateOHServiceException
public class SupplierOperations {

	private SupplierIoOperationRepository repository;
	
	public SupplierOperations(SupplierIoOperationRepository supplierIoOperationRepository) {
		this.repository = supplierIoOperationRepository;
		ExaminationParameters.initialize();
	}

	/**
	 * Save or update a {@link Supplier}.
	 * @param supplier - the {@link Supplier} to save or update
	 * return the recently saved or updated {@link Supplier} object.
	 * @throws OHServiceException 
	 */
	public Supplier saveOrUpdate(Supplier supplier) throws OHServiceException {
		return repository.save(supplier);
	}

	/**
	 * Returns a {@link Supplier} with specified ID
	 * @param id - supplier ID
	 * @return supplier - the {@link Supplier} object with specified ID or {@code null} if not found
	 * @throws OHServiceException 
	 */
	public Supplier getByID(int id) throws OHServiceException {
		return repository.findById(id).orElse(null);
	}
	
	/**
	 * Returns the list of all {@link Supplier}s, active and inactive
	 * @return supList - the list of {@link Supplier}s
	 * @throws OHServiceException 
	 */
	public List<Supplier> getAll() throws OHServiceException {
		return repository.findAll();
	}

	/**
	 * Returns the list of active {@link Supplier}s
	 * @return supList - the list of {@link Supplier}s
	 * @throws OHServiceException 
	 */
	public List<Supplier> getList() throws OHServiceException {
		return repository.findAllWhereNotDeleted();
	}
}
