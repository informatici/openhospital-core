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
package org.isf.medstockmovtype.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.medstockmovtype.TestMovementType;
import org.isf.medstockmovtype.model.MovementType;
import org.isf.utils.exception.OHException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MedicalDsrStockMovementTypeIoOperationTest extends OHCoreTestCase {

	private static TestMovementType testMovementType;

	@Autowired
	MedicalDsrStockMovementTypeIoOperation medicalDsrStockMovementTypeIoOperation;
	@Autowired
	MedicalDsrStockMovementTypeIoOperationRepository medicalDsrStockMovementTypeIoOperationRepository;

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
		MovementType foundMovementType = medicalDsrStockMovementTypeIoOperation.findOneByCode(code);
		List<MovementType> movementTypes = medicalDsrStockMovementTypeIoOperation.getMedicalDsrStockMovementType();
		assertThat(movementTypes.get(movementTypes.size() - 1).getDescription()).isEqualTo(foundMovementType.getDescription());
	}

	@Test
	void findOneByCode_shouldReturnNull_whenCodeDoesNotExist() throws Exception {
		assertThat(medicalDsrStockMovementTypeIoOperation.findOneByCode("notThere")).isNull();
	}

	@Test
	void updateMedicalDsrStockMovementType_shouldSaveNewDescription() throws Exception {
		String code = setupTestMovementType(false);
		MovementType foundMovementType = medicalDsrStockMovementTypeIoOperation.findOneByCode(code);
		foundMovementType.setDescription("Update");
		MovementType result = medicalDsrStockMovementTypeIoOperation.updateMedicalDsrStockMovementType(foundMovementType);
		assertThat(result).isNotNull();
		MovementType updateMovementType = medicalDsrStockMovementTypeIoOperation.findOneByCode(code);
		assertThat(updateMovementType.getDescription()).isEqualTo("Update");
	}

	@Test
	void newMedicalDsrStockMovementType_shouldSaveMovementType() throws Exception {
		MovementType movementType = testMovementType.setup(true);
		MovementType result = medicalDsrStockMovementTypeIoOperation.newMedicalDsrStockMovementType(movementType);
		assertThat(result).isNotNull();
		checkMovementTypeIntoDb(movementType.getCode());
	}

	@Test
	void isCodePresent_shouldReturnTrue_whenCodeExists() throws Exception {
		String code = setupTestMovementType(false);
		boolean result = medicalDsrStockMovementTypeIoOperation.isCodePresent(code);
		assertThat(result).isTrue();
	}

	@Test
	void deleteMedicalDsrStockMovementType_shouldRemoveMovementType() throws Exception {
		String code = setupTestMovementType(false);
		MovementType foundMovementType = medicalDsrStockMovementTypeIoOperation.findOneByCode(code);
		medicalDsrStockMovementTypeIoOperation.deleteMedicalDsrStockMovementType(foundMovementType);
		assertThat(medicalDsrStockMovementTypeIoOperation.isCodePresent(code)).isFalse();
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
