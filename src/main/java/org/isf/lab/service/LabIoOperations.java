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
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.pagination.PageInfo;
import org.isf.utils.pagination.PagedResponse;
import org.isf.utils.time.TimeTools;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = OHServiceException.class)
@TranslateOHServiceException
public class LabIoOperations {

	private LabIoOperationRepository repository;

	private LabRowIoOperationRepository rowRepository;

	public LabIoOperations(LabIoOperationRepository labIoOperationRepository, LabRowIoOperationRepository labRowIoOperationRepository) {
		this.repository = labIoOperationRepository;
		this.rowRepository = labRowIoOperationRepository;
	}

	/**
	 * Return a list of results ({@link LaboratoryRow}s) for passed lab code.
	 *
	 * @param code - the {@link Laboratory} record ID.
	 * @return the list of {@link LaboratoryRow}s. It could be {@code empty}
	 * @throws OHServiceException
	 */
	public List<LaboratoryRow> getLabRow(Integer code) throws OHServiceException {
		return rowRepository.findByLaboratory_Code(code);
	}

	/**
	 * Return the list of exams ({@link Laboratory}s) divided by pages.
	 * 
	 * @param oneWeek
	 * @param pageNo
	 * @param pageSize
	 * @return the list of {@link Laboratory}s (could be empty)
	 * @throws OHServiceException
	 */
	public PagedResponse<Laboratory> getLaboratoryPageable(boolean oneWeek, int pageNo, int pageSize) throws OHServiceException {
		Pageable pageable = PageRequest.of(pageNo, pageSize);
		if (oneWeek) {
			LocalDateTime time2 = TimeTools.getDateToday24();
			LocalDateTime time1 = time2.minusWeeks(1);
			Page<Laboratory> pagedResult = repository.findByLabDateBetweenOrderByLabDateDescPage(time1, time2, pageable);
			return setPaginationData(pagedResult);
		}
		Page<Laboratory> pagedResult = repository.findAll(pageable);
		return setPaginationData(pagedResult);
	}

	/**
	 * Return the whole list of exams ({@link Laboratory}s) within the last week.
	 *
	 * @return the list of {@link Laboratory}s 
	 * @throws OHServiceException
	 */
	public List<Laboratory> getLaboratory() throws OHServiceException {
		LocalDateTime time2 = TimeTools.getDateToday24();
		LocalDateTime time1 = time2.minusWeeks(1);
		return getLaboratory(null, time1, time2);
	}

	/**
	 * Return a list of exams ({@link Laboratory}s) between specified dates and matching passed exam name.
	 *
	 * @param exam - the exam name as {@code String}
	 * @param dateFrom - the lower date for the range
	 * @param dateTo - the highest date for the range
	 * @return the list of {@link Laboratory}s 
	 * @throws OHServiceException
	 */
	public List<Laboratory> getLaboratory(String exam, LocalDateTime dateFrom, LocalDateTime dateTo) throws OHServiceException {
		return exam != null ? repository.findByLabDateBetweenAndExamDescriptionOrderByLabDateDesc(TimeTools.truncateToSeconds(dateFrom),
						TimeTools.truncateToSeconds(dateTo),
						exam)
						: repository.findByLabDateBetweenOrderByLabDateDesc(TimeTools.truncateToSeconds(dateFrom.with(LocalTime.MIN)),
										TimeTools.truncateToSeconds(dateTo.with(LocalTime.MAX)));
	}

