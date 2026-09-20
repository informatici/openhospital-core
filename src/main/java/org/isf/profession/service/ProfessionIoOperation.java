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
package org.isf.profession.service;

import java.util.List;
import java.util.Optional;

import org.isf.profession.model.Profession;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Persistence class for the Profession module.
 */
@Service
@Transactional(rollbackFor=OHServiceException.class)
@TranslateOHServiceException
public class ProfessionIoOperation {

	private final ProfessionIoOperationRepository repository;

	public ProfessionIoOperation(ProfessionIoOperationRepository repository) {
		this.repository = repository;
	}

	/**
	 * Returns all the stored {@link Profession}s.
	 * @return a list of professions.
	 * @throws OHServiceException if an error occurs retrieving the professions list.
	 */
	public List<Profession> getProfessions() throws OHServiceException {
		return repository.findAllByOrderByDescriptionAsc();
	}

	/**
	 * Updates the specified {@link Profession}.
	 * @param profession the profession to update.
	 * @return the updated {@link Profession} object.
	 * @throws OHServiceException if an error occurs during the update operation.
	 */
	public Profession updateProfession(Profession profession) throws OHServiceException {
		return repository.save(profession);
	}

	/**
	 * Store the specified {@link Profession}.
	 * @param profession the profession to store.
	 * @return the new stored {@link Profession} object.
	 * @throws OHServiceException if an error occurs during the store operation.
	 */
	public Profession newProfession(Profession profession) throws OHServiceException {
		return repository.save(profession);
	}

	/**
	 * Deletes the specified {@link Profession}.
	 * @param profession the profession to remove.
	 * @throws OHServiceException if an error occurs during the delete procedure.
	 */
	public void deleteProfession(Profession profession) throws OHServiceException {
		repository.delete(profession);
	}

	/**
	 * Checks if the specified code is already used by any {@link Profession}.
	 * @param code the code to check.
	 * @return {@code true} if the code is used, false otherwise.
	 * @throws OHServiceException if an error occurs during the check.
	 */
	public boolean isCodePresent(String code) throws OHServiceException {
		return repository.existsById(code);
	}

	/**
	 * Returns {@link Profession} given the profession code.
	 *
	 * @Param code
	 * @return object {@link Profession}, {@code null} otherwise.
	 * @throws OHServiceException if an error occurs retrieving the profession.
	 */
	public Profession getProfession(String code) throws OHServiceException {
		Optional<Profession> profession = repository.findById(code);
		if (profession.isPresent()) {
			return profession.get();
		}
		return null;
	}

}
