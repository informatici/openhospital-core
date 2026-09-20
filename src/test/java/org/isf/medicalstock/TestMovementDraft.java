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
import static org.assertj.core.api.Assertions.within;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.isf.medicalstock.model.MovementDraft;
import org.isf.medicalstock.model.MovementDraftKind;
import org.isf.medstockmovtype.model.MovementType;
import org.isf.supplier.model.Supplier;
import org.isf.utils.exception.OHException;
import org.isf.utils.time.TimeTools;
import org.isf.ward.model.Ward;

public class TestMovementDraft {

	private Integer id = null;
	private String kind = MovementDraftKind.charge.toString();
	private LocalDateTime date = TimeTools.getNow();
	private String refNo = "TestDraftRef";

	public MovementDraft setup(MovementType movementType, Supplier supplier, Ward ward, boolean usingSet) throws OHException {
		MovementDraft movementDraft;

		if (usingSet) {
			movementDraft = new MovementDraft();
			setParameters(movementDraft, movementType, supplier, ward);
		} else {
			// Create MovementDraft with all parameters
			movementDraft = new MovementDraft(id, kind, movementType, date, refNo, supplier, ward);
		}
		return movementDraft;
	}

	public void setParameters(MovementDraft movementDraft, MovementType movementType, Supplier supplier, Ward ward) {
		movementDraft.setId(id);
		movementDraft.setKind(kind);
		movementDraft.setType(movementType);
		movementDraft.setDate(date);
		movementDraft.setRefNo(refNo);
		movementDraft.setSupplier(supplier);
		movementDraft.setWard(ward);
	}

	public void check(MovementDraft movementDraft, int id) {
		assertThat(movementDraft.getId()).isEqualTo(id);
		assertThat(movementDraft.getKind()).isEqualTo(kind);
		assertThat(movementDraft.getDate()).isCloseTo(date, within(1, ChronoUnit.SECONDS));
		assertThat(movementDraft.getRefNo()).isEqualTo(refNo);
	}
}