	/**
	 * Return a list of exams ({@link Laboratory}s) between specified dates and matching passed exam name.
	 *
	 * @param exam - the exam name as {@code String}
	 * @param dateFrom - the lower date for the range
	 * @param dateTo - the highest date for the range
	 * @param patient - the object of patient 
	 * @return the list of {@link Laboratory}s 
	 * @throws OHServiceException
	 */
	public List<Laboratory> getLaboratory(String exam, LocalDateTime dateFrom, LocalDateTime dateTo, Patient patient) throws OHServiceException {
		List<Laboratory> laboritories = new ArrayList<>();
		LocalDateTime truncatedDateFrom = TimeTools.truncateToSeconds(dateFrom.with(LocalTime.MIN));
		LocalDateTime truncatedDateTo = TimeTools.truncateToSeconds(dateTo.with(LocalTime.MAX));

		if (!exam.isEmpty() && patient != null) {
			laboritories = repository.findByLabDateBetweenAndExamDescriptionAndPatientCode(truncatedDateFrom, truncatedDateTo, exam, patient.getCode());
		}
		if (!exam.isEmpty() && patient == null) {
			laboritories = repository.findByLabDateBetweenAndExamDescriptionOrderByLabDateDesc(truncatedDateFrom, truncatedDateTo, exam);
		}
		if (patient != null && exam.isEmpty()) {
			laboritories = repository.findByLabDateBetweenAndPatientCode(truncatedDateFrom, truncatedDateTo, patient.getCode());
		}
		if (patient == null && exam.isEmpty()) {
			laboritories = repository.findByLabDateBetweenOrderByLabDateDesc(truncatedDateFrom, truncatedDateTo);
		}
		return laboritories;
	}

	/**
	 * Return a list of exams ({@link Laboratory}s) related to a {@link Patient}.
	 *
	 * @param aPatient - the {@link Patient}.
	 * @return the list of {@link Laboratory}s related to the {@link Patient}.
	 * @throws OHServiceException
	 */
	public List<Laboratory> getLaboratory(Patient aPatient) throws OHServiceException {
		return repository.findByPatient_CodeOrderByLabDate(aPatient.getCode());
	}

	/**
	 * Return a list of exams suitable for printing ({@link LaboratoryForPrint}s) within the last week.
	 *
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
	 * between specified dates and matching passed exam name.
	 *
	 * @param exam - the exam name as {@code String}
	 * @param dateFrom - the lower date for the range
	 * @param dateTo - the highest date for the range
	 * @return the list of {@link LaboratoryForPrint}s 
	 * @throws OHServiceException
	 */
	public List<LaboratoryForPrint> getLaboratoryForPrint(String exam, LocalDateTime dateFrom, LocalDateTime dateTo, Patient patient)
					throws OHServiceException {
		List<LaboratoryForPrint> pLaboratory = new ArrayList<>();
		List<Laboratory> laboritories = new ArrayList<>();
		LocalDateTime truncatedDateFrom = TimeTools.truncateToSeconds(dateFrom.with(LocalTime.MIN));
		LocalDateTime truncatedDateTo = TimeTools.truncateToSeconds(dateTo.with(LocalTime.MAX));

		if (exam != null && patient != null) {
			laboritories = repository.findByLabDateBetweenAndExamDescriptionAndPatientCode(truncatedDateFrom, truncatedDateTo, exam, patient.getCode());
		}
		if (exam != null && patient == null) {
			laboritories = repository.findByLabDateBetweenAndExamDescriptionOrderByLabDateDesc(truncatedDateFrom, truncatedDateTo, exam);
		}
		if (patient != null && exam == null) {
			laboritories = repository.findByLabDateBetweenAndPatientCode(truncatedDateFrom, truncatedDateTo, patient.getCode());
		}
		if (patient == null && exam == null) {
			laboritories = repository.findByLabDateBetweenOrderByLabDateDesc(truncatedDateFrom, truncatedDateTo);
		}
		for (Laboratory laboratory : laboritories) {

			pLaboratory.add(new LaboratoryForPrint(
							laboratory.getCode(),
							laboratory.getExam(),
							laboratory.getLabDate(),
							laboratory.getResult(),
							laboratory.getPatName(),
							laboratory.getPatient().getCode()));
		}
		return pLaboratory;
	}

	/**
	 * Insert a new Laboratory exam {@link Laboratory} and return the generated laboratory code.
	 *
	 * @param laboratory - the {@link Laboratory} to insert
	 * @return the newly persisted {@link Laboratory} object.
	 * @throws OHServiceException
	 */
	private Laboratory newLaboratory(Laboratory laboratory) throws OHServiceException {
		return repository.save(laboratory);
	}

	/**
	 * Inserts one Laboratory exam {@link Laboratory} (Procedure One)
	 *
	 * @param laboratory - the {@link Laboratory} to insert
	 * @return the newly persisted {@link Laboratory} object.
	 * @throws OHServiceException
	 */
	public Laboratory newLabFirstProcedure(Laboratory laboratory) throws OHServiceException {
		return newLaboratory(laboratory);
	}

