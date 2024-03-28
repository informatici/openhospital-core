/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2024 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.isf.patient.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import jakarta.persistence.EntityManager;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.isf.generaldata.GeneralData;
import org.isf.patient.model.Patient;
import org.isf.patient.model.PatientMergedEvent;
import org.isf.patient.model.PatientProfilePhoto;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.pagination.PageInfo;
import org.isf.utils.pagination.PagedResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = OHServiceException.class)
@TranslateOHServiceException
public class PatientIoOperations {

	private static final Logger LOGGER = LoggerFactory.getLogger(PatientIoOperations.class);

	public static final String LOAD_FROM_DB = "DB";

	public static final char NOT_DELETED_STATUS = 'N';

	private final PatientIoOperationRepository repository;

	private final ApplicationEventPublisher applicationEventPublisher;

	private final FileSystemPatientPhotoRepository fileSystemPatientPhotoRepository;

	private final EntityManager entityManager;

	public PatientIoOperations(PatientIoOperationRepository repository, ApplicationEventPublisher applicationEventPublisher, FileSystemPatientPhotoRepository fileSystemPatientPhotoRepository, EntityManager entityManager) {
		this.repository = repository;
		this.applicationEventPublisher = applicationEventPublisher;
		this.fileSystemPatientPhotoRepository = fileSystemPatientPhotoRepository;
		this.entityManager = entityManager;
	}
	/**
	 * Method that returns the full list of {@link Patient}s not logically deleted,
	 *
	 * @return the list of {@link Patient}s
	 * @throws OHServiceException
	 */
	public List<Patient> getPatients() throws OHServiceException {
		return repository.findByDeletedOrDeletedIsNull(NOT_DELETED_STATUS);
	}

	/**
	 * Method that returns the full list of {@link Patient}s not logically deleted by page.
	 *
	 * @return the list of {@link Patient}s
	 * @throws OHServiceException
	 */
	public List<Patient> getPatients(Pageable pageable) throws OHServiceException {
		return repository.findAllByDeletedIsNullOrDeletedEqualsOrderByName('N', pageable).getContent();
	}
	
	public PagedResponse<Patient> getPatientsPageable(Pageable pageable) throws OHServiceException {
		Page<Patient> pagedResult = repository.findAllByDeletedIsNullOrDeletedEqualsOrderByName('N', pageable);
		return setPaginationData(pagedResult);
	}

	/**
	 * Method that returns the full list of {@link Patient}s with specified parameters.
	 *
	 * @param parameters
	 * @return the list of {@link Patient}s.
	 * @throws OHServiceException
	 */
	public List<Patient> getPatients(Map<String, Object> parameters) throws OHServiceException {
		return repository.getPatientsByParams(parameters);
	}

	/**
	 * Method that returns the full list of {@link Patient}s not logically deleted, having
	 * the passed String in:<br>
	 * - code<br>
	 * - firstName<br>
	 * - secondName<br>
	 * - taxCode<br>
	 * - note<br>
	 *
	 * @param keyword - String to search, use {@code null} for full list
	 * @return the list of {@link Patient}s (could be empty),
	 * @throws OHServiceException
	 */
	public List<Patient> getPatientsByOneOfFieldsLike(String keyword) throws OHServiceException {
		return repository.findByFieldsContainingWordsFromLiteral(keyword);
	}

	/**
	 * Method that gets a {@link Patient}s by his/her ID.
	 *
	 * @param code
	 * @return the {@link Patient} that matches the specified ID or {@code null}.
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
	 * Method that gets a {@link Patient} by his/her name.
	 * 
	 * @param name
	 * @return the {@link Patient} that matches the specified ID or {@code null}.
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
	 * Get a {@link Patient} by his/her ID, even if he/her has been logically deleted.
	 *
	 * @param code
	 * @return  the {@link Patient} that matches the specified ID or {@code null}.
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
	 * Save / update a {@link Patient}.
	 *
	 * @param patient the recently saved {@link Patient}.
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
			LOGGER.error("Exception in savePatient method.", e);
		}
		return null;
	}

	/**
	 * Method that updates an existing {@link Patient}.
	 *
	 * @param patient - the {@link Patient} to update
	 * @return the updated {@link Patient} object.
	 * @throws OHServiceException
	 */
	public Patient updatePatient(Patient patient) throws OHServiceException {
		return repository.save(patient);
	}

	/**
	 * Method that logically deletes a {@link Patient} (not physically deleted).
	 *
	 * @param patient
	 * @throws OHServiceException
	 */
	public void deletePatient(Patient patient) throws OHServiceException {
		boolean isLoadProfilePhotoFromDB = LOAD_FROM_DB.equals(GeneralData.PATIENTPHOTOSTORAGE);
		if (isLoadProfilePhotoFromDB) {
			Optional<Patient> foundPatient = repository.findById(patient.getCode());
			if (foundPatient.isPresent()) {
				foundPatient.get().setPatientProfilePhoto(null);
			} else {
				LOGGER.error("Patient not found to delete with code {}.", patient.getCode());
				throw new OHServiceException(new OHExceptionMessage("Patient not found to delete with code " + patient.getCode()));
			}
		} else {
			fileSystemPatientPhotoRepository.delete(GeneralData.PATIENTPHOTOSTORAGE, patient.getCode());
		}
		repository.updateDeleted(patient.getCode());
	}

	/**
	 * Method that check if a {@link Patient}  is already present in the DB by his/her name
	 * (the passed string 'name' should be a concatenation of firstName + " " + secondName),
	 *
	 * @param name
	 * @return true - if the patient is already present
	 * @throws OHServiceException
	 */
	public boolean isPatientPresentByName(String name) throws OHServiceException {
		return !repository.findByNameAndDeleted(name, NOT_DELETED_STATUS).isEmpty();
	}

	/**
	 * Method that gets the next PAT_ID that is going to be used.
	 *
	 * @return code
	 * @throws OHServiceException
	 */
	public int getNextPatientCode() throws OHServiceException {
		return repository.findMaxCode() + 1;
	}

	/**
	 * Method that merges all clinic details under the same PAT_ID.
	 *
	 * @param mergedPatient
	 * @param obsoletePatient
	 * @throws OHServiceException
	 */
	public void mergePatientHistory(Patient mergedPatient, Patient obsoletePatient) throws OHServiceException {
		repository.updateDeleted(obsoletePatient.getCode());
		applicationEventPublisher.publishEvent(new PatientMergedEvent(obsoletePatient, mergedPatient));
	}

	/**
	 * Checks if the code is already in use.
	 *
	 * @param code - the patient code
	 * @return {@code true} if the code is already in use, {@code false} otherwise
	 * @throws OHServiceException
	 */
	public boolean isCodePresent(Integer code) throws OHServiceException {
		return repository.existsById(code);
	}

	/**
	 * Method that returns a list of cities to be used.
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
		} else {
			((Session) this.entityManager.getDelegate()).evict(patient);
			fileSystemPatientPhotoRepository.loadInPatient(patient, GeneralData.PATIENTPHOTOSTORAGE);
		}
		return patient.getPatientProfilePhoto();
	}

	PagedResponse<Patient> setPaginationData(Page<Patient> pages){
		PagedResponse<Patient> data = new PagedResponse<>();
		data.setData(pages.getContent());
		data.setPageInfo(PageInfo.from(pages));
		return data;
	}
  
	/**
	 * Count all active {@link Patient}s
	 * 
	 * @return
	 * @throws OHServiceException
	 */
	public long countAllActivePatients() throws OHServiceException {
		return repository.countAllActiveNotDeletedPatients();
	}

}
