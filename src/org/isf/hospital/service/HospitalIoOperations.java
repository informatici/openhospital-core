package org.isf.hospital.service;

import java.util.ArrayList;
import java.util.List;

import org.isf.hospital.model.Hospital;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class offers the io operations for recovering and
 * managing hospital record from the database
 * 
 * @author Fin8, Furla, Thoia
 * 
 */
@Service
@Transactional(rollbackFor=OHServiceException.class)
@TranslateOHServiceException
public class HospitalIoOperations {

	@Autowired
	private HospitalIoOperationRepository repository;
	
	/**
	 * Reads from database hospital informations
	 * 
	 * @return {@link Hospital} object
	 * @throws OHServiceException 
	 */
	public Hospital getHospital() throws OHServiceException 
	{
		ArrayList<Hospital> hospitals = (ArrayList<Hospital>) repository.findAll();
				

		return hospitals.get(0);
	}
	
	/**
	 * Reads from database currency cod
	 * @return currency cod
	 * @throws OHServiceException
	 */
	public String getHospitalCurrencyCod() throws OHServiceException {
		List<String> currencyCodes = repository.findAllHospitalCurrencyCode();
		return currencyCodes.isEmpty() ? null : currencyCodes.get(0);
	}
	
	/**
	 * updates hospital informations
	 * 
	 * @return <code>true</code> if the hospital informations have been updated, <code>false</code> otherwise
	 * @throws OHServiceException 
	 */
	public boolean updateHospital(
			Hospital hospital) throws OHServiceException 
	{
		boolean result = true;
	

		Hospital savedHospital = repository.save(hospital);
		result = (savedHospital != null);
		
		return result;
	} 
	
	/**
	 * Sanitize the given {@link String} value. 
	 * This method is maintained only for backward compatibility.
	 * @param value the value to sanitize.
	 * @return the sanitized value or <code>null</code> if the passed value is <code>null</code>.
	 */
	protected String sanitize(String value)
	{
		String result = null;
		
		
		if (value != null) 
		{
			result = value.trim().replaceAll("'", "''");
		}
		
		return result;
	}
}