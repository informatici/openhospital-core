/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2025 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.patient.dto;

import java.util.List;

import org.isf.accounting.model.Bill;
import org.isf.accounting.model.BillItems;
import org.isf.accounting.model.BillPayments;
import org.isf.admission.model.Admission;
import org.isf.examination.model.PatientExamination;
import org.isf.lab.model.Laboratory;
import org.isf.opd.model.Opd;
import org.isf.operation.model.OperationRow;
import org.isf.patient.model.Patient;
import org.isf.patvac.model.PatientVaccine;
import org.isf.therapy.model.TherapyRow;

/**
 * Aggregate of a {@link Patient} record and all the records connected to it, used for
 * GDPR Art. 20 (right to data portability) exports.
 */
public class PatientExport {

	private Patient patient;
	private List<Admission> admissions;
	private List<Opd> opds;
	private List<Laboratory> laboratories;
	private List<TherapyRow> therapies;
	private List<OperationRow> operations;
	private List<PatientVaccine> vaccines;
	private List<PatientExamination> examinations;
	private List<Bill> bills;
	private List<BillItems> billItems;
	private List<BillPayments> billPayments;

	public PatientExport() {
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public List<Admission> getAdmissions() {
		return admissions;
	}

	public void setAdmissions(List<Admission> admissions) {
		this.admissions = admissions;
	}

	public List<Opd> getOpds() {
		return opds;
	}

	public void setOpds(List<Opd> opds) {
		this.opds = opds;
	}

	public List<Laboratory> getLaboratories() {
		return laboratories;
	}

	public void setLaboratories(List<Laboratory> laboratories) {
		this.laboratories = laboratories;
	}

	public List<TherapyRow> getTherapies() {
		return therapies;
	}

	public void setTherapies(List<TherapyRow> therapies) {
		this.therapies = therapies;
	}

	public List<OperationRow> getOperations() {
		return operations;
	}

	public void setOperations(List<OperationRow> operations) {
		this.operations = operations;
	}

	public List<PatientVaccine> getVaccines() {
		return vaccines;
	}

	public void setVaccines(List<PatientVaccine> vaccines) {
		this.vaccines = vaccines;
	}

	public List<PatientExamination> getExaminations() {
		return examinations;
	}

	public void setExaminations(List<PatientExamination> examinations) {
		this.examinations = examinations;
	}

	public List<Bill> getBills() {
		return bills;
	}

	public void setBills(List<Bill> bills) {
		this.bills = bills;
	}

	public List<BillItems> getBillItems() {
		return billItems;
	}

	public void setBillItems(List<BillItems> billItems) {
		this.billItems = billItems;
	}

	public List<BillPayments> getBillPayments() {
		return billPayments;
	}

	public void setBillPayments(List<BillPayments> billPayments) {
		this.billPayments = billPayments;
	}
}
