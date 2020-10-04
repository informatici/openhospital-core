/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2020 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
 *
 * Open Hospital is a free and open source software for healthcare data management.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
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
	 * Reads from database hospital information
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
	 * Updates hospital information
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