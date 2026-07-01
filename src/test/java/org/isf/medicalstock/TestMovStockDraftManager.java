/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2025 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.medicalstock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.medicals.TestMedical;
import org.isf.medicals.model.Medical;
import org.isf.medicals.service.MedicalsIoOperationRepository;
import org.isf.medicalstock.manager.MovStockDraftManager;
import org.isf.medicalstock.model.MovementDraft;
import org.isf.medicalstock.model.MovementDraftKind;
import org.isf.medicalstock.model.MovementDraftRow;
import org.isf.medicalstock.service.MovementDraftIoOperationRepository;
import org.isf.medicalstock.service.MovementDraftRowIoOperationRepository;
import org.isf.medstockmovtype.TestMovementType;
import org.isf.medstockmovtype.model.MovementType;
import org.isf.medstockmovtype.service.MedicalDsrStockMovementTypeIoOperationRepository;
import org.isf.medtype.TestMedicalType;
import org.isf.medtype.model.MedicalType;
import org.isf.medtype.service.MedicalTypeIoOperationRepository;
import org.isf.supplier.TestSupplier;
import org.isf.supplier.model.Supplier;
import org.isf.supplier.service.SupplierIoOperationRepository;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHException;
import org.isf.ward.TestWard;
import org.isf.ward.model.Ward;
import org.isf.ward.service.WardIoOperationRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class TestMovStockDraftManager extends OHCoreTestCase {

	private static TestMovementDraft testMovementDraft;
	private static TestMovementDraftRow testMovementDraftRow;
	private static TestMovementType testMovementType;
	private static TestSupplier testSupplier;
	private static TestWard testWard;
	private static TestMedical testMedical;
	private static TestMedicalType testMedicalType;

	@Autowired
	MovStockDraftManager movStockDraftManager;

	@Autowired
	MovementDraftIoOperationRepository movementDraftIoOperationRepository;

	@Autowired
	MovementDraftRowIoOperationRepository movementDraftRowIoOperationRepository;

	@Autowired
	MedicalsIoOperationRepository medicalsIoOperationRepository;

	@Autowired
	MedicalTypeIoOperationRepository medicalTypeIoOperationRepository;

	@Autowired
	MedicalDsrStockMovementTypeIoOperationRepository medicalDsrStockMovementTypeIoOperationRepository;

	@Autowired
	SupplierIoOperationRepository supplierIoOperationRepository;

	@Autowired
	WardIoOperationRepository wardIoOperationRepository;

	@BeforeAll
	static void setUpClass() {
		testMovementDraft = new TestMovementDraft();
		testMovementDraftRow = new TestMovementDraftRow();
		testMovementType = new TestMovementType();
		testSupplier = new TestSupplier();
		testWard = new TestWard();
		testMedical = new TestMedical();
		testMedicalType = new TestMedicalType();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void testMgrSaveNewChargeDraft() throws Exception {
		MovementDraft draft = setupTestMovementDraft(MovementDraftKind.charge);
		Medical medical = setupTestMedical();
		List<MovementDraftRow> rows = new ArrayList<>();
		rows.add(testMovementDraftRow.setup(draft, medical, false));
		rows.add(new MovementDraftRow(null, draft, medical, 7, 0, "SECONDLOT", null, null, null, false, true));
		// half-typed row: no lot data at all, quantity still zero
		rows.add(new MovementDraftRow(null, draft, medical, 0, 0, null, null, null, null, false, false));
		MovementDraft savedDraft = movStockDraftManager.saveMovementDraft(draft, rows);

		testMovementDraft.check(savedDraft, savedDraft.getId());
		List<MovementDraft> chargeDrafts = movStockDraftManager.getMovementDrafts(MovementDraftKind.charge);
		assertThat(chargeDrafts).hasSize(1);
		assertThat(chargeDrafts.get(0).getSupplier().getSupId()).isEqualTo(draft.getSupplier().getSupId());
		assertThat(chargeDrafts.get(0).getType().getCode()).isEqualTo(draft.getType().getCode());

		List<MovementDraftRow> savedRows = movStockDraftManager.getMovementDraftRows(savedDraft.getId());
		assertThat(savedRows).hasSize(3);
		savedRows.sort(Comparator.comparing(MovementDraftRow::getId));
		testMovementDraftRow.check(savedRows.get(0), savedRows.get(0).getId());
		assertThat(savedRows.get(1).isUpdateLotCost()).isTrue();
		MovementDraftRow halfTypedRow = savedRows.get(2);
		assertThat(halfTypedRow.getQuantity()).isZero();
		assertThat(halfTypedRow.getLotCode()).isNull();
		assertThat(halfTypedRow.getLotPreparationDate()).isNull();
		assertThat(halfTypedRow.getLotDueDate()).isNull();
		assertThat(halfTypedRow.getLotCost()).isNull();
		assertThat(halfTypedRow.isNewLot()).isFalse();
		assertThat(halfTypedRow.isUpdateLotCost()).isFalse();
	}

	@Test
	void testMgrGetMovementDraftsFiltersByKind() throws Exception {
		MovementDraft chargeDraft = setupTestMovementDraft(MovementDraftKind.charge);
		movStockDraftManager.saveMovementDraft(chargeDraft, new ArrayList<>());
		Ward ward = chargeDraft.getWard();
		MovementDraft dischargeDraft = new MovementDraft(null, MovementDraftKind.discharge.toString(), null, null, "DischargeRef", null, ward);
		movStockDraftManager.saveMovementDraft(dischargeDraft, new ArrayList<>());

		List<MovementDraft> chargeDrafts = movStockDraftManager.getMovementDrafts(MovementDraftKind.charge);
		assertThat(chargeDrafts).hasSize(1);
		assertThat(chargeDrafts.get(0).getKind()).isEqualTo(MovementDraftKind.charge.toString());
		List<MovementDraft> dischargeDrafts = movStockDraftManager.getMovementDrafts(MovementDraftKind.discharge);
		assertThat(dischargeDrafts).hasSize(1);
		assertThat(dischargeDrafts.get(0).getKind()).isEqualTo(MovementDraftKind.discharge.toString());
		assertThat(dischargeDrafts.get(0).getWard().getCode()).isEqualTo(ward.getCode());
		assertThat(dischargeDrafts.get(0).getSupplier()).isNull();
	}

	@Test
	void testMgrGetMovementDraft() throws Exception {
		MovementDraft draft = setupTestMovementDraft(MovementDraftKind.charge);
		MovementDraft savedDraft = movStockDraftManager.saveMovementDraft(draft, new ArrayList<>());
		MovementDraft foundDraft = movStockDraftManager.getMovementDraft(savedDraft.getId());
		assertThat(foundDraft).isNotNull();
		testMovementDraft.check(foundDraft, savedDraft.getId());
		assertThat(movStockDraftManager.getMovementDraft(-99999)).isNull();
	}

	@Test
	void testMgrUpdateDraftReplacesRows() throws Exception {
		MovementDraft draft = setupTestMovementDraft(MovementDraftKind.charge);
		Medical medical = setupTestMedical();
		List<MovementDraftRow> rows = new ArrayList<>();
		rows.add(testMovementDraftRow.setup(draft, medical, false));
		rows.add(new MovementDraftRow(null, draft, medical, 7, 0, "SECONDLOT", null, null, null, false, false));
		MovementDraft savedDraft = movStockDraftManager.saveMovementDraft(draft, rows);
		List<MovementDraftRow> oldRows = movStockDraftManager.getMovementDraftRows(savedDraft.getId());
		assertThat(oldRows).hasSize(2);

		savedDraft.setRefNo("UpdatedRef");
		List<MovementDraftRow> newRows = new ArrayList<>();
		newRows.add(new MovementDraftRow(null, savedDraft, medical, 33, 1, "THIRDLOT", null, null, null, true, false));
		MovementDraft updatedDraft = movStockDraftManager.saveMovementDraft(savedDraft, newRows);

		assertThat(updatedDraft.getId()).isEqualTo(savedDraft.getId());
		assertThat(updatedDraft.getRefNo()).isEqualTo("UpdatedRef");
		List<MovementDraftRow> replacedRows = movStockDraftManager.getMovementDraftRows(updatedDraft.getId());
		assertThat(replacedRows).hasSize(1);
		assertThat(replacedRows.get(0).getLotCode()).isEqualTo("THIRDLOT");
		for (MovementDraftRow oldRow : oldRows) {
			assertThat(movementDraftRowIoOperationRepository.findById(oldRow.getId())).isEmpty();
		}
	}

	@Test
	void testMgrCountMovementDraftRows() throws Exception {
		MovementDraft draft = setupTestMovementDraft(MovementDraftKind.charge);
		Medical medical = setupTestMedical();
		List<MovementDraftRow> rows = new ArrayList<>();
		rows.add(testMovementDraftRow.setup(draft, medical, false));
		rows.add(new MovementDraftRow(null, draft, medical, 7, 0, "SECONDLOT", null, null, null, false, false));
		MovementDraft savedDraft = movStockDraftManager.saveMovementDraft(draft, rows);
		assertThat(movStockDraftManager.countMovementDraftRows(savedDraft.getId())).isEqualTo(2);
		assertThat(movStockDraftManager.countMovementDraftRows(-99999)).isZero();
	}

	@Test
	void testMgrDeleteMovementDraft() throws Exception {
		MovementDraft draft = setupTestMovementDraft(MovementDraftKind.charge);
		Medical medical = setupTestMedical();
		List<MovementDraftRow> rows = new ArrayList<>();
		rows.add(testMovementDraftRow.setup(draft, medical, false));
		MovementDraft savedDraft = movStockDraftManager.saveMovementDraft(draft, rows);
		int draftId = savedDraft.getId();
		assertThat(movStockDraftManager.countMovementDraftRows(draftId)).isEqualTo(1);

		movStockDraftManager.deleteMovementDraft(savedDraft);
		assertThat(movementDraftIoOperationRepository.findById(draftId)).isEmpty();
		assertThat(movStockDraftManager.getMovementDraftRows(draftId)).isEmpty();
	}

	@Test
	void testMgrValidationRowWithoutMedical() throws Exception {
		MovementDraft draft = setupTestMovementDraft(MovementDraftKind.charge);
		List<MovementDraftRow> rows = new ArrayList<>();
		rows.add(new MovementDraftRow(null, draft, null, 10, 0, null, null, null, null, false, false));
		assertThatThrownBy(() -> movStockDraftManager.saveMovementDraft(draft, rows))
			.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	void testMgrValidationInvalidKind() throws Exception {
		MovementDraft nullKindDraft = new MovementDraft(null, null, null, null, "SomeRef", null, null);
		assertThatThrownBy(() -> movStockDraftManager.saveMovementDraft(nullKindDraft, new ArrayList<>()))
			.isInstanceOf(OHDataValidationException.class);
		MovementDraft invalidKindDraft = new MovementDraft(null, "notakind", null, null, "SomeRef", null, null);
		assertThatThrownBy(() -> movStockDraftManager.saveMovementDraft(invalidKindDraft, new ArrayList<>()))
			.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	void testMgrPermissiveDraft() throws Exception {
		// a draft is persisted wizard state: everything but the kind and the row medicals may be missing
		Medical medical = setupTestMedical();
		MovementDraft draft = new MovementDraft(null, MovementDraftKind.discharge.toString(), null, null, null, null, null);
		List<MovementDraftRow> rows = new ArrayList<>();
		rows.add(new MovementDraftRow(null, draft, medical, 0, 0, null, null, null, null, false, false));
		MovementDraft savedDraft = movStockDraftManager.saveMovementDraft(draft, rows);

		MovementDraft foundDraft = movStockDraftManager.getMovementDraft(savedDraft.getId());
		assertThat(foundDraft).isNotNull();
		assertThat(foundDraft.getType()).isNull();
		assertThat(foundDraft.getDate()).isNull();
		assertThat(foundDraft.getRefNo()).isNull();
		assertThat(foundDraft.getSupplier()).isNull();
		assertThat(foundDraft.getWard()).isNull();
		assertThat(movStockDraftManager.countMovementDraftRows(savedDraft.getId())).isEqualTo(1);
	}

	@Test
	void testMgrAuditFieldsPopulated() throws Exception {
		MovementDraft draft = setupTestMovementDraft(MovementDraftKind.charge);
		Medical medical = setupTestMedical();
		List<MovementDraftRow> rows = new ArrayList<>();
		rows.add(testMovementDraftRow.setup(draft, medical, false));
		MovementDraft savedDraft = movStockDraftManager.saveMovementDraft(draft, rows);

		MovementDraft foundDraft = movementDraftIoOperationRepository.findById(savedDraft.getId()).orElse(null);
		assertThat(foundDraft).isNotNull();
		assertThat(foundDraft.getCreatedDate()).isNotNull();
		assertThat(foundDraft.getLastModifiedDate()).isNotNull();
		MovementDraftRow foundRow = movStockDraftManager.getMovementDraftRows(savedDraft.getId()).get(0);
		assertThat(foundRow.getCreatedDate()).isNotNull();
		assertThat(foundRow.getLastModifiedDate()).isNotNull();
	}

	private MovementDraft setupTestMovementDraft(MovementDraftKind kind) throws OHException {
		MovementType movementType = testMovementType.setup(false);
		medicalDsrStockMovementTypeIoOperationRepository.saveAndFlush(movementType);
		Supplier supplier = testSupplier.setup(false);
		supplierIoOperationRepository.saveAndFlush(supplier);
		Ward ward = testWard.setup(false);
		wardIoOperationRepository.saveAndFlush(ward);
		MovementDraft movementDraft = testMovementDraft.setup(movementType, supplier, ward, false);
		movementDraft.setKind(kind.toString());
		return movementDraft;
	}

	private Medical setupTestMedical() throws OHException {
		MedicalType medicalType = testMedicalType.setup(false);
		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		Medical medical = testMedical.setup(medicalType, false);
		return medicalsIoOperationRepository.saveAndFlush(medical);
	}
}
