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
package org.isf.medicalinventory.manager;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.medicalinventory.model.MedicalInventory;
import org.isf.medicalinventory.model.MedicalInventoryRow;
import org.isf.medicalinventory.service.MedicalInventoryIoOperation;
import org.isf.medicalinventory.service.MedicalInventoryRowIoOperation;
import org.isf.medicalinventory.service.MedicalInventoryRowIoOperationRepository;
import org.isf.medicals.TestMedical;
import org.isf.medicals.model.Medical;
import org.isf.medicals.service.MedicalsIoOperationRepository;
import org.isf.medicalsinventory.TestMedicalInventory;
import org.isf.medicalsinventory.TestMedicalInventoryRow;
import org.isf.medicalstock.TestLot;
import org.isf.medicalstock.model.Lot;
import org.isf.medicalstock.service.LotIoOperationRepository;
import org.isf.medtype.TestMedicalType;
import org.isf.medtype.model.MedicalType;
import org.isf.medtype.service.MedicalTypeIoOperationRepository;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.isf.ward.TestWard;
import org.isf.ward.model.Ward;
import org.isf.ward.service.WardIoOperationRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MedicalInventoryRowManagerTest extends OHCoreTestCase {

	private static TestMedicalInventory testMedicalInventory;
	private static TestMedicalInventoryRow testMedicalInventoryRow;
	private static TestWard testWard;
	private static TestMedical testMedical;
	private static TestLot testLot;
	private static TestMedicalType testMedicalType;

	@Autowired
	MedicalInventoryRowManager medicalInventoryRowManager;

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
	void newMedicalInventoryRow_shouldSaveInventoryRow() throws Exception {
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
	void deleteMedicalInventoryRow_shouldRemoveInventoryRow() throws Exception {
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
	void updateMedicalInventoryRow_shouldSaveNewRealQty() throws Exception {
		Integer id = setupTestMedicalInventoryRow();
		MedicalInventoryRow foundMedicalInventoryRow = medIvnRowIoOperationRepository.findById(id).orElse(null);
		assertThat(foundMedicalInventoryRow).isNotNull();
		BigDecimal realQty = new BigDecimal("100.0");
		foundMedicalInventoryRow.setRealqty(realQty);
		MedicalInventoryRow updatedMedicalInventoryRow = medicalInventoryRowManager.updateMedicalInventoryRow(foundMedicalInventoryRow);
		assertThat(updatedMedicalInventoryRow.getRealQty()).isEqualByComparingTo(realQty);
	}

	@Test
	void getMedicalInventoryRowByInventoryId_shouldReturnRowsOfInventory() throws Exception {
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
		int inventoryId = savedMedicalInventoryRow.getId();
		MedicalInventoryRow newMedicalInventoryRow = medicalInventoryRowManager.newMedicalInventoryRow(medicalInventoryRow);
		assertThat(newMedicalInventoryRow).isNotNull();
		List<MedicalInventoryRow> medicalInventoryRows = medicalInventoryRowManager.getMedicalInventoryRowByInventoryId(inventoryId);
		assertThat(medicalInventoryRows).isNotEmpty();
		assertThat(medicalInventoryRows).hasSize(1);
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

}
