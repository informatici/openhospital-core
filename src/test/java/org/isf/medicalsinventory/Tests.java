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
import java.util.stream.Stream;

import org.isf.OHCoreTestCase;
import org.isf.generaldata.GeneralData;
import org.isf.medicalinventory.manager.MedicalInventoryManager;
import org.isf.medicalinventory.manager.MedicalInventoryRowManager;
import org.isf.medicalinventory.model.InventoryStatus;
import org.isf.medicalinventory.model.InventoryType;
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
import org.isf.medicalstock.TestMedicalStock;
import org.isf.medicalstock.TestMovement;
import org.isf.medicalstock.model.Lot;
import org.isf.medicalstock.model.MedicalStock;
import org.isf.medicalstock.model.Movement;
import org.isf.medicalstock.service.LotIoOperationRepository;
import org.isf.medicalstock.service.MedicalStockIoOperationRepository;
import org.isf.medicalstock.service.MedicalStockIoOperations;
import org.isf.medstockmovtype.model.MovementType;
import org.isf.medstockmovtype.service.MedicalDsrStockMovementTypeIoOperationRepository;
import org.isf.medtype.TestMedicalType;
import org.isf.medtype.model.MedicalType;
import org.isf.medtype.service.MedicalTypeIoOperationRepository;
import org.isf.supplier.model.Supplier;
import org.isf.supplier.service.SupplierIoOperationRepository;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.time.TimeTools;
import org.isf.ward.TestWard;
import org.isf.ward.model.Ward;
import org.isf.ward.service.WardIoOperationRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

class Tests extends OHCoreTestCase {

	private static TestMedicalInventory testMedicalInventory;
	private static TestMedicalInventoryRow testMedicalInventoryRow;
	private static TestWard testWard;
	private static TestMedical testMedical;
	private static TestLot testLot;
	private static TestMedicalType testMedicalType;
	private static TestMovement testMovement;
	private static TestMedicalStock testMedicalStock;

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

	@Autowired
	MedicalInventoryRowIoOperationRepository medicalInventoryRowIoOperationRepository;

	@Autowired
	MedicalDsrStockMovementTypeIoOperationRepository medicalDsrStockMovementTypeIoOperationRepository;

	@Autowired
	SupplierIoOperationRepository supplierIoOperationRepository;
	
	@Autowired
	MedicalStockIoOperationRepository medicalStockIoOperationRepository;
	
	@Autowired
	MedicalStockIoOperations medicalStockIoOperation;

	static Stream<Arguments> automaticlot() {
		return Stream.of(Arguments.of(true, true, false),
						 Arguments.of(true, true, true),
						 Arguments.of(false, true, false),
						 Arguments.of(false, true, true));
	}

	private static void setGeneralData(boolean in, boolean out, boolean toward) {
		GeneralData.AUTOMATICLOT_IN = in;
		GeneralData.AUTOMATICLOT_OUT = out;
		GeneralData.AUTOMATICLOTWARD_TOWARD = toward;
	}
	
