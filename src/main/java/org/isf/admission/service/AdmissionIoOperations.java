/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2024 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.admission.service;

import static java.time.temporal.TemporalAdjusters.firstDayOfYear;
import static java.time.temporal.TemporalAdjusters.lastDayOfYear;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.List;

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
import org.isf.patient.service.PatientIoOperations;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.pagination.PageInfo;
import org.isf.utils.pagination.PagedResponse;
import org.isf.utils.time.TimeTools;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = OHServiceException.class)
@TranslateOHServiceException
public class AdmissionIoOperations {

	private AdmissionIoOperationRepository repository;

	private AdmissionTypeIoOperationRepository typeRepository;

	private DischargeTypeIoOperationRepository dischargeRepository;

	private PatientIoOperationRepository patientRepository;

	public AdmissionIoOperations(AdmissionIoOperationRepository admissionIoOperationRepository,
	                             AdmissionTypeIoOperationRepository admissionTypeIoOperationRepository,
	                             DischargeTypeIoOperationRepository dischargeTypeIoOperationRepository,
	                             PatientIoOperationRepository patientIoOperationRepository) {
		this.repository = admissionIoOperationRepository;
		this.typeRepository = admissionTypeIoOperationRepository;
		this.dischargeRepository = dischargeTypeIoOperationRepository;
		this.patientRepository = patientIoOperationRepository;
	}

	/**
	 * Returns all patients with ward in which they are admitted.
	 *
	 * @return the patient list with associated ward.
	 * @throws OHServiceException if an error occurs during database request.
	 */
	public List<AdmittedPatient> getAdmittedPatients() throws OHServiceException {
		return getAdmittedPatients(null);
	}

	/**
	 * Returns all patients with ward in which they are admitted filtering the list using the passed search term.
	 *
	 * @param searchTerms the search terms to use for filter the patient list, {@code null} if no filter is to be applied.
	 * @return the filtered patient list.
	 * @throws OHServiceException if an error occurs during database request.
	 */
	public List<AdmittedPatient> getAdmittedPatients(String searchTerms) throws OHServiceException {
		LocalDateTime[] admissionRange = new LocalDateTime[2];
		LocalDateTime[] dischargeRange = new LocalDateTime[2];
		return repository.findPatientAdmissionsBySearchAndDateRanges(searchTerms, admissionRange, dischargeRange);
	}

	/**
	 * Returns all patients based on the applied filters.
	 *
	 * @param admissionRange (two-dimensions array) the patient admission dates range, both {@code null} if no filter is to be applied.
	 * @param dischargeRange (two-dimensions array) the patient discharge dates range, both {@code null} if no filter is to be applied.
	 * @param searchTerms the search terms to use for filter the patient list, {@code null} if no filter is to be applied.
	 * @return the filtered patient list.
	 * @throws OHServiceException if an error occurs during database request.
	 */
	public List<AdmittedPatient> getAdmittedPatients(String searchTerms, LocalDateTime[] admissionRange, LocalDateTime[] dischargeRange)
					throws OHServiceException {
		return repository.findPatientAdmissionsBySearchAndDateRanges(searchTerms, admissionRange, dischargeRange);
	}

	/**
	 * Load patient together with the profile photo, or {@code null} if there is no patient with the given id
	 */
	public AdmittedPatient loadAdmittedPatient(int patientId) {
		boolean isLoadPatientProfilePhotoFromDb = PatientIoOperations.LOAD_FROM_DB.equals(GeneralData.PATIENTPHOTOSTORAGE);
		Patient patient = patientRepository.findById(patientId).orElse(null);
		if (patient == null) {
			return null;
		}
		if (isLoadPatientProfilePhotoFromDb) {
			Hibernate.initialize(patient.getPatientProfilePhoto());
		}
		Admission admission = repository.findOneWherePatientIn(patientId);
		return new AdmittedPatient(patient, admission);
	}

	/**
	 * Returns the current admission (or null if none) for the specified patient.
	 *
	 * @param patient the patient target of the admission.
	 * @return the patient admission.
	 */
	public Admission getCurrentAdmission(Patient patient) {
		return repository.findOneWherePatientIn(patient.getCode());
	}

