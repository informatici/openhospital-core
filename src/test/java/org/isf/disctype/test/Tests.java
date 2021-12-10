/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2021 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.disctype.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.assertj.core.api.Condition;
import org.isf.OHCoreTestCase;
import org.isf.disctype.manager.DischargeTypeBrowserManager;
import org.isf.disctype.model.DischargeType;
import org.isf.disctype.service.DischargeTypeIoOperation;
import org.isf.disctype.service.DischargeTypeIoOperationRepository;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class Tests extends OHCoreTestCase {

	private static TestDischargeType testDischargeType;

	@Autowired
	DischargeTypeIoOperation dischargeTypeIoOperation;
	@Autowired
	DischargeTypeIoOperationRepository dischargeTypeIoOperationRepository;
	@Autowired
	DischargeTypeBrowserManager dischargeTypeBrowserManager;

	@BeforeClass
	public static void setUpClass() {
		testDischargeType = new TestDischargeType();
	}

	@Before
	public void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	public void testDischargeTypeGets() throws Exception {
		String code = setupTestDischargeType(false);
		checkDischargeTypeIntoDb(code);
	}

	@Test
	public void testDischargeTypeSets() throws Exception {
		String code = setupTestDischargeType(true);
		checkDischargeTypeIntoDb(code);
	}

	@Test
	public void testIoGetDischargeType() throws Exception {
		String code = setupTestDischargeType(false);
		DischargeType foundDischargeType = dischargeTypeIoOperationRepository.findById(code).get();
		List<DischargeType> dischargeTypes = dischargeTypeIoOperation.getDischargeType();
		assertThat(dischargeTypes.get(dischargeTypes.size() - 1).getDescription()).isEqualTo(foundDischargeType.getDescription());
	}

	@Test
	public void testIoNewDischargeType() throws Exception {
		DischargeType dischargeType = testDischargeType.setup(true);
		boolean result = dischargeTypeIoOperation.newDischargeType(dischargeType);
		assertThat(result).isTrue();
		checkDischargeTypeIntoDb(dischargeType.getCode());
	}

	@Test
	public void testIoIsCodePresent() throws Exception {
		String code = setupTestDischargeType(false);
		boolean result = dischargeTypeIoOperation.isCodePresent(code);
		assertThat(result).isTrue();
	}

	@Test
	public void testIoDeleteDischargeType() throws Exception {
		String code = setupTestDischargeType(false);
		DischargeType foundDischargeType = dischargeTypeIoOperationRepository.findById(code).get();
		boolean result = dischargeTypeIoOperation.deleteDischargeType(foundDischargeType);
		assertThat(result).isTrue();
		result = dischargeTypeIoOperation.isCodePresent(code);
		assertThat(result).isFalse();
	}

	@Test
	public void testIoUpdateDischargeType() throws Exception {
		String code = setupTestDischargeType(false);
		DischargeType foundDischargeType = dischargeTypeIoOperationRepository.findById(code).get();
		foundDischargeType.setDescription("Update");
		boolean result = dischargeTypeIoOperation.updateDischargeType(foundDischargeType);
		assertThat(result).isTrue();
		DischargeType updateDischargeType = dischargeTypeIoOperationRepository.findById(code).get();
		assertThat(updateDischargeType.getDescription()).isEqualTo("Update");
	}

	@Test
	public void testMgrGetDischargeType() throws Exception {
		String code = setupTestDischargeType(false);
		DischargeType foundDischargeType = dischargeTypeIoOperationRepository.findById(code).get();
		List<DischargeType> dischargeTypes = dischargeTypeBrowserManager.getDischargeType();
		assertThat(dischargeTypes.get(dischargeTypes.size() - 1).getDescription()).isEqualTo(foundDischargeType.getDescription());
	}

	@Test
	public void testMgrNewDischargeType() throws Exception {
		DischargeType dischargeType = testDischargeType.setup(true);
		boolean result = dischargeTypeBrowserManager.newDischargeType(dischargeType);
		assertThat(result).isTrue();
		checkDischargeTypeIntoDb(dischargeType.getCode());
	}

	@Test
	public void testMgrIsCodePresent() throws Exception {
		String code = setupTestDischargeType(false);
		boolean result = dischargeTypeBrowserManager.isCodePresent(code);
		assertThat(result).isTrue();
	}

	@Test
	public void testMgrDeleteDischargeType() throws Exception {
		String code = setupTestDischargeType(false);
		DischargeType foundDischargeType = dischargeTypeIoOperationRepository.findById(code).get();
		boolean result = dischargeTypeBrowserManager.deleteDischargeType(foundDischargeType);
		assertThat(result).isTrue();
		result = dischargeTypeBrowserManager.isCodePresent(code);
		assertThat(result).isFalse();
	}

	@Test
	public void testMgrUpdateDischargeType() throws Exception {
		String code = setupTestDischargeType(false);
		DischargeType foundDischargeType = dischargeTypeIoOperationRepository.findById(code).get();
		foundDischargeType.setDescription("Update");
		boolean result = dischargeTypeBrowserManager.updateDischargeType(foundDischargeType);
		assertThat(result).isTrue();
		DischargeType updateDischargeType = dischargeTypeIoOperationRepository.findById(code).get();
		assertThat(updateDischargeType.getDescription()).isEqualTo("Update");
	}

	@Test
	public void testMgrValidateDeleteDischargeType() throws Exception {
		DischargeType dischargeType = new DischargeType("D", "TestDescription");
		dischargeTypeIoOperationRepository.saveAndFlush(dischargeType);
		DischargeType foundDischargeType = dischargeTypeIoOperationRepository.findById(dischargeType.getCode()).get();
		assertThatThrownBy(() -> dischargeTypeBrowserManager.deleteDischargeType(foundDischargeType))
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
	}

	@Test
	public void testMgrValidateDischargeType() throws Exception {
		String code = setupTestDischargeType(false);
		DischargeType foundDischargeType = dischargeTypeIoOperationRepository.findById(code).get();
		foundDischargeType.setDescription("Update");
		boolean result = dischargeTypeBrowserManager.updateDischargeType(foundDischargeType);
		// empty string
		foundDischargeType.setCode("");
		assertThatThrownBy(() -> dischargeTypeBrowserManager.updateDischargeType(foundDischargeType))
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
		// too long
		foundDischargeType.setCode("123456789ABCDEF");
		assertThatThrownBy(() -> dischargeTypeBrowserManager.updateDischargeType(foundDischargeType))
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
		// key already exists
		foundDischargeType.setCode(code);
		assertThatThrownBy(() -> dischargeTypeBrowserManager.newDischargeType(foundDischargeType))
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
		// description empty
		foundDischargeType.setDescription("");
		assertThatThrownBy(() -> dischargeTypeBrowserManager.updateDischargeType(foundDischargeType))
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
	}

	@Test
	public void testDischargeTypeEqualHashToString() throws Exception {
		String code = setupTestDischargeType(false);
		DischargeType dischargeType = dischargeTypeIoOperationRepository.findById(code).get();
		DischargeType dischargeType2 = new DischargeType("someCode", "someDescription");
		assertThat(dischargeType.equals(dischargeType)).isTrue();
		assertThat(dischargeType)
				.isNotEqualTo(dischargeType2)
				.isNotEqualTo("xyzzy");
		dischargeType2.setCode(code);
		assertThat(dischargeType).isEqualTo(dischargeType2);

		assertThat(dischargeType.hashCode()).isPositive();

		assertThat(dischargeType2).hasToString("someDescription");
	}

	private String setupTestDischargeType(boolean usingSet) throws OHException {
		DischargeType dischargeType = testDischargeType.setup(usingSet);
		dischargeTypeIoOperationRepository.saveAndFlush(dischargeType);
		return dischargeType.getCode();
	}

	private void checkDischargeTypeIntoDb(String code) throws OHException {
		DischargeType foundDischargeType = dischargeTypeIoOperationRepository.findById(code).get();
		testDischargeType.check(foundDischargeType);
	}
}