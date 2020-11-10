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
package org.isf.exa.service;

/*------------------------------------------
 * IoOperations - provides the io operations for recovering and managing exam records from the database.
 * -----------------------------------------
 * modification history
 * ??/??/2005 - Davide/Theo - first beta version 
 * 07/11/2006 - ross - modified to accept, within the description, the character quote (')
 *                     (to do this, just double every quote. replaceall("'","''") 
 *                     when record locked all data is saved now, not only descritpion
 *------------------------------------------*/

import java.util.ArrayList;

import org.isf.exa.model.ExamRow;
import org.isf.exatype.model.ExamType;
import org.isf.exatype.service.ExamTypeIoOperationRepository;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor=OHServiceException.class)
@TranslateOHServiceException
public class ExamRowIoOperations {

	//@Autowired
	//private ExamIoOperationRepository repository;
	
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
	public ArrayList<ExamRow> getExamRow(
			int aExamCode, 
			String aDescription) throws OHServiceException 
	{
            ArrayList<ExamRow> examrows;
            
            if (aExamCode != 0) 
            {   
                if (aDescription != null) 
                {
                    examrows = (ArrayList<ExamRow>) rowRepository.findAllByCodeAndDescriptionOrderByCodeAscDescriptionAsc(aExamCode, aDescription);
                }
                else
                {
                    examrows = (ArrayList<ExamRow>) rowRepository.findAllByCodeOrderByDescription(aExamCode);
                }
            }
            else
            {   
                examrows = (ArrayList<ExamRow>) rowRepository.findAll();
            }
            return examrows;
	}

	/**
	 * Returns the list of {@link ExamRow}s
	 * @return the list of {@link ExamRow}s
	 * @throws OHServiceException
	 */
	public ArrayList<ExamRow> getExamrows() throws OHServiceException 
	{
		return getExamsRowByDesc(null);
	}
	
	/**
	 * Returns the list of {@link ExamRow}s that matches passed description
	 * @param description - the examRow description
	 * @return the list of {@link ExamRow}s
	 * @throws OHServiceException
	 */
	public ArrayList<ExamRow> getExamsRowByDesc(
			String description) throws OHServiceException 
	{ 
		ArrayList<ExamRow> examrows = new ArrayList<ExamRow>();
				
		
		if (description != null) 
		{
			examrows = (ArrayList<ExamRow>) rowRepository.findAllByDescriptionOrderByDescriptionAsc(description);
		}
		else
		{
			examrows = (ArrayList<ExamRow>) rowRepository.findAll();
		}
		return examrows;
	}

	/**
	 * Returns the list of {@link ExamType}s
	 * @return the list of {@link ExamType}s
	 * @throws OHServiceException
	 */
	public ArrayList<ExamType> getExamType() throws OHServiceException 
	{
		ArrayList<ExamType> examTypes = (ArrayList<ExamType>) typeRepository.findAllByOrderByDescriptionAsc();
				
	
		return examTypes;
	}

	/**
	 * Insert a new {@link ExamRow} in the DB.
	 * 
	 * @param examRow - the {@link ExamRow} to insert
	 * @return <code>true</code> if the {@link ExamRow} has been inserted, <code>false</code> otherwise
	 * @throws OHServiceException 
	 */
	public boolean newExamRow(
			ExamRow examRow) throws OHServiceException 
	{
		boolean result = true;
	

		ExamRow savedExam = rowRepository.save(examRow);
		result = (savedExam != null);
		
		return result;
	}

	/**
	 * Update an already existing {@link ExamRow}.
	 * @param examRow - the {@link ExamRow} to update
	 * @return <code>true</code> if the {@link ExamRow} has been updated, <code>false</code> otherwise
	 * @throws OHServiceException
	 */
	public boolean updateExamRow(
			ExamRow examRow) throws OHServiceException 
	{
		boolean result = true;
		
		rowRepository.save(examRow);
    	
		return result;	
	}

	/**
	 * Delete an {@link ExamRow}
	 * @param examRow - the {@link ExamRow} to delete
	 * @return <code>true</code> if the {@link ExamRow} has been deleted, <code>false</code> otherwise
	 * @throws OHServiceException
	 */
	public boolean deleteExamRow(
                    ExamRow examRow) throws OHServiceException 
        {
            boolean result = true;
            rowRepository.delete(examRow.getCode());
            return result;	
	}

	
	/**
	 * This function controls the presence of a record with the same key as in
	 * the parameter; Returns false if the query finds no record, else returns
	 * true
	 * 
	 * @param examrow the {@link Exam}
	 * @return <code>true</code> if the Exam code has already been used, <code>false</code> otherwise
	 * @throws OHServiceException 
	 */
	public boolean isKeyPresent(
			ExamRow examrow) throws OHServiceException 
	{
		boolean result = false;
		ExamRow foundExam = rowRepository.findOne(examrow.getCode());
		
		
		if (foundExam != null)
		{
			result = true;
		}
		
		return result;
	}
	
	/**
	 * Sanitize the given {@link String} value. 
	 * This method is maintained only for backward compatibility.
	 * @param value the value to sanitize.
	 * @return the sanitized value or <code>null</code> if the passed value is <code>null</code>.
	 */
	protected String sanitize(
			String value)
	{
		String result = null;
		
		
		if (value != null) 
		{
			result = value.trim().replaceAll("'", "''");
		}
		
		return result;
	}

	/**
	 * Checks if the code is already in use
	 *
	 * @param code - the exam code
	 * @return <code>true</code> if the code is already in use, <code>false</code> otherwise
	 * @throws OHServiceException 
	 */
	public boolean isCodePresent(int code) throws OHServiceException{
		return rowRepository.exists(code);
	}

	/**
	 * Checks if the code is already in use
	 *
	 * @param code - the exam row code
	 * @return <code>true</code> if the code is already in use, <code>false</code> otherwise
	 * @throws OHServiceException 
	 */
	public boolean isRowPresent(Integer code) throws OHServiceException {
		return rowRepository.exists(code);
	}

    public ArrayList<ExamRow> getExamRowByExamCode(String aExamCode)  throws OHServiceException {
       ArrayList<ExamRow> examrows = (ArrayList<ExamRow>) rowRepository.findAllByExam_CodeOrderByDescription(aExamCode);
       return examrows;
    }

}
