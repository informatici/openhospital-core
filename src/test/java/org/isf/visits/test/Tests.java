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
package org.isf.visits.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.generaldata.GeneralData;
import org.isf.patient.model.Patient;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.patient.test.TestPatient;
import org.isf.sms.model.Sms;
import org.isf.sms.service.SmsOperations;
import org.isf.utils.exception.OHException;
import org.isf.utils.time.TimeTools;
import org.isf.visits.manager.VisitManager;
import org.isf.visits.model.Visit;
import org.isf.visits.service.VisitsIoOperationRepository;
import org.isf.visits.service.VisitsIoOperations;
import org.isf.ward.model.Ward;
import org.isf.ward.service.WardIoOperationRepository;
import org.isf.ward.test.TestWard;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class Tests extends OHCoreTestCase {

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

	@BeforeClass
	public static void setUpClass() {
		testVisit = new TestVisit();
		testPatient = new TestPatient();
		testWard = new TestWard();
	}

	@Before
	public void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	public void testVisitGets() throws Exception {
		int id = setupTestVisit(false);
		checkVisitIntoDb(id);
	}

	@Test
	public void testVisitSets() throws Exception {
		int id = setupTestVisit(true);
		checkVisitIntoDb(id);
	}

	@Test
	public void testIoGetVisitShouldReturnVisitWhenPatientCodeProvided() throws Exception {
		// given:
		int id = setupTestVisit(false);
		// when:
		Visit foundVisit = visitsIoOperationRepository.findById(id).get();
		List<Visit> visits = visitsIoOperation.getVisits(foundVisit.getPatient().getCode());
		// then:
		assertThat(visits.get(visits.size() - 1).getDate()).isCloseTo(foundVisit.getDate(), within(1, ChronoUnit.SECONDS));
	}

	@Test
	public void testIoGetVisitShouldReturnAllVisitsWhenZeroPatientCodeProvided() throws Exception {
		// given:
		int id = setupTestVisit(false);
		Visit foundVisit = visitsIoOperationRepository.findById(id).get();

		// when:
		List<Visit> visits = visitsIoOperation.getVisits(0);

		// then:
		assertThat(visits.get(visits.size() - 1).getDate()).isCloseTo(foundVisit.getDate(), within(1, ChronoUnit.SECONDS));
	}

	@Test
	public void testIoGetVisitsWardNullWardId() throws Exception {
		int id = setupTestVisit(false);
		Visit visit = visitsIoOperationRepository.findById(id).get();
		List<Visit> visits = visitsIoOperation.getVisitsWard(null);
		assertThat(visits.get(visits.size() - 1).getDate()).isCloseTo(visit.getDate(), within(1, ChronoUnit.SECONDS));
	}

	@Test
	public void testIoGetVisitsWardWardId() throws Exception {
		int id = setupTestVisit(false);
		Visit visit = visitsIoOperationRepository.findById(id).get();
		List<Visit> visits = visitsIoOperation.getVisitsWard(visit.getWard().getCode());
		assertThat(visits.get(visits.size() - 1).getDate()).isCloseTo(visit.getDate(), within(1, ChronoUnit.SECONDS));
	}

	@Test
	public void testIoNewVisit() throws Exception {
		Patient patient = testPatient.setup(false);
		Ward ward = testWard.setup(false);
		patientIoOperationRepository.saveAndFlush(patient);
		wardIoOperationRepository.saveAndFlush(ward);
		Visit visit = testVisit.setup(patient, true, ward);
		int id = visitsIoOperation.newVisit(visit).getVisitID();
		checkVisitIntoDb(id);
	}

	@Test
	public void testIoDeleteVisit() throws Exception {
		int id = setupTestVisit(false);
		Visit foundVisit = visitsIoOperation.findVisit(id);
		boolean result = visitsIoOperation.deleteAllVisits(foundVisit.getPatient().getCode());

		assertThat(result).isTrue();
		result = visitsIoOperation.isCodePresent(id);
		assertThat(result).isFalse();
	}

	@Test
	public void testIoFindVisit() throws Exception {
		int id = setupTestVisit(false);
		Visit result = visitsIoOperation.findVisit(id);

		assertThat(result).isNotNull();
		assertThat(result.getVisitID()).isEqualTo(id);
	}

	@Test
	public void testMgrGetVisitPatientCode() throws Exception {
		int id = setupTestVisit(false);
		Visit visit = visitsIoOperationRepository.findById(id).get();
		List<Visit> visits = visitManager.getVisits(visit.getPatient().getCode());
		assertThat(visits.get(visits.size() - 1).getDate()).isCloseTo(visit.getDate(), within(1, ChronoUnit.SECONDS));
	}

	@Test
	public void testMgrGetVisitNoPatientCode() throws Exception {
		int id = setupTestVisit(false);
		Visit visit = visitsIoOperationRepository.findById(id).get();
		List<Visit> visits = visitManager.getVisits(0);
		assertThat(visits.get(visits.size() - 1).getDate()).isCloseTo(visit.getDate(), within(1, ChronoUnit.SECONDS));
	}

	@Test
	public void testMgrGetVisitsWard() throws Exception {
		int id = setupTestVisit(false);
		Visit visit = visitsIoOperationRepository.findById(id).get();
		List<Visit> visits = visitManager.getVisitsWard();
		assertThat(visits.get(visits.size() - 1).getDate()).isCloseTo(visit.getDate(), within(1, ChronoUnit.SECONDS));
	}

	@Test
	public void testMgrGetVisitsWardWardId() throws Exception {
		int id = setupTestVisit(false);
		Visit visit = visitsIoOperationRepository.findById(id).get();
		List<Visit> visits = visitManager.getVisitsWard(visit.getWard().getCode());
		assertThat(visits.get(visits.size() - 1).getDate()).isCloseTo(visit.getDate(), within(1, ChronoUnit.SECONDS));
	}

	@Test
	public void testMgrGetVisitsEmpty() throws Exception {
		assertThat(visitManager.getVisits(0)).isEmpty();
	}

	@Test
	public void testMgrNewVisit() throws Exception {
		Patient patient = testPatient.setup(false);
		Ward ward = testWard.setup(false);
		patientIoOperationRepository.saveAndFlush(patient);
		wardIoOperationRepository.saveAndFlush(ward);
		Visit visit = testVisit.setup(patient, true, ward);
		int id = visitManager.newVisit(visit).getVisitID();
		checkVisitIntoDb(id);
	}
	
	@Test
	public void testMgrUpdateVisit() throws Exception {
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
	public void testMgrNewVisitsEmptyList() throws Exception {
		assertThat(visitManager.newVisits(new ArrayList<>())).isTrue();
	}

	@Test
	public void testMgrNewVisitsSMSFalse() throws Exception {
		List<Visit> visits = new ArrayList<>();
		int id = setupTestVisit(false);
		Visit visit = visitsIoOperationRepository.findById(id).get();
		visit.setSms(false);
		visits.add(visit);
		assertThat(visitManager.newVisits(visits)).isTrue();
	}

	@Test
	public void testMgrNewVisitsSMSTrueDatePassed() throws Exception {
		List<Visit> visits = new ArrayList<>();
		int id = setupTestVisit(false);
		Visit visit = visitsIoOperationRepository.findById(id).get();
		visits.add(visit);
		assertThat(visitManager.newVisits(visits)).isTrue();
	}

	@Test
	public void testMgrNewVisitsSMSTrueDateFuture() throws Exception {
		GeneralData.PATIENTPHOTOSTORAGE = "DB";
		List<Visit> visits = new ArrayList<>();
		int id = setupTestVisit(false);
		LocalDateTime date = TimeTools.getNow();
		Visit visit = visitsIoOperationRepository.findById(id).get();
		visit.setDate(date.plusMonths(1));
		visits.add(visit);
		assertThat(visitManager.newVisits(visits)).isTrue();

		List<Sms> sms = smsOperations.getAll(date, date.plusMonths(1));
		assertThat(sms).hasSize(1);
		LocalDateTime scheduledDate = visit.getDate().minusDays(1);
		assertThat(sms.get(0).getSmsDateSched()).isEqualTo(scheduledDate);
	}

	@Test
	public void testMgrNewVisitsSMSTrueMessageTooLong() throws Exception {
		GeneralData.PATIENTPHOTOSTORAGE = "DB";
		List<Visit> visits = new ArrayList<>();
		int id = setupTestVisit(false);
		Visit visit = visitsIoOperationRepository.findById(id).get();
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
	public void testMgrDeleteVisit() throws Exception {
		int id = setupTestVisit(false);
		Visit foundVisit = visitManager.findVisit(id);
		assertThat(visitManager.deleteAllVisits(foundVisit.getPatient().getCode())).isTrue();
		assertThat(visitsIoOperation.isCodePresent(id)).isFalse();
	}

	@Test
	public void testMgrFindVisit() throws Exception {
		int id = setupTestVisit(false);
		Visit result = visitManager.findVisit(id);
		assertThat(result).isNotNull();
		assertThat(result.getVisitID()).isEqualTo(id);
	}

	@Test
	public void testVisitGetSet() throws Exception {
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
	public void testVisitToStringSMS() throws Exception {
		int id = setupTestVisit(false);
		Visit visit = visitManager.findVisit(id);
		assertThat(visit.toStringSMS()).isEqualTo("08/09/10 00:00");
	}

	@Test
	public void testVisitToString() throws Exception {
		int id = setupTestVisit(false);
		Visit visit = visitManager.findVisit(id);
		assertThat(visit).hasToString("TestDescription - testService - 08/09/10 - 00:00:00");
	}

	@Test
	public void testVisitFormatDateTime() throws Exception {
		int id = setupTestVisit(false);
		Visit visit = visitManager.findVisit(id);
		assertThat(visit.formatDateTime(visit.getDate())).isEqualTo("08/09/10 - 00:00:00");
	}

	@Test
	public void testVisitFormatDateTimeSMS() throws Exception {
		int id = setupTestVisit(false);
		Visit visit = visitManager.findVisit(id);
		assertThat(visit.formatDateTimeSMS(visit.getDate())).isEqualTo("08/09/10 00:00");
	}

	@Test
	public void testVisitEquals() throws Exception {
		int id = setupTestVisit(false);
		Visit visit = visitManager.findVisit(id);

		assertThat(visit.equals(visit)).isTrue();
		assertThat(visit)
				.isNotNull()
				.isNotEqualTo("someString");

		Visit visit2 = new Visit(-1, null, null, null, false, null, null, null);
		assertThat(visit).isNotEqualTo(visit2);

		visit2.setVisitID(visit.getVisitID());
		assertThat(visit).isEqualTo(visit2);
	}

	@Test
	public void testVisitHashCode() throws Exception {
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

	private void checkVisitIntoDb(int id) throws OHException {
		Visit foundVisit = visitsIoOperation.findVisit(id);
		testVisit.check(foundVisit);
	}
}
