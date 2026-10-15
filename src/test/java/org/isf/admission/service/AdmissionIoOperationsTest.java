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
package org.isf.admission.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.admission.TestAdmission;
import org.isf.admission.manager.AdmissionBrowserManager;
import org.isf.admission.model.Admission;
import org.isf.admission.model.AdmittedPatient;
import org.isf.admtype.TestAdmissionType;
import org.isf.admtype.model.AdmissionType;
import org.isf.admtype.service.AdmissionTypeIoOperationRepository;
import org.isf.disctype.TestDischargeType;
import org.isf.disctype.model.DischargeType;
import org.isf.disctype.service.DischargeTypeIoOperationRepository;
import org.isf.disease.TestDisease;
import org.isf.disease.model.Disease;
import org.isf.disease.service.DiseaseIoOperationRepository;
import org.isf.distype.TestDiseaseType;
import org.isf.distype.model.DiseaseType;
import org.isf.distype.service.DiseaseTypeIoOperationRepository;
import org.isf.dlvrrestype.TestDeliveryResultType;
import org.isf.dlvrrestype.model.DeliveryResultType;
import org.isf.dlvrrestype.service.DeliveryResultIoOperationRepository;
import org.isf.dlvrtype.TestDeliveryType;
import org.isf.dlvrtype.model.DeliveryType;
import org.isf.dlvrtype.service.DeliveryTypeIoOperationRepository;
import org.isf.generaldata.GeneralData;
import org.isf.operation.TestOperation;
import org.isf.operation.model.Operation;
import org.isf.operation.service.OperationIoOperationRepository;
import org.isf.opetype.TestOperationType;
import org.isf.opetype.model.OperationType;
import org.isf.opetype.service.OperationTypeIoOperationRepository;
import org.isf.patient.TestPatient;
import org.isf.patient.model.Patient;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.pregtreattype.TestPregnantTreatmentType;
import org.isf.pregtreattype.model.PregnantTreatmentType;
import org.isf.pregtreattype.service.PregnantTreatmentTypeIoOperationRepository;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.pagination.PagedResponse;
import org.isf.utils.time.TimeTools;
import org.isf.ward.TestWard;
import org.isf.ward.model.Ward;
import org.isf.ward.service.WardIoOperationRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

class AdmissionIoOperationsTest extends OHCoreTestCase {

	private static TestAdmission testAdmission;
	private static TestWard testWard;
	private static TestPatient testPatient;
	private static TestAdmissionType testAdmissionType;
	private static TestDiseaseType testDiseaseType;
	private static TestDisease testDisease;
	private static TestOperationType testOperationType;
	private static TestOperation testOperation;
	private static TestDischargeType testDischargeType;
	private static TestPregnantTreatmentType testPregnantTreatmentType;
	private static TestDeliveryType testDeliveryType;
	private static TestDeliveryResultType testDeliveryResultType;

	@Autowired
	AdmissionIoOperations admissionIoOperation;
	@Autowired
	AdmissionIoOperationRepository admissionIoOperationRepository;
	@Autowired
	AdmissionBrowserManager admissionBrowserManager;
	@Autowired
	WardIoOperationRepository wardIoOperationRepository;
	@Autowired
	PatientIoOperationRepository patientIoOperationRepository;
	@Autowired
	AdmissionTypeIoOperationRepository admissionTypeIoOperationRepository;
	@Autowired
	DiseaseTypeIoOperationRepository diseaseTypeIoOperationRepository;
	@Autowired
	DiseaseIoOperationRepository diseaseIoOperationRepository;
	@Autowired
	OperationTypeIoOperationRepository operationTypeIoOperationRepository;
	@Autowired
	OperationIoOperationRepository operationIoOperationRepository;
	@Autowired
	DischargeTypeIoOperationRepository dischargeTypeIoOperationRepository;
	@Autowired
	PregnantTreatmentTypeIoOperationRepository pregnantTreatmentTypeIoOperationRepository;
	@Autowired
	DeliveryTypeIoOperationRepository deliveryTypeIoOperationRepository;
	@Autowired
	DeliveryResultIoOperationRepository deliveryResultIoOperationRepository;

