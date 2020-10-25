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
package org.isf.admission.service;

/*----------------------------------------------------------
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
 *-----------------------------------------------------------*/

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

import java.time.LocalDateTime;
import java.time.Month;

import static java.time.temporal.TemporalAdjusters.firstDayOfYear;
import static java.time.temporal.TemporalAdjusters.lastDayOfYear;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
	 * @throws OHServiceException if an error occurs during database request.
	 */
	public List<AdmittedPatient> getAdmittedPatients() throws OHServiceException {
		return getAdmittedPatients(null);
	}

	/**
	 * Returns all patients with ward in which they are admitted filtering the list using the passed search term.
	 * @param searchTerms the search terms to use for filter the patient list, <code>null</code> if no filter have to be applied.
	 * @return the filtered patient list.
	 * @throws OHServiceException if an error occurs during database request.
	 */
	public List<AdmittedPatient> getAdmittedPatients(String searchTerms) throws OHServiceException {
		return patientRepository.findByFieldsContainingWordsFromLiteral(searchTerms).stream()
			.map(patient -> new AdmittedPatient(patient, repository.findOneWherePatientIn(patient.getCode())))
			.collect(Collectors.toList());
	}

	/**
	 * Returns all patients based on the applied filters.
	 * @param admissionRange the patient admission range
	 * @param dischargeRange the patient discharge range
	 * @param searchTerms the search terms to use for filter the patient list, <code>null</code> if no filter have to be applied.
	 * @return the filtered patient list.
	 * @throws OHServiceException if an error occurs during database request.
	 */
	public List<AdmittedPatient> getAdmittedPatients(
			String searchTerms, LocalDateTime[] admissionRange,
			LocalDateTime[] dischargeRange) throws OHServiceException {
		return patientRepository.findByFieldsContainingWordsFromLiteral(searchTerms).stream()
			.map(patient -> new AdmittedPatient(patient, repository.findOneByPatientAndDateRanges(patient, admissionRange, dischargeRange).orElse(null)))
			.filter(admittedPatient -> admittedPatient.getPatient()!= null && admittedPatient.getAdmission() != null)
			.collect(Collectors.toList());
	}

	/**
	 * Load patient together with the profile photo, or <code>null</code> if there is no patient with the given id
	 */
	public AdmittedPatient loadAdmittedPatient(final Integer patientId) {
		final Patient patient = patientRepository.findById(patientId).orElse(null);
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
	 * @throws OHServiceException if an error occurs during database request.
	 */
	public Admission getCurrentAdmission(Patient patient) throws OHServiceException	{
		return repository.findOneWherePatientIn(patient.getCode());
	}

	/**
	 * Returns the admission with the selected id.
	 * @param id the admission id.
	 * @return the admission with the specified id, <code>null</code> otherwise.
	 * @throws OHServiceException if an error occurs during database request.
	 */
	public Admission getAdmission(int id) throws OHServiceException {
		return repository.findById(id).orElse(null);
	}

	/**
	 * Returns all the admissions for the specified patient.
	 * @param patient the patient.
	 * @return the admission list.
	 * @throws OHServiceException if an error occurs during database request.
	 */
	public ArrayList<Admission> getAdmissions(Patient patient) throws OHServiceException {
		return  (ArrayList<Admission>) repository.findAllWherePatientByOrderByDate(patient.getCode());
	}
	
	/**
	 * Inserts a new admission.
	 * @param admission the admission to insert.
	 * @return <code>true</code> if the admission has been successfully inserted, <code>false</code> otherwise.
	 * @throws OHServiceException if an error occurs during the insertion.
	 */
	public boolean newAdmission(
			Admission admission) throws OHServiceException 
	{
		boolean result = true;
	

		Admission savedAdmission = repository.save(admission);
		result = (savedAdmission != null);
		
		return result;
	}

	/**
	 * Inserts a new {@link Admission} and the returns the generated id.
	 * @param admission the admission to insert.
	 * @return the generated id.
	 * @throws OHServiceException if an error occurs during the insertion.
	 */
	public int newAdmissionReturnKey(
			Admission admission) throws OHServiceException 
	{
		newAdmission(admission);
		
		return admission.getId();
	}

	/**
	 * Updates the specified {@link Admission} object.
	 * @param admission the admission object to update.
	 * @return <code>true</code> if has been updated, <code>false</code> otherwise.
	 * @throws OHServiceException if an error occurs.
	 */
	public boolean updateAdmission(
			Admission admission) throws OHServiceException 
	{
		boolean result = true;
	

		Admission savedAdmission = repository.save(admission);
		result = (savedAdmission != null);
		
		return result;
	}

	/**
	 * Lists the {@link AdmissionType}s.
	 * @return the admission types.
	 * @throws OHServiceException 
	 */
	public ArrayList<AdmissionType> getAdmissionType() throws OHServiceException 
	{
		ArrayList<AdmissionType> padmissiontype = (ArrayList<AdmissionType>) typeRepository.findAll();

		
		return padmissiontype;
	}

	/**
	 * Lists the {@link DischargeType}s.
	 * @return the discharge types.
	 * @throws OHServiceException 
	 */
	public ArrayList<DischargeType> getDischargeType() throws OHServiceException 
	{
		ArrayList<DischargeType> dischargeTypes = (ArrayList<DischargeType>) dischargeRepository.findAll();
				
		return dischargeTypes;
	}

    
	/**
	 * Returns the next prog in the year for a certain ward.
	 * @param wardId the ward id.
	 * @return the next prog.
	 * @throws OHServiceException if an error occurs retrieving the value.
	 */
	public int getNextYProg(
			String wardId) throws OHServiceException 
	{
		int next = 1;
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime first = null;
		LocalDateTime last = null;
		
		if (wardId.equalsIgnoreCase("M") && GeneralData.MATERNITYRESTARTINJUNE) 
		{
			if (now.getMonthValue() < Month.JUNE.getValue()) 
			{
				first = now.minusYears(1).withMonth(Month.JULY.getValue()).withDayOfMonth(1);
				last = now.withMonth(Month.JUNE.getValue()).withDayOfMonth(30);
			} 
			else 
			{
				first = now.withMonth(Month.JULY.getValue()).withDayOfMonth(1);
				last = now.plusYears(1).withMonth(Month.JUNE.getValue()).withDayOfMonth(30);
			}

		} 
		else 
		{
			first = now.with(firstDayOfYear());
			last = now.with(lastDayOfYear());
		}
		
		List<Admission> admissions = repository.findAllWhereWardAndDates(wardId, first, last);
		if (!admissions.isEmpty())
		{
			next = admissions.get(0).getYProg() + 1; 		
		} 
		
		return next;
	}

	/**
	 * Sets an admission record to deleted.
	 * @param admissionId the admission id.
	 * @return <code>true</code> if the record has been set to delete.
	 * @throws OHServiceException if an error occurs.
	 */
	public boolean setDeleted(
			int admissionId) throws OHServiceException 
	{
		boolean result = true;
		
		
		Admission foundAdmission = repository.findById(admissionId).orElse(null);
		foundAdmission.setDeleted("Y");
		Admission savedAdmission = repository.save(foundAdmission);
		result = (savedAdmission != null);    	
    	
		return result;
	}

	/**
	 * Counts the number of used bed for the specified ward.
	 * @param wardId the ward id.
	 * @return the number of used beds.
	 * @throws OHServiceException if an error occurs retrieving the bed count.
	 */
	public int getUsedWardBed(
			String wardId) throws OHServiceException 
	{
    	List<Admission> admissionList = repository.findAllWhereWardIn(wardId);
		

		return admissionList.size();
	}

	/**
	 * Deletes the patient photo.
	 * @param patientId the patient id.
	 * @return <code>true</code> if the photo has been deleted, <code>false</code> otherwise.
	 * @throws OHServiceException if an error occurs.
	 */
	public boolean deletePatientPhoto(
			int patientId) throws OHServiceException
	{
		boolean result = true;
		
		
		Patient foundPatient = patientRepository.findById(patientId).orElse(null);
		if (foundPatient.getPatientProfilePhoto() != null && foundPatient.getPatientProfilePhoto().getPhoto() != null) {
			foundPatient.getPatientProfilePhoto().setPhoto(null);
		}
        Patient savedPatient = patientRepository.save(foundPatient);
		result = (savedPatient != null);    
		
		return result;
	}
}
