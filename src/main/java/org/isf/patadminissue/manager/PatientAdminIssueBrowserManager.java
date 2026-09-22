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
package org.isf.patadminissue.manager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.isf.generaldata.MessageBundle;
import org.isf.patadminissue.model.PatientAdminIssue;
import org.isf.patadminissue.service.PatientAdminIssueIoOperations;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.time.TimeTools;
import org.springframework.stereotype.Component;

@Component
public class PatientAdminIssueBrowserManager {

	private final PatientAdminIssueIoOperations ioOperations;

	public PatientAdminIssueBrowserManager(PatientAdminIssueIoOperations patientAdminIssueIoOperations) {
		this.ioOperations = patientAdminIssueIoOperations;
	}

	/**
	 * Return every {@link PatientAdminIssue} ever raised on the specified patient, the most recent first.
	 *
	 * @param patientCode the patient code
	 * @return the issues, resolved ones included. It could be empty.
	 * @throws OHServiceException
	 */
	public List<PatientAdminIssue> getIssues(int patientCode) throws OHServiceException {
		return ioOperations.getIssues(patientCode);
	}

	/**
	 * Return the {@link PatientAdminIssue}s still open on the specified patient, the oldest first. The patient is flagged
	 * while this list is not empty.
	 *
	 * @param patientCode the patient code
	 * @return the open issues. It could be empty.
	 * @throws OHServiceException
	 */
	public List<PatientAdminIssue> getOpenIssues(int patientCode) throws OHServiceException {
		return ioOperations.getOpenIssues(patientCode);
	}

	/**
	 * Return the {@link PatientAdminIssue}s still open on any of the specified patients, the oldest first.
	 *
	 * @param patientCodes the patient codes
	 * @return the open issues. It could be empty.
	 * @throws OHServiceException
	 */
	public List<PatientAdminIssue> getOpenIssues(Collection<Integer> patientCodes) throws OHServiceException {
		if (patientCodes.isEmpty()) {
			return List.of();
		}
		return ioOperations.getOpenIssues(patientCodes);
	}

	/**
	 * Open a new {@link PatientAdminIssue}: the issue starts now and stays open until it is resolved.
	 *
	 * @param issue the issue to open, with its patient and reason
	 * @return the opened issue.
	 * @throws OHServiceException if the issue is not valid
	 */
	public PatientAdminIssue openIssue(PatientAdminIssue issue) throws OHServiceException {
		return saveIssues(List.of(issue), List.of()).get(0);
	}

	/**
	 * Resolve an open {@link PatientAdminIssue}: the issue ends now and is kept on record.
	 *
	 * @param issue the issue to resolve
	 * @return the resolved issue.
	 * @throws OHServiceException if the issue is not open
	 */
	public PatientAdminIssue resolveIssue(PatientAdminIssue issue) throws OHServiceException {
		return saveIssues(List.of(), List.of(issue)).get(0);
	}

	/**
	 * Open and resolve {@link PatientAdminIssue}s in a single transaction: the issues to open start now, the issues to
	 * resolve end now, and nothing is written unless every issue is valid.
	 *
	 * @param issuesToOpen the issues to open, each with its patient and reason
	 * @param issuesToResolve the open issues to resolve
	 * @return the saved issues, the opened ones first.
	 * @throws OHServiceException if an issue to open is not valid or an issue to resolve is not open
	 */
	public List<PatientAdminIssue> saveIssues(List<PatientAdminIssue> issuesToOpen, List<PatientAdminIssue> issuesToResolve) throws OHServiceException {
		List<OHExceptionMessage> errors = new ArrayList<>();
		for (PatientAdminIssue issue : issuesToOpen) {
			validateIssue(issue, errors);
		}
		for (PatientAdminIssue issue : issuesToResolve) {
			if (issue.getId() == 0 || !issue.isOpen()) {
				errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.patadminissue.theissueisnotopen.msg")));
			}
		}
		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
		LocalDateTime now = TimeTools.getNow();
		for (PatientAdminIssue issue : issuesToOpen) {
			issue.setReason(issue.getReason().trim());
			issue.setFromDate(now);
			issue.setToDate(null);
		}
		issuesToResolve.forEach(issue -> issue.setToDate(now));
		List<PatientAdminIssue> issues = new ArrayList<>(issuesToOpen);
		issues.addAll(issuesToResolve);
		try {
			return ioOperations.saveIssues(issues);
		} catch (OHServiceException e) {
			// nothing was written, so the issues to resolve are still open: leave them as they were
			issuesToResolve.forEach(issue -> issue.setToDate(null));
			throw e;
		}
	}

	/**
	 * Verify if the {@link PatientAdminIssue} is valid for CRUD and add the errors found, if any, to the given list.
	 *
	 * @param issue the issue to validate
	 * @param errors the list the errors are added to
	 */
	private void validateIssue(PatientAdminIssue issue, List<OHExceptionMessage> errors) {
		if (issue.getPatient() == null || issue.getPatient().getCode() == null) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.patadminissue.thepatientismandatory.msg")));
		}
		String reason = issue.getReason();
		if (reason == null || reason.isBlank()) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.patadminissue.thereasonismandatory.msg")));
		} else if (reason.trim().length() > PatientAdminIssue.REASON_LENGTH) {
			errors.add(new OHExceptionMessage(MessageBundle.formatMessage("angal.patadminissue.thereasonistoolongmaxchars.fmt.msg",
							PatientAdminIssue.REASON_LENGTH)));
		}
	}

}
