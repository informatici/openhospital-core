package org.isf.exatype.manager;

import java.util.ArrayList;
import java.util.List;

import org.isf.exatype.model.ExamType;
import org.isf.exatype.service.ExamTypeIoOperation;
import org.isf.generaldata.MessageBundle;
import org.isf.menu.manager.Context;
import org.isf.utils.exception.OHDataIntegrityViolationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ExamTypeBrowserManager {

	private final Logger logger = LoggerFactory.getLogger(ExamTypeBrowserManager.class);
	
	@Autowired
	private ExamTypeIoOperation ioOperations;

	/**
	 * Verify if the object is valid for CRUD and return a list of errors, if any
	 * @param examType
	 * @param insert <code>true</code> or updated <code>false</code>
	 * @throws OHServiceException 
	 */
	protected void validateExamType(ExamType examType, boolean insert) throws OHServiceException {
		String key = examType.getCode();
		String description = examType.getDescription();
        List<OHExceptionMessage> errors = new ArrayList<OHExceptionMessage>();
        if(key.isEmpty() ){
	        errors.add(new OHExceptionMessage("codeEmptyError", 
	        		MessageBundle.getMessage("angal.exatype.pleaseinsertacode"), 
	        		OHSeverityLevel.ERROR));
        }
        if(key.length()>2){
	        errors.add(new OHExceptionMessage("codeTooLongError", 
	        		MessageBundle.getMessage("angal.exatype.codetoolongmaxchar"), 
	        		OHSeverityLevel.ERROR));
        }
        if(description.isEmpty() ){
            errors.add(new OHExceptionMessage("descriptionEmptyError", 
            		MessageBundle.getMessage("angal.exatype.pleaseinsertavaliddescription"), 
            		OHSeverityLevel.ERROR));
        }
        if (insert) {
        	if (codeControl(examType.getCode())){
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
	 * Return the list of {@link ExamType}s.
	 * @return the list of {@link ExamType}s. It could be <code>null</code>
	 * @throws OHServiceException 
	 */
	public ArrayList<ExamType> getExamType() throws OHServiceException {
		return ioOperations.getExamType();
	}

	/**
	 * Insert a new {@link ExamType} in the DB.
	 * 
	 * @param examType - the {@link ExamType} to insert.
	 * @return <code>true</code> if the examType has been inserted, <code>false</code> otherwise.
	 * @throws OHServiceException 
	 */
	public boolean newExamType(ExamType examType) throws OHServiceException {
		validateExamType(examType, true);
		return ioOperations.newExamType(examType);
	}

	/**
	 * Update an already existing {@link ExamType}.
	 * @param examType - the {@link ExamType} to update
	 * @return <code>true</code> if the examType has been updated, <code>false</code> otherwise.
	 * @throws OHServiceException 
	 */
	public boolean updateExamType(ExamType examType) throws OHServiceException {
		validateExamType(examType, false);
		return ioOperations.updateExamType(examType);
	}

	/**
	 * This function controls the presence of a record with the same code as in
	 * the parameter.
	 * @param code - the code
	 * @return <code>true</code> if the code is present, <code>false</code> otherwise.
	 * @throws OHServiceException 
	 */
	public boolean codeControl(String code) throws OHServiceException {
		return ioOperations.isCodePresent(code);
	}

	/**
	 * Delete the passed {@link ExamType}.
	 * @param examType - the {@link ExamType} to delete.
	 * @return <code>true</code> if the examType has been deleted, <code>false</code> otherwise.
	 * @throws OHServiceException 
	 * @throws OHServiceException
	 */
	public boolean deleteExamType(ExamType examType) throws OHServiceException {
		return ioOperations.deleteExamType(examType);
	}
}
