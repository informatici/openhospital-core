/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2026 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.patient.manager;

import java.util.ArrayList;
import java.util.List;

import org.isf.accounting.manager.BillBrowserManager;
import org.isf.accounting.model.Bill;
import org.isf.accounting.model.BillItems;
import org.isf.accounting.model.BillPayments;
import org.isf.admission.manager.AdmissionBrowserManager;
import org.isf.examination.manager.ExaminationBrowserManager;
import org.isf.lab.manager.LabManager;
import org.isf.opd.manager.OpdBrowserManager;
import org.isf.operation.manager.OperationRowBrowserManager;
import org.isf.patient.dto.PatientExport;
import org.isf.patient.model.Patient;
import org.isf.patvac.manager.PatVacManager;
import org.isf.therapy.manager.TherapyManager;
import org.isf.utils.exception.OHServiceException;
import org.springframework.stereotype.Component;

/**
 * Manager that aggregates a {@link Patient} record and all the records connected to it
 * into a single {@link PatientExport}, for GDPR Art. 20 (right to data portability) exports.
 */
@Component
public class PatientExportManager {

	private final PatientBrowserManager patientBrowserManager;

	private final AdmissionBrowserManager admissionBrowserManager;

	private final OpdBrowserManager opdBrowserManager;

	private final LabManager labManager;

	private final TherapyManager therapyManager;

	private final OperationRowBrowserManager operationRowBrowserManager;

	private final PatVacManager patVacManager;

	private final ExaminationBrowserManager examinationBrowserManager;

	private final BillBrowserManager billBrowserManager;

	public PatientExportManager(PatientBrowserManager patientBrowserManager, AdmissionBrowserManager admissionBrowserManager,
		OpdBrowserManager opdBrowserManager, LabManager labManager, TherapyManager therapyManager,
		OperationRowBrowserManager operationRowBrowserManager, PatVacManager patVacManager,
		ExaminationBrowserManager examinationBrowserManager, BillBrowserManager billBrowserManager) {
		this.patientBrowserManager = patientBrowserManager;
		this.admissionBrowserManager = admissionBrowserManager;
		this.opdBrowserManager = opdBrowserManager;
		this.labManager = labManager;
		this.therapyManager = therapyManager;
		this.operationRowBrowserManager = operationRowBrowserManager;
		this.patVacManager = patVacManager;
		this.examinationBrowserManager = examinationBrowserManager;
		this.billBrowserManager = billBrowserManager;
	}

	/**
	 * Aggregates the {@link Patient} record and all the records connected to it (admissions, OPDs, laboratories,
	 * therapies, operations, vaccines, examinations, bills with their items and payments) into a {@link PatientExport}.
	 *
	 * @param patientId the code of the patient to export
	 * @return the {@link PatientExport} aggregate, or {@code null} if the patient is not found
	 * @throws OHServiceException
	 */
	public PatientExport exportPatientData(int patientId) throws OHServiceException {
		if (patientId <= 0) {
			return null;
		}
		Patient patient = patientBrowserManager.getPatientById(patientId);
		if (patient == null) {
			return null;
		}
		PatientExport export = new PatientExport();
		export.setPatient(patient);
		export.setAdmissions(admissionBrowserManager.getAdmissions(patient));
		export.setOpds(opdBrowserManager.getOpdList(patientId));
		export.setLaboratories(labManager.getLaboratory(patient));
		export.setTherapies(therapyManager.getTherapyRows(patientId));
		export.setOperations(operationRowBrowserManager.getOperationRowByPatientCode(patient));
		export.setVaccines(patVacManager.getPatientVaccines(patientId));
		export.setExaminations(examinationBrowserManager.getByPatID(patientId));
		List<Bill> bills = billBrowserManager.getPatientBills(patientId);
		export.setBills(bills);
		List<BillItems> billItems = new ArrayList<>();
		List<BillPayments> billPayments = new ArrayList<>();
		for (Bill bill : bills) {
			billItems.addAll(billBrowserManager.getItems(bill.getId()));
			billPayments.addAll(billBrowserManager.getPayments(bill.getId()));
		}
		export.setBillItems(billItems);
		export.setBillPayments(billPayments);
		return export;
	}
}
