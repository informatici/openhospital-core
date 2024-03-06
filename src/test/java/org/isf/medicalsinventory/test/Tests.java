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
package org.isf.medicalsinventory.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.medicalinventory.model.MedicalInventory;
import org.isf.medicalinventory.model.MedicalInventoryRow;
import org.isf.medicalinventory.service.MedicalInventoryIoOperation;
import org.isf.medicalinventory.service.MedicalInventoryIoOperationRepository;
import org.isf.medicalinventory.service.MedicalInventoryRowIoOperation;
import org.isf.medicalinventory.service.MedicalInventoryRowIoOperationRepository;
import org.isf.medicals.model.Medical;
import org.isf.medicals.service.MedicalsIoOperationRepository;
import org.isf.medicals.test.TestMedical;
import org.isf.medicalstock.model.Lot;
import org.isf.medicalstock.service.LotIoOperationRepository;
import org.isf.medicalstock.test.TestLot;
import org.isf.medtype.model.MedicalType;
import org.isf.medtype.service.MedicalTypeIoOperationRepository;
import org.isf.medtype.test.TestMedicalType;
import org.isf.utils.exception.OHException;
import org.isf.ward.model.Ward;
import org.isf.ward.service.WardIoOperationRepository;
import org.isf.ward.test.TestWard;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class Tests extends OHCoreTestCase {
	
	private static TestMedicalInventory testMedicalInventory;
	private static TestMedicalInventoryRow testMedicalInventoryRow;
	private static TestWard testWard;
	private static TestMedical testMedical;
	private static TestLot testLot;
	private static TestMedicalType testMedicalType;
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
	
	
	@BeforeClass
	public static void setUpClass() {
		testMedicalInventory = new TestMedicalInventory();
		testWard = new TestWard();
		testMedicalInventoryRow = new TestMedicalInventoryRow();
		testMedical = new TestMedical();
		testLot = new TestLot();
		testMedicalType = new TestMedicalType();
	}
	
	@Before
	public void setUp() {
		cleanH2InMemoryDb();
	}
	
	@Test
	public void testMedicalInventoryGets() throws Exception {
		int code = setupTestMedicalInventory(false);
		checkMedicalInventoryIntoDb(code);
	}
	
	@Test
	public void testIoGetMedicalInventories() throws Exception {
		int id = setupTestMedicalInventory(false);
		MedicalInventory foundMedicalinventory = medIvnIoOperationRepository.findById(id).orElse(null);
		assertThat(foundMedicalinventory).isNotNull();
		List<MedicalInventory> medicalInventories = medicalInventoryIoOperation.getMedicalInventory();
		assertThat(medicalInventories.get(medicalInventories.size() - 1).getId()).isEqualTo((Integer) id);
	}
	
	@Test
	public void testIoNewMedicalInventory() throws Exception {
		Ward ward = testWard.setup(false);
		MedicalInventory medicalInventory = testMedicalInventory.setup(ward, false);
		MedicalInventory newMedicalInventory = medicalInventoryIoOperation.newMedicalInventory(medicalInventory);
		checkMedicalInventoryIntoDb(newMedicalInventory.getId());
	}
	
	@Test
	public void testIoUpdateMedicalInventory() throws Exception {
		Integer id = setupTestMedicalInventory(false);
		MedicalInventory foundMedicalInventory = medIvnIoOperationRepository.findById(id).orElse(null);
		assertThat(foundMedicalInventory).isNotNull();
		String status = "canceled";
		foundMedicalInventory.setStatus(status);
		MedicalInventory updatedMedicalInventory = medicalInventoryIoOperation.updateMedicalInventoryRow(foundMedicalInventory);
		assertThat(updatedMedicalInventory.getStatus()).isEqualTo(status);
	}
	
	@Test
	public void testIoDeleteMedicalInventory() throws Exception {
		Integer id = setupTestMedicalInventory(false);
		MedicalInventory foundMedicalInventory = medIvnIoOperationRepository.findById(id).orElse(null);
		assertThat(foundMedicalInventory).isNotNull();
		String reference = foundMedicalInventory.getInventoryReference();
		medicalInventoryIoOperation.deleteMedicalInventory(foundMedicalInventory);
		assertThat(medicalInventoryIoOperation.referenceExists(reference)).isFalse();
	}
	
	@Test
	public void testIoGetMedicalInventoryWithStatus() throws Exception {
		int id = setupTestMedicalInventory(false);
		MedicalInventory firstMedicalInventory = medIvnIoOperationRepository.findById(id).orElse(null);
		assertThat(firstMedicalInventory).isNotNull();
		Ward ward = testWard.setup(false);
		MedicalInventory inventory = testMedicalInventory.setup(ward, false);
		String status = "validated";
		inventory.setStatus(status);
		MedicalInventory secondMedicalInventory = medIvnIoOperationRepository.saveAndFlush(inventory);
		assertThat(secondMedicalInventory).isNotNull();
		List<MedicalInventory> medicalInventories = medicalInventoryIoOperation
				.getMedicalInventoryByStatus(firstMedicalInventory.getStatus());
		assertThat(medicalInventories.get(0).getStatus()).isEqualTo(firstMedicalInventory.getStatus());
		assertThat(medicalInventories).hasSize(1);
	}
	
	@Test
	public void testIoGetMedicalInventoryWithStatusAndWard() throws Exception {
		int id = setupTestMedicalInventory(false);
		MedicalInventory firstMedicalInventory = medIvnIoOperationRepository.findById(id).orElse(null);
		assertThat(firstMedicalInventory).isNotNull();
		Ward ward = testWard.setup(false);
		MedicalInventory inventory = testMedicalInventory.setup(ward, false);
		String wardCode = "P";
		String status = "validated";
		inventory.setWard(wardCode);
		inventory.setStatus(status);
		MedicalInventory secondMedicalInventory = medIvnIoOperationRepository.saveAndFlush(inventory);
		assertThat(secondMedicalInventory).isNotNull();
		List<MedicalInventory> medicalinventories = medicalInventoryIoOperation
				.getMedicalInventoryByStatusAndWard(firstMedicalInventory.getStatus(), firstMedicalInventory.getWard());
		assertThat(medicalinventories.get(0).getStatus()).isEqualTo(firstMedicalInventory.getStatus());
		assertThat(medicalinventories.get(0).getWard()).isEqualTo(firstMedicalInventory.getWard());
		assertThat(medicalinventories.size()).isEqualTo(1);
	}
	
	@Test
	public void testIoNewMedicalInventoryRow() throws Exception {
		Ward ward = testWard.setup(false);
		MedicalInventory inventory = testMedicalInventory.setup(ward, false);
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical = testMedical.setup(medicalType, false);
		Lot lot = testLot.setup(medical, false);
		MedicalInventoryRow medicalInventoryRow = testMedicalInventoryRow.setup(inventory, medical, lot, false);
		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		medicalsIoOperationRepository.saveAndFlush(medical);
		lotIoOperationRepository.saveAndFlush(lot);
		medIvnIoOperationRepository.saveAndFlush(inventory);
		MedicalInventoryRow newMedicalInventoryRow = medIvnRowIoOperation.newMedicalInventoryRow(medicalInventoryRow);
		checkMedicalInventoryIntoDb(newMedicalInventoryRow.getId());
	}

	@Test
	public void testIoUpdateMedicalInventoryRow() throws Exception {
		Integer id = setupTestMedicalInventoryRow(false);
		MedicalInventoryRow foundMedicalInventoryRow = medIvnRowIoOperationRepository.findById(id).orElse(null);
		assertThat(foundMedicalInventoryRow).isNotNull();
		double realQty = 100.0;
		foundMedicalInventoryRow.setRealqty(realQty);
		MedicalInventoryRow updatedMedicalInventoryRow = medIvnRowIoOperation.updateMedicalInventoryRow(foundMedicalInventoryRow);
		assertThat(updatedMedicalInventoryRow.getRealQty()).isEqualTo(realQty);
	}
	
	private int setupTestMedicalInventory(boolean usingSet) throws OHException {
		Ward ward = testWard.setup(false);
		MedicalInventory medicalInventory = testMedicalInventory.setup(ward, false);
		medIvnIoOperationRepository.saveAndFlush(medicalInventory);
		return medicalInventory.getId();
	}
	
	private void checkMedicalInventoryIntoDb(int id) throws OHException {
		MedicalInventory foundMedicalInventory = medIvnIoOperationRepository.findById(id).orElse(null);
		assertThat(foundMedicalInventory).isNotNull();
		testMedicalInventory.check(foundMedicalInventory);
	}
	
	private int setupTestMedicalInventoryRow(boolean usingSet) throws OHException {
		Ward ward = testWard.setup(false);
		MedicalInventory inventory = testMedicalInventory.setup(ward, false);
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical = testMedical.setup(null, false);
		Lot lot = testLot.setup(medical, false);
		MedicalInventoryRow medicalInventoryRow = testMedicalInventoryRow.setup(inventory, medical, lot, false);
		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		medicalsIoOperationRepository.saveAndFlush(medical);
		lotIoOperationRepository.saveAndFlush(lot);
		medIvnIoOperationRepository.saveAndFlush(inventory);
		medIvnRowIoOperationRepository.saveAndFlush(medicalInventoryRow);
		return medicalInventoryRow.getId();
	}
}
