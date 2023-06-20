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
package org.isf.patvac.manager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.isf.generaldata.MessageBundle;
import org.isf.patvac.model.PatientVaccine;
import org.isf.patvac.service.PatVacIoOperations;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * ------------------------------------------
 * PatVacManager - patient-vaccine manager
 * -----------------------------------------
 * modification history
 * 25/08/2011 - claudia - first beta version
 * 14/11/2011 - claudia - inserted search condition on date
 * ------------------------------------------
 */
@Component
public class PatVacManager {

	@Autowired
	private PatVacIoOperations ioOperations;

	/**
	 * Returns all {@link PatientVaccine}s of today or one week ago
	 *
	 * @param minusOneWeek - if <code>true</code> return the last week
	 * @return the list of {@link PatientVaccine}s
	 * @throws OHServiceException
	 */
	public List<PatientVaccine> getPatientVaccine(boolean minusOneWeek) throws OHServiceException {
		return ioOperations.getPatientVaccine(minusOneWeek);
	}

	/**
	 * Returns all {@link PatientVaccine}s within <code>dateFrom</code> and
	 * <code>dateTo</code>
	 *
	 * @param vaccineTypeCode
	 * @param vaccineCode
	 * @param dateFrom
	 * @param dateTo
	 * @param sex
	 * @param ageFrom
	 * @param ageTo
	 * @return the list of {@link PatientVaccine}s
	 * @throws OHServiceException
	 */
	public List<PatientVaccine> getPatientVaccine(String vaccineTypeCode, String vaccineCode, LocalDateTime dateFrom, LocalDateTime dateTo, char sex,
			int ageFrom, int ageTo) throws OHServiceException {
		return ioOperations.getPatientVaccine(vaccineTypeCode, vaccineCode, dateFrom, dateTo, sex, ageFrom, ageTo);
	}

	/**
	 * Inserts a {@link PatientVaccine} in the DB
	 *
	 * @param patVac - the {@link PatientVaccine} to insert
	 * @return <code>true</code> if the item has been inserted, <code>false</code> otherwise
	 * @throws OHServiceException
	 */
	public PatientVaccine newPatientVaccine(PatientVaccine patVac) throws OHServiceException {
		validatePatientVaccine(patVac);
		return ioOperations.newPatientVaccine(patVac);
	}

	/**
	 * Updates a {@link PatientVaccine}
	 *
	 * @param patVac - the {@link PatientVaccine} to update
	 * @return <code>true</code> if the item has been updated, <code>false</code> otherwise
	 * @throws OHServiceException
	 */
	public PatientVaccine updatePatientVaccine(PatientVaccine patVac) throws OHServiceException {
		validatePatientVaccine(patVac);
		return ioOperations.updatePatientVaccine(patVac);
	}

	/**
	 * Deletes a {@link PatientVaccine}
	 *
	 * @param patVac - the {@link PatientVaccine} to delete
	 * @return <code>true</code> if the item has been deleted, <code>false</code> otherwise
	 * @throws OHServiceException
	 */
	public boolean deletePatientVaccine(PatientVaccine patVac) throws OHServiceException {
		return ioOperations.deletePatientVaccine(patVac);
	}

	/**
	 * Returns the max progressive number within specified year or within current year if <code>0</code>.
	 *
	 * @param year
	 * @return <code>int</code> - the progressive number in the year
	 * @throws OHServiceException
	 */
	public int getProgYear(int year) throws OHServiceException {
		return ioOperations.getProgYear(year);
	}
	
	public Optional<PatientVaccine> getPatientVaccine(int code) throws OHServiceException {
		return ioOperations.getPatientVaccine(code);
	}

	/**
	 * Verify if the object is valid for CRUD and return a list of errors, if any
	 *
	 * @param patientVaccine
	 * @throws OHDataValidationException
	 */
	protected void validatePatientVaccine(PatientVaccine patientVaccine) throws OHDataValidationException {
		List<OHExceptionMessage> errors = new ArrayList<>();

		if (patientVaccine.getVaccineDate() == null) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
					MessageBundle.getMessage("angal.patvac.pleaseinsertvaccinedate.msg"),
					OHSeverityLevel.ERROR));
		}
		if (patientVaccine.getProgr() < 0) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
					MessageBundle.getMessage("angal.patvac.pleaseinsertavalidprogressive.msg"),
					OHSeverityLevel.ERROR));
		}
		if (patientVaccine.getVaccine() == null || 
						patientVaccine.getVaccine().getDescription().equals(MessageBundle.getMessage("angal.patvac.allvaccine"))) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
					MessageBundle.getMessage("angal.patvac.pleaseselectavaccine.msg"),
					OHSeverityLevel.ERROR));
		}
		if (patientVaccine.getPatient() == null
				|| StringUtils.isEmpty(patientVaccine.getPatName())
				|| StringUtils.isEmpty(String.valueOf(patientVaccine.getPatSex()))) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
					MessageBundle.getMessage("angal.common.pleaseselectapatient.msg"),
					OHSeverityLevel.ERROR));
		}
		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
	}
}
