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
package org.isf.medicals.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.medicals.TestMedical;
import org.isf.medicals.model.Medical;
import org.isf.medicals.service.MedicalsIoOperationRepository;
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
import org.isf.utils.exception.OHDataIntegrityViolationException;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHException;
import org.isf.ward.TestWard;
import org.isf.ward.model.Ward;
import org.isf.ward.service.WardIoOperationRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MedicalBrowsingManagerTest extends OHCoreTestCase {

	private static TestMedical testMedical;
	private static TestMedicalType testMedicalType;
	private static TestMovement testMovement;
	private static TestMovementType testMovementType;
	private static TestWard testWard;
	private static TestLot testLot;
	private static TestSupplier testSupplier;

	@Autowired
	MedicalsIoOperationRepository medicalsIoOperationRepository;
	@Autowired
	MedicalBrowsingManager medicalBrowsingManager;
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
		Medical medical = medicalBrowsingManager.getMedical(code);
		assertThat(medical.getCode()).isEqualTo(foundMedical.getCode());
	}

	@Test
	void getMedicals_shouldReturnMedical() throws Exception {
		int code = setupTestMedical(false);
		Medical foundMedical = medicalsIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedical).isNotNull();
		List<Medical> medicals = medicalBrowsingManager.getMedicals();
		assertThat(medicals.get(medicals.size() - 1).getCode()).isEqualTo((Integer) code);
	}

	@Test
	void getMedicalsSortedByName_shouldReturnMedical() throws Exception {
		int code = setupTestMedical(false);
		Medical foundMedical = medicalsIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedical).isNotNull();
		List<Medical> medicals = medicalBrowsingManager.getMedicalsSortedByName();
		assertThat(medicals.get(medicals.size() - 1).getCode()).isEqualTo((Integer) code);
	}

	@Test
	void getMedicalsSortedByCode_shouldReturnMedical() throws Exception {
		int code = setupTestMedical(false);
		Medical foundMedical = medicalsIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedical).isNotNull();
		List<Medical> medicals = medicalBrowsingManager.getMedicalsSortedByCode();
		assertThat(medicals.get(medicals.size() - 1).getCode()).isEqualTo((Integer) code);
	}

	@Test
	void getMedicals_shouldReturnMedical_whenDescriptionGiven() throws Exception {
		int code = setupTestMedical(false);
		Medical foundMedical = medicalsIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedical).isNotNull();
		List<Medical> medicals = medicalBrowsingManager.getMedicals(foundMedical.getDescription());
		assertThat(medicals.get(medicals.size() - 1).getCode()).isEqualTo((Integer) code);
	}

	@Test
	void getMedicals_shouldReturnMedical_whenTypeDescriptionGivenAndNotNameSorted() throws Exception {
		int code = setupTestMedical(false);
		Medical foundMedical = medicalsIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedical).isNotNull();
		List<Medical> medicals = medicalBrowsingManager.getMedicals(foundMedical.getDescription(), false);
		assertThat(medicals.get(medicals.size() - 1).getCode()).isEqualTo((Integer) code);
	}

	@Test
	void getMedicals_shouldReturnMedical_whenTypeDescriptionGivenAndNameSorted() throws Exception {
		int code = setupTestMedical(false);
		Medical foundMedical = medicalsIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedical).isNotNull();
		List<Medical> medicals = medicalBrowsingManager.getMedicals(foundMedical.getDescription(), true);
		assertThat(medicals.get(medicals.size() - 1).getCode()).isEqualTo((Integer) code);
	}

	@Test
	void getMedicals_shouldReturnMedical_whenFilteredByDescriptionTypeAndCritical() throws Exception {
		int code = setupTestMedical(false);
		Medical foundMedical = medicalsIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedical).isNotNull();
		List<Medical> medicals = medicalBrowsingManager.getMedicals(foundMedical.getDescription(), foundMedical.getType().getCode(), true);
		assertThat(medicals.get(medicals.size() - 1).getCode()).isEqualTo((Integer) code);
	}

	@Test
	void newMedical_shouldSaveMedical() throws Exception {
		MedicalType medicalType = testMedicalType.setup(false);
		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		Medical medical = testMedical.setup(medicalType, true);
		assertThat(medicalBrowsingManager.newMedical(medical)).isNotNull();
		checkMedicalIntoDb(medical.getCode());
	}

	@Test
	void newMedical_shouldSaveMedical_whenNotIgnoringSimilar() throws Exception {
		MedicalType medicalType = testMedicalType.setup(false);
		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		Medical medical = testMedical.setup(medicalType, true);
		assertThat(medicalBrowsingManager.newMedical(medical, false)).isNotNull();
		checkMedicalIntoDb(medical.getCode());
	}

	@Test
	void updateMedical_shouldSaveNewDescription() throws Exception {
		int code = setupTestMedical(false);
		Medical foundMedical = medicalsIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedical).isNotNull();
		foundMedical.setDescription("Update");
		assertThat(medicalBrowsingManager.updateMedical(foundMedical)).isNotNull();
		Medical updatedMedical = medicalsIoOperationRepository.findById(code).orElse(null);
		assertThat(updatedMedical).isNotNull();
		assertThat(updatedMedical.getDescription()).isEqualTo("Update");
	}

	@Test
	void updateMedical_shouldSaveNewDescription_whenIgnoringSimilar() throws Exception {
		int code = setupTestMedical(false);
		Medical foundMedical = medicalsIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedical).isNotNull();
		foundMedical.setDescription("Update");
		assertThat(medicalBrowsingManager.updateMedical(foundMedical, true)).isNotNull();
		Medical updatedMedical = medicalsIoOperationRepository.findById(code).orElse(null);
		assertThat(updatedMedical).isNotNull();
		assertThat(updatedMedical.getDescription()).isEqualTo("Update");
	}

	@Test
	void updateMedical_shouldSaveNewDescription_whenNotIgnoringSimilar() throws Exception {
		int code = setupTestMedical(false);
		Medical foundMedical = medicalsIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedical).isNotNull();
		foundMedical.setDescription("Update");
		assertThat(medicalBrowsingManager.updateMedical(foundMedical, false)).isNotNull();
		Medical updatedMedical = medicalsIoOperationRepository.findById(code).orElse(null);
		assertThat(updatedMedical).isNotNull();
		assertThat(updatedMedical.getDescription()).isEqualTo("Update");
	}

	@Test
	void deleteMedical_shouldRemoveMedical() throws Exception {
		int code = setupTestMedical(false);
		Medical foundMedical = medicalsIoOperationRepository.findById(code).orElse(null);
		assertThat(foundMedical).isNotNull();
		medicalBrowsingManager.deleteMedical(foundMedical);
		Medical medical = medicalsIoOperationRepository.findById(code).orElse(null);
		assertThat(medical).isNull();
	}

	@Test
	void deleteMedical_shouldThrow_whenMedicalIsInStockMovement() throws Exception {
		assertThatThrownBy(() -> {
			int medicalCode = setupTestMedical(false);
			Medical medical = medicalsIoOperationRepository.findById(medicalCode).orElse(null);
			assertThat(medical).isNotNull();
			MedicalType medicalType = testMedicalType.setup(false);
			MovementType movementType = testMovementType.setup(false);
			Ward ward = testWard.setup(false);
			Lot lot = testLot.setup(medical, false);
			Supplier supplier = testSupplier.setup(false);
			Movement movement = testMovement.setup(medical, movementType, ward, lot, supplier, true);
			supplierIoOperationRepository.saveAndFlush(supplier);
			wardIoOperationRepository.saveAndFlush(ward);
			medicalDsrStockMovementTypeIoOperationRepository.saveAndFlush(movementType);
			medicalTypeIoOperationRepository.saveAndFlush(medicalType);
			medicalsIoOperationRepository.saveAndFlush(medical);
			lotIoOperationRepository.saveAndFlush(lot);
			movementIoOperationRepository.saveAndFlush(movement);
			medicalBrowsingManager.deleteMedical(medical);
		})
						.isInstanceOf(OHDataIntegrityViolationException.class);
	}

	@Test
	void validateMedical_shouldThrow_whenMinQtyIsNegative() throws Exception {
		assertThatThrownBy(() -> {
			MedicalType medicalType = testMedicalType.setup(false);
			Medical medical = testMedical.setup(medicalType, false);
			medical.setMinqty(-1);
			medicalBrowsingManager.validateMedical(medical, false, false);
		})
						.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	void validateMedical_shouldThrow_whenPcsPerPckIsNegative() throws Exception {
		assertThatThrownBy(() -> {
			MedicalType medicalType = testMedicalType.setup(false);
			Medical medical = testMedical.setup(medicalType, false);
			medical.setPcsperpck(-1);
			medicalBrowsingManager.validateMedical(medical, false, false);
		})
						.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	void validateMedical_shouldThrow_whenDescriptionIsEmpty() throws Exception {
		assertThatThrownBy(() -> {
			MedicalType medicalType = testMedicalType.setup(false);
			Medical medical = testMedical.setup(medicalType, false);
			medical.setDescription("");
			medicalBrowsingManager.validateMedical(medical, false, false);
		})
						.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	void validateMedical_shouldThrow_whenProductCodeExists() throws Exception {
		assertThatThrownBy(() -> {
			int medicalCode = setupTestMedical(false);
			Medical medical = medicalsIoOperationRepository.findById(medicalCode).orElse(null);
			assertThat(medical).isNotNull();
			medicalBrowsingManager.validateMedical(medical, false, false);
		})
						.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	void validateMedical_shouldThrow_whenTypeAndDescriptionExist() throws Exception {
		assertThatThrownBy(() -> {
			int medicalCode = setupTestMedical(false);
			Medical medical = medicalsIoOperationRepository.findById(medicalCode).orElse(null);
			assertThat(medical).isNotNull();
			medical.setProdCode("");
			medicalBrowsingManager.validateMedical(medical, false, false);
		})
						.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	void validateMedical_shouldThrow_whenSimilarMedicalExistsAndNotIgnoringSimilar() throws Exception {
		assertThatThrownBy(() -> {
			int medicalCode = setupTestMedical(false);
			Medical medical = medicalsIoOperationRepository.findById(medicalCode).orElse(null);
			assertThat(medical).isNotNull();
			medical.setProdCode("");
			MedicalType medicalType = new MedicalType("code", "description");
			medicalTypeIoOperationRepository.saveAndFlush(medicalType);
			medical.setType(medicalType);
			medicalBrowsingManager.validateMedical(medical, false, false);
		})
						.isInstanceOf(OHDataValidationException.class);
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

}