	/**
	 * Returns the admission with the selected id.
	 *
	 * @param id the admission id.
	 * @return the admission with the specified id, {@code null} otherwise.
	 * @throws OHServiceException if an error occurs during database request.
	 */
	public Admission getAdmission(int id) throws OHServiceException {
		return repository.findById(id).orElse(null);
	}

	/**
	 * Returns all the admissions for the specified patient.
	 *
	 * @param patient the patient.
	 * @return the admission list.
	 * @throws OHServiceException if an error occurs during database request.
	 */
	public List<Admission> getAdmissions(Patient patient) throws OHServiceException {
		return repository.findAllWherePatientByOrderByDate(patient.getCode());
	}

	/**
	 * Inserts a new admission.
	 *
	 * @param admission the admission to insert.
	 * @return the new/saved Admission object.
	 * @throws OHServiceException if an error occurs during the insertion.
	 */
	public Admission newAdmission(Admission admission) throws OHServiceException {
		return repository.save(admission);
	}

	/**
	 * Inserts a new {@link Admission} and the returns the generated id.
	 *
	 * @param admission the admission to insert.
	 * @return the generated id.
	 * @throws OHServiceException if an error occurs during the insertion.
	 */
	public int newAdmissionReturnKey(Admission admission) throws OHServiceException {
		Admission newAdmission = newAdmission(admission);
		return newAdmission.getId();
	}

	/**
	 * Updates the specified {@link Admission} object.
	 *
	 * @param admission the admission object to update.
	 * @return the updated Admission object
	 * @throws OHServiceException if an error occurs.
	 */
	public Admission updateAdmission(Admission admission) throws OHServiceException {
		return repository.save(admission);
	}

	/**
	 * Lists the {@link AdmissionType}s.
	 *
	 * @return the admission types.
	 * @throws OHServiceException
	 */
	public List<AdmissionType> getAdmissionType() throws OHServiceException {
		return typeRepository.findAll();
	}

	/**
	 * Lists the {@link DischargeType}s.
	 *
	 * @return the discharge types.
	 * @throws OHServiceException
	 */
	public List<DischargeType> getDischargeType() throws OHServiceException {
		return dischargeRepository.findAll();
	}

