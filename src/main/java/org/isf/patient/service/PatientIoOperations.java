/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2023 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.patient.service;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.isf.generaldata.GeneralData;
import org.isf.patient.model.Patient;
import org.isf.patient.model.PatientMergedEvent;
import org.isf.patient.model.PatientProfilePhoto;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ------------------------------------------<br />
 * PatientIoOperations - dB operations for the patient entity<br />
 * -----------------------------------------<br />
 * modification history<br />
 * 05/05/2005 - giacomo  - first beta version.<br />
 * 03/11/2006 - ross - added toString method.<br />
 * 11/08/2008 - alessandro - added father & mother's names.<br />
 * 26/08/2008 - claudio - added birth date modified age.<br />
 * 01/01/2009 - Fabrizio - changed the calls to PAT_AGE fields to return again an int type.<br />
 * 03/12/2009 - Alex - added method for merge two patients history.<br />
 * ------------------------------------------
 */
@Service
@Transactional(rollbackFor = OHServiceException.class)
@TranslateOHServiceException
public class PatientIoOperations {

	public static final String LOAD_FROM_DB = "DB";

	public static final char NOT_DELETED_STATUS = 'N';
	
	@Autowired
	private PatientIoOperationRepository repository;
	
	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;
 
	@Autowired
	private FileSystemPatientPhotoRepository fileSystemPatientPhotoRepository;

	@Autowired
	private EntityManager entityManager;
	/**
	 * Method that returns the full list of Patients not logically deleted
	 *
	 * @return the list of patients
	 * @throws OHServiceException
	 */
	public List<Patient> getPatients() throws OHServiceException {
		return repository.findByDeletedOrDeletedIsNull(NOT_DELETED_STATUS);
	}

	/**
	 * Method that returns the full list of Patients not logically deleted by page
	 *
	 * @return the list of patients
	 * @throws OHServiceException
	 */
	public List<Patient> getPatients(Pageable pageable) throws OHServiceException {
		return repository.findAllByDeletedIsNullOrDeletedEqualsOrderByName('N', pageable);
	}

	/**
	 * Method that returns the full list of Patients by parameters
	 *
	 * @param parameters
	 * @return
	 * @throws OHServiceException
	 */
	public List<Patient> getPatients(Map<String, Object> parameters) throws OHServiceException {
		return repository.getPatientsByParams(parameters);
	}

	/**
	 * Method that returns the full list of Patients not logically deleted, having
	 * the passed String in:<br>
	 * - code<br>
	 * - firstName<br>
	 * - secondName<br>
	 * - taxCode<br>
	 * - note<br>
	 *
	 * @param keyword - String to search, <code>null</code> for full list
	 * @return the list of Patients (could be empty)
	 * @throws OHServiceException
	 */
	public List<Patient> getPatientsByOneOfFieldsLike(String keyword) throws OHServiceException {
		return repository.findByFieldsContainingWordsFromLiteral(keyword);
	}

	/**
	 * Method that gets a Patient by his/her ID
	 *
	 * @param code
	 * @return the Patient that match specified ID
	 * @throws OHServiceException
	 */
	public Patient getPatient(Integer code) throws OHServiceException {
		List<Patient> patients = repository.findAllWhereIdAndDeleted(code, NOT_DELETED_STATUS);
		if (!patients.isEmpty()) {
			Patient patient = patients.get(patients.size() - 1);
			retrievePatientProfilePhoto(patient);
			return patient;
		}
		return null;
	}

	/**
	 * Method that gets a Patient by his/her name
	 * 
	 * @param name
	 * @return
	 * @throws OHServiceException
	 */
	public Patient getPatient(String name) throws OHServiceException {
		List<Patient> patients = repository.findByNameAndDeletedOrderByName(name, NOT_DELETED_STATUS);
		if (!patients.isEmpty()) {
			Patient patient = patients.get(patients.size() - 1);
			retrievePatientProfilePhoto(patient);
			return patient;
		}
		return null;
	}

	/**
	 * Get a Patient by his/her ID, even if he/her has been logically deleted
	 *
	 * @param code
	 * @return the list of Patients
	 * @throws OHServiceException
	 */
	public Patient getPatientAll(Integer code) throws OHServiceException {
		Patient patient = repository.findById(code).orElse(null);
		if (patient != null) {
			retrievePatientProfilePhoto(patient);
		}
		return patient;
	}

