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
package org.isf.malnutrition.manager;

import java.util.ArrayList;
import java.util.List;

import org.isf.generaldata.MessageBundle;
import org.isf.malnutrition.model.Malnutrition;
import org.isf.malnutrition.service.MalnutritionIoOperation;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.springframework.stereotype.Component;

/**
 * Manager for malnutrition module.
 */
@Component
public class MalnutritionManager {

	private MalnutritionIoOperation ioOperation;

	public MalnutritionManager(MalnutritionIoOperation malnutritionIoOperation) {
		this.ioOperation = malnutritionIoOperation;
	}

	/**
	 * Verify if the object is valid for CRUD and return a list of errors, if any.
	 *
	 * @param malnutrition
	 * @throws OHDataValidationException
	 */
	protected void validateMalnutrition(Malnutrition malnutrition) throws OHDataValidationException {
		List<OHExceptionMessage> errors = new ArrayList<>();
		if (malnutrition.getDateSupp() == null) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.malnutrition.pleaseinsertavalidvisitdate.msg")));
		}
		if (malnutrition.getDateConf() == null) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.malnutrition.pleaseinsertavalidcontroldate.msg")));
		}
		if (malnutrition.getDateSupp() != null &&
				malnutrition.getDateConf() != null &&
				malnutrition.getDateConf().isBefore(malnutrition.getDateSupp())) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.malnutrition.controldatemustbeaftervisitdate.msg")));
		}
		if (malnutrition.getWeight() == 0) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.malnutrition.insertcorrectvalueinweightfield.msg")));
		}
		if (malnutrition.getHeight() == 0) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.malnutrition.insertcorrectvalueinheightfield.msg")));
		}
		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
	}

	/**
	 * Retrieves all the {@link Malnutrition} associated to the given admission id.
	 *
	 * @param admissionID the admission id to use as filter.
	 * @return all the retrieved malnutrition or {@code null} if the specified admission id is {@code null}.
	 * @throws OHServiceException
	 */
	public List<Malnutrition> getMalnutrition(String admissionID) throws OHServiceException {
		return ioOperation.getMalnutritions(admissionID);
	}

	/**
	 * Returns the last {@link Malnutrition} entry for specified patient ID
	 *
	 * @param patientID - the patient ID
	 * @return the last {@link Malnutrition} for specified patient ID. {@code null} if none.
	 * @throws OHServiceException
	 */
	public Malnutrition getLastMalnutrition(int patientID) throws OHServiceException {
		return ioOperation.getLastMalnutrition(patientID);
	}

	/**
	 * Stores a new {@link Malnutrition}. The malnutrition object is updated with the generated id.
	 *
	 * @param malnutrition the malnutrition to store.
	 * @return the newly stored new {@link Malnutrition} object.
	 * @throws OHServiceException
	 */
	public Malnutrition newMalnutrition(Malnutrition malnutrition) throws OHServiceException {
		validateMalnutrition(malnutrition);
		return ioOperation.newMalnutrition(malnutrition);
	}

	/**
	 * Update the specified {@link Malnutrition}.
	 *
	 * @param malnutrition the {@link Malnutrition} to update.
	 * @return the updated {@link Malnutrition} object.
	 * @throws OHServiceException
	 */
	public Malnutrition updateMalnutrition(Malnutrition malnutrition) throws OHServiceException {
		validateMalnutrition(malnutrition);
		return ioOperation.updateMalnutrition(malnutrition);
	}
	
	/**
	 * Get the specified {@link Malnutrition}.
	 *
	 * @param code of {@link Malnutrition}
	 * @return the {@link Malnutrition}
	 * @throws OHServiceException
	 */
	public Malnutrition getMalnutrition(int code) throws OHServiceException {
		return ioOperation.getMalnutrition(code);
	}

	/**
	 * Deletes the specified {@link Malnutrition}.
	 *
	 * @param malnutrition the malnutrition to delete.
	 * @throws OHServiceException
	 */
	public void deleteMalnutrition(Malnutrition malnutrition) throws OHServiceException {
		ioOperation.deleteMalnutrition(malnutrition);
	}
}