	/**
	 * Returns the next prog in the year for a certain ward.
	 *
	 * @param wardId the ward id.
	 * @return the next prog.
	 * @throws OHServiceException if an error occurs retrieving the value.
	 */
	public int getNextYProg(String wardId) throws OHServiceException {
		int next = 1;
		LocalDateTime now = getNow();
		LocalDateTime first;
		LocalDateTime last;

		if ("M".equalsIgnoreCase(wardId) && GeneralData.MATERNITYRESTARTINJUNE) {
			if (now.getMonthValue() < Month.JUNE.getValue()) {
				first = now.minusYears(1).withMonth(Month.JULY.getValue()).withDayOfMonth(1).with(LocalTime.MIN).truncatedTo(ChronoUnit.SECONDS);
				last = now.withMonth(Month.JUNE.getValue()).withDayOfMonth(30).with(LocalTime.MAX).truncatedTo(ChronoUnit.SECONDS);
			} else {
				first = now.withMonth(Month.JULY.getValue()).withDayOfMonth(1).with(LocalTime.MIN).truncatedTo(ChronoUnit.SECONDS);
				last = now.plusYears(1).withMonth(Month.JUNE.getValue()).withDayOfMonth(30).with(LocalTime.MAX).truncatedTo(ChronoUnit.SECONDS);
			}
		} else {
			first = now.with(firstDayOfYear()).with(LocalTime.MIN).truncatedTo(ChronoUnit.SECONDS);
			last = now.with(lastDayOfYear()).with(LocalTime.MAX).truncatedTo(ChronoUnit.SECONDS);
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
	 * TODO: once the LocalDateTime object is replaced by Java 8+ date/time objects this can be revisited
	 * as there is more flexibility in modifying the new objects in Java 8+.
	 */
	public static boolean testing;
	public static boolean afterJune;

	public static LocalDateTime getNow() {
		LocalDateTime now = TimeTools.getNow();
		if (!testing) {
			return now;
		}
		// testing date June or later
		if (afterJune) {
			return LocalDateTime.of(now.getYear(), 8, 2, 0, 0);
		}
		// testing data before June
		return LocalDateTime.of(now.getYear(), 1, 2, 0, 0);
	}

	/**
	 * Sets an admission record as deleted.
	 *
	 * @param admissionId the admission id.
	 * @return return the "deleted" admission object or null if the admission is not found
	 * @throws OHServiceException if an error occurs.
	 */
	public Admission setDeleted(int admissionId) throws OHServiceException {
		Admission foundAdmission = repository.findById(admissionId).orElse(null);
		if (foundAdmission == null) {
			return null;
		}
		foundAdmission.setDeleted('Y');
		return repository.save(foundAdmission);
	}

	/**
	 * Counts the number of used bed for the specified ward.
	 *
	 * @param wardId the ward id.
	 * @return the number of used beds.
	 * @throws OHServiceException if an error occurs retrieving the bed count.
	 */
	public int getUsedWardBed(String wardId) throws OHServiceException {
		List<Admission> admissionList = repository.findAllWhereWardIn(wardId);
		return admissionList.size();
	}

	/**
	 * Deletes the patient photo.
	 *
	 * @param patientId the patient id.
	 * @return the updated patient object or null if patient is not found
	 * @throws OHServiceException if an error occurs.
	 */
	public Patient deletePatientPhoto(int patientId) throws OHServiceException {
		Patient foundPatient = patientRepository.findById(patientId).orElse(null);
		if (foundPatient == null) {
			return null;
		}
		if (foundPatient.getPatientProfilePhoto() != null && foundPatient.getPatientProfilePhoto().getPhoto() != null) {
			foundPatient.getPatientProfilePhoto().setPhoto(null);
		}
		return patientRepository.save(foundPatient);
	}

	/**
	 * Returns the list of Admissions within a date range
	 *
	 * @param dateFrom
	 * @param dateTo
	 * @return the list of {@link Admission}.
	 * @throws OHServiceException if an error occurs during database request.
	 */
	public List<Admission> getAdmissionsByAdmissionDate(LocalDateTime dateFrom, LocalDateTime dateTo) throws OHServiceException {
		return repository.findAllWhereAdmissionDate(dateFrom, dateTo);
	}

	/**
	 * Returns the list of Admissions with discharge
	 *
	 * @param dateFrom
	 * @param dateTo
	 * @param pageable
	 * @return the list of {@link Admission}.
	 * @throws OHServiceException if an error occurs during database request.
	 */
	public List<Admission> getAdmissionsByDischargeDate(LocalDateTime dateFrom, LocalDateTime dateTo) throws OHServiceException {
		return repository.findAllWhereDischargeDate(dateFrom, dateTo);
	}

	/**
	 * Returns the list of Admissions by page
	 *
	 * @param dateFrom
	 * @param dateTo
	 * @param pageable
	 * @return the list of {@link Admission}.
	 * @throws OHServiceException if an error occurs during database request.
	 */
	public PagedResponse<Admission> getAdmissionsByAdmissionDates(LocalDateTime dateFrom, LocalDateTime dateTo, Pageable pageable) throws OHServiceException {
		Page<Admission> pagedResult = repository.findAllWhere_AdmissionDate_Paginated(dateFrom, dateTo, pageable);
		return setPaginationData(pagedResult);
	}

	/**
	 * Returns the list of Admissions with discharge by page
	 *
	 * @param dateFrom
	 * @param dateTo
	 * @param pageable
	 * @return the list of {@link Admission}.
	 * @throws OHServiceException if an error occurs during database request.
	 */
	public PagedResponse<Admission> getAdmissionsByDischargeDates(LocalDateTime dateFrom, LocalDateTime dateTo, Pageable pageable) throws OHServiceException {
		Page<Admission> pagedResult = repository.findAllWhere_DischargeDate_Paginated(dateFrom, dateTo, pageable);
		return setPaginationData(pagedResult);
	}

	/**
	 * Returns the list of Admissions with page info
	 *
	 * @param pages of admissions
	 * @return {@link PagedResponse<Admission>}.
	 */
	PagedResponse<Admission> setPaginationData(Page<Admission> pages) {
		PagedResponse<Admission> data = new PagedResponse<>();
		data.setData(pages.getContent());
		data.setPageInfo(PageInfo.from(pages));
		return data;
	}

	/**
	 * Count not deleted {@link Admission}s
	 * 
	 * @return the number of recorded {@link Admission}s
	 * @throws OHServiceException
	 */
	public long countAllActiveAdmissions() {
		return this.repository.countAllActiveNotDeletedAdmissions();
	}

}
