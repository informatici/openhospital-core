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
package org.isf.admission.service;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.Hibernate;
import org.isf.admission.model.Admission;
import org.isf.admission.model.AdmittedPatient;
import org.isf.admtype.model.AdmissionType;
import org.isf.admtype.service.AdmissionTypeIoOperationRepository;
import org.isf.disctype.model.DischargeType;
import org.isf.disctype.service.DischargeTypeIoOperationRepository;
import org.isf.generaldata.GeneralData;
import org.isf.patient.model.Patient;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ---------------------------------------------------------
 * modification history
 * ====================
 * 10/11/06 - ross - removed from the list the deleted patients
 *                   the list is now in alphabetical  order
 * 11/08/08 - alessandro - addedd getFather&Mother Names
 * 26/08/08 - claudio - changed getAge for managing varchar type
 * 					  - added getBirthDate
 * 01/01/09 - Fabrizio - changed the calls to PAT_AGE fields to
 *                       return again an integer type
 * 20/01/09 - Chiara -   restart of progressive number of maternity
 * 						 ward on 1st July conditioned to parameter
 * 						 MATERNITYRESTARTINJUNE in generalData.properties
 * -----------------------------------------------------------
 */
@Service
@Transactional(rollbackFor=OHServiceException.class)
@TranslateOHServiceException
public class AdmissionIoOperations 
{
	@Autowired
	private AdmissionIoOperationRepository repository;
	@Autowired
	private AdmissionTypeIoOperationRepository typeRepository;
	@Autowired
	private DischargeTypeIoOperationRepository dischargeRepository;
	@Autowired
	private PatientIoOperationRepository patientRepository;
	
	/**
	 * Returns all patients with ward in which they are admitted.
	 * @return the patient list with associated ward.
	 */
	public List<AdmittedPatient> getAdmittedPatients() {
		return getAdmittedPatients(null);
	}

	/**
	 * Returns all patients with ward in which they are admitted filtering the list using the passed search term.
	 * @param searchTerms the search terms to use for filter the patient list, {@code null} if no filter is to be applied.
	 * @return the filtered patient list.
	 */
	public List<AdmittedPatient> getAdmittedPatients(String searchTerms) {
		return patientRepository.findByFieldsContainingWordsFromLiteral(searchTerms).stream()
			.map(patient -> new AdmittedPatient(patient, repository.findOneWherePatientIn(patient.getCode())))
			.collect(Collectors.toList());
	}

	/**
	 * Returns all patients based on the applied filters.
	 * @param admissionRange (two-dimensions array) the patient admission dates range, both {@code null} if no filter is to be applied.
	 * @param dischargeRange (two-dimensions array) the patient discharge dates range, both {@code null} if no filter is to be applied.
	 * @param searchTerms the search terms to use for filter the patient list, {@code null} if no filter is to be applied.
	 * @return the filtered patient list.
	 */
	public List<AdmittedPatient> getAdmittedPatients(
			String searchTerms, GregorianCalendar[] admissionRange,
			GregorianCalendar[] dischargeRange) {
		return patientRepository.findByFieldsContainingWordsFromLiteral(searchTerms).stream()
			.map(patient -> new AdmittedPatient(patient, repository.findOneByPatientAndDateRanges(patient, admissionRange, dischargeRange).orElse(null)))
			.filter(admittedPatient -> (isRangeSet(admissionRange) || isRangeSet(dischargeRange)) ? 
								(admittedPatient.getPatient() != null && admittedPatient.getAdmission() != null) : true)
			.collect(Collectors.toList());
	}

	private boolean isRangeSet(GregorianCalendar[] range) {
		return range != null && (range[0] != null || range[1] != null);
	}

	/**
	 * Load patient together with the profile photo, or {@code null} if there is no patient with the given id
	 */
	public AdmittedPatient loadAdmittedPatient(final Integer patientId) {
		final Patient patient = patientRepository.findOne(patientId);
		if (patient == null) {
			return null;
		}
		Hibernate.initialize(patient.getPatientProfilePhoto());
		final Admission admission = repository.findOneWherePatientIn(patientId);
		return new AdmittedPatient(patient, admission);
	}
	
	/**
	 * Returns the current admission (or null if none) for the specified patient.
	 * @param patient the patient target of the admission.
	 * @return the patient admission.
	 */
	public Admission getCurrentAdmission(Patient patient) {
		return repository.findOneWherePatientIn(patient.getCode());
	}

	/**
	 * Returns the admission with the selected id.
	 * @param id the admission id.
	 * @return the admission with the specified id, {@code null} otherwise.
	 */
	public Admission getAdmission(int id) {
		return repository.findOne(id);
	}

	/**
	 * Returns all the admissions for the specified patient.
	 * @param patient the patient.
	 * @return the admission list.
	 */
	public List<Admission> getAdmissions(Patient patient) {
		return repository.findAllWherePatientByOrderByDate(patient.getCode());
	}
	
