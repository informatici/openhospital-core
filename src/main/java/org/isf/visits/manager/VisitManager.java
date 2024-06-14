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
package org.isf.visits.manager;

import static java.util.stream.Collectors.toList;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
import org.isf.utils.time.TimeTools;
import org.isf.visits.model.Visit;
import org.isf.visits.service.VisitsIoOperations;
import org.isf.ward.model.Ward;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Mwithi
 */
@Component
public class VisitManager {

	private VisitsIoOperations ioOperations;

	private SmsOperations smsOp;

	private OpdIoOperationRepository opdRepository;

	private PatientBrowserManager patientBrowserManager;

	public VisitManager(VisitsIoOperations visitsIoOperations, SmsOperations smsOperations, OpdIoOperationRepository opdOperationIoRepository, PatientBrowserManager patientBrowserManager) {
		this.ioOperations = visitsIoOperations;
		this.smsOp = smsOperations;
		this.opdRepository = opdOperationIoRepository;
		this.patientBrowserManager = patientBrowserManager;
	}
	/**
	 * Verify if the visit is valid for CRUD, if not throws an {@link OHServiceException} listing the validation errors.
	 *
	 * @param visit the visit to validate
	 * @throws OHServiceException
	 */
	public void validateVisit(Visit visit) throws OHServiceException {
		List<OHExceptionMessage> errors = new ArrayList<>();
		LocalDateTime visitDate = visit.getDate();
		if (visitDate == null) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.visit.pleasechooseadate.msg")));
		}
		Integer visitDuration = visit.getDuration();
		if (visitDuration == null || visitDuration <= 0) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.visit.invalidvisitduration.msg")));
		}
		Ward ward = visit.getWard();
		if (ward == null) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.visit.pleasechooseaward.msg")));
		}
		Patient patient = visit.getPatient();
		if (patient == null) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.visit.pleasechooseapatient.msg")));
		}
		if (errors.isEmpty()) {
			String sex = String.valueOf(patient.getSex());
			if ((sex.equalsIgnoreCase("F") && !ward.isFemale())
					|| (sex.equalsIgnoreCase("M") && !ward.isMale())) {
				errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.visit.thepatientssexandwarddonotagree.msg")));
			}
		}
		validateNoOverlappingPatientVisitsInDifferentWards(visit).ifPresent(errors::add);
		validateVisitNotOverlappingAnyVisitsInSameWard(visit).ifPresent(errors::add);
		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
	}

	private Optional<OHExceptionMessage> validateVisitNotOverlappingAnyVisitsInSameWard(Visit visit) throws OHServiceException {
		LocalDateTime visitDate = visit.getDate();
		Ward visitWard = visit.getWard();
		if (visitDate == null || visitWard == null || visit.getDuration() == null || visit.getDuration() <= 0) {
			// it's not possible to check overlapping visits if we don't know the new visit ward, date or duration
			return Optional.empty();
		}
		// look if the visit overlaps any existing ones in the ward
		LocalDateTime visitEnd = visit.getEnd();
		List<Visit> overlappingVisits = getVisitsWard(visitWard.getCode()).stream()
				.filter(otherVisit -> otherVisit.getVisitID() != visit.getVisitID()) // don't compare visit with its current state in the DB (if any)
				.filter(otherVisit -> visitOverlaps(visitDate, visitEnd, otherVisit))
				.collect(toList());
		if (overlappingVisits.isEmpty()) {
			return Optional.empty();
		}
		if (overlappingVisits.size() == 1) {
			return Optional.of(new OHExceptionMessage(MessageBundle.formatMessage("angal.visit.overlappingvisitinward.msg", overlappingVisits.get(0))));
		}
		String visitsDescription = overlappingVisits.stream().map(Visit::toString).collect(Collectors.joining(", ", "", ""));
		return Optional.of(new OHExceptionMessage(MessageBundle.formatMessage("angal.visit.overlappingmanyvisitsinward.msg", visitsDescription)));
	}

	private Optional<OHExceptionMessage> validateNoOverlappingPatientVisitsInDifferentWards(Visit visit) throws OHServiceException {
		LocalDateTime visitDate = visit.getDate();
		Ward visitWard = visit.getWard();
		Patient visitPatient = visit.getPatient();
		if (visitDate == null || visit.getDuration() == null || visit.getDuration() <= 0 || visitWard == null || visitPatient == null) {
			// it's not possible to perform the check if we don't know the visit ward, patient, date or duration
			return Optional.empty();
		}
		LocalDateTime visitEnd = visit.getEnd();
		List<Visit> patientVisits = getVisits(visitPatient.getCode());
		List<Visit> overlappingPatientVisitsInDifferentWards = patientVisits.stream()
				.filter(otherVisit -> otherVisit.getVisitID() != visit.getVisitID()) // don't compare visit with its current state in the DB (if any)
				.filter(otherVisit -> !visitWard.equals(otherVisit.getWard())) // different ward
				.filter(otherVisit -> visitOverlaps(visitDate, visitEnd, otherVisit)) // overlapping visits
				.collect(toList());
		if (overlappingPatientVisitsInDifferentWards.isEmpty()) {
			return Optional.empty();
		}
		if (overlappingPatientVisitsInDifferentWards.size() == 1) {
			return Optional.of(new OHExceptionMessage(MessageBundle.formatMessage("angal.visit.overlappingpatientvisitsindifferentwards.msg",
					overlappingPatientVisitsInDifferentWards.get(0).toString())));
		}
		String visitsDescription = overlappingPatientVisitsInDifferentWards.stream().map(Visit::toString).collect(Collectors.joining(", ", "", ""));
		return Optional.of(new OHExceptionMessage(MessageBundle.formatMessage("angal.visit.manyoverlappingpatientvisitsindifferentwards.msg",
				visitsDescription)));
	}

	private static boolean visitOverlaps(LocalDateTime visitStart, LocalDateTime visitEnd, Visit otherVisit) {
		return visitStart.isBefore(otherVisit.getEnd()) && visitEnd.isAfter(otherVisit.getDate());
	}

	/**
	 * Returns the list of all {@link Visit}s related to a patID
	 *
	 * @param patID - the {@link Patient} ID. If {@code 0} return the list of all {@link Visit}s
	 * @return the list of {@link Visit}s
	 * @throws OHServiceException
	 */
	public List<Visit> getVisits(int patID) throws OHServiceException {
		return ioOperations.getVisits(patID);
	}

	/**
	 * Returns the list of all {@link Visit}s related to a patID in OPD (Ward is {@code null}).
	 *
	 * @param patID - the {@link Patient} ID. If {@code 0} return the list of all {@link Visit}s
	 * @return the list of {@link Visit}s
	 * @throws OHServiceException
	 */
	public List<Visit> getVisitsOPD(int patID) throws OHServiceException {
		return ioOperations.getVisitsOPD(patID);
	}

	/**
	 * Returns the list of all {@link Visit}s for all wards
	 *
	 * @return the list of {@link Visit}s for all wards
	 * @throws OHServiceException
	 */
	public List<Visit> getVisitsWard() throws OHServiceException {
		return getVisitsWard(null);
	}

	/**
	 * Returns the list of all {@link Visit}s related to a wardId
	 *
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
	 * to avoid visits overlapping and patients ubiquity
	 *
	 * @param visits - the list of {@link Visit}s related to patID.
	 * @return {@code true} if the list has been replaced, {@code false} otherwise
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
	 * to avoid visits overlapping and patients ubiquity
	 *
	 * @param visits - the list of {@link Visit}s related to patID.
	 * @param removedVisits - the list of {@link Visit}s eventually removed
	 * @return {@code true} if the list has been replaced, {@code false} otherwise
	 * @throws OHServiceException
	 */
	@Transactional(rollbackFor = OHServiceException.class)
	@TranslateOHServiceException
	public boolean newVisits(List<Visit> visits, List<Visit> removedVisits) throws OHServiceException {
		if (!visits.isEmpty()) {
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
						Patient pat = patientBrowserManager.getPatientById(visit.getPatient().getCode());
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
	 * @return {@code true} if the list has been deleted, {@code false} otherwise
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