	@BeforeAll
	static void setUpClass() {
		GeneralData.PATIENTPHOTOSTORAGE = "DB";
		testAdmission = new TestAdmission();
		testWard = new TestWard();
		testPatient = new TestPatient();
		testAdmissionType = new TestAdmissionType();
		testDiseaseType = new TestDiseaseType();
		testDisease = new TestDisease();
		testOperationType = new TestOperationType();
		testOperation = new TestOperation();
		testDischargeType = new TestDischargeType();
		testPregnantTreatmentType = new TestPregnantTreatmentType();
		testDeliveryType = new TestDeliveryType();
		testDeliveryResultType = new TestDeliveryResultType();
	}

	static Collection<Object[]> maternityRestartInJune() {
		return Arrays.asList(new Object[][] {
			{ false },
			{ true }
		});
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void getAdmittedPatients_shouldReturnPatientWithAdmission_whenNoSearchTerms(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		int id = setupTestAdmission(false);
		Admission foundAdmission = admissionIoOperation.getAdmission(id);
		List<AdmittedPatient> patients = admissionIoOperation.getAdmittedPatients();
		List<AdmittedPatient> searchResult = admissionIoOperation.getAdmittedPatients(null);
		assertThat(searchResult).hasSameSizeAs(patients);
		assertThat(patients.get(0).getAdmission().getId()).isEqualTo(foundAdmission.getId());
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void getAdmittedPatients_shouldFilterByAdmissionAndDischargeDateRanges(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		int id = setupTestAdmission(false);
		Admission foundAdmission = admissionIoOperation.getAdmission(id);
		List<AdmittedPatient> patients = admissionIoOperation.getAdmittedPatients();
		LocalDateTime admissionDate = foundAdmission.getAdmDate();
		LocalDateTime dischargeDate = foundAdmission.getDisDate();
		List<AdmittedPatient> searchResult = admissionIoOperation.getAdmittedPatients(null, null, null);
		assertThat(searchResult).hasSameSizeAs(patients);
		assertThat(patients.get(0).getAdmission().getId()).isEqualTo(foundAdmission.getId());
		LocalDateTime beforeAdmissionDate = admissionDate.minusDays(1);

		LocalDateTime oneDayAfterAdmissionDate = admissionDate.plusDays(1);

		LocalDateTime twoDaysAfterAdmissionDate = admissionDate.plusDays(2);

		LocalDateTime beforeDischargeDate = dischargeDate.minusDays(1);

		LocalDateTime oneDayAfterDischargeDate = dischargeDate.plusDays(1);

		LocalDateTime twoDaysAfterDischargeDate = dischargeDate.plusDays(2);

		// search by admission date
		List<AdmittedPatient> searchOneresult = admissionIoOperation.getAdmittedPatients(null,
			new LocalDateTime[] { beforeAdmissionDate, oneDayAfterAdmissionDate },
			null);
		assertThat(searchOneresult).hasSameSizeAs(patients);
		assertThat(patients.get(0).getAdmission().getId()).isEqualTo(foundAdmission.getId());

		List<AdmittedPatient> searchTwoResult = admissionIoOperation.getAdmittedPatients(null,
			new LocalDateTime[] { oneDayAfterAdmissionDate, twoDaysAfterAdmissionDate },
			null);
		assertThat(searchTwoResult).isEmpty();

		// search by discharge date
		searchOneresult = admissionIoOperation.getAdmittedPatients(null, null,
			new LocalDateTime[] { beforeDischargeDate, oneDayAfterDischargeDate });
		assertThat(searchOneresult).hasSameSizeAs(patients);
		assertThat(patients.get(0).getAdmission().getId()).isEqualTo(foundAdmission.getId());

		searchTwoResult = admissionIoOperation.getAdmittedPatients(null, null,
			new LocalDateTime[] { oneDayAfterDischargeDate, twoDaysAfterDischargeDate });
		assertThat(searchTwoResult).isEmpty();

		// complex search by both admission and discharge date
		searchOneresult = admissionIoOperation.getAdmittedPatients(null,
			new LocalDateTime[] { beforeAdmissionDate, oneDayAfterAdmissionDate },
			new LocalDateTime[] { beforeDischargeDate, oneDayAfterDischargeDate });
		assertThat(searchOneresult).hasSameSizeAs(patients);
		assertThat(patients.get(0).getAdmission().getId()).isEqualTo(foundAdmission.getId());
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void getAdmittedPatients_shouldReturnAllPatients_whenSearchTermsIsNull(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		int id = setupTestAdmission(false);
		Admission foundAdmission = admissionIoOperation.getAdmission(id);
		List<AdmittedPatient> patients = admissionIoOperation.getAdmittedPatients();
		List<AdmittedPatient> patientsNull = admissionIoOperation.getAdmittedPatients(null);
		assertThat(patientsNull).hasSameSizeAs(patients);
		assertThat(patients.get(0).getAdmission().getId()).isEqualTo(foundAdmission.getId());
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void getAdmittedPatients_shouldFindAdmittedPatient_whenSearchTermsIsFirstName(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		// given:
		int id = setupTestAdmission(false);
		Admission foundAdmission = admissionIoOperation.getAdmission(id);
		Patient foundPatient = foundAdmission.getPatient();

		// when:
		List<AdmittedPatient> patients = admissionIoOperation.getAdmittedPatients(foundPatient.getFirstName());

		// then:
		assertThat(patients.get(0).getAdmission().getId()).isEqualTo(foundAdmission.getId());
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void getAdmissionsByAdmissionDate_shouldFindAdmission_whenRangeContainsAdmissionDate(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		// given:
		int id = setupTestAdmission(false);
		String str = "2000-01-01T10:11:30";
		String str2 = "2023-05-05T10:11:30";
		LocalDateTime dateFrom = LocalDateTime.parse(str);
		LocalDateTime dateTo = LocalDateTime.parse(str2);
		int page = 0;
		int size = 10;
		Admission foundAdmission = admissionIoOperation.getAdmission(id);
		// when:
		List<Admission> patients = admissionIoOperation.getAdmissionsByAdmissionDate(dateFrom, dateTo);

		// then:
		assertThat(patients.get(0).getId()).isEqualTo(foundAdmission.getId());
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void getAdmissionsByAdmissionDate_shouldReturnAdmission_whenAdmissionDateInRange(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		// given:
		int id = setupTestAdmission(false);
		String str = "2000-01-01T10:11:30";
		String str2 = "2023-05-05T10:11:30";
		LocalDateTime dateFrom = LocalDateTime.parse(str);
		LocalDateTime dateTo = LocalDateTime.parse(str2);
		Admission foundAdmission = admissionIoOperation.getAdmission(id);
		// when:
		List<Admission> admissions = admissionIoOperation.getAdmissionsByAdmissionDate(dateFrom, dateTo);

		// then:
		assertThat(admissions.get(0).getId()).isEqualTo(foundAdmission.getId());
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void getAdmissionsByDischargeDates_shouldReturnPageWithAdmission_whenDischargeDateInRange(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		// given:
		int id = setupTestAdmission(false);
		String str = "2000-01-01T10:11:30";
		String str2 = "2023-05-05T10:11:30";
		LocalDateTime dateFrom = LocalDateTime.parse(str);
		LocalDateTime dateTo = LocalDateTime.parse(str2);
		int page = 0;
		int size = 10;
		Admission foundAdmission = admissionIoOperation.getAdmission(id);
		// when:
		PagedResponse<Admission> patients = admissionIoOperation.getAdmissionsByDischargeDates(dateFrom, dateTo, PageRequest.of(page, size));

		// then:
		assertThat(patients.getData().get(0).getId()).isEqualTo(foundAdmission.getId());
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void getAdmittedPatients_shouldFindAdmittedPatient_whenSearchTermsIsFullName(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		// given:
		int id = setupTestAdmission(false);
		Admission foundAdmission = admissionIoOperation.getAdmission(id);
		Patient foundPatient = foundAdmission.getPatient();

		// when:
		List<AdmittedPatient> patients = admissionIoOperation.getAdmittedPatients(foundPatient.getName());

		// then:
		assertThat(patients.get(0).getAdmission().getId()).isEqualTo(foundAdmission.getId());
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void getAdmittedPatients_shouldFindAdmittedPatient_whenSearchTermsIsNullNote(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		// given:
		int id = setupTestAdmission(false);
		Admission foundAdmission = admissionIoOperation.getAdmission(id);
		Patient foundPatient = foundAdmission.getPatient();

		// when:
		List<AdmittedPatient> patients = admissionIoOperation.getAdmittedPatients(foundPatient.getNote());

		// then:
		assertThat(patients.get(0).getAdmission().getId()).isEqualTo(foundAdmission.getId());
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void getAdmittedPatients_shouldFindAdmittedPatient_whenSearchTermsIsTaxCode(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		// given:
		int id = setupTestAdmission(false);
		Admission foundAdmission = admissionIoOperation.getAdmission(id);
		Patient foundPatient = foundAdmission.getPatient();

		// when:
		List<AdmittedPatient> patients = admissionIoOperation.getAdmittedPatients(foundPatient.getTaxCode());

		// then:
		assertThat(patients.get(0).getAdmission().getId()).isEqualTo(foundAdmission.getId());
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void getAdmittedPatients_shouldFindAdmittedPatient_whenSearchTermsIsPatientCode(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		// given:
		int id = setupTestAdmission(false);
		Admission foundAdmission = admissionIoOperation.getAdmission(id);
		Patient foundPatient = foundAdmission.getPatient();

		// when:
		List<AdmittedPatient> patients = admissionIoOperation.getAdmittedPatients(foundPatient.getCode().toString());

		// then:
		assertThat(patients.get(0).getAdmission().getId()).isEqualTo(foundAdmission.getId());
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void getAdmittedPatients_shouldReturnEmpty_whenSearchTermsMatchNoPatient(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		// given:
		int id = setupTestAdmission(false);
		Admission foundAdmission = admissionIoOperation.getAdmission(id);

		// when:
		List<AdmittedPatient> patients = admissionIoOperation.getAdmittedPatients("dupsko");

		// then:
		assertThat(patients).isEmpty();
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void getAdmittedPatients_shouldReturnEmpty_whenAdmissionDateOutsideRange(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		// given:
		int id = setupTestAdmission(false);
		Admission foundAdmission = admissionIoOperation.getAdmission(id);
		Patient foundPatient = foundAdmission.getPatient();
		LocalDateTime[] admissionRange = {
			foundAdmission.getAdmDate().minusDays(2),
			foundAdmission.getAdmDate().minusDays(1)
		};
		LocalDateTime[] dischargeRange = {
			foundAdmission.getDisDate().minusDays(1),
			foundAdmission.getDisDate().plusDays(1)
		};

		// when:
		List<AdmittedPatient> patients = admissionIoOperation.getAdmittedPatients(null, admissionRange, dischargeRange);

		// then:
		assertThat(patients).isEmpty();
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void getAdmittedPatients_shouldReturnEmpty_whenDischargeDateOutsideRange(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		// given:
		int id = setupTestAdmission(false);
		Admission foundAdmission = admissionIoOperation.getAdmission(id);
		Patient foundPatient = foundAdmission.getPatient();
		LocalDateTime[] admissionRange = {
			foundAdmission.getAdmDate().minusDays(1),
			foundAdmission.getAdmDate().plusDays(1)
		};
		LocalDateTime[] dischargeRange = {
			foundAdmission.getDisDate().minusDays(2),
			foundAdmission.getDisDate().minusDays(1)
		};

		// when:
		List<AdmittedPatient> patients = admissionIoOperation.getAdmittedPatients(null, admissionRange, dischargeRange);

		// then:
		assertThat(patients).isEmpty();
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void getCurrentAdmission_shouldReturnPatientAdmission(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		int id = setupTestAdmission(false);
		Admission foundAdmission = admissionIoOperation.getAdmission(id);
		foundAdmission.setDisDate(null);
		Admission ioAdmission = admissionIoOperation.getCurrentAdmission(foundAdmission.getPatient());
		assertThat(ioAdmission.getNote()).isEqualTo(foundAdmission.getNote());
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void getAdmission_shouldReturnAdmission_whenIdExists(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		int id = setupTestAdmission(false);
		Admission foundAdmission = admissionIoOperation.getAdmission(id);

		testAdmission.check(foundAdmission);
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void getAdmissions_shouldReturnPatientAdmissions(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		int id = setupTestAdmission(false);
		Admission foundAdmission = admissionIoOperation.getAdmission(id);
		List<Admission> admissions = admissionIoOperation.getAdmissions(foundAdmission.getPatient());
		assertThat(admissions.get(admissions.size() - 1).getId()).isEqualTo(foundAdmission.getId());
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void newAdmission_shouldSaveAdmission(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		Admission admission = buildNewAdmission();
		Admission result = admissionIoOperation.newAdmission(admission);
		assertThat(result)
			.isNotNull()
			.isEqualTo(admission);
		admission = admissionBrowserManager.getAdmission(admission.getId());
		testAdmission.check(admission);
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void newAdmissionReturnKey_shouldReturnIdOfSavedAdmission(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		int id = admissionIoOperation.newAdmissionReturnKey(buildNewAdmission());
		Admission admission = admissionBrowserManager.getAdmission(id);
		testAdmission.check(admission);
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void newAdmission_shouldStorePatientSexAndAgeAtAdmissionDate(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		Admission admission = buildNewAdmission();
		Admission result = admissionIoOperation.newAdmission(admission);
		String expectedSex = String.valueOf(admission.getPatient().getSex());
		// the stored age is the patient's age AT the admission date, not the current age
		Integer expectedAge = (int) ChronoUnit.YEARS.between(admission.getPatient().getBirthDate(), admission.getAdmDate().toLocalDate());
		assertThat(result.getSex()).isEqualTo(expectedSex);
		assertThat(result.getAge()).isEqualTo(expectedAge);
		// reload from the DB to confirm the snapshot is persisted on the admission itself
		Admission reloaded = admissionBrowserManager.getAdmission(result.getId());
		assertThat(reloaded.getSex()).isEqualTo(expectedSex);
		assertThat(reloaded.getAge()).isEqualTo(expectedAge);
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void newAdmission_shouldStoreRecordedAge_whenBirthDateIsMissing(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		Admission admission = buildNewAdmission();
		// patients are often registered by age alone, with no birth date; the snapshot must still be usable
		Patient patient = admission.getPatient();
		patient.setBirthDate(null);
		patient.setAge(7);

		Admission result = admissionIoOperation.newAdmission(admission);

		assertThat(result.getAge()).isEqualTo(7);
		Admission reloaded = admissionBrowserManager.getAdmission(result.getId());
		assertThat(reloaded.getAge()).isEqualTo(7);
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void updateAdmission_shouldSaveNewNote(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		int id = setupTestAdmission(false);
		Admission foundAdmission = admissionIoOperation.getAdmission(id);
		foundAdmission.setNote("Update");
		Admission result = admissionIoOperation.updateAdmission(foundAdmission);
		assertThat(result).isNotNull();
		Admission updateAdmission = admissionIoOperation.getAdmission(id);
		assertThat(updateAdmission).isNotNull();
		assertThat(updateAdmission.getNote()).isEqualTo("Update");
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void getAdmissionType_shouldReturnSavedAdmissionType(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		int id = setupTestAdmission(false);
		Admission foundAdmission = admissionIoOperation.getAdmission(id);
		List<AdmissionType> admissionTypes = admissionIoOperation.getAdmissionType();
		assertThat(admissionTypes.get(admissionTypes.size() - 1).getDescription()).isEqualTo(foundAdmission.getAdmType().getDescription());
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void getDischargeType_shouldReturnSavedDischargeType(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		int id = setupTestAdmission(false);
		Admission foundAdmission = admissionIoOperation.getAdmission(id);
		List<DischargeType> dischargeTypes = admissionIoOperation.getDischargeType();
		assertThat(dischargeTypes.get(dischargeTypes.size() - 1).getDescription()).isEqualTo(foundAdmission.getDisType().getDescription());
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void getNextYProg_shouldReturnLastYProgPlusOne(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		int id = setupTestAdmission(false);
		Admission foundAdmission = admissionIoOperation.getAdmission(id);
		foundAdmission.setYProg(99);
		foundAdmission.setAdmDate(TimeTools.getNow());
		admissionIoOperation.updateAdmission(foundAdmission);
		foundAdmission = admissionIoOperation.getAdmission(id);
		int next = admissionIoOperation.getNextYProg(foundAdmission.getWard().getCode());

		assertThat(next).isEqualTo(foundAdmission.getYProg() + 1);
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void setDeleted_shouldMarkAdmissionDeleted(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		int id = setupTestAdmission(false);
		Admission admission = admissionIoOperation.getAdmission(id);
		Admission deletedAdmission = admissionIoOperation.setDeleted(admission.getId());
		assertThat(deletedAdmission).isNotNull();
		assertThat(deletedAdmission.getDeleted()).isEqualTo('Y');
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void setDeleted_shouldSetDeletedDate(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		int id = setupTestAdmission(false);
		assertThat(admissionIoOperation.getAdmission(id).getDeletedDate()).isNull();

		Admission deleted = admissionIoOperation.setDeleted(id);
		assertThat(deleted).isNotNull();
		assertThat(deleted.getDeleted()).isEqualTo('Y');
		assertThat(deleted.getDeletedDate()).isNotNull();
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void setDeleted_shouldReturnNull_whenAdmissionNotFound(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		setupTestAdmission(false);
		Admission deletedAdmission = admissionIoOperation.setDeleted(-999999);
		assertThat(deletedAdmission).isNull();
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void getUsedWardBed_shouldCountAdmittedPatientsInWard(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		int id = setupTestAdmission(false);
		Admission foundAdmission = admissionIoOperation.getAdmission(id);
		int result = admissionIoOperation.getUsedWardBed(foundAdmission.getWard().getCode());
		assertThat(result).isEqualTo(1);
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	@Transactional
		// requires active session because of lazy loading of patient photo
	void deletePatientPhoto_shouldLeavePhotoNull_whenPatientHasNoPhoto(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		int id = setupTestAdmission(false);
		Admission admission = admissionIoOperation.getAdmission(id);
		Patient updatedPatient = admissionIoOperation.deletePatientPhoto(admission.getPatient().getCode());
		assertThat(updatedPatient).isNotNull();
		assertThat(updatedPatient.getPatientProfilePhoto().getPhoto()).isNull();
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	@Transactional
	// requires active session because of lazy loading of patient photo
	void deletePatientPhoto_shouldReturnNull_whenPatientNotFound(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		Patient deletedPatient = admissionIoOperation.deletePatientPhoto(-99999);
		assertThat(deletedPatient).isNull();
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void loadAdmittedPatient_shouldReturnNull_whenPatientNotFound(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		assertThat(admissionIoOperation.loadAdmittedPatient(-1)).isNull();
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void loadAdmittedPatient_shouldReturnAdmittedPatient_whenPatientExists(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		int id = setupTestAdmission(false);
		Admission admission = admissionIoOperation.getAdmission(id);
		assertThat(admission).isNotNull();
		assertThat(admissionIoOperation.loadAdmittedPatient(admission.getPatient().getCode())).isNotNull();
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void getNextYProg_shouldReturnLastYProgPlusOne_whenMaternityWardBeforeJune(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		// set date before June
		AdmissionIoOperations.testing = true;
		AdmissionIoOperations.afterJune = false;

		int id = setupTestAdmission(false, true);
		Admission foundAdmission = admissionIoOperation.getAdmission(id);
		foundAdmission.setYProg(99);
		foundAdmission.setAdmDate(AdmissionIoOperations.getNow());
		admissionIoOperation.updateAdmission(foundAdmission);
		foundAdmission = admissionIoOperation.getAdmission(id);

		int next = admissionIoOperation.getNextYProg(foundAdmission.getWard().getCode());

		assertThat(next).isEqualTo(foundAdmission.getYProg() + 1);
		// reset
		AdmissionIoOperations.testing = false;
		AdmissionIoOperations.afterJune = false;
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void getNextYProg_shouldReturnLastYProgPlusOne_whenMaternityWardAfterJune(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		// set date after June
		AdmissionIoOperations.testing = true;
		AdmissionIoOperations.afterJune = true;

		int id = setupTestAdmission(false, true);
		Admission foundAdmission = admissionIoOperation.getAdmission(id);
		foundAdmission.setYProg(99);
		foundAdmission.setAdmDate(AdmissionIoOperations.getNow());
		admissionIoOperation.updateAdmission(foundAdmission);
		foundAdmission = admissionIoOperation.getAdmission(id);

		int next = admissionIoOperation.getNextYProg(foundAdmission.getWard().getCode());
		assertThat(next).isEqualTo(foundAdmission.getYProg() + 1);
		// reset
		AdmissionIoOperations.testing = false;
		AdmissionIoOperations.afterJune = false;
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void countAllActiveAdmissions_shouldCountSavedAdmission(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		setupTestAdmission(false);
		long count = admissionIoOperation.countAllActiveAdmissions();
		assertThat(count).isEqualTo(1);
	}

	private int setupTestAdmission(boolean usingSet) throws OHException, InterruptedException, OHServiceException {
		return setupTestAdmission(usingSet, false);
	}

	private int setupTestAdmission(boolean usingSet, boolean maternity) throws OHException, InterruptedException, OHServiceException {
		Ward ward = testWard.setup(false, maternity);
		Patient patient = testPatient.setup(false);
		AdmissionType admissionType = testAdmissionType.setup(false);
		DiseaseType diseaseType = testDiseaseType.setup(false);
		Disease diseaseIn = testDisease.setup(diseaseType, true, false, false, false);
		Disease diseaseOut1 = testDisease.setup(diseaseType, false, true, false, false);
		diseaseOut1.setCode("888");
		Disease diseaseOut2 = testDisease.setup(diseaseType, false, true, false, false);
		diseaseOut2.setCode("777");
		Disease diseaseOut3 = testDisease.setup(diseaseType, false, true, false, false);
		diseaseOut3.setCode("666");
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, false);
		DischargeType dischargeType = testDischargeType.setup(false);
		PregnantTreatmentType pregTreatmentType = testPregnantTreatmentType.setup(false);
		DeliveryType deliveryType = testDeliveryType.setup(false);
		DeliveryResultType deliveryResult = testDeliveryResultType.setup(false);

		Admission admission = testAdmission.setup(ward, patient, admissionType, diseaseIn, diseaseOut1,
			diseaseOut2, diseaseOut3, operation, dischargeType, pregTreatmentType,
			deliveryType, deliveryResult, usingSet);

		wardIoOperationRepository.saveAndFlush(ward);
		patientIoOperationRepository.saveAndFlush(patient);
		admissionTypeIoOperationRepository.saveAndFlush(admissionType);
		diseaseTypeIoOperationRepository.saveAndFlush(diseaseType);
		diseaseIoOperationRepository.saveAndFlush(diseaseIn);
		diseaseIoOperationRepository.saveAndFlush(diseaseOut1);
		diseaseIoOperationRepository.saveAndFlush(diseaseOut2);
		diseaseIoOperationRepository.saveAndFlush(diseaseOut3);
		operationTypeIoOperationRepository.saveAndFlush(operationType);
		operationIoOperationRepository.saveAndFlush(operation);
		dischargeTypeIoOperationRepository.saveAndFlush(dischargeType);
		pregnantTreatmentTypeIoOperationRepository.saveAndFlush(pregTreatmentType);
		deliveryTypeIoOperationRepository.saveAndFlush(deliveryType);
		deliveryResultIoOperationRepository.saveAndFlush(deliveryResult);
		Admission savedAdmission = admissionIoOperation.newAdmission(admission);
		return savedAdmission.getId();
	}

	// Typically used to build a second admission record thus the need to set new codes because of database key values
	private Admission buildNewAdmission() throws Exception {
		Ward ward = testWard.setup(false);
		Patient patient = testPatient.setup(true);
		AdmissionType admissionType = testAdmissionType.setup(false);
		DiseaseType diseaseType = testDiseaseType.setup(false);
		Disease diseaseIn = testDisease.setup(diseaseType, true, false, false, false);
		Disease diseaseOut1 = testDisease.setup(diseaseType, false, true, false, false);
		diseaseOut1.setCode("888");
		Disease diseaseOut2 = testDisease.setup(diseaseType, false, true, false, false);
		diseaseOut2.setCode("777");
		Disease diseaseOut3 = testDisease.setup(diseaseType, false, true, false, false);
		diseaseOut3.setCode("666");
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, false);
		DischargeType dischargeType = testDischargeType.setup(false);
		PregnantTreatmentType pregTreatmentType = testPregnantTreatmentType.setup(false);
		DeliveryType deliveryType = testDeliveryType.setup(false);
		DeliveryResultType deliveryResult = testDeliveryResultType.setup(false);

		ward.setCode("A");
		diseaseIn.setCode("555");
		diseaseOut1.setCode("889");
		diseaseOut2.setCode("778");
		diseaseOut3.setCode("667");
		operation.setCode("9999");
		wardIoOperationRepository.saveAndFlush(ward);
		patientIoOperationRepository.saveAndFlush(patient);
		admissionTypeIoOperationRepository.saveAndFlush(admissionType);
		diseaseTypeIoOperationRepository.saveAndFlush(diseaseType);
		diseaseIoOperationRepository.saveAndFlush(diseaseIn);
		diseaseIoOperationRepository.saveAndFlush(diseaseOut1);
		diseaseIoOperationRepository.saveAndFlush(diseaseOut2);
		diseaseIoOperationRepository.saveAndFlush(diseaseOut3);
		operationTypeIoOperationRepository.saveAndFlush(operationType);
		operationIoOperationRepository.saveAndFlush(operation);
		dischargeTypeIoOperationRepository.saveAndFlush(dischargeType);
		pregnantTreatmentTypeIoOperationRepository.saveAndFlush(pregTreatmentType);
		deliveryTypeIoOperationRepository.saveAndFlush(deliveryType);
		deliveryResultIoOperationRepository.saveAndFlush(deliveryResult);

		return testAdmission.setup(ward, patient, admissionType, diseaseIn, diseaseOut1,
			diseaseOut2, diseaseOut3, operation, dischargeType, pregTreatmentType,
			deliveryType, deliveryResult, true);
	}

}
