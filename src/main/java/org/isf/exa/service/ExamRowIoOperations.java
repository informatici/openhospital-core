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
package org.isf.exa.service;

import java.util.List;

import org.isf.exa.model.ExamRow;
import org.isf.exatype.model.ExamType;
import org.isf.exatype.service.ExamTypeIoOperationRepository;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ------------------------------------------
 * ExamRowIoOperations - provides the I/OO operations for recovering and managing exam records from the database.
 * -----------------------------------------
 * modification history
 * ??/??/2005 - Davide/Theo - first beta version
 * 07/11/2006 - ross - modified to accept, within the description, the character quote (')
 *                     (to do this, just double every quote. replaceall("'","''")
 *                     when record locked all data is saved now, not only descritpion
 * ------------------------------------------
 */
@Service
@Transactional(rollbackFor=OHServiceException.class)
@TranslateOHServiceException
public class ExamRowIoOperations {

	@Autowired
	private ExamRowIoOperationRepository rowRepository;
	
	@Autowired
	private ExamTypeIoOperationRepository typeRepository;
	
	/**
	 * Returns a list of {@link ExamRow}s that matches passed exam code and description
	 * @param aExamCode - the exam code
	 * @param aDescription - the exam description
	 * @return the list of {@link ExamRow}s
	 * @throws OHServiceException
	 */
	public List<ExamRow> getExamRow(int aExamCode, String aDescription) throws OHServiceException {
		List<ExamRow> examrows;

		if (aExamCode != 0) {
			if (aDescription != null) {
				examrows = rowRepository.findAllByCodeAndDescriptionOrderByCodeAscDescriptionAsc(aExamCode, aDescription);
			} else {
				examrows = rowRepository.findAllByCodeOrderByDescription(aExamCode);
			}
		} else {
			examrows = rowRepository.findAll();
		}
		return examrows;
	}

	/**
	 * Returns the list of {@link ExamRow}s
	 * @return the list of {@link ExamRow}s
	 * @throws OHServiceException
	 */
	public List<ExamRow> getExamRows() throws OHServiceException {
		return getExamsRowByDesc(null);
	}

	/**
	 * Returns the list of {@link ExamRow}s that matches passed description
	 * @param description - the examRow description
	 * @return the list of {@link ExamRow}s
	 * @throws OHServiceException
	 */
	public List<ExamRow> getExamsRowByDesc(String description) throws OHServiceException {
		if (description != null) {
			return rowRepository.findAllByDescriptionOrderByDescriptionAsc(description);
		}
		return rowRepository.findAll();
	}

	/**
	 * Returns the list of {@link ExamType}s
	 * @return the list of {@link ExamType}s
	 * @throws OHServiceException
	 */
	public List<ExamType> getExamType() throws OHServiceException {
		return typeRepository.findAllByOrderByDescriptionAsc();
	}

	/**
	 * Insert a new {@link ExamRow} in the DB.
	 * 
	 * @param examRow - the {@link ExamRow} to insert
	 * @return <code>true</code> if the {@link ExamRow} has been inserted, <code>false</code> otherwise
	 * @throws OHServiceException 
	 */
	public ExamRow newExamRow(ExamRow examRow) throws OHServiceException {
		return rowRepository.save(examRow);
	}

	/**
	 * Update an already existing {@link ExamRow}.
	 * @param examRow - the {@link ExamRow} to update
	 * @return <code>true</code> if the {@link ExamRow} has been updated, <code>false</code> otherwise
	 * @throws OHServiceException
	 */
	public ExamRow updateExamRow(ExamRow examRow) throws OHServiceException {
		return rowRepository.save(examRow);
	}

	/**
	 * Delete an {@link ExamRow}
	 * @param examRow - the {@link ExamRow} to delete
	 * @return <code>true</code> if the {@link ExamRow} has been deleted, <code>false</code> otherwise
	 * @throws OHServiceException
	 */
	public boolean deleteExamRow(ExamRow examRow) throws OHServiceException {
		rowRepository.deleteById(examRow.getCode());
		return true;
	}

	
	/**
	 * This function controls the presence of a record with the same key as in
	 * the parameter; Returns false if the query finds no record, else returns
	 * true
	 * 
	 * @param examrow the {@link ExamRow}
	 * @return <code>true</code> if the Exam code has already been used, <code>false</code> otherwise
	 * @throws OHServiceException 
	 */
	public boolean isKeyPresent(ExamRow examrow) throws OHServiceException {
		return rowRepository.findById(examrow.getCode()).orElse(null) != null;
	}
	
	/**
	 * Sanitize the given {@link String} value. 
	 * This method is maintained only for backward compatibility.
	 * @param value the value to sanitize.
	 * @return the sanitized value or <code>null</code> if the passed value is <code>null</code>.
	 */
	protected String sanitize(String value) {
		if (value == null) {
			return null;
		}
		return value.trim().replace("'", "''");
	}

	/**
	 * Checks if the code is already in use
	 *
	 * @param code - the exam code
	 * @return <code>true</code> if the code is already in use, <code>false</code> otherwise
	 * @throws OHServiceException 
	 */
	public boolean isCodePresent(int code) throws OHServiceException{
		return rowRepository.existsById(code);
	}

	/**
	 * Checks if the code is already in use
	 *
	 * @param code - the exam row code
	 * @return <code>true</code> if the code is already in use, <code>false</code> otherwise
	 * @throws OHServiceException 
	 */
	public boolean isRowPresent(Integer code) throws OHServiceException {
		return rowRepository.existsById(code);
	}

	public List<ExamRow> getExamRowByExamCode(String aExamCode) throws OHServiceException {
		return rowRepository.findAllByExam_CodeOrderByDescription(aExamCode);
    }

}
