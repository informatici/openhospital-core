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
package org.isf.opetype.manager;

import java.util.ArrayList;
import java.util.List;

import org.isf.generaldata.MessageBundle;
import org.isf.opetype.model.OperationType;
import org.isf.opetype.service.OperationTypeIoOperation;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.springframework.stereotype.Component;

@Component
public class OperationTypeBrowserManager {

	private OperationTypeIoOperation ioOperations;

	public OperationTypeBrowserManager(OperationTypeIoOperation operationTypeIoOperation) {
		this.ioOperations = operationTypeIoOperation;
	}

	/**
	 * Return the list of {@link OperationType}s
	 *
	 * @return the list of {@link OperationType}s. It could be {@code empty} or {@code null}.
	 * @throws OHServiceException
	 */
	public List<OperationType> getOperationType() throws OHServiceException {
		return ioOperations.getOperationType();
	}

	/**
	 * Insert an {@link OperationType} in the DB
	 *
	 * @param operationType - the {@link OperationType} to insert
	 * @return the newly inserted {@link OperationType} object.
	 * @throws OHServiceException
	 */
	public OperationType newOperationType(OperationType operationType) throws OHServiceException {
		validateOperationType(operationType, true);
		return ioOperations.newOperationType(operationType);
	}

	/**
	 * Update an {@link OperationType}
	 *
	 * @param operationType - the {@link OperationType} to update
	 * @return the newly updated {@link OperationType} object.
	 * @throws OHServiceException
	 */
	public OperationType updateOperationType(OperationType operationType) throws OHServiceException {
		validateOperationType(operationType, false);
		return ioOperations.updateOperationType(operationType);
	}

	/**
	 * Delete an {@link OperationType} object. If the object does not exist it is silently ignored.  If the
	 * object is null a {@link OHServiceException} is thrown.
	 *
	 * @param operationType - the {@link OperationType} to delete
	 * @throws OHServiceException
	 */
	public void deleteOperationType(OperationType operationType) throws OHServiceException {
		ioOperations.deleteOperationType(operationType);
	}

	/**
	 * Checks if an {@link OperationType} code has already been used
	 *
	 * @param code - the code
	 * @return {@code true} if the code is already in use, {@code false} otherwise.
	 * @throws OHServiceException
	 */
	public boolean isCodePresent(String code) throws OHServiceException {
		return ioOperations.isCodePresent(code);
	}

	protected void validateOperationType(OperationType operationType, boolean insert) throws OHServiceException {
		String key = operationType.getCode();
		String description = operationType.getDescription();
		List<OHExceptionMessage> errors = new ArrayList<>();
		if (key == null || key.isEmpty()) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.pleaseinsertacode.msg")));
		} else {
			if (key.length() > 2) {
				errors.add(new OHExceptionMessage(MessageBundle.formatMessage("angal.common.thecodeistoolongmaxchars.fmt.msg", 2)));
			}
		}
		if (insert && isCodePresent(key)) {
				errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.thecodeisalreadyinuse.msg")));
		}
		if (description == null || description.isEmpty()) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.pleaseinsertavaliddescription.msg")));
		}
		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
	}

}
