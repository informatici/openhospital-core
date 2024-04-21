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
package org.isf.dlvrrestype.service;

import java.util.List;

import org.isf.dlvrrestype.model.DeliveryResultType;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Persistence class for DeliveryResultType module.
 */
@Service
@Transactional(rollbackFor=OHServiceException.class)
@TranslateOHServiceException
public class DeliveryResultTypeIoOperation {

	private DeliveryResultIoOperationRepository repository;

	public DeliveryResultTypeIoOperation(DeliveryResultIoOperationRepository deliveryResultIoOperationRepository) {
		this.repository = deliveryResultIoOperationRepository;
	}

	/**
	 * Returns all stored {@link DeliveryResultType}s.
	 * @return the stored {@link DeliveryResultType}s.
	 * @throws OHServiceException if an error occurs retrieving the stored delivery result types.
	 */
	public List<DeliveryResultType> getDeliveryResultType() throws OHServiceException {
		return repository.findAllByOrderByDescriptionAsc();
	}

	/**
	 * Updates the specified {@link DeliveryResultType}.
	 * @param deliveryResultType the delivery result type to update.
	 * @return the updated {@link DeliveryResultType}.
	 * @throws OHServiceException if an error occurs during the update.
	 */
	public DeliveryResultType updateDeliveryResultType(DeliveryResultType deliveryResultType) throws OHServiceException {
		return repository.save(deliveryResultType);
	}

	/**
	 * Stores the specified {@link DeliveryResultType}.
	 * @param deliveryResultType the delivery result type to store.
	 * @return the new {@link DeliveryResultType}.
	 * @throws OHServiceException if an error occurs during the store operation.
	 */
	public DeliveryResultType newDeliveryResultType(DeliveryResultType deliveryResultType) throws OHServiceException {
		return repository.save(deliveryResultType);
	}

	/**
	 * Deletes the specified {@link DeliveryResultType}.
	 * @param deliveryResultType the delivery result type to delete.
	 * @throws OHServiceException if an error occurs during the delete operation.
	 */
	public void deleteDeliveryResultType(DeliveryResultType deliveryResultType) throws OHServiceException {
		repository.delete(deliveryResultType);
	}

	/**
	 * Checks if the specified code is already used by other {@link DeliveryResultType}s.
	 * @param code the code to check.
	 * @return {@code true} if the code is used, {@code false} otherwise.
	 * @throws OHServiceException if an error occurs during the check.
	 */
	public boolean isCodePresent(String code) throws OHServiceException {
		return repository.existsById(code);
	}
}
