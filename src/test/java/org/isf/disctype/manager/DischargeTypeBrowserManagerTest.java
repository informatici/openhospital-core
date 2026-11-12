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
package org.isf.disctype.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.assertj.core.api.Condition;
import org.isf.OHCoreTestCase;
import org.isf.disctype.TestDischargeType;
import org.isf.disctype.model.DischargeType;
import org.isf.disctype.service.DischargeTypeIoOperationRepository;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class DischargeTypeBrowserManagerTest extends OHCoreTestCase {

	private static TestDischargeType testDischargeType;

	@Autowired
	DischargeTypeIoOperationRepository dischargeTypeIoOperationRepository;
	@Autowired
	DischargeTypeBrowserManager dischargeTypeBrowserManager;

	@BeforeAll
	static void setUpClass() {
		testDischargeType = new TestDischargeType();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void getDischargeType_shouldReturnSavedDischargeType() throws Exception {
		String code = setupTestDischargeType(false);
		DischargeType foundDischargeType = dischargeTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(foundDischargeType).isNotNull();
		List<DischargeType> dischargeTypes = dischargeTypeBrowserManager.getDischargeType();
		assertThat(dischargeTypes.get(dischargeTypes.size() - 1).getDescription()).isEqualTo(foundDischargeType.getDescription());
	}

	@Test
	void newDischargeType_shouldSaveDischargeType() throws Exception {
		DischargeType dischargeType = testDischargeType.setup(true);
		DischargeType newDischargeType = dischargeTypeBrowserManager.newDischargeType(dischargeType);
		checkDischargeTypeIntoDb(newDischargeType.getCode());
	}

	@Test
	void isCodePresent_shouldReturnTrue_whenCodeExists() throws Exception {
		String code = setupTestDischargeType(false);
		boolean result = dischargeTypeBrowserManager.isCodePresent(code);
		assertThat(result).isTrue();
	}

	@Test
	void deleteDischargeType_shouldRemoveDischargeType() throws Exception {
		String code = setupTestDischargeType(false);
		DischargeType foundDischargeType = dischargeTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(foundDischargeType).isNotNull();
		dischargeTypeBrowserManager.deleteDischargeType(foundDischargeType);
		assertThat(dischargeTypeBrowserManager.isCodePresent(code)).isFalse();
	}

	@Test
	void updateDischargeType_shouldSaveNewDescription() throws Exception {
		String code = setupTestDischargeType(false);
		DischargeType foundDischargeType = dischargeTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(foundDischargeType).isNotNull();
		foundDischargeType.setDescription("Update");
		DischargeType updatedDischargeType = dischargeTypeBrowserManager.updateDischargeType(foundDischargeType);
		assertThat(updatedDischargeType.getDescription()).isEqualTo("Update");
	}

	@Test
	void deleteDischargeType_shouldThrow_whenCodeIsD() throws Exception {
		DischargeType dischargeType = new DischargeType("D", "TestDescription");
		dischargeTypeIoOperationRepository.saveAndFlush(dischargeType);
		DischargeType foundDischargeType = dischargeTypeIoOperationRepository.findById(dischargeType.getCode()).orElse(null);
		assertThat(foundDischargeType).isNotNull();
		assertThatThrownBy(() -> dischargeTypeBrowserManager.deleteDischargeType(foundDischargeType))
			.isInstanceOf(OHDataValidationException.class)
			.has(
				new Condition<Throwable>(
					e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error")
			);
	}

	@Test
	void newAndUpdateDischargeType_shouldThrow_whenCodeOrDescriptionIsInvalid() throws Exception {
		String code = setupTestDischargeType(false);
		DischargeType foundDischargeType = dischargeTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(foundDischargeType).isNotNull();
		foundDischargeType.setDescription("Update");
		dischargeTypeBrowserManager.updateDischargeType(foundDischargeType);
		// empty string
		foundDischargeType.setCode("");
		assertThatThrownBy(() -> dischargeTypeBrowserManager.updateDischargeType(foundDischargeType))
			.isInstanceOf(OHDataValidationException.class)
			.has(
				new Condition<Throwable>(
					e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error")
			);
		// too long
		foundDischargeType.setCode("123456789ABCDEF");
		assertThatThrownBy(() -> dischargeTypeBrowserManager.updateDischargeType(foundDischargeType))
			.isInstanceOf(OHDataValidationException.class)
			.has(
				new Condition<Throwable>(
					e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error")
			);
		// key already exists
		foundDischargeType.setCode(code);
		assertThatThrownBy(() -> dischargeTypeBrowserManager.newDischargeType(foundDischargeType))
			.isInstanceOf(OHDataValidationException.class)
			.has(
				new Condition<Throwable>(
					e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error")
			);
		// description empty
		foundDischargeType.setDescription("");
		assertThatThrownBy(() -> dischargeTypeBrowserManager.updateDischargeType(foundDischargeType))
			.isInstanceOf(OHDataValidationException.class)
			.has(
				new Condition<Throwable>(
					e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error")
			);
	}

	private String setupTestDischargeType(boolean usingSet) throws OHException {
		DischargeType dischargeType = testDischargeType.setup(usingSet);
		dischargeTypeIoOperationRepository.saveAndFlush(dischargeType);
		return dischargeType.getCode();
	}

	private void checkDischargeTypeIntoDb(String code) throws OHException {
		DischargeType foundDischargeType = dischargeTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(foundDischargeType).isNotNull();
		testDischargeType.check(foundDischargeType);
	}

}