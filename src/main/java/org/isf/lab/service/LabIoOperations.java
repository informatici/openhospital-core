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
package org.isf.lab.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.isf.lab.model.Laboratory;
import org.isf.lab.model.LaboratoryForPrint;
import org.isf.lab.model.LaboratoryRow;
import org.isf.patient.model.Patient;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.time.TimeTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ------------------------------------------
 * lab.service.LabIoOperations - laboratory exam database io operations
 * -----------------------------------------
 * modification history
 * 02/03/2006 - theo - first beta version
 * 10/11/2006 - ross - added editing capability.
 * 					   new fields data esame, sex, age, material, inout flag added
 * 21/06/2008 - ross - do not add 1 to toDate!.
 *                     the selection date switched to exam date,
 * 04/01/2009 - ross - do not use roll, use add(week,-1)!
 *                     roll does not change the year!
 * 16/11/2012 - mwithi - added logging capability
 * 					   - to do lock management
 * 04/02/2013 - mwithi - lock management done
 * ------------------------------------------
 */
@Service
@Transactional(rollbackFor=OHServiceException.class)
@TranslateOHServiceException
public class LabIoOperations {

	@Autowired
	private LabIoOperationRepository repository;
        
	@Autowired
	private LabRowIoOperationRepository rowRepository;
	
	/**
	 * Return a list of results ({@link LaboratoryRow}s) for passed lab entry.
	 * @param code - the {@link Laboratory} record ID.
	 * @return the list of {@link LaboratoryRow}s. It could be <code>empty</code>
	 * @throws OHServiceException
	 */
	public List<LaboratoryRow> getLabRow(Integer code) throws OHServiceException	{
		return rowRepository.findByLaboratory_Code(code);
	}
	
	/**
	 * Return the whole list of exams ({@link Laboratory}s) within last year.
	 * @return the list of {@link Laboratory}s 
	 * @throws OHServiceException
	 */
	public List<Laboratory> getLaboratory() throws OHServiceException {
		LocalDateTime time2 = TimeTools.getDateToday24();
		LocalDateTime time1 = time2.minusWeeks(1);
		return getLaboratory(null, time1, time2);
	}

	/**
	 * Return a list of exams ({@link Laboratory}s) between specified dates and matching passed exam name
	 * @param exam - the exam name as <code>String</code>
	 * @param dateFrom - the lower date for the range
	 * @param dateTo - the highest date for the range
	 * @return the list of {@link Laboratory}s 
	 * @throws OHServiceException
	 */
	public List<Laboratory> getLaboratory(String exam, LocalDateTime dateFrom, LocalDateTime dateTo) throws OHServiceException {
		return exam != null ?
				repository.findByLabDateBetweenAndExam_DescriptionOrderByLabDateDesc(TimeTools.truncateToSeconds(dateFrom),
				                                                                     TimeTools.truncateToSeconds(dateTo),
				                                                                     exam) :
				repository.findByCreatedDateBetweenOrderByLabDateDesc(TimeTools.truncateToSeconds(dateFrom.with(LocalTime.MIN)),
				                                                      TimeTools.truncateToSeconds(dateTo.with(LocalTime.MAX)));
	}
	
	/**
	 * Return a list of exams ({@link Laboratory}s) between specified dates and matching passed exam name
	 * @param exam - the exam name as <code>String</code>
	 * @param dateFrom - the lower date for the range
	 * @param dateTo - the highest date for the range
	 * @param patient - the object of patient 
	 * @return the list of {@link Laboratory}s 
	 * @throws OHServiceException
	 */
	public List<Laboratory> getLaboratory(String exam, LocalDateTime dateFrom, LocalDateTime dateTo, Patient patient) throws OHServiceException {
		List<Laboratory> laboritories = new ArrayList<>();
		
		if (!exam.equals("") && patient != null) {
			laboritories = repository.findByLabDateBetweenAndExamDescriptionAndPatientCode(dateFrom, dateTo, exam, patient.getCode());
		}
		if (!exam.equals("") && patient == null ) {
			laboritories = repository.findByLabDateBetweenAndExam_DescriptionOrderByLabDateDesc(dateFrom, dateTo, exam);
		}
		if (patient != null && exam.equals("")) {
			laboritories = repository.findByLabDateBetweenAndPatientCode(dateFrom, dateTo, patient.getCode());
		}
		if (patient == null && exam.equals("")) {
			laboritories= repository.findByCreatedDateBetweenOrderByLabDateDesc(dateFrom, dateTo);
		}
		return laboritories;
	}
	
	/**
	 * Return a list of exams ({@link Laboratory}s) related to a {@link Patient}.
	 * @param aPatient - the {@link Patient}.
	 * @return the list of {@link Laboratory}s related to the {@link Patient}.
	 * @throws OHServiceException
	 */
	public List<Laboratory> getLaboratory(Patient aPatient) throws OHServiceException {
		return repository.findByPatient_CodeOrderByLabDate(aPatient.getCode());
	}
	
