/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2020 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.isf.medtype.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;

import org.isf.OHCoreTestCase;
import org.isf.medtype.model.MedicalType;
import org.isf.medtype.service.MedicalTypeIoOperation;
import org.isf.medtype.service.MedicalTypeIoOperationRepository;
import org.isf.utils.exception.OHException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class Tests extends OHCoreTestCase {

	private static TestMedicalType testMedicalType;

	@Autowired
	MedicalTypeIoOperation medicalTypeIoOperation;
	@Autowired
	MedicalTypeIoOperationRepository medicalTypeIoOperationRepository;

	@BeforeClass
	public static void setUpClass() {
		testMedicalType = new TestMedicalType();
	}

	@Before
	public void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	public void testMedicalTypeGets() throws Exception {
		String code = _setupTestMedicalType(false);
		_checkMedicalTypeIntoDb(code);
	}

	@Test
	public void testMedicalTypeSets() throws Exception {
		String code = _setupTestMedicalType(true);
		_checkMedicalTypeIntoDb(code);
	}

	@Test
	public void testIoGetMedicalType() throws Exception {
		String code = _setupTestMedicalType(false);
		MedicalType foundMedicalType = medicalTypeIoOperationRepository.findOne(code);
		ArrayList<MedicalType> medicalTypes = medicalTypeIoOperation.getMedicalTypes();
		assertThat(medicalTypes.get(medicalTypes.size() - 1).getDescription()).isEqualTo(foundMedicalType.getDescription());
	}

	@Test
	public void testIoUpdateMedicalType() throws Exception {
		String code = _setupTestMedicalType(false);
		MedicalType foundMedicalType = medicalTypeIoOperationRepository.findOne(code);
		foundMedicalType.setDescription("Update");
		boolean result = medicalTypeIoOperation.updateMedicalType(foundMedicalType);
		assertThat(result).isTrue();
		MedicalType updateMedicalType = medicalTypeIoOperationRepository.findOne(code);
		assertThat(updateMedicalType.getDescription()).isEqualTo("Update");
	}

	@Test
	public void testIoNewMedicalType() throws Exception {
		MedicalType medicalType = testMedicalType.setup(true);
		boolean result = medicalTypeIoOperation.newMedicalType(medicalType);
		assertThat(result).isTrue();
		_checkMedicalTypeIntoDb(medicalType.getCode());
	}

	@Test
	public void testIoIsCodePresent() throws Exception {
		String code = _setupTestMedicalType(false);
		boolean result = medicalTypeIoOperation.isCodePresent(code);
		assertThat(result).isTrue();
	}

	@Test
	public void testIoDeleteMedicalType() throws Exception {
		String code = _setupTestMedicalType(false);
		MedicalType foundMedicalType = medicalTypeIoOperationRepository.findOne(code);
		boolean result = medicalTypeIoOperation.deleteMedicalType(foundMedicalType);
		assertThat(result).isTrue();
		result = medicalTypeIoOperation.isCodePresent(code);
		assertThat(result).isFalse();
	}

	private String _setupTestMedicalType(boolean usingSet) throws OHException {
		MedicalType medicalType = testMedicalType.setup(usingSet);
		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		return medicalType.getCode();
	}

	private void _checkMedicalTypeIntoDb(String code) throws OHException {
		MedicalType foundMedicalType = medicalTypeIoOperationRepository.findOne(code);
		testMedicalType.check(foundMedicalType);
	}
}