	/**
	 * Save / update patient
	 *
	 * @param patient
	 * @return saved / updated patient
	 */
	public Patient savePatient(Patient patient) {
		boolean isLoadProfilePhotoFromDB = LOAD_FROM_DB.equals(GeneralData.PATIENTPHOTOSTORAGE);
		if (isLoadProfilePhotoFromDB) {
			return repository.save(patient);
		}
		try {
			PatientProfilePhoto photo = patient.getPatientProfilePhoto();
			patient.setPatientProfilePhoto(null);
			Patient patientSaved = repository.save(patient);
			((Session) this.entityManager.getDelegate()).evict(patient);
			if (photo != null && photo.getPhoto() != null) {
				fileSystemPatientPhotoRepository.save(GeneralData.PATIENTPHOTOSTORAGE, patient.getCode(), photo.getPhoto());
			} else if (this.fileSystemPatientPhotoRepository.exist(GeneralData.PATIENTPHOTOSTORAGE, patient.getCode())) {
				this.fileSystemPatientPhotoRepository.delete(GeneralData.PATIENTPHOTOSTORAGE, patient.getCode());
			}
				
			return patientSaved;
		} catch (OHServiceException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Method that updates an existing {@link Patient} in the db
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
	 * Method that logically deletes a Patient (not physically deleted)
	 *
	 * @param patient
	 * @return true - if the Patient has been deleted (logically)
	 * @throws OHServiceException
	 */
	public boolean deletePatient(Patient patient) throws OHServiceException {
		boolean isLoadProfilePhotoFromDB = LOAD_FROM_DB.equals(GeneralData.PATIENTPHOTOSTORAGE);
		if (isLoadProfilePhotoFromDB) {
			repository.findById(patient.getCode()).get().setPatientProfilePhoto(null);
		} else {
			this.fileSystemPatientPhotoRepository.delete(GeneralData.PATIENTPHOTOSTORAGE, patient.getCode());		
		}
		return  repository.updateDeleted(patient.getCode()) > 0;
	}

	/**
	 * Method that check if a Patient is already present in the DB by his/her name
	 * (the passed string 'name' should be a concatenation of firstName + " " + secondName
	 *
	 * @param name
	 * @return true - if the patient is already present
	 * @throws OHServiceException
	 */
	public boolean isPatientPresentByName(String name) throws OHServiceException {
		return !repository.findByNameAndDeleted(name, NOT_DELETED_STATUS).isEmpty();
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
	 * Method that merges all clinic details under the same PAT_ID
	 *
	 * @param mergedPatient
	 * @param obsoletePatient
	 * @return true - if no OHServiceExceptions occurred
	 * @throws OHServiceException
	 */
	public boolean mergePatientHistory(Patient mergedPatient, Patient obsoletePatient) throws OHServiceException {
		repository.updateDeleted(obsoletePatient.getCode());
		applicationEventPublisher.publishEvent(new PatientMergedEvent(obsoletePatient, mergedPatient));
		return true;
	}

	/**
	 * Checks if the code is already in use
	 *
	 * @param code - the patient code
	 * @return <code>true</code> if the code is already in use, <code>false</code> otherwise
	 * @throws OHServiceException
	 */
	public boolean isCodePresent(Integer code) throws OHServiceException {
		return repository.existsById(code);
	}
	
	/**
	 * Method that get cities is going to be used.
	 *
	 * @return list of Cities
	 * @throws OHServiceException
	 */
	public List<String> getCities() throws OHServiceException {
		return repository.findCities();
	}

	public PatientProfilePhoto retrievePatientProfilePhoto(Patient patient) throws OHServiceException {
		boolean isLoadProfilePhotoFromDB = LOAD_FROM_DB.equals(GeneralData.PATIENTPHOTOSTORAGE);
		if (isLoadProfilePhotoFromDB) {
			Hibernate.initialize(patient.getPatientProfilePhoto());
			return patient.getPatientProfilePhoto();
		} else {
			((Session) this.entityManager.getDelegate()).evict(patient);
			fileSystemPatientPhotoRepository.loadInPatient(patient, GeneralData.PATIENTPHOTOSTORAGE);
			return patient.getPatientProfilePhoto();
		}
	}

}
