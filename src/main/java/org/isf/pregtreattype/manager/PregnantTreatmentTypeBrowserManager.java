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
package org.isf.pregtreattype.manager;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.isf.generaldata.MessageBundle;
import org.isf.pregtreattype.model.PregnantTreatmentType;
import org.isf.pregtreattype.service.PregnantTreatmentTypeIoOperation;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.springframework.stereotype.Component;

@Component
public class PregnantTreatmentTypeBrowserManager {

	private PregnantTreatmentTypeIoOperation ioOperations;

	public PregnantTreatmentTypeBrowserManager(PregnantTreatmentTypeIoOperation pregnantTreatmentTypeIoOperation) {
		this.ioOperations = pregnantTreatmentTypeIoOperation;
	}

	/**
	 * Return the list of {@link PregnantTreatmentType}s.
	 *
	 * @return the list of {@link PregnantTreatmentType}s
	 * @throws OHServiceException
	 */
	public List<PregnantTreatmentType> getPregnantTreatmentType() throws OHServiceException {
		return ioOperations.getPregnantTreatmentType();
	}

	/**
	 * Insert a {@link PregnantTreatmentType} into the DB.
	 *
	 * @param pregnantTreatmentType - the {@link PregnantTreatmentType} to insert
	 * @return the newly inserted {@link PregnantTreatmentType} object.
	 * @throws OHServiceException
	 */
	public PregnantTreatmentType newPregnantTreatmentType(PregnantTreatmentType pregnantTreatmentType) throws OHServiceException {
		validatePregnantTreatmentType(pregnantTreatmentType, true);
		return ioOperations.newPregnantTreatmentType(pregnantTreatmentType);
	}

	/**
	 * Update a {@link PregnantTreatmentType} in the DB
	 *
	 * @param pregnantTreatmentType - the {@link PregnantTreatmentType} to update
	 * @return the updated {@link PregnantTreatmentType} object.
	 * @throws OHServiceException
	 */
	public PregnantTreatmentType updatePregnantTreatmentType(PregnantTreatmentType pregnantTreatmentType) throws OHServiceException {
		validatePregnantTreatmentType(pregnantTreatmentType, false);
		return ioOperations.updatePregnantTreatmentType(pregnantTreatmentType);
	}

	/**
	 * Delete a {@link PregnantTreatmentType} in the DB.
	 *
	 * @param pregnantTreatmentType - the {@link PregnantTreatmentType} to delete
	 * @throws OHServiceException
	 */
	public void deletePregnantTreatmentType(PregnantTreatmentType pregnantTreatmentType) throws OHServiceException {
		ioOperations.deletePregnantTreatmentType(pregnantTreatmentType);
	}

	/**
	 * Check if the code is already in use
	 *
	 * @param code - the code
	 * @return {@code true} if the code is already in use, {@code false} otherwise
	 * @throws OHServiceException
	 */
	public boolean isCodePresent(String code) throws OHServiceException {
		return ioOperations.isCodePresent(code);
	}

	/**
	 * Verify if the object is valid for CRUD and return a list of errors, if any.
	 *
	 * @param pregnantTreatmentType
	 * @param insert {@code true} or updated {@code false}
	 * @throws OHDataValidationException
	 */
	protected void validatePregnantTreatmentType(PregnantTreatmentType pregnantTreatmentType, boolean insert) throws OHServiceException {
		List<OHExceptionMessage> errors = new ArrayList<>();
		String key = pregnantTreatmentType.getCode();
		if (StringUtils.isEmpty(key)) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.pleaseinsertacode.msg")));
		}
		if (key.length() > 10) {
			errors.add(new OHExceptionMessage(MessageBundle.formatMessage("angal.common.thecodeistoolongmaxchars.fmt.msg", 10)));
		}
		if (insert && isCodePresent(key)) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.thecodeisalreadyinuse.msg")));
		}
		if (StringUtils.isEmpty(pregnantTreatmentType.getDescription())) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.pleaseinsertavaliddescription.msg")));
		}
		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
	}
}
