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
package org.isf.opd.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.assertj.core.api.Condition;
import org.isf.OHCoreTestCase;
import org.isf.disease.model.Disease;
import org.isf.disease.service.DiseaseIoOperationRepository;
import org.isf.disease.test.TestDisease;
import org.isf.distype.model.DiseaseType;
import org.isf.distype.service.DiseaseTypeIoOperationRepository;
import org.isf.distype.test.TestDiseaseType;
import org.isf.generaldata.GeneralData;
import org.isf.opd.manager.OpdBrowserManager;
import org.isf.opd.model.Opd;
import org.isf.opd.service.OpdIoOperationRepository;
import org.isf.opd.service.OpdIoOperations;
import org.isf.patient.model.Patient;
import org.isf.patient.model.PatientMergedEvent;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.patient.test.TestPatient;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.time.TimeTools;
import org.isf.visits.model.Visit;
import org.isf.visits.service.VisitsIoOperationRepository;
import org.isf.visits.test.TestVisit;
import org.isf.ward.model.Ward;
import org.isf.ward.service.WardIoOperationRepository;
import org.isf.ward.test.TestWard;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

@RunWith(Parameterized.class)
public class Tests extends OHCoreTestCase {

	@ClassRule
	public static final SpringClassRule springClassRule = new SpringClassRule();

	@Rule
	public final SpringMethodRule springMethodRule = new SpringMethodRule();

	private static TestOpd testOpd;
	private static TestPatient testPatient;
	private static TestDiseaseType testDiseaseType;
	private static TestDisease testDisease;
	private static TestWard testWard;
	private static TestVisit testVisit;

	@Autowired
	OpdIoOperations opdIoOperation;
	@Autowired
	OpdIoOperationRepository opdIoOperationRepository;
	@Autowired
	PatientIoOperationRepository patientIoOperationRepository;
	@Autowired
	WardIoOperationRepository wardIoOperationRepository;
	@Autowired
	VisitsIoOperationRepository visitsIoOperationRepository;
	@Autowired
	OpdBrowserManager opdBrowserManager;
	@Autowired
	DiseaseTypeIoOperationRepository diseaseTypeIoOperationRepository;
	@Autowired
	DiseaseIoOperationRepository diseaseIoOperationRepository;
	@Autowired
	ApplicationEventPublisher applicationEventPublisher;

	public Tests(boolean opdExtended) {
		GeneralData.OPDEXTENDED = opdExtended;
	}

	@BeforeClass
	public static void setUpClass() {
		testOpd = new TestOpd();
		testPatient = new TestPatient();
		testDisease = new TestDisease();
		testDiseaseType = new TestDiseaseType();
		testWard = new TestWard();
		testVisit = new TestVisit();
	}

	@Before
	public void setUp() {
		cleanH2InMemoryDb();
	}

	@Parameterized.Parameters(name = "Test with OPDEXTENDED={0}")
	public static Collection<Object[]> opdExtended() {
		return Arrays.asList(new Object[][] {
				{ false },
				{ true }
		});
	}
	
	@Test
	public void testOpdGets() throws Exception {
		int code = setupTestOpd(false);
		checkOpdIntoDb(code);
	}

	@Test
	public void testOpdSets() throws Exception {
		int code = setupTestOpd(true);
		checkOpdIntoDb(code);
	}

	@Test
	public void testIoGetOpdList() throws Exception {
		int code = setupTestOpd(false);
		Opd foundOpd = opdIoOperationRepository.findById(code).get();
		List<Opd> opds = opdIoOperation.getOpdList(
				foundOpd.getWard(),
				foundOpd.getDisease().getType().getCode(),
				foundOpd.getDisease().getCode(),
				foundOpd.getDate().toLocalDate(),
				foundOpd.getDate().toLocalDate(),
				foundOpd.getAge() - 1,
				foundOpd.getAge() + 1,
				foundOpd.getSex(),
				foundOpd.getNewPatient(),
				foundOpd.getUserID());
		assertThat(opds.get(opds.size() - 1).getCode()).isEqualTo(foundOpd.getCode());
	}

	@Test
	public void testIoGetOpdListPatientId() throws Exception {
		int code = setupTestOpd(false);
		Opd foundOpd = opdIoOperationRepository.findById(code).get();
		List<Opd> opds = opdIoOperation.getOpdList(foundOpd.getPatient().getCode());
		assertThat(opds.get(opds.size() - 1).getCode()).isEqualTo(foundOpd.getCode());
	}

