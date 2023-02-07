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
package org.isf.agetype.service;

import java.util.List;

import org.isf.agetype.model.AgeType;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Persistence class for agetype module.
 */
@Service
@Transactional(rollbackFor=OHServiceException.class)
@TranslateOHServiceException
public class AgeTypeIoOperations 
{
	@Autowired
	private AgeTypeIoOperationRepository repository;
	
	/**
	 * Returns all available age types.
	 * @return a list of {@link AgeType}.
	 * @throws OHServiceException if an error occurs retrieving the age types.
	 */
	public List<AgeType> getAgeType() throws OHServiceException {
		return repository.findAllByOrderByCodeAsc();
	}

	/**
	 * Updates the list of {@link AgeType}s.
	 * @param ageType the {@link AgeType} to update.
	 * @return <code>true</code> if the list has been updated, <code>false</code> otherwise.
	 * @throws OHServiceException if an error occurs during the update.
	 */
	public boolean updateAgeType(List<AgeType> ageType) throws OHServiceException {
		return repository.saveAll(ageType) != null;
	}

	/**
	 * Gets the {@link AgeType} from the code index.
	 * @param index the code index.
	 * @return the retrieved element, <code>null</code> otherwise.
	 * @throws OHServiceException if an error occurs retrieving the item.
	 */
	public AgeType getAgeTypeByCode(int index) throws OHServiceException {
		String code = "d" + (index - 1);
		return repository.findOneByCode(code);
	}
	
	/**
	 * Gets the {@link AgeType} from the code index.
	 * @param code of agetype.
	 * @return the retrieved element, <code>null</code> otherwise.
	 * @throws OHServiceException if an error occurs retrieving the item.
	 */
	public AgeType getAgeTypeByCode(String code) throws OHServiceException {

		return repository.findOneByCode(code);
	}
}
