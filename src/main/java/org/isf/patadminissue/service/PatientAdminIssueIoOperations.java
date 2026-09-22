/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2026 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.patadminissue.service;

import java.util.Collection;
import java.util.List;

import org.isf.patadminissue.model.PatientAdminIssue;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = OHServiceException.class)
@TranslateOHServiceException
public class PatientAdminIssueIoOperations {

	private final PatientAdminIssueIoOperationRepository repository;

	public PatientAdminIssueIoOperations(PatientAdminIssueIoOperationRepository patientAdminIssueIoOperationRepository) {
		this.repository = patientAdminIssueIoOperationRepository;
	}

	/**
	 * Return every {@link PatientAdminIssue} ever raised on the specified patient, the most recent first.
	 *
	 * @param patientCode the patient code
	 * @return the issues, resolved ones included. It could be empty.
	 * @throws OHServiceException
	 */
	public List<PatientAdminIssue> getIssues(int patientCode) throws OHServiceException {
		return repository.findByPatient_CodeOrderByFromDateDesc(patientCode);
	}

	/**
	 * Return the {@link PatientAdminIssue}s still open on the specified patient, the oldest first.
	 *
	 * @param patientCode the patient code
	 * @return the open issues. It could be empty.
	 * @throws OHServiceException
	 */
	public List<PatientAdminIssue> getOpenIssues(int patientCode) throws OHServiceException {
		return repository.findByPatient_CodeAndToDateIsNullOrderByFromDateAsc(patientCode);
	}

	/**
	 * Return the {@link PatientAdminIssue}s still open on any of the specified patients, the oldest first.
	 *
	 * @param patientCodes the patient codes
	 * @return the open issues. It could be empty.
	 * @throws OHServiceException
	 */
	public List<PatientAdminIssue> getOpenIssues(Collection<Integer> patientCodes) throws OHServiceException {
		return repository.findByPatient_CodeInAndToDateIsNullOrderByFromDateAsc(patientCodes);
	}

	/**
	 * Save a {@link PatientAdminIssue}.
	 *
	 * @param issue the issue to save
	 * @return the saved issue.
	 * @throws OHServiceException
	 */
	public PatientAdminIssue saveIssue(PatientAdminIssue issue) throws OHServiceException {
		return repository.save(issue);
	}

}
