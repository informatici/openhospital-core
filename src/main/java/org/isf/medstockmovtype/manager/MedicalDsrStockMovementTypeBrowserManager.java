/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2024 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.medstockmovtype.manager;

import java.util.ArrayList;
import java.util.List;

import org.isf.generaldata.MessageBundle;
import org.isf.medstockmovtype.model.MovementType;
import org.isf.medstockmovtype.service.MedicalDsrStockMovementTypeIoOperation;
import org.isf.utils.exception.OHDataIntegrityViolationException;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.springframework.stereotype.Component;

/**
 * Manager class for the medical stock movement type.
 */
@Component
public class MedicalDsrStockMovementTypeBrowserManager {

	private MedicalDsrStockMovementTypeIoOperation ioOperations;

	public MedicalDsrStockMovementTypeBrowserManager(MedicalDsrStockMovementTypeIoOperation medicalDsrStockMovementTypeIoOperation) {
		this.ioOperations = medicalDsrStockMovementTypeIoOperation;
	}

	/**
	 * Verify if the object is valid for CRUD and return a list of errors, if any
	 *
	 * @param movementType
	 * @param insert
	 *            {@code true} or updated {@code false}
	 * @throws OHServiceException
	 */
	protected void validateMovementType(MovementType movementType, boolean insert) throws OHServiceException {
		String key = movementType.getCode();
		String key2 = movementType.getType();
		String description = movementType.getDescription();
		List<OHExceptionMessage> errors = new ArrayList<>();
		if (key.isEmpty()) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.pleaseinsertacode.msg")));
		}
		if (key.length() > 10) {
			errors.add(new OHExceptionMessage(MessageBundle.formatMessage("angal.common.thecodeistoolongmaxchars.fmt.msg", 10)));
		}
		if (key2.length() > 2) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.medstockmovtype.thetypeistoolongmax2chars.msg")));
		}
		if (description.isEmpty()) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.pleaseinsertavaliddescription.msg")));
		}
		if (insert && isCodePresent(key)) {
			throw new OHDataIntegrityViolationException(new OHExceptionMessage(MessageBundle.getMessage("angal.common.thecodeisalreadyinuse.msg")));
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
	public List<MovementType> getMedicalDsrStockMovementType() throws OHServiceException {
		return ioOperations.getMedicalDsrStockMovementType();
	}

	/**
	 * Save the specified {@link MovementType}.
	 *
	 * @param medicalDsrStockMovementType
	 *            the medical stock movement type to save.
	 * @return {@code true} if the medical stock movement type has been saved, {@code false} otherwise.
	 * @throws OHServiceException
	 */
	public MovementType newMedicalDsrStockMovementType(MovementType medicalDsrStockMovementType) throws OHServiceException {
		validateMovementType(medicalDsrStockMovementType, true);
		return ioOperations.newMedicalDsrStockMovementType(medicalDsrStockMovementType);
	}

	/**
	 * Updates the specified {@link MovementType}.
	 *
	 * @param medicalDsrStockMovementType
	 *            the medical stock movement type to update.
	 * @return {@code true} if the medical stock movement type has been updated, {@code false} otherwise.
	 * @throws OHServiceException
	 */
	public MovementType updateMedicalDsrStockMovementType(MovementType medicalDsrStockMovementType) throws OHServiceException {
		validateMovementType(medicalDsrStockMovementType, false);
		return ioOperations.updateMedicalDsrStockMovementType(medicalDsrStockMovementType);
	}

	/**
	 * Checks if the specified {@link MovementType} code is already used.
	 *
	 * @param code
	 *            the code to check.
	 * @return {@code true} if the code is used, {@code false} otherwise.
	 * @throws OHServiceException
	 */
	public boolean isCodePresent(String code) throws OHServiceException {
		return ioOperations.isCodePresent(code);
	}

	/**
	 * Deletes the specified {@link MovementType}.
	 *
	 * @param medicalDsrStockMovementType
	 *            the medical stock movement type to delete.
	 * @throws OHServiceException
	 */
	public void deleteMedicalDsrStockMovementType(MovementType medicalDsrStockMovementType) throws OHServiceException {
		ioOperations.deleteMedicalDsrStockMovementType(medicalDsrStockMovementType);
	}

	/**
	 * Get the {@link MovementType} code. In case of error a message error is shown and a {@code false} value is returned.
	 *
	 * @param code
	 *            the code to check.
	 * @return {@code true} if the code is used, {@code false} otherwise.
	 */
	public MovementType getMovementType(String code) {
		return ioOperations.findOneByCode(code);
	}
}
