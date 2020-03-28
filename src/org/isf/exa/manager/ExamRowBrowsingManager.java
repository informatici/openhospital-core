package org.isf.exa.manager;

import java.util.ArrayList;
import java.util.List;

import org.isf.exa.model.ExamRow;
import org.isf.exa.service.ExamRowIoOperations;
import org.isf.generaldata.MessageBundle;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ExamRowBrowsingManager {
	
	@Autowired
	private ExamRowIoOperations ioOperations;
		
	private final Logger logger = LoggerFactory.getLogger(ExamRowBrowsingManager.class);
	
	/**
	 * Verify if the object is valid for CRUD and return a list of errors, if any
	 * @param examRow
	 * @throws OHDataValidationException 
	 */
	protected void validateExamRow(ExamRow examRow) throws OHDataValidationException {
		String description = examRow.getDescription();
        List<OHExceptionMessage> errors = new ArrayList<OHExceptionMessage>();
        if(description.isEmpty()){
	        errors.add(new OHExceptionMessage("descriptionEmptyError", 
	        		MessageBundle.getMessage("angal.exa.insertdescription"), 
	        		OHSeverityLevel.ERROR));
        }
        if (!errors.isEmpty()){
	        throw new OHDataValidationException(errors);
	    }
    }
	
	/**
	 * Returns the list of {@link ExamRow}s
	 * @return the list of {@link ExamRow}s. It could be <code>null</code>
	 * @throws OHServiceException 
	 */
	public ArrayList<ExamRow> getExamRow() throws OHServiceException {
            
            return this.getExamRow(0, null);
	}
	/**
	 * Returns a list of {@link ExamRow}s that matches passed exam code
	 * @param aExamCode - the exam code
	 * @return the list of {@link ExamRow}s. It could be <code>null</code>
	 * @throws OHServiceException 
	 */
	public ArrayList<ExamRow> getExamRow(int aExamCode) throws OHServiceException {
		return this.getExamRow(aExamCode, null);
	}

	/**
	 * Returns a list of {@link ExamRow}s that matches passed exam code and description
	 * @param aExamRowCode - the exam code
	 * @param aDescription - the exam description
	 * @return the list of {@link ExamRow}s. It could be <code>null</code>
	 * @throws OHServiceException 
	 */
	public ArrayList<ExamRow> getExamRow(int aExamRowCode, String aDescription) throws OHServiceException {
		return ioOperations.getExamRow(aExamRowCode, aDescription);
	}
	
	/**
	 * Insert a new {@link ExamRow} in the DB.
	 * 
	 * @param examRow - the {@link ExamRow} to insert
	 * @return <code>true</code> if the {@link ExamRow} has been inserted, <code>false</code> otherwise
	 * @throws OHServiceException 
	 */
	public boolean newExamRow(ExamRow examRow) throws OHServiceException {
		validateExamRow(examRow);
		return ioOperations.newExamRow(examRow);
	}

	/**
	 * Delete an {@link ExamRow}.
	 * @param examRow - the {@link ExamRow} to delete
	 * @return <code>true</code> if the {@link ExamRow} has been deleted, <code>false</code> otherwise
	 * @throws OHServiceException 
	 */
	public boolean deleteExamRow(ExamRow examRow) throws OHServiceException {
		return ioOperations.deleteExamRow(examRow);
	}

    /**
	 * Returns a list of {@link ExamRow}s that matches passed exam code
	 * @param aExamCode - the exam code
	 * @return the list of {@link ExamRow}s. It could be <code>null</code>
	 * @throws OHServiceException 
	 */
	public ArrayList<ExamRow> getExamRowByExamCode(String aExamCode) throws OHServiceException {
		return ioOperations.getExamRowByExamCode(aExamCode);
	}
}
