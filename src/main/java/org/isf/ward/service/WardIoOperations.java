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
package org.isf.ward.service;

import java.util.ArrayList;
import java.util.List;

import org.isf.admission.model.Admission;
import org.isf.admission.service.AdmissionIoOperationRepository;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.isf.ward.model.Ward;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class offers the io operations for recovering and managing
 * ward records from the database
 * 
 * @author Rick
 */
@Service
@Transactional(rollbackFor=OHServiceException.class)
@TranslateOHServiceException
public class WardIoOperations {

	@Autowired
	private WardIoOperationRepository repository;
	@Autowired
	private AdmissionIoOperationRepository admissionRepository;
	
	/**
	 * Retrieves the number of patients currently admitted in the {@link Ward}
	 * @param ward - the ward
	 * @return the number of patients currently admitted
	 * @throws OHServiceException
	 */
	public int getCurrentOccupation(Ward ward) throws OHServiceException {
		List<Admission> admissions = new ArrayList<>(admissionRepository.findAllWhereWard(ward.getCode()));
		return admissions.size();
	}
	
	/**
	 * Retrieves all stored {@link Ward}s with flag maternity equals <code>false</code>.
	 * @return the retrieved wards.
	 * @throws OHServiceException if an error occurs retrieving the diseases.
	 */
	public List<Ward> getWardsNoMaternity() throws OHServiceException {
		return new ArrayList<>(repository.findByCodeNot("M"));
	}
	
	/**
	 * Retrieves all stored {@link Ward}s with the specified ward ID.
	 * @param wardID - the ward ID, can be <code>null</code>
	 * @return the retrieved wards.
	 * @throws OHServiceException if an error occurs retrieving the wards.
	 */
	public List<Ward> getWards(String wardID) throws OHServiceException {
		List<Ward> wards = null;

		if (wardID != null && wardID.trim().length() > 0) {
			wards = new ArrayList<>(repository.findByCodeContains(wardID));
		} else {
			wards = new ArrayList<>(repository.findAll());
		}

		return wards;
	}
	
	/**
	 * Stores the specified {@link Ward}. 
	 * @param ward the ward to store.
	 * @return ward that has been stored.
	 * @throws OHServiceException if an error occurs storing the ward.
	 */
	public Ward newWard(Ward ward) throws OHServiceException {
		return repository.save(ward);
	}
	
	/**
	 * Updates the specified {@link Ward}.
	 * @param ward the {@link Ward} to update.
	 * @return ward that has been updated.
	 * @throws OHServiceException if an error occurs during the update.
	 */
	public Ward updateWard(Ward ward) throws OHServiceException {
		return repository.save(ward);
	}
	
	/**
	 * Mark as deleted the specified {@link Ward}.
	 * @param ward the ward to make delete.
	 * @return <code>true</code> if the ward has been marked, <code>false</code> otherwise.
	 * @throws OHServiceException if an error occurred during the delete operation.
	 */
	public boolean deleteWard(Ward ward) throws OHServiceException {
		repository.delete(ward);
		return true;
	}
	
	/**
	 * Check if the specified code is used by other {@link Ward}s.
	 * @param code the code to check.
	 * @return <code>true</code> if it is already used, <code>false</code> otherwise.
	 * @throws OHServiceException if an error occurs during the check.
	 */
	public boolean isCodePresent(String code) throws OHServiceException {
		return repository.exists(code);
	}
	
	
	/**
	 * Check if the maternity ward exists
	 * @return <code>true</code> if is exist, <code>false</code> otherwise.
	 * @throws OHServiceException if an error occurs during the check.
	 */
	public boolean isMaternityPresent() throws OHServiceException {
		return isCodePresent("M");
	}

	/**
	 * Returns the {@link Ward} based on code
	 *
	 * @param code - the code, must not be {@literal null}
	 * @return the {@link Ward} or {@literal null} if none found
	 * @throws OHServiceException
	 * @throws IllegalArgumentException if {@code code} is {@literal null}
	 */
	public Ward findWard(String code) throws OHServiceException
	{
		if (code != null) {
			return repository.findOne(code);
		}else
			throw new IllegalArgumentException("code must not be null");
	}
}
