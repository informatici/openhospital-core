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
package org.isf.opd.manager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.isf.disease.model.Disease;
import org.isf.generaldata.GeneralData;
import org.isf.generaldata.MessageBundle;
import org.isf.menu.manager.UserBrowsingManager;
import org.isf.opd.model.Opd;
import org.isf.opd.service.OpdIoOperations;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.isf.ward.model.Ward;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Vero
 */
@Component
public class OpdBrowserManager {

	@Autowired
	private OpdIoOperations ioOperations;

	protected void setPatientConsistency(Opd opd) {
		if (GeneralData.OPDEXTENDED && opd.getPatient() != null) {
			/*
			 * Age and Sex has not to be updated for reporting purposes
			 */
			opd.setAge(opd.getPatient().getAge());
			opd.setSex(opd.getPatient().getSex());
		}
	}

	/**
	 * Verify if the object is valid for CRUD and return a list of errors, if any
	 *
	 * @param opd
	 * @param insert <code>true</code> or updated <code>false</code>
	 * @throws OHDataValidationException
	 */
	protected void validateOpd(Opd opd, boolean insert) throws OHDataValidationException {

		Disease disease = opd.getDisease();
		Disease disease2 = opd.getDisease2();
		Disease disease3 = opd.getDisease3();
		Ward ward = opd.getWard();
		if (opd.getUserID() == null) {
			opd.setUserID(UserBrowsingManager.getCurrentUser());
		}
		List<OHExceptionMessage> errors = new ArrayList<>();
		// Check Visit Date
		if (opd.getDate() == null) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
					MessageBundle.getMessage("angal.opd.pleaseinsertattendancedate.msg"),
					OHSeverityLevel.ERROR));
		}
		// Check Patient
		if (GeneralData.OPDEXTENDED && opd.getPatient() == null) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
					MessageBundle.getMessage("angal.common.pleaseselectapatient.msg"),
					OHSeverityLevel.ERROR));
		}
		// Check Patient
		if (ward == null) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
					MessageBundle.getMessage("angal.common.pleaseselectaward.msg"),
					OHSeverityLevel.ERROR));
		} else {
			if (!ward.isOpd()) {
				errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
					MessageBundle.getMessage("angal.opd.specifiedwardisnotenabledforopdservice.msg"),
					OHSeverityLevel.ERROR));
			}
		}
		// Check Sex and Age
		if (opd.getAge() < 0) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
					MessageBundle.getMessage("angal.opd.pleaseinsertthepatientsage.msg"),
					OHSeverityLevel.ERROR));
		}
		if (opd.getSex() == ' ') {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
					MessageBundle.getMessage("angal.opd.pleaseselectpatientssex.msg"),
					OHSeverityLevel.ERROR));
		}
		// Check Disease n.1
		if (disease == null) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
					MessageBundle.getMessage("angal.opd.pleaseselectadisease.msg"),
					OHSeverityLevel.ERROR));
		} else {
			// Check double diseases
			if (disease2 != null && disease.getCode().equals(disease2.getCode())) {
				errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
						MessageBundle.getMessage("angal.opd.specifyingduplicatediseasesisnotallowed.msg"),
						OHSeverityLevel.ERROR));
			}
			if (disease3 != null && disease.getCode().equals(disease3.getCode())) {
				errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
						MessageBundle.getMessage("angal.opd.specifyingduplicatediseasesisnotallowed.msg"),
						OHSeverityLevel.ERROR));
			}
			if (disease2 != null && disease3 != null && disease2.getCode().equals(disease3.getCode())) {
				errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
						MessageBundle.getMessage("angal.opd.specifyingduplicatediseasesisnotallowed.msg"),
						OHSeverityLevel.ERROR));
			}
		}
		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
	}

	/**
	 * Return all Opds of today or since one week ago
	 *
	 * @param oneWeek - if <code>true</code> return the last week, only today otherwise.
	 * @return the list of Opds. It could be <code>null</code>.
	 * @throws OHServiceException
	 */
	public List<Opd> getOpd(boolean oneWeek) throws OHServiceException {
		return ioOperations.getOpdList(oneWeek);
	}
	
	/**
	 * Return all Opds within specified dates and parameters
	 * 
	 * @param ward 
	 * @param diseaseTypeCode
	 * @param diseaseCode
	 * @param dateFrom
	 * @param dateTo
	 * @param ageFrom
	 * @param ageTo
	 * @param sex
	 * @param newPatient
	 * @param user
	 * @return the list of Opds. It could be <code>null</code>.
	 * @throws OHServiceException
	 */
	public List<Opd> getOpd(Ward ward, String diseaseTypeCode, String diseaseCode, LocalDate dateFrom, LocalDate dateTo, int ageFrom, int ageTo, char sex, char newPatient, String user)
			throws OHServiceException {
		return ioOperations.getOpdList(ward, diseaseTypeCode, diseaseCode, dateFrom, dateTo, ageFrom, ageTo, sex, newPatient, user);
	}
	

	/**
	 * Returns all {@link Opd}s associated to specified patient ID
	 *
	 * @param patientcode - the patient ID
	 * @return the list of {@link Opd}s associated to specified patient ID.
	 * the whole list of {@link Opd}s if <code>0</code> is passed.
	 * @throws OHServiceException
	 */
	public List<Opd> getOpdList(int patientcode) throws OHServiceException {
		return ioOperations.getOpdList(patientcode);
	}

	/**
	 * Insert a new item in the db
	 *
	 * @param opd an {@link Opd}
	 * @return <code>true</code> if the item has been inserted
	 * @throws OHServiceException
	 */
	public Opd newOpd(Opd opd) throws OHServiceException {
		setPatientConsistency(opd);
		validateOpd(opd, true);
		return ioOperations.newOpd(opd);
	}

	/**
	 * Updates the specified {@link Opd} object.
	 *
	 * @param opd - the {@link Opd} object to update.
	 * @return the updated {@link Opd}
	 * @throws OHServiceException
	 */
	public Opd updateOpd(Opd opd) throws OHServiceException {
		validateOpd(opd, false);
		return ioOperations.updateOpd(opd);
	}

	/**
	 * Delete an {@link Opd} from the db
	 *
	 * @param opd - the {@link Opd} to delete
	 * @return <code>true</code> if the item has been deleted. <code>false</code> otherwise.
	 * @throws OHServiceException
	 */
	public boolean deleteOpd(Opd opd) throws OHServiceException {
		return ioOperations.deleteOpd(opd);
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

	/**
	 * Return the last {@link Opd} in time associated with specified patient ID.
	 *
	 * @param patientcode - the patient ID
	 * @return last Opd associated with specified patient ID or <code>null</code>
	 * @throws OHServiceException
	 */
	public Opd getLastOpd(int patientcode) throws OHServiceException {
		return ioOperations.getLastOpd(patientcode);
	}

	/**
	 * Check if the given {@code opdNum} does already exist for the give {@code year}
	 *
	 * @param opdNum - the OPD progressive in year
	 * @param year - the year
	 * @return <code>true</code> if the given number exists in year, <code>false</code> otherwise
	 */
	public Boolean isExistOpdNum(int opdNum, int year) throws OHServiceException {
		return ioOperations.isExistOpdNum(opdNum, year);
	}
	
	/**
	 * Get an OPD by its code
	 * @param code - the OPD code
	 * @return an OPD or {@code null}
	 */
	public Optional<Opd> getOpdById(int code) {
		return ioOperations.getOpdById(code);
	}

	/**
	 * Get a list of OPD with specified Progressive in Year number
	 * @param code - the OPD code
	 * @return a list of OPD or an empty list
	 */
	public List<Opd> getOpdByProgYear(int code) {
		return ioOperations.getOpdByProgYear(code);
	}
}
