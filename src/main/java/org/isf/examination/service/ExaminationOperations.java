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
package org.isf.examination.service;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import org.isf.examination.model.PatientExamination;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.beans.factory.annotation.Autowired;
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

	@Autowired
	private ExaminationIoOperationRepository repository;
	
	public ExaminationOperations() {
	}

	/**
	 * Get from last PatientExamination (only height, weight & note)
	 */
	public PatientExamination getFromLastPatientExamination(
			PatientExamination lastPatientExamination)
	{
		PatientExamination newPatientExamination = new PatientExamination(new GregorianCalendar(),
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
		return newPatientExamination;
	}

	/**
	 * 
	 * @param patex
	 *            - the PatientExamination to save
	 * @throws OHServiceException
	 */
	public void saveOrUpdate(PatientExamination patex) throws OHServiceException {
		repository.save(patex);
	}

	public PatientExamination getByID(int ID) throws OHServiceException {
		return repository.findOne(ID);
	}

	public PatientExamination getLastByPatID(int patID) throws OHServiceException	{
		List<PatientExamination> patExamination = getByPatID(patID);
		return !patExamination.isEmpty() ? patExamination.get(0) : null;
	}

	public List<PatientExamination> getLastNByPatID(int patID, int number) throws OHServiceException {
		if (number > 0) {
			return new ArrayList<>(repository
							.findByPatient_CodeOrderByPexDateDesc(patID, new PageRequest(0, number)).getContent());
		} else {
			return new ArrayList<>(repository
							.findByPatient_CodeOrderByPexDateDesc(patID));
		}
		
	}

	public List<PatientExamination> getByPatID(int patID) throws OHServiceException	{
		return repository.findByPatient_CodeOrderByPexDateDesc(patID);
	}

	public void remove(List<PatientExamination> patexList) throws OHServiceException {
		repository.delete(patexList);
	}
}
