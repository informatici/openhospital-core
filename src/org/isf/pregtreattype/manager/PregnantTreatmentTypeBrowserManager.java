package org.isf.pregtreattype.manager;

import java.util.ArrayList;
import java.util.List;

import org.isf.generaldata.MessageBundle;
import org.isf.menu.manager.Context;
import org.isf.pregtreattype.model.PregnantTreatmentType;
import org.isf.pregtreattype.service.PregnantTreatmentTypeIoOperation;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class PregnantTreatmentTypeBrowserManager {
	
	@Autowired
	private PregnantTreatmentTypeIoOperation ioOperations;
	
	/**
	 * return the list of {@link PregnantTreatmentType}s
	 * 
	 * @return the list of {@link PregnantTreatmentType}s
	 * @throws OHServiceException 
	 */
	public ArrayList<PregnantTreatmentType> getPregnantTreatmentType() throws OHServiceException {
        return ioOperations.getPregnantTreatmentType();
	}
	
	/**
	 * insert a {@link PregnantTreatmentType} in the DB
	 * 
	 * @param pregnantTreatmentType - the {@link PregnantTreatmentType} to insert
	 * @return <code>true</code> if the item has been inserted, <code>false</code> otherwise
	 * @throws OHServiceException 
	 */
	public boolean newPregnantTreatmentType(PregnantTreatmentType pregnantTreatmentType) throws OHServiceException {
		validatePregnantTreatmentType(pregnantTreatmentType, true);
        return ioOperations.newPregnantTreatmentType(pregnantTreatmentType);
	}

	/**
	 * update a {@link PregnantTreatmentType} in the DB
	 * 
	 * @param pregnantTreatmentType - the {@link PregnantTreatmentType} to update
	 * @return <code>true</code> if the item has been updated, <code>false</code> otherwise
	 * @throws OHServiceException 
	 */
	public boolean updatePregnantTreatmentType(PregnantTreatmentType pregnantTreatmentType) throws OHServiceException {
        validatePregnantTreatmentType(pregnantTreatmentType, false);
        return ioOperations.updatePregnantTreatmentType(pregnantTreatmentType);
	}
	
	/**
	 * delete a {@link PregnantTreatmentType} in the DB
	 * 
	 * @param pregnantTreatmentType - the {@link PregnantTreatmentType} to delete
	 * @return <code>true</code> if the item has been deleted, <code>false</code> otherwise
	 * @throws OHServiceException 
	 */
	public boolean deletePregnantTreatmentType(PregnantTreatmentType pregnantTreatmentType) throws OHServiceException {
        return ioOperations.deletePregnantTreatmentType(pregnantTreatmentType);
	}
	
	/**
	 * check if the code is already in use
	 * 
	 * @param code - the code
	 * @return <code>true</code> if the code is already in use, <code>false</code> otherwise
	 * @throws OHServiceException 
	 */
	public boolean codeControl(String code) throws OHServiceException {
        return ioOperations.isCodePresent(code);
	}

	/**
	 * Verify if the object is valid for CRUD and return a list of errors, if any
	 * @param pregnantTreatmentType
	 * @param insert <code>true</code> or updated <code>false</code>
	 * @throws OHDataValidationException 
	 */
    protected void validatePregnantTreatmentType(PregnantTreatmentType pregnantTreatmentType, boolean insert) throws OHServiceException {
        List<OHExceptionMessage> errors = new ArrayList<OHExceptionMessage>();
        String key = pregnantTreatmentType.getCode();
        if (StringUtils.isEmpty(key)){
            errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.hospital"),
                    MessageBundle.getMessage("angal.preagtreattype.pleaseinsertacode"),
                    OHSeverityLevel.ERROR));
        }
        if (key.length()>10){
            errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.hospital"),
                    MessageBundle.getMessage("angal.preagtreattype.codetoolong"),
                    OHSeverityLevel.ERROR));
        }
        if(insert){
            if (codeControl(key)){
                errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.hospital"),MessageBundle.getMessage("angal.common.codealreadyinuse"),
                        OHSeverityLevel.ERROR));
            }
        }
        if (StringUtils.isEmpty(pregnantTreatmentType.getDescription())){
            errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.hospital"),
                    MessageBundle.getMessage("angal.preagtreattype.pleaseinsertavaliddescription"),
                    OHSeverityLevel.ERROR));
        }
        if(!errors.isEmpty()){
	        throw new OHDataValidationException(errors);
	    }
    }
}
