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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.List;

import org.assertj.core.api.Condition;
import org.isf.OHCoreTestCase;
import org.isf.generaldata.GeneralData;
import org.isf.patadminissue.manager.PatientAdminIssueBrowserManager;
import org.isf.patadminissue.model.PatientAdminIssue;
import org.isf.patadminissue.service.PatientAdminIssueIoOperationRepository;
import org.isf.patient.TestPatient;
import org.isf.patient.model.Patient;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class Tests extends OHCoreTestCase {

	private static TestPatientAdminIssue testPatientAdminIssue;

	@Autowired
	PatientIoOperationRepository patientIoOperationRepository;
	@Autowired
	PatientAdminIssueIoOperationRepository patientAdminIssueIoOperationRepository;
	@Autowired
	PatientAdminIssueBrowserManager patientAdminIssueBrowserManager;

	@BeforeAll
	static void setUpClass() {
		GeneralData.PATIENTPHOTOSTORAGE = "DB";
		testPatientAdminIssue = new TestPatientAdminIssue();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void testIssueGets() throws Exception {
		PatientAdminIssue issue = setupTestPatientAdminIssue(false);
		testPatientAdminIssue.check(issue);
		assertThat(issue.isOpen()).isTrue();
	}

	@Test
	void testIssueSets() throws Exception {
		PatientAdminIssue issue = setupTestPatientAdminIssue(true);
		testPatientAdminIssue.check(issue);
	}

	@Test
	void testMgrOpenIssue() throws Exception {
		Patient patient = setupTestPatient();
		PatientAdminIssue opened = patientAdminIssueBrowserManager.openIssue(new PatientAdminIssue(patient, "  Identity document to be verified  "));
		assertThat(opened.getId()).isPositive();
		assertThat(opened.getReason()).isEqualTo("Identity document to be verified");
		assertThat(opened.getFromDate()).isNotNull();
		assertThat(opened.getToDate()).isNull();
		assertThat(opened.isOpen()).isTrue();
		List<PatientAdminIssue> openIssues = patientAdminIssueBrowserManager.getOpenIssues(patient.getCode());
		assertThat(openIssues).hasSize(1);
		assertThat(openIssues.get(0).getReason()).isEqualTo("Identity document to be verified");
	}

	@Test
	void testMgrOpenIssueStartsNowAndOpen() throws Exception {
		Patient patient = setupTestPatient();
		PatientAdminIssue issue = new PatientAdminIssue(patient, "Missing referral letter");
		issue.setFromDate(LocalDateTime.of(2000, 1, 1, 0, 0));
		issue.setToDate(LocalDateTime.of(2000, 1, 2, 0, 0));
		PatientAdminIssue opened = patientAdminIssueBrowserManager.openIssue(issue);
		assertThat(opened.getFromDate()).isAfter(LocalDateTime.of(2000, 1, 1, 0, 0));
		assertThat(opened.getToDate()).isNull();
	}

	@Test
	void testMgrSeveralIssuesCanBeOpenAtOnce() throws Exception {
		Patient patient = setupTestPatient();
		patientAdminIssueBrowserManager.openIssue(new PatientAdminIssue(patient, "Missing referral letter"));
		patientAdminIssueBrowserManager.openIssue(new PatientAdminIssue(patient, "Identity document to be verified"));
		List<PatientAdminIssue> openIssues = patientAdminIssueBrowserManager.getOpenIssues(patient.getCode());
		assertThat(openIssues).extracting(PatientAdminIssue::getReason)
						.containsExactlyInAnyOrder("Missing referral letter", "Identity document to be verified");
	}

	@Test
	void testMgrResolveIssueKeepsItOnRecord() throws Exception {
		Patient patient = setupTestPatient();
		PatientAdminIssue opened = patientAdminIssueBrowserManager.openIssue(new PatientAdminIssue(patient, "Missing referral letter"));
		PatientAdminIssue resolved = patientAdminIssueBrowserManager.resolveIssue(opened);
		assertThat(resolved.getId()).isEqualTo(opened.getId());
		assertThat(resolved.getToDate()).isNotNull();
		assertThat(resolved.isOpen()).isFalse();
		assertThat(patientAdminIssueBrowserManager.getOpenIssues(patient.getCode())).isEmpty();
		List<PatientAdminIssue> history = patientAdminIssueBrowserManager.getIssues(patient.getCode());
		assertThat(history).hasSize(1);
		assertThat(history.get(0).getReason()).isEqualTo("Missing referral letter");
		assertThat(history.get(0).getToDate()).isNotNull();
	}

	@Test
	void testMgrResolveOneIssueLeavesTheOthersOpen() throws Exception {
		Patient patient = setupTestPatient();
		PatientAdminIssue first = patientAdminIssueBrowserManager.openIssue(new PatientAdminIssue(patient, "Missing referral letter"));
		patientAdminIssueBrowserManager.openIssue(new PatientAdminIssue(patient, "Identity document to be verified"));
		patientAdminIssueBrowserManager.resolveIssue(first);
		List<PatientAdminIssue> openIssues = patientAdminIssueBrowserManager.getOpenIssues(patient.getCode());
		assertThat(openIssues).extracting(PatientAdminIssue::getReason).containsExactly("Identity document to be verified");
		assertThat(patientAdminIssueBrowserManager.getIssues(patient.getCode())).hasSize(2);
	}

	@Test
	void testMgrResolveIssueTwice() throws Exception {
		Patient patient = setupTestPatient();
		PatientAdminIssue opened = patientAdminIssueBrowserManager.openIssue(new PatientAdminIssue(patient, "Missing referral letter"));
		PatientAdminIssue resolved = patientAdminIssueBrowserManager.resolveIssue(opened);
		assertThatThrownBy(() -> patientAdminIssueBrowserManager.resolveIssue(resolved))
						.isInstanceOf(OHServiceException.class)
						.has(new Condition<Throwable>((e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error"));
	}

	@Test
	void testMgrOpenIssueWithoutReason() throws Exception {
		Patient patient = setupTestPatient();
		assertThatThrownBy(() -> patientAdminIssueBrowserManager.openIssue(new PatientAdminIssue(patient, "   ")))
						.isInstanceOf(OHServiceException.class)
						.has(new Condition<Throwable>((e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error"));
		assertThat(patientAdminIssueBrowserManager.getIssues(patient.getCode())).isEmpty();
	}

	@Test
	void testMgrOpenIssueReasonTooLong() throws Exception {
		Patient patient = setupTestPatient();
		String reason = "a".repeat(PatientAdminIssue.REASON_LENGTH + 1);
		assertThatThrownBy(() -> patientAdminIssueBrowserManager.openIssue(new PatientAdminIssue(patient, reason)))
						.isInstanceOf(OHServiceException.class)
						.has(new Condition<Throwable>((e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error"));
	}

	@Test
	void testMgrOpenIssueWithoutPatient() throws Exception {
		assertThatThrownBy(() -> patientAdminIssueBrowserManager.openIssue(new PatientAdminIssue(null, "Missing referral letter")))
						.isInstanceOf(OHServiceException.class)
						.has(new Condition<Throwable>((e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error"));
	}

	@Test
	void testMgrGetOpenIssuesOfSeveralPatients() throws Exception {
		Patient flagged = setupTestPatient();
		Patient cleared = setupTestPatient();
		Patient untouched = setupTestPatient();
		patientAdminIssueBrowserManager.openIssue(new PatientAdminIssue(flagged, "Missing referral letter"));
		PatientAdminIssue resolved = patientAdminIssueBrowserManager.openIssue(new PatientAdminIssue(cleared, "Identity document to be verified"));
		patientAdminIssueBrowserManager.resolveIssue(resolved);
		List<PatientAdminIssue> openIssues = patientAdminIssueBrowserManager
						.getOpenIssues(List.of(flagged.getCode(), cleared.getCode(), untouched.getCode()));
		assertThat(openIssues).hasSize(1);
		assertThat(openIssues.get(0).getPatient().getCode()).isEqualTo(flagged.getCode());
	}

	@Test
	void testToString() throws Exception {
		PatientAdminIssue issue = setupTestPatientAdminIssue(true);
		issue.setFromDate(LocalDateTime.of(2026, 1, 2, 3, 4, 5));
		assertThat(issue.toString())
						.isEqualTo("PatientAdminIssue [id=" + issue.getId() + ", patient=" + issue.getPatient().getCode() + ", reason=TestReason, "
										+ "fromDate=2026-01-02T03:04:05, toDate=null]");
	}

	private Patient setupTestPatient() throws OHException {
		return patientIoOperationRepository.saveAndFlush(new TestPatient().setup(true));
	}

	private PatientAdminIssue setupTestPatientAdminIssue(boolean usingSet) throws OHException {
		PatientAdminIssue issue = testPatientAdminIssue.setup(usingSet);
		patientIoOperationRepository.saveAndFlush(issue.getPatient());
		issue.setFromDate(LocalDateTime.of(2026, 1, 2, 3, 4, 5));
		return patientAdminIssueIoOperationRepository.saveAndFlush(issue);
	}
}
