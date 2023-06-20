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
package org.isf.disctype.manager;

import java.util.ArrayList;
import java.util.List;

import org.isf.disctype.model.DischargeType;
import org.isf.disctype.service.DischargeTypeIoOperation;
import org.isf.generaldata.MessageBundle;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DischargeTypeBrowserManager {

	@Autowired
	private DischargeTypeIoOperation ioOperations;

	/**
	 * Method that returns all DischargeTypes in a list
	 *
	 * @return the list of all DischargeTypes (could be null)
	 * @throws OHServiceException
	 */
	public List<DischargeType> getDischargeType() throws OHServiceException {
		return ioOperations.getDischargeType();
	}

	/**
	 * Method that create a new DischargeType
	 *
	 * @param dischargeType
	 * @return true - if the new DischargeType has been inserted
	 * @throws OHServiceException
	 */
	public boolean newDischargeType(DischargeType dischargeType) throws OHServiceException {
		validateDischargeType(dischargeType, true);
		return ioOperations.newDischargeType(dischargeType);
	}

	/**
	 * Method that updates an already existing DischargeType
	 *
	 * @param dischargeType
	 * @return true - if the existing DischargeType has been updated
	 * @throws OHServiceException
	 */
	public boolean updateDischargeType(DischargeType dischargeType) throws OHServiceException {
		validateDischargeType(dischargeType, false);
		return ioOperations.newDischargeType(dischargeType);
	}

	/**
	 * Method that check if a DischargeType already exists
	 *
	 * @param code
	 * @return true - if the DischargeType already exists
	 * @throws OHServiceException
	 */
	public boolean isCodePresent(String code) throws OHServiceException {
		return ioOperations.isCodePresent(code);
	}

	/**
	 * Method that delete a DischargeType
	 *
	 * @param dischargeType
	 * @return true - if the DischargeType has been deleted
	 * @throws OHServiceException
	 */
	public boolean deleteDischargeType(DischargeType dischargeType) throws OHServiceException {
		validateDeleteDischargeType(dischargeType);
		return ioOperations.deleteDischargeType(dischargeType);
	}

	/**
	 * Verify if the object is valid for CRUD and return a list of errors, if any
	 *
	 * @param dischargeType
	 * @throws OHDataValidationException
	 */
	protected void validateDeleteDischargeType(DischargeType dischargeType) throws OHDataValidationException {
		List<OHExceptionMessage> errors = new ArrayList<>();
		if (dischargeType.getCode().equals("D")) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
					MessageBundle.getMessage("angal.disctype.youcannotdeletethisrecord.msg"),
					OHSeverityLevel.ERROR));
		}
		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
	}

	/**
	 * Verify if the object is valid for CRUD and return a list of errors, if any
	 *
	 * @param dischargeType
	 * @param insert <code>true</code> or updated <code>false</code>
	 * @throws OHServiceException
	 */
	protected void validateDischargeType(DischargeType dischargeType, boolean insert) throws OHServiceException {
		List<OHExceptionMessage> errors = new ArrayList<>();
		String key = dischargeType.getCode();
		if (key.equals("")) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
					MessageBundle.getMessage("angal.common.pleaseinsertacode.msg"),
					OHSeverityLevel.ERROR));
		}
		if (key.length() > 10) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
					MessageBundle.formatMessage("angal.common.thecodeistoolongmaxchars.fmt.msg", 10),
					OHSeverityLevel.ERROR));
		}

		if (insert) {
			if (isCodePresent(key)) {
				errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
						MessageBundle.getMessage("angal.common.thecodeisalreadyinuse.msg"),
						OHSeverityLevel.ERROR));
			}
		}
		if (dischargeType.getDescription().equals("")) {
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
