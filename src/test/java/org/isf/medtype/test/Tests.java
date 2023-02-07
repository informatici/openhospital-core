/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2023 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.medtype.manager.MedicalTypeBrowserManager;
import org.isf.medtype.model.MedicalType;
import org.isf.medtype.service.MedicalTypeIoOperation;
import org.isf.medtype.service.MedicalTypeIoOperationRepository;
import org.isf.utils.exception.OHDataIntegrityViolationException;
import org.isf.utils.exception.OHDataValidationException;
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
	@Autowired
	MedicalTypeBrowserManager medicalTypeBrowserManager;

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
		String code = setupTestMedicalType(false);
		checkMedicalTypeIntoDb(code);
	}

	@Test
	public void testMedicalTypeSets() throws Exception {
		String code = setupTestMedicalType(true);
		checkMedicalTypeIntoDb(code);
	}

	@Test
	public void testIoGetMedicalType() throws Exception {
		String code = setupTestMedicalType(false);
		MedicalType foundMedicalType = medicalTypeIoOperationRepository.findById(code).get();
		List<MedicalType> medicalTypes = medicalTypeIoOperation.getMedicalTypes();
		assertThat(medicalTypes.get(medicalTypes.size() - 1).getDescription()).isEqualTo(foundMedicalType.getDescription());
	}

	@Test
	public void testIoUpdateMedicalType() throws Exception {
		String code = setupTestMedicalType(false);
		MedicalType foundMedicalType = medicalTypeIoOperationRepository.findById(code).get();
		foundMedicalType.setDescription("Update");
		MedicalType result = medicalTypeIoOperation.updateMedicalType(foundMedicalType);
		assertThat(result);
		MedicalType updateMedicalType = medicalTypeIoOperationRepository.findById(code).get();
		assertThat(updateMedicalType.getDescription()).isEqualTo("Update");
	}

	@Test
	public void testIoNewMedicalType() throws Exception {
		MedicalType medicalType = testMedicalType.setup(true);
		MedicalType result = medicalTypeIoOperation.newMedicalType(medicalType);
		assertThat(result);
		checkMedicalTypeIntoDb(medicalType.getCode());
	}

	@Test
	public void testIoIsCodePresent() throws Exception {
		String code = setupTestMedicalType(false);
		boolean result = medicalTypeIoOperation.isCodePresent(code);
		assertThat(result).isTrue();
	}

	@Test
	public void testIoDeleteMedicalType() throws Exception {
		String code = setupTestMedicalType(false);
		MedicalType foundMedicalType = medicalTypeIoOperationRepository.findById(code).get();
		boolean result = medicalTypeIoOperation.deleteMedicalType(foundMedicalType);
		assertThat(result).isTrue();
		result = medicalTypeIoOperation.isCodePresent(code);
		assertThat(result).isFalse();
	}

	@Test
	public void testMgrGetMedicalType() throws Exception {
		String code = setupTestMedicalType(false);
		MedicalType foundMedicalType = medicalTypeIoOperationRepository.findById(code).get();
		List<MedicalType> medicalTypes = medicalTypeBrowserManager.getMedicalType();
		assertThat(medicalTypes.get(medicalTypes.size() - 1).getDescription()).isEqualTo(foundMedicalType.getDescription());
	}

	@Test
	public void testMgrUpdateMedicalType() throws Exception {
		String code = setupTestMedicalType(false);
		MedicalType foundMedicalType = medicalTypeIoOperationRepository.findById(code).get();
		foundMedicalType.setDescription("Update");
		assertThat(medicalTypeBrowserManager.updateMedicalType(foundMedicalType));
		MedicalType updateMedicalType = medicalTypeIoOperationRepository.findById(code).get();
		assertThat(updateMedicalType.getDescription()).isEqualTo("Update");
	}

	@Test
	public void testMgrNewMedicalType() throws Exception {
		MedicalType medicalType = testMedicalType.setup(true);
		assertThat(medicalTypeBrowserManager.newMedicalType(medicalType));
		checkMedicalTypeIntoDb(medicalType.getCode());
	}

	@Test
	public void testMgrIsCodePresent() throws Exception {
		String code = setupTestMedicalType(false);
		assertThat(medicalTypeBrowserManager.isCodePresent(code)).isTrue();
		assertThat(medicalTypeBrowserManager.isCodePresent("isNotThere")).isFalse();
	}

	@Test
	public void testMgrDeleteMedicalType() throws Exception {
		String code = setupTestMedicalType(false);
		MedicalType foundMedicalType = medicalTypeIoOperationRepository.findById(code).get();
		assertThat(medicalTypeBrowserManager.deleteMedicalType(foundMedicalType)).isTrue();
		assertThat(medicalTypeBrowserManager.isCodePresent(code)).isFalse();
	}

	@Test
	public void testMgrValidateMedicalTypeNoKey() throws Exception {
		assertThatThrownBy(() ->
		{
			MedicalType medicalType = new MedicalType("", "description");
			medicalTypeBrowserManager.newMedicalType(medicalType);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	public void testMgrValidateMedicalTypeKeyTooLong() throws Exception {
		assertThatThrownBy(() ->
		{
			MedicalType medicalType = new MedicalType("thisIsTooLong", "description");
			medicalTypeBrowserManager.newMedicalType(medicalType);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	public void testMgrValidateMedicalTypeNoDescription() throws Exception {
		assertThatThrownBy(() ->
		{
			MedicalType medicalType = new MedicalType("Z", "");
			medicalTypeBrowserManager.newMedicalType(medicalType);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	public void testMgrValidateMedicalTypeCodeAlreadyExists() throws Exception {
		assertThatThrownBy(() ->
		{
			String code = setupTestMedicalType(false);
			MedicalType foundMedicalType = medicalTypeIoOperationRepository.findById(code).get();
			MedicalType medicalType = new MedicalType(foundMedicalType.getCode(), foundMedicalType.getDescription());
			medicalTypeBrowserManager.newMedicalType(medicalType);
		})
				.isInstanceOf(OHDataIntegrityViolationException.class);
	}

	@Test
	public void testMedicalTypeToString() throws Exception {
		String code = setupTestMedicalType(false);
		MedicalType foundMedicalType = medicalTypeIoOperationRepository.findById(code).get();
		assertThat(foundMedicalType).hasToString(foundMedicalType.getDescription());
	}

	@Test
	public void testMedicalTypeEquals() throws Exception {
		MedicalType medicalType1 = new MedicalType("Z", "description");
		MedicalType medicalType2 = new MedicalType("A", "description");
		MedicalType medicalType3 = new MedicalType("Z", "otherDescription");

		assertThat(medicalType1.equals(medicalType1)).isTrue();
		assertThat(medicalType1)
				.isNotEqualTo("someString")
				.isNotEqualTo(medicalType2)
				.isNotEqualTo(medicalType3);
	}

	@Test
	public void testMedicalTypeHashCode() throws Exception {
		MedicalType medicalType = new MedicalType("Z", "description");
		// compute value
		int hashCode = medicalType.hashCode();
		assertThat(hashCode).isPositive();
		// used already computed value
		assertThat(medicalType.hashCode()).isEqualTo(hashCode);
	}

	private String setupTestMedicalType(boolean usingSet) throws OHException {
		MedicalType medicalType = testMedicalType.setup(usingSet);
		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		return medicalType.getCode();
	}

	private void checkMedicalTypeIntoDb(String code) throws OHException {
		MedicalType foundMedicalType = medicalTypeIoOperationRepository.findById(code).get();
		testMedicalType.check(foundMedicalType);
	}
}