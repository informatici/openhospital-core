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
package org.isf.opd.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.isf.OHCoreTestCase;
import org.isf.disease.TestDisease;
import org.isf.disease.model.Disease;
import org.isf.disease.service.DiseaseIoOperationRepository;
import org.isf.distype.TestDiseaseType;
import org.isf.distype.model.DiseaseType;
import org.isf.distype.service.DiseaseTypeIoOperationRepository;
import org.isf.generaldata.GeneralData;
import org.isf.opd.TestOpd;
import org.isf.opd.model.Opd;
import org.isf.patient.TestPatient;
import org.isf.patient.model.Patient;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.utils.exception.OHException;
import org.isf.utils.pagination.PagedResponse;
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

class OpdIoOperationsTest extends OHCoreTestCase {

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
	DiseaseTypeIoOperationRepository diseaseTypeIoOperationRepository;
	@Autowired
	DiseaseIoOperationRepository diseaseIoOperationRepository;

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
	void getOpdList_shouldReturnOpd_whenAllFiltersMatch(boolean opdExtended) throws Exception {
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
	void getOpdListPageable_shouldPageOpdsInDateRangeOrderedByDateDescAndCodeDesc(boolean opdExtended) throws Exception {
		GeneralData.OPDEXTENDED = opdExtended;
		// the OPDs share patient, disease, ward and visit, so only the date filter and the order tell them apart
		Patient patient = testPatient.setup(false);
		DiseaseType diseaseType = testDiseaseType.setup(false);
		Disease disease = testDisease.setup(diseaseType, false);
		disease.setCode("199");
		Ward ward = testWard.setup(false);
		Visit nextVisit = testVisit.setup(patient, false, ward);
		patientIoOperationRepository.saveAndFlush(patient);
		diseaseTypeIoOperationRepository.saveAndFlush(diseaseType);
		diseaseIoOperationRepository.saveAndFlush(disease);
		wardIoOperationRepository.saveAndFlush(ward);
		visitsIoOperationRepository.saveAndFlush(nextVisit);

		// not saved in date order, so the code alone does not give the expected order; the progressive number restarts with the new year,
		// two visits share the same date and the last one is before the filter
		LocalDate dateFrom = LocalDate.of(2019, 12, 31);
		LocalDate dateTo = LocalDate.of(2020, 1, 1);
		List<LocalDateTime> dates = List.of(dateTo.atTime(8, 0), dateFrom.atTime(8, 0), dateTo.atTime(8, 0), dateFrom.minusDays(1).atTime(8, 0));
		List<Integer> progYears = List.of(1, 2, 2, 1);
		List<Integer> codes = new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			Opd opd = testOpd.setup(patient, disease, ward, nextVisit, false);
			opd.setDate(dates.get(i));
			opd.setProgYear(progYears.get(i));
			codes.add(opdIoOperationRepository.saveAndFlush(opd).getCode());
		}

		PagedResponse<Opd> page0 = opdIoOperation.getOpdListPageable(null, "", "", dateFrom, dateTo, 0, 0, 'A', 'A', null, 0, 2);
		PagedResponse<Opd> page1 = opdIoOperation.getOpdListPageable(null, "", "", dateFrom, dateTo, 0, 0, 'A', 'A', null, 1, 2);

		assertThat(page0.getData()).hasSize(2);
		assertThat(page1.getData()).hasSize(1);
		assertThat(page0.getPageInfo().getTotalNbOfElements()).isEqualTo(3);
		assertThat(page0.getPageInfo().getTotalPages()).isEqualTo(2);

		// every matching row once across the pages: newest first, and the highest code first for the two with the same date
		List<Integer> pagedCodes = new ArrayList<>();
		page0.getData().forEach(opd -> pagedCodes.add(opd.getCode()));
		page1.getData().forEach(opd -> pagedCodes.add(opd.getCode()));
		assertThat(pagedCodes).containsExactly(codes.get(2), codes.get(0), codes.get(1));

		// a page past the end used to throw: it is empty and the total stays correct
		PagedResponse<Opd> beyond = opdIoOperation.getOpdListPageable(null, "", "", dateFrom, dateTo, 0, 0, 'A', 'A', null, 5, 2);
		assertThat(beyond.getData()).isEmpty();
		assertThat(beyond.getPageInfo().getTotalNbOfElements()).isEqualTo(3);
	}

