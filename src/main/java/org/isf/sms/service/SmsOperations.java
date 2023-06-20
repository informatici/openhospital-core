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
package org.isf.sms.service;

import java.time.LocalDateTime;
import java.util.List;

import org.isf.sms.model.Sms;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.time.TimeTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Mwithi
 * @see org.isf.sms.model.Sms
 *
 * Generated 31-gen-2014 15.39.04 by Hibernate Tools 3.4.0.CR1
 */
@Service
@Transactional(rollbackFor=OHServiceException.class)
@TranslateOHServiceException
public class SmsOperations {

	@Autowired
	private SmsIoOperationRepository repository;
	
	public SmsOperations() {}
	
	/**
	 * Save or Update a {@link Sms}
	 * @param sms - the {@link Sms} to save or update
	 * @return <code>true</code> if data has been saved, <code>false</code> otherwise. 
	 * @throws OHServiceException 
	 */
	public boolean saveOrUpdate(Sms sms) throws OHServiceException {
		return repository.save(sms) != null;
	}
	
	/**
	 * Save or Update a list of {@link Sms}s
	 * @param smsList - the list of {@link Sms} to save or update
	 * @return <code>true</code> if data has been saved, <code>false</code> otherwise. 
	 * @throws OHServiceException 
	 */
	public boolean saveOrUpdate(List<Sms> smsList) throws OHServiceException {
		return repository.saveAll(smsList) != null;
	}
	
	/**
	 * Returns a {@link Sms} with specified ID
	 * @param ID - sms ID
	 * @return sms - the sms with specified ID
	 * @throws OHServiceException 
	 */
	public Sms getByID(int id) throws OHServiceException {
		return repository.findById(id).orElse(null);
	}
	
	/**
	 * Returns the list of all {@link Sms}s, sent and not sent, between the two dates
	 * @return smsList - the list of {@link Sms}s
	 * @throws OHServiceException 
	 */
	public List<Sms> getAll(LocalDateTime dateFrom, LocalDateTime dateTo) throws OHServiceException {
		return repository.findBySmsDateSchedBetweenOrderBySmsDateSchedAsc(TimeTools.truncateToSeconds(dateFrom), TimeTools.truncateToSeconds(dateTo));
	}
	
	/**
	 * Returns the list of not sent {@link Sms}s between the two dates
	 * @return smsList - the list of {@link Sms}s
	 * @throws OHServiceException 
	 */
	public List<Sms> getList(LocalDateTime dateFrom, LocalDateTime dateTo) throws OHServiceException {
		return repository.findBySmsDateSchedBetweenAndSmsDateSentIsNullOrderBySmsDateSchedAsc(TimeTools.truncateToSeconds(dateFrom),
		                                                                                      TimeTools.truncateToSeconds(dateTo));
	}
	
	/**
	 * Returns the list of not sent {@link Sms}s
	 * @return smsList - the list of {@link Sms}s
	 * @throws OHServiceException 
	 */
	public List<Sms> getList() throws OHServiceException {
		return repository.findBySmsDateSentIsNullOrderBySmsDateSchedAsc();
	}
	
	/**
	 * Delete the specified {@link Sms}
	 * @param sms - the {@link Sms}s to delete
	 * @throws OHServiceException 
	 */
	public void delete(Sms sms) throws OHServiceException {
		repository.delete(sms);
	}

	/**
	 * Delete the specified list of {@link Sms}
	 * @param smsList - the list of {@link Sms}s to delete
	 * @throws OHServiceException 
	 */
	public void delete(List<Sms> smsList) throws OHServiceException	{
		repository.deleteAll(smsList);
	}

	/**
	 * Delete the specified {@link Sms}s if not already sent
	 * @param module - the module name which generated the {@link Sms}s
	 * @param moduleID - the module ID within its generated {@link Sms}s
	 * @throws OHServiceException 
	 */
	public void deleteByModuleModuleID(String module, String moduleID) throws OHServiceException {
		repository.deleteByModuleAndModuleIDAndSmsDateSentIsNull(module, moduleID);
	}

	/**
	 * Checks if the code is already in use
	 *
	 * @param code - the Sms code
	 * @return <code>true</code> if the code is already in use, <code>false</code> otherwise
	 * @throws OHServiceException 
	 */
	public boolean isCodePresent(Integer code) throws OHServiceException {
		return repository.existsById(code);
	}

}
