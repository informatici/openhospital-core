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
package org.isf.medicalstock.manager;

import java.util.ArrayList;
import java.util.List;

import org.isf.generaldata.MessageBundle;
import org.isf.medicalstock.model.MovementDraft;
import org.isf.medicalstock.model.MovementDraftKind;
import org.isf.medicalstock.model.MovementDraftRow;
import org.isf.medicalstock.service.MovementDraftIoOperation;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.springframework.stereotype.Component;

/**
 * Manager for {@link MovementDraft}s: persisted wizard state of not-yet-confirmed
 * charge/discharge stock movements. Drafts are permissive by definition (header and
 * rows can be incomplete): only structural checks are performed here, the full
 * movement validation runs untouched at final approval through
 * {@link MovStockInsertingManager}.
 */
@Component
public class MovStockDraftManager {

	private final MovementDraftIoOperation ioOperations;

	public MovStockDraftManager(MovementDraftIoOperation movementDraftIoOperation) {
		this.ioOperations = movementDraftIoOperation;
	}

	/**
	 * Return the list of {@link MovementDraft}s of the given kind, most recently modified first.
	 *
	 * @param kind - the {@link MovementDraftKind}.
	 * @return the list of {@link MovementDraft}s. It could be {@code empty}.
	 * @throws OHServiceException
	 */
	public List<MovementDraft> getMovementDrafts(MovementDraftKind kind) throws OHServiceException {
		return ioOperations.getMovementDrafts(kind.toString());
	}

	/**
	 * Fetch a {@link MovementDraft} by id.
	 *
	 * @param id - the {@link MovementDraft} id.
	 * @return the {@link MovementDraft}. It could be {@code null}.
	 * @throws OHServiceException
	 */
	public MovementDraft getMovementDraft(int id) throws OHServiceException {
		return ioOperations.getMovementDraft(id);
	}

	/**
	 * Return the list of {@link MovementDraftRow}s of the given draft.
	 *
	 * @param draftId - the {@link MovementDraft} id.
	 * @return the list of {@link MovementDraftRow}s. It could be {@code empty}.
	 * @throws OHServiceException
	 */
	public List<MovementDraftRow> getMovementDraftRows(int draftId) throws OHServiceException {
		return ioOperations.getMovementDraftRows(draftId);
	}

	/**
	 * Count the {@link MovementDraftRow}s of the given draft.
	 *
	 * @param draftId - the {@link MovementDraft} id.
	 * @return the number of rows or zero.
	 * @throws OHServiceException
	 */
	public int countMovementDraftRows(int draftId) throws OHServiceException {
		return ioOperations.countMovementDraftRows(draftId);
	}

	/**
	 * Save a {@link MovementDraft} with its {@link MovementDraftRow}s.
	 * Any previously persisted row of the draft is replaced by the given ones.
	 *
	 * @param draft - the {@link MovementDraft} to save (insert or update).
	 * @param rows - the complete list of {@link MovementDraftRow}s of the draft; it could be {@code empty}.
	 * @return the persisted {@link MovementDraft} object.
	 * @throws OHServiceException
	 */
	public MovementDraft saveMovementDraft(MovementDraft draft, List<MovementDraftRow> rows) throws OHServiceException {
		validateMovementDraft(draft, rows);
		return ioOperations.saveMovementDraft(draft, rows);
	}

	/**
	 * Delete the specified {@link MovementDraft} with all its {@link MovementDraftRow}s.
	 *
	 * @param draft - the {@link MovementDraft} to delete.
	 * @throws OHServiceException
	 */
	public void deleteMovementDraft(MovementDraft draft) throws OHServiceException {
		ioOperations.deleteMovementDraft(draft);
	}

	/**
	 * Verify that the draft is structurally valid: parseable {@link MovementDraftKind} and a medical on every row.
	 * Everything else (date, reference number, supplier/ward, quantities, lot data, costs) is deliberately
	 * not checked: a draft may be incomplete, the standard validation runs at final approval.
	 *
	 * @param draft - the {@link MovementDraft} to validate.
	 * @param rows - the {@link MovementDraftRow}s to validate.
	 * @throws OHDataValidationException
	 */
	private void validateMovementDraft(MovementDraft draft, List<MovementDraftRow> rows) throws OHDataValidationException {
		List<OHExceptionMessage> errors = new ArrayList<>();
		boolean validKind = false;
		if (draft.getKind() != null) {
			for (MovementDraftKind kind : MovementDraftKind.values()) {
				if (kind.toString().equals(draft.getKind())) {
					validKind = true;
					break;
				}
			}
		}
		if (!validKind) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.medicalstock.pleasechooseatype.msg")));
		}
		for (MovementDraftRow row : rows) {
			if (row.getMedical() == null) {
				errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.medicalstock.chooseamedical.msg")));
				break;
			}
		}
		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
	}
}
