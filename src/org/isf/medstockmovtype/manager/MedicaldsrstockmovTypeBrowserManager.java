package org.isf.medstockmovtype.manager;

import java.util.ArrayList;
import java.util.List;

import org.isf.generaldata.MessageBundle;
import org.isf.medstockmovtype.model.MovementType;
import org.isf.medstockmovtype.service.MedicalStockMovementTypeIoOperation;
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
 * Manager class for the medical stock movement type.
 *
 */
@Component
public class MedicaldsrstockmovTypeBrowserManager {

	private final Logger logger = LoggerFactory.getLogger(MedicaldsrstockmovTypeBrowserManager.class);
	
	@Autowired
	private MedicalStockMovementTypeIoOperation ioOperations;

	/**
	 * Verify if the object is valid for CRUD and return a list of errors, if any
	 * @param movementType
	 * @param insert <code>true</code> or updated <code>false</code>
	 * @throws OHServiceException 
	 */
	protected void validateMovementType(MovementType movementType, boolean insert) throws OHServiceException  {
		String key = movementType.getCode();
		String key2 = movementType.getType();
		String description = movementType.getDescription();
        List<OHExceptionMessage> errors = new ArrayList<OHExceptionMessage>();
        if(key.isEmpty() ){
	        errors.add(new OHExceptionMessage("codeEmptyError", 
	        		MessageBundle.getMessage("angal.medstockmovtype.pleaseinsertacode"), 
	        		OHSeverityLevel.ERROR));
        }
        if(key.length()>10){
	        errors.add(new OHExceptionMessage("codeTooLongError", 
	        		MessageBundle.getMessage("angal.medstockmovtype.codetoolongmaxchar"), 
	        		OHSeverityLevel.ERROR));
        }
        if(key2.length()>2){
	        errors.add(new OHExceptionMessage("codeTypeTooLongError", 
	        		MessageBundle.getMessage("angal.medstockmovtype.typetoolongmaxchars"), 
	        		OHSeverityLevel.ERROR));
        }
        if(description.isEmpty() ){
            errors.add(new OHExceptionMessage("descriptionEmptyError", 
            		MessageBundle.getMessage("angal.medstockmovtype.pleaseinsertavaliddescription"), 
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
	 * Returns all the medical stock movement types.
	 * @return all the medical stock movement types.
	 * @throws OHServiceException 
	 */
	public ArrayList<MovementType> getMedicaldsrstockmovType() throws OHServiceException {
		return ioOperations.getMedicaldsrstockmovType();
	}

	/**
	 * Save the specified {@link MovementType}.
	 * @param medicaldsrstockmovType the medical stock movement type to save.
	 * @return <code>true</code> if the medical stock movement type has been saved, <code>false</code> otherwise.
	 * @throws OHServiceException 
	 */
	public boolean newMedicaldsrstockmovType(MovementType medicaldsrstockmovType) throws OHServiceException {
		validateMovementType(medicaldsrstockmovType, true);
		return ioOperations.newMedicaldsrstockmovType(medicaldsrstockmovType);
	}

	/**
	 * Updates the specified {@link MovementType}.
	 * @param medicaldsrstockmovType the medical stock movement type to update.
	 * @return <code>true</code> if the medical stock movement type has been updated, <code>false</code> otherwise.
	 * @throws OHServiceException 
	 */
	public boolean updateMedicaldsrstockmovType(MovementType medicaldsrstockmovType) throws OHServiceException {
		validateMovementType(medicaldsrstockmovType, false);
		return ioOperations.updateMedicaldsrstockmovType(medicaldsrstockmovType);
	}

	/**
	 * Checks if the specified {@link MovementType} code is already used.
	 * @param code the code to check.
	 * @return <code>true</code> if the code is used, <code>false</code> otherwise.
	 * @throws OHServiceException 
	 */
	public boolean codeControl(String code) throws OHServiceException {
		return ioOperations.isCodePresent(code);
	}

	/**
	 * Deletes the specified {@link MovementType}.
	 * @param medicaldsrstockmovType the medical stock movement type to delete.
	 * @return <code>true</code> if the medical stock movement type has been deleted, <code>false</code> otherwise.
	 * @throws OHServiceException 
	 */
	public boolean deleteMedicaldsrstockmovType(MovementType medicaldsrstockmovType) throws OHServiceException {
		return ioOperations.deleteMedicaldsrstockmovType(medicaldsrstockmovType);
	}
        
        /**
	 * Get the  {@link MovementType} code.
	 * In case of error a message error is shown and a <code>false</code> value is returned.
	 * @param code the code to check.
	 * @return <code>true</code> if the code is used, <code>false</code> otherwise.
	 */
	public MovementType getMovementType(String code) {
		return ioOperations.getMovementType(code);
	}
}
