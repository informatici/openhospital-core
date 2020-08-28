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
		return repository.exists(code);
	}

	/**
	 * returns the {@link Visit} based on code
	 *
	 * @param code - the id
	 * @return the {@link Visit} or {@literal null} if none found
	 */
	public Visit findVisit(int id)
	{
		return repository.findOne(id);
	}
}
