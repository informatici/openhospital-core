/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.isf.operation.service;

import java.util.ArrayList;
import java.util.List;

import org.isf.admission.model.Admission;
import org.isf.opd.model.Opd;
import org.isf.operation.model.OperationRow;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author hp
 */
@Service
@Transactional(rollbackFor=OHServiceException.class)
@TranslateOHServiceException
public class OperationRowIoOperations {
	
    @Autowired
    private OperationRowIoOperationRepository repository;
    
    public ArrayList<OperationRow> getOperationRow() throws OHServiceException{
        return repository.getOperationRow();
    }

    public List<OperationRow> getOperationRowByAdmission(Admission adm) throws OHServiceException{
        return repository.findByAdmission(adm);
    }
    
    public ArrayList<OperationRow> getOperationRowByOpd(Opd opd) throws OHServiceException {
        return repository.findByOpd(opd);
    }

    public boolean deleteOperationRow(OperationRow operationRow) throws OHServiceException{
        OperationRow found = repository.findById(operationRow.getId());
        if(found != null) {
            repository.delete(found);
            return true;
        }
        return false;
    }

    public void updateOperationRow(OperationRow opRow) throws OHServiceException {
        OperationRow found = repository.findById(opRow.getId());
        if(found != null) {
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
}