	/**
	 * Inserts one Laboratory exam {@link Laboratory} with multiple results (Procedure Two)
	 *
	 * @param laboratory - the {@link Laboratory} to insert
	 * @param labRow - the list of results ({@link String}s)
	 * @return the newly persisted {@link Laboratory} object.
	 * @throws OHServiceException
	 */
	public Laboratory newLabSecondProcedure(Laboratory laboratory, List<String> labRow) throws OHServiceException {
		Laboratory newLaboratory = newLaboratory(laboratory);
		if (newLaboratory.getCode() > 0) {
			for (String aLabRow : labRow) {
				LaboratoryRow laboratoryRow = new LaboratoryRow();
				laboratoryRow.setLabId(laboratory);
				laboratoryRow.setDescription(aLabRow);
				rowRepository.save(laboratoryRow);
			}
		}
		return newLaboratory;
	}

	/**
	 * Return a list of exams suitable for printing ({@link LaboratoryForPrint}s) 
	 * between specified dates and matching passed exam name.
	 *
	 * @param exam - the exam name as {@code String}
	 * @param dateFrom - the starting date for the date range
	 * @param dateTo - the ending date for the date range
	 * @return the list of {@link LaboratoryForPrint}s 
	 * @throws OHServiceException
	 */
	public List<LaboratoryForPrint> getLaboratoryForPrint(String exam, LocalDateTime dateFrom, LocalDateTime dateTo) throws OHServiceException {
		List<LaboratoryForPrint> pLaboratory = new ArrayList<>();
		LocalDateTime truncatedDateFrom = TimeTools.truncateToSeconds(dateFrom.with(LocalTime.MIN));
		LocalDateTime truncatedDateTo = TimeTools.truncateToSeconds(dateTo.with(LocalTime.MAX));

		Iterable<Laboratory> laboritories = exam != null
						? repository.findByLabDateBetweenAndExam_DescriptionContainingOrderByExam_Examtype_DescriptionDesc(truncatedDateFrom, truncatedDateTo, exam)
						: repository.findByLabDateBetweenOrderByExam_Examtype_DescriptionDesc(truncatedDateFrom, truncatedDateTo);

		for (Laboratory laboratory : laboritories) {
			pLaboratory.add(new LaboratoryForPrint(
							laboratory.getCode(),
							laboratory.getExam(),
							laboratory.getLabDate(),
							laboratory.getResult()));
		}
		return pLaboratory;
	}
	/**
	 * Inserts one Laboratory exam {@link Laboratory} with multiple results (Procedure Two)
	 *
	 * @param laboratory - the {@link Laboratory} to insert
	 * @param labRow - the list of results ({@link String}s)
	 * @return the newly persisted {@link Laboratory} object.
	 * @throws OHServiceException
	 */
	public Laboratory newLabSecondProcedure2(Laboratory laboratory, List<LaboratoryRow> labRow) throws OHServiceException {
		Laboratory newLaboratory = newLaboratory(laboratory);
		int newCode = newLaboratory.getCode();
		if (newCode > 0) {
			laboratory = repository.findById(newCode).orElse(null);
			if (laboratory == null) {
				throw new OHServiceException(new OHExceptionMessage("Laboratory with code '" + newCode + "' not found"));
			}
			for (LaboratoryRow aLabRow : labRow) {
				aLabRow.setLabId(laboratory);
				rowRepository.save(aLabRow);
			}
		}
		return newLaboratory;
	}

	/**
	 * Update an already existing Laboratory exam {@link Laboratory}.
	 *
	 * @param laboratory - the {@link Laboratory} to update
	 * @return the updated {@link Laboratory} object.
	 * @throws OHServiceException
	 */
	private Laboratory updateLaboratory(Laboratory laboratory) throws OHServiceException {
		return repository.save(laboratory);
	}

	/**
	 * Update an already existing Laboratory exam {@link Laboratory} (Procedure One).
	 * If the old exam was Procedure Two then all its related results are deleted.
	 *
	 * @param laboratory - the {@link Laboratory} to update
	 * @return {@code true} if the exam has been updated, {@code false} otherwise
	 * @throws OHServiceException
	 */
	public Laboratory updateLabFirstProcedure(Laboratory laboratory) throws OHServiceException {
		Laboratory updatedLaborator = updateLaboratory(laboratory);
		rowRepository.deleteByLaboratory_Code(updatedLaborator.getCode());
		return updatedLaborator;
	}

