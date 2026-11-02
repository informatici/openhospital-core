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
package org.isf.medicals.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.medicals.TestMedical;
import org.isf.medicals.model.Medical;
import org.isf.medicalstock.TestLot;
import org.isf.medicalstock.TestMovement;
import org.isf.medicalstock.model.Lot;
import org.isf.medicalstock.model.Movement;
import org.isf.medicalstock.service.LotIoOperationRepository;
import org.isf.medicalstock.service.MovementIoOperationRepository;
import org.isf.medstockmovtype.TestMovementType;
import org.isf.medstockmovtype.model.MovementType;
import org.isf.medstockmovtype.service.MedicalDsrStockMovementTypeIoOperationRepository;
import org.isf.medtype.TestMedicalType;
import org.isf.medtype.model.MedicalType;
import org.isf.medtype.service.MedicalTypeIoOperationRepository;
import org.isf.supplier.TestSupplier;
import org.isf.supplier.model.Supplier;
import org.isf.supplier.service.SupplierIoOperationRepository;
import org.isf.utils.exception.OHException;
import org.isf.ward.TestWard;
import org.isf.ward.model.Ward;
import org.isf.ward.service.WardIoOperationRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MedicalsIoOperationsTest extends OHCoreTestCase {

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
	MedicalDsrStockMovementTypeIoOperationRepository medicalDsrStockMovementTypeIoOperationRepository;
	@Autowired
	WardIoOperationRepository wardIoOperationRepository;
	@Autowired
	LotIoOperationRepository lotIoOperationRepository;
	@Autowired
	SupplierIoOperationRepository supplierIoOperationRepository;

	@BeforeAll
	static void setUpClass() {
		testMedical = new TestMedical();
		testMedicalType = new TestMedicalType();
		testMovement = new TestMovement();
		testMovementType = new TestMovementType();
		testWard = new TestWard();
		testLot = new TestLot();
		testSupplier = new TestSupplier();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void getMedical_shouldReturnMedical_whenCodeExists() throws Exception {
		int code = setupTestMedical(false);
		Medical foundMedical = medicalsIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedical).isNotNull();
		Medical medical = medicalsIoOperations.getMedical(code);
		assertThat(medical.getCode()).isEqualTo(foundMedical.getCode());
	}

	@Test
	void getMedicals_shouldReturnMedical() throws Exception {
		int code = setupTestMedical(false);
		Medical foundMedical = medicalsIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedical).isNotNull();
		List<Medical> medicals = medicalsIoOperations.getMedicals();
		assertThat(medicals.get(medicals.size() - 1).getCode()).isEqualTo((Integer) code);
	}

	@Test
	void getMedicals_shouldReturnMedical_whenDescriptionGiven() throws Exception {
		int code = setupTestMedical(false);
		Medical foundMedical = medicalsIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedical).isNotNull();
		List<Medical> medicals = medicalsIoOperations.getMedicals(String.valueOf(foundMedical.getDescription()));
		assertThat(medicals.get(medicals.size() - 1).getCode()).isEqualTo((Integer) code);
	}

	@Test
	void getMedicals_shouldReturnMedical_whenDescriptionIsNull() throws Exception {
		int code = setupTestMedical(false);
		Medical foundMedical = medicalsIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedical).isNotNull();
		List<Medical> medicals = medicalsIoOperations.getMedicals(null);
		assertThat(medicals.get(medicals.size() - 1).getCode()).isEqualTo((Integer) code);
	}

	@Test
	void getMedicals_shouldReturnMedical_whenTypeIsNullAndNotNameSorted() throws Exception {
		Integer code = setupTestMedical(false);
		Medical foundMedical = medicalsIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedical).isNotNull();
		List<Medical> medicals = medicalsIoOperations.getMedicals(null, false);
		assertThat(medicals.get(medicals.size() - 1).getCode()).isEqualTo(code);
	}

	@Test
	void getMedicals_shouldReturnMedical_whenTypeDescriptionGivenAndNameSorted() throws Exception {
		Integer code = setupTestMedical(false);
		Medical foundMedical = medicalsIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedical).isNotNull();
		List<Medical> medicals = medicalsIoOperations.getMedicals(String.valueOf(foundMedical.getDescription()), true);
		assertThat(medicals.get(medicals.size() - 1).getCode()).isEqualTo(code);
	}

	@Test
	void getMedicals_shouldReturnMedical_whenFilteredByTypeAndCritical() throws Exception {
		Integer code = setupTestMedical(false);
		Medical foundMedical = medicalsIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedical).isNotNull();
		List<Medical> medicals = medicalsIoOperations.getMedicals(null, foundMedical.getType().getCode(), true);
		assertThat(medicals.get(medicals.size() - 1).getCode()).isEqualTo(code);
	}

	@Test
	void getMedicals_shouldReturnMedical_whenFilteredByType() throws Exception {
		Integer code = setupTestMedical(false);
		Medical foundMedical = medicalsIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedical).isNotNull();
		List<Medical> medicals = medicalsIoOperations.getMedicals(null, foundMedical.getType().getCode(), false);
		assertThat(medicals.get(medicals.size() - 1).getCode()).isEqualTo(code);
	}

	@Test
	void getMedicals_shouldReturnMedical_whenFilteredByCritical() throws Exception {
		Integer code = setupTestMedical(false);
		Medical foundMedical = medicalsIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedical).isNotNull();
		List<Medical> medicals = medicalsIoOperations.getMedicals(null, null, true);
		assertThat(medicals.get(medicals.size() - 1).getCode()).isEqualTo(code);
	}

	@Test
	void getMedicals_shouldReturnMedical_whenNotFiltered() throws Exception {
		Integer code = setupTestMedical(false);
		Medical foundMedical = medicalsIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedical).isNotNull();
		List<Medical> medicals = medicalsIoOperations.getMedicals(null, null, false);
		assertThat(medicals.get(medicals.size() - 1).getCode()).isEqualTo(code);
	}

	@Test
	void getMedicals_shouldReturnMedical_whenFilteredByDescriptionTypeAndCritical() throws Exception {
		Integer code = setupTestMedical(false);
		Medical foundMedical = medicalsIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedical).isNotNull();
		List<Medical> medicals = medicalsIoOperations.getMedicals(foundMedical.getDescription(), foundMedical.getType().getCode(), true);
		assertThat(medicals.get(medicals.size() - 1).getCode()).isEqualTo(code);
	}

	@Test
	void getMedicals_shouldReturnMedical_whenFilteredByDescriptionAndType() throws Exception {
		Integer code = setupTestMedical(false);
		Medical foundMedical = medicalsIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedical).isNotNull();
		List<Medical> medicals = medicalsIoOperations.getMedicals(foundMedical.getDescription(), foundMedical.getType().getCode(), false);
		assertThat(medicals.get(medicals.size() - 1).getCode()).isEqualTo(code);
	}

	@Test
	void getMedicals_shouldReturnMedical_whenFilteredByDescriptionAndCritical() throws Exception {
		Integer code = setupTestMedical(false);
		Medical foundMedical = medicalsIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedical).isNotNull();
		List<Medical> medicals = medicalsIoOperations.getMedicals(foundMedical.getDescription(), null, true);
		assertThat(medicals.get(medicals.size() - 1).getCode()).isEqualTo(code);
	}

	@Test
	void getMedicals_shouldReturnMedical_whenFilteredByDescription() throws Exception {
		Integer code = setupTestMedical(false);
		Medical foundMedical = medicalsIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedical).isNotNull();
		List<Medical> medicals = medicalsIoOperations.getMedicals(foundMedical.getDescription(), null, false);
		assertThat(medicals.get(medicals.size() - 1).getCode()).isEqualTo(code);
	}

	@Test
	void medicalCheck_shouldReturnMedicalItself_whenUpdateIsFalse() throws Exception {
		int code = setupTestMedical(false);
		Medical foundMedical = medicalsIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedical).isNotNull();
		List<Medical> medicals = medicalsIoOperations.medicalCheck(foundMedical, false);
		assertThat(medicals.get(medicals.size() - 1).getCode()).isEqualTo(code);
	}

	@Test
	void medicalCheck_shouldReturnSimilarMedical_whenUpdateIsFalse() throws Exception {
		int code = setupTestMedical(false);
		Medical foundMedical = medicalsIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedical).isNotNull();
		List<Medical> medicals = medicalsIoOperations.medicalCheck(foundMedical, false);
		assertThat(medicals.get(medicals.size() - 1).getCode()).isEqualTo(code);
	}

	@Test
	void productCodeExists_shouldReturnFalse_whenUpdatingSameMedical() throws Exception {
		int code = setupTestMedical(false);
		Medical foundMedical = medicalsIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedical).isNotNull();
		assertThat(medicalsIoOperations.productCodeExists(foundMedical, true)).isFalse();
	}

	@Test
	void productCodeExists_shouldReturnTrue_whenCodeExistsAndNotUpdating() throws Exception {
		int code = setupTestMedical(false);
		Medical foundMedical = medicalsIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedical).isNotNull();
		assertThat(medicalsIoOperations.productCodeExists(foundMedical, false)).isTrue();
	}

	@Test
	void medicalExists_shouldReturnFalse_whenUpdatingSameMedical() throws Exception {
		int code = setupTestMedical(false);
		Medical foundMedical = medicalsIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedical).isNotNull();
		assertThat(medicalsIoOperations.medicalExists(foundMedical, true)).isFalse();
	}

	@Test
	void medicalExists_shouldReturnTrue_whenMedicalExistsAndNotUpdating() throws Exception {
		int code = setupTestMedical(false);
		Medical foundMedical = medicalsIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedical).isNotNull();
		assertThat(medicalsIoOperations.medicalExists(foundMedical, false)).isTrue();
	}

	@Test
	void newMedical_shouldSaveMedical() throws Exception {
		MedicalType medicalType = testMedicalType.setup(false);
		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		Medical medical = testMedical.setup(medicalType, true);
		Medical result = medicalsIoOperations.newMedical(medical);
		assertThat(result).isNotNull();
		checkMedicalIntoDb(medical.getCode());
	}

	@Test
	void updateMedical_shouldSaveNewDescription() throws Exception {
		int code = setupTestMedical(false);
		Medical foundMedical = medicalsIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedical).isNotNull();
		foundMedical.setDescription("Update");
		Medical result = medicalsIoOperations.updateMedical(foundMedical);
		assertThat(result).isNotNull();
		Medical updatedMedical = medicalsIoOperationRepository.findById(code).orElse(null);
		assertThat(updatedMedical).isNotNull();
		assertThat(updatedMedical.getDescription()).isEqualTo("Update");
	}

	@Test
	void deleteMedical_shouldRemoveMedical() throws Exception {
		int code = setupTestMedical(false);
		Medical foundMedical = medicalsIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedical).isNotNull();
		medicalsIoOperations.deleteMedical(foundMedical);
		assertThat(medicalsIoOperationRepository.findById(code)).isEmpty();
	}

	@Test
	void isMedicalReferencedInStockMovement_shouldReturnTrue_whenMovementExists() throws Exception {
		int code = setupTestMovement(false);
		Movement foundMovement = movementIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMovement).isNotNull();
		assertThat(medicalsIoOperations.isMedicalReferencedInStockMovement(foundMovement.getMedical().getCode())).isTrue();
	}

	private int setupTestMedical(boolean usingSet) throws OHException {
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical = testMedical.setup(medicalType, usingSet);
		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		medicalsIoOperationRepository.saveAndFlush(medical);
		return medical.getCode();
	}

	private void checkMedicalIntoDb(int code) throws OHException {
		Medical foundMedical = medicalsIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedical).isNotNull();
		testMedical.check(foundMedical);
	}

	private int setupTestMovement(boolean usingSet) throws OHException {
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical = testMedical.setup(medicalType, false);
		MovementType movementType = testMovementType.setup(false);
		Ward ward = testWard.setup(false);
		Lot lot = testLot.setup(medical, false);
		Supplier supplier = testSupplier.setup(false);
		Movement movement = testMovement.setup(medical, movementType, ward, lot, supplier, usingSet);
		wardIoOperationRepository.saveAndFlush(ward);
		supplierIoOperationRepository.saveAndFlush(supplier);
		medicalDsrStockMovementTypeIoOperationRepository.saveAndFlush(movementType);
		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		medicalsIoOperationRepository.saveAndFlush(medical);
		lotIoOperationRepository.saveAndFlush(lot);
		movementIoOperationRepository.saveAndFlush(movement);
		return movement.getCode();
	}

}
