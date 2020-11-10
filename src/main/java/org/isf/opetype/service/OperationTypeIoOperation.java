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
package org.isf.opetype.service;

import java.util.ArrayList;

import org.isf.opetype.model.OperationType;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor=OHServiceException.class)
@TranslateOHServiceException
public class OperationTypeIoOperation {

	@Autowired
	private OperationTypeIoOperationRepository repository;
	
	/**
	 * Return the list of {@link OperationType}s
	 * 
	 * @return the list of {@link OperationType}s. It could be <code>empty</code> or <code>null</code>.
	 * @throws OHServiceException 
	 */
	public ArrayList<OperationType> getOperationType() throws OHServiceException 
	{
		return new ArrayList<OperationType>(repository.findAllByOrderByDescriptionAsc()); 
	}
	
	/**
	 * Insert an {@link OperationType} in the DB
	 * 
	 * @param operationType - the {@link OperationType} to insert
	 * @return <code>true</code> if the {@link OperationType} has been inserted, <code>false</code> otherwise.
	 * @throws OHServiceException 
	 */
	public boolean newOperationType(
			OperationType operationType) throws OHServiceException 
	{
		boolean result = true;
	

		OperationType savedOperationType = repository.save(operationType);
		result = (savedOperationType != null);
		
		return result;
	}
	
	/**
	 * Update an {@link OperationType}
	 * 
	 * @param operationType - the {@link OperationType} to update
	 * @return <code>true</code> if the {@link OperationType} has been updated, <code>false</code> otherwise.
	 * @throws OHServiceException 
	 */
	public boolean updateOperationType(
			OperationType operationType) throws OHServiceException 
	{
		boolean result = true;
	

		OperationType savedOperationType = repository.save(operationType);
		result = (savedOperationType != null);
		
		return result;
	}
	
	/**
	 * Delete an {@link OperationType}
	 * 
	 * @param operationType - the {@link OperationType} to delete
	 * @return <code>true</code> if the {@link OperationType} has been delete, <code>false</code> otherwise.
	 * @throws OHServiceException 
	 */
	public boolean deleteOperationType(
			OperationType operationType) throws OHServiceException 
	{
		boolean result = true;
	
		
		repository.delete(operationType);
		
		return result;
	}
	
	/**
	 * Checks if an {@link OperationType} code has already been used
	 * @param code - the code
	 * @return <code>true</code> if the code is already in use, <code>false</code> otherwise.
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