	@Test
	public void testIoGetOpdListPatientIdZero() throws Exception {
		Patient patient = testPatient.setup(false);
		DiseaseType diseaseType = testDiseaseType.setup(false);

		Disease disease = testDisease.setup(diseaseType, false);
		disease.setCode("angal.opd.alldiseases.txt");
		disease.getType().setCode("angal.common.alltypes.txt");
		
		Ward ward = testWard.setup(false);
		
		Visit nextVisit = testVisit.setup(patient, true, ward);

		Opd opd = testOpd.setup(patient, disease, ward, nextVisit, true);
		LocalDate now = LocalDate.now();
		opd.setDate(now.atStartOfDay());

		patientIoOperationRepository.saveAndFlush(patient);
		diseaseTypeIoOperationRepository.saveAndFlush(diseaseType);
		diseaseIoOperationRepository.saveAndFlush(disease);
		wardIoOperationRepository.saveAndFlush(ward);
		visitsIoOperationRepository.saveAndFlush(nextVisit);
		
		opdIoOperationRepository.saveAndFlush(opd);

		List<Opd> opds = opdIoOperation.getOpdList(0);
		assertThat(opds.get(opds.size() - 1).getCode()).isEqualTo(opd.getCode());
	}

	@Test
	public void testIoGetOpdListToday() throws Exception {
		Patient patient = testPatient.setup(false);
		DiseaseType diseaseType = testDiseaseType.setup(false);

		Disease disease = testDisease.setup(diseaseType, false);
		disease.setCode("angal.opd.alldiseases.txt");
		disease.getType().setCode("angal.common.alltypes.txt");

		Ward ward = testWard.setup(false);
		
		Visit nextVisit = testVisit.setup(patient, true, ward);

		Opd opd = testOpd.setup(patient, disease, ward, nextVisit, true);
		// set date to be today
		LocalDate today = LocalDate.now();
		opd.setDate(today.atStartOfDay());

		patientIoOperationRepository.saveAndFlush(patient);
		diseaseTypeIoOperationRepository.saveAndFlush(diseaseType);
		diseaseIoOperationRepository.saveAndFlush(disease);
		wardIoOperationRepository.saveAndFlush(ward);
		visitsIoOperationRepository.saveAndFlush(nextVisit);
		
		opdIoOperationRepository.saveAndFlush(opd);
		Patient patient2 = testPatient.setup(false);
		DiseaseType diseaseType2 = testDiseaseType.setup(false);

		Disease disease2 = testDisease.setup(diseaseType2, false);
		
		Ward ward2 = testWard.setup(false);
		ward2.setCode("ZZ");
		
		Visit nextVisit2 = testVisit.setup(patient, true, ward2);

		Opd opd2 = testOpd.setup(patient2, disease2, ward2, nextVisit2, true);
		// set date to be 14 days ago (not within the TODAY test)
		opd2.setDate(today.minusDays(14).atStartOfDay());

		patientIoOperationRepository.saveAndFlush(patient2);
		diseaseTypeIoOperationRepository.saveAndFlush(diseaseType2);
		diseaseIoOperationRepository.saveAndFlush(disease2);
		wardIoOperationRepository.saveAndFlush(ward2);
		visitsIoOperationRepository.saveAndFlush(nextVisit2);
		opdIoOperationRepository.saveAndFlush(opd2);

		List<Opd> opds = opdIoOperation.getOpdList(false);
		assertThat(opds).hasSize(1);
		assertThat(opds.get(opds.size() - 1).getCode()).isEqualTo(opd.getCode());
	}

	@Test
	public void testIoGetOpdListLastWeek() throws Exception {
		Patient patient = testPatient.setup(false);
		DiseaseType diseaseType = testDiseaseType.setup(false);

		Disease disease = testDisease.setup(diseaseType, false);
		disease.setCode("angal.opd.alldiseases.txt");
		disease.getType().setCode("angal.common.alltypes.txt");

		Ward ward = testWard.setup(false);
		
		Visit nextVisit = testVisit.setup(patient, true, ward);

		Opd opd = testOpd.setup(patient, disease, ward, nextVisit, true);
		LocalDate date = LocalDate.now();
		// set date to be 3 days ago (within last week)
		date.minusDays(3);
		opd.setDate(date.atStartOfDay());

		patientIoOperationRepository.saveAndFlush(patient);
		diseaseTypeIoOperationRepository.saveAndFlush(diseaseType);
		diseaseIoOperationRepository.saveAndFlush(disease);
		wardIoOperationRepository.saveAndFlush(ward);
		visitsIoOperationRepository.saveAndFlush(nextVisit);
		
		opdIoOperationRepository.saveAndFlush(opd);

		Patient patient2 = testPatient.setup(false);
		DiseaseType diseaseType2 = testDiseaseType.setup(false);

		Disease disease2 = testDisease.setup(diseaseType2, false);

		Ward ward2 = testWard.setup(false);
		ward2.setCode("ZZ");
		
		Visit nextVisit2 = testVisit.setup(patient, true, ward2);

		Opd opd2 = testOpd.setup(patient2, disease2, ward2, nextVisit2, true);
		LocalDate date2 = LocalDate.now();
		// set date to be 13 days aga (not within last week)
		date2 = date2.minusDays(13);
		opd2.setDate(date2.atStartOfDay());

		patientIoOperationRepository.saveAndFlush(patient2);
		diseaseTypeIoOperationRepository.saveAndFlush(diseaseType2);
		diseaseIoOperationRepository.saveAndFlush(disease2);
		wardIoOperationRepository.saveAndFlush(ward2);
		visitsIoOperationRepository.saveAndFlush(nextVisit2);
		opdIoOperationRepository.saveAndFlush(opd2);

		List<Opd> opds = opdIoOperation.getOpdList(true);
		assertThat(opds).hasSize(1);
		assertThat(opds.get(opds.size() - 1).getCode()).isEqualTo(opd.getCode());
	}

