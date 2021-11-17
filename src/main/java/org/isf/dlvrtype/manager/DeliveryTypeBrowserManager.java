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
package org.isf.dlvrtype.manager;

import java.util.ArrayList;
import java.util.List;

import org.isf.dlvrtype.model.DeliveryType;
import org.isf.dlvrtype.service.DeliveryTypeIoOperation;
import org.isf.generaldata.MessageBundle;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The manager class for the DeliveryType module.
 */
@Component
public class DeliveryTypeBrowserManager {

	@Autowired
	private DeliveryTypeIoOperation ioOperations;

	/**
	 * Returns all stored {@link DeliveryType}s.
	 * In case of error a message error is shown and a <code>null</code> value is returned.
	 *
	 * @return all stored delivery types, <code>null</code> if an error occurred.
	 * @throws OHServiceException
	 */
	public List<DeliveryType> getDeliveryType() throws OHServiceException {
		return ioOperations.getDeliveryType();
	}

	/**
	 * Stores the specified {@link DeliveryType}.
	 * In case of error a message error is shown and a <code>false</code> value is returned.
	 *
	 * @param deliveryType the delivery type to store.
	 * @return <code>true</code> if the delivery type has been stored, <code>false</code> otherwise.
	 * @throws OHServiceException
	 */
	public boolean newDeliveryType(DeliveryType deliveryType) throws OHServiceException {
		validateDeliveryType(deliveryType, true);
		return ioOperations.newDeliveryType(deliveryType);
	}

	/**
	 * Updates the specified {@link DeliveryType}.
	 * In case of error a message error is shown and a <code>false</code> value is returned.
	 *
	 * @param deliveryType the delivery type to update.
	 * @return <code>true</code> if the delivery type has been update.
	 * @throws OHServiceException
	 */
	public boolean updateDeliveryType(DeliveryType deliveryType) throws OHServiceException {
		validateDeliveryType(deliveryType, false);
		return ioOperations.updateDeliveryType(deliveryType);
	}

	/**
	 * Checks if the specified code is already used by others {@link DeliveryType}s.
	 * In case of error a message error is shown and a <code>false</code> value is returned.
	 *
	 * @param code the code to check.
	 * @return <code>true</code> if the code is used, <code>false</code> otherwise.
	 * @throws OHServiceException
	 */
	public boolean isCodePresent(String code) throws OHServiceException {
		return ioOperations.isCodePresent(code);
	}

	/**
	 * Delete the specified {@link DeliveryType}.
	 * In case of error a message error is shown and a <code>false</code> value is returned.
	 *
	 * @param deliveryType the delivery type to delete.
	 * @return <code>true</code> if the delivery type has been deleted, <code>false</code> otherwise.
	 * @throws OHServiceException
	 */
	public boolean deleteDeliveryType(DeliveryType deliveryType) throws OHServiceException {
		return ioOperations.deleteDeliveryType(deliveryType);
	}

	/**
	 * Verify if the object is valid for CRUD and return a list of errors, if any
	 *
	 * @param deliveryType
	 * @param insert <code>true</code> or updated <code>false</code>
	 * @throws OHServiceException
	 */
	protected void validateDeliveryType(DeliveryType deliveryType, boolean insert) throws OHServiceException {
		List<OHExceptionMessage> errors = new ArrayList<>();
		String key = deliveryType.getCode();
		if (key.equals("")) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
					MessageBundle.getMessage("angal.common.pleaseinsertacode.msg"),
					OHSeverityLevel.ERROR));
		}
		if (key.length() > 1) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
					MessageBundle.getMessage("angal.common.thecodeistoolongmax1char.msg"),
					OHSeverityLevel.ERROR));
		}
		if (insert) {
			if (isCodePresent(key)) {
				errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
						MessageBundle.getMessage("angal.common.thecodeisalreadyinuse.msg"),
						OHSeverityLevel.ERROR));
			}
		}
		if (deliveryType.getDescription().equals("")) {
			errors.add(
					new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
							MessageBundle.getMessage("angal.common.pleaseinsertavaliddescription.msg"),
							OHSeverityLevel.ERROR));
		}
		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
	}
}
