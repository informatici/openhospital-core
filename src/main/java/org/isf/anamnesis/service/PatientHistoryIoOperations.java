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
package org.isf.anamnesis.service;

import org.isf.anamnesis.model.PatientHistory;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = OHServiceException.class)
@TranslateOHServiceException
public class PatientHistoryIoOperations {

	private PatientHistoryIoOperationRepository repository;

	public PatientHistoryIoOperations(PatientHistoryIoOperationRepository patientHistoryIoOperationRepository) {
		this.repository = patientHistoryIoOperationRepository;
	}

	public PatientHistory getByPatientId(int patId) {
		return repository.findByPatientId(patId);
	}

	public PatientHistory saveOrUpdate(PatientHistory patHis) {
		return repository.save(patHis);
	}
}