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
package org.isf.visits.service;

import java.util.List;

import org.isf.patient.model.PatientMergedEvent;
import org.isf.utils.exception.OHServiceException;
import org.isf.visits.model.Visit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class VisitsPatientMergedEventListener {
	@Autowired
	VisitsIoOperations visitsIoOperations;

	@EventListener
	public void handle(PatientMergedEvent patientMergedEvent) throws OHServiceException {
		List<Visit> visits = visitsIoOperations.getVisits(patientMergedEvent.getObsoletePatient().getCode());
		for(Visit visit : visits) {
			visit.setPatient(patientMergedEvent.getMergedPatient());
		}
	}

}
