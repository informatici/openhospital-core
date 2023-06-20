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
package org.isf.operation.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.isf.accounting.model.Bill;
import org.isf.admission.model.Admission;
import org.isf.opd.model.Opd;
import org.isf.operation.model.Operation;
import org.isf.operation.model.OperationRow;
import org.isf.utils.exception.OHException;

public class TestOperationRow {

	private String prescriber = "prescriber";
	private String opResult = "opResult";
	private LocalDateTime opDate = LocalDateTime.of(2020, 1, 1, 0, 0);
	private String remarks = "remarks";
	private Admission admission;
	private Opd opd;
	private Bill bill;
	private Float transUnit = 10.F;

	public OperationRow setup(Operation operation, boolean usingSet) throws OHException {
		OperationRow operationRow;

		if (usingSet) {
			operationRow = new OperationRow();
			setParameters(operationRow, operation);
		} else {
			// Create OperationRow with all parameters
			operationRow = new OperationRow(operation, prescriber, opResult, opDate, remarks, admission, opd, bill, transUnit);
		}

		return operationRow;
	}

	public void setParameters(OperationRow operationRow, Operation operation) {
		operationRow.setOperation(operation);
		operationRow.setPrescriber(prescriber);
		operationRow.setOpResult(opResult);
		operationRow.setOpDate(opDate);
		operationRow.setRemarks(remarks);
		operationRow.setAdmission(admission);
		operationRow.setOpd(opd);
		operationRow.setBill(bill);
		operationRow.setTransUnit(transUnit);
	}

	public void check(OperationRow operationRow) {
		assertThat(operationRow.getPrescriber()).isEqualTo(prescriber);
		assertThat(operationRow.getOpResult()).isEqualTo(opResult);
		assertThat(operationRow.getOpDate()).isEqualTo(opDate);
		assertThat(operationRow.getRemarks()).isEqualTo(remarks);
		assertThat(operationRow.getAdmission()).isEqualTo(admission);
		assertThat(operationRow.getOpd()).isEqualTo(opd);
		assertThat(operationRow.getBill()).isEqualTo(bill);
		assertThat(operationRow.getTransUnit()).isEqualTo(transUnit);
	}
}
