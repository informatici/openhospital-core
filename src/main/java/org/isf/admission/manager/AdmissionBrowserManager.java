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
package org.isf.admission.manager;

import org.isf.admission.model.Admission;
import org.isf.admission.model.AdmittedPatient;
import org.isf.admission.service.AdmissionIoOperations;
import org.isf.admtype.model.AdmissionType;
import org.isf.disctype.model.DischargeType;
import org.isf.generaldata.GeneralData;
import org.isf.generaldata.MessageBundle;
import org.isf.patient.model.Patient;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;
import java.util.List;
import java.util.Locale;

@Component
public class AdmissionBrowserManager {

	@Autowired
	private AdmissionIoOperations ioOperations;

	/**
	 * Returns all patients with ward in which they are admitted.
	 * @return the patient list with associated ward or <code>null</code> if the operation fails.
	 * @throws OHServiceException 
	 */
	public ArrayList<AdmittedPatient> getAdmittedPatients() throws OHServiceException{
        return new ArrayList<>(ioOperations.getAdmittedPatients());
	}

	/**
	 * Returns all patients with ward in which they are admitted filtering the list using the passed search term.
	 * @param searchTerms the search terms to use for filter the patient list, <code>null</code> if no filter have to be applied.
	 * @return the filtered patient list or <code>null</code> if the operation fails.
	 * @throws OHServiceException 
	 */
	public ArrayList<AdmittedPatient> getAdmittedPatients(String searchTerms) throws OHServiceException{
        return new ArrayList<>(ioOperations.getAdmittedPatients(searchTerms));
	}
	
	/**
	 * Returns all patients based on the applied filters.
	 * @param admissionRange the patient admission range
	 * @param dischargeRange the patient discharge range
	 * @param searchTerms the search terms to use for filter the patient list, <code>null</code> if no filter have to be applied.
	 * @return the filtered patient list.
	 * @throws OHServiceException if an error occurs during database request.
	 */
	public ArrayList<AdmittedPatient> getAdmittedPatients(LocalDateTime[] admissionRange, //
			LocalDateTime[] dischargeRange, String searchTerms) throws OHServiceException{
		return new ArrayList<>(ioOperations.getAdmittedPatients(searchTerms, admissionRange, dischargeRange));
	}

	public AdmittedPatient loadAdmittedPatients(final Integer patientId) {
		return ioOperations.loadAdmittedPatient(patientId);
	}

	/**
	 * Returns the admission with the selected id.
	 * @param id the admission id.
	 * @return the admission with the specified id, <code>null</code> otherwise.
	 * @throws OHServiceException 
	 */
	public Admission getAdmission(int id) throws OHServiceException{
        return ioOperations.getAdmission(id);
	}

	/**
	 * Returns the only one admission without adimission date (or null if none) for the specified patient.
	 * @param patient the patient target of the admission.
	 * @return the patient admission or <code>null</code> if the operation fails.
	 * @throws OHServiceException 
	 */
	public Admission getCurrentAdmission(Patient patient) throws OHServiceException{
        return ioOperations.getCurrentAdmission(patient);
	}

	/**
	 * Returns all the admissions for the specified patient.
	 * @param patient the patient.
	 * @return the admission list or <code>null</code> if the operation fails.
	 * @throws OHServiceException 
	 */
	public ArrayList<Admission> getAdmissions(Patient patient) throws OHServiceException{
        return ioOperations.getAdmissions(patient);
	}

	/**
	 * Returns the next prog in the year for a certain ward.
	 * @param wardId the ward id.
	 * @return the next prog
	 * @throws OHServiceException 
	 */
	public int getNextYProg(String wardId) throws OHServiceException{
        return ioOperations.getNextYProg(wardId);
	}

	/**
	 * Lists the {@link AdmissionType}s.
	 * @return the admission types  or <code>null</code> if the operation fails.
	 * @throws OHServiceException 
	 */
	public ArrayList<AdmissionType> getAdmissionType() throws OHServiceException{	
        return ioOperations.getAdmissionType();
	}

	/**
	 * Lists the {@link DischargeType}s.
	 * @return the discharge types  or <code>null</code> if the operation fails.
	 * @throws OHServiceException 
	 */
	public ArrayList<DischargeType> getDischargeType() throws OHServiceException{	
        return ioOperations.getDischargeType();
	}

	/**
	 * Inserts a new admission.
	 * @param admission the admission to insert.
	 * @return <code>true</code> if the admission has been successfully inserted, <code>false</code> otherwise.
	 * @throws OHServiceException 
	 */
	public boolean newAdmission(Admission admission) throws OHServiceException{
        validateAdmission(admission, true);
        return ioOperations.newAdmission(admission);
	}