	@BeforeAll
	static void setUpClass() {
		testMedicalInventory = new TestMedicalInventory();
		testWard = new TestWard();
		testMedicalInventoryRow = new TestMedicalInventoryRow();
		testMedical = new TestMedical();
		testLot = new TestLot();
		testMedicalType = new TestMedicalType();
		testMovement = new TestMovement();
		testMedicalStock = new TestMedicalStock();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void testMedicalInventoryGets() throws Exception {
		int code = setupTestMedicalInventory();
		checkMedicalInventoryIntoDb(code);
	}

	@Test
	void testMedicalInventoryGetsSets() throws Exception {
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

	@Test
	void testMedicalInventoryRowGetsSets() throws Exception {
		Integer id = setupTestMedicalInventoryRow();
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
		medicalInventoryRow.setRealQty(realQty); // Note the uppercase 'Q'
		assertThat(medicalInventoryRow.getRealQty()).isEqualTo(realQty);

		int lock = -99;
		medicalInventoryRow.setLock(lock);
		assertThat(medicalInventoryRow.getLock()).isEqualTo(lock);

		assertThat(medicalInventoryRow.getSearchString()).isEqualTo("testdescriptiontp1");
	}

	@Test
	void testMgrGetMedicalInventory() throws Exception {
		int id = setupTestMedicalInventory();
		MedicalInventory foundMedicalinventory = medIvnIoOperationRepository.findById(id).orElse(null);
		assertThat(foundMedicalinventory).isNotNull();
		List<MedicalInventory> medicalInventories = medicalInventoryManager.getMedicalInventory();
		assertThat(medicalInventories.get(medicalInventories.size() - 1).getId()).isEqualTo((Integer) id);
	}

	@Test
	void testIoGetMedicalInventory() throws Exception {
		int id = setupTestMedicalInventory();
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
		Integer id = setupTestMedicalInventory();
		MedicalInventory foundMedicalInventory = medIvnIoOperationRepository.findById(id).orElse(null);
		assertThat(foundMedicalInventory).isNotNull();
		String status = InventoryStatus.canceled.toString();
		foundMedicalInventory.setStatus(status);
		MedicalInventory updatedMedicalInventory = medicalInventoryManager.updateMedicalInventory(foundMedicalInventory, true);
		assertThat(updatedMedicalInventory.getStatus()).isEqualTo(status);
	}

	@Test
	void testIoUpdateMedicalInventory() throws Exception {
		Integer id = setupTestMedicalInventory();
		MedicalInventory foundMedicalInventory = medIvnIoOperationRepository.findById(id).orElse(null);
		assertThat(foundMedicalInventory).isNotNull();
		String status = "canceled";
		foundMedicalInventory.setStatus(status);
		MedicalInventory updatedMedicalInventory = medicalInventoryIoOperation.updateMedicalInventory(foundMedicalInventory);
		assertThat(updatedMedicalInventory.getStatus()).isEqualTo(status);
	}

	@Test
	void testDeleteMedicalInventoryWithInventoryRowsWithoutNewLot() throws Exception {
		Integer id = setupTestMedicalInventory();
		MedicalInventory foundMedicalInventory = medIvnIoOperationRepository.findById(id).orElse(null);
		assertThat(foundMedicalInventory).isNotNull();
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical = testMedical.setup(medicalType, false);
		Lot lot = testLot.setup(medical, false);
		medicalTypeIoOperationRepository.save(medicalType);
		medical = medicalsIoOperationRepository.save(medical);
		lot = lotIoOperationRepository.save(lot);
		MedicalInventoryRow medicalInventoryRowOne = testMedicalInventoryRow.setup(foundMedicalInventory, medical, lot, false);
		medicalInventoryRowIoOperationRepository.saveAndFlush(medicalInventoryRowOne);
		medicalInventoryManager.deleteInventory(foundMedicalInventory);
		foundMedicalInventory = medIvnIoOperationRepository.findById(id).orElse(null);
		assertThat(foundMedicalInventory).isNotNull();
		assertThat(foundMedicalInventory.getStatus()).isEqualTo(InventoryStatus.canceled.toString());
	}
	
	@Test
	void testDeleteMedicalInventoryWithInventoryRowsWithNewLot() throws Exception {
		Integer id = setupTestMedicalInventory();
		MedicalInventory foundMedicalInventory = medIvnIoOperationRepository.findById(id).orElse(null);
		assertThat(foundMedicalInventory).isNotNull();
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical = testMedical.setup(medicalType, false);
		Lot lot = testLot.setup(medical, false);
		medicalTypeIoOperationRepository.save(medicalType);
		medical = medicalsIoOperationRepository.save(medical);
		lot = lotIoOperationRepository.save(lot);
		MedicalInventoryRow medicalInventoryRowOne = testMedicalInventoryRow.setup(foundMedicalInventory, medical, lot, false);
		medicalInventoryRowOne.setNewLot(true);
		medicalInventoryRowIoOperationRepository.saveAndFlush(medicalInventoryRowOne);
		medicalInventoryManager.deleteInventory(foundMedicalInventory);
		foundMedicalInventory = medIvnIoOperationRepository.findById(id).orElse(null);
		assertThat(foundMedicalInventory).isNotNull();
		assertThat(foundMedicalInventory.getStatus()).isEqualTo(InventoryStatus.canceled.toString());
	}

	@Test
	void testMgrGetMedicalInventoryWithStatus() throws Exception {
		int id = setupTestMedicalInventory();
		MedicalInventory firstMedicalInventory = medIvnIoOperationRepository.findById(id).orElse(null);
		assertThat(firstMedicalInventory).isNotNull();
		Ward ward = testWard.setup(false);
		MedicalInventory inventory = testMedicalInventory.setup(ward, false);
		String status = InventoryStatus.draft.toString();
		String inventoryType = InventoryType.main.toString();
		inventory.setStatus(status);
		inventory.setInventoryType(inventoryType);
		MedicalInventory secondMedicalInventory = medIvnIoOperationRepository.saveAndFlush(inventory);
		assertThat(secondMedicalInventory).isNotNull();
		List<MedicalInventory> medicalInventories = medicalInventoryManager.getMedicalInventoryByStatusAndInventoryType(firstMedicalInventory.getStatus(),
						firstMedicalInventory.getInventoryType());
		assertThat(medicalInventories).hasSize(1);
		assertThat(medicalInventories.get(0).getStatus()).isEqualTo(firstMedicalInventory.getStatus());
	}

	@Test
	void testIoGetMedicalInventoryWithStatus() throws Exception {
		int id = setupTestMedicalInventory();
		MedicalInventory firstMedicalInventory = medIvnIoOperationRepository.findById(id).orElse(null);
		assertThat(firstMedicalInventory).isNotNull();
		Ward ward = testWard.setup(false);
		MedicalInventory inventory = testMedicalInventory.setup(ward, false);
		String status = InventoryStatus.draft.toString();
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
	void testMgrGetMedicalInventoryWithStatusAndWard() throws Exception {
		int id = setupTestMedicalInventory();
		MedicalInventory firstMedicalInventory = medIvnIoOperationRepository.findById(id).orElse(null);
		assertThat(firstMedicalInventory).isNotNull();
		Ward ward = testWard.setup(false);
		MedicalInventory inventory = testMedicalInventory.setup(ward, false);
		int idInventory = 2;
		inventory.setId(idInventory);
		String wardCode = "P";
		String status = InventoryStatus.validated.toString();
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
		int id = setupTestMedicalInventory();
		MedicalInventory firstMedicalInventory = medIvnIoOperationRepository.findById(id).orElse(null);
		assertThat(firstMedicalInventory).isNotNull();
		Ward ward = testWard.setup(false);
		MedicalInventory inventory = testMedicalInventory.setup(ward, false);
		int idInventory = 2;
		inventory.setId(idInventory);
		String wardCode = "P";
		String status = InventoryStatus.validated.toString();
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
		int id = setupTestMedicalInventory();
		MedicalInventory firstMedicalInventory = medIvnIoOperationRepository.findById(id).orElse(null);
		assertThat(firstMedicalInventory).isNotNull();
		Ward ward = testWard.setup(false);
		MedicalInventory inventory = testMedicalInventory.setup(ward, false);
		int idInventory = 2;
		inventory.setId(idInventory);
		String wardCode = "P";
		String status = InventoryStatus.validated.toString();
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
		int id = setupTestMedicalInventory();
		MedicalInventory firstMedicalInventory = medIvnIoOperationRepository.findById(id).orElse(null);
		assertThat(firstMedicalInventory).isNotNull();
		Ward ward = testWard.setup(false);
		MedicalInventory inventory = testMedicalInventory.setup(ward, false);
		int idInventory = 2;
		inventory.setId(idInventory);
		String wardCode = "P";
		String status = InventoryStatus.validated.toString();
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
		int id = setupTestMedicalInventory();
		MedicalInventory firstMedicalInventory = medIvnIoOperationRepository.findById(id).orElse(null);
		assertThat(firstMedicalInventory).isNotNull();
		Ward ward = testWard.setup(false);
		MedicalInventory inventory = testMedicalInventory.setup(ward, false);
		int idInventory = 2;
		inventory.setId(idInventory);
		String wardCode = "P";
		String status = InventoryStatus.validated.toString();
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
		int id = setupTestMedicalInventory();
		MedicalInventory firstMedicalInventory = medIvnIoOperationRepository.findById(id).orElse(null);
		assertThat(firstMedicalInventory).isNotNull();
		Ward ward = testWard.setup(false);
		MedicalInventory inventory = testMedicalInventory.setup(ward, false);
		int idInventory = 2;
		inventory.setId(idInventory);
		String wardCode = "P";
		String status = InventoryStatus.validated.toString();
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
		Integer id = setupTestMedicalInventoryRow();
		MedicalInventoryRow foundMedicalInventoryRow = medIvnRowIoOperationRepository.findById(id).orElse(null);
		assertThat(foundMedicalInventoryRow).isNotNull();
		double realQty = 100.0;
		foundMedicalInventoryRow.setRealqty(realQty);
		MedicalInventoryRow updatedMedicalInventoryRow = medIvnRowIoOperation.updateMedicalInventoryRow(foundMedicalInventoryRow);
		assertThat(updatedMedicalInventoryRow.getRealQty()).isEqualTo(realQty);
	}

	@Test
	void testMgrUpdateMedicalInventoryRow() throws Exception {
		Integer id = setupTestMedicalInventoryRow();
		MedicalInventoryRow foundMedicalInventoryRow = medIvnRowIoOperationRepository.findById(id).orElse(null);
		assertThat(foundMedicalInventoryRow).isNotNull();
		double realQty = 100.0;
		foundMedicalInventoryRow.setRealqty(realQty);
		MedicalInventoryRow updatedMedicalInventoryRow = medicalInventoryRowManager.updateMedicalInventoryRow(foundMedicalInventoryRow);
		assertThat(updatedMedicalInventoryRow.getRealQty()).isEqualTo(realQty);
	}

	@Test
	void testMgrGetMedicalInventoryRowByInventoryId() throws Exception {
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
		int inventoryId = medicalInventoryRow.getId();
		MedicalInventoryRow newMedicalInventoryRow = medicalInventoryRowManager.newMedicalInventoryRow(medicalInventoryRow);
		assertThat(newMedicalInventoryRow).isNotNull();
		List<MedicalInventoryRow> medicalInventoryRows = medicalInventoryRowManager.getMedicalInventoryRowByInventoryId(inventoryId);
		assertThat(medicalInventoryRows).isNotEmpty();
		assertThat(medicalInventoryRows).hasSize(1);
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

	private void checkMedicalInventoryRowIntoDb(int id) throws OHException {
		MedicalInventoryRow foundMedicalInventoryRow = medIvnRowIoOperationRepository.findById(id).orElse(null);
		assertThat(foundMedicalInventoryRow).isNotNull();
		testMedicalInventoryRow.check(foundMedicalInventoryRow, foundMedicalInventoryRow.getId());
	}

	private int setupTestMedicalInventoryRow() throws OHServiceException, OHException {
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

	@Test
	void testDeleteInventory() throws Exception {
		int id = setupTestMedicalInventory();
		MedicalInventory inventory = medIvnIoOperationRepository.findById(id).orElse(null);
		assertThat(inventory).isNotNull();
		inventory.setStatus(InventoryStatus.draft.toString());
		medIvnIoOperationRepository.saveAndFlush(inventory);
		medicalInventoryManager.deleteInventory(inventory);
		MedicalInventory deletedInventory = medIvnIoOperationRepository.findById(id).orElse(null);
		assertThat(deletedInventory).isNotNull();
		assertThat(deletedInventory.getStatus()).isEqualTo(InventoryStatus.canceled.toString());
	}

	@Test
	void testValidateMedicalInventoryRow() throws Exception {
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
		int inventoryRowId = medicalInventoryRow.getId();
		MedicalInventoryRow newMedicalInventoryRow = medicalInventoryRowManager.newMedicalInventoryRow(medicalInventoryRow);
		assertThat(newMedicalInventoryRow).isNotNull();
		List<MedicalInventoryRow> medicalInventoryRows = medicalInventoryRowManager.getMedicalInventoryRowByInventoryId(inventoryRowId);
		assertThat(medicalInventoryRows).isNotEmpty();
		assertThat(medicalInventoryRows).hasSize(1);
		medicalInventoryManager.validateMedicalInventoryRow(savedInventory, medicalInventoryRows);
		int inventoryId = inventory.getId();
		inventory = medicalInventoryIoOperation.getInventoryById(inventoryId);
		assertThat(inventory).isNotNull();
	}

	@ParameterizedTest(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	@MethodSource("automaticlot")
	void testConfirmMedicalInventory(boolean in, boolean out, boolean toward) throws Exception {
		setGeneralData(in, out, toward);
		Ward ward = testWard.setup(false);
		wardIoOperationRepository.saveAndFlush(ward);
		MovementType chargeType = new MovementType("inventory+", "Inventory+", "+", "non-operational");
		MovementType dischargeType = new MovementType("inventory-", "Inventory-", "-", "non-operational");
		Supplier supplier = new Supplier(1, "INVENTORY", null, null, null, null, null, null);
		Ward destination = new Ward("INV", "ward inventory", null, null, null, 8, 1, 1, false, false);
		dischargeType = medicalDsrStockMovementTypeIoOperationRepository.save(dischargeType);
		chargeType = medicalDsrStockMovementTypeIoOperationRepository.save(chargeType);
		supplier = supplierIoOperationRepository.save(supplier);
		destination = wardIoOperationRepository.save(destination);
		MedicalInventory inventory = testMedicalInventory.setup(ward, false);
		inventory.setChargeType(chargeType.getCode());
		inventory.setDestination(destination.getCode());
		inventory.setSupplier(supplier.getSupId());
		inventory.setDischargeType(dischargeType.getCode());
		inventory = medicalInventoryIoOperation.newMedicalInventory(inventory);
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical = testMedical.setup(medicalType, false);
		Lot lotOne = testLot.setup(medical, false);
		Movement firstMovement = testMovement.setup(medical, chargeType, ward, lotOne, supplier, false);
		firstMovement.setQuantity(100);
		MedicalStock firstmedicalStock = testMedicalStock.setup(firstMovement);
		Lot lotTwo = testLot.setup(medical, false);
		lotTwo.setCode("LOT-001");
		Movement secondMovement = testMovement.setup(medical, chargeType, ward, lotTwo, supplier, false);
		secondMovement.setQuantity(100);
		MedicalStock secondmedicalStock = testMedicalStock.setup(firstMovement);
		Lot lotThree = testLot.setup(medical, false);
		lotTwo.setCode("LOT-003");
		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		medical = medicalsIoOperationRepository.save(medical);
		lotOne = lotIoOperationRepository.save(lotOne);
		lotTwo = lotIoOperationRepository.save(lotTwo);
		lotThree = lotIoOperationRepository.save(lotThree);
		medicalStockIoOperation.newMovement(firstMovement);
		medicalStockIoOperation.newMovement(secondMovement);
		medicalStockIoOperationRepository.saveAndFlush(firstmedicalStock);
		medicalStockIoOperationRepository.saveAndFlush(secondmedicalStock);
		MedicalInventoryRow medicalInventoryRowOne = testMedicalInventoryRow.setup(inventory, medical, lotOne, false);
		medicalInventoryRowOne.setRealqty(60);
		MedicalInventoryRow medicalInventoryRowTwo = testMedicalInventoryRow.setup(inventory, medical, lotTwo, false); 
		medicalInventoryRowTwo.setId(2);
		medicalInventoryRowTwo.setRealqty(30);
		MedicalInventoryRow medicalInventoryRowThree = testMedicalInventoryRow.setup(inventory, medical, lotThree, false);
		medicalInventoryRowThree.setId(3);
		medicalInventoryRowIoOperationRepository.saveAndFlush(medicalInventoryRowOne);
		medicalInventoryRowIoOperationRepository.saveAndFlush(medicalInventoryRowTwo);
		medicalInventoryRowIoOperationRepository.saveAndFlush(medicalInventoryRowThree);
		int inventoryId = inventory.getId();
		List<MedicalInventoryRow> medicalInventoryRows = medicalInventoryRowManager.getMedicalInventoryRowByInventoryId(inventoryId);
		assertThat(medicalInventoryRows).isNotEmpty();
		assertThat(medicalInventoryRows).hasSize(3);
		List<Movement> insertMovements = medicalInventoryManager.confirmMedicalInventoryRow(inventory, medicalInventoryRows);
		assertThat(insertMovements).isNotEmpty();
		String status = InventoryStatus.done.toString();
		inventory = medicalInventoryIoOperation.getInventoryById(inventoryId);
		assertThat(inventory).isNotNull();
		assertThat(inventory.getStatus()).isEqualTo(status);
	}
	
	@Test
	void testReferenceOfInventoryExist() throws Exception {
		int id = setupTestMedicalInventory();
		MedicalInventory inventory = medIvnIoOperationRepository.findById(id).orElse(null);
		assertThat(inventory).isNotNull();
		String reference = inventory.getInventoryReference();
		boolean exist = medicalInventoryManager.referenceExists(reference);
		assertThat(exist).isTrue();
	}
	
	@Test
	void testGetInventoryByID() throws Exception {
		int id = setupTestMedicalInventory();
		MedicalInventory inventory = medicalInventoryManager.getInventoryById(id);
		assertThat(inventory).isNotNull();
	}
	
	@Test
	void testGetInventoryByReference() throws Exception {
		int id = setupTestMedicalInventory();
		MedicalInventory inventory = medIvnIoOperationRepository.findById(id).orElse(null);
		assertThat(inventory).isNotNull();
		String reference = inventory.getInventoryReference();
		MedicalInventory found = medicalInventoryManager.getInventoryByReference(reference);
		assertThat(found).isNotNull();
		assertThat(found.getInventoryReference()).isEqualTo(inventory.getInventoryReference());
	}
	
	@Test
	void testValidateMedicalInventory() throws Exception {
		Ward ward = testWard.setup(false);
		wardIoOperationRepository.saveAndFlush(ward);
		MovementType chargeType = new MovementType("inventory+", "Inventory+", "+", "non-operational");
		MovementType dischargeType = new MovementType("inventory-", "Inventory-", "-", "non-operational");
		Supplier supplier = new Supplier(1, "INVENTORY", null, null, null, null, null, null);
		Ward destination = new Ward("INV", "ward inventory", null, null, null, 8, 1, 1, false, false);
		dischargeType = medicalDsrStockMovementTypeIoOperationRepository.save(dischargeType);
		chargeType = medicalDsrStockMovementTypeIoOperationRepository.save(chargeType);
		supplier = supplierIoOperationRepository.save(supplier);
		destination = wardIoOperationRepository.save(destination);
		MedicalInventory inventory = testMedicalInventory.setup(ward, false);
		inventory.setChargeType(chargeType.getCode());
		inventory.setDestination(destination.getCode());
		inventory.setSupplier(supplier.getSupId());
		inventory.setDischargeType(dischargeType.getCode());
		inventory = medicalInventoryIoOperation.newMedicalInventory(inventory);
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical = testMedical.setup(medicalType, false);
		Lot lotOne = testLot.setup(medical, false);
		Movement firstMovement = testMovement.setup(medical, chargeType, ward, lotOne, supplier, false);
		firstMovement.setQuantity(100);
		MedicalStock firstmedicalStock = testMedicalStock.setup(firstMovement);
		Lot lotTwo = testLot.setup(medical, false);
		lotTwo.setCode("LOT-001");
		Movement secondMovement = testMovement.setup(medical, chargeType, ward, lotTwo, supplier, false);
		secondMovement.setQuantity(100);
		MedicalStock secondmedicalStock = testMedicalStock.setup(firstMovement);
		Lot lotThree = testLot.setup(medical, false);
		lotTwo.setCode("LOT-003");
		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		medical = medicalsIoOperationRepository.save(medical);
		lotOne = lotIoOperationRepository.save(lotOne);
		lotTwo = lotIoOperationRepository.save(lotTwo);
		lotThree = lotIoOperationRepository.save(lotThree);
		MedicalInventoryRow medicalInventoryRowOne = testMedicalInventoryRow.setup(inventory, medical, lotOne, false);
		medicalInventoryRowOne.setRealqty(60);
		MedicalInventoryRow medicalInventoryRowTwo = testMedicalInventoryRow.setup(inventory, medical, lotTwo, false); 
		medicalInventoryRowTwo.setId(2);
		medicalInventoryRowTwo.setRealqty(30);
		MedicalInventoryRow medicalInventoryRowThree = testMedicalInventoryRow.setup(inventory, medical, lotThree, false);
		medicalInventoryRowThree.setId(3);
		medicalInventoryRowIoOperationRepository.saveAndFlush(medicalInventoryRowOne);
		medicalInventoryRowIoOperationRepository.saveAndFlush(medicalInventoryRowTwo);
		medicalInventoryRowIoOperationRepository.saveAndFlush(medicalInventoryRowThree);
		medicalStockIoOperation.newMovement(firstMovement);
		medicalStockIoOperation.newMovement(secondMovement);
		medicalStockIoOperationRepository.saveAndFlush(firstmedicalStock);
		medicalStockIoOperationRepository.saveAndFlush(secondmedicalStock);
		int inventoryId = inventory.getId();
		List<MedicalInventoryRow> medicalInventoryRows = medicalInventoryRowManager.getMedicalInventoryRowByInventoryId(inventoryId);
		assertThat(medicalInventoryRows).isNotEmpty();
		assertThat(medicalInventoryRows).hasSize(3);
		medicalInventoryManager.validateMedicalInventoryRow(inventory, medicalInventoryRows);
	}
}
