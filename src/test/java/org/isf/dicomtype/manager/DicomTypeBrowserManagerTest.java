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
package org.isf.dicomtype.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.assertj.core.api.Condition;
import org.isf.OHCoreTestCase;
import org.isf.dicomtype.TestDicomType;
import org.isf.dicomtype.model.DicomType;
import org.isf.dicomtype.service.DicomTypeIoOperationRepository;
import org.isf.utils.exception.OHDataIntegrityViolationException;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class DicomTypeBrowserManagerTest extends OHCoreTestCase {

	private static TestDicomType testDicomType;

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
	void getDicomType_shouldReturnSavedDicomType() throws Exception {
		setupTestDicomType(false);
		List<DicomType> dicomTypes = dicomTypeBrowserManager.getDicomType();
		testDicomType.check(dicomTypes.get(0));
	}

	@Test
	void updateDicomType_shouldSaveNewDescription() throws Exception {
		String typeId = setupTestDicomType(false);
		DicomType dicomType = dicomTypeIoOperationRepository.findById(typeId).orElse(null);
		assertThat(dicomType).isNotNull();
		dicomType.setDicomTypeDescription("newDescription");
		DicomType updatedDicomType = dicomTypeBrowserManager.updateDicomType(dicomType);
		assertThat(updatedDicomType).isNotNull();
		assertThat(updatedDicomType.getDicomTypeDescription()).isEqualTo("newDescription");
	}

	@Test
	void newDicomType_shouldSaveDicomType() throws Exception {
		DicomType dicomType = new DicomType("id", "description");
		DicomType newDicomType = dicomTypeBrowserManager.newDicomType(dicomType);
		assertThat(newDicomType).isNotNull();
		assertThat(newDicomType.getDicomTypeDescription()).isEqualTo("description");
	}

	@Test
	void deleteDicomType_shouldRemoveDicomType() throws Exception {
		DicomType dicomType = new DicomType("id", "description");
		DicomType newDicomType = dicomTypeBrowserManager.newDicomType(dicomType);
		assertThat(newDicomType).isNotNull();
		dicomTypeBrowserManager.deleteDicomType(newDicomType);
		assertThat(dicomTypeBrowserManager.isCodePresent(newDicomType.getDicomTypeID())).isFalse();
	}

	@Test
	void isCodePresent_shouldReturnTrue_whenCodeExists() throws Exception {
		DicomType dicomType = new DicomType("id", "description");
		DicomType newDicomType = dicomTypeBrowserManager.newDicomType(dicomType);
		assertThat(newDicomType).isNotNull();
		assertThat(dicomTypeBrowserManager.isCodePresent(newDicomType.getDicomTypeID())).isTrue();
	}

	@Test
	void updateDicomType_shouldThrow_whenTypeIdIsEmpty() throws Exception {
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
	void updateDicomType_shouldThrow_whenTypeIdIsTooLong() throws Exception {
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
	void newDicomType_shouldThrow_whenDescriptionIsEmpty() throws Exception {
		DicomType dicomType = new DicomType("id", "");
		assertThatThrownBy(() -> dicomTypeBrowserManager.newDicomType(dicomType))
			.isInstanceOf(OHDataValidationException.class)
			.has(
				new Condition<Throwable>(
					e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error")
			);
	}

	@Test
	void newDicomType_shouldThrowDataIntegrityViolation_whenCodeExists() throws Exception {
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

	private String setupTestDicomType(boolean usingSet) throws OHException {
		DicomType dicomType = testDicomType.setup(usingSet);
		dicomTypeIoOperationRepository.saveAndFlush(dicomType);
		return dicomType.getDicomTypeID();
	}

}