	/**
	 * Inserts a new {@link Admission} and the returns the generated id.
	 * @param admission the admission to insert.
	 * @return the generated id or <code>null</code> if the operation fails.
	 * @throws OHServiceException 
	 */
	public int newAdmissionReturnKey(Admission admission) throws OHServiceException{
		validateAdmission(admission, true);
        return ioOperations.newAdmissionReturnKey(admission);
	}

	/**
	 * Updates the specified {@link Admission} object.
	 * @param admission the admission object to update.
	 * @return <code>true</code> if has been updated, <code>false</code> otherwise.
	 * @throws OHServiceException 
	 */
	public boolean updateAdmission(Admission admission) throws OHServiceException{
		validateAdmission(admission, false);
        return ioOperations.updateAdmission(admission);
	}

	/**
	 * Sets an admission record to deleted.
	 * @param admissionId the admission id.
	 * @return <code>true</code> if the record has been set to delete.
	 * @throws OHServiceException 
	 */
	public boolean setDeleted(int admissionId) throws OHServiceException{
        return ioOperations.setDeleted(admissionId);
	}

	/**
	 * Counts the number of used bed for the specified ward.
	 * @param wardId the ward id.
	 * @return the number of used beds.
	 * @throws OHServiceException 
	 */
	public int getUsedWardBed(String wardId) throws OHServiceException {
        return ioOperations.getUsedWardBed(wardId);
	}

	/**
	 * Deletes the patient photo.
	 * @param id the patient id.
	 * @return <code>true</code> if the photo has been deleted, <code>false</code> otherwise.
	 * @throws OHServiceException 
	 */
	public boolean deletePatientPhoto(int id) throws OHServiceException {
        return ioOperations.deletePatientPhoto(id);
	}

