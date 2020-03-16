package org.isf.medtype.manager;

import org.isf.generaldata.MessageBundle;
import org.isf.medtype.model.MedicalType;
import org.isf.medtype.service.MedicalTypeIoOperation;
import org.isf.utils.exception.OHDataIntegrityViolationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Manager class for the medical type module.
 *
 */
@Component
public class MedicalTypeBrowserManager {

	private final Logger logger = LoggerFactory.getLogger(MedicalTypeBrowserManager.class);

	@Autowired
	private MedicalTypeIoOperation ioOperations;
	
	/**
	 * Verify if the object is valid for CRUD and return a list of errors, if any
	 * @param medicalType
	 * @param insert <code>true</code> or updated <code>false</code>
	 * @throws OHServiceException 
	 */
	protected void validateMedicalType(MedicalType medicalType, boolean insert) throws OHServiceException {
		String key = medicalType.getCode();
		String description = medicalType.getDescription();
        List<OHExceptionMessage> errors = new ArrayList<OHExceptionMessage>();
        if(key.isEmpty() ){
	        errors.add(new OHExceptionMessage("codeEmptyError", 
	        		MessageBundle.getMessage("angal.medtype.pleaseinsertacode"), 
	        		OHSeverityLevel.ERROR));
        }
        if(key.length()>1){
	        errors.add(new OHExceptionMessage("codeTooLongError", 
	        		MessageBundle.getMessage("angal.medtype.codetoolongmaxchars"), 
	        		OHSeverityLevel.ERROR));
        }
        if(description.isEmpty() ){
            errors.add(new OHExceptionMessage("descriptionEmptyError", 
            		MessageBundle.getMessage("angal.medtype.pleaseinsertavaliddescription"), 
            		OHSeverityLevel.ERROR));
        }
        if (insert) {
        	if (codeControl(medicalType.getCode())){
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
	 * Retrieves all the medical types.
	 * @return all the medical types.
	 * @throws OHServiceException 
	 */
	public ArrayList<MedicalType> getMedicalType() throws OHServiceException {
		return ioOperations.getMedicalTypes();
	}

	/**
	 * Saves the specified medical type.
	 * @param medicalType the medical type to save.
	 * @return <code>true</code> if the medical type has been saved, <code>false</code> otherwise.
	 * @throws OHServiceException 
	 */
	public boolean newMedicalType(MedicalType medicalType) throws OHServiceException {
		validateMedicalType(medicalType, true);
		return ioOperations.newMedicalType(medicalType);
	}

	/**
	 * Updates the specified medical type.
	 * @param medicalType the medical type to update.
	 * @return <code>true</code> if the medical type has been updated, <code>false</code> otherwise.
	 * @throws OHServiceException 
	 */
	public boolean updateMedicalType(MedicalType medicalType) throws OHServiceException {
		validateMedicalType(medicalType, false);
		return ioOperations.updateMedicalType(medicalType);
	}

	/**
	 * Checks if the specified medical type code is already used.
	 * @param code the code to check.
	 * @return <code>true</code> if the code is used, <code>false</code> owtherwise.
	 * @throws OHServiceException 
	 */
	public boolean codeControl(String code) throws OHServiceException {
		return ioOperations.isCodePresent(code);
	}

	/**
	 * Deletes the specified medical type.
	 * In case of error a message error is shown and a <code>false</code> value is returned.
	 * @param medicalType the medical type to delete.
	 * @return <code>true</code> if the medical type has been deleted, <code>false</code> otherwise.
	 * @throws OHServiceException 
	 */
	public boolean deleteMedicalType(MedicalType medicalType) throws OHServiceException {
		return ioOperations.deleteMedicalType(medicalType);
	}
}
