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
package org.isf.therapy.manager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.isf.generaldata.MessageBundle;
import org.isf.medicals.manager.MedicalBrowsingManager;
import org.isf.medicals.model.Medical;
import org.isf.medicalstockward.manager.MovWardBrowserManager;
import org.isf.menu.manager.UserBrowsingManager;
import org.isf.patient.manager.PatientBrowserManager;
import org.isf.patient.model.Patient;
import org.isf.sms.manager.SmsManager;
import org.isf.sms.model.Sms;
import org.isf.sms.service.SmsOperations;
import org.isf.therapy.model.Therapy;
import org.isf.therapy.model.TherapyRow;
import org.isf.therapy.service.TherapyIoOperations;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.time.TimeTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TherapyManager {

	@Autowired
	private TherapyIoOperations ioOperations;

	@Autowired
	private SmsOperations smsOp;

	@Autowired
	private PatientBrowserManager patientManager;

	@Autowired
	private MedicalBrowsingManager medManager;

	@Autowired
	private MovWardBrowserManager wardManager;

	/**
	 * Returns a {@link Therapy} object from a {@link TherapyRow} (DB record)
	 *
	 * @param th - the {@link TherapyRow}
	 * @return the {@link Therapy}
	 * @throws OHServiceException
	 */
	public Therapy createTherapy(TherapyRow th) throws OHServiceException {
		return createTherapy(th.getTherapyID(), th.getPatient().getCode(), th.getMedical(), th.getQty(), th.getStartDate(), th.getEndDate(),
				th.getFreqInPeriod(), th.getFreqInDay(), th.getNote(), th.isNotify(), th.isSms());
	}

	/**
	 * Creates a {@link Therapy} from its parameters, fetching the {@link Medical}
	 * and building the array of Dates ({@link LocalDateTime})
	 *
	 * @param therapyID
	 * @param patID
	 * @param medId
	 * @param qty
	 * @param startDate
	 * @param endDate
	 * @param freqInPeriod
	 * @param freqInDay
	 * @param note
	 * @param notify
	 * @param sms
	 * @return the {@link Therapy}
	 */
	private Therapy createTherapy(int therapyID, int patID, Integer medId, Double qty,
			LocalDateTime startDate, LocalDateTime endDate, int freqInPeriod,
			int freqInDay, String note, boolean notify, boolean sms) throws OHServiceException {

		List<LocalDateTime> datesArray = new ArrayList<>();

		LocalDateTime stepDate = TimeTools.truncateToSeconds(startDate);
		datesArray.add(stepDate);

		endDate = TimeTools.truncateToSeconds(endDate);

		while (stepDate.isBefore(endDate)) {
			LocalDateTime newDate = stepDate.plusDays(freqInPeriod);
			datesArray.add(newDate);
			stepDate = newDate;
		}

		LocalDateTime[] dates = new LocalDateTime[datesArray.size()];

		for (int i = 0; i < datesArray.size(); i++) {
			dates[i] = datesArray.get(i);
		}

		Medical med = medManager.getMedical(medId);
		return new Therapy(therapyID, patID, dates, med, qty, "", freqInDay, note, notify, sms);
	}

	/**
	 * Returns a list of {@link Therapy}s from a list of {@link TherapyRow}s (DB records)
	 *
	 * @param thRows - the list of {@link TherapyRow}s
	 * @return the list of {@link Therapy}s
	 * @throws OHServiceException
	 */
	public List<Therapy> getTherapies(List<TherapyRow> thRows) throws OHServiceException {

		if (thRows != null) {
			List<Therapy> therapies = new ArrayList<>();
			for (TherapyRow thRow : thRows) {
				therapies.add(createTherapy(thRow));
			}
			return therapies;
		}
		return null;
	}

	/**
	 * Return the list of {@link TherapyRow}s (therapies) for specified Patient ID
	 * or
	 * return all {@link TherapyRow}s (therapies) if <code>0</code> is passed
	 *
	 * @param code - the Patient ID
	 * @return the list of {@link TherapyRow}s (therapies)
	 * @throws OHServiceException
	 */
	public List<TherapyRow> getTherapyRows(int code) throws OHServiceException {
		return ioOperations.getTherapyRows(code);
	}

	/**
	 * Insert a new {@link TherapyRow} (therapy) for related Patient
	 *
	 * @param thRow - the {@link TherapyRow}s (therapy)
	 * @return the therapyID
	 * @throws OHServiceException
	 */
	public TherapyRow newTherapy(TherapyRow thRow) throws OHServiceException {
		return ioOperations.newTherapy(thRow);
	}

	/**
	 * Replace all {@link TherapyRow}s (therapies) for related Patient
	 *
	 * @param thRows - the list of {@link TherapyRow}s (therapies)
	 * @return <code>true</code> if the row has been inserted, <code>false</code> otherwise
	 * @throws OHServiceException
	 */
	@Transactional(rollbackFor = OHServiceException.class)
	@TranslateOHServiceException
	public boolean newTherapies(List<TherapyRow> thRows) throws OHServiceException {
		if (!thRows.isEmpty()) {

			int patID = thRows.get(0).getPatient().getCode();
			smsOp.deleteByModuleModuleID("therapy", String.valueOf(patID));

			for (TherapyRow thRow : thRows) {

				ioOperations.newTherapy(thRow);
				if (thRow.isSms()) {
					Therapy th = createTherapy(thRow);
					LocalDateTime[] dates = th.getDates();
					for (LocalDateTime date : dates) {
						date = date.withHour(8);
						if (date.isAfter(TimeTools.getDateToday24())) {
							Patient pat = patientManager.getPatientById(patID);

							Sms sms = new Sms();
							sms.setSmsDateSched(date);
							sms.setSmsNumber(pat.getTelephone());
							sms.setSmsText(prepareSmsFromTherapy(th));
							sms.setSmsUser(UserBrowsingManager.getCurrentUser());
							sms.setModule("therapy");
							sms.setModuleID(String.valueOf(patID));
							smsOp.saveOrUpdate(sms);
						}
					}
				}
			}
		}
		return true;
	}

	/**
	 * Builds the {@link Sms} text for the specified {@link Therapy}
	 * If length exceed {@code SmsManager.MAX_LENGHT} the message will be cropped
	 * (example:
	 * "REMINDER: {@link Medical} 3pcs - 2pd - {@link Therapy#getNote()}")
	 *
	 * @param th - the {@link Therapy}s
	 * @return a string containing the text
	 */
	private String prepareSmsFromTherapy(Therapy th) {

		String note = th.getNote();
		StringBuilder sb = new StringBuilder(MessageBundle.getMessage("angal.common.reminder.txt").toUpperCase()).append(": ");
		sb.append(th.getMedical().toString()).append(" - ");
		sb.append(th.getQty()).append(th.getUnits()).append(" - ");
		sb.append(th.getFreqInDay()).append("pd");
		if (note != null && !note.isEmpty()) {
			sb.append(" - ").append(note);
		}
		if (sb.toString().length() > SmsManager.MAX_LENGHT) {
			return sb.substring(0, SmsManager.MAX_LENGHT);
		}
		return sb.toString();
	}

	/**
	 * Delete all {@link TherapyRow}s (therapies) for specified Patient ID
	 *
	 * @param code - the Patient ID
	 * @return <code>true</code> if the therapies have been deleted, <code>false</code> otherwise
	 * @throws OHServiceException
	 */
	@Transactional(rollbackFor = OHServiceException.class)
	@TranslateOHServiceException
	public boolean deleteAllTherapies(Integer code) throws OHServiceException {
		Patient patient = patientManager.getPatientById(code);
		return ioOperations.deleteAllTherapies(patient);
	}

	/**
	 * Returns the {@link Medical}s that are not available for the specified list of {@link Therapy}s
	 *
	 * @param therapies - the list of {@link Therapy}s
	 * @return the list of {@link Medical}s out of stock
	 * @throws OHServiceException
	 */
	@Transactional(rollbackFor = OHServiceException.class)
	@TranslateOHServiceException
	public List<Medical> getMedicalsOutOfStock(List<Therapy> therapies) throws OHServiceException {
		List<Medical> medOutStock = new ArrayList<>();
		List<Medical> medArray = medManager.getMedicals();

		double neededQty;
		double actualQty;

		for (Therapy th : therapies) {

			// CALCULATING NEEDINGS
			Double qty = th.getQty();
			int freq = th.getFreqInDay();
			LocalDateTime todayDate = TimeTools.getDateToday0();

			int dayCount = 0;
			for (LocalDateTime date : th.getDates()) {
				if (date.isAfter(todayDate) || date.equals(todayDate)) {
					dayCount++;
				}
			}

			if (dayCount != 0) {

				neededQty = qty * freq * dayCount;

				// CALCULATING STOCK QUANTITIES
				Medical med = medArray.get(medArray.indexOf(th.getMedical()));
				actualQty = med.getInitialqty() + med.getInqty() - med.getOutqty(); // MAIN STORE
				int currentQuantity = wardManager.getCurrentQuantityInWard(null, med);
				actualQty += currentQuantity;

				if (neededQty > actualQty) {
					if (!medOutStock.contains(med)) {
						medOutStock.add(med);
					}
				}
			}
		}
		return medOutStock;
	}

	/**
	 * Insert a new {@link TherapyRow} (therapy) for related Patient
	 * 
	 * @param therapyID
	 * @param patID
	 * @param startDate
	 * @param endDate
	 * @param medical
	 * @param qty
	 * @param unitID
	 * @param freqInDay
	 * @param freqInPeriod
	 * @param note
	 * @param notify
	 * @param sms
	 * @return 
	 * @throws OHServiceException
	 */
	public TherapyRow newTherapy(int therapyID, int patID, LocalDateTime startDate, LocalDateTime endDate, Medical medical, Double qty, int unitID,
			int freqInDay, int freqInPeriod, String note, boolean notify, boolean sms) throws OHServiceException {
		Patient patient = patientManager.getPatientById(patID);
		TherapyRow thRow = new TherapyRow(therapyID, patient, startDate, endDate, medical, qty, unitID, freqInDay, freqInPeriod, note, notify, sms);
		return newTherapy(thRow);
	}
	
	/**
	 * Prepare a {@link TherapyRow} (DB record) object from a {@link Therapy}
	 *
	 * @param therapyID
	 * @param patID
	 * @param startDate
	 * @param endDate
	 * @param medical
	 * @param qty
	 * @param unitID
	 * @param freqInDay
	 * @param freqInPeriod
	 * @param note
	 * @param notify
	 * @param sms
	 * @return the {@link TherapyRow}
	 * @throws OHServiceException
	 */
	public TherapyRow getTherapyRow(int therapyID, int patID, LocalDateTime startDate, LocalDateTime endDate, Medical medical, Double qty, int unitID,
			int freqInDay, int freqInPeriod, String note, boolean notify, boolean sms) throws OHServiceException {
		Patient patient = patientManager.getPatientById(patID);
		return new TherapyRow(therapyID, patient, startDate, endDate, medical, qty, unitID, freqInDay, freqInPeriod, note, notify, sms);
	}

}
