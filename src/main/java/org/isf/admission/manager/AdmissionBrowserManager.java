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
package org.isf.admission.manager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.isf.admission.model.Admission;
import org.isf.admission.model.AdmittedPatient;
import org.isf.admission.service.AdmissionIoOperations;
import org.isf.admtype.model.AdmissionType;
import org.isf.disctype.model.DischargeType;
import org.isf.disease.manager.DiseaseBrowserManager;
import org.isf.disease.model.Disease;
import org.isf.generaldata.MessageBundle;
import org.isf.patient.model.Patient;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.pagination.PagedResponse;
import org.isf.utils.time.TimeTools;
import org.isf.ward.model.Ward;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

@Component
public class AdmissionBrowserManager {

	private AdmissionIoOperations ioOperations;

	private DiseaseBrowserManager diseaseManager;

	public AdmissionBrowserManager(AdmissionIoOperations admissionIoOperations, DiseaseBrowserManager diseaseBrowserManager) {
		this.ioOperations = admissionIoOperations;
		this.diseaseManager = diseaseBrowserManager;
	}

	/**
	 * Returns all patients with ward in which they are admitted.
	 *
	 * @return the patient list with associated ward or {@code null} if the operation fails.
	 * @throws OHServiceException
	 */
	public List<AdmittedPatient> getAdmittedPatients() throws OHServiceException {
		return ioOperations.getAdmittedPatients();
	}

	/**
	 * Returns all patients with ward in which they are admitted filtering the list using the passed search term.
	 *
	 * @param searchTerms the search terms to use for filter the patient list, {@code null} if no filter have to be applied.
	 * @return the filtered patient list or {@code null} if the operation fails.
	 * @throws OHServiceException
	 */
	public List<AdmittedPatient> getAdmittedPatients(String searchTerms) throws OHServiceException {
		return ioOperations.getAdmittedPatients(searchTerms);
	}

	/**
	 * Returns all patients based on the applied filters.
	 *
	 * @param admissionRange (two-dimensions array) the patient admission dates range, both {@code null} if no filter have to be applied.
	 * @param dischargeRange (two-dimensions array) the patient admission dates range, both {@code null} if no filter have to be applied.
	 * @param searchTerms the search terms to use for filter the patient list, {@code null} if no filter have to be applied.
	 * @return the filtered patient list.
	 * @throws OHServiceException if an error occurs during database request.
	 */
	public List<AdmittedPatient> getAdmittedPatients(LocalDateTime[] admissionRange, LocalDateTime[] dischargeRange, String searchTerms)
					throws OHServiceException {
		return ioOperations.getAdmittedPatients(searchTerms, admissionRange, dischargeRange);
	}

	public AdmittedPatient loadAdmittedPatients(int patientId) {
		return ioOperations.loadAdmittedPatient(patientId);
	}

	/**
	 * Returns the admission with the selected id.
	 *
	 * @param id the admission id.
	 * @return the admission with the specified id, {@code null} otherwise.
	 * @throws OHServiceException
	 */
	public Admission getAdmission(int id) throws OHServiceException {
		return ioOperations.getAdmission(id);
	}

	/**
	 * Returns the only one admission without admission date (or null if none) for the specified patient.
	 *
	 * @param patient the patient target of the admission.
	 * @return the patient admission or {@code null} if the operation fails.
	 */
	public Admission getCurrentAdmission(Patient patient) {
		return ioOperations.getCurrentAdmission(patient);
	}

	/**
	 * Returns all the admissions for the specified patient.
	 *
	 * @param patient the patient.
	 * @return the admission list or {@code null} if the operation fails.
	 * @throws OHServiceException
	 */
	public List<Admission> getAdmissions(Patient patient) throws OHServiceException {
		return ioOperations.getAdmissions(patient);
	}

	/**
	 * Method that returns the list of Admissions not logically deleted
	 * within the specified date range, divided by pages
	 *
	 * @param dateFrom
	 * @param dateTo
	 * @param page
	 * @param size
	 * @return {@link PagedResponse<Admission>}.
	 * @throws OHServiceException
	 */
	public PagedResponse<Admission> getAdmissionsPageable(LocalDateTime dateFrom, LocalDateTime dateTo, int page, int size) throws OHServiceException {
		return ioOperations.getAdmissionsByAdmissionDates(dateFrom, dateTo, PageRequest.of(page, size));
	}

