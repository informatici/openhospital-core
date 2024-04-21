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
package org.isf.dicomtype.service;

import java.util.List;

import org.isf.dicomtype.model.DicomType;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor=OHServiceException.class)
@TranslateOHServiceException
public class DicomTypeIoOperation {

	private DicomTypeIoOperationRepository repository;

	public DicomTypeIoOperation(DicomTypeIoOperationRepository dicomTypeIoOperationRepository) {
		this.repository = dicomTypeIoOperationRepository;
	}

	/**
	 * Method that returns all DicomTypes in a list
	 * 
	 * @return the list of all DicomTypes
	 * @throws OHServiceException
	 */
	public List<DicomType> getDicomType() throws OHServiceException {
		return repository.findAllByOrderByDicomTypeDescriptionAsc();
	}

	/**
	 * Method that updates an already existing DicomType
	 * 
	 * @param dicomType
	 * @return returns the saved DicomType object.
	 * @throws OHServiceException
	 */
	public DicomType updateDicomType(DicomType dicomType) throws OHServiceException {
		return repository.save(dicomType);
	}

	/**
	 * Method that create a new DicomType
	 * 
	 * @param dicomType
	 * @return returns the updated DicomType object.
	 * @throws OHServiceException
	 */
	public DicomType newDicomType(DicomType dicomType) throws OHServiceException {
		return repository.save(dicomType);
	}

	/**
	 * Method that delete a DicomType
	 * 
	 * @param dicomType
	 * @throws OHServiceException
	 */
	public void deleteDicomType(DicomType dicomType) throws OHServiceException {
		repository.delete(dicomType);
	}

	/**
	 * Method that check if a DicomType already exists
	 * 
	 * @param code
	 * @return true - if the DicomType already exists
	 * @throws OHServiceException 
	 */
	public boolean isCodePresent(String code) throws OHServiceException {
		return repository.existsById(code);
	}
}
