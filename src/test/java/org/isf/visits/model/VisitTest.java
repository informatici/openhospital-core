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
package org.isf.visits.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.BDDAssertions.then;

import java.time.LocalDateTime;

import org.isf.OHCoreTestCase;
import org.isf.menu.model.User;
import org.isf.patient.TestPatient;
import org.isf.patient.model.Patient;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.sessionaudit.model.UserSession;
import org.isf.utils.exception.OHException;
import org.isf.visits.TestVisit;
import org.isf.visits.manager.VisitManager;
import org.isf.visits.service.VisitsIoOperationRepository;
import org.isf.visits.service.VisitsIoOperations;
import org.isf.ward.TestWard;
import org.isf.ward.model.Ward;
import org.isf.ward.service.WardIoOperationRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class VisitTest extends OHCoreTestCase {

	private static TestVisit testVisit;
	private static TestPatient testPatient;
	private static TestWard testWard;

	@Autowired
	VisitsIoOperations visitsIoOperation;
	@Autowired
	VisitManager visitManager;
	@Autowired
	VisitsIoOperationRepository visitsIoOperationRepository;
	@Autowired
	PatientIoOperationRepository patientIoOperationRepository;
	@Autowired
	WardIoOperationRepository wardIoOperationRepository;

	@BeforeAll
	static void setUpClass() {
		UserSession.setUser(new User("TestUser", null, "testpass", "test"));
		testVisit = new TestVisit();
		testPatient = new TestPatient();
		testWard = new TestWard();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void constructor_shouldSetFields() throws Exception {
		int id = setupTestVisit(false);
		checkVisitIntoDb(id);
	}

	@Test
	void setters_shouldSetFields() throws Exception {
		int id = setupTestVisit(true);
		checkVisitIntoDb(id);
	}

	@Test
	void getEnd_shouldAddDurationToDate() {
		// given
		Visit visit = new Visit();
		visit.setDate(LocalDateTime.of(2023, 3, 15, 14, 30, 0));
		visit.setDuration(90);
		// when
		LocalDateTime visitEnd = visit.getEnd();
		// then
		then(visitEnd).isEqualTo(LocalDateTime.of(2023, 3, 15, 16, 0, 0));
	}

	@Test
	void setters_shouldUpdateDurationAndService() throws Exception {
		int id = setupTestVisit(false);
		Visit visit = visitManager.findVisit(id);

		assertThat(visit.getDuration()).isEqualTo(10);
		visit.setDuration(null);
		assertThat(visit.getDuration()).isNull();

		assertThat(visit.getService()).isEqualTo("testService");
		visit.setService("");
		assertThat(visit.getService()).isEmpty();
	}

	@Test
	void toStringSMS_shouldFormatDate() throws Exception {
		int id = setupTestVisit(false);
		Visit visit = visitManager.findVisit(id);
		assertThat(visit.toStringSMS()).isEqualTo("08/09/10 00:00");
	}

	@Test
	void toString_shouldUseWardServiceAndDate() throws Exception {
		int id = setupTestVisit(false);
		Visit visit = visitManager.findVisit(id);
		assertThat(visit).hasToString("TestDescription - testService - 08/09/10 - 00:00:00");
	}

	@Test
	void toString_shouldUseOpdLabel_whenWardIsNull() throws Exception {
		int id = setupTestVisit(false);
		Visit visit = visitManager.findVisit(id);
		visit.setWard(null);
		// NOTE: resource bundle key value included here
		assertThat(visit).hasToString("angal.menu.opd - testService - 08/09/10 - 00:00:00");
	}

	@Test
	void toString_shouldOmitService_whenServiceIsNull() throws Exception {
		int id = setupTestVisit(false);
		Visit visit = visitManager.findVisit(id);
		visit.setService(null);
		assertThat(visit).hasToString("TestDescription - 08/09/10 - 00:00:00");
	}

	@Test
	void formatDateTime_shouldFormatDateTime() throws Exception {
		int id = setupTestVisit(false);
		Visit visit = visitManager.findVisit(id);
		assertThat(visit.formatDateTime(visit.getDate())).isEqualTo("08/09/10 - 00:00:00");
	}

	@Test
	void formatDateTimeSMS_shouldFormatDateTimeWithoutSeconds() throws Exception {
		int id = setupTestVisit(false);
		Visit visit = visitManager.findVisit(id);
		assertThat(visit.formatDateTimeSMS(visit.getDate())).isEqualTo("08/09/10 00:00");
	}

	@Test
	void equals_shouldUseVisitId() throws Exception {
		int id = setupTestVisit(false);
		Visit visit = visitManager.findVisit(id);

		assertThat(visit)
			.isNotNull()
			.isNotEqualTo("someString");

		Visit visit2 = new Visit(-1, null, null, null, false, null, null, null);
		assertThat(visit).isNotEqualTo(visit2);

		visit2.setVisitID(visit.getVisitID());
		assertThat(visit).isEqualTo(visit2);
	}

	@Test
	void hashCode_shouldUseVisitId() throws Exception {
		int id = setupTestVisit(true);
		Visit visit = visitManager.findVisit(id);
		int hashCode = visit.hashCode();
		assertThat(hashCode).isEqualTo(23 * 133 + visit.getVisitID());
		// return computed value
		assertThat(visit.hashCode()).isEqualTo(hashCode);
	}

	private int setupTestVisit(boolean usingSet) throws OHException {
		Patient patient = testPatient.setup(false);
		Ward ward = testWard.setup(false);
		patientIoOperationRepository.saveAndFlush(patient);
		wardIoOperationRepository.saveAndFlush(ward);
		Visit visit = testVisit.setup(patient, usingSet, ward);
		visitsIoOperationRepository.saveAndFlush(visit);
		return visit.getVisitID();
	}

	private void checkVisitIntoDb(int id) {
		Visit foundVisit = visitsIoOperation.findVisit(id);
		testVisit.check(foundVisit);
	}

}
