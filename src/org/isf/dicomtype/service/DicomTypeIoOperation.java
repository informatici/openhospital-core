package org.isf.dicomtype.service;

import java.util.ArrayList;

import org.isf.dicomtype.model.DicomType;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor=OHServiceException.class)
@TranslateOHServiceException
public class DicomTypeIoOperation {

	@Autowired
	private DicomTypeIoOperationRepository repository;
	
	/**
	 * method that returns all DicomTypes in a list
	 * 
	 * @return the list of all DicomTypes
	 * @throws OHServiceException
	 */
	public ArrayList<DicomType> getDicomType() throws OHServiceException 
	{
		return new ArrayList<DicomType>(repository.findAllByOrderByDicomTypeDescriptionAsc());
	}

	/**
	 * method that updates an already existing DicomType
	 * 
	 * @param DicomType
	 * @return true - if the existing DicomType has been updated
	 * @throws OHServiceException
	 */
	public boolean updateDicomType(
			DicomType DicomType) throws OHServiceException 
	{
		boolean result = true;
	
		
		DicomType savedDicomType = repository.save(DicomType);
		result = (savedDicomType != null);
		
		return result;
	}

	/**
	 * method that create a new DicomType
	 * 
	 * @param DicomType
	 * @return true - if the new DicomType has been inserted
	 * @throws OHServiceException
	 */
	public boolean newDicomType(
			DicomType DicomType) throws OHServiceException 
	{
		boolean result = true;
	
		
		DicomType savedDicomType = repository.save(DicomType);
		result = (savedDicomType != null);
		
		return result;
	}

	/**
	 * method that delete a DicomType
	 * 
	 * @param DicomType
	 * @return true - if the DicomType has been deleted
	 * @throws OHServiceException
	 */
	public boolean deleteDicomType(
			DicomType DicomType) throws OHServiceException
	{
		boolean result = true;
	
		
		repository.delete(DicomType);
		
		return result;
	}

	/**
	 * method that check if a DicomType already exists
	 * 
	 * @param code
	 * @return true - if the DicomType already exists
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
