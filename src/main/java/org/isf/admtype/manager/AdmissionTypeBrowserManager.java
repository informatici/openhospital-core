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
package org.isf.admtype.manager;

import java.util.ArrayList;
import java.util.List;

import org.isf.admtype.model.AdmissionType;
import org.isf.admtype.service.AdmissionTypeIoOperation;
import org.isf.generaldata.MessageBundle;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.tuple.JPAImmutableTriple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AdmissionTypeBrowserManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(AdmissionTypeBrowserManager.class);

	@Autowired
	private AdmissionTypeIoOperation ioOperations;

	/**
	 * Returns all the available {@link AdmissionType}s.
	 *
	 * @return a list of admission types or {@code null} if the operation fails.
	 * @throws OHServiceException
	 */
	public List<AdmissionType> getAdmissionType() throws OHServiceException {
		return ioOperations.getAdmissionType();
	}

	/**
	 * Stores a new {@link AdmissionType}.
	 *
	 * @param admissionType the admission type object to store.
	 * @return {@code JPAImmutableTriple}
	 */
	public JPAImmutableTriple newAdmissionType(AdmissionType admissionType) {
		List<String> errors = validateAdmissionType(admissionType, true);
		if (!errors.isEmpty()) {
			return new JPAImmutableTriple(false, null, errors);
		}
		try {
			AdmissionType newAdmissionType = ioOperations.newAdmissionType(admissionType);
			return new JPAImmutableTriple(true, newAdmissionType, null);
		} catch (OHServiceException ohServiceException) {
			errors.add(ohServiceException.getMessage());
			return new JPAImmutableTriple(false, null, errors);
		}
	}

	/**
	 * Updates the specified {@link AdmissionType}.
	 *
	 * @param admissionType the admission type to update.
	 * @return {@code JPAImmutableTriple}
	 */
	public JPAImmutableTriple updateAdmissionType(AdmissionType admissionType) {
		List<String> errors = validateAdmissionType(admissionType, false);
		if (!errors.isEmpty()) {
			return new JPAImmutableTriple(false, null, errors);
		}
		try {
			AdmissionType updatedAdmissionType = ioOperations.updateAdmissionType(admissionType);
			return new JPAImmutableTriple(true, updatedAdmissionType, null);
		} catch (OHServiceException ohServiceException) {
			errors.add(ohServiceException.getMessage());
			return new JPAImmutableTriple(false, null, errors);
		}
	}

	/**
	 * Checks if the specified Code is already used by others {@link AdmissionType}s.
	 *
	 * @param code the admission type code to check.
	 * @return {@code true} if the code is already used, {@code false} otherwise.
	 */
	public boolean isCodePresent(String code) {
		try {
			return ioOperations.isCodePresent(code);
		} catch (OHServiceException ohServiceException) {
			LOGGER.error(ohServiceException.getMessage(), ohServiceException);
			return false;
		}
	}

	/**
	 * Deletes the specified {@link AdmissionType}.
	 *
	 * @param admissionType the admission type to delete.
	 * @return {@code true} if the admission type has been deleted, {@code false} otherwise.
	 */
	public boolean deleteAdmissionType(AdmissionType admissionType) {
		try {
			ioOperations.deleteAdmissionType(admissionType);
			return true;
		} catch (OHServiceException ohServiceException) {
			LOGGER.error(ohServiceException.getMessage(), ohServiceException);
			return false;
		}
	}

	/**
	 * Verify if the object is valid for CRUD and return a list of errors, if any
	 *
	 * @param admissionType
	 * @param insert {@code true} or updated {@code false}
	 */
	protected List<String> validateAdmissionType(AdmissionType admissionType, boolean insert) {
		List<String> errors = new ArrayList<>();
		String key = admissionType.getCode();
		if (key.isEmpty()) {
			errors.add(MessageBundle.getMessage("angal.common.pleaseinsertacode.msg"));
		}
		if (key.length() > 10) {
			errors.add(MessageBundle.formatMessage("angal.common.thecodeistoolongmaxchars.fmt.msg", 10));
		}
		if (insert && isCodePresent(key)) {
			errors.add(MessageBundle.getMessage("angal.common.thecodeisalreadyinuse.msg"));
		}
		if (admissionType.getDescription().isEmpty()) {
			errors.add(MessageBundle.getMessage("angal.common.pleaseinsertavaliddescription.msg"));
		}
		return errors;
	}

}
