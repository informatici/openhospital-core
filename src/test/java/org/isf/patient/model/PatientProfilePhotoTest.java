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
package org.isf.patient.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.nio.file.Files;

import org.isf.OHCoreTestCase;
import org.isf.generaldata.GeneralData;
import org.isf.patient.TestPatient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class PatientProfilePhotoTest extends OHCoreTestCase {

	private static TestPatient testPatient;

	@BeforeAll
	static void setUpClass() {
		GeneralData.PATIENTPHOTOSTORAGE = "DB";
		testPatient = new TestPatient();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	// This test requires access to the opencv java native library
	@Disabled
	@Test
	void setters_shouldUpdatePhotoAndPatient() throws Exception {
		Patient patient = testPatient.setup(true);
		PatientProfilePhoto patientProfilePhoto = new PatientProfilePhoto();

		File file = new File(getClass().getResource("patient.jpg").getFile());
		byte[] bytes = Files.readAllBytes(file.toPath());
		patientProfilePhoto.setPhoto(bytes);
		assertThat(patientProfilePhoto.getPhotoAsImage()).isNotNull();

		patientProfilePhoto.setPhoto(null);
		assertThat(patientProfilePhoto.getPhoto()).isNull();

		patientProfilePhoto.setPatient(patient);
		assertThat(patientProfilePhoto.getPatient()).isEqualTo(patient);
	}

}
