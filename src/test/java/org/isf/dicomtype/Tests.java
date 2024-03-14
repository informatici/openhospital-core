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
package org.isf.dicomtype;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.assertj.core.api.Condition;
import org.isf.OHCoreTestCase;
import org.isf.dicomtype.manager.DicomTypeBrowserManager;
import org.isf.dicomtype.model.DicomType;
import org.isf.dicomtype.service.DicomTypeIoOperation;
import org.isf.dicomtype.service.DicomTypeIoOperationRepository;
import org.isf.utils.exception.OHDataIntegrityViolationException;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class Tests extends OHCoreTestCase {

	private static TestDicomType testDicomType;

	@Autowired
	DicomTypeIoOperation dicomTypeIoOperation;
	@Autowired
	DicomTypeIoOperationRepository dicomTypeIoOperationRepository;
	@Autowired
	DicomTypeBrowserManager dicomTypeBrowserManager;

	@BeforeAll
	static void setUpClass() {
		testDicomType = new TestDicomType();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void testDicomTypeGets() throws Exception {
		String code = setupTestDicomType(false);
		checkDicomTypeIntoDb(code);
	}

	@Test
	void testDicomTypeSets() throws Exception {
		String code = setupTestDicomType(true);
		checkDicomTypeIntoDb(code);
	}

	@Test
	void testIoGetDicomType() throws Exception {
		setupTestDicomType(false);
		List<DicomType> dicomTypes = dicomTypeIoOperation.getDicomType();
		testDicomType.check(dicomTypes.get(0));
	}

	@Test
	void testIoUpdateDicomType() throws Exception {
		String typeId = setupTestDicomType(false);
		DicomType dicomType = dicomTypeIoOperationRepository.findById(typeId).orElse(null);
		assertThat(dicomType).isNotNull();
		dicomType.setDicomTypeDescription("newDescription");
		DicomType updatedDicomType = dicomTypeIoOperation.updateDicomType(dicomType);
		assertThat(updatedDicomType).isNotNull();
		assertThat(updatedDicomType.getDicomTypeDescription()).isEqualTo("newDescription");
	}

	@Test
	void testIoNewDicomType() throws Exception {
		DicomType dicomType = new DicomType("id", "description");
		DicomType savedDicomType = dicomTypeIoOperation.newDicomType(dicomType);
		assertThat(savedDicomType.getDicomTypeDescription()).isEqualTo("description");
	}

	@Test
	void testIoDeleteDicomType() throws Exception {
		DicomType dicomType = new DicomType("id", "description");
		DicomType newDicomType = dicomTypeIoOperation.newDicomType(dicomType);
		assertThat(newDicomType).isNotNull();
		dicomTypeIoOperation.deleteDicomType(newDicomType);
		assertThat(dicomTypeIoOperation.isCodePresent(newDicomType.getDicomTypeID())).isFalse();
	}

	@Test
	void testIoIsCodePresent() throws Exception {
		DicomType dicomType = new DicomType("id", "description");
		DicomType newDicomType = dicomTypeIoOperation.newDicomType(dicomType);
		assertThat(dicomTypeIoOperation.isCodePresent(newDicomType.getDicomTypeID())).isTrue();
	}

	@Test
	void testMgrGetDicomType() throws Exception {
		setupTestDicomType(false);
		List<DicomType> dicomTypes = dicomTypeBrowserManager.getDicomType();
		testDicomType.check(dicomTypes.get(0));
	}

	@Test
	void testMgrUpdateDicomType() throws Exception {
		String typeId = setupTestDicomType(false);
		DicomType dicomType = dicomTypeIoOperationRepository.findById(typeId).orElse(null);
		assertThat(dicomType).isNotNull();
		dicomType.setDicomTypeDescription("newDescription");
		DicomType updatedDicomType = dicomTypeBrowserManager.updateDicomType(dicomType);
		assertThat(updatedDicomType).isNotNull();
		assertThat(updatedDicomType.getDicomTypeDescription()).isEqualTo("newDescription");
	}

	@Test
	void testMgrNewDicomType() throws Exception {
		DicomType dicomType = new DicomType("id", "description");
		DicomType newDicomType = dicomTypeBrowserManager.newDicomType(dicomType);
		assertThat(newDicomType).isNotNull();
		assertThat(newDicomType.getDicomTypeDescription()).isEqualTo("description");
	}

	@Test
	void testMgrDeleteDicomType() throws Exception {
		DicomType dicomType = new DicomType("id", "description");
		DicomType newDicomType = dicomTypeBrowserManager.newDicomType(dicomType);
		assertThat(newDicomType).isNotNull();
		dicomTypeBrowserManager.deleteDicomType(newDicomType);
		assertThat(dicomTypeBrowserManager.isCodePresent(newDicomType.getDicomTypeID())).isFalse();
	}

	@Test
	void testMgrIsCodePresent() throws Exception {
		DicomType dicomType = new DicomType("id", "description");
		DicomType newDicomType = dicomTypeBrowserManager.newDicomType(dicomType);
		assertThat(newDicomType).isNotNull();
		assertThat(dicomTypeBrowserManager.isCodePresent(newDicomType.getDicomTypeID())).isTrue();
	}

	@Test
	void testMgrValidationTypeIdIsEmpty() throws Exception {
		String typeId = setupTestDicomType(false);
		DicomType dicomType = dicomTypeIoOperationRepository.findById(typeId).orElse(null);
		assertThat(dicomType).isNotNull();
		dicomType.setDicomTypeID("");
		assertThatThrownBy(() -> dicomTypeBrowserManager.updateDicomType(dicomType))
			.isInstanceOf(OHDataValidationException.class)
			.has(
				new Condition<Throwable>(
					e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error")
			);
	}

	@Test
	void testMgrValidationTypeIdIsTooLong() throws Exception {
		String typeId = setupTestDicomType(false);
		DicomType dicomType = dicomTypeIoOperationRepository.findById(typeId).orElse(null);
		assertThat(dicomType).isNotNull();
		dicomType.setDicomTypeID("thisIsAKeyThatIsTooLong");
		assertThatThrownBy(() -> dicomTypeBrowserManager.updateDicomType(dicomType))
			.isInstanceOf(OHDataValidationException.class)
			.has(
				new Condition<Throwable>(
					e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error")
			);
	}

	@Test
	void testMgrValidationDescriptionIsEmpty() throws Exception {
		DicomType dicomType = new DicomType("id", "");
		assertThatThrownBy(() -> dicomTypeBrowserManager.newDicomType(dicomType))
			.isInstanceOf(OHDataValidationException.class)
			.has(
				new Condition<Throwable>(
					e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error")
			);
	}

	@Test
	void testMgrValidationCodeAlreadyExists() throws Exception {
		String typeId = setupTestDicomType(true);
		DicomType dicomType = dicomTypeIoOperationRepository.findById(typeId).orElse(null);
		assertThat(dicomType).isNotNull();
		assertThatThrownBy(() -> dicomTypeBrowserManager.newDicomType(dicomType))
			.isInstanceOf(OHDataIntegrityViolationException.class)
			.has(
				new Condition<Throwable>(
					e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error")
			);
	}

	@Test
	void testDicomTypeToString() throws Exception {
		DicomType dicomType = new DicomType("id", "someDescription");
		assertThat(dicomType).hasToString("someDescription");
	}

	private String setupTestDicomType(boolean usingSet) throws OHException {
		DicomType dicomType = testDicomType.setup(usingSet);
		dicomTypeIoOperationRepository.saveAndFlush(dicomType);
		return dicomType.getDicomTypeID();
	}

	private void checkDicomTypeIntoDb(String code) throws OHException {
		DicomType foundDicomType = dicomTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(foundDicomType).isNotNull();
		testDicomType.check(foundDicomType);
	}
}
