package org.isf.encounter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.encouter.manager.EncounterBrowserManager;
import org.isf.encouter.model.Encounter;
import org.isf.encouter.model.EncounterStatus;
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
        Integer patientCode = firstEncounter.getPatient().getCode();
        Encounter secondEncounter = new Encounter("CODE", EncounterStatus.OPEN, firstEncounter.getPatient());
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

	
	private String setupEncounter(boolean usingSet) throws OHException, OHServiceException {
		Patient patient = testPatient.setup(false);
		Encounter encounter = testEncounter.setup(false);
		encounter.setPatient(patient);
		
		patientIoOperationRepository.saveAndFlush(patient);
		encounter = encounterBrowserManager.saveEncounter(encounter);
		
		return encounter.getCode();
	}
}
