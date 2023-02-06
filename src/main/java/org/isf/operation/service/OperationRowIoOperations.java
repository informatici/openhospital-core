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
package org.isf.operation.service;

import java.util.ArrayList;
import java.util.List;

import org.isf.admission.model.Admission;
import org.isf.opd.model.Opd;
import org.isf.operation.model.OperationRow;
import org.isf.patient.model.Patient;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author hp
 */
@Service
@Transactional(rollbackFor=OHServiceException.class)
@TranslateOHServiceException
public class OperationRowIoOperations {
	
    @Autowired
    private OperationRowIoOperationRepository repository;
    
    public List<OperationRow> getOperationRow() throws OHServiceException{
        return repository.findByOrderByOpDateDesc();
    }

    public List<OperationRow> getOperationRowByAdmission(Admission adm) throws OHServiceException{
        return repository.findByAdmission(adm);
    }

	public List<OperationRow> getOperationRowByOpd(Opd opd) throws OHServiceException {
		if (opd.isPersisted()) {
			return repository.findByOpd(opd);
		}
		return new ArrayList<>();
	}

	public boolean deleteOperationRow(OperationRow operationRow) throws OHServiceException {
		OperationRow found = repository.findById(operationRow.getId());
		if (found != null) {
			repository.delete(found);
			return true;
		}
		return false;
	}

	public void updateOperationRow(OperationRow opRow) throws OHServiceException {
		OperationRow found = repository.findById(opRow.getId());
		if (found != null) {
			found.setAdmission(opRow.getAdmission());
			found.setBill(opRow.getBill());
			found.setOpDate(opRow.getOpDate());
			found.setOpResult(opRow.getOpResult());
			found.setOpd(opRow.getOpd());
			found.setOperation(opRow.getOperation());
			found.setPrescriber(opRow.getPrescriber());
			found.setRemarks(opRow.getRemarks());
			found.setTransUnit(opRow.getTransUnit());
			repository.save(found);
		}
	}

	public void newOperationRow(OperationRow opRow) throws OHServiceException {
		repository.save(opRow);
	}

	public List<OperationRow> getOperationRowByPatient(Patient patient) throws OHServiceException {
		return repository.findByAdmissionPatientOrOpdPatient(patient, patient);
	}
}