	@Test
	public void testIoNewOpd() throws Exception {
		Patient patient = testPatient.setup(false);
		DiseaseType diseaseType = testDiseaseType.setup(false);
		Disease disease = testDisease.setup(diseaseType, false);
		Ward ward = testWard.setup(false);
		Visit nextVisit = testVisit.setup(patient, true, ward);
		patientIoOperationRepository.saveAndFlush(patient);
		diseaseTypeIoOperationRepository.saveAndFlush(diseaseType);
		diseaseIoOperationRepository.saveAndFlush(disease);
		wardIoOperationRepository.saveAndFlush(ward);
		visitsIoOperationRepository.saveAndFlush(nextVisit);
		
		Opd opd = testOpd.setup(patient, disease, ward, nextVisit, false);
		Opd result = opdIoOperation.newOpd(opd);
		assertThat(result).isNotNull();
		checkOpdIntoDb(opd.getCode());
	}

	@Test
	public void testIoUpdateOpd() throws Exception {
		int code = setupTestOpd(false);
		Opd foundOpd = opdIoOperationRepository.findById(code).get();
		/*foundOpd.setReason("update reason");
		foundOpd.setAnamnesis("update anamnesis");
		foundOpd.setTherapies("update therapie");
		foundOpd.setAllergies("update allergies");
		foundOpd.setPrescription("update presciption");*/
		Opd result = opdIoOperation.updateOpd(foundOpd);
		Opd updateOpd = opdIoOperationRepository.findById(code).get();
		assertThat(result).isNotNull();
		/*assertThat(updateOpd.getReason()).isEqualTo("update reason");
		assertThat(updateOpd.getAnamnesis()).isEqualTo("update anamnesis");
		assertThat(updateOpd.getTherapies()).isEqualTo("update therapies");
		assertThat(updateOpd.getAllergies()).isEqualTo("update allergies");
		assertThat(updateOpd.getPrescription()).isEqualTo("update prescription");*/
	}

	@Test
	public void testIoDeleteOpd() throws Exception {
		int code = setupTestOpd(false);
		Opd foundOpd = opdIoOperationRepository.findById(code).get();
		boolean result = opdIoOperation.deleteOpd(foundOpd);
		assertThat(result).isTrue();
		result = opdIoOperation.isCodePresent(code);
		assertThat(result).isFalse();
	}

	@Test
	public void testIoGetProgYearZero() throws Exception {
		int code = setupTestOpd(false);
		int progYear = opdIoOperation.getProgYear(0);
		Opd foundOpd = opdIoOperationRepository.findById(code).get();
		assertThat(progYear).isEqualTo(foundOpd.getProgYear());
	}

	@Test
	public void testIoGetProgYear() throws Exception {
		int code = setupTestOpd(false);
		Opd opd = opdIoOperationRepository.findById(code).get();
		int progYear = opdIoOperation.getProgYear(opd.getDate().getYear());
		assertThat(progYear).isEqualTo(opd.getProgYear());
	}

	@Test
	public void testIoIsExistsOpdNumShouldReturnTrueWhenOpdWithGivenOPDProgressiveYearAndVisitYearExists() throws Exception {
		// given:
		int code = setupTestOpd(false);
		Opd foundOpd = opdIoOperationRepository.findById(code).get();

		// when:
		boolean result = opdIoOperation.isExistOpdNum(foundOpd.getProgYear(), foundOpd.getDate().getYear());

		// then:
		assertThat(result).isTrue();
	}

	@Test
	public void testIoIsExistsOpdNumShouldReturnTrueWhenOpdNumExistsAndVisitYearIsNotProvided() throws Exception {
		// given:
		int code = setupTestOpd(false);
		Opd foundOpd = opdIoOperationRepository.findById(code).get();

		// when:
		Boolean result = opdIoOperation.isExistOpdNum(foundOpd.getProgYear(), 0);

		// then:
		assertThat(result).isTrue();
	}

	@Test
	public void testIoIsExistsOpdNumShouldReturnFalseWhenOpdNumExistsAndVisitYearIsIncorrect() throws Exception {
		// given:
		int code = setupTestOpd(false);
		Opd foundOpd = opdIoOperationRepository.findById(code).get();

		// when:
		Boolean result = opdIoOperation.isExistOpdNum(foundOpd.getProgYear(), 1488);

		// then:
		assertThat(result).isFalse();
	}

