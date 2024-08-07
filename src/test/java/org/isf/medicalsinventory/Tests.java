/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2024 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.medicalsinventory;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.medicalinventory.manager.MedicalInventoryManager;
import org.isf.medicalinventory.manager.MedicalInventoryRowManager;
import org.isf.medicalinventory.model.MedicalInventory;
import org.isf.medicalinventory.model.MedicalInventoryRow;
import org.isf.medicalinventory.service.MedicalInventoryIoOperation;
import org.isf.medicalinventory.service.MedicalInventoryIoOperationRepository;
import org.isf.medicalinventory.service.MedicalInventoryRowIoOperation;
import org.isf.medicalinventory.service.MedicalInventoryRowIoOperationRepository;
import org.isf.medicals.TestMedical;
import org.isf.medicals.model.Medical;
import org.isf.medicals.service.MedicalsIoOperationRepository;
import org.isf.medicalstock.TestLot;
import org.isf.medicalstock.model.Lot;
import org.isf.medicalstock.service.LotIoOperationRepository;
import org.isf.medtype.TestMedicalType;
import org.isf.medtype.model.MedicalType;
import org.isf.medtype.service.MedicalTypeIoOperationRepository;
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
import org.springframework.data.domain.Page;

class Tests extends OHCoreTestCase {
	
	private static TestMedicalInventory testMedicalInventory;
	private static TestMedicalInventoryRow testMedicalInventoryRow;
	private static TestWard testWard;
	private static TestMedical testMedical;
	private static TestLot testLot;
	private static TestMedicalType testMedicalType;

	@Autowired
	MedicalInventoryManager medicalInventoryManager;

	@Autowired
	MedicalInventoryRowManager medicalInventoryRowManager;

	@Autowired
	MedicalInventoryIoOperationRepository medIvnIoOperationRepository;
	
	@Autowired
	WardIoOperationRepository wardIoOperationRepository;
	
	@Autowired
	MedicalInventoryIoOperation medicalInventoryIoOperation;
	
	@Autowired
	MedicalInventoryRowIoOperationRepository medIvnRowIoOperationRepository;
	
	@Autowired
	MedicalInventoryRowIoOperation medIvnRowIoOperation;
	
	@Autowired
	MedicalsIoOperationRepository medicalsIoOperationRepository;
	
	@Autowired
	MedicalTypeIoOperationRepository medicalTypeIoOperationRepository;
	
	@Autowired
	LotIoOperationRepository lotIoOperationRepository;

