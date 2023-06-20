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
package org.isf.lab.service;

import java.util.List;

import org.isf.lab.model.Laboratory;
import org.isf.patient.model.Patient;
import org.isf.patient.model.PatientMergedEvent;
import org.isf.utils.exception.OHServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class LabPatientMergedEventListener {

	@Autowired
	LabIoOperations labIoOperations;

	@EventListener
	@Transactional
	public void handle(PatientMergedEvent patientMergedEvent) throws OHServiceException {
		List<Laboratory> laboratories = labIoOperations.getLaboratory(patientMergedEvent.getObsoletePatient());
		for (Laboratory laboratory : laboratories) {
			Patient mergedPatient = patientMergedEvent.getMergedPatient();
			laboratory.setPatient(mergedPatient);
			laboratory.setPatName(mergedPatient.getName());
			laboratory.setAge(mergedPatient.getAge());
			laboratory.setSex(String.valueOf(mergedPatient.getSex()));
		}
	}
}
