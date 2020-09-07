package org.isf.vactype.service;

/*------------------------------------------
 * IoOperation - methods to interact with DB
 * -----------------------------------------
 * modification history
 * 19/10/2011 - Cla - version is now 1.0
 *------------------------------------------*/


import java.util.ArrayList;

import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.isf.vaccine.model.Vaccine;
import org.isf.vactype.model.VaccineType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor=OHServiceException.class)
@TranslateOHServiceException
public class VacTypeIoOperation {

	@Autowired
	private VaccineTypeIoOperationRepository repository;
	
	/**
	 * returns all {@link VaccineType}s from DB	
	 * 	
	 * @return the list of {@link VaccineType}s
	 * @throws OHServiceException 
	 */
	public ArrayList<VaccineType> getVaccineType() throws OHServiceException {
		return new ArrayList<VaccineType>(repository.findAllByOrderByDescriptionAsc()); 
	}
	
	/**
	 * inserts a new {@link VaccineType} into DB
	 * 
	 * @param vaccineType - the {@link VaccineType} to insert 
	 * @return <code>true</code> if the item has been inserted, <code>false</code> otherwise
	 * @throws OHServiceException 
	 */
	public boolean newVaccineType(VaccineType vaccineType) throws OHServiceException {
		return repository.save(vaccineType) != null;
	}
	
	/**
	 * updates a {@link VaccineType} in the DB
	 *
	 * @param vaccineType - the item to update
	 * @return <code>true</code> if the item has been updated, <code>false</code> otherwise
	 * @throws OHServiceException 
	 */
	public boolean updateVaccineType(VaccineType vaccineType) throws OHServiceException	{
		return repository.save(vaccineType) != null;
	}
	
	/**
	 * deletes a {@link VaccineType} in the DB
	 *
	 * @param vaccineType - the item to delete
	 * @return <code>true</code> if the item has been deleted, <code>false</code> otherwise
	 * @throws OHServiceException 
	 */
	public boolean deleteVaccineType(VaccineType vaccineType) throws OHServiceException {
		repository.delete(vaccineType);
		return true;
	}
	
	
	/**
	 * checks if the code is already in use
	 *
	 * @param code - the {@link VaccineType} code
	 * @return <code>true</code> if the code is already in use, <code>false</code> otherwise
	 * @throws OHServiceException 
	 */
	public boolean isCodePresent(String code) throws OHServiceException {
		return repository.existsById(code);
	}
	
	/**
	 * returns the {@link VaccineType} based on code
	 *
	 * @param code - the code, must not be {@literal null}
	 * @return the {@link VaccineType} or {@literal null} if none found
	 * @throws OHServiceException 
	 * @throws IllegalArgumentException if {@code code} is {@literal null}
	 */
	public VaccineType findVaccineType(String code) throws OHServiceException 
	{
		if (code != null) {
			return repository.getOne(code);
		}else
			throw new IllegalArgumentException("code must not be null");
	} 
}
