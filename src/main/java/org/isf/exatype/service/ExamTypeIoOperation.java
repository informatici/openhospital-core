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
package org.isf.exatype.service;

import java.util.List;

import org.isf.exatype.model.ExamType;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor=OHServiceException.class)
@TranslateOHServiceException
public class ExamTypeIoOperation {

	@Autowired
	private ExamTypeIoOperationRepository repository;
	
	/**
	 * Return the list of {@link ExamType}s.
	 * @return the list of {@link ExamType}s.
	 * @throws OHServiceException
	 */
	public List<ExamType> getExamType() throws OHServiceException {
		return repository.findAllByOrderByDescriptionAsc();
	}
	
	/**
	 * Update an already existing {@link ExamType}.
	 * @param examType - the {@link ExamType} to update
	 * @return <code>true</code> if the examType has been updated, <code>false</code> otherwise.
	 * @throws OHServiceException
	 */
	public boolean updateExamType(
			ExamType examType) throws OHServiceException 
	{
		boolean result = true;
	

		ExamType savedExamType = repository.save(examType);
		result = (savedExamType != null);
		
		return result;
	}
	
	/**
	 * Insert a new {@link ExamType} in the DB.
	 * @param examType - the {@link ExamType} to insert.
	 * @return <code>true</code> if the examType has been inserted, <code>false</code> otherwise.
	 * @throws OHServiceException
	 */
	public boolean newExamType(
			ExamType examType) throws OHServiceException 
	{
		boolean result = true;
	
		
		repository.save(examType);
		
		return result;
	}
	
	/**
	 * Delete the passed {@link ExamType}.
	 * @param examType - the {@link ExamType} to delete.
	 * @return <code>true</code> if the examType has been deleted, <code>false</code> otherwise.
	 * @throws OHServiceException
	 */
	public boolean deleteExamType(
			ExamType examType) throws OHServiceException 
	{
		boolean result = true;
	
		
		repository.delete(examType);
		
		return result;
	}
	
	/**
	 * This function controls the presence of a record with the same code as in
	 * the parameter.
	 * @param code - the code
	 * @return <code>true</code> if the code is present, <code>false</code> otherwise.
	 * @throws OHServiceException
	 */
	public boolean isCodePresent(
			String code) throws OHServiceException
	{
		boolean result = true;
	
		
		result = repository.exists(code);
		
		return result;
	}
}
