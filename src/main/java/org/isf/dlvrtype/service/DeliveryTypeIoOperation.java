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
package org.isf.dlvrtype.service;

import java.util.List;

import org.isf.dlvrtype.model.DeliveryType;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * The persistence class for the DeliveryType module.
 */
@Service
@Transactional(rollbackFor=OHServiceException.class)
@TranslateOHServiceException
public class DeliveryTypeIoOperation {

	private DeliveryTypeIoOperationRepository repository;

	public DeliveryTypeIoOperation(DeliveryTypeIoOperationRepository deliveryTypeIoOperationRepository) {
		this.repository = deliveryTypeIoOperationRepository;
	}

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
	 * @return the updated {@link DeliveryType} object.
	 * @throws OHServiceException if an error occurs during the update operation.
	 */
	public DeliveryType updateDeliveryType(DeliveryType deliveryType) throws OHServiceException {
		return repository.save(deliveryType);
	}

	/**
	 * Stores the specified {@link DeliveryType}.
	 * @param deliveryType the delivery type to store.
	 * @return the newly saved {@link DeliveryType} object.
	 * @throws OHServiceException if an error occurred during the store operation.
	 */
	public DeliveryType newDeliveryType(DeliveryType deliveryType) throws OHServiceException {
		return repository.save(deliveryType);
	}

	/**
	 * Delete the specified {@link DeliveryType}.
	 * @param deliveryType the delivery type to delete.
	 * @throws OHServiceException if an error occurred during the delete operation.
	 */
	public void deleteDeliveryType(DeliveryType deliveryType) throws OHServiceException {
		repository.delete(deliveryType);
	}

	/**
	 * Checks if the specified code is already used by others {@link DeliveryType}s.
	 * @param code the code to check.
	 * @return {@code true} if the code is used, {@code false} otherwise.
	 * @throws OHServiceException if an error occurs during the check.
	 */
	public boolean isCodePresent(String code) throws OHServiceException {
		return repository.existsById(code);
	}
}
