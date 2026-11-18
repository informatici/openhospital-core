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
package org.isf.medstockmovtype.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.medstockmovtype.TestMovementType;
import org.isf.medstockmovtype.model.MovementType;
import org.isf.medstockmovtype.service.MedicalDsrStockMovementTypeIoOperation;
import org.isf.medstockmovtype.service.MedicalDsrStockMovementTypeIoOperationRepository;
import org.isf.utils.exception.OHDataIntegrityViolationException;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MedicalDsrStockMovementTypeBrowserManagerTest extends OHCoreTestCase {

	private static TestMovementType testMovementType;

	@Autowired
	MedicalDsrStockMovementTypeIoOperation medicalDsrStockMovementTypeIoOperation;
	@Autowired
	MedicalDsrStockMovementTypeIoOperationRepository medicalDsrStockMovementTypeIoOperationRepository;
	@Autowired
	MedicalDsrStockMovementTypeBrowserManager medicalDsrStockMovementTypeBrowserManager;

	@BeforeAll
	static void setUpClass() {
		testMovementType = new TestMovementType();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void getMedicalDsrStockMovementType_shouldIncludeSavedType() throws Exception {
		String code = setupTestMovementType(false);
		MovementType foundMovementType = medicalDsrStockMovementTypeBrowserManager.getMovementType(code);
		List<MovementType> movementTypes = medicalDsrStockMovementTypeBrowserManager.getMedicalDsrStockMovementType();
		assertThat(movementTypes.get(movementTypes.size() - 1).getDescription()).isEqualTo(foundMovementType.getDescription());
	}

	@Test
	void getMovementType_shouldReturnNull_whenCodeDoesNotExist() throws Exception {
		assertThat(medicalDsrStockMovementTypeBrowserManager.getMovementType("notThere")).isNull();
	}

	@Test
	void updateMedicalDsrStockMovementType_shouldSaveNewDescription() throws Exception {
		String code = setupTestMovementType(false);
		MovementType foundMovementType = medicalDsrStockMovementTypeBrowserManager.getMovementType(code);
		foundMovementType.setDescription("Update");
		assertThat(medicalDsrStockMovementTypeBrowserManager.updateMedicalDsrStockMovementType(foundMovementType)).isNotNull();
		MovementType updateMovementType = medicalDsrStockMovementTypeBrowserManager.getMovementType(code);
		assertThat(updateMovementType).isNotNull();
		assertThat(updateMovementType.getDescription()).isEqualTo("Update");
	}

	@Test
	void newMedicalDsrStockMovementType_shouldSaveMovementType() throws Exception {
		MovementType movementType = testMovementType.setup(true);
		assertThat(medicalDsrStockMovementTypeBrowserManager.newMedicalDsrStockMovementType(movementType)).isNotNull();
		checkMovementTypeIntoDb(movementType.getCode());
	}

	@Test
	void isCodePresent_shouldReturnTrue_whenCodeExists() throws Exception {
		String code = setupTestMovementType(false);
		assertThat(medicalDsrStockMovementTypeBrowserManager.isCodePresent(code)).isTrue();
	}

	@Test
	void deleteMedicalDsrStockMovementType_shouldRemoveMovementType() throws Exception {
		String code = setupTestMovementType(false);
		MovementType foundMovementType = medicalDsrStockMovementTypeBrowserManager.getMovementType(code);
		medicalDsrStockMovementTypeBrowserManager.deleteMedicalDsrStockMovementType(foundMovementType);
		assertThat(medicalDsrStockMovementTypeBrowserManager.isCodePresent(code)).isFalse();
	}

	@Test
	void newMedicalDsrStockMovementType_shouldThrow_whenCodeIsEmpty() throws Exception {
		assertThatThrownBy(() -> {
			MovementType movementType = new MovementType("", "TestDescription", "+", "operational");
			medicalDsrStockMovementTypeBrowserManager.newMedicalDsrStockMovementType(movementType);
		})
						.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	void newMedicalDsrStockMovementType_shouldThrow_whenCodeIsTooLong() throws Exception {
		assertThatThrownBy(() -> {
			MovementType movementType = new MovementType("abcdefghijklmnopqrstuvwxyz", "TestDescription", "+", "operational");
			medicalDsrStockMovementTypeBrowserManager.newMedicalDsrStockMovementType(movementType);
		})
						.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	void newMedicalDsrStockMovementType_shouldThrow_whenTypeIsTooLong() throws Exception {
		assertThatThrownBy(() -> {
			MovementType movementType = new MovementType("ZZABCD", "TestDescription", "+++++", "operational");
			medicalDsrStockMovementTypeBrowserManager.newMedicalDsrStockMovementType(movementType);
		})
						.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	void newMedicalDsrStockMovementType_shouldThrow_whenDescriptionIsEmpty() throws Exception {
		assertThatThrownBy(() -> {
			MovementType movementType = new MovementType("ZZABCD", "", "+", "operational");
			medicalDsrStockMovementTypeBrowserManager.newMedicalDsrStockMovementType(movementType);
		})
						.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	void newMedicalDsrStockMovementType_shouldThrowDataIntegrityViolation_whenCodeExists() throws Exception {
		assertThatThrownBy(() -> {
			MovementType movementType = testMovementType.setup(false);
			medicalDsrStockMovementTypeIoOperationRepository.saveAndFlush(movementType);
			MovementType movementType2 = new MovementType(movementType.getCode(), movementType.getDescription(), movementType.getType(),
							movementType.getCategory());
			medicalDsrStockMovementTypeBrowserManager.newMedicalDsrStockMovementType(movementType2);
		})
						.isInstanceOf(OHDataIntegrityViolationException.class);
	}

	@Test
	void newMedicalDsrStockMovementType_shouldThrow_whenCategoryIsNull() throws Exception {
		assertThatThrownBy(() -> {
			MovementType movementType = new MovementType("ZZABCD", "TestDescription", "+", null);
			medicalDsrStockMovementTypeBrowserManager.newMedicalDsrStockMovementType(movementType);
		})
						.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	void newMedicalDsrStockMovementType_shouldThrow_whenCategoryIsNotAllowed() throws Exception {
		assertThatThrownBy(() -> {
			MovementType movementType = new MovementType("ZZABCD", "TestDescription", "+", "abcdefg");
			medicalDsrStockMovementTypeBrowserManager.newMedicalDsrStockMovementType(movementType);
		})
						.isInstanceOf(OHDataValidationException.class);
	}

	private String setupTestMovementType(boolean usingSet) throws OHException {
		MovementType movementType = testMovementType.setup(usingSet);
		medicalDsrStockMovementTypeIoOperationRepository.saveAndFlush(movementType);
		return movementType.getCode();
	}

	private void checkMovementTypeIntoDb(String code) throws OHException {
		MovementType foundMovementType = medicalDsrStockMovementTypeIoOperation.findOneByCode(code);
		testMovementType.check(foundMovementType);
	}

}