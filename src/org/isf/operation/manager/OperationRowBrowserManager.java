/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.isf.operation.manager;

import java.util.ArrayList;
import java.util.List;

import org.isf.admission.model.Admission;
import org.isf.opd.model.Opd;
import org.isf.operation.model.OperationRow;
import org.isf.operation.service.OperationRowIoOperations;
import org.isf.utils.exception.OHServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.isf.operation.model.OperationRow;
import org.isf.operation.service.OperationRowIoOperations;
import org.isf.utils.exception.OHServiceException;
/**
 *
 * @author xavier
 */
@Component
public class OperationRowBrowserManager {
    private final Logger logger = LoggerFactory.getLogger(OperationRowBrowserManager.class);
    @Autowired
    private OperationRowIoOperations ioOperations;
    
    public List<OperationRow> getOperationRowByAdmission(Admission adm) throws OHServiceException{
	return ioOperations.getOperationRowByAdmission(adm);
    }
    
    public ArrayList<OperationRow> getOperationRowByOpd(Opd opd) throws OHServiceException {
        return ioOperations.getOperationRowByOpd(opd);
    }
    
    public boolean deleteOperationRow(OperationRow operationRow) throws OHServiceException {
        return ioOperations.deleteOperationRow(operationRow);
    }

    public boolean updateOperationRow(OperationRow opRow) throws OHServiceException {
        try {
            ioOperations.updateOperationRow(opRow);
            return true;
        } catch (OHServiceException ex) {
            throw ex;
        }
    }

    public boolean newOperationRow(OperationRow opRow) throws OHServiceException {
        try {
            ioOperations.newOperationRow(opRow);
            return true;
        } catch (OHServiceException ex) {
        	throw ex;
        }
    }
}