	/**
	 * Update an already existing Laboratory exam {@link Laboratory} (Procedure Two).
	 * Previous results are deleted and replaced with new ones.
	 * @param laboratory - the {@link Laboratory} to update
	 * @return the updated {@link Laboratory} object.
	 * @throws OHServiceException
	 */
	public Laboratory updateLabSecondProcedure(Laboratory laboratory, List<String> labRow) throws OHServiceException {
		Laboratory updatedLaboratory = updateLabFirstProcedure(laboratory);
		for (String aLabRow : labRow) {
			LaboratoryRow laboratoryRow = new LaboratoryRow();
			laboratoryRow.setLabId(laboratory);
			laboratoryRow.setDescription(aLabRow);
			rowRepository.save(laboratoryRow);
		}
		return updatedLaboratory;
	}

	/**
	 * Delete a Laboratory exam {@link Laboratory} (Procedure One or Two).
	 * Previous results, if any, are deleted as well.
	 * @param aLaboratory - the {@link Laboratory} to delete
	 * @throws OHServiceException
	 */
	public void deleteLaboratory(Laboratory aLaboratory) throws OHServiceException {
		Laboratory objToRemove = repository.findById(aLaboratory.getCode()).orElse(null);
		if (objToRemove == null) {
			throw new OHServiceException(new OHExceptionMessage("Laboratory object not found to delete"));
		}
		if (objToRemove.getExam().getProcedure() == 2) {
			rowRepository.deleteByLaboratory_Code(objToRemove.getCode());
		}
		repository.deleteById(objToRemove.getCode());
	}

	/**
	 * Checks if the code is already in use
	 *
	 * @param code - the laboratory code
	 * @return {@code true} if the code is already in use, {@code false} otherwise
	 * @throws OHServiceException 
	 */
	public boolean isCodePresent(Integer code) throws OHServiceException {
		return repository.existsById(code);
	}

	public Optional<Laboratory> getLaboratory(int code) throws OHServiceException {
		return repository.findById(code);
	}

	public PagedResponse<Laboratory> getLaboratoryPageable(String exam, LocalDateTime dateFrom, LocalDateTime dateTo, Patient patient, int page, int size)
					throws OHServiceException {
		Page<Laboratory> laboritories = null;
		LocalDateTime truncatedDateFrom = TimeTools.truncateToSeconds(dateFrom.with(LocalTime.MIN));
		LocalDateTime truncatedDateTo = TimeTools.truncateToSeconds(dateTo.with(LocalTime.MAX));

		if (exam != null && patient != null) {
			laboritories = repository.findByLabDateBetweenAndExamDescriptionAndPatientCodePage(truncatedDateFrom, truncatedDateTo, exam, patient, PageRequest.of(page, size));
		}
		if (exam != null && patient == null) {
			laboritories = repository.findByLabDateBetweenAndExam_DescriptionOrderByLabDateDescPage(truncatedDateFrom, truncatedDateTo, exam, PageRequest.of(page, size));
		}
		if (patient != null && exam == null) {
			laboritories = repository.findByLabDateBetweenAndPatientCodePage(truncatedDateFrom, truncatedDateTo, patient, PageRequest.of(page, size));
		}
		if (patient == null && exam == null) {
			laboritories = repository.findByLabDateBetweenOrderByLabDateDescPage(truncatedDateFrom, truncatedDateTo, PageRequest.of(page, size));
		}
		return setPaginationData(laboritories);
	}

	PagedResponse<Laboratory> setPaginationData(Page<Laboratory> pages) {
		PagedResponse<Laboratory> data = new PagedResponse<>();
		data.setData(pages.getContent());
		data.setPageInfo(PageInfo.from(pages));
		return data;
	}

	/**
	 * Count active {@link Laboratory}s
	 * 
	 * @return the number of recorded {@link Laboratory}s
	 * @throws OHServiceException
	 */
	public long countAllActiveLabs() {
		return this.repository.countAllActiveLabs();
	}

}
