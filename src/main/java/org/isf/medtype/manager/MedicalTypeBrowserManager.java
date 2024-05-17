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
package org.isf.medtype.manager;

import java.util.ArrayList;
import java.util.List;

import org.isf.generaldata.MessageBundle;
import org.isf.medtype.model.MedicalType;
import org.isf.medtype.service.MedicalTypeIoOperation;
import org.isf.utils.exception.OHDataIntegrityViolationException;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.springframework.stereotype.Component;

/**
 * Manager class for the medical type module.
 */
@Component
public class MedicalTypeBrowserManager {

	private MedicalTypeIoOperation ioOperations;

	public MedicalTypeBrowserManager(MedicalTypeIoOperation medicalTypeIoOperation) {
		this.ioOperations = medicalTypeIoOperation;
	}

	/**
	 * Verify if the object is valid for CRUD and return a list of errors, if any.
	 *
	 * @param medicalType
	 * @param insert {@code true} or updated {@code false}
	 * @throws OHServiceException
	 */
	protected void validateMedicalType(MedicalType medicalType, boolean insert) throws OHServiceException {
		String key = medicalType.getCode();
		String description = medicalType.getDescription();
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
		if (insert && isCodePresent(medicalType.getCode())) {
			throw new OHDataIntegrityViolationException(new OHExceptionMessage(MessageBundle.getMessage("angal.common.thecodeisalreadyinuse.msg")));
		}
		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
	}

	/**
	 * Retrieves all the {@link MedicalType}s.
	 *
	 * @return a list of all the {@link MedicalType}s.
	 * @throws OHServiceException
	 */
	public List<MedicalType> getMedicalType() throws OHServiceException {
		return ioOperations.getMedicalTypes();
	}

	/**
	 * Saves the specified {@link MedicalType}.
	 *
	 * @param medicalType the medical type to save.
	 * @return the newly saved {@link MedicalType} object.
	 * @throws OHServiceException
	 */
	public MedicalType newMedicalType(MedicalType medicalType) throws OHServiceException {
		validateMedicalType(medicalType, true);
		return ioOperations.newMedicalType(medicalType);
	}

	/**
	 * Updates the specified {@link MedicalType}.
	 *
	 * @param medicalType the medical type to update.
	 * @return the updated {@link MedicalType} object.
	 * @throws OHServiceException
	 */
	public MedicalType updateMedicalType(MedicalType medicalType) throws OHServiceException {
		validateMedicalType(medicalType, false);
		return ioOperations.updateMedicalType(medicalType);
	}

	/**
	 * Checks if the specified {@link MedicalType} code is already used.
	 *
	 * @param code the code to check.
	 * @return {@code true} if the code is used, {@code false} otherwise.
	 * @throws OHServiceException
	 */
	public boolean isCodePresent(String code) throws OHServiceException {
		return ioOperations.isCodePresent(code);
	}

	/**
	 * Deletes the specified {@link MedicalType} object.
	 *
	 * @param medicalType the medical type to delete.
	 * @throws OHServiceException
	 */
	public void deleteMedicalType(MedicalType medicalType) throws OHServiceException {
		ioOperations.deleteMedicalType(medicalType);
	}
}
