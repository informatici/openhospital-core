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
package org.isf.distype.service;

import java.util.List;
import java.util.Optional;

import org.isf.distype.model.DiseaseType;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Persistence class for the DisType module.
 */
@Service
@Transactional(rollbackFor=OHServiceException.class)
@TranslateOHServiceException
public class DiseaseTypeIoOperation {

	private final DiseaseTypeIoOperationRepository repository;

	public DiseaseTypeIoOperation(DiseaseTypeIoOperationRepository repository) {
		this.repository = repository;
	}
	
	/**
	 * Returns all the stored {@link DiseaseType}s.
	 * @return a list of disease type.
	 * @throws OHServiceException if an error occurs retrieving the diseases list.
	 */
	public List<DiseaseType> getDiseaseTypes() throws OHServiceException {
		return repository.findAllByOrderByDescriptionAsc();
	}

	/**
	 * Updates the specified {@link DiseaseType}.
	 * @param diseaseType the disease type to update.
	 * @return the updated {@link DiseaseType} object.
	 * @throws OHServiceException if an error occurs during the update operation.
	 */
	public DiseaseType updateDiseaseType(DiseaseType diseaseType) throws OHServiceException {
		return repository.save(diseaseType);
	}

	/**
	 * Store the specified {@link DiseaseType}.
	 * @param diseaseType the disease type to store.
	 * @return the new stored {@link DiseaseType} object.
	 * @throws OHServiceException if an error occurs during the store operation.
	 */
	public DiseaseType newDiseaseType(DiseaseType diseaseType) throws OHServiceException {
		return repository.save(diseaseType);
	}

	/**
	 * Deletes the specified {@link DiseaseType}.
	 * @param diseaseType the disease type to remove.
	 * @throws OHServiceException if an error occurs during the delete procedure.
	 */
	public void deleteDiseaseType(DiseaseType diseaseType) throws OHServiceException {
		repository.delete(diseaseType);
	}

	/**
	 * Checks if the specified code is already used by any {@link DiseaseType}.
	 * @param code the code to check.
	 * @return {@code true} if the code is used, false otherwise.
	 * @throws OHServiceException if an error occurs during the check.
	 */
	public boolean isCodePresent(String code) throws OHServiceException {
		return repository.existsById(code);
	}
	
	/**
	 * Returns {@link DiseaseType} given the type code.
	 * 
	 * @Param code
	 * @return object {@link DiseaseType}, {@code null} otherwise.
	 * @throws OHServiceException if an error occurs retrieving the diseases type.
	 */
	public DiseaseType getDiseaseType(String code) throws OHServiceException {
		Optional<DiseaseType> diseaseType = repository.findById(code);
		if (diseaseType.isPresent()) {
			return diseaseType.get();
		}
		return null;
	}

}
