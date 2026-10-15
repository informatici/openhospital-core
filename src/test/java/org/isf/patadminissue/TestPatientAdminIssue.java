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
package org.isf.patadminissue;

import static org.assertj.core.api.Assertions.assertThat;

import org.isf.patadminissue.model.PatientAdminIssue;
import org.isf.patient.TestPatient;
import org.isf.patient.model.Patient;
import org.isf.utils.exception.OHException;

public class TestPatientAdminIssue {

	private static String REASON = "TestReason";

	public PatientAdminIssue setup(boolean usingSet) throws OHException {
		PatientAdminIssue issue;
		Patient patient = new TestPatient().setup(true);
		if (usingSet) {
			issue = new PatientAdminIssue();
			setParameters(issue, patient);
		} else {
			// Create PatientAdminIssue with all parameters
			issue = new PatientAdminIssue(patient, REASON);
		}
		return issue;
	}

	public void setParameters(PatientAdminIssue issue, Patient patient) {
		issue.setPatient(patient);
		issue.setReason(REASON);
	}

	public void check(PatientAdminIssue issue) {
		assertThat(issue.getPatient()).isNotNull();
		assertThat(issue.getReason()).isEqualTo(REASON);
	}

}
