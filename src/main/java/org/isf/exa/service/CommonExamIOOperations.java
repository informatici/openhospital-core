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
import org.isf.utils.exception.OHServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * ------------------------------------------
 * CommonExamIOOperations - provides the I/O operations for recovering and managing exam records from the database.
 * ------------------------------------------
 */
@Service
public class CommonExamIOOperations {

	@Autowired
	private ExamRowIoOperationRepository rowRepository;

	@Autowired
	private ExamTypeIoOperationRepository typeRepository;

	/**
	 * Returns the list of {@link ExamType}s
	 * @return the list of {@link ExamType}s
	 * @throws OHServiceException
	 */
	public List<ExamType> getExamType() throws OHServiceException {
		return typeRepository.findAllByOrderByDescriptionAsc();
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

	/**
	 * Delete an {@link ExamRow}.
	 * @param examRow - the {@link ExamRow} to delete
	 * @return <code>true</code> if the {@link ExamRow} has been deleted, <code>false</code> otherwise
	 * @throws OHServiceException
	 */
	public boolean deleteExamRow(ExamRow examRow) throws OHServiceException {
		rowRepository.deleteById(examRow.getCode());
		return true;
	}
}
