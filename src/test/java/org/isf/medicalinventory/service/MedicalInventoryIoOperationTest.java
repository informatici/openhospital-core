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
package org.isf.medicalinventory.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.medicalinventory.model.InventoryStatus;
import org.isf.medicalinventory.model.InventoryType;
import org.isf.medicalinventory.model.MedicalInventory;
import org.isf.medicalsinventory.TestMedicalInventory;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.isf.ward.TestWard;
import org.isf.ward.model.Ward;
import org.isf.ward.service.WardIoOperationRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MedicalInventoryIoOperationTest extends OHCoreTestCase {

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
	void getMedicalInventory_shouldReturnSavedInventory() throws Exception {
		int id = setupTestMedicalInventory();
		MedicalInventory foundMedicalinventory = medIvnIoOperationRepository.findById(id).orElse(null);
		assertThat(foundMedicalinventory).isNotNull();
		List<MedicalInventory> medicalInventories = medicalInventoryIoOperation.getMedicalInventory();
		assertThat(medicalInventories.get(medicalInventories.size() - 1).getId()).isEqualTo((Integer) id);
	}

	@Test
	void newMedicalInventory_shouldSaveInventory() throws Exception {
		Ward ward = testWard.setup(false);
		MedicalInventory medicalInventory = testMedicalInventory.setup(ward, false);
		MedicalInventory newMedicalInventory = medicalInventoryIoOperation.newMedicalInventory(medicalInventory);
		checkMedicalInventoryIntoDb(newMedicalInventory.getId());
		assertThat(medicalInventoryIoOperation.referenceExists(newMedicalInventory.getInventoryReference())).isTrue();
	}

	@Test
	void updateMedicalInventory_shouldSaveNewStatus() throws Exception {
		Integer id = setupTestMedicalInventory();
		MedicalInventory foundMedicalInventory = medIvnIoOperationRepository.findById(id).orElse(null);
		assertThat(foundMedicalInventory).isNotNull();
		String status = "canceled";
		foundMedicalInventory.setStatus(status);
		MedicalInventory updatedMedicalInventory = medicalInventoryIoOperation.updateMedicalInventory(foundMedicalInventory);
		assertThat(updatedMedicalInventory.getStatus()).isEqualTo(status);
	}

	@Test
	void getMedicalInventoryByStatusAndInventoryType_shouldReturnOnlyInventoriesWithGivenStatus() throws Exception {
		int id = setupTestMedicalInventory();
		MedicalInventory firstMedicalInventory = medIvnIoOperationRepository.findById(id).orElse(null);
		assertThat(firstMedicalInventory).isNotNull();
		Ward ward = testWard.setup(false);
		MedicalInventory inventory = testMedicalInventory.setup(ward, false);
		String status = InventoryStatus.canceled.toString();
		String inventoryType = InventoryType.main.toString();
		inventory.setStatus(status);
		inventory.setInventoryType(inventoryType);
		MedicalInventory secondMedicalInventory = medIvnIoOperationRepository.saveAndFlush(inventory);
		assertThat(secondMedicalInventory).isNotNull();
		List<MedicalInventory> medicalInventories = medicalInventoryIoOperation.getMedicalInventoryByStatusAndInventoryType(firstMedicalInventory.getStatus(),
			firstMedicalInventory.getInventoryType());
		assertThat(medicalInventories).hasSize(1);
		assertThat(medicalInventories.get(0).getStatus()).isEqualTo(firstMedicalInventory.getStatus());
	}

	@Test
	void getMedicalInventoryByStatusAndWard_shouldReturnOnlyInventoriesWithGivenStatusAndWard() throws Exception {
		int id = setupTestMedicalInventory();
		MedicalInventory firstMedicalInventory = medIvnIoOperationRepository.findById(id).orElse(null);
		assertThat(firstMedicalInventory).isNotNull();
		Ward ward = testWard.setup(false);
		MedicalInventory inventory = testMedicalInventory.setup(ward, false);
		inventory.setId(null);
		String wardCode = "P";
		String status = InventoryStatus.validated.toString();
		inventory.setWardCode(wardCode);
		inventory.setStatus(status);
		MedicalInventory secondMedicalInventory = medIvnIoOperationRepository.saveAndFlush(inventory);
		assertThat(secondMedicalInventory).isNotNull();
		List<MedicalInventory> medicalinventories = medicalInventoryIoOperation
			.getMedicalInventoryByStatusAndWard(firstMedicalInventory.getStatus(), firstMedicalInventory.getWardCode());
		assertThat(medicalinventories).hasSize(1);
		assertThat(medicalinventories.get(0).getStatus()).isEqualTo(firstMedicalInventory.getStatus());
		assertThat(medicalinventories.get(0).getWardCode()).isEqualTo(firstMedicalInventory.getWardCode());
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
