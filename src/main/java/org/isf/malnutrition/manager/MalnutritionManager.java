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
package org.isf.malnutrition.manager;

import org.isf.generaldata.MessageBundle;
import org.isf.malnutrition.model.Malnutrition;
import org.isf.malnutrition.service.MalnutritionIoOperation;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Manager for malnutrition module.
 *
 */
@Component
public class MalnutritionManager {

	private final Logger logger = LoggerFactory.getLogger(MalnutritionManager.class);

	@Autowired
	private MalnutritionIoOperation ioOperation;
	
	/**
	 * Verify if the object is valid for CRUD and return a list of errors, if any
	 * @param malnutrition
	 * @throws OHDataValidationException 
	 */
	protected void validateMalnutrition(Malnutrition malnutrition) throws OHDataValidationException {
		List<OHExceptionMessage> errors = new ArrayList<OHExceptionMessage>();
		if(malnutrition.getDateSupp()==null) {
			errors.add(new OHExceptionMessage("visitDateNullError", 
	        		MessageBundle.getMessage("angal.malnutrition.pleaseinsertavalidvisitdate"), 
	        		OHSeverityLevel.ERROR));
		}
		if(malnutrition.getDateConf()==null) {
			errors.add(new OHExceptionMessage("controlDateNullError", 
	        		MessageBundle.getMessage("angal.malnutrition.pleaseinsertavalidcontroldate"), 
	        		OHSeverityLevel.ERROR));
		}
		if(malnutrition.getDateSupp()!=null && 
				malnutrition.getDateConf()!=null &&
				malnutrition.getDateConf().before(malnutrition.getDateSupp())) {
			errors.add(new OHExceptionMessage("controlBeforeVisitError", 
	        		MessageBundle.getMessage("angal.malnutrition.controldatemustbeaftervisitdate"), 
	        		OHSeverityLevel.ERROR));
		}
		if(malnutrition.getWeight()==0) {
			errors.add(new OHExceptionMessage("zeroWeightError", 
	        		MessageBundle.getMessage("angal.malnutrition.insertcorrectvalueinweightfield"), 
	        		OHSeverityLevel.ERROR));
		}
		if(malnutrition.getHeight()==0) {
			errors.add(new OHExceptionMessage("zeroHeightError", 
	        		MessageBundle.getMessage("angal.malnutrition.insertcorrectvalueinheightfield"), 
	        		OHSeverityLevel.ERROR));
		}
		if (!errors.isEmpty()){
	        throw new OHDataValidationException(errors);
	    }
    }

	/**
	 * Retrieves all the {@link Malnutrition} associated to the given admission id.
	 * In case of wrong parameters an error message is shown and <code>null</code> value is returned.
	 * In case of error a message error is shown and an empty list is returned.
	 * @param admissionID the admission id to use as filter.
	 * @return all the retrieved malnutrition or <code>null</code> if the specified admission id is <code>null</code>.
	 * @throws OHServiceException 
	 */
	public ArrayList<Malnutrition> getMalnutrition(String admissionID) throws OHServiceException{
		return ioOperation.getMalnutritions(admissionID);
	}
	
	/**
	 * returns the last {@link Malnutrition} entry for specified patient ID
	 * @param patientID - the patient ID
	 * @return the last {@link Malnutrition} for specified patient ID. <code>null</code> if none.
	 * @throws OHServiceException 
	 */
	public Malnutrition getLastMalnutrition(int patientID) throws OHServiceException {
		return ioOperation.getLastMalnutrition(patientID);
	}

	/**
	 * Stores a new {@link Malnutrition}. The malnutrition object is updated with the generated id.
	 * @param malnutrition the malnutrition to store.
	 * @return <code>true</code> if the malnutrition has been stored
	 * @throws OHServiceException 
	 */
	public boolean newMalnutrition(Malnutrition malnutrition) throws OHServiceException{
		validateMalnutrition(malnutrition);
		return ioOperation.newMalnutrition(malnutrition);
	}

	/**
	 * Updates the specified malnutrition
	 * 
	 * returns false if abortIfLocked == true and there is a lock from another user on the record 
	 * (someone else updated it during this update)
	 * 
	 *  true if the record was updated
	 *  
	 *  throws an exception if the update fails for some other reason
	 *  
	 * @param malnutrition the {@link Malnutrition} to update
	 * @return the updated {@link Malnutrition}
	 * @throws OHServiceException
	 */
	public Malnutrition updateMalnutrition(Malnutrition malnutrition) throws OHServiceException {
		validateMalnutrition(malnutrition);
		return ioOperation.updateMalnutrition(malnutrition);
	}
	
	/**
	 * Deletes the specified {@link Malnutrition}.
	 * In case of wrong parameters an error message is shown and <code>false</code> value is returned.
	 * In case of error a message error is shown and a <code>false</code> value is returned.
	 * @param malnutrition the malnutrition to delete.
	 * @return <code>true</code> if the malnutrition has been deleted, <code>false</code> otherwise.
	 * @throws OHServiceException 
	 */
	public boolean deleteMalnutrition(Malnutrition malnutrition) throws OHServiceException{
		return ioOperation.deleteMalnutrition(malnutrition);
	}
}
