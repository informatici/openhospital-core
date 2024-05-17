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
package org.isf.pregtreattype.service;

import java.util.List;

import org.isf.pregtreattype.model.PregnantTreatmentType;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor=OHServiceException.class)
@TranslateOHServiceException
public class PregnantTreatmentTypeIoOperation {

	private PregnantTreatmentTypeIoOperationRepository repository;

	public PregnantTreatmentTypeIoOperation(PregnantTreatmentTypeIoOperationRepository pregnantTreatmentTypeIoOperationRepository) {
		this.repository = pregnantTreatmentTypeIoOperationRepository;
	}
	
	/**
	 * Return the list of {@link PregnantTreatmentType}s.
	 * 
	 * @return the list of {@link PregnantTreatmentType}s
	 * @throws OHServiceException 
	 */
	public List<PregnantTreatmentType> getPregnantTreatmentType() throws OHServiceException {
		return repository.findAllByOrderByDescriptionAsc();
	}
	
	/**
	 * Insert a {@link PregnantTreatmentType} into the DB.
	 * 
	 * @param pregnantTreatmentType - the {@link PregnantTreatmentType} to insert
	 * @return the newly inserted {@link PregnantTreatmentType} object.
	 * @throws OHServiceException 
	 */
	public PregnantTreatmentType newPregnantTreatmentType(PregnantTreatmentType pregnantTreatmentType) throws OHServiceException {
		return repository.save(pregnantTreatmentType);
	}
	
	/**
	 * Update a {@link PregnantTreatmentType} in the DB.
	 * 
	 * @param pregnantTreatmentType - the {@link PregnantTreatmentType} to update
	 * @return the updated {@link PregnantTreatmentType} object.
	 * @throws OHServiceException 
	 */
	public PregnantTreatmentType updatePregnantTreatmentType(PregnantTreatmentType pregnantTreatmentType) throws OHServiceException {
		return repository.save(pregnantTreatmentType);
	}
	
	/**
	 * Delete a {@link PregnantTreatmentType} in the DB
	 * 
	 * @param pregnantTreatmentType - the {@link PregnantTreatmentType} to delete
	 * @throws OHServiceException
	 */
	public void deletePregnantTreatmentType(PregnantTreatmentType pregnantTreatmentType) throws OHServiceException {
		repository.delete(pregnantTreatmentType);
	}
	
	/**
	 * Check if the code is already in use.
	 * 
	 * @param code - the code
	 * @return {@code true} if the code is already in use, {@code false} otherwise
	 * @throws OHServiceException 
	 */
	public boolean isCodePresent(String code) throws OHServiceException {
		return repository.existsById(code);
	}

}
