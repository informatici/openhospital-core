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
package org.isf.distype.manager;

import java.util.ArrayList;
import java.util.List;

import org.isf.distype.model.DiseaseType;
import org.isf.distype.service.DiseaseTypeIoOperation;
import org.isf.generaldata.MessageBundle;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.springframework.stereotype.Component;

/**
 * Manager class for DisType module.
 */
@Component
public class DiseaseTypeBrowserManager {

	private DiseaseTypeIoOperation ioOperations;

	public DiseaseTypeBrowserManager(DiseaseTypeIoOperation diseaseTypeIoOperation) {
		this.ioOperations = diseaseTypeIoOperation;
	}

	/**
	 * Returns all the stored {@link DiseaseType}s.
	 *
	 * @return a list of disease type, {@code null} if the operation is failed.
	 * @throws OHServiceException
	 */
	public List<DiseaseType> getDiseaseType() throws OHServiceException {
		return ioOperations.getDiseaseTypes();
	}

	/**
	 * Store the specified {@link DiseaseType}.
	 *
	 * @param diseaseType the disease type to store.
	 * @return the newly stored {@link DiseaseType} object.
	 * @throws OHServiceException
	 */
	public DiseaseType newDiseaseType(DiseaseType diseaseType) throws OHServiceException {
		validateDiseaseType(diseaseType, true);
		return ioOperations.newDiseaseType(diseaseType);
	}

	/**
	 * Updates the specified {@link DiseaseType}.
	 *
	 * @param diseaseType the disease type to update.
	 * @return the updated {@link DiseaseType} object.
	 * @throws OHServiceException
	 */
	public DiseaseType updateDiseaseType(DiseaseType diseaseType) throws OHServiceException {
		validateDiseaseType(diseaseType, false);
		return ioOperations.updateDiseaseType(diseaseType);
	}

	/**
	 * Checks if the specified code is already used by any {@link DiseaseType}.
	 *
	 * @param code the code to check.
	 * @return {@code true} if the code is used, {@code false} otherwise.
	 * @throws OHServiceException
	 */
	public boolean isCodePresent(String code) throws OHServiceException {
		return ioOperations.isCodePresent(code);
	}

	/**
	 * Deletes the specified {@link DiseaseType}.
	 *
	 * @param diseaseType the disease type to remove.
	 * @throws OHServiceException
	 */
	public void deleteDiseaseType(DiseaseType diseaseType) throws OHServiceException {
		ioOperations.deleteDiseaseType(diseaseType);
	}

	/**
	 * Verify if the object is valid for CRUD and return a list of errors, if any
	 *
	 * @param diseaseType
	 * @param insert {@code true} or updated {@code false}
	 * @throws OHServiceException
	 */
	protected void validateDiseaseType(DiseaseType diseaseType, boolean insert) throws OHServiceException {
		List<OHExceptionMessage> errors = new ArrayList<>();
		String key = diseaseType.getCode();
		if (key.isEmpty()) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.pleaseinsertacode.msg")));
		}
		if (key.length() > 2) {
			errors.add(new OHExceptionMessage(MessageBundle.formatMessage("angal.common.thecodeistoolongmaxchars.fmt.msg", 2)));
		}

		if (insert && isCodePresent(key)) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.thecodeisalreadyinuse.msg")));
		}
		if (diseaseType.getDescription().isEmpty()) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.pleaseinsertavaliddescription.msg")));
		}
		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
	}
	
	/**
	 * Returns a {@link DiseaseType} given the type code.
	 *
	 * @param code
	 * @return  object {@link DiseaseType}, {@code null} otherwise.
	 * @throws OHServiceException
	 */
	public DiseaseType getDiseaseType(String code) throws OHServiceException {
		return ioOperations.getDiseaseType(code);
	}

}
