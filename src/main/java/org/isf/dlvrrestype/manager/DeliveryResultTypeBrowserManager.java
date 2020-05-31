package org.isf.dlvrrestype.manager;

import java.util.ArrayList;
import java.util.List;

import org.isf.dlvrrestype.model.DeliveryResultType;
import org.isf.dlvrrestype.service.DeliveryResultTypeIoOperation;
import org.isf.generaldata.MessageBundle;
import org.isf.utils.exception.OHDataIntegrityViolationException;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Manager class for DeliveryResultTypeModule.
 */
@Component
public class DeliveryResultTypeBrowserManager {

	private final Logger logger = LoggerFactory.getLogger(DeliveryResultTypeBrowserManager.class);
	
	@Autowired
	private DeliveryResultTypeIoOperation ioOperations;
	
	/**
	 * Verify if the object is valid for CRUD and return a list of errors, if any
	 * @param deliveryResultType
	 * @param insert <code>true</code> or updated <code>false</code>
	 * @throws OHServiceException 
	 */
	protected void validateDeliveryResultType(DeliveryResultType deliveryResultType, boolean insert) throws OHServiceException {
		String key = deliveryResultType.getCode();
		String description = deliveryResultType.getDescription();
        List<OHExceptionMessage> errors = new ArrayList<OHExceptionMessage>();
        if(key.isEmpty() ){
	        errors.add(new OHExceptionMessage("codeEmptyError", 
	        		MessageBundle.getMessage("angal.dlvrrestype.pleaseinsertacode"), 
	        		OHSeverityLevel.ERROR));
        }
        if(key.length()>1){
	        errors.add(new OHExceptionMessage("codeTooLongError", 
	        		MessageBundle.getMessage("angal.dlvrrestype.codetoolongmaxchar"), 
	        		OHSeverityLevel.ERROR));
        }
        if(description.isEmpty() ){
            errors.add(new OHExceptionMessage("descriptionEmptyError", 
            		MessageBundle.getMessage("angal.dlvrrestype.pleaseinsertavaliddescription"), 
            		OHSeverityLevel.ERROR));
        }
        if (insert) {
        	if (codeControl(key)){
				throw new OHDataIntegrityViolationException(new OHExceptionMessage(null, 
						MessageBundle.getMessage("angal.common.codealreadyinuse"), 
						OHSeverityLevel.ERROR));
			}
        }
        if (!errors.isEmpty()){
	        throw new OHDataValidationException(errors);
	    }
    }
	
	/**
	 * Returns all stored {@link DeliveryResultType}s.
	 * In case of error a message error is shown and a <code>null</code> value is returned.
	 * @return the stored {@link DeliveryResultType}s, <code>null</code> if an error occurred.
	 * @throws OHServiceException 
	 */
	public ArrayList<DeliveryResultType> getDeliveryResultType() throws OHServiceException {
		return ioOperations.getDeliveryResultType();
	}

	/**
	 * Stores the specified {@link DeliveryResultType}.
	 * In case of error a message error is shown and a <code>false</code> value is returned.
	 * @param deliveryresultType the delivery result type to store.
	 * @return <code>true</code> if the delivery result type has been stored.
	 * @throws OHServiceException 
	 */
	public boolean newDeliveryResultType(DeliveryResultType deliveryresultType) throws OHServiceException {
		validateDeliveryResultType(deliveryresultType, true);
        return ioOperations.newDeliveryResultType(deliveryresultType);
	}

	/**
	 * Updates the specified {@link DeliveryResultType}.
	 * In case of error a message error is shown and a <code>false</code> value is returned.
	 * @param deliveryresultType the delivery result type to update.
	 * @return <code>true</code> if the delivery result type has been updated, <code>false</code> otherwise.
	 * @throws OHServiceException 
	 */
	public boolean updateDeliveryResultType(DeliveryResultType deliveryresultType) throws OHServiceException {
		validateDeliveryResultType(deliveryresultType, false);
		return ioOperations.updateDeliveryResultType(deliveryresultType);
	}

	/**
	 * Checks if the specified code is already used by others {@link DeliveryResultType}s.
	 * In case of error a message error is shown and a <code>false</code> value is returned.
	 * @param code the code to check.
	 * @return <code>true</code> if the code is used, <code>false</code> otherwise.
	 * @throws OHServiceException 
	 */
	public boolean codeControl(String code) throws OHServiceException {
		return ioOperations.isCodePresent(code);
	}

	/**
	 * Deletes the specified {@link DeliveryResultType}.
	 * In case of error a message error is shown and a <code>false</code> value is returned.
	 * @param deliveryresultType the delivery result type to delete.
	 * @return <code>true</code> if the delivery result type has been deleted, <code>false</code> otherwise.
	 * @throws OHServiceException 
	 */
	public boolean deleteDeliveryResultType(DeliveryResultType deliveryresultType) throws OHServiceException {
		return ioOperations.deleteDeliveryResultType(deliveryresultType);
	}

}
