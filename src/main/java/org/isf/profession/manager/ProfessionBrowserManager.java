/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2025 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.profession.manager;

import java.util.ArrayList;
import java.util.List;

import org.isf.generaldata.MessageBundle;
import org.isf.profession.model.Profession;
import org.isf.profession.service.ProfessionIoOperation;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.springframework.stereotype.Component;

/**
 * Manager class for the Profession module.
 */
@Component
public class ProfessionBrowserManager {

	private final ProfessionIoOperation ioOperations;

	public ProfessionBrowserManager(ProfessionIoOperation professionIoOperation) {
		this.ioOperations = professionIoOperation;
	}

	/**
	 * Returns all the stored {@link Profession}s.
	 *
	 * @return a list of professions, {@code null} if the operation is failed.
	 * @throws OHServiceException
	 */
	public List<Profession> getProfessions() throws OHServiceException {
		return ioOperations.getProfessions();
	}

	/**
	 * Store the specified {@link Profession}.
	 *
	 * @param profession the profession to store.
	 * @return the newly stored {@link Profession} object.
	 * @throws OHServiceException
	 */
	public Profession newProfession(Profession profession) throws OHServiceException {
		validateProfession(profession, true);
		return ioOperations.newProfession(profession);
	}

	/**
	 * Updates the specified {@link Profession}.
	 *
	 * @param profession the profession to update.
	 * @return the updated {@link Profession} object.
	 * @throws OHServiceException
	 */
	public Profession updateProfession(Profession profession) throws OHServiceException {
		validateProfession(profession, false);
		return ioOperations.updateProfession(profession);
	}

	/**
	 * Checks if the specified code is already used by any {@link Profession}.
	 *
	 * @param code the code to check.
	 * @return {@code true} if the code is used, {@code false} otherwise.
	 * @throws OHServiceException
	 */
	public boolean isCodePresent(String code) throws OHServiceException {
		return ioOperations.isCodePresent(code);
	}

	/**
	 * Deletes the specified {@link Profession}.
	 *
	 * @param profession the profession to remove.
	 * @throws OHServiceException
	 */
	public void deleteProfession(Profession profession) throws OHServiceException {
		ioOperations.deleteProfession(profession);
	}

	/**
	 * Verify if the object is valid for CRUD and throw an exception with the list of errors, if any.
	 *
	 * @param profession
	 * @param insert {@code true} or updated {@code false}
	 * @throws OHServiceException
	 */
	protected void validateProfession(Profession profession, boolean insert) throws OHServiceException {
		List<OHExceptionMessage> errors = new ArrayList<>();
		String key = profession.getCode();
		if (key.isEmpty()) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.pleaseinsertacode.msg")));
		}
		if (key.length() > 50) {
			errors.add(new OHExceptionMessage(MessageBundle.formatMessage("angal.common.thecodeistoolongmaxchars.fmt.msg", 50)));
		}

		if (insert && isCodePresent(key)) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.thecodeisalreadyinuse.msg")));
		}
		if (profession.getDescription().isEmpty()) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.pleaseinsertavaliddescription.msg")));
		}
		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
	}

	/**
	 * Returns a {@link Profession} given the profession code.
	 *
	 * @param code
	 * @return object {@link Profession}, {@code null} otherwise.
	 * @throws OHServiceException
	 */
	public Profession getProfession(String code) throws OHServiceException {
		return ioOperations.getProfession(code);
	}

}
