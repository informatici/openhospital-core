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
package org.isf.dicomtype.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.dicomtype.TestDicomType;
import org.isf.dicomtype.model.DicomType;
import org.isf.utils.exception.OHException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class DicomTypeIoOperationTest extends OHCoreTestCase {

	private static TestDicomType testDicomType;

	@Autowired
	DicomTypeIoOperation dicomTypeIoOperation;
	@Autowired
	DicomTypeIoOperationRepository dicomTypeIoOperationRepository;

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
		List<DicomType> dicomTypes = dicomTypeIoOperation.getDicomType();
		testDicomType.check(dicomTypes.get(0));
	}

	@Test
	void updateDicomType_shouldSaveNewDescription() throws Exception {
		String typeId = setupTestDicomType(false);
		DicomType dicomType = dicomTypeIoOperationRepository.findById(typeId).orElse(null);
		assertThat(dicomType).isNotNull();
		dicomType.setDicomTypeDescription("newDescription");
		DicomType updatedDicomType = dicomTypeIoOperation.updateDicomType(dicomType);
		assertThat(updatedDicomType).isNotNull();
		assertThat(updatedDicomType.getDicomTypeDescription()).isEqualTo("newDescription");
	}

	@Test
	void newDicomType_shouldSaveDicomType() throws Exception {
		DicomType dicomType = new DicomType("id", "description");
		DicomType savedDicomType = dicomTypeIoOperation.newDicomType(dicomType);
		assertThat(savedDicomType.getDicomTypeDescription()).isEqualTo("description");
	}

	@Test
	void deleteDicomType_shouldRemoveDicomType() throws Exception {
		DicomType dicomType = new DicomType("id", "description");
		DicomType newDicomType = dicomTypeIoOperation.newDicomType(dicomType);
		assertThat(newDicomType).isNotNull();
		dicomTypeIoOperation.deleteDicomType(newDicomType);
		assertThat(dicomTypeIoOperation.isCodePresent(newDicomType.getDicomTypeID())).isFalse();
	}

	@Test
	void isCodePresent_shouldReturnTrue_whenCodeExists() throws Exception {
		DicomType dicomType = new DicomType("id", "description");
		DicomType newDicomType = dicomTypeIoOperation.newDicomType(dicomType);
		assertThat(dicomTypeIoOperation.isCodePresent(newDicomType.getDicomTypeID())).isTrue();
	}

	private String setupTestDicomType(boolean usingSet) throws OHException {
		DicomType dicomType = testDicomType.setup(usingSet);
		dicomTypeIoOperationRepository.saveAndFlush(dicomType);
		return dicomType.getDicomTypeID();
	}

}
