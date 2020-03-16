package org.isf.dlvrrestype.service;

import java.util.ArrayList;

import org.isf.dlvrrestype.model.DeliveryResultType;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Persistence class for DeliveryResultType module.
 */
@Service
@Transactional(rollbackFor=OHServiceException.class)
@TranslateOHServiceException
public class DeliveryResultTypeIoOperation {
	
	@Autowired
	private DeliveryResultIoOperationRepository repository;

	/**
	 * Returns all stored {@link DeliveryResultType}s.
	 * @return the stored {@link DeliveryResultType}s.
	 * @throws OHServiceException if an error occurs retrieving the stored delivery result types.
	 */
	public ArrayList<DeliveryResultType> getDeliveryResultType() throws OHServiceException 
	{
		return new ArrayList<DeliveryResultType>(repository.findAllByOrderByDescriptionAsc()); 		
	}

	/**
	 * Updates the specified {@link DeliveryResultType}.
	 * @param deliveryresultType the delivery result type to update.
	 * @return <code>true</code> if the delivery result type has been updated, <code>false</code> otherwise.
	 * @throws OHServiceException if an error occurs during the update.
	 */
	public boolean updateDeliveryResultType(
			DeliveryResultType deliveryResultType) throws OHServiceException 
	{
		boolean result = true;
	

		DeliveryResultType savedDeliveryResultType = repository.save(deliveryResultType);
		result = (savedDeliveryResultType != null);
		
		return result;
	}

	/**
	 * Stores the specified {@link DeliveryResultType}.
	 * @param deliveryresultType the delivery result type to store.
	 * @return <code>true</code> if the delivery result type has been stored. 
	 * @throws OHServiceException if an error occurs during the store operation.
	 */
	public boolean newDeliveryResultType(
			DeliveryResultType deliveryResultType) throws OHServiceException 
	{
		boolean result = true;
	

		DeliveryResultType savedDeliveryResultType = repository.save(deliveryResultType);
		result = (savedDeliveryResultType != null);
		
		return result;
	}

	/**
	 * Deletes the specified {@link DeliveryResultType}.
	 * @param deliveryresultType the delivery result type to delete.
	 * @return <code>true</code> if the delivery result type has been deleted, <code>false</code> otherwise.
	 * @throws OHServiceException if an error occurs during the delete operation.
	 */
	public boolean deleteDeliveryResultType(
			DeliveryResultType deliveryResultType) throws OHServiceException 
	{
		boolean result = true;
	
		
		repository.delete(deliveryResultType);
		
		return result;
	}

	/**
	 * Checks if the specified code is already used by others {@link DeliveryResultType}s.
	 * @param code the code to check.
	 * @return <code>true</code> if the code is used, <code>false</code> otherwise.
	 * @throws OHServiceException if an error occurs during the check.
	 */
	public boolean isCodePresent(
			String code) throws OHServiceException 
	{
		boolean result = true;
	
		
		result = repository.exists(code);
		
		return result;
	}
}
