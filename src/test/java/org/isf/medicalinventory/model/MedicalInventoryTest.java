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
package org.isf.medicalinventory.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.isf.OHCoreTestCase;
import org.isf.medicalinventory.service.MedicalInventoryIoOperation;
import org.isf.medicalinventory.service.MedicalInventoryIoOperationRepository;
import org.isf.medicalsinventory.TestMedicalInventory;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.time.TimeTools;
import org.isf.ward.TestWard;
import org.isf.ward.model.Ward;
import org.isf.ward.service.WardIoOperationRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MedicalInventoryTest extends OHCoreTestCase {

	private static TestMedicalInventory testMedicalInventory;
	private static TestWard testWard;

	@Autowired
	MedicalInventoryIoOperationRepository medIvnIoOperationRepository;

	@Autowired
	WardIoOperationRepository wardIoOperationRepository;

	@Autowired
	MedicalInventoryIoOperation medicalInventoryIoOperation;

	@BeforeAll
	static void setUpClass() {
		testMedicalInventory = new TestMedicalInventory();
		testWard = new TestWard();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void constructor_shouldSetFields() throws Exception {
		int code = setupTestMedicalInventory();
		checkMedicalInventoryIntoDb(code);
	}

	@Test
	void setters_shouldSetDateUserReferenceTypeAndLock() throws Exception {
		int id = setupTestMedicalInventory();
		MedicalInventory medicalInventory = medIvnIoOperationRepository.findById(id).orElse(null);
		assertThat(medicalInventory).isNotNull();

		LocalDateTime now = TimeTools.getNow();
		medicalInventory.setInventoryDate(now);
		assertThat(medicalInventory.getInventoryDate()).isEqualTo(now);

		String user = "admin";
		medicalInventory.setUser(user);
		assertThat(medicalInventory.getUser()).isEqualTo(user);

		String inventoryReference = "ref";
		medicalInventory.setInventoryReference(inventoryReference);
		assertThat(medicalInventory.getInventoryReference()).isEqualTo(inventoryReference);

		String inventoryType = InventoryType.main.toString();
		medicalInventory.setInventoryType(inventoryType);
		assertThat(medicalInventory.getInventoryType()).isEqualTo(inventoryType);

		int lock = -99;
		medicalInventory.setLock(lock);
		assertThat(medicalInventory.getLock()).isEqualTo(lock);
	}

	private int setupTestMedicalInventory() throws OHException, OHServiceException {
		Ward ward = testWard.setup(false);
		MedicalInventory medicalInventory = testMedicalInventory.setup(ward, false);
		wardIoOperationRepository.saveAndFlush(ward);
		MedicalInventory savedMedicalInventory = medicalInventoryIoOperation.newMedicalInventory(medicalInventory);
		return savedMedicalInventory.getId();
	}

	private void checkMedicalInventoryIntoDb(int id) throws OHException {
		MedicalInventory foundMedicalInventory = medIvnIoOperationRepository.findById(id).orElse(null);
		assertThat(foundMedicalInventory).isNotNull();
		testMedicalInventory.check(foundMedicalInventory, id);
	}

}
