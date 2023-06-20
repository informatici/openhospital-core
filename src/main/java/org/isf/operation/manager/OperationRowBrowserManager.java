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
package org.isf.operation.manager;

import java.util.List;

import org.isf.admission.model.Admission;
import org.isf.opd.model.Opd;
import org.isf.operation.model.OperationRow;
import org.isf.operation.service.OperationRowIoOperations;
import org.isf.patient.model.Patient;
import org.isf.utils.exception.OHServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author xavier
 */
@Component
public class OperationRowBrowserManager {

	@Autowired
	private OperationRowIoOperations ioOperations;

	public List<OperationRow> getOperationRowByAdmission(Admission adm) throws OHServiceException {
		return ioOperations.getOperationRowByAdmission(adm);
	}
	
	public List<OperationRow> getOperationRowByOpd(Opd opd) throws OHServiceException {
		return ioOperations.getOperationRowByOpd(opd);
	}

	public boolean deleteOperationRow(OperationRow operationRow) throws OHServiceException {
		return ioOperations.deleteOperationRow(operationRow);
	}

	public boolean updateOperationRow(OperationRow opRow) throws OHServiceException {
		ioOperations.updateOperationRow(opRow);
		return true;
	}

	public boolean newOperationRow(OperationRow opRow) throws OHServiceException {
		ioOperations.newOperationRow(opRow);
		return true;
	}
	
	public List<OperationRow> getOperationRowByPatientCode(Patient patient) throws OHServiceException {
		return ioOperations.getOperationRowByPatient(patient);
	}

}
