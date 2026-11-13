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
package org.isf.disctype.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.disctype.TestDischargeType;
import org.isf.disctype.model.DischargeType;
import org.isf.utils.exception.OHException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class DischargeTypeIoOperationTest extends OHCoreTestCase {

	private static TestDischargeType testDischargeType;

	@Autowired
	DischargeTypeIoOperation dischargeTypeIoOperation;
	@Autowired
	DischargeTypeIoOperationRepository dischargeTypeIoOperationRepository;

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
		List<DischargeType> dischargeTypes = dischargeTypeIoOperation.getDischargeType();
		assertThat(dischargeTypes.get(dischargeTypes.size() - 1).getDescription()).isEqualTo(foundDischargeType.getDescription());
	}

	@Test
	void newDischargeType_shouldSaveDischargeType() throws Exception {
		DischargeType dischargeType = testDischargeType.setup(true);
		DischargeType newDischargeType = dischargeTypeIoOperation.newDischargeType(dischargeType);
		checkDischargeTypeIntoDb(newDischargeType.getCode());
	}

	@Test
	void isCodePresent_shouldReturnTrue_whenCodeExists() throws Exception {
		String code = setupTestDischargeType(false);
		boolean result = dischargeTypeIoOperation.isCodePresent(code);
		assertThat(result).isTrue();
	}

	@Test
	void deleteDischargeType_shouldRemoveDischargeType() throws Exception {
		String code = setupTestDischargeType(false);
		DischargeType foundDischargeType = dischargeTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(foundDischargeType).isNotNull();
		dischargeTypeIoOperation.deleteDischargeType(foundDischargeType);
		assertThat(dischargeTypeIoOperation.isCodePresent(code)).isFalse();
	}

	@Test
	void updateDischargeType_shouldSaveNewDescription() throws Exception {
		String code = setupTestDischargeType(false);
		DischargeType foundDischargeType = dischargeTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(foundDischargeType).isNotNull();
		foundDischargeType.setDescription("Update");
		DischargeType updatedDischargeType = dischargeTypeIoOperation.updateDischargeType(foundDischargeType);
		assertThat(updatedDischargeType.getDescription()).isEqualTo("Update");
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
