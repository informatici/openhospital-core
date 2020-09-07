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
	public ArrayList<Operation> getOperation(String typeDescription) throws OHServiceException {
		return new ArrayList<Operation>(typeDescription == null ?
			repository.findByOrderByDescriptionDesc() :
			repository.findAllByDescriptionContainsOrderByDescriptionDesc(typeDescription));
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

