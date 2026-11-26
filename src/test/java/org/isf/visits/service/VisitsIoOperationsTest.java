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
package org.isf.visits.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.time.temporal.ChronoUnit;
import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.menu.model.User;
import org.isf.patient.TestPatient;
import org.isf.patient.model.Patient;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.sessionaudit.model.UserSession;
import org.isf.utils.exception.OHException;
import org.isf.visits.TestVisit;
import org.isf.visits.manager.VisitManager;
import org.isf.visits.model.Visit;
import org.isf.ward.TestWard;
import org.isf.ward.model.Ward;
import org.isf.ward.service.WardIoOperationRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class VisitsIoOperationsTest extends OHCoreTestCase {

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
	void getVisits_shouldReturnPatientVisits_whenPatientCodeIsGiven() throws Exception {
		// given:
		int id = setupTestVisit(false);
		// when:
		Visit foundVisit = visitsIoOperationRepository.findById(id).orElse(null);
		assertThat(foundVisit).isNotNull();
		List<Visit> visits = visitsIoOperation.getVisits(foundVisit.getPatient().getCode());
		// then:
		assertThat(visits.get(visits.size() - 1).getDate()).isCloseTo(foundVisit.getDate(), within(1, ChronoUnit.SECONDS));
	}

	@Test
	void getVisits_shouldReturnAllVisits_whenPatientCodeIsZero() throws Exception {
		// given:
		int id = setupTestVisit(false);
		Visit foundVisit = visitsIoOperationRepository.findById(id).orElse(null);
		assertThat(foundVisit).isNotNull();

		// when:
		List<Visit> visits = visitsIoOperation.getVisits(0);

		// then:
		assertThat(visits.get(visits.size() - 1).getDate()).isCloseTo(foundVisit.getDate(), within(1, ChronoUnit.SECONDS));
	}

	@Test
	void getVisitsWard_shouldReturnAllVisits_whenNoWardIdIsGiven() throws Exception {
		int id = setupTestVisit(false);
		Visit visit = visitsIoOperationRepository.findById(id).orElse(null);
		assertThat(visit).isNotNull();
		List<Visit> visits = visitsIoOperation.getVisitsWard(null);
		assertThat(visits.get(visits.size() - 1).getDate()).isCloseTo(visit.getDate(), within(1, ChronoUnit.SECONDS));
	}

	@Test
	void getVisitsWard_shouldReturnWardVisits_whenWardIdIsGiven() throws Exception {
		int id = setupTestVisit(false);
		Visit visit = visitsIoOperationRepository.findById(id).orElse(null);
		assertThat(visit).isNotNull();
		List<Visit> visits = visitsIoOperation.getVisitsWard(visit.getWard().getCode());
		assertThat(visits.get(visits.size() - 1).getDate()).isCloseTo(visit.getDate(), within(1, ChronoUnit.SECONDS));
	}

	@Test
	void newVisit_shouldSaveVisit() throws Exception {
		Patient patient = testPatient.setup(false);
		Ward ward = testWard.setup(false);
		patientIoOperationRepository.saveAndFlush(patient);
		wardIoOperationRepository.saveAndFlush(ward);
		Visit visit = testVisit.setup(patient, true, ward);
		int id = visitsIoOperation.newVisit(visit).getVisitID();
		checkVisitIntoDb(id);
	}

	@Test
	void findVisit_shouldReturnVisit_whenIdExists() throws Exception {
		int id = setupTestVisit(false);
		Visit result = visitsIoOperation.findVisit(id);

		assertThat(result).isNotNull();
		assertThat(result.getVisitID()).isEqualTo(id);
	}

	@Test
	void getVisitsOPD_shouldReturnEmpty_whenPatientVisitHasWard() throws Exception {
		// given:
		int id = setupTestVisit(false);
		// when:
		Visit foundVisit = visitsIoOperationRepository.findById(id).orElse(null);
		assertThat(foundVisit).isNotNull();
		List<Visit> visits = visitsIoOperation.getVisitsOPD(foundVisit.getPatient().getCode());
		// then:
		assertThat(visits).isEmpty();
	}

	@Test
	void getVisitsOPD_shouldReturnEmpty_whenPatientCodeIsZeroAndVisitHasWard() throws Exception {
		// given:
		setupTestVisit(false);
		// when:
		List<Visit> visits = visitsIoOperation.getVisitsOPD(0);
		// then:
		assertThat(visits).isEmpty();
	}

	@Test
	void getVisitsOPD_shouldReturnPatientVisit_whenVisitHasNoWard() throws Exception {
		// given:
		Patient patient = testPatient.setup(false);
		patientIoOperationRepository.saveAndFlush(patient);
		Visit visit = testVisit.setup(patient, true, null);
		visitsIoOperationRepository.saveAndFlush(visit);
		// when:
		List<Visit> visits = visitsIoOperation.getVisitsOPD(visit.getPatient().getCode());
		// then:
		assertThat(visits).isNotEmpty();
		assertThat(visits.get(visits.size() - 1).getDate()).isCloseTo(visit.getDate(), within(1, ChronoUnit.SECONDS));
	}

	@Test
	void getVisitsOPD_shouldReturnVisitWithoutWard_whenPatientCodeIsZero() throws Exception {
		// given:
		Patient patient = testPatient.setup(false);
		patientIoOperationRepository.saveAndFlush(patient);
		Visit visit = testVisit.setup(patient, true, null);
		visitsIoOperationRepository.saveAndFlush(visit);
		// when:
		List<Visit> visits = visitsIoOperation.getVisitsOPD(0);
		// then:
		assertThat(visits).isNotEmpty();
		assertThat(visits.get(visits.size() - 1).getDate()).isCloseTo(visit.getDate(), within(1, ChronoUnit.SECONDS));
	}

	@Test
	void getVisit_shouldReturnNull_whenIdDoesNotExist() throws Exception {
		int id = setupTestVisit(false);
		Visit visit = visitsIoOperationRepository.findById(id).orElse(null);
		assertThat(visit).isNotNull();

		Visit foundVisit = visitsIoOperation.getVisit(visit.getVisitID());
		assertThat(foundVisit).isEqualTo(foundVisit);

		assertThat(visitsIoOperation.getVisit(-99)).isNull();
	}

	@Test
	void countAllActiveAppointments_shouldReturnOne_whenOneVisitIsSaved() throws Exception {
		setupTestVisit(true);
		assertThat(visitsIoOperation.countAllActiveAppointments()).isEqualTo(1);
	}

	@Test
	void updateVisit_shouldSaveNewNote() throws Exception {
		Patient patient = testPatient.setup(false);
		Ward ward = testWard.setup(false);
		patientIoOperationRepository.saveAndFlush(patient);
		wardIoOperationRepository.saveAndFlush(ward);
		Visit visit = testVisit.setup(patient, true, ward);
		int id = visitManager.newVisit(visit).getVisitID();
		Visit foundVisit = visitsIoOperation.findVisit(id);
		foundVisit.setNote("Update");
		Visit result = visitsIoOperation.updateVisit(foundVisit);
		assertThat(result.getNote()).isEqualTo("Update");
		Visit updateVisit = visitsIoOperation.findVisit(id);
		assertThat(updateVisit.getNote()).isEqualTo("Update");
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
