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
import org.isf.patient.model.PatientMergedEvent;
import org.isf.patient.service.PatientIoOperationRepository;
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

class OpdPatientMergedEventListenerTest extends OHCoreTestCase {

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
	void handle_shouldUpdatePatientToMergedPatient_whenPatientMergedEventArrives(boolean opdExtended) throws Exception {
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

}
