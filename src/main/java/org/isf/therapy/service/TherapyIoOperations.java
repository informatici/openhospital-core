/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2021 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.therapy.service;

import java.util.List;

import org.isf.patient.model.Patient;
import org.isf.therapy.model.TherapyRow;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = OHServiceException.class)
@TranslateOHServiceException
public class TherapyIoOperations {

	@Autowired
	private TherapyIoOperationRepository repository;

	/**
	 * Insert a new {@link TherapyRow} (therapy) in the DB
	 *
	 * @param thRow - the {@link TherapyRow} (therapy)
	 * @return the therapyID
	 * @throws OHServiceException
	 */
	public TherapyRow newTherapy(TherapyRow thRow) throws OHServiceException {
		return repository.save(thRow);
	}

	/**
	 * Return the list of {@link TherapyRow}s (therapies) for specified Patient ID
	 * or
	 * return all {@link TherapyRow}s (therapies) if <code>0</code> is passed
	 *
	 * @param patID - the Patient ID
	 * @return the list of {@link TherapyRow}s (therapies)
	 * @throws OHServiceException
	 */
	public List<TherapyRow> getTherapyRows(int patID) throws OHServiceException {
		return patID != 0 ? repository.findByPatientCodeOrderByPatientCodeAscTherapyIDAsc(patID)
				: repository.findAllByOrderByPatientAscTherapyIDAsc();
	}

	/**
	 * Delete all {@link TherapyRow}s (therapies) for specified {@link Patient}
	 *
	 * @param patient - the {@link Patient}
	 * @return <code>true</code> if the therapies have been deleted, <code>false</code> otherwise
	 * @throws OHServiceException
	 */
	public boolean deleteAllTherapies(Patient patient) throws OHServiceException {
		repository.deleteByPatient(patient);
		return true;
	}

	/**
	 * Checks if the code is already in use
	 *
	 * @param code - the therapy code
	 * @return <code>true</code> if the code is already in use, <code>false</code> otherwise
	 * @throws OHServiceException
	 */
	public boolean isCodePresent(Integer code) throws OHServiceException {
		return repository.existsById(code);
	}
}