	/**
	 * Method that returns the list of Admissions not logically deleted
	 * within the specified date range
	 *
	 * @param dateFrom
	 * @param dateTo
	 * @return the list of Admissions (could be empty)
	 * @throws OHServiceException
	 */
	public List<Admission> getAdmissionsByAdmissionDate(LocalDateTime dateFrom, LocalDateTime dateTo) throws OHServiceException {
		return ioOperations.getAdmissionsByAdmissionDate(dateFrom, dateTo);
	}

	/**
	 * Method that returns the list of completed Admissions (Discharges) not logically deleted
	 * within the specified date range, divided by pages
	 * @param dateFrom
	 * @param dateTo
	 * @param page
	 * @param size
	 * @return {@link PagedResponse<Admission>}
	 * @throws OHServiceException
	 */
	public PagedResponse<Admission> getDischargesPageable(LocalDateTime dateFrom, LocalDateTime dateTo, int page, int size) throws OHServiceException {
		return ioOperations.getAdmissionsByDischargeDates(dateFrom, dateTo, PageRequest.of(page, size));
	}

	/**
	 * Returns the next prog in the year for a certain ward.
	 *
	 * @param wardId the ward id.
	 * @return the next prog
	 * @throws OHServiceException
	 */
	public int getNextYProg(String wardId) throws OHServiceException {
		return ioOperations.getNextYProg(wardId);
	}

	/**
	 * Lists the {@link AdmissionType}s.
	 *
	 * @return the admission types  or {@code null} if the operation fails.
	 * @throws OHServiceException
	 */
	public List<AdmissionType> getAdmissionType() throws OHServiceException {
		return ioOperations.getAdmissionType();
	}

	/**
	 * Lists the {@link DischargeType}s.
	 *
	 * @return the discharge types  or {@code null} if the operation fails.
	 * @throws OHServiceException
	 */
	public List<DischargeType> getDischargeType() throws OHServiceException {
		return ioOperations.getDischargeType();
	}

	/**
	 * Inserts a new admission.
	 *
	 * @param admission the admission to insert.
	 * @return {@code true} if the admission has been successfully inserted, {@code false} otherwise.
	 * @throws OHServiceException
	 */
	public Admission newAdmission(Admission admission) throws OHServiceException {
		validateAdmission(admission, true);
		return ioOperations.newAdmission(admission);
	}

	/**
	 * Inserts a new {@link Admission} and the returns the generated id.
	 *
	 * @param admission the admission to insert.
	 * @return the generated id or {@code null} if the operation fails.
	 * @throws OHServiceException
	 */
	public int newAdmissionReturnKey(Admission admission) throws OHServiceException {
		validateAdmission(admission, true);
		return ioOperations.newAdmission(admission).getId();
	}

	/**
	 * Updates the specified {@link Admission} object.
	 *
	 * @param admission the admission object to update.
	 * @return {@code true} if has been updated, {@code false} otherwise.
	 * @throws OHServiceException
	 */
	public Admission updateAdmission(Admission admission) throws OHServiceException {
		validateAdmission(admission, false);
		return ioOperations.updateAdmission(admission);
	}

	/**
	 * Sets an admission record as deleted.
	 *
	 * @param admissionId the admission id.
	 * @return return the "deleted" admission or null if the admission is not found
	 * @throws OHServiceException
	 */
	public Admission setDeleted(int admissionId) throws OHServiceException {
		return ioOperations.setDeleted(admissionId);
	}

	/**
	 * Counts the number of used bed for the specified ward.
	 *
	 * @param wardId the ward id.
	 * @return the number of used beds.
	 * @throws OHServiceException
	 */
	public int getUsedWardBed(String wardId) throws OHServiceException {
		return ioOperations.getUsedWardBed(wardId);
	}

	/**
	 * Deletes the patient photo.
	 *
	 * @param id the patient id.
	 * @return the updated patient object or null if not found
	 * @throws OHServiceException
	 */
	public Patient deletePatientPhoto(int id) throws OHServiceException {
		return ioOperations.deletePatientPhoto(id);
	}

	/**
	 * Verify if the object is valid for CRUD and return a list of errors, if any
	 *
	 * @param admission
	 * @param insert {@code true} or updated {@code false}
	 * @throws OHDataValidationException
	 */
	protected void validateAdmission(Admission admission, boolean insert) throws OHServiceException {
		List<OHExceptionMessage> errors = new ArrayList<>();

		Ward ward = admission.getWard();
		if (ward == null) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.admission.admissionwardcannotbeempty.msg")));
		}

