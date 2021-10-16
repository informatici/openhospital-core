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
package org.isf.distype.manager;

import java.util.ArrayList;
import java.util.List;

import org.isf.distype.model.DiseaseType;
import org.isf.distype.service.DiseaseTypeIoOperation;
import org.isf.generaldata.MessageBundle;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Manager class for DisType module.
 */
@Component
public class DiseaseTypeBrowserManager {

	@Autowired
	private DiseaseTypeIoOperation ioOperations;

	/**
	 * Returns all the stored {@link DiseaseType}s.
	 *
	 * @return a list of disease type, <code>null</code> if the operation is failed.
	 * @throws OHServiceException
	 */
	public List<DiseaseType> getDiseaseType() throws OHServiceException {
		return ioOperations.getDiseaseTypes();
	}

	/**
	 * Store the specified {@link DiseaseType}.
	 *
	 * @param diseaseType the disease type to store.
	 * @return <code>true</code> if the {@link DiseaseType} has been stored, <code>false</code> otherwise.
	 * @throws OHServiceException
	 */
	public boolean newDiseaseType(DiseaseType diseaseType) throws OHServiceException {
		validateDiseaseType(diseaseType, true);
		return ioOperations.newDiseaseType(diseaseType);
	}

	/**
	 * Updates the specified {@link DiseaseType}.
	 *
	 * @param diseaseType the disease type to update.
	 * @return <code>true</code> if the disease type has been updated, false otherwise.
	 * @throws OHServiceException
	 */
	public boolean updateDiseaseType(DiseaseType diseaseType) throws OHServiceException {
		validateDiseaseType(diseaseType, false);
		return ioOperations.updateDiseaseType(diseaseType);
	}

	/**
	 * Checks if the specified code is already used by any {@link DiseaseType}.
	 *
	 * @param code the code to check.
	 * @return <code>true</code> if the code is used, false otherwise.
	 * @throws OHServiceException
	 */
	public boolean isCodePresent(String code) throws OHServiceException {
		return ioOperations.isCodePresent(code);
	}

	/**
	 * Deletes the specified {@link DiseaseType}.
	 *
	 * @param diseaseType the disease type to remove.
	 * @return <code>true</code> if the disease has been removed, <code>false</code> otherwise.
	 * @throws OHServiceException
	 */
	public boolean deleteDiseaseType(DiseaseType diseaseType) throws OHServiceException {
		return ioOperations.deleteDiseaseType(diseaseType);
	}

	/**
	 * Verify if the object is valid for CRUD and return a list of errors, if any
	 *
	 * @param diseaseType
	 * @param insert <code>true</code> or updated <code>false</code>
	 * @throws OHServiceException
	 */
	protected void validateDiseaseType(DiseaseType diseaseType, boolean insert) throws OHServiceException {
		List<OHExceptionMessage> errors = new ArrayList<>();
		String key = diseaseType.getCode();
		if (key.equals("")) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
					MessageBundle.getMessage("angal.common.pleaseinsertacode.msg"),
					OHSeverityLevel.ERROR));
		}
		if (key.length() > 2) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
					MessageBundle.formatMessage("angal.common.thecodeistoolongmaxchars.fmt.msg", 2),
					OHSeverityLevel.ERROR));
		}

		if (insert) {
			if (isCodePresent(key)) {
				errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
						MessageBundle.getMessage("angal.common.thecodeisalreadyinuse.msg"),
						OHSeverityLevel.ERROR));
			}
		}
		if (diseaseType.getDescription().equals("")) {
			errors.add(
					new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
							MessageBundle.getMessage("angal.common.pleaseinsertavaliddescription.msg"),
							OHSeverityLevel.ERROR));
		}
		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
	}

}
