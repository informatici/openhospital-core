package org.isf.conditioning.service;

import org.isf.admission.model.Admission;
import org.isf.conditioning.model.Conditioning;
import org.isf.generaldata.MessageBundle;
import org.isf.menu.model.User;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(rollbackFor = OHServiceException.class)
@TranslateOHServiceException
public class ConditioningOperations {
	private final ConditioningOperationRepository operationRepository;

	public ConditioningOperations(ConditioningOperationRepository operationRepository) {
		this.operationRepository = operationRepository;
	}

	public Conditioning getConditioning(int id) throws OHServiceException {
		return operationRepository.findById(id).orElse(null);
	}

	public Conditioning updateConditioning(Conditioning conditioning) throws OHServiceException {
		return operationRepository.save(conditioning);
	}


}
