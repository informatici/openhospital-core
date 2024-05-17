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
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.isf.patconsensus.service;

import java.util.Optional;

import org.isf.patconsensus.model.PatientConsensus;
import org.isf.patient.model.Patient;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = OHServiceException.class)
@TranslateOHServiceException
public class PatientConsensusIoOperation {

	private PatientConmsensusIoOperationRepository repository;

	private PatientIoOperationRepository patientRepository;

	public PatientConsensusIoOperation(PatientConmsensusIoOperationRepository patientConmsensusIoOperationRepository, PatientIoOperationRepository patientIoOperationRepository) {
		this.repository = patientConmsensusIoOperationRepository;
		this.patientRepository = patientIoOperationRepository;
	}

	/**
	 * Return the {@link PatientConsensus} if patient code exists.
	 *
	 * @return the {@link PatientConsensus}. It could be {@code empty}.
	 * @throws OHServiceException
	 */
	public Optional<PatientConsensus> getPatientConsensusByUserId(Integer patientId) throws OHServiceException {
		return repository.findByPatient_Code(patientId);
	}


	/**
	 * Update an {@link PatientConsensus}.
	 *
	 * @param patientConsensus
	 *            - the {@link PatientConsensus} to update
	 * @return the updated {@link PatientConsensus}.
	 * @throws OHServiceException
	 */
	public PatientConsensus updatePatientConsensus(PatientConsensus patientConsensus) throws OHServiceException {
		Optional<Patient> foundPatient = patientRepository.findById(patientConsensus.getPatient().getCode());
		if (foundPatient.isPresent()) {
			patientConsensus.setPatient(foundPatient.get());
			return repository.save(patientConsensus);
		}
		throw new OHServiceException(new OHExceptionMessage("Patient not found."));
	}

	/**
	 * Checks if the {@link PatientConsensus} with the specified code has already been used.
	 *
	 * @param code
	 *            - the code
	 * @return {@code true} if the code is already in use, {@code false} otherwise.
	 * @throws OHServiceException
	 */
	public boolean existsByPatientCode(Integer code) throws OHServiceException {
		return repository.existsByPatient_Code(code);
	}

}
