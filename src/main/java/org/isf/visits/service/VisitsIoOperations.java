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
package org.isf.visits.service;

import java.util.ArrayList;

import org.isf.patient.model.Patient;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.isf.visits.model.Visit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor=OHServiceException.class)
@TranslateOHServiceException
public class VisitsIoOperations {

	@Autowired
	private VisitsIoOperationRepository repository;
	
	/**
	 * returns the list of all {@link Visit}s related to a patID
	 * 
	 * @param patID - the {@link Patient} ID. If <code>0</code> return the list of all {@link Visit}s
	 * @return the list of {@link Visit}s
	 * @throws OHServiceException 
	 */
	public ArrayList<Visit> getVisits(Integer patID) throws OHServiceException {
		return patID != 0 ?
			new ArrayList<Visit>(repository.findAllByPatient_CodeOrderByPatient_CodeAscDateAsc(patID)) :
		 	new ArrayList<Visit>(repository.findAllByOrderByPatient_CodeAscDateAsc());
	}


	public ArrayList<Visit> getVisitsWard(String wardId
			) throws OHServiceException
	{
		ArrayList<Visit> visits = null;

		if (wardId != null)
			visits = new ArrayList<Visit>(repository.findAllWhereWardByOrderPatientAndDateAsc(wardId));
		else
			visits = new ArrayList<Visit>(repository.findAllByOrderByPatient_CodeAscDateAsc());

		return visits;
	}


	/**
	 * Insert a new {@link Visit} for a patID
	 * 
	 * @param visit - the {@link Visit} related to patID. 
	 * @return the visitID
	 * @throws OHServiceException 
	 */
	public Visit newVisit(Visit visit) throws OHServiceException {
		return repository.save(visit);
	}
	
	/**
	 * Deletes all {@link Visit}s related to a patID
	 * 
	 * @param patID - the {@link Patient} ID
	 * @return <code>true</code> if the list has been deleted, <code>false</code> otherwise
	 * @throws OHServiceException 
	 */
	public boolean deleteAllVisits(int patID) throws OHServiceException {
		repository.deleteByPatient_Code(patID);
        return true;
	}

	/**
	 * checks if the code is already in use
	 *
	 * @param code - the visit code
	 * @return <code>true</code> if the code is already in use, <code>false</code> otherwise
	 * @throws OHServiceException 
	 */
	public boolean isCodePresent(Integer code) throws OHServiceException {
		return repository.existsById(code);
	}

	/**
	 * returns the {@link Visit} based on code
	 *
	 * @param code - the id
	 * @return the {@link Visit} or {@literal null} if none found
	 */
	public Visit findVisit(int id)
	{
		return repository.findById(id).orElse(null);
	}
}
