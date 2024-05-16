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
package org.isf.vactype.manager;

import java.util.ArrayList;
import java.util.List;

import org.isf.generaldata.MessageBundle;
import org.isf.utils.exception.OHDataIntegrityViolationException;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.vactype.model.VaccineType;
import org.isf.vactype.service.VacTypeIoOperation;
import org.springframework.stereotype.Component;

@Component
public class VaccineTypeBrowserManager {

	private VacTypeIoOperation ioOperations;

	public VaccineTypeBrowserManager( VacTypeIoOperation vacTypeIoOperation) {
		this.ioOperations = vacTypeIoOperation;
	}

	/**
	 * Verify if the object is valid for CRUD and return a list of errors, if any.
	 *
	 * @param vaccineType
	 * @param insert {@code true} or updated {@code false}
	 * @throws OHServiceException
	 */
	protected void validateVaccineType(VaccineType vaccineType, boolean insert) throws OHServiceException {
		String key = vaccineType.getCode();
		String description = vaccineType.getDescription();
		List<OHExceptionMessage> errors = new ArrayList<>();
		if (key.isEmpty()) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.pleaseinsertacode.msg")));
		}
		if (key.length() > 1) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.thecodeistoolongmax1char.msg")));
		}
		if (description.isEmpty()) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.pleaseinsertavaliddescription.msg")));
		}
		if (insert && !key.isEmpty() && isCodePresent(key)) {
			throw new OHDataIntegrityViolationException(new OHExceptionMessage(MessageBundle.getMessage("angal.common.thecodeisalreadyinuse.msg")));
		}
		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
	}

	/**
	 * This method returns all {@link VaccineType}s from the DB.
	 *
	 * @return the list of {@link VaccineType}s
	 * @throws OHServiceException
	 */
	public List<VaccineType> getVaccineType() throws OHServiceException {
		return ioOperations.getVaccineType();
	}

	/**
	 * Inserts a new {@link VaccineType} into the DB.
	 *
	 * @param vaccineType - the {@link VaccineType} to insert
	 * @return the newly inserted {@link VaccineType} object.
	 * @throws OHServiceException
	 */
	public VaccineType newVaccineType(VaccineType vaccineType) throws OHServiceException {
		validateVaccineType(vaccineType, true);
		return ioOperations.newVaccineType(vaccineType);
	}

	/**
	 * Update a {@link VaccineType} in the DB.
	 *
	 * @param vaccineType - the item to update
	 * @return the updated {@link VaccineType} object.
	 * @throws OHServiceException
	 */
	public VaccineType updateVaccineType(VaccineType vaccineType) throws OHServiceException {
		validateVaccineType(vaccineType, false);
		return ioOperations.updateVaccineType(vaccineType);
	}

	/**
	 * Deletes a {@link VaccineType} in the DB.
	 *
	 * @param vaccineType - the item to delete
	 * @throws OHServiceException
	 */
	public void deleteVaccineType(VaccineType vaccineType) throws OHServiceException {
		ioOperations.deleteVaccineType(vaccineType);
	}

	/**
	 * Checks if the code is already in use.
	 *
	 * @param code - the {@link VaccineType} code
	 * @return {@code true} if the code is already in use, {@code false} otherwise
	 * @throws OHServiceException
	 */
	public boolean isCodePresent(String code) throws OHServiceException {
		return ioOperations.isCodePresent(code);
	}

	/**
	 * Returns the {@link VaccineType} based on vaccine type code.
	 *
	 * @param code - the  {@link VaccineType} code.
	 * @return the {@link VaccineType} or {@literal null} if none found
	 */
	public VaccineType findVaccineType(String code) {
		return ioOperations.findVaccineType(code);
	}
}
