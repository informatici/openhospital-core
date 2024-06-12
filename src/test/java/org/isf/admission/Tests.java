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
package org.isf.admission;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.assertj.core.api.Condition;
import org.isf.OHCoreTestCase;
import org.isf.admission.manager.AdmissionBrowserManager;
import org.isf.admission.model.Admission;
import org.isf.admission.model.AdmittedPatient;
import org.isf.admission.service.AdmissionIoOperationRepository;
import org.isf.admission.service.AdmissionIoOperationRepositoryCustom;
import org.isf.admission.service.AdmissionIoOperationRepositoryCustom.PatientAdmission;
import org.isf.admission.service.AdmissionIoOperations;
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
import org.isf.patient.manager.PatientBrowserManager;
import org.isf.patient.model.Patient;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.pregtreattype.TestPregnantTreatmentType;
import org.isf.pregtreattype.model.PregnantTreatmentType;
import org.isf.pregtreattype.service.PregnantTreatmentTypeIoOperationRepository;
import org.isf.utils.exception.OHDataValidationException;
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

class Tests extends OHCoreTestCase {

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
	PatientBrowserManager patientBrowserManager;
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
	void testAdmissionGets(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		int id = setupTestAdmission(false);
		checkAdmissionIntoDb(id);
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void testAdmissionSets(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		int id = setupTestAdmission(true);
		checkAdmissionIntoDb(id);
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void testSimpleGetAdmittedPatients(boolean maternityRestartInJune) throws Exception {
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
	void testGetAdmittedPatientWithDateRanges(boolean maternityRestartInJune) throws Exception {
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
	void testIoGetAdmittedPatients(boolean maternityRestartInJune) throws Exception {
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
	void testIoGetAdmittedPatientsShouldFindByOneOfFieldsLikeFirstName(boolean maternityRestartInJune) throws Exception {
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
	void testIoGetAdmissionsByAdmissionDatePageable(boolean maternityRestartInJune) throws Exception {
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
	void testIoGetAdmissionsByAdmDate(boolean maternityRestartInJune) throws Exception {
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
	void testIoGetDischargesByDatePageable(boolean maternityRestartInJune) throws Exception {
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
	void testIoGetAdmittedPatientsShouldFindByOneOfFieldsLikeLastName(boolean maternityRestartInJune) throws Exception {
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
	void testIoGetAdmittedPatientsShouldFindByOneOfFieldsLikeNote(boolean maternityRestartInJune) throws Exception {
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
	void testIoGetAdmittedPatientsShouldFindByOneOfFieldsLikeTaxCode(boolean maternityRestartInJune) throws Exception {
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
	void testIoGetAdmittedPatientsShouldFindByOneOfFieldsLikeId(boolean maternityRestartInJune) throws Exception {
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
	void testIoGetAdmittedPatientsShouldNotFindAnythingWhenNotExistingWordProvided(boolean maternityRestartInJune) throws Exception {
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
	void testIoGetAdmittedPatientsShouldNotFindWhenAdmissionOutsideOfDateRange(boolean maternityRestartInJune) throws Exception {
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
	void testIoGetAdmittedPatientsShouldNotFindWhenDischargeOutsideOfDateRange(boolean maternityRestartInJune) throws Exception {
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
	void testIoGetCurrentAdmission(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		int id = setupTestAdmission(false);
		Admission foundAdmission = admissionIoOperation.getAdmission(id);
		foundAdmission.setDisDate(null);
		Admission ioAdmission = admissionIoOperation.getCurrentAdmission(foundAdmission.getPatient());
		assertThat(ioAdmission.getNote()).isEqualTo(foundAdmission.getNote());
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void testIoGetAdmission(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		int id = setupTestAdmission(false);
		Admission foundAdmission = admissionIoOperation.getAdmission(id);

		testAdmission.check(foundAdmission);
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void testIoGetAdmissions(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		int id = setupTestAdmission(false);
		Admission foundAdmission = admissionIoOperation.getAdmission(id);
		List<Admission> admissions = admissionIoOperation.getAdmissions(foundAdmission.getPatient());
		assertThat(admissions.get(admissions.size() - 1).getId()).isEqualTo(foundAdmission.getId());
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void testIoNewAdmission(boolean maternityRestartInJune) throws Exception {
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
	void testIoNewAdmissionReturnKey(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		int id = admissionIoOperation.newAdmissionReturnKey(buildNewAdmission());
		Admission admission = admissionBrowserManager.getAdmission(id);
		testAdmission.check(admission);
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void testIoUpdateAdmission(boolean maternityRestartInJune) throws Exception {
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
	void testIoGetAdmissionType(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		int id = setupTestAdmission(false);
		Admission foundAdmission = admissionIoOperation.getAdmission(id);
		List<AdmissionType> admissionTypes = admissionIoOperation.getAdmissionType();
		assertThat(admissionTypes.get(admissionTypes.size() - 1).getDescription()).isEqualTo(foundAdmission.getAdmType().getDescription());
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void testIoGetDischargeType(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		int id = setupTestAdmission(false);
		Admission foundAdmission = admissionIoOperation.getAdmission(id);
		List<DischargeType> dischargeTypes = admissionIoOperation.getDischargeType();
		assertThat(dischargeTypes.get(dischargeTypes.size() - 1).getDescription()).isEqualTo(foundAdmission.getDisType().getDescription());
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void testIoGetNextYProg(boolean maternityRestartInJune) throws Exception {
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
	void testIoSetDeleted(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		int id = setupTestAdmission(false);
		Admission admission = admissionIoOperation.getAdmission(id);
		Admission deletedAdmission = admissionIoOperation.setDeleted(admission.getId());
		assertThat(deletedAdmission).isNotNull();
		assertThat(deletedAdmission.getDeleted()).isEqualTo('Y');
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void testIoSetDeletedNotFound(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		setupTestAdmission(false);
		Admission deletedAdmission = admissionIoOperation.setDeleted(-999999);
		assertThat(deletedAdmission).isNull();
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void testIoGetUsedWardBed(boolean maternityRestartInJune) throws Exception {
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
	void testIoDeletePatientPhoto(boolean maternityRestartInJune) throws Exception {
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
	void testIoDeletePatientPhotoNoPatient(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		Patient deletedPatient = admissionIoOperation.deletePatientPhoto(-99999);
		assertThat(deletedPatient).isNull();
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void testIoLoadAdmittedPatientNotThere(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		assertThat(admissionIoOperation.loadAdmittedPatient(-1)).isNull();
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void testIoLoadAdmittedPatient(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		int id = setupTestAdmission(false);
		Admission admission = admissionIoOperation.getAdmission(id);
		assertThat(admission).isNotNull();
		assertThat(admissionIoOperation.loadAdmittedPatient(admission.getPatient().getCode())).isNotNull();
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void testIoGetNextYProgMaternityWardBeforeJune(boolean maternityRestartInJune) throws Exception {
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
	void testIoGetNextYProgMaternityWardAfterJune(boolean maternityRestartInJune) throws Exception {
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
	void testAdmissionGettersSetters(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		int id = setupTestAdmission(false);
		Admission foundAdmission = admissionIoOperation.getAdmission(id);
		Patient foundPatient = foundAdmission.getPatient();
		foundAdmission.setPatient(null);
		assertThat(foundAdmission.getPatient()).isNull();
		foundAdmission.setPatient(foundPatient);
		assertThat(foundAdmission.getPatient()).usingRecursiveComparison().isEqualTo(foundPatient);
		int lock = foundAdmission.getLock();
		foundAdmission.setLock(-1);
		assertThat(foundAdmission.getLock()).isEqualTo(-1);
		foundAdmission.setLock(lock);
		assertThat(foundAdmission.getLock()).isEqualTo(lock);

		Admission admission = buildNewAdmission();
		int admissionId = admission.getId();
		admission.setId(-1);
		assertThat(admission.getId()).isEqualTo(-1);
		admission.setId(admissionId);
		assertThat(admission.getId()).isEqualTo(admissionId);
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void testAdmittedPatientGettersSetters(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		int id = setupTestAdmission(false);
		List<AdmittedPatient> patients = admissionIoOperation.getAdmittedPatients();
		assertThat(patients).hasSize(1);
		AdmittedPatient admittedPatient = patients.get(0);
		Patient patient = admittedPatient.getPatient();
		admittedPatient.setPatient(null);
		assertThat(admittedPatient.getPatient()).isNull();
		admittedPatient.setPatient(patient);
		assertThat(admittedPatient.getPatient()).usingRecursiveComparison().isEqualTo(patient);
		Admission admission = admittedPatient.getAdmission();
		admittedPatient.setAdmission(null);
		assertThat(admittedPatient.getAdmission()).isNull();
		admittedPatient.setAdmission(admission);
		assertThat(admittedPatient.getAdmission()).usingRecursiveComparison().isEqualTo(admission);
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void testMgrSimpleGetAdmittedPatients(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		int id = setupTestAdmission(false);
		Admission foundAdmission = admissionIoOperation.getAdmission(id);
		List<AdmittedPatient> patients = admissionIoOperation.getAdmittedPatients();
		List<AdmittedPatient> searchResult = admissionBrowserManager.getAdmittedPatients(null);
		assertThat(searchResult).hasSameSizeAs(patients);
		assertThat(patients.get(0).getAdmission().getId()).isEqualTo(foundAdmission.getId());
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void testMgrGetAdmittedPatientWithDateRanges(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		int id = setupTestAdmission(false);
		Admission foundAdmission = admissionBrowserManager.getAdmission(id);
		List<AdmittedPatient> patients = admissionBrowserManager.getAdmittedPatients();
		LocalDateTime admissionDate = foundAdmission.getAdmDate();
		LocalDateTime dischargeDate = foundAdmission.getDisDate();
		List<AdmittedPatient> searchResult = admissionBrowserManager.getAdmittedPatients(null, null, null);
		assertThat(searchResult).hasSameSizeAs(patients);
		assertThat(patients.get(0).getAdmission().getId()).isEqualTo(foundAdmission.getId());
		LocalDateTime beforeAdmissionDate = admissionDate.minusDays(1);

		LocalDateTime oneDayAfterAdmissionDate = admissionDate.plusDays(1);

		LocalDateTime twoDaysAfterAdmissionDate = admissionDate.plusDays(2);

		LocalDateTime beforeDischargeDate = dischargeDate.minusDays(1);

		LocalDateTime oneDayAfterDischargeDate = dischargeDate.plusDays(1);

		LocalDateTime twoDaysAfterDischargeDate = dischargeDate.plusDays(2);

		// search by admission date
		List<AdmittedPatient> searchOneresult = admissionBrowserManager.getAdmittedPatients(
			new LocalDateTime[] { beforeAdmissionDate, oneDayAfterAdmissionDate }, null, null);
		assertThat(searchOneresult).hasSameSizeAs(patients);
		assertThat(patients.get(0).getAdmission().getId()).isEqualTo(foundAdmission.getId());

		List<AdmittedPatient> searchTwoResult = admissionBrowserManager.getAdmittedPatients(null,
			new LocalDateTime[] { oneDayAfterAdmissionDate, twoDaysAfterAdmissionDate }, null);
		assertThat(searchTwoResult).isEmpty();

		// search by discharge date
		searchOneresult = admissionBrowserManager.getAdmittedPatients(null,
			new LocalDateTime[] { beforeDischargeDate, oneDayAfterDischargeDate }, null);
		assertThat(searchOneresult).hasSameSizeAs(patients);
		assertThat(patients.get(0).getAdmission().getId()).isEqualTo(foundAdmission.getId());

		searchTwoResult = admissionBrowserManager.getAdmittedPatients(
			new LocalDateTime[] { oneDayAfterDischargeDate, twoDaysAfterDischargeDate }, null, null);
		assertThat(searchTwoResult).isEmpty();

		// complex search by both admission and discharge date
		searchOneresult = admissionBrowserManager.getAdmittedPatients(
			new LocalDateTime[] { beforeAdmissionDate, oneDayAfterAdmissionDate },
			new LocalDateTime[] { beforeDischargeDate, oneDayAfterDischargeDate }, null);
		assertThat(searchOneresult).hasSameSizeAs(patients);
		assertThat(patients.get(0).getAdmission().getId()).isEqualTo(foundAdmission.getId());
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void testMgrGetAdmittedPatients(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;

		int id = setupTestAdmission(false);
		Admission foundAdmission = admissionBrowserManager.getAdmission(id);
		List<AdmittedPatient> patients = admissionBrowserManager.getAdmittedPatients();
		List<AdmittedPatient> patientsNull = admissionBrowserManager.getAdmittedPatients(null);
		assertThat(patientsNull).hasSameSizeAs(patients);
		assertThat(patients.get(0).getAdmission().getId()).isEqualTo(foundAdmission.getId());
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void testMgrGetAdmittedPatientsShouldFindByOneOfFieldsLikeFirstName(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		// given:
		int id = setupTestAdmission(false);
		Admission foundAdmission = admissionBrowserManager.getAdmission(id);
		Patient foundPatient = foundAdmission.getPatient();

		// when:
		List<AdmittedPatient> patients = admissionBrowserManager.getAdmittedPatients(foundPatient.getFirstName());

		// then:
		assertThat(patients.get(0).getAdmission().getId()).isEqualTo(foundAdmission.getId());
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void testMgrGetAdmittedPatientsShouldFindByOneOfFieldsLikeLastName(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		// given:
		int id = setupTestAdmission(false);
		Admission foundAdmission = admissionBrowserManager.getAdmission(id);
		Patient foundPatient = foundAdmission.getPatient();

		// when:
		List<AdmittedPatient> patients = admissionBrowserManager.getAdmittedPatients(foundPatient.getName());

		// then:
		assertThat(patients.get(0).getAdmission().getId()).isEqualTo(foundAdmission.getId());
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void testMgrGetAdmittedPatientsShouldFindByOneOfFieldsLikeNote(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		// given:
		int id = setupTestAdmission(false);
		Admission foundAdmission = admissionBrowserManager.getAdmission(id);
		Patient foundPatient = foundAdmission.getPatient();

		// when:
		List<AdmittedPatient> patients = admissionBrowserManager.getAdmittedPatients(foundPatient.getNote());

		// then:
		assertThat(patients.get(0).getAdmission().getId()).isEqualTo(foundAdmission.getId());
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void testMgrGetAdmittedPatientsShouldFindByOneOfFieldsLikeTaxCode(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		// given:
		int id = setupTestAdmission(false);
		Admission foundAdmission = admissionBrowserManager.getAdmission(id);
		Patient foundPatient = foundAdmission.getPatient();

		// when:
		List<AdmittedPatient> patients = admissionBrowserManager.getAdmittedPatients(foundPatient.getTaxCode());

		// then:
		assertThat(patients.get(0).getAdmission().getId()).isEqualTo(foundAdmission.getId());
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void testMgrGetAdmittedPatientsShouldFindByOneOfFieldsLikeId(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		// given:
		int id = setupTestAdmission(false);
		Admission foundAdmission = admissionBrowserManager.getAdmission(id);
		Patient foundPatient = foundAdmission.getPatient();

		// when:
		List<AdmittedPatient> patients = admissionBrowserManager.getAdmittedPatients(foundPatient.getCode().toString());

		// then:
		assertThat(patients.get(0).getAdmission().getId()).isEqualTo(foundAdmission.getId());
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void testMgrGetAdmittedPatientsShouldNotFindAnythingWhenNotExistingWordProvided(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		// given:
		int id = setupTestAdmission(false);
		Admission foundAdmission = admissionBrowserManager.getAdmission(id);

		// when:
		List<AdmittedPatient> patients = admissionBrowserManager.getAdmittedPatients("dupsko");

		// then:
		assertThat(patients).isEmpty();
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void testMgrGetAdmittedPatientsShouldNotFindWhenAdmissionOutsideOfDateRange(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		// given:
		int id = setupTestAdmission(false);
		Admission foundAdmission = admissionBrowserManager.getAdmission(id);
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
		List<AdmittedPatient> patients = admissionBrowserManager.getAdmittedPatients(admissionRange, dischargeRange, null);

		// then:
		assertThat(patients).isEmpty();
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void testMgrGetAdmittedPatientsShouldNotFindWhenDischargeOutsideOfDateRange(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		// given:
		int id = setupTestAdmission(false);
		Admission foundAdmission = admissionBrowserManager.getAdmission(id);
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
		List<AdmittedPatient> patients = admissionBrowserManager.getAdmittedPatients(admissionRange, dischargeRange, null);

		// then:
		assertThat(patients).isEmpty();
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void testMgrLoadAdmittedPatientsNotThere(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		assertThat(admissionBrowserManager.loadAdmittedPatients(-1)).isNull();
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void testMgrGetCurrentAdmission(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		int id = setupTestAdmission(false);
		Admission foundAdmission = admissionBrowserManager.getAdmission(id);
		foundAdmission.setDisDate(null);
		Admission ioAdmission = admissionBrowserManager.getCurrentAdmission(foundAdmission.getPatient());
		assertThat(ioAdmission.getNote()).isEqualTo(foundAdmission.getNote());
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void testMgrGetAdmissionsByPatient(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		int id = setupTestAdmission(false);
		Admission foundAdmission = admissionBrowserManager.getAdmission(id);
		List<Admission> admissions = admissionBrowserManager.getAdmissions(foundAdmission.getPatient());
		assertThat(admissions.get(admissions.size() - 1).getId()).isEqualTo(foundAdmission.getId());
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void testMgrGetNextYProg(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		int id = setupTestAdmission(false);
		Admission foundAdmission = admissionBrowserManager.getAdmission(id);
		foundAdmission.setYProg(99);
		foundAdmission.setAdmDate(TimeTools.getNow());
		admissionIoOperation.updateAdmission(foundAdmission);
		foundAdmission = admissionIoOperation.getAdmission(id);
		int next = admissionBrowserManager.getNextYProg(foundAdmission.getWard().getCode());

		assertThat(next).isEqualTo(foundAdmission.getYProg() + 1);
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void testMgrGetNextYProgMaternityWardBeforeJune(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		// set date before June
		AdmissionIoOperations.testing = true;
		AdmissionIoOperations.afterJune = false;

		int id = setupTestAdmission(false, true);
		Admission foundAdmission = admissionBrowserManager.getAdmission(id);
		foundAdmission.setYProg(99);
		foundAdmission.setAdmDate(AdmissionIoOperations.getNow());
		admissionIoOperation.updateAdmission(foundAdmission);
		foundAdmission = admissionIoOperation.getAdmission(id);
		int next = admissionBrowserManager.getNextYProg(foundAdmission.getWard().getCode());

		assertThat(next).isEqualTo(foundAdmission.getYProg() + 1);
		// reset
		AdmissionIoOperations.testing = false;
		AdmissionIoOperations.afterJune = false;
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void testMgrGetNextYProgMaternityWardAfterJune(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		// set date after June
		AdmissionIoOperations.testing = true;
		AdmissionIoOperations.afterJune = true;

		int id = setupTestAdmission(false, true);
		Admission foundAdmission = admissionBrowserManager.getAdmission(id);
		foundAdmission.setYProg(99);
		foundAdmission.setAdmDate(AdmissionIoOperations.getNow());
		admissionIoOperation.updateAdmission(foundAdmission);
		foundAdmission = admissionIoOperation.getAdmission(id);
		int next = admissionBrowserManager.getNextYProg(foundAdmission.getWard().getCode());
		assertThat(next).isEqualTo(foundAdmission.getYProg() + 1);
		// reset
		AdmissionIoOperations.testing = false;
		AdmissionIoOperations.afterJune = false;
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void testMgrGetAdmissionType(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		int id = setupTestAdmission(false);
		Admission foundAdmission = admissionBrowserManager.getAdmission(id);
		List<AdmissionType> admissionTypes = admissionBrowserManager.getAdmissionType();
		assertThat(admissionTypes.get(admissionTypes.size() - 1).getDescription()).isEqualTo(foundAdmission.getAdmType().getDescription());
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void testMgrGetDischargeType(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		int id = setupTestAdmission(false);
		Admission foundAdmission = admissionBrowserManager.getAdmission(id);
		List<DischargeType> dischargeTypes = admissionBrowserManager.getDischargeType();
		assertThat(dischargeTypes.get(dischargeTypes.size() - 1).getDescription()).isEqualTo(foundAdmission.getDisType().getDescription());
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void testMgrNewAdmission(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		Admission admission = buildNewAdmission();
		Admission result = admissionBrowserManager.newAdmission(admission);
		assertThat(result)
			.isNotNull()
			.isEqualTo(admission);
		admission = admissionBrowserManager.getAdmission(admission.getId());
		testAdmission.check(admission);
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void testMgrNewAdmissionInvalidDate(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		GeneralData.LANGUAGE = "en";

		int id = setupTestAdmission(false);
		Admission record1 = admissionBrowserManager.getAdmission(id);

		Patient patient = patientBrowserManager.getPatientById(record1.getPatient().getCode());
		Admission record2 = buildNewAdmission();
		record2.setPatient(patient);

		record2.setAdmDate(record1.getAdmDate().plusDays(30));

		// inserted patient already admitted
		// invalid admission period
		assertThatThrownBy(() -> admissionBrowserManager.newAdmission(record2))
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								e -> ((OHServiceException) e).getMessages().size() == 2, "Expecting two validation errors"));
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void testMgrNewAdmissionReturnKey(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		GeneralData.LANGUAGE = "en";
		int id = admissionBrowserManager.newAdmissionReturnKey(buildNewAdmission());
		Admission admission = admissionBrowserManager.getAdmission(id);
		testAdmission.check(admission);
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void testMgrUpdateAdmission(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		GeneralData.LANGUAGE = "en";
		Admission admission = buildNewAdmission();
		Admission newAdmission = admissionBrowserManager.newAdmission(admission);
		int id = newAdmission.getId();
		newAdmission.setNote("Update");
		Admission result = admissionBrowserManager.updateAdmission(admission);
		assertThat(result).isNotNull();
		Admission updateAdmission = admissionBrowserManager.getAdmission(id);
		assertThat(updateAdmission).isNotNull();
		assertThat(updateAdmission.getNote()).isEqualTo("Update");
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void testMgrSetDeleted(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		int id = setupTestAdmission(false);
		Admission admission = admissionBrowserManager.getAdmission(id);
		Admission deletedAdmission = admissionBrowserManager.setDeleted(admission.getId());
		assertThat(deletedAdmission).isNotNull();
		assertThat(deletedAdmission.getDeleted()).isEqualTo('Y');
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void testMgrGetUsedWardBed(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		int id = setupTestAdmission(false);
		Admission foundAdmission = admissionBrowserManager.getAdmission(id);
		int result = admissionBrowserManager.getUsedWardBed(foundAdmission.getWard().getCode());
		assertThat(result).isEqualTo(1);
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	@Transactional
		// requires active session because of lazy loading of patient photo
	void testMgrDeletePatientPhoto(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		int id = setupTestAdmission(false);
		Admission admission = admissionBrowserManager.getAdmission(id);
		Patient updatedPatient = admissionBrowserManager.deletePatientPhoto(admission.getPatient().getCode());
		assertThat(updatedPatient).isNotNull();
		assertThat(updatedPatient.getPatientProfilePhoto().getPhoto()).isNull();
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void testMgrValidateWardNotNull(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		int id = setupTestAdmission(false);
		Admission admission = admissionBrowserManager.getAdmission(id);
		GeneralData.LANGUAGE = "en";

		// Ward cannot be null
		admission.setWard(null);
		assertThatThrownBy(() -> admissionBrowserManager.newAdmission(admission))
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error"));
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void testMgrValidateAdmissionDate(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		int id = setupTestAdmission(false);
		Admission admission = admissionBrowserManager.getAdmission(id);
		GeneralData.LANGUAGE = "en";

		// Admission date cannot be null
		admission.setAdmDate(null);
		assertThatThrownBy(() -> admissionBrowserManager.newAdmission(admission))
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error"));
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void testMgrValidateDiseaseIn(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		int id = setupTestAdmission(false);
		Admission admission = admissionBrowserManager.getAdmission(id);
		GeneralData.LANGUAGE = "en";

		// DiseaseIn cannot be null
		admission.setDiseaseIn(null);
		assertThatThrownBy(() -> admissionBrowserManager.updateAdmission(admission))
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error"));
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void testMgrValidateWeight(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		int id = setupTestAdmission(false);
		Admission admission = admissionBrowserManager.getAdmission(id);
		GeneralData.LANGUAGE = "en";

		// Weight cannot be negative
		admission.setWeight(-99.0f);
		assertThatThrownBy(() -> admissionBrowserManager.updateAdmission(admission))
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error"));
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void testMgrValidateMaternityVisitDate(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		int id = setupTestAdmission(false, true);
		Admission admission = admissionBrowserManager.getAdmission(id);
		GeneralData.LANGUAGE = "en";

		admission.setVisitDate(admission.getAdmDate().minusDays(30));
		assertThatThrownBy(() -> admissionBrowserManager.updateAdmission(admission))
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error"));
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void testMgrValidateDeliveryDate(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		int id = setupTestAdmission(false, true);
		Admission admission = admissionBrowserManager.getAdmission(id);
		GeneralData.LANGUAGE = "en";

		admission.setVisitDate(null); // here just to increase code tests coverage
		admission.setDeliveryDate(admission.getAdmDate().minusDays(30));
		assertThatThrownBy(() -> admissionBrowserManager.updateAdmission(admission))
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error"));
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void testMgrValidateCtrl1DateNoDeliveryDate(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		int id = setupTestAdmission(false, true);
		Admission admission = admissionBrowserManager.getAdmission(id);
		GeneralData.LANGUAGE = "en";

		admission.setDeliveryDate(null);
		assertThatThrownBy(() -> admissionBrowserManager.updateAdmission(admission))
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error"));
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void testMgrValidateAbortDate(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		int id = setupTestAdmission(false, true);
		Admission admission = admissionBrowserManager.getAdmission(id);
		GeneralData.LANGUAGE = "en";

		admission.setDisDate(null);
		admission.setAbortDate(admission.getVisitDate().minusDays(30));
		assertThatThrownBy(() -> admissionBrowserManager.updateAdmission(admission))
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								e -> ((OHServiceException) e).getMessages().size() == 2, "Expecting two validation errors"));
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void testMgrValidateAbortDateBeforeVisitDate(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		int id = setupTestAdmission(false, true);
		Admission admission = admissionBrowserManager.getAdmission(id);
		GeneralData.LANGUAGE = "en";

		admission.setCtrlDate1(null);
		admission.setCtrlDate2(null);
		admission.setAbortDate(admission.getVisitDate().minusDays(30));
		assertThatThrownBy(() -> admissionBrowserManager.updateAdmission(admission))
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting one validation error"));
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void testMgrValidateAbortDateAfterLimit(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		int id = setupTestAdmission(false, true);
		Admission admission = admissionBrowserManager.getAdmission(id);
		GeneralData.LANGUAGE = "en";

		admission.setCtrlDate1(null);
		admission.setCtrlDate2(null);
		admission.setAbortDate(admission.getDisDate().plusDays(30));
		assertThatThrownBy(() -> admissionBrowserManager.updateAdmission(admission))
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting one validation error"));
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void testMgrValidateDuplicateDiseases(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		int id = setupTestAdmission(false);
		Admission admission = admissionBrowserManager.getAdmission(id);
		GeneralData.LANGUAGE = "en";

		// Can't duplicate diseases
		admission.setDiseaseOut1(admission.getDiseaseOut2());
		assertThatThrownBy(() -> admissionBrowserManager.updateAdmission(admission))
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error"));
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void testMgrValidate(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		int id = setupTestAdmission(false);
		Admission admission = admissionBrowserManager.getAdmission(id);
		GeneralData.LANGUAGE = "en";

		// Bad progressive id
		admission.setYProg(-1);
		LocalDateTime disDate = admission.getDisDate();
		Disease diseaseOut1 = admission.getDiseaseOut1();
		Disease diseaseOut2 = admission.getDiseaseOut2();
		Disease diseaseOut3 = admission.getDiseaseOut3();
		admission.setDisDate(null);
		admission.setDiseaseOut1(null);
		admission.setDiseaseOut2(null);
		admission.setDiseaseOut3(null);
		assertThatThrownBy(() -> admissionBrowserManager.updateAdmission(admission))
			.isInstanceOf(OHDataValidationException.class)
			.has(
				new Condition<Throwable>(
					e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error"));
		admission.setYProg(0);
		admission.setDisDate(disDate);
		admission.setDiseaseOut1(diseaseOut1);
		admission.setDiseaseOut2(diseaseOut2);
		admission.setDiseaseOut3(diseaseOut3);

		// Admission date future date
		LocalDateTime admDate = admission.getAdmDate();
		disDate = admission.getDisDate();
		admission.setAdmDate(LocalDateTime.of(9999, 1, 1, 0, 0, 0));
		admission.setDisDate(null);
		admission.setDisDate(null);
		admission.setDiseaseOut1(null);
		admission.setDiseaseOut2(null);
		admission.setDiseaseOut3(null);
		assertThatThrownBy(() -> admissionBrowserManager.updateAdmission(admission))
			.isInstanceOf(OHDataValidationException.class)
			.has(
				new Condition<Throwable>(
					e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error"));
		admission.setAdmDate(admDate);
		admission.setDisDate(disDate);
		admission.setDiseaseOut1(diseaseOut1);
		admission.setDiseaseOut2(diseaseOut2);
		admission.setDiseaseOut3(diseaseOut3);

		// Admission DiseaseIn not IpdIn enabled
		Disease diseaseIn = admission.getDiseaseIn();
		Disease disabledDisease = testDisease.setup(diseaseIn.getType(), false); // includeIpdIn = includeIpdOut = includeOpd = false
		disabledDisease.setCode("disabled");
		diseaseIoOperationRepository.saveAndFlush(disabledDisease);
		admission.setDiseaseIn(disabledDisease);
		assertThatThrownBy(() -> admissionBrowserManager.updateAdmission(admission))
			.isInstanceOf(OHDataValidationException.class)
			.has(
				new Condition<Throwable>(
					e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error"));
		admission.setDiseaseIn(diseaseIn);

		// Discharge date is after today
		disDate = admission.getDisDate();
		admission.setDisDate(LocalDateTime.of(9999, 1, 1, 0, 0, 0));
		assertThatThrownBy(() -> admissionBrowserManager.updateAdmission(admission))
			.isInstanceOf(OHDataValidationException.class)
			.has(
				new Condition<Throwable>(
					e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error"));
		admission.setDisDate(disDate);

		// DiseaseOut1() == null && DisDate() != null
		Disease disease = admission.getDiseaseOut1();
		admission.setDiseaseOut1(null);
		assertThatThrownBy(() -> admissionBrowserManager.updateAdmission(admission))
			.isInstanceOf(OHDataValidationException.class);
		admission.setDiseaseOut1(disease);

		// DiseaseOut1() != null && DisDate() == null
		disDate = admission.getDisDate();
		admission.setDisDate(null);
		assertThatThrownBy(() -> admissionBrowserManager.updateAdmission(admission))
			.isInstanceOf(OHDataValidationException.class)
			.has(
				new Condition<Throwable>(
					e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error"));
		admission.setDisDate(disDate);

		// Admission DiseaseOut1 not IpdOut enabled
		Disease diseaseOut = admission.getDiseaseOut1();
		admission.setDiseaseOut1(disabledDisease);
		assertThatThrownBy(() -> admissionBrowserManager.updateAdmission(admission))
			.isInstanceOf(OHDataValidationException.class)
			.has(
				new Condition<Throwable>(
					e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error"));
		admission.setDiseaseOut1(diseaseOut);

		// Admission DiseaseOut2 not IpdOut enabled
		diseaseOut = admission.getDiseaseOut2();
		admission.setDiseaseOut2(disabledDisease);
		assertThatThrownBy(() -> admissionBrowserManager.updateAdmission(admission))
			.isInstanceOf(OHDataValidationException.class)
			.has(
				new Condition<Throwable>(
					e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error"));
		admission.setDiseaseOut2(diseaseOut);

		// Admission DiseaseOut3 not IpdOut enabled
		diseaseOut = admission.getDiseaseOut3();
		admission.setDiseaseOut3(disabledDisease);
		assertThatThrownBy(() -> admissionBrowserManager.updateAdmission(admission))
			.isInstanceOf(OHDataValidationException.class)
			.has(
				new Condition<Throwable>(
					e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error"));
		admission.setDiseaseOut3(diseaseOut);
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void testMgrValidateMaternity(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		int id = setupTestAdmission(false, true);
		Admission admission = admissionBrowserManager.getAdmission(id);
		GeneralData.LANGUAGE = "en";

		// control dates after discharge dates
		LocalDateTime ctrlDate = admission.getCtrlDate1();
		admission.setCtrlDate1(LocalDateTime.of(9999, 1, 1, 0, 0, 0));
		assertThatThrownBy(() -> admissionBrowserManager.updateAdmission(admission))
			.isInstanceOf(OHDataValidationException.class);
		admission.setCtrlDate1(ctrlDate);
		ctrlDate = admission.getCtrlDate2();
		admission.setCtrlDate2(LocalDateTime.of(9999, 1, 1, 0, 0, 0));
		assertThatThrownBy(() -> admissionBrowserManager.updateAdmission(admission))
			.isInstanceOf(OHDataValidationException.class)
			.has(
				new Condition<Throwable>(
					e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error"));
		admission.setCtrlDate2(ctrlDate);

		// controlDate2 != null && controlDate1 == null
		LocalDateTime admDate = admission.getCtrlDate1();
		admission.setCtrlDate1(null);
		assertThatThrownBy(() -> admissionBrowserManager.updateAdmission(admission))
			.isInstanceOf(OHDataValidationException.class)
			.has(
				new Condition<Throwable>(
					e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error"));
		admission.setCtrlDate1(admDate);

		// abort date before visit date
		LocalDateTime abortDate = admission.getAbortDate();
		LocalDateTime changeDate = admission.getVisitDate().minusMonths(1);
		admission.setAbortDate(changeDate);
		assertThatThrownBy(() -> admissionBrowserManager.updateAdmission(admission))
			.isInstanceOf(OHDataValidationException.class)
			.has(
				new Condition<Throwable>(
					e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error"));
		admission.setAbortDate(abortDate);
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void testMgrGetAdmissionsPageable(boolean maternityRestartInJune) throws Exception {
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
		PagedResponse<Admission> patients = admissionBrowserManager.getAdmissionsPageable(dateFrom, dateTo, page, size);

		// then:
		assertThat(patients.getData().get(0).getId()).isEqualTo(foundAdmission.getId());
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void testMgrGetDischargesPageable(boolean maternityRestartInJune) throws Exception {
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
		PagedResponse<Admission> patients = admissionBrowserManager.getDischargesPageable(dateFrom, dateTo, page, size);

		// then:
		assertThat(patients.getData().get(0).getId()).isEqualTo(foundAdmission.getId());
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void testMgrGetAdmissionsByAdmissionsDate(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		// given:
		int id = setupTestAdmission(false);
		String str = "2000-01-01T10:11:30";
		String str2 = "2023-05-05T10:11:30";
		LocalDateTime dateFrom = LocalDateTime.parse(str);
		LocalDateTime dateTo = LocalDateTime.parse(str2);
		Admission foundAdmission = admissionIoOperation.getAdmission(id);
		// when:
		List<Admission> patients = admissionBrowserManager.getAdmissionsByAdmissionDate(dateFrom, dateTo);

		// then:
		assertThat(patients.get(0).getId()).isEqualTo(foundAdmission.getId());
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void testAdmissionEqualHash(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		int id = setupTestAdmission(false);
		Admission admission = admissionBrowserManager.getAdmission(id);
		Admission admission2 = buildNewAdmission();
		admission2.setId(id); // no really legal but needed for these tests
		assertThat(admission)
				.isEqualTo(admission)
				.isEqualTo(admission2)
				.isNotEqualTo("xyzzy");

		assertThat(admission.compareTo(admission2)).isZero();
		admission2.setId(9999);
		assertThat(admission.compareTo(admission2)).isEqualTo(admission.getId() - 9999);

		assertThat(admission.hashCode()).isPositive();
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void testIoAdmissionIoOperationRepositoryCustom(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		PatientAdmission patientAdmission = new PatientAdmission(1, 2);
		assertThat(patientAdmission.getPatientId()).isEqualTo(1);
		assertThat(patientAdmission.getAdmissionId()).isEqualTo(2);
	}

	@ParameterizedTest(name = "Test with MATERNITYRESTARTINJUNE={0}")
	@MethodSource("maternityRestartInJune")
	void testIoCountAllActiveAdmissions(boolean maternityRestartInJune) throws Exception {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
		setupTestAdmission(false);
		long count = admissionIoOperation.countAllActiveAdmissions();
		assertThat(count).isEqualTo(1);
	}

	class MyAdmissionIoOperationRepositoryCustom implements AdmissionIoOperationRepositoryCustom {

		@Override
		public List<AdmittedPatient> findPatientAdmissionsBySearchAndDateRanges(String searchTerms, LocalDateTime[] admissionRange,
			LocalDateTime[] dischargeRange) throws OHServiceException {
			return null;
		}
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

	private void checkAdmissionIntoDb(int id) throws OHServiceException {
		Admission foundAdmission = admissionIoOperation.getAdmission(id);
		testAdmission.check(foundAdmission);
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
