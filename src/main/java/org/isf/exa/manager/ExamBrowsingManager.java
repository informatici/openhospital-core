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

import org.isf.exa.model.Exam;
import org.isf.exa.service.ExamIoOperations;
import org.isf.exatype.model.ExamType;
import org.isf.generaldata.MessageBundle;
import org.isf.utils.exception.OHDataIntegrityViolationException;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Class that provides gui separation from database operations and gives some
 * useful logic manipulations of the dinamic data (memory)
 *
 * @author bob
 * 19-dec-2005
 * 14-jan-2006
 */
@Component
public class ExamBrowsingManager {

	@Autowired
	private ExamIoOperations ioOperations;

	/**
	 * Verify if the object is valid for CRUD and return a list of errors, if any
	 *
	 * @param exam
	 * @param insert <code>true</code> or updated <code>false</code>
	 * @throws OHServiceException
	 */
	protected void validateExam(Exam exam, boolean insert) throws OHServiceException {
		String key = exam.getCode();
		String description = exam.getDescription();
		List<OHExceptionMessage> errors = new ArrayList<>();
		if (key.isEmpty()) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
					MessageBundle.getMessage("angal.common.pleaseinsertacode.msg"),
					OHSeverityLevel.ERROR));
		}
		if (description.isEmpty()) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
					MessageBundle.getMessage("angal.common.pleaseinsertavaliddescription.msg"),
					OHSeverityLevel.ERROR));
		}
		if (insert) {
			if (isKeyPresent(exam)) {
				throw new OHDataIntegrityViolationException(new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
						MessageBundle.getMessage("angal.common.thecodeisalreadyinuse.msg"),
						OHSeverityLevel.ERROR));
			}
		}
		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
	}

	/**
	 * Returns the list of {@link Exam}s
	 *
	 * @return the list of {@link Exam}s. It could be <code>null</code>
	 * @throws OHServiceException
	 */
	public List<Exam> getExams() throws OHServiceException {
		return ioOperations.getExams();
	}

	/**
	 * Returns the list of {@link Exam}s
	 *
	 * @return the list of {@link Exam}s. It could be <code>null</code>
	 * @throws OHServiceException
	 * @deprecated use getExam() instead
	 */
	@Deprecated
	public List<Exam> getExamsbyDesc() throws OHServiceException {
		return this.getExams();
	}

	/**
	 * Returns the list of {@link Exam}s that matches passed description
	 *
	 * @param description - the exam description
	 * @return the list of {@link Exam}s. It could be <code>null</code>
	 * @throws OHServiceException
	 */
	public List<Exam> getExams(String description) throws OHServiceException {
		return ioOperations.getExamsByDesc(description);
	}

	/**
	 * Returns the list of {@link Exam}s by {@link ExamType} description
	 *
	 * @param description - the exam description
	 * @return the list of {@link Exam}s. It could be <code>null</code>
	 * @throws OHServiceException
	 */
	public List<Exam> getExamsByTypeDescription(String description) throws OHServiceException {
		return ioOperations.getExamsByExamTypeDesc(description);
	}

	/**
	 * Returns the list of {@link ExamType}s
	 *
	 * @return the list of {@link ExamType}s. It could be <code>null</code>
	 * @throws OHServiceException
	 */
	public List<ExamType> getExamType() throws OHServiceException {
		return ioOperations.getExamType();
	}

	/**
	 * This function controls the presence of a record with the same key as in
	 * the parameter; Returns false if the query finds no record, else returns
	 * true
	 *
	 * @param exam the {@link Exam}
	 * @return <code>true</code> if the Exam code has already been used, <code>false</code> otherwise
	 * @throws OHServiceException
	 */
	public boolean isKeyPresent(Exam exam) throws OHServiceException {
		return ioOperations.isKeyPresent(exam);
	}

	/**
	 * Insert a new {@link Exam} in the DB.
	 *
	 * @param exam - the {@link Exam} to insert
	 * @return <code>true</code> if the {@link Exam} has been inserted, <code>false</code> otherwise
	 * @throws OHServiceException
	 */
	public boolean newExam(Exam exam) throws OHServiceException {
		validateExam(exam, true);
		return ioOperations.newExam(exam);
	}

	/**
	 * Updates an existing {@link Exam} in the db
	 *
	 * @param exam -  the {@link Exam} to update
	 * @return <code>true</code> if the existing {@link Exam} has been updated, <code>false</code> otherwise
	 * @throws OHServiceException
	 */
	public Exam updateExam(Exam exam) throws OHServiceException {
		validateExam(exam, false);
		return ioOperations.updateExam(exam);
	}

	/**
	 * Delete an {@link Exam}
	 *
	 * @param exam - the {@link Exam} to delete
	 * @return <code>true</code> if the {@link Exam} has been deleted, <code>false</code> otherwise
	 * @throws OHServiceException
	 */
	public boolean deleteExam(Exam exam) throws OHServiceException {
		return ioOperations.deleteExam(exam);
	}
}