	/**
	 * Inserts a new admission.
	 * @param admission the admission to insert.
	 * @return <code>true</code> if the admission has been successfully inserted, <code>false</code> otherwise.
	 */
	public boolean newAdmission(Admission admission) {
		Admission savedAdmission = repository.save(admission);
		return savedAdmission != null;
	}

	/**
	 * Inserts a new {@link Admission} and the returns the generated id.
	 * @param admission the admission to insert.
	 * @return the generated id.
	 */
	public int newAdmissionReturnKey(Admission admission) {
		newAdmission(admission);
		return admission.getId();
	}

	/**
	 * Updates the specified {@link Admission} object.
	 * @param admission the admission object to update.
	 * @return <code>true</code> if has been updated, <code>false</code> otherwise.
	 */
	public boolean updateAdmission(Admission admission) {
		Admission savedAdmission = repository.save(admission);
		return savedAdmission != null;
	}

	/**
	 * Lists the {@link AdmissionType}s.
	 * @return the admission types.
	 */
	public List<AdmissionType> getAdmissionType() {
		return typeRepository.findAll();
	}

	/**
	 * Lists the {@link DischargeType}s.
	 * @return the discharge types.
	 */
	public List<DischargeType> getDischargeType() {
		return dischargeRepository.findAll();
	}

	/**
	 * Returns the next prog in the year for a certain ward.
	 * @param wardId the ward id.
	 * @return the next prog.
	 */
	public int getNextYProg(String wardId) {
		int next = 1;
		GregorianCalendar now = getNow();
		GregorianCalendar first;
		GregorianCalendar last;

		if (wardId.equalsIgnoreCase("M") && GeneralData.MATERNITYRESTARTINJUNE) {
			if (now.get(Calendar.MONTH) < 6) {
				first = new GregorianCalendar(now.get(Calendar.YEAR) - 1, Calendar.JULY, 1);
				last = new GregorianCalendar(now.get(Calendar.YEAR), Calendar.JULY, 1);
			} else {
				first = new GregorianCalendar(now.get(Calendar.YEAR), Calendar.JULY, 1);
				last = new GregorianCalendar(now.get(Calendar.YEAR) + 1, Calendar.JULY, 1);

			}
		} else {
			first = new GregorianCalendar(now.get(Calendar.YEAR), 0, 1);
			last = new GregorianCalendar(now.get(Calendar.YEAR), 11, 31);
		}

		List<Admission> admissions = repository.findAllWhereWardAndDates(wardId, first, last);
		if (!admissions.isEmpty()) {
			next = admissions.get(0).getYProg() + 1;
		}

		return next;
	}

	/**
	 * The variables, {@code testing} and {@code afterJune}, are here only for testing purposes and are **NOT** to be used
	 * in production code.   
	 * The default path ({@code testing == false}) ensures that the code performs as it
	 * always has in the past.
	 * This code permits the unit testing of maternity wards with dates before and after June.
	 * TODO: once the GregorianCalendar object is replaced by Java 8+ date/time objects this can be revisited
	 * as there is more flexibility in modifying the new objects in Java 8+.
	 */
	public static boolean testing = false;
	public static boolean afterJune = false;
	public static GregorianCalendar getNow() {
		GregorianCalendar now = new GregorianCalendar();
		if (!testing) {
			return now;
		}
		// testing date June or later
		if (afterJune) {
			return new GregorianCalendar(now.get(Calendar.YEAR), 8, 2);
		}
		// testing data before June
		return new GregorianCalendar(now.get(Calendar.YEAR), 0, 2);
	}

	/**
	 * Sets an admission record to deleted.
	 * @param admissionId the admission id.
	 * @return <code>true</code> if the record has been set to delete.
	 */
	public boolean setDeleted(int admissionId) {
		Admission foundAdmission = repository.findOne(admissionId);
		foundAdmission.setDeleted("Y");
		Admission savedAdmission = repository.save(foundAdmission);
		return savedAdmission != null;
	}

	/**
	 * Counts the number of used bed for the specified ward.
	 * @param wardId the ward id.
	 * @return the number of used beds.
	 */
	public int getUsedWardBed(String wardId) {
		List<Admission> admissionList = repository.findAllWhereWardIn(wardId);
		return admissionList.size();
	}

	/**
	 * Deletes the patient photo.
	 * @param patientId the patient id.
	 * @return <code>true</code> if the photo has been deleted, <code>false</code> otherwise.
	 */
	public boolean deletePatientPhoto(int patientId) {
		Patient foundPatient = patientRepository.findOne(patientId);
		if (foundPatient.getPatientProfilePhoto() != null && foundPatient.getPatientProfilePhoto().getPhoto() != null) {
			foundPatient.getPatientProfilePhoto().setPhoto(null);
		}
		Patient savedPatient = patientRepository.save(foundPatient);
		return savedPatient != null;
	}
}
