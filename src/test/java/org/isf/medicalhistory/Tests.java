package org.isf.medicalhistory;


import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.medicalhistory.manager.MedicalHistoryBrowsingManager;
import org.isf.medicalhistory.model.MedicalHistory;
import org.isf.medicalhistory.service.MedicalHistoryIoOperationRepository;
import org.isf.medicalhistory.service.MedicalHistoryIoOperations;
import org.isf.patient.TestPatient;
import org.isf.patient.model.Patient;
import org.isf.patient.service.PatientIoOperationRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class Tests extends OHCoreTestCase {

	private static TestMedicalHistory testMedicalHistory;
	private static TestPatient testPatient = new TestPatient();

	@Autowired
	PatientIoOperationRepository patientIoOperationRepository;

	@Autowired
	MedicalHistoryBrowsingManager manager;

	@Autowired
	MedicalHistoryIoOperations ioOperations;

	@Autowired
	MedicalHistoryIoOperationRepository repository;

	@BeforeAll
	static void setUpClass() {
		testPatient = new TestPatient();
		testMedicalHistory = new TestMedicalHistory();
	}

	@Test
	void testServiceAddMedicalHistory() throws Exception {
		MedicalHistory medicalHistory = setupTestMedicalHistory();
		assertThat(medicalHistory).isNotNull();

		MedicalHistory medHist = ioOperations.add(medicalHistory);
		medicalHistory = repository.getReferenceById(medHist.getId());

		assertThat(medHist.getId()).isEqualTo(medicalHistory.getId());
	}

	@Test
	void testManagerAddMedicalHistory() throws Exception {
		MedicalHistory medicalHistory = setupTestMedicalHistory();
		assertThat(medicalHistory).isNotNull();

		MedicalHistory medHist = manager.add(medicalHistory);
		medicalHistory = repository.getReferenceById(medHist.getId());

		assertThat(medHist.getId()).isEqualTo(medicalHistory.getId());
	}

	@Test
	void testServiceUpdateMedicalHistory() throws Exception {
		MedicalHistory medicalHistory = setupTestMedicalHistory();
		assertThat(medicalHistory).isNotNull();

		MedicalHistory medHist = ioOperations.add(medicalHistory);
		medicalHistory = repository.getReferenceById(medHist.getId());

		medicalHistory.setOtherFamilyPathologies("yes");
		medicalHistory.setAllergyPrecision("allergic to sugar");

		manager.update(medicalHistory);
		medicalHistory = repository.getReferenceById(medHist.getId());

		assertThat(medHist.getOtherFamilyPathologies()).isEqualTo("yes");
		assertThat(medicalHistory.getAllergyPrecision()).isEqualTo("allergic to sugar");
	}

	@Test
	void testManagerUpdateMedicalHistory() throws Exception {
		MedicalHistory medicalHistory = setupTestMedicalHistory();
		assertThat(medicalHistory).isNotNull();

		MedicalHistory medHist = manager.add(medicalHistory);
		medicalHistory = repository.getReferenceById(medHist.getId());

		medicalHistory.setOtherFamilyPathologies("yes");
		medicalHistory.setAllergyPrecision("allergic to sugar");

		manager.update(medicalHistory);
		medicalHistory = repository.getReferenceById(medHist.getId());

		assertThat(medHist.getOtherFamilyPathologies()).isEqualTo("yes");
		assertThat(medicalHistory.getAllergyPrecision()).isEqualTo("allergic to sugar");
	}

	@Test
	void testServiceGetMedicalHistoryById() throws Exception {
		List<MedicalHistory> medicalHistories = setupTestMedicalHistories(3);
		repository.saveAllAndFlush(medicalHistories);

		MedicalHistory medHistory = ioOperations.getMedicalHistoryById(medicalHistories.get(0).getId());

		assertThat(medicalHistories.get(0)).isEqualTo(medHistory);
	}

	@Test
	void testManagerGetMedicalHistoryById() throws Exception {
		List<MedicalHistory> medicalHistories = setupTestMedicalHistories(3);
		repository.saveAllAndFlush(medicalHistories);

		MedicalHistory medHistory = manager.getMedicalHistoryById(medicalHistories.get(0).getId());

		assertThat(medicalHistories.get(0)).isEqualTo(medHistory);
	}

	@Test
	void testServiceGetAllMedicalHistoriesByPatientCode() throws Exception {
		List<MedicalHistory> medicalHistories = setupTestMedicalHistories(3);
		repository.saveAllAndFlush(medicalHistories);
		List<MedicalHistory> medHistories = ioOperations.getMedicalHistoriesByPatientCode(medicalHistories.get(0).getPatient().getCode());

		assertThat(medHistories).hasSize(3);
	}

	@Test
	void testManagerGetAllMedicalHistoryByPatientCode() throws Exception {
		List<MedicalHistory> medicalHistories = setupTestMedicalHistories(3);
		repository.saveAllAndFlush(medicalHistories);
		List<MedicalHistory> medHistories = manager.getMedicalHistoriesByPatientCode(medicalHistories.get(0).getPatient().getCode());

		assertThat(medHistories).hasSize(3);
	}

	@Test
	void testServiceGetAllMedicalHistories() throws Exception {
		List<MedicalHistory> medicalHistories = setupTestMedicalHistories(3);
		repository.saveAllAndFlush(medicalHistories);

		List<MedicalHistory> medicalHistories1 = setupTestMedicalHistories(5);
		repository.saveAllAndFlush(medicalHistories1);

		List<MedicalHistory> medHistories = ioOperations.getAll();

		assertThat(medHistories).hasSize(8);
	}

	@Test
	void testManagerGetAllMedicalHistory() throws Exception {
		List<MedicalHistory> medicalHistories = setupTestMedicalHistories(3);
		repository.saveAllAndFlush(medicalHistories);

		List<MedicalHistory> medicalHistories1 = setupTestMedicalHistories(5);
		repository.saveAllAndFlush(medicalHistories1);

		List<MedicalHistory> medHistories = manager.getAll();

		assertThat(medHistories).hasSize(8);
	}

	private MedicalHistory setupTestMedicalHistory() throws Exception {
		Patient patient = testPatient.setup(false);

		patientIoOperationRepository.saveAndFlush(patient);
		MedicalHistory medicalHistory = testMedicalHistory.createMedicalHistory(patient);

		repository.saveAndFlush(medicalHistory);
		return medicalHistory;
	}

	private List<MedicalHistory> setupTestMedicalHistories(int length) throws Exception {
		List<MedicalHistory> histories = new ArrayList<>();

		Patient patient = testPatient.setup(false);
		patientIoOperationRepository.saveAndFlush(patient);

		for (int i = 0; i < length; i++) {
			MedicalHistory medicalHistory = testMedicalHistory.createMedicalHistory(patient);
			histories.add(medicalHistory);
		}

		return histories;
	}

}
