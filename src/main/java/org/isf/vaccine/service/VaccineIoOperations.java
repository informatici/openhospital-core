package org.isf.vaccine.service;

import java.util.ArrayList;

import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.isf.vaccine.model.Vaccine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class offers the io operations for recovering and managing
 * vaccine records from the database
 *
 * @author Eva
 * 
 * modification history
 * 20/10/2011 - Cla - insert vaccinetype managment
 *
 */

@Service
@Transactional(rollbackFor=OHServiceException.class)
@TranslateOHServiceException
public class VaccineIoOperations {

	@Autowired
	private VaccineIoOperationRepository repository;
	
	/**
	 * returns the list of {@link Vaccine}s based on vaccine type code
	 *
	 * @param vaccineTypeCode - the type code. If <code>null</code> returns all {@link Vaccine}s in the DB
	 * @return the list of {@link Vaccine}s
	 * @throws OHServiceException 
	 */
	public ArrayList<Vaccine> getVaccine(String vaccineTypeCode) throws OHServiceException {
		return new ArrayList<Vaccine>(
			vaccineTypeCode != null ?
				repository.findByVaccineType_CodeOrderByDescriptionAsc(vaccineTypeCode) :
				repository.findAllByOrderByDescriptionAsc()
		);
	}

	/**
	 * inserts a new {@link Vaccine} in the DB
	 *
	 * @param vaccine - the item to insert
	 * @return <code>true</code> if the item has been inserted, <code>false</code> otherwise
	 * @throws OHServiceException 
	 */
	public boolean newVaccine(Vaccine vaccine) throws OHServiceException {
		return repository.save(vaccine) != null;
	}
	
	/**
	 * updates a {@link Vaccine} in the DB
	 *
	 * @param vaccine - the item to update
	 * @return <code>true</code> if the item has been updated, <code>false</code> otherwise
	 * @throws OHServiceException 
	 */
	public boolean updateVaccine(Vaccine vaccine) throws OHServiceException {
		return repository.save(vaccine) != null;
	}

	/**
	 * deletes a {@link Vaccine} in the DB
	 *
	 * @param vaccine - the item to delete
	 * @return <code>true</code> if the item has been deleted, <code>false</code> otherwise
	 * @throws OHServiceException 
	 */
	public boolean deleteVaccine(Vaccine vaccine) throws OHServiceException {
		repository.delete(vaccine);
		return true;
	}

	/**
	 * checks if the code is already in use
	 *
	 * @param code - the vaccine code
	 * @return <code>true</code> if the code is already in use, <code>false</code> otherwise
	 * @throws OHServiceException 
	 */
	public boolean isCodePresent(String code) throws OHServiceException {
		return repository.exists(code);
	}
	
	/**
	 * returns the {@link Vaccine} based on code
	 *
	 * @param code - the code, must not be {@literal null}
	 * @return the {@link Vaccine} or {@literal null} if none found
	 * @throws OHServiceException 
	 * @throws IllegalArgumentException if {@code code} is {@literal null}
	 */
	public Vaccine findVaccine(String code) throws OHServiceException 
	{
		if (code != null) {
			return repository.findOne(code);
		}else
			throw new IllegalArgumentException("code must not be null");
	} 
}


