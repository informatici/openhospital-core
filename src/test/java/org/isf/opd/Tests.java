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
package org.isf.opd;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import org.assertj.core.api.Condition;
import org.isf.OHCoreTestCase;
import org.isf.disease.TestDisease;
import org.isf.disease.model.Disease;
import org.isf.disease.service.DiseaseIoOperationRepository;
import org.isf.distype.TestDiseaseType;
import org.isf.distype.model.DiseaseType;
import org.isf.distype.service.DiseaseTypeIoOperationRepository;
import org.isf.generaldata.GeneralData;
import org.isf.opd.manager.OpdBrowserManager;
import org.isf.opd.model.Opd;
import org.isf.opd.service.OpdIoOperationRepository;
import org.isf.opd.service.OpdIoOperations;
import org.isf.patient.TestPatient;
import org.isf.patient.model.Patient;
import org.isf.patient.model.PatientMergedEvent;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.time.TimeTools;
import org.isf.visits.TestVisit;
import org.isf.visits.model.Visit;
import org.isf.visits.service.VisitsIoOperationRepository;
import org.isf.ward.TestWard;
import org.isf.ward.model.Ward;
import org.isf.ward.service.WardIoOperationRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

class Tests extends OHCoreTestCase {

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

	@BeforeAll
	static void setUpClass() {
		testOpd = new TestOpd();
		testPatient = new TestPatient();
		testDisease = new TestDisease();
		testDiseaseType = new TestDiseaseType();
		testWard = new TestWard();
		testVisit = new TestVisit();
	}

	static Stream<Arguments> opdExtended() {
		return Stream.of(Arguments.of(false), Arguments.of(true));
	}

	@BeforeEach
	void setUp() throws Exception {
		cleanH2InMemoryDb();
		setupDiseaseRecords();
	}

	@ParameterizedTest(name = "Test with OPDEXTENDED={0}")
	@MethodSource("opdExtended")
	void testOpdGets(boolean opdExtended) throws Exception {
		GeneralData.OPDEXTENDED = opdExtended;
		int code = setupTestOpd(false);
		checkOpdIntoDb(code);
	}

	@ParameterizedTest(name = "Test with OPDEXTENDED={0}")
	@MethodSource("opdExtended")
	void testOpdSets(boolean opdExtended) throws Exception {
		GeneralData.OPDEXTENDED = opdExtended;
		int code = setupTestOpd(true);
		checkOpdIntoDb(code);
	}

