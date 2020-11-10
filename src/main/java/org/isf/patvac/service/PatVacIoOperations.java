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
package org.isf.patvac.service;

/*------------------------------------------
 * IoOperations  - Patient Vaccine Io operations
 * -----------------------------------------
 * modification history
 * 25/08/2011 - claudia - first beta version
 * 20/10/2011 - insert vaccine type management
 * 14/11/2011 - claudia - inserted search condition on date
 *------------------------------------------*/
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import org.isf.patvac.model.PatientVaccine;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor=OHServiceException.class)
@TranslateOHServiceException
public class PatVacIoOperations {

	@Autowired
	private PatVacIoOperationRepository repository;
	
	/**
	 * returns all {@link PatientVaccine}s of today or one week ago
	 * 
	 * @param minusOneWeek - if <code>true</code> return the last week
	 * @return the list of {@link PatientVaccine}s
	 * @throws OHServiceException 
	 */
	public ArrayList<PatientVaccine> getPatientVaccine(
			boolean minusOneWeek) throws OHServiceException 
	{
		GregorianCalendar timeTo = new GregorianCalendar();
		GregorianCalendar timeFrom = new GregorianCalendar();
	
		
		if (minusOneWeek)
		{
			timeFrom.add(GregorianCalendar.WEEK_OF_YEAR, -1);			
		}
		
		return getPatientVaccine(null, null, timeFrom, timeTo, 'A', 0, 0);
	}

	/**
	 * returns all {@link PatientVaccine}s within <code>dateFrom</code> and
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
	public ArrayList<PatientVaccine> getPatientVaccine(
			String vaccineTypeCode, 
			String vaccineCode, 
			GregorianCalendar dateFrom, 
			GregorianCalendar dateTo, 
			char sex, 
			int ageFrom, 
			int ageTo) throws OHServiceException {
		return new ArrayList<PatientVaccine>(repository.findAllByCodesAndDatesAndSexAndAges(
				vaccineTypeCode, vaccineCode, dateFrom, dateTo, sex, ageFrom, ageTo));
	}

	public List<PatientVaccine> findForPatient(int patientCode) {
		return repository.findByPatient_code(patientCode);
	}

	/**
	 * Inserts a {@link PatientVaccine} in the DB
	 * 
	 * @param patVac - the {@link PatientVaccine} to insert
	 * @return <code>true</code> if the item has been inserted, <code>false</code> otherwise 
	 * @throws OHServiceException 
	 */
	public boolean newPatientVaccine(PatientVaccine patVac) throws OHServiceException {
		return repository.save(patVac) != null;
	}

	/**
	 * Updates a {@link PatientVaccine}
	 * 
	 * @param patVac - the {@link PatientVaccine} to update
	 * @return <code>true</code> if the item has been updated, <code>false</code> otherwise 
	 * @throws OHServiceException 
	 */
	public boolean updatePatientVaccine(PatientVaccine patVac) throws OHServiceException {
		return repository.save(patVac) != null;
	}

	/**
	 * Deletes a {@link PatientVaccine}
	 * 
	 * @param patVac - the {@link PatientVaccine} to delete
	 * @return <code>true</code> if the item has been deleted, <code>false</code> otherwise 
	 * @throws OHServiceException 
	 */
	public boolean deletePatientVaccine(PatientVaccine patVac) throws OHServiceException {
		repository.delete(patVac);
		return true;
	}

	/**
	 * Returns the max progressive number within specified year or within current year if <code>0</code>.
	 * 
	 * @param year
	 * @return <code>int</code> - the progressive number in the year
	 * @throws OHServiceException 
	 */
	public int getProgYear(int year) throws OHServiceException {
		Integer progYear =year != 0 ?
			repository.findMaxCodeWhereVaccineDate(getBeginningOfYear(year), getBeginningOfYear(year + 1)) :
			repository.findMaxCode();

		return progYear == null ? 0 : progYear;
	}

	/**
	 * Checks if the code is already in use
	 *
	 * @param code - the patient vaccine code
	 * @return <code>true</code> if the code is already in use, <code>false</code> otherwise
	 * @throws OHServiceException 
	 */
	public boolean isCodePresent(Integer code) throws OHServiceException {
		return repository.exists(code);
	}

	private GregorianCalendar getBeginningOfYear(int year) {
		return new DateTime().withYear(year).dayOfYear().withMinimumValue().withTimeAtStartOfDay().toGregorianCalendar();
	}
}