	@Test
	public void testIoGetLastOpd() throws Exception {
		int code = setupTestOpd(false);
		Opd foundOpd = opdIoOperationRepository.findById(code).get();
		Opd lastOpd = opdIoOperation.getLastOpd(foundOpd.getPatient().getCode());
		assertThat(lastOpd.getCode()).isEqualTo(foundOpd.getCode());
	}

	@Test
	public void testListenerShouldUpdatePatientToMergedWhenPatientMergedEventArrive() throws Exception {
		// given:
		int id = setupTestOpd(false);
		Opd found = opdIoOperationRepository.findById(id).get();
		Patient mergedPatient = setupTestPatient(false);

		// when:
		applicationEventPublisher.publishEvent(new PatientMergedEvent(found.getPatient(), mergedPatient));

		// then:
		Opd result = opdIoOperationRepository.findById(id).get();
		assertThat(result.getPatient().getCode()).isEqualTo(mergedPatient.getCode());
	}

	@Test
	public void testMgrGetOpd() throws Exception {
		int code = setupTestOpd(false);
		Opd foundOpd = opdIoOperationRepository.findById(code).get();
		List<Opd> opds = opdBrowserManager.getOpd(
				foundOpd.getWard(),
				foundOpd.getDisease().getType().getCode(),
				foundOpd.getDisease().getCode(),
				foundOpd.getDate().toLocalDate(),
				foundOpd.getDate().toLocalDate(),
				foundOpd.getAge() - 1,
				foundOpd.getAge() + 1,
				foundOpd.getSex(),
				foundOpd.getNewPatient(),
				foundOpd.getUserID());
		assertThat(opds.get(opds.size() - 1).getCode()).isEqualTo(foundOpd.getCode());
	}

	@Test
	public void testMgrGetOpdListPatientId() throws Exception {
		int code = setupTestOpd(false);
		Opd foundOpd = opdIoOperationRepository.findById(code).get();
		List<Opd> opds = opdBrowserManager.getOpdList(foundOpd.getPatient().getCode());
		assertThat(opds.get(opds.size() - 1).getCode()).isEqualTo(foundOpd.getCode());
	}

	@Test
	public void testMgrGetOpdListPatientIdZero() throws Exception {
		Patient patient = testPatient.setup(false);
		DiseaseType diseaseType = testDiseaseType.setup(false);

		Disease disease = testDisease.setup(diseaseType, false);
		disease.setCode("angal.opd.alldiseases.txt");
		disease.getType().setCode("angal.common.alltypes.txt");

		Ward ward = testWard.setup(false);
		
		Visit nextVisit = testVisit.setup(patient, true, ward);

		Opd opd = testOpd.setup(patient, disease, ward, nextVisit, true);
		LocalDate now = LocalDate.now();
		opd.setDate(now.atStartOfDay());

		patientIoOperationRepository.saveAndFlush(patient);
		diseaseTypeIoOperationRepository.saveAndFlush(diseaseType);
		diseaseIoOperationRepository.saveAndFlush(disease);
		wardIoOperationRepository.saveAndFlush(ward);
		visitsIoOperationRepository.saveAndFlush(nextVisit);
		
		opdIoOperationRepository.saveAndFlush(opd);

		List<Opd> opds = opdBrowserManager.getOpdList(0);
		assertThat(opds.get(opds.size() - 1).getCode()).isEqualTo(opd.getCode());
	}

	@Test
	public void testMgrGetOpdToday() throws Exception {
		Patient patient = testPatient.setup(false);
		DiseaseType diseaseType = testDiseaseType.setup(false);

		Disease disease = testDisease.setup(diseaseType, false);
		disease.setCode("angal.opd.alldiseases.txt");
		disease.getType().setCode("angal.common.alltypes.txt");

		Ward ward = testWard.setup(false);
		
		Visit nextVisit = testVisit.setup(patient, true, ward);

		Opd opd = testOpd.setup(patient, disease, ward, nextVisit, true);
		// set date to be today
		LocalDate today = LocalDate.now();
		opd.setDate(today.atStartOfDay());

		patientIoOperationRepository.saveAndFlush(patient);
		diseaseTypeIoOperationRepository.saveAndFlush(diseaseType);
		diseaseIoOperationRepository.saveAndFlush(disease);
		wardIoOperationRepository.saveAndFlush(ward);
		visitsIoOperationRepository.saveAndFlush(nextVisit);
		
		opdIoOperationRepository.saveAndFlush(opd);

		Patient patient2 = testPatient.setup(false);
		DiseaseType diseaseType2 = testDiseaseType.setup(false);

		Disease disease2 = testDisease.setup(diseaseType2, false);

		Ward ward2 = testWard.setup(false);
		ward2.setCode("ZZ");
		
		Visit nextVisit2 = testVisit.setup(patient, true, ward2);

		Opd opd2 = testOpd.setup(patient2, disease2, ward2, nextVisit2, true);
		LocalDate now = LocalDate.now();
		// set date to be 14 days ago (not within the TODAY test)
		now = now.minusDays(14);
		opd2.setDate(now.atStartOfDay());

		patientIoOperationRepository.saveAndFlush(patient2);
		diseaseTypeIoOperationRepository.saveAndFlush(diseaseType2);
		diseaseIoOperationRepository.saveAndFlush(disease2);
		wardIoOperationRepository.saveAndFlush(ward2);
		visitsIoOperationRepository.saveAndFlush(nextVisit2);
		opdIoOperationRepository.saveAndFlush(opd2);

		List<Opd> opds = opdBrowserManager.getOpd(false);
		assertThat(opds).hasSize(1);
		assertThat(opds.get(opds.size() - 1).getCode()).isEqualTo(opd.getCode());
	}

