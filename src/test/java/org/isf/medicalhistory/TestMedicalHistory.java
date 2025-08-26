package org.isf.medicalhistory;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.LocalDateTime;

import org.isf.medicalhistory.model.MedicalHistory;
import org.isf.patient.TestPatient;
import org.isf.patient.model.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestMedicalHistory {

	private final LocalDateTime testDate = LocalDateTime.of(2025, 8, 21, 10, 0, 0);
	private Patient patient;
	private TestPatient testPatient = new TestPatient();

	public TestMedicalHistory() {

	}

	@BeforeEach
	public void setup() throws Exception {
		patient = testPatient.setup(false);
	}

	public MedicalHistory createMedicalHistory(Patient patient) {
		MedicalHistory mh = new MedicalHistory();
		mh.setPatient(patient);
		mh.setSiblingRank(2);
		mh.setTermPregnancy("Full term");
		mh.setDeliveryMode("Cesarean");
		mh.setApgarScore("8/10");
		mh.setBirthWeight(3.2);
		mh.setVaccinationState("Up to date");
		mh.setAntiMalarialProphylaxis("Yes");
		mh.setDiet("Balanced");
		mh.setDeParasitization("Done");
		mh.setPsychomotorDev("Normal");
		mh.setSomaticGrowth("Normal");
		mh.setIronSupplement(true);
		mh.setFolicAcidSupplement(true);
		mh.setVitASupplement(false);
		mh.setOtherSupplements("None");
		mh.setTransfusion(false);
		mh.setLastTransfusionDate(testDate);
		mh.setSickleCell(false);
		mh.setDrugAllergy(false);
		mh.setAllergyPrecision("None");
		mh.setHemylosis("None");
		mh.setOtherPersonalPathologies("None");
		mh.setOtherFamilyPathologies("None");
		return mh;
	}

	@Test
	public void testMedicalHistoryFields() {
		MedicalHistory mh = createMedicalHistory(patient);

		assertThat(mh.getPatient()).isEqualTo(patient);
		assertThat(mh.getSiblingRank()).isEqualTo(2);
		assertThat(mh.getTermPregnancy()).isEqualTo("Full term");
		assertThat(mh.getDeliveryMode()).isEqualTo("Cesarean");
		assertThat(mh.getApgarScore()).isEqualTo("8/10");
		assertThat(mh.getBirthWeight()).isEqualTo(3.2);
		assertThat(mh.getVaccinationState()).isEqualTo("Up to date");
		assertThat(mh.getAntiMalarialProphylaxis()).isEqualTo("Yes");
		assertThat(mh.getDiet()).isEqualTo("Balanced");
		assertThat(mh.getDeParasitization()).isEqualTo("Done");
		assertThat(mh.getPsychomotorDev()).isEqualTo("Normal");
		assertThat(mh.getSomaticGrowth()).isEqualTo("Normal");
		assertThat(mh.getIronSupplement()).isTrue();
		assertThat(mh.getFolicAcidSupplement()).isTrue();
		assertThat(mh.getVitASupplement()).isFalse();
		assertThat(mh.getOtherSupplements()).isEqualTo("None");
		assertThat(mh.getTransfusion()).isFalse();
		assertThat(mh.getLastTransfusionDate()).isEqualTo(testDate);
		assertThat(mh.getSickleCell()).isFalse();
		assertThat(mh.getDrugAllergy()).isFalse();
		assertThat(mh.getAllergyPrecision()).isEqualTo("None");
		assertThat(mh.getHemylosis()).isEqualTo("None");
		assertThat(mh.getOtherPersonalPathologies()).isEqualTo("None");
		assertThat(mh.getOtherFamilyPathologies()).isEqualTo("None");
	}
}
