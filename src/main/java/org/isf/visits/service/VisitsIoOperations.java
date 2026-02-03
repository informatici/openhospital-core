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
package org.isf.visits.service;

import java.util.List;

import org.isf.patient.model.Patient;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.isf.visits.model.Visit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = OHServiceException.class)
@TranslateOHServiceException
public class VisitsIoOperations {

	private VisitsIoOperationRepository repository;

	public VisitsIoOperations(VisitsIoOperationRepository visitsIoOperationRepository) {
		this.repository = visitsIoOperationRepository;
	}

	/**
	 * Returns the list of all {@link Visit}s related to a {@link Patient} ID.
	 * 
	 * @param patID - the {@link Patient} ID. If {@code 0} return the list of all {@link Visit}s
	 * @return the list of {@link Visit}s
	 * @throws OHServiceException 
	 */
	public List<Visit> getVisits(Integer patID) throws OHServiceException {
		return patID != 0 ? repository.findAllByPatient_CodeOrderByPatient_CodeAscDateAsc(patID) : repository.findAllByOrderByPatient_CodeAscDateAsc();
	}

	/**
	 * Returns the list of all {@link Visit}s related to a {@link Patient} ID in OPD (Ward is {@code null}).
	 *
	 * @param patID - the {@link Patient} ID. If {@code 0} return the list of all OPD {@link Visit}s
	 * @return the list of {@link Visit}s
	 * @throws OHServiceException
	 */
	public List<Visit> getVisitsOPD(Integer patID) throws OHServiceException {
		return patID != 0 ? repository.findAllByWardIsNullAndPatient_CodeOrderByPatient_CodeAscDateAsc(patID)
						: repository.findAllByWardIsNullOrderByPatient_CodeAscDateAsc();
	}
	public Visit getVisit(int visitID) throws OHServiceException {
		return repository.findAllByVisitID(visitID);

	}

	/**
	 * Returns the list of all {@link Visit}s related to a wardId.
	 *
	 * @param wardId - if {@code null}, returns all visits for all wards
	 * @return the list of {@link Visit}s
	 * @throws OHServiceException
	 */
	public List<Visit> getVisitsWard(String wardId) throws OHServiceException {
		List<Visit> visits;
		if (wardId != null) {
			visits = repository.findAllWhereWardByOrderDateAsc(wardId);
		} else {
			visits = repository.findAllByOrderByPatient_CodeAscDateAsc();
		}
		return visits;
	}

	/**
	 * Insert a new {@link Visit}.
	 * 
	 * @param visit - the {@link Visit}.
	 * @return the inserted {@link Visit} object.
	 * @throws OHServiceException 
	 */
	public Visit newVisit(Visit visit) throws OHServiceException {
		return repository.save(visit);
	}

	/**
	 * Update a {@link Visit}.
	 * 
	 * @param visit - the {@link Visit}.
	 * @return the updated {@link Visit} object.
	 * @throws OHServiceException 
	 */
	@Transactional
	public Visit updateVisit(Visit visit) throws OHServiceException {
		return repository.save(visit);
	}

	/**
	 * Checks if the code is already in use.
	 *
	 * @param code - the {@link Visit} code
	 * @return {@code true} if the code is already in use, {@code false} otherwise
	 * @throws OHServiceException 
	 */
	public boolean isCodePresent(Integer code) throws OHServiceException {
		return repository.existsById(code);
	}

	/**
	 * Returns a {@link Visit} based on the Visit id.
	 *
	 * @param id - the id
	 * @return the {@link Visit} or {@literal null} if none found
	 */
	public Visit findVisit(int id) {
		return repository.findById(id).orElse(null);
	}

	/**
	 * Delete a {@link Visit}.
	 *
	 * @param visit - the {@link Visit}
	 */
	public void deleteVisit(Visit visit) throws OHServiceException {
		repository.delete(visit);
	}

	/**
	 * Count active {@link Visit}s
	 * 
	 * @return the number of recorded {@link Visit}s
	 * @throws OHServiceException
	 */
	public long countAllActiveAppointments() {
		return this.repository.countAllActiveAppointments();
	}

}