	@ParameterizedTest(name = "Test with OPDEXTENDED={0}")
	@MethodSource("opdExtended")
	void getOpdList_shouldReturnPatientOpds_whenPatientCodeIsGiven(boolean opdExtended) throws Exception {
		GeneralData.OPDEXTENDED = opdExtended;
		int code = setupTestOpd(false);
		Opd foundOpd = opdIoOperationRepository.findById(code).orElse(null);
		assertThat(foundOpd).isNotNull();
		List<Opd> opds = opdIoOperation.getOpdList(foundOpd.getPatient().getCode());
		assertThat(opds.get(opds.size() - 1).getCode()).isEqualTo(foundOpd.getCode());
	}

	@ParameterizedTest(name = "Test with OPDEXTENDED={0}")
	@MethodSource("opdExtended")
	void getOpdList_shouldReturnAllOpds_whenPatientCodeIsZero(boolean opdExtended) throws Exception {
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
	void getOpdList_shouldReturnOnlyTodayOpds_whenOneWeekIsFalse(boolean opdExtended) throws Exception {
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
	void getOpdList_shouldReturnOnlyLastWeekOpds_whenOneWeekIsTrue(boolean opdExtended) throws Exception {
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
	void newOpd_shouldSaveOpd(boolean opdExtended) throws Exception {
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
	void updateOpd_shouldNotThrow_whenOpdIsUnchanged(boolean opdExtended) throws Exception {
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
	void deleteOpd_shouldRemoveOpd(boolean opdExtended) throws Exception {
		GeneralData.OPDEXTENDED = opdExtended;
		int code = setupTestOpd(false);
		Opd foundOpd = opdIoOperationRepository.findById(code).orElse(null);
		assertThat(foundOpd).isNotNull();
		opdIoOperation.deleteOpd(foundOpd);
		assertThat(opdIoOperation.isCodePresent(code)).isFalse();
	}

	@ParameterizedTest(name = "Test with OPDEXTENDED={0}")
	@MethodSource("opdExtended")
	void getProgYear_shouldReturnMaxProgYear_whenYearIsZero(boolean opdExtended) throws Exception {
		GeneralData.OPDEXTENDED = opdExtended;
		int code = setupTestOpd(false);
		int progYear = opdIoOperation.getProgYear(0);
		Opd foundOpd = opdIoOperationRepository.findById(code).orElse(null);
		assertThat(foundOpd).isNotNull();
		assertThat(progYear).isEqualTo(foundOpd.getProgYear());
	}

	@ParameterizedTest(name = "Test with OPDEXTENDED={0}")
	@MethodSource("opdExtended")
	void getProgYear_shouldReturnMaxProgYearOfThatYear_whenYearIsGiven(boolean opdExtended) throws Exception {
		GeneralData.OPDEXTENDED = opdExtended;
		int code = setupTestOpd(false);
		Opd opd = opdIoOperationRepository.findById(code).orElse(null);
		assertThat(opd).isNotNull();
		int progYear = opdIoOperation.getProgYear(opd.getDate().getYear());
		assertThat(progYear).isEqualTo(opd.getProgYear());
	}

	@ParameterizedTest(name = "Test with OPDEXTENDED={0}")
	@MethodSource("opdExtended")
	void isExistOpdNum_shouldReturnTrue_whenOpdNumExistsInVisitYear(boolean opdExtended) throws Exception {
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
	void isExistOpdNum_shouldReturnTrue_whenOpdNumExistsAndVisitYearIsZero(boolean opdExtended) throws Exception {
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
	void isExistOpdNum_shouldReturnFalse_whenOpdNumExistsInAnotherYear(boolean opdExtended) throws Exception {
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
	void getLastOpd_shouldReturnLastOpdOfPatient(boolean opdExtended) throws Exception {
		GeneralData.OPDEXTENDED = opdExtended;
		int code = setupTestOpd(false);
		Opd foundOpd = opdIoOperationRepository.findById(code).orElse(null);
		assertThat(foundOpd).isNotNull();
		Opd lastOpd = opdIoOperation.getLastOpd(foundOpd.getPatient().getCode());
		assertThat(lastOpd.getCode()).isEqualTo(foundOpd.getCode());
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
