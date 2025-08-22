package org.isf.medicalhistory.manager;

import java.util.List;

import jakarta.persistence.Column;

import org.isf.medicalhistory.model.MedicalHistory;
import org.isf.medicalhistory.service.MedicalHistoryIoOperations;
import org.isf.utils.exception.OHServiceException;
import org.springframework.stereotype.Component;

@Component
public class MedicalHistoryBrowsingManager {
	private final MedicalHistoryIoOperations ioOperations;

	public MedicalHistoryBrowsingManager(MedicalHistoryIoOperations ioOperations) {
		this.ioOperations = ioOperations;
	}

	/**
	 * Returns the list of {@link MedicalHistory}s
	 * @return the list of {@link MedicalHistory}s
	 * @throws OHServiceException if fails to fetch all {@link MedicalHistory}s
	 */
	public List<MedicalHistory> getAll() throws OHServiceException{
		return ioOperations.getAll();
	}

	/**
	 * add a new {@link MedicalHistory}
	 * @param medicalHistory the {@link MedicalHistory} to add
	 * @return the added {@link MedicalHistory}
	 * @throws OHServiceException when fails to add a new {@link MedicalHistory}
	 */
	public MedicalHistory add(MedicalHistory medicalHistory) throws OHServiceException {
		return ioOperations.add(medicalHistory);
	}

	/**
	 * update a new {@link MedicalHistory}
	 * @param medicalHistory the {@link MedicalHistory} to update
	 * @return a {@link MedicalHistory}
	 * @throws OHServiceException when fails to update the given {@link MedicalHistory}
	 */
	public MedicalHistory update(MedicalHistory medicalHistory) throws OHServiceException {
		return ioOperations.update(medicalHistory);
	}

	/**
	 * get all {@link MedicalHistory}s for a given patient
	 * @param patientCode the id of the {@link org.isf.patient.model.Patient} whose {@link MedicalHistory}s are to be fetched
	 * @return all the {@link MedicalHistory}s for the given patient
	 * @throws OHServiceException when fails to update the given {@link MedicalHistory}
	 */
	public List<MedicalHistory> getMedicalHistoriesByPatientCode(Integer patientCode) throws OHServiceException {
		return ioOperations.getMedicalHistoriesByPatientCode(patientCode);
	}

	/**
	 * get a {@link MedicalHistory} by ID
	 * @param id the id of the {@link MedicalHistory} to be fetched
	 * @return the fetched {@link MedicalHistory}
	 * @throws OHServiceException when fails to fetch the {@link MedicalHistory}
	 */
	public MedicalHistory getMedicalHistoryById(Integer id) throws OHServiceException {
		return ioOperations.getMedicalHistoryById(id);
	}
}