	/**
	 * Return a list of exams suitable for printing ({@link LaboratoryForPrint}s) 
	 * within last year
	 * @return the list of {@link LaboratoryForPrint}s 
	 * @throws OHServiceException
	 */
	public List<LaboratoryForPrint> getLaboratoryForPrint() throws OHServiceException {
		LocalDateTime time2 = TimeTools.getNow();
		LocalDateTime time1 = time2.minusWeeks(1);
		return getLaboratoryForPrint(null, time1, time2);
	}
	
	/**
	 * Return a list of exams suitable for printing ({@link LaboratoryForPrint}s) 
	 * between specified dates and matching passed exam name
	 * @param exam - the exam name as <code>String</code>
	 * @param dateFrom - the lower date for the range
	 * @param dateTo - the highest date for the range
	 * @return the list of {@link LaboratoryForPrint}s 
	 * @throws OHServiceException
	 */
	public List<LaboratoryForPrint> getLaboratoryForPrint(String exam, LocalDateTime dateFrom, LocalDateTime dateTo, Patient patient) throws OHServiceException {
		List<LaboratoryForPrint> pLaboratory = new ArrayList<>();
		List<Laboratory> laboritories = new ArrayList<>();
			if (exam != null && patient != null) {
				laboritories = repository.findByLabDateBetweenAndExamDescriptionAndPatientCode(dateFrom, dateTo, exam, patient.getCode());
			}
			if (exam != null && patient == null ) {
				laboritories = repository.findByLabDateBetweenAndExam_DescriptionOrderByLabDateDesc(dateFrom, dateTo, exam);
			}
			if (patient != null && exam == null) {
				laboritories = repository.findByLabDateBetweenAndPatientCode(dateFrom, dateTo, patient.getCode());
			}
			if (patient == null && exam == null) {
				laboritories= repository.findByCreatedDateBetweenOrderByLabDateDesc(dateFrom, dateTo);
			}
		for (Laboratory laboratory : laboritories) {
			
			pLaboratory.add(new LaboratoryForPrint(
							laboratory.getCode(),
							laboratory.getExam(),
							laboratory.getDate(),
							laboratory.getResult(),
							laboratory.getPatName(),
							laboratory.getPatient().getCode()
					)
			);
		}
		return pLaboratory;
	}
	
	/**
	 * Insert a Laboratory exam {@link Laboratory} and return generated key. No commit is performed.
	 * @param laboratory - the {@link Laboratory} to insert
	 * @return the generated key
	 * @throws OHServiceException
	 */
	private Integer newLaboratory(Laboratory laboratory) throws OHServiceException {
		Laboratory savedLaboratory = repository.save(laboratory);
		return savedLaboratory.getCode();
	}
	
	/**
	 * Inserts one Laboratory exam {@link Laboratory} (Procedure One)
	 * @param laboratory - the {@link Laboratory} to insert
	 * @return <code>true</code> if the exam has been inserted, <code>false</code> otherwise
	 * @throws OHServiceException
	 */
	public boolean newLabFirstProcedure(Laboratory laboratory) throws OHServiceException {
		return newLaboratory(laboratory) > 0;
	}

	/**
	 * Inserts one Laboratory exam {@link Laboratory} with multiple results (Procedure Two)
	 *
	 * @param laboratory - the {@link Laboratory} to insert
	 * @param labRow - the list of results ({@link String}s)
	 * @return <code>true</code> if the exam has been inserted with all its results, <code>false</code> otherwise
	 * @throws OHServiceException
	 */
	public boolean newLabSecondProcedure(Laboratory laboratory, List<String> labRow) throws OHServiceException {
		boolean result = true;

		int newCode = newLaboratory(laboratory);
		if (newCode > 0) {
			for (String aLabRow : labRow) {
				LaboratoryRow laboratoryRow = new LaboratoryRow();
				laboratoryRow.setLabId(laboratory);
				laboratoryRow.setDescription(aLabRow);

				LaboratoryRow savedLaboratoryRow = rowRepository.save(laboratoryRow);
				result = result && (savedLaboratoryRow != null);
			}
		}
		return result;
	}
	/**
	 * Return a list of exams suitable for printing ({@link LaboratoryForPrint}s) 
	 * within last year
	 * @return the list of {@link LaboratoryForPrint}s 
	 * @throws OHServiceException
	 */
	public List<LaboratoryForPrint> getLaboratoryForPrints() throws OHServiceException {
		LocalDateTime time2 = TimeTools.getNow();
		LocalDateTime time1 = time2.minusWeeks(1);
		return getLaboratoryForPrint(null, time1, time2);
	}
	