	@BeforeAll
	static void setUpClass() {
		testMedicalInventory = new TestMedicalInventory();
		testWard = new TestWard();
		testMedicalInventoryRow = new TestMedicalInventoryRow();
		testMedical = new TestMedical();
		testLot = new TestLot();
		testMedicalType = new TestMedicalType();
	}
	
	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}
	
	@Test
	void testMedicalInventoryGets() throws Exception {
		int code = setupTestMedicalInventory(false);
		checkMedicalInventoryIntoDb(code);
	}

	@Test
	void testMedicalInventoryGetsSets() throws Exception {
		int id = setupTestMedicalInventory(false);
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

		String inventoryType = "type";
		medicalInventory.setInventoryType(inventoryType);
		assertThat(medicalInventory.getInventoryType()).isEqualTo(inventoryType);

		int lock = -99;
		medicalInventory.setLock(lock);
		assertThat(medicalInventory.getLock()).isEqualTo(lock);
	}

	@Test
	void testMedicalInventoryRowGetsSets() throws Exception {
		Integer id = setupTestMedicalInventoryRow(false);
		MedicalInventoryRow medicalInventoryRow = medIvnRowIoOperationRepository.findById(id).orElse(null);
		assertThat(medicalInventoryRow).isNotNull();

		id = -99;
		medicalInventoryRow.setId(id);
		assertThat(medicalInventoryRow.getId()).isEqualTo(id);

		double theoreticQty = -17.9;
		medicalInventoryRow.setTheoreticQty(theoreticQty);
		assertThat(medicalInventoryRow.getTheoreticQty()).isEqualTo(theoreticQty);

		double realQty = -37.3;
		medicalInventoryRow.setRealqty(realQty);
		assertThat(medicalInventoryRow.getRealQty()).isEqualTo(realQty);
		medicalInventoryRow.setRealQty(realQty);  // Note the uppercase 'Q'
		assertThat(medicalInventoryRow.getRealQty()).isEqualTo(realQty);

		int lock = -99;
		medicalInventoryRow.setLock(lock);
		assertThat(medicalInventoryRow.getLock()).isEqualTo(lock);

		assertThat(medicalInventoryRow.getSearchString()).isEqualTo("testdescriptiontp1");
	}

	@Test
	void testMgrGetMedicalInventory() throws Exception {
		int id = setupTestMedicalInventory(false);
		MedicalInventory foundMedicalinventory = medIvnIoOperationRepository.findById(id).orElse(null);
		assertThat(foundMedicalinventory).isNotNull();
		List<MedicalInventory> medicalInventories = medicalInventoryManager.getMedicalInventory();
		assertThat(medicalInventories.get(medicalInventories.size() - 1).getId()).isEqualTo((Integer) id);
	}

	@Test
	void testIoGetMedicalInventory() throws Exception {
		int id = setupTestMedicalInventory(false);
		MedicalInventory foundMedicalinventory = medIvnIoOperationRepository.findById(id).orElse(null);
		assertThat(foundMedicalinventory).isNotNull();
		List<MedicalInventory> medicalInventories = medicalInventoryIoOperation.getMedicalInventory();
		assertThat(medicalInventories.get(medicalInventories.size() - 1).getId()).isEqualTo((Integer) id);
	}

	@Test
	void testMgrNewMedicalInventory() throws Exception {
		Ward ward = testWard.setup(false);
		MedicalInventory medicalInventory = testMedicalInventory.setup(ward, false);
		MedicalInventory newMedicalInventory = medicalInventoryManager.newMedicalInventory(medicalInventory);
		checkMedicalInventoryIntoDb(newMedicalInventory.getId());
	}

	@Test
	void testIoNewMedicalInventory() throws Exception {
		Ward ward = testWard.setup(false);
		MedicalInventory medicalInventory = testMedicalInventory.setup(ward, false);
		MedicalInventory newMedicalInventory = medicalInventoryIoOperation.newMedicalInventory(medicalInventory);
		checkMedicalInventoryIntoDb(newMedicalInventory.getId());
		assertThat(medicalInventoryIoOperation.referenceExists(newMedicalInventory.getInventoryReference())).isTrue();
	}
	
	@Test
	void testMgrUpdateMedicalInventory() throws Exception {
		Integer id = setupTestMedicalInventory(false);
		MedicalInventory foundMedicalInventory = medIvnIoOperationRepository.findById(id).orElse(null);
		assertThat(foundMedicalInventory).isNotNull();
		String status = "canceled";
		foundMedicalInventory.setStatus(status);
		MedicalInventory updatedMedicalInventory = medicalInventoryManager.updateMedicalInventory(foundMedicalInventory);
		assertThat(updatedMedicalInventory.getStatus()).isEqualTo(status);
	}

	@Test
	void testIoUpdateMedicalInventory() throws Exception {
		Integer id = setupTestMedicalInventory(false);
		MedicalInventory foundMedicalInventory = medIvnIoOperationRepository.findById(id).orElse(null);
		assertThat(foundMedicalInventory).isNotNull();
		String status = "canceled";
		foundMedicalInventory.setStatus(status);
		MedicalInventory updatedMedicalInventory = medicalInventoryIoOperation.updateMedicalInventory(foundMedicalInventory);
		assertThat(updatedMedicalInventory.getStatus()).isEqualTo(status);
	}

	@Test
	void testMgrDeleteMedicalInventory() throws Exception {
		Integer id = setupTestMedicalInventory(false);
		MedicalInventory foundMedicalInventory = medIvnIoOperationRepository.findById(id).orElse(null);
		assertThat(foundMedicalInventory).isNotNull();
		String reference = foundMedicalInventory.getInventoryReference();
		medicalInventoryManager.deleteMedicalInventory(foundMedicalInventory);
		assertThat(medicalInventoryManager.referenceExists(reference)).isFalse();
	}

	@Test
	void testIoDeleteMedicalInventory() throws Exception {
		Integer id = setupTestMedicalInventory(false);
		MedicalInventory foundMedicalInventory = medIvnIoOperationRepository.findById(id).orElse(null);
		assertThat(foundMedicalInventory).isNotNull();
		String reference = foundMedicalInventory.getInventoryReference();
		Integer code = foundMedicalInventory.getId();
		medicalInventoryIoOperation.deleteMedicalInventory(foundMedicalInventory);
		assertThat(medicalInventoryIoOperation.referenceExists(reference)).isFalse();
		assertThat(medicalInventoryIoOperation.isCodePresent(code)).isFalse();
	}

	@Test
	void testMgrGetMedicalInventoryWithStatus() throws Exception {
		int id = setupTestMedicalInventory(false);
		MedicalInventory firstMedicalInventory = medIvnIoOperationRepository.findById(id).orElse(null);
		assertThat(firstMedicalInventory).isNotNull();
		Ward ward = testWard.setup(false);
		MedicalInventory inventory = testMedicalInventory.setup(ward, false);
		String status = "STATUS";
		inventory.setStatus(status);
		MedicalInventory secondMedicalInventory = medIvnIoOperationRepository.saveAndFlush(inventory);
		assertThat(secondMedicalInventory).isNotNull();
		List<MedicalInventory> medicalInventories = medicalInventoryManager.getMedicalInventoryByStatus(firstMedicalInventory.getStatus());
		assertThat(medicalInventories).hasSize(1);
		assertThat(medicalInventories.get(0).getStatus()).isEqualTo(firstMedicalInventory.getStatus());
	}

	@Test
	void testIoGetMedicalInventoryWithStatus() throws Exception {
		int id = setupTestMedicalInventory(false);
		MedicalInventory firstMedicalInventory = medIvnIoOperationRepository.findById(id).orElse(null);
		assertThat(firstMedicalInventory).isNotNull();
		Ward ward = testWard.setup(false);
		MedicalInventory inventory = testMedicalInventory.setup(ward, false);
		String status = "STATUS";
		inventory.setStatus(status);
		MedicalInventory secondMedicalInventory = medIvnIoOperationRepository.saveAndFlush(inventory);
		assertThat(secondMedicalInventory).isNotNull();
		List<MedicalInventory> medicalInventories = medicalInventoryIoOperation.getMedicalInventoryByStatus(firstMedicalInventory.getStatus());
		assertThat(medicalInventories).hasSize(1);
		assertThat(medicalInventories.get(0).getStatus()).isEqualTo(firstMedicalInventory.getStatus());
	}

	@Test
	void testMgrGetMedicalInventoryWithStatusAndWard() throws Exception {
		int id = setupTestMedicalInventory(false);
		MedicalInventory firstMedicalInventory = medIvnIoOperationRepository.findById(id).orElse(null);
		assertThat(firstMedicalInventory).isNotNull();
		Ward ward = testWard.setup(false);
		MedicalInventory inventory = testMedicalInventory.setup(ward, false);
		int idInventory = 2;
		inventory.setId(idInventory);
		String wardCode = "P";
		String status = "validated";
		inventory.setWard(wardCode);
		inventory.setStatus(status);
		MedicalInventory secondMedicalInventory = medIvnIoOperationRepository.saveAndFlush(inventory);
		assertThat(secondMedicalInventory).isNotNull();
		List<MedicalInventory> medicalinventories = medicalInventoryManager.getMedicalInventoryByStatusAndWard(firstMedicalInventory.getStatus(),
						firstMedicalInventory.getWard());
		assertThat(medicalinventories).hasSize(1);
		assertThat(medicalinventories.get(0).getStatus()).isEqualTo(firstMedicalInventory.getStatus());
		assertThat(medicalinventories.get(0).getWard()).isEqualTo(firstMedicalInventory.getWard());
	}

	@Test
	void testIoGetMedicalInventoryWithStatusAndWard() throws Exception {
		int id = setupTestMedicalInventory(false);
		MedicalInventory firstMedicalInventory = medIvnIoOperationRepository.findById(id).orElse(null);
		assertThat(firstMedicalInventory).isNotNull();
		Ward ward = testWard.setup(false);
		MedicalInventory inventory = testMedicalInventory.setup(ward, false);
		int idInventory = 2;
		inventory.setId(idInventory);
		String wardCode = "P";
		String status = "validated";
		inventory.setWard(wardCode);
		inventory.setStatus(status);
		MedicalInventory secondMedicalInventory = medIvnIoOperationRepository.saveAndFlush(inventory);
		assertThat(secondMedicalInventory).isNotNull();
		List<MedicalInventory> medicalinventories = medicalInventoryIoOperation
				.getMedicalInventoryByStatusAndWard(firstMedicalInventory.getStatus(), firstMedicalInventory.getWard());
		assertThat(medicalinventories).hasSize(1);
		assertThat(medicalinventories.get(0).getStatus()).isEqualTo(firstMedicalInventory.getStatus());
		assertThat(medicalinventories.get(0).getWard()).isEqualTo(firstMedicalInventory.getWard());
	}

	@Test
	void testMgrGetMedicalInventoryByParams() throws Exception {
		int id = setupTestMedicalInventory(false);
		MedicalInventory firstMedicalInventory = medIvnIoOperationRepository.findById(id).orElse(null);
		assertThat(firstMedicalInventory).isNotNull();
		Ward ward = testWard.setup(false);
		MedicalInventory inventory = testMedicalInventory.setup(ward, false);
		int idInventory = 2;
		inventory.setId(idInventory);
		String wardCode = "P";
		String status = "validated";
		inventory.setWard(wardCode);
		inventory.setStatus(status);
		MedicalInventory secondMedicalInventory = medIvnIoOperationRepository.saveAndFlush(inventory);
		assertThat(secondMedicalInventory).isNotNull();
		List<MedicalInventory> medicalinventories = medicalInventoryManager.getMedicalInventoryByParams(secondMedicalInventory.getInventoryDate().minusDays(2),
						secondMedicalInventory.getInventoryDate().plusDays(2), status, secondMedicalInventory.getInventoryType());
		assertThat(medicalinventories).hasSize(1);
		assertThat(medicalinventories.get(0).getStatus()).isEqualTo(secondMedicalInventory.getStatus());
		assertThat(medicalinventories.get(0).getWard()).isEqualTo(secondMedicalInventory.getWard());
	}

	@Test
	void testMgrGetMedicalInventoryByParamsNullStatus() throws Exception {
		int id = setupTestMedicalInventory(false);
		MedicalInventory firstMedicalInventory = medIvnIoOperationRepository.findById(id).orElse(null);
		assertThat(firstMedicalInventory).isNotNull();
		Ward ward = testWard.setup(false);
		MedicalInventory inventory = testMedicalInventory.setup(ward, false);
		int idInventory = 2;
		inventory.setId(idInventory);
		String wardCode = "P";
		String status = "validated";
		inventory.setWard(wardCode);
		inventory.setStatus(status);
		MedicalInventory secondMedicalInventory = medIvnIoOperationRepository.saveAndFlush(inventory);
		assertThat(secondMedicalInventory).isNotNull();
		List<MedicalInventory> medicalinventories = medicalInventoryManager.getMedicalInventoryByParams(secondMedicalInventory.getInventoryDate().minusDays(2),
						secondMedicalInventory.getInventoryDate().plusDays(2), null, secondMedicalInventory.getInventoryType());
		assertThat(medicalinventories).hasSize(2);
		assertThat(medicalinventories.get(0).getStatus()).containsAnyOf(firstMedicalInventory.getStatus(), status);
		assertThat(medicalinventories.get(0).getWard()).containsAnyOf(firstMedicalInventory.getWard(), wardCode);
		assertThat(medicalinventories.get(1).getStatus()).containsAnyOf(firstMedicalInventory.getStatus(), status);
		assertThat(medicalinventories.get(1).getWard()).containsAnyOf(firstMedicalInventory.getWard(), wardCode);
	}

	@Test
	void testMgrGetMedicalInventoryByParamsPageable() throws Exception {
		int id = setupTestMedicalInventory(false);
		MedicalInventory firstMedicalInventory = medIvnIoOperationRepository.findById(id).orElse(null);
		assertThat(firstMedicalInventory).isNotNull();
		Ward ward = testWard.setup(false);
		MedicalInventory inventory = testMedicalInventory.setup(ward, false);
		int idInventory = 2;
		inventory.setId(idInventory);
		String wardCode = "P";
		String status = "validated";
		inventory.setWard(wardCode);
		inventory.setStatus(status);
		MedicalInventory secondMedicalInventory = medIvnIoOperationRepository.saveAndFlush(inventory);
		assertThat(secondMedicalInventory).isNotNull();
		Page<MedicalInventory> medicalinventories = medicalInventoryManager.getMedicalInventoryByParamsPageable(
						secondMedicalInventory.getInventoryDate().minusDays(2), secondMedicalInventory.getInventoryDate().plusDays(2), status,
						secondMedicalInventory.getInventoryType(), 0, 10);
		assertThat(medicalinventories).hasSize(1);
		assertThat(medicalinventories.getContent().get(0).getStatus()).isEqualTo(secondMedicalInventory.getStatus());
		assertThat(medicalinventories.getContent().get(0).getWard()).isEqualTo(secondMedicalInventory.getWard());
	}

	@Test
	void testMgrGetMedicalInventoryByParamsNullStatusPagable() throws Exception {
		int id = setupTestMedicalInventory(false);
		MedicalInventory firstMedicalInventory = medIvnIoOperationRepository.findById(id).orElse(null);
		assertThat(firstMedicalInventory).isNotNull();
		Ward ward = testWard.setup(false);
		MedicalInventory inventory = testMedicalInventory.setup(ward, false);
		int idInventory = 2;
		inventory.setId(idInventory);
		String wardCode = "P";
		String status = "validated";
		inventory.setWard(wardCode);
		inventory.setStatus(status);
		MedicalInventory secondMedicalInventory = medIvnIoOperationRepository.saveAndFlush(inventory);
		assertThat(secondMedicalInventory).isNotNull();
		Page<MedicalInventory> medicalinventories = medicalInventoryManager.getMedicalInventoryByParamsPageable(
						secondMedicalInventory.getInventoryDate().minusDays(2), secondMedicalInventory.getInventoryDate().plusDays(2), null,
						secondMedicalInventory.getInventoryType(), 0, 10);
		assertThat(medicalinventories).hasSize(2);
		assertThat(medicalinventories.getContent().get(0).getStatus()).containsAnyOf(firstMedicalInventory.getStatus(), status);
		assertThat(medicalinventories.getContent().get(0).getWard()).containsAnyOf(firstMedicalInventory.getWard(), wardCode);
		assertThat(medicalinventories.getContent().get(1).getStatus()).containsAnyOf(firstMedicalInventory.getStatus(), status);
		assertThat(medicalinventories.getContent().get(1).getWard()).containsAnyOf(firstMedicalInventory.getWard(), wardCode);
	}

	@Test
	void testIoNewMedicalInventoryRow() throws Exception {
		Ward ward = testWard.setup(false);
		wardIoOperationRepository.saveAndFlush(ward);
		MedicalInventory inventory = testMedicalInventory.setup(ward, false);
		MedicalInventory savedInventory = medicalInventoryIoOperation.newMedicalInventory(inventory);
		MedicalType medicalType = testMedicalType.setup(false);
		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		Medical medical = testMedical.setup(medicalType, false);
		medicalsIoOperationRepository.saveAndFlush(medical);
		Lot lot = testLot.setup(medical, false);
		lotIoOperationRepository.saveAndFlush(lot);
		MedicalInventoryRow medicalInventoryRow = testMedicalInventoryRow.setup(savedInventory, medical, lot, false);
		MedicalInventoryRow newMedicalInventoryRow = medIvnRowIoOperation.newMedicalInventoryRow(medicalInventoryRow);
		checkMedicalInventoryRowIntoDb(newMedicalInventoryRow.getId());
	}

	@Test
	void testMgrNewMedicalInventoryRow() throws Exception {
		Ward ward = testWard.setup(false);
		wardIoOperationRepository.saveAndFlush(ward);
		MedicalInventory inventory = testMedicalInventory.setup(ward, false);
		MedicalInventory savedInventory = medicalInventoryIoOperation.newMedicalInventory(inventory);
		MedicalType medicalType = testMedicalType.setup(false);
		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		Medical medical = testMedical.setup(medicalType, false);
		medicalsIoOperationRepository.saveAndFlush(medical);
		Lot lot = testLot.setup(medical, false);
		lotIoOperationRepository.saveAndFlush(lot);
		MedicalInventoryRow medicalInventoryRow = testMedicalInventoryRow.setup(savedInventory, medical, lot, false);
		MedicalInventoryRow newMedicalInventoryRow = medicalInventoryRowManager.newMedicalInventoryRow(medicalInventoryRow);
		checkMedicalInventoryRowIntoDb(newMedicalInventoryRow.getId());
	}

	@Test
	void testMgrDeleteMedicalInventoryRow() throws Exception {
		Ward ward = testWard.setup(false);
		wardIoOperationRepository.saveAndFlush(ward);
		MedicalInventory inventory = testMedicalInventory.setup(ward, false);
		MedicalInventory savedInventory = medicalInventoryIoOperation.newMedicalInventory(inventory);
		MedicalType medicalType = testMedicalType.setup(false);
		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		Medical medical = testMedical.setup(medicalType, false);
		medicalsIoOperationRepository.saveAndFlush(medical);
		Lot lot = testLot.setup(medical, false);
		lotIoOperationRepository.saveAndFlush(lot);
		MedicalInventoryRow medicalInventoryRow = testMedicalInventoryRow.setup(savedInventory, medical, lot, false);
		MedicalInventoryRow newMedicalInventoryRow = medicalInventoryRowManager.newMedicalInventoryRow(medicalInventoryRow);
		checkMedicalInventoryRowIntoDb(newMedicalInventoryRow.getId());
		int inventoryId = newMedicalInventoryRow.getId();
		medicalInventoryRowManager.deleteMedicalInventoryRow(newMedicalInventoryRow);
		List<MedicalInventoryRow> found = medicalInventoryRowManager.getMedicalInventoryRowByInventoryId(inventoryId);
		assertThat(found).isEmpty();
	}

	@Test
	void testIoUpdateMedicalInventoryRow() throws Exception {
		Integer id = setupTestMedicalInventoryRow(false);
		MedicalInventoryRow foundMedicalInventoryRow = medIvnRowIoOperationRepository.findById(id).orElse(null);
		assertThat(foundMedicalInventoryRow).isNotNull();
		double realQty = 100.0;
		foundMedicalInventoryRow.setRealqty(realQty);
		MedicalInventoryRow updatedMedicalInventoryRow = medIvnRowIoOperation.updateMedicalInventoryRow(foundMedicalInventoryRow);
		assertThat(updatedMedicalInventoryRow.getRealQty()).isEqualTo(realQty);
	}

	@Test
	void testMgrUpdateMedicalInventoryRow() throws Exception {
		Integer id = setupTestMedicalInventoryRow(false);
		MedicalInventoryRow foundMedicalInventoryRow = medIvnRowIoOperationRepository.findById(id).orElse(null);
		assertThat(foundMedicalInventoryRow).isNotNull();
		double realQty = 100.0;
		foundMedicalInventoryRow.setRealqty(realQty);
		MedicalInventoryRow updatedMedicalInventoryRow = medicalInventoryRowManager.updateMedicalInventoryRow(foundMedicalInventoryRow);
		assertThat(updatedMedicalInventoryRow.getRealQty()).isEqualTo(realQty);
	}

	private int setupTestMedicalInventory(boolean usingSet) throws OHException, OHServiceException {
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

	private void checkMedicalInventoryRowIntoDb(int id) throws OHException {
		MedicalInventoryRow foundMedicalInventoryRow = medIvnRowIoOperationRepository.findById(id).orElse(null);
		assertThat(foundMedicalInventoryRow).isNotNull();
		testMedicalInventoryRow.check(foundMedicalInventoryRow, foundMedicalInventoryRow.getId());
	}

	private int setupTestMedicalInventoryRow(boolean usingSet) throws OHServiceException, OHException {
		Ward ward = testWard.setup(false);
		wardIoOperationRepository.saveAndFlush(ward);
		MedicalInventory inventory = testMedicalInventory.setup(ward, false);
		MedicalInventory savedInventory = medicalInventoryIoOperation.newMedicalInventory(inventory);
		MedicalType medicalType = testMedicalType.setup(false);
		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		Medical medical = testMedical.setup(medicalType, false);
		medicalsIoOperationRepository.saveAndFlush(medical);
		Lot lot = testLot.setup(medical, false);
		lotIoOperationRepository.saveAndFlush(lot);
		MedicalInventoryRow medicalInventoryRow = testMedicalInventoryRow.setup(savedInventory, medical, lot, false);
		MedicalInventoryRow savedMedicalInventoryRow = medIvnRowIoOperation.newMedicalInventoryRow(medicalInventoryRow);
		return savedMedicalInventoryRow.getId();
	}
}
