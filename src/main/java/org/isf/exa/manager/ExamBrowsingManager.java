/**
 * 19-dec-2005
 * 14-jan-2006
 */
package org.isf.exa.manager;

import java.util.ArrayList;
import java.util.List;

import org.isf.exa.model.Exam;
import org.isf.exa.service.ExamIoOperations;
import org.isf.exatype.model.ExamType;
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
 * Class that provides gui separation from database operations and gives some
 * useful logic manipulations of the dinamic data (memory)
 * 
 * @author bob
 * 
 */
@Component
public class ExamBrowsingManager {

	@Autowired
	private ExamIoOperations ioOperations;

	private final Logger logger = LoggerFactory.getLogger(ExamBrowsingManager.class);
	
	/**
	 * Verify if the object is valid for CRUD and return a list of errors, if any
	 * @param exam
	 * @param insert <code>true</code> or updated <code>false</code>
	 * @throws OHServiceException 
	 */
	protected void validateExam(Exam exam, boolean insert) throws OHServiceException {
		String key = exam.getCode();
		String description = exam.getDescription();
        List<OHExceptionMessage> errors = new ArrayList<OHExceptionMessage>();
        if(key.isEmpty() || description.isEmpty()){
	        errors.add(new OHExceptionMessage("codeAndOrDescriptionEmptyError", 
	        		MessageBundle.getMessage("angal.exa.pleaseinsertcodeoranddescription"), 
	        		OHSeverityLevel.ERROR));
        }
        if (insert) {
        	if (true == isKeyPresent(exam)) {
				throw new OHDataIntegrityViolationException(new OHExceptionMessage(null, 
						MessageBundle.getMessage("angal.exa.changethecodebecauseisalreadyinuse"),
						OHSeverityLevel.ERROR));
			}
        }
        if (!errors.isEmpty()){
	        throw new OHDataValidationException(errors);
	    }
    }
	
	/**
	 * Returns the list of {@link Exam}s
	 * @return the list of {@link Exam}s. It could be <code>null</code>
	 * @throws OHServiceException 
	 */
	public ArrayList<Exam> getExams() throws OHServiceException {
		return ioOperations.getExams();
	}
	
	/**
	 * Returns the list of {@link Exam}s
	 * @return the list of {@link Exam}s. It could be <code>null</code>
	 * @throws OHServiceException 
	 * @deprecated use getExam() instead
	 */
	public ArrayList<Exam> getExamsbyDesc() throws OHServiceException {
		return this.getExams();
	}
	
	/**
	 * Returns the list of {@link Exam}s that matches passed description
	 * @param description - the exam description
	 * @return the list of {@link Exam}s. It could be <code>null</code>
	 * @throws OHServiceException 
	 */
	public ArrayList<Exam> getExams(String description) throws OHServiceException {
		return ioOperations.getExamsByDesc(description);
	}
	
	/**
	 * Returns the list of {@link ExamType}s
	 * @return the list of {@link ExamType}s. It could be <code>null</code>
	 * @throws OHServiceException 
	 */
	public ArrayList<ExamType> getExamType() throws OHServiceException {
		return ioOperations.getExamType();
	}

	/**
	 * This function controls the presence of a record with the same key as in
	 * the parameter; Returns false if the query finds no record, else returns
	 * true
	 * 
	 * @param the {@link Exam}
	 * @return <code>true</code> if the Exam code has already been used, <code>false</code> otherwise
	 * @throws OHServiceException 
	 */
	public boolean isKeyPresent(Exam exam) throws OHServiceException {
		return ioOperations.isKeyPresent(exam);
	}
	
	/**
	 * Insert a new {@link Exam} in the DB.
	 * 
	 * @param exam - the {@link Exam} to insert
	 * @return <code>true</code> if the {@link Exam} has been inserted, <code>false</code> otherwise
	 * @throws OHServiceException 
	 */
	public boolean newExam(Exam exam) throws OHServiceException {
		validateExam(exam, true);
		return ioOperations.newExam(exam);
	}

	/**
	 * Updates an existing {@link Exam} in the db
	 * 
	 * @param exam -  the {@link Exam} to update
	 * @return <code>true</code> if the existing {@link Exam} has been updated, <code>false</code> otherwise
	 * @throws OHServiceException 
	 */
	public boolean updateExam(Exam exam) throws OHServiceException {
		validateExam(exam, false);
		return ioOperations.updateExam(exam);
	}

	/**
	 * Delete an {@link Exam}
	 * @param exam - the {@link Exam} to delete
	 * @return <code>true</code> if the {@link Exam} has been deleted, <code>false</code> otherwise
	 * @throws OHServiceException 
	 */
	public boolean deleteExam(Exam exam) throws OHServiceException {
		return ioOperations.deleteExam(exam);
	}
}
