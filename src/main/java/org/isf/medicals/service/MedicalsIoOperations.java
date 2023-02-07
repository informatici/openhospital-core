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
package org.isf.medicals.service;

import java.util.List;

import org.isf.medicals.model.Medical;
import org.isf.medicalstock.service.MovementIoOperationRepository;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class offers the io operations for recovering and managing
 * medical records from the database
 * 
 * @author bob 
 * 		   modified by alex:
 * 			- column product code
 * 			- column pieces per packet
 * 11-dec-2005
 * 14-jan-2006
 */
@Service
@Transactional(rollbackFor=OHServiceException.class)
@TranslateOHServiceException
public class MedicalsIoOperations 
{
	@Autowired
	private MedicalsIoOperationRepository repository;
	@Autowired	
	private MovementIoOperationRepository moveRepository;
	
	/**
	 * Retrieves the specified {@link Medical}.
	 * @param code the medical code
	 * @return the stored medical.
	 * @throws OHServiceException if an error occurs retrieving the stored medical.
	 */
	public Medical getMedical(int code) throws OHServiceException {
		return repository.findById(code).orElse(null);
	}

	/**
	 * Gets all stored {@link Medical}s.
	 * @return all the stored medicals.
	 * @throws OHServiceException if an error occurs retrieving the stored medicals.
	 */
	public List<Medical> getMedicals() throws OHServiceException {
		return getMedicals(null, false);
	}

	/**
	 * Retrieves all stored {@link Medical}s.
	 * If a description value is provides the medicals are filtered.
	 * @param description the medical description.
	 * @return the stored medicals.
	 * @throws OHServiceException if an error occurs retrieving the stored medicals.
	 */
	public List<Medical> getMedicals(String description) throws OHServiceException {
		if (description != null) {
			return repository.findAllWhereDescriptionOrderByDescription(description);
		}
		return repository.findAllByOrderByDescription();
	}
	
	/**
	 * Retrieves all stored {@link Medical}s.
	 * If a description value is provided the medicals are filtered.
	 * @param type the medical type description.
	 * @param nameSorted if <code>true</code> return the list in alphabetical order, by code otherwise
	 * @return the stored medicals.
	 * @throws OHServiceException if an error occurs retrieving the stored medicals.
	 */
	public List<Medical> getMedicals(String type, boolean nameSorted) throws OHServiceException {
		if (type != null) {
			return getMedicalsByType(type, nameSorted);
		}
		return getMedicals(nameSorted);
	}

	/**
	 * Retrieves the stored {@link Medical}s based on the specified filter criteria.
	 * @param description the medical description or <code>null</code>
	 * @param type the medical type or <code>null</code>
	 * @param critical <code>true</code> if include only medicals under critical level.
	 * @return the retrieved medicals.
	 * @throws OHServiceException if an error occurs retrieving the medicals.
	 */
	public List<Medical> getMedicals(String description, String type, boolean critical) throws OHServiceException {
		List<Medical> medicals;

		if (description != null) {
			if (type != null) {
				if (critical) {
					medicals = repository.findAllWhereDescriptionAndTypeAndCriticalOrderByTypeAndDescription(description, type);
				} else {
					medicals = repository.findAllWhereDescriptionAndTypeOrderByTypeAndDescription(description, type);
				}
			} else {
				if (critical) {
					medicals = repository.findAllWhereDescriptionAndCriticalOrderByTypeAndDescription(description);
				} else {
					medicals = repository.findAllWhereDescriptionOrderByTypeAndDescription(description);
				}
			}
		} else {
			if (type != null) {
				if (critical) {
					medicals = repository.findAllWhereTypeAndCriticalOrderByTypeAndDescription(type);
				} else {
					medicals = repository.findAllWhereTypeOrderByTypeAndDescription(type);
				}
			} else {
				if (critical) {
					medicals = repository.findAllWhereCriticalOrderByTypeAndDescription();
				} else {
					medicals = repository.findAllByOrderByTypeAndDescription();
				}
			}
		}
		return medicals;
	}
	
	/**
	 * Checks if the specified {@link Medical} exists or not.
	 * @param medical - the medical to check.
	 * @param update - if <code>true</code> excludes the actual {@link Medical}
	 * @return all {@link Medical} with similar description
	 * @throws OHServiceException if an SQL error occurs during the check.
	 */
	public List<Medical> medicalCheck(Medical medical, boolean update) throws OHServiceException {
		if (update) {
			return repository.findAllWhereDescriptionSoundsLike(medical.getDescription(), medical.getCode());
		}
		return repository.findAllWhereDescriptionSoundsLike(medical.getDescription());
	}
	
