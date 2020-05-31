package org.isf.dicomtype.manager;

import java.util.ArrayList;
import java.util.List;

import org.isf.dicomtype.model.DicomType;
import org.isf.dicomtype.service.DicomTypeIoOperation;
import org.isf.generaldata.MessageBundle;
import org.isf.utils.exception.OHDataIntegrityViolationException;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DicomTypeBrowserManager {
	
	@Autowired
	private DicomTypeIoOperation ioOperations;

	public boolean newDicomType(DicomType dicomType) throws OHServiceException {
		List<OHExceptionMessage> errors = validateVaccineType(dicomType, true);
		if(!errors.isEmpty()){
	        throw new OHDataValidationException(errors);
	    }
		return ioOperations.newDicomType(dicomType);
	}

	public boolean updateDicomType(DicomType dicomType) throws OHServiceException {
		List<OHExceptionMessage> errors = validateVaccineType(dicomType, false);
		if(!errors.isEmpty()){
	        throw new OHDataValidationException(errors);
	    }
		return ioOperations.updateDicomType(dicomType);
	}

	public boolean deleteDicomType(DicomType dicomType) throws OHServiceException {
		return ioOperations.deleteDicomType(dicomType);
	}

	public ArrayList<DicomType> getDicomType() throws OHServiceException {
		return ioOperations.getDicomType();
	}
	
	/**
	 * Verify if the object is valid for CRUD and return a list of errors, if any
	 * @param dicomType
	 * @param insert <code>true</code> or updated <code>false</code>
	 * @throws OHServiceException 
	 */
	protected List<OHExceptionMessage> validateVaccineType(DicomType dicomType, boolean insert) throws OHServiceException {
		String key = dicomType.getDicomTypeID();
		String description = dicomType.getDicomTypeDescription();
        List<OHExceptionMessage> errors = new ArrayList<OHExceptionMessage>();
        if(key.isEmpty() ){
	        errors.add(new OHExceptionMessage("codeEmptyError", 
	        		MessageBundle.getMessage("angal.dicomtype.pleaseinsertacode"), 
	        		OHSeverityLevel.ERROR));
        }
        if(key.length()>3){
	        errors.add(new OHExceptionMessage("codeTooLongError", 
	        		MessageBundle.getMessage("angal.dicomtype.codemaxchars"), 
	        		OHSeverityLevel.ERROR));
        }
        if(description.isEmpty() ){
            errors.add(new OHExceptionMessage("descriptionEmptyError", 
            		MessageBundle.getMessage("angal.dicomtype.pleaseinsertavaliddescription"), 
            		OHSeverityLevel.ERROR));
        }
        if (insert) {
        	if (codeControl(dicomType.getDicomTypeID())){
    			throw new OHDataIntegrityViolationException(new OHExceptionMessage(null, 
    					MessageBundle.getMessage("angal.common.codealreadyinuse"), 
    					OHSeverityLevel.ERROR));
    		}
        }
        return errors;
    }
	
	public boolean codeControl(String code) throws OHServiceException {
		return ioOperations.isCodePresent(code);
	}

}
