/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2023 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.admtype.service;

import java.util.List;

import org.isf.admtype.model.AdmissionType;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Persistence class for admtype module.
 */
@Service
@Transactional(rollbackFor=OHServiceException.class)
@TranslateOHServiceException
public class AdmissionTypeIoOperation {

	private AdmissionTypeIoOperationRepository repository;

	public AdmissionTypeIoOperation(AdmissionTypeIoOperationRepository admissionTypeIoOperationRepository) {
		this.repository = admissionTypeIoOperationRepository;
	}

	/**
	 * Returns all the available {@link AdmissionType}s.
	 * @return a list of admission types.
	 * @throws OHServiceException if an error occurs.
	 */
	public List<AdmissionType> getAdmissionType() throws OHServiceException {
		return repository.findAllByOrderByDescriptionAsc();
	}

	/**
	 * Updates the specified {@link AdmissionType}.
	 * @param admissionType the admission type to update.
	 * @return the updated admissionType object that is persisted.
	 * @throws OHServiceException if an error occurs during the update.
	 */
	public AdmissionType updateAdmissionType(AdmissionType admissionType) throws OHServiceException {
		return repository.save(admissionType);
	}

	/**
	 * Stores a new {@link AdmissionType}.
	 * @param admissionType the admission type to store.
	 * @return the updated admissionType object that is persisted.
	 * @throws OHServiceException if an error occurs during the storing operation.
	 */
	public AdmissionType newAdmissionType(AdmissionType admissionType) throws OHServiceException {
		return repository.save(admissionType);
	}

	/**
	 * Deletes the specified {@link AdmissionType}.
	 * @param admissionType the admission type to delete.
	 * @throws OHServiceException if an error occurs during the delete operation.
	 */
	public void deleteAdmissionType(AdmissionType admissionType) throws OHServiceException {
		repository.delete(admissionType);
	}

	/**
	 * Checks if the specified Code is already used by others {@link AdmissionType}s.
	 * @param code the admission type code to check.
	 * @return {@code true} if the code is already used, {@code false} otherwise.
	 * @throws OHServiceException if an error occurs during the check.
	 */
	public boolean isCodePresent(String code) throws OHServiceException {
		return repository.existsById(code);
	}
}