	/**
	 * Checks if the specified {@link Medical} ProductCode exists or not.
	 * @param medical - the medical to check.
	 * @param update - if <code>true</code> excludes the actual {@link Medical}
	 * @return <code>true</code> if exists, <code>false</code> otherwise.
	 * @throws OHServiceException if an SQL error occurs during the check.
	 */
	public boolean productCodeExists(Medical medical, boolean update) throws OHServiceException {
		Medical foundMedical;
		if (update) {
			foundMedical = repository.findOneWhereProductCode(medical.getProdCode(), medical.getCode());
		} else {
			foundMedical = repository.findOneWhereProductCode(medical.getProdCode());
		}
		return foundMedical != null;
	}
    

	/**
	 * Checks if the specified {@link Medical} exists or not.
	 * @param medical the medical to check.
	 * @param update - if <code>true</code> exclude the current medical itself from search
	 * @return <code>true</code> if exists <code>false</code> otherwise.
	 * @throws OHServiceException if an error occurs during the check.
	 */
	public boolean medicalExists(Medical medical, boolean update) throws OHServiceException {
		Medical foundMedical;
		if (update) {
			foundMedical = repository.findOneWhereDescriptionAndType(medical.getDescription(), medical.getType().getCode(), medical.getCode());
		} else {
			foundMedical = repository.findOneWhereDescriptionAndType(medical.getDescription(), medical.getType().getCode());
		}
		return foundMedical != null;
	}
	
	/**
	 * Stores the specified {@link Medical}.
	 * @param medical the medical to store.
	 * @return <code>true</code> if the medical has been stored, <code>false</code> otherwise.
	 * @throws OHServiceException if an error occurs storing the medical.
	 */
	public Medical newMedical(Medical medical) throws OHServiceException {
		return repository.save(medical);
	}

	/**
	 * Updates the specified {@link Medical}.
	 * @param medical the medical to update.
	 * @return <code>true</code> if the medical has been updated <code>false</code> otherwise.
	 * @throws OHServiceException if an error occurs during the update.
	 */
	public Medical updateMedical(Medical medical) throws OHServiceException {
		return repository.save(medical);
	}

	/**
	 * Checks if the specified {@link Medical} is referenced in stock movement.
	 * @param code the medical code.
	 * @return <code>true</code> if the medical is referenced, <code>false</code> otherwise.
	 * @throws OHServiceException if an error occurs during the check.
	 */
	public boolean isMedicalReferencedInStockMovement(int code) throws OHServiceException {
		return moveRepository.findAllByMedicalCode(code) != null;
	}

	/**
	 * Deletes the specified {@link Medical}.
	 * @param medical the medical to delete.
	 * @return <code>true</code> if the medical has been deleted, <code>false</code> otherwise.
	 * @throws OHServiceException if an error occurs during the medical deletion.
	 */
	public boolean deleteMedical(Medical medical) throws OHServiceException {
		repository.delete(medical);
		return true;
	}

	/**
	 * Retrieves all stored medicals, sorted by description or smart code.
	 * @param nameSorted if true the found medicals are sorted by description, otherwise sorted by
	 *                      prod_code and description.
	 * @return sorted List of medicals or empty list if none found.
	 * @throws OHServiceException
	 */
	private List<Medical> getMedicals(boolean nameSorted) throws OHServiceException {
		if (nameSorted) {
			return getMedicals(null);
		}
		return repository.findAllOrderBySmartCodeAndDescription();
	}

	/**
	 * Retrieves all stored medicals by a given type, sorted by description or smart code.
	 * @param type the type the found medicals should have.
	 * @param nameSorted if true the found medicals are sorted by description, otherwise sorted by
	 *                      prod_code and description.
	 * @return sorted List of medicals or empty list if none found.
	 * @throws OHServiceException
	 */
	private List<Medical> getMedicalsByType(String type, boolean nameSorted) {
		if (nameSorted) {
			return repository.findAllWhereTypeOrderByDescription(type);
		}
		return repository.findAllWhereTypeOrderBySmartCodeAndDescription(type);
	}

}