	@ParameterizedTest(name = "Test with OPDEXTENDED={0}")
	@MethodSource("opdExtended")
	void testIoGetOpdList(boolean opdExtended) throws Exception {
		GeneralData.OPDEXTENDED = opdExtended;
		int code = setupTestOpd(false);
		Opd foundOpd = opdIoOperationRepository.findById(code).orElse(null);
		assertThat(foundOpd).isNotNull();
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

	@ParameterizedTest(name = "Test with OPDEXTENDED={0}")
	@MethodSource("opdExtended")
	void testIoGetOpdListPatientId(boolean opdExtended) throws Exception {
		GeneralData.OPDEXTENDED = opdExtended;
		int code = setupTestOpd(false);
		Opd foundOpd = opdIoOperationRepository.findById(code).orElse(null);
		assertThat(foundOpd).isNotNull();
		List<Opd> opds = opdIoOperation.getOpdList(foundOpd.getPatient().getCode());
		assertThat(opds.get(opds.size() - 1).getCode()).isEqualTo(foundOpd.getCode());
	}

	@ParameterizedTest(name = "Test with OPDEXTENDED={0}")
	@MethodSource("opdExtended")
	void testIoGetOpdListPatientIdZero(boolean opdExtended) throws Exception {
		GeneralData.OPDEXTENDED = opdExtended;		Patient patient = testPatient.setup(false);
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

	@ParameterizedTest(name = "Test with OPDEXTENDED={0}")
	@MethodSource("opdExtended")
	void testIoGetOpdListToday(boolean opdExtended) throws Exception {
		GeneralData.OPDEXTENDED = opdExtended;

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
		disease2.setCode("299");
		
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

	@ParameterizedTest(name = "Test with OPDEXTENDED={0}")
	@MethodSource("opdExtended")
	void testIoGetOpdListLastWeek(boolean opdExtended) throws Exception {
		GeneralData.OPDEXTENDED = opdExtended;
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
		disease2.setCode("499");

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

	@ParameterizedTest(name = "Test with OPDEXTENDED={0}")
	@MethodSource("opdExtended")
	void testIoNewOpd(boolean opdExtended) throws Exception {
		GeneralData.OPDEXTENDED = opdExtended;
		Patient patient = testPatient.setup(false);
		DiseaseType diseaseType = testDiseaseType.setup(false);
		Disease disease = testDisease.setup(diseaseType, false);
		disease.setCode("699");
		Ward ward = testWard.setup(false);
		Visit nextVisit = testVisit.setup(patient, true, ward);
		patientIoOperationRepository.saveAndFlush(patient);
		diseaseTypeIoOperationRepository.saveAndFlush(diseaseType);
		diseaseIoOperationRepository.saveAndFlush(disease);
		wardIoOperationRepository.saveAndFlush(ward);
		visitsIoOperationRepository.saveAndFlush(nextVisit);
		
		Opd opd = testOpd.setup(patient, disease, ward, nextVisit, false);
		Opd newOpd = opdIoOperation.newOpd(opd);
		checkOpdIntoDb(newOpd.getCode());
	}

	@ParameterizedTest(name = "Test with OPDEXTENDED={0}")
	@MethodSource("opdExtended")
	void testIoUpdateOpd(boolean opdExtended) throws Exception {
		GeneralData.OPDEXTENDED = opdExtended;
		int code = setupTestOpd(false);
		Opd foundOpd = opdIoOperationRepository.findById(code).orElse(null);
		assertThat(foundOpd).isNotNull();
		/*foundOpd.setReason("update reason");
		foundOpd.setAnamnesis("update anamnesis");
		foundOpd.setTherapies("update therapie");
		foundOpd.setAllergies("update allergies");
		foundOpd.setPrescription("update presciption");*/
		Opd updatedOpd = opdIoOperation.updateOpd(foundOpd);
		/*assertThat(updatedOpd.getReason()).isEqualTo("update reason");
		assertThat(updatedOpd.getAnamnesis()).isEqualTo("update anamnesis");
		assertThat(updatedOpd.getTherapies()).isEqualTo("update therapies");
		assertThat(updatedOpd.getAllergies()).isEqualTo("update allergies");
		assertThat(updatedOpd.getPrescription()).isEqualTo("update prescription");*/
	}

	@ParameterizedTest(name = "Test with OPDEXTENDED={0}")
	@MethodSource("opdExtended")
	void testIoDeleteOpd(boolean opdExtended) throws Exception {
		GeneralData.OPDEXTENDED = opdExtended;
		int code = setupTestOpd(false);
		Opd foundOpd = opdIoOperationRepository.findById(code).orElse(null);
		assertThat(foundOpd).isNotNull();
		opdIoOperation.deleteOpd(foundOpd);
		assertThat(opdIoOperation.isCodePresent(code)).isFalse();
	}

	@ParameterizedTest(name = "Test with OPDEXTENDED={0}")
	@MethodSource("opdExtended")
	void testIoGetProgYearZero(boolean opdExtended) throws Exception {
		GeneralData.OPDEXTENDED = opdExtended;
		int code = setupTestOpd(false);
		int progYear = opdIoOperation.getProgYear(0);
		Opd foundOpd = opdIoOperationRepository.findById(code).orElse(null);
		assertThat(foundOpd).isNotNull();
		assertThat(progYear).isEqualTo(foundOpd.getProgYear());
	}

	@ParameterizedTest(name = "Test with OPDEXTENDED={0}")
	@MethodSource("opdExtended")
	void testIoGetProgYear(boolean opdExtended) throws Exception {
		GeneralData.OPDEXTENDED = opdExtended;
		int code = setupTestOpd(false);
		Opd opd = opdIoOperationRepository.findById(code).orElse(null);
		assertThat(opd).isNotNull();
		int progYear = opdIoOperation.getProgYear(opd.getDate().getYear());
		assertThat(progYear).isEqualTo(opd.getProgYear());
	}

	@ParameterizedTest(name = "Test with OPDEXTENDED={0}")
	@MethodSource("opdExtended")
	void testIoIsExistsOpdNumShouldReturnTrueWhenOpdWithGivenOPDProgressiveYearAndVisitYearExists(boolean opdExtended) throws Exception {
		GeneralData.OPDEXTENDED = opdExtended;
		// given:
		int code = setupTestOpd(false);
		Opd foundOpd = opdIoOperationRepository.findById(code).orElse(null);
		assertThat(foundOpd).isNotNull();

		// when:
		boolean result = opdIoOperation.isExistOpdNum(foundOpd.getProgYear(), foundOpd.getDate().getYear());

		// then:
		assertThat(result).isTrue();
	}

	@ParameterizedTest(name = "Test with OPDEXTENDED={0}")
	@MethodSource("opdExtended")
	void testIoIsExistsOpdNumShouldReturnTrueWhenOpdNumExistsAndVisitYearIsNotProvided(boolean opdExtended) throws Exception {
		GeneralData.OPDEXTENDED = opdExtended;
		// given:
		int code = setupTestOpd(false);
		Opd foundOpd = opdIoOperationRepository.findById(code).orElse(null);
		assertThat(foundOpd).isNotNull();

		// when:
		boolean result = opdIoOperation.isExistOpdNum(foundOpd.getProgYear(), 0);

		// then:
		assertThat(result).isTrue();
	}

	@ParameterizedTest(name = "Test with OPDEXTENDED={0}")
	@MethodSource("opdExtended")
	void testIoIsExistsOpdNumShouldReturnFalseWhenOpdNumExistsAndVisitYearIsIncorrect(boolean opdExtended) throws Exception {
		GeneralData.OPDEXTENDED = opdExtended;
		// given:
		int code = setupTestOpd(false);
		Opd foundOpd = opdIoOperationRepository.findById(code).orElse(null);
		assertThat(foundOpd).isNotNull();

		// when:
		boolean result = opdIoOperation.isExistOpdNum(foundOpd.getProgYear(), 1488);

		// then:
		assertThat(result).isFalse();
	}

	@ParameterizedTest(name = "Test with OPDEXTENDED={0}")
	@MethodSource("opdExtended")
	void testIoGetLastOpd(boolean opdExtended) throws Exception {
		GeneralData.OPDEXTENDED = opdExtended;
		int code = setupTestOpd(false);
		Opd foundOpd = opdIoOperationRepository.findById(code).orElse(null);
		assertThat(foundOpd).isNotNull();
		Opd lastOpd = opdIoOperation.getLastOpd(foundOpd.getPatient().getCode());
		assertThat(lastOpd.getCode()).isEqualTo(foundOpd.getCode());
	}

	@ParameterizedTest(name = "Test with OPDEXTENDED={0}")
	@MethodSource("opdExtended")
	void testListenerShouldUpdatePatientToMergedWhenPatientMergedEventArrive(boolean opdExtended) throws Exception {
		GeneralData.OPDEXTENDED = opdExtended;
		// given:
		int id = setupTestOpd(false);
		Opd foundOpd = opdIoOperationRepository.findById(id).orElse(null);
		assertThat(foundOpd).isNotNull();
		Patient mergedPatient = setupTestPatient(false);

		// when:
		applicationEventPublisher.publishEvent(new PatientMergedEvent(foundOpd.getPatient(), mergedPatient));

		// then:
		Opd result = opdIoOperationRepository.findById(id).orElse(null);
		assertThat(result).isNotNull();
		assertThat(result.getPatient().getCode()).isEqualTo(mergedPatient.getCode());
	}

	@ParameterizedTest(name = "Test with OPDEXTENDED={0}")
	@MethodSource("opdExtended")
	void testMgrGetOpd(boolean opdExtended) throws Exception {
		GeneralData.OPDEXTENDED = opdExtended;

		int code = setupTestOpd(false);
		Opd foundOpd = opdIoOperationRepository.findById(code).orElse(null);
		assertThat(foundOpd).isNotNull();
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

	@ParameterizedTest(name = "Test with OPDEXTENDED={0}")
	@MethodSource("opdExtended")
	void testMgrGetOpdListPatientId(boolean opdExtended) throws Exception {
		GeneralData.OPDEXTENDED = opdExtended;
		int code = setupTestOpd(false);
		Opd foundOpd = opdIoOperationRepository.findById(code).orElse(null);
		assertThat(foundOpd).isNotNull();
		List<Opd> opds = opdBrowserManager.getOpdList(foundOpd.getPatient().getCode());
		assertThat(opds.get(opds.size() - 1).getCode()).isEqualTo(foundOpd.getCode());
	}

	@ParameterizedTest(name = "Test with OPDEXTENDED={0}")
	@MethodSource("opdExtended")
	void testMgrGetOpdListPatientIdZero(boolean opdExtended) throws Exception {
		GeneralData.OPDEXTENDED = opdExtended;
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

	@ParameterizedTest(name = "Test with OPDEXTENDED={0}")
	@MethodSource("opdExtended")
	void testMgrGetOpdToday(boolean opdExtended) throws Exception {
		GeneralData.OPDEXTENDED = opdExtended;

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
		diseaseType2.setCode("AZ");

		Disease disease2 = testDisease.setup(diseaseType2, false);
		disease2.setCode("399");

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

	@ParameterizedTest(name = "Test with OPDEXTENDED={0}")
	@MethodSource("opdExtended")
	void testMgrGetOpdLastWeek(boolean opdExtended) throws Exception {
		GeneralData.OPDEXTENDED = opdExtended;
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
		disease2.setCode("799");

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

	@ParameterizedTest(name = "Test with OPDEXTENDED={0}")
	@MethodSource("opdExtended")
	void testMgrNewOpd(boolean opdExtended) throws Exception {
		GeneralData.OPDEXTENDED = opdExtended;
		Patient patient = testPatient.setup(false);
		Disease disease = diseaseIoOperationRepository.findOneByCode("1");
		Ward ward = testWard.setup(false);
		Visit nextVisit = testVisit.setup(patient, true, ward);
		patientIoOperationRepository.saveAndFlush(patient);
		wardIoOperationRepository.saveAndFlush(ward);
		visitsIoOperationRepository.saveAndFlush(nextVisit);
		
		Opd opd = testOpd.setup(patient, disease, ward, nextVisit, true);
		// Need this to pass validation checks in manager
		Disease disease2 = diseaseIoOperationRepository.findOneByCode("2");
		Disease disease3 = diseaseIoOperationRepository.findOneByCode("3");
		opd.setDisease2(disease2);
		opd.setDisease3(disease3);
		Opd newOpd = opdBrowserManager.newOpd(opd);
		checkOpdIntoDb(newOpd.getCode());
	}

	@ParameterizedTest(name = "Test with OPDEXTENDED={0}")
	@MethodSource("opdExtended")
	void testMgrUpdateOpd(boolean opdExtended) throws Exception {
		GeneralData.OPDEXTENDED = opdExtended;
		Patient patient = testPatient.setup(false);
		Disease disease = diseaseIoOperationRepository.findOneByCode("1");
		Ward ward = testWard.setup(false);
		Visit nextVisit = testVisit.setup(patient, true, ward);
		patientIoOperationRepository.saveAndFlush(patient);
		wardIoOperationRepository.saveAndFlush(ward);
		visitsIoOperationRepository.saveAndFlush(nextVisit);
		
		Opd opd = testOpd.setup(patient, disease, ward, nextVisit, true);
		// Need this to pass validation checks in manager
		Disease disease2 = diseaseIoOperationRepository.findOneByCode("2");
		Disease disease3 = diseaseIoOperationRepository.findOneByCode("3");
		opd.setDisease2(disease2);
		opd.setDisease3(disease3);
		opd.setDate(TimeTools.getNow());
		Opd newOpd = opdBrowserManager.newOpd(opd);
		newOpd.setNote("Update");
		Opd updatedOpd = opdBrowserManager.updateOpd(newOpd);
		/*assertThat(updatedOpd.getReason()).isEqualTo("update reason");
		assertThat(updatedOpd.getAnamnesis()).isEqualTo("update anamnesis");
		assertThat(updatedOpd.getTherapies()).isEqualTo("update therapies");
		assertThat(updatedOpd.getAllergies()).isEqualTo("update allergies");*/
		assertThat(updatedOpd.getNote()).isEqualTo("Update");
	}

	@ParameterizedTest(name = "Test with OPDEXTENDED={0}")
	@MethodSource("opdExtended")
	void testMgrDeleteOpd(boolean opdExtended) throws Exception {
		GeneralData.OPDEXTENDED = opdExtended;
		int code = setupTestOpd(false);
		Opd foundOpd = opdIoOperationRepository.findById(code).orElse(null);
		assertThat(foundOpd).isNotNull();
		opdBrowserManager.deleteOpd(foundOpd);
		assertThat(opdIoOperation.isCodePresent(code)).isFalse();
	}

	@ParameterizedTest(name = "Test with OPDEXTENDED={0}")
	@MethodSource("opdExtended")
	void testMgrGetProgYearZero(boolean opdExtended) throws Exception {
		GeneralData.OPDEXTENDED = opdExtended;
		int code = setupTestOpd(false);
		int progYear = opdBrowserManager.getProgYear(0);
		Opd foundOpd = opdIoOperationRepository.findById(code).orElse(null);
		assertThat(foundOpd).isNotNull();
		assertThat(progYear).isEqualTo(foundOpd.getProgYear());
	}

	@ParameterizedTest(name = "Test with OPDEXTENDED={0}")
	@MethodSource("opdExtended")
	void testMgrGetProgYear(boolean opdExtended) throws Exception {
		GeneralData.OPDEXTENDED = opdExtended;

		int code = setupTestOpd(false);
		Opd opd = opdIoOperationRepository.findById(code).orElse(null);
		assertThat(opd).isNotNull();
		int progYear = opdBrowserManager.getProgYear(opd.getDate().getYear());
		assertThat(progYear).isEqualTo(opd.getProgYear());
	}

	@ParameterizedTest(name = "Test with OPDEXTENDED={0}")
	@MethodSource("opdExtended")
	void testMgrIsExistsOpdNumShouldReturnTrueWhenOpdWithGivenOPDProgressiveYearAndVisitYearExists(boolean opdExtended) throws Exception {
		GeneralData.OPDEXTENDED = opdExtended;
		int code = setupTestOpd(false);
		Opd opd = opdIoOperationRepository.findById(code).orElse(null);
		assertThat(opd).isNotNull();
		assertThat(opdBrowserManager.isExistOpdNum(opd.getProgYear(), opd.getDate().getYear())).isTrue();
	}

	@ParameterizedTest(name = "Test with OPDEXTENDED={0}")
	@MethodSource("opdExtended")
	void testMgrIsExistsOpdNumShouldReturnTrueWhenOpdNumExistsAndVisitYearIsNotProvided(boolean opdExtended) throws Exception {
		GeneralData.OPDEXTENDED = opdExtended;
		int code = setupTestOpd(false);
		Opd opd = opdIoOperationRepository.findById(code).orElse(null);
		assertThat(opd).isNotNull();
		assertThat(opdBrowserManager.isExistOpdNum(opd.getProgYear(), 0)).isTrue();
	}

	@ParameterizedTest(name = "Test with OPDEXTENDED={0}")
	@MethodSource("opdExtended")
	void testMgrIsExistsOpdNumShouldReturnFalseWhenOpdNumExistsAndVisitYearIsIncorrect(boolean opdExtended) throws Exception {
		GeneralData.OPDEXTENDED = opdExtended;
		int code = setupTestOpd(false);
		Opd opd = opdIoOperationRepository.findById(code).orElse(null);
		assertThat(opd).isNotNull();
		assertThat(opdBrowserManager.isExistOpdNum(opd.getProgYear(), 1488)).isFalse();
	}

	@ParameterizedTest(name = "Test with OPDEXTENDED={0}")
	@MethodSource("opdExtended")
	void testMgrGetLastOpd(boolean opdExtended) throws Exception {
		GeneralData.OPDEXTENDED = opdExtended;
		int code = setupTestOpd(false);
		Opd foundOpd = opdIoOperationRepository.findById(code).orElse(null);
		assertThat(foundOpd).isNotNull();
		Opd lastOpd = opdBrowserManager.getLastOpd(foundOpd.getPatient().getCode());
		assertThat(lastOpd.getCode()).isEqualTo(foundOpd.getCode());
	}

	@ParameterizedTest(name = "Test with OPDEXTENDED={0}")
	@MethodSource("opdExtended")
	void testMgrValidationVisitDateNull(boolean opdExtended) throws Exception {
		GeneralData.OPDEXTENDED = opdExtended;
		assertThatThrownBy(() ->
		{
			Patient patient = testPatient.setup(false);
			DiseaseType diseaseType = testDiseaseType.setup(false);
			Disease disease = testDisease.setup(diseaseType, false);
			disease.setCode("1");
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
					e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error")
			);
	}

	@ParameterizedTest(name = "Test with OPDEXTENDED={0}")
	@MethodSource("opdExtended")
	void testMgrValidationOPDEXTENDEDPatientNull(boolean opdExtended) throws Exception {
		GeneralData.OPDEXTENDED = opdExtended;
		if (!GeneralData.OPDEXTENDED) {
			return;
		}
		assertThatThrownBy(() ->
		{
			Patient patient = testPatient.setup(false);
			DiseaseType diseaseType = testDiseaseType.setup(false);
			Disease disease = testDisease.setup(diseaseType, false);
			disease.setCode("1");
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
					e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error")
			);
	}

	@ParameterizedTest(name = "Test with OPDEXTENDED={0}")
	@MethodSource("opdExtended")
	void testMgrValidationAgeIsLessThanZero(boolean opdExtended) throws Exception {
		GeneralData.OPDEXTENDED = opdExtended;
		assertThatThrownBy(() ->
		{
			Patient patient = testPatient.setup(false);
			DiseaseType diseaseType = testDiseaseType.setup(false);
			Disease disease = testDisease.setup(diseaseType, false);
			disease.setCode("2");
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
					e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error")
			);
	}

	@ParameterizedTest(name = "Test with OPDEXTENDED={0}")
	@MethodSource("opdExtended")
	void testMgrValidationSexIsEmpty(boolean opdExtended) throws Exception {
		GeneralData.OPDEXTENDED = opdExtended;
		assertThatThrownBy(() ->
		{
			Patient patient = testPatient.setup(false);
			DiseaseType diseaseType = testDiseaseType.setup(false);
			Disease disease = testDisease.setup(diseaseType, false);
			disease.setCode("3");
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
					e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error")
			);
	}

	@ParameterizedTest(name = "Test with OPDEXTENDED={0}")
	@MethodSource("opdExtended")
	void testMgrValidationDiseaseIsEmpty(boolean opdExtended) throws Exception {
		GeneralData.OPDEXTENDED = opdExtended;
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
					e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error")
			);
	}

	@ParameterizedTest(name = "Test with OPDEXTENDED={0}")
	@MethodSource("opdExtended")
	void testMgrValidationDiseaseIsEqualToDisease2(boolean opdExtended) throws Exception {
		GeneralData.OPDEXTENDED = opdExtended;
		assertThatThrownBy(() ->
		{
			Patient patient = testPatient.setup(false);
			DiseaseType diseaseType = testDiseaseType.setup(false);
			Disease disease = testDisease.setup(diseaseType, false);
			disease.setCode("1");
			Ward ward = testWard.setup(false);
			Visit nextVisit = testVisit.setup(patient, false, ward);
			Opd opd = testOpd.setup(patient, disease, ward, nextVisit, false);

			Disease disease2 = new Disease("1", "TestDescription 2", diseaseType);
			opd.setDisease2(disease2);
			opd.setDisease3(null);
			opdBrowserManager.newOpd(opd);
		})
			.isInstanceOf(OHDataValidationException.class)
			.has(
				new Condition<Throwable>(
					e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error")
			);
	}

	@ParameterizedTest(name = "Test with OPDEXTENDED={0}")
	@MethodSource("opdExtended")
	void testMgrValidationDiseaseIsEqualToDisease3(boolean opdExtended) throws Exception {
		GeneralData.OPDEXTENDED = opdExtended;
		assertThatThrownBy(() ->
		{
			Patient patient = testPatient.setup(false);
			DiseaseType diseaseType = testDiseaseType.setup(false);
			Disease disease = testDisease.setup(diseaseType, false);
			disease.setCode("1");
			Ward ward = testWard.setup(false);
			Visit nextVisit = testVisit.setup(patient, false, ward);
			Opd opd = testOpd.setup(patient, disease, ward, nextVisit, false);

			Disease disease2 = new Disease("3", "TestDescription 2", diseaseType);
			opd.setDisease2(disease2);
			Disease disease3 = new Disease("1", "TestDescription 3", diseaseType);
			opd.setDisease3(disease3);
			opdBrowserManager.newOpd(opd);
		})
			.isInstanceOf(OHDataValidationException.class)
			.has(
				new Condition<Throwable>(
					e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error")
			);
	}

	@ParameterizedTest(name = "Test with OPDEXTENDED={0}")
	@MethodSource("opdExtended")
	void testMgrValidationDisease2IsEqualToDisease3(boolean opdExtended) throws Exception {
		GeneralData.OPDEXTENDED = opdExtended;
		assertThatThrownBy(() ->
		{
			Patient patient = testPatient.setup(false);
			DiseaseType diseaseType = testDiseaseType.setup(false);
			Disease disease = testDisease.setup(diseaseType, false);
			disease.setCode("1");
			Ward ward = testWard.setup(false);
			Visit nextVisit = testVisit.setup(patient, false, ward);
			Opd opd = testOpd.setup(patient, disease, ward, nextVisit, false);
			Disease disease2 = new Disease("2", "TestDescription 2", diseaseType);
			opd.setDisease2(disease2);
			opd.setDisease3(disease2);
			opdBrowserManager.newOpd(opd);
		})
			.isInstanceOf(OHDataValidationException.class)
			.has(
				new Condition<Throwable>(
					e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error")
			);
	}

	@ParameterizedTest(name = "Test with OPDEXTENDED={0}")
	@MethodSource("opdExtended")
	void testOpdGetSet(boolean opdExtended) throws Exception {
		GeneralData.OPDEXTENDED = opdExtended;
		Patient patient = testPatient.setup(false);
		DiseaseType diseaseType = testDiseaseType.setup(false);
		Disease disease = testDisease.setup(diseaseType, false);
		Ward ward = testWard.setup(false);
		Visit nextVisit = testVisit.setup(patient, false, ward);
		Opd opd = testOpd.setup(patient, disease, ward, nextVisit, false);

		Disease disease2 = new Disease("3", "TestDescription 2", diseaseType);
		opd.setDisease2(disease2);

		Disease disease3 = new Disease("2", "TestDescription 3", diseaseType);
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

	@ParameterizedTest(name = "Test with OPDEXTENDED={0}")
	@MethodSource("opdExtended")
	void testOpdHashCode(boolean opdExtended) throws Exception {
		GeneralData.OPDEXTENDED = opdExtended;
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

	@ParameterizedTest(name = "Test with OPDEXTENDED={0}")
	@MethodSource("opdExtended")
	void testOpdEquals(boolean opdExtended) throws Exception {
		GeneralData.OPDEXTENDED = opdExtended;
		Patient patient = testPatient.setup(false);
		DiseaseType diseaseType = testDiseaseType.setup(false);
		Disease disease = testDisease.setup(diseaseType, false);
		Ward ward = testWard.setup(false);
		Visit nextVisit = testVisit.setup(patient, false, ward);
		Opd opd = testOpd.setup(patient, disease, ward, nextVisit, false);

		assertThat(opd).isEqualTo(opd)
				.isNotNull()
				.isNotEqualTo("someString");
	}

	@ParameterizedTest(name = "Test with OPDEXTENDED={0}")
	@MethodSource("opdExtended")
	void testValidateOpdDiseasesSingleDiseaseValid(boolean opdExtended) throws Exception {
		GeneralData.OPDEXTENDED = opdExtended;
		Patient patient = testPatient.setup(false);
		DiseaseType diseaseType = testDiseaseType.setup(false);
		Disease disease = testDisease.setup(diseaseType, false);
		disease.setCode("1");
		Ward ward = testWard.setup(false);
		Visit nextVisit = testVisit.setup(patient, false, ward);
		Opd opd = testOpd.setup(patient, disease, ward, nextVisit, false);
		opd.setDisease2(null);
		opd.setDisease3(null);
		assertThatNoException().isThrownBy(() -> opdBrowserManager.validateOpd(opd, false));
	}

	@ParameterizedTest(name = "Test with OPDEXTENDED={0}")
	@MethodSource("opdExtended")
	void testValidateOpdDiseasesTwoDiseasesValid(boolean opdExtended) throws Exception {
		GeneralData.OPDEXTENDED = opdExtended;
		Patient patient = testPatient.setup(false);
		DiseaseType diseaseType = testDiseaseType.setup(false);
		Disease disease = testDisease.setup(diseaseType, false);
		disease.setCode("1");
		Ward ward = testWard.setup(false);
		Visit nextVisit = testVisit.setup(patient, false, ward);
		Opd opd = testOpd.setup(patient, disease, ward, nextVisit, false);
		Disease disease2 = testDisease.setup(diseaseType, false);
		disease2.setCode("2");
		opd.setDisease2(disease2);
		opd.setDisease3(null);
		assertThatNoException().isThrownBy(() -> opdBrowserManager.validateOpd(opd, false));
	}

	@ParameterizedTest(name = "Test with OPDEXTENDED={0}")
	@MethodSource("opdExtended")
	void testValidateOpdDiseasesThreeDiseasesValid(boolean opdExtended) throws Exception {
		GeneralData.OPDEXTENDED = opdExtended;
		Patient patient = testPatient.setup(false);
		DiseaseType diseaseType = testDiseaseType.setup(false);
		Disease disease = testDisease.setup(diseaseType, false);
		disease.setCode("1");
		Ward ward = testWard.setup(false);
		Visit nextVisit = testVisit.setup(patient, false, ward);
		Opd opd = testOpd.setup(patient, disease, ward, nextVisit, false);
		Disease disease2 = testDisease.setup(diseaseType, false);
		disease2.setCode("2");
		opd.setDisease2(disease2);
		Disease disease3 = testDisease.setup(diseaseType, false);
		disease3.setCode("3");
		opd.setDisease3(disease3);
		assertThatNoException().isThrownBy(() -> opdBrowserManager.validateOpd(opd, false));
	}

	@ParameterizedTest(name = "Test with OPDEXTENDED={0}")
	@MethodSource("opdExtended")
	void testValidateOpdDiseasesSingleDiseaseException(boolean opdExtended) throws Exception {
		GeneralData.OPDEXTENDED = opdExtended;
		Patient patient = testPatient.setup(false);
		DiseaseType diseaseType = testDiseaseType.setup(false);
		Disease disease = testDisease.setup(diseaseType, false);
		disease.setCode("101");
		Ward ward = testWard.setup(false);
		Visit nextVisit = testVisit.setup(patient, false, ward);
		Opd opd = testOpd.setup(patient, disease, ward, nextVisit, false);
		opd.setDisease2(null);
		opd.setDisease3(null);
		assertThatThrownBy(() -> opdBrowserManager.validateOpd(opd, false))
			.isInstanceOf(OHDataValidationException.class)
			.has(
				new Condition<Throwable>(
					e -> ((OHDataValidationException) e).getMessages().size() == 1, "Expecting single validation error")
			);
	}

	@ParameterizedTest(name = "Test with OPDEXTENDED={0}")
	@MethodSource("opdExtended")
	void testValidateOpdDiseasesTwoDiseasesException(boolean opdExtended) throws Exception {
		GeneralData.OPDEXTENDED = opdExtended;
		Patient patient = testPatient.setup(false);
		DiseaseType diseaseType = testDiseaseType.setup(false);
		Disease disease = testDisease.setup(diseaseType, false);
		Ward ward = testWard.setup(false);
		Visit nextVisit = testVisit.setup(patient, false, ward);
		Opd opd = testOpd.setup(patient, disease, ward, nextVisit, false);
		Disease disease2 = testDisease.setup(diseaseType, false);
		disease2.setCode("102");
		opd.setDisease2(disease2);
		opd.setDisease3(null);
		assertThatThrownBy(() -> opdBrowserManager.validateOpd(opd, false))
			.isInstanceOf(OHDataValidationException.class)
			.has(
				new Condition<Throwable>(
					e -> ((OHDataValidationException) e).getMessages().size() == 2, "Expecting two validation errors")
			);
	}

	@ParameterizedTest(name = "Test with OPDEXTENDED={0}")
	@MethodSource("opdExtended")
	void testValidateOpdDiseasesThreeDiseasesException(boolean opdExtended) throws Exception {
		GeneralData.OPDEXTENDED = opdExtended;
		Patient patient = testPatient.setup(false);
		DiseaseType diseaseType = testDiseaseType.setup(false);
		Disease disease = testDisease.setup(diseaseType, false);
		Ward ward = testWard.setup(false);
		Visit nextVisit = testVisit.setup(patient, false, ward);
		Opd opd = testOpd.setup(patient, disease, ward, nextVisit, false);
		Disease disease2 = testDisease.setup(diseaseType, false);
		disease2.setCode("102");
		opd.setDisease2(disease2);
		Disease disease3 = testDisease.setup(diseaseType, false);
		disease3.setCode("103");
		opd.setDisease3(disease3);
		assertThatThrownBy(() -> opdBrowserManager.validateOpd(opd, false))
			.isInstanceOf(OHDataValidationException.class)
			.has(
				new Condition<Throwable>(
					e -> ((OHDataValidationException) e).getMessages().size() == 3, "Expecting three validation errors")
			);
	}

	private void setupDiseaseRecords() throws Exception {
		DiseaseType diseaseType = testDiseaseType.setup(false);
		Disease disease = testDisease.setup(diseaseType, false);
		diseaseTypeIoOperationRepository.saveAndFlush(diseaseType);

		Disease disease2 = testDisease.setup(diseaseType, false);
		disease2.setCode("1");
		disease2.setOpdInclude(true);
		disease2.setDescription("code = 1 and is OPD");
		diseaseIoOperationRepository.saveAndFlush(disease2);

		Disease disease3 = testDisease.setup(diseaseType, false);
		disease3.setCode("2");
		disease3.setOpdInclude(true);
		disease3.setDescription("code = 2 and is OPD");
		diseaseIoOperationRepository.saveAndFlush(disease3);

		Disease disease4 = testDisease.setup(diseaseType, false);
		disease4.setCode("3");
		disease4.setOpdInclude(true);
		disease4.setDescription("code = 3 and is OPD");
		diseaseIoOperationRepository.saveAndFlush(disease4);

		Disease disease5 = testDisease.setup(diseaseType, false);
		disease5.setCode("101");
		disease5.setOpdInclude(false);
		disease5.setDescription("code = 101 and is NOT OPD");
		diseaseIoOperationRepository.saveAndFlush(disease5);

		Disease disease6 = testDisease.setup(diseaseType, false);
		disease6.setCode("102");
		disease6.setOpdInclude(false);
		disease6.setDescription("code = 102 and is NOT OPD");
		diseaseIoOperationRepository.saveAndFlush(disease6);

		Disease disease7 = testDisease.setup(diseaseType, false);
		disease7.setCode("103");
		disease7.setOpdInclude(false);
		disease7.setDescription("code = 103 and is NOT OPD");
		diseaseIoOperationRepository.saveAndFlush(disease);
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
		disease.setCode("199");
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
		Opd foundOpd = opdIoOperationRepository.findById(code).orElse(null);
		assertThat(foundOpd).isNotNull();
		testOpd.check(foundOpd);
	}
}
