package org.isf.cares.manager;

import org.isf.cares.model.Care;
import org.isf.cares.services.CareIoOperation;
import org.isf.conditioning.model.Conditioning;
import org.isf.utils.exception.OHServiceException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CareManager {

	private final CareIoOperation careIoOperation;


	public CareManager(CareIoOperation careIoOperation) {
		this.careIoOperation = careIoOperation;
	}

	/**
	 * Method that inserts a new {@link Care}.
	 * @param care
	 * @return saved / updated {@link Care}
	 * @throws OHServiceException when validation failed
	 */
	public Care saveCare(Care care) throws OHServiceException {
		return careIoOperation.saveCare(care);
	}

	/**
	 * Validate and update an existing {@link Care}.
	 *
	 * @param care - Care entity to validate and update
	 * @return updated {@link Care} if successful
	 * @throws OHServiceException When validation or update operation fails
	 */
	public Care updateCare(Care care) throws OHServiceException {
		return careIoOperation.updateCare(care);
	}

	/**
	 * Method that returns the list of {@link Care}s with patient id.
	 * @param patientId - the patient id.
	 * @return the list of {@link Care}s.
	 * @throws OHServiceException
	 */
	public List<Care> getCaresByPatient(Integer patientId) throws OHServiceException {
		return careIoOperation.getCaresByPatient(patientId);
	}

	/**
	 * Method that returns the {@link Care} with id.
	 * @param careId - the care id.
	 * @return the {@link Care}.
	 * @throws OHServiceException
	 */
	public Care getCareById(int careId) throws OHServiceException {
		return careIoOperation.getCareById(careId);
	}
}
