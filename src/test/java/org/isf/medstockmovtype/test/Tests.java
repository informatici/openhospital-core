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
package org.isf.medstockmovtype.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;

import org.isf.OHCoreTestCase;
import org.isf.medstockmovtype.model.MovementType;
import org.isf.medstockmovtype.service.MedicalStockMovementTypeIoOperation;
import org.isf.medstockmovtype.service.MedicalStockMovementTypeIoOperationRepository;
import org.isf.utils.exception.OHException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class Tests extends OHCoreTestCase {

	private static TestMovementType testMovementType;

	@Autowired
	MedicalStockMovementTypeIoOperation medicalStockMovementTypeIoOperation;
	@Autowired
	MedicalStockMovementTypeIoOperationRepository medicalStockMovementTypeIoOperationRepository;

	@BeforeClass
	public static void setUpClass() {
		testMovementType = new TestMovementType();
	}

	@Before
	public void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	public void testMovementTypeGets() throws Exception {
		String code = _setupTestMovementType(false);
		_checkMovementTypeIntoDb(code);
	}

	@Test
	public void testMovementTypeSets() throws Exception {
		String code = _setupTestMovementType(true);
		_checkMovementTypeIntoDb(code);
	}

	@Test
	public void testIoGetMovementType() throws Exception {
		String code = _setupTestMovementType(false);
		MovementType foundMovementType = medicalStockMovementTypeIoOperation.getMovementType(code);
		ArrayList<MovementType> movementTypes = medicalStockMovementTypeIoOperation.getMedicaldsrstockmovType();
		assertThat(movementTypes.get(movementTypes.size() - 1).getDescription()).isEqualTo(foundMovementType.getDescription());
	}

	@Test
	public void testIoUpdateMovementType() throws Exception {
		String code = _setupTestMovementType(false);
		MovementType foundMovementType = medicalStockMovementTypeIoOperation.getMovementType(code);
		foundMovementType.setDescription("Update");
		boolean result = medicalStockMovementTypeIoOperation.updateMedicaldsrstockmovType(foundMovementType);
		assertThat(result).isTrue();
		MovementType updateMovementType = medicalStockMovementTypeIoOperation.getMovementType(code);
		assertThat(updateMovementType.getDescription()).isEqualTo("Update");
	}

	@Test
	public void testIoNewMovementType() throws Exception {
		MovementType movementType = testMovementType.setup(true);
		boolean result = medicalStockMovementTypeIoOperation.newMedicaldsrstockmovType(movementType);
		assertThat(result).isTrue();
		_checkMovementTypeIntoDb(movementType.getCode());
	}

	@Test
	public void testIoIsCodePresent() throws Exception {
		String code = _setupTestMovementType(false);
		boolean result = medicalStockMovementTypeIoOperation.isCodePresent(code);
		assertThat(result).isTrue();
	}

	@Test
	public void testIoDeleteMovementType() throws Exception {
		String code = _setupTestMovementType(false);
		MovementType foundMovementType = medicalStockMovementTypeIoOperation.getMovementType(code);
		boolean result = medicalStockMovementTypeIoOperation.deleteMedicaldsrstockmovType(foundMovementType);
		assertThat(result).isTrue();
		result = medicalStockMovementTypeIoOperation.isCodePresent(code);
		assertThat(result).isFalse();
	}

	private String _setupTestMovementType(boolean usingSet) throws OHException {
		MovementType movementType = testMovementType.setup(usingSet);
		medicalStockMovementTypeIoOperationRepository.saveAndFlush(movementType);
		return movementType.getCode();
	}

	private void _checkMovementTypeIntoDb(String code) throws OHException {
		MovementType foundMovementType = medicalStockMovementTypeIoOperation.getMovementType(code);
		testMovementType.check(foundMovementType);
	}
}