	@Test
	public void testMgrGetOpdLastWeek() throws Exception {
		Patient patient = testPatient.setup(false);
		DiseaseType diseaseType = testDiseaseType.setup(false);

		Disease disease = testDisease.setup(diseaseType, false);
		disease.setCode("angal.opd.alldiseases.txt");
		disease.getType().setCode("angal.common.alltypes.txt");

		Ward ward = testWard.setup(false);
		
		Visit nextVisit = testVisit.setup(patient, true, ward);

		Opd opd = testOpd.setup(patient, disease, ward, nextVisit, true);
		LocalDate date = LocalDate.now();
		// set date to be 3 days ago (within last week)
		date = date.minusDays(3);
		opd.setDate(date.atStartOfDay());

		patientIoOperationRepository.saveAndFlush(patient);
		diseaseTypeIoOperationRepository.saveAndFlush(diseaseType);
		diseaseIoOperationRepository.saveAndFlush(disease);
		wardIoOperationRepository.saveAndFlush(ward);
		visitsIoOperationRepository.saveAndFlush(nextVisit);
		
		opdIoOperationRepository.saveAndFlush(opd);

		Patient patient2 = testPatient.setup(false);
		DiseaseType diseaseType2 = testDiseaseType.setup(false);

		Disease disease2 = testDisease.setup(diseaseType2, false);

		Ward ward2 = testWard.setup(false);
		ward2.setCode("ZZ");
		
		Visit nextVisit2 = testVisit.setup(patient, true, ward2);

		Opd opd2 = testOpd.setup(patient2, disease2, ward2, nextVisit2, true);
		LocalDate date2 = LocalDate.now();
		// set date to be 13 days ago (not within last week)
		date2 = date2.minusDays(13);
		opd2.setDate(date2.atStartOfDay());

		patientIoOperationRepository.saveAndFlush(patient2);
		diseaseTypeIoOperationRepository.saveAndFlush(diseaseType2);
		diseaseIoOperationRepository.saveAndFlush(disease2);
		wardIoOperationRepository.saveAndFlush(ward2);
		visitsIoOperationRepository.saveAndFlush(nextVisit2);
		opdIoOperationRepository.saveAndFlush(opd2);

		List<Opd> opds = opdBrowserManager.getOpd(true);
		assertThat(opds).hasSize(1);
		assertThat(opds.get(opds.size() - 1).getCode()).isEqualTo(opd.getCode());
	}

	@Test
	public void testMgrNewOpd() throws Exception {
		Patient patient = testPatient.setup(false);
		DiseaseType diseaseType = testDiseaseType.setup(false);
		Disease disease = testDisease.setup(diseaseType, false);
		Ward ward = testWard.setup(false);
		Visit nextVisit = testVisit.setup(patient, true, ward);
		patientIoOperationRepository.saveAndFlush(patient);
		diseaseTypeIoOperationRepository.saveAndFlush(diseaseType);
		diseaseIoOperationRepository.saveAndFlush(disease);
		wardIoOperationRepository.saveAndFlush(ward);
		visitsIoOperationRepository.saveAndFlush(nextVisit);
		
		Opd opd = testOpd.setup(patient, disease, ward, nextVisit, true);
		// Need this to pass validation checks in manager
		Disease disease2 = new Disease("998", "TestDescription 2", diseaseType);
		diseaseIoOperationRepository.saveAndFlush(disease2);
		Disease disease3 = new Disease("997", "TestDescription 3", diseaseType);
		diseaseIoOperationRepository.saveAndFlush(disease3);
		opd.setDisease2(disease2);
		opd.setDisease3(disease3);
		assertThat(opdBrowserManager.newOpd(opd));
		checkOpdIntoDb(opd.getCode());
	}

