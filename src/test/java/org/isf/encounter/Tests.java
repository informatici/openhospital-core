package org.isf.encounter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.apache.logging.log4j.core.util.Assert;
import org.isf.OHCoreTestCase;
import org.isf.encounter.manager.EncounterBrowserManager;
import org.isf.encounter.model.Encounter;
import org.isf.encounter.model.EncounterStatus;
import org.isf.patient.TestPatient;
import org.isf.patient.model.Patient;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class Tests extends OHCoreTestCase {

	private static TestEncounter testEncounter;
	
	private static TestPatient testPatient;
	
	@Autowired
	PatientIoOperationRepository patientIoOperationRepository;
	
	@Autowired
	EncounterBrowserManager encounterBrowserManager;

	@BeforeAll
	static void setUpClass() {
		testEncounter = new TestEncounter();
		testPatient = new TestPatient();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
    void saveEncounter_shouldSaveAndReturnEncounter() throws OHServiceException, OHException {
        String code = setupEncounter(false);
        
        Encounter encounterSave = encounterBrowserManager.getEncountersByCode(code);
        // Assert
        assertNotNull(encounterSave);
    }

    @Test
    void getEncountersByPatient_shouldReturnEncounters() throws OHServiceException, OHException {
        String code = setupEncounter(true);
        Encounter firstEncounter = encounterBrowserManager.getEncountersByCode(code);
        Integer patientCode = firstEncounter.getPatientCode();
        Encounter secondEncounter = new Encounter("CODE", EncounterStatus.OPEN, firstEncounter.getPatientCode());
        secondEncounter = encounterBrowserManager.saveEncounter(secondEncounter);
        List<Encounter> patientEncounters = encounterBrowserManager.getEncountersByPatient(patientCode);  

        // Assert
        assertNotNull(patientEncounters);
        assertEquals(2, patientEncounters.size());
        assertEquals("CODE", patientEncounters.get(1).getCode());
    }

    @Test
    void getEncountersByCode_shouldReturnEncounter() throws OHServiceException, OHException {
    	 String code = setupEncounter(false);
         
         Encounter encounterSave = encounterBrowserManager.getEncountersByCode(code);
         // Assert
         assertNotNull(encounterSave);
         assertEquals(code, encounterSave.getCode());
    }

	@Test
	void getCurrentEncounter_shouldReturnEncounter() throws OHServiceException, OHException {
		String code = setupEncounter(false);
		Encounter encounterSaved = encounterBrowserManager.getEncountersByCode(code);
		Encounter currentEncounter = encounterBrowserManager.getCurrentEncounter(encounterSaved.getPatientCode());
		// Assert
		assertNotNull(currentEncounter);
		assertEquals(code, currentEncounter.getCode());
	}

	private String setupEncounter(boolean usingSet) throws OHException, OHServiceException {
		Patient patient = testPatient.setup(false);
		Patient patientSaved = patientIoOperationRepository.saveAndFlush(patient);
		Encounter encounter = testEncounter.setup(false);
		encounter.setPatientCode(patientSaved.getCode());
		encounter = encounterBrowserManager.saveEncounter(encounter);
		Assert.isNonEmpty(encounter);
		return encounter.getCode();
	}
}