	/**
	 * Return a list of exams suitable for printing ({@link LaboratoryForPrint}s) 
	 * between specified dates and matching passed exam name
	 * @param exam - the exam name as <code>String</code>
	 * @param dateFrom - the lower date for the range
	 * @param dateTo - the highest date for the range
	 * @return the list of {@link LaboratoryForPrint}s 
	 * @throws OHServiceException
	 */
	public List<LaboratoryForPrint> getLaboratoryForPrint(String exam, LocalDateTime dateFrom, LocalDateTime dateTo) throws OHServiceException {
		List<LaboratoryForPrint> pLaboratory = new ArrayList<>();
		Iterable<Laboratory> laboritories = exam != null
				? repository.findByLabDateBetweenAndExam_DescriptionContainingOrderByExam_Examtype_DescriptionDesc(TimeTools.truncateToSeconds(dateFrom),
				                                                                                                   TimeTools.truncateToSeconds(dateTo),
				                                                                                                   exam)
				: repository.findByLabDateBetweenOrderByExam_Examtype_DescriptionDesc(TimeTools.truncateToSeconds(dateFrom),
				                                                                      TimeTools.truncateToSeconds(dateTo));

		for (Laboratory laboratory : laboritories) {
			pLaboratory.add(new LaboratoryForPrint(
							laboratory.getCode(),
							laboratory.getExam(),
							laboratory.getDate(),
							laboratory.getResult()
					)
			);
		}
		return pLaboratory;
	}
	/**
	 * Inserts one Laboratory exam {@link Laboratory} with multiple results (Procedure Two)
	 *
	 * @param laboratory - the {@link Laboratory} to insert
	 * @param labRow - the list of results ({@link String}s)
	 * @return <code>true</code> if the exam has been inserted with all its results, <code>false</code> otherwise
	 * @throws OHServiceException
	 */
	public boolean newLabSecondProcedure2(Laboratory laboratory, List<LaboratoryRow> labRow) throws OHServiceException {
		boolean result = true;

		int newCode = newLaboratory(laboratory);
		if (newCode > 0) {
			laboratory = repository.findById(newCode).orElse(null);
			for (LaboratoryRow aLabRow : labRow) {
				aLabRow.setLabId(laboratory);
				LaboratoryRow savedLaboratoryRow = rowRepository.save(aLabRow);
				result = result && (savedLaboratoryRow != null);
			}
		}
		return result;
	}
	
	/**
	 * Update an already existing Laboratory exam {@link Laboratory}. No commit is performed.
	 * @param laboratory - the {@link Laboratory} to update
	 * @return <code>true</code> if the exam has been updated with all its results, <code>false</code> otherwise
	 * @throws OHServiceException
	 */
	private boolean updateLaboratory(Laboratory laboratory) throws OHServiceException	{
		Laboratory savedLaboratory = repository.save(laboratory);
		return savedLaboratory != null;
	}

	/**
	 * Update an already existing Laboratory exam {@link Laboratory} (Procedure One).
	 * If old exam was Procedure Two all its releated result are deleted.
	 * @param laboratory - the {@link Laboratory} to update
	 * @return <code>true</code> if the exam has been updated, <code>false</code> otherwise
	 * @throws OHServiceException
	 */
	public boolean updateLabFirstProcedure(Laboratory laboratory) throws OHServiceException	{
		boolean result = updateLaboratory(laboratory);
		rowRepository.deleteByLaboratory_Code(laboratory.getCode());
		return result;
	}

	/**
	 * Update an already existing Laboratory exam {@link Laboratory} (Procedure Two).
	 * Previous results are deleted and replaced with new ones.
	 * @param laboratory - the {@link Laboratory} to update
	 * @return <code>true</code> if the exam has been updated with all its results, <code>false</code> otherwise
	 * @throws OHServiceException
	 */
	public boolean updateLabSecondProcedure(Laboratory laboratory, List<String> labRow) throws OHServiceException {
		boolean result = updateLabFirstProcedure(laboratory);
		if (result)	{
			for (String aLabRow : labRow) {
				LaboratoryRow laboratoryRow = new LaboratoryRow();
				laboratoryRow.setLabId(laboratory);
				laboratoryRow.setDescription(aLabRow);	
				rowRepository.save(laboratoryRow);
			}
		}
		return result;
	}

	/**
	 * Delete a Laboratory exam {@link Laboratory} (Procedure One or Two).
	 * Previous results, if any, are deleted as well.
	 * @param aLaboratory - the {@link Laboratory} to delete
	 * @return <code>true</code> if the exam has been deleted with all its results, if any. <code>false</code> otherwise
	 * @throws OHServiceException
	 */
	public boolean deleteLaboratory(Laboratory aLaboratory) throws OHServiceException {
		Laboratory objToRemove = repository.findById(aLaboratory.getCode()).orElse(null);
		if (objToRemove.getExam().getProcedure() == 2) {
			rowRepository.deleteByLaboratory_Code(objToRemove.getCode());
		}
		repository.deleteById(objToRemove.getCode());
		return true;
	}

	/**
	 * Checks if the code is already in use
	 *
	 * @param code - the laboratory code
	 * @return <code>true</code> if the code is already in use, <code>false</code> otherwise
	 * @throws OHServiceException 
	 */
	public boolean isCodePresent(Integer code) throws OHServiceException {
		return repository.existsById(code);
	}
	
	public Optional<Laboratory> getLaboratory(int code) throws OHServiceException {
		return repository.findById(code);
	}

}
