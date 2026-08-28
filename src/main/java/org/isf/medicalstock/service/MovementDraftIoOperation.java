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
package org.isf.medicalstock.service;

import java.util.List;

import org.isf.medicalstock.model.MovementDraft;
import org.isf.medicalstock.model.MovementDraftRow;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = OHServiceException.class)
@TranslateOHServiceException
public class MovementDraftIoOperation {

	private MovementDraftIoOperationRepository repository;

	private MovementDraftRowIoOperationRepository rowRepository;

	public MovementDraftIoOperation(MovementDraftIoOperationRepository movementDraftIoOperationRepository,
		MovementDraftRowIoOperationRepository movementDraftRowIoOperationRepository) {
		this.repository = movementDraftIoOperationRepository;
		this.rowRepository = movementDraftRowIoOperationRepository;
	}

	/**
	 * Return the list of {@link MovementDraft}s of the given kind, most recently modified first.
	 *
	 * @param kind - the {@link org.isf.medicalstock.model.MovementDraftKind} as string ({@code 'charge'} or {@code 'discharge'}).
	 * @return the list of {@link MovementDraft}s. It could be {@code empty}.
	 * @throws OHServiceException
	 */
	public List<MovementDraft> getMovementDrafts(String kind) throws OHServiceException {
		return repository.findByKind(kind);
	}

	/**
	 * Fetch a {@link MovementDraft} by id.
	 *
	 * @param id - the {@link MovementDraft} id.
	 * @return the {@link MovementDraft}. It could be {@code null}.
	 * @throws OHServiceException
	 */
	public MovementDraft getMovementDraft(int id) throws OHServiceException {
		return repository.findById(id).orElse(null);
	}

	/**
	 * Return the list of {@link MovementDraftRow}s of the given draft.
	 *
	 * @param draftId - the {@link MovementDraft} id.
	 * @return the list of {@link MovementDraftRow}s. It could be {@code empty}.
	 * @throws OHServiceException
	 */
	public List<MovementDraftRow> getMovementDraftRows(int draftId) throws OHServiceException {
		return rowRepository.findByDraftId(draftId);
	}

	/**
	 * Count the {@link MovementDraftRow}s of the given draft.
	 *
	 * @param draftId - the {@link MovementDraft} id.
	 * @return the number of rows or zero.
	 * @throws OHServiceException
	 */
	public int countMovementDraftRows(int draftId) throws OHServiceException {
		return rowRepository.countByDraftId(draftId);
	}

	/**
	 * Save a {@link MovementDraft} with its {@link MovementDraftRow}s.
	 * Any previously persisted row of the draft is replaced by the given ones (wholesale replace, no diffing).
	 * The draft back-reference of every row is set to the persisted draft.
	 *
	 * @param draft - the {@link MovementDraft} to save (insert or update).
	 * @param rows - the complete list of {@link MovementDraftRow}s of the draft; it could be {@code empty}.
	 * @return the persisted {@link MovementDraft} object.
	 * @throws OHServiceException
	 */
	public MovementDraft saveMovementDraft(MovementDraft draft, List<MovementDraftRow> rows) throws OHServiceException {
		MovementDraft savedDraft = repository.save(draft);
		rowRepository.deleteByDraftId(savedDraft.getId());
		for (MovementDraftRow row : rows) {
			row.setDraft(savedDraft);
			rowRepository.save(row);
		}
		return savedDraft;
	}

	/**
	 * Delete the specified {@link MovementDraft} with all its {@link MovementDraftRow}s.
	 *
	 * @param draft - the {@link MovementDraft} to delete.
	 * @throws OHServiceException
	 */
	public void deleteMovementDraft(MovementDraft draft) throws OHServiceException {
		rowRepository.deleteByDraftId(draft.getId());
		repository.delete(draft);
	}
}
