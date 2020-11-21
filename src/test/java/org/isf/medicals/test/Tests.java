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
package org.isf.medicals.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;

import org.isf.OHCoreTestCase;
import org.isf.medicals.model.Medical;
import org.isf.medicals.service.MedicalsIoOperationRepository;
import org.isf.medicals.service.MedicalsIoOperations;
import org.isf.medicalstock.model.Lot;
import org.isf.medicalstock.model.Movement;
import org.isf.medicalstock.service.LotIoOperationRepository;
import org.isf.medicalstock.service.MovementIoOperationRepository;
import org.isf.medicalstock.test.TestLot;
import org.isf.medicalstock.test.TestMovement;
import org.isf.medstockmovtype.model.MovementType;
import org.isf.medstockmovtype.service.MedicalStockMovementTypeIoOperationRepository;
import org.isf.medstockmovtype.test.TestMovementType;
import org.isf.medtype.model.MedicalType;
import org.isf.medtype.service.MedicalTypeIoOperationRepository;
import org.isf.medtype.test.TestMedicalType;
import org.isf.supplier.model.Supplier;
import org.isf.supplier.service.SupplierIoOperationRepository;
import org.isf.supplier.test.TestSupplier;
import org.isf.utils.exception.OHException;
import org.isf.ward.model.Ward;
import org.isf.ward.service.WardIoOperationRepository;
import org.isf.ward.test.TestWard;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class Tests extends OHCoreTestCase {

	private static TestMedical testMedical;
	private static TestMedicalType testMedicalType;
	private static TestMovement testMovement;
	private static TestMovementType testMovementType;
	private static TestWard testWard;
	private static TestLot testLot;
	private static TestSupplier testSupplier;

	@Autowired
	MedicalsIoOperations medicalsIoOperations;
	@Autowired
	MedicalsIoOperationRepository medicalsIoOperationRepository;
	@Autowired
	MedicalTypeIoOperationRepository medicalTypeIoOperationRepository;
	@Autowired
	MovementIoOperationRepository movementIoOperationRepository;
	@Autowired
	MedicalStockMovementTypeIoOperationRepository medicalStockMovementTypeIoOperationRepository;
	@Autowired
	WardIoOperationRepository wardIoOperationRepository;
	@Autowired
	LotIoOperationRepository lotIoOperationRepository;
	@Autowired
	SupplierIoOperationRepository supplierIoOperationRepository;

	@BeforeClass
	public static void setUpClass() {
		testMedical = new TestMedical();
		testMedicalType = new TestMedicalType();
		testMovement = new TestMovement();
		testMovementType = new TestMovementType();
		testWard = new TestWard();
		testLot = new TestLot();
		testSupplier = new TestSupplier();
	}

	@Before
	public void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	public void testMedicalGets() throws Exception {
		int code = _setupTestMedical(false);
		_checkMedicalIntoDb(code);
	}

	@Test
	public void testMedicalSets() throws Exception {
		int code = _setupTestMedical(true);
		_checkMedicalIntoDb(code);
	}

	@Test
	public void testIoGetMedical() throws Exception {
		int code = _setupTestMedical(false);
		Medical foundMedical = medicalsIoOperationRepository.findOne(code);
		Medical medical = medicalsIoOperations.getMedical(code);
		assertThat(medical.getCode()).isEqualTo(foundMedical.getCode());
	}

	@Test
	public void testIoGetMedicals() throws Exception {
		int code = _setupTestMedical(false);
		Medical foundMedical = medicalsIoOperationRepository.findOne(code);
		ArrayList<Medical> medicals = medicalsIoOperations.getMedicals(String.valueOf(foundMedical.getDescription()));
		assertThat(medicals.get(medicals.size() - 1).getCode()).isEqualTo((Integer) code);
	}

	@Test
	public void testIoGetMedicalsType() throws Exception {
		Integer code = _setupTestMedical(false);
		Medical foundMedical = medicalsIoOperationRepository.findOne(code);
		ArrayList<Medical> medicals = medicalsIoOperations.getMedicals(foundMedical.getDescription(), foundMedical.getType().getCode(), false);
		assertThat(medicals.get(medicals.size() - 1).getCode()).isEqualTo(code);
	}

	@Test
	public void testIoMedicalExists() throws Exception {
		int code = _setupTestMedical(false);
		Medical foundMedical = medicalsIoOperationRepository.findOne(code);
		boolean result = medicalsIoOperations.medicalExists(foundMedical, false);
		assertThat(result).isTrue();
	}

	@Test
	public void testIoUpdateMedical() throws Exception {
		int code = _setupTestMedical(false);
		Medical foundMedical = medicalsIoOperationRepository.findOne(code);
		foundMedical.setDescription("Update");
		boolean result = medicalsIoOperations.updateMedical(foundMedical);
		assertThat(result).isTrue();
		Medical updateMedical = medicalsIoOperationRepository.findOne(code);
		assertThat(updateMedical.getDescription()).isEqualTo("Update");
	}

	@Test
	public void testIoNewMedical() throws Exception {
		MedicalType medicalType = testMedicalType.setup(false);
		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		Medical medical = testMedical.setup(medicalType, true);
		boolean result = medicalsIoOperations.newMedical(medical);
		assertThat(result).isTrue();
		_checkMedicalIntoDb(medical.getCode());
	}

	@Test
	public void testIoDeleteMedical() throws Exception {
		int code = _setupTestMedical(false);
		Medical foundMedical = medicalsIoOperationRepository.findOne(code);
		boolean result = medicalsIoOperations.deleteMedical(foundMedical);
		assertThat(result).isTrue();
		Medical deletedMedical = medicalsIoOperationRepository.findOne(code);
		assertThat(deletedMedical).isNull();
	}

	@Test
	public void testIsMedicalReferencedInStockMovement() throws Exception {
		int code = _setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findOne(code);
		boolean result = medicalsIoOperations.isMedicalReferencedInStockMovement(foundMovement.getMedical().getCode());
		assertThat(result).isTrue();
	}

	private int _setupTestMedical(boolean usingSet) throws OHException {
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical = testMedical.setup(medicalType, usingSet);
		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		medicalsIoOperationRepository.saveAndFlush(medical);
		return medical.getCode();
	}

	private void _checkMedicalIntoDb(int code) throws OHException {
		Medical foundMedical = medicalsIoOperationRepository.findOne(code);
		testMedical.check(foundMedical);
	}

	private int _setupTestMovement(boolean usingSet) throws OHException {
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical = testMedical.setup(medicalType, false);
		MovementType movementType = testMovementType.setup(false);
		Ward ward = testWard.setup(false);
		Lot lot = testLot.setup(false);
		Supplier supplier = testSupplier.setup(false);
		Movement movement = testMovement.setup(medical, movementType, ward, lot, supplier, usingSet);
		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		medicalsIoOperationRepository.saveAndFlush(medical);
		medicalStockMovementTypeIoOperationRepository.saveAndFlush(movementType);
		wardIoOperationRepository.saveAndFlush(ward);
		lotIoOperationRepository.saveAndFlush(lot);
		supplierIoOperationRepository.saveAndFlush(supplier);
		movementIoOperationRepository.saveAndFlush(movement);
		return movement.getCode();
	}
}