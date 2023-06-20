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
package org.isf.medstockmovtype.manager;

import java.util.ArrayList;
import java.util.List;

import org.isf.generaldata.MessageBundle;
import org.isf.medstockmovtype.model.MovementType;
import org.isf.medstockmovtype.service.MedicalStockMovementTypeIoOperation;
import org.isf.utils.exception.OHDataIntegrityViolationException;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Manager class for the medical stock movement type.
 */
@Component
public class MedicaldsrstockmovTypeBrowserManager {

	@Autowired
	private MedicalStockMovementTypeIoOperation ioOperations;

	/**
	 * Verify if the object is valid for CRUD and return a list of errors, if any
	 *
	 * @param movementType
	 * @param insert <code>true</code> or updated <code>false</code>
	 * @throws OHServiceException
	 */
	protected void validateMovementType(MovementType movementType, boolean insert) throws OHServiceException {
		String key = movementType.getCode();
		String key2 = movementType.getType();
		String description = movementType.getDescription();
		List<OHExceptionMessage> errors = new ArrayList<>();
		if (key.isEmpty()) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
					MessageBundle.getMessage("angal.common.pleaseinsertacode.msg"),
					OHSeverityLevel.ERROR));
		}
		if (key.length() > 10) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
					MessageBundle.formatMessage("angal.common.thecodeistoolongmaxchars.fmt.msg", 10),
					OHSeverityLevel.ERROR));
		}
		if (key2.length() > 2) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
					MessageBundle.getMessage("angal.medstockmovtype.thetypeistoolongmax2chars.msg"),
					OHSeverityLevel.ERROR));
		}
		if (description.isEmpty()) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
					MessageBundle.getMessage("angal.common.pleaseinsertavaliddescription.msg"),
					OHSeverityLevel.ERROR));
		}
		if (insert) {
			if (isCodePresent(key)) {
				throw new OHDataIntegrityViolationException(new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
						MessageBundle.getMessage("angal.common.thecodeisalreadyinuse.msg"),
						OHSeverityLevel.ERROR));
			}
		}
		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
	}

	/**
	 * Returns all the medical stock movement types.
	 *
	 * @return all the medical stock movement types.
	 * @throws OHServiceException
	 */
	public List<MovementType> getMedicaldsrstockmovType() throws OHServiceException {
		return ioOperations.getMedicaldsrstockmovType();
	}

	/**
	 * Save the specified {@link MovementType}.
	 *
	 * @param medicaldsrstockmovType the medical stock movement type to save.
	 * @return <code>true</code> if the medical stock movement type has been saved, <code>false</code> otherwise.
	 * @throws OHServiceException
	 */
	public MovementType newMedicaldsrstockmovType(MovementType medicaldsrstockmovType) throws OHServiceException {
		validateMovementType(medicaldsrstockmovType, true);
		return ioOperations.newMedicaldsrstockmovType(medicaldsrstockmovType);
	}

	/**
	 * Updates the specified {@link MovementType}.
	 *
	 * @param medicaldsrstockmovType the medical stock movement type to update.
	 * @return <code>true</code> if the medical stock movement type has been updated, <code>false</code> otherwise.
	 * @throws OHServiceException
	 */
	public MovementType updateMedicaldsrstockmovType(MovementType medicaldsrstockmovType) throws OHServiceException {
		validateMovementType(medicaldsrstockmovType, false);
		return ioOperations.updateMedicaldsrstockmovType(medicaldsrstockmovType);
	}

	/**
	 * Checks if the specified {@link MovementType} code is already used.
	 *
	 * @param code the code to check.
	 * @return <code>true</code> if the code is used, <code>false</code> otherwise.
	 * @throws OHServiceException
	 */
	public boolean isCodePresent(String code) throws OHServiceException {
		return ioOperations.isCodePresent(code);
	}

	/**
	 * Deletes the specified {@link MovementType}.
	 *
	 * @param medicaldsrstockmovType the medical stock movement type to delete.
	 * @return <code>true</code> if the medical stock movement type has been deleted, <code>false</code> otherwise.
	 * @throws OHServiceException
	 */
	public boolean deleteMedicaldsrstockmovType(MovementType medicaldsrstockmovType) throws OHServiceException {
		return ioOperations.deleteMedicaldsrstockmovType(medicaldsrstockmovType);
	}

	/**
	 * Get the  {@link MovementType} code.
	 * In case of error a message error is shown and a <code>false</code> value is returned.
	 *
	 * @param code the code to check.
	 * @return <code>true</code> if the code is used, <code>false</code> otherwise.
	 */
	public MovementType getMovementType(String code) {
		return ioOperations.findOneByCode(code);
	}
}
