/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2025 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.encounter.manager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.isf.encounter.model.Encounter;
import org.isf.encounter.model.EncounterStatus;
import org.isf.encounter.service.EncounterIoRepository;
import org.isf.generaldata.MessageBundle;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EncounterBrowserManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(EncounterBrowserManager.class);

	private final EncounterIoRepository encounterIoRepository;

	public EncounterBrowserManager(EncounterIoRepository encounterIoRepository) {
		this.encounterIoRepository = encounterIoRepository;
	}

	/**
	 * Method that inserts a new {@link Encounter}.
	 * @param encounter
	 * @return saved / updated {@link Encounter}
	 * @throws OHServiceException when validation failed
	 */
	public Encounter saveEncounter(Encounter encounter) throws OHServiceException {
		validateEncounter(encounter);
		return encounterIoRepository.save(encounter);
	}

	/**
	 * Method that returns the list of {@link Encounter}s with patient id.
	 * @param patientId - the patient id.
	 * @return the list of {@link Encounter}s.
	 * @throws OHServiceException
	 */
	public List<Encounter> getEncountersByPatient(Integer patientId) throws OHServiceException {
		return encounterIoRepository.findByPatient(patientId);
	}

	/**
	 * Method that returns the {@link Encounter} with specified codes.
	 * @param code - the encounter code.
	 * @return the {@link Encounter}.
	 * @throws OHServiceException
	 */
	public Encounter getEncountersByCode(String code) throws OHServiceException {
		return encounterIoRepository.findByCode(code);
	}

	/**
	 * Method that returns the open {@link Encounter} with patient code.
	 * @param patientCode - the patient code.
	 * @return the {@link Encounter}.
	 * @throws OHServiceException
	 */
	public Encounter getCurrentEncounter(Integer patientCode) throws OHServiceException {
		return encounterIoRepository.findByPatientCodeAndStatusAndClosedAt(patientCode, EncounterStatus.ACTIVE, null);
	}

	/**
	 * Method that returns the {@link Encounter} with id.
	 * @param encounterId - the encounter id.
	 * @return the {@link Encounter}.
	 * @throws OHServiceException
	 */
	public Encounter getEncounterById(int encounterId) throws OHServiceException {
		return encounterIoRepository.findById(encounterId).orElse(null);
	}

	/**
	 * Finds all {@link Encounter} entities for a given patient code and encounter status.
	 * @param code the unique code identifying the patient (must not be {@code null})
	 * @param status the {@link EncounterStatus} of the encounter to filter by (must not be {@code null})
	 * @return a list of {@link Encounter} entities matching the patient code and status, or an empty list if none are found
	 * @throws OHServiceException - If an error occurs
	 */
	List<Encounter> findAllByPatientCodeAndStatus(Integer code, EncounterStatus status) {
		return encounterIoRepository.findAllByPatientCodeAndStatus(code, status);
	}

	/**
	 * Verify if the object is valid for CRUD and return a list of errors, if any.
	 * @param encounter
	 * @throws OHDataValidationException
	 */
	protected void validateEncounter(Encounter encounter) throws OHServiceException {
		List<OHExceptionMessage> errors = new ArrayList<>();

		if (encounter.getPatient() == null) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.encounter.insertpatient.msg")));
		}

		if (encounter.getCode() == null) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.encounter.insertcode.msg")));
		}

		if (encounter.getClosedAt() != null && encounter.getPerformedAt().isAfter(encounter.getClosedAt())) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("performedAt must be before closedAt")));
		}

		//TO DO: Add the check to know if the encounter is on the range of another encounter for the same patient

		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
	}

	/**
	 * Method that checks if there is already an active {@link Encounter} for the given patient at the specified performed date.
	 * @param patientCode - the patient code.
	 * @param performedAt - the date and time when the encounter is performed.
	 * @return {@code true} if another active {@link Encounter} exists at the given date and time, {@code false} otherwise.
	 * @throws OHServiceException if the check fails due to a repository error.
	 */
	private Boolean checkEncounterExisting(Integer patientCode, LocalDateTime performedAt) {
		return encounterIoRepository.existsActiveEncounterAt(patientCode, performedAt);
	}

}
