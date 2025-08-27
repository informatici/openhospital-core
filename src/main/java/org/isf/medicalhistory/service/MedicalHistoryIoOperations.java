package org.isf.medicalhistory.service;

import java.util.List;

import org.isf.medicalhistory.model.MedicalHistory;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@TranslateOHServiceException
public class MedicalHistoryIoOperations {
	
	private final MedicalHistoryIoOperationRepository repository;

	public MedicalHistoryIoOperations(MedicalHistoryIoOperationRepository repository) {
		this.repository = repository;
	}

	/**
	 * Returns the list of {@link MedicalHistory}s
	 * @return the list of {@link MedicalHistory}s
	 * @throws OHServiceException if fails to fetch all {@link MedicalHistory}s
	 */
	public List<MedicalHistory> getAll() throws OHServiceException{
		return repository.findAll();
	}

	/**
	 * add a new {@link MedicalHistory}
	 * @param medicalHistory the {@link MedicalHistory} to add
	 * @return the added {@link MedicalHistory}
	 * @throws OHServiceException when fails to add a new {@link MedicalHistory}
	 */
	public MedicalHistory add(MedicalHistory medicalHistory) throws OHServiceException {
		return repository.save(medicalHistory);
	}

	/**
	 * update a new {@link MedicalHistory}
	 * @param medicalHistory the {@link MedicalHistory} to update
	 * @return a {@link MedicalHistory}
	 * @throws OHServiceException when fails to update the given {@link MedicalHistory}
	 */
	public MedicalHistory update(MedicalHistory medicalHistory) throws OHServiceException {
		return repository.save(medicalHistory);
	}

	/**
	 * get all {@link MedicalHistory}s for a given patient
	 * @param patientCode the id of the {@link org.isf.patient.model.Patient} whose {@link MedicalHistory}s are to be fetched
	 * @return all the {@link MedicalHistory}s for the given patient
	 * @throws OHServiceException when fails to update the given {@link MedicalHistory}
	 */
	public List<MedicalHistory> getMedicalHistoriesByPatientCode(Integer patientCode) throws OHServiceException {
		return repository.findByPatientCode(patientCode);
	}

	/**
	 * get a {@link MedicalHistory} by ID
	 * @param id the id of the {@link MedicalHistory} to be fetched
	 * @return the fetched {@link MedicalHistory}
	 * @throws OHServiceException when fails to fetch the {@link MedicalHistory}
	 */
	public MedicalHistory getMedicalHistoryById(int id) throws OHServiceException {
		return repository.findById(id).orElse(null);
	}
}