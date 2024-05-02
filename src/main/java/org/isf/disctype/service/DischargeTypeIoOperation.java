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
package org.isf.disctype.service;

import java.util.List;

import org.isf.disctype.model.DischargeType;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor=OHServiceException.class)
@TranslateOHServiceException
public class DischargeTypeIoOperation {

	private DischargeTypeIoOperationRepository repository;

	public DischargeTypeIoOperation(DischargeTypeIoOperationRepository dischargeTypeIoOperationRepository) {
		this.repository = dischargeTypeIoOperationRepository;
	}

	/**
	 * Method that returns all {@link DischargeType}s in a list
	 * 
	 * @return the list of all DischargeTypes
	 * @throws OHServiceException
	 */
	public List<DischargeType> getDischargeType() throws OHServiceException {
		return repository.findAllByOrderByDescriptionAsc();
	}

	/**
	 * Method that updates an already existing {@link DischargeType}
	 * 
	 * @param dischargeType
	 * @return the persisted updated DischargeType object.
	 * @throws OHServiceException
	 */
	public DischargeType updateDischargeType(DischargeType dischargeType) throws OHServiceException {
		return repository.save(dischargeType);
	}

	/**
	 * Method that create a new {@link DischargeType}.
	 * 
	 * @param dischargeType
	 * @return the persisted new DischargeType object.
	 * @throws OHServiceException
	 */
	public DischargeType newDischargeType(DischargeType dischargeType) throws OHServiceException {
		return repository.save(dischargeType);
	}

	/**
	 * Method that deletes a {@link DischargeType}.
	 * 
	 * @param dischargeType
	 * @throws OHServiceException
	 */
	public void deleteDischargeType(DischargeType dischargeType) throws OHServiceException {
		repository.delete(dischargeType);
	}

	/**
	 * Method that checks if a {@link DischargeType} already exists.
	 * 
	 * @param code
	 * @return true - if the DischargeType already exists
	 * @throws OHServiceException 
	 */
	public boolean isCodePresent(String code) throws OHServiceException {
		return repository.existsById(code);
	}
}
