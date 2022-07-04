/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2021 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.isf.distype.service;

import java.util.List;

import org.isf.distype.model.DiseaseType;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Persistence class for the DisType module.
 */
@Service
@Transactional(rollbackFor=OHServiceException.class)
@TranslateOHServiceException
public class DiseaseTypeIoOperation {

	@Autowired
	private DiseaseTypeIoOperationRepository repository;
	
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
	 * @return <code>true</code> if the disease type has been updated, false otherwise.
	 * @throws OHServiceException if an error occurs during the update operation.
	 */
	public boolean updateDiseaseType(DiseaseType diseaseType) throws OHServiceException {
		return repository.save(diseaseType) != null;
	}

	/**
	 * Store the specified {@link DiseaseType}.
	 * @param diseaseType the disease type to store.
	 * @return <code>true</code> if the {@link DiseaseType} has been stored, <code>false</code> otherwise.
	 * @throws OHServiceException if an error occurs during the store operation.
	 */
	public boolean newDiseaseType(DiseaseType diseaseType) throws OHServiceException {
		return repository.save(diseaseType) != null;
	}

	/**
	 * Deletes the specified {@link DiseaseType}.
	 * @param diseaseType the disease type to remove.
	 * @return <code>true</code> if the disease has been removed, <code>false</code> otherwise.
	 * @throws OHServiceException if an error occurs during the delete procedure.
	 */
	public boolean deleteDiseaseType(DiseaseType diseaseType) throws OHServiceException {
		repository.delete(diseaseType);
		return true;
	}

	/**
	 * Checks if the specified code is already used by any {@link DiseaseType}.
	 * @param code the code to check.
	 * @return <code>true</code> if the code is used, false otherwise.
	 * @throws OHServiceException if an error occurs during the check.
	 */
	public boolean isCodePresent(String code) throws OHServiceException {
		return repository.existsById(code);
	}
}
