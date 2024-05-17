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
package org.isf.examination.service;

import java.util.List;

import org.isf.examination.model.PatientExamination;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.pagination.PageInfo;
import org.isf.utils.pagination.PagedResponse;
import org.isf.utils.time.TimeTools;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Mwithi
 */
@Service
@Transactional(rollbackFor=OHServiceException.class)
@TranslateOHServiceException
public class ExaminationOperations {

	private ExaminationIoOperationRepository repository;
	
	public ExaminationOperations(ExaminationIoOperationRepository examinationIoOperationRepository) {
		this.repository = examinationIoOperationRepository;
	}

	/**
	 * Get from last PatientExamination
	 */
	public PatientExamination getFromLastPatientExamination(PatientExamination lastPatientExamination) {
		return new PatientExamination(TimeTools.getNow(),
		                              lastPatientExamination.getPatient(),
		                              lastPatientExamination.getPex_height(),
		                              lastPatientExamination.getPex_weight(),
		                              lastPatientExamination.getPex_ap_min(),
		                              lastPatientExamination.getPex_ap_max(),
		                              lastPatientExamination.getPex_hr(),
		                              lastPatientExamination.getPex_temp(),
		                              lastPatientExamination.getPex_sat(),
		                              lastPatientExamination.getPex_hgt(),
		                              lastPatientExamination.getPex_diuresis(),
		                              lastPatientExamination.getPex_diuresis_desc(),
		                              lastPatientExamination.getPex_bowel_desc(),
		                              lastPatientExamination.getPex_rr(),
		                              lastPatientExamination.getPex_auscultation(),
		                              lastPatientExamination.getPex_note());
	}

	/**
	 * 
	 * @param patex
	 *            - the PatientExamination to save
	 * @throws OHServiceException
	 */
	public PatientExamination saveOrUpdate(PatientExamination patex) throws OHServiceException {
		return repository.save(patex);
	}

	public PatientExamination getByID(int id) throws OHServiceException {
		return repository.findById(id).orElse(null);
	}

	public PatientExamination getLastByPatID(int patID) throws OHServiceException	{
		List<PatientExamination> patExamination = getByPatID(patID);
		return !patExamination.isEmpty() ? patExamination.get(0) : null;
	}

	public List<PatientExamination> getLastNByPatID(int patID, int number) throws OHServiceException {
		if (number > 0) {
			return repository.findByPatient_CodeOrderByPexDateDesc(patID, PageRequest.of(0, number));
		}
		return repository.findByPatient_CodeOrderByPexDateDesc(patID);
	}
	
	public PagedResponse<PatientExamination> getLastNByPatIDPageable(int patID, int number) throws OHServiceException {	
		Page<PatientExamination> pagedResult = repository.findByPatient_CodeOrderByPexDateDesc_Paginated(patID, PageRequest.of(0, number));
		return setPaginationData(pagedResult);
	}

	public List<PatientExamination> getByPatID(int patID) throws OHServiceException	{
		return repository.findByPatient_CodeOrderByPexDateDesc(patID);
	}

	public void remove(List<PatientExamination> patexList) throws OHServiceException {
		repository.deleteAll(patexList);
	}
	
	PagedResponse<PatientExamination> setPaginationData(Page<PatientExamination> pages){
		PagedResponse<PatientExamination> data = new PagedResponse<>();
		data.setData(pages.getContent());
		data.setPageInfo(PageInfo.from(pages));
		return data;
	}
}
