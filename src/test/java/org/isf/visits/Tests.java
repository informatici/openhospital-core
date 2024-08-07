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
package org.isf.visits;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.assertj.core.api.Assertions.within;
import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenNoException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.isf.OHCoreTestCase;
import org.isf.generaldata.GeneralData;
import org.isf.menu.model.User;
import org.isf.patient.TestPatient;
import org.isf.patient.model.Patient;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.sessionaudit.model.UserSession;
import org.isf.sms.model.Sms;
import org.isf.sms.service.SmsOperations;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.isf.utils.time.TimeTools;
import org.isf.visits.manager.VisitManager;
import org.isf.visits.model.Visit;
import org.isf.visits.service.VisitsIoOperationRepository;
import org.isf.visits.service.VisitsIoOperations;
import org.isf.ward.TestWard;
import org.isf.ward.model.Ward;
import org.isf.ward.service.WardIoOperationRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class Tests extends OHCoreTestCase {

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
	@Autowired
	private SmsOperations smsOperations;

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
	void testVisitGets() throws Exception {
		int id = setupTestVisit(false);
		checkVisitIntoDb(id);
	}

	@Test
	void testVisitSets() throws Exception {
		int id = setupTestVisit(true);
		checkVisitIntoDb(id);
	}

	@Test
	void testIoGetVisitShouldReturnVisitWhenPatientCodeProvided() throws Exception {
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
	void testIoGetVisitShouldReturnAllVisitsWhenZeroPatientCodeProvided() throws Exception {
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
	void testIoGetVisitsWardNullWardId() throws Exception {
		int id = setupTestVisit(false);
		Visit visit = visitsIoOperationRepository.findById(id).orElse(null);
		assertThat(visit).isNotNull();
		List<Visit> visits = visitsIoOperation.getVisitsWard(null);
		assertThat(visits.get(visits.size() - 1).getDate()).isCloseTo(visit.getDate(), within(1, ChronoUnit.SECONDS));
	}

	@Test
	void testIoGetVisitsWardWardId() throws Exception {
		int id = setupTestVisit(false);
		Visit visit = visitsIoOperationRepository.findById(id).orElse(null);
		assertThat(visit).isNotNull();
		List<Visit> visits = visitsIoOperation.getVisitsWard(visit.getWard().getCode());
		assertThat(visits.get(visits.size() - 1).getDate()).isCloseTo(visit.getDate(), within(1, ChronoUnit.SECONDS));
	}

	@Test
	void testIoNewVisit() throws Exception {
		Patient patient = testPatient.setup(false);
		Ward ward = testWard.setup(false);
		patientIoOperationRepository.saveAndFlush(patient);
		wardIoOperationRepository.saveAndFlush(ward);
		Visit visit = testVisit.setup(patient, true, ward);
		int id = visitsIoOperation.newVisit(visit).getVisitID();
		checkVisitIntoDb(id);
	}

	@Test
	void testIoFindVisit() throws Exception {
		int id = setupTestVisit(false);
		Visit result = visitsIoOperation.findVisit(id);

		assertThat(result).isNotNull();
		assertThat(result.getVisitID()).isEqualTo(id);
	}

	@Test
	void testIoGetVisitNoOPDWithPatientCode() throws Exception {
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
	void testIoGetVisitNoOPDWithOutPatientCode() throws Exception {
		// given:
		setupTestVisit(false);
		// when:
		List<Visit> visits = visitsIoOperation.getVisitsOPD(0);
		// then:
		assertThat(visits).isEmpty();
	}

	@Test
	void testIoGetVisitSingleOPDWithPatientCode() throws Exception {
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
	void testIoGetVisitSingleOPDWithOutPatientCode() throws Exception {
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
	void testMgrGetVisitNoOPDWithPatientCode() throws Exception {
		// given:
		int id = setupTestVisit(false);
		// when:
		Visit foundVisit = visitsIoOperationRepository.findById(id).orElse(null);
		assertThat(foundVisit).isNotNull();
		List<Visit> visits = visitManager.getVisitsOPD(foundVisit.getPatient().getCode());
		// then:
		assertThat(visits).isEmpty();
	}

	@Test
	void testMgrGetVisitNoOPDWithOutPatientCode() throws Exception {
		// given:
		setupTestVisit(false);
		// when:
		List<Visit> visits = visitManager.getVisitsOPD(0);
		// then:
		assertThat(visits).isEmpty();
	}

	@Test
	void testMgrGetVisitSingleOPDWithPatientCode() throws Exception {
		// given:
		Patient patient = testPatient.setup(false);
		patientIoOperationRepository.saveAndFlush(patient);
		Visit visit = testVisit.setup(patient, true, null);
		visitsIoOperationRepository.saveAndFlush(visit);
		// when:
		List<Visit> visits = visitManager.getVisitsOPD(visit.getPatient().getCode());
		// then:
		assertThat(visits).isNotEmpty();
		assertThat(visits.get(visits.size() - 1).getDate()).isCloseTo(visit.getDate(), within(1, ChronoUnit.SECONDS));
	}

	@Test
	void testMgrGetVisitSingleOPDWithOutPatientCode() throws Exception {
		// given:
		Patient patient = testPatient.setup(false);
		patientIoOperationRepository.saveAndFlush(patient);
		Visit visit = testVisit.setup(patient, true, null);
		visitsIoOperationRepository.saveAndFlush(visit);
		// when:
		List<Visit> visits = visitManager.getVisitsOPD(0);
		// then:
		assertThat(visits).isNotEmpty();
		assertThat(visits.get(visits.size() - 1).getDate()).isCloseTo(visit.getDate(), within(1, ChronoUnit.SECONDS));
	}

	@Test
	void testIoGetVisit() throws Exception {
		int id = setupTestVisit(false);
		Visit visit = visitsIoOperationRepository.findById(id).orElse(null);
		assertThat(visit).isNotNull();

		Visit foundVisit = visitsIoOperation.getVisit(visit.getVisitID());
		assertThat(foundVisit).isEqualTo(foundVisit);

		assertThat(visitsIoOperation.getVisit(-99)).isNull();
	}

	@Test
	void testCountAllActiveAppointments() throws Exception {
		setupTestVisit(true);
		assertThat(visitsIoOperation.countAllActiveAppointments()).isEqualTo(1);
	}

	@Test
	void testMgrGetVisitPatientCode() throws Exception {
		int id = setupTestVisit(false);
		Visit visit = visitsIoOperationRepository.findById(id).orElse(null);
		assertThat(visit).isNotNull();
		List<Visit> visits = visitManager.getVisits(visit.getPatient().getCode());
		assertThat(visits.get(visits.size() - 1).getDate()).isCloseTo(visit.getDate(), within(1, ChronoUnit.SECONDS));
	}

	@Test
	void testMgrGetVisitNoPatientCode() throws Exception {
		int id = setupTestVisit(false);
		Visit visit = visitsIoOperationRepository.findById(id).orElse(null);
		assertThat(visit).isNotNull();
		List<Visit> visits = visitManager.getVisits(0);
		assertThat(visits.get(visits.size() - 1).getDate()).isCloseTo(visit.getDate(), within(1, ChronoUnit.SECONDS));
	}

	@Test
	void testMgrGetVisitsWard() throws Exception {
		int id = setupTestVisit(false);
		Visit visit = visitsIoOperationRepository.findById(id).orElse(null);
		assertThat(visit).isNotNull();
		List<Visit> visits = visitManager.getVisitsWard();
		assertThat(visits.get(visits.size() - 1).getDate()).isCloseTo(visit.getDate(), within(1, ChronoUnit.SECONDS));
	}

	@Test
	void testMgrGetVisitsWardWardId() throws Exception {
		int id = setupTestVisit(false);
		Visit visit = visitsIoOperationRepository.findById(id).orElse(null);
		assertThat(visit).isNotNull();
		List<Visit> visits = visitManager.getVisitsWard(visit.getWard().getCode());
		assertThat(visits.get(visits.size() - 1).getDate()).isCloseTo(visit.getDate(), within(1, ChronoUnit.SECONDS));
	}

	@Test
	void testMgrGetVisitsEmpty() throws Exception {
		assertThat(visitManager.getVisits(0)).isEmpty();
	}

	@Test
	void testMgrNewVisit() throws Exception {
		Patient patient = testPatient.setup(false);
		Ward ward = testWard.setup(false);
		patientIoOperationRepository.saveAndFlush(patient);
		wardIoOperationRepository.saveAndFlush(ward);
		Visit visit = testVisit.setup(patient, true, ward);
		int id = visitManager.newVisit(visit).getVisitID();
		checkVisitIntoDb(id);
	}

	@Test
	void testMgrUpdateVisit() throws Exception {
		Patient patient = testPatient.setup(false);
		Ward ward = testWard.setup(false);
		patientIoOperationRepository.saveAndFlush(patient);
		wardIoOperationRepository.saveAndFlush(ward);
		Visit visit = testVisit.setup(patient, true, ward);
		int id = visitManager.newVisit(visit).getVisitID();
		Visit foundVisit = visitsIoOperation.findVisit(id);
		foundVisit.setNote("Update");
		Visit result = visitManager.updateVisit(foundVisit);
		assertThat(result.getNote()).isEqualTo("Update");
		Visit updateVisit = visitsIoOperation.findVisit(id);
		assertThat(updateVisit.getNote()).isEqualTo("Update");
	}

	@Test
	void testIoUpdateVisit() throws Exception {
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

	@Test
	void testMgrNewVisitsEmptyList() throws Exception {
		assertThat(visitManager.newVisits(new ArrayList<>())).isTrue();
	}

	@Test
	void testMgrNewVisitsSMSFalse() throws Exception {
		List<Visit> visits = new ArrayList<>();
		int id = setupTestVisit(false);
		Visit visit = visitsIoOperationRepository.findById(id).orElse(null);
		assertThat(visit).isNotNull();
		visit.setSms(false);
		visits.add(visit);
		assertThat(visitManager.newVisits(visits)).isTrue();
	}

	@Test
	void testMgrNewVisitsSMSTrueDatePassed() throws Exception {
		List<Visit> visits = new ArrayList<>();
		int id = setupTestVisit(false);
		Visit visit = visitsIoOperationRepository.findById(id).orElse(null);
		assertThat(visit).isNotNull();
		visits.add(visit);
		assertThat(visitManager.newVisits(visits)).isTrue();
	}

	@Test
	void testMgrNewVisitsSMSTrueDateFuture() throws Exception {
		GeneralData.PATIENTPHOTOSTORAGE = "DB";
		List<Visit> visits = new ArrayList<>();
		int id = setupTestVisit(false);
		LocalDateTime date = TimeTools.getNow();
		Visit visit = visitsIoOperationRepository.findById(id).orElse(null);
		assertThat(visit).isNotNull();
		visit.setDate(date.plusMonths(1));
		visits.add(visit);
		assertThat(visitManager.newVisits(visits)).isTrue();

		List<Sms> sms = smsOperations.getAll(date, date.plusMonths(1));
		assertThat(sms).hasSize(1);
		LocalDateTime scheduledDate = visit.getDate().minusDays(1);
		assertThat(sms.get(0).getSmsDateSched()).isEqualTo(scheduledDate);
	}

	@Test
	void testMgrNewVisitsSMSTrueMessageTooLong() throws Exception {
		GeneralData.PATIENTPHOTOSTORAGE = "DB";
		List<Visit> visits = new ArrayList<>();
		int id = setupTestVisit(false);
		Visit visit = visitsIoOperationRepository.findById(id).orElse(null);
		assertThat(visit).isNotNull();
		String longText = "This is a very long note that should cause the SMS code to truncate the entire message to 160 characters.";
		visit.setNote(longText + ' ' + longText);
		visit.setDate(TimeTools.getNow().plusMonths(1));
		visits.add(visit);
		assertThat(visitManager.newVisits(visits)).isTrue();

		LocalDateTime dateFrom = TimeTools.getNow();
		List<Sms> sms = smsOperations.getAll(dateFrom, dateFrom.plusMonths(1));
		assertThat(sms).hasSize(1);
	}

	@Test
	void testMgrDeleteVisit() throws Exception {
		int id = setupTestVisit(false);
		Visit foundVisit = visitManager.findVisit(id);
		assertThat(visitManager.deleteAllVisits(foundVisit.getPatient().getCode())).isTrue();
		assertThat(visitsIoOperation.isCodePresent(id)).isFalse();
	}

	@Test
	void testMgrFindVisit() throws Exception {
		int id = setupTestVisit(false);
		Visit result = visitManager.findVisit(id);
		assertThat(result).isNotNull();
		assertThat(result.getVisitID()).isEqualTo(id);
	}

	@Test
	void testMgrValidateNoPatientOverlappingVisitsInDifferentWards() throws Exception {
		// test data common setup
		Patient patient = testPatient.setup(false);
		patientIoOperationRepository.saveAndFlush(patient);
		Ward ward1 = testWard.setup(false);
		ward1.setCode("ward1");
		wardIoOperationRepository.saveAndFlush(ward1);
		// existing visit
		LocalDateTime existingVisitDateTime = LocalDateTime.of(2023, 3, 15, 14, 30, 0);
		int existingVisitDuration = 60;
		Visit existingVisit = testVisit.setup(patient, true, ward1);
		existingVisit.setDate(existingVisitDateTime);
		existingVisit.setDuration(existingVisitDuration);
		visitsIoOperationRepository.saveAndFlush(existingVisit);
		// different ward for the other visits
		Ward ward2 = testWard.setup(false);
		ward2.setCode("ward2");

		// test param: visit datetime, visit duration (will be better as a JUnit 5 parameterized test)
		List<Pair<LocalDateTime, Integer>> invalidVisitsParams = List.of(
				Pair.of(existingVisitDateTime, existingVisitDuration), // exactly same visit time
				Pair.of(existingVisitDateTime.plusMinutes(existingVisitDuration - 1), existingVisitDuration), // overlaps the end
				Pair.of(existingVisitDateTime.minusMinutes(existingVisitDuration - 1), existingVisitDuration), // overlaps the start
				Pair.of(existingVisitDateTime.plusMinutes(existingVisitDuration / 2), existingVisitDuration / 4), // inside existing visit time
				Pair.of(existingVisitDateTime.minusMinutes(5), existingVisitDuration + 10) // encompassing existing visit time
		);
		for (Pair<LocalDateTime, Integer> invalidVisitParams : invalidVisitsParams) {
			// given
			Visit invalidVisit = testVisit.setup(patient, true, ward2); // different ward from existingVisit
			invalidVisit.setDate(invalidVisitParams.getLeft());
			invalidVisit.setDuration(invalidVisitParams.getRight());
			// when
			OHDataValidationException ohDataValidationException = catchThrowableOfType(() -> visitManager.validateVisit(invalidVisit),
					OHDataValidationException.class);
			// then
			then(ohDataValidationException)
					.as("should have detected an invalid visit at %s lasting %s minutes", invalidVisit.getDate(), invalidVisit.getDuration())
					.isNotNull();
			then(ohDataValidationException.getMessages()).hasSize(1);
			OHExceptionMessage ohExceptionMessage = ohDataValidationException.getMessages().get(0);
			then(ohExceptionMessage.getTitle()).isEqualTo("angal.common.error.title");
			then(ohExceptionMessage.getMessage()).isEqualTo("angal.visit.overlappingpatientvisitsindifferentwards.msg");
			then(ohExceptionMessage.getLevel()).isEqualTo(OHSeverityLevel.ERROR);
		}
	}

	@Test
	void testMgrValidateNoPatientMultipleOverlappingVisitsInDifferentWards() throws Exception {
		// given
		Patient patient = testPatient.setup(false);
		patientIoOperationRepository.saveAndFlush(patient);
		// first existing visit: 14h00 -> 14h45 in ward1
		Ward ward1 = testWard.setup(false);
		ward1.setCode("ward1");
		wardIoOperationRepository.saveAndFlush(ward1);
		Visit existingVisit1 = testVisit.setup(patient, true, ward1);
		existingVisit1.setDate(LocalDateTime.of(2023, 3, 30, 14, 0, 0));
		existingVisit1.setDuration(45);
		visitsIoOperationRepository.saveAndFlush(existingVisit1);
		// second existing visit: 15h00 -> 15h30 in ward2
		Ward ward2 = testWard.setup(false);
		ward2.setCode("ward2");
		wardIoOperationRepository.saveAndFlush(ward2);
		Visit existingVisit2 = testVisit.setup(patient, true, ward2);
		existingVisit2.setDate(LocalDateTime.of(2023, 3, 30, 15, 0, 0));
		existingVisit2.setDuration(30);
		visitsIoOperationRepository.saveAndFlush(existingVisit2);
		// new visit: 14h15 -> 15h15 in ward3, overlaps visit 1 and visit 2
		Ward ward3 = testWard.setup(false);
		ward3.setCode("ward3");
		Visit invalidVisit = testVisit.setup(patient, true, ward3);
		invalidVisit.setDate(LocalDateTime.of(2023, 3, 30, 14, 15, 0));
		invalidVisit.setDuration(60);

		// when
		OHDataValidationException ohDataValidationException = catchThrowableOfType(() -> visitManager.validateVisit(invalidVisit),
				OHDataValidationException.class);

		// then
		then(ohDataValidationException)
				.as("should have detected an invalid visit %s lasting %s minutes", invalidVisit, invalidVisit.getDuration())
				.isNotNull();
		then(ohDataValidationException.getMessages()).hasSize(1);
		OHExceptionMessage ohExceptionMessage = ohDataValidationException.getMessages().get(0);
		then(ohExceptionMessage.getTitle()).isEqualTo("angal.common.error.title");
		then(ohExceptionMessage.getMessage()).isEqualTo("angal.visit.manyoverlappingpatientvisitsindifferentwards.msg");
		then(ohExceptionMessage.getLevel()).isEqualTo(OHSeverityLevel.ERROR);
	}

	@Test
	void testMgrValidateVisitNotOverlappingAnyVisitsInSameWard() throws Exception {
		// setup two patients
		Patient patient1 = testPatient.setup(false);
		patient1.setName("patient1");
		patientIoOperationRepository.saveAndFlush(patient1);
		Patient patient2 = testPatient.setup(false);
		patient2.setName("patient2");
		patientIoOperationRepository.saveAndFlush(patient2);
		Ward ward = testWard.setup(false);
		ward.setCode("ward");
		wardIoOperationRepository.saveAndFlush(ward);
		// existing visit
		LocalDateTime existingVisitDateTime = LocalDateTime.of(2023, 3, 15, 14, 30, 0);
		int existingVisitDuration = 60;
		Visit existingVisit = testVisit.setup(patient1, true, ward);
		existingVisit.setDate(existingVisitDateTime);
		existingVisit.setDuration(existingVisitDuration);
		visitsIoOperationRepository.saveAndFlush(existingVisit);

		// test param: visit datetime, visit duration (will be better as a JUnit 5 parameterized test)
		List<Pair<LocalDateTime, Integer>> invalidVisitsParams = List.of(
				Pair.of(existingVisitDateTime, existingVisitDuration), // exactly same visit time
				Pair.of(existingVisitDateTime.plusMinutes(existingVisitDuration - 1), existingVisitDuration), // overlaps the end
				Pair.of(existingVisitDateTime.minusMinutes(existingVisitDuration - 1), existingVisitDuration), // overlaps the start
				Pair.of(existingVisitDateTime.plusMinutes(existingVisitDuration / 2), existingVisitDuration / 4), // inside existing visit time
				Pair.of(existingVisitDateTime.minusMinutes(5), existingVisitDuration + 10) // encompassing existing visit time
		);
		for (Pair<LocalDateTime, Integer> invalidVisitParams : invalidVisitsParams) {
			// given
			Visit invalidVisit = testVisit.setup(patient2, true, ward);
			invalidVisit.setDate(invalidVisitParams.getLeft());
			invalidVisit.setDuration(invalidVisitParams.getRight());
			// when
			OHDataValidationException ohDataValidationException = catchThrowableOfType(() -> visitManager.validateVisit(invalidVisit),
					OHDataValidationException.class);
			// then
			then(ohDataValidationException)
					.as("should have detected an invalid visit at %s lasting %s minutes", invalidVisit.getDate(), invalidVisit.getDuration())
					.isNotNull();
			then(ohDataValidationException.getMessages()).hasSize(1);
			OHExceptionMessage ohExceptionMessage = ohDataValidationException.getMessages().get(0);
			then(ohExceptionMessage.getTitle()).isEqualTo("angal.common.error.title");
			then(ohExceptionMessage.getMessage()).isEqualTo("angal.visit.overlappingvisitinward.msg");
			then(ohExceptionMessage.getLevel()).isEqualTo(OHSeverityLevel.ERROR);
		}
	}

	@Test
	void testMgrValidateVisitNotOverlappingManyVisitsInSameWard() throws Exception {
		Ward ward = testWard.setup(false);
		ward.setCode("ward");
		wardIoOperationRepository.saveAndFlush(ward);
		// patient1 visit: 14h30 -> 15h15
		Patient patient1 = testPatient.setup(false);
		patient1.setName("patient1");
		patientIoOperationRepository.saveAndFlush(patient1);
		Visit patient1Visit = testVisit.setup(patient1, true, ward);
		patient1Visit.setDate(LocalDateTime.of(2023, 3, 28, 14, 30, 0));
		patient1Visit.setDuration(45);
		visitsIoOperationRepository.saveAndFlush(patient1Visit);
		// patient2 visit: 15h30 -> 16h30
		Patient patient2 = testPatient.setup(false);
		patient2.setName("patient2");
		patientIoOperationRepository.saveAndFlush(patient2);
		Visit patient2Visit = testVisit.setup(patient2, true, ward);
		patient2Visit.setDate(LocalDateTime.of(2023, 3, 28, 15, 30, 0));
		patient2Visit.setDuration(60);
		visitsIoOperationRepository.saveAndFlush(patient2Visit);

		// given patient3 visit 15h00 -> 16h00 overlapping both patient1 and patient2 visits
		Patient patient3 = testPatient.setup(false);
		patient3.setName("patient3");
		patientIoOperationRepository.saveAndFlush(patient3);
		Visit invalidVisit = testVisit.setup(patient3, true, ward);
		invalidVisit.setDate(LocalDateTime.of(2023, 3, 28, 15, 0, 0));
		invalidVisit.setDuration(60);

		// when
		OHDataValidationException ohDataValidationException = catchThrowableOfType(() -> visitManager.validateVisit(invalidVisit),
				OHDataValidationException.class);

		// then
		then(ohDataValidationException)
				.as("should have detected an invalid visit at %s lasting %s minutes", invalidVisit.getDate(), invalidVisit.getDuration())
				.isNotNull();
		then(ohDataValidationException.getMessages()).hasSize(1);
		OHExceptionMessage ohExceptionMessage = ohDataValidationException.getMessages().get(0);
		then(ohExceptionMessage.getTitle()).isEqualTo("angal.common.error.title");
		then(ohExceptionMessage.getMessage()).isEqualTo("angal.visit.overlappingmanyvisitsinward.msg");
		then(ohExceptionMessage.getLevel()).isEqualTo(OHSeverityLevel.ERROR);
	}

	@Test
	void testMgrValidateShouldAcceptValidVisits() throws Exception {
		// test data common setup
		LocalDateTime existingVisitDateTime = LocalDateTime.of(2023, 3, 15, 14, 30, 0);
		int existingVisitDuration = 60;
		Patient patient = testPatient.setup(false);
		patientIoOperationRepository.saveAndFlush(patient);
		// existing visit
		Ward ward1 = testWard.setup(false);
		ward1.setCode("ward1");
		wardIoOperationRepository.saveAndFlush(ward1);
		Visit existingVisit = testVisit.setup(patient, true, ward1);
		existingVisit.setDate(existingVisitDateTime);
		existingVisit.setDuration(existingVisitDuration);
		visitsIoOperationRepository.saveAndFlush(existingVisit);
		Ward ward2 = testWard.setup(false);
		ward2.setCode("ward2");

		// test param: visit datetime, visit duration
		List<Pair<LocalDateTime, Integer>> validVisitsParams = List.of(
				Pair.of(existingVisitDateTime.plusMinutes(existingVisitDuration), 20), // starts after existing visit time
				Pair.of(existingVisitDateTime.minusMinutes(existingVisitDuration + 30), 20) // ends before existing visit time
		);
		for (Pair<LocalDateTime, Integer> validVisitParams : validVisitsParams) {
			// given
			Visit validVisit = testVisit.setup(patient, true, ward2);
			validVisit.setDate(validVisitParams.getLeft());
			validVisit.setDuration(validVisitParams.getRight());
			// then
			thenNoException().isThrownBy(() -> visitManager.validateVisit(validVisit));
		}
	}

	@Test
	void testMgrValidateVisitDurationIsPositive() throws OHException {
		// given
		Patient patient = testPatient.setup(false);
		Ward ward = testWard.setup(false);
		patientIoOperationRepository.saveAndFlush(patient);
		wardIoOperationRepository.saveAndFlush(ward);
		Visit invalidVisit = testVisit.setup(patient, true, ward);
		invalidVisit.setDuration(0);
		// when
		OHDataValidationException ohDataValidationException = catchThrowableOfType(() -> visitManager.validateVisit(invalidVisit),
				OHDataValidationException.class);
		// then
		then(ohDataValidationException).as("should have detected an invalid visit").isNotNull();
		then(ohDataValidationException.getMessages()).hasSize(1);
		OHExceptionMessage ohExceptionMessage = ohDataValidationException.getMessages().get(0);
		then(ohExceptionMessage.getTitle()).isEqualTo("angal.common.error.title");
		then(ohExceptionMessage.getMessage()).isEqualTo("angal.visit.invalidvisitduration.msg");
		then(ohExceptionMessage.getLevel()).isEqualTo(OHSeverityLevel.ERROR);
	}

	@Test
	void testMgrValidateVisitMissingDate() throws Exception {
		// given
		Patient patient = testPatient.setup(false);
		Ward ward = testWard.setup(false);
		patientIoOperationRepository.saveAndFlush(patient);
		wardIoOperationRepository.saveAndFlush(ward);
		Visit invalidVisit = testVisit.setup(patient, true, ward);
		invalidVisit.setDate(null);
		// when
		OHDataValidationException ohDataValidationException = catchThrowableOfType(() -> visitManager.validateVisit(invalidVisit),
						OHDataValidationException.class);
		// then
		then(ohDataValidationException).as("should have detected an invalid visit").isNotNull();
		then(ohDataValidationException.getMessages()).hasSize(1);
		OHExceptionMessage ohExceptionMessage = ohDataValidationException.getMessages().get(0);
		then(ohExceptionMessage.getTitle()).isEqualTo("angal.common.error.title");
		then(ohExceptionMessage.getMessage()).isEqualTo("angal.visit.pleasechooseadate.msg");
		then(ohExceptionMessage.getLevel()).isEqualTo(OHSeverityLevel.ERROR);
	}

	@Test
	void testMgrValidateVisitMissingWard() throws Exception {
		// given
		Patient patient = testPatient.setup(false);
		Ward ward = testWard.setup(false);
		patientIoOperationRepository.saveAndFlush(patient);
		wardIoOperationRepository.saveAndFlush(ward);
		Visit invalidVisit = testVisit.setup(patient, true, ward);
		invalidVisit.setWard(null);
		// when
		OHDataValidationException ohDataValidationException = catchThrowableOfType(() -> visitManager.validateVisit(invalidVisit),
						OHDataValidationException.class);
		// then
		then(ohDataValidationException).as("should have detected an invalid visit").isNotNull();
		then(ohDataValidationException.getMessages()).hasSize(1);
		OHExceptionMessage ohExceptionMessage = ohDataValidationException.getMessages().get(0);
		then(ohExceptionMessage.getTitle()).isEqualTo("angal.common.error.title");
		then(ohExceptionMessage.getMessage()).isEqualTo("angal.visit.pleasechooseaward.msg");
		then(ohExceptionMessage.getLevel()).isEqualTo(OHSeverityLevel.ERROR);
	}

	@Test
	void testMgrValidateVisitMissingPatient() throws Exception {
		// given
		Patient patient = testPatient.setup(false);
		Ward ward = testWard.setup(false);
		patientIoOperationRepository.saveAndFlush(patient);
		wardIoOperationRepository.saveAndFlush(ward);
		Visit invalidVisit = testVisit.setup(patient, true, ward);
		invalidVisit.setPatient(null);
		// when
		OHDataValidationException ohDataValidationException = catchThrowableOfType(() -> visitManager.validateVisit(invalidVisit),
						OHDataValidationException.class);
		// then
		then(ohDataValidationException).as("should have detected an invalid visit").isNotNull();
		then(ohDataValidationException.getMessages()).hasSize(1);
		OHExceptionMessage ohExceptionMessage = ohDataValidationException.getMessages().get(0);
		then(ohExceptionMessage.getTitle()).isEqualTo("angal.common.error.title");
		then(ohExceptionMessage.getMessage()).isEqualTo("angal.visit.pleasechooseapatient.msg");
		then(ohExceptionMessage.getLevel()).isEqualTo(OHSeverityLevel.ERROR);
	}

	@Test
	void testMgrValidateVisitSexAndWardDoNotAgree() throws Exception {
		// given
		Patient patient = testPatient.setup(false);
		patient.setSex('M');
		Ward ward = testWard.setup(false);
		ward.setMale(false);
		patientIoOperationRepository.saveAndFlush(patient);
		wardIoOperationRepository.saveAndFlush(ward);
		Visit invalidVisit = testVisit.setup(patient, true, ward);
		// when
		OHDataValidationException ohDataValidationException = catchThrowableOfType(() -> visitManager.validateVisit(invalidVisit),
						OHDataValidationException.class);
		// then
		then(ohDataValidationException).as("should have detected an invalid visit").isNotNull();
		then(ohDataValidationException.getMessages()).hasSize(1);
		OHExceptionMessage ohExceptionMessage = ohDataValidationException.getMessages().get(0);
		then(ohExceptionMessage.getTitle()).isEqualTo("angal.common.error.title");
		then(ohExceptionMessage.getMessage()).isEqualTo("angal.visit.thepatientssexandwarddonotagree.msg");
		then(ohExceptionMessage.getLevel()).isEqualTo(OHSeverityLevel.ERROR);
	}

	@Test
	void testVisitGetEnd() {
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
	void testVisitGetSet() throws Exception {
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
	void testVisitToStringSMS() throws Exception {
		int id = setupTestVisit(false);
		Visit visit = visitManager.findVisit(id);
		assertThat(visit.toStringSMS()).isEqualTo("08/09/10 00:00");
	}

	@Test
	void testVisitToString() throws Exception {
		int id = setupTestVisit(false);
		Visit visit = visitManager.findVisit(id);
		assertThat(visit).hasToString("TestDescription - testService - 08/09/10 - 00:00:00");
	}

	@Test
	void testVisitToStringNullWard() throws Exception {
		int id = setupTestVisit(false);
		Visit visit = visitManager.findVisit(id);
		visit.setWard(null);
		// NOTE: resource bundle key value included here
		assertThat(visit).hasToString("angal.menu.opd - testService - 08/09/10 - 00:00:00");
	}

	@Test
	void testVisitToStringNullService() throws Exception {
		int id = setupTestVisit(false);
		Visit visit = visitManager.findVisit(id);
		visit.setService(null);
		assertThat(visit).hasToString("TestDescription - 08/09/10 - 00:00:00");
	}

	@Test
	void testVisitFormatDateTime() throws Exception {
		int id = setupTestVisit(false);
		Visit visit = visitManager.findVisit(id);
		assertThat(visit.formatDateTime(visit.getDate())).isEqualTo("08/09/10 - 00:00:00");
	}

	@Test
	void testVisitFormatDateTimeSMS() throws Exception {
		int id = setupTestVisit(false);
		Visit visit = visitManager.findVisit(id);
		assertThat(visit.formatDateTimeSMS(visit.getDate())).isEqualTo("08/09/10 00:00");
	}

	@Test
	void testVisitEquals() throws Exception {
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
	void testVisitHashCode() throws Exception {
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
