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
package org.isf.operation.manager;

import java.util.ArrayList;

import org.isf.generaldata.MessageBundle;
import org.isf.operation.model.Operation;
import org.isf.operation.service.OperationIoOperations;
import org.isf.opetype.model.OperationType;
import org.isf.utils.exception.OHServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Class that provides gui separation from database operations and gives some
 * useful logic manipulations of the dynamic data (memory)
 * 
 * @author Rick, Vero, Pupo
 * 
 */
@Component
public class OperationBrowserManager {

	private final Logger logger = LoggerFactory.getLogger(OperationBrowserManager.class);
	
	@Autowired
	private OperationIoOperations ioOperations;

	/**
	 * Return the list of {@link Operation}s
	 * 
	 * @return the list of {@link Operation}s. It could be <code>empty</code> or <code>null</code>.
	 * @throws OHServiceException 
	 */
	//TODO: Evaluate the use of a parameter in one method only
	public ArrayList<Operation> getOperationOpd() throws OHServiceException {
		return ioOperations.getOperationOpd();
	}
	public ArrayList<Operation> getOperationAdm() throws OHServiceException {
		return ioOperations.getOperationAdm();
	}
	public ArrayList<Operation> getOperation() throws OHServiceException {
		return ioOperations.getOperationByTypeDescription(null);
	}
	/**
	 * return the {@link Operation} with the specified code
	 * @param code
	 * @return
	 * @throws OHServiceException
	 */
    public Operation getOperationByCode(String code) throws OHServiceException {
		return ioOperations.findByCode(code);
    }
        
	/**
	 * return the {@link Operation}s whose {@link OperationType} matches specified string
	 * 
	 * @param typecode - a type description
	 * @return the list of {@link Operation}s. It could be <code>empty</code> or <code>null</code>.
	 * @throws OHServiceException 
	 */
	public ArrayList<Operation> getOperationByTypeDescription(String typecode) throws OHServiceException {
		return ioOperations.getOperationByTypeDescription(typecode);
	}

	/**
	 * Insert an {@link Operation} in the DB
	 * 
	 * @param operation - the {@link Operation} to insert
	 * @return <code>true</code> if the operation has been inserted, <code>false</code> otherwise.
	 * @throws OHServiceException 
	 */
	public boolean newOperation(Operation operation) throws OHServiceException {
		return ioOperations.newOperation(operation);
	}
	
	/** 
	 * Updates an {@link Operation} in the DB
	 * 
	 * @param operation - the {@link Operation} to update
	 * @return <code>true</code> if the item has been updated. <code>false</code> other
	 * @throws OHServiceException 
	 */
	public boolean updateOperation(Operation operation) throws OHServiceException {
		// the user has confirmed he wants to overwrite the record
		return ioOperations.updateOperation(operation);
	}

	/** 
	 * Delete a {@link Operation} in the DB
	 * @param operation - the {@link Operation} to delete
	 * @return <code>true</code> if the item has been updated, <code>false</code> otherwise.
	 * @throws OHServiceException 
	 */
	public boolean deleteOperation(Operation operation) throws OHServiceException {
		return ioOperations.deleteOperation(operation);
	}
	
	/**
	 * Checks if an {@link Operation} code has already been used
	 * @param code - the code
	 * @return <code>true</code> if the code is already in use, <code>false</code> otherwise.
	 * @throws OHServiceException 
	 */
	public boolean codeControl(String code) throws OHServiceException {
		return ioOperations.isCodePresent(code);
	}
	
	/**
	 * Checks if an {@link Operation} description has already been used within the specified {@link OperationType}
	 * 
	 * @param description - the {@link Operation} description
	 * @param typeCode - the {@link OperationType} code
	 * @return <code>true</code> if the description is already in use, <code>false</code> otherwise.
	 * @throws OHServiceException 
	 */
	public boolean descriptionControl(String description, String typeCode) throws OHServiceException {
		return ioOperations.isDescriptionPresent(description,typeCode);
	}
	
	/**
	 * Get the list of possible operation results
	 * @return the found list
	 */
	public ArrayList<String> getResultsList() {
		ArrayList<String> resultsList = new ArrayList<String>();
		resultsList.add(MessageBundle.getMessage("angal.operation.result.sucess"));
		resultsList.add(MessageBundle.getMessage("angal.operation.result.failure"));
		resultsList.add(MessageBundle.getMessage("angal.operation.result.undefined"));
		return resultsList;
	}
}
