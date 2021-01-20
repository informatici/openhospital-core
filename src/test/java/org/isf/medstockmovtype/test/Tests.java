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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;

import org.isf.OHCoreTestCase;
import org.isf.medstockmovtype.manager.MedicaldsrstockmovTypeBrowserManager;
import org.isf.medstockmovtype.model.MovementType;
import org.isf.medstockmovtype.service.MedicalStockMovementTypeIoOperation;
import org.isf.medstockmovtype.service.MedicalStockMovementTypeIoOperationRepository;
import org.isf.utils.exception.OHDataIntegrityViolationException;
import org.isf.utils.exception.OHDataValidationException;
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
	@Autowired
	MedicaldsrstockmovTypeBrowserManager medicaldsrstockmovTypeBrowserManager;

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
	public void testIoGetMovementTypeNull() throws Exception {
		assertThat(medicalStockMovementTypeIoOperation.getMovementType("notThere")).isNull();
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

	@Test
	public void testMgrGetMovementType() throws Exception {
		String code = _setupTestMovementType(false);
		MovementType foundMovementType = medicaldsrstockmovTypeBrowserManager.getMovementType(code);
		ArrayList<MovementType> movementTypes = medicaldsrstockmovTypeBrowserManager.getMedicaldsrstockmovType();
		assertThat(movementTypes.get(movementTypes.size() - 1).getDescription()).isEqualTo(foundMovementType.getDescription());
	}

	@Test
	public void testMgrGetMovementTypeNull() throws Exception {
		assertThat(medicaldsrstockmovTypeBrowserManager.getMovementType("notThere")).isNull();
	}

	@Test
	public void testMgrUpdateMovementType() throws Exception {
		String code = _setupTestMovementType(false);
		MovementType foundMovementType = medicaldsrstockmovTypeBrowserManager.getMovementType(code);
		foundMovementType.setDescription("Update");
		assertThat(medicaldsrstockmovTypeBrowserManager.updateMedicaldsrstockmovType(foundMovementType)).isTrue();
		MovementType updateMovementType = medicaldsrstockmovTypeBrowserManager.getMovementType(code);
		assertThat(updateMovementType.getDescription()).isEqualTo("Update");
	}

	@Test
	public void testMgrNewMovementType() throws Exception {
		MovementType movementType = testMovementType.setup(true);
		assertThat(medicaldsrstockmovTypeBrowserManager.newMedicaldsrstockmovType(movementType)).isTrue();
		_checkMovementTypeIntoDb(movementType.getCode());
	}

	@Test
	public void testMgrCodeControl() throws Exception {
		String code = _setupTestMovementType(false);
		assertThat(medicaldsrstockmovTypeBrowserManager.codeControl(code)).isTrue();
	}

	@Test
	public void testMgrDeleteMovementType() throws Exception {
		String code = _setupTestMovementType(false);
		MovementType foundMovementType = medicaldsrstockmovTypeBrowserManager.getMovementType(code);
		assertThat(medicaldsrstockmovTypeBrowserManager.deleteMedicaldsrstockmovType(foundMovementType)).isTrue();
		assertThat(medicaldsrstockmovTypeBrowserManager.codeControl(code)).isFalse();
	}

	@Test
	public void testMgrMovementTypeValidationNoKey() throws Exception {
		assertThatThrownBy(() ->
		{
			MovementType movementType = new MovementType("", "TestDescription", "+");
			medicaldsrstockmovTypeBrowserManager.newMedicaldsrstockmovType(movementType);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	public void testMgrMovementTypeValidationKeyTooLong() throws Exception {
		assertThatThrownBy(() ->
		{
			MovementType movementType = new MovementType("abcdefghijklmnopqrstuvwxyz", "TestDescription", "+");
			medicaldsrstockmovTypeBrowserManager.newMedicaldsrstockmovType(movementType);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	public void testMgrMovementTypeValidationTypeTooLong() throws Exception {
		assertThatThrownBy(() ->
		{
			MovementType movementType = new MovementType("ZZABCD", "TestDescription", "+++++");
			medicaldsrstockmovTypeBrowserManager.newMedicaldsrstockmovType(movementType);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	public void testMgrMovementTypeValidationNoDescription() throws Exception {
		assertThatThrownBy(() ->
		{
			MovementType movementType = new MovementType("ZZABCD", "", "+");
			medicaldsrstockmovTypeBrowserManager.newMedicaldsrstockmovType(movementType);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	public void testMgrMovementTypeValidationCodeAlreadyExists() throws Exception {
		assertThatThrownBy(() ->
		{
			MovementType movementType = testMovementType.setup(false);
			medicalStockMovementTypeIoOperationRepository.saveAndFlush(movementType);
			MovementType movementType2 = new MovementType(movementType.getCode(), movementType.getDescription(), movementType.getType());
			medicaldsrstockmovTypeBrowserManager.newMedicaldsrstockmovType(movementType2);
		})
				.isInstanceOf(OHDataIntegrityViolationException.class);
	}

	@Test
	public void testMovementTypeToString() throws Exception {
		MovementType movementType = new MovementType("ZZABCD", "TestDescription", "+");
		assertThat(movementType.toString()).isEqualTo("TestDescription");
	}

	@Test
	public void testMovementTypeEquals() throws Exception {
		MovementType movementType1 = new MovementType("ZZABCD", "TestDescription", "+");
		MovementType movementType2 = new MovementType("ABCDZZ", "TestDescription", "+");
		MovementType movementType3 = new MovementType("ZZABCD", "AnotherDescription", "+");
		MovementType movementType4 = new MovementType("ZZABCD", "TestDescription", "++");

		assertThat(movementType1.equals(movementType1)).isTrue();
		assertThat(movementType1.equals(new Integer(-1))).isFalse();
		assertThat(movementType1.equals(movementType2)).isFalse();
		assertThat(movementType1.equals(movementType3)).isTrue();
		assertThat(movementType1.equals(movementType4)).isTrue();
	}

	@Test
	public void testMovementTypeHashCode() {
		MovementType movementType = new MovementType("ZZABCD", "TestDescription", "+");
		// generate hashCode
		int hashCode = movementType.hashCode();
		assertThat(hashCode).isNotZero();
		// used computed value
		assertThat(movementType.hashCode()).isEqualTo(hashCode);
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