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

import org.isf.opetype.model.OperationType;
import org.isf.patconsensus.model.PatientConsensus;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = OHServiceException.class)
@TranslateOHServiceException
public class PatientConsensusIoOperation {

	@Autowired
	private PatientConmsensusIoOperationRepository repository;

	@Autowired
	private PatientIoOperationRepository patientRepository;

	/**
	 * Return the PatientConsensus if exists {@link PatientConsensus}s
	 *
	 * @return the {@link PatientConsensus}s. It could be <code>empty</code>.
	 * @throws OHServiceException
	 */
	public Optional<PatientConsensus> getPatientConsensusByUserId(Integer patientId) throws OHServiceException {
		return repository.findByPatient_Code(patientId);
	}


	/**
	 * Update an {@link PatientConsensus}
	 *
	 * @param patientConsensus
	 *            - the {@link PatientConsensus} to update
	 * @return <code>PatientConsensus</code>.
	 * @throws OHServiceException
	 */
	public PatientConsensus updatePatientConsensus(PatientConsensus patientConsensus) throws OHServiceException {
		patientConsensus.setPatient(this.patientRepository.findById(patientConsensus.getPatient().getCode()).get());
		return repository.save(patientConsensus);
	}


	/**
	 * Checks if an {@link OperationType} code has already been used
	 *
	 * @param code
	 *            - the code
	 * @return <code>true</code> if the code is already in use, <code>false</code> otherwise.
	 * @throws OHServiceException
	 */
	public boolean existsByPatientCode(Integer code) throws OHServiceException {
		return repository.existsByPatient_Code(code);
	}

}
