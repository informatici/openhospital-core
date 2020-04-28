package org.isf.patient.service;

/*------------------------------------------
 * IoOperations - dB operations for the patient entity
 * -----------------------------------------
 * modification history
 * 05/05/2005 - giacomo  - first beta version 
 * 03/11/2006 - ross - added toString method. Gestione apici per
 *                     nome, cognome, citta', indirizzo e note
 * 11/08/2008 - alessandro - added father & mother's names
 * 26/08/2008 - claudio    - added birth date
 * 							 modififed age
 * 01/01/2009 - Fabrizio   - changed the calls to PAT_AGE fields to
 *                           return again an int type
 * 03/12/2009 - Alex       - added method for merge two patients history
 *------------------------------------------*/

import java.util.ArrayList;
import java.util.List;

import org.isf.patient.model.Patient;
import org.isf.patient.model.PatientMergedEvent;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(rollbackFor=OHServiceException.class)
@TranslateOHServiceException
public class PatientIoOperations 
{
	public static final String NOT_DELETED_STATUS = "N";
	@Autowired
	private PatientIoOperationRepository repository;
	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;
	
	/**
	 * method that returns the full list of Patients not logically deleted
	 * 
	 * @return the list of patients
	 * @throws OHServiceException
	 */
	public ArrayList<Patient> getPatients() throws OHServiceException {
		return new ArrayList<Patient>(repository.findByDeletedOrDeletedIsNull(NOT_DELETED_STATUS));
	}
	
	/**
	 * method that returns the full list of Patients not logically deleted by page
	 * 
	 * @return the list of patients
	 * @throws OHServiceException
	 */
	public ArrayList<Patient> getPatients(Pageable pageable) throws OHServiceException {
		return new ArrayList<Patient>(repository.findAllByDeletedIsNullOrDeletedEqualsOrderByName("N", pageable));
	}

	/**
	 * method that returns the full list of Patients not logically deleted with Height and Weight 
	 * 
	 * @param regex
	 * @return the full list of Patients with Height and Weight
	 * @throws OHServiceException
	 */
	public ArrayList<Patient> getPatientsByOneOfFieldsLike(String regex) throws OHServiceException {
		return new ArrayList<Patient>(repository.findByFieldsContainingWordsFromLiteral(regex));
	}	

	/**
	 * method that get a Patient by his/her name
	 * 
	 * @param name
	 * @return the Patient that match specified name
	 * @throws OHServiceException
	 */
	public Patient getPatient(String name) throws OHServiceException {
		List<Patient> patients = repository.findByNameAndDeletedOrderByName(name, NOT_DELETED_STATUS);
		return patients.size() > 0 ? patients.get(patients.size()-1) : null;
	}

	/**
	 * method that get a Patient by his/her ID
	 * 
	 * @param code
	 * @return the Patient
	 * @throws OHServiceException
	 */
	public Patient getPatient(Integer code) throws OHServiceException {
		List<Patient> patients = repository.findAllWhereIdAndDeleted(code, NOT_DELETED_STATUS);
		return patients.size() > 0 ? patients.get(patients.size()-1) : null;
	}

	/**
	 * get a Patient by his/her ID, even if he/her has been logically deleted
	 * 
	 * @param code
	 * @return the list of Patients
	 * @throws OHServiceException
	 */
	public Patient getPatientAll(Integer code) throws OHServiceException {
		return repository.findOne(code);
	}

	/**
	 * Method that insert a new Patient in the dB
	 * 
	 * @param patient
	 * @return true - if the new Patient has been inserted
	 * @throws OHServiceException
	 */
	public boolean newPatient(Patient patient) throws OHServiceException {
		return repository.save(patient) != null;
	}
	
	/**
	 * 
	 * method that update an existing {@link Patient} in the db
	 * 
	 * @param patient - the {@link Patient} to update
	 * @return true - if the existing {@link Patient} has been updated
	 * @throws OHServiceException
	 */
	public boolean updatePatient(Patient patient) throws OHServiceException {
		repository.save(patient);
		return true;
	}

	/**
	 * method that logically delete a Patient (not physically deleted)
	 * 
	 * @param aPatient
	 * @return true - if the Patient has been deleted (logically)
	 * @throws OHServiceException
	 */
	public boolean deletePatient(Patient patient) throws OHServiceException {
		return repository.updateDeleted(patient.getCode()) > 0;
	}

	/**
	 * method that check if a Patient is already present in the DB by his/her name
	 * 
	 * @param name
	 * @return true - if the patient is already present
	 * @throws OHServiceException
	 */
	public boolean isPatientPresent(String name) throws OHServiceException {
		return repository.findByNameAndDeleted(name, NOT_DELETED_STATUS).size() > 0;
	}

	/**
	 * Method that get next PAT_ID is going to be used.
	 * 
	 * @return code
	 * @throws OHServiceException
	 */
	public int getNextPatientCode() throws OHServiceException {
		return repository.findMaxCode() + 1;
	}

	/**
	 * method that merge all clinic details under the same PAT_ID
	 * 
	 * @param mergedPatient
	 * @param obsoletePatient
	 * @return true - if no OHServiceExceptions occurred
	 * @throws OHServiceException 
	 */
	@Transactional
	public boolean mergePatientHistory(Patient mergedPatient, Patient obsoletePatient) throws OHServiceException {
		repository.updateDelete(obsoletePatient.getCode());
		applicationEventPublisher.publishEvent(new PatientMergedEvent(obsoletePatient, mergedPatient));
		
		return true;
	}

	/**
	 * checks if the code is already in use
	 *
	 * @param code - the patient code
	 * @return <code>true</code> if the code is already in use, <code>false</code> otherwise
	 * @throws OHServiceException 
	 */
	public boolean isCodePresent(Integer code) throws OHServiceException {
		return repository.exists(code);
	}

}
