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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.isf.disease.service;

import java.util.List;

import org.isf.disease.model.Disease;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * -----------------------------------------
 * This class offers the io operations for recovering and managing
 * diseases records from the database
 * 
 * @author Rick, Vero
 * <p>
 * modification history
 * 25/01/2006 - Rick, Vero, Pupo  - first beta version
 * 08/11/2006 - ross - added support for OPD and IPD flags
 * 09/06/2007 - ross - when updating, now the user can change the "dis type" also
 * 02/09/2008 - alex - added method for getting a Disease by his code
 * 					   added method for getting a DiseaseType by his code
 * 13/02/2009 - alex - modified query for ordering resultset
 *                     by description only
 * ------------------------------------------
 */
@Service
@Transactional(rollbackFor=OHServiceException.class)
@TranslateOHServiceException
public class DiseaseIoOperations {

	@Autowired
	private DiseaseIoOperationRepository repository;
	
	/**
	 * Gets a {@link Disease} with the specified code.
	 * @param code the disease code.
	 * @return the found disease, <code>null</code> if no disease has found.
	 * @throws OHServiceException if an error occurred getting the disease.
	 */
	public Disease getDiseaseByCode(String code) throws OHServiceException {
		return repository.findOneByCode(code);
	}
	
	/**
	 * Retrieves stored disease with the specified search parameters. 
	 * Booleans <code>opd</code>, <code>ipdIn</code> and <code>ipdOut</code> in AND logic between 
	 * each other only when <code>true</code>, ignored otherwise
	 * @param disTypeCode - not <code>null</code> apply to disease type
	 * @param opd - if <code>true</code> retrieves diseases related to outpatients
	 * @param ipdIn - if <code>true</code> retrieves diseases related to inpatients' admissions
	 * @param ipdOut - if <code>true</code> retrieves diseases related to inpatients' discharges
	 * @return the retrieved diseases.
	 * @throws OHServiceException if an error occurs retrieving the diseases.
	 */
	public List<Disease> getDiseases(String disTypeCode, boolean opd, boolean ipdIn, boolean ipdOut) throws OHServiceException {
		List<Disease> diseases;

		if (disTypeCode != null) {
			if (opd) {
				if (ipdIn) {
					if (ipdOut) {
						diseases = repository.findAllByDiseaseTypeCodeAndOpdAndIpdInAndIpdOut(disTypeCode);
					} else {
						diseases = repository.findAllByDiseaseTypeCodeAndOpdAndIpdIn(disTypeCode);
					}
				} else {
					if (ipdOut) {
						diseases = repository.findAllByDiseaseTypeCodeAndOpdAndIpdOut(disTypeCode);
					} else {
						diseases = repository.findAllByDiseaseTypeCodeAndOpd(disTypeCode);
					}
				}
			} else {

				if (ipdIn) {
					if (ipdOut) {
						diseases = repository.findAllByDiseaseTypeCodeAndIpdInAndIpdOut(disTypeCode);

					} else {
						diseases = repository.findAllByDiseaseTypeCodeAndIpdIn(disTypeCode);
					}
				} else {
					if (ipdOut) {
						diseases = repository.findAllByDiseaseTypeCodeAndIpdOut(disTypeCode);
					} else {
						diseases = repository.findAllByDiseaseTypeCode(disTypeCode);
					}
				}
			}
		} else {
			if (opd) {
				if (ipdIn) {
					if (ipdOut) {
						diseases = repository.findAllByOpdAndIpdInAndIpdOut();
					} else {
						diseases = repository.findAllByOpdAndIpdIn();
					}
				} else {
					if (ipdOut) {
						diseases = repository.findAllByOpdAndIpdOut();
					} else {
						diseases = repository.findAllByOpd();
					}
				}
			} else {

				if (ipdIn) {
					if (ipdOut) {
						diseases = repository.findAllByIpdInAndIpdOut();
					} else {
						diseases = repository.findAllByIpdIn();
					}
				} else {
					if (ipdOut) {
						diseases = repository.findAllByIpdOut();
					} else {
						diseases = repository.findAll();
					}
				}
			}
		}
		return diseases;
	}
	
	/**
	 * Stores the specified {@link Disease}. 
	 * @param disease the disease to store.
	 * @return disease that has been stored
	 * @throws OHServiceException if an error occurs storing the disease.
	 */
	public Disease newDisease(Disease disease) throws OHServiceException {
		return repository.save(disease);
	}

	/**
	 * Updates the specified {@link Disease}.
	 * @param disease the {@link Disease} to update.
	 * @return disease that has been updated
	 * @throws OHServiceException if an error occurs during the update.
	 */
	public Disease updateDisease(Disease disease) throws OHServiceException {
		return repository.save(disease);
	}

	/**
	 * Mark as deleted the specified {@link Disease}.
	 * @param disease the disease to make delete.
	 * @return <code>true</code> if the disease has been marked, <code>false</code> otherwise.
	 * @throws OHServiceException if an error occurred during the delete operation.
	 */
	public boolean deleteDisease(Disease disease) throws OHServiceException {
		disease.setOpdInclude(false);
		disease.setIpdInInclude(false);
		disease.setIpdOutInclude(false);
		repository.save(disease);
		return true;
	}

	/**
	 * Check if the specified code is used by other {@link Disease}s.
	 * @param code the code to check.
	 * @return <code>true</code> if it is already used, <code>false</code> otherwise.
	 * @throws OHServiceException if an error occurs during the check.
	 */
	public boolean isCodePresent(String code) throws OHServiceException {
		return repository.existsById(code);
	}

	/**
	 * Checks if the specified description is used by a disease with the specified type code.
	 * @param description the description to check.
	 * @param typeCode the disease type code.
	 * @return <code>true</code> if is used, <code>false</code> otherwise.
	 * @throws OHServiceException if an error occurs during the check.
	 */
	public boolean isDescriptionPresent(String description, String typeCode) throws OHServiceException {
		Disease foundDisease = repository.findOneByDescriptionAndTypeCode(description, typeCode);
		return (foundDisease != null && foundDisease.getDescription().compareTo(description) == 0);
	}
}
