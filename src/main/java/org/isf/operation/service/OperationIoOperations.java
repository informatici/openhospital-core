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
package org.isf.operation.service;

/*----------------------------------------------------------
 * modification history
 * ====================
 * 13/02/09 - Alex - modified query for ordering resultset
 *                   by description only
 * 13/02/09 - Alex - added Major/Minor control
 -----------------------------------------------------------*/

import java.util.ArrayList;

import org.isf.operation.model.Operation;
import org.isf.opetype.model.OperationType;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class offers the io operations for recovering and managing
 * operations records from the database
 * 
 * @author Rick, Vero, pupo
 */
@Service
@Transactional(rollbackFor=OHServiceException.class)
@TranslateOHServiceException
public class OperationIoOperations {

	@Autowired
	private OperationIoOperationRepository repository;
	
	/**
	 * return the {@link Operation}s whose type matches specified string
	 * 
	 * @param typeDescription - a type description
	 * @return the list of {@link Operation}s. It could be <code>empty</code> or <code>null</code>.
	 * @throws OHServiceException 
	 */
	public ArrayList<Operation> getOperationByTypeDescription(String typeDescription) throws OHServiceException {
		return new ArrayList<Operation>(typeDescription == null ?
			repository.findByOrderByDescriptionDesc() :
			repository.findAllByType_DescriptionContainsOrderByDescriptionDesc(typeDescription));
	}

	public Operation findByCode(String code) throws OHServiceException{
    	return repository.findByCode(code);
	}

	public ArrayList<Operation> getOperationOpd(
			) throws OHServiceException {

    	ArrayList<Operation> operations = null;


			operations = repository.findAllWithoutDescriptionOpd();



		return operations;
	}

	public ArrayList<Operation> getOperationAdm(
			) throws OHServiceException {

    	ArrayList<Operation> operations = null;


			operations = repository.findAllWithoutDescriptionAdm();



		return operations;
	}


	/**
	 * insert an {@link Operation} in the DBs
	 * 
	 * @param operation - the {@link Operation} to insert
	 * @return <code>true</code> if the operation has been inserted, <code>false</code> otherwise.
	 * @throws OHServiceException 
	 */
	public boolean newOperation(Operation operation) throws OHServiceException {
		return repository.save(operation) != null;
	}
	
	/** 
	 * updates an {@link Operation} in the DB
	 * 
	 * @param operation - the {@link Operation} to update
	 * @return <code>true</code> if the item has been updated, <code>false</code> otherwise.
	 * @throws OHServiceException 
	 */
	public boolean updateOperation(Operation operation) throws OHServiceException {
		return repository.save(operation) != null;
	}
	
	/** 
	 * Delete a {@link Operation} in the DB
	 * @param operation - the {@link Operation} to delete
	 * @return <code>true</code> if the item has been updated, <code>false</code> otherwise.
	 * @throws OHServiceException 
	 */
	public boolean deleteOperation(Operation operation) throws OHServiceException {
		repository.delete(operation);
		return true;
	}
	
	/**
	 * checks if an {@link Operation} code has already been used
	 * @param code - the code
	 * @return <code>true</code> if the code is already in use, <code>false</code> otherwise.
	 * @throws OHServiceException 
	 */
	public boolean isCodePresent(String code) throws OHServiceException {
		return repository.existsById(code);
	}
	
	/**
	 * checks if an {@link Operation} description has already been used within the specified {@link OperationType} 
	 * 
	 * @param description - the {@link Operation} description
	 * @param typeCode - the {@link OperationType} code
	 * @return <code>true</code> if the description is already in use, <code>false</code> otherwise.
	 * @throws OHServiceException 
	 */
	public boolean isDescriptionPresent(String description, String typeCode) throws OHServiceException {
		Operation foundOperation = repository.findOneByDescriptionAndType_Code(description, typeCode);
		return foundOperation != null && foundOperation.getDescription().compareTo(description) == 0;
	}
}

