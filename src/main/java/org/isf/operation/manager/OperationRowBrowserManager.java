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
package org.isf.operation.manager;

import java.util.ArrayList;
import java.util.List;

import org.isf.admission.model.Admission;
import org.isf.generaldata.MessageBundle;
import org.isf.opd.model.Opd;
import org.isf.operation.model.OperationRow;
import org.isf.operation.service.OperationRowIoOperations;
import org.isf.patient.model.Patient;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.springframework.stereotype.Component;

/**
 * @author xavier
 */
@Component
public class OperationRowBrowserManager {

	private final OperationRowIoOperations ioOperations;

	public OperationRowBrowserManager(OperationRowIoOperations operationRowIoOperations) {
		this.ioOperations = operationRowIoOperations;
	}

	public List<OperationRow> getOperationRowByAdmission(Admission adm) throws OHServiceException {
		return ioOperations.getOperationRowByAdmission(adm);
	}

	public List<OperationRow> getOperationRowByOpd(Opd opd) throws OHServiceException {
		return ioOperations.getOperationRowByOpd(opd);
	}

	public void deleteOperationRow(OperationRow operationRow) throws OHServiceException {
		ioOperations.deleteOperationRow(operationRow);
	}

	public OperationRow updateOperationRow(OperationRow opRow) throws OHServiceException {
		validateOperationRow(opRow);
		return ioOperations.updateOperationRow(opRow);
	}

	public OperationRow newOperationRow(OperationRow opRow) throws OHServiceException {
		validateOperationRow(opRow);
		return ioOperations.newOperationRow(opRow);
	}

	/**
	 * Verify if the {@link OperationRow} is valid for CRUD and throw an exception with the list of errors, if any.
	 *
	 * @param opRow the {@link OperationRow} to validate
	 * @throws OHDataValidationException if the {@link OperationRow} is not valid
	 */
	protected void validateOperationRow(OperationRow opRow) throws OHServiceException {
		List<OHExceptionMessage> errors = new ArrayList<>();
		if (opRow.getOperation() == null) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.operationrow.pleaseinsertanoperation.msg")));
		}
		String prescriber = opRow.getPrescriber();
		if (prescriber == null || prescriber.isEmpty()) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.operationrow.pleaseinsertaprescriber.msg")));
		} else if (prescriber.length() > 150) {
			errors.add(new OHExceptionMessage(MessageBundle.formatMessage("angal.operationrow.theprescriberistoolongmaxchars.fmt.msg", 150)));
		}
		String opResult = opRow.getOpResult();
		if (opResult == null || opResult.isEmpty()) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.operationrow.pleaseselectaresult.msg")));
		} else if (opResult.length() > 250) {
			errors.add(new OHExceptionMessage(MessageBundle.formatMessage("angal.operationrow.theresultistoolongmaxchars.fmt.msg", 250)));
		}
		if (opRow.getOpDate() == null) {
			errors.add(new OHExceptionMessage(MessageBundle.getMessage("angal.operationrow.pleaseinsertavaliddate.msg")));
		}
		// remarks is optional free text: only its length is constrained
		String remarks = opRow.getRemarks();
		if (remarks != null && remarks.length() > 250) {
			errors.add(new OHExceptionMessage(MessageBundle.formatMessage("angal.operationrow.theremarksaretoolongmaxchars.fmt.msg", 250)));
		}
		if (!errors.isEmpty()) {
			throw new OHDataValidationException(errors);
		}
	}

	public List<OperationRow> getOperationRowByPatientCode(Patient patient) throws OHServiceException {
		return ioOperations.getOperationRowByPatient(patient);
	}

}
