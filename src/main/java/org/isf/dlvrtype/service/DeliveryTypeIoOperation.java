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
package org.isf.dlvrtype.service;

import java.util.List;

import org.isf.dlvrtype.model.DeliveryType;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * The persistence class for the DeliveryType module.
 */
@Service
@Transactional(rollbackFor=OHServiceException.class)
@TranslateOHServiceException
public class DeliveryTypeIoOperation {
	
	@Autowired
	private DeliveryTypeIoOperationRepository repository;
	    
	/**
	 * Returns all stored {@link DeliveryType}s.
	 * @return all stored delivery types.
	 * @throws OHServiceException if an error occurs retrieving the delivery types. 
	 */
	public List<DeliveryType> getDeliveryType() throws OHServiceException {
		return repository.findAll();
	}

	/**
	 * Updates the specified {@link DeliveryType}.
	 * @param deliveryType the delivery type to update.
	 * @return <code>true</code> if the delivery type has been update.
	 * @throws OHServiceException if an error occurs during the update operation.
	 */
	public boolean updateDeliveryType(DeliveryType deliveryType) throws OHServiceException {
		return repository.save(deliveryType) != null;
	}

	/**
	 * Stores the specified {@link DeliveryType}.
	 * @param deliveryType the delivery type to store.
	 * @return <code>true</code> if the delivery type has been stored, <code>false</code> otherwise.
	 * @throws OHServiceException if an error occurred during the store operation.
	 */
	public boolean newDeliveryType(DeliveryType deliveryType) throws OHServiceException {
		return repository.save(deliveryType) != null;
	}

	/**
	 * Delete the specified {@link DeliveryType}.
	 * @param deliveryType the delivery type to delete.
	 * @return <code>true</code> if the delivery type has been deleted, <code>false</code> otherwise.
	 * @throws OHServiceException if an error occurred during the delete operation.
	 */
	public boolean deleteDeliveryType(DeliveryType deliveryType) throws OHServiceException {
		repository.delete(deliveryType);
		return true;
	}

	/**
	 * Checks if the specified code is already used by others {@link DeliveryType}s.
	 * @param code the code to check.
	 * @return <code>true</code> if the code is used, <code>false</code> otherwise.
	 * @throws OHServiceException if an error occurs during the check.
	 */
	public boolean isCodePresent(String code) throws OHServiceException {
		return repository.existsById(code);
	}
}
