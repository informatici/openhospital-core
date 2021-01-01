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
package org.isf.opetype.manager;

import java.util.ArrayList;
import java.util.List;

import org.isf.generaldata.MessageBundle;
import org.isf.opetype.model.OperationType;
import org.isf.opetype.service.OperationTypeIoOperation;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OperationTypeBrowserManager {

	@Autowired
	private OperationTypeIoOperation ioOperations;

	/**
	 * Return the list of {@link OperationType}s
	 *
	 * @return the list of {@link OperationType}s. It could be <code>empty</code> or <code>null</code>.
	 * @throws OHServiceException
	 */
	public ArrayList<OperationType> getOperationType() throws OHServiceException {
		return ioOperations.getOperationType();
	}

	/**
	 * Insert an {@link OperationType} in the DB
	 *
	 * @param operationType - the {@link OperationType} to insert
	 * @return <code>true</code> if the {@link OperationType} has been inserted, <code>false</code> otherwise.
	 * @throws OHServiceException
	 */
	public boolean newOperationType(OperationType operationType) throws OHServiceException {
		List<OHExceptionMessage> errors = validateOperationType(operationType, true);
		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
		return ioOperations.newOperationType(operationType);
	}

	/**
	 * Update an {@link OperationType}
	 *
	 * @param operationType - the {@link OperationType} to update
	 * @return <code>true</code> if the {@link OperationType} has been updated, <code>false</code> otherwise.
	 * @throws OHServiceException
	 */
	public boolean updateOperationType(OperationType operationType) throws OHServiceException {
		List<OHExceptionMessage> errors = validateOperationType(operationType, false);
		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
		return ioOperations.updateOperationType(operationType);
	}

	/**
	 * Delete an {@link OperationType}
	 *
	 * @param operationType - the {@link OperationType} to delete
	 * @return <code>true</code> if the {@link OperationType} has been delete, <code>false</code> otherwise.
	 * @throws OHServiceException
	 */
	public boolean deleteOperationType(OperationType operationType) throws OHServiceException {
		return ioOperations.deleteOperationType(operationType);
	}

	/**
	 * Checks if an {@link OperationType} code has already been used
	 *
	 * @param code - the code
	 * @return <code>true</code> if the code is already in use, <code>false</code> otherwise.
	 * @throws OHServiceException
	 */
	public boolean codeControl(String code) throws OHServiceException {
		return ioOperations.isCodePresent(code);
	}

	protected List<OHExceptionMessage> validateOperationType(OperationType operationType, boolean insert) throws OHServiceException {
		String key = operationType.getCode();
		String description = operationType.getDescription();
		List<OHExceptionMessage> errors = new ArrayList<OHExceptionMessage>();
		if (key == null || key.isEmpty()) {
			errors.add(new OHExceptionMessage("codeEmptyError",
					MessageBundle.getMessage("angal.opetype.pleaseinsertacode"),
					OHSeverityLevel.ERROR));
		}
		else {
			if (key.length() > 2) {
				errors.add(new OHExceptionMessage("codeTooLongError",
						MessageBundle.getMessage("angal.opetype.codetoolongmaxchars"),
						OHSeverityLevel.ERROR));
			}
		}
		if (insert) {
			if (codeControl(key)) {
				errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.hospital"), MessageBundle.getMessage("angal.common.codealreadyinuse"),
						OHSeverityLevel.ERROR));
			}
		}
		if (description == null || description.isEmpty()) {
			errors.add(new OHExceptionMessage("descriptionEmptyError",
					MessageBundle.getMessage("angal.opetype.pleaseinsertavaliddescription"),
					OHSeverityLevel.ERROR));
		}
		return errors;
	}
}
