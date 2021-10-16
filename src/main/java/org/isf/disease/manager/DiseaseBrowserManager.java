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
package org.isf.disease.manager;

import java.util.ArrayList;
import java.util.List;

import org.isf.disease.model.Disease;
import org.isf.disease.service.DiseaseIoOperations;
import org.isf.generaldata.MessageBundle;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * ------------------------------------------
 * DiseaseBrowserManager - Class that provides gui separation from database operations and gives some
 * useful logic manipulations of the dinamic data (memory)
 * -----------------------------------------
 * modification history
 * 25/01/2006 - Rick, Vero, Pupo  - first beta version
 * 08/11/2006 - ross - added getDiseaseOpd members, and getDiseaseIpd to get only opd/ipd related diseases
 * ------------------------------------------
 */
@Component
public class DiseaseBrowserManager {

	@Autowired
	private DiseaseIoOperations ioOperations;

	/**
	 * Returns all the stored {@link Disease} with ODP flag <code>true</code>.
	 * In case of error a message error is shown and a <code>null</code> value is returned.
	 *
	 * @return the stored diseases with ODP flag true.
	 * @throws OHServiceException
	 */
	public List<Disease> getDiseaseOpd() throws OHServiceException {
		return ioOperations.getDiseases(null, true, false, false);
	}

	/**
	 * Returns all diseases, deleted ones also
	 * In case of error a message error is shown and a <code>null</code> value is returned.
	 *
	 * @return the stored diseases.
	 * @throws OHServiceException
	 */
	public List<Disease> getDiseaseAll() throws OHServiceException {
		return ioOperations.getDiseases(null, false, false, false);
	}

	/**
	 * Returns all the stored {@link Disease} with the specified typecode and flag ODP true.
	 * In case of error a message error is shown and a <code>null</code> value is returned.
	 *
	 * @param typecode the filter typecode.
	 * @return the retrieved diseases.
	 * @throws OHServiceException
	 */
	public List<Disease> getDiseaseOpd(String typecode) throws OHServiceException {
		return ioOperations.getDiseases(typecode, true, false, false);
	}

	/**
	 * Returns all the stored {@link Disease} with IPD_OUT flag <code>true</code>.
	 * In case of error a message error is shown and a <code>null</code> value is returned.
	 *
	 * @return the stored disease with IPD flag <code>true</code>.
	 * @throws OHServiceException
	 */
	public List<Disease> getDiseaseIpdOut() throws OHServiceException {
		return ioOperations.getDiseases(null, false, false, true);
	}

	/**
	 * Returns all the stored {@link Disease} with the specified typecode and the flag IPD_OUT <code>true</code>.
	 * In case of error a message error is shown and a <code>null</code> value is returned.
	 *
	 * @param typecode the filter typecode.
	 * @return the retrieved diseases.
	 * @throws OHServiceException
	 */
	public List<Disease> getDiseaseIpdOut(String typecode) throws OHServiceException {
		return ioOperations.getDiseases(typecode, false, false, true);
	}

	/**
	 * Returns all the stored {@link Disease} with IPD_IN flag <code>true</code>.
	 * In case of error a message error is shown and a <code>null</code> value is returned.
	 *
	 * @return the stored disease with IPD flag <code>true</code>.
	 * @throws OHServiceException
	 */
	public List<Disease> getDiseaseIpdIn() throws OHServiceException {
		return ioOperations.getDiseases(null, false, true, false);
	}

	/**
	 * Returns all the stored {@link Disease} with the specified typecode and the flag IPD_IN <code>true</code>.
	 * In case of error a message error is shown and a <code>null</code> value is returned.
	 *
	 * @param typecode the filter typecode.
	 * @return the retrieved diseases.
	 * @throws OHServiceException
	 */
	public List<Disease> getDiseaseIpdIn(String typecode) throws OHServiceException {
		return ioOperations.getDiseases(typecode, false, true, false);
	}

	/**
	 * Returns both OPD and IPDs diseases.
	 * In case of error a message error is shown and a <code>null</code> value is returned.
	 *
	 * @return the stored diseases.
	 * @throws OHServiceException
	 */
	public List<Disease> getDisease() throws OHServiceException {
		return ioOperations.getDiseases(null, true, true, true);
	}

