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
package org.isf.patconsensus.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.isf.generaldata.MessageBundle;
import org.isf.patconsensus.model.PatientConsensus;
import org.isf.patconsensus.service.PatientConsensusIoOperation;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PatientConsensusBrowserManager {

	@Autowired
	private PatientConsensusIoOperation ioOperations;

	/**
	 * Return the {@link PatientConsensus}
	 *
	 * @return the {@link PatientConsensus}s. It could be <code>empty</code>qq.
	 * @throws OHServiceException
	 */
	public Optional<PatientConsensus> getPatientConsensusByUserId(Integer userId) throws OHServiceException {
		return ioOperations.getPatientConsensusByUserId(userId);
	}


	/**
	 * Update an {@link PatientConsensus}
	 *
	 * @param PatientConsensus - the {@link PatientConsensus} to update
	 * @return the {@link PatientConsensus}.
	 * @throws OHServiceException
	 */
	public PatientConsensus updatePatientConsensus(PatientConsensus patientConsensus) throws OHServiceException {
		validate(patientConsensus);
		return ioOperations.updatePatientConsensus(patientConsensus);
	}


	private void validate(PatientConsensus patientConsensus) throws OHDataValidationException {
		List<OHExceptionMessage> errors = new ArrayList<>();
		if (!patientConsensus.isConsensusFlag()) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.patient.consensus.consensus.mandatory.msg")));
		}
		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}

	}


	/**
	 * Checks if an <code>username</code> has a {@link PatientConsensus}
	 *
	 * @param username - the username
	 * @return <code>true</code> if the username has a PatientConsensus, <code>false</code> otherwise.
	 * @throws OHServiceException
	 */
	public boolean existsByPatientCode(Integer code) throws OHServiceException {
		return ioOperations.existsByPatientCode(code);
	}



}