		LocalDateTime dateIn = admission.getAdmDate();
		if (dateIn == null) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.admission.admissiondatecannotbeempty.msg")));
		}

		Patient patient = admission.getPatient();
		if (patient == null) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.admission.admissionpatientcannotbeempty.msg")));
		}
		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}

		/*
		 * Initialize AdmissionBrowserManager
		 *
		 * ward, dateIn, and patient most all be resent for this next command to work
		 */
		List<Admission> admList = getAdmissions(patient);

		/*
		 * Today LocalDateTime
		 */
		LocalDateTime today = TimeTools.getDateToday24();
		// get year prog ( not null)
		if (admission.getYProg() < 0) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.admission.pleaseinsertacorrectprogressiveid.msg")));
		}
		if (dateIn.isAfter(today)) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.admission.futuredatenotallowed.msg")));
		}
		if (dateIn.isBefore(today)) {
			// check for invalid date
			for (Admission ad : admList) {
				if (!insert && ad.getId() == admission.getId()) {
					continue;
				}
				if ((ad.getAdmDate().isBefore(dateIn) || ad.getAdmDate().isEqual(dateIn))
								&& (ad.getDisDate() != null && ad.getDisDate().isAfter(dateIn))) {
					errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.admission.ininserteddatepatientwasalreadyadmitted.msg")));
				}
			}
		}
		Disease diseaseIn = admission.getDiseaseIn();
		if (diseaseIn == null) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.admission.diagnosisincannotbeempty.msg")));
		} else {
			Disease disease = diseaseManager.getIpdInDiseaseByCode(diseaseIn.getCode());
			if (disease == null) {
				errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.admission.diagnosisinisnotallowed.msg")));
			}
		}

		Admission last;
		if (!admList.isEmpty()) {
			last = admList.get(admList.size() - 1);
		} else {
			last = admission;
		}
		if (admission.getDisDate() == null && !insert && admission.getId() != last.getId()) {
			// if we are editing an old admission
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.admission.youareeditinganoldadmission.msg")));
		} else if (admission.getDisDate() != null) {
			LocalDateTime dateOut = admission.getDisDate();
			// date control
			if (dateOut.isBefore(dateIn)) {
				errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.admission.dischargedatemustbeafteradmissiondate.msg")));
			}
			if (dateOut.isAfter(today)) {
				errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.admission.futuredatenotallowed.msg")));
			} else {
				// check for invalid date
				boolean invalidDate = false;
				LocalDateTime invalidStart = TimeTools.getNow();
				LocalDateTime invalidEnd = TimeTools.getNow();
				for (Admission ad : admList) {
					// case current admission : let it be
					if (!insert && ad.getId() == admission.getId()) {
						continue;
					}
					// found an open admission
					// only if i close my own first of it
					if (ad.getDisDate() == null) {
						if (!dateOut.isAfter(ad.getAdmDate())) {
							// ok
						} else {
							errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.admission.intheselecteddatepatientwasadmittedagain.msg")));
						}
					}
					// general case
					else {
						// DateIn >= adOut
						if (dateIn.isAfter(ad.getDisDate()) || dateIn.equals(ad.getDisDate())) {
							// ok
						}
						// dateOut <= adIn
						else if (dateOut.isBefore(ad.getAdmDate()) || dateOut.equals(ad.getAdmDate())) {
							// ok
						} else {
							invalidDate = true;
							invalidStart = ad.getAdmDate();
							invalidEnd = ad.getDisDate();
							break;
						}
					}
				}
				if (invalidDate) {
					errors.add(new OHExceptionMessage(MessageBundle.formatMessage("angal.admission.invalidadmissionperiod.fmt.msg",
									DateTimeFormatter.ISO_LOCAL_DATE.format(invalidStart),
									DateTimeFormatter.ISO_LOCAL_DATE.format(invalidEnd))));
				}
			}
		}
		Disease diseaseOut1 = admission.getDiseaseOut1();
		Disease diseaseOut2 = admission.getDiseaseOut2();
		Disease diseaseOut3 = admission.getDiseaseOut3();
		if (diseaseOut1 == null && admission.getDisDate() != null) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.admission.pleaseselectatleastfirstdiagnosisout.msg")));
		} else if (diseaseOut1 != null && admission.getDisDate() == null) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.admission.pleaseinsertadischargedate.msg")));
		}
		if (admission.getDisDate() != null && diseaseOut1 != null) {

			// Check duplicated diseases
			if (checkDuplicatedDiseaseOut(diseaseOut1, diseaseOut2, diseaseOut3)) {
				errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.admission.specifyingduplicatediseasesisnotallowed.msg")));
			}

			Disease disease;
			if (diseaseOut1 != null) {
				disease = diseaseManager.getIpdOutDiseaseByCode(diseaseOut1.getCode());
				if (disease == null) {
					errors.add(new OHExceptionMessage(MessageBundle.formatMessage("angal.opd.specifieddiseaseisnoenabledforopdservice.fmt.msg", "1")));
				}
			}
			if (diseaseOut2 != null) {
				disease = diseaseManager.getIpdOutDiseaseByCode(diseaseOut2.getCode());
				if (disease == null) {
					errors.add(new OHExceptionMessage(MessageBundle.formatMessage("angal.opd.specifieddiseaseisnoenabledforopdservice.fmt.msg", "2")));
				}
			}
			if (diseaseOut3 != null) {
				disease = diseaseManager.getIpdOutDiseaseByCode(diseaseOut3.getCode());
				if (disease == null) {
					errors.add(new OHExceptionMessage(MessageBundle.formatMessage("angal.opd.specifieddiseaseisnoenabledforopdservice.fmt.msg", "3")));
				}
			}
		}

		Float f = admission.getWeight();
		if (f != null && f < 0.0f) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.admission.pleaseinsertavalidweightvalue.msg")));
		}

		if (ward != null && "M".equalsIgnoreCase(ward.getCode())) {

			LocalDateTime visitDate = admission.getVisitDate();
			if (visitDate != null) {
				LocalDateTime limit;
				if (admission.getDisDate() == null) {
					limit = today;
				} else {

					limit = admission.getDisDate();
				}
				if (visitDate.isBefore(dateIn) || visitDate.isAfter(limit)) {
					errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.admission.pleaseinsertavalidvisitdate.msg")));
				}
			}

			if (admission.getDeliveryDate() != null) {
				LocalDateTime deliveryDate = admission.getDeliveryDate();

				// date control
				LocalDateTime start;
				if (admission.getVisitDate() == null) {
					start = admission.getAdmDate();
				} else {
					start = admission.getVisitDate();
				}

				LocalDateTime limit;
				if (admission.getDisDate() == null) {
					limit = today;
				} else {
					limit = admission.getDisDate();
				}

				if (deliveryDate.isBefore(start) || deliveryDate.isAfter(limit)) {
					errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.admission.pleaseinsertavaliddeliverydate.msg")));
				}
			}

			LocalDateTime ctrl1Date = admission.getCtrlDate1();
			if (ctrl1Date != null) {
				// date control
				if (admission.getDeliveryDate() == null) {
					errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.admission.controln1datenodeliverydatefound.msg")));
				} else {
					LocalDateTime limit;
					if (admission.getDisDate() == null) {
						limit = today;
					} else {
						limit = admission.getDisDate();
					}
					if (ctrl1Date.isBefore(admission.getDeliveryDate()) || ctrl1Date.isAfter(limit)) {
						errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.admission.pleaseinsertavalidcontroln1date.msg")));
					}
				}
			}

			LocalDateTime ctrl2Date = admission.getCtrlDate2();
			if (ctrl2Date != null) {
				if (ctrl1Date == null) {
					errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.admission.controldaten2controldaten1notfound.msg")));
				} else {
					// date control
					LocalDateTime limit;
					if (admission.getDisDate() == null) {
						limit = today;
					} else {
						limit = admission.getDisDate();
					}
					if (ctrl1Date != null && (ctrl2Date.isBefore(ctrl1Date) || ctrl2Date.isAfter(limit))) {
						errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.admission.pleaseinsertavalidcontroln2date.msg")));
					}
				}
			}
			LocalDateTime abortDate = admission.getAbortDate();
			if (abortDate != null) {
				// date control
				LocalDateTime limit;
				if (admission.getDisDate() == null) {
					limit = today;
				} else {
					limit = admission.getDisDate();
				}
				if (ctrl2Date != null && abortDate.isBefore(ctrl2Date) || ctrl1Date != null && abortDate.isBefore(ctrl1Date)
								|| abortDate.isBefore(visitDate)
								|| abortDate.isAfter(limit)) {
					errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.admission.pleaseinsertavalidabortdate.msg")));
				}
			}
		}
		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
	}

	private boolean checkDuplicatedDiseaseOut(Disease diseaseOut1, Disease diseaseOut2, Disease diseaseOut3) {
		return (diseaseOut2 != null && diseaseOut1.getCode().equals(diseaseOut2.getCode()))
						|| (diseaseOut3 != null && diseaseOut1.getCode().equals(diseaseOut3.getCode()))
						|| (diseaseOut2 != null && diseaseOut3 != null && diseaseOut2.getCode().equals(diseaseOut3.getCode()));
	}
}
