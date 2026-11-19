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
package org.isf.opd.model;

import static org.assertj.core.api.Assertions.assertThat;

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
import org.isf.opd.service.OpdIoOperationRepository;
import org.isf.patient.TestPatient;
import org.isf.patient.model.Patient;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.utils.exception.OHException;
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

class OpdTest extends OHCoreTestCase {

	private static TestOpd testOpd;
	private static TestPatient testPatient;
	private static TestDiseaseType testDiseaseType;
	private static TestDisease testDisease;
	private static TestWard testWard;
	private static TestVisit testVisit;

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
	void constructor_shouldSetFields(boolean opdExtended) throws Exception {
		GeneralData.OPDEXTENDED = opdExtended;
		int code = setupTestOpd(false);
		checkOpdIntoDb(code);
	}

	@ParameterizedTest(name = "Test with OPDEXTENDED={0}")
	@MethodSource("opdExtended")
	void setters_shouldSetFields(boolean opdExtended) throws Exception {
		GeneralData.OPDEXTENDED = opdExtended;
		int code = setupTestOpd(true);
		checkOpdIntoDb(code);
	}

	@ParameterizedTest(name = "Test with OPDEXTENDED={0}")
	@MethodSource("opdExtended")
	void settersAndGetters_shouldUsePatientFieldsAndUpdateCodeDiseasesLockAndNextVisit(boolean opdExtended) throws Exception {
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
	void hashCode_shouldBeComputedFromCode(boolean opdExtended) throws Exception {
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
	void equals_shouldReturnTrueForSameInstanceAndFalseForOtherType(boolean opdExtended) throws Exception {
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
