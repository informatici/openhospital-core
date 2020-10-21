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
package org.isf.opd.service;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

import org.isf.generaldata.MessageBundle;
import org.isf.opd.model.Opd;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/*----------------------------------------------------
 * (org.isf.opd.service)IoOperations - services for opd class
 * ---------------------------------------------------
 * modification history
 * 11/12/2005 - Vero, Rick  - first beta version 
 * 03/01/2008 - ross - selection for opd browser is performed on Opd_DATE_VIS instead of Opd_DATE
 *                   - selection now is less than or equal, before was only less than
 * 21/06/2008 - ross - for multilanguage version, the test for "all type" and "all disease"
 *                     must be done on the translated resource, not in english
 *                   - fix:  getSurgery() method should not add 1 day to toDate
 * 05/09/2008 - alex - added method for patient related Opd query
 * 05/01/2009 - ross - fix: in insert, referralfrom was written both in referralfrom and referralto
 * 09/01/2009 - fabrizio - Modified queried to accomodate type change of date field in Opd class.
 *                         Modified construction of queries, concatenation is performed with
 *                         StringBuilders instead than operator +. Removed some nested try-catch
 *                         blocks. Modified methods to format dates.                          
 *------------------------------------------*/

@Service
@Transactional(rollbackFor=OHServiceException.class)
@TranslateOHServiceException
public class OpdIoOperations {

	@Autowired
	private OpdIoOperationRepository repository;
	
	/**
	 * return all Opds of today or one week ago
	 * 
	 * @param oneWeek - if <code>true</code> return the last week, only today otherwise.
	 * @return the list of Opds. It could be <code>empty</code>.
	 * @throws OHServiceException 
	 */
	public ArrayList<Opd> getOpdList(boolean oneWeek) throws OHServiceException	{
		LocalDate dateTo = LocalDate.now();
		LocalDate dateFrom = dateTo.minusWeeks(1);
		
		return getOpdList(MessageBundle.getMessage("angal.opd.alltype"),MessageBundle.getMessage("angal.opd.alldisease"),dateFrom,dateTo,0,0,'A','A');
	}
	
	/**
	 * 
	 * return all Opds within specified dates
	 * 
	 * @param diseaseTypeCode
	 * @param diseaseCode
	 * @param dateFrom
	 * @param dateTo
	 * @param ageFrom
	 * @param ageTo
	 * @param sex
	 * @param newPatient
	 * @return the list of Opds. It could be <code>empty</code>.
	 * @throws OHServiceException 
	 */
	public ArrayList<Opd> getOpdList(
			String diseaseTypeCode,
			String diseaseCode, 
			LocalDate dateFrom,
			LocalDate dateTo,
			int ageFrom, 
			int ageTo,
			char sex,
			char newPatient) throws OHServiceException	{
		return new ArrayList<Opd>(repository.findAllOpdWhereParams(
				diseaseTypeCode, diseaseCode, dateFrom, dateTo,
				ageFrom, ageTo, sex, newPatient));			
	}
	
	/**
	 * returns all {@link Opd}s associated to specified patient ID
	 * 
	 * @param patID - the patient ID
	 * @return the list of {@link Opd}s associated to specified patient ID.
	 * 		   the whole list of {@link Opd}s if <code>0</code> is passed.
	 * @throws OHServiceException 
	 */
	public ArrayList<Opd> getOpdList(int patID) throws OHServiceException {
		return  new ArrayList<Opd>(patID == 0 ?
			repository.findAllOrderByProgYearDesc() :
			repository.findAllByPatient_CodeOrderByProgYearDesc(patID));
	}
		
	/**
	 * insert a new item in the db
	 * 
	 * @param opd - an {@link Opd}
	 * @return <code>true</code> if the item has been inserted
	 * @throws OHServiceException 
	 */
	public boolean newOpd(Opd opd) throws OHServiceException {
		return repository.save(opd) != null;
	}
	
	/**
	 * modify an {@link Opd} in the db
	 * 
	 * @param opd - an {@link Opd}
	 * @return the updated {@link Opd}.
	 * @throws OHServiceException 
	 */
	public Opd updateOpd(Opd opd) throws OHServiceException {
		return repository.save(opd);
	}
	
	/**
	 * delete an {@link Opd} from the db
	 * 
	 * @param opd - the {@link Opd} to delete
	 * @return <code>true</code> if the item has been deleted. <code>false</code> otherwise.
	 * @throws OHServiceException 
	 */
	public boolean deleteOpd(Opd opd) throws OHServiceException {
		repository.delete(opd);
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
		Integer progYear = year == 0 ?
			repository.findMaxProgYear() :
			repository.findMaxProgYearWhereDateBetween(LocalDate.of(year, 1, 1), LocalDate.of(year + 1, 1, 1));

		return progYear == null ? 0 : progYear;
	}

	/**
	 * return the last Opd in time associated with specified patient ID. 
	 * 
	 * @param patID - the patient ID
	 * @return last Opd associated with specified patient ID or <code>null</code>
	 * @throws OHServiceException 
	 */
	public Opd getLastOpd(int patID) throws OHServiceException {
		List<Opd> opdList = repository.findTop1ByPatient_CodeOrderByDateDesc(patID);
		return opdList.isEmpty() ? null : opdList.get(0);
	}

	/**
	 * checks if the code is already in use
	 *
	 * @param code - the opd code
	 * @return <code>true</code> if the code is already in use, <code>false</code> otherwise
	 * @throws OHServiceException 
	 */
	public boolean isCodePresent(Integer code) throws OHServiceException {
		return repository.existsById(code);
	}
	
	/**
	 * Check if the given <param>opdNum<param> does already exist for the give <param>year<param>
	 * 
	 * @param opdNum - the OPD progressive in year
	 * @param year - the year
	 * @return <code>true<code> if the given number exists in year, <code>false</code> otherwise
	 * @throws OHServiceException
	 */
	public Boolean isExistOpdNum(int opdNum, int year) throws OHServiceException {
		List<Opd> opds = year == 0 ?
			repository.findByProgYear(opdNum) :
			repository.findByProgYearAndVisitDateBetween(opdNum, LocalDate.of(year, 1, 1), LocalDate.of(year + 1, 1, 1));

		return !opds.isEmpty();
	}
}