	@Test
	public void testMgrUpdateOpd() throws Exception {
		Patient patient = testPatient.setup(false);
		DiseaseType diseaseType = testDiseaseType.setup(false);
		Disease disease = testDisease.setup(diseaseType, false);
		Ward ward = testWard.setup(false);
		Visit nextVisit = testVisit.setup(patient, true, ward);
		patientIoOperationRepository.saveAndFlush(patient);
		diseaseTypeIoOperationRepository.saveAndFlush(diseaseType);
		diseaseIoOperationRepository.saveAndFlush(disease);
		wardIoOperationRepository.saveAndFlush(ward);
		visitsIoOperationRepository.saveAndFlush(nextVisit);
		
		Opd opd = testOpd.setup(patient, disease, ward, nextVisit, true);
		// Need this to pass validation checks in manager
		Disease disease2 = new Disease("998", "TestDescription 2", diseaseType);
		diseaseIoOperationRepository.saveAndFlush(disease2);
		Disease disease3 = new Disease("997", "TestDescription 3", diseaseType);
		diseaseIoOperationRepository.saveAndFlush(disease3);
		opd.setDisease2(disease2);
		opd.setDisease3(disease3);
		opd.setDate(TimeTools.getNow());
		assertThat(opdBrowserManager.newOpd(opd)).isNotNull();
		opd.setNote("Update");
		assertThat(opdBrowserManager.updateOpd(opd)).isNotNull();
		Opd updateOpd = opdIoOperationRepository.findById(opd.getCode()).get();
		/*assertThat(updateOpd.getReason()).isEqualTo("update reason");
		assertThat(updateOpd.getAnamnesis()).isEqualTo("update anamnesis");
		assertThat(updateOpd.getTherapies()).isEqualTo("update therapies");
		assertThat(updateOpd.getAllergies()).isEqualTo("update allergies");*/
		assertThat(updateOpd.getNote()).isEqualTo("Update");
	}

	@Test
	public void testMgrDeleteOpd() throws Exception {
		int code = setupTestOpd(false);
		Opd foundOpd = opdIoOperationRepository.findById(code).get();
		boolean result = opdBrowserManager.deleteOpd(foundOpd);
		assertThat(result).isTrue();
		result = opdIoOperation.isCodePresent(code);
		assertThat(result).isFalse();
	}

	@Test
	public void testMgrGetProgYearZero() throws Exception {
		int code = setupTestOpd(false);
		int progYear = opdBrowserManager.getProgYear(0);
		Opd foundOpd = opdIoOperationRepository.findById(code).get();
		assertThat(progYear).isEqualTo(foundOpd.getProgYear());
	}

	@Test
	public void testMgrGetProgYear() throws Exception {
		int code = setupTestOpd(false);
		Opd opd = opdIoOperationRepository.findById(code).get();
		int progYear = opdBrowserManager.getProgYear(opd.getDate().getYear());
		assertThat(progYear).isEqualTo(opd.getProgYear());
	}

	@Test
	public void testMgrIsExistsOpdNumShouldReturnTrueWhenOpdWithGivenOPDProgressiveYearAndVisitYearExists() throws Exception {
		int code = setupTestOpd(false);
		Opd opd = opdIoOperationRepository.findById(code).get();
		assertThat(opdBrowserManager.isExistOpdNum(opd.getProgYear(), opd.getDate().getYear())).isTrue();
	}

	@Test
	public void testMgrIsExistsOpdNumShouldReturnTrueWhenOpdNumExistsAndVisitYearIsNotProvided() throws Exception {
		int code = setupTestOpd(false);
		Opd opd = opdIoOperationRepository.findById(code).get();
		assertThat(opdBrowserManager.isExistOpdNum(opd.getProgYear(), 0)).isTrue();
	}

	@Test
	public void testMgrIsExistsOpdNumShouldReturnFalseWhenOpdNumExistsAndVisitYearIsIncorrect() throws Exception {
		int code = setupTestOpd(false);
		Opd opd = opdIoOperationRepository.findById(code).get();
		assertThat(opdBrowserManager.isExistOpdNum(opd.getProgYear(), 1488)).isFalse();
	}

	@Test
	public void testMgrGetLastOpd() throws Exception {
		int code = setupTestOpd(false);
		Opd foundOpd = opdIoOperationRepository.findById(code).get();
		Opd lastOpd = opdBrowserManager.getLastOpd(foundOpd.getPatient().getCode());
		assertThat(lastOpd.getCode()).isEqualTo(foundOpd.getCode());
	}

	@Test
	public void testMgrValidationVisitDateNull() throws Exception {
		assertThatThrownBy(() ->
		{
			Patient patient = testPatient.setup(false);
			DiseaseType diseaseType = testDiseaseType.setup(false);
			Disease disease = testDisease.setup(diseaseType, false);
			Ward ward = testWard.setup(false);
			Visit nextVisit = testVisit.setup(patient, false, ward);
			Opd opd = testOpd.setup(patient, disease, ward, nextVisit, false);
			// Need this to pass validation checks in manager
			opd.setDisease2(null);
			opd.setDisease3(null);

			// also let validation set userID
			opd.setUserID(null);

			opd.setDate(null);
			opdBrowserManager.newOpd(opd);
		})
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
	}

