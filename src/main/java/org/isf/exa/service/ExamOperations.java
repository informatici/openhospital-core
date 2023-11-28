package org.isf.exa.service;

import org.isf.exa.model.ExamRow;
import org.isf.exatype.model.ExamType;
import org.isf.exatype.service.ExamTypeIoOperationRepository;
import org.isf.utils.exception.OHServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExamOperations {
	@Autowired
	private ExamRowIoOperationRepository examRowIoOperationRepository;

	@Autowired
	private ExamTypeIoOperationRepository examTypeIoOperationRepository;

	public List<ExamType> getExamType() throws OHServiceException {
		return examTypeIoOperationRepository.findAllByOrderByDescriptionAsc();
	}

	public boolean isRowPresent(Integer code) throws OHServiceException {
		return examRowIoOperationRepository.existsById(code);
	}

	public boolean deleteExamRow(ExamRow examRow) throws OHServiceException {
		examRowIoOperationRepository.deleteById(examRow.getCode());
		return true;
	}
}
