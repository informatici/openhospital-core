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
package org.isf.exatype.manager;

import java.util.ArrayList;
import java.util.List;

import org.isf.exatype.model.ExamType;
import org.isf.exatype.service.ExamTypeIoOperation;
import org.isf.generaldata.MessageBundle;
import org.isf.utils.exception.OHDataIntegrityViolationException;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.springframework.stereotype.Component;

@Component
public class ExamTypeBrowserManager {

	private ExamTypeIoOperation ioOperations;

	public ExamTypeBrowserManager(ExamTypeIoOperation examTypeIoOperation) {
		this.ioOperations = examTypeIoOperation;
	}

	/**
	 * Verify if the object is valid for CRUD and return a list of errors, if any
	 *
	 * @param examType
	 * @param insert {@code true} or updated {@code false}
	 * @throws OHServiceException
	 */
	protected void validateExamType(ExamType examType, boolean insert) throws OHServiceException {
		String key = examType.getCode();
		String description = examType.getDescription();
		List<OHExceptionMessage> errors = new ArrayList<>();
		if (key.isEmpty()) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.pleaseinsertacode.msg")));
		}
		if (key.length() > 2) {
			errors.add(new OHExceptionMessage(MessageBundle.formatMessage("angal.common.thecodeistoolongmaxchars.fmt.msg", 2)));
		}
		if (description.isEmpty()) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.pleaseinsertavaliddescription.msg")));
		}
		if (insert && isCodePresent(examType.getCode())) {
			throw new OHDataIntegrityViolationException(new OHExceptionMessage(MessageBundle.getMessage("angal.common.thecodeisalreadyinuse.msg")));
		}
		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
	}

	/**
	 * Return the list of {@link ExamType}s.
	 *
	 * @return the list of {@link ExamType}s. It could be {@code null}
	 * @throws OHServiceException
	 */
	public List<ExamType> getExamType() throws OHServiceException {
		return ioOperations.getExamType();
	}

	/**
	 * Insert a new {@link ExamType} into the DB.
	 *
	 * @param examType - the {@link ExamType} to insert.
	 * @return the newly inserted {@link ExamType}.
	 * @throws OHServiceException
	 */
	public ExamType newExamType(ExamType examType) throws OHServiceException {
		validateExamType(examType, true);
		return ioOperations.newExamType(examType);
	}

	/**
	 * Update an already existing {@link ExamType}.
	 *
	 * @param examType - the {@link ExamType} to update
	 * @return the updated {@link ExamType}.
	 * @throws OHServiceException
	 */
	public ExamType updateExamType(ExamType examType) throws OHServiceException {
		validateExamType(examType, false);
		return ioOperations.updateExamType(examType);
	}

	/**
	 * This checks for the presence of a record with the same code as in
	 * the parameter.
	 *
	 * @param code - the code
	 * @return {@code true} if the code is present, {@code false} otherwise.
	 * @throws OHServiceException
	 */
	public boolean isCodePresent(String code) throws OHServiceException {
		return ioOperations.isCodePresent(code);
	}

	/**
	 * Delete the passed {@link ExamType}.
	 *
	 * @param examType - the {@link ExamType} to delete.
	 * @throws OHServiceException
	 */
	public void deleteExamType(ExamType examType) throws OHServiceException {
		ioOperations.deleteExamType(examType);
	}
}
