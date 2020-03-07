package org.isf.vactype.manager;

/*------------------------------------------
 * VaccineTypeBrowserManager - 
 * -----------------------------------------
 * modification history
 * 19/10/2011 - Cla - version is now 1.0
 *------------------------------------------*/

import java.util.ArrayList;
import java.util.List;

import org.isf.generaldata.MessageBundle;
import org.isf.menu.manager.Context;
import org.isf.utils.exception.OHDataIntegrityViolationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.isf.vactype.model.VaccineType;
import org.isf.vactype.service.VacTypeIoOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VaccineTypeBrowserManager {
	
    private final Logger logger = LoggerFactory.getLogger(VaccineTypeBrowserManager.class);
	
    @Autowired
	private VacTypeIoOperation ioOperations;
	
	/**
	 * Verify if the object is valid for CRUD and return a list of errors, if any
	 * @param vaccineType
	 * @param insert <code>true</code> or updated <code>false</code>
	 * @throws OHServiceException 
	 */
	protected void validateVaccineType(VaccineType vaccineType, boolean insert) throws OHServiceException {
		String key = vaccineType.getCode();
		String description = vaccineType.getDescription();
        List<OHExceptionMessage> errors = new ArrayList<OHExceptionMessage>();
        if(key.isEmpty() ){
	        errors.add(new OHExceptionMessage("codeEmptyError", 
	        		MessageBundle.getMessage("angal.vactype.pleaseinsertacode"), 
	        		OHSeverityLevel.ERROR));
        }
        if(key.length()>1){
	        errors.add(new OHExceptionMessage("codeTooLongError", 
	        		MessageBundle.getMessage("angal.vactype.codemaxchars"), 
	        		OHSeverityLevel.ERROR));
        }
        if(description.isEmpty() ){
            errors.add(new OHExceptionMessage("descriptionEmptyError", 
            		MessageBundle.getMessage("angal.vactype.pleaseinsertavaliddescription"), 
            		OHSeverityLevel.ERROR));
        }
        if (insert) {
        	if (codeControl(vaccineType.getCode())){
    			throw new OHDataIntegrityViolationException(new OHExceptionMessage(null, 
    					MessageBundle.getMessage("angal.common.codealreadyinuse"), 
    					OHSeverityLevel.ERROR));
    		}
        }
        if(!errors.isEmpty()){
	        throw new OHDataValidationException(errors);
	    }
    }
		
		/**
	 * This method returns all {@link VaccineType}s from DB	
	 * 	
	 * @return the list of {@link VaccineType}s
	 * @throws OHServiceException 
	 */
	public ArrayList<VaccineType> getVaccineType() throws OHServiceException {
		return ioOperations.getVaccineType();
	}
	
	/**
	 * inserts a new {@link VaccineType} into DB
	 * 
	 * @param vaccineType - the {@link VaccineType} to insert 
	 * @return <code>true</code> if the item has been inserted, <code>false</code> otherwise
	 * @throws OHServiceException 
	 */
	public boolean newVaccineType(VaccineType vaccineType) throws OHServiceException {
		validateVaccineType(vaccineType, true);
		return ioOperations.newVaccineType(vaccineType);
	}

	/**
	 * update a {@link VaccineType} in the DB
	 *
	 * @param vaccineType - the item to update
	 * @return <code>true</code> if the item has been inserted, <code>false</code> otherwise
	 * @throws OHServiceException 
	 */
	public boolean updateVaccineType(VaccineType vaccineType) throws OHServiceException {
		validateVaccineType(vaccineType, false);
		return ioOperations.updateVaccineType(vaccineType);
	}
	
	/**
	 * deletes a {@link VaccineType} in the DB
	 *
	 * @param vaccineType - the item to delete
	 * @return <code>true</code> if the item has been deleted, <code>false</code> otherwise
	 * @throws OHServiceException 
	 */
	public boolean deleteVaccineType(VaccineType vaccineType) throws OHServiceException {
		return ioOperations.deleteVaccineType(vaccineType);
	}
	
	/**
	 * checks if the code is already in use
	 *
	 * @param code - the {@link VaccineType} code
	 * @return <code>true</code> if the code is already in use, <code>false</code> otherwise
	 * @throws OHServiceException 
	 */
	public boolean codeControl(String code) throws OHServiceException {
		return ioOperations.isCodePresent(code);
	}
}
