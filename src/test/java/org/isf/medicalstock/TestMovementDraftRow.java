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
import static org.assertj.core.data.Offset.offset;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.isf.medicals.model.Medical;
import org.isf.medicalstock.model.MovementDraft;
import org.isf.medicalstock.model.MovementDraftRow;
import org.isf.utils.exception.OHException;

public class TestMovementDraftRow {

	private Integer id = null;
	private int quantity = 100;
	private int unitsOrPackets = 1;
	private String lotCode = "TESTDRAFTLOT";
	private LocalDateTime lotPreparationDate = LocalDateTime.of(2000, 1, 1, 0, 0, 0);
	private LocalDateTime lotDueDate = LocalDateTime.of(2001, 1, 1, 0, 0, 0);
	private BigDecimal lotCost = new BigDecimal("10.50");
	private boolean newLot = true;
	private boolean updateLotCost = false;

	public MovementDraftRow setup(MovementDraft draft, Medical medical, boolean usingSet) throws OHException {
		MovementDraftRow movementDraftRow;

		if (usingSet) {
			movementDraftRow = new MovementDraftRow();
			setParameters(movementDraftRow);
			movementDraftRow.setDraft(draft);
			movementDraftRow.setMedical(medical);
		} else {
			// Create MovementDraftRow with all parameters
			movementDraftRow = new MovementDraftRow(id, draft, medical, quantity, unitsOrPackets, lotCode, lotPreparationDate, lotDueDate, lotCost, newLot,
				updateLotCost);
		}
		return movementDraftRow;
	}

	public void setParameters(MovementDraftRow movementDraftRow) {
		movementDraftRow.setId(id);
		movementDraftRow.setQuantity(quantity);
		movementDraftRow.setUnitsOrPackets(unitsOrPackets);
		movementDraftRow.setLotCode(lotCode);
		movementDraftRow.setLotPreparationDate(lotPreparationDate);
		movementDraftRow.setLotDueDate(lotDueDate);
		movementDraftRow.setLotCost(lotCost);
		movementDraftRow.setNewLot(newLot);
		movementDraftRow.setUpdateLotCost(updateLotCost);
	}

	public void check(MovementDraftRow movementDraftRow, int id) {
		assertThat(movementDraftRow.getId()).isEqualTo(id);
		assertThat(movementDraftRow.getQuantity()).isEqualTo(quantity);
		assertThat(movementDraftRow.getUnitsOrPackets()).isEqualTo(unitsOrPackets);
		assertThat(movementDraftRow.getLotCode()).isEqualTo(lotCode);
		assertThat(movementDraftRow.getLotPreparationDate()).isEqualTo(lotPreparationDate);
		assertThat(movementDraftRow.getLotDueDate()).isEqualTo(lotDueDate);
		assertThat(movementDraftRow.getLotCost().doubleValue()).isCloseTo(lotCost.doubleValue(), offset(0.0));
		assertThat(movementDraftRow.isNewLot()).isEqualTo(newLot);
		assertThat(movementDraftRow.isUpdateLotCost()).isEqualTo(updateLotCost);
	}
}