	/**
	 * Retrieves all OPD and IPDs {@link Disease} with the specified typecode.
	 * In case of error a message error is shown and a <code>null</code> value is returned.
	 *
	 * @param typecode the filter typecode.
	 * @return all the diseases with the specified typecode.
	 * @throws OHServiceException
	 */
	public List<Disease> getDisease(String typecode) throws OHServiceException {
		return ioOperations.getDiseases(typecode, false, false, false);
	}

	/**
	 * Gets a {@link Disease} with the specified code.
	 * In case of error a message error is shown and a <code>null</code> value is returned.
	 *
	 * @param code the disease code.
	 * @return the found disease, <code>null</code> if no disease has found.
	 * @throws OHServiceException
	 */
	public Disease getDiseaseByCode(String code) throws OHServiceException {
		return ioOperations.getDiseaseByCode(code);
	}

	/**
	 * Stores the specified {@link Disease}.
	 * In case of error a message error is shown and a <code>false</code> value is returned.
	 *
	 * @param disease the disease to store.
	 * @return the disease that has been stored.
	 * @throws OHServiceException
	 */
	public Disease newDisease(Disease disease) throws OHServiceException {
		validateDisease(disease, true);
		return ioOperations.newDisease(disease);
	}

	/**
	 * Updates the specified {@link Disease}.
	 * If the disease has been updated concurrently a overwrite confirmation message is shown.
	 * In case of error a message error is shown and a <code>false</code> value is returned.
	 *
	 * @param disease the disease to update.
	 * @return the disease that has been updated.
	 * @throws OHServiceException
	 */
	public Disease updateDisease(Disease disease) throws OHServiceException {
		validateDisease(disease, false);
		return ioOperations.updateDisease(disease);
	}

	/**
	 * Mark as deleted the specified {@link Disease}.
	 * In case of error a message error is shown and a <code>false</code> value is returned.
	 *
	 * @param disease the disease to make delete.
	 * @return <code>true</code> if the disease has been marked, <code>false</code> otherwise.
	 * @throws OHServiceException
	 */
	public boolean deleteDisease(Disease disease) throws OHServiceException {
		return ioOperations.deleteDisease(disease);
	}

	/**
	 * Check if the specified code is used by other {@link Disease}s.
	 * In case of error a message error is shown and a <code>false</code> value is returned.
	 *
	 * @param code the code to check.
	 * @return <code>true</code> if it is already used, <code>false</code> otherwise.
	 * @throws OHServiceException
	 */
	public boolean isCodePresent(String code) throws OHServiceException {
		return ioOperations.isCodePresent(code);
	}

	/**
	 * Checks if the specified description is used by a disease with the specified type code.
	 * In case of error a message error is shown and a <code>false</code> value is returned.
	 *
	 * @param description the description to check.
	 * @param typeCode the disease type code.
	 * @return <code>true</code> if is used, <code>false</code> otherwise.
	 * @throws OHServiceException
	 */
	public boolean descriptionControl(String description, String typeCode) throws OHServiceException {
		return ioOperations.isDescriptionPresent(description, typeCode);
	}

	/**
	 * Verify if the object is valid for CRUD and return a list of errors, if any
	 *
	 * @param disease
	 * @param insert <code>true</code> or updated <code>false</code>
	 * @throws OHServiceException
	 */
	protected void validateDisease(Disease disease, boolean insert) throws OHServiceException {
		List<OHExceptionMessage> errors = new ArrayList<>();

		if (insert) {
			String key = disease.getCode();
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
			if (isCodePresent(key)) {
				errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
						MessageBundle.getMessage("angal.common.thecodeisalreadyinuse.msg"),
						OHSeverityLevel.ERROR));
			}
		}

		if (disease.getDescription().equals("")) {
			errors.add(
					new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
							MessageBundle.getMessage("angal.common.pleaseinsertavaliddescription.msg"),
							OHSeverityLevel.ERROR));
		}

		Disease oldDisease = null;
		if (!insert) {
			oldDisease = getDiseaseByCode(disease.getCode());
		}
		String lastDescription = oldDisease == null ? null : oldDisease.getDescription();
		// if inserting or description has changed on updating
		// avoid two disease with the same description for the same type
		if (lastDescription == null || !lastDescription.equals(disease.getDescription())) {
			if (descriptionControl(disease.getDescription(), disease.getType().getCode())) {
				errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
						MessageBundle.getMessage("angal.disease.thediseasisealreadypresent.msg"),
						OHSeverityLevel.ERROR));
			}
		}

		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
	}
}
