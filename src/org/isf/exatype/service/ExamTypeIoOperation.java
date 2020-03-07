package org.isf.exatype.service;

import java.util.ArrayList;

import org.isf.exatype.model.ExamType;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor=OHServiceException.class)
@TranslateOHServiceException
public class ExamTypeIoOperation {

	@Autowired
	private ExamTypeIoOperationRepository repository;
	
	/**
	 * Return the list of {@link ExamType}s.
	 * @return the list of {@link ExamType}s.
	 * @throws OHServiceException
	 */
	public ArrayList<ExamType> getExamType() throws OHServiceException 
	{
		return new ArrayList<ExamType>(repository.findAllByOrderByDescriptionAsc()); 	
	}
	
	/**
	 * Update an already existing {@link ExamType}.
	 * @param examType - the {@link ExamType} to update
	 * @return <code>true</code> if the examType has been updated, <code>false</code> otherwise.
	 * @throws OHServiceException
	 */
	public boolean updateExamType(
			ExamType examType) throws OHServiceException 
	{
		boolean result = true;
	

		ExamType savedExamType = repository.save(examType);
		result = (savedExamType != null);
		
		return result;
	}
	
	/**
	 * Insert a new {@link ExamType} in the DB.
	 * @param examType - the {@link ExamType} to insert.
	 * @return <code>true</code> if the examType has been inserted, <code>false</code> otherwise.
	 * @throws OHServiceException
	 */
	public boolean newExamType(
			ExamType examType) throws OHServiceException 
	{
		boolean result = true;
	
		
		repository.save(examType);
		
		return result;
	}
	
	/**
	 * Delete the passed {@link ExamType}.
	 * @param examType - the {@link ExamType} to delete.
	 * @return <code>true</code> if the examType has been deleted, <code>false</code> otherwise.
	 * @throws OHServiceException
	 */
	public boolean deleteExamType(
			ExamType examType) throws OHServiceException 
	{
		boolean result = true;
	
		
		repository.delete(examType);
		
		return result;
	}
	
	/**
	 * This function controls the presence of a record with the same code as in
	 * the parameter.
	 * @param code - the code
	 * @return <code>true</code> if the code is present, <code>false</code> otherwise.
	 * @throws OHServiceException
	 */
	public boolean isCodePresent(
			String code) throws OHServiceException
	{
		boolean result = true;
	
		
		result = repository.exists(code);
		
		return result;
	}
}
