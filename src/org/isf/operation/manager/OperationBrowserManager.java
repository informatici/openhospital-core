package org.isf.operation.manager;

import java.util.ArrayList;

import org.isf.menu.manager.Context;
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
 * useful logic manipulations of the dinamic data (memory)
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
	 * return the list of {@link Operation}s
	 * 
	 * @return the list of {@link Operation}s. It could be <code>empty</code> or <code>null</code>.
	 * @throws OHServiceException 
	 */
	public ArrayList<Operation> getOperation() throws OHServiceException {
		return ioOperations.getOperation(null);
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
	 * return the {@link Operation}s whose type matches specified string
	 * 
	 * @param typeDescription - a type description
	 * @return the list of {@link Operation}s. It could be <code>empty</code> or <code>null</code>.
	 * @throws OHServiceException 
	 */
	public ArrayList<Operation> getOperation(String typecode) throws OHServiceException {
		return ioOperations.getOperation(typecode);
	}

	/**
	 * insert an {@link Operation} in the DB
	 * 
	 * @param operation - the {@link Operation} to insert
	 * @return <code>true</code> if the operation has been inserted, <code>false</code> otherwise.
	 * @throws OHServiceException 
	 */
	public boolean newOperation(Operation operation) throws OHServiceException {
		return ioOperations.newOperation(operation);
	}
	
	/** 
	 * updates an {@link Operation} in the DB
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
	 * checks if an {@link Operation} code has already been used
	 * @param code - the code
	 * @return <code>true</code> if the code is already in use, <code>false</code> otherwise.
	 * @throws OHServiceException 
	 */
	public boolean codeControl(String code) throws OHServiceException {
		return ioOperations.isCodePresent(code);
	}
	
	/**
	 * checks if an {@link Operation} description has already been used within the specified {@link OperationType} 
	 * 
	 * @param description - the {@link Operation} description
	 * @param typeCode - the {@link OperationType} code
	 * @return <code>true</code> if the description is already in use, <code>false</code> otherwise.
	 * @throws OHServiceException 
	 */
	public boolean descriptionControl(String description, String typeCode) throws OHServiceException {
		return ioOperations.isDescriptionPresent(description,typeCode);
	}
	
}
