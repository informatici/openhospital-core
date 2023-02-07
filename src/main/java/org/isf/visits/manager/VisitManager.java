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
package org.isf.visits.manager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.isf.generaldata.MessageBundle;
import org.isf.menu.manager.UserBrowsingManager;
import org.isf.opd.service.OpdIoOperationRepository;
import org.isf.patient.manager.PatientBrowserManager;
import org.isf.patient.model.Patient;
import org.isf.sms.manager.SmsManager;
import org.isf.sms.model.Sms;
import org.isf.sms.service.SmsOperations;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.isf.utils.time.TimeTools;
import org.isf.visits.model.Visit;
import org.isf.visits.service.VisitsIoOperations;
import org.isf.ward.model.Ward;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Mwithi
 */
@Component
public class VisitManager {

	@Autowired
	private VisitsIoOperations ioOperations;

	@Autowired
	private SmsOperations smsOp;
	
	@Autowired
	private OpdIoOperationRepository opdRepository;

	@Autowired
	private ApplicationContext applicationContext;
	
	/**
	 * Verify if the object is valid for CRUD and return a list of errors, if any
	 *
	 * @param visit
	 * @throws OHServiceException
	 */
	public void validateVisit(Visit visit) throws OHServiceException {
		List<OHExceptionMessage> errors = new ArrayList<>();
		LocalDateTime visitDate = visit.getDate();
		Patient patient = visit.getPatient();
		if (visitDate == null) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
							MessageBundle.getMessage("angal.visit.pleasechooseadate.msg"),
							OHSeverityLevel.ERROR));
		}
		Ward ward = visit.getWard();
		if (ward == null) { 
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
							MessageBundle.getMessage("angal.visit.pleasechooseaward.msg"),
							OHSeverityLevel.ERROR));

		}
		if (patient == null) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
							MessageBundle.getMessage("angal.visit.pleasechooseapatient.msg"),
							OHSeverityLevel.ERROR));
		}
		if (errors.isEmpty()) {
			String sex = String.valueOf(patient.getSex());
			if ((sex.equalsIgnoreCase("F") && !ward.isFemale())
				|| (sex.equalsIgnoreCase("M") && !ward.isMale())) {
				errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
						MessageBundle.getMessage("angal.visit.thepatientssexandwarddonotagree.msg"),
						OHSeverityLevel.ERROR));
			}
		}
		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
	}

	/**
	 * Returns the list of all {@link Visit}s related to a patID
	 *
	 * @param patID - the {@link Patient} ID. If <code>0</code> return the list of all {@link Visit}s
	 * @return the list of {@link Visit}s
	 * @throws OHServiceException
	 */
	public List<Visit> getVisits(int patID) throws OHServiceException {
		return ioOperations.getVisits(patID);
	}
	
	/**
	 * Returns the list of all {@link Visit}s related to a patID in OPD (Ward is {@code null}).
	 *
	 * @param patID - the {@link Patient} ID. If <code>0</code> return the list of all {@link Visit}s
	 * @return the list of {@link Visit}s
	 * @throws OHServiceException
	 */
	public List<Visit> getVisitsOPD(int patID) throws OHServiceException {
		return ioOperations.getVisitsOPD(patID);
	}

	/**
	 * Returns the list of all {@link Visit}s related to a wardId
	 * @param wardId - if {@code null}, returns all visits for all wards
	 * @return the list of {@link Visit}s
	 * @throws OHServiceException
	 */
	public List<Visit> getVisitsWard() throws OHServiceException {
		return getVisitsWard(null);
	}

	/**
	 * Returns the list of all {@link Visit}s related to a wardId
	 * @param wardId - if {@code null}, returns all visits for all wards
	 * @return the list of {@link Visit}s
	 * @throws OHServiceException
	 */
	public List<Visit> getVisitsWard(String wardId) throws OHServiceException {
		return ioOperations.getVisitsWard(wardId);
	}

	/**
	 * Insert a new {@link Visit} for related Patient
	 *
	 * @param visit - the {@link Visit}
	 * @return the persisted Visit
	 * @throws OHServiceException
	 */
	public Visit newVisit(Visit visit) throws OHServiceException {
		validateVisit(visit);
		return ioOperations.newVisit(visit);
	}
	
	/**
	 * Update a new {@link Visit} for related Patient
	 *
	 * @param visit - the {@link Visit}
	 * @return the updated Visit
	 * @throws OHServiceException
	 */
	public Visit updateVisit(Visit visit) throws OHServiceException {
		validateVisit(visit);
		return ioOperations.newVisit(visit);
	}
	
	/**
	 * Delete the {@link Visit} for related Patient
	 *
	 * @param visit - the {@link Visit}
	 * @return the visitID
	 */
	public void deleteVisit(Visit visit) throws OHServiceException {
		ioOperations.deleteVisit(visit);
	}
	
	/**
	 * Inserts or replaces all {@link Visit}s related to a patID<br>
	 * <br>
	 * TODO: OP-722 add overall validation on {@code visits} beside single {@link validateVisit} 
	 * to avoid visits overlapping and patients ubiquity
	 * 
	 * @param visits - the list of {@link Visit}s related to patID.
	 * @return <code>true</code> if the list has been replaced, <code>false</code> otherwise
	 * @throws OHServiceException
	 */
	@Transactional(rollbackFor = OHServiceException.class)
	@TranslateOHServiceException
	public boolean newVisits(List<Visit> visits) throws OHServiceException {
		return newVisits(visits, new ArrayList<>());
	}
	
	/**
	 * Inserts or replaces all {@link Visit}s related to a patID<br>
	 * <br>
	 * TODO: OP-722 Add overall validation on {@code visits} beside single {@link validateVisit} 
	 * to avoid visits overlapping and patients ubiquity
	 * 
	 * @param visits - the list of {@link Visit}s related to patID.
	 * @param removedVisits - the list of {@link Visits}s eventually removed
	 * @return <code>true</code> if the list has been replaced, <code>false</code> otherwise
	 * @throws OHServiceException
	 */
	@Transactional(rollbackFor = OHServiceException.class)
	@TranslateOHServiceException
	public boolean newVisits(List<Visit> visits, List<Visit> removedVisits) throws OHServiceException {
		if (!visits.isEmpty()) {
			PatientBrowserManager patMan = this.applicationContext.getBean(PatientBrowserManager.class);
			int patID = visits.get(0).getPatient().getCode();
			for (Visit visit : removedVisits) {
				deleteVisit(visit);
			}
			smsOp.deleteByModuleModuleID("visit", String.valueOf(patID));
			
			for (Visit visit : visits) {
				validateVisit(visit);
				
				int visitID = ioOperations.newVisit(visit).getVisitID();
				if (visitID == 0) {
					return false;
				}
				visit.setVisitID(visitID);
				
				if (visit.isSms()) {
					LocalDateTime date = visit.getDate().minusDays(1);
					if (visit.getDate().isAfter(TimeTools.getDateToday24())) {
						Patient pat = patMan.getPatientById(visit.getPatient().getCode());
						Sms sms = new Sms();
						sms.setSmsDateSched(date);
						sms.setSmsNumber(pat.getTelephone());
						sms.setSmsText(prepareSmsFromVisit(visit));
						sms.setSmsUser(UserBrowsingManager.getCurrentUser());
						sms.setModule("visit");
						sms.setModuleID(String.valueOf(patID));
						smsOp.saveOrUpdate(sms);
					}
				}
			}
		}
		return true;
	}

	/**
	 * Deletes all {@link Visit}s related to a patID
	 *
	 * @param patID - the {@link Patient} ID
	 * @return <code>true</code> if the list has been deleted, <code>false</code> otherwise
	 * @throws OHServiceException
	 */
	@Transactional(rollbackFor = OHServiceException.class)
	@TranslateOHServiceException
	public boolean deleteAllVisits(int patID) throws OHServiceException {
		List<Visit> visits = ioOperations.getVisits(patID);
		for (Visit visit : visits) {
			deleteVisit(visit);
		}
		smsOp.deleteByModuleModuleID("visit", String.valueOf(patID));
		return true;
	}

	/**
	 * Builds the {@link Sms} text for the specified {@link Visit}
	 * If the length exceeds {@code SmsManager.MAX_LENGHT} the message will be truncated to
	 * the maximum length.
	 * (example:
	 * "REMINDER: dd/MM/yy - HH:mm:ss - {@link Visit#getNote()}")
	 *
	 * @param visit - the {@link Visit}
	 * @return a string containing the text
	 */
	private String prepareSmsFromVisit(Visit visit) {

		String note = visit.getNote();
		StringBuilder sb = new StringBuilder(MessageBundle.getMessage("angal.common.reminder.txt").toUpperCase()).append(": ");
		sb.append(visit.toStringSMS());
		if (note != null && !note.isEmpty()) {
			sb.append(" - ").append(note);
		}
		if (sb.toString().length() > SmsManager.MAX_LENGHT) {
			return sb.substring(0, SmsManager.MAX_LENGHT);
		}
		return sb.toString();
	}

	/**
	 * Returns the {@link Visit} based on visit id
	 *
	 * @param id - the  {@link Visit} id.
	 * @return the {@link Visit}
	 */
	public Visit findVisit(int id) throws OHServiceException {
		return ioOperations.findVisit(id);
	}
}
