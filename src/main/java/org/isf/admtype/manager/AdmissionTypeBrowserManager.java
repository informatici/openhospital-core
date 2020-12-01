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
package org.isf.admtype.manager;

import java.util.ArrayList;
import java.util.List;

import org.isf.admtype.model.AdmissionType;
import org.isf.admtype.service.AdmissionTypeIoOperation;
import org.isf.generaldata.MessageBundle;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AdmissionTypeBrowserManager {

	@Autowired
	private AdmissionTypeIoOperation ioOperations;

	/**
	 * Returns all the available {@link AdmissionType}s.
	 *
	 * @return a list of admission types or <code>null</code> if the operation fails.
	 * @throws OHServiceException
	 */
	public ArrayList<AdmissionType> getAdmissionType() throws OHServiceException {
		return ioOperations.getAdmissionType();
	}

	/**
	 * Stores a new {@link AdmissionType}.
	 *
	 * @param admissionType the admission type to store.
	 * @return <code>true</code> if the admission type has been stored, <code>false</code> otherwise.
	 * @throws OHServiceException
	 */
	public boolean newAdmissionType(AdmissionType admissionType) throws OHServiceException {
		validateAdmissionType(admissionType, true);
		return ioOperations.newAdmissionType(admissionType);
	}

	/**
	 * Updates the specified {@link AdmissionType}.
	 *
	 * @param admissionType the admission type to update.
	 * @return <code>true</code> if the admission type has been updated, <code>false</code> otherwise.
	 * @throws OHServiceException
	 */
	public boolean updateAdmissionType(AdmissionType admissionType) throws OHServiceException {
		validateAdmissionType(admissionType, false);
		return ioOperations.updateAdmissionType(admissionType);
	}

	/**
	 * Checks if the specified Code is already used by others {@link AdmissionType}s.
	 *
	 * @param code the admission type code to check.
	 * @return <code>true</code> if the code is already used, <code>false</code> otherwise.
	 * @throws OHServiceException
	 */
	public boolean codeControl(String code) throws OHServiceException {
		return ioOperations.isCodePresent(code);
	}

	/**
	 * Deletes the specified {@link AdmissionType}.
	 *
	 * @param admissionType the admission type to delete.
	 * @return <code>true</code> if the admission type has been deleted, <code>false</code> otherwise.
	 * @throws OHServiceException
	 */
	public boolean deleteAdmissionType(AdmissionType admissionType) throws OHServiceException {
		return ioOperations.deleteAdmissionType(admissionType);
	}

	/**
	 * Verify if the object is valid for CRUD and return a list of errors, if any
	 *
	 * @param admissionType
	 * @param insert <code>true</code> or updated <code>false</code>
	 * @throws OHDataValidationException
	 */
	protected void validateAdmissionType(AdmissionType admissionType, boolean insert) throws OHServiceException {
		List<OHExceptionMessage> errors = new ArrayList<>();
		String key = admissionType.getCode();
		if (key.equals("")) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.hospital"), MessageBundle.getMessage("angal.admtype.pleaseinsertacode"),
					OHSeverityLevel.ERROR));
		}
		if (key.length() > 10) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.hospital"), MessageBundle.getMessage("angal.admtype.codetoolongmaxchars"),
					OHSeverityLevel.ERROR));
		}

		if (insert) {
			if (codeControl(key)) {
				errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.hospital"), MessageBundle.getMessage("angal.common.codealreadyinuse"),
						OHSeverityLevel.ERROR));
			}
		}
		if (admissionType.getDescription().equals("")) {
			errors.add(
					new OHExceptionMessage(MessageBundle.getMessage("angal.hospital"), MessageBundle.getMessage("angal.admtype.pleaseinsertavaliddescription"),
							OHSeverityLevel.ERROR));
		}
		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
	}

}
