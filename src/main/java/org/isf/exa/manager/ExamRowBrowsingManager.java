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
package org.isf.exa.manager;

import java.util.ArrayList;
import java.util.List;

import org.isf.exa.model.ExamRow;
import org.isf.exa.service.ExamRowIoOperations;
import org.isf.generaldata.MessageBundle;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ExamRowBrowsingManager {

	@Autowired
	private ExamRowIoOperations ioOperations;

	/**
	 * Verify if the object is valid for CRUD and return a list of errors, if any
	 *
	 * @param examRow
	 * @throws OHDataValidationException
	 */
	protected void validateExamRow(ExamRow examRow) throws OHDataValidationException {
		String description = examRow.getDescription();
		List<OHExceptionMessage> errors = new ArrayList<>();
		if (description.isEmpty()) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
					MessageBundle.getMessage("angal.common.pleaseinsertavaliddescription.msg"),
					OHSeverityLevel.ERROR));
		}
		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
	}

	/**
	 * Returns the list of {@link ExamRow}s
	 *
	 * @return the list of {@link ExamRow}s. It could be <code>null</code>
	 * @throws OHServiceException
	 */
	public List<ExamRow> getExamRow() throws OHServiceException {
		return this.getExamRow(0, null);
	}

	/**
	 * Returns a list of {@link ExamRow}s that matches passed exam code
	 *
	 * @param aExamCode - the exam code
	 * @return the list of {@link ExamRow}s. It could be <code>null</code>
	 * @throws OHServiceException
	 */
	public List<ExamRow> getExamRow(int aExamCode) throws OHServiceException {
		return this.getExamRow(aExamCode, null);
	}

	/**
	 * Returns a list of {@link ExamRow}s that matches passed exam code and description
	 *
	 * @param aExamRowCode - the exam code
	 * @param aDescription - the exam description
	 * @return the list of {@link ExamRow}s. It could be <code>null</code>
	 * @throws OHServiceException
	 */
	public List<ExamRow> getExamRow(int aExamRowCode, String aDescription) throws OHServiceException {
		return ioOperations.getExamRow(aExamRowCode, aDescription);
	}

	/**
	 * Insert a new {@link ExamRow} in the DB.
	 *
	 * @param examRow - the {@link ExamRow} to insert
	 * @return <code>true</code> if the {@link ExamRow} has been inserted, <code>false</code> otherwise
	 * @throws OHServiceException
	 */
	public ExamRow newExamRow(ExamRow examRow) throws OHServiceException {
		validateExamRow(examRow);
		return ioOperations.newExamRow(examRow);
	}

	/**
	 * Delete an {@link ExamRow}.
	 *
	 * @param examRow - the {@link ExamRow} to delete
	 * @return <code>true</code> if the {@link ExamRow} has been deleted, <code>false</code> otherwise
	 * @throws OHServiceException
	 */
	public boolean deleteExamRow(ExamRow examRow) throws OHServiceException {
		return ioOperations.deleteExamRow(examRow);
	}

	/**
	 * Returns a list of {@link ExamRow}s that matches passed exam code
	 *
	 * @param aExamCode - the exam code
	 * @return the list of {@link ExamRow}s. It could be <code>null</code>
	 * @throws OHServiceException
	 */
	public List<ExamRow> getExamRowByExamCode(String aExamCode) throws OHServiceException {
		return ioOperations.getExamRowByExamCode(aExamCode);
	}
}
