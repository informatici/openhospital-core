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
package org.isf.patient;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import java.sql.Blob;

import org.isf.OHCoreTestCase;
import org.isf.patient.model.Patient;
import org.isf.patient.service.FileSystemPatientPhotoRepository;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.patient.service.PatientIoOperations;
import org.isf.utils.exception.OHException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class TestFileSystemPatientPhotoRepository extends OHCoreTestCase {

	private static TestPatient testPatient;

	@Autowired
	FileSystemPatientPhotoRepository fileSystemPatientPhotoRepository;
	@Autowired
	PatientIoOperationRepository patientIoOperationRepository;
	@Autowired
	PatientIoOperations patientIoOperation;

	@BeforeAll
	static void setUpClass() {
		testPatient = new TestPatient();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void testExist() throws Exception {
		Integer code = setupTestPatient(true);
		assertThat(fileSystemPatientPhotoRepository.exist("rsc-test/patient", code)).isTrue();
	}

	@Test
	void testLoadInPatient() throws Exception {
		Integer code = setupTestPatient(false);
		Patient patient = patientIoOperation.getPatient(code);
		fileSystemPatientPhotoRepository.loadInPatient(patient, "rsc-test/patient");
	}

	@Test
	void testSaveAndDelete() throws Exception {
		Blob blob = getBlob();
		fileSystemPatientPhotoRepository.save("rsc-test/patient", 2, blob.getBytes(1, (int)blob.length()));
		fileSystemPatientPhotoRepository.delete("rsc-test/patient", 2);
	}

	private Blob getBlob() throws Exception {
		Method method = fileSystemPatientPhotoRepository.getClass().getDeclaredMethod("load", Integer.class, String.class);
		method.setAccessible(true);
		Blob blob = (Blob)method.invoke(fileSystemPatientPhotoRepository, 1, "rsc-test/patient");
		return blob;
	}


	private Integer setupTestPatient(boolean usingSet) throws OHException {
		Patient patient = testPatient.setup(usingSet);
		patientIoOperationRepository.saveAndFlush(patient);
		return patient.getCode();
	}
}