	@Test
	public void testMgrValidationOPDEXTENDEDPatientNull() throws Exception {
		if (!GeneralData.OPDEXTENDED) {
			return;
		}
		assertThatThrownBy(() ->
		{
			Patient patient = testPatient.setup(false);
			DiseaseType diseaseType = testDiseaseType.setup(false);
			Disease disease = testDisease.setup(diseaseType, false);
			Ward ward = testWard.setup(false);
			Visit nextVisit = testVisit.setup(patient, false, ward);
			Opd opd = testOpd.setup(patient, disease, ward, nextVisit, false);
			// Need this to pass validation checks in manager
			opd.setDisease2(null);
			opd.setDisease3(null);

			opd.setPatient(null);
			opdBrowserManager.newOpd(opd);
		})
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
	}

	@Test
	public void testMgrValidationAgeIsLessThanZero() throws Exception {
		assertThatThrownBy(() ->
		{
			Patient patient = testPatient.setup(false);
			DiseaseType diseaseType = testDiseaseType.setup(false);
			Disease disease = testDisease.setup(diseaseType, false);
			Ward ward = testWard.setup(false);
			Visit nextVisit = testVisit.setup(patient, false, ward);
			Opd opd = testOpd.setup(patient, disease, ward, nextVisit, false);
			// Need this to pass validation checks in manager
			opd.setDisease2(null);
			opd.setDisease3(null);

			opd.setAge(-99);
			opdBrowserManager.updateOpd(opd);
		})
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
	}

	@Test
	public void testMgrValidationSexIsEmpty() throws Exception {
		assertThatThrownBy(() ->
		{
			Patient patient = testPatient.setup(false);
			DiseaseType diseaseType = testDiseaseType.setup(false);
			Disease disease = testDisease.setup(diseaseType, false);
			Ward ward = testWard.setup(false);
			Visit nextVisit = testVisit.setup(patient, false, ward);
			Opd opd = testOpd.setup(patient, disease, ward, nextVisit, false);
			// Need this to pass validation checks in manager
			opd.setDisease2(null);
			opd.setDisease3(null);

			patient.setSex(' ');
			opd.setSex(' ');
			opdBrowserManager.newOpd(opd);
		})
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
	}

	@Test
	public void testMgrValidationDiseaseIsEmpty() throws Exception {
		assertThatThrownBy(() ->
		{
			Patient patient = testPatient.setup(false);
			DiseaseType diseaseType = testDiseaseType.setup(false);
			Disease disease = testDisease.setup(diseaseType, false);
			Ward ward = testWard.setup(false);
			Visit nextVisit = testVisit.setup(patient, false, ward);
			Opd opd = testOpd.setup(patient, disease, ward, nextVisit, false);
			// Need this to pass validation checks in manager
			opd.setDisease2(null);
			opd.setDisease3(null);

			opd.setDisease(null);
			opdBrowserManager.newOpd(opd);
		})
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
	}

	@Test
	public void testMgrValidationDiseaseIsEqualToDisease2() throws Exception {
		assertThatThrownBy(() ->
		{
			Patient patient = testPatient.setup(false);
			DiseaseType diseaseType = testDiseaseType.setup(false);
			Disease disease = testDisease.setup(diseaseType, false);
			Ward ward = testWard.setup(false);
			Visit nextVisit = testVisit.setup(patient, false, ward);
			Opd opd = testOpd.setup(patient, disease, ward, nextVisit, false);

			Disease disease2 = new Disease("997", "TestDescription 2", diseaseType);
			opd.setDisease2(disease2);
			opdBrowserManager.newOpd(opd);
		})
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
	}

	@Test
	public void testMgrValidationDiseaseIsEqualToDisease3() throws Exception {
		assertThatThrownBy(() ->
		{
			Patient patient = testPatient.setup(false);
			DiseaseType diseaseType = testDiseaseType.setup(false);
			Disease disease = testDisease.setup(diseaseType, false);
			Ward ward = testWard.setup(false);
			Visit nextVisit = testVisit.setup(patient, false, ward);
			Opd opd = testOpd.setup(patient, disease, ward, nextVisit, false);

			Disease disease3 = new Disease("998", "TestDescription 3", diseaseType);
			opd.setDisease3(disease3);
			opdBrowserManager.newOpd(opd);
		})
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
	}

	@Test
	public void testMgrValidationDisease2IsEqualToDisease3() throws Exception {
		assertThatThrownBy(() ->
		{
			Patient patient = testPatient.setup(false);
			DiseaseType diseaseType = testDiseaseType.setup(false);
			Disease disease = testDisease.setup(diseaseType, false);
			Ward ward = testWard.setup(false);
			Visit nextVisit = testVisit.setup(patient, false, ward);
			Opd opd = testOpd.setup(patient, disease, ward, nextVisit, false);

			Disease disease2 = new Disease("998", "TestDescription 2", diseaseType);
			opd.setDisease2(disease2);
			opd.setDisease3(disease2);
			opdBrowserManager.newOpd(opd);
		})
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
	}