	/**
	 * Verify if the object is valid for CRUD and return a list of errors, if any
	 * @param admission
	 * @param insert <code>true</code> or updated <code>false</code>
	 * @throws OHDataValidationException
	 */
    protected void validateAdmission(Admission admission, boolean insert) throws OHServiceException {
        List<OHExceptionMessage> errors = new ArrayList<OHExceptionMessage>();

        /*
         * Initialize AdmissionBrowserManager
         */
        Patient patient = admission.getPatient();
        ArrayList<Admission> admList = getAdmissions(patient);

        /*
         * Today Gregorian Calendar
         */
        LocalDateTime today = LocalDateTime.now();
        // get year prog ( not null)
        if (admission.getYProg() < 0) {
            errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.hospital"), MessageBundle.getMessage("angal.admission.pleaseinsertacorrectprogressiveid"),
                    OHSeverityLevel.ERROR));
        }

        LocalDateTime dateIn = admission.getAdmDate();
        if (dateIn.isAfter(today)) {
            errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.hospital"), MessageBundle.getMessage("angal.admission.futuredatenotallowed"),
                    OHSeverityLevel.ERROR));
        }
        if (dateIn.isBefore(today)) {
            // check for invalid date
            for (Admission ad : admList) {
                if (!insert && ad.getId() == admission.getId()) {
                    continue;
                }
                if ((ad.getAdmDate().isBefore(dateIn) || ad.getAdmDate().compareTo(dateIn) == 0)
                        && (ad.getDisDate() != null && ad.getDisDate().isAfter(dateIn))) {
                    errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.hospital"), MessageBundle.getMessage("angal.admission.ininserteddatepatientwasalreadyadmitted"),
                            OHSeverityLevel.ERROR));
                }
            }
        }

        Admission last = null;
        if (admList.size() > 0) {
            last = admList.get(admList.size() - 1);
        } else {
            last = admission;
        }
        if (admission.getDisDate() == null && !insert && admission.getId() != last.getId()) {
            // if we are editing an old admission
            errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.hospital"), MessageBundle.getMessage("angal.admission.youareeditinganoldadmission"),
                    OHSeverityLevel.ERROR));
        }
        else if (admission.getDisDate() != null){
            LocalDateTime dateOut = admission.getDisDate();
            // date control
            if (dateOut.isBefore(dateIn)) {
                errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.hospital"), MessageBundle.getMessage("angal.admission.dischargedatemustbeafteradmissiondate"),
                        OHSeverityLevel.ERROR));
            }
            if (dateOut.isAfter(today)) {
                errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.hospital"), MessageBundle.getMessage("angal.admission.futuredatenotallowed"),
                        OHSeverityLevel.ERROR));
            } else {
                // check for invalid date
                boolean invalidDate = false;
                LocalDateTime invalidStart = LocalDateTime.now();
                LocalDateTime invalidEnd = LocalDateTime.now();
                for (Admission ad : admList) {
                    // case current admission : let it be
                    if (!insert && ad.getId() == admission.getId()) {
                        continue;
                    }
                    // found an open admission
                    // only if i close my own first of it
                    if (ad.getDisDate() == null) {
                        if (!dateOut.isAfter(ad.getAdmDate()))
                            ;// ok
                        else {
                            errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.hospital"), MessageBundle.getMessage("angal.admission.intheselecteddatepatientwasadmittedagain"),
                                    OHSeverityLevel.ERROR));
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
                    errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.hospital"), MessageBundle.getMessage("angal.admission.invalidadmissionperiod") + MessageBundle.getMessage("angal.admission.theadmissionbetween") + " "
                            + DateTimeFormatter.ISO_LOCAL_DATE.format(invalidStart) + " " + MessageBundle.getMessage("angal.admission.and") + " " + DateTimeFormatter.ISO_LOCAL_DATE.format(invalidEnd) + " "
                            + MessageBundle.getMessage("angal.admission.alreadyexists"),
                            OHSeverityLevel.ERROR));
                }

            }

            java.time.LocalDateTime operationDate = admission.getOpDate();
            if(operationDate != null) {
                LocalDateTime limit;
                if (admission.getDisDate() == null) {
                    limit = today;
                } else {
                    limit = admission.getDisDate();
                }

                if (operationDate.isBefore(dateIn) || operationDate.isAfter(limit)) {
                    errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.hospital"), MessageBundle.getMessage("angal.admission.pleaseinsertavalidvisitdate"),
                            OHSeverityLevel.ERROR));
                }
            }

            if (admission.getDiseaseOut1() == null && admission.getDisDate() != null) {
                errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.hospital"), MessageBundle.getMessage("angal.admission.pleaseselectatleastfirstdiagnosisout"),
                        OHSeverityLevel.ERROR));
            }else if (admission.getDiseaseOut1() != null && admission.getDisDate() == null) {
                errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.hospital"), MessageBundle.getMessage("angal.admission.pleaseinsertadischargedate"),
                        OHSeverityLevel.ERROR));
            }

            LocalDateTime visitDate = admission.getVisitDate();
            if (admission.getWard().getCode().equalsIgnoreCase("M")) {
                LocalDateTime limit;
                if (admission.getDisDate() == null) {
                    limit = today;
                } else {
                    limit = admission.getDisDate();
                }


            }

            Float f = admission.getWeight();
            if (f != null && f < 0.0f) {
                errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.hospital"),  MessageBundle.getMessage("angal.admission.pleaseinsertavalidweightvalue"),
                        OHSeverityLevel.ERROR));
            }

            if(admission.getDeliveryDate() != null){
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
                    errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.hospital"), MessageBundle.getMessage("angal.admission.pleaseinsertavaliddeliverydate"),
                            OHSeverityLevel.ERROR));
                }
            }

            LocalDateTime ctrl1Date = admission.getCtrlDate1();
            if(ctrl1Date != null){
                // date control
                if (admission.getDeliveryDate() == null) {
                    errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.hospital"), MessageBundle.getMessage("angal.admission.controln1datenodeliverydatefound"),
                            OHSeverityLevel.ERROR));
                }
                LocalDateTime limit;
                if (admission.getDisDate() == null) {
                    limit = today;
                } else {
                    limit = admission.getDisDate();
                }
                if (ctrl1Date.isBefore(admission.getDeliveryDate()) || ctrl1Date.isAfter(limit)) {
                    errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.hospital"), MessageBundle.getMessage("angal.admission.pleaseinsertavalidcontroln1date"),
                            OHSeverityLevel.ERROR));
                }
            }

            LocalDateTime ctrl2Date = admission.getCtrlDate2();
            if(ctrl2Date != null){
                if (admission.getCtrlDate1() == null) {
                    errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.hospital"), MessageBundle.getMessage("angal.admission.controldaten2controldaten1notfound"),
                            OHSeverityLevel.ERROR));
                }
                // date control
                LocalDateTime limit;
                if (admission.getDisDate() == null) {
                    limit = today;
                } else {
                    limit = admission.getDisDate();
                }
                if (ctrl2Date.isBefore(ctrl1Date) || ctrl2Date.isAfter(limit)) {
                    errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.hospital"), MessageBundle.getMessage("angal.admission.pleaseinsertavalidcontroln2date"),
                            OHSeverityLevel.ERROR));
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
                if (ctrl2Date != null && abortDate.isBefore(ctrl2Date) || ctrl1Date != null && abortDate.isBefore(ctrl1Date) || abortDate.isBefore(visitDate) || abortDate.isAfter(limit)) {
                    errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.hospital"), MessageBundle.getMessage("angal.admission.pleaseinsertavalidabortdate"),
                            OHSeverityLevel.ERROR));
                }
            }
        }
        if (!errors.isEmpty()){
	        throw new OHDataValidationException(errors);
	    }
    }
}
