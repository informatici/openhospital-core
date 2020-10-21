/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2020 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.disctype.service;

import java.util.ArrayList;

import org.isf.disctype.model.DischargeType;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor=OHServiceException.class)
@TranslateOHServiceException
public class DischargeTypeIoOperation {

	@Autowired
	private DischargeTypeIoOperationRepository repository;
	
	/**
	 * method that returns all DischargeTypes in a list
	 * 
	 * @return the list of all DischargeTypes
	 * @throws OHServiceException
	 */
	public ArrayList<DischargeType> getDischargeType() throws OHServiceException 
	{
		return new ArrayList<DischargeType>(repository.findAllByOrderByDescriptionAsc());
	}

	/**
	 * method that updates an already existing DischargeType
	 * 
	 * @param dischargeType
	 * @return true - if the existing DischargeType has been updated
	 * @throws OHServiceException
	 */
	public boolean updateDischargeType(
			DischargeType dischargeType) throws OHServiceException 
	{
		boolean result = true;
	
		
		DischargeType savedDischargeType = repository.save(dischargeType);
		result = (savedDischargeType != null);
		
		return result;
	}

	/**
	 * method that create a new DischargeType
	 * 
	 * @param dischargeType
	 * @return true - if the new DischargeType has been inserted
	 * @throws OHServiceException
	 */
	public boolean newDischargeType(
			DischargeType dischargeType) throws OHServiceException 
	{
		boolean result = true;
	
		
		DischargeType savedDischargeType = repository.save(dischargeType);
		result = (savedDischargeType != null);
		
		return result;
	}

	/**
	 * method that delete a DischargeType
	 * 
	 * @param dischargeType
	 * @return true - if the DischargeType has been deleted
	 * @throws OHServiceException
	 */
	public boolean deleteDischargeType(
			DischargeType dischargeType) throws OHServiceException
	{
		boolean result = true;
	
		
		repository.delete(dischargeType);
		
		return result;
	}

	/**
	 * method that check if a DischargeType already exists
	 * 
	 * @param code
	 * @return true - if the DischargeType already exists
	 * @throws OHServiceException 
	 */
	public boolean isCodePresent(
			String code) throws OHServiceException 
	{
		return repository.existsById(code);
	}
}
