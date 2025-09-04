/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2025 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.encounter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
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
        Patient patient = firstEncounter.getPatient();
        List<Encounter> patientEncounters = encounterBrowserManager.getEncountersByPatient(patient.getCode());

        // Assert
        assertNotNull(patientEncounters);
        assertEquals(1, patientEncounters.size());
        assertEquals(code, patientEncounters.get(0).getCode());
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
		Encounter currentEncounter = encounterBrowserManager.getCurrentEncounter(encounterSaved.getPatient().getCode());
		// Assert
		assertNotNull(currentEncounter);
		assertEquals(code, currentEncounter.getCode());
	}

	private String setupEncounter(boolean usingSet) throws OHException, OHServiceException {
		Patient patient = testPatient.setup(false);
		Patient patientSaved = patientIoOperationRepository.saveAndFlush(patient);
		Encounter encounter = testEncounter.setup(false);
		encounter.setPatient(patientSaved);
		encounter = encounterBrowserManager.saveEncounter(encounter);
		Assert.isNonEmpty(encounter);
		return encounter.getCode();
	}
}