	@Test
	public void testOpdGetSet() throws Exception {
		Patient patient = testPatient.setup(false);
		DiseaseType diseaseType = testDiseaseType.setup(false);
		Disease disease = testDisease.setup(diseaseType, false);
		Ward ward = testWard.setup(false);
		Visit nextVisit = testVisit.setup(patient, false, ward);
		Opd opd = testOpd.setup(patient, disease, ward, nextVisit, false);

		Disease disease2 = new Disease("997", "TestDescription 2", diseaseType);
		opd.setDisease2(disease2);

		Disease disease3 = new Disease("998", "TestDescription 3", diseaseType);
		opd.setDisease3(disease3);

		opd.getPatient().setFirstName("John");
		opd.getPatient().setSecondName("Smith");
		assertThat(opd.getFullName()).isEqualTo("John Smith");

		assertThat(opd.getfirstName()).isEqualTo("John");
		assertThat(opd.getsecondName()).isEqualTo("Smith");

		opd.getPatient().setFirstName(null);
		opd.getPatient().setSecondName(null);

		assertThat(opd.getFullName()).isEqualTo("null null");
		assertThat(opd.getfirstName()).isNull();
		assertThat(opd.getsecondName()).isNull();

		opd.getPatient().setNextKin("nextOfKin");
		opd.getPatient().setCity("city");
		opd.getPatient().setAddress("address");

		assertThat(opd.getnextKin()).isEqualTo("nextOfKin");
		assertThat(opd.getcity()).isEqualTo("city");
		assertThat(opd.getaddress()).isEqualTo("address");

		opd.getPatient().setNextKin(null);
		opd.getPatient().setCity(null);
		opd.getPatient().setAddress(null);

		assertThat(opd.getnextKin()).isNull();
		assertThat(opd.getcity()).isNull();
		assertThat(opd.getaddress()).isNull();

		opd.setPatient(null);

		assertThat(opd.getFullName()).isEmpty();
		assertThat(opd.getfirstName()).isEmpty();
		assertThat(opd.getsecondName()).isEmpty();
		assertThat(opd.getnextKin()).isEmpty();
		assertThat(opd.getcity()).isEmpty();
		assertThat(opd.getaddress()).isEmpty();

		opd.setCode(32000);
		assertThat(opd.getCode()).isEqualTo(32000);

		assertThat(opd.getDisease2()).isEqualTo(disease2);
		assertThat(opd.getDisease3()).isEqualTo(disease3);

		opd.setLock(-1);
		assertThat(opd.getLock()).isEqualTo(-1);
		
		opd.setNextVisit(null);
		assertThat(opd.getNextVisit()).isNull();
	}

	@Test
	public void testOpdHashCode() throws Exception {
		Patient patient = testPatient.setup(false);
		DiseaseType diseaseType = testDiseaseType.setup(false);
		Disease disease = testDisease.setup(diseaseType, false);
		Ward ward = testWard.setup(false);
		Visit nextVisit = testVisit.setup(patient, false, ward);
		Opd opd = testOpd.setup(patient, disease, ward, nextVisit, false);
		// compute
		int hashCode = opd.hashCode();
		assertThat(hashCode).isEqualTo(23 * 133 + opd.getCode());
		// used computed value
		assertThat(opd.hashCode()).isEqualTo(hashCode);
	}

	@Test
	public void testOpdEquals() throws Exception {
		Patient patient = testPatient.setup(false);
		DiseaseType diseaseType = testDiseaseType.setup(false);
		Disease disease = testDisease.setup(diseaseType, false);
		Ward ward = testWard.setup(false);
		Visit nextVisit = testVisit.setup(patient, false, ward);
		Opd opd = testOpd.setup(patient, disease, ward, nextVisit, false);

		assertThat(opd.equals(opd)).isTrue();
		assertThat(opd)
				.isNotNull()
				.isNotEqualTo("someString");
	}

	private Patient setupTestPatient(boolean usingSet) throws Exception {
		Patient patient = testPatient.setup(usingSet);
		patientIoOperationRepository.saveAndFlush(patient);
		return patient;
	}

	private int setupTestOpd(boolean usingSet) throws Exception {
		Patient patient = testPatient.setup(false);
		DiseaseType diseaseType = testDiseaseType.setup(false);
		Disease disease = testDisease.setup(diseaseType, false);
		Ward ward = testWard.setup(false);
		Visit nextVisit = testVisit.setup(patient, false, ward);
		
		Opd opd = testOpd.setup(patient, disease, ward, nextVisit, false);
		patientIoOperationRepository.saveAndFlush(patient);
		diseaseTypeIoOperationRepository.saveAndFlush(diseaseType);
		diseaseIoOperationRepository.saveAndFlush(disease);
		wardIoOperationRepository.saveAndFlush(ward);
		visitsIoOperationRepository.saveAndFlush(nextVisit);
		
		opdIoOperationRepository.saveAndFlush(opd);
		return opd.getCode();
	}

	private void checkOpdIntoDb(int code) throws OHException {
		Opd foundOpd = opdIoOperationRepository.findById(code).get();
		testOpd.check(foundOpd);
	}
}
