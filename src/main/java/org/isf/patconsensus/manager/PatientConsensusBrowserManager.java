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

import java.util.Optional;

import org.isf.patconsensus.model.PatientConsensus;
import org.isf.patconsensus.service.PatientConsensusIoOperation;
import org.isf.utils.exception.OHServiceException;
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
	public Optional<PatientConsensus> getPatientConsensusByUserId(String userName) throws OHServiceException {
		return ioOperations.getPatientConsensusByUserId(userName);
	}

	/**
	 * Insert an {@link PatientConsensus} in the DB
	 *
	 * @param PatientConsensus - the {@link PatientConsensus} to insert
	 * @return the {@link PatientConsensus}.
	 * @throws OHServiceException
	 */
	public PatientConsensus newPatientConsensus(PatientConsensus patientConsensus) throws OHServiceException {
		return ioOperations.newPatientConsensus(patientConsensus);
	}

	/**
	 * Update an {@link PatientConsensus}
	 *
	 * @param PatientConsensus - the {@link PatientConsensus} to update
	 * @return the {@link PatientConsensus}.
	 * @throws OHServiceException
	 */
	public PatientConsensus updatePatientConsensus(PatientConsensus patientConsensus) throws OHServiceException {
		return ioOperations.updatePatientConsensus(patientConsensus);
	}

	/**
	 * Delete an {@link PatientConsensus}
	 *
	 * @param PatientConsensus - the {@link PatientConsensus} to delete
	 * @return <code>true</code> if the {@link PatientConsensus} has been delete, <code>false</code> otherwise.
	 * @throws OHServiceException
	 */
	public void deletePatientConsensus(PatientConsensus patientConsensus) throws OHServiceException {
		 ioOperations.deletePatientConsensus(patientConsensus);
	}

	/**
	 * Checks if an <code>username</code> has a {@link PatientConsensus}
	 *
	 * @param username - the username
	 * @return <code>true</code> if the username has a PatientConsensus, <code>false</code> otherwise.
	 * @throws OHServiceException
	 */
	public boolean existsByUserName(String username) throws OHServiceException {
		return ioOperations.existsByUserName(username);
	}



